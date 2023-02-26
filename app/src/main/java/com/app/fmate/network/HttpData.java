package com.app.fmate.network;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 统一接口数据结构
 */

/**
 * {
 *     	"success" : false,
 *     	"code" : "SYS50000",
 *     	"msg" : "手机号码格式不正确!",
 *     	"traceId" : "6c56c9aa6a91",
 *     	"data" : null
 *     }
 *
 *
 *   {
 *     	"success" : true,
 *     	"code" : "200",
 *     	"msg" : "请求成功",
 *     	"traceId" : "a39b6898dd6c",
 *     	"data" : true
 *     }
 */
public class HttpData<T> {

    /** 返回码 */
    private String code;
    /** 提示语 */
    private String msg;

    //是否成功
    private boolean success;

    /** 数据 */
    private T data;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return msg;
    }
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
    public T getData() {
        return data;
    }

    /**
     * 是否请求成功
     */
    public boolean isRequestSucceed() {
        return code.equals("200");
    }


    //是否请求成功，非200返回
    public boolean isNo200Code(){
        return !success || !code.equals("200");
    }

    /**
     * 是否 Token 失效
     */
    public boolean isTokenFailure() {
        return code.equals("BIZ10007");
    }
}