package com.facishare.crm.sfa.predefine.service;

import com.facishare.crm.openapi.Utils;
import com.facishare.crm.sfa.predefine.service.model.HistoryCasesModel;
import com.facishare.crm.sfa.utilities.constant.CasesConstants;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.metadata.MetaDataFindService;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.search.IDataRightsParameter;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.search.DataRightsParameter;
import com.facishare.paas.metadata.impl.search.OrderBy;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by luxin on 2018/4/3.
 */
@ServiceModule("cases")
@Component
public class CasesServiceImpl implements CasesService {
    @Autowired
    private MetaDataFindService metaDataFindService;

    @Override
    @ServiceMethod("get_history_cases")
    public HistoryCasesModel.Result getHistoryCases(ServiceContext context, HistoryCasesModel.Arg arg) {
        SearchTemplateQuery query = new SearchTemplateQuery();
        query.setOffset(arg.getOffset() == null ? 0 : arg.getOffset());
        query.setLimit(arg.getLimit() == null ? 5 : arg.getLimit());

        //需要过滤数据权限
        IDataRightsParameter dataRightsParameter = new DataRightsParameter();
        dataRightsParameter.setRoleType("o");
        dataRightsParameter.setSceneType("all");
        dataRightsParameter.setCascadeDept(true);
        dataRightsParameter.setCascadeSubordinates(true);
        query.setDataRightsParameter(dataRightsParameter);

        query.setPermissionType(1);
        query.setOrders(Lists.newArrayList(new OrderBy("last_modified_time", false)));

        // TODO: 2018/4/9 这个需要设计一下,避免工单对象下的ref字段增加一个对象,就需要修改一次代码
        List<IFilter> filters = Lists.newArrayListWithCapacity(1);
        SearchUtil.fillFilterEq(filters, CasesConstants.REF_OBJECT_API_NAME_2_DB_KEY_WORD.get(arg.getApiName()), arg.getObjectId());
        query.setFilters(filters);

        QueryResult<IObjectData> result = metaDataFindService.findBySearchQuery(context.getUser(), Utils.CASES_API_NAME, query);
        return new HistoryCasesModel.Result(ObjectDataDocument.ofList(result.getData()), result.getTotalNumber());
    }


}
