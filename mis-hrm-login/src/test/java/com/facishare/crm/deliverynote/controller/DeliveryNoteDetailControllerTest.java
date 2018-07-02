package com.facishare.crm.deliverynote.controller;

import com.facishare.crm.deliverynote.predefine.controller.DeliveryNoteDetailController;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.ILayout;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class DeliveryNoteDetailControllerTest {

    @Test
    public void after_Success() throws Exception {
        Method method = DeliveryNoteDetailController.class.getDeclaredMethod("delRelatedProductObjectAddButton", StandardDetailController.Result.class);
        method.setAccessible(true);

        String resultString = "{\n" +
                "            \"components\":[\n" +
                "                {\n" +
                "                    \"buttons\":[\n" +
                "\n" +
                "                    ],\n" +
                "                    \"child_components\":[\n" +
                "                        {\n" +
                "                            \"field_section\":[\n" +
                "                                {\n" +
                "                                    \"show_header\":true,\n" +
                "                                    \"form_fields\":[\n" +
                "                                        {\n" +
                "                                            \"is_readonly\":true,\n" +
                "                                            \"is_required\":true,\n" +
                "                                            \"render_type\":\"auto_number\",\n" +
                "                                            \"field_name\":\"name\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"is_readonly\":false,\n" +
                "                                            \"is_required\":true,\n" +
                "                                            \"render_type\":\"object_reference\",\n" +
                "                                            \"field_name\":\"sales_order_id\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"is_readonly\":false,\n" +
                "                                            \"is_required\":true,\n" +
                "                                            \"render_type\":\"date\",\n" +
                "                                            \"field_name\":\"delivery_date\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"is_readonly\":false,\n" +
                "                                            \"is_required\":false,\n" +
                "                                            \"render_type\":\"select_one\",\n" +
                "                                            \"field_name\":\"express_org\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"is_readonly\":false,\n" +
                "                                            \"is_required\":false,\n" +
                "                                            \"render_type\":\"text\",\n" +
                "                                            \"field_name\":\"express_order_id\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"is_readonly\":false,\n" +
                "                                            \"is_required\":true,\n" +
                "                                            \"render_type\":\"object_reference\",\n" +
                "                                            \"field_name\":\"delivery_warehouse_id\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"is_readonly\":false,\n" +
                "                                            \"is_required\":true,\n" +
                "                                            \"render_type\":\"employee\",\n" +
                "                                            \"field_name\":\"owner\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"is_readonly\":false,\n" +
                "                                            \"is_required\":false,\n" +
                "                                            \"render_type\":\"text\",\n" +
                "                                            \"field_name\":\"remark\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"is_readonly\":true,\n" +
                "                                            \"is_required\":false,\n" +
                "                                            \"render_type\":\"select_one\",\n" +
                "                                            \"field_name\":\"status\"\n" +
                "                                        }\n" +
                "                                    ],\n" +
                "                                    \"api_name\":\"base_field_section__c\",\n" +
                "                                    \"header\":\"基本信息\"\n" +
                "                                },\n" +
                "                                {\n" +
                "                                    \"show_header\":true,\n" +
                "                                    \"form_fields\":[\n" +
                "                                        {\n" +
                "                                            \"is_readonly\":true,\n" +
                "                                            \"is_required\":false,\n" +
                "                                            \"render_type\":\"employee\",\n" +
                "                                            \"field_name\":\"created_by\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"is_readonly\":true,\n" +
                "                                            \"is_required\":false,\n" +
                "                                            \"render_type\":\"date_time\",\n" +
                "                                            \"field_name\":\"create_time\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"is_readonly\":true,\n" +
                "                                            \"is_required\":false,\n" +
                "                                            \"render_type\":\"employee\",\n" +
                "                                            \"field_name\":\"last_modified_by\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"is_readonly\":true,\n" +
                "                                            \"is_required\":false,\n" +
                "                                            \"render_type\":\"date_time\",\n" +
                "                                            \"field_name\":\"last_modified_time\"\n" +
                "                                        }\n" +
                "                                    ],\n" +
                "                                    \"api_name\":\"system_field_section__c\",\n" +
                "                                    \"header\":\"系统信息\"\n" +
                "                                }\n" +
                "                            ],\n" +
                "                            \"buttons\":[\n" +
                "\n" +
                "                            ],\n" +
                "                            \"api_name\":\"form_component\",\n" +
                "                            \"type\":\"form\"\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"type\":\"group\",\n" +
                "                    \"api_name\":\"detailInfo\",\n" +
                "                    \"header\":\"详细信息\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"buttons\":[\n" +
                "\n" +
                "                    ],\n" +
                "                    \"child_components\":[\n" +
                "                        {\n" +
                "                            \"buttons\":[\n" +
                "                                {\n" +
                "                                    \"action_type\":\"default\",\n" +
                "                                    \"api_name\":\"Add_button_default\",\n" +
                "                                    \"action\":\"Add\",\n" +
                "                                    \"label\":\"新建\"\n" +
                "                                }\n" +
                "                            ],\n" +
                "                            \"child_components\":[\n" +
                "                                {\n" +
                "                                    \"buttons\":[\n" +
                "                                        {\n" +
                "                                            \"action_type\":\"default\",\n" +
                "                                            \"api_name\":\"Add_button_default\",\n" +
                "                                            \"action\":\"Add\",\n" +
                "                                            \"label\":\"新建\"\n" +
                "                                        }\n" +
                "                                    ],\n" +
                "                                    \"type\":\"table\",\n" +
                "                                    \"api_name\":\"DeliveryNoteProductObj_table_component\",\n" +
                "                                    \"related_list_name\":\"target_related_list_dnp_delivery_note_id\",\n" +
                "                                    \"header\":\"预设业务类型\",\n" +
                "                                    \"include_fields\":[\n" +
                "                                        {\n" +
                "                                            \"label\":\"发货单产品id\",\n" +
                "                                            \"render_type\":\"auto_number\",\n" +
                "                                            \"field_name\":\"name\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"label\":\"发货单编号\",\n" +
                "                                            \"render_type\":\"master_detail\",\n" +
                "                                            \"field_name\":\"delivery_note_id\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"label\":\"销售订单编号\",\n" +
                "                                            \"render_type\":\"object_reference\",\n" +
                "                                            \"field_name\":\"sales_order_id\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"label\":\"产品名称\",\n" +
                "                                            \"render_type\":\"object_reference\",\n" +
                "                                            \"field_name\":\"product_id\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"label\":\"产品规格\",\n" +
                "                                            \"render_type\":\"quote\",\n" +
                "                                            \"field_name\":\"specs\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"label\":\"单位\",\n" +
                "                                            \"render_type\":\"quote\",\n" +
                "                                            \"field_name\":\"unit\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"label\":\"订单产品数量\",\n" +
                "                                            \"render_type\":\"number\",\n" +
                "                                            \"field_name\":\"order_product_amount\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"label\":\"已发货数\",\n" +
                "                                            \"render_type\":\"number\",\n" +
                "                                            \"field_name\":\"has_delivered_num\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"label\":\"实际库存\",\n" +
                "                                            \"render_type\":\"quote\",\n" +
                "                                            \"field_name\":\"real_stock\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"label\":\"本次发货数\",\n" +
                "                                            \"render_type\":\"number\",\n" +
                "                                            \"field_name\":\"delivery_num\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"label\":\"备注\",\n" +
                "                                            \"render_type\":\"text\",\n" +
                "                                            \"field_name\":\"remark\"\n" +
                "                                        }\n" +
                "                                    ],\n" +
                "                                    \"ref_object_api_name\":\"default__c\"\n" +
                "                                }\n" +
                "                            ],\n" +
                "                            \"type\":\"multi_table\",\n" +
                "                            \"header\":\"发货单产品\",\n" +
                "                            \"ref_object_api_name\":\"DeliveryNoteProductObj\",\n" +
                "                            \"related_list_name\":\"target_related_list_dnp_delivery_note_id\"\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"type\":\"group\",\n" +
                "                    \"api_name\":\"DeliveryNoteProductObj_md_group_component\",\n" +
                "                    \"header\":\"发货单产品\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"buttons\":[\n" +
                "\n" +
                "                    ],\n" +
                "                    \"child_components\":[\n" +
                "                        {\n" +
                "                            \"buttons\":[\n" +
                "                                {\n" +
                "                                    \"action_type\":\"default\",\n" +
                "                                    \"api_name\":\"AddEvent_button_default\",\n" +
                "                                    \"action\":\"AddEvent\",\n" +
                "                                    \"label\":\"新建销售记录\"\n" +
                "                                }\n" +
                "                            ],\n" +
                "                            \"type\":\"related_record\",\n" +
                "                            \"api_name\":\"sale_log\",\n" +
                "                            \"header\":\"销售记录\",\n" +
                "                            \"fields\":{\n" +
                "                                \"operation_time\":{\n" +
                "                                    \"render_type\":\"date_time\",\n" +
                "                                    \"field_name\":\"create_time\"\n" +
                "                                },\n" +
                "                                \"message\":{\n" +
                "                                    \"render_type\":\"text\",\n" +
                "                                    \"field_name\":\"content\"\n" +
                "                                },\n" +
                "                                \"user\":{\n" +
                "                                    \"render_type\":\"employee\",\n" +
                "                                    \"field_name\":\"sender_id\"\n" +
                "                                }\n" +
                "                            }\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"buttons\":[\n" +
                "                                {\n" +
                "                                    \"action_type\":\"default\",\n" +
                "                                    \"api_name\":\"AddTeamMember_button_default\",\n" +
                "                                    \"action\":\"AddTeamMember\",\n" +
                "                                    \"label\":\"添加团队成员\"\n" +
                "                                },\n" +
                "                                {\n" +
                "                                    \"action_type\":\"default\",\n" +
                "                                    \"api_name\":\"EditTeamMember_button_default\",\n" +
                "                                    \"action\":\"EditTeamMember\",\n" +
                "                                    \"label\":\"编辑团队成员\"\n" +
                "                                },\n" +
                "                                {\n" +
                "                                    \"action_type\":\"default\",\n" +
                "                                    \"api_name\":\"DeleteTeamMember_button_default\",\n" +
                "                                    \"action\":\"DeleteTeamMember\",\n" +
                "                                    \"label\":\"删除团队成员\"\n" +
                "                                }\n" +
                "                            ],\n" +
                "                            \"type\":\"user_list\",\n" +
                "                            \"api_name\":\"relevant_team_component\",\n" +
                "                            \"header\":\"相关团队\",\n" +
                "                            \"is_show_avatar\":true,\n" +
                "                            \"include_fields\":[\n" +
                "                                {\n" +
                "                                    \"embedded_fields\":[\n" +
                "                                        {\n" +
                "                                            \"employee_render_type\":\"text\",\n" +
                "                                            \"employee_render_fields\":\"name\",\n" +
                "                                            \"label\":\"姓名\",\n" +
                "                                            \"render_type\":\"employee_nest\",\n" +
                "                                            \"field_name\":\"teamMemberEmployee\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"employee_render_type\":\"text\",\n" +
                "                                            \"employee_render_fields\":\"post\",\n" +
                "                                            \"label\":\"职位\",\n" +
                "                                            \"render_type\":\"employee_nest\",\n" +
                "                                            \"field_name\":\"teamMemberEmployee\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"label\":\"成员角色\",\n" +
                "                                            \"render_type\":\"select_one\",\n" +
                "                                            \"field_name\":\"teamMemberRole\"\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                            \"label\":\"成员权限类型\",\n" +
                "                                            \"render_type\":\"select_one\",\n" +
                "                                            \"field_name\":\"teamMemberPermissionType\"\n" +
                "                                        }\n" +
                "                                    ],\n" +
                "                                    \"render_type\":\"embedded_object_list\",\n" +
                "                                    \"field_name\":\"relevant_team\"\n" +
                "                                }\n" +
                "                            ]\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"type\":\"group\",\n" +
                "                    \"api_name\":\"relatedObject\",\n" +
                "                    \"header\":\"相关\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"buttons\":[\n" +
                "\n" +
                "                    ],\n" +
                "                    \"type\":\"simple\",\n" +
                "                    \"api_name\":\"top_info\",\n" +
                "                    \"header\":\"顶部信息\",\n" +
                "                    \"field_section\":[\n" +
                "                        {\n" +
                "                            \"api_name\":\"detail\",\n" +
                "                            \"form_fields\":[\n" +
                "                                {\n" +
                "                                    \"field_name\":\"sales_order_id\",\n" +
                "                                    \"is_readonly\":true,\n" +
                "                                    \"render_type\":\"object_reference\",\n" +
                "                                    \"is_required\":true\n" +
                "                                },\n" +
                "                                {\n" +
                "                                    \"field_name\":\"delivery_date\",\n" +
                "                                    \"is_readonly\":true,\n" +
                "                                    \"render_type\":\"date\",\n" +
                "                                    \"is_required\":true\n" +
                "                                },\n" +
                "                                {\n" +
                "                                    \"field_name\":\"express_org\",\n" +
                "                                    \"is_readonly\":true,\n" +
                "                                    \"render_type\":\"select_one\",\n" +
                "                                    \"is_required\":true\n" +
                "                                },\n" +
                "                                {\n" +
                "                                    \"field_name\":\"express_order_id\",\n" +
                "                                    \"is_readonly\":true,\n" +
                "                                    \"render_type\":\"text\",\n" +
                "                                    \"is_required\":true\n" +
                "                                },\n" +
                "                                {\n" +
                "                                    \"field_name\":\"delivery_warehouse_id\",\n" +
                "                                    \"is_readonly\":true,\n" +
                "                                    \"render_type\":\"object_reference\",\n" +
                "                                    \"is_required\":true\n" +
                "                                },\n" +
                "                                {\n" +
                "                                    \"field_name\":\"status\",\n" +
                "                                    \"is_readonly\":true,\n" +
                "                                    \"render_type\":\"select_one\",\n" +
                "                                    \"is_required\":true\n" +
                "                                }\n" +
                "                            ]\n" +
                "                        }\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"buttons\":[\n" +
                "\n" +
                "                    ],\n" +
                "                    \"child_components\":[\n" +
                "                        {\n" +
                "                            \"buttons\":[\n" +
                "\n" +
                "                            ],\n" +
                "                            \"type\":\"related_record\",\n" +
                "                            \"api_name\":\"operation_log\",\n" +
                "                            \"header\":\"修改记录\",\n" +
                "                            \"fields\":{\n" +
                "                                \"operation_time\":{\n" +
                "                                    \"render_type\":\"date_time\",\n" +
                "                                    \"field_name\":\"operation_time\"\n" +
                "                                },\n" +
                "                                \"message\":{\n" +
                "                                    \"render_type\":\"text\",\n" +
                "                                    \"field_name\":\"log_msg\"\n" +
                "                                },\n" +
                "                                \"user\":{\n" +
                "                                    \"render_type\":\"employee\",\n" +
                "                                    \"field_name\":\"user_id\"\n" +
                "                                }\n" +
                "                            }\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"type\":\"group\",\n" +
                "                    \"api_name\":\"otherInfo\",\n" +
                "                    \"header\":\"其他信息\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"last_modified_time\":\"Jan 22, 2018 4:00:28 PM\",\n" +
                "            \"is_deleted\":false,\n" +
                "            \"version\":2,\n" +
                "            \"create_time\":\"Jan 22, 2018 4:00:05 PM\",\n" +
                "            \"_id\":\"5a659a05830bdbac27873f51\",\n" +
                "            \"api_name\":\"DeliveryNoteObj_default_layout__c\",\n" +
                "            \"created_by\":\"1000\",\n" +
                "            \"display_name\":\"默认布局\",\n" +
                "            \"is_default\":true,\n" +
                "            \"last_modified_by\":\"1000\",\n" +
                "            \"layout_type\":\"detail\",\n" +
                "            \"package\":\"CRM\",\n" +
                "            \"ref_object_api_name\":\"DeliveryNoteObj\",\n" +
                "            \"tenant_id\":\"55985\"\n" +
                "        }";
        LayoutDocument layoutDocument = new LayoutDocument();
        ILayout iLayout = layoutDocument.toLayout();
        iLayout.fromJsonString(resultString);
        StandardDetailController.Result result = StandardDetailController.Result.builder().layout(LayoutDocument.of(iLayout)).build();

        method.invoke(new DeliveryNoteDetailController(), result);

        List<IComponent> componentList = result.getLayout().toLayout().getComponents();
        componentList.stream()
                .filter(component -> DeliveryNoteDetailController.DELIVERY_NOTE_PRODUCT_MD_GROUP_COMPONENT.equals(component.get(IComponent.NAME, String.class)))
                .forEach(component -> {
                    List<Map> childComponents = (List) component.get(DeliveryNoteDetailController.CHILD_COMPONENTS, ArrayList.class);
                    childComponents.forEach(childComponent -> {
                        List<Map> relatedListChildComponents = (List) childComponent.get(DeliveryNoteDetailController.CHILD_COMPONENTS);
                        relatedListChildComponents.forEach(relatedListChildComponent -> {
                            List<Map> relatedListChildButtonMap = (List) relatedListChildComponent.get(DeliveryNoteDetailController.BUTTONS);
                            relatedListChildButtonMap.removeIf(btnMap -> btnMap.get(DeliveryNoteDetailController.ACTION).toString().equals(ObjectAction.CREATE.getActionCode()));
                            Assert.assertTrue(relatedListChildButtonMap.size() == 0);
                        });
                    });
                });

    }

    public void objectDetail_Success() {

    }
}
