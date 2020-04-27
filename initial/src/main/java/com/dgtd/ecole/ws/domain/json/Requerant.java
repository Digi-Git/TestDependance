package com.dgtd.ecole.ws.domain.json;

import com.google.gson.annotations.SerializedName;
import org.springframework.stereotype.Component;

import java.io.Serializable;


@Component
public class Requerant implements Serializable , PersonJson{

    @SerializedName("postNomRequerant")
    private String postnom ="";
    @SerializedName("nomRequerant")
    private String nom="";
    @SerializedName("prenomRequerant")
    private String prenom="";
    private String lienDeParente="";                           //Pere mere
    private int idQualite;                                  // id qualite dans discoUtils
    private String qualite="";                                 //pere mere mandataire
    @SerializedName("lieuNaissanceRequerant")
    private String lieuNaissance="";                           //Lieu de naissance (ville) du requerant
    @SerializedName("NaissanceLocalId")
    private int idLocalisationLieuNaissance;
    @SerializedName("autreLocalisationLieuNaissanceRequerant")
    private String autreLocalisationLieuNaissance="";
    @SerializedName("dateNaissanceRequerant")
    private String dateNaissance="";
    @SerializedName("idProfessionRequerant")
    private int idProfession;                               //Id profession dans dicoUtil
    @SerializedName("professionRequerant")
    private String profession="";
    @SerializedName("professionRequerantAutre")
    private String autreProfession="";                         //Profession du Requerant non trouve dans dicoUtil
    @SerializedName("idNationaliteRequerant")
    private int idNationalite;                              //id nationalite dans dicoUtil
    @SerializedName("nationaliteRequerant")
    private String nationalite="";
    @SerializedName("nationaliteRequerantAutre")
    private String nationaliteAutre="";                    //nationnalité du Requerant non trouve dans dicoUtil
    @SerializedName("communeRequerant")
    private String commune="";                                 //Commune de residence du requerant
    @SerializedName("AdrLocalId")
    private int idLocalisationCommune;                      //Id localisation de l'adresse
    @SerializedName("autreLocalisationRequerant")
    private String autreLocalisation="";
    @SerializedName("adresseAvenueRequerant")
    private String adresseAvenue="";
    @SerializedName("adresseNumeroRequerant")
    private String adresseNumero="";
    @SerializedName("adresseQuartierRequerant")
    private String adresseQuartier="";

    private String email="";                                   //adresse mail du requerant
    private String tel="";                                     //Numero de telephone du requerant
    private String NaissanceAutre="";                          //Lieu de naissance autre non connue dans localisationUI
    private String ResidenceAutre="";                          //Lieu de residence autre non connue dans localisationUI


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

    public String getLienDeParente() {
        return lienDeParente;
    }

    public void setLienDeParente(String lienDeParente) {
        this.lienDeParente = lienDeParente;
    }

    public int getIdQualite() {
        return idQualite;
    }

    public void setIdQualite(int idQualite) {
        this.idQualite = idQualite;
    }

    public String getQualite() {
        return qualite;
    }

    public void setQualite(String qualite) {
        this.qualite = qualite;
    }

    public String getLieuNaissance() {
        return lieuNaissance;
    }

    public void setLieuNaissance(String lieuNaissance) {
        this.lieuNaissance = lieuNaissance;
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
    public String getAutreLocalisationLieuNaissance() {
        return autreLocalisationLieuNaissance;
    }

    @Override
    public void setAutreLocalisationLieuNaissance(String autreLocalisationLieuNaissance) {
        this.autreLocalisationLieuNaissance = autreLocalisationLieuNaissance;
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
    public String getProfession() {
        return profession;
    }

    @Override
    public void setProfession(String profession) {
        this.profession = profession;
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

    @Override
    public String getNationalite() {
        return nationalite;
    }

    @Override
    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
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
    public String getCommune() {
        return commune;
    }

    @Override
    public void setCommune(String commune) {
        this.commune = commune;
    }

    @Override
    public int getIdLocalisationCommune() {
        return idLocalisationCommune;
    }

    @Override
    public void setIdLocalisationCommune(int idLocalisationCommune) {
        this.idLocalisationCommune = idLocalisationCommune;
    }

    @Override
    public String getAutreLocalisation() {
        return autreLocalisation;
    }

    @Override
    public void setAutreLocalisation(String autreLocalisation) {
        this.autreLocalisation = autreLocalisation;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getNaissanceAutre() {
        return NaissanceAutre;
    }

    public void setNaissanceAutre(String naissanceAutre) {
        NaissanceAutre = naissanceAutre;
    }

    public String getResidenceAutre() {
        return ResidenceAutre;
    }

    public void setResidenceAutre(String residenceAutre) {
        ResidenceAutre = residenceAutre;
    }
}
