package com.jstarcraft.core.common.api;

/**
 * 接口异常
 * 
 * @author Birdy
 */
public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 1932713027634067951L;

    /** 异常状态 */
    protected int status;

    protected ApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}
