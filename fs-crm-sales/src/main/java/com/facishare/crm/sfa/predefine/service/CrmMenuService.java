package com.facishare.crm.sfa.predefine.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.sfa.predefine.service.model.CrmMenuListArg;
import com.facishare.crm.sfa.predefine.version.VersionService;
import com.facishare.crm.sfa.utilities.common.convert.SearchUtil;
import com.facishare.crm.sfa.utilities.proxy.GetHomePermissionsProxy;
import com.facishare.crm.sfa.utilities.proxy.model.GetHomePermissionsModel;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.common.util.StopWatch;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.exception.MetaDataException;
import com.facishare.paas.appframework.metadata.menu.MenuConstants;
import com.facishare.paas.appframework.metadata.menu.model.MenuItemConfigObject;
import com.facishare.paas.appframework.privilege.FunctionPrivilegeService;
import com.facishare.paas.appframework.privilege.UserRoleInfoService;
import com.facishare.paas.metadata.api.DELETE_STATUS;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.describe.ObjectDescribe;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.facishare.paas.metadata.service.impl.ObjectDataServiceImpl;
import com.fxiaoke.release.FsGrayRelease;
import com.fxiaoke.release.FsGrayReleaseBiz;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import static com.facishare.paas.appframework.core.model.RequestContext.Android_CLIENT_INFO_PREFIX;
import static com.facishare.paas.appframework.core.model.RequestContext.IOS_CLIENT_INFO_PREFIX;

/**
 * 个人菜单请求接口类
 */
@ServiceModule("crm_menu")
@Component
@Slf4j
public class CrmMenuService {
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private CrmMenuAdminService crmMenuAdminService;
    @Autowired
    private CrmWorkBenchService crmWorkBenchService;
    @Autowired
    FunctionPrivilegeService functionPrivilegeService;
    @Autowired
    private GetHomePermissionsProxy homePermissionsProxy;
    @Autowired
    IObjectDescribeService objectDescribeService;
    @Autowired
    private CrmMenuActionService crmMenuActionService;
    @Autowired
    private ObjectDataServiceImpl objectDataService;
    @Autowired
    VersionService versionService;
    @Autowired
    UserRoleInfoService userRoleInfoService;
    private FsGrayReleaseBiz menuGray = FsGrayRelease.getInstance("menu");

