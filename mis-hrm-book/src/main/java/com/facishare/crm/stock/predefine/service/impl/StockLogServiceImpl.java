package com.facishare.crm.stock.predefine.service.impl;

import com.facishare.common.proxy.helper.StringUtils;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.model.StockLogDO;
import com.facishare.crm.stock.predefine.manager.StockLogManager;
import com.facishare.crm.stock.predefine.service.StockLogService;
import com.facishare.crm.stock.predefine.service.model.QueryStockLogByTemplateModel;
import com.facishare.crm.stock.predefine.service.model.SaveStockLogModel;
import com.facishare.crm.stock.util.ConfigCenter;
import com.facishare.open.common.storage.mysql.dao.Pager;
import com.facishare.paas.appframework.core.model.ServiceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author linchf
 * @date 2018/5/31
 */
@Slf4j(topic = "stockAccess")
@Component
public class StockLogServiceImpl implements StockLogService {
    @Resource
    private StockLogManager stockLogManager;

    @Override
    public QueryStockLogByTemplateModel.Result queryByPage(ServiceContext serviceContext, QueryStockLogByTemplateModel.Arg arg) {
        QueryStockLogByTemplateModel.Result result = new QueryStockLogByTemplateModel.Result();

        if (Objects.equals(ConfigCenter.SUPPER_ADMIN_ID, serviceContext.getTenantId() + "." + serviceContext.getUser().getUserId())) {
            if (arg.getTemplate() == null) {
                result.setResult("查询模板不能为空");
                return result;
            }

            if (StringUtils.isBlank(arg.getTemplate().getTenantId())) {
                result.setResult("企业id不能为空");
                return result;
            }

            try {
                Pager<QueryStockLogByTemplateModel.StockLogVO> pager = new Pager<>();
                pager.setCurrentPage(null == arg.getTemplate().getCurrentPage() ? 1 : arg.getTemplate().getCurrentPage());
                pager.setPageSize(null == arg.getTemplate().getPageSize() ? 10 : arg.getTemplate().getPageSize());
                Pager<QueryStockLogByTemplateModel.StockLogVO> stockLogs = stockLogManager.queryByPage(pager, arg.getTemplate());
                result.setPager(stockLogs);
            } catch (StockBusinessException e) {
                log.warn("queryByPage failed. arg[{}]", arg, e);
                result.setResult(e.getMessage());
                return result;
            } catch (Exception ex) {
                log.warn("queryByPage failed. arg[{}]", arg, ex);
                result.setResult("查询失败，系统异常");
            }
        } else {
            log.warn("queryByPage failed. authorized failed. user[{}], arg[{}]", serviceContext.getUser(), arg);
            result.setResult("查询失败，身份验证失败");
        }
        return result;
    }

    @Override
    public SaveStockLogModel.Result saveSalesOrderStockLog(ServiceContext serviceContext, SaveStockLogModel.Arg arg) {
        SaveStockLogModel.Result result = new SaveStockLogModel.Result();
        if (Objects.equals(ConfigCenter.SUPPER_ADMIN_ID, serviceContext.getTenantId() + "." + serviceContext.getUser().getUserId())) {
            if (arg.getStockLogDO() == null) {
                result.setResult("库存记录不能为空");
                return result;
            }

            if (StringUtils.isBlank(arg.getStockLogDO().getTenantId())) {
                result.setResult("企业id不能为空");
                return result;
            }

            //暂只支持订单库存记录
            if (!Objects.equals(arg.getStockLogDO().getOperateObjectType(), StockOperateObjectTypeEnum.SALES_ORDER.value)) {
                result.setResult("只支持订单库存记录");
                return result;
            }

            if (StringUtils.isBlank(arg.getStockLogDO().getOperateObjectId())) {
                result.setResult("订单id不能为空");
                return result;
            }

            if (StringUtils.isBlank(arg.getStockLogDO().getProductId())) {
                result.setResult("产品id不能为空");
                return result;
            }

            if (StringUtils.isBlank(arg.getStockLogDO().getStockId())) {
                result.setResult("库存id不能为空");
                return result;
            }

            if (StringUtils.isBlank(arg.getStockLogDO().getModifiedBlockedStockNum())) {
                result.setResult("修改的冻结库存数不能为空");
                return result;
            }
            arg.getStockLogDO().setModifiedTime(System.currentTimeMillis());
            arg.getStockLogDO().setId(null);
            try {
                stockLogManager.bulkUpdate(Arrays.asList(arg.getStockLogDO()));
            } catch (StockBusinessException e) {
                log.warn("saveStockLog failed. arg[{}]", arg, e);
                result.setResult(e.getMessage());
                return result;
            } catch (Exception ex) {
                log.warn("saveStockLog failed. arg[{}]", arg, ex);
                result.setResult("保存失败，系统异常");
            }
        } else {
            log.warn("saveStockLog failed. authorized failed. user[{}], arg[{}]", serviceContext.getUser(), arg);
            result.setResult("保存失败，身份验证失败");
        }
        return result;
    }
}
