package com.dgtd.rdc.entity;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Collection;
import java.util.Objects;

@Entity
@Component
public class Dossier implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String commentaire;
    private Date dateCreation;
    private Date dateSignature;
    private int ecoleId;
    private String noDossier;
    private String nomPrenomUtilisateurAgent;
    private String nom_dossier_source;
    private Integer export;
    private String evelinLink;
    private Date dateExport;
    private Ecole ecoleByEcoleId;
    private Collection<Enfant> enfantsById;
    private Collection<Personne> personnesById;
    private Timestamp dateExportBo;
    private Timestamp dateImportBo;
    private Timestamp dateIntegration;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "commentaire")
    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    @Basic
    @Column(name = "date_creation")
    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {

        this.dateCreation = dateCreation;
    }

    @Basic
    @Column(name = "date_signature")
    public Date getDateSignature() {
        return dateSignature;
    }

    public void setDateSignature(Date dateSignature) {
        this.dateSignature = dateSignature;
    }

    @Basic
    @Column(name = "ecole_id", nullable = false, updatable = false)
    public int getEcoleId() {
        return ecoleId;
    }

    public void setEcoleId(int ecoleId) {
        this.ecoleId = ecoleId;
    }

    @Basic
    @Column(name = "no_dossier", length = 15)
    public String getNoDossier() {
        return noDossier;
    }

    public void setNoDossier(String noDossier) {
        this.noDossier = noDossier;
    }

    @Basic
    @Column(name = "nom_prenom_utilisateur_agent")
    public String getNomPrenomUtilisateurAgent() {
        return nomPrenomUtilisateurAgent;
    }

    public void setNomPrenomUtilisateurAgent(String nomPrenomUtilisateurAgent) {
        this.nomPrenomUtilisateurAgent = nomPrenomUtilisateurAgent;
    }

    @Basic
    @Column(name="nom_dossier_source", length = 100)
    public String getNom_dossier_source() {
        return nom_dossier_source;
    }

    public void setNom_dossier_source(String nom_dossier_source) {
        this.nom_dossier_source = nom_dossier_source;
    }

    @Basic
    @Column(name="date_export_bo", length = 100)
    public Timestamp getDateExportBo() {
        return dateExportBo;
    }

    public void setDateExportBo(Timestamp dateExportBo) {
        this.dateExportBo = dateExportBo;
    }

    @Basic
    @Column(name="date_import_bo", length = 100)
    public Timestamp getDateImportBo() {
        return dateImportBo;
    }

    public void setDateImportBo(Timestamp dateImportBo) {
        this.dateImportBo = dateImportBo;
    }

    @Basic
    @Column(name="date_integration", length = 100)
    public Timestamp getDateIntegration() {
        return dateIntegration;
    }

    public void setDateIntegration(Timestamp dateIntegration) {
        this.dateIntegration = dateIntegration;
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
    @Column(name = "export")
    public Integer getExport() {
        return export;
    }

    public void setExport(Integer export) {
        this.export = export;
    }

    @Basic
    @Column(name="evelin_link", length = 50)
    public String getEvelinLink() {
        return evelinLink;
    }

    public void setEvelinLink(String evelinLink) {
        this.evelinLink = evelinLink;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dossier)) return false;
        Dossier dossier = (Dossier) o;
        return getId() == dossier.getId() &&
                getEcoleId() == dossier.getEcoleId() &&
                Objects.equals(getCommentaire(), dossier.getCommentaire()) &&
                Objects.equals(getDateCreation(), dossier.getDateCreation()) &&
                Objects.equals(getDateSignature(), dossier.getDateSignature()) &&
                Objects.equals(getNoDossier(), dossier.getNoDossier()) &&
                Objects.equals(getNomPrenomUtilisateurAgent(), dossier.getNomPrenomUtilisateurAgent()) &&
                Objects.equals(getEcoleByEcoleId(), dossier.getEcoleByEcoleId()) &&
                Objects.equals(getEnfantsById(), dossier.getEnfantsById()) &&
                Objects.equals(getPersonnesById(), dossier.getPersonnesById());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCommentaire(), getDateCreation(), getDateSignature(), getEcoleId(), getNoDossier(), getNomPrenomUtilisateurAgent(), getEcoleByEcoleId(), getEnfantsById(), getPersonnesById());
    }

    @ManyToOne
    @JoinColumn(name = "ecole_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    public Ecole getEcoleByEcoleId() {
        return ecoleByEcoleId;
    }
    public void setEcoleByEcoleId(Ecole ecoleByEcoleId) {
        this.ecoleByEcoleId = ecoleByEcoleId;
    }

    @OneToMany( mappedBy = "dossierByDossierId", cascade = CascadeType.REMOVE, fetch= FetchType.LAZY)
    public Collection<Enfant> getEnfantsById() {
        return enfantsById;
    }
    public void setEnfantsById(Collection<Enfant> enfantsById) {
        this.enfantsById = enfantsById;
    }

    @OneToMany( mappedBy = "dossierByDossierId",cascade = CascadeType.REMOVE, fetch= FetchType.LAZY)
    public Collection<Personne> getPersonnesById() {
        return personnesById;
    }
    public void setPersonnesById(Collection<Personne> personnesById) {
        this.personnesById = personnesById;
    }
}
