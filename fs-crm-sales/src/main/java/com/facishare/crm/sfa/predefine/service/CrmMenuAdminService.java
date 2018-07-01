package com.facishare.crm.sfa.predefine.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.facishare.crm.sfa.predefine.service.model.CrmAdminCreate;
import com.facishare.crm.sfa.predefine.service.model.CrmAdminEnableDisableArg;
import com.facishare.crm.sfa.predefine.service.model.CrmAdminView;
import com.facishare.crm.sfa.predefine.service.model.CrmMenuAdminList;
import com.facishare.crm.sfa.predefine.service.model.MenuItemObject;
import com.facishare.crm.sfa.predefine.service.model.MenuRoleArg;
import com.facishare.crm.sfa.predefine.service.model.ValidateMenuName;
import com.facishare.crm.sfa.predefine.version.VersionService;
import com.facishare.crm.sfa.utilities.common.convert.SearchUtil;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ObjectDescribeDocument;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.exception.MetaDataBusinessException;
import com.facishare.paas.appframework.metadata.menu.MenuConstants;
import com.facishare.paas.appframework.metadata.menu.model.MenuItemConfigObject;
import com.facishare.paas.appframework.privilege.FunctionPrivilegeService;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.search.OrderBy;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;

import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * crm菜单后台设置类
 */
@ServiceModule("crm_menu_admin")
@Component
@Slf4j
public class CrmMenuAdminService {
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    FunctionPrivilegeService functionPrivilegeService;
    @Autowired
    private IObjectDescribeService objectDescribeService;
    @Autowired
    private CrmMenuActionService crmMenuActionService;
    @Autowired
    private VersionService versionService;

    /**
     * 菜单设置-菜单列表页接口
     */
    @ServiceMethod("menu_list")
    public CrmMenuAdminList.Result menuList(ServiceContext context) {
        SearchTemplateQuery menuSearchQuery = new SearchTemplateQuery();
        fillMenuQueryAddOrder(menuSearchQuery);
        menuSearchQuery.setLimit(1000);
        QueryResult<IObjectData> menuList = serviceFacade.findBySearchQuery(context.getUser(), MenuConstants.MENU_API_NAME, menuSearchQuery);
        //获取所有菜单的id
        List<String> menuIdList = menuList.getData().stream().map(objectData -> objectData.getId()).collect(Collectors.toList());
        //查询角色
        Map<String, MenuRoleArg> roleCodeMap = findAllRoleCodeMap(context.getUser());
        Map<String, List<String>> roleSourceMap = this.findRoleSourceId(context.getUser(), menuIdList);
        menuList.getData().forEach(objectData -> {
            Boolean isSystem = objectData.get(MenuConstants.MenuField.ISSTYTEM.getApiName(), Boolean.class);
            if (!isSystem) {
                List<String> roleIdList = roleSourceMap.get(objectData.getId());
                List<String> roleNameList = roleIdList.stream().filter(k -> Objects.nonNull(roleCodeMap.get(k)))
                        .map(k -> roleCodeMap.get(k).getDisplay_name()).collect(Collectors.toList());
                objectData.set("role_name_list", roleNameList);
            }
        });
        //查询所有的角色列表
        return CrmMenuAdminList.Result.builder().menuList(menuList.getData().stream().map(k -> ObjectDataDocument.of(k)).collect(Collectors.toList())).build();
    }

    /**
     * 菜单设置-菜单项选择接口
     */
    @ServiceMethod("menu_item_list")
    public List<MenuItemObject> menuItemList(ServiceContext context) {
        return this.findAllApiNamesList(context.getUser());
    }

    /**
     * 菜单设置-默认CRM菜单重置初始化
     */
    @ServiceMethod("menu_reset")
    public CrmAdminEnableDisableArg.Result menuReset(ServiceContext context) {
        IObjectData systemCrm = this.findDefaultCrmMenu(context.getUser());
        String menuId = systemCrm.getId();
        crmMenuActionService.resetDefaultMenu(context.getUser(), menuId);
        return CrmAdminEnableDisableArg.Result.builder().result(true).build();
    }

