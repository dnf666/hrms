package com.facishare.crm.checkins;

import com.facishare.appserver.utils.DateUtils;
import com.facishare.paas.appframework.common.util.DocumentBaseEntity;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ObjectDescribeDocument;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.elasticsearch.common.Strings;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by zhangsm on 2018/4/11/0011.
 */
@Slf4j
public class CheckinsUtils {



    public static LayoutDocument clearLayoutDocument(LayoutDocument layoutDocument){
        if (Objects.nonNull(layoutDocument)) {
            ILayout layout = layoutDocument.toLayout();
            layout.setButtons(Lists.newArrayList());
            List<IComponent> iComponentList = null;
            try {
                iComponentList = layout.getComponents();
                iComponentList.stream().forEach(o -> {
                            if (!"detailInfo".equals(o.getName())) {
                                o.set("child_components",Lists.newArrayList());
                            }
                        }

                );
                layout.setComponents(iComponentList);
            } catch (Exception e) {
                log.error("", e);
            }
           return LayoutDocument.of(layout);
        }
        return null;
    }
    public static ObjectDescribeDocument clearObjectDescribeDocument(ObjectDescribeDocument objectDescribeDocument){
        if (Objects.nonNull(objectDescribeDocument)){
            IObjectDescribe iObjectDescribe = objectDescribeDocument.toObjectDescribe();
            iObjectDescribe.set("actions", Lists.newArrayList());
            return ObjectDescribeDocument.of(iObjectDescribe);
        }
        return null;
    }
    public static List<Map<String, Object>> clearRefObjects(List<Map<String, Object>> refObjects){
        if (CollectionUtils.isNotEmpty(refObjects)){
            refObjects.stream().forEach(o->{
                if (null != o.get("describe")){
                    ObjectDescribeDocument tempObjDoc = new ObjectDescribeDocument(o);
                    IObjectDescribe tempIObj = tempObjDoc.toObjectDescribe();
                    tempIObj.set("actions",Lists.newArrayList());
                    o.put("describe", ObjectDescribeExt.of(tempIObj).toMap());
                }
                if (null != o.get("layout")){
                    LayoutDocument layoutDocument1 = new LayoutDocument(o);
                    ILayout layout = layoutDocument1.toLayout();
                    layout.setButtons(Lists.newArrayList());
                    List<IComponent> iComponentList = null;
                    try {
                        iComponentList = layout.getComponents();
                        iComponentList.stream().forEach(e -> {
                                    if (!"detailInfo".equals(e.getName())) {
                                        e.set("child_components",Lists.newArrayList());
                                    }
                                }

                        );
                        layout.setComponents(iComponentList);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                    o.put("layout", LayoutExt.of(layout).toMap());
                }
            });
        }
        return refObjects;
    }

    public static List<DocumentBaseEntity> hideField4Checkins(List<DocumentBaseEntity> fieldList) {
        List<String> hideKeyList = Lists.newArrayList("action_id","created_by","life_status","lock_status","out_owner","status",
                "select_ids","rule_id","relation_obj_id","relation_obj_api_name","name","feed_id","signer","enterprise_account",
                "client_version","check_id","check_type_id","check_action_list","unique_action_id","last_modified_by","checkin_id","address_city"
                ,"address_country","address_country","address_street","address_street","circle_ids","employee_ids","crm_child_object","address_street_num"
        ,"address_province");
        if (CollectionUtils.isNotEmpty(fieldList)) {
           fieldList = fieldList.stream().filter(o->{
                if (hideKeyList.containsAll(o.keySet())){
                    return false;
                }
                return true;
           }).collect(Collectors.toList());
           return fieldList;
        }
        return null;
    }

    /**
     * 用来格式化图片对象列表页 高级外勤对像的name
     * @param dataDocumentList
     * @return
     */
    public static List<ObjectDataDocument> formatDataListName(List<ObjectDataDocument> dataDocumentList) {
        String CheckinsObjName = "object_checkins_id__r";
        if (CollectionUtils.isNotEmpty(dataDocumentList)){
            for (ObjectDataDocument objectDataDocument : dataDocumentList) {
                if (objectDataDocument.keySet().contains(CheckinsObjName)){
                    String checkinId = (String) objectDataDocument.get("checkin_id");
                    if (Strings.isNullOrEmpty(checkinId)){
                        objectDataDocument.put(CheckinsObjName,"高级外勤对象");
                    }else {
                        objectDataDocument.put(CheckinsObjName,"高级外勤对象--" + DateUtils.getStringFromDate(new ObjectId(checkinId).getDate(),DateUtils.DateFormat));
                    }
                }else {
                    return dataDocumentList;
                }
            }
        }
        return dataDocumentList;
    }

    public static ObjectDataDocument formatDetailName(ObjectDataDocument dataDocument) {
        String CheckinsObjKey = "check_type_id";
        String CheckinsImgObjKey = "action_id";
        if (Objects.nonNull(dataDocument)){
            if (dataDocument.keySet().contains(CheckinsObjKey)){
                //高级外勤详情
                String checkinId = (String)  dataDocument.get("check_id");
                if (Strings.isNullOrEmpty(checkinId)){
                    dataDocument.put("name","高级外勤对象");
                }else {
                    dataDocument.put("name","高级外勤对象--" + DateUtils.getStringFromDate(new ObjectId(checkinId).getDate(),DateUtils.DateFormat));
                }
            }else if(dataDocument.keySet().contains(CheckinsImgObjKey)){
                //高级外勤图片详情
                String checkinId = (String) dataDocument.get("checkin_id");
                String actionId = (String) dataDocument.get("action_id");
                if (Strings.isNullOrEmpty(checkinId)){
                    dataDocument.put("object_checkins_id__r","高级外勤对象");
                }else {
                    dataDocument.put("object_checkins_id__r","高级外勤对象--" + DateUtils.getStringFromDate(new ObjectId(checkinId).getDate(),DateUtils.DateFormat));
                }
                String actionName = dataDocument.get("action_name") == null? "外勤图片":(String) dataDocument.get("action_name");
                if (Strings.isNullOrEmpty(actionId)){
                    dataDocument.put("name",actionName);
                }else {
                    dataDocument.put("name",actionName+"--" + DateUtils.getStringFromDate(new ObjectId(actionId).getDate(),DateUtils.DateFormat));
                }
            }
            return dataDocument;
        }



        return dataDocument;
    }
}
