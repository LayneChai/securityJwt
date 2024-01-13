package com.example.demo.common.Vo;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;
    public Result() {
    }
    public Result(T data) {
        this.data = data;
    }

    /**
     * 成功返回结果
     * @return
     */
    public static Result success() {
        Result result = new Result<>();
        result.setCode(200);
        result.setMsg("成功");
        return result;
    }

    /**
     * 成功返回结果
     * @param data
     * @return
     * @param <T>
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>(data);
        result.setCode(200);
        result.setMsg("成功");
        return result;
    }

    /**
     * 失败返回结果
     * @param code
     * @param msg
     * @return
     */
    public static Result error(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    /**
     * 失败返回结果
     * @param msg
     * @return
     */
    public static Result error500(String msg) {
        Result result = new Result();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }

}