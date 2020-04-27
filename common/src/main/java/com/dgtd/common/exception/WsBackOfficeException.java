package com.dgtd.common.exception;


public class WsBackOfficeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

   public WsBackOfficeException(String message){
        super(message);
    }
    public WsBackOfficeException(String message, Throwable cause){
        super(message, cause);
    }


}