    /**
     * 终端/WEB端使用的菜单&菜单项大接口
     */
    @ServiceMethod("all_menu")
    public CrmMenuListArg.Result allMenu(ServiceContext context) {
        StopWatch stopWatch = StopWatch.create("CrmMenuService all_menu");
        User user = context.getUser();
        //1、获取所有的对象apiname集合、需要验证的权限对象数据
        stopWatch.lap("allMenu start");
        Set<String> supportApiNameList = this.findAllMenuItemApiName(user);
        if (CollectionUtils.isEmpty(supportApiNameList)) {
            throw new ValidateException("CRM菜单不存在，请联系纷享客服！");
        }
        //根据版本过滤到不支持的对象
        versionService.filterSupportObj(user.getTenantId(), supportApiNameList);

        Set<String> needCheckPrivilegeApiNames = Sets.newHashSet();
        supportApiNameList.stream().forEach(apiName -> {
            MenuItemConfigObject configObject = MenuConstants.configMenuItemMap.get(apiName);
            if (configObject != null) {
                if (configObject.getValidatePrivilege()) {
                    needCheckPrivilegeApiNames.add(apiName);
                }
            } else {
                //自定义对象
                needCheckPrivilegeApiNames.add(apiName);
            }
        });
        stopWatch.lap("get needCheckPrivilegeApiNames finished");

        Map<String, IObjectDescribe> describeMap = crmMenuAdminService.findDescribeListByApiNamesWithoutFields(context.getUser(), new ArrayList(supportApiNameList), ObjectAction.VIEW_LIST.getActionCode());
        stopWatch.lap("get allApiName describe finished");

        Map<String, Map<String, Boolean>> needCheckPrivilegeDescribe = batchFunPrivilegeCheck(user, new ArrayList<>(needCheckPrivilegeApiNames),
                Lists.newArrayList(ObjectAction.VIEW_LIST.getActionCode(), ObjectAction.CREATE.getActionCode()));
        stopWatch.lap("get allApiName funcPrivilege finished");

        CrmMenuListArg.Result result = CrmMenuListArg.Result.builder().build();
        List<IObjectData> userMenuList = findUserMenuList(user);
        //2、获取当前登录人可以查看的所有菜单列表,同时转成输出参数
        List<String> userMenuIdList = userMenuList.stream().map(k -> {
            CrmMenuListArg.Menu menu = new CrmMenuListArg.Menu();
            menu.setDisplayName(k.getName());
            menu.setId(k.getId());
            menu.setIsSystem(k.get(MenuConstants.MenuField.ISSTYTEM.getApiName(), Boolean.class));
            result.getMenus().add(menu);
            return k.getId();
        }).collect(Collectors.toList());

        //3、根据所有的菜单，获取所有的菜单项,建立菜单和菜单项的索引集合
        List<IObjectData> menuItemList = crmMenuActionService.findMenuItems(user, userMenuIdList);
        Map<String, List<CrmMenuListArg.MenuItem>> menuToMenuItemArgMap = Maps.newHashMap();
        String deviceType = getDeviceType(context);
        stopWatch.lap("findMenuItems  list finished");
        //4、依据配置的预设对象、对象功能权限和其他配置项，把所有菜单项转换成可输出的菜单项集合CrmMenuListArg.MenuItem
        Boolean isShowServiceManager = isShowServiceManager(user);
        menuItemList.stream()
                .filter(menuItem -> {
                    //服务管理特殊处理
                    String apiName = getMenuItemApiName(menuItem);
                    return !"CrmServiceManager".equals(apiName) || isShowServiceManager;
                })
                //supportApiNameList为版本过滤后的apiName，过滤
                .filter(menuItem -> supportApiNameList.contains(getMenuItemApiName(menuItem)))
                //验证请求端类型
                .filter(menuItem -> validateDeviceType(menuItem, deviceType))
                //验证功能权限
                .filter(menuItem -> validatePrivilege(menuItem, needCheckPrivilegeDescribe.getOrDefault(getMenuItemApiName(menuItem), Maps.newHashMap())))
                //验证describe
                .filter(menuItem -> validateDescribe(menuItem, describeMap.getOrDefault(getMenuItemApiName(menuItem), new ObjectDescribe())))
                .forEach(menuItem -> {
                    String apiName = getMenuItemApiName(menuItem);
                    CrmMenuListArg.MenuItem menuItemArg = convertMenuItemToArgMenuItem(menuItem, deviceType,
                            describeMap.getOrDefault(apiName, new ObjectDescribe()), needCheckPrivilegeDescribe.getOrDefault(apiName, Maps.newHashMap()));
                    if (menuItemArg != null) {
                        menuToMenuItemArgMap.computeIfAbsent(menuItemArg.getMenuId(), k -> Lists.newArrayList());
                        menuToMenuItemArgMap.get(menuItemArg.getMenuId()).add(menuItemArg);
                    }
                });
        stopWatch.lap("convertMenuItemToArgMenuItem  list finished");

        //6、处理web有工作台的个人数据，合并工作台数据返回，终端没有个人工作台
        if (!MenuConstants.DEVICE_TYPE_MOBILE.equals(deviceType)) {
            List<IObjectData> workBenchList = crmWorkBenchService.findUserMenuWorkBenchList(user, userMenuIdList);
            fillWorkBenchToMenu(workBenchList, menuToMenuItemArgMap);
            stopWatch.lap("fillWorkBenchToMenu  list finished");
        }
        //7、把上一步处理好的菜单和菜单项的索引引入到输出menu中,只有web端返回
        String userMenuCurrentId = crmWorkBenchService.getUserMenuCurrentId(user);
        result.getMenus().forEach(menu -> {
            menu.setItems(menuToMenuItemArgMap.get(menu.getId()));
            //登录人最近一次选择默认的crm菜单
            if ((StringUtils.isEmpty(userMenuCurrentId) || !userMenuIdList.contains(userMenuCurrentId))) {
                menu.setIsCurrent(menu.getIsSystem());
            } else {
                menu.setIsCurrent(menu.getId().equals(userMenuCurrentId));
            }
        });
        stopWatch.lap("result menu finished");
        //其他信息
        result.setConfiginfo(getHomePermissions(context));
        stopWatch.lap("getHomePermissions  list finished");
        stopWatch.logSlow(5000L);
        return result;
    }

