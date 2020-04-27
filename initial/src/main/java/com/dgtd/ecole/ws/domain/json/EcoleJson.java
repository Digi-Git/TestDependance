package com.dgtd.ecole.ws.domain.json;

import com.google.gson.annotations.SerializedName;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Component
public class EcoleJson implements Serializable {

    private Timestamp dateImportBO;
    private Timestamp dateExportBO;
    private Timestamp dateCreationBO;
    private Date date_creation_tournee;
    private Date date_fin_tournee;
    private String superviseur;
    private int TypeFormulaireId;
    private int localisation_id;
    private String localisation_autre;
    @SerializedName("idbo")
    private int id;
    private String nom;
    private double latitude;
    private double longitude;
    private String district;
    private String quartier;
    private String Province;
    private String SousDivision;
    private List< Document > documentList;
    private boolean TransferChecked;
    private boolean TransferError;
    private boolean TransferDone;
    private String ecolePrePrimaire;
    private String ecolePrimaire;
    private int nbEnfantSansActe;
    private int codeProvinceEducationnelleSousDivision;

    public EcoleJson(){
        setDistrict("");
        setQuartier("");
        setLocalisation_autre("");

    }


    public String isEcolePrePrimaire() {

        if(ecolePrePrimaire.equals("true") && ecolePrimaire.equals("true")){
            ecolePrePrimaire = "Oui";
            ecolePrimaire = "Non";
        }else if(ecolePrePrimaire.equals("true") && ecolePrimaire.equals("false")){
            ecolePrePrimaire = "Non";
            ecolePrimaire = "Oui";
        }else if(ecolePrePrimaire.equals("false") && ecolePrimaire.equals("true")){
            ecolePrePrimaire = "Oui";
            ecolePrimaire = "Oui";
        }

        setEcolePrimaire(ecolePrimaire);
        return ecolePrePrimaire;
    }

    public void setEcolePrePrimaire(String ecolePrePrimaire) {
        this.ecolePrePrimaire = ecolePrePrimaire;
    }

    public String isEcolePrimaire() {

        return ecolePrimaire;
    }

    public void setEcolePrimaire(String ecolePrimaire) {
        this.ecolePrimaire = ecolePrimaire;
    }

    public Timestamp getDateImportBO() {
        return dateImportBO;
    }

    public void setDateImportBO(Timestamp dateImportBO) {
        this.dateImportBO = dateImportBO;
    }

    public Timestamp getDateExportBO() {
        return dateExportBO;
    }

    public void setDateExportBO(Timestamp dateExportBO) {
        this.dateExportBO = dateExportBO;
    }

    public Timestamp getDateCreationBO() {
        return dateCreationBO;
    }

    public void setDateCreationBO(Timestamp dateCreationBO) {
        this.dateCreationBO = dateCreationBO;
    }

    public Date getDate_creation_tournee() {
        return date_creation_tournee;
    }

    public void setDate_creation_tournee(Date date_creation_tournee) {
        this.date_creation_tournee = date_creation_tournee;
    }

    public Date getDate_fin_tournee() {
        return date_fin_tournee;
    }

    public void setDate_fin_tournee(Date date_fin_tournee) {
        this.date_fin_tournee = date_fin_tournee;
    }

    public String getSuperviseur() {
        return superviseur;
    }

    public void setSuperviseur(String superviseur) {
        this.superviseur = superviseur;
    }

    public int getTypeFormulaireId() {
        return TypeFormulaireId;
    }

    public void setTypeFormulaireId(int typeFormulaireId) {
        TypeFormulaireId = typeFormulaireId;
    }

    public int getLocalisation_id() {
        return localisation_id;
    }

    public void setLocalisation_id(int localisation_id) {
        this.localisation_id = localisation_id;
    }

    public String getLocalisation_autre() {
        return localisation_autre;
    }

    public void setLocalisation_autre(String localisation_autre) {
        this.localisation_autre = localisation_autre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getQuartier() {
        return quartier;
    }

    public void setQuartier(String quartier) {
        this.quartier = quartier;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        Province = province;
    }

    public String getSousDivision() {
        return SousDivision;
    }

    public void setSousDivision(String sousDivision) {
        SousDivision = sousDivision;
    }

    public List<Document> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(List<Document> documentList) {
        this.documentList = documentList;
    }

    public boolean isTransferChecked() {
        return TransferChecked;
    }

    public void setTransferChecked(boolean transferChecked) {
        TransferChecked = transferChecked;
    }

    public boolean isTransferError() {
        return TransferError;
    }

    public void setTransferError(boolean transferError) {
        TransferError = transferError;
    }

    public boolean isTransferDone() {
        return TransferDone;
    }

    public void setTransferDone(boolean transferDone) {
        TransferDone = transferDone;
    }

    public String getEcolePrePrimaire() {
        return ecolePrePrimaire;
    }

    public String getEcolePrimaire() {
        return ecolePrimaire;
    }

    public int getNbEnfantSansActe() {
        return nbEnfantSansActe;
    }

    public void setNbEnfantSansActe(int nbEnfantSansActe) {
        this.nbEnfantSansActe = nbEnfantSansActe;
    }

    public int getCodeProvinceEducationnelleSousDivision() {
        return codeProvinceEducationnelleSousDivision;
    }

    public void setCodeProvinceEducationnelleSousDivision(int codeProvinceEducationnelleSousDivision) {
        this.codeProvinceEducationnelleSousDivision = codeProvinceEducationnelleSousDivision;
    }
}
