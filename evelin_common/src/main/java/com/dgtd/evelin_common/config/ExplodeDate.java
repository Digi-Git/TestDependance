package com.dgtd.evelin_common.config;

public class ExplodeDate {

    private String annee;
    private String mois;
    private String jour;
    private String heure;
    private String min;

    private String dateExploded;
    private String heureExploded;
    private String heureExplodedEvelin;
    private String dateExplodedEvelin;

    public ExplodeDate () {
        dateExploded = "";
        dateExplodedEvelin = "";
        heureExploded = "";
        heureExplodedEvelin = "";
        annee= "";
        mois = "";
        jour = "";
        heure = "";
        min = "";
    }

    public String getAnnee() {
        return annee;
    }

    public void setAnnee(String annee) {
        this.annee = annee;
    }

    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public String getJour() {
        return jour;
    }

    public void setJour(String jour) {
        this.jour = jour;
    }

    public String getDateExploded() {
        return dateExploded;
    }

    public void setDateExploded(String dateExploded) {
        this.dateExploded = dateExploded;
    }

    public String getHeureExploded() {
        return heureExploded;
    }

    public void setHeureExploded(String heureExploded) {
        this.heureExploded = heureExploded;
    }

    public String getDateExplodedEvelin() {
        return dateExplodedEvelin;
    }

    public void setDateExplodedEvelin(String dateExplodedEvelin) {
        this.dateExplodedEvelin = dateExplodedEvelin;
    }

    public String getHeureExplodedEvelin() {
        return heureExplodedEvelin;
    }

    public void setHeureExplodedEvelin(String heureExplodedEvelin) {
        this.heureExplodedEvelin = heureExplodedEvelin;
    }

    // Explode - frontale
    public void Explode (String dateSaisie, String heureSaisie) {

        ExplodeDate(dateSaisie);
        ExplodeTime(heureSaisie);
    }

    // Reformatage de l'heure
    private void ExplodeTime (String heureSaisie) {

        heureExplodedEvelin = "0000";
        heureExploded = "00:00";

        if (heureSaisie == null)
            return;

        String source = heureSaisie.trim();
        if ("".contentEquals(source))
            return;

        String[] pa = source.split(":");

        //heure = String.format("%02d", Integer.parseInt(pa[0]));
        heure  = (Integer.parseInt(pa[0]) >= 24) ? "00" : String.format("%02d", Integer.parseInt(pa[0]));

        //min = String.format("%02d", Integer.parseInt(pa[1]));
        String tmpmin = "00";
        if (pa.length >= 2)
            tmpmin = String.format("%02d", Integer.parseInt(pa[1]));
        min  = (Integer.parseInt(pa[0]) > 59) ? "00" : tmpmin;

        heureExploded = heure + ":" + min;
        heureExplodedEvelin = heure + min;
    }

    // Reformatage de la date
    public void ExplodeDate (String dateSaisie) {

        dateExplodedEvelin = "00000000";
        dateExploded = "00/00/0000";

        String source = dateSaisie.trim();

        if ("".contentEquals(source))
            return;

        String[] pa = source.split("/");
        jour = String.format("%02d", Integer.parseInt(pa[0]));
        mois = String.format("%02d", Integer.parseInt(pa[1]));
        annee = String.format("%04d", Integer.parseInt(pa[2]));
        dateExploded = jour + "/" + mois + "/" + annee;

        dateExplodedEvelin = annee + mois + jour;
    }
}
