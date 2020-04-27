package com.dgtd.rdc.localisation.service;


import com.dgtd.common.exception.WsBackOfficeException;
import com.dgtd.common.service.GenericService;
import com.dgtd.common.util.Misc;
import com.dgtd.rdc.localisation.entity.Localisation;
import com.dgtd.rdc.localisation.repository.LocalisationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.trim;


@Service
public class LocalisationService implements GenericService<Localisation,Integer> {

    private static final short ENTITE_URBAINE = 0 ;
    private static final short ENTITE_RURALE = 1;

    private final Logger log = LoggerFactory.getLogger(LocalisationService.class);

    // Constante des colonnes du fichiers d'import d'une localisation
    private static final int IMPORT_COL_ID   = 0;                           // A - Code unique
    private static final int IMPORT_COL_CODE_PROVINCE = 1;                  // B - RDC : code province
    private static final int IMPORT_COL_LIB_PROVINCE = 2;                   // C - RDC : province
    private static final int IMPORT_COL_CODE_VILLE = 3;                     // D - RDC : code ville
    private static final int IMPORT_COL_LIB_VILLE = 4;                      // E - RDC : ville
    private static final int IMPORT_COL_CODE_COMMUNE_URBAINE = 5;           // F - RDC : code commune urbaine
    private static final int IMPORT_COL_LIB_COMMUNE_URBAINE = 6;            // G - RDC : libelle commune urbaine

    private static final int IMPORT_COL_CODE_TERRITOIRE = 7;                // H - RDC : code territoire
    private static final int IMPORT_COL_LIB_TERRITOIRE = 8;                 // I - RDC : libelle territoire
    private static final int IMPORT_COL_CODE_COMMUNE_RURALE = 9;            // J - RDC : code commune rurale
    private static final int IMPORT_COL_LIB_COMMUNE_RURALE = 10;            // K - RDC : libelle commune rurale
    private static final int IMPORT_COL_CODE_SECTEUR = 11;                  // L - RDC : code secteur
    private static final int IMPORT_COL_LIB_SECTEUR = 12;                   // M - RDC : libelle secteur
    private static final int IMPORT_COL_CODE_CHEF = 13;                     // N - RDC : code chefferie
    private static final int IMPORT_COL_LIB_CHEF = 14;                      // O - RDC : libelle chefferie

    private static final int IMPORT_NB_COL_MIN = 5;                         // Nombre de colonne mini du fichier localisation
    private static final int IMPORT_NB_COL_MAX = 15;                        // Nombre de colonne maxi du fichier localisation


    private LocalisationRepository localisationRepository;

    @Autowired
    public LocalisationService(LocalisationRepository localisationRepository){
        this.localisationRepository = localisationRepository;
    }

    @Override
    public Localisation save(Localisation localisation) {
        log.debug("Request to save Etablissement : {}", localisation);
        return localisationRepository.save(localisation);
    }

    @Override
    public Page<Localisation> findAll(Pageable pageable) {
        log.debug("Request to get all Etablissement (pageable)");
        return localisationRepository.findAll(pageable);
    }

    @Override
    public Optional<Localisation> findOne(Integer id) {
        log.debug("Request to get Etablissement : {}", id);
        return localisationRepository.findById(id);

    }

    @Override
    public void delete(Integer id) {
        log.debug("Request to delete localisation : {}", id);
        localisationRepository.deleteById(id);
    }

    @Override
    public List<Localisation> findAll() {
        log.debug("Request to get all localisation");
        return localisationRepository.findAll();
    }

    /**
     * Récupération de l'id de localisation en fonction des informations contenues dans niv_1 et niv_3
     * Si l'id n'est pas retrouvé en bdd l'id 0 est retourné (correspondant à autre)
     * @param localisation champ texte récupéré concaténé avec niv1 /niv3
     * @return l'id localisation présent en base de données
     *
     */
    public int findByNiv1AndNiv3(String localisation){

        log.debug("Request to get id localisation with niv1 and niv3");
        int id =0;
        String niv_1="";
        String niv_3="";

        if(localisation.equals("")){
            return id;
        }
        Localisation loc = null;
        String[] niv1_niv3 = getNiv1AndNiv3(localisation);
        niv_1 = niv1_niv3[0];
        niv_3 = niv1_niv3[1];

        try {
            if (niv_3.equals("")) {
                loc = localisationRepository.findByNiv1AndNiv2(niv_1, "");
            }else if( niv_1.equals("")){
                return id;
            }else{
                loc = localisationRepository.findByNiv1AndNiv3(niv_1,niv_3);
            }
            id = loc.getId();

        }catch (Exception e){
            log.error("Niv1 et niv3 ne sont pas disponible en BDD, niv1 : {} , niv3 : {}",niv_1,niv_3);

            id=0;
        }


        return id;
    }