    private String getMenuItemApiName(IObjectData menuItem) {
        return menuItem.get(MenuConstants.MenuItemField.REFERENCEAPINAME.getApiName(), String.class);
    }

    //是否可以查看服务管理菜单
    private Boolean isShowServiceManager(User user) {
        List<String> allRoleCodeList = userRoleInfoService.getUserRole(user);
        return allRoleCodeList.contains("00000000000000000000000000000006") || allRoleCodeList.contains("00000000000000000000000000000010");
    }

    /**
     * 获取请求端类型，web或终端
     */
    private String getDeviceType(ServiceContext context) {
        String clientInfo = context.getRequestContext().getClientInfo();
        if (clientInfo != null && (clientInfo.startsWith(Android_CLIENT_INFO_PREFIX) ||
                clientInfo.startsWith(IOS_CLIENT_INFO_PREFIX))) {
            return MenuConstants.DEVICE_TYPE_MOBILE;
        }
        return MenuConstants.DEVICE_TYPE_WEB;
    }

    private Map<String, Object> getHomePermissions(ServiceContext context) {
        try {
            String clientInfo = context.getRequestContext().getClientInfo();
            GetHomePermissionsModel.Result homePermiOrMenuResult;
            if (clientInfo != null && (clientInfo.startsWith(Android_CLIENT_INFO_PREFIX) ||
                    clientInfo.startsWith(IOS_CLIENT_INFO_PREFIX))) {
                homePermiOrMenuResult = homePermissionsProxy.getHomePermissionsByTenantId(context.getTenantId(), context.getUser().getUserId());
            } else {
                homePermiOrMenuResult = homePermissionsProxy.getMenuByTenantId(context.getTenantId(), context.getUser().getUserId());
            }
            if (homePermiOrMenuResult != null) {
                return homePermiOrMenuResult.getValue();
            }
        } catch (Exception e) {
            log.error("Call GetHomePermissionsProxy error:", e);
        }
        return null;
    }

    /**
     * 把工作台数据信息 合并到输出的arg菜单项中
     */
    private void fillWorkBenchToMenu(List<IObjectData> workBenchList, Map<String, List<CrmMenuListArg.MenuItem>> menuToMenuItemArgMap) {
        if (CollectionUtils.isEmpty(workBenchList)) {
            return;
        }
        //key:菜单id value中：key是菜单项id，value是工作台节点数据
        Map<String, Map<String, IObjectData>> workBenchMenuAndItemMap = Maps.newHashMap();
        //1、建立工作台菜单项id和工作台节点信息的索引
        //2、把工作台中是分组类型的合并到menuToMenuItemArgMap输出对象中
        workBenchList.stream().filter(k -> menuToMenuItemArgMap.get(k.get(MenuConstants.MenuWorkBenchField.MENUID.getApiName(), String.class)) != null)
                .forEach(workbench -> {
                    String menuId = workbench.get(MenuConstants.MenuWorkBenchField.MENUID.getApiName(), String.class);
                    String menuItemId = workbench.get(MenuConstants.MenuWorkBenchField.MENUITEMID.getApiName(), String.class);
                    workBenchMenuAndItemMap.computeIfAbsent(menuId, k -> Maps.newHashMap());
                    Map<String, IObjectData> menuItemMap = workBenchMenuAndItemMap.get(menuId);

                    if (StringUtils.isNotBlank(menuItemId)) {
                        menuItemMap.put(menuItemId, workbench);
                    } else {
                        //如果menuItem为空,则说明是分组类型
                        CrmMenuListArg.MenuItem menuItemArg = CrmMenuListArg.MenuItem.builder().build();
                        menuItemArg.setIsHidden(workbench.get(MenuConstants.MenuWorkBenchField.ISHIDDEN.getApiName(), Boolean.class));
                        menuItemArg.setNumber(workbench.get(MenuConstants.MenuWorkBenchField.NUMBER.getApiName(), Integer.class));
                        menuItemArg.setId(workbench.getId());
                        menuItemArg.setPid(workbench.get(MenuConstants.MenuWorkBenchField.PID.getApiName(), String.class));
                        menuItemArg.setDisplayName(workbench.get(MenuConstants.MenuWorkBenchField.DISPLAYNAME.getApiName(), String.class));
                        menuItemArg.setMenuId(menuId);
                        //分组类型
                        menuItemArg.setType(MenuConstants.MenuWorkBenchField.TYPE_GROUP.getApiName());
                        menuToMenuItemArgMap.get(menuId).add(menuItemArg);
                    }
                });
        //3、把工作台节点的信息插入到输出集合中
        menuToMenuItemArgMap.forEach((menuId, menuItemArgList) -> menuItemArgList.forEach(menuItemArg -> {
            //过滤分组
            Map<String, IObjectData> workBenchMenuItemMap = workBenchMenuAndItemMap.get(menuItemArg.getMenuId());
            if (workBenchMenuItemMap != null && !menuItemArg.getType().equals(MenuConstants.MenuWorkBenchField.TYPE_GROUP.getApiName())) {
                String menuItemId = menuItemArg.getMenuItemId();
                IObjectData workbench = workBenchMenuItemMap.get(menuItemId);
                if (workbench != null) {
                    menuItemArg.setIsHidden(workbench.get(MenuConstants.MenuWorkBenchField.ISHIDDEN.getApiName(), Boolean.class));
                    menuItemArg.setNumber(workbench.get(MenuConstants.MenuWorkBenchField.NUMBER.getApiName(), Integer.class));
                    menuItemArg.setId(workbench.getId());
                    menuItemArg.setPid(workbench.get(MenuConstants.MenuWorkBenchField.PID.getApiName(), String.class));
                } else {
                    //如果工作台节点不存在该菜单项，则此菜单项可能是后来新建的菜单项，需要改变order排在最后面,直接在原来order基础的上加10000保证有序的在最后面
                    menuItemArg.setNumber(menuItemArg.getNumber() + 10000);
                    menuItemArg.setIsHidden(false);
                }
            }
        }));
    }

