package com.facishare.crm.deliverynote.predefine.manager;

import com.facishare.crm.constants.CommonConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.deliverynote.enums.DeliveryNoteSwitchEnum;
import com.facishare.crm.deliverynote.enums.ProductFunctionNumberEnum;
import com.facishare.crm.deliverynote.exception.DeliveryNoteBusinessException;
import com.facishare.crm.deliverynote.exception.DeliveryNoteErrorCode;
import com.facishare.crm.deliverynote.predefine.service.dto.DeliveryNoteType;
import com.facishare.crm.deliverynote.predefine.util.DeliveryNoteUtil;
import com.facishare.crm.rest.CrmRestApi;
import com.facishare.crm.rest.SendCrmMessageProxy;
import com.facishare.crm.rest.TemplateApi;
import com.facishare.crm.rest.dto.SalesOrderModel;
import com.facishare.crm.rest.dto.SendCrmMessageModel;
import com.facishare.crm.rest.dto.SyncTenantSwitchModel;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.DescribeLogicService;
import com.facishare.paas.appframework.privilege.DeliveryNotePrivilegeService;
import com.facishare.paas.appframework.privilege.FunctionPrivilegeService;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 初始化
 * Created by chenzs on 2018/1/10.
 */
@Slf4j
@Service
public class InitObjManager {
    @Autowired
    protected ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) SpringUtil.getContext().getBean("taskExecutor");

    @Resource
    private DescribeLogicService describeLogicService;
    @Resource
    private ObjectDescribeCreateManager objectDescribeCreateManager;
    @Autowired
    private FunctionPrivilegeService functionPrivilegeService;
    @Autowired
    private DeliveryNotePrivilegeService deliveryNotePrivilegeService;
    @Resource
    private ConfigManager configManager;
    @Resource
    private ObjectDescribeManager objectDescribeManager;
    @Resource
    private TemplateApi templateApi;
    @Resource
    private CrmRestApi crmRestApi;
    @Autowired
    private SendCrmMessageProxy sendCrmMessageProxy;
    @Autowired
    private SalesOrderManager salesOrderManager;
    @Resource
    private ErpStockSwitchManager erpStockSwitchManager;

    public DeliveryNoteType.EnableDeliveryNoteResult testUpdateSalesOrderStatus(String tenantId, User user) {
        configManager.updateDeliveryNoteStatus(user, DeliveryNoteSwitchEnum.OPEN_FAIL);

        DeliveryNoteType.EnableDeliveryNoteResult result = new DeliveryNoteType.EnableDeliveryNoteResult();
//        Map<String, String> headers = DeliveryNoteUtil.getHeadersWithLength(tenantId, user.getUserId());
//
//        SalesOrderModel.ExistsDeliveredOrders existsDeliveredOrdersResult = crmRestApi.existsDeliveredOrders(headers);
//        SalesOrderModel.UpdateCustomerOrderDeliveryToReceivedTask updateResult = crmRestApi.createUpdateCustomerOrderDeliveryToReceivedTask(headers);
//
//        String title = "发货单启用成功";
//        String content = "发货单已成功启用，建议在使用之前先去后台设置发货单对象的字段，布局等信息。";
//        sendMessageToOpener(tenantId, User.SUPPER_ADMIN_USER_ID, Integer.valueOf(user.getUserId()), title, content);
        return result;
    }

    /**
     * 开启发货单
     */
    public DeliveryNoteType.EnableDeliveryNoteResult enableDeliveryNote(String tenantId, User user) {
        DeliveryNoteType.EnableDeliveryNoteResult result = new DeliveryNoteType.EnableDeliveryNoteResult();

        //校验ERP库存是否开启
        if (erpStockSwitchManager.isErpStockEnable(tenantId)) {
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.OPEN_DELIVERY_NOTE_FAIL, "ERP库存已开启，不能开启发货单");
        }

        //查看当前开关状态（开启中也返回）
        DeliveryNoteSwitchEnum deliveryNoteSwitchStatus = configManager.getDeliveryNoteStatus(tenantId);
        if (Objects.equals(deliveryNoteSwitchStatus.getStatus(), DeliveryNoteSwitchEnum.OPENING.getStatus())) {
            result.setEnableStatus(DeliveryNoteSwitchEnum.OPENING.getStatus());
            result.setMessage("系统正在处理，请耐心等待");
            return result;
        }

        return enableDeliveryNoteNotReturnIfOpening(tenantId, user, false, null);
    }

    /**
     * 开启发货单:如果在开启中，不直接返回
     *
     * @param tenantId
     * @param user
     * @param isNeedSendMessage 是否需要通知启用者开启成功了
     * @param receiverId        启用者
     * @return
     */
    public DeliveryNoteType.EnableDeliveryNoteResult enableDeliveryNoteNotReturnIfOpening(String tenantId, User user, boolean isNeedSendMessage, String receiverId) {
        log.info("enableDeliveryNoteNotReturnIfOpening, tenantId[{}], user[{}], isNeedSendMessage[{}], receiverId[{}]", tenantId, user, isNeedSendMessage, receiverId);
        DeliveryNoteType.EnableDeliveryNoteResult result = new DeliveryNoteType.EnableDeliveryNoteResult();

        //1、是否crm管理员

        //2、查看当前开关状态
        DeliveryNoteSwitchEnum deliveryNoteSwitchStatus = configManager.getDeliveryNoteStatus(tenantId);
        if (Objects.equals(deliveryNoteSwitchStatus.getStatus(), DeliveryNoteSwitchEnum.OPENED.getStatus())) {
            result.setEnableStatus(DeliveryNoteSwitchEnum.OPENED.getStatus());
            result.setMessage("发货单已开启");
            return result;
        }

        //3、displayName是否被使用了，被使用了则不能创建
        Set<String> existDisplayNames = objectDescribeManager.getExistDisplayName(tenantId);
        if (CollectionUtils.isNotEmpty(existDisplayNames)) {
            String errorMsg = Joiner.on(",").join(existDisplayNames).concat("名称已存在");
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.OPEN_DELIVERY_NOTE_FAIL, errorMsg);
        }

        //4、所有销售订单中，不包括状态为已发货的订单，如果存在则先提示用户将已发货的订单改成已收货后才能开启发货单
        boolean existsDeliveredOrders = salesOrderManager.existsDeliveredOrders(tenantId, user.getUserId());
        if (existsDeliveredOrders) {
            /**
             * 如果有已发货状态的订单，就调接口POST /customerorder/createupdatecustomerorderdeliverytoreceivedtask，把所有已发货状态的订单改成已收货状态
             * 然后这些接口执行完就发MQ，我们监听MQ消息，就触发开启发货单
             */
            Map<String, String> headers = DeliveryNoteUtil.getHeadersWithLength(tenantId, user.getUserId());
            SalesOrderModel.UpdateCustomerOrderDeliveryToReceivedTask updateResult = crmRestApi.createUpdateCustomerOrderDeliveryToReceivedTask(headers);
            if (updateResult.isValue()) {
                //修改开关状态
                configManager.updateDeliveryNoteStatus(user, DeliveryNoteSwitchEnum.OPENING);

                result.setEnableStatus(DeliveryNoteSwitchEnum.OPENING.getStatus());
                result.setMessage("系统正在处理，请耐心等待");
                return result;
            }
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.OPEN_DELIVERY_NOTE_FAIL, DeliveryNoteErrorCode.EXIST_DELIVERED_ORDER.getMessage());
        }

        //5、初始化、更新开关
        try {
            init(tenantId, user);
            configManager.updateDeliveryNoteStatus(user, DeliveryNoteSwitchEnum.OPENED);
            result.setEnableStatus(DeliveryNoteSwitchEnum.OPENED.getStatus());
            result.setMessage(DeliveryNoteSwitchEnum.OPENED.getMessage());

            //6、开启成功通知SFA（会添加 LogisticsStatus发货状态等）
            Map<String, String> headersHasParameter = DeliveryNoteUtil.getHeaders(tenantId, user.getUserId());
            SyncTenantSwitchModel.Arg arg = new SyncTenantSwitchModel.Arg();
            arg.setKey("33");    //发货单
            arg.setValue("1");   //1-true,0-false   0不好使。只能传1.因为不可停用。
            SyncTenantSwitchModel.Result crmResult = crmRestApi.syncTenantSwitch(arg, headersHasParameter);  //可重复调用
            log.info("sync delivery note status, arg:{}, headers:{}, result:{}", arg, headersHasParameter, crmResult);
            log.info("before sleep time[{}]", System.currentTimeMillis());
            //7、销售订单、销售订单产品：加字段
            Thread.sleep(5000);
            log.info("after sleep time[{}]", System.currentTimeMillis());
            salesOrderManager.salesOrderAddDeliveredAmountSumField(user);
            salesOrderManager.salesOrderProductAddDeliveredCountAndDeliveryAmountField(user);

            //8、通知开启者
            if (isNeedSendMessage) {
                String title = "发货单启用成功";
                String content = "发货单已成功启用，建议在使用之前先去后台设置发货单对象的字段，布局等信息。";
                sendMessageToOpener(tenantId, User.SUPPER_ADMIN_USER_ID, Integer.valueOf(receiverId), title, content);  //自己发给自己，crm通知没有红点提示
            }
        } catch (Exception e) {
            log.error("init failed, tenantId:{}, user:{}", tenantId, user, e);
            configManager.updateDeliveryNoteStatus(user, DeliveryNoteSwitchEnum.OPEN_FAIL);
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.INIT_DESCRIBE_PRIVILEGE_FAILED, "初始化对象、权限、打印模板失败, " + e);
        }
        return result;
    }

    /**
     * 把'已发货'的销售订单改为'已收货'失败了
     * 1、修改初始化开关
     * 2、发crm通知
     */
    public void updateSalesOrderStatusFail(User user) {
        //1、修改初始化开关
        configManager.updateDeliveryNoteStatus(user, DeliveryNoteSwitchEnum.OPEN_FAIL);

        //2、发crm通知
        String title = "发货单启用失败";
        String content = "发货单开启失败，请在发货和库存管理页面重新尝试。";
        sendMessageToOpener(user.getTenantId(), User.SUPPER_ADMIN_USER_ID, Integer.valueOf(user.getUserId()), title, content);  //自己发给自己，crm通知没有红点提示
    }

    /**
     * 发消息通知开启者
     */
    private void sendMessageToOpener(String tenantId, String senderId, Integer receiverId, String title, String content) {
        Set<Integer> receiverIds = Sets.newHashSet(Integer.valueOf(receiverId));

        SendCrmMessageModel.Arg sendArg = new SendCrmMessageModel.Arg();
        sendArg.setEmployeeId(senderId);
        sendArg.setContent(content);
        sendArg.setRemindRecordType(92);
        sendArg.setDataId("");  //没有也要""
        sendArg.setReceiverIds(receiverIds);
        sendArg.setTitle(title);

        Map<String, String> headers = DeliveryNoteUtil.getHeaders(tenantId, senderId);
        SendCrmMessageModel.Result result = sendCrmMessageProxy.sendCrmMessages(headers, sendArg);
        if (!result.isSuccess()) {
            log.warn("sendCrmMessageProxy.sendCrmMessages failed, tenantId:{}, sendArg:{}, result:{}", tenantId, sendArg, result);
        } else {
            log.info("sendCrmMessageProxy.sendCrmMessages success, tenantId:{}, sendArg:{}, result:{}", tenantId, sendArg, result);
        }
    }

    public boolean init(String tenantId, User user) {
        log.info("init begin, tenantId:{}, user:{}", tenantId, user);
        //判断对象是否已经创建，没有则创建
        String fsUserId = user.getUserId();

        //1、创建定义、layout
        Set<String> apiNames = Sets.newHashSet(DeliveryNoteObjConstants.API_NAME, DeliveryNoteProductObjConstants.API_NAME);
        Map<String, IObjectDescribe> describeMap = describeLogicService.findObjects(tenantId, apiNames);
        log.info("init, describeLogicService.findObjects, describeMap[{}]", describeMap);
        if (!describeMap.containsKey(DeliveryNoteObjConstants.API_NAME)) {
            objectDescribeCreateManager.createDeliveryNoteDescribeAndLayout(tenantId, fsUserId);
        }
        if (!describeMap.containsKey(DeliveryNoteProductObjConstants.API_NAME)) {
            objectDescribeCreateManager.createDeliveryNoteProductDescribeAndLayout(tenantId, fsUserId);
        }

        //2、初始化权限
        initPrivilege(user);

        //3、创建发货单打印模板
        initPrintTemplate(user);
        return true;
    }

    /**
     * 权限初始化
     */
    public void initPrivilege(User user) {
        //1、给发货单创建"查看物流"的权限
        createFuncCode(user, DeliveryNoteObjConstants.API_NAME, DeliveryNoteObjConstants.Button.ViewLogistics.apiName, DeliveryNoteObjConstants.Button.ViewLogistics.label);

        //2、给发货单创建"确认收货"的权限
        createFuncCode(user, DeliveryNoteObjConstants.API_NAME, DeliveryNoteObjConstants.Button.ConfirmReceipt.apiName, DeliveryNoteObjConstants.Button.ConfirmReceipt.label);

        //3、给角色授权
        addFuncAccessToRole(user);

        //4、给角色添加发货单的权限
        addFuncAccessForRole(user);
    }

    /**
     * 给对象objectApiName创建权限
     */
    private void createFuncCode(User user, String objectApiName, String actionCode, String actionDisplayName) {
        try {
            functionPrivilegeService.createFuncCode(user, objectApiName, actionCode, actionDisplayName);
            log.info("functionPrivilegeService.createFuncCode, user:{}, objectApiName:{}, actionCode:{}, actionDisplayName:{}", user, objectApiName, actionCode, actionDisplayName);
        } catch (Exception e) {
            log.error("functionPrivilegeService.createFuncCode, user:{}, objectApiName:{}, actionCode:{}, actionDisplayName:{} ", user, objectApiName, actionCode, actionDisplayName, e);
            /**
             * 如果报错是：
             * "初始化功能权限失败,原因:功能唯一标识重复查看物流"
             * "初始化功能权限失败,原因:功能唯一标识重复确认收货"
             * 则跳过
             */
            if (!e.getMessage().contains("功能唯一标识重复")) {
                throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.CREATE_FUNCODE_FOR_OBJECT_FAILED, "给发货单创建" + actionDisplayName + "权限失败, " + e);
            }
        }
    }

    /**
     * 给角色添加权限
     * 1 发货单"查看物流"的权限，授权给"发货人员、订单管理员、订单财务、订货人员" （还要给CRM管理员）
     * 2 发货单"确认收货"的权限，授权给"发货人员                    订货人员" （还要给CRM管理员）
     * 3 产品"新建、查看列表、查看详情"，授权给"发货人员"（'新建'权限是库存的需要，也放在这了）
     */
    private void addFuncAccessToRole(User user) {
        List<String> actionCodes = Lists.newArrayList(DeliveryNoteObjConstants.Button.ViewLogistics.apiName, DeliveryNoteObjConstants.Button.ConfirmReceipt.apiName);
        //CRM管理员
        addUserDefinedFuncAccess(user, CommonConstants.CRM_MANAGER_ROLE, DeliveryNoteObjConstants.API_NAME, actionCodes);
        //发货人员
        addUserDefinedFuncAccess(user, CommonConstants.GOODS_SENDING_PERSON_ROLE, DeliveryNoteObjConstants.API_NAME, actionCodes);
        //订货人员
        addUserDefinedFuncAccess(user, CommonConstants.ORDERING_PERSON_ROLE, DeliveryNoteObjConstants.API_NAME, actionCodes);


        List<String> viewLogisticsActionCode = Lists.newArrayList(DeliveryNoteObjConstants.Button.ViewLogistics.apiName);
        //订单管理员
        addUserDefinedFuncAccess(user, CommonConstants.ORDER_MANAGER_ROLE, DeliveryNoteObjConstants.API_NAME, viewLogisticsActionCode);
        //订单财务
        addUserDefinedFuncAccess(user, CommonConstants.ORDER_FINANCE_ROLE, DeliveryNoteObjConstants.API_NAME, viewLogisticsActionCode);


        List<String> productAddFuncCodes = Lists.newArrayList(ProductFunctionNumberEnum.VIEW_LIST.getFunctionNumber(), ProductFunctionNumberEnum.VIEW_DETAIL.getFunctionNumber(), ProductFunctionNumberEnum.CREATE.getFunctionNumber());
        //发货人员
        functionPrivilegeService.updatePreDefinedFuncAccess(user, CommonConstants.GOODS_SENDING_PERSON_ROLE, productAddFuncCodes, null);
        log.info("functionPrivilegeService.updatePreDefinedFuncAccess,  user:{}, roleCode:{}, addFuncCodes:{}, deleteFuncCodes:{}", user, CommonConstants.GOODS_SENDING_PERSON_ROLE, productAddFuncCodes, null);
    }

    /**
     * 给角色添加权限
     * （functionPrivilegeService.updateUserDefinedFuncAccess可以重复调用，原来有权限a、b, 要加权限c, actionCodes=[c]）
     */
    private void addUserDefinedFuncAccess(User user, String roleCode, String objectApiName, List<String> addActionCodes) {
        functionPrivilegeService.updateUserDefinedFuncAccess(user, roleCode, objectApiName, addActionCodes, null);
        log.info("functionPrivilegeService.updateUserDefinedFuncAccess, user:{}, roleCode:{}, objectApiName:{}, addActionCodes:{}, deleteActionCodes:{}", user, roleCode, objectApiName, addActionCodes, null);
    }

    /**
     * 原来拥有订单【确认发货】权限的人（角色），默认有发货单的【新建】   权限；
     * 原来拥有订单【确认收货】权限的人（角色），默认有发货单的【确认收货】权限
     */
    public void addFuncAccessForRole(User user) {
        executor.execute(() -> {
            //可重复调用
            deliveryNotePrivilegeService.deliveryNoteAddFuncAccess(user.getTenantId(), user.getUserId());
        });
    }

    /**
     * 初始化发货单打印模板（可以多次调用，每次都是更新）
     */
    public void initPrintTemplate(User user) {
        Map headers = new HashMap();
        headers.put("x-tenant-id", user.getTenantId());
        headers.put("x-user-id", User.SUPPER_ADMIN_USER_ID);
        headers.put("Content-Type", "application/json");

        Map pathMap = new HashMap();
        pathMap.put("tenantId", user.getTenantId());

        Map queryMap = new HashMap();
        queryMap.put("initDescribeApiNames", DeliveryNoteObjConstants.API_NAME);

        try {
            Object result = templateApi.init(pathMap, queryMap, headers);
            log.info("templateApi.init (pathMap:{}, queryMap:{}, headers:{}, result:{}", pathMap, queryMap, headers, result);
        } catch (Exception e) {
            log.warn("templateApi.init (pathMap:{}, queryMap:{}, headers:{}", pathMap, queryMap, headers, e);
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.INIT_PRINT_TEMPLATE_FAILED, DeliveryNoteErrorCode.INIT_PRINT_TEMPLATE_FAILED.getMessage());
        }
    }
}