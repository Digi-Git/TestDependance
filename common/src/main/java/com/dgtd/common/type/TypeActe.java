package com.dgtd.common.type;

import java.util.HashMap;
import java.util.Map;

public enum TypeActe {

    NAISSANCE("Acte de Naissance"),
    DECES("Acte de décès"),
    ORDONNANCE("Ordonnance");

    private String typeActe;

    TypeActe(String typeActe) {
        this.typeActe = typeActe;
    }

    public String getTypeActe() {
        return typeActe;
    }

    private static final Map<String, TypeActe> Recherche = new HashMap<>();

    static
    {
        for(TypeActe tacte : values())
        {
            Recherche.put(tacte.getTypeActe().replace("Acte de ",""), tacte);
        }
    }

    /**
     * Attention, pas de gestion du "non trouvé"
     * @param typeActe Correspond à la partie chaine de l'énum.
     * @return null ou le TypeActe correspondant à l'énum.
     */
    public static TypeActe get(String typeActe) {
        return Recherche.get(typeActe);
    }


    public static String getAllValues(){
        String values = "";
        for(TypeActe typeActe : TypeActe.values()){
            values += typeActe.getTypeActe() + " ";
        }
        return values;
    }
}
