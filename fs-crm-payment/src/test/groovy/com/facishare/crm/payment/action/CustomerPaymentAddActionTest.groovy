package com.facishare.crm.payment.action

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.eclipsesource.json.Json
import org.bson.BSON
import org.bson.Document
import spock.lang.Specification

class CustomerPaymentAddActionTest extends Specification {

    def describeJson = """
{
  "index_version": 1,
  "store_table_name": "order_payment",
  "package": "CRM",
  "api_name": "OrderPaymentTempObj",
  "description": "回款明细",
  "define_type": "package",
  "display_name": "回款明细Temp",
  "fields": {
    "owner": {
      "is_index": true,
      "is_active": true,
      "is_unique": false,
      "label": "负责人",
      "type": "employee",
      "field_num": 1,
      "is_need_convert": false,
      "is_required": true,
      "api_name": "owner",
      "define_type": "package",
      "is_single": true,
      "help_text": "",
      "status": "new"
    },
    "lock_rule": {
      "is_index": false,
      "is_active": true,
      "description": "锁定规则",
      "is_unique": false,
      "default_value": "default_lock_rule",
      "rules": [],
      "label": "锁定规则",
      "type": "lock_rule",
      "field_num": 2,
      "is_need_convert": false,
      "is_required": false,
      "api_name": "lock_rule",
      "define_type": "package",
      "is_single": false,
      "help_text": "锁定规则",
      "status": "new"
    },
    "lock_status": {
      "is_index": false,
      "is_active": true,
      "description": "锁定状态",
      "is_unique": false,
      "default_value": "0",
      "label": "锁定状态",
      "type": "select_one",
      "field_num": 3,
      "is_need_convert": false,
      "is_required": false,
      "api_name": "lock_status",
      "options": [
        {
          "label": "未锁定",
          "value": "0"
        },
        {
          "label": "锁定",
          "value": "1"
        }
      ],
      "define_type": "package",
      "option_id": "f8f6e3b07ca18b590c9a3cf86e014576",
      "is_single": false,
      "help_text": "锁定状态",
      "status": "new"
    },
    "life_status": {
      "is_index": true,
      "is_active": true,
      "description": "生命状态",
      "is_unique": false,
      "default_value": "normal",
      "label": "生命状态",
      "type": "select_one",
      "field_num": 4,
      "is_need_convert": false,
      "is_required": true,
      "api_name": "life_status",
      "options": [
        {
          "label": "未生效",
          "value": "ineffective"
        },
        {
          "label": "审核中",
          "value": "under_review"
        },
        {
          "label": "正常",
          "value": "normal"
        },
        {
          "label": "变更中",
          "value": "in_change"
        },
        {
          "label": "作废",
          "value": "invalid"
        }
      ],
      "define_type": "package",
      "option_id": "18de2c4eae8cf7a180ffb0f175010e6f",
      "is_single": false,
      "help_text": "生命状态",
      "status": "new"
    },
    "life_status_before_invalid": {
      "is_index": true,
      "is_active": true,
      "pattern": "",
      "description": "作废前生命状态",
      "is_unique": false,
      "label": "作废前生命状态",
      "type": "text",
      "field_num": 5,
      "is_need_convert": false,
      "is_required": false,
      "api_name": "life_status_before_invalid",
      "define_type": "package",
      "is_single": false,
      "help_text": "作废前生命状态",
      "max_length": 256,
      "status": "new"
    },
    "name": {
      "is_index": true,
      "is_active": true,
      "prefix": "OrderPayment",
      "is_unique": true,
      "default_value": "01",
      "serial_number": 15,
      "start_number": 1,
      "label": "回款明细编号",
      "type": "auto_number",
      "is_required": true,
      "api_name": "name",
      "define_type": "system",
      "postfix": "",
      "is_single": false,
      "help_text": "",
      "status": "new"
    },
    "owner_department": {
      "default_is_expression": false,
      "is_index": false,
      "is_active": true,
      "pattern": "",
      "is_unique": false,
      "default_value": "",
      "label": "负责人所在部门",
      "type": "text",
      "default_to_zero": false,
      "is_need_convert": false,
      "is_required": false,
      "api_name": "owner_department",
      "define_type": "package",
      "is_single": true,
      "help_text": "",
      "max_length": 100,
      "status": "new"
    },
    "lock_user": {
      "is_index": false,
      "is_active": true,
      "description": "加锁人",
      "is_unique": false,
      "label": "加锁人",
      "type": "employee",
      "field_num": 6,
      "is_need_convert": false,
      "is_required": false,
      "api_name": "lock_user",
      "define_type": "package",
      "is_single": true,
      "help_text": "加锁人",
      "status": "new"
    },
    "relevant_team": {
      "embedded_fields": {
        "teamMemberEmployee": {
          "is_index": true,
          "is_need_convert": true,
          "is_required": false,
          "api_name": "teamMemberEmployee",
          "is_unique": false,
          "define_type": "package",
          "description": "成员员工",
          "label": "成员员工",
          "type": "employee",
          "is_single": true,
          "help_text": "成员员工"
        },
        "teamMemberRole": {
          "is_index": true,
          "is_need_convert": false,
          "is_required": false,
          "api_name": "teamMemberRole",
          "options": [
            {
              "label": "负责人",
              "value": "1"
            },
            {
              "label": "普通成员",
              "value": "4"
            }
          ],
          "is_unique": false,
          "define_type": "package",
          "description": "成员角色",
          "label": "成员角色",
          "type": "select_one",
          "help_text": "成员角色"
        },
        "teamMemberPermissionType": {
          "is_index": true,
          "is_need_convert": false,
          "is_required": false,
          "api_name": "teamMemberPermissionType",
          "options": [
            {
              "label": "只读",
              "value": "1"
            },
            {
              "label": "读写",
              "value": "2"
            }
          ],
          "is_unique": false,
          "define_type": "package",
          "description": "成员权限类型",
          "label": "成员权限类型",
          "type": "select_one",
          "help_text": "成员权限类型"
        }
      },
      "is_index": true,
      "is_active": true,
      "is_unique": false,
      "label": "相关团队",
      "type": "embedded_object_list",
      "is_need_convert": false,
      "is_required": false,
      "api_name": "relevant_team",
      "define_type": "package",
      "is_single": false,
      "help_text": "相关团队",
      "status": "new"
    },
    "record_type": {
      "is_index": true,
      "is_active": true,
      "description": "record_type",
      "is_unique": false,
      "label": "业务类型",
      "type": "record_type",
      "is_need_convert": false,
      "is_required": false,
      "api_name": "record_type",
      "options": [
        {
          "is_active": true,
          "api_name": "default__c",
          "description": "预设业务类型",
          "label": "预设业务类型"
        }
      ],
      "define_type": "package",
      "option_id": "504eae3cf17c3d7223be4a3e0e893ecb",
      "is_single": false,
      "index_name": "record_type",
      "status": "released"
    },
    "customer_payment_name": {
      "is_index": true,
      "is_active": true,
      "is_unique": false,
      "label": "回款编号",
      "target_api_name": "CustomerPaymentObj",
      "type": "master_detail",
      "target_related_list_name": "target_related_list_OrderPaymentTempObj_CustomerPaymentObj",
      "field_num": 7,
      "target_related_list_label": "回款明细",
      "is_required": true,
      "api_name": "customer_payment_name",
      "define_type": "package",
      "is_create_when_master_create": true,
      "is_required_when_master_create": false,
      "is_single": false,
      "help_text": "回款编号",
      "status": "new"
    },
    "payment_plan_name": {
      "is_index": true,
      "is_active": true,
      "is_unique": false,
      "label": "回款计划编号",
      "target_api_name": "PaymentPlanObj",
      "type": "object_reference",
      "target_related_list_name": "target_related_list_OrderPaymentTempObj_PaymentPlanObj",
      "field_num": 9,
      "target_related_list_label": "回款明细",
      "action_on_target_delete": "set_null",
      "is_required": false,
      "wheres": [],
      "api_name": "payment_plan_name",
      "define_type": "package",
      "is_single": false,
      "help_text": "",
      "status": "new"
    },
    "order_id": {
      "is_index": true,
      "is_active": true,
      "is_unique": false,
      "label": "订单编号",
      "target_api_name": "SalesOrderObj",
      "type": "object_reference",
      "target_related_list_name": "target_related_list_OrderPaymentTempObj_SalesOrderObj",
      "field_num": 8,
      "target_related_list_label": "回款明细",
      "action_on_target_delete": "set_null",
      "is_required": true,
      "wheres": [],
      "api_name": "order_id",
      "define_type": "package",
      "is_single": false,
      "help_text": "订单编号",
      "status": "new"
    },
    "order_account": {
      "expression_type": "currency",
      "default_is_expression": false,
      "is_index": true,
      "is_active": true,
      "length": 12,
      "is_unique": false,
      "default_value": "",
      "label": "本次回款金额",
      "currency_unit": "￥",
      "type": "currency",
      "decimal_places": 2,
      "field_num": 10,
      "default_to_zero": true,
      "is_required": true,
      "api_name": "order_account",
      "define_type": "package",
      "is_single": false,
      "round_mode": 4,
      "help_text": "",
      "max_length": 14,
      "status": "new"
    },
    "attach": {
      "file_amount_limit": 10,
      "is_index": true,
      "is_active": true,
      "is_unique": false,
      "label": "附件",
      "type": "file_attachment",
      "field_num": 11,
      "file_size_limit": 104857600,
      "is_required": false,
      "api_name": "attach",
      "define_type": "package",
      "is_single": false,
      "support_file_types": [],
      "help_text": "单个文件不得超过100M",
      "status": "new"
    },
    "remark": {
      "expression_type": "long_text",
      "default_is_expression": false,
      "is_index": true,
      "is_active": true,
      "pattern": "",
      "is_unique": false,
      "default_value": "",
      "label": "备注",
      "type": "long_text",
      "field_num": 12,
      "default_to_zero": false,
      "is_required": false,
      "api_name": "remark",
      "define_type": "package",
      "is_single": false,
      "help_text": "",
      "max_length": 2000,
      "status": "new"
    },
    "notification_time": {
      "is_index": true,
      "is_active": true,
      "is_unique": false,
      "label": "提醒时间",
      "time_zone": "GMT+8",
      "type": "date_time",
      "field_num": 13,
      "is_required": false,
      "api_name": "notification_time",
      "define_type": "package",
      "date_format": "yyyy-MM-dd HH:mm",
      "is_single": false,
      "help_text": "",
      "status": "new"
    },
    "_id": {
      "type": "text",
      "define_type": "system",
      "is_index": false,
      "is_need_convert": false,
      "is_required": false,
      "is_unique": false,
      "max_length": 200,
      "pattern": "",
      "label": "_id",
      "api_name": "_id",
      "description": "_id",
      "resource_bundle_key": "OrderPaymentTempObj._id",
      "status": "released"
    },
    "created_by": {
      "type": "text",
      "define_type": "system",
      "is_index": false,
      "is_need_convert": false,
      "is_required": false,
      "is_unique": false,
      "max_length": 200,
      "pattern": "",
      "label": "创建人",
      "api_name": "created_by",
      "description": "created_by",
      "resource_bundle_key": "OrderPaymentTempObj.created_by",
      "status": "released"
    },
    "last_modified_by": {
      "type": "text",
      "define_type": "system",
      "is_index": false,
      "is_need_convert": false,
      "is_required": false,
      "is_unique": false,
      "max_length": 200,
      "pattern": "",
      "label": "最后修改人",
      "api_name": "last_modified_by",
      "description": "last_modified_by",
      "resource_bundle_key": "OrderPaymentTempObj.last_modified_by",
      "status": "released"
    },
    "package": {
      "type": "text",
      "define_type": "system",
      "is_index": false,
      "is_need_convert": false,
      "is_required": false,
      "is_unique": false,
      "max_length": 200,
      "pattern": "",
      "label": "package",
      "api_name": "package",
      "description": "package",
      "resource_bundle_key": "OrderPaymentTempObj.package",
      "status": "released"
    },
    "tenant_id": {
      "type": "text",
      "define_type": "system",
      "is_index": false,
      "is_need_convert": false,
      "is_required": true,
      "is_unique": false,
      "max_length": 200,
      "pattern": "",
      "label": "tenant_id",
      "api_name": "tenant_id",
      "description": "tenant_id",
      "resource_bundle_key": "OrderPaymentTempObj.tenant_id",
      "status": "released"
    },
    "object_describe_id": {
      "type": "text",
      "define_type": "system",
      "is_index": false,
      "is_need_convert": false,
      "is_required": true,
      "is_unique": false,
      "max_length": 200,
      "pattern": "",
      "label": "object_describe_id",
      "api_name": "object_describe_id",
      "description": "object_describe_id",
      "resource_bundle_key": "OrderPaymentTempObj.object_describe_id",
      "status": "released"
    },
    "object_describe_api_name": {
      "type": "text",
      "define_type": "system",
      "is_index": false,
      "is_need_convert": false,
      "is_required": true,
      "is_unique": false,
      "max_length": 200,
      "pattern": "",
      "label": "object_describe_api_name",
      "api_name": "object_describe_api_name",
      "description": "object_describe_api_name",
      "resource_bundle_key": "OrderPaymentTempObj.object_describe_api_name",
      "status": "released"
    },
    "version": {
      "type": "number",
      "define_type": "system",
      "is_index": false,
      "is_need_convert": false,
      "is_required": false,
      "is_unique": false,
      "round_mode": 4,
      "length": 8,
      "decimal_places": 0,
      "label": "version",
      "api_name": "version",
      "description": "version",
      "resource_bundle_key": "OrderPaymentTempObj.version",
      "status": "released"
    },
    "create_time": {
      "type": "date_time",
      "define_type": "system",
      "is_index": true,
      "is_need_convert": false,
      "is_required": false,
      "is_unique": false,
      "time_zone": "",
      "date_format": "yyyy-MM-dd HH:mm:ss",
      "label": "创建时间",
      "api_name": "create_time",
      "description": "create_time",
      "resource_bundle_key": "OrderPaymentTempObj.create_time",
      "status": "released"
    },
    "last_modified_time": {
      "type": "date_time",
      "define_type": "system",
      "is_index": true,
      "is_need_convert": false,
      "is_required": false,
      "is_unique": false,
      "time_zone": "",
      "date_format": "yyyy-MM-dd HH:mm:ss",
      "label": "最后修改时间",
      "api_name": "last_modified_time",
      "description": "last_modified_time",
      "resource_bundle_key": "OrderPaymentTempObj.last_modified_time",
      "status": "released"
    },
    "is_deleted": {
      "type": "true_or_false",
      "define_type": "system",
      "is_index": false,
      "is_need_convert": false,
      "is_required": false,
      "is_unique": false,
      "label": "is_deleted",
      "api_name": "is_deleted",
      "description": "is_deleted",
      "default_value": false,
      "resource_bundle_key": "OrderPaymentTempObj.is_deleted",
      "status": "released"
    },
    "extend_obj_data_id": {
      "is_required": false,
      "api_name": "extend_obj_data_id",
      "is_unique": true,
      "description": "连接通表的记录ID,扩展字段用",
      "define_type": "system",
      "label": "扩展字段在mt_data中的记录ID",
      "type": "text",
      "maxLength": 64,
      "is_extend": false
    }
  }
}
"""

    def testParse() {
        when:
        def d = [
                "active"          : true,
                "include_layout"  : false,
                "json_data"       : describeJson,
                "json_layout"     : "",
                "json_list_layout": ""
        ]

        println(new JSONObject(d).toJSONString())

        then:
        noExceptionThrown()
    }
}
