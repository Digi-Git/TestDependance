package com.dgtd.rdc.entity;

import com.dgtd.rdc.localisation.entity.Localisation;
import com.dgtd.security.domain.entity.Utilisateur;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;

@Entity
@Component
public class Ecole implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private Timestamp dateCreationBo;
    private Timestamp dateExportBo;
    private Timestamp dateImportBo;
    private Timestamp dateIntegration;
    private String district;
    private Integer educationnelleId;
    private String estPrePrimaire;
    private String estPrimaire;
    private int lastversion;
    private Double latitude;
    private String localisationAutre;
    private Integer localisationId;
    private Double longitude;
    private Integer nbenfantsansacte;
    private String typeEcole;
    private String nom;
    private String quartier;
    private String unicName;
    private Integer utilisateurSuperId;
    private Collection<Dossier> dossiersById;
    private Educationnelle educationnelleByEducationnelleId;
    private Localisation localisationByLocalisationId;
    private Utilisateur utilisateurByUtilisateurSuperId;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "date_creation_bo")
    public Timestamp getDateCreationBo() {
        return dateCreationBo;
    }

    public void setDateCreationBo(Timestamp dateCreationBo) {
        this.dateCreationBo = dateCreationBo;
    }

    @Basic
    @Column(name = "date_export_bo")
    public Timestamp getDateExportBo() {
        return dateExportBo;
    }

    public void setDateExportBo(Timestamp dateExportBo) {
        this.dateExportBo = dateExportBo;
    }

    @Basic
    @Column(name = "date_import_bo")
    public Timestamp getDateImportBo() {
        return dateImportBo;
    }

    public void setDateImportBo(Timestamp dateImportBo) {
        this.dateImportBo = dateImportBo;
    }

    @Basic
    @Column(name = "date_integration")
    public Timestamp getDateIntegration() {
        return dateIntegration;
    }

    public void setDateIntegration(Timestamp dateIntegration) {
        this.dateIntegration = dateIntegration;
    }

    @Basic
    @Column(name = "district", length = 100)
    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @Basic
    @Column(name = "educationnelle_id")
    public Integer getEducationnelleId() {
        return educationnelleId;
    }

    public void setEducationnelleId(Integer educationnelleId) {
        this.educationnelleId = educationnelleId;
    }

    @Basic
    @Column(name = "est_pre_primaire",  length = 10)
    public String getEstPrePrimaire() {
        return estPrePrimaire;
    }

    public void setEstPrePrimaire(String estPrePrimaire) {
        this.estPrePrimaire = estPrePrimaire;
    }

    @Basic
    @Column(name = "est_primaire",  length = 10)
    public String getEstPrimaire() {
        return estPrimaire;
    }

    public void setEstPrimaire(String estPrimaire) {
        this.estPrimaire = estPrimaire;
    }



    @Basic
    @Column(name = "lastversion", nullable = false)
    public int getLastversion() {
        return lastversion;
    }

    public void setLastversion(int lastversion) {
        this.lastversion = lastversion;
    }

    @Basic
    @Column(name = "latitude")
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @Basic
    @Column(name = "localisation_autre",  length = 50)
    public String getLocalisationAutre() {
        return localisationAutre;
    }

    public void setLocalisationAutre(String localisationAutre) {
        this.localisationAutre = localisationAutre;
    }

    @Basic
    @Column(name = "localisation_id")
    public Integer getLocalisationId() {
        return localisationId;
    }

    public void setLocalisationId(Integer localisationId) {
        this.localisationId = localisationId;
    }

    @Basic
    @Column(name = "longitude",  precision = 0)
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Basic
    @Column(name = "nbenfantsansacte")
    public Integer getNbenfantsansacte() {
        return nbenfantsansacte;
    }

    public void setNbenfantsansacte(Integer nbenfantsansacte) {
        this.nbenfantsansacte = nbenfantsansacte;
    }

    @Basic
    @Column(name = "nom", length = 36)
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }



    @Basic
    @Column(name = "quartier", length = 100)
    public String getQuartier() {
        return quartier;
    }

    public void setQuartier(String quartier) {
        this.quartier = quartier;
    }

    @Basic
    @Column(name = "unic_name",  length = 36)
    public String getUnicName() {
        return unicName;
    }

    public void setUnicName(String unicName) {
        this.unicName = unicName;
    }

    @Basic
    @Column(name = "utilisateur_super_id")
    public Integer getUtilisateurSuperId() {
        return utilisateurSuperId;
    }
    public void setUtilisateurSuperId(Integer utilisateurSuperId) {
        this.utilisateurSuperId = utilisateurSuperId;
    }

    public String getTypeEcole() { return typeEcole; }
    public void setTypeEcole(String typeEcole) {
        this.typeEcole = typeEcole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ecole ecole = (Ecole) o;
        return id == ecole.id &&
                lastversion == ecole.lastversion &&
                Objects.equals(dateCreationBo, ecole.dateCreationBo) &&
                Objects.equals(dateExportBo, ecole.dateExportBo) &&
                Objects.equals(dateImportBo, ecole.dateImportBo) &&
                Objects.equals(dateIntegration, ecole.dateIntegration) &&
                Objects.equals(district, ecole.district) &&
                Objects.equals(educationnelleId, ecole.educationnelleId) &&
                Objects.equals(estPrePrimaire, ecole.estPrePrimaire) &&
                Objects.equals(estPrimaire, ecole.estPrimaire) &&
                Objects.equals(latitude, ecole.latitude) &&
                Objects.equals(localisationAutre, ecole.localisationAutre) &&
                Objects.equals(localisationId, ecole.localisationId) &&
                Objects.equals(longitude, ecole.longitude) &&
                Objects.equals(nbenfantsansacte, ecole.nbenfantsansacte) &&
                Objects.equals(nom, ecole.nom) &&
                Objects.equals(quartier, ecole.quartier) &&
                Objects.equals(unicName, ecole.unicName) &&
                Objects.equals(utilisateurSuperId, ecole.utilisateurSuperId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateCreationBo, dateExportBo, dateImportBo, dateIntegration, district, educationnelleId, estPrePrimaire, estPrimaire, lastversion, latitude, localisationAutre, localisationId, longitude, nbenfantsansacte, nom,  quartier, unicName, utilisateurSuperId);
    }

    @OneToMany(mappedBy = "ecoleByEcoleId", fetch = FetchType.LAZY)
    public Collection<Dossier> getDossiersById() {
        return dossiersById;
    }

    public void setDossiersById(Collection<Dossier> dossiersById) {
        this.dossiersById = dossiersById;
    }

    @ManyToOne
    @JoinColumn(name = "educationnelle_id", referencedColumnName = "id", insertable = false, updatable=false)
    public Educationnelle getEducationnelleByEducationnelleId() {
        return educationnelleByEducationnelleId;
    }

    public void setEducationnelleByEducationnelleId(Educationnelle educationnelleByEducationnelleId) {
        this.educationnelleByEducationnelleId = educationnelleByEducationnelleId;
    }

    @ManyToOne
    @JoinColumn(name = "localisation_id", referencedColumnName = "id", insertable = false, updatable = false)
    public Localisation getLocalisationByLocalisationId() {
        return localisationByLocalisationId;
    }

    public void setLocalisationByLocalisationId(Localisation localisationByLocalisationId) {
        this.localisationByLocalisationId = localisationByLocalisationId;
    }

    @ManyToOne
    @JoinColumn(name = "utilisateur_super_id", referencedColumnName = "id", insertable = false, updatable = false)
    public Utilisateur getUtilisateurByUtilisateurSuperId() {
        return utilisateurByUtilisateurSuperId;
    }

    public void setUtilisateurByUtilisateurSuperId(Utilisateur utilisateurByUtilisateurSuperId) {
        this.utilisateurByUtilisateurSuperId = utilisateurByUtilisateurSuperId;
    }


}