    /**
     * 菜单角色选择接口
     */
    @ServiceMethod("role_list")
    public List<MenuRoleArg> roleList(ServiceContext context) {
        return this.findAllRoleCodeList(context.getUser());
    }

    /**
     * 菜单配置-保存/编辑接口
     */
    @ServiceMethod("create")
    public CrmAdminCreate.Result create(CrmAdminCreate.Arg arg, ServiceContext context) {
        IObjectData objectData = arg.getObjectData().toObjectData();
        if (Objects.isNull(objectData.getId())) {
            //编辑时不验证个数
            validateMenuLimit(context.getUser());
        }
        crmMenuActionService.createOrUpdateMenu(context.getUser(), objectData, arg.getRoleIdList(), arg.getDetailMenuItems());
        return CrmAdminCreate.Result.builder().result(true).build();
    }

    /**
     * 菜单配置-详情接口
     */
    @ServiceMethod("view")
    public CrmAdminView.Result view(CrmAdminView.Arg arg, ServiceContext context) {
        String menuId = arg.getMenuId();
        IObjectData objectData = findMenuById(context.getUser(), menuId);
        Map<String, List<String>> roleSourceIdMap = findRoleSourceId(context.getUser(), Lists.newArrayList(menuId));
        Collection<String> roleIdList = roleSourceIdMap.getOrDefault(menuId, Lists.newArrayList());
        //移除已经不存在的角色
        Map<String, MenuRoleArg> allRoleCodeMap = findAllRoleCodeMap(context.getUser());
        roleIdList.removeIf(k -> !allRoleCodeMap.containsKey(k));

        //移除不存在的对象
        List<IObjectData> menuItemList = crmMenuActionService.findMenuItems(context.getUser(), menuId);
        List<String> objectsList = getMenuItemsObjects(menuItemList);
        Map<String, MenuItemObject> allApiNameMap = findAllApiNamesMap(context.getUser());
        objectsList.removeIf(k -> !allApiNameMap.containsKey(k));

        IObjectDescribe describe = serviceFacade.findObject(context.getTenantId(), MenuConstants.MENU_API_NAME);
        return CrmAdminView.Result.builder().objectData(ObjectDataDocument.of(objectData)).roleIdList(roleIdList)
                .detailMenuItems(objectsList).describe(ObjectDescribeDocument.of(describe)).build();
    }

