package com.facishare.crm.sfa.predefine.service;

import com.google.common.collect.Lists;

import com.facishare.crm.sfa.predefine.service.model.CrmWorkBenchCreateArg;
import com.facishare.crm.sfa.utilities.common.convert.SearchUtil;
import com.facishare.paas.appframework.common.util.StopWatch;
import com.facishare.paas.appframework.config.ConfigService;
import com.facishare.paas.appframework.config.ConfigValueType;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.menu.MenuConstants;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 个人工作台操作类
 */
@ServiceModule("crm_workbench")
@Component
@Slf4j
public class CrmWorkBenchService {
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    CrmMenuAdminService crmMenuAdminService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private CrmMenuActionService crmMenuActionService;

    /**
     * 终端/WEB端使用的菜单&菜单项大接口
     */
    @ServiceMethod("create")
    public CrmWorkBenchCreateArg.Result create(CrmWorkBenchCreateArg.Arg arg, ServiceContext context) {
        StopWatch stopWatch = StopWatch.create("CrmWorkBenchService create");

        String menuId = CollectionUtils.isNotEmpty(arg.getDataList()) ? arg.getDataList().get(0).getMenuId() : arg.getMenuId();
        if (StringUtils.isEmpty(menuId)) {
            throw new ValidateException("菜单不能为空");
        }
        //删除
        if (CollectionUtils.isNotEmpty(arg.getDataList())) {
            List<IObjectData> dataList = fillWorkBenchs(context.getUser(), arg.getDataList());
            stopWatch.lap("fillWorkBenchs");
            crmMenuActionService.createWorkBench(context.getUser(), menuId, dataList);
            stopWatch.lap("createWorkBench");
        }
        //保存工作台个人默认CRM菜单id
        this.updateMenuDefaultConfig(context.getUser(), menuId);
        stopWatch.lap("updateMenuDefaultConfig");
        stopWatch.logSlow(3000);
        return CrmWorkBenchCreateArg.Result.builder().result(true).build();
    }

    /**
     * 根据菜单id查询当前登录人配置的工作台列表
     */
    public List<IObjectData> findUserMenuWorkBenchList(User user, List<String> menuId) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(20000);
        List<IFilter> filters = searchQuery.getFilters();
        SearchUtil.fillFilterIn(filters, MenuConstants.MenuWorkBenchField.MENUID.getApiName(), menuId);
        SearchUtil.fillFilterEq(filters, MenuConstants.MenuWorkBenchField.USERID.getApiName(), user.getUserId());
        QueryResult<IObjectData> menuWorkBenchList = serviceFacade.findBySearchQuery(user, MenuConstants.MENU_WORKBENCH_API_NAME, searchQuery);
        return menuWorkBenchList.getData();
    }

    public List<IObjectData> fillWorkBenchs(User user, List<CrmWorkBenchCreateArg.WorkBench> workBenchList) {
        IObjectDescribe workBenchDescribe = serviceFacade.findObject(user.getTenantId(), MenuConstants.MENU_WORKBENCH_API_NAME);
        List<IObjectData> objectDataList = Lists.newArrayList();
        workBenchList.stream().forEach(workBench -> {
            IObjectData objectData = fillWorkBench(user, workBenchDescribe, workBench);
            objectDataList.add(objectData);
            if (CollectionUtils.isNotEmpty(workBench.getChildren())) {
                String pid = objectData.getId();
                //处理内部的子菜单项
                workBench.getChildren().stream().forEach(itemWorkBench -> {
                    IObjectData itemObjectData = fillWorkBench(user, workBenchDescribe, itemWorkBench);
                    itemObjectData.set(MenuConstants.MenuWorkBenchField.PID.getApiName(), pid);
                    objectDataList.add(itemObjectData);
                });
            }
        });
        return objectDataList;
    }

    public IObjectData fillWorkBench(User user, IObjectDescribe workBenchDescribe, CrmWorkBenchCreateArg.WorkBench workBench) {
        ObjectData objectData = new ObjectData();
        objectData.set(MenuConstants.MenuWorkBenchField.USERID.getApiName(), user.getUserId());
        objectData.set(MenuConstants.MenuWorkBenchField.MENUID.getApiName(), workBench.getMenuId());
        objectData.set(MenuConstants.MenuWorkBenchField.MENUITEMID.getApiName(), workBench.getMenuItemId());
        objectData.set(MenuConstants.MenuWorkBenchField.NUMBER.getApiName(), workBench.getNumber());
        objectData.set(MenuConstants.MenuWorkBenchField.MODULE.getApiName(), MenuConstants.MenuWorkBenchField.MODULE_WEB.getApiName());
        objectData.set(MenuConstants.MenuWorkBenchField.ISHIDDEN.getApiName(), workBench.getIsHidden());
        objectData.setDescribeApiName(MenuConstants.MENU_WORKBENCH_API_NAME);
        if (StringUtils.isBlank(workBench.getMenuItemId())) {
            //分组类型、分组名称
            objectData.set(MenuConstants.MenuWorkBenchField.DISPLAYNAME.getApiName(), workBench.getDisplayName());
            objectData.set(MenuConstants.MenuWorkBenchField.TYPE.getApiName(), MenuConstants.MenuWorkBenchField.TYPE_GROUP.getApiName());
        } else {
            objectData.set(MenuConstants.MenuWorkBenchField.TYPE.getApiName(), MenuConstants.MenuWorkBenchField.TYPE_MENU.getApiName());
        }
        objectData.setId(serviceFacade.generateId());
        objectData.setTenantId(user.getTenantId());
        objectData.setDescribeId(workBenchDescribe.getId());
        return objectData;
    }

    public String getUserMenuCurrentId(User user) {
        return configService.findUserConfig(user, MenuConstants.CONFIG_MENU_DEFAULT);
    }

    /**
     * 更新配置项个人默认CRM菜单
     */
    private void updateMenuDefaultConfig(User user, String menuId) {
        String configValue = getUserMenuCurrentId(user);
        if (configValue == null) {
            configService.createUserConfig(user, MenuConstants.CONFIG_MENU_DEFAULT, menuId, ConfigValueType.STRING);
        } else {
            configService.updateUserConfig(user, MenuConstants.CONFIG_MENU_DEFAULT, menuId, ConfigValueType.STRING);
        }
    }

}
