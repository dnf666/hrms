package com.facishare.crm.sfa.predefine.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.facishare.crm.sfa.predefine.service.model.CrmMenuInitAddObject;
import com.facishare.crm.sfa.predefine.service.model.CrmMenuInitAddObjects;
import com.facishare.crm.sfa.predefine.service.model.InitSystemCrmArg;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.menu.MenuCommonService;
import com.facishare.paas.appframework.metadata.menu.MenuConstants;
import com.facishare.paas.appframework.metadata.menu.model.MenuItemConfigObject;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.ObjectData;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * crm菜单初始化类
 */
@ServiceModule("crm_menu_init")
@Service
@Slf4j
public class CrmMenuInitService {
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private CrmMenuAdminService crmMenuAdminService;
    @Autowired
    private MenuCommonService menuCommonService;
    @Autowired
    private CrmMenuActionService crmMenuActionService;

    @ServiceMethod("init_system_menus")
    public InitSystemCrmArg.Result initSystemCrm(InitSystemCrmArg.Arg arg, ServiceContext context) {
        List<String> tenantIds = Arrays.asList(arg.getTenantIds().split(","));
        InitSystemCrmArg.Result result = InitSystemCrmArg.Result.builder().result(Maps.newHashMap()).build();
        for (String tenantId : tenantIds) {
            User user = new User(tenantId, "-10000");
            tenantId = tenantId.trim();
            InitSystemCrmArg.ResultModel resultModel = new InitSystemCrmArg.ResultModel();
            try {
                log.info("fs-crm:initSystemCrm:start,tenantId {}", tenantId);
                initSystemCrmMenu(user);
                log.info("fs-crm:initSystemCrm:success,tenantId {}", tenantId);
                resultModel.setFlag(true);
            } catch (Exception e) {
                resultModel.setFlag(false);
                resultModel.setErrorMsg(e.getMessage());
                log.error("fs-crm:initSystemCrm:error,tenantId {} ", tenantId, e);
            }
            result.getResult().put(tenantId, resultModel);
        }
        return result;
    }

    @ServiceMethod("menu_add_items")
    public CrmMenuInitAddObjects.Result menuAddObjects(CrmMenuInitAddObjects.Arg arg, ServiceContext context) {
        CrmMenuInitAddObjects.Result result = CrmMenuInitAddObjects.Result.builder().result(Maps.newHashMap()).build();
        List<String> tenantIds = Arrays.asList(arg.getTenantIds().split(","));
        for (String tenantId : tenantIds) {
            User user = new User(tenantId, "-10000");
            InitSystemCrmArg.ResultModel resultModel = new InitSystemCrmArg.ResultModel();
            try {
                log.info("fs-crm:menuAddObjects:start,tenantId {}", tenantId);
                List<String> apiNameList = Lists.newArrayList(arg.getApiNames().split(","));
                createMenuItem(user, apiNameList, arg.getAfterApiName());
                log.info("fs-crm:menuAddObjects:success,tenantId {}", tenantId);
                resultModel.setFlag(true);
            } catch (Exception e) {
                resultModel.setFlag(false);
                resultModel.setErrorMsg(e.getMessage());
                log.error("fs-crm:menuAddObjects:error,tenantId {} ", tenantId, e);
            }
            result.getResult().put(tenantId, resultModel);
        }
        return result;
    }

    @ServiceMethod("menu_add_item")
    public CrmMenuInitAddObject.Result menuAddItem(CrmMenuInitAddObject.Arg arg, ServiceContext context) {
        CrmMenuInitAddObject.Result result = CrmMenuInitAddObject.Result.builder().result(true).build();
        String tenantId = arg.getTenantId();
        log.info("fs-crm:menuAddItem:start,tenantId {}", tenantId);
        User user = new User(tenantId, "-10000");
        try {
            menuCommonService.createMenuItem(user, arg.getApiName());
            log.info("fs-crm:menuAddItem:success,tenantId {}", tenantId);
        } catch (Exception e) {
            result.setResult(false);
            log.error("fs-crm:menuAddItem:error,tenantId {} ", tenantId, e);
        }
        return result;
    }

