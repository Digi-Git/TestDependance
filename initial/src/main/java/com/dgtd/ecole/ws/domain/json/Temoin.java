package com.dgtd.ecole.ws.domain.json;

import com.google.gson.annotations.SerializedName;
import org.springframework.stereotype.Component;

@Component
public class Temoin implements PersonJson {

    @SerializedName("postnomTemoin1")
    private String postnom="";
    @SerializedName("nomTemoin1")
    private String nom="";
    @SerializedName("prenomTemoin1")
    private String prenom="";
    @SerializedName("idLocalisationLieuNaissanceTemoin1")
    private int idLocalisationLieuNaissance;
    @SerializedName("idLocalisationCommuneTemoin1")
    private int idLocalisationCommune;
    @SerializedName("autreLocalisationLieuNaissanceTemoin1")
    private String autreLocalisationLieuNaissance="";
    @SerializedName("autreLocalisationCommuneTemoin1")
    private String autreLocalisation="";
    @SerializedName("CommuneTemoin1")
    private String commune="";
    @SerializedName("dateNaissanceTemoin1")
    private String dateNaissance="";
    @SerializedName("idProfessionTemoin1")
    private int idProfession;
    @SerializedName("idNationaliteTemoin1")
    private int idNationalite;
    @SerializedName("nationaliteTemoin1Autre")
    private String nationaliteAutre="";
    @SerializedName("professionTemoin1")
    private String profession="";
    @SerializedName("professionTemoin1Autre")
    private String autreProfession="";
    @SerializedName("nationaliteTemoin1")
    private String nationalite="";
    @SerializedName("adresseAvenueTemoin1")
    private String adresseAvenue="";
    @SerializedName("adresseNumeroTemoin1")
    private String adresseNumero="";
    @SerializedName("adresseQuartierTemoin1")
    private String adresseQuartier="";

    public Temoin() {
    }

    @Override
    public String getPostnom() {
        return postnom;
    }

    @Override
    public void setPostnom(String postnom) {
        this.postnom = postnom;
    }

    @Override
    public String getNom() {
        return nom;
    }

    @Override
    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String getPrenom() {
        return prenom;
    }

    @Override
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    @Override
    public int getIdLocalisationLieuNaissance() {
        return idLocalisationLieuNaissance;
    }

    @Override
    public void setIdLocalisationLieuNaissance(int idLocalisationLieuNaissance) {
        this.idLocalisationLieuNaissance = idLocalisationLieuNaissance;
    }

    @Override
    public int getIdLocalisationCommune() {
        return idLocalisationCommune;
    }

    @Override
    public void setIdLocalisationCommune(int idLocalisationCommune) {
        this.idLocalisationCommune = idLocalisationCommune;
    }

    public String getAutreLocalisationLieuNaissance() {
        return autreLocalisationLieuNaissance;
    }

    public void setAutreLocalisationLieuNaissance(String autreLocalisationLieuNaissance) {
        this.autreLocalisationLieuNaissance = autreLocalisationLieuNaissance;
    }

    public String getAutreLocalisation() {
        return autreLocalisation;
    }

    public void setAutreLocalisation(String autreLocalisation) {
        this.autreLocalisation = autreLocalisation;
    }

    @Override
    public String getCommune() {
        return commune;
    }

    @Override
    public void setCommune(String commune) {
        this.commune = commune;
    }

    @Override
    public String getDateNaissance() {
        return dateNaissance;
    }

    @Override
    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    @Override
    public int getIdProfession() {
        return idProfession;
    }

    @Override
    public void setIdProfession(int idProfession) {
        this.idProfession = idProfession;
    }

    @Override
    public int getIdNationalite() {
        return idNationalite;
    }

    @Override
    public void setIdNationalite(int idNationalite) {
        this.idNationalite = idNationalite;
    }

    @Override
    public String getNationaliteAutre() {
        return nationaliteAutre;
    }

    @Override
    public void setNationaliteAutre(String nationaliteAutre) {
        this.nationaliteAutre = nationaliteAutre;
    }

    @Override
    public String getProfession() {
        return profession;
    }

    @Override
    public void setProfession(String profession) {
        this.profession = profession;
    }

    @Override
    public String getNationalite() {
        return nationalite;
    }

    @Override
    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }

    @Override
    public String getAdresseAvenue() {
        return adresseAvenue;
    }

    @Override
    public void setAdresseAvenue(String adresseAvenue) {
        this.adresseAvenue = adresseAvenue;
    }

    @Override
    public String getAdresseNumero() {
        return adresseNumero;
    }

    @Override
    public void setAdresseNumero(String adresseNumero) {
        this.adresseNumero = adresseNumero;
    }

    @Override
    public String getAdresseQuartier() {
        return adresseQuartier;
    }

    @Override
    public void setAdresseQuartier(String adresseQuartier) {
        this.adresseQuartier = adresseQuartier;
    }

    public String getAutreProfession() {
        return autreProfession;
    }

    public void setAutreProfession(String autreProfession) {
        this.autreProfession = autreProfession;
    }


}
