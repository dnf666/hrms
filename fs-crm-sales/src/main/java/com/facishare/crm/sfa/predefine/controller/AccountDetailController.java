package com.facishare.crm.sfa.predefine.controller;

import com.google.common.collect.Lists;

import com.facishare.crm.openapi.Utils;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.component.GroupComponent;
import com.facishare.paas.metadata.impl.ui.layout.component.RelatedObjectList;
import com.facishare.paas.metadata.ui.layout.ILayout;

import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

/**
 * 客户对象详情页
 * <p>
 * Created by liyiguang on 2017/7/12.
 */
@Slf4j
public class AccountDetailController extends SFADetailController {

    @Override
    protected Result doService(Arg arg) {
        Result result = super.doService(arg);

        ILayout layout = new Layout(result.getLayout());
        handleRelatedListByHighSeaSetting(layout);
//        addPriceBookToLayoutComponent(layout);
        return result;
    }

    /**
     * 如果有权限就,将价目表添加到layout的里面
     *
     * @param layout
     */
    private void addPriceBookToLayoutComponent(ILayout layout) {
        Boolean havePrivilege = false;
        try {
            Map<String, Boolean> apiName2Status = serviceFacade.isHaveViewListPrivilege(getSessionContext(), Lists.newArrayList(Utils.PRICE_BOOK_API_NAME));
            havePrivilege = apiName2Status.get(Utils.PRICE_BOOK_API_NAME);
        } catch (Exception e) {
            log.error("get priceBook viewList privilege error.", e);
        }
        Optional<GroupComponent> relatedComponent = LayoutExt.of(layout).getRelatedComponent();
        if (!relatedComponent.isPresent()) {
            return;
        }
        if (havePrivilege) {
            relatedComponent.get().addComponent(getPriceBookComponent());
        }
    }


    private RelatedObjectList getPriceBookComponent() {
        RelatedObjectList component = new RelatedObjectList();
        component.setName(Utils.PRICE_BOOK_API_NAME + "related_list_generate_by_UDObjectServer__c");
        component.setHeader("价目表");
        component.setRefObjectApiName(Utils.PRICE_BOOK_API_NAME);
        component.setRelatedListName("target_related_list_pricebook");
        return component;
    }

}
