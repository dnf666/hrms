package com.facishare.crm.sfa.predefine.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.alibaba.fastjson.JSON;
import com.facishare.crm.openapi.Utils;
import com.facishare.crm.sfa.predefine.service.model.ImportPartnerAndOutInfoByName;
import com.facishare.enterprise.common.result.Result;
import com.facishare.enterprise.common.result.ResultCode;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.SearchTemplateQueryExt;
import com.facishare.paas.appframework.metadata.dto.sfa.BulkChangePartner;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.fs.enterprise.relation.outapi.service.EnterpriseRelationService;
import org.fs.enterprise.relation.outapi.vo.RelationDownstreamVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@ServiceModule("partner")
@Service
@Slf4j
public class PartnerService {
    @Autowired
    ServiceFacade serviceFacade;
    @Autowired
    EnterpriseRelationService enterpriseRelationService;

    @ServiceMethod("import_with_partner_name")
    public Map<String, ImportPartnerAndOutInfoByName.PartnerOutInfo> ImportPartnerAndOutInfoByName(ImportPartnerAndOutInfoByName.Arg arg, ServiceContext serviceContext) {
        User user = serviceContext.getUser();
        List<ImportPartnerAndOutInfoByName.importArg> importArgs = arg.getImportArgs();
        Set<String> nameList = importArgs.stream().map(k -> k.getName()).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(nameList)) {
            return Maps.newHashMap();
        }
        SearchTemplateQueryExt searchTemplateQueryExt = SearchTemplateQueryExt.of(new SearchTemplateQuery());
        searchTemplateQueryExt.setLimit(500);
        searchTemplateQueryExt.addFilter(Operator.EQ, IObjectData.NAME, Lists.newArrayList(nameList));
        QueryResult<IObjectData> partnerResult = serviceFacade.findBySearchQuery(user, Utils.PARTNER_API_NAME, searchTemplateQueryExt.toSearchTemplateQuery());
        List<IObjectData> partnerList = partnerResult.getData();
        Map<String, String> partnerIdNameMap = partnerList.stream().collect(Collectors.toMap(k -> k.getName(), k -> k.getId(), (k, v) -> v));
        Map<String, RelationDownstreamVo> relationDownstreamVoMap = findRelationDownstreamVoByPartnerIds(user, Sets.newHashSet(partnerIdNameMap.values()));
        Map<String, ImportPartnerAndOutInfoByName.PartnerOutInfo> resultMap = importArgs.stream().collect(Collectors.toMap(k -> k.getRowNo(), k -> {
            String partnerId = partnerIdNameMap.get(k.getName());
            ImportPartnerAndOutInfoByName.PartnerOutInfo partnerOutInfo = new ImportPartnerAndOutInfoByName.PartnerOutInfo();
            if (StringUtils.isNotEmpty(partnerId)) {
                partnerOutInfo.setId(partnerId);
                partnerOutInfo.setName(k.getName());
                RelationDownstreamVo relationDownstream = relationDownstreamVoMap.get(partnerId);
                if (relationDownstream != null) {
                    partnerOutInfo.setOutOwner(relationDownstream.getRelationOwnerOuterId());
                    partnerOutInfo.setOutTenantId(relationDownstream.getDownstreamEi());
                }
            }
            return partnerOutInfo;
        }, (k, v) -> v));
        return resultMap;
    }

    /**
     * 请求深研接口，根据合作伙伴id获取外部企业id和外部负责人id
     */
    public Map<String, RelationDownstreamVo> findRelationDownstreamVoByPartnerIds(User user, Set<String> partnerIds) {
        Result<Map<String, RelationDownstreamVo>> result = enterpriseRelationService.batchGetRelationDownstream(Utils.PARTNER_API_NAME, Integer.parseInt(user.getTenantId()), Lists.newArrayList(partnerIds));
        log.info("PartnerService findRelationDownstreamVoByPartnerIds,do enterpriseRelationService.batchGetRelationDownstream," +
                "tenantId {},partnerIds {},result:{}", user.getTenantId(), JSON.toJSONString(partnerIds), JSON.toJSONString(result));
        if (result.getErrCode() != ResultCode.SUCCESS.getErrorCode()) {
            //如果请求失败时
            log.error("enterpriseRelationService batchGetRelationDownstream error,tenantId:{},errorCode:{},errorMsg:{},errorDescription:{}",
                    user.getTenantId(), result.getErrCode(), result.getErrMessage(), result.getErrDescription());
            return Maps.newHashMap();
        }
        return result.getData();
    }

    public void changePartnerAndOwner(User user, String apiName, Set<String> dataIds, String partnerId) {
        this.changePartnerAndOwner(user, apiName, dataIds, partnerId, null);
    }

    public void changePartnerAndOwner(User user, String apiName, Set<String> dataIds, String partnerId, Long outOwnerId) {
        if (StringUtils.isEmpty(partnerId)) {
            return;
        }
        log.info("PartnerService changePartnerAndOwner,user {},apiName {},dataIds {},partnerId {},outOwnerId {}", JSON.toJSONString(user), apiName, dataIds, partnerId, outOwnerId);
        Integer outTenantId = null;
        Map<String, RelationDownstreamVo> downstreamMap = this.findRelationDownstreamVoByPartnerIds(user, Sets.newHashSet(partnerId));
        RelationDownstreamVo downstream = downstreamMap.get(partnerId);
        if (downstream != null) {
            //设置外部企业和外部负责人
            outTenantId = downstream.getDownstreamEi();
            if (Objects.isNull(outOwnerId)) {
                outOwnerId = downstream.getRelationOwnerOuterId();
            }
        } else {
            outOwnerId = null;
        }
        BulkChangePartner.Pojo pojo = BulkChangePartner.Pojo.builder()
                .partnerId(partnerId)
                .outTenantId(outTenantId)
                .outOwner(outOwnerId)
                .objectIds(Sets.newHashSet(dataIds))
                .build();
        serviceFacade.bulkChangePartner(user, apiName, pojo);
    }

    public void removePartner(User user, String apiName, Set<String> dataIds) {
        BulkChangePartner.Pojo pojo = BulkChangePartner.Pojo.builder()
                .partnerId(null)
                .outTenantId(null)
                .outOwner(null)
                .objectIds(dataIds)
                .build();
        serviceFacade.bulkChangePartner(user, apiName, pojo);

    }
}