    private Boolean validateDescribe(IObjectData menuItem, IObjectDescribe objectDescribe) {
        //需要使用的数据
        String apiName = getMenuItemApiName(menuItem);
        //如果配置项相应的对象为空，是自定义对象
        MenuItemConfigObject menuItemConfigObject = MenuConstants.configMenuItemMap.get(apiName);
        //预制的crm对象，并且配置中不存在该对象时，则过滤
        if (IObjectDescribe.DEFINE_TYPE_PACKAGE.equals(objectDescribe.getDefineType()) && menuItemConfigObject == null) {
            return false;
        }
        //自定义对象默认校验describe
        Boolean needValidateDescribe = menuItemConfigObject == null ? true : menuItemConfigObject.getValidateDescribe();
        //验证describe
        if (needValidateDescribe && (objectDescribe.get(IObjectDescribe.IS_ACTIVE, Boolean.class) == null || !objectDescribe.isActive())) {
            return false;
        }
        return true;
    }

    private Boolean validatePrivilege(IObjectData menuItem, Map<String, Boolean> privilegeMap) {
        String apiName = getMenuItemApiName(menuItem);
        MenuItemConfigObject menuItemConfigObject = MenuConstants.configMenuItemMap.get(apiName);
        //自定义对象默认校验权限
        Boolean needValidatePrivilege = menuItemConfigObject == null ? true : menuItemConfigObject.getValidatePrivilege();
        //判断权限
        if (needValidatePrivilege && !privilegeMap.get(ObjectAction.VIEW_LIST.getActionCode())) {
            return false;
        }
        return true;
    }

    private Boolean validateDeviceType(IObjectData menuItem, String deviceType) {
        String apiName = getMenuItemApiName(menuItem);
        MenuItemConfigObject menuItemConfigObject = MenuConstants.configMenuItemMap.get(apiName);
        //判断请求端类型,判断是否需要显示菜单
        String itemConfigDeviceType = menuItemConfigObject == null ? MenuConstants.DEVICE_TYPE_ALL : menuItemConfigObject.getDeviceType();
        if (!MenuConstants.DEVICE_TYPE_ALL.equals(itemConfigDeviceType) && !deviceType.equals(itemConfigDeviceType)) {
            return false;
        }
        return true;
    }

    /**
     * 把数据库中查出的菜单项转成端可用的arg menuItem数据
     * 填充项：名称、图标、url、排序号、类型等
     * 依赖：配置中心配置的预设对象配置、元数据底层数据
     */
    public CrmMenuListArg.MenuItem convertMenuItemToArgMenuItem(IObjectData menuItem, String deviceType, IObjectDescribe objectDescribe, Map<String, Boolean> privilegeMap) {
        //需要使用的数据
        String apiName = getMenuItemApiName(menuItem);
        String tenantId = menuItem.getTenantId();
        //如果配置项相应的对象为空，是自定义对象
        MenuItemConfigObject menuItemConfigObject = MenuConstants.configMenuItemMap.getOrDefault(apiName,
                new MenuItemConfigObject(true, true, MenuConstants.DEVICE_TYPE_ALL));
        //处理灰度
        if (MenuConstants.grayMenuRuleConfig != null) {
            JSONObject grayMenuObj = MenuConstants.grayMenuRuleConfig.getJSONObject(apiName);
            if (grayMenuObj != null && menuGray.isAllow("menu_rule", tenantId)) {
                //合并menuConfig
                JSONObject menuItemConfigObjectTemp = JSON.parseObject(JSON.toJSONString(menuItemConfigObject));
                menuItemConfigObjectTemp.putAll(grayMenuObj);
                menuItemConfigObject = JSONObject.parseObject(menuItemConfigObjectTemp.toJSONString(), MenuItemConfigObject.class);
            }
        }
        //核心处理
        CrmMenuListArg.MenuItem menuItemArg = CrmMenuListArg.MenuItem.builder()
                .displayName(String.valueOf(emptyElse(objectDescribe.getDisplayName(), menuItemConfigObject.getDisplayName())))
                .iconIndex((Integer) emptyElse(menuItemConfigObject.getIconIndex(), objectDescribe.getIconIndex()))
                .iconPathHome(String.valueOf(emptyElse(menuItemConfigObject.getIconPathHome(), objectDescribe.getIconPath())))
                .iconPathMenu(String.valueOf(emptyElse(menuItemConfigObject.getIconPathMenu(), objectDescribe.getIconPath())))
                .menuItemId(menuItem.getId())
                .referenceApiname(apiName)
                .number(menuItem.get(MenuConstants.MenuItemField.NUMBER.getApiName(), Integer.class))
                .type(MenuConstants.MenuWorkBenchField.TYPE_MENU.getApiName())
                .menuId(menuItem.get(MenuConstants.MenuItemField.MENUID.getApiName()).toString())
                .url(menuItemConfigObject.getUrl())
                .build();
        //填充权限标识
        menuItemArg.setPrivilegeAction(Lists.newArrayList(ObjectAction.VIEW_LIST.getActionCode()));
        //终端下发不同的action，支持h5、weex等
        if (MenuConstants.DEVICE_TYPE_MOBILE.equals(deviceType) && menuItemConfigObject.getMobileConfig() != null) {
            MenuItemConfigObject.MobileConfig mobileConfig = menuItemConfigObject.getMobileConfig();
            CrmMenuListArg.MobileConfig mobileConfigArg = new CrmMenuListArg.MobileConfig();
            mobileConfigArg.setListAction(mobileConfig.getMobileListAction());
            mobileConfigArg.setAddAction(mobileConfig.getMobileAddAction());
            menuItemArg.setMobileConfig(mobileConfigArg);
        }
        //验证是否需要赋予add权限，比如终端不需要价目表的Add权限（快速新建）
        String quickAddExcludeApiConfig = MenuConstants.quickAddExcludeDevice.getOrDefault(apiName, MenuConstants.DEVICE_TYPE_ALL);
        if (!MenuConstants.DEVICE_TYPE_ALL.equals(quickAddExcludeApiConfig) && !deviceType.equals(quickAddExcludeApiConfig)) {
            return menuItemArg;
        }
        Boolean addAction = privilegeMap.get(ObjectAction.CREATE.getActionCode());
        if (Objects.nonNull(addAction) && addAction) {
            menuItemArg.getPrivilegeAction().add(ObjectAction.CREATE.getActionCode());
        }
        return menuItemArg;
    }

    private Object emptyElse(Object var1, Object var2) {
        if (var1 instanceof String) {
            return StringUtils.isNotBlank(var1.toString()) ? var1 : var2;
        } else {
            return Objects.nonNull(var1) ? var1 : var2;
        }
    }

    /**
     * 个人CRM菜单列表接口
     */
    @ServiceMethod("menu_list")
    public List<IObjectData> objectList(ServiceContext context) {
        return this.findUserMenuList(context.getUser());
    }

