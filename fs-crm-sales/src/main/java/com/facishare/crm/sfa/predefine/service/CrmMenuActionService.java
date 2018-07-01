package com.facishare.crm.sfa.predefine.service;

import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.search.OrderBy;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.facishare.paas.metadata.service.impl.ObjectDataServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.facishare.crm.sfa.utilities.common.convert.SearchUtil;
import com.facishare.paas.appframework.common.util.StopWatch;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ActionContextExt;
import com.facishare.paas.appframework.metadata.exception.MetaDataBusinessException;
import com.facishare.paas.appframework.metadata.menu.MenuCommonService;
import com.facishare.paas.appframework.metadata.menu.MenuConstants;
import com.facishare.paas.appframework.metadata.menu.model.MenuItemConfigObject;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.ObjectData;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CrmMenuActionService {
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private ObjectDataServiceImpl objectDataService;
    @Autowired
    private MenuCommonService menuCommonService;
    @Autowired
    private CrmMenuAdminService crmMenuAdminService;

    @Transactional
    public void createOrUpdateMenu(User user, IObjectData objectData, List<String> roleIdList, List<String> detailObjects) {
        //处理菜单关联的对象，生成菜单项
        if (CollectionUtils.isEmpty(detailObjects)) {
            throw new ValidateException("关联的对象不能为空");
        }
        StopWatch stopWatch = StopWatch.create("CrmMenuActionService createOrUpdateMenu");
        Boolean isSystemObj = objectData.get(MenuConstants.MenuField.ISSTYTEM.getApiName(), Boolean.class);
        Boolean isSystem = isSystemObj != null && isSystemObj ? true : false;
        //预制crm菜单不验证适用角色
        if (!isSystem && CollectionUtils.isEmpty(roleIdList)) {
            throw new ValidateException("适用角色不能为空");
        }
        IObjectDescribe describe = serviceFacade.findObject(user.getTenantId(), MenuConstants.MENU_API_NAME);
        stopWatch.lap("get describe");
        objectData.setTenantId(user.getTenantId());
        String menuId = objectData.getId() != null && StringUtils.isNotBlank(objectData.getId()) ? objectData.getId() : null;
        //保存或更新菜单对象
        objectData.setDescribeApiName(describe.getApiName());
        objectData.setDescribeId(describe.getId());
        if (StringUtils.isEmpty(menuId)) {
            serviceFacade.saveObjectData(user, objectData);
            menuId = objectData.getId();
        } else {
            serviceFacade.updateObjectData(user, objectData);
        }
        stopWatch.lap("save or update menu finished");
        this.saveOrUpdateMenuItem(user, menuId, detailObjects);
        stopWatch.lap("saveOrUpdateMenuItem finished");
        if (!isSystem) {
            //预制crm菜单不保存适用角色
            this.saveRoleSource(user, menuId, roleIdList);
            stopWatch.lap("save roleSource finished");
        }
        stopWatch.logSlow(2000);
    }

    @Transactional
    public void createWorkBench(User user, String menuId, List<IObjectData> dataList) {
        //删除
        StopWatch stopWatch = StopWatch.create("CrmMenuActionService createWorkBench");
        //删除个人旧工作台设置
        deleteWorkBenchByUser(user, menuId);
        stopWatch.lap("deleteWorkBench finished");
        serviceFacade.bulkSaveObjectData(dataList, user);
        stopWatch.lap("save workBench data finished");
        stopWatch.logSlow(2000);
    }

    /**
     * 系统CRM菜单恢复默认，初始化
     */
    @Transactional
    public void resetDefaultMenu(User user, String menuId) {
        //配置的预设对象菜单
        List<MenuItemConfigObject> configObjectList = MenuConstants.configMenuItemList;
        List<String> systemMenuApiNameList = configObjectList.stream().map(menuItemConfigObject -> menuItemConfigObject.getApiName()).collect(Collectors.toList());
        //自定义对象菜单
        List<IObjectDescribe> customDescribeList = crmMenuAdminService.findUserCustomDescribe(user, false, false, true);
        //此apinames是为了验证保证最后生成菜单的apiname不重复
        Set apiNames = Sets.newHashSet();
        List<String> customMenuApiNameList = customDescribeList.stream().filter(k -> !apiNames.contains(k.getApiName())).map(k -> k.getApiName()).collect(Collectors.toList());
        systemMenuApiNameList.addAll(customMenuApiNameList);
        //指定顺序重置
        saveOrUpdateMenuItem(user, menuId, systemMenuApiNameList);
    }

    /**
     * 保存或更新菜单项
     */
    private void saveOrUpdateMenuItem(User user, String menuId, List<String> detailObjects) {
        StopWatch stopWatch = StopWatch.create("CrmMenuActionService saveOrUpdateMenuItem");
        List<IObjectData> menuItemList = findMenuItems(user, menuId);
        Map<String, IObjectData> menuItemApiNameMap = Maps.newHashMap();
        menuItemList.forEach(k -> {
            menuItemApiNameMap.put(k.get(MenuConstants.MenuItemField.REFERENCEAPINAME.getApiName(), String.class), k);
        });
        IObjectDescribe menuItemDescribe = serviceFacade.findObject(user.getTenantId(), MenuConstants.MENU_ITEM_API_NAME);
        List<IObjectData> menuItemCreateList = Lists.newArrayList();
        List<IObjectData> menuItemUpdateList = Lists.newArrayList();
        //此apinames是为了验证保证最后生成菜单的apiname不重复，因为有些脏数据的原因
        stopWatch.lap("get old menuItems");
        Set apiNames = Sets.newHashSet();
        for (int i = 0; i < detailObjects.size(); i++) {
            String apiName = detailObjects.get(i);
            if (apiNames.contains(apiName)) {
                continue;
            }
            apiNames.add(apiName);
            IObjectData menuItem = null;
            if (menuItemApiNameMap.get(apiName) != null) {
                menuItem = menuItemApiNameMap.get(apiName);
                //如果已有该菜单，则只做更新操作，更新排序号
                menuItem.set(MenuConstants.MenuItemField.NUMBER.getApiName(), i);
                menuItemUpdateList.add(menuItem);
                //更新完成后map中移除当前已处理的菜单项
                menuItemApiNameMap.remove(apiName);
            } else {
                menuItem = menuCommonService.buildMenuItem(user, menuId, menuItemDescribe, apiName);
                menuItem.set(MenuConstants.MenuItemField.NUMBER.getApiName(), i);
                menuItemCreateList.add(menuItem);
            }
        }
        stopWatch.lap("build menu items");
        //修改数据
        this.bulkUpdateDataWithoutPrivilege(user, menuItemUpdateList);
        stopWatch.lap("bulkUpdateDataWithoutPrivilege");

        //新增数据
        if (CollectionUtils.isNotEmpty(menuItemCreateList)) {
            serviceFacade.bulkSaveObjectData(menuItemCreateList, user);
            stopWatch.lap("bulkSaveObjectData");
        }

        //删除的数据
        serviceFacade.bulkInvalidAndDeleteWithSuperPrivilege(menuItemApiNameMap.values().stream().collect(Collectors.toList()), user);
        stopWatch.lap("bulkInvalidAndDeleteWithSuperPrivilege");
        stopWatch.logSlow(2000);

    }

    public List<IObjectData> findMenuItems(User user, String menuId) {
        return this.findMenuItems(user, Lists.newArrayList(menuId));
    }

    /*
     * 根据菜单id获取所有菜单项列表,根据order升序
     */
    public List<IObjectData> findMenuItems(User user, List<String> menuIdList) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        //每个企业有30个菜单，最多300个自定义对象，目前有20多个老对象，也就是20*(300+20)
        searchQuery.setLimit(20000);
        List<IFilter> filters = searchQuery.getFilters();
        searchQuery.setOrders(Lists.newArrayList(new OrderBy(MenuConstants.MenuItemField.NUMBER.getApiName(), true)));
        SearchUtil.fillFilterIn(filters, MenuConstants.MenuItemField.MENUID.getApiName(), menuIdList);
        QueryResult<IObjectData> dataQueryResult = serviceFacade.findBySearchQuery(user, MenuConstants.MENU_ITEM_API_NAME, searchQuery);
        return dataQueryResult.getData();
    }

    /**
     * 保存菜单和角色的关联关系
     */
    private void saveRoleSource(User user, String menuId, List<String> roleIdList) {
        StopWatch stopWatch = StopWatch.create("CrmMenuActionService saveRoleSource");
        IObjectDescribe roleSourceDescribe = serviceFacade.findObject(user.getTenantId(), MenuConstants.ROLE_SOURCE_API_NAME);
        stopWatch.lap("find describe finished");
        //全部删除
        this.deleteRoleSource(user, menuId);
        stopWatch.lap("delete old role source finished");
        //重新生成并保存
        List<IObjectData> objectList = roleIdList.stream().map(roleId -> {
            IObjectData objectData = new ObjectData();
            objectData.set(MenuConstants.RoleSourceField.ROLE_ID.getApiName(), roleId);
            objectData.set(MenuConstants.RoleSourceField.SOURCETYPE.getApiName(), MenuConstants.RoleSourceField.SOURCETYPE_MENU.getApiName());
            objectData.set(MenuConstants.RoleSourceField.SOURCEID.getApiName(), menuId);
            objectData.setTenantId(user.getTenantId());
            objectData.setDescribeId(roleSourceDescribe.getId());
            objectData.setDescribeApiName(roleSourceDescribe.getApiName());
            return objectData;
        }).collect(Collectors.toList());
        stopWatch.lap("build roleObjectDataList finished");
        serviceFacade.bulkSaveObjectData(objectList, user);
        stopWatch.lap("save roleSource finished");
        stopWatch.logSlow(1000);
    }

    /**
     * 业务删除菜单关联的数据
     */
    @Transactional
    public void deleteMenuRelate(User user, IObjectData menuObject) {
        // TODO: 2018/1/22 保珠的删除方法没有删除索引和namecache
        String menuId = menuObject.getId();
        //删除菜单下工作台
        SearchTemplateQuery workbenchDeleteQuery = new SearchTemplateQuery();
        SearchUtil.fillFilterEq(workbenchDeleteQuery.getFilters(), MenuConstants.MenuWorkBenchField.MENUID.getApiName(), menuId);
        this.deleteBySearchTemplate(user, MenuConstants.MENU_WORKBENCH_API_NAME, workbenchDeleteQuery);
        //删除菜单项
        SearchTemplateQuery menuItemQuery = new SearchTemplateQuery();
        SearchUtil.fillFilterEq(menuItemQuery.getFilters(), MenuConstants.MenuItemField.MENUID.getApiName(), menuId);
        this.deleteBySearchTemplate(user, MenuConstants.MENU_ITEM_API_NAME, menuItemQuery);
        //删除菜单角色关系
        this.deleteRoleSource(user, menuId);
    }

    /**
     * 删除用户自身关联的工作台
     */
    private void deleteWorkBenchByUser(User user, String menuId) {
        SearchTemplateQuery workbenchDeleteQuery = new SearchTemplateQuery();
        SearchUtil.fillFilterEq(workbenchDeleteQuery.getFilters(), MenuConstants.MenuWorkBenchField.MENUID.getApiName(), menuId);
        SearchUtil.fillFilterEq(workbenchDeleteQuery.getFilters(), MenuConstants.MenuWorkBenchField.USERID.getApiName(), user.getUserId());
        this.deleteBySearchTemplate(user, MenuConstants.MENU_WORKBENCH_API_NAME, workbenchDeleteQuery);
    }

    /**
     * 根据菜单id删除角色和资源的关联
     */
    private void deleteRoleSource(User user, String menuId) {
        SearchTemplateQuery roleSourceQuery = new SearchTemplateQuery();
        SearchUtil.fillFilterEq(roleSourceQuery.getFilters(), MenuConstants.RoleSourceField.SOURCEID.getApiName(), menuId);
        this.deleteBySearchTemplate(user, MenuConstants.ROLE_SOURCE_API_NAME, roleSourceQuery);
    }

    public void deleteBySearchTemplate(User user, String apiName, SearchTemplateQuery searchTemplateQuery) {
        try {
            objectDataService.deleteBySearchTemplate(user.getTenantId(), apiName, searchTemplateQuery, ActionContextExt.of(user).getContext());
        } catch (MetadataServiceException e) {
            log.error("deleteBySearchTemplate error ,tenantId {},apiName {},filters {}", user.getTenantId(), apiName, searchTemplateQuery, e);
            throw new MetaDataBusinessException(e.getMessage());
        }
    }

    public void bulkUpdateDataWithoutPrivilege(User user, List<IObjectData> objectDataList) {
        ActionContextExt context = ActionContextExt.of(user);
        context.setPrivilegeCheck(false);
        try {
            if (CollectionUtils.isNotEmpty(objectDataList)) {
                objectDataService.batchUpdate(objectDataList, context.getContext());
            }
        } catch (MetadataServiceException e) {
            throw new MetaDataBusinessException(e.getMessage());
        }
    }
}
