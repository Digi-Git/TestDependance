package com.dgtd.common.payload;

public enum TypeStatus {

    EN_COURS("En cours"),

    ACCEPTE("Enregistré"),

    REFUSE("Refusé") ;

    public String typeStatus;

    private TypeStatus(String typeStatus){
        this.typeStatus = typeStatus;
    }
    public String getTypeStatus(){
        return this.typeStatus;
    }

    @Override
    public String toString() {
        return "Le statut de la requête est : " + typeStatus ;
    }
}
