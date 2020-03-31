package cn.acyou.aries.common;

import java.io.Serializable;

/**
 * @author JianJun.Li
 * @date 2018/12/11 17:59
 **/
public class Result<T> implements Serializable {

    private int code;
    private String message;
    private boolean success = true;
    private T data;

    private Result() {
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private Result(int code, T data, String message) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    public static <T> Result<T> error(int code, String message) {
        Result result = new Result(code, message);
        result.setSuccess(false);
        return result;
    }

    public static <T> Result<T> error(int code, T data, String message) {
        Result result = new Result(code, data, message);
        result.setSuccess(false);
        return result;
    }

    public static <T> Result<T> success() {
        return success(null, "处理成功");
    }

    public static <T> Result<T> success(T data) {
        return success(data, "处理成功");
    }

    public static <T> Result<T> success(T data, String message) {
        return new Result(0, data, message);
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public boolean notSuccess() {
        return !this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Result data(T data) {
        return success(data, (String) null);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Result{");
        sb.append("code=").append(code);
        sb.append(", message='").append(message).append('\'');
        sb.append(", success=").append(success);
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
