package com.dgtd.ecole.ws.service.DBentities;

import com.dgtd.common.dicoutil.PersonType;
import com.dgtd.common.error.ErrorObject;
import com.dgtd.common.error.ErrorService;
import com.dgtd.common.error.TypeError;
import com.dgtd.common.exception.DataException;
import com.dgtd.common.exception.WsBackOfficeException;
import com.dgtd.ecole.ws.config.FileStorageProperties;
import com.dgtd.ecole.ws.domain.json.*;
import com.dgtd.ecole.ws.repository.DossierRepository;
import com.dgtd.evelin_common.EvelinService;
import com.dgtd.evelin_common.config.EvelinProperties;
import com.dgtd.rdc.entity.Dossier;
import com.dgtd.rdc.entity.Ecole;
import com.dgtd.rdc.entity.Enfant;
import com.dgtd.rdc.entity.Personne;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class DossierService implements GenericService<Dossier, Integer>, EvelinService<Dossier> {

    private final Logger log = LoggerFactory.getLogger(DossierService.class);

    private final EntityManager entityManager;

    private final Ecole ecole;

    private final DossierRepository dossierRepository;

    private final PersonneService personneService;

    private final EnfantService enfantService;

    private final FileStorageProperties fileStorageProperties;

    private final EvelinProperties evelinProperties;

    private final ErrorService errorService;

    public DossierService(EntityManager entityManager,Ecole ecole, DossierRepository dossierRepository, PersonneService personneService,
                          EnfantService enfantService, FileStorageProperties fileStorageProperties, EvelinProperties evelinProperties,
                          ErrorService errorService) {
        this.entityManager = entityManager;
        this.ecole = ecole;
        this.dossierRepository = dossierRepository;
        this.personneService = personneService;
        this.enfantService = enfantService;
        this.fileStorageProperties = fileStorageProperties;
        this.evelinProperties = evelinProperties;
        this.errorService = errorService;
    }

    /**
     * Save a dossier.
     *
     * @param dossier the com.dgtd.rdc.localisation.entity to save
     * @return the persisted com.dgtd.rdc.localisation.entity
     */
    @Override
    public Dossier save(Dossier dossier) {
        log.debug("Requête de sauvegarde de dossier id : {}", dossier.getId());
        return dossierRepository.save(dossier);
    }

    /**
     * Get all the dossiers.
     *
     * @param pageable the pagination information
     * @return the list of DBentities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Dossier> findAll(Pageable pageable) {
        log.debug("Requête de l'ensemble des dossiers (pageable)");
        return dossierRepository.findAll(pageable);
    }

    /**
     * Get the "id" dossier.
     *
     * @param id the id of the com.dgtd.rdc.localisation.entity
     * @return the com.dgtd.rdc.localisation.entity
     */
    @Override
    @Transactional(readOnly = true)
    public Dossier findOne(Integer id) {
        log.debug("Requête du dossier id n°: {}", id);
        return dossierRepository.getOne(id);
    }

    /**
     * Delete the "id" dossier.
     *
     * @param id the id of the com.dgtd.rdc.localisation.entity
     */
    @Override
    public void delete(Integer id) {
        log.debug("Requête de suppression du dossier id n°: {}", id);
        dossierRepository.deleteById(id);
    }

    /**
     * Get all the dossiers.
     *
     * @return liste de dossiers
     */
    @Override
    @Transactional(readOnly = true)
    public List<Dossier> findAll() {
        log.debug("Requête de l'ensemble des dossiers");
        List<Dossier> dossiers = new ArrayList<>();
        dossierRepository.findAll().forEach(dossiers :: add);
        return dossiers;
    }



    /**
     *
     * @param distinctionAndNoDossier champ distinctif des numéros de dossiers uniques de ceux qui sont en double avec l'id du dossier interne bdd
     * @param noDossier indiqué sur les formulaires papier (champ noDossier)
     * @return liste des dossiers qui contiennent strictement le numéro de dossier papier +
     * les éventuels dossiers avec le même numéro de dossier papier plus l'id du dossier
     */
    public List<Dossier> findAllByNoDossierContaining(String distinctionAndNoDossier, String noDossier){
        return dossierRepository.findAllByNoDossierContaining(distinctionAndNoDossier,noDossier);
    }

    public void updateNoDossier(Dossier dossier, String noDossier){
        dossier.setNoDossier(noDossier);
        dossierRepository.save(dossier);

    }

    public void deleteCascade(Dossier dossier){
        //
        dossierRepository.delete(dossier);
    }

    /**
     * Gestion globale de la chaine d'enregistrement en BDD
     * Création du dossier, des personnes qui entourent l'enfant (mandataire, pere, mere, temoins),
     * et des enfants
     * @param ecoleJson objet conteneur JSON qui peut contenir plusieurs dossiers (documents)
     * @param doc dossier en cours de traitement
     * @return Dossier modèle de BDD
     */
    public Dossier createCascade(EcoleJson ecoleJson, Document doc){
        Dossier dossier = new Dossier();
        Requerant requerantJson = doc.getRequerant();
        Pere pereJson = doc.getPere();
        Mere mereJson = doc.getMere();
        Temoin temoin1Json = doc.getTemoin1();
        Temoin2 temoin2Json = doc.getTemoin2();

        try {
           dossier = transformJSONtoJAVA(ecoleJson, doc);
        } catch (WsBackOfficeException e) {
            e.printStackTrace();
        }
        //sauvegarde du dossier en bdd
        save(dossier);

        //Création des personnes
        //Création de l'entité Personne du requérant
        Personne req = personneService
                .transformPersonJsonToJava(dossier, requerantJson, PersonType.DEMANDEUR);

        //Sauvegarde en BDD
        personneService.save(req);

        //Pere
        Personne pere = personneService.transformPersonJsonToJava(dossier, pereJson, PersonType.PERE);
        personneService.save(pere);

        //Mere
        Personne mere = personneService.transformPersonJsonToJava(dossier, mereJson, PersonType.MERE);
        personneService.save(mere);

        //Temoin1
        Personne temoin1 = personneService.transformPersonJsonToJava(dossier, temoin1Json, PersonType.TEMOIN1);
        personneService.save(temoin1);

        //Temoin2
        Personne temoin2 = personneService.transformPersonJsonToJava(dossier, temoin2Json, PersonType.TEMOIN2);
        personneService.save(temoin2);


        //Gestion de la liste d'enfants
        for (EnfantJson enfantJson : doc.getEnfantList()) {
            Enfant enfant = enfantService.transformEnfant(dossier, enfantJson);
            enfantService.save(enfant);
        }
        return dossier;
    }
    /**
     * Gestion d'un dossier : enregistrement en BDD
     * @param ecoleJson conteneur global JSON
     * @param doc dossier en cours d'enregistrement
     * @return Dossier modèle de BDD
     * @throws WsBackOfficeException Problème d'enregistrement du dossier
     */
    @Transactional(propagation= Propagation.REQUIRED)
    public Dossier transformJSONtoJAVA(EcoleJson ecoleJson, Document doc)  {
        Dossier dossier = null;
        //Sauvegarde des dossiers & enregistrement des objets présents dans ecoleJson
        try {
            dossier = new Dossier();
            dossier.setNoDossier(doc.getDossierNumber());
            dossier.setDateCreation( doc.getDateSaisie());
            dossier.setNomPrenomUtilisateurAgent(doc.getAgentName());
            dossier.setDateSignature( doc.getDateSignature());
            dossier.setCommentaire(doc.getCommentaire());
            dossier.setEcoleId(ecoleJson.getId());
            dossier.setEcoleByEcoleId(ecole);

            String[] tabFolderName = fileStorageProperties.getZipPath().split("/");
            int size = tabFolderName.length -1;
            String folderName = tabFolderName[size].replace(".zip","");
            dossier.setNom_dossier_source(folderName);
            dossier.setDateImportBo(ecoleJson.getDateImportBO());
            dossier.setDateExportBo(ecoleJson.getDateExportBO());
            dossier.setDateIntegration(new Timestamp(new Date().getTime()));
            dossier.setExport(0);

        } catch (Exception ex) {
            log.error("Problème d'enregistrement du dossier numero formulaire {}", doc.getDossierNumber());
            throw new WsBackOfficeException("Données incorrectes pour le dossier numero formulaire " + doc.getDossierNumber() + " " + ex.getLocalizedMessage());
        }

        return dossier;
    }

    @Override
    public List<Dossier> getDataToDeclare() {

        List<Dossier> dossiers = null;
        try{

            // Génération de la requete : oui c'est une copie de la version pour l'export.
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Dossier> dossierCriteriaQuery = builder.createQuery(Dossier.class);

            // Utilisation de graph plutôt que fetch direct : sinon gros load, gros caca.
            // EntityGraph<Dossier> dossierEntityGraph = entityManager.createEntityGraph(Dossier.class);
            // dossierEntityGraph.addAttributeNodes("personnesById");

            // Pour la partie "select"
            Root<Dossier> dossierRoot = dossierCriteriaQuery.from(Dossier.class);
            dossierCriteriaQuery.where(builder.equal(dossierRoot.get("export"),0));
           /* Join<Dossier, Personne> dossierPersonneJoin = dossierRoot.join("personnesById", JoinType.INNER);
            Join<Personne, Localisation> personneLocalisationJoin = dossierPersonneJoin.join("localisationByLocalisationResidenceId", JoinType.INNER);
            Predicate p1 = builder.equal(personneLocalisationJoin.get("id"), 1218);
            Predicate p2 = builder.equal(dossierPersonneJoin.get("typePersonne"), "Demandeur");
            Predicate et = builder.and(p1, p2);

            // Et finalement la recherche
            dossierCriteriaQuery.where(et);
            */
            dossierRoot.fetch("enfantsById");

            dossiers = entityManager.createQuery(dossierCriteriaQuery)
                    //.setHint("javax.persistence.loadgraph", dossierEntityGraph)
                    .setMaxResults(evelinProperties.getNbSynchroEvelin())
                    .getResultList();

        }catch(Exception e){
            String error = "Erreur SQL " + e;
            errorService.insertError(new ErrorObject( TypeError.TRANSFERT_EVELIN, error),false);
            log.error("***** {}", error);
            throw new DataException(error);
        }

        return  dossiers;
    }


    public List<Personne> getPersonnesFromDossier(Dossier dossier){
        return personneService.getAllByAndDossierByDossierId(dossier);
    }
}