    /**
     * Récupération de l'id de localisation en fonction du nom répertorié sous la forme
     *  "xxxxx xxxxx [ xxxxx ]"
     *  La première partie représente le niv_3 (dans la table localisation) et entre crochets il s'agit du niv_1
     *
     * @param localisation complète
     * @return un tableau avec en première position le niv_1 et en deuxième position le niv_3
     */
    public String[] getNiv1AndNiv3(String localisation){
        String[]niv1_niv3 = new String[2];
        String niv1="";
        String niv3="";

        String[] loc = localisation.split("\\[|\\]");

        int size = loc.length;
        int sizeWithNiv1 = size-1;
        niv1 = trim(loc[sizeWithNiv1]);
        niv3 = trim(loc[0]);
        niv1_niv3[0] = niv1;
        niv1_niv3[1] = trim(niv3);

        return niv1_niv3;
    }


    /**
     * Fonction appelée lors de l'initialisation de la base de donnée
     */
    public void loadBaseData () throws WsBackOfficeException, IOException {

        Optional<Localisation> localisationDef = localisationRepository.findById(0);
        if (localisationDef.isPresent()){
            File fileTribunaux = new ClassPathResource("definitions/tribunaux.txt").getFile();
            loadTribunaux(fileTribunaux);
            return;
        }


        // Chargement du fichier des localisations par défaut
        try {
            File file = new ClassPathResource("definitions/localisation.txt").getFile();
            loadLocalisation(file);
            File fileTribunaux = new ClassPathResource("definitions/tribunaux.txt").getFile();
            loadTribunaux(fileTribunaux);


        }
        catch (Exception ex) {
            log.error("erreur : impossiblité de créer / charger les localisations : " + ex.getLocalizedMessage());
            throw new WsBackOfficeException("Erreur obtention FILE pour les localisations (resources/definitions)" + ex.getLocalizedMessage());
        }


    }

    /**
     *
     * @return localisation non définie mais sur le territoire concerné
     */
    public Localisation getNonDefinieTerritoire (){
        return localisationRepository.findById(0).get();
    }

    /**
     *
     * @return localisation non définie mais à l'étranger
     */
    public Localisation getNonDefinieEtranger(){
        return localisationRepository.findById(1).get();
    }

