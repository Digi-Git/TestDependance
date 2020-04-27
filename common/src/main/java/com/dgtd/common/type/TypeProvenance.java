package com.dgtd.common.type;

public enum TypeProvenance {

    BLUEKANGO("Bluekango"),
    RAPIDPRO("Rapidpro"),
    TABLETTE("Tablette");

    public String typeProvenance;

    private TypeProvenance(String typeProvenance){
        this.typeProvenance = typeProvenance;
    }

    public  String getTypeProvenance() {
        return typeProvenance;
    }
}
