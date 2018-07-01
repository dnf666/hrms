package com.facishare.crm.sfa.predefine.service;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by luxin on 2018/5/2.
 */
@Component
public class PredefinedObjSearchServiceManger implements ApplicationContextAware {

    private Map<String, PredefinedObjSearchService> apiName2PredefinedObjSearchServiceMap = Maps.newHashMap();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, PredefinedObjSearchService> springBeanMap = applicationContext.getBeansOfType(PredefinedObjSearchService.class);
        springBeanMap.values().forEach(searchService -> {
            if (StringUtils.isNotEmpty(searchService.getApiName())) {
                apiName2PredefinedObjSearchServiceMap.put(searchService.getApiName(), searchService);
            }
        });
    }


    public PredefinedObjSearchService getSearchService(String apiName) {
        PredefinedObjSearchService tmp = apiName2PredefinedObjSearchServiceMap.get(apiName);
        if (tmp != null) {
            return tmp;
        } else {
            throw new NoSuchBeanDefinitionException(apiName);
        }
    }

}
