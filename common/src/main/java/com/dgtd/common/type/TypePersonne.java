package com.dgtd.common.type;

import java.util.HashMap;
import java.util.Map;

public enum TypePersonne {

    INTERESSE("Interessé"),
    PERE("Père"),
    MERE("Mère"),
    CONJOINT("Conjoint"),
    //Titre donné principalement dans les ordonnances et expérimentations
    DECLARANT("Déclarant"),
    //Titre donné dans les actes de naissance standards - non utilisé pour les expérimentations
    DEMANDEUR("Demandeur"),
    //non utilisé pour les expérimentations
    REQUERANT("Requerant"),
    //non utilisé pour les expérimentations
    TEMOIN1("Témoin 1"),
    //non utilisé pour les expérimentations
    TEMOIN2("Témoin 2");

    private String typePersonne;
    private static final Map<String, TypePersonne> Recherche = new HashMap<>();
    static
    {
        for(TypePersonne tp: values())
        {
            Recherche.put(tp.getTypePersonne(), tp);
        }
    }

    private TypePersonne(String typePersonne){
        this.typePersonne = typePersonne;
    }

    public String getTypePersonne(){
        return this.typePersonne;
    }

    public static TypePersonne getTypePersonne(String typePersonne){

        return Recherche.get(typePersonne);


    }
}
