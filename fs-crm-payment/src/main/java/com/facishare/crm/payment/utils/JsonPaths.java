package com.facishare.crm.payment.utils;

public class JsonPaths {

  public static final String DESCRIBE_DATA = "$.data";
  public static final String DESCRIBE_LAYOUT_DETAIL = "$.layout.components[?(@.layout_type=='detailInfo')]";
  public static final String DESCRIBE_LAYOUT_DETAIL_FORM = DESCRIBE_LAYOUT_DETAIL + ".child_components[?(@.api_name=='form_component')]";
  public static final String DESCRIBE_LAYOUT_DETAIL_FORM_BASE_FIELDS = DESCRIBE_LAYOUT_DETAIL_FORM + ".field_section[?(@.api_name=='base_field_section__c')].form_fields";
  public static final String DESCRIBE_LAYOUT_DETAIL_FORM_SYSTEM_FIELDS = DESCRIBE_LAYOUT_DETAIL_FORM + ".field_section[?(@.api_name=='system_field_section__c')]";

//  public static final String DESCRIBE_LAYOUT_DETAIL_TOP = "$.layout.components[?(@.api_name=='top_info')]";
//  public static final String DESCRIBE_LAYOUT_DETAIL_TOP_FIELDS = DESCRIBE_LAYOUT_DETAIL_TOP + ".field_section[?(@.api_name=='detail')].form_fields";


//  public static final String DESCRIBE_LAYOUT_HEADER_FIELDS = "$.layout.components[?(@.api_name=='top_info')].field_section[?(@.api_name=='detail')].form_fields";
  public static final String DESCRIBE_DETAIL_DESCRIBE_FIELDS = "$.detailObjectList[?(@.fieldApiName=='payment_id')].objectDescribe.fields";
  public static final String CUSTOMER_PAYMENT_DESCRIBE_DETAIL_LAYOUT_FIELDS = "$.detailObjectList[?(@.fieldApiName=='payment_id')].layoutList[*].detail_layout.components[?(@.api_name=='form_component')].field_section[?(@.api_name=='base_field_section__c')].form_fields";
//  public static final String DESCRIBE_LAYOUT_ORDER_PAYMENT_COMPONENTS = "$.layout.components[?(@.api_name=='OrderPaymentObj_md_group_component')].child_components[?(@.type=='multi_table')].ref_object_api_name";

  public static final String DESCRIBE_LAYOUT_LIST_FIELDS = "$.objectDescribe.fields";


  public static final String DESCRIBE_LAYOUT_HEADER_FIELDS_EXTEND_ID = "$.fieldList[?(@.extend_obj_data_id==true)]";
  public static final String DETAIL_LAYOUT_BUTTONS = "$.layout.buttons";
  public static final String DETAIL_RELATED_OBJECT = "$.layout.components[?(@.api_name=='relatedObject')].child_components";
  public static final String DETAIL_RELATED_OBJECT_REBATE = "$.layout.components[?(@.api_name=='relatedObject')].child_components[?(@.ref_object_api_name=='RebateOutcomeDetailObj')].buttons";
  public static final String DETAIL_RELATED_OBJECT_PREPAY = "$.layout.components[?(@.api_name=='relatedObject')].child_components[?(@.ref_object_api_name=='PrepayDetailObj')].buttons";

  public static final String DESCRIBE_DETAIL_LAYOUT_COMPONENTS = "$.layout.components";
  public static final String DESCRIBE_DETAIL_LAYOUT_FORM = DESCRIBE_DETAIL_LAYOUT_COMPONENTS + "[?(@.api_name=='form_component')]";
  public static final String DESCRIBE_DETAIL_LAYOUT_FORM_BASE_FIELDS = DESCRIBE_DETAIL_LAYOUT_FORM + ".field_section[?(@.api_name=='base_field_section__c')].form_fields";

  public static final String CUSTOMER_PAYMENT_DESCRIBE_DETAIL_LAYOUTS = "$.detailObjectList[?(@.fieldApiName=='payment_id')].layoutList";
  public static final String ORDER_PAYMENT_LAYOUT = "$.detail_layout.components[?(@.api_name=='form_component')].field_section[?(@.api_name=='base_field_section__c')].form_fields";
}
