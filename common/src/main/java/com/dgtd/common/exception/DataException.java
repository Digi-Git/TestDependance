package com.dgtd.common.exception;

import com.dgtd.common.error.ErrorObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DataException extends RuntimeException {
    public DataException(String message){
        super(message);
    }
    public DataException(String message, Throwable cause){
        super(message, cause);
    }

    public DataException(ErrorObject errorObject){
        super(errorObject.getTypeError()  + " "+ errorObject.getDetails());
    };
}
