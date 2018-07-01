package com.facishare.crm.sfa.utilities.common.convert;

import com.facishare.paas.metadata.support.CountryAreaService;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.privilege.util.EmployeeUtil;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IMultiLevelSelectOption;
import com.facishare.paas.metadata.api.IRecordTypeOption;
import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.impl.describe.BooleanFieldDescribe;
import com.facishare.paas.metadata.impl.describe.CityFiledDescribe;
import com.facishare.paas.metadata.impl.describe.CountryFieldDescribe;
import com.facishare.paas.metadata.impl.describe.DateFieldDescribe;
import com.facishare.paas.metadata.impl.describe.DateTimeFieldDescribe;
import com.facishare.paas.metadata.impl.describe.DepartmentFieldDescribe;
import com.facishare.paas.metadata.impl.describe.DistrictFieldDescribe;
import com.facishare.paas.metadata.impl.describe.EmployeeFieldDescribe;
import com.facishare.paas.metadata.impl.describe.LocationFieldDescribe;
import com.facishare.paas.metadata.impl.describe.MultiLevelSelectOneFieldDescribe;
import com.facishare.paas.metadata.impl.describe.PercentileFieldDescribe;
import com.facishare.paas.metadata.impl.describe.ProvinceFieldDescribe;
import com.facishare.paas.metadata.impl.describe.RecordTypeFieldDescribe;
import com.facishare.paas.metadata.impl.describe.SelectManyFieldDescribe;
import com.facishare.paas.metadata.impl.describe.SelectOneFieldDescribe;
import com.facishare.paas.metadata.impl.describe.TimeFieldDescribe;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * Created with IntelliJ IDEA. User: quzhf Date: 2017/11/21 14:06 Description:通用对象字段处理转换类
 */
@Slf4j
@Service
public class ObjectDataFieldConvert {
    @Autowired
    private CountryAreaService countryAreaService;
    private static ObjectDataFieldConvert objectDataFieldConvert;
    private Set<String> userId2UserNameFields = Sets.newHashSet("owner,assigner_id,finance_employee_id,last_follower,last_modified_by,created_by".split(","));

    public static ObjectDataFieldConvert me() {
        if (objectDataFieldConvert == null) {
            objectDataFieldConvert = (ObjectDataFieldConvert) SpringUtil.getContext().getBean("objectDataFieldConvert");
        }
        return objectDataFieldConvert;
    }


