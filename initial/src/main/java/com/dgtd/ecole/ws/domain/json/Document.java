package com.dgtd.ecole.ws.domain.json;

import com.google.gson.annotations.SerializedName;
import org.springframework.stereotype.Component;

import java.io.Serializable;


import java.util.Date;
import java.util.List;
@Component
public class Document implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient int DossierID;                                                 //     id du dossier de la tablette
    private int EcoleID;                                                             //     id de l'ecole contenant ce dossier
    private String DossierNumber;                                                    //     numero de dossier tel que sur le formulaire papier
    private String AgentName;
    private Requerant Requerant;
    private com.dgtd.ecole.ws.domain.json.Pere Pere;
    private com.dgtd.ecole.ws.domain.json.Mere Mere;
    private com.dgtd.ecole.ws.domain.json.Temoin Temoin1;
    private com.dgtd.ecole.ws.domain.json.Temoin2 Temoin2;
    private List<com.dgtd.ecole.ws.domain.json.EnfantJson> EnfantList;        //     liste des enfant dans le dossier
    @SerializedName("Commentaire")
    private String commentaire;                                           //     commentaire sur le dossier
    @SerializedName("DateSaisie")
    private Date dateSaisie;                                              //     Date de saisie du dossier
    private boolean deleted;                                              //     Dossier supprimé de la tablette
    private Date dateSignature;

    public Document() {
        setCommentaire("");
        setAgentName("");
    }

    public int getDossierID() {
        return DossierID;
    }

    public void setDossierID(int dossierID) {
        DossierID = dossierID;
    }

    public int getEcoleID() {
        return EcoleID;
    }

    public void setEcoleID(int ecoleID) {
        EcoleID = ecoleID;
    }

    public String getDossierNumber() {
        return DossierNumber;
    }

    public void setDossierNumber(String dossierNumber) {
        DossierNumber = dossierNumber;
    }


    public String getAgentName() {
        return AgentName;
    }

    public void setAgentName(String agentName) {
        AgentName = agentName;
    }

    public com.dgtd.ecole.ws.domain.json.Requerant getRequerant() {
        return Requerant;
    }

    public void setRequerant(com.dgtd.ecole.ws.domain.json.Requerant requerant) {
        Requerant = requerant;
    }

    public com.dgtd.ecole.ws.domain.json.Pere getPere() {
        return Pere;
    }

    public void setPere(com.dgtd.ecole.ws.domain.json.Pere pere) {
        Pere = pere;
    }

    public com.dgtd.ecole.ws.domain.json.Mere getMere() {
        return Mere;
    }

    public void setMere(com.dgtd.ecole.ws.domain.json.Mere mere) {
        Mere = mere;
    }

    public Temoin getTemoin1() {
        return Temoin1;
    }

    public void setTemoin1(Temoin temoin1) {
        Temoin1 = temoin1;
    }

    public com.dgtd.ecole.ws.domain.json.Temoin2 getTemoin2() {
        return Temoin2;
    }

    public void setTemoin2(com.dgtd.ecole.ws.domain.json.Temoin2 temoin2) {
        Temoin2 = temoin2;
    }

    public List<EnfantJson> getEnfantList() {
        return EnfantList;
    }

    public void setEnfantList(List<EnfantJson> enfantList) {
        EnfantList = enfantList;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Date getDateSaisie() {
        return dateSaisie;
    }

    public void setDateSaisie(Date dateSaisie) {
        this.dateSaisie = dateSaisie;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getDateSignature() {
        return dateSignature;
    }

    public void setDateSignature(Date dateSignature) {
        this.dateSignature = dateSignature;
    }
}
