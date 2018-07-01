package com.facishare.crm.electronicsign.exception;

import com.facishare.paas.appframework.core.exception.ErrorCode;

public enum ElecSignErrorCode implements ErrorCode {
    SYSTEM_ERROR(1000, "系统异常"),
    BUSINESS_ERROR(1001, "业务异常"),
    PARAM_ERROR(1002, "参数错误"),
    METADATA_QUERY_ERROR(1003, "数据查询异常"),
    ACCOUNT_NO_CERTIFIED(1004, "该客户不是'已认证'的客户"),
    GET_OBJ_TEMPLATE_PATH_FAILED(1005, "获取业务对象打印模板路径失败"),
    DOWN_OBJ_TEMPLATE_FAILED(1006, "下载业务对象打印模板失败"),
    UPLOAD_PDF_TO_BEST_SIGN_FAIL(1007, "上传PDF到上上签失败"),
    CREATE_CONTRACT_FAIL(1008, "创建合同失败"),
    UPDATE_TEMPLATE_CREATE_CONTRACT_FAIL(1008, "上传PDF创建合同失败"),
    GET_KEYWORD_POSITION_FAIL(1009, "根据关键字获取签字位置失败"),
    NO_FIND_KEYWORD(1011, "找不到关键字"),
    SEND_CONTRACT_FAIL(1012, "发送合同，获取签署URL失败"),
    TENANT_ELEC_SIGN_OFF(1013, "租户电子签章开关未开启"),
    APP_ELEC_SIGN_OFF(1014, "应用电子签章开关未开启"),
    INDIVIDUAL_QUOTA_NO_ENOUGH(1015, "个人用户配额不足"),
    ENTERPRISE_QUOTA_NO_ENOUGH(1016, "企业用户配额不足"),
    QUERY_SIGNER_STATUS_FAILED(1017, "查询合同签署者状态失败"),
    LOCK_AND_FINISH_CONTRACT_FAILED(1018, "锁定并结束合同失败"),
    NO_RECORD_IN_DB(1019, "数据库中没有对应的记录"),
    DOWN_CONTRACT_FROM_BEST_SIGN_FAILED(1020, "从上上签下周合同失败"),
    UPLOAD_FILE_TO_FILE_SYSTEM_FAILED(1021, "上传文件到文件系统失败"),
    DEDUCT_QUOTA_FAILED(1022, "扣除配额失败"),
    NO_ACCOUNT_SIGN_CERTIFY_OBJ_DATA(1024, "没有客户签章认证记录"),
    SIGNER_HAS_SIGNED(1025, "已签署"),
    NO_SIGN_REQUEST_RECORD_IN_DB(1026, "没有对应签署请求记录"),
    CONTRACT_HAS_NOT_SIGNED_AND_FINISH(1027, "合同还没签署完"),
    GET_CONTRACT_FILE_ATTACHMENT_FAILED(1028, "获取合同附件失败"),
    SIGN_RESULT_CALL_BACK_FAILED(1029, "签署结果回调处理失败"),
    INIT_ELEC_SIGN_FAILED(1030, "电子签章初始化失败"),
    GET_PDF_TOTAL_PAGE_NUM_FAILED(1031, "获取PDF页数失败"),
    CONTRACT_HAS_EXPIRE(1032, "合同已过期"),
    DATA_HAS_SIGNED_CONTRACT(1033, "该条数据已签署过合同"),
    NO_SIGN_SETTING_EXIST(1034, "没有签署设置信息"),
    NOT_SUPPORT_APPTYPE_OBJAPINAME(1035, "暂不支持该应用在该对象上使用电子签章"),
    AUTO_SIGN_FAILED(1036, "自动签失败"),
    CERTIFY_CALLBACK_FAILED(1037, "认证回调失败"),
    HAS_NO_CERTIFIED_AND_ENABLE_TENANT_CERTIFY_OBJ_RECORD(1038, "找不到'已认证'并且是'已启用'的内部签章认证账号"),
    ACCOUNT_IS_NOT_ENABLE(1040, "客户签章认证不是'已启用'状态"),
    GENERATE_SIGNATURE_IMAGE_FAIL(1041, "生成印章失败"),
    UPLOAD_SIGNATURE_IMAGE_TO_BEST_SIGN_FAIL(1042, "上传用户签名/印章到上上签失败"),
    CREATE_SIGNATURE_IMAGE_BY_BEST_SIGN_FAIL(1043, "上上签生成用户签名/印章失败"),
    NO_SIGNER_FOUND_FOR_SIGNER_BEST_SIGN_ACCOUNT(1045, "通过上上签账号，找不到对应的签署者"),
    NO_SUPPORT_OBJ(1046, "不支持该自定义对象"),
    HAS_CHANGED(1047,"操作期间记录已被修改，请检查配额"),
    METADATA_ADD_ERROR(1048, "数据添加异常"),
    ARG_NULL_ERROR(1049,"参数为空"),
    NO_SUPPORT_QUOTA_TYPE(1050,"不支持该配额类型"),
    NO_SUPPORT_CERTIFY_TYPE(1051,"不支持该认证类型"),
    NO_SUPPORT_CERTIFY_STATUS(1052,"不支持该认证状态"),
    GENERATE_BEST_SIGN_ACCOUNT(1053,"生成上上签帐号异常"),
    GET_SIGN_STATUS_FAILED(1054, "获取签署状态是不"),
    GET_IS_HAS_SIGN_PERMISSION_FAIL(1055, "查询是否有签署权限失败"),
    ;

    int code;
    String message;

    ElecSignErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    ElecSignErrorCode(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
