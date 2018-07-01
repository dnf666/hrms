package com.facishare.crm.sfa.predefine.controller;


import com.google.common.collect.Lists;

import com.facishare.crm.openapi.Utils;
import com.facishare.crm.sfa.utilities.util.PhoneUtil;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.appframework.privilege.FunctionPrivilegeService;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.Button;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.TableColumn;
import com.facishare.paas.metadata.impl.ui.layout.component.RelatedObjectList;
import com.facishare.paas.metadata.ui.layout.IButton;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.facishare.paas.metadata.ui.layout.ITableColumn;
import com.facishare.paas.metadata.util.SpringUtil;

import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import static com.facishare.paas.appframework.core.model.RequestContext.Android_CLIENT_INFO_PREFIX;
import static com.facishare.paas.appframework.core.model.RequestContext.CLIENT_INFO;
import static com.facishare.paas.appframework.core.model.RequestContext.IOS_CLIENT_INFO_PREFIX;

/**
 * Created by luohl on 2017/11/14.
 */
@Slf4j
public class ContactDetailBaseController extends SFADetailController {
    private FunctionPrivilegeService functionPrivilegeService = SpringUtil.getContext().getBean("functionPrivilegeService", FunctionPrivilegeService.class);


    @Override
    protected Result after(Arg arg, Result result) {
        Result newResult = super.after(arg, result);

        ObjectDataDocument objectData = result.getData();
        //web处理是否决策人这个字段类型时，无法采用字符串方式
        Object primaryContact = objectData.get("primary_contact");
        if (primaryContact != null && primaryContact instanceof Integer) {
            objectData.put("primary_contact", String.valueOf(primaryContact));
        }
        handleInvalidBirthDayField(result);
        handleInvalidCardField(newResult);

        //特殊处理手机，电话字段，将原来的text转换成单个的phone_number
        ObjectDataDocument data = newResult.getData();
        PhoneUtil.splitPhoneNumber(data);

        ILayout layout = new Layout(newResult.getLayout());
        specialLogicForLayout(layout);
        return newResult;
    }


    protected void specialLogicForLayout(ILayout layout) {
        String clientInfo = getControllerContext().getRequestContext().getAttribute(CLIENT_INFO);

        Map<String, Map<String, Boolean>> objApiNameAndActionCodePrivilegeMapping = functionPrivilegeService.batchFunPrivilegeCheck(controllerContext.getUser(),
                Lists.newArrayList(Utils.OPPORTUNITY_API_NAME, Utils.CONTACT_API_NAME, Utils.VISITING_API_NAME),
                Lists.newArrayList(ObjectAction.CREATE.getActionCode(), ObjectAction.VIEW_LIST.getActionCode()));


        LayoutExt.of(layout).getRelatedComponent().ifPresent(x -> {
            try {
                List<IComponent> childComponents = x.getChildComponents();

                Map<String, Boolean> opportunityActionCodeAndStatusMapping = objApiNameAndActionCodePrivilegeMapping.get(Utils.OPPORTUNITY_API_NAME);

                Map<String, Boolean> visitingActionCodeAndStatusMapping = objApiNameAndActionCodePrivilegeMapping.get(Utils.VISITING_API_NAME);

                boolean isHaveContactAddPrivilege = objApiNameAndActionCodePrivilegeMapping.get(Utils.CONTACT_API_NAME)
                        .getOrDefault(ObjectAction.CREATE.getActionCode(), Boolean.FALSE);

                //移除审批流程table
                childComponents.removeIf(k -> "Approval_related_list".equals(k.get("api_name", String.class)));
                //手机端移除邮件table
                if (clientInfo.startsWith(Android_CLIENT_INFO_PREFIX) || clientInfo.startsWith(IOS_CLIENT_INFO_PREFIX)) {
                    childComponents.removeIf(k -> "CRMEmail_related_list".equals(k.get("api_name", String.class)));
                }
                //移除销售线索按钮
                removeLeadsObjButtons(childComponents);

                if (!clientInfo.startsWith(Android_CLIENT_INFO_PREFIX) && !clientInfo.startsWith(IOS_CLIENT_INFO_PREFIX)) {
                    //添加拜访table页，判断功能权限
                    if (visitingActionCodeAndStatusMapping.get(ObjectAction.VIEW_LIST.getActionCode())) {

                        RelatedObjectList visiting = getVisiting();

                        List<IButton> visitingButtons = Lists.newArrayList();
                        fillButtonByPrivilege(visitingActionCodeAndStatusMapping, isHaveContactAddPrivilege, visitingButtons);
                        visiting.setButtons(visitingButtons);

                        ObjectDescribeExt visitingDescribeExt = ObjectDescribeExt.of(serviceFacade.findObject(layout.getTenantId(), "VisitingObj"));
                        List<IFieldDescribe> visitingFieldList = getVisitingFieldList(visitingDescribeExt);
                        visiting.setIncludeFields(getTableColumns(visitingFieldList));

                        childComponents.add(visiting);
                    }

                    //添加商机table页，判断功能权限
                    if (opportunityActionCodeAndStatusMapping.get(ObjectAction.VIEW_LIST.getActionCode())) {
                        RelatedObjectList opportunity = getOpprrtunity();

                        List<IButton> opportunityButtons = Lists.newArrayList();

                        fillButtonByPrivilege(opportunityActionCodeAndStatusMapping, isHaveContactAddPrivilege, opportunityButtons);
                        opportunity.setButtons(opportunityButtons);

                        ObjectDescribeExt describeExt = ObjectDescribeExt.of(serviceFacade.findObject(layout.getTenantId(), "OpportunityObj"));
                        List<IFieldDescribe> opportunityFieldList = getOpprrtunityFieldList(describeExt);
                        opportunity.setIncludeFields(getTableColumns(opportunityFieldList));

                        childComponents.add(opportunity);
                    }
                }
                x.setChildComponents(childComponents);
            } catch (MetadataServiceException e) {
                log.error("getChildComponents error", e);
            }
        });
        removeMobileClientAndPcButtons(layout, clientInfo);
    }

