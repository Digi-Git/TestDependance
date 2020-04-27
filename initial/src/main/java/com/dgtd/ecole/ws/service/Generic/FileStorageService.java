package com.dgtd.ecole.ws.service.Generic;


import com.dgtd.common.error.ErrorObject;
import com.dgtd.common.error.ErrorService;
import com.dgtd.common.error.TypeError;
import com.dgtd.common.exception.FileStorageException;
import com.dgtd.common.payload.ResponseType;
import com.dgtd.common.util.ErrorMailService;
import com.dgtd.ecole.ws.config.Constants;
import com.dgtd.ecole.ws.config.FileStorageProperties;
import com.dgtd.ecole.ws.payload.EcoleResponseContent;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class FileStorageService {

    public final Path fileStorageLocation;

    public static final String NAISSANCE = "NAISSANCE";

    public static final String ERROR = "ERROR";

    public static final String SUCCESS = "SUCCESS";

    public static final String INDISPONIBLE = "Non disponible";

    public static final String TEMP = "Temp";

    private FileStorageProperties fileStorageProperties;
    private ErrorService errorService;


    private final Logger log = LoggerFactory.getLogger(FileStorageService.class);



    public FileStorageService(FileStorageProperties fileStorageProperties, ErrorService errorService) {

        this.errorService = errorService;
        this.fileStorageProperties = fileStorageProperties;
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Impossible de créer le dossier ou les fichiers doivent être stockés ", ex);
        }

    }


    /**
     * Création d'un chemin de stockage concaténé avec la propriété générique de
     * paramétrage digitech.storage.upload-dir
     * @see FileStorageProperties
     * @param name nom de dossier
     * @return Nouveau chemin de stockage (sous dossier)
     */
    private Path createPathFromUploadDir(String name){

        Path path;

        try {
            path = Paths.get(fileStorageProperties.getUploadDir()+"/"+ name);
            Files.createDirectories(path);
        } catch (Exception ex) {
            log.error("Impossible de créer le dossier de stockage ", ex);
            throw new FileStorageException("Impossible de créer le dossier de stockage ", ex);
        }


        return path;

    }

    /**
     * Crée le nom du dossier de stockage en fonction du nom du dossier zip
     * @param file Dossier zip
     * @return nom du dossier sans l'extension .zip
     */
    public String createFolderNameFromZipFile(File file){
        String folderName = file.getName().replace(".zip","");
        fileStorageProperties.setFolderName(folderName);
        return folderName;
    }


    /**
     *
     * Création d'un dossier sur disque
     * @param path chemin complet
     *
     */
    public void mkdir(String path){
        // creation ou replacement du dossier de destination
        try {
            FileUtils.forceMkdir(new File(path));
        } catch (Exception e) {
            throw new FileStorageException("Problème lors de la création du dossier "
                    + e.getLocalizedMessage());

        }
    }
    public void unzip(File zip, String destinationPath)throws ZipException{
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(zip);
            zipFile.extractAll(destinationPath);
        } catch (ZipException ze) {
            String error = "Problème lors de l'extraction du fichier zip, "+ zipFile.getFile().getPath()+" données non exploitables "
                    + ze.getLocalizedMessage();
            log.error("***** "+ error);
            errorService.insertError(new ErrorObject(TypeError.IO, error),true);
            throw  ze;

        }
    }
    /**
     * fonction de dezippage
     * @param zip fichier zip
     * @param  path chemin d'emplacement de destination
     */
    public void unzip (ZipFile zip, String path)  {

        //unzip the file
        try{
            zip.extractAll(path);
        } catch (ZipException e) {
            log.debug("Problème lors de l'extraction du zip");
            e.printStackTrace();
        }

    }
    /**
     * Convertit le fichier en resource URL stocké sur le basePath
     * @param file dossier
     * @return Resource URL contenant le fichier
     */
    public Resource loadZipFileAsResource(File file) throws MalformedURLException, FileNotFoundException {

        String basePath = fileStorageProperties.getPath();
        String pathWithFile = basePath+ "\\"+ Constants.NOM_DATA_JSON;
        Resource resource;
        Path uploadFilesPath;
        try {

            uploadFilesPath = fileStorageLocation.resolve(pathWithFile).normalize();
            resource = new UrlResource(uploadFilesPath.toUri());

            if (!resource.exists()) {
                throw new FileNotFoundException(" " + Constants.NOM_DATA_JSON
                        + " non retrouvé, vous essayez d'importer le dossier "
                        + basePath);
                      // + pathWithFile.substring(0,pathWithFile.length() - 11));
            }

        } catch (MalformedURLException ex) {
            throw new com.dgtd.common.exception.FileNotFoundException("Fichier " + Constants.NOM_DATA_JSON +
                    " indisponible ", ex);
        }

        return resource;

    }

    /**
     * Récupère et stocke le fichier zippé dans l'emplacement prévu dans la configuration (digitech.uploadDir)
     * Stocke également un dossier dézippé afin de le décrypter et l'enregistrer en objet java
     * @param file dossier à stocker
     */
    public void storeZipFile(File file, String folder) throws java.io.FileNotFoundException {
        InputStream is;
        try {
             is = new FileInputStream(file);
        } catch (java.io.FileNotFoundException e) {
            throw e;
        }

        //String basePathFromFile = fileStorageProperties.getBasePathWithFile(file);
        String uploadDir = fileStorageProperties.getUploadDir()+ fileStorageProperties.getSuccessFolder();
        //Stockage du dossier avec la date au détail de la milliseconde
        DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
        Date today = Calendar.getInstance().getTime();
        String date= dateFormat .format(today);

        //
        String path = uploadDir+"/"+folder+date;
        fileStorageProperties.setPath(path);


        mkdir(path);

        File zip ;
        String zipPath = path+ ".zip";

        try {
            //Si on ne souhaite pas conserver le fichier zip
            //zip = File.createTempFile(UUID.randomUUID().toString(),"temp");
            zip = new File(path + ".zip");
            FileOutputStream o = new FileOutputStream(zip);
            IOUtils.copy(is,o);
            o.close();
            is.close();

        } catch (IOException e) {
            throw new FileStorageException("Probleme lors de la création du fichier");
        }
        //unzip the file
        try{
            ZipFile zipFile = new ZipFile(zip);
            fileStorageProperties.setZipPath(zipPath);
            zipFile.extractAll(path);
        }catch (ZipException ze){
            log.debug("Problème lors de l'extraction du fichier zip " + ze.getLocalizedMessage());

        }

    }

    public String getBasePathPerType(String type){
        switch (type){
            case (SUCCESS)      : return  fileStorageProperties.getSuccessFolder();
            case (ERROR)        : return  fileStorageProperties.getKoFolder();
        }
        return "/Autre";
    }

    /**
     * Mise à jour de l'URL de stockage d'un fichier zip avec un nom de dossier différent
     * Copie du fichier zip au nouvel emplacement et suppression de l'ancien
     * @param file URL ou se trouve le dossier de stockage
     * @param newFolder nom du dossier à modifier
     * @throws IOException en cas de ressource non disponible
     */
    public void updateResourceLocation(File file, String newFolder)throws IOException{
        file.getPath().replace("/datas.json", "");
        //Récupération du nom de dossier d'origine
        String oldFolder = file.getParent();
        ZipFile zipOld = null;
        ZipFile zip;

        //Création d'un nouvel URL avec un dossier
        Path path = createPathFromUploadDir(newFolder);
        String errorFile = path.toString()+ "/"+ fileStorageProperties.getFolderName();

        if(oldFolder== null){
            oldFolder = file.getPath();
            try{
                zip= new ZipFile(errorFile + ".zip");
                zip.addFile(file, new ZipParameters());

            }catch(Exception e){
                log.error("***** Erreur lors de la copie du fichier zip à l'emplacement "+ oldFolder );

            }
        }


        try{
            //Création d'un nouveau dossier zip
            zip = new ZipFile(errorFile +".zip");
            //Ajout du zip réceptionné par le BO
           zip.addFolder(oldFolder , new ZipParameters(){});
           // zip.addFolder(file,new ZipParameters());


        }catch (ZipException ze){
            log.error("***** Erreur lors de la copie du fichier zip à l'emplacement "+ oldFolder );
            ze.getLocalizedMessage();

        }

        //Suppression du fichier zip stocké à l'ancien emplacement
        deleteZipFile(fileStorageProperties.getZipPath());

    }

    /**
     * Convertit un fichier en ressource URL avec la possibilité de rajouter un sous dossier
     * supplémentaire au basePath défini au niveau de l'application
     * @param foldername dossier de stockage après le basePath
     * @param filename nom du fichier à convertir
     * @return UrlResource resource file avec URL
     * @throws MalformedURLException problème de chemin, droit d'écriture
     */
    public Resource createFileAsResource(String foldername, String filename) throws MalformedURLException {
        Path newStorageLocation = Paths.get(fileStorageLocation +"\\"+ foldername);
        if(foldername != null){
            try {
                Files.createDirectories(newStorageLocation);
            } catch (IOException ex) {
                log.debug("\"Impossible de créer le dossier à l'endroit spécifié " + newStorageLocation);
                throw new FileStorageException("Impossible de créer le dossier à l'endroit spécifié "
                        + newStorageLocation, ex);
            }
        }

        String targetFile = newStorageLocation.toString()+"\\"+filename;
        Path filePath = this.fileStorageLocation.resolve(targetFile).normalize();

        return new UrlResource(filePath.toUri());
    }



    /**
     * fonction de dezippage
     * @param zip
     */
    public void unzip (Resource zip) throws IOException {
        InputStream file = zip.getInputStream();
        String basePath = zip.getURI().getPath();
        //unzip the file
        try{
            ZipFile zipFile = new ZipFile(String.valueOf(file));
            zipFile.extractAll(basePath);

        } catch (ZipException e) {
            log.debug("Problème lors de l'extraction du zip");
            e.printStackTrace();
        }

    }

    public void updateStorageIfError(EcoleResponseContent ecoleResponseContent, File res, String folderWithErrors) throws IOException {

        updateResourceLocation(res,folderWithErrors);

    if(ecoleResponseContent !=null){
        Map<String, ResponseType> map = ecoleResponseContent.getDossiers();
        if(!map.isEmpty()){
            for(Map.Entry<String, ResponseType> entry: map.entrySet()){

                if(entry.getValue().equals(ResponseType.REFUSE)){
                    log.error("***** Dossier n° "+ entry.getKey()+" refusé car déjà enregistré en base de données " );
                }
            }
        }
    }



    }



    /**
     * Suppression du fichier décrypté et du dossier
     * @param resource pour vérification de sa présence
     *                 pas utilisé directement car récupération des PATHS
     *                 avec FileStorageProperties
     * @see FileStorageProperties
     */
    public void deleteResource(Resource resource){

        String basePath = fileStorageProperties.getPath();

        Path path = null;
        try {
            path = Paths.get(resource.getURI());
        } catch (IOException e) {
            log.error("***** Erreur : ressource inexistante");
        }
        try {
            Files.delete(Objects.<Path>requireNonNull(path));
        } catch (IOException e) {
             log.error("***** Erreur : Problème de suppression du fichier");
        }

        try {
            Files.delete(Paths.get(basePath));
        } catch (IOException e) {
            log.error("***** Problème de suppression du dossier" + e.getMessage());
        }
    }


    /**
     * Suppression d'un fichier zip
     * @param zipPath emplacement du fichier zip
     *
     */
    public void deleteZipFile(String zipPath){
        try{
            File fileToDelete = new File(zipPath);
            fileToDelete.delete();
        }catch(Exception e){
            log.error("***** Erreur lors de la suppression du fichier zip à la racine du dossier de stockage " +
                    fileStorageProperties.getUploadDir() + " " + e);
        }

    }


    /**
     * Suppression du fichier décrypté
     * @param folder pour vérification de sa présence
     *                 pas utilisé directement car récupération des PATHS
     *                 avec FileStorageProperties
     * @see FileStorageProperties
     * @return boolean si la suppression s'est bien passée ou non
     */
    public boolean deleteFolder(File folder) throws IOException {

        //Récupération du chemin du dossier source à supprimer
//        String basePath = fileStorageProperties.getPath().replace(filename,"");
        if(folder.getPath().contains(".zip")){
            try{
                Files.delete(Paths.get(folder.getPath()));
            }catch(Exception e){
                if(folder.exists()){
                    log.error("***** Problème de suppression du fichier zip "+ folder.getPath());
                    return false;
                }
                String folderPath = folder.getPath().replace(".zip", "");
                deleteFolder(new File(folderPath));
            }

        }else{
            File[] files = folder.listFiles();
            if(files!=null){
                if(files.length>0){
                    //Récupération de l'ensemble des fichiers compris dans le dossier
                    for(File fileEntry : files){

                        //Suppression d'éventuels dossiers
                        if(fileEntry.isDirectory()){
                            deleteFolder(fileEntry);

                            //Suppression du dossier une fois les fichiers contenus supprimés
                            // Files.delete(Paths.get(fileEntry.getPath()));

                        }else{
                            Files.delete(Paths.get(fileEntry.getPath()));
                        }
                    }
                }
            }


            //Tentative de suppression du dossier source, si exception la suppression de l'ensemble des fichiers
            //n'est pas terminée
            try {
                Files.delete(Paths.get(folder.getPath()));
                // Files.delete(Paths.get(basePath ));
            } catch (IOException e) {

                log.error("***** Erreur : Problème de suppression du dossier " + e.getMessage());
                return false;
            }
        }


        return true;
    }



}