    /**
     * 企业初始化crm菜单和菜单项
     */
    public void initSystemCrmMenu(User user) {
        //1、初始化菜单
        IObjectData systemCrm = updateDefaultCrmMenu(user, true);
        String menuId = systemCrm.getId();
        //2、初始化预制对象、自定义对象菜单项
        List<IObjectData> menuItemList = buildObjectMenuItem(user, menuId);
        serviceFacade.bulkSaveObjectData(menuItemList, user);
    }

    /**
     * 构建预设对象菜单项和自定义对象CRM菜单
     */
    private List<IObjectData> buildObjectMenuItem(User user, String menuId) {
        IObjectDescribe menuItemDescribe = serviceFacade.findObject(user.getTenantId(), MenuConstants.MENU_ITEM_API_NAME);
        //配置的预设对象菜单
        List<MenuItemConfigObject> configObjectList = MenuConstants.configMenuItemList;
        List<IObjectData> menuItemList = configObjectList.stream().map(menuItemConfigObject -> {
            IObjectData menuItem = menuCommonService.buildMenuItem(user, menuId, menuItemDescribe, menuItemConfigObject.getApiName());
            menuItem.set(MenuConstants.MenuItemField.NUMBER.getApiName(), menuItemConfigObject.getNumber());
            return menuItem;
        }).collect(Collectors.toList());
        //自定义对象菜单
        List<IObjectDescribe> customDescribeList = crmMenuAdminService.findUserCustomDescribe(user, false, false, true);
        //自定义对象默认排序号从10000开始
        AtomicInteger order = new AtomicInteger(10000);
        //此apinames是为了验证保证最后生成菜单的apiname不重复
        Set apiNames = Sets.newHashSet();
        List<IObjectData> customMenuItemList = customDescribeList.stream().filter(k -> !apiNames.contains(k.getApiName())).map(k -> {
            apiNames.add(k.getApiName());
            IObjectData menuItem = menuCommonService.buildMenuItem(user, menuId, menuItemDescribe, k.getApiName());
            menuItem.set(MenuConstants.MenuItemField.NUMBER.getApiName(), order.intValue());
            order.getAndAdd(5);
            return menuItem;
        }).collect(Collectors.toList());
        menuItemList.addAll(customMenuItemList);
        return menuItemList;
    }

    private ObjectData buildSystemCrm(User user, IObjectDescribe describe) {
        ObjectData objectData = new ObjectData();
        objectData.setDescribeApiName(MenuConstants.MENU_API_NAME);
        objectData.set(IObjectData.NAME, "CRM");
        objectData.set(MenuConstants.MenuField.MODULE.getApiName(), MenuConstants.MenuField.MODULE_CRMINDEX.getApiName());
        objectData.set(MenuConstants.MenuField.ISSTYTEM.getApiName(), true);
        objectData.set(MenuConstants.MenuField.ACTIVESTATUS.getApiName(), MenuConstants.MenuField.ACTIVESTATUS_ON.getApiName());
        objectData.set(MenuConstants.MenuField.APINAME.getApiName(), MenuConstants.MENU_SYSTEM_APINAME);
        objectData.setTenantId(user.getTenantId());
        objectData.setDescribeId(describe.getId());
        return objectData;
    }

