package com.facishare.crm.sfa.utilities.common.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.sfa.utilities.util.JsonUtil;
import com.github.autoconf.ConfigFactory;
import com.github.autoconf.api.IConfig;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by lilei on 2017/7/28.
 */
@Slf4j
public class ConvertorFactory {

    /***
     * 预置对象的apiName下的字段对应的老对象字段的名称影射，例如：
     * {
     * "AccountObj":[{"name":"Name"},{"account_status":"status"}]
     * }
     */
    private static HashMap<String, HashMap> FIELD_NAMES_MAPPING_N2O = Maps.newHashMap();
    private static HashMap<String, HashMap> FIELD_NAMES_MAPPING_O2N = Maps.newHashMap();
    private static HashMap<String, String> APINAMES_MAPPING_N2O = Maps.newHashMap();
    private static HashMap<String, String> APINAMES_MAPPING_O2N = Maps.newHashMap();

    private static List<String> NEW_API_NAMES = new ArrayList();

    /***
     * @param configValue format like: "Name:name,Category:category,Price:price,OpportunityID:opportunity_id,OpportunityName:opportunity_id__r,Unit:unit,ProductLine:product_line,Specs:product_spec,Status:product_status,Barcode:barcode,ProductCode:product_code,ProductGroupID:product_group_id,Remark:remark,CreatorID:created_by,CreateTime:create_time,ShelfTime:on_shelves_time,SoldOutTime:off_shelves_time,UpdateTime:last_modified_time,UpdatorID:last_modified_by,IsDeleted:is_deleted,Department:owner_department"
     * @return
     */
    private static HashMap<String, String> keyValuePairsToMap(String configValue) {
        HashMap<String, String> result = Maps.newHashMap();
        String[] pairArray = configValue.split(",");
        for (String pair : pairArray) {
            String[] keyValue = pair.split(":");
            if (keyValue.length < 2 || keyValue.length > 2) {
                continue;
            }

            if (result.containsKey(keyValue[0])) {
                continue;
            }

            result.put(keyValue[0], keyValue[1]);
        }
        return result;
    }

    static {

        //出于性能考虑，两层for循环没有拆成子方法，目的是在一次for循环中生成两组影射数据O2N & N2O
        ConfigFactory.getInstance().getConfig("fs-crm-java-apiName-o2n", ConvertorFactory::initFieldsMapping);

        ConfigFactory.getInstance().getConfig("fs-crm-default-obj-config", ConvertorFactory::initObjectApiNamesMapping);
    }

    private static void initFieldsMapping(IConfig config) {
        String jsonConvert_describesApiNames = config.get("jsonConvert_describesApiNames").trim();
        String[] describesApiNames = jsonConvert_describesApiNames.split(",");
        for (String describeApiName : describesApiNames) {
            NEW_API_NAMES.add(describeApiName);
            String jsonValue = config.get(describeApiName).trim();

            HashMap<String, String> resultN2O = Maps.newHashMap();
            HashMap<String, String> resultO2N = Maps.newHashMap();

            String[] pairArray = jsonValue.split(",");
            for (String pair : pairArray) {
                String[] keyValue = pair.split(":");
                if (keyValue.length < 2 || keyValue.length > 2) {
                    continue;
                }

                if (!resultO2N.containsKey(keyValue[0])) {
                    resultO2N.put(keyValue[0], keyValue[1]);
                }

                if (!resultN2O.containsKey(keyValue[1])) {
                    resultN2O.put(keyValue[1], keyValue[0]);
                }
            }

            FIELD_NAMES_MAPPING_N2O.put(describeApiName, resultN2O);
            FIELD_NAMES_MAPPING_O2N.put(describeApiName, resultO2N);
        }
    }

    private static void initObjectApiNamesMapping(IConfig config) {
        String json = config.get("NewApiNameToSFAOldApiNameMapping").trim();
        JSONObject object = JSONObject.parseObject(json);
        for (String oldApiName : object.keySet()) {
            APINAMES_MAPPING_N2O.put(oldApiName, object.getString(oldApiName));
            APINAMES_MAPPING_N2O.put(object.getString(oldApiName), oldApiName);
        }
    }

