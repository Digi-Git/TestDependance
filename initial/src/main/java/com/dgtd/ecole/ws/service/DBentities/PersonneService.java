package com.dgtd.ecole.ws.service.DBentities;

import com.dgtd.common.dicoutil.DicoUtils;
import com.dgtd.common.dicoutil.ListValeurItem;
import com.dgtd.common.dicoutil.PersonType;
import com.dgtd.common.dicoutil.TypeSexe;
import com.dgtd.ecole.ws.domain.json.PersonJson;
import com.dgtd.ecole.ws.domain.json.Requerant;
import com.dgtd.rdc.entity.Dossier;
import com.dgtd.ecole.ws.repository.PersonneRepository;
import com.dgtd.rdc.entity.Personne;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class PersonneService implements GenericService<Personne, Integer> {

    private final Logger log = LoggerFactory.getLogger(PersonneService.class);


    private PersonneRepository personneRepository;

    public PersonneService(PersonneRepository personneRepository) {
        this.personneRepository = personneRepository;
    }

    /**
     * @param personne (Demandeur, Pere, Mere, Temoin1 ou Temoin 2)
     * @return Personne avec id créé en base
     */
    @Override
    public Personne save(Personne personne) {
        log.debug("Demande de sauvegarde d'une personne:{}",personne);
        return personneRepository.save(personne);
    }

     /**
     * @param pageable
     * @return page of persons
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Personne> findAll(Pageable pageable) {
        log.debug("Requête de l'ensemble des personnes (pageable) ");
        return personneRepository.findAll(pageable);
    }

    /**
     * @param id de personne
     * @return Personne
     */
    @Override
    @Transactional(readOnly = true)
    public Personne findOne(Integer id) {
        log.debug("Requête d'une personne avec l'id n°: {}", id);
        return personneRepository.getOne(id);
    }

    /**
     * @param id de la personne
     */
    @Override
    public void delete(Integer id) {
        log.debug("Requête de suppression de la personne id n°: {}", id);
        personneRepository.deleteById(id);
    }

    /**
     *
     * @return une liste de personnes
     */
    @Override
    @Transactional(readOnly = true)
    public List<Personne> findAll() {
        log.debug("Requête de l'ensemble des personnes");
        return personneRepository.findAll();
    }

    public List<Personne> getAllByAndDossierByDossierId(Dossier dossier){
        return personneRepository.getAllByDAndDossierByDossierId(dossier);
    }

    /**
     *
     * @param dossier modele de BDD
     * @param personJson objet conteneur d'une personne avec les caractéristiques principales liées aux personnes
     * @param type enumération du type de personne concerné
     * @return Personne objet de BDD
     */

    public Personne transformPersonJsonToJava(Dossier dossier, PersonJson personJson, PersonType type) {

        Personne personne = new Personne();

        int id = type.getIdPersonType();
        ListValeurItem lvPerson = DicoUtils.getInstance()
                .getListValeurItemGroupe("TYPE_PERSONNE").getListValeurItemFromId(id);
        personne.setTypePersonne(lvPerson.getIntitule());
        personne.setNom(personJson.getNom());
        personne.setPrenom(personJson.getPrenom());
        personne.setDossierByDossierId(dossier);

        //si il s'agit du pere ou de la mère nous retrouvons le genre
        if(type == PersonType.PERE || type == PersonType.MERE){
            int idGenre = getGenre(type);
            ListValeurItem lvGenre = DicoUtils.getInstance()
                    .getListValeurItemGroupe("TYPE_SEXE").getListValeurItemFromId(idGenre);
            personne.setGenre(lvGenre.getIntitule());
        }

        personne.setLocalisationNaissanceId(personJson.getIdLocalisationLieuNaissance());
        personne.setNaissanceAutre(personJson.getAutreLocalisationLieuNaissance());
        personne.setDateNaissance(personJson.getDateNaissance());
        personne.setProfession(personJson.getProfession());
        personne.setProfessionAutre(personJson.getAutreProfession());
        personne.setNationalite(personJson.getNationalite());
        personne.setNationaliteAutre(personJson.getNationaliteAutre());
        personne.setNumero(personJson.getAdresseNumero());
        personne.setAvenue(personJson.getAdresseAvenue());
        personne.setQuartier(personJson.getAdresseQuartier());
        personne.setLocalisationResidenceId(personJson.getIdLocalisationCommune());
        personne.setResidenceAutre(personJson.getAutreLocalisation());
        if(type== PersonType.DEMANDEUR){
            Requerant requerantJson = (Requerant) personJson;
            handleRequerant(personne, requerantJson);
        }
        return personne;
    }

    private Personne handleRequerant(Personne req, Requerant requerantJson){
        String qualite = requerantJson.getQualite()
                .replace(" (indiquer le lien avec l'enfant)", "");
        String qualiteAutre = requerantJson.getLienDeParente();

        req.setQualite(qualite);
        req.setQualiteAutre(qualiteAutre);

        //tentative de retrouver le genre avec la qualite
        if(!qualite.equals("")){

            req.setGenre(getGenreWithLienDeParente(qualite));
        }
        //dernière tentative avec le lien de parenté
        else if (qualiteAutre !=null){
            req.setGenre(getGenreWithLienDeParente(qualiteAutre));

        }
        //Ajout des caractéristiques complémentaires & spécifiques au requérant
        req.setPostnom(requerantJson.getPostnom());
        req.setNaissanceAutre(requerantJson.getNaissanceAutre());
        req.setResidenceAutre(requerantJson.getResidenceAutre());
        req.setTel(requerantJson.getTel());
        req.setEmail(requerantJson.getTel());
        req.setEmail(requerantJson.getEmail());

        return req;
    }

    public int getGenre(PersonType type){
        TypeSexe typeSexe;
        switch (type){
            case PERE:
                typeSexe = TypeSexe.MASCULIN;
                return typeSexe.getTypeSexe();
            case MERE:
                typeSexe = TypeSexe.FEMININ;

                return typeSexe.getTypeSexe();
            default: return 0;
        }
    }

    public String getGenreWithLienDeParente(String type){
        PersonType personType;

        if (type.equals("Père")|| type.equals("père")|| type.equals("Pere")||type.equals("pere")){
            personType = PersonType.PERE;

        }
        else if(type.equals("Mère") || type.equals("Mere")|| type.equals("mere") || type.equals("mère")){
            personType = PersonType.MERE;
        }else{
            return "";
        }

        int idGenre = getGenre(personType);
        ListValeurItem lvGenre;
        if(idGenre !=0){
            lvGenre = DicoUtils.getInstance()
                    .getListValeurItemGroupe("TYPE_SEXE").getListValeurItemFromId(idGenre);
            return lvGenre.getIntitule();
        }else{
            return "";
        }


    }
}
