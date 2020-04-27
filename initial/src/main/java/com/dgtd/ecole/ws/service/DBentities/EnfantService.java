package com.dgtd.ecole.ws.service.DBentities;

import com.dgtd.common.dicoutil.DicoUtils;
import com.dgtd.common.dicoutil.ListValeurItem;
import com.dgtd.common.error.ErrorObject;
import com.dgtd.common.error.ErrorService;
import com.dgtd.common.error.TypeError;
import com.dgtd.common.exception.DataException;
import com.dgtd.ecole.ws.domain.json.EnfantJson;
import com.dgtd.ecole.ws.repository.EnfantRepository;
import com.dgtd.evelin_common.EvelinService;
import com.dgtd.evelin_common.config.EvelinProperties;
import com.dgtd.rdc.entity.Dossier;
import com.dgtd.rdc.entity.Enfant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class EnfantService implements GenericService<Enfant, Integer> , EvelinService<Enfant> {

    private final Logger log = LoggerFactory.getLogger(EnfantService.class);

    private final EnfantRepository enfantRepository;

    private final EntityManager entityManager;

    private final EvelinProperties evelinProperties;

    private final ErrorService errorService;

    @Autowired
    public EnfantService(EnfantRepository enfantRepository, EntityManager entityManager,
                         EvelinProperties evelinProperties, ErrorService errorService) {
        this.enfantRepository = enfantRepository;
        this.entityManager = entityManager;
        this.evelinProperties = evelinProperties;
        this.errorService = errorService;
    }

    @Override
    public Enfant save(Enfant enfant) {
        log.debug("Requête de sauvegarde d'un enfant id : {}", enfant.getIdPersonne());
        return enfantRepository.save(enfant);
    }

    @Override
    public Enfant findOne(Integer id) {
        log.debug("Requête d'un enfant id : {}", id);
        return enfantRepository.getOne(id);
    }

    @Override
    public Page<Enfant> findAll(Pageable pageable) {
        log.debug("Requête de l'ensemble des enfants (pageable)");
        return enfantRepository.findAll(pageable);
    }

    @Override
    public List<Enfant> findAll() {
        log.debug("Requête de l'ensemble des enfants");
        return enfantRepository.findAll();
    }

    @Override
    public void delete(Integer id) {
        log.debug("Requête de suppression de l'enfant id :{}", id);
        enfantRepository.deleteById(id);

    }

    public List<Enfant> getAllByDossierByDossierId(Dossier dossier){
        return enfantRepository.getAllByDossierByDossierId(dossier);
    }

    public Enfant transformEnfant(Dossier dossier, EnfantJson enfantJson){
        Enfant enfant = new Enfant();
        enfant.setNom(enfantJson.getNomEnfant());
        enfant.setDossierByDossierId(dossier);
        enfant.setPrenom(enfantJson.getPrenomEnfant());
        enfant.setPostnom(enfantJson.getPostNomEnfant());
        enfant.setDateNaissance(enfantJson.getDateNaissanceEnfant());
        enfant.setLocalisationNaissanceId(enfantJson.getIdLocalisationLieuNaissanceEnfant());
        enfant.setNaissanceAutre(enfantJson.getAutreLocalisationLieuNaissanceEnfant());
        ListValeurItem lvGenre = DicoUtils.getInstance().getListValeurItemGroupe("TYPE_SEXE")
                .getListValeurItemFromId(enfantJson.getSexeEnfant());
        enfant.setGenre(lvGenre.getIntitule());
        if(enfantJson.getHeureNaissanceEnfant().equals(":") || enfantJson.getHeureNaissanceEnfant().isEmpty()){
                enfant.setHeureNaissance("");
        }else{
            enfant.setHeureNaissance(enfantJson.getHeureNaissanceEnfant());
        }
        enfant.setScolarise(String.valueOf(enfantJson.isScolarise()));
        enfant.setExport(0);

        return enfant;
    }

    public boolean checkEnfants(Dossier dossier){
        boolean isExport = false ;
        Collection<Enfant> enfants = dossier.getEnfantsById();
        if(enfants == null){
            return false;
        }

        Iterator itr = enfants.iterator();

        while (itr.hasNext()) {
            Enfant enfant = (Enfant) itr.next();
            if (enfant.getExport() == null || enfant.getExport() == 0) {
                isExport = false;
            }else{
                isExport = true;
            }
        }
        return isExport;
    }


    @Override
    public List<Enfant> getDataToDeclare(){

        List<Enfant> actesToDeclare ;
        // 1 - Génération de la requête permettant d'obtenir les actes non générés
        try{
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();

            //CriteriaQuery<Acte> query = builder.createQuery(Acte.class);
            //Root<Acte> root = query.from(Acte.class);

            CriteriaQuery<Enfant> query = builder.createQuery(Enfant.class);
            Root<Enfant> root = query.from(Enfant.class);
            query.where(builder.equal(root.get("export"),0));
            query.select(root);
            root.fetch("localisationByLocalisationNaissanceId");
            root.fetch("dossierByDossierId");
            actesToDeclare = entityManager.createQuery(query)
                    .setMaxResults(evelinProperties.getNbSynchroEvelin())
                    .getResultList();

        }catch (Exception e){
            String error = "Erreur SQL " + e;
            errorService.insertError(new ErrorObject( TypeError.TRANSFERT_EVELIN, error),false);
            log.error("***** {}", error);
            throw new DataException(error);

        }

        //Arrêt de la fonction si aucun acte à créer
        if(actesToDeclare.isEmpty()){
            return new ArrayList<>();
        }


        return actesToDeclare;
    }

    public List<Enfant> getEnfantList(Dossier dossier){
        return (List<Enfant>) dossier.getEnfantsById();
    }


}
