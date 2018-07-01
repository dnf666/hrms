package com.facishare.crm.customeraccount.predefine.manager;

import com.facishare.crm.customeraccount.dao.CustomerAccountBillDao;
import com.facishare.crm.customeraccount.entity.CustomerAccountBill;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class CustomerAccountBillManager {
    @Autowired
    private CustomerAccountBillDao customerAccountBillDao;

    /**
     * 把sourceAccountId对应的客户账户流水合并到destAccountId对应的客户账户下<br>
     * 合并流水的具体流程为：<br>
     * 1.查询出sourceBillList.
     * 2.遍历sourceBillList,在原客户账户明细下新生成一条冲销流水，克隆原明细下的流水修改其客户账户Id<br>
     * FIXME 如果部分成功，我们通过事物保证。
     * @param sourceAccountIds
     * @param destAccountId
     */
    public void mergeBill(List<String> sourceAccountIds, String destAccountId) {
        for (String sourceAccountId : sourceAccountIds) {
            try {
                List<CustomerAccountBill> sourceBillList = customerAccountBillDao.findByCustomerAccountId(sourceAccountId);
                if (!CollectionUtils.isEmpty(sourceBillList)) {
                    List<CustomerAccountBill> tobeInsertList = new ArrayList<CustomerAccountBill>();
                    for (CustomerAccountBill sourceBill : sourceBillList) {
                        CustomerAccountBill hedgingBill = generateHedgingBill(sourceBill);
                        CustomerAccountBill newDestBill = generateNewDestBill(sourceBill, destAccountId);
                        tobeInsertList.add(hedgingBill);
                        tobeInsertList.add(newDestBill);
                    }
                    customerAccountBillDao.batchInsert(tobeInsertList);
                }

            } catch (Exception e) {
                String errorMsg = String.format("error occur when mergeBill,for sourAccountId={%s},destAccountId={%s}", sourceAccountId, destAccountId);
                log.warn(errorMsg, e);
            }
        }
    }

    public void addCustomerAccountBillAccordPrepay(String customerAccountId, String prepayId, double prepayAmount, double prepayLockAmount, String tenantId, String info) {
        try {
            CustomerAccountBill bill1 = new CustomerAccountBill();
            bill1.setRelateId(prepayId);
            bill1.setCreateTime(new Date());
            bill1.setBillDate(new Date());
            bill1.setCustomerAccountId(customerAccountId);
            bill1.setPrepayAmountChange(prepayAmount);
            bill1.setPrepayLockedAmountChange(prepayLockAmount);
            bill1.setRemark(info);
            bill1.setTenantId(tenantId);
            log.debug("addCustomerAccountBillAccordPrepay(),customeraccountbill:{}", bill1);
            customerAccountBillDao.insert(bill1);
        } catch (Exception e) {
            log.warn("addCustomerAccountBillAccordPrepay error,tenantId:{},customerAccountId:{},prepayId:{},prepayAmount:{},prepayLockAmount:{},info:{}", tenantId, customerAccountId, prepayId, prepayAmount, prepayLockAmount, info, e);
        }
    }

    /**
     * 增加客户账户流水<br>
     * @param customerAccountId
     * @param rebateId
     * @param rebateAmount
     * @param rebateLockAmount
     * @param tenantId
     * @param info
     */
    public void addCustomerAccountBillAccordRebate(String customerAccountId, String rebateId, double rebateAmount, double rebateLockAmount, String tenantId, String info) {
        try {
            CustomerAccountBill bill1 = new CustomerAccountBill();
            bill1.setRelateId(rebateId);
            bill1.setCreateTime(new Date());
            bill1.setBillDate(new Date());
            bill1.setCustomerAccountId(customerAccountId);
            bill1.setRebateAmountChange(rebateAmount);
            bill1.setRebateLockedAmountChange(rebateLockAmount);
            bill1.setRemark(info);
            bill1.setTenantId(tenantId);
            log.debug("addCustomerAccountBillAccordRebate(),customeraccountbill:{}", bill1);
            customerAccountBillDao.insert(bill1);
        } catch (Exception e) {
            log.warn("addCustomerAccountBillAccordRebate, tenantId{},customerAccountId:{},rebateId:{},rebateAmount:{},rebateLockAmount:{},info:{}", tenantId, customerAccountId, rebateId, rebateAmount, rebateLockAmount, info, e);
        }
    }

    /**
     * 生成一条冲销流水的目的是对账的时候需要，当原有的客户账户作废了其对应的明细也作废了。<br>
     * 此时为了保持对账对平需要使其流水总和为0<br>
     * @param sourceBill
     * @return
     */
    private CustomerAccountBill generateHedgingBill(CustomerAccountBill sourceBill) {
        CustomerAccountBill hedgingBill = new CustomerAccountBill();
        BeanUtils.copyProperties(sourceBill, hedgingBill);
        hedgingBill.setId(null);
        //把所有金额置换成其相反数,对于0这种情况-0还是零。
        hedgingBill.setPrepayAmountChange(reverseValue(hedgingBill.getPrepayAmountChange()));
        hedgingBill.setPrepayLockedAmountChange(reverseValue(hedgingBill.getPrepayLockedAmountChange()));
        hedgingBill.setRebateAmountChange(reverseValue(hedgingBill.getRebateAmountChange()));
        hedgingBill.setRebateLockedAmountChange(reverseValue(hedgingBill.getRebateLockedAmountChange()));
        hedgingBill.setCreateTime(new Date());
        hedgingBill.setRemark("冲销流水，原流水id为" + sourceBill.getId());
        log.debug("generateHedgingBill(),customeraccountbill:{}", hedgingBill);

        //hedgingBill.setBillDate(new Date());
        return hedgingBill;
    }

    private double reverseValue(double value) {
        return -value;
    }

    private CustomerAccountBill generateNewDestBill(CustomerAccountBill sourceBill, String destAccountId) {
        CustomerAccountBill newDestBill = new CustomerAccountBill();
        BeanUtils.copyProperties(sourceBill, newDestBill);
        newDestBill.setId(null);
        //明细id不变，客户账户id换成新的客户账户Id<br>
        newDestBill.setRelateId(sourceBill.getRelateId());
        newDestBill.setCustomerAccountId(destAccountId);
        newDestBill.setCreateTime(new Date());
        newDestBill.setRemark("合并流水，原流水id为" + sourceBill.getId());
        log.debug("generateNewDestBill(),customeraccountbill:{}", newDestBill);
        //newDestBill.setBillDate(new Date());
        return newDestBill;
    }

    //    /**
    //     * 增加客户账户流水<br>
    //     */
    //    public void addCustomerAccountBillAccordPrepay(String customerAccountId, String prepayId, double prepayAmount, double prepayLockAmount, String tenantId, String info) {
    //        CustomerAccountBill bill1 = new CustomerAccountBill();
    //        bill1.setRelateId(prepayId);
    //        bill1.setCreateTime(new Date());
    //        bill1.setBillDate(new Date());
    //        bill1.setCustomerAccountId(customerAccountId);
    //        bill1.setPrepayAmountChange(prepayAmount);
    //        bill1.setPrepayLockedAmountChange(prepayLockAmount);
    //        bill1.setRemark(info);
    //        bill1.setTenantId(tenantId);
    //        //bill1.setCreateBy(RequestUtil.getSysteomUser());
    //        customerAccountBillDao.insert(bill1);
    //    }
    //
    //    public void addCustomerAccountBillAccordRebate(String customerAccountId, String rebateId, double rebateAmount, double rebateLockAmount, String tenantId, String info) {
    //        CustomerAccountBill bill1 = new CustomerAccountBill();
    //        bill1.setRelateId(rebateId);
    //        bill1.setCreateTime(new Date());
    //        bill1.setBillDate(new Date());
    //        bill1.setCustomerAccountId(customerAccountId);
    //        bill1.setPrepayAmountChange(rebateAmount);
    //        bill1.setPrepayLockedAmountChange(rebateLockAmount);
    //        bill1.setRemark(info);
    //        bill1.setTenantId(tenantId);
    //        customerAccountBillDao.insert(bill1);
    //    }

}
