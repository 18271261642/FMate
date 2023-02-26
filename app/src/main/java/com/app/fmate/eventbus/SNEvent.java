package com.app.fmate.eventbus;

/**
 * 功能:事件
 */

public class SNEvent<T> {


    private int code;
    private T data;


    public SNEvent(int code) {
        this.code = code;
    }

    public SNEvent(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public T getData() {
        return data;
    }
}
