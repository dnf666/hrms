package com.facishare.crm.customeraccount.exception;

import com.facishare.paas.appframework.core.exception.ErrorCode;

/**
 * Created by xujf on 2017/10/11.
 */
public enum CustomerAccountErrorCode implements ErrorCode {
    ARGUMENT_EXCEPTION(301010001),

    ADD_FUNC_TO_OBJECT_EXCEPTION(0),

    INIT_FUNC_EXCEPTION(0),

    OUT_INVOKE_RESULT_ISNULL(0),

    ADD_FUNC_TO_ROLE(0),

    /**
     * 客户账户相关错误从020-100
     */
    CUSTOMER_ACCOUNT_NOT_EXIST(20, "客户账户不存在"),

    GET_CUSTOMER_ACCOUNT_DESCRBER_ERROR(21, "获取客户描述失败"),

    CUSTOMER_ACCOUNT_CAN_NOT_INVALID(22, "客户账户不能手动作废"),

    QUERY_CUSTOMER_ERROR(23, "SFA查询客户异常"),

    DESCRIBE_INIT_ERROR(24, "客户账户初始化异常"),

    CUSTOMER_ACCOUNT_INIT_READY(25, "客户账户已经初始化"),

    ERROR_GET_USED_CREDIT_AMOUNT(26, "获取已用信用额度出错"),

    CAN_NOT_INVALID_CUSTOMER_ACCOUNT(27, "客户账户有余额不能批量作废客户账户"),

    NO_RELATED_PAYMENT_OR_REFUEND(28, "无关联的回款或退款"),

    METADATA_QUERY_ERROR(29, "与数据查询异常"),

    AVAILABLE_CREDIT_LESS_THAN_ZERO(30, "可用信用小于0"),

    WRONG_LIEF_STATUS_WHEN_EDIT(31, "编辑触发生命状态不对"),

    QUERY_CUSTOMER_FROM_PG_ERROR(32, "PG查询客户异常"),

    PREPAY_TRANSFER_ERROR(33, "预存款迁移异常"),

    REBATE_OUTCOME_TRANSFER_ERROR(34, "返利迁移异常"),

    CUSTOMER_PAYMENT_ERROR(35, "回款服务异常"),

    ;

    int code;
    String message;

    CustomerAccountErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    CustomerAccountErrorCode(int code) {
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
