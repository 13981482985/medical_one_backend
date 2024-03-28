package com.cqupt.software_1.common;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class R<T>{

    private Integer code ;
    private String msg;
    private Object data;

    private Integer total;



    public R(){}

    public R(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // 操作成功返回数据
    public static R success(Object data) {
        return success(200, "操作成功", data);
    }

    public static R success(int code ,String msg) {
        return success(code, msg, null);
    }

    public static R success(String msg, Object data) {
        return success(200,msg,data);
    }
    public static R fail(String msg) {
        return fail(400,msg,null);
    }

    public static R fail(int code, String msg) {
        return fail(code,msg,"null");
    }

    public static R fail(String msg, Object data) {
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
    public static R success(int code, String msg, Object data) {
        R r = new R();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    // 操作异常返回
    public static R fail(int code, String msg, Object data) {
        R r = new R();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

}
