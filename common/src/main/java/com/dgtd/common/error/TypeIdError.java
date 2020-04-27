package com.dgtd.common.error;

public enum TypeIdError {

    BK("BluekangoId"),
    BDD("Id interne BDD"),
    TYPE_DATE("Type+Date"),
    SECURITE("Securité"),
    CONFIGURATION("Configuration");

    private String typeIdError;

    TypeIdError(String typeIdError){
        this.typeIdError = typeIdError;
    }

    public String getTypeIdError() {
        return typeIdError;
    }

    public static String generateIdError(TypeIdError error, String id){
       return  error + " -"+ id;
    }
}
