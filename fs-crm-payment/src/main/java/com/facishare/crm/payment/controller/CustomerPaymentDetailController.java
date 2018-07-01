package com.facishare.crm.payment.controller;

import com.facishare.crm.payment.constant.CustomerPaymentObj;
import com.facishare.crm.payment.constant.OrderPaymentObj;
import com.facishare.crm.payment.service.CustomerPaymentService;
import com.facishare.crm.payment.utils.JsonObjectUtils;
import com.facishare.crm.payment.utils.JsonPaths;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class CustomerPaymentDetailController extends StandardDetailController {

  private CustomerPaymentService customerPaymentService = SpringUtil.getContext()
      .getBean(CustomerPaymentService.class);

  @Override
  protected Result doService(Arg arg) {
    Result r = super.doService(arg);
    if(r.getData().containsKey(CustomerPaymentObj.FIELD_ID)){
      ServiceContext serviceContext = new ServiceContext( controllerContext.getRequestContext(), null,
              null);
      List<Map> orders=customerPaymentService.queryOrderPaymentList(serviceContext,r.getData().get(CustomerPaymentObj.FIELD_ID).toString());
      Set<String> set = Sets.newHashSet();
      orders.forEach(item->set.add(item.getOrDefault(OrderPaymentObj.FIELD_ORDER_ID,"").toString()));
      if (CollectionUtils.isNotEmpty(set)) {
        Joiner joiner = Joiner.on(",");
        String order_id = joiner.join(set);
        r.getData().put(CustomerPaymentObj.FIELD_ORDER_ID,order_id);
      }
    }
    r.setData(customerPaymentService.parseOrderNames(controllerContext.getUser(),
        Lists.newArrayList(r.getData())).get(0));


    r = JsonObjectUtils.append(r, Result.class, JsonPaths.DETAIL_RELATED_OBJECT, generateOpenPayLayout());
    return r;
  }

  private Map<String, Object> generateOpenPayLayout() {
    Map<String, Object> openPayLayout = new HashMap<>();
    openPayLayout.put("api_name", "payment_record_related_list");
    openPayLayout.put("buttons", Lists.newArrayList());
    openPayLayout.put("header", "企业收款明细");
    openPayLayout.put("is_hidden", false);
    openPayLayout.put("is_show_avatar", true);
    openPayLayout.put("include_fields", LayoutExt.buildPaymentTableColumnList(true).stream().map( c ->
          ImmutableMap.of("api_name", c.getName(), "field_name", c.getName(), "label", c.getLabelName(), "render_type", c.getRenderType())
        ).collect(Collectors.toList()));
    openPayLayout.put("order", 3);
    openPayLayout.put("related_list_name", "payment_record_LIST");
    openPayLayout.put("ref_object_api_name", "payment_record");
    openPayLayout.put("relationType", 2);
    openPayLayout.put("type", "relatedlist");
    return openPayLayout;
  }
}
