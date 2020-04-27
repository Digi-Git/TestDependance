package com.dgtd.ecole.ws.service.DBentities;

import com.dgtd.common.dicoutil.DicoUtils;
import com.dgtd.common.dicoutil.ListValeurItem;
import com.dgtd.common.exception.WsBackOfficeException;
import com.dgtd.ecole.ws.domain.json.EcoleJson;
import com.dgtd.rdc.entity.Ecole;
import com.dgtd.ecole.ws.repository.EcoleRepository;
import com.dgtd.ecole.ws.service.Generic.FileStorageService;
import com.dgtd.security.domain.entity.Utilisateur;
import com.dgtd.security.domain.service.UtilisateurService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class EcoleService implements GenericService<Ecole, Integer> ,
        LoadBaseData
         {

    private final Logger log = LoggerFactory.getLogger(EcoleService.class);

    //Constantes des colonnes du fichier d'import des écoles
   /* private static final int IMPORT_COL_ID = 0;                     //Code unique
    private static final int IMPORT_COL_LIB_ECOLE = 1;              //Nom de l'école
    private static final int IMPORT_COL_ID_SOUS_DIV = 2;            //ID de la table des sous-divisions

    private static final int IMPORT_NB_COL_MIN = 1;                 //Nombre de colonnes mini du fichier
    private static final int IMPORT_NB_COL_MAX = 5;                 //Nombre de colonnes maxi du fichier

    */


    final Ecole ecole;

    private final EcoleRepository ecoleRepository;

    private final UtilisateurService utilisateurService;

    private final FileStorageService fileStorageService;

    public EcoleService(Ecole ecole, EcoleRepository ecoleRepository, UtilisateurService utilisateurService,
                         FileStorageService fileStorageService) {
         this.ecole = ecole;
         this.ecoleRepository = ecoleRepository;
         this.utilisateurService = utilisateurService;
         this.fileStorageService = fileStorageService;
    }

     @Override
    public Ecole save(Ecole ecole) {
        log.debug("Demande de sauvegarde d'une école : {}", ecole);
        return ecoleRepository.save(ecole) ;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Ecole> findAll(Pageable pageable) {
        log.debug("Requête de l'ensemble des personnes (pageable)");
        return ecoleRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Ecole findOne(Integer id) {
        log.debug("Requête d'une école avec id n°: {}", id);
        return ecoleRepository.getOne(id);
    }


    @Override
    public void delete(Integer id) {
        log.debug("suppression d'une école id : {}", id);
        ecoleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ecole> findAll() {
        log.debug("Requête de l'ensemble des écoles");
        return ecoleRepository.findAll();
    }


    public Ecole transformJSONtoJAVA(EcoleJson ecoleJson) throws WsBackOfficeException {

        //Integration du superviseur et création de l'utilisateur si besoin avec profil superviseur
        boolean isUserHandled = utilisateurService.handleSuperviseur(ecoleJson.getSuperviseur());

       try {
           ecole.setId(ecoleJson.getId());
           ecole.setNom(ecoleJson.getNom());
           ecole.setDistrict(ecoleJson.getDistrict());
           ecole.setQuartier(ecoleJson.getQuartier());
           ecole.setLatitude(ecoleJson.getLatitude());
           ecole.setLongitude(ecoleJson.getLongitude());
           ecole.setLocalisationAutre(ecoleJson.getLocalisation_autre());

           if(isUserHandled){
               Utilisateur utilisateur = utilisateurService.findByIdentifiant(ecoleJson.getSuperviseur());
               ecole.setUtilisateurByUtilisateurSuperId(utilisateur);
               ecole.setUtilisateurSuperId(utilisateur.getId());
           }

           try {
               ecole.setLocalisationId(ecoleJson.getLocalisation_id());
           }catch (Exception e){
               throw new WsBackOfficeException("Localisation non connue en base de données "
                       + ecoleJson.getLocalisation_id() + " " + e.getLocalizedMessage());
           }

           ecole.setEducationnelleId(ecoleJson.getCodeProvinceEducationnelleSousDivision());
           ecole.setDateCreationBo(ecoleJson.getDateCreationBO());
           ecole.setDateExportBo(ecoleJson.getDateExportBO());
           ecole.setDateImportBo(ecoleJson.getDateImportBO());

           ecole.setDateIntegration(new Timestamp(new Date().getTime()));

           ecole.setEstPrePrimaire(ecoleJson.isEcolePrePrimaire());
           ecole.setEstPrimaire(ecoleJson.isEcolePrimaire());

           // ecole.setUtilisateurSuperId(ecoleJson.getUtilisateur_super_id());
           // Recherche de l'item adéquate pour le type école
           ListValeurItem lvForm = DicoUtils.getInstance().getListValeurItemGroupe("TYPE_FORMULAIRE")
                   .getListValeurItemFromId(ecoleJson.getTypeFormulaireId());

           ecole.setTypeEcole(lvForm.getIntitule());
           ecole.setNbenfantsansacte(ecoleJson.getNbEnfantSansActe());


       }catch (Exception e){
           throw  new WsBackOfficeException("Problème d'enregistrement de l'école " + e.getLocalizedMessage());
       }
        return ecole;

    }


    /**
     *
     * Generation des écoles avec les id et sous divisions correspondantes
     * au démarrage de l'application
     * @throws IOException
     * @throws WsBackOfficeException
     */
    /*
    public void generateBaseEcole() throws IOException, WsBackOfficeException{
        Optional<Ecole> ecoleOpt = ecoleRepository.findById(0);
        if (ecoleOpt.isPresent()){
            return;
        }

        //Chargement du fichier des écoles
        File file = new ClassPathResource("/ecoles.txt").getFile();
        loadBaseEcole(file);

    }

    public void loadBaseEcole(File file) throws IOException, WsBackOfficeException{

        //Création minimaliste des écoles
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        int lig = 0;
        String line = "";

        try {
            while ((line = bufferedReader.readLine()) != null) {
                lig++;
                String[] str = line.split("\t", -1);
                if ((str.length >= IMPORT_NB_COL_MIN) && (str.length <= IMPORT_NB_COL_MAX)) {
                    int id = Misc.tryParseInt(str[IMPORT_COL_ID].trim()) ? Integer.parseInt(str[IMPORT_COL_ID].trim()) : -1;
                    //Si id est à -1 pas d'information dans le fichier
                    if (id == -1) {
                        bufferedReader.close();
                        throw new WsBackOfficeException(String.format("ligne : %d : %s ", lig, " id à 0"));
                    }

                    Ecole ecole = new Ecole();
                    ecole.setIdPersonne(id);
                    ecole.setNom(str[IMPORT_COL_LIB_ECOLE]);
                    ecole.setEducationnelleId(Integer.valueOf(str[IMPORT_COL_ID_SOUS_DIV]));

                }

            }

            bufferedReader.close();
        }catch (Exception e){
            log.error("erreur : impossiblité d'ajouter les écoles : " + e.getLocalizedMessage());

        }
    }
    */

}
