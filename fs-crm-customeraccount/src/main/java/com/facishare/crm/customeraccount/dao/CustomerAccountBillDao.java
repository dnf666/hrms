package com.facishare.crm.customeraccount.dao;

import com.facishare.crm.customeraccount.entity.CustomerAccountBill;
import com.github.mybatis.mapper.IBatchMapper;
import com.github.mybatis.mapper.ICrudMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 
 */
public interface CustomerAccountBillDao extends ICrudMapper<CustomerAccountBill>, IBatchMapper<CustomerAccountBill> {

    @Select("select * from customer_account_bill where customer_account_id = #{customerAccountId}")
    List<CustomerAccountBill> findByCustomerAccountId(@Param("customerAccountId") String customerAccountId);
}