    /**
     * 查询可用的菜单列表
     */
    private List<IObjectData> findUserMenuList(User user) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(1000);
        //用户的角色
        List<String> roleIdList = serviceFacade.getUserRole(user);
        if (CollectionUtils.isEmpty(roleIdList)) {
            log.error("findUserMenuList user role not exist,tenantId {},userId {}", user.getTenantId(), user.getUserId());
            throw new ValidateException("角色不存在,请联系纷享客服！");
        }
        List<IFilter> filters = searchQuery.getFilters();
        SearchUtil.fillFilterIn(filters, MenuConstants.RoleSourceField.ROLE_ID.getApiName(), roleIdList);
        //查询菜单资源类型的
        SearchUtil.fillFilterEq(filters, MenuConstants.RoleSourceField.SOURCETYPE.getApiName(), MenuConstants.RoleSourceField.SOURCETYPE_MENU.getApiName());
        QueryResult<IObjectData> roleSourceList = serviceFacade.findBySearchQuery(user, MenuConstants.ROLE_SOURCE_API_NAME, searchQuery);
        List<String> menuIdsList = roleSourceList.getData().stream()
                .map(k -> k.get(MenuConstants.RoleSourceField.SOURCEID.getApiName(), String.class)).collect(Collectors.toList());
        List<IObjectData> menuList = Lists.newArrayList();
        //默认增加当前企业默认的crm菜单
        menuList.add(crmMenuAdminService.findDefaultCrmMenu(user));
        if (CollectionUtils.isNotEmpty(menuIdsList)) {
            menuList.addAll(this.findMenuByIds(user, menuIdsList));
        }
        return menuList;
    }

    /**
     * 根据menuId查找启用状态的菜单
     */
    private List<IObjectData> findMenuByIds(User user, List menuIdsList) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(menuIdsList.size());
        List<IFilter> filters = searchQuery.getFilters();
        SearchUtil.fillFilterIn(filters, IObjectData.ID, menuIdsList);
        //启用的
        SearchUtil.fillFilterEq(filters, MenuConstants.MenuField.ACTIVESTATUS.getApiName(), MenuConstants.MenuField.ACTIVESTATUS_ON.getApiName());
        crmMenuAdminService.fillMenuQueryAddOrder(searchQuery);
        QueryResult<IObjectData> menuDatalist = serviceFacade.findBySearchQuery(user, MenuConstants.MENU_API_NAME, searchQuery);
        return menuDatalist.getData();
    }

    private Set<String> findAllMenuItemApiName(User user) {
        String sql = String.format("select DISTINCT reference_apiname reference_apiname from menu_item where tenant_id='%s' and is_deleted='%s'", user.getTenantId()
                , DELETE_STATUS.NORMAL.getValue());
        try {
            QueryResult<IObjectData> queryResult = objectDataService.findBySql(sql, user.getTenantId(), MenuConstants.MENU_ITEM_API_NAME);
            return queryResult.getData().stream().map(k -> k.get("reference_apiname", String.class)).collect(Collectors.toSet());
        } catch (MetadataServiceException e) {
            log.error("findAllMenuApiName error,tanantId {}", user.getTenantId(), e);
            throw new MetaDataException(e.getMessage());
        }
    }

    private Map<String, Map<String, Boolean>> batchFunPrivilegeCheck(User user, List apiNames, List actionCodes) {
        Map<String, Map<String, Boolean>> map = functionPrivilegeService.batchFunPrivilegeCheck(user, apiNames, actionCodes);
        JSONObject privilegeActionListDepends = MenuConstants.privilegeActionListDepends;
        privilegeActionListDepends.keySet().forEach(key -> {
            Optional.ofNullable(map.get(key)).filter(k -> k.get(ObjectAction.VIEW_LIST.getActionCode())).ifPresent(m -> {
                JSONArray needDependsApiNames = privilegeActionListDepends.getJSONArray(key);
                Optional.ofNullable(needDependsApiNames).get().forEach(needDependsApiName -> {
                    Map<String, Boolean> newHashMap = Maps.newHashMap();
                    newHashMap.put(ObjectAction.VIEW_LIST.getActionCode(), true);
                    map.put(needDependsApiName.toString(), newHashMap);
                });
            });
        });
        return map;
    }
}
