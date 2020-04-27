package com.dgtd.common.error;

public enum TypeError {

    NO_PHOTO ("Pas de photo"),
    DOUBLON("Dossier déjà existant"),
    IO ("Input/Output"),
    DESERIALISATION ("Désérialisation"),
    DATA_INTEGRATION("Intégration en BDD"),
    TRANSFERT_EVELIN("Transfert EVELIN"),
    SECURITE("Sécurité"),
    CSV_MAPPING("Mapping CSV"),
    MISSING_INFO ("Information manquante"),
    BLUEKANGO ("Bluekango"),
    RAPIDPRO("RapidPro"),
    EXCEPTION("Exception");

    private String typeError;

    private TypeError(String typeError){
        this.typeError = typeError;
    }

    public String getTypeError() {
        return typeError;
    }
}
