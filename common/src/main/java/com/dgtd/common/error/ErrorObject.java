package com.dgtd.common.error;


import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ErrorObject implements DGTDError {

    //private boolean isCurrent;
    private String id;
    private TypeError typeError;
    private String details;
    private String date;


    public ErrorObject(){
        id = temporaryKey;
        DateFormat formatter = new SimpleDateFormat("YYYY/MM/dd");
        this.date = formatter.format(new Date());

    }


    public ErrorObject(TypeError typeError, String details){
        this();
        this.typeError = typeError;
        this.details = details;
    }

    public ErrorObject(String id, TypeError typeError, String details) {
       this();
       if(!id.equals(temporaryKey)){
           this.id = id;
       }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getDate() {
        return this.date;
    }

    @Override
    public TypeError getTypeError() {
        return typeError;
    }

    @Override
    public void setTypeError(TypeError typeError) {
        this.typeError = typeError;
    }

    @Override
    public String getDetails() {
        return details;
    }

    @Override
    public void setDetails(String details) {
        this.details = details;
    }


    @Override
    public String toString() {
        return "ErrorObject{" +
                "id='" + id + '\'' +
                ", typeError=" + typeError +
                ", details='" + details + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
