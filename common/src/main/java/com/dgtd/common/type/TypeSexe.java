package com.dgtd.common.type;

public enum TypeSexe {

    FEMININ ("Féminin"),
    MASCULIN ("Masculin");
    private String genre;
    private TypeSexe(String genre){
        this.genre = genre;
    }
    public String getGenre(){
        return this.genre;
    }
}
