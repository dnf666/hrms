package com.facishare.crm.sfa.predefine.service;

import com.facishare.crm.sfa.predefine.service.model.QueryDuplicateSearch;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;

/**
 * Created by rensx on 2017/7/31.
 */
@ServiceModule("duplicate_search")
public interface DuplicateSearchService {

    @ServiceMethod("query")
    QueryDuplicateSearch.Result query(QueryDuplicateSearch.Arg arg);
}
