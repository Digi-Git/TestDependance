package com.dgtd.controller;


import com.dgtd.common.error.ErrorService;
import com.dgtd.common.exception.DataException;
import com.dgtd.common.util.Misc;
import com.dgtd.ecole.ws.service.Generic.FileStorageService;
import com.dgtd.security.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.StreamSupport;

import static com.dgtd.common.util.Misc.convertAndStore;

@RestController
@RequestMapping("/tools")
public class ToolController {

    private final Logger log = LoggerFactory.getLogger(ToolController.class);

    @Value("${info.project.version}")
    private String version;

   // final private ProfilRepository profilRepository;

    //final private UtilisateurRepository utilisateurRepository;

    private final  FileStorageService fileStorageService;
    private final FileService fileService;
    private final ErrorService errorService;
    private final Environment environment;

    @Autowired
    public ToolController(
                            //ProfilRepository profilRepository, UtilisateurRepository utilisateurRepository,
                          FileStorageService fileStorageService, FileService fileService, ErrorService errorService,
                          Environment environment
                          ){
       // this.profilRepository = profilRepository;
       // this.utilisateurRepository = utilisateurRepository;

        this.fileStorageService = fileStorageService;
        this.fileService = fileService;
        this.errorService = errorService;
        this.environment = environment;

    }
/*
    //Fonction de test
    @GetMapping("/utilisateur/all")
    public List<Utilisateur> getProfils(){

        List<Utilisateur> utilisateurs = utilisateurRepository.findAllByIdentifiant("s.chesneau");

        return utilisateurRepository.findAll();
    }

 */

    @GetMapping("/echo")
    public ResponseEntity echo(){

        return ResponseEntity.ok("echo");
    }

    @GetMapping("/version")
    public ResponseEntity getCurrentVersion(){
        return ResponseEntity.ok(version);
    }

    /**
     * Fonction de décryptage d'un fichier
     * @param multipartFile File
     * @return fichier décrypté
     * @throws IOException en cas d'erreur de parsing
     */
    @PostMapping("/decrypt")
    public ResponseEntity decryptFile(@RequestParam("file") MultipartFile multipartFile) throws Exception {
        String tempFolder = fileStorageService.getBasePathPerType(FileStorageService.TEMP);
        File file = convertAndStore(tempFolder, multipartFile);
        String decrypted;
        try{
            if(Misc.checkIfZipFile(file)){
                throw new DataException("Vous essayez d'envoyer un fichier zip, veuillez envoyer le fichier concerné uniquement");
            }
            decrypted = fileService.decrypt(file);
        }catch (Exception e){

            throw  e;
        }finally {
            Files.delete(file.toPath());

        }
        return ResponseEntity.ok(decrypted);
    }



    @GetMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getErrors(){
        return ResponseEntity.ok(errorService.toString());
    }



    /**
     * @return paramétrages du web service
     */
    @RequestMapping(value = "/properties/environment", method = RequestMethod.GET)
    public ResponseEntity<Properties> getAppDetails() {

        Properties props = new Properties();
        MutablePropertySources propSrcs = ((AbstractEnvironment) environment).getPropertySources();
        StreamSupport.stream(propSrcs.spliterator(), false)
                .filter(ps -> ps instanceof EnumerablePropertySource)
                .map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
                .flatMap(Arrays::<String>stream)
                .forEach(propName -> props.setProperty(propName, environment.getProperty(propName)));


        return ResponseEntity.ok(props);

    }



}
