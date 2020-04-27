package com.dgtd.ecole.ws.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * Configuration des dossiers de stockage en lien avec le fichier application-release.yml
 * Utilitaire complémentaire à la configuration pour manipuler les fichiers en fonction du dossier réceptionné
 */
@Component
@ConfigurationProperties(prefix = "digitech.storage")
public class FileStorageProperties {
    //CONFIGURATION
    //Nom du fichier json échangé avec tablette/BO PC ...
    private String filename;
    //Configuration du chemin principal d'enregistrement des dossiers
    private String uploadDir;
    private String successFolder;
    //Configuration du dossier d'enregistrement des dossiers avec erreur de traitement (stockage dans uploadDir)
    private String koFolder;
    //Configuration du chemin d'enregistrement des dossiers relatifs aux déclarations de naissance (hors Ecole)
    private String uploadDirNaissance;

    //DATA TEMPORAIRES
    //Données définies à partir du fichier Multipart réceptionné pour extraire les données et stocker le fichier
    //à l'endroit souhaité
    //Nom du fichier zip
    private String zipFolderName;
    //Chemin du fichier zip
    private String zipPath;
    //Nom de dossier à manipuler
    private String folderName ;
    //Chemin du dossier en cours de traitement
    private String path;
    //Chemin de stockage en fonction de la configuration et du dossier réceptionné
    private String targetFile;


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Configuration du chemin de stockage racine des dossiers d'audit dans le fichier
     * application-release.yml
     * @return String uploadDir - chemin de chargement des fichiers d'audit effectués
     */
    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }


    public String getSuccessFolder() {
        return successFolder;
    }

    public void setSuccessFolder(String successFolder) {
        this.successFolder = successFolder;
    }

    /**
     *
     * @return le chemin de stockage dédiés aux dossiers avec erreur de traitement
     */
    public String getKoFolder() {
        return koFolder;
    }

    public void setKoFolder(String koFolder) {
        this.koFolder = koFolder;
    }

    /**
     *
     * @return le chemin de stockage des dossiers de déclaration de naissance (hors écoles)
     */
    public String getUploadDirNaissance() {
        return uploadDirNaissance;
    }

    public void setUploadDirNaissance(String uploadDirNaissance) {
        this.uploadDirNaissance = uploadDirNaissance;
    }

    /**
     *
     * @return nom du fichier zip avec extension
     */
    public String getZipFolderName() {
        return zipFolderName;
    }

    public void setZipFolderName(String zipFolderName) {
        this.zipFolderName = zipFolderName;
    }

    /**
     *
     * @return chemin global de stockage du fichier zip nom du fichier zip
     */
    public String getZipPath() {
        return zipPath;
    }

    public void setZipPath(String zipPath) {
        this.zipPath = zipPath;
    }

    /**
     *
     * @return chemin "global" sans fichier final
     */
    public String getPath() {
        return path;
    }

    public void setPath(String path){
        this.path = path;
    }



    /**
     *
     * @return nom du dossier de stockage
     */
    public String getFolderName(){
        return this.folderName ;

    }


    public void setFolderName(String folderName){
        this.folderName = folderName;
    }



    /**
     *
     * @return chemin String
     */
    public String getTargetFile() {

        //this.targetFile = getPath().replace(destinationFolder, "");
        this.targetFile = getPath();
        String[] str = targetFile.split("\\\\");
        int i = str.length;
        targetFile = str[i-1];
        return targetFile;
    }



}
