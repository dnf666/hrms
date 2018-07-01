package com.facishare.crm.deliverynote.predefine.manager;

import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.DescribeLogicService;
import com.facishare.paas.appframework.metadata.dto.DescribeResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.ui.layout.ILayout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 对象定义的创建（主要是初始化时用到）
 *
 * Created by chenzs on 2018/1/9.
 */
@Service
@Slf4j
public class ObjectDescribeCreateManager {
    @Resource
    private DescribeLogicService describeLogicService;
    @Resource
    private ObjectDescribeDraftManager objectDescribeDraftManager;
    @Resource
    private LayoutManager layoutManager;

    /**
     * 创建"发货单"对象
     */
    public DescribeResult createDeliveryNoteDescribeAndLayout(String tenantId, String fsUserId) {
        IObjectDescribe objectDescribeDraft = objectDescribeDraftManager.generateDeliveryNoteDescribeDraft(tenantId, fsUserId);
        ILayout detailLayout = layoutManager.generateDeliveryNoteDetailLayout(tenantId, fsUserId);
        ILayout listLayout = layoutManager.generateDeliveryNoteListLayout(tenantId, fsUserId);

        return createDescribe(tenantId, fsUserId, objectDescribeDraft, detailLayout, listLayout);
    }

    /**
     * 创建"发货单产品"对象
     */
    public DescribeResult createDeliveryNoteProductDescribeAndLayout(String tenantId, String fsUserId) {
        IObjectDescribe objectDescribeDraft = objectDescribeDraftManager.generateDeliveryNoteProductDescribeDraft(tenantId, fsUserId);
        ILayout detailLayout = layoutManager.generateDeliveryNoteProductDetailLayout(tenantId, fsUserId);
        ILayout listLayout = layoutManager.generateDeliveryNoteProductListLayout(tenantId, fsUserId);

        return createDescribe(tenantId, fsUserId, objectDescribeDraft, detailLayout, listLayout);
    }

    private DescribeResult createDescribe(String tenantId, String fsUserId, IObjectDescribe objectDescribeDraft, ILayout detailLayout, ILayout listLayout) {
        String describeJson = objectDescribeDraft.toJsonString();
        String detailLayoutJson = detailLayout.toJsonString();
        String listLayoutJson = listLayout.toJsonString();
        User user = new User(tenantId, fsUserId);
        DescribeResult describeResult = describeLogicService.createDescribe(user, describeJson, detailLayoutJson, listLayoutJson, true, true);
        log.info("describeLogicService.createDescribe user:{}, jsonData:{}, jsonLayout:{}, jsonListLayout:{}, isActive:{}, isIncludeLayout:{}, result:{}",
                user, describeJson, detailLayoutJson, listLayoutJson, true, true, describeResult);
        return describeResult;
    }
}