package com.facishare.crm.payment.service;

import com.facishare.crm.customeraccount.service.PrepayDetailTransferService;
import com.facishare.crm.customeraccount.service.RebateOutcomeDetailTransferService;
import com.facishare.crm.payment.service.dto.PaymentInitialize;
import com.facishare.crm.payment.service.dto.PaymentTransfer;
import com.facishare.crm.payment.service.dto.PaymentTransferDispatch;
import com.facishare.crm.payment.transfer.*;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.restdriver.TenantMetadataService;
import com.facishare.paas.appframework.metadata.restdriver.dto.GetCrmTenantList;
import com.facishare.paas.pod.client.PodClient;
import com.fxiaoke.transfer.dto.*;
import com.fxiaoke.transfer.service.BaseTransformerService;
import com.fxiaoke.transfer.service.ConnectionService;
import com.fxiaoke.transfer.service.TableSchemeService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@ServiceModule("paymenttransfer")
public class PaymentTransferService extends BaseTransformerService {


  public static final String DATA_TRANSFER_SPEC_TABLE_SQL = "select * from trade_payment where ei = ${ei}";
  public static final String DATA_TRANSFER_COMMON_TABLE_SQL = "select md.* from mt_data md INNER JOIN trade_payment p ON p.extend_obj_data_id = md.id and md.tenant_id = ${ei}";
  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentTransferService.class);
  private static final String DATA_TRANSFER_PAYMENTTRANSFER_PATH = "/API/v1/inner/object/paymenttransfer/service/transfer";
  private static final String DATA_TRANSFER_COPY_CUSTOM_FIELD_SQL = "select f.* from mt_field f INNER JOIN mt_describe d ON d.describe_id = f.describe_id where f.tenant_id = ${ei} and f.define_type = 'custom' and d.describe_api_name = 'PaymentObj' and d.is_current = true";

  @Autowired
  private ServiceFacade serviceFacade;
  @Autowired
  private PodClient podClient;
  @Autowired
  private TableSchemeService tableSchemeService;
  @Autowired
  private ConnectionService connectionService;
  @Autowired
  private PrepayDetailTransferService prepayDetailTransferService;
  @Autowired
  private RebateOutcomeDetailTransferService rebateOutcomeDetailTransferService;
  @Autowired
  private PaymentInitializeService paymentInitializeService;
  @Autowired
  private DataTransferProxy dataTransferProxy;
  @Autowired
  private DependencyTransferService dependencyTransferService;
  @Autowired
  private SearchFilterTransferService searchFilterTransferService;
  @Autowired
  private TenantMetadataService tenantMetadataService;


  private Map<String, Transformer> transformerMap = new HashMap<>();

  @PostConstruct
  public void initialize() {
    transformerMap.put("trade_payment", new PaymentTransformer(serviceFacade));
    transformerMap.put("mt_field", new CustomFieldTransformer(serviceFacade));
    transformerMap.put("mt_data", new CustomDataTransformer(serviceFacade));
  }

  /**
   * 数据迁移统一操作（特殊操作除外，如：附件、权限流程、直接执行SQL等除外）
   */
  @ServiceMethod("dispatch")
  public PaymentTransferDispatch.Result dispatch(PaymentTransferDispatch.Arg arg) {
    PaymentTransferDispatch.Result result = new PaymentTransferDispatch.Result();
    if (!arg.getAll() && StringUtils.isBlank(arg.getTenantIds())) {
      return result;
    }
    if (arg.getAll()) {
      int offset = arg.getOffset();
      int limit = arg.getLimit();
      ExecutorService executor = Executors.newFixedThreadPool(arg.getPoolSize());
      while (true) {
        if (offset >= arg.getMax()) {
          break;
        }
        GetCrmTenantList.Arg tenantIdListArg = new GetCrmTenantList.Arg();
        tenantIdListArg.setLimit(limit);
        tenantIdListArg.setOffset(offset);
        List<String> tenantIdList = tenantMetadataService
            .getCrmTenantList(tenantIdListArg);
        if (CollectionUtils.isEmpty(tenantIdList)) {
          break;
        }
        int finalOffset = offset;
        final PaymentTransferDispatch.Arg finalArg = new PaymentTransferDispatch.Arg(
            String.join(",", tenantIdList), arg.getHost());
        executor.submit(() -> {
          LOGGER.info("Payment transferring start at offset {}.", finalOffset);
          doDispatch(finalArg);
          LOGGER.info("Payment transferring end at offset {}.", finalOffset);
        });
        offset += limit;
      }
      return result;
    } else {
      return doDispatch(arg);
    }
  }

  private PaymentTransferDispatch.Result doDispatch(PaymentTransferDispatch.Arg arg) {
    LOGGER.info("Payment transferring starting {}.", arg);
    PaymentTransferDispatch.Result result = new PaymentTransferDispatch.Result();
    String biz = getEnv();
    //刷描述
    PaymentInitialize.Arg paymentInitializeArg = new PaymentInitialize.Arg();
    paymentInitializeArg.setTenantIds(arg.getTenantIds());
    paymentInitializeArg.setMode(PaymentInitialize.InitializeMode.ALL);
    PaymentInitialize.Result initializeResult = paymentInitializeService
        .initialize(paymentInitializeArg);
    if (CollectionUtils.isNotEmpty(initializeResult.getFails())) {
      LOGGER.warn("Failed in initializing describes, fails: {}", initializeResult.getFails());
      result.getFails().addAll(initializeResult.getFails());
    }
    LOGGER.info("Describe initialized in transfer dispatch.");
    //老回款自定义字段复制一份到回款明细
    createDataTransferJob(biz, arg.getTenantIds(), arg.getHost(),
        DATA_TRANSFER_PAYMENTTRANSFER_PATH,
        DATA_TRANSFER_COPY_CUSTOM_FIELD_SQL);
    //迁移专表数据
    createDataTransferJob(biz, arg.getTenantIds(), arg.getHost(),
        DATA_TRANSFER_PAYMENTTRANSFER_PATH,
        DATA_TRANSFER_SPEC_TABLE_SQL);
    //迁移通表数据
    createDataTransferJob(biz, arg.getTenantIds(), arg.getHost(),
        DATA_TRANSFER_PAYMENTTRANSFER_PATH,
        DATA_TRANSFER_COMMON_TABLE_SQL);
    LOGGER.info("Transfer Job created in transfer dispatch.");
    Set<String> tenantIdList = Arrays.stream(arg.getTenantIds().split(",")).collect(
        Collectors.toSet());
    for (String tenantId : tenantIdList) {
      ServiceContext ctx = generateServiceContext(tenantId);
      try {
        // 刷客户账户描述及数据
        prepayDetailTransferService.processTransfer(ctx);
        rebateOutcomeDetailTransferService.processTransfer(ctx);
      } catch (Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
        result.getFails().add(tenantId);
        LOGGER.warn("Failed in process customer account describe and data: {}.", tenantId);
      }
      LOGGER.info("Customer account describe and data processed.");
      //迁移字段依赖关系
      try {
        if (!dependencyTransferService.transferPayment(ctx)) {
          result.getFails().add(tenantId);
          LOGGER.warn("Failed in process dependency transfer: {}.", tenantId);
        }
      } catch (Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
      }
      LOGGER.info("Dependency data transferred.");
      //迁移自定义查询场景
      try {
        if (!searchFilterTransferService.transferPayment(ctx)) {
          result.getFails().add(tenantId);
          LOGGER.warn("Failed in process search filter transfer: {}.", tenantId);
        }
      } catch (Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
      }
      LOGGER.info("Search filter data transferred.");
    }
    LOGGER.info("Transfer dispatch finished: {}.", result);
    return result;
  }

  @ServiceMethod("dispatchext")
  public PaymentTransferDispatch.Result dispatchExt(PaymentTransferDispatch.Arg arg) {
    PaymentTransferDispatch.Result result = new PaymentTransferDispatch.Result();
    String biz = getEnv();
    // 回款级联字段
    createDataTransferJob(biz, arg.getTenantIds(), arg.getHost(),
        CustomerPaymentCascadeTransferService.TRANSFER_HOOK_PATH,
        CustomerPaymentCascadeTransferService.QUERY_SQL);
    // 回款明细级联字段
    createDataTransferJob(biz, arg.getTenantIds(), arg.getHost(),
        OrderPaymentCascadeTransferService.TRANSFER_HOOK_PATH,
        OrderPaymentCascadeTransferService.QUERY_SQL);
    return result;
  }

  public String getEnv() {
    String biz = System.getProperty("process.profile");
    if (StringUtils.isBlank(biz)) {
      return "ceshi113";
    }
    return biz.equals("fstest") ? "ceshi112" : biz;
  }

  private ServiceContext generateServiceContext(String tenantId) {
    RequestContext requestContext = RequestContext.builder().tenantId(tenantId)
        .user(Optional.of(new User(tenantId, User.SUPPER_ADMIN_USER_ID))).build();
    return new ServiceContext(requestContext, null, null);
  }

  public Boolean createDataTransferJob(String biz, String tenantIds, String host, String path,
      String sql) {
    DataTransferModel.Arg arg = new DataTransferModel.Arg();
    arg.setBiz(biz);
    arg.setEidsAll(tenantIds);
    arg.setHook(host + path);
    arg.setSql(sql);
    try {
      return dataTransferProxy.createJob(arg);
    } catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
      return false;
    }
  }

  @ServiceMethod("customeraccount")
  public PaymentTransfer.Result transferCustomerAccount(ServiceContext context,
      PaymentTransfer.Arg arg) {
    PaymentTransfer.Result result = new PaymentTransfer.Result();
    if (arg == null || StringUtils.isBlank(arg.getTenantIds())) {
      return result;
    }
    Set<String> tenantIdList = Arrays.stream(arg.getTenantIds().split(",")).collect(
        Collectors.toSet());
    for (String tenantId : tenantIdList) {
      User user = new User(tenantId, User.SUPPER_ADMIN_USER_ID);
      RequestContext requestContext = RequestContext.builder().tenantId(tenantId).user(
          Optional.of(user)).build();
      ServiceContext ctx = new ServiceContext(requestContext, context.getServiceName(),
          context.getServiceMethod());
      prepayDetailTransferService.processTransfer(ctx);
      rebateOutcomeDetailTransferService.processTransfer(ctx);
    }
    return result;
  }

  @ServiceMethod("transfer")
  public boolean doTransfer(RequestData requestData) {
    if (null == requestData) {
      return false;
    }
    transfer(requestData);
    return true;
  }

  @Override
  public void transfer(RequestData requestData) {
    ResponseData responseData = new ResponseData();
    List<SourceData> sourceDataList = requestData.getSourceDataList();
    List<SourceItem> sourceItemList = Lists.newArrayList();
    if (CollectionUtils.isEmpty(sourceDataList)) {
      LOGGER.warn("Source data to be transferred is empty.");
      return;
    }
    LOGGER.debug("Transfer request: {}, size: {}.", requestData.getOperationJob(),
        requestData.getSourceDataList().size());
    for (SourceData sourceData : sourceDataList) {
      try {
        SourceItem sourceItem = SourceItem.builder()
            .dbUrl(podClient.getResource(sourceData.getTenantId(), PKG, MODULE, "pg"))
            .table(sourceData.getTable())
            .tenantId(sourceData.getTenantId())
            .build();
        TableSchema tableSchema = tableSchemeService
            .getTableSchema(sourceData.getTable(), connectionService.biz);
        List<Record> recordList = Lists.newArrayList();
        recordList.addAll(parseRecord(sourceData, tableSchema));
        sourceItem.setRecordList(recordList);
        sourceItemList.add(sourceItem);
      } catch (Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
      }
    }
    responseData.setSourceItemList(sourceItemList);
    responseData.setOperationJob(requestData.getOperationJob());
    try {
      sendData(responseData);
    } catch (Exception e) {
      LOGGER.error("Send data error: " + e.getMessage(), e);
    }
  }

  @Override
  protected List<Record> parseRecord(SourceData sourceData, TableSchema tableSchema) {
    List<Record> records = Lists.newArrayList();
    String tableName = sourceData.getTable();
    if (StringUtils.isBlank(tableName)) {
      return records;
    }
    Transformer transformer = transformerMap.get(tableName);
    if (null != transformer) {
      records = transformer.parseRecord(sourceData, tableSchema);
    }
    return records;
  }
}
