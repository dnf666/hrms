package com.facishare.crm.sfa.predefine.controller;

import com.facishare.crm.openapi.Utils;
import com.facishare.crm.sfa.utilities.util.JsonUtil;
import com.facishare.crm.sfa.utilities.util.PreDefLayoutUtil;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.appframework.privilege.FunctionPrivilegeService;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.Button;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.TableColumn;
import com.facishare.paas.metadata.impl.ui.layout.component.GroupComponent;
import com.facishare.paas.metadata.impl.ui.layout.component.RelatedObjectList;
import com.facishare.paas.metadata.ui.layout.IButton;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.facishare.paas.metadata.ui.layout.ITableColumn;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.facishare.paas.appframework.core.model.RequestContext.Android_CLIENT_INFO_PREFIX;
import static com.facishare.paas.appframework.core.model.RequestContext.CLIENT_INFO;
import static com.facishare.paas.appframework.core.model.RequestContext.IOS_CLIENT_INFO_PREFIX;

/**
 * Created by luohl on 2017/11/14.
 */
@Slf4j
public class LeadsDetailController extends SFADetailController {
    private FunctionPrivilegeService functionPrivilegeService = SpringUtil.getContext().getBean("functionPrivilegeService", FunctionPrivilegeService.class);

    @Override
    protected Result doService(Arg arg) {
        Result result = super.doService(arg);

        ILayout layout = new Layout(result.getLayout());

        handleRelatedListByHighSeaSetting(layout);

//        Optional<GroupComponent> relatedComponent = LayoutExt.of(layout).getRelatedComponent();
//        if(relatedComponent.isPresent()){
//            relatedComponent.get().addComponent(PreDefLayoutUtil.getEmailComponent());
//        }

        result.setLayout(LayoutDocument.of(layout));

        return result;
    }

    @Override
    protected Result after(Arg arg, Result result) {
       //RequestContext requestContext = getControllerContext().getRequestContext();
       //requestContext.setAttribute(CLIENT_INFO, requestContext.getClientInfo());
        Result newResult = super.after(arg, result);

        Object card = (newResult.getData()).get("picture_path");
        if(card!=null) {
            List cardlist =(List)card;
            if(cardlist==null||cardlist.isEmpty()){
                newResult.getData().put("picture_path", Lists.newArrayList());
            }else {
                Object path = ((Map) (cardlist.get(0))).get("path");
                if (path == null || StringUtils.isEmpty(path.toString())) {
                    newResult.getData().put("picture_path", Lists.newArrayList());
                }
            }
        }

        ILayout layout = new Layout(newResult.getLayout());
        specialLogicForLayout(layout);

        return result;
    }

    private void specialLogicForLayout(ILayout layout) {
        String clientInfo = getControllerContext().getRequestContext().getAttribute(CLIENT_INFO);
        log.info("specialLogicForLayout-clientInfo="+clientInfo);

        //手机端需要做的特殊处理
        if (clientInfo.startsWith(Android_CLIENT_INFO_PREFIX) || clientInfo.startsWith(IOS_CLIENT_INFO_PREFIX)) {
            List<IButton> buttons = layout.getButtons();
            List<IButton> remainButtons = Lists.newCopyOnWriteArrayList(buttons);
            removeCommonButtons(remainButtons);
            List<IButton> finalButtons = Lists.newArrayList();
            removePhoneButtons(remainButtons);

            for (IButton item: buttons) {
                if(item.getAction().equals(ObjectAction.ADD_EVENT.getActionCode())){
                    finalButtons.add(createButton(ObjectAction.SALE_RECORD));
                    break;
                }
            }
            finalButtons.add(createButton(ObjectAction.DIAL));
            finalButtons.add(createButton(ObjectAction.SEND_MAIL));
            finalButtons.add(createButton(ObjectAction.DISCUSS));
            finalButtons.add(createButton(ObjectAction.SCHEDULE));
            finalButtons.add(createButton(ObjectAction.REMIND));
            finalButtons.addAll(remainButtons);
            layout.setButtons(finalButtons);

            LayoutExt.of(layout).getRelatedComponent().ifPresent(x -> {
                try {
                    List<IComponent> childComponents = x.getChildComponents();
                    childComponents.removeIf(k -> "CRMEmail_related_list".equals(k.get("api_name", String.class)));
                    x.setChildComponents(childComponents);
                } catch (MetadataServiceException e) {
                    log.error("getChildComponents error", e);
                }
            });
        }

    }
    private IButton createButton(ObjectAction action) {
        IButton button = new Button();
        button.setName(action.getActionCode() + "_button_" + IButton.ACTION_TYPE_DEFAULT);
        button.setAction(action.getActionCode());
        button.setActionType(IButton.ACTION_TYPE_DEFAULT);
        button.setLabel(action.getActionLabel());
        return button;
    }

    private void removeCommonButtons(List<IButton> buttons) {
        buttons.removeIf(k -> k.getAction().equals(ObjectAction.VIEW_DETAIL.getActionCode())
                | k.getAction().equals(ObjectAction.CREATE.getActionCode())
                || k.getAction().equals(ObjectAction.BATCH_EXPORT.getActionCode())
                || k.getAction().equals(ObjectAction.BATCH_IMPORT.getActionCode())
                || k.getAction().equals(ObjectAction.VIEW_ENTIRE_BPM.getActionCode())
                || k.getAction().equals(ObjectAction.CHANGE_BPM_APPROVER.getActionCode())
                || k.getAction().equals(ObjectAction.STOP_BPM.getActionCode())
                || k.getAction().equals(ObjectAction.SALE_RECORD.getActionCode())
                ||k.getAction().equals(ObjectAction.DIAL.getActionCode())
                || k.getAction().equals(ObjectAction.SEND_MAIL.getActionCode())
                || k.getAction().equals(ObjectAction.DISCUSS.getActionCode())
                || k.getAction().equals(ObjectAction.SCHEDULE.getActionCode())
                || k.getAction().equals(ObjectAction.REMIND.getActionCode())
                || k.getAction().equals(ObjectAction.VIEW_LIST.getActionCode())
        );
    }

    private void removePhoneButtons(List<IButton> buttons) {
        buttons.removeIf(k -> k.getAction().equals(ObjectAction.PRINT.getActionCode())
                || k.getAction().equals(ObjectAction.EDIT_TEAM_MEMBER.getActionCode())
                || k.getAction().equals("Merge")
                || k.getAction().equals("ModifyLog_Recover")
                || k.getAction().equals(ObjectAction.VIEW_FEED_CARD.getActionCode())
                || k.getAction().equals(ObjectAction.ADD_EVENT.getActionCode())
        );
    }
}
