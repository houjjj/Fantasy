//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.houjun.rocketmq.utils;

public class CustomException extends RuntimeException {
    public static final long serialVersionUID = 1L;
    private int status;
    private Long host;
    private Object data;

    public CustomException(int status) {
        this.status = status;
    }

    public CustomException(int status, String message) {
        super(message);
        this.status = status;
    }

    public CustomException(int status, String message, Long host) {
        super(message);
        this.status = status;
        this.host = host;
    }

    public CustomException(int status, String message, Object data) {
        super(message);
        this.status = status;
        this.data = data;
    }

    public CustomException(int status, Long host, Object data) {
        this.status = status;
        this.host = host;
        this.data = data;
    }

    public CustomException(String message, int status, Long host, Object data) {
        super(message);
        this.status = status;
        this.host = host;
        this.data = data;
    }

    public CustomException(String message, Throwable cause, int status, Long host, Object data) {
        super(message, cause);
        this.status = status;
        this.host = host;
        this.data = data;
    }

    public CustomException(Throwable cause, int status, Long host, Object data) {
        super(cause);
        this.status = status;
        this.host = host;
        this.data = data;
    }

    public CustomException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int status, Long host, Object data) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.status = status;
        this.host = host;
        this.data = data;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getHost() {
        return this.host;
    }

    public void setHost(Long host) {
        this.host = host;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
