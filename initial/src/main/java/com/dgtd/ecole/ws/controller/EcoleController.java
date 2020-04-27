package com.dgtd.ecole.ws.controller;

import com.dgtd.common.exception.DataException;
import com.dgtd.common.util.Misc;
import com.dgtd.ecole.ws.payload.EcoleResponseContent;
import com.dgtd.ecole.ws.service.Generic.FileStorageService;
import com.dgtd.ecole.ws.service.Generic.TransformDataService;
import com.dgtd.security.service.FileService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;


@RestController
public class EcoleController {


    private static final Logger log = LoggerFactory.getLogger(EcoleController.class);


    private FileStorageService fileStorageService;

    private TransformDataService transformDataService;


    private FileService fileService;

    public EcoleController(FileStorageService fileStorageService, TransformDataService transformDataService,
                           FileService fileService) {
        this.fileStorageService = fileStorageService;
        this.transformDataService = transformDataService;
        this.fileService = fileService;
    }

    @ApiOperation(value = "Envoi des données de rencensement des enfants", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Intégration des données avec succès"),
            @ApiResponse(code = 401, message = "Vous devez vous authentifier pour accéder à la ressource"),
            @ApiResponse(code = 403, message = "Vous n'êtes pas autorisé à accéder à la ressource"),
            @ApiResponse(code = 404, message = "La ressource n'a pas été trouvée"),
            @ApiResponse(code = 409, message = "Problème d'enregistrement du fichier"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur")
    })
    @Secured(value = {"Administrateur", "Manager", "Superviseur"})
    @PostMapping(value = "/pushEcole", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<  ?> add(@RequestParam("file") MultipartFile multipartFile) throws Exception {

        Resource resource = null;
        EcoleResponseContent dataResponse = null;

        File file = null;
        String firstFolder = null;
        String errorFolder = fileStorageService.getBasePathPerType(FileStorageService.ERROR);
            try {
                file = Misc.convert(multipartFile);
                log.info("******* Chargement du fichier sur serveur");
                firstFolder = fileStorageService.createFolderNameFromZipFile(file);
                fileStorageService.storeZipFile(file,firstFolder);
                resource = fileStorageService.loadZipFileAsResource(file);

                if(resource!=null){

                    fileService.decrypt(resource.getFile());
                    log.debug("******* Enregistrement effectué avec succès, fichier disponible à l\'URL {}", resource.getURL());
                    log.debug("******* Décryptage avec Succès {} ", resource.getFile().toString());
                    dataResponse = transformDataService.handleDataToDB(resource);
                }else{
                     fileStorageService.getBasePathPerType(fileStorageService.ERROR);
                    throw new DataException("La ressource fournie n'est pas valide "+ file.getName());
                }

                if(dataResponse.isError()) {
                    firstFolder = fileStorageService.getBasePathPerType(FileStorageService.ERROR);
                    fileStorageService.updateStorageIfError(dataResponse,resource.getFile(),firstFolder);
                }

        }catch(Exception e){
            log.error("***** {}", e.getLocalizedMessage());

            if(resource !=null){
                fileStorageService.updateStorageIfError(dataResponse,resource.getFile(),errorFolder);
            }else{
                fileStorageService.updateStorageIfError(dataResponse,file,errorFolder);
            }
            throw e;
            }finally {

               if(resource!=null){ fileStorageService.deleteResource(resource);}
                Files.delete(Paths.get(file.getPath()));
            }

        log.info("******* Envoi de la réponse avec succès {}", dataResponse.getDossiers().toString());
        return ResponseEntity.ok(dataResponse);
    }




}
