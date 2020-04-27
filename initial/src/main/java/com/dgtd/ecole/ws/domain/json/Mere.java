package com.dgtd.ecole.ws.domain.json;

import com.google.gson.annotations.SerializedName;
import org.springframework.stereotype.Component;

@Component
public class Mere implements PersonJson {

    @SerializedName("postnomMere")
    private String postnom="";
    @SerializedName("nomMere")
    private String nom="";
    @SerializedName("prenomMere")
    private String prenom="";
    @SerializedName("idLocalisationLieuNaissanceMere")
    private int idLocalisationLieuNaissance;
    @SerializedName("autreLocalisationLieuNaissanceMere")
    private String autreLocalisationLieuNaissance="";
    @SerializedName("idLocalisationCommuneMere")
    private int idLocalisationCommune;
    @SerializedName("autreLocalisationCommuneMere")
    private String autreLocalisation="";
    @SerializedName("CommuneMere")
    private  String commune="";
    @SerializedName("dateNaissanceMere")
    private String dateNaissance="";
    @SerializedName("idProfessionMere")
    private int idProfession;
    @SerializedName("idNationaliteMere")
    private int idNationalite;
    @SerializedName("professionMere")
    private String profession="";
    @SerializedName("professionMereAutre")
    private String autreProfession="";
    @SerializedName("nationaliteMere")
    private String nationalite="";
    @SerializedName("nationaliteMereAutre")
    private String nationaliteAutre="";
    @SerializedName("adresseAvenueMere")
    private String adresseAvenue="";
    @SerializedName("adresseNumeroMere")
    private String adresseNumero="";
    @SerializedName("adresseQuartierMere")
    private String adresseQuartier="";


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
    public String getAutreProfession() {
        return autreProfession;
    }

    @Override
    public void setAutreProfession(String autreProfession) {
        this.autreProfession = autreProfession;
    }

    @Override
    public int getIdNationalite() {
        return idNationalite;
    }

    @Override
    public void setIdNationalite(int idNationalite) {
        this.idNationalite = idNationalite;
    }

    public String getNationaliteAutre() {
        return nationaliteAutre;
    }

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
}
