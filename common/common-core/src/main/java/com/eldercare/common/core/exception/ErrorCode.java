package com.eldercare.common.core.exception;

/**
 * 统一错误码枚举。
 * 通用错误码：100 ~ 999；用户域 1xxx；订单/工单域 2xxx；健康域 3xxx；消息域 4xxx。
 */
public enum ErrorCode {

    SUCCESS(200, "操作成功"),

    // 通用错误
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方式不支持"),
    SYSTEM_ERROR(500, "系统繁忙，请稍后再试"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    // 用户域 1xxx
    USER_NOT_EXIST(1001, "用户不存在"),
    PASSWORD_ERROR(1002, "用户名或密码错误"),
    ACCOUNT_DISABLED(1003, "账号已被禁用"),
    TOKEN_INVALID(1004, "令牌无效或已过期"),
    USERNAME_EXIST(1005, "用户名已存在"),
    PHONE_EXIST(1006, "手机号已注册"),
    OLD_PASSWORD_ERROR(1007, "原密码错误"),

    // 订单/工单域 2xxx
    ORDER_NOT_EXIST(2001, "订单不存在"),
    WORK_ORDER_NOT_EXIST(2002, "工单不存在"),
    SERVICE_NOT_EXIST(2003, "服务项目不存在"),
    ORDER_STATUS_ERROR(2004, "订单状态不允许该操作"),
    WORK_ORDER_STATUS_ERROR(2005, "工单状态不允许该操作"),
    NO_AVAILABLE_PROVIDER(2006, "暂无可派单的服务商"),
    ALREADY_DISPATCHED(2007, "工单已派单，请勿重复操作"),
    ALREADY_EVALUATED(2008, "该工单已评价"),

    // 健康域 3xxx
    HEALTH_RECORD_NOT_EXIST(3001, "健康档案不存在"),
    ELDER_NOT_EXIST(3002, "老人档案不存在"),

    // 消息域 4xxx
    MESSAGE_NOT_EXIST(4001, "消息不存在");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
