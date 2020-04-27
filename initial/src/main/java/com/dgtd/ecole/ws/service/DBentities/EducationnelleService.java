package com.dgtd.ecole.ws.service.DBentities;

import com.dgtd.common.exception.WsBackOfficeException;
import com.dgtd.common.util.Misc;
import com.dgtd.rdc.entity.Educationnelle;
import com.dgtd.ecole.ws.repository.EductionnelleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;
import java.util.Optional;


@Service
@Transactional(rollbackFor = Exception.class)
public class    EducationnelleService implements GenericService<Educationnelle, Integer>,
        LoadBaseData{

    @Autowired
    private EductionnelleRepository eductionnelleRepository;

    private final Logger log = LoggerFactory.getLogger(EducationnelleService.class);




    //Constantes des colonnes du fichier d'import des localisations + sous divisions
    private static final int IMPORT_COL_ID   = 0;                           //  Code unique (localisation + sous division)
    private static final int IMPORT_COL_ID_LOCALISATION = 1;                //  Code localisation
    private static final int IMPORT_COL_LIB_LOCALISATION = 2;               //  Libellé de la localisation
    private static final int IMPORT_COL_ID_SOUSDIV = 3;                     //  Code de la sous division
    private static final int IMPORT_COL_LIB_SOUSDIV= 4;                     //  Libellé de la sous- division

    private static final int IMPORT_NB_COL_MIN = 5;                         // Nombre de colonne mini du fichier sousdivision
    private static final int IMPORT_NB_COL_MAX = 5;                         // Nombre de colonne maxi du fichier sousdivision


    @Override
    public Educationnelle save(Educationnelle educationnelle) {
        log.debug("demande de sauvegarde d'une province éducationnelle id n°: {}", educationnelle.getId());
        return eductionnelleRepository.save(educationnelle);
    }

    @Override
    @Transactional(readOnly = true)
    public Educationnelle findOne(Integer id) {
        log.debug("Demande de sauvegarde de la province éducationnelle id n°:{} ",id);
        return eductionnelleRepository.getOne(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Educationnelle> findAll(Pageable pageable) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Educationnelle> findAll() {
        log.debug("Requête de l'ensemble des provinces éducationnelles ");
        return eductionnelleRepository.findAll();
    }

    @Override
    public void delete(Integer id) {
        log.debug("Demande de suppression de la province éducationnelle n°: ", id);
        eductionnelleRepository.deleteById(id);

    }

    @PostConstruct
    public void loadBaseData() throws IOException, WsBackOfficeException {

        Optional<Educationnelle> educationnelleDef = eductionnelleRepository.findById(3);
        if (educationnelleDef.isPresent())
            return;
        File file;
        // Chargement du fichier des sous divisions aka provinces educationnelles par défaut
        try {
           file = new ClassPathResource("definitions/sousdivision.txt").getFile();

        }
        catch (Exception ex) {
            log.error("erreur lors du chargement des sous divisions (/resources/definitions/sousdivision.txt)" + ex.getLocalizedMessage());
            throw new WsBackOfficeException("Erreur obtention FILE pour les sous-division (resources/definitions/sousdivision.txt)" + ex.getLocalizedMessage());
        }

        try{
            loadBaseEducationnelle (file);
        }catch(Exception e){
            log.error("erreur de chargement des données en base de données " + e.getLocalizedMessage());
            throw new WsBackOfficeException("Erreur de chargement des données en base de données " + e.getLocalizedMessage());
        }
    }

    public void loadBaseEducationnelle(File file) throws IOException, WsBackOfficeException {

        //Création des provinces + sous divisions par défaut

        BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        int lig = 0;
        String line = "";
        while ((line = buffer.readLine()) != null) {
            lig++;
            //skip le header
            if (lig == 1) continue;

            String[] str = line.split("\t", -1);
            if ((str.length >= IMPORT_NB_COL_MIN) && (str.length <= IMPORT_NB_COL_MAX)) {

                int id = Misc.tryParseInt(str[IMPORT_COL_ID].trim()) ? Integer.parseInt(str[IMPORT_COL_ID].trim()) : -1;


                // Si l'id est à 0, on dégage
                if (id == -1) {
                    buffer.close();
                    throw new WsBackOfficeException(String.format("ligne : %d : %s", lig, "id à 0"));
                }

                Educationnelle educationnelle = new Educationnelle();
                educationnelle.setId(id);
                educationnelle.setProvince(str[IMPORT_COL_LIB_LOCALISATION]);
                educationnelle.setSousDivision(str[IMPORT_COL_LIB_SOUSDIV]);
                save(educationnelle);

            }
        }
        buffer.close();
    }


}
