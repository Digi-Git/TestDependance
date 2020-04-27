package com.dgtd.evelin.ordonnance;

import com.dgtd.common.error.ErrorService;
import com.dgtd.common.exception.DataException;
import com.dgtd.common.exception.WsBackOfficeException;
import com.dgtd.common.type.TypeActe;
import com.dgtd.common.type.TypePersonne;
import com.dgtd.ecole.ws.service.DBentities.DossierService;
import com.dgtd.ecole.ws.service.DBentities.EnfantService;
import com.dgtd.evelin_common.RDC2Certificate;
import com.dgtd.evelin_common.config.EvelinProperties;
import com.dgtd.rdc.entity.Dossier;
import com.dgtd.rdc.entity.Ecole;
import com.dgtd.rdc.entity.Enfant;
import com.dgtd.rdc.entity.Personne;
import com.dgtd.rdc.localisation.entity.Localisation;
import com.dgtd.rdc.localisation.service.LocalisationService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Transactional
public class Ordonnance extends RDC2Certificate<Dossier> {
    private final Logger log = LoggerFactory.getLogger(Ordonnance.class);
    private DossierService dossierService;
    private EnfantService enfantService;
    private Dossier dossier;
    private List<Enfant> enfants;
    private String tag;
    private static int enfantNum;
    Map<TypePersonne, Personne> personnes;
    private Map<String, String> mtc;
    private String idDossier;

    private List<String> metaTemplate;

    private List<String> beanTemplate;

    private String dateCourante;
    private String heureCourante;

    /**
     *
     * @param dossier
     * @param enfants
     * @param evelinProperties
     * @param errorService
     * @throws IOException en cas d'erreur d'import de template XML
     */
    public Ordonnance(DossierService dossierService,EnfantService enfantService,LocalisationService localisationService,
                      Dossier dossier, List<Enfant> enfants, EvelinProperties evelinProperties, ErrorService errorService) throws Exception {
        super(evelinProperties,errorService,localisationService);
        this.dossierService = dossierService;
        this.enfantService = enfantService;
        this.dossier = dossier;
        setEcole(dossier.getEcoleByEcoleId());
        setIdDossier(dossier);
        this.dateSaisie = buildDate(this.dossier.getDateCreation(),"yyyyMMdd");
        this.enfants = enfants;
        this.personnes = generatePersonnesMap();
        this.tag = evelinProperties.getOrdoTag();
        enfantNum = 0;
        this.metaTemplate = getMetasTemplate();
        this.personTemplate = getPersonnesTemplate();
        this.beanTemplate = getBeanTemplate();

        this.dateCourante = generateEventDate();
        this.heureCourante = generateEventHeure();
        this.mtc= new HashMap<>();
    }




    /**
     *
     * @return un certificat de type Ordonnance sans entête
     * @throws Exception si pas de metadata, pas de demandeur
     *
     */
    @Transactional( propagation = Propagation.REQUIRES_NEW)
    public List<String> generateECW() throws Exception {

        // Récupération du demandeur
        Personne demandeur = personnes.get(TypePersonne.DEMANDEUR);

        if (demandeur == null) {
            String error = "Pas de demandeur identifié pour le dossier bdd " + dossier.getId();
            throw new DataException(error);
        }

        String bigmeta= handleMeta(this.mtc);

        List<String> ret;

        try {

            List<String> persons = new ArrayList<>();
            persons.addAll(buildPersonXml(demandeur, idDossier, "INT"));
            String office = getOfficeCode(TypePersonne.DEMANDEUR);

            // Date de saisie au format AAAMMJJ000000
            String eventDate = getDateSaisie() + "000000";

            String province = getProvinceCode();
            ret = buildCertificateXML(persons, bigmeta, idDossier,eventDate, office, province);
            if(ret == null || ret.equals("")){
                throw new DataException(String.format("Erreur lors de la cosntruction du XML, Fichier final vide %d ", dossier.getId()));
            }


        }catch (Exception e){
            throw new DataException(String.format("Erreur lors de la construction du XML du dossier %d ", dossier.getId()));
        }

        log.debug(String.format("Synchro Evelin evelin : %d - dossier  %s", dossier.getId(),
                dossier.getNoDossier()));

        try {


            if (evelinProperties.getEcwExportUpdate() == 1) {
                java.sql.Date exportDate = new java.sql.Date(new java.util.Date().getTime());
                for (Enfant enfant : enfants) {
                    enfant.setExport(1);
                    enfant.setEvelinLink(idActe);
                    enfant.setDateExport(exportDate);
                    enfantService.save(enfant);
                }
                dossier.setExport(1);
                dossier.setEvelinLink(idActe);
                dossier.setDateExport(exportDate);
                dossierService.save(dossier);


            }
        }catch (Exception e){
            String error = "Erreur pendant la génération de l'acte brouillon (ordonnance) n°" + this.dossier.getId()+ " details " + e.getLocalizedMessage();
            //errorService.insertError(String.valueOf(acte.getIdPersonne()),new ErrorObject(TypeError.TRANSFERT_EVELIN,error));
            log.error("*****  " + error);
            throw new WsBackOfficeException(error);
        }


        return ret;
    }



