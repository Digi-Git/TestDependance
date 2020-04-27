package com.dgtd.common.dicoutil;

public enum PersonType {
    DEMANDEUR(20),
    PERE (21) ,
    MERE (22),
    TEMOIN1 (23),
    TEMOIN2(24),
    INTERESSE (50),
    DECLARANT(51);
    private  final int idPersonType;

    private PersonType(int idPersonType){
        this.idPersonType = idPersonType;
    }

    public int getIdPersonType() {
        return idPersonType;
    }

    public static String getIntitulePersonType(int idPersonType){
        switch (idPersonType){
            case 20 : return "Demandeur";
            case 21: return "Père";
            case 22: return "Mère";
            case 23: return "Temoin 1";
            case 24 :return "Temoin 2";
            case 50 : return "Interessé";
            case 51 : return "Déclarant";
        }
        return "NC";
    }

}
