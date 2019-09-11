package com.jstarcraft.core.monitor.route.exception;

/**
 * 路由异常
 * 
 * @author Birdy
 */
public class RouteException extends RuntimeException {

    private static final long serialVersionUID = 6940090345522183684L;

    public RouteException() {
        super();
    }

    public RouteException(String message, Throwable exception) {
        super(message, exception);
    }

    public RouteException(String message) {
        super(message);
    }

    public RouteException(Throwable exception) {
        super(exception);
    }

}
