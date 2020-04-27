package com.dgtd.rdc.entity;

import com.dgtd.rdc.localisation.entity.Localisation;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

@Entity
@Component
public class Enfant implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idPersonne;
    private Date dateExport;
    private String dateNaissance;
    private Integer export;
    private String genre;
    private String heureNaissance;
    private Integer localisationNaissanceId;
    private String naissanceAutre;
    private String nom;
    private String postnom;
    private String prenom;
    private String scolarise;
    private String evelinLink;

    private Dossier dossierByDossierId;
    private Localisation localisationByLocalisationNaissanceId;



    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getIdPersonne() {
        return idPersonne;
    }



    public void setIdPersonne(int idPersonne) {
        this.idPersonne = idPersonne;
    }




    @Basic
    @Column(name = "date_export")
    public Date getDateExport() {
        return dateExport;
    }

    public void setDateExport(Date dateExport) {
        this.dateExport = dateExport;
    }



    @Basic
    @Column(name = "date_naissance", length = 10)
    public String getDateNaissance() {
        return dateNaissance;
    }



    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }


    @Basic
    @Column(name = "export")
    public Integer getExport() {
        return export;
    }

    public void setExport(Integer export) {
        this.export = export;
    }



    @Basic
    @Column(name = "genre", length = 50)
    public String getGenre() {
        return genre;
    }


    public void setGenre(String genre) {
        this.genre = genre;
    }


    @Basic
    @Column(name = "heure_naissance",  length = 10)
    public String getHeureNaissance() {
        return heureNaissance;
    }


    public void setHeureNaissance(String heureNaissance) {
        this.heureNaissance = heureNaissance;
    }


    @Basic
    @Column(name = "localisation_naissance_id")
    public Integer getLocalisationNaissanceId() {
        return localisationNaissanceId;
    }


    public void setLocalisationNaissanceId(Integer localisationNaissanceId) {
        this.localisationNaissanceId = localisationNaissanceId;
    }

    @Basic
    @Column(name = "naissance_autre",  length = 50)
    public String getNaissanceAutre() {
        return naissanceAutre;
    }


    public void setNaissanceAutre(String naissanceAutre) {
        this.naissanceAutre = naissanceAutre;
    }


    @Basic
    @Column(name = "nom", length = 50)
    public String getNom() {
        return nom;
    }


    public void setNom(String nom) {
        this.nom = nom;
    }


    @Basic
    @Column(name = "postnom",  length = 50)
    public String getPostnom() {
        return postnom;
    }



    public void setPostnom(String postnom) {
        this.postnom = postnom;
    }



    @Basic
    @Column(name = "prenom",  length = 50)
    public String getPrenom() {
        return prenom;
    }



    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    @Basic
    @Column(name = "scolarise",  length = 10)
    public String getScolarise() {
        return scolarise;
    }

    public void setScolarise(String scolarise) {
        this.scolarise = scolarise;
    }

    @Basic
    @Column(name="evelinLink", length = 50)
    public String getEvelinLink() {
        return evelinLink;
    }

    public void setEvelinLink(String evelinLink) {
        this.evelinLink = evelinLink;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enfant enfant = (Enfant) o;
        return getIdPersonne() == enfant.getIdPersonne() &&
                Objects.equals(getDateExport(), enfant.getDateExport()) &&
                Objects.equals(getDateNaissance(), enfant.getDateNaissance()) &&
                Objects.equals(getExport(), enfant.getExport()) &&
                Objects.equals(getGenre(), enfant.getGenre()) &&
                Objects.equals(getHeureNaissance(), enfant.getHeureNaissance()) &&
                Objects.equals(getLocalisationNaissanceId(), enfant.getLocalisationNaissanceId()) &&
                Objects.equals(getNaissanceAutre(), enfant.getNaissanceAutre()) &&
                Objects.equals(getNom(), enfant.getNom()) &&
                Objects.equals(getPostnom(), enfant.getPostnom()) &&
                Objects.equals(getPrenom(), enfant.getPrenom()) &&
                Objects.equals(getScolarise(), enfant.getScolarise()) &&
                Objects.equals(getEvelinLink(), enfant.getEvelinLink()) &&

                Objects.equals(getLocalisationByLocalisationNaissanceId(), enfant.getLocalisationByLocalisationNaissanceId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdPersonne(), getDateExport(), getDateNaissance(), getExport(), getGenre(), getHeureNaissance(), getLocalisationNaissanceId(), getNaissanceAutre(), getNom(), getPostnom(), getPrenom(), getScolarise(), getEvelinLink(),  getLocalisationByLocalisationNaissanceId());
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dossier_id", referencedColumnName = "id", nullable = false)
    public Dossier getDossierByDossierId() {
        return dossierByDossierId;
    }
    public void setDossierByDossierId(Dossier dossierByDossierId) {
        this.dossierByDossierId = dossierByDossierId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "localisation_naissance_id", referencedColumnName = "id", insertable = false, updatable=false)
    public Localisation getLocalisationByLocalisationNaissanceId() {
        return localisationByLocalisationNaissanceId;
    }

    public void setLocalisationByLocalisationNaissanceId(Localisation localisationByLocalisationNaissanceId) {
        this.localisationByLocalisationNaissanceId = localisationByLocalisationNaissanceId;
    }






}