    protected void PopulateMeta(){

      if(enfants == null){
          throw new DataException("Pas d\'enfants à déclarer, Ordonnance brouillon non enregistrée ");
      }


        if(enfants.size()>0){
            for(Enfant enfant : enfants){
                enfantNum++;
                String enfantTag = "ENFANT_"+ enfantNum +".";
                PopulateOrdonnanceMetaEnfant(enfant, enfantTag);
                // Génération des metas des personnes
                PopulateOrdonnanceMetaPersonnes();
                // Généraiton des métas communes

            }
        }else {
            throw new DataException("Pas d\'enfants à déclarer, Ordonnance brouillon non enregistrée ");
        }
        PopulateOrdonnanceMetaCommon();
        Ecole ecole = this.dossier.getEcoleByEcoleId();
        PopulateMetaEcole(this.mtc, this.tag,ecole);
        if(mtc == null){
            throw new DataException("Pas de métadonnées à importer, Ordonnance brouillon non enregistrée ");
        }


    }

    /**
     *
     */
    private void PopulateOrdonnanceMetaPersonnes(){
         for(Map.Entry<TypePersonne, Personne> p : personnes.entrySet()) {
              String personneTag = getPersonTag(p.getKey());

             //Appelle l'implémentation générale
            PopulateMetaPersonne(this.mtc,p.getValue(),personneTag);

            if(personneTag.equals("REQUERANT.")){
                mtc.put(tag+"SUIVI.DEMANDEUR_QUALITE", StringEscapeUtils.escapeXml11(p.getValue().getQualite()));
                mtc.put(tag+"SUIVI.DEMANDEUR_PARENTE", StringEscapeUtils.escapeXml11(p.getValue().getQualiteAutre()));

                String qualite = getQualiteTypeId(p.getValue().getQualite());
                if(qualite.equals("2")){
                    mtc.put(tag+ "ORDON_NOT.AUTRE_QUALITE_DEM", StringEscapeUtils.escapeXml11(p.getValue().getQualiteAutre()));
                }

                mtc.put(tag+ "ORDON_NOT.QUALITE_DEM",qualite);

            }
         }

     }




    /**
     * Génération des metas communes
     */
    private void PopulateOrdonnanceMetaCommon ()  {
        PopulateMetaCommon(this.mtc,this.tag, this.dossier);
        this.mtc.put(this.tag + "ORDON_NOT.DATE_REQUETE", getDateSaisie());

    }


    /**
     * Complément de la méthode PopulateMetaEnfant
     * @param enfant modèle de données de la BDD
     * @param enfantTag tag général indiquée dans les métadonnées du certificat pour l'enfant concerné
     */
    protected void PopulateOrdonnanceMetaEnfant (Enfant enfant,String enfantTag) {
            PopulateMetaEnfant(this.mtc,enfant,enfantTag);

            this.mtc.put(this.tag+enfantTag+ "SCOLARISE",transformIsScolarise(enfant));


    }

