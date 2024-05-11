package com.cqupt.software_1.common;

public class Result<T> {
    private Object data;
    private String msg;
    private int code;

    public Result() {
    }

    public Result(Object data, int code) {
        this.data = data;
        this.code=code;
    }

    // getter setter 省略，构造方法省略
    // 操作成功返回数据
    public static Result success(Object data) {
        return success(200, "操作成功", data);
    }

    public static Result success(int code ,String msg) {
        return success(code, msg, null);
    }

    public static Result success(String msg, Object data) {
        return success(200,msg,data);
    }

    public static Result success(int code, String msg, Object data) {
        Result r = new Result();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }



    // 操作异常返回
    public static Result fail(int code, String msg, Object data) {
        Result r = new Result();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    public static Result fail(String msg) {
        return fail(400,msg,null);
    }

    public static Result fail(int code, String msg) {
        return fail(code,msg,"null");
    }

    public static Result fail(String msg, Object data) {
        return fail(400,msg,data);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
