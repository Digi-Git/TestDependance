package com.dgtd.ecole.ws.domain.json;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
public class EnfantJson implements Serializable {

    private int id;                                     // id de l'enfant créé en base de données
    private int id_document;                            // id du dossier contenant l'enfant indiqué sur le formulaire papier
    private String nomEnfant;                          // nom de l'enfant
    private String postNomEnfant;                      // post nom de l'enfant
    private String prenomEnfant;                       // prenom de l'enfant
    private String lieuNaissanceEnfant;                // lieu de naissance de l'enfant
    private String autreLocalisationLieuNaissanceEnfant;
    private int idLocalisationLieuNaissanceEnfant;
    private int sexeEnfant;                            // id sexe masculin = 18 ou sexe feminin = 19 dans dicoUtils
    private String dateNaissanceEnfant;
    private String heureNaissanceEnfant;               // heure de naissance de lenfant (ex : 12h30)
    private String scolarise;                         // l'enfant est il scolarisé dans l'ecole


    public EnfantJson(){
        setPostNomEnfant("");
        setAutreLocalisationLieuNaissanceEnfant("");
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_document() {
        return id_document;
    }

    public void setId_document(int id_document) {
        this.id_document = id_document;
    }

    public String getNomEnfant() {
        return nomEnfant;
    }

    public void setNomEnfant(String nomEnfant) {
        this.nomEnfant = nomEnfant;
    }

    public String getPostNomEnfant() {
        return postNomEnfant;
    }

    public void setPostNomEnfant(String postNomEnfant) {
        this.postNomEnfant = postNomEnfant;
    }

    public String getPrenomEnfant() {
        return prenomEnfant;
    }

    public void setPrenomEnfant(String prenomEnfant) {
        this.prenomEnfant = prenomEnfant;
    }

    public String getLieuNaissanceEnfant() {
        return lieuNaissanceEnfant;
    }

    public void setLieuNaissanceEnfant(String lieuNaissanceEnfant) {
        this.lieuNaissanceEnfant = lieuNaissanceEnfant;
    }

    public String getAutreLocalisationLieuNaissanceEnfant() {
        return autreLocalisationLieuNaissanceEnfant;
    }

    public void setAutreLocalisationLieuNaissanceEnfant(String autreLocalisationLieuNaissanceEnfant) {
        this.autreLocalisationLieuNaissanceEnfant = autreLocalisationLieuNaissanceEnfant;
    }

    public int getIdLocalisationLieuNaissanceEnfant() {
        return idLocalisationLieuNaissanceEnfant;
    }

    public void setIdLocalisationLieuNaissanceEnfant(int idLocalisationLieuNaissanceEnfant) {
        this.idLocalisationLieuNaissanceEnfant = idLocalisationLieuNaissanceEnfant;
    }

    public int getSexeEnfant() {
        return sexeEnfant;
    }

    public void setSexeEnfant(int sexeEnfant) {
        this.sexeEnfant = sexeEnfant;
    }

    public String getDateNaissanceEnfant() {
        return dateNaissanceEnfant;
    }

    public void setDateNaissanceEnfant(String dateNaissanceEnfant) {
        this.dateNaissanceEnfant = dateNaissanceEnfant;
    }

    public String getHeureNaissanceEnfant() {
        return heureNaissanceEnfant;
    }

    public void setHeureNaissanceEnfant(String heureNaissanceEnfant) {
        this.heureNaissanceEnfant = heureNaissanceEnfant;
    }

    public String isScolarise() {

        switch(scolarise){
            case "true":    return "Oui";
            case "false":   return "Non";
        }
        return scolarise;
    }

    public void setScolarise(String scolarise) {
        this.scolarise = scolarise;
    }
}
