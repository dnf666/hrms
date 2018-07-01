package com.facishare.crm.sfa.predefine.service;

import com.alibaba.fastjson.JSON;
import com.facishare.crm.sfa.utilities.common.convert.ConvertorFactory;
import com.facishare.crm.sfa.predefine.SFAPreDefineObject;
import com.facishare.crm.sfa.predefine.service.model.QueryDuplicateSearch;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rensx on 2017/7/31.
 */
@Component
public class DuplicateSearchServiceImpl implements DuplicateSearchService {
    @Override
    public QueryDuplicateSearch.Result query(QueryDuplicateSearch.Arg arg) {

        Object object = ConvertorFactory.convertToOldFieldNames(arg.getMasterApiName(), arg.getData());

        String dataJson = ConvertorFactory.specialFieldConvert(arg.getMasterApiName(), JSON.toJSONString(object));

        Map<String, Object> objectData = new HashMap<>();
        //objectData.put()
        return null;
    }
}
