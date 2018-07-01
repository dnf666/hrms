package com.facishare.crm.checkins.action;

import com.facishare.paas.appframework.core.predef.action.StandardAddAction;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by zhangsm on 2018/4/9/0009.
 */
@Slf4j
public class CheckinsSysAddAction extends StandardAddAction {
//add 增加缓存 防止重复插入
//    @Resource
//    private  MergeJedisCmd checkinsRedis;
//
//    public  final String rediskey = "fs-appserver-checkins_";
//
//    public  void setDataIdRedis(String checkinsId,String dataId) {
//        try{
//            checkinsRedis.setex(rediskey + checkinsId,86400000,dataId);//缓存一天
//        }catch (Exception e){
//            log.error("setDataIdRedis error checkinsId {} ,dataId {}",checkinsId,dataId,e);
//        }
//    }
//    public  String getDataIdByRedis(String checkinsId) {
//        try{
//            return checkinsRedis.get(rediskey + checkinsId);//缓存一天
//        }catch (Exception e){
//            log.error("getDataIdByRedis error checkinsId {} ",checkinsId,e);
//        }
//        return null;
//    }
//    @Override
//    protected void before(Arg arg) {
//        super.before(arg);
//        ObjectDataDocument objectDataDocument = arg.getObjectData();
//        if (!Objects.isNull(objectDataDocument)){
//            IObjectData iObjectData = objectDataDocument.toObjectData();
//            if (!Objects.isNull(iObjectData.get("check_id"))){
//                String checkinsId = iObjectData.get("check_id").toString();
//                String dataId = getDataIdByRedis(checkinsId);
//                if (!Strings.isNullOrEmpty(dataId)){
//                    throw new CheckinsException(dataId, CheckinsErrorCode.REPEAT_INSERT);
//                }
//            }
//        }
//    }
//
//    @Override
//    protected Result after(Arg arg, Result result) {
//        result = super.after(arg, result);
//        ObjectDataDocument objectDataDocument = result.getObjectData();
//        if (!Objects.isNull(objectDataDocument)){
//            IObjectData iObjectData = objectDataDocument.toObjectData();
//            if (!Objects.isNull(iObjectData.get("check_id")) && !Objects.isNull(iObjectData.getId())){
//                String checkinsId = iObjectData.get("check_id").toString();
//                String dataId = iObjectData.getId().toString();
//                setDataIdRedis(checkinsId,dataId);
//            }
//        }
//        return result;
//    }
}
