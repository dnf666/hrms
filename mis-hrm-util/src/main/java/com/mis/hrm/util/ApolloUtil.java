package com.mis.hrm.util;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;

/**
 * created by dailf on 2019-01-11
 *
 * @author dailf
 */
public class ApolloUtil {
    //namespace跟项目有关。请参考自己的配置
    private static Config config = ConfigService.getConfig("application");

    public static String getString(String key) {
        config.addChangeListener(new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                for (String key : changeEvent.changedKeys()) {
                    ConfigChange change = changeEvent.getChange(key);
                    System.out.println(String.format("Found change - key: %s, oldValue: %s, newValue: %s, changeType: %s", change.getPropertyName(), change.getOldValue(), change.getNewValue(), change.getChangeType()));
                }
            }
        });
        String someKey = key;
        String someDefaultValue = null;
        String value = config.getProperty(someKey, someDefaultValue);
        System.out.println(value);
        return value;
    }
}
