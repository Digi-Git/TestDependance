package com.dgtd.common.dicoutil;

/**
 * Définition d'un élément dans la liste.
 */
public class ListValeurItem  {

    private int id;             // Id interne
    private String intitule;    // Libellé dans la liste
    private boolean defaut;     // Valeur par défaut dans la liste
    private int ordre;          // Ordre pour la présentation
    private int devTag;         // Usage particulier pour le développément (ex : cas de detection de la clim dans la liste des alimentations par exemple).

    private int arrayPos;       // Valeur interne mise à jour à la volée de position réelle dans la liste


    public ListValeurItem() {
    }

    public ListValeurItem(int id, String intitule, boolean defaut) {
        this.id = id;
        this.intitule = intitule;
        this.defaut = defaut;

        this.ordre = 0;
        this.devTag = 0;
        this.arrayPos = 0;
    }

    public ListValeurItem(int id, String intitule, boolean defaut, int ordre) {
        this.id = id;
        this.intitule = intitule;
        this.defaut = defaut;
        this.ordre = ordre;

        this.devTag = 0;
        this.arrayPos = 0;
    }

    public ListValeurItem(int id, String intitule, boolean defaut, int ordre, int devTag) {
        this.id = id;
        this.intitule = intitule;
        this.defaut = defaut;
        this.ordre = ordre;
        this.devTag = devTag;

        this.arrayPos = 0;
    }

    @Override
    public String toString() {
        if (intitule == null)
            return super.toString();
        else
            return this.intitule;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public boolean isDefaut() {
        return defaut;
    }

    public void setDefaut(boolean defaut) {
        this.defaut = defaut;
    }

    public int getOrdre() {
        return ordre;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }

    public int getArrayPos() {
        return arrayPos;
    }

    public void setArrayPos(int arrayPos) {
        this.arrayPos = arrayPos;
    }

    public int getDevTag() {
        return devTag;
    }

    public void setDevTag(int devTag) {
        this.devTag = devTag;
    }
}
