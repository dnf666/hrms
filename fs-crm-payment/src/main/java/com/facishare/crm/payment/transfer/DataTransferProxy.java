package com.facishare.crm.payment.transfer;

import com.facishare.rest.proxy.annotation.Body;
import com.facishare.rest.proxy.annotation.POST;
import com.facishare.rest.proxy.annotation.RestResource;

/**
 * Created on 2018/2/7.
 * todo 线上注意配置fs-paas-appframework-rest  以及  确认地址是否加入白名单
 */
@RestResource(value = "CRM_DATA_TRANSFER", desc = "数据迁移服务", contentType = "application/json")
public interface DataTransferProxy {

  @POST(value = "/transfer/db/start", desc = "数据迁移任务创建")
  Boolean createJob(@Body DataTransferModel.Arg arg);
}
