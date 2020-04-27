package com.dgtd.common.excel;

public class MappingExportChamp {

    private String propriete;
    private String colonne;
    private String output;

    public MappingExportChamp(String propriete, String colonne) {
        this.propriete = propriete;
        this.colonne = colonne;
        this.output = "";
    }

    public String getPropriete() {
        return propriete;
    }

    public void setPropriete(String propriete) {
        this.propriete = propriete;
    }

    public String getColonne() {
        return colonne;
    }

    public void setColonne(String colonne) {
        this.colonne = colonne;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