    private void fillButtonByPrivilege(Map<String, Boolean> visitingActionCodeAndStatusMapping, boolean isHaveContactAddPrivilege, List<IButton> vistitingButtons) {
        if (visitingActionCodeAndStatusMapping.get(ObjectAction.CREATE.getActionCode())) {
            vistitingButtons.add(createButton(ObjectAction.BULK_RELATE));
            vistitingButtons.add(createButton(ObjectAction.BULK_DISRELATE));

            if (isHaveContactAddPrivilege) {
                vistitingButtons.add(createButton(ObjectAction.CREATE));
            }
        }
    }


    private void handleInvalidBirthDayField(Result result) {
        //处理生日字段 生日字段可能存在 0000-00-00/0000-12-17/1991-10-00这三种状态,需要特殊处理
        String birthDay = (String) result.getData().get("date_of_birth");
        if (StringUtils.isEmpty(birthDay) || "0000-00-00".equals(result.getData().get("date_of_birth"))) {
            result.getData().put("date_of_birth", "");
        } else if (birthDay.startsWith("0000-")) {
            result.getData().put("date_of_birth", birthDay.replaceAll("0000-", ""));
        } else {
            result.getData().put("date_of_birth", birthDay.replaceAll("-00", ""));
        }
    }


    private void handleInvalidCardField(Result newResult) {
        Object card = (newResult.getData()).get("card");
        if (card instanceof List) {
            List cards = (List) card;
            if (cards.isEmpty()) {
                newResult.getData().put("card", Lists.newArrayList());
            } else {
                cards.stream().findFirst().ifPresent(x -> {
                    if (x instanceof Map) {
                        Object path = ((Map) x).get("path");
                        if (path == null || "".equals(path)) {
                            newResult.getData().put("card", Lists.newArrayList());
                        }
                    } else {
                        newResult.getData().put("card", Lists.newArrayList());
                    }
                });
            }
        }
    }


    private void removeMobileClientAndPcButtons(ILayout layout, String clientInfo) {
        List<IButton> buttons = layout.getButtons();
        removeCommonButtons(buttons);

        //手机端需要做的特殊处理
        if (clientInfo.startsWith(Android_CLIENT_INFO_PREFIX) || clientInfo.startsWith(IOS_CLIENT_INFO_PREFIX)) {
            removePhoneButtons(buttons);
            List<IButton> remainButtons = Lists.newCopyOnWriteArrayList(buttons);
            buttons.clear();
            buttons.add(createButton(ObjectAction.SALE_RECORD));
            buttons.add(createButton(ObjectAction.DIAL));
            buttons.add(createButton(ObjectAction.SEND_MAIL));
            buttons.add(createButton(ObjectAction.DISCUSS));
            buttons.add(createButton(ObjectAction.SCHEDULE));
            buttons.add(createButton(ObjectAction.REMIND));
            buttons.addAll(remainButtons);
        } else {
            removePcButtons(buttons);
        }
        layout.setButtons(buttons);
    }

    private void removeLeadsObjButtons(List<IComponent> childComponents) {
        childComponents.stream().forEach(childComponent -> {
            if ("LeadsObj".equals(childComponent.get("ref_object_api_name", String.class))) {
                List<IButton> childComponentButtons = childComponent.getButtons();
                childComponentButtons.removeIf(button -> ObjectAction.CREATE.getActionCode().equals(button.getAction())
                        || ObjectAction.BULK_RELATE.getActionCode().equals(button.getAction())
                        || ObjectAction.BULK_DISRELATE.getActionCode().equals(button.getAction()));
                childComponent.setButtons(childComponentButtons);
            }
        });
    }

