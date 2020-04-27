package com.dgtd.common.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * Bean des données génériques configurables par le client dans le fichier dédié
 * application-release.yml
 */
@Component
@ConfigurationProperties("digitech.app")
public class DigitechProperties {

    private String dataStore = "c:/temp";
    private String tempExport = "c:/temp";
    private int createSuperv;
    private String defautSupervPassword;
    private boolean decrypt;
    private int nbForRequest;
    private String applicationName;
    private String[] mailTo;
    private boolean update;

    //TODO passer le bean fileStorageProperties dans module common pour gestion centralisée du stockage
    private String excelLocalDir;

    // Regex for acceptable logins
    // Support les chars en maj/min, les chiffres chiffre et quelques caracteres spéciaux :
    // @ ! * . -
    public static final String LOGIN_REGEX = "^[_'.*!@A-Za-z0-9-]*$";

    // Messages d'erreur
    public static final String ERR_ARGUMENT_FORMDATA = "Argument absent ou invalide pour requête de type form data";
    public static final String ERR_CREATE_FOLDER = "Création d'un repertoire impossible sur le serveur";
    public static final String ERR_WRITE_FILE = "Erreur d'écriture du fichier sur le serveur";
    public static final String ERR_JSON_PARSE = "Erreur de parsing du fichier Json d'un centre";
    public static final String ERR_USER_EXIST = "Utilisateur déja existant (identifiant)";
    public static final String ERR_PROFIL_INEXIST = "Profil inexistant";

    public DigitechProperties(){
    }

    public int getCreateSuperv() {
        return createSuperv;
    }

    public void setCreateSuperv(int createSuperv) {
        this.createSuperv = createSuperv;
    }

    public String getDefautSupervPassword() {
        return defautSupervPassword;
    }

    public void setDefautSupervPassword(String defautSupervPassword) {
        this.defautSupervPassword = defautSupervPassword;
    }

    public boolean isDecrypt() {
        return decrypt;
    }

    public void setDecrypt(boolean decrypt) {
        this.decrypt = decrypt;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getExcelLocalDir() {
        return excelLocalDir;
    }

    public void setExcelLocalDir(String excelLocalDir) {
        this.excelLocalDir = excelLocalDir;
    }

    public String[] getMailTo() {
        return mailTo;
    }

    public void setMailTo(String[] mailTo) {
        this.mailTo = mailTo;
    }


    public String getDataStore() {
        return dataStore;
    }

    public void setDataStore(String dataStore) {
        this.dataStore = dataStore;
    }

    public String getTempExport() {
        return tempExport;
    }

    public void setTempExport(String tempExport) {
        this.tempExport = tempExport;
    }

    public int getNbForRequest() {
        return nbForRequest;
    }

    public void setNbForRequest(int nbForRequest) {
        this.nbForRequest = nbForRequest;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }
}