    public Object transformDataField(User user, Object fieldValue, IFieldDescribe fieldDescribe) {
        String tenantId = user.getTenantId();
        String fieldApiName = fieldDescribe.getApiName();
        if (userId2UserNameFields.contains(fieldApiName)) {
            return transformUserId(user, fieldValue);
        }
        //日期字段处理
        if (fieldDescribe instanceof DateTimeFieldDescribe) {
            DateTimeFieldDescribe dateTimeFieldDescribe = (DateTimeFieldDescribe) fieldDescribe;
            return formatDateDataForProductObjInner(fieldValue, dateTimeFieldDescribe.getDateFormat());
        } else if (fieldDescribe instanceof DateFieldDescribe) {
            DateFieldDescribe dateFieldDescribe = (DateFieldDescribe) fieldDescribe;
            return formatDateDataForProductObjInner(fieldValue, dateFieldDescribe.getDateFormat());
        } else if (fieldDescribe instanceof TimeFieldDescribe) {
            TimeFieldDescribe timeFieldDescribe = (TimeFieldDescribe) fieldDescribe;
            return formatDateDataForProductObjInner(fieldValue, timeFieldDescribe.getDateFormat());
        } else if (fieldDescribe instanceof SelectManyFieldDescribe) {
            SelectManyFieldDescribe selectManyFieldDescribe = (SelectManyFieldDescribe) fieldDescribe;
            List<ISelectOption> optionList = selectManyFieldDescribe.getSelectOptions();
            if (CollectionUtils.isEmpty(optionList)) {
                return null;
            }
            Map optionMap = new HashMap<>();
            for (ISelectOption k : optionList) {
                optionMap.put(k.getValue(), k.getLabel());
            }

            List fieldValueList = (List) fieldValue;
            List labelList = new LinkedList();
            for (Object k : fieldValueList) {
                if (k != null && StringUtils.isNotBlank(k.toString())) {
                    if (optionMap.containsKey(k.toString())) {
                        labelList.add(optionMap.get(k.toString()));
                    }
                }
            }
            return CollectionUtils.isNotEmpty(labelList) ? labelList : null;
        } else if (fieldDescribe instanceof SelectOneFieldDescribe) {
            SelectOneFieldDescribe selectOneFieldDescribe = null;

            // TODO: 2017/6/15 fromJsonString 在转换超长数字会变成科学技术法
            if (fieldDescribe instanceof CountryFieldDescribe) {
                CountryFieldDescribe countryFieldDescribe = new CountryFieldDescribe();
                countryFieldDescribe.fromJsonString(getCountryDescribeObj("country").toJSONString());
                selectOneFieldDescribe = countryFieldDescribe;
            } else if (fieldDescribe instanceof ProvinceFieldDescribe) {
                ProvinceFieldDescribe provinceFieldDescribe = new ProvinceFieldDescribe();
                provinceFieldDescribe.fromJsonString(getCountryDescribeObj("province").toJSONString());
                selectOneFieldDescribe = provinceFieldDescribe;
            } else if (fieldDescribe instanceof CityFiledDescribe) {
                ProvinceFieldDescribe cityFiledDescribe = new ProvinceFieldDescribe();
                cityFiledDescribe.fromJsonString(getCountryDescribeObj("city").toJSONString());
                selectOneFieldDescribe = cityFiledDescribe;
            } else if (fieldDescribe instanceof DistrictFieldDescribe) {
                ProvinceFieldDescribe districtFieldDescribe = new ProvinceFieldDescribe();
                districtFieldDescribe.fromJsonString(getCountryDescribeObj("district").toJSONString());
                selectOneFieldDescribe = districtFieldDescribe;
            } else {
                selectOneFieldDescribe = (SelectOneFieldDescribe) fieldDescribe;
            }

            if (selectOneFieldDescribe == null) {
                return null;
            }
            List<ISelectOption> selectOptions = selectOneFieldDescribe.getSelectOptions();
            String valStr = String.valueOf(fieldValue);
            //判断是否有其他选项
            /*Object ortherlabel = "";
            if ("other".equals(valStr)) {
                ortherlabel = data.get(fieldDescribe.getApiName() + "__o");
                ortherlabel = (!Objects.isNull(ortherlabel) && StringUtils.isNotBlank(ortherlabel.toString()) ? ":" + ortherlabel.toString() : "");
            }*/

            for (ISelectOption option : selectOptions) {
                if (StringUtils.equals(option.getValue(), valStr)) {
                    return option.getLabel();//+ ortherlabel
                }
            }
        } else if (fieldDescribe instanceof MultiLevelSelectOneFieldDescribe) {
            return formatMultiLeveSelectOneFieldValue(fieldValue, fieldDescribe);
        } else if (fieldDescribe instanceof RecordTypeFieldDescribe) {
            //一般指业务类型字段处理
            RecordTypeFieldDescribe recordTypeFieldDescribe = (RecordTypeFieldDescribe) fieldDescribe;
            IRecordTypeOption typeOption = recordTypeFieldDescribe.getRecordTypeOption(fieldValue.toString());
            return typeOption != null ? typeOption.getLabel() : null;
        } else if (fieldDescribe instanceof BooleanFieldDescribe) {
            BooleanFieldDescribe booleanFieldDescribe = (BooleanFieldDescribe) fieldDescribe;
            List<Map> optionsList = booleanFieldDescribe.get("options", List.class);
            if (optionsList != null) {
                for (Map option : optionsList) {
                    if (fieldValue.equals(String.valueOf(option.get("value")))) {
                        return option.get("label");
                    }
                }
            }
            return null;
        } else if (fieldDescribe instanceof EmployeeFieldDescribe && fieldValue instanceof List) {
            //团队成员
            List valueList = (List) fieldValue;
            if (valueList.size() <= 0) {
                return "";
            }
            StringBuilder sb = new StringBuilder("");
            valueList.forEach(value -> {
                sb.append(EmployeeUtil.getUserName(value.toString(), tenantId));
                sb.append(",");
            });
            return sb.substring(0, sb.length() - 1);
        } else if (fieldDescribe instanceof DepartmentFieldDescribe && fieldValue instanceof List) {
            //团队成员
            if (((List) fieldValue).size() <= 0) {
                return "";
            } else {
                return EmployeeUtil.getDepartmentNameByDepart(((List) fieldValue).get(0).toString(), tenantId);
            }
        } else if (fieldDescribe instanceof LocationFieldDescribe) {
            return transformLocation(fieldValue.toString());
        } else if (fieldDescribe instanceof PercentileFieldDescribe) {
            return StringUtils.isNotBlank(fieldValue.toString()) ? String.valueOf(fieldValue) + "%" : null;
        }
        return fieldValue;
    }

