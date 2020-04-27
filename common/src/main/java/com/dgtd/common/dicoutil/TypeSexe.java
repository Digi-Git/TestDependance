package com.dgtd.common.dicoutil;

public enum TypeSexe {

    MASCULIN(18),
    FEMININ(19);

    private final int typeSexe;

    private TypeSexe(int typeSexe){
        this.typeSexe = typeSexe;
    }

    public int getTypeSexe(){
        return typeSexe;
    }
}