    /**
     * FormapersonneTage de l'ID pour le dossier. Il est composé de l'identifiant de l'école, le numero réel du dossier ainsi que l'ID unique de l'enfant.
     * Il sert d'external ID pour l'acte. Pour l'enfant, on suffixe ce numero par _I
     * @param dossier
     * @return L'ID
     */
    private void setIdDossier (Dossier dossier) {

       this.idDossier = String.format("%07d_%s_%07d_ON", dossier.getEcoleByEcoleId().getId(), dossier.getNoDossier(), dossier.getId());

    }

    public String getPersonTag(TypePersonne typePersonne) {
        String tag="";

        //En BDD le type de personne est demandeur pour les actes de naissance standard
        //il s'agit du demandeur mais pour les ordonnances il s'agit du requérant...
        if(typePersonne.equals(TypePersonne.DEMANDEUR)) {
            tag = "REQUERANT.";
        }else if(typePersonne.equals(TypePersonne.INTERESSE)) {

            tag = "ENFANT_"+ enfantNum + ".";
            enfantNum++;
        }else if(typePersonne.equals(TypePersonne.PERE)) {
            tag = "PERE_"+ enfantNum + ".";
        }else if(typePersonne.equals(TypePersonne.MERE)) {
            tag = "MERE_"+ enfantNum + ".";
        }else if(typePersonne.equals(TypePersonne.TEMOIN1)) {
            tag ="WITNESS_1.";
        }else if(typePersonne.equals(TypePersonne.TEMOIN2)) {
            tag ="WITNESS_2.";
        }
        return tag;
    }





    @Override
    public Collection<Personne> getPersonnes() {
        return dossierService.getPersonnesFromDossier(this.dossier);
    }

    @Override
    public String getDateSaisie() {
        return this.dateSaisie;
    }


    @Override
    public List<String> getBeanTemplate() throws IOException {
        return FileUtils.readLines (new ClassPathResource("evelinTemplate/ordonnance/template_acte.xml").getFile(), StandardCharsets.UTF_8);

    }

    @Override
    public List<String> getMetasTemplate() throws IOException {
        return FileUtils.readLines (new ClassPathResource("evelinTemplate/ordonnance/metadata.xml").getFile(), StandardCharsets.UTF_8);
    }


    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public TypeActe getTypeActe() {
        return this.typeActe;
    }

    @Override
    public Dossier getInteresse() {
        return this.dossier;
    }

    @Override
    public String getIdActe() {
        return this.idActe;
    }



    @Override
    public String getIdActePerson( Personne personne) {
        return String.format("%07d_%s_%07d",this.dossier.getId() , this.dossier.getId(), personne.getIdPersonne());

    }
    @Override
    public void buildIdActe(Dossier interesse){
        try{
            this.idActe = String.format("%07d_%s_%07d_ON", dossier.getEcoleByEcoleId().getId(), dossier.getNoDossier(), dossier.getId());
        }
        catch (NullPointerException e){
            String error = "Erreur lors de la génération de l'id Acte, donnée à null, ecole ID / NoDossier ou idDossier ";
            throw new DataException(error);
        }
        catch (Exception e){
            String error = "Erreur lors de la génération de l'id Acte "+ e.getLocalizedMessage();
            throw new DataException(error);
        }
    }



    private String getQualiteTypeId(String qualite){
        String code="";
       switch (qualite){
           case "Père" : code = "1";
           break;
           case "Mère" : code = "0";
           break;
           default: code = "2";


       }

       return code;
    }

    /**
     * Utilise la map de personnes
     * @param typePersonne personne de référence pour définir le code office
     * @return Tribunal lié à la commune du domicile de la personne du type passé en param ou AUTRE si non définie
     */
    protected String getOfficeCode(TypePersonne typePersonne) {
        String officeAutre = "AUTRE";
        String office="";
        int commune = personnes.get(typePersonne).getLocalisationResidenceId();

        Optional<Localisation> localisation = localisationService.findOne(commune);
        if (localisation.isPresent()){
            office = localisation.get().getFiltre4();
            if(office.equals("0") || office.equals("") || office.length()<2){
                return officeAutre;
            }else{
                return office;
            }
        }else return officeAutre;
    }



}