    public static String specialFieldConvert(String apiName, String dataJson) {

        SpecialFieldConvertor convertor;

        switch (apiName) {
            case "AccountObj":
                convertor = new AccountSpecialFieldConvertorImpl();
                break;
            case "SalesOrderObj":
                convertor = new SalesOrderSpecialFieldConvertorImpl();
                break;
            default:
                convertor = new DefaultSpecialFieldConvertorImpl();
                break;
        }

        return convertor.specialFieldConvert(dataJson);
    }

    /****
     * 将jsonData中的新字段名称转化为.net中对应的旧字段名
     *
     * @param objectApiName 对象类型apiName，如：AccountObj, SalesOrderObj etc.
     * @param jsonData      {"name":"","account_status":0}
     * @return {"CustomerName":"","Status":0 }
     */
    public static String convertToNewFieldNamesString(String objectApiName, String jsonData) {
        return JsonUtil.toJsonWithNullValues(convertToFieldNames(JSON.parse(jsonData), true, objectApiName));
    }

    public static String convertToOldFieldNamesString(String objectApiName, String jsonData) {
        return JsonUtil.toJsonWithNullValues(convertToFieldNames(JSON.parse(jsonData), false, objectApiName));
    }

    public static String convertToOldFieldNamesStringForAddSpec(String objectApiName, String jsonData) {
        return JsonUtil.toJsonWithNullValues(convertToFieldNamesForAddSpec(JSON.parse(jsonData), false, objectApiName));
    }

    public static Object convertToNewFieldNames(String objectApiName, Object jsonData) {
        return convertToFieldNames(jsonData, true, objectApiName);
    }

    public static Object convertToOldFieldNames(String objectApiName, Object jsonData) {
        return convertToFieldNames(jsonData, false, objectApiName);
    }

    public static Object convertToFieldNames(Object root, boolean isOld2New, String objectApiName) {

        HashMap<String, String> maps;
        if (isOld2New) {
            maps = getFieldNamesOld2New(objectApiName);
        } else {
            maps = getFieldNamesNew2Old(objectApiName);
        }

        // is array: []
        if (root instanceof JSONObject) {
            JSONObject resultObject = new JSONObject();
            JSONObject rootObject = (JSONObject) root;
            for (String key : rootObject.keySet()) {
                Object valueObject = rootObject.get(key);

                if (NEW_API_NAMES.contains(key) && valueObject instanceof JSONObject) {
                    //嵌套了子对象
                    String oldApiName = getOldApiName(key);
                    resultObject.put(oldApiName, convertToFieldNames(valueObject, isOld2New, key));
                    continue;
                }

                if (NEW_API_NAMES.contains(key) && valueObject instanceof JSONArray) {
                    //嵌套了子对象
                    String oldApiName = getOldApiName(key);
                    JSONArray subArray = new JSONArray();
                    JSONArray array = (JSONArray) valueObject;
                    for (Object anArray : array) {
                        subArray.add(convertToFieldNames(anArray, isOld2New, key));
                    }
                    resultObject.put(oldApiName, subArray);
                    continue;
                }

                if (valueObject == null) {
                    if (Objects.equals(key, "SalesOrderProductObj")) {
                        valueObject = new ArrayList();
                    } else {
                        valueObject = "";
                    }

                }
                if (maps.containsKey(key)) {
                    resultObject.put(maps.get(key), valueObject);
                } else {

                    if (key.lastIndexOf("__c") > 0) {
                        resultObject.put(key.replace("__c", ""), valueObject);
                    } else {
                        resultObject.put(key, valueObject);
                    }

                }
            }

            return resultObject;
        }

        // is object: {}
        if (root instanceof JSONArray) {
            JSONArray resultArray = new JSONArray();
            JSONArray array = (JSONArray) root;
            for (Object anArray : array) {
                resultArray.add(convertToFieldNames(anArray, isOld2New, objectApiName));
            }
            return resultArray;
        }
        return root;
    }