    /**
     * 特殊字段处理：处理用户id
     */
    public Object transformUserId(User user, Object fieldValue) {
        StringBuilder sb = new StringBuilder("");
        List<String> fieldValueList = null;
        if (fieldValue instanceof String && StringUtils.isNotBlank(fieldValue.toString())) {
            fieldValueList = Lists.newArrayList(String.valueOf(fieldValue));
        } else if (fieldValue instanceof List) {
            fieldValueList = (List<String>) fieldValue;
        } else {
            return null;
        }
        for (Object userId : fieldValueList) {
            String userName = EmployeeUtil.getUserName(userId.toString(), user.getTenantId());
            sb.append(userName);
            sb.append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }

    private JSONObject getCountryDescribeObj(String countryType) {
        String countryAreaJson = countryAreaService.getCountryCascadeJsonStringIncludeDeleted();
        return JSON.parseObject(countryAreaJson).getJSONObject(countryType);
    }

    private String formatDateDataForProductObjInner(Object dataObj, String dateFormat) {
        Long time = 0L;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        if (dataObj instanceof Number) {
            time = ((Number) dataObj).longValue();
            ;
        } else if (dataObj instanceof String) {
            try {
                if ("0000-00-00".equals(dataObj)) {
                    return "--";
                } else {
                    time = Long.parseLong((String) dataObj);
                }
            } catch (NumberFormatException nfe) {
                return dataObj.toString();
            }
        } else if (dataObj instanceof Date) {
            return sdf.format((Date) dataObj);
        }
        if (time == null || time == 946656000000L || time == 0L) {
            return null;
        }
        return sdf.format(new Date(time));
    }

    private String formatMultiLeveSelectOneFieldValue(Object fieldValue, IFieldDescribe field) {
        if (fieldValue == null) {
            return "";
        }
        MultiLevelSelectOneFieldDescribe mutilSelectDescribe = (MultiLevelSelectOneFieldDescribe) field;
        for (IMultiLevelSelectOption parentSelection : mutilSelectDescribe.getSelectOptions()) {
            for (IMultiLevelSelectOption childOptions : parentSelection.getChildOptions()) {
                if (childOptions.getValue().equals(fieldValue)) {
                    String childLabel = childOptions.getLabel();
                    String parentLabel = parentSelection.getLabel();
                    return parentLabel + "/" + childLabel;
                }
            }
        }
        return "";
    }

    private String transformLocation(String location) {
        if (!Strings.isNullOrEmpty(location)) {
            String[] strArrary = location.split("\\#\\%\\$");
            if (strArrary.length > 2) {
                return strArrary[2];
            }
        }
        return location;
    }
}