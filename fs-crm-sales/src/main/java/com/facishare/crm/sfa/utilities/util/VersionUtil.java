package com.facishare.crm.sfa.utilities.util;

import com.facishare.paas.appframework.common.util.Tuple;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import static com.facishare.paas.appframework.core.model.RequestContext.Android_CLIENT_INFO_PREFIX;
import static com.facishare.paas.appframework.core.model.RequestContext.IOS_CLIENT_INFO_PREFIX;

@Slf4j
public class VersionUtil {

    private static Tuple<String,Integer> getClientTypeAndVersion(RequestContext context){
        String[] clientTypeAndVersion = !Strings.isNullOrEmpty(context.getClientInfo())
                ? context.getClientInfo().split("\\.")
                : new String[] {"unknown", "0"};

        return  Tuple.of(clientTypeAndVersion[0],
                clientTypeAndVersion.length > 1 ? Integer.valueOf(clientTypeAndVersion[1]) : 0);
    }

    public static Boolean isVersionEarlierEqualThan610(RequestContext context){
        Tuple<String,Integer> clientInfo = getClientTypeAndVersion(context);
        String clientType = clientInfo.getKey();
        Integer clientVersion = clientInfo.getValue();

        log.debug("isVersionEarlierEqualThan610->clientType:{},clientVersion:{}",clientType,clientVersion);
        if ( clientVersion > 100000000 ) {
            clientVersion = clientVersion - 100000000;
        }

        switch (clientType)
        {
            case Android_CLIENT_INFO_PREFIX:
                return clientVersion < ClientVersion.Android610.getValue();
            case IOS_CLIENT_INFO_PREFIX:
                return clientVersion < ClientVersion.IOS610.getValue();
            default:
                return false;
        }
    }

    public static Boolean isVersionEarlierEqualThan620(RequestContext context){
        Tuple<String,Integer> clientInfo = getClientTypeAndVersion(context);
        String clientType = clientInfo.getKey();
        Integer clientVersion = clientInfo.getValue();

        log.debug("isVersionEarlierEqualThan620->clientType:{},clientVersion:{}",clientType,clientVersion);
        if ( clientVersion > 100000000 ) {
            clientVersion = clientVersion - 100000000;
        }

        switch (clientType)
        {
            case Android_CLIENT_INFO_PREFIX:
                return clientVersion < ClientVersion.Android620.getValue();
            case IOS_CLIENT_INFO_PREFIX:
                return clientVersion < ClientVersion.IOS620.getValue();
            default:
                return false;
        }
    }

    public enum ClientVersion{

        Android610(610000),
        IOS610(610000),
        Android620(620000),
        IOS620(620000);

        private Integer value;
        ClientVersion(Integer value){
            this.value = value;
        }

        public Integer getValue(){
            return this.value;
        }
    }
}