    /**
     * Chargement d'un fichier des localisations (conforme à celui de la solution)
     * @param file
     * @throws IOException
     * @throws WsBackOfficeException
     */
    @Transactional(rollbackFor = Exception.class)
    public void loadLocalisation (File file) throws IOException, WsBackOfficeException {

        // Création des localisations par defaut
        // Etablissement non définie pour les urbaines

        Localisation lo0 = createEmptyLocalisation(ENTITE_URBAINE);
        lo0.setId (0);
        localisationRepository.save(lo0);

        // ... et pour les rurales
        Localisation lo1 = createEmptyLocalisation(ENTITE_RURALE);
        lo1.setId(1);
        localisationRepository.save(lo1);

        // Import du fichier
        int lig = 0;

        BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

        String line = "";
        while ((line = buffer.readLine()) != null) {

            lig++;

            // 1ere ligne, contient le header
            if (lig == 1) continue;

            String[] str = line.split("\t");
            if ((str.length >= IMPORT_NB_COL_MIN) && (str.length <= IMPORT_NB_COL_MAX)) {

                int id = Misc.tryParseInt(str[IMPORT_COL_ID].trim()) ? Integer.parseInt(str[IMPORT_COL_ID].trim()) : -1;

                // Si l'id est à 0, on dégage
                if (id == -1) {
                    buffer.close();
                    throw new WsBackOfficeException(String.format("ligne : %d : %s", lig, "id à 0"));
                }

                // Convenu avec Sully : l'id est le numero de ligne Excel + 1
                Localisation localisation = new Localisation();
                localisation.setId(id);

                localisation.setNiv1("");
                localisation.setNiv2("");
                localisation.setNiv3("");
                localisation.setNiv4("");
                localisation.setNiv5("");
                localisation.setNiv6("");
                localisation.setNiv7("");
                localisation.setNiv8("");
                localisation.setFiltre1("");
                localisation.setFiltre2("");
                localisation.setFiltre3("");
                localisation.setFiltre4("");
                localisation.setFiltre5("");
                localisation.setTypeEntite((short) IMPORT_COL_LIB_VILLE);

                // Fonction du type d'entité. 1 = urbaine - 0 = rurale
                localisation.setTypeEntite((str[IMPORT_COL_LIB_VILLE].trim().isEmpty()) ? ENTITE_RURALE : ENTITE_URBAINE);

                // Setup des données importées.
                localisation.setFiltre1(str[IMPORT_COL_CODE_PROVINCE].trim());
                localisation.setNiv1(str[IMPORT_COL_LIB_PROVINCE].trim());                    // Province

                // Urbaine
                if (localisation.getTypeEntite() == ENTITE_URBAINE) {

                    localisation.setNiv2(str[IMPORT_COL_LIB_VILLE].trim());
                    localisation.setFiltre2(str[IMPORT_COL_CODE_VILLE].trim());

                    // La commune peut être absente pour indiquer un rattachement direct à la ville
                    if (str.length >= 7) {
                        localisation.setNiv3(str[IMPORT_COL_LIB_COMMUNE_URBAINE].trim());
                        localisation.setFiltre3(str[IMPORT_COL_CODE_COMMUNE_URBAINE].trim());
                    }
                } else {
                    localisation.setNiv2(str[IMPORT_COL_LIB_TERRITOIRE].trim());
                    if ((str.length >= 10)) {
                        localisation.setNiv3(str[IMPORT_COL_LIB_COMMUNE_RURALE].trim());
                        localisation.setFiltre3(str[IMPORT_COL_CODE_COMMUNE_RURALE].trim());
                    } else {
                        localisation.setNiv3("");
                        localisation.setFiltre3("");
                    }
                    localisation.setFiltre2(str[IMPORT_COL_CODE_TERRITOIRE].trim());


                    // Suivant la découpe cvs, ces colonnes ne sont pas forcement présentes
                    if (str.length >= 12) {
                        localisation.setNiv4(str[IMPORT_COL_LIB_SECTEUR].trim());
                        localisation.setFiltre4(str[IMPORT_COL_CODE_SECTEUR].trim());

                        if (str.length >= 15) {
                            localisation.setNiv5(str[IMPORT_COL_LIB_CHEF].trim());
                            localisation.setFiltre5(str[IMPORT_COL_CODE_CHEF].trim());
                        }
                    }
                }

                // Ajout du centre dans la liste d'import
                localisationRepository.save(localisation);
            }
        } // fin while
        buffer.close();

    }

    /**
     * Création d'une localisation par défaut
     * @param typeLocalite
     * @return localisation
     */
    private Localisation createEmptyLocalisation (short typeLocalite) {
        Localisation lo1 = new Localisation();
        lo1.setNiv1("<Non définie>");
        lo1.setNiv2("<Non définie>");
        lo1.setNiv3("<Non définie>");
        lo1.setNiv4("<Non définie>");
        lo1.setNiv5("<Non définie>");
        lo1.setNiv6("");
        lo1.setNiv7("");
        lo1.setNiv8("");
        lo1.setFiltre1("0");
        lo1.setFiltre2("0");
        lo1.setFiltre3("0");
        lo1.setFiltre4("0");
        lo1.setFiltre5("0");

        return lo1;
    }


    /**
     * Tribunaux sont spécifiques uniquement pour la commune de Kinshasa
     *
     * @param file Tribunal.txt
     * @throws IOException en cas de problème de lecture du fichier tribunal.txt
     */
    private void loadTribunaux(File file) throws IOException {
        int id_commune =0;
        int kinshasa_commune = 1;

        int tribunal = 3;

        String kinshasa = "KINSHASA";

        int lig = 0;
        BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String line = "";

        while ((line = buffer.readLine()) != null) {

            lig++;
            if (lig == 1) continue;
            String[] str = line.split("\t", -1);
            if(str.length> 1){
                String id = str[id_commune];
                String commune = str[kinshasa_commune];
                String trib = str[tribunal];

                try{
                    Localisation localisation = localisationRepository.findByNiv2AndNiv3(kinshasa,commune);
                    if(localisation!=null){
                        localisation.setFiltre4(trib);
                        localisationRepository.save(localisation);
                        log.debug(id+ " insertion du tribunal "+ trib + " pour la localisation id " + localisation.getId() + " commune " + commune);
                    }else{
                        log.error("Erreur lors de la récupération de la commune "+ commune +" pas d'insertion du tribunal id "+id);
                    }
               }catch (Exception e){
                    log.error("Erreur lors de la récupération de la commune "+ commune +" pas d'insertion du tribunal id "+id);

                }

            }

        }
    }

}