    private void removeCommonButtons(List<IButton> buttons) {
        buttons.removeIf(k -> k.getAction().equals(ObjectAction.VIEW_DETAIL.getActionCode())
                || k.getAction().equals(ObjectAction.CREATE.getActionCode())
                || k.getAction().equals(ObjectAction.ADD_EVENT.getActionCode())
                || k.getAction().equals(ObjectAction.BATCH_EXPORT.getActionCode())
                || k.getAction().equals(ObjectAction.BATCH_IMPORT.getActionCode())
                || k.getAction().equals(ObjectAction.VIEW_ENTIRE_BPM.getActionCode())
                || k.getAction().equals(ObjectAction.CHANGE_BPM_APPROVER.getActionCode())
                || k.getAction().equals(ObjectAction.STOP_BPM.getActionCode())
                || k.getAction().equals(ObjectAction.SEND_MAIL.getActionCode())
                || k.getAction().equals(ObjectAction.DISCUSS.getActionCode())
                || k.getAction().equals(ObjectAction.SCHEDULE.getActionCode())
                || k.getAction().equals(ObjectAction.REMIND.getActionCode())
                || k.getAction().equals(ObjectAction.VIEW_FEED_CARD.getActionCode())
                || k.getAction().equals("ModifyLog_Recover")
                || k.getAction().equals("Share")
                || k.getAction().equals("Merge"));
    }

    private void removePhoneButtons(List<IButton> buttons) {
        buttons.removeIf(k -> k.getAction().equals(ObjectAction.PRINT.getActionCode()) ||
                k.getAction().equals(ObjectAction.EDIT_TEAM_MEMBER.getActionCode()));
    }

    private void removePcButtons(List<IButton> buttons) {
        buttons.removeIf(k -> k.getAction().equals(ObjectAction.START_BPM.getActionCode()) ||
                k.getAction().equals("SaveToPhone"));
    }

    private RelatedObjectList getVisiting() {
        RelatedObjectList visiting = new RelatedObjectList();
        visiting.setName("Visiting_contact_id_related_list");
        visiting.setHeader("拜访");
        visiting.setRefObjectApiName("VisitingObj");
        visiting.setRelatedListName("contact_visiting_list");
        visiting.getContainerDocument().put("relationType", 4);
        visiting.setOrder(6);
        return visiting;
    }


    private RelatedObjectList getOpprrtunity() {
        RelatedObjectList opportunity = new RelatedObjectList();
        opportunity.setName("OppoObj_contact_id_related_list");
        opportunity.setHeader("商机");
        opportunity.setRefObjectApiName("OpportunityObj");
        opportunity.setRelatedListName("contact_oppo_list");
        opportunity.getContainerDocument().put("relationType", 4);
        opportunity.setOrder(7);
        return opportunity;
    }


    private List<ITableColumn> getTableColumns(List<IFieldDescribe> fieldList) {
        List<ITableColumn> ret = fieldList.stream().filter(Objects::nonNull).map(x -> {
            ITableColumn column = new TableColumn();
            column.setLabelName(x.getLabel());
            column.setName(x.getApiName());
            column.setRenderType(LayoutExt.getRenderType(describe.getApiName(), x.getApiName(), x.getType()));
            return column;
        }).collect(Collectors.toList());

        return ret;
    }

    private List<IFieldDescribe> getOpprrtunityFieldList(ObjectDescribeExt describe) {
        List<IFieldDescribe> fieldList = Lists.newArrayList(describe.getFieldDescribeSilently(IObjectData.NAME).orElse(null));
        fieldList.add(describe.getFieldDescribe("account_id"));
        fieldList.add(describe.getFieldDescribe("expected_deal_closed_date"));
        fieldList.add(describe.getFieldDescribe("expected_deal_amount"));
        fieldList.add(describe.getFieldDescribe("status"));
        fieldList.add(describe.getFieldDescribe("owner"));
        return fieldList;
    }

    private List<IFieldDescribe> getVisitingFieldList(ObjectDescribeExt describe) {
        List<IFieldDescribe> fieldList = Lists.newArrayList(describe.getFieldDescribeSilently(IObjectData.NAME).orElse(null));
        fieldList.add(describe.getFieldDescribe("VisitingObj"));
        fieldList.add(describe.getFieldDescribe("visiting_type"));
        fieldList.add(describe.getFieldDescribe("account_id"));
        fieldList.add(describe.getFieldDescribe("visit_time"));
        fieldList.add(describe.getFieldDescribe("owner"));
        return fieldList;
    }

    private IButton createButton(ObjectAction action) {
        IButton button = new Button();
        button.setName(action.getActionCode() + "_button_" + IButton.ACTION_TYPE_DEFAULT);
        button.setAction(action.getActionCode());
        button.setActionType(IButton.ACTION_TYPE_DEFAULT);
        button.setLabel(action.getActionLabel());
        return button;
    }
}
