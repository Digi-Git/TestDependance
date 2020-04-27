package com.dgtd.common.payload;

public enum ResponseType {

    ENREGISTRE(1),
    REFUSE(0);
    private final int responseType;

    private ResponseType(int responseType){
        this.responseType = responseType;
    }

    public int getResponseType(){
        return responseType;
    }
}
