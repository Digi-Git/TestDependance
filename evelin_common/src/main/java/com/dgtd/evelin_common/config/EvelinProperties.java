package com.dgtd.evelin_common.config;

import com.dgtd.common.type.TypeActe;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "digitech.evelin")
public class EvelinProperties {

    //Répertoire de dépôt des ECW
    private String ecwExport;
    private String tempExport;
    //A 1 si le fichier xml doit être supprimé du répertoire temporaire (tempExport)
    private int deleteCertif;
    // 0 si synchro Evelin/cityweb desactivée, sinon 1.
    private int enableSynchroEvelin;
    private int enableSynchroEvelinFromDir;
    // Pour interface Evelin : si 1, un sous repertoire nommé avec la date est crée dans ecwExport
    private int folderMode;
    // Frequence de lancement des mises à jour.
    private int frequence;
    private int nbSynchroEvelin;
    //Tag pour les metas experimentation des naissances
    private String naiTag;
    //Tag pour les metas experimentation des décès
    private String decesTag;
    private String ordoTag;
    // Id dans la table des localisations
    private int idEtranger;
    // Texte préformaté pour le pays
    private String textePays;
    private String texteNonDefini;
    private int ecwExportUpdate;
    private String evelinSynchroDirectory;
    private int maxExport;
    private TypeActe typeActe;


    public String getEcwExport() {
        return ecwExport;
    }

    public void setEcwExport(String ecwExport) {
        this.ecwExport = ecwExport;
    }

    public String getTempExport() {
        return tempExport;
    }

    public void setTempExport(String tempExport) {
        this.tempExport = tempExport;
    }

    public int getDeleteCertif() {
        return deleteCertif;
    }

    public void setDeleteCertif(int deleteCertif) {
        this.deleteCertif = deleteCertif;
    }

    public int getEnableSynchroEvelin() {
        return enableSynchroEvelin;
    }

    public void setEnableSynchroEvelin(int enableSynchroEvelin) {
        this.enableSynchroEvelin = enableSynchroEvelin;
    }

    public int getEnableSynchroEvelinFromDir() {
        return enableSynchroEvelinFromDir;
    }

    public void setEnableSynchroEvelinFromDir(int enableSynchroEvelinFromDir) {
        this.enableSynchroEvelinFromDir = enableSynchroEvelinFromDir;
    }

    public int getFolderMode() {
        return folderMode;
    }

    public void setFolderMode(int folderMode) {
        this.folderMode = folderMode;
    }

    public int getFrequence() {
        return frequence;
    }

    public void setFrequence(int frequence) {
        this.frequence = frequence;
    }

    public int getNbSynchroEvelin() {
        return nbSynchroEvelin;
    }

    public void setNbSynchroEvelin(int nbSynchroEvelin) {
        this.nbSynchroEvelin = nbSynchroEvelin;
    }

    public String getNaiTag() {
        return naiTag;
    }

    public void setNaiTag(String naiTag) {
        this.naiTag = naiTag;
    }

    public String getDecesTag() {
        return decesTag;
    }

    public void setDecesTag(String decesTag) {
        this.decesTag = decesTag;
    }

    public int getIdEtranger() {
        return idEtranger;
    }

    public void setIdEtranger(int idEtranger) {
        this.idEtranger = idEtranger;
    }

    public String getTextePays() {
        return textePays;
    }

    public void setTextePays(String textePays) {
        this.textePays = textePays;
    }

    public String getTexteNonDefini() {
        return texteNonDefini;
    }

    public void setTexteNonDefini(String texteNonDefini) {
        this.texteNonDefini = texteNonDefini;
    }

    public int getEcwExportUpdate() {
        return ecwExportUpdate;
    }

    public void setEcwExportUpdate(int ecwExportUpdate) {
        this.ecwExportUpdate = ecwExportUpdate;
    }

    public String getEvelinSynchroDirectory() {
        return evelinSynchroDirectory;
    }

    public void setEvelinSynchroDirectory(String evelinSynchroDirectory) {
        this.evelinSynchroDirectory = evelinSynchroDirectory;
    }

    public int getMaxExport() {
        return maxExport;
    }

    public void setMaxExport(int maxExport) {
        this.maxExport = maxExport;
    }

    public String getOrdoTag() {
        return ordoTag;
    }

    public void setOrdoTag(String ordoTag) {
        this.ordoTag = ordoTag;
    }

    public TypeActe getTypeActe() {
        return typeActe;
    }

    public void setTypeActe(TypeActe typeActe) {
        this.typeActe = typeActe;
    }
}