    /**
     * 菜单配置-验证名称是否重复
     */
    @ServiceMethod("validate_menu_name")
    public ValidateMenuName.Result validateMenuName(ValidateMenuName.Arg arg, ServiceContext context) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(1);
        List<IFilter> filters = searchQuery.getFilters();
        SearchUtil.fillFilterEq(filters, IObjectData.NAME, arg.getName());
        QueryResult<IObjectData> dataQueryResult = serviceFacade.findBySearchQuery(context.getUser(), MenuConstants.MENU_API_NAME, searchQuery);
        IObjectData objectData = CollectionUtils.isNotEmpty(dataQueryResult.getData()) ? dataQueryResult.getData().get(0) : null;
        if (objectData != null && objectData.getId().equals(arg.getMenuId())) {
            return ValidateMenuName.Result.builder().result(true).build();
        }
        return ValidateMenuName.Result.builder().result(dataQueryResult.getTotalNumber() > 0 ? false : true).build();
    }

    /**
     * 菜单配置-验证apiname是否重复
     */
    @ServiceMethod("validate_menu_apiname")
    public ValidateMenuName.Result validateMenuApiname(ValidateMenuName.ApinameArg arg, ServiceContext context) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(1);
        List<IFilter> filters = searchQuery.getFilters();
        SearchUtil.fillFilterEq(filters, MenuConstants.MenuField.APINAME.getApiName(), arg.getApiname());
        QueryResult<IObjectData> dataQueryResult = serviceFacade.findBySearchQuery(context.getUser(), MenuConstants.MENU_API_NAME, searchQuery);
        return ValidateMenuName.Result.builder().result(dataQueryResult.getTotalNumber() > 0 ? false : true).build();
    }

    /**
     * 菜单配置-验证名称是否重复
     */
    @ServiceMethod("validate_menu_limit")
    public ValidateMenuName.Result validateMenuLimit(ServiceContext context) {
        validateMenuLimit(context.getUser());
        return ValidateMenuName.Result.builder().result(true).build();
    }

    /**
     * 菜单配置-启用接口
     */
    @ServiceMethod("enable")
    public CrmAdminEnableDisableArg.Result enable(CrmAdminEnableDisableArg.Arg arg, ServiceContext context) {
        String menuId = arg.getMenuId();
        User user = context.getUser();
        IObjectData menu = findMenuById(context.getUser(), menuId);
        if (MenuConstants.MenuField.ACTIVESTATUS_OFF.getApiName().equals(menu.get(MenuConstants.MenuField.ACTIVESTATUS.getApiName(), String.class))) {
            menu.set(MenuConstants.MenuField.ACTIVESTATUS.getApiName(), MenuConstants.MenuField.ACTIVESTATUS_ON.getApiName());
            serviceFacade.updateObjectData(user, menu);
        }
        return CrmAdminEnableDisableArg.Result.builder().result(true).build();
    }

    /**
     * 菜单配置-禁用接口
     */
    @ServiceMethod("disable")
    public CrmAdminEnableDisableArg.Result disable(CrmAdminEnableDisableArg.Arg arg, ServiceContext context) {
        String menuId = arg.getMenuId();
        User user = context.getUser();
        IObjectData menu = findMenuById(context.getUser(), menuId);
        if (menu.get(MenuConstants.MenuField.ISSTYTEM.getApiName(), Boolean.class)) {
            throw new ValidateException("预设CRM菜单不能禁用");
        }
        if (MenuConstants.MenuField.ACTIVESTATUS_ON.getApiName().equals(menu.get(MenuConstants.MenuField.ACTIVESTATUS.getApiName(), String.class))) {
            menu.set(MenuConstants.MenuField.ACTIVESTATUS.getApiName(), MenuConstants.MenuField.ACTIVESTATUS_OFF.getApiName());
            serviceFacade.updateObjectData(user, menu);
        }
        return CrmAdminEnableDisableArg.Result.builder().result(true).build();
    }

    /**
     * 菜单配置-删除菜单接口
     */
    @ServiceMethod("delete")
    @Transactional(rollbackFor = Exception.class)
    public CrmAdminEnableDisableArg.Result delete(CrmAdminEnableDisableArg.Arg arg, ServiceContext context) {
        String menuId = arg.getMenuId();
        User user = context.getUser();
        IObjectData menu = findMenuById(context.getUser(), menuId);
        if (menu.get(MenuConstants.MenuField.ISSTYTEM.getApiName(), Boolean.class)) {
            throw new ValidateException("预设CRM菜单不能删除");
        }
        crmMenuActionService.deleteMenuRelate(user, menu);
        //最后删除菜单
        serviceFacade.bulkInvalidAndDeleteWithSuperPrivilege(Lists.newArrayList(menu), user);
        return CrmAdminEnableDisableArg.Result.builder().result(true).build();
    }

    /**
     * 获取所有菜单对象
     */
    private Map<String, MenuItemObject> findAllApiNamesMap(User user) {
        List<MenuItemObject> itemList = findAllApiNamesList(user);
        Map<String, MenuItemObject> map = Maps.newHashMap();
        itemList.forEach(k -> map.put(k.getApi_name(), k));
        return map;
    }

    /**
     * 获取所有菜单对象列表
     */
    private List<MenuItemObject> findAllApiNamesList(User user) {
        List<MenuItemConfigObject> configObjectList = MenuConstants.configMenuItemList;
        //过滤掉版本中不存在的对象
        Set<String> supportApiNames = configObjectList.stream().map(menuItemConfigObject -> menuItemConfigObject.getApiName()).collect(Collectors.toSet());
        versionService.filterSupportObj(user.getTenantId(), supportApiNames);

        List<String> needValidateDescribe = configObjectList.stream().filter(k -> k.getValidateDescribe()).map(k -> k.getApiName()).collect(Collectors.toList());
        Map<String, IObjectDescribe> needValidateDescribeMap = this.findDescribeListByApiNamesWithoutFields(user, needValidateDescribe, ObjectAction.VIEW_LIST.getActionCode());
        //过滤掉对象中已被禁用或删除的对象
        List<MenuItemObject> objectList = configObjectList.stream()
                .filter(k -> supportApiNames.contains(k.getApiName()))
                .filter(k -> !k.getValidateDescribe() || needValidateDescribeMap.get(k.getApiName()) != null).map(k -> {
                    IObjectDescribe describe = needValidateDescribeMap.get(k.getApiName());
                    MenuItemObject arg = MenuItemObject.builder()
                            .display_name(describe == null ? k.getDisplayName() : describe.getDisplayName())
                            .api_name(k.getApiName())
                            .icon_index(k.getIconIndex())
                            .is_active(!k.getValidateDescribe() ? true : describe.isActive())
                            .build();
                    return arg;
                }).collect(Collectors.toList());
        //查询自定义对象
        List<IObjectDescribe> customDescribeList = this.findUserCustomDescribe(user, false, false, true);
        List<MenuItemObject> customObjectList = customDescribeList.stream().map(k -> {
            MenuItemObject arg = MenuItemObject.builder()
                    .display_name(k.getDisplayName())
                    .api_name(k.getApiName())
                    .icon_index(k.getIconIndex())
                    .is_active(k.isActive())
                    .build();
            return arg;
        }).collect(Collectors.toList());
        objectList.addAll(customObjectList);
        return objectList;
    }

    /**
     * 获取所有角色列表
     */
    public List<MenuRoleArg> findAllRoleCodeList(User user) {
        List<Map<String, String>> roleCodeList = functionPrivilegeService.getRoleList(user);
        List<MenuRoleArg> roleList = roleCodeList.stream().map(k -> {
            MenuRoleArg roleArg = MenuRoleArg.builder()
                    .role_id(k.get("roleCode")).display_name(k.get("roleName")).build();
            return roleArg;
        }).collect(Collectors.toList());
        return roleList;
    }

    /**
     * 获取所有角色列表
     */
    public Map<String, MenuRoleArg> findAllRoleCodeMap(User user) {
        List<MenuRoleArg> roleCodeList = findAllRoleCodeList(user);
        Map roleMap = Maps.newHashMap();
        roleCodeList.forEach(k -> roleMap.put(k.getRole_id(), k));
        return roleMap;
    }

    /*
     * 根据资源id获取角色资源id
     * @param user
     * @param sourceIdList
     * @return key代表sourceId,value代表角色集合
     */
    public Map<String, List<String>> findRoleSourceId(User user, List<String> sourceIdList) {
        List<IObjectData> roleSourceList = findRoleSourceData(user, sourceIdList);
        Map<String, List<String>> map = Maps.newHashMap();
        roleSourceList.forEach(objectData -> {
            String sourceId = objectData.get(MenuConstants.RoleSourceField.SOURCEID.getApiName(), String.class);
            map.computeIfAbsent(sourceId, k -> Lists.newLinkedList());
            map.get(sourceId).add(objectData.get(MenuConstants.RoleSourceField.ROLE_ID.getApiName(), String.class));
        });
        return map;
    }

    public void validateMenuLimit(User user) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(100);
        QueryResult<IObjectData> dataQueryResult = serviceFacade.findBySearchQuery(user, MenuConstants.MENU_API_NAME, searchQuery);
        if (dataQueryResult.getTotalNumber() >= 30) {
            throw new ValidateException("您所在企业目前支持30个自定义菜单");
        }
    }

    /*
     * 根据资源id获取角色资源数据
     * @param user
     * @param sourceIdList
     */
    public List<IObjectData> findRoleSourceData(User user, List<String> sourceIdList) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(20000);
        List<IFilter> filters = searchQuery.getFilters();
        SearchUtil.fillFilterIn(filters, MenuConstants.RoleSourceField.SOURCEID.getApiName(), sourceIdList);
        SearchUtil.fillFilterEq(filters, MenuConstants.RoleSourceField.SOURCETYPE.getApiName(), MenuConstants.RoleSourceField.SOURCETYPE_MENU.getApiName());
        QueryResult<IObjectData> roleSourceList = serviceFacade.findBySearchQuery(user, MenuConstants.ROLE_SOURCE_API_NAME, searchQuery);
        return roleSourceList.getData();
    }

    /**
     * 根据菜单项集合获取内部关联的对象集合
     */
    private List<String> getMenuItemsObjects(List<IObjectData> menuItemsList) {
        return menuItemsList.stream().map(k -> k.get(MenuConstants.MenuItemField.REFERENCEAPINAME.getApiName(), String.class))
                .collect(Collectors.toList());
    }

    /**
     * 获取菜单数据
     */
    public IObjectData findMenuById(User user, String menuId) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(1);
        List<IFilter> filters = searchQuery.getFilters();
        SearchUtil.fillFilterEq(filters, IFieldDescribe.ID, menuId);
        QueryResult<IObjectData> dataQueryResult = serviceFacade.findBySearchQuery(user, MenuConstants.MENU_API_NAME, searchQuery);
        if (CollectionUtils.isEmpty(dataQueryResult.getData())) {
            throw new ValidateException("菜单不存在");
        }
        return dataQueryResult.getData().get(0);
    }

    /**
     * 查找所有自定义对象的describe
     */
    public List<IObjectDescribe> findUserCustomDescribe(User user, Boolean isOnlyActivate, Boolean isExcludeDetailObj, Boolean isAsc) {
        Document example = new Document();
        example.put("package", "CRM");
        example.put("define_type", "custom");

        if (isOnlyActivate) {
            example.put("is_active", Boolean.TRUE);
        }

        if (isExcludeDetailObj) {
            example.put("exclude_detail", Boolean.TRUE);
        }

        if (isAsc) {
            example.put("is_asc", Boolean.TRUE);
        }
        List<IObjectDescribe> describeList;
        try {
            describeList = this.objectDescribeService.findByExample(user.getTenantId(), example);
        } catch (MetadataServiceException e) {
            log.warn("findByExample error,tenantId:{},example:{},e", new Object[]{user.getTenantId(), example, e});
            throw new MetaDataBusinessException(e.getMessage());
        }

        return describeList.stream().filter(Objects::nonNull).filter((x) -> {
            return !"bi".equals(x.getVisibleScope());
        }).collect(Collectors.toList());
    }

    public Map<String, IObjectDescribe> findDescribeListByApiNamesWithoutFields(User user, List<String> apiNameList, String actionCode) {
        String tenantId = user.getTenantId();
        try {
            List<IObjectDescribe> list = objectDescribeService.findDescribeListByApiNamesWithoutFields(tenantId, apiNameList);
            //add 20180516查询describe过滤权限
            List<String> authorizedApiNames = functionPrivilegeService.funPrivilegeCheck(user, apiNameList, actionCode);

            Map map = Maps.newHashMap();
            list.forEach(k -> {
                if (authorizedApiNames.contains(k.getApiName())) {
                    map.put(k.getApiName(), k);
                }
            });
            return map;
        } catch (MetadataServiceException e) {
            log.warn("error in find describelist by apiname list,tenantId:{},apiNames:{}", new Object[]{tenantId, apiNameList});
            throw new MetaDataBusinessException(e.getMessage());
        }
    }

    public void fillMenuQueryAddOrder(SearchTemplateQuery searchTemplateQuery) {
        searchTemplateQuery.setOrders(Lists.newArrayList(
                new OrderBy(MenuConstants.MenuField.ISSTYTEM.getApiName(), false),
                new OrderBy(IObjectData.LAST_MODIFIED_TIME, false)));
    }

    /**
     * 获取系统默认的CRM菜单
     */
    public IObjectData findDefaultCrmMenu(User user) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(1);
        List<IFilter> filters = searchQuery.getFilters();
        SearchUtil.fillFilterEq(filters, MenuConstants.MenuField.ISSTYTEM.getApiName(), true);
        QueryResult<IObjectData> dataQueryResult = serviceFacade.findBySearchQuery(user, MenuConstants.MENU_API_NAME, searchQuery);
        return CollectionUtils.isNotEmpty(dataQueryResult.getData()) ? dataQueryResult.getData().get(0) : null;
    }

}
