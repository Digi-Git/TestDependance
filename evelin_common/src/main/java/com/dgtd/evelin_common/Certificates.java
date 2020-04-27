package com.dgtd.evelin_common;

import com.dgtd.common.error.ErrorObject;
import com.dgtd.common.error.ErrorService;
import com.dgtd.common.error.TypeError;
import com.dgtd.common.exception.WsBackOfficeException;
import com.dgtd.common.util.Misc;
import com.dgtd.evelin_common.config.EvelinProperties;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;




public abstract class Certificates<T>  implements CertificatesDAO<T>{

    private final Logger log = LoggerFactory.getLogger(Certificates.class);
    protected final EvelinProperties evelinProperties;
    protected final ErrorService errorService;

    protected int currentNbActes;
    protected String targetFolder;
    protected String dateFic;

    protected List<String> mainTemplate;

    protected List<String> certificates;


    public Certificates(EvelinProperties evelinProperties, ErrorService errorService)throws Exception{
        this.evelinProperties = evelinProperties;
        this.errorService = errorService;

        this.currentNbActes = 0;
        this.targetFolder = evelinProperties.getEcwExport();
        this.dateFic= "";

        this.mainTemplate = getMainTemplate();

        this.certificates = new ArrayList<String>();
    }


    public abstract List<T> getData();


    /**
     * Création des répertoire de stockage dédié aux ECW
     * Utilisation des propriétés paramétrables en configuration YML
     * Possibilité de stocker l'ECW dans un dossier avec le paramètre targetFolder si dans
     * la configuration YML folderMode est à 1
     * sinon le stockage est fait dans le path ecwExport
     *
     */
    @Override
    public void miskTargetFolders(){
        try {
            //Répertoire de dépôt de l'ECW
            Misc.mkdir(targetFolder);
            Misc.mkdir(evelinProperties.getEcwExport());
        } catch (Exception e) {
            log.error("***** Erreur lors de la création du répertoire "+ targetFolder + " ou" +evelinProperties.getEcwExport() );
            e.printStackTrace();
        }
    }


    /**
     * Finalisation du fichier complet xml avec l'ensemble des certificats
     * @param certificates concaténation de l'ensemble des certificats sous forme XML
     * @return un certificat avec entête finalisé
     */
    @Override
    public List<String> createGlobalXML(List<String> certificates){
        //Récupération du template XML principal
        try {
            mainTemplate = getMainTemplate();
        } catch (IOException e) {
            String error = "Pas d'accès au template principal dédié à la construction des ECW ";
            errorService.insertError(new ErrorObject(TypeError.DATA_INTEGRATION, error),false);
            throw new WsBackOfficeException(error  );
        }

        //Intégration du fichier d'origine dans le template
        List<String> globalCertif = new LinkedList<>();
        List<String> fichier;
        //Merge avec le template global
        fichier = new ArrayList<>();
        for(String mlig : mainTemplate){
            String lig = mlig;
            if(mlig.indexOf("@@CERTIFICATES@@")>0){
                fichier.addAll(certificates);
                globalCertif = fichier;
                lig = "";
            }

            if(!"".contentEquals(lig)){
                fichier.add(lig);
            }

        }
        return fichier;
    }


    protected String createTemporaryFile(List<String> fichier) throws IOException {
        //Fichier de sortie - temporaire
        String targetFile = FilenameUtils.concat(evelinProperties.getTempExport(), "certificates.xml");
        FileWriter writer = new FileWriter(targetFile);
        for(String str: fichier) {
            writer.write(str + System.lineSeparator());
        }
        writer.close();

        // Prob UTF-8 sur le serveur
        FileUtils.writeLines(new File(targetFile),"UTF-8", fichier);
        return targetFile;
    }



    protected void createZipAndClean(String targetFile) throws ZipException, IOException {
        // Création du zip
        String targetZipFile = FilenameUtils.concat(targetFolder,  "evelin_" + getDateFic()+ ".ecw");
        ZipFile zipFile = new ZipFile(targetZipFile);
        zipFile.addFile(new File(targetFile),new ZipParameters());
        //Misc.saveToZipFile (targetFile, zipFile);
        File folder = new File(targetFolder);
        File[] files = folder.listFiles();
        for(File f : files){
            if(!f.isDirectory()&& !f.getName().endsWith(".ecw")){
                // Misc.saveToZipFile(f.getPath(), zipFile);
                zipFile.addFile(f,new ZipParameters());
                FileUtils.forceDelete(f);
            }
        }
        log.info("***** Création de l'ECW à l'emplacement " + targetZipFile);

        if (evelinProperties.getDeleteCertif() == 1)
            FileUtils.forceDelete(new File(targetFile));
    }



    protected String createTargetFolder(){
        this.targetFolder = evelinProperties.getEcwExport();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        String dateFic = sdf.format(new Date());

        //Formatage pour les noms de fichier / répertoire
        if(evelinProperties.getFolderMode() == 1){
            this.targetFolder = FilenameUtils.concat(evelinProperties.getEcwExport(), dateFic);
        }
        return targetFolder;
    }


    /**
     * dateFic actualisée à chaque get
     * @return dateFic utilisée pour nommer le fichier ECW
     */
    public String getDateFic() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        dateFic = sdf.format(new Date());
        return dateFic;
    }


    /**
     * Fonction utile lors du stockage des ecw dans des dossiers spécifiques
     *
     */
    public String getTargetFolder() {
        return targetFolder;
    }


    public void setTargetFolder(String targetFolder){
        this.targetFolder = targetFolder;
    }
    public List<String> getMainTemplate() throws IOException {
        return FileUtils.readLines(new ClassPathResource("evelinTemplate/common/template_container.xml").getFile(), StandardCharsets.UTF_8);
    }


}
