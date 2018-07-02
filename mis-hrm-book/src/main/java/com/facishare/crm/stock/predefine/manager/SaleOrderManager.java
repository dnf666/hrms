package com.facishare.crm.stock.predefine.manager;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.rest.CrmRestApi;
import com.facishare.crm.rest.dto.SalesOrderModel;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.util.StockUtils;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.FormField;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by linchf on 2018/1/15.
 */
@Slf4j(topic = "stockAccess")
@Service
public class SaleOrderManager extends CommonManager {
    @Resource
    private CrmRestApi crmRestApi;

    @Resource
    private InitManager initManager;

    private static final String SALES_ORDER_WAREHOUSE_FIELD_NAME = "shipping_warehouse_id";

    private static final String API_NAME_SUFFIX = "_generate_by_UDObjectServer__c";
    /**
     * 获取订单详情
     */
    public SalesOrderModel.SalesOrderVo getById(User user, String salesOrderId) {
        Map<String, String> headers = StockUtils.getHeaders(user.getTenantId(), User.SUPPER_ADMIN_USER_ID);
        try {
            SalesOrderModel.GetByIdResult result = crmRestApi.getCustomerOrderById(salesOrderId, headers);
            if (!result.isSuccess()) {
                log.warn("crmRestApi.getCustomerOrderById failed. result:{}, salesOrderId:{}, headers:{}",
                        result, salesOrderId, headers);
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, result.getMessage());
            } else {
                return result.getValue();
            }
        } catch (StockBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("crmRestApi.getCustomerOrderById fail! headers[{}], salesOrderId[{}]", headers, salesOrderId);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "根据订单id, 查询订单详情异常");
        }
    }

    public List<SalesOrderModel.SalesOrderVo> getByIds(User user, List<String> salesOrderIds) {
        Map<String, String> headers = StockUtils.getHeaders(user.getTenantId(), User.SUPPER_ADMIN_USER_ID);

        try {
            SalesOrderModel.GetByIdsResult result = crmRestApi.getCustomerOrderByIds(salesOrderIds.toArray(new String[salesOrderIds.size()]), headers);
            if (!result.isSuccess()) {
                log.warn("crmRestApi.getCustomerOrderByIds failed. result:{}, salesOrderIds:{}, headers:{}",
                        result, salesOrderIds, headers);
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, result.getMessage());
            } else {
                return result.getValue();
            }
        } catch (StockBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("crmRestApi.getCustomerOrderByIds fail! headers[{}], salesOrderIds[{}]", headers, salesOrderIds);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "根据订单id列表, 查询订单详情异常");
        }
    }

    public void hideSalesOrderWarehouse(User user) {
        List<ILayout> layouts = initManager.findLayoutByObjectDescribeApiNameAndTenantId(SystemConstants.SalesOrderApiName, user.getTenantId());
        if (!CollectionUtils.isEmpty(layouts)) {
            Optional<ILayout> defaultLayoutOpt = layouts.stream().filter(layout -> Objects.equals(layout.getName(), SystemConstants.SalesOrderApiName + "_layout_generate_by_UDObjectServer__c")).findFirst();
            if (defaultLayoutOpt.isPresent()) {
                ILayout defaultLayout = defaultLayoutOpt.get();

                IFieldSection baseFieldSection = getBaseFieldSection(defaultLayout);
                //3、添加新字段
                List<IFormField> newFormFields = Lists.newArrayList();
                List<IFormField> oldFormFields = baseFieldSection.getFields();

                oldFormFields.forEach(oldFormField -> {
                    if (!Objects.equals(oldFormField.getFieldName(), SALES_ORDER_WAREHOUSE_FIELD_NAME)) {
                        newFormFields.add(oldFormField);
                    }
                });
                if (newFormFields.size() != oldFormFields.size()) {
                    baseFieldSection.setFields(newFormFields);
                    initManager.replaceObjectLayout(defaultLayout);
                }
            }
        }
    }

    public void showSalesOrderWarehouse(User user) {
        List<ILayout> layouts = initManager.findLayoutByObjectDescribeApiNameAndTenantId(SystemConstants.SalesOrderApiName, user.getTenantId());
        if (!CollectionUtils.isEmpty(layouts)) {
            Optional<ILayout> defaultLayoutOpt = layouts.stream().filter(layout -> Objects.equals(layout.getName(), SystemConstants.SalesOrderApiName + "_layout_generate_by_UDObjectServer__c")).findFirst();
            if (defaultLayoutOpt.isPresent()) {
                ILayout defaultLayout = defaultLayoutOpt.get();

                IFieldSection baseFieldSection = getBaseFieldSection(defaultLayout);
                if (baseFieldSection != null) {
                    List<IFormField> oldFormFields = baseFieldSection.getFields();
                    if (!CollectionUtils.isEmpty(oldFormFields)) {
                        if (!oldFormFields.stream().anyMatch(oldFormField -> Objects.equals(oldFormField.getFieldName(), SALES_ORDER_WAREHOUSE_FIELD_NAME))) {
                            IFormField warehouseField = new FormField();
                            warehouseField.setFieldName(SALES_ORDER_WAREHOUSE_FIELD_NAME);
                            warehouseField.setReadOnly(false);
                            warehouseField.setRequired(false);
                            warehouseField.setRenderType(SystemConstants.RenderType.ObjectReference.renderType);
                            oldFormFields.add(warehouseField);
                            baseFieldSection.setFields(oldFormFields);
                            initManager.replaceObjectLayout(defaultLayout);
                        }
                    }
                }
            }
        }
    }

    public void modifySalesOrderWarehouseNotRequired(User user) {
        List<ILayout> layouts = initManager.findLayoutByObjectDescribeApiNameAndTenantId(SystemConstants.SalesOrderApiName, user.getTenantId());
        if (!CollectionUtils.isEmpty(layouts)) {
            Optional<ILayout> defaultLayoutOpt = layouts.stream().filter(layout -> Objects.equals(layout.getName(), SystemConstants.SalesOrderApiName + "_layout_generate_by_UDObjectServer__c")).findFirst();
            if (defaultLayoutOpt.isPresent()) {
                ILayout defaultLayout = defaultLayoutOpt.get();

                IFieldSection baseFieldSection = getBaseFieldSection(defaultLayout);

                if (baseFieldSection != null) {
                    List<IFormField> oldFormFields = baseFieldSection.getFields();

                    Optional<IFormField> warehouseFieldOpt = oldFormFields.stream().filter(oldFormField -> Objects.equals(oldFormField.getFieldName(), SALES_ORDER_WAREHOUSE_FIELD_NAME)).findFirst();
                    if (warehouseFieldOpt.isPresent()) {
                        warehouseFieldOpt.get().setRequired(false);
                        initManager.replaceObjectLayout(defaultLayout);
                    }
                }

            }
        }
    }

    public boolean isSalesOrderWarehouseLayoutExisted(User user) {
        List<ILayout> layouts = initManager.findLayoutByObjectDescribeApiNameAndTenantId(SystemConstants.SalesOrderApiName, user.getTenantId());
        if (!CollectionUtils.isEmpty(layouts)) {
            Optional<ILayout> defaultLayoutOpt = layouts.stream().filter(layout -> Objects.equals(layout.getName(), SystemConstants.SalesOrderApiName + "_layout_generate_by_UDObjectServer__c")).findFirst();
            if (defaultLayoutOpt.isPresent()) {
                ILayout defaultLayout = defaultLayoutOpt.get();
                IFieldSection baseFieldSection = getBaseFieldSection(defaultLayout);
                if (baseFieldSection != null) {
                    List<IFormField> formFields = baseFieldSection.getFields();
                    if (!CollectionUtils.isEmpty(formFields)) {
                        if (formFields.stream().anyMatch(formField -> Objects.equals(formField.getFieldName(), SALES_ORDER_WAREHOUSE_FIELD_NAME))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private IFieldSection getBaseFieldSection(ILayout defaultLayout) {
        List<IComponent> components = null;
        FormComponent formComponent = null;
        try {
            components = defaultLayout.getComponents();
        } catch (MetadataServiceException e) {
            log.warn("layout.getComponents failed, layout:{}", defaultLayout);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "layout获取Component信息错误," + e.getMessage());
        }
        for (IComponent iComponent : components) {
            if (Objects.equals(iComponent.getName(), LayoutConstants.FORM_COMPONENT_API_NAME)
                    || Objects.equals(iComponent.getName(), LayoutConstants.FORM_COMPONENT_API_NAME + API_NAME_SUFFIX)) {
                formComponent = (FormComponent) iComponent;
                break;
            }
        }

        //2、获取formFields
        IFieldSection baseFieldSection = null;
        if (formComponent != null) {
            List<IFieldSection> fieldSections = formComponent.getFieldSections();
            for (IFieldSection fieldSection : fieldSections) {
                if (Objects.equals(fieldSection.getName(), LayoutConstants.BASE_FIELD_SECTION_API_NAME)) {
                    baseFieldSection = fieldSection;
                    break;
                }
            }
        }

        return baseFieldSection;
    }
}