    //新增规格字段转换
    public static Object convertToFieldNamesForAddSpec(Object root, boolean isOld2New, String objectApiName) {
        HashMap<String, String> maps;
        if (isOld2New) {
            maps = getFieldNamesOld2New(objectApiName);
        } else {
            maps = getFieldNamesNew2Old(objectApiName);
        }

        // is array: []
        if (root instanceof JSONObject) {
            JSONObject resultObject = new JSONObject();
            JSONObject rootObject = (JSONObject) root;
            for (String key : rootObject.keySet()) {
                Object valueObject = rootObject.get(key);

                if ((NEW_API_NAMES.contains(key) || "SpecProductInfoList".equals(key)) && valueObject instanceof JSONObject) {
                    //嵌套了子对象
                    String oldApiName = getOldApiName(key);
                    resultObject.put(oldApiName, convertToFieldNames(valueObject, isOld2New, key));
                    continue;
                }

                if ((NEW_API_NAMES.contains(key) || "SpecProductInfoList".equals(key)) && valueObject instanceof JSONArray) {
                    //嵌套了子对象


                    String oldApiName = getOldApiName(key);
                    if ("SpecProductInfoList".equals(key)) {
                        key = "ProductObj";
                        JSONArray subArray = new JSONArray();
                        JSONArray array = (JSONArray) valueObject;
                        for (Object anArray : array) {
                            subArray.add(convertToFieldNamesForAddSpec(anArray, isOld2New, key));
                        }
                        resultObject.put("SpecProductInfoList", subArray);
                    } else {
                        JSONArray subArray = new JSONArray();
                        JSONArray array = (JSONArray) valueObject;
                        for (Object anArray : array) {
                            subArray.add(convertToFieldNamesForAddSpec(anArray, isOld2New, key));
                        }
                        resultObject.put(oldApiName, subArray);
                    }
                    continue;
                }

                if (valueObject == null) {
                    if (Objects.equals(key, "SalesOrderProductObj")) {
                        valueObject = new ArrayList();
                    } else {
                        valueObject = "";
                    }

                }
                if (maps.containsKey(key)) {
                    resultObject.put(maps.get(key), valueObject);
                } else {

                    if (key.lastIndexOf("__c") > 0) {
                        resultObject.put(key.replace("__c", ""), valueObject);
                    } else {
                        resultObject.put(key, valueObject);
                    }

                }
            }

            return resultObject;
        }

        // is object: {}
        if (root instanceof JSONArray) {
            JSONArray resultArray = new JSONArray();
            JSONArray array = (JSONArray) root;
            for (Object anArray : array) {
                resultArray.add(convertToFieldNames(anArray, isOld2New, objectApiName));
            }
            return resultArray;
        }
        return root;
    }


    //根据元数据对象apiName获取.net的对象名称
    public static String getOldApiName(String newApiName) {
        return APINAMES_MAPPING_N2O.get(newApiName);
    }

    //根据.net的对象名称获取元数据对象apiName
    public static String getNewApiName(String oldApiName) {
        return APINAMES_MAPPING_O2N.get(oldApiName);
    }

    public static HashMap<String, String> getFieldNamesNew2Old(String objectApiName) {

        if (FIELD_NAMES_MAPPING_N2O.containsKey(objectApiName)) {
            return FIELD_NAMES_MAPPING_N2O.get(objectApiName);
        }

        log.error("Cannot find fs-crm-java-apiName-o2n for {}", objectApiName);
        return Maps.newHashMap();
    }

    public static HashMap<String, String> getFieldNamesOld2New(String objectApiName) {

        if (FIELD_NAMES_MAPPING_O2N.containsKey(objectApiName)) {
            return FIELD_NAMES_MAPPING_O2N.get(objectApiName);
        }

        log.error("Cannot find fs-crm-java-apiName-o2n for {}", objectApiName);
        return Maps.newHashMap();
    }

}