    /**
     * 在预制CRM菜单下对象新增对象菜单项
     * apiName 要增加的对象apiName
     * afterApiName 当前apiname要跟随指定的apiname后
     */
    @Transactional
    public void createMenuItem(User user, List<String> apiNameList, String afterApiName) {
        IObjectDescribe menuItemDescribe = serviceFacade.findObject(user.getTenantId(), MenuConstants.MENU_ITEM_API_NAME);

        IObjectData systemCrm = crmMenuAdminService.findDefaultCrmMenu(user);

        String menuId = systemCrm.getId();
        //如果发现apiname重复，则报错
        if (CollectionUtils.isNotEmpty(menuCommonService.findMenuItemByApiName(user, menuId, apiNameList))) {
            throw new ValidateException(String.format("生成失败，当前对象已生成菜单项，apiName %s", apiNameList));
        }
        if (StringUtils.isEmpty(afterApiName)) {
            this.insertMenuItemInLast(user, menuItemDescribe, menuId, apiNameList);
            return;
        }
        //需要跟随在某对象之后时
        List<IObjectData> menuItemList = crmMenuActionService.findMenuItems(user, menuId);
        Integer number = null;
        Boolean start = false;
        List<IObjectData> updateObjectList = Lists.newLinkedList();
        List<IObjectData> createObjectList = Lists.newLinkedList();
        for (IObjectData menuItem : menuItemList) {
            String apiName = menuItem.get(MenuConstants.MenuItemField.REFERENCEAPINAME.getApiName(), String.class);
            if (start) {
                //后面所有需要更新number的值
                menuItem.set(MenuConstants.MenuItemField.NUMBER.getApiName(), number += 5);
                updateObjectList.add(menuItem);
            }
            //找到指定的apiname后，跟踪记录后面所有的菜单项，更新number并更新数据库
            if (afterApiName.equals(apiName)) {
                start = true;
                number = menuItem.get(MenuConstants.MenuItemField.NUMBER.getApiName(), Integer.class);
                createObjectList.addAll(buildMenuItemStartWithNumber(user, menuId, menuItemDescribe, apiNameList, number));
                //buildMenuItemStartWithNumber方法中，每一个apiname递增5的幅度
                number = (apiNameList.size() * 5) + number;
            }
        }
        if (!start) {
            //如果菜单下没有找到afterApiName，则默认插入到最后
            this.insertMenuItemInLast(user, menuItemDescribe, menuId, apiNameList);
        }
        if (CollectionUtils.isNotEmpty(updateObjectList)) {
            crmMenuActionService.bulkUpdateDataWithoutPrivilege(user, updateObjectList);
        }
        if (CollectionUtils.isNotEmpty(createObjectList)) {
            serviceFacade.bulkSaveObjectData(createObjectList, user);
        }
    }

    /**
     * 1、查找默认的CRM菜单
     * 2、有默认菜单时，更新，然后删除当前菜单关联的菜单项数据、角色数据、个人工作台数据
     * 3、没有就重新生成CRM菜单
     * whenExistRelateDelete:存在关联数据时，是否删除关联的数据
     */
    public IObjectData updateDefaultCrmMenu(User user, Boolean whenExistRelateDelete) {
        IObjectData systemCrm = crmMenuAdminService.findDefaultCrmMenu(user);
        IObjectDescribe describe = serviceFacade.findObject(user.getTenantId(), MenuConstants.MENU_API_NAME);
        IObjectData newSystemCrm = buildSystemCrm(user, describe);
        if (systemCrm != null) {
            if (whenExistRelateDelete) {
                crmMenuActionService.deleteMenuRelate(user, systemCrm);
            }
            newSystemCrm.setId(systemCrm.getId());
            serviceFacade.updateObjectData(user, newSystemCrm);
        } else {
            serviceFacade.saveObjectData(user, newSystemCrm);
        }
        return newSystemCrm;
    }

    private void insertMenuItemInLast(User user, IObjectDescribe menuItemDescribe, String menuId, List<String> apiNameList) {
        IObjectData lastMenuItem = menuCommonService.findLastCrmMenuItem(user, menuId);
        Integer lastOrder = lastMenuItem.get(MenuConstants.MenuItemField.NUMBER.getApiName(), Integer.class);
        List<IObjectData> menuItemList = this.buildMenuItemStartWithNumber(user, menuId, menuItemDescribe, apiNameList, lastOrder);
        serviceFacade.bulkSaveObjectData(menuItemList, user);
    }

    private List<IObjectData> buildMenuItemStartWithNumber(User user, String menuId, IObjectDescribe menuItemDescribe, List<String> apiNameList, Integer number) {
        AtomicInteger order = new AtomicInteger(number);
        return apiNameList.stream().map(apiName -> {
            order.getAndAdd(5);
            IObjectData newMenuItem = menuCommonService.buildMenuItem(user, menuId, menuItemDescribe, apiName);
            newMenuItem.set(MenuConstants.MenuItemField.NUMBER.getApiName(), order.intValue());
            return newMenuItem;
        }).collect(Collectors.toList());
    }
}
