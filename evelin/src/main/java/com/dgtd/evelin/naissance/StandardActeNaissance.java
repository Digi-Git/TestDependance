package com.dgtd.evelin.naissance;

import com.dgtd.common.error.ErrorService;
import com.dgtd.common.exception.DataException;
import com.dgtd.common.exception.WsBackOfficeException;
import com.dgtd.common.type.TypeActe;
import com.dgtd.common.type.TypePersonne;
import com.dgtd.ecole.ws.service.DBentities.DossierService;
import com.dgtd.ecole.ws.service.DBentities.EnfantService;
import com.dgtd.evelin_common.RDC2Certificate;
import com.dgtd.evelin_common.config.EvelinProperties;
import com.dgtd.evelin_common.config.ExplodeDate;
import com.dgtd.rdc.entity.Dossier;
import com.dgtd.rdc.entity.Ecole;
import com.dgtd.rdc.entity.Enfant;
import com.dgtd.rdc.entity.Personne;
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
public class StandardActeNaissance extends RDC2Certificate<Enfant> {
    private final Logger log = LoggerFactory.getLogger(StandardActeNaissance.class);

    private Dossier dossier;
    private Enfant enfant;
    private final EnfantService enfantService;
    private final DossierService dossierService;
    private String tag;
    private Map<String,String> mtc;

    /**
     *
     * @param dossier dossier
     * @param enfants liste d'un enfant
     * @param evelinProperties configuration de l'application
     * @param errorService gestion des erreurs de l'application
     * @throws IOException en cas d'erreur d'import de template XML
     */
    public StandardActeNaissance(Dossier dossier, List<Enfant> enfants, EnfantService enfantService,
                                 DossierService dossierService, LocalisationService localisationService,
                                 EvelinProperties evelinProperties, ErrorService errorService) throws Exception {

        super(evelinProperties,errorService,localisationService);
        this.enfantService = enfantService;
        this.dossierService = dossierService;
        this.dossier = dossier;
        setEcole(this.dossier.getEcoleByEcoleId());
        if(enfants.isEmpty() ){
            throw new DataException("Pas d'enfant à déclarer dans cet acte ");
        }
        this.enfant = enfants.get(0);
        this.tag = evelinProperties.getNaiTag();
        this.personnes = generatePersonnesMap();
        this.beanTemplate = getBeanTemplate();
        this.metaTemplate = getMetasTemplate();
        buildIdActe(enfant);
        this.idActe = getIdActe();
        Date dateCreation = this.dossier.getDateCreation();
        if(dateCreation!= null){
            this.dateSaisie = buildDate(dateCreation,"yyyyMMdd");
        }
        this.mtc = new HashMap<>();

    }

    /**
     * Fonction d'entrée pour la génération d'un seul certificat.xml
     * @return un certificat sous forme de List<String>
     */
    @Transactional( propagation = Propagation.REQUIRES_NEW)
    public List<String> generateECW(){

        List<String> ret;

        try {


            String bigMeta = handleMeta(this.mtc);

            List<String> persons = generatePersons();
            ExplodeDate dNai = new ExplodeDate();
            dNai.Explode(enfant.getDateNaissance(), enfant.getHeureNaissance());

            String eventDate =  dNai.getAnnee() + dNai.getMois() + dNai.getJour() + dNai.getHeureExplodedEvelin() + "00";

            String province = getProvinceCode();
            String office = province;
            ret = buildCertificateXML(persons,bigMeta,this.idActe,eventDate,office, province);

            log.debug("Intégration des données du transfert vers Evelin dans la table Enfant ");

            if (evelinProperties.getEcwExportUpdate() == 1) {
                this.enfant.setExport(1);
                this.enfant.setEvelinLink(this.idActe);
                this.enfant.setDateExport(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
                enfantService.save(this.enfant);


            }


        } catch (Exception e) {
            String error = "Erreur pendant la génération de l'acte brouillon n°" + this.enfant.getIdPersonne() + " details " + e.getLocalizedMessage();
            log.error("*****  {}", error);
            throw new WsBackOfficeException(error);
        }

        log.info("***** Enregistrement sur serveur de l'ECW correspondant à l'enfant n° {}",this.enfant.getIdPersonne() );

        return ret;
    }



    protected void PopulateMeta() {
        Ecole ecole = this.dossier.getEcoleByEcoleId();
        PopulateMetaEcole(this.mtc, this.tag, ecole);
        populateNaissanceMetaCommon();
        populateNaissanceMetaEnfant();
        for(Map.Entry<TypePersonne, Personne> personne : personnes.entrySet()){
            String personTag = getPersonTag(personne.getKey());
            populateNaissanceMetaPersonne(personne.getValue(), personTag);
        }

    }


    private void populateNaissanceMetaCommon(){

        PopulateMetaCommon(this.mtc, this.tag, this.dossier);
        mtc.put(this.tag + "ACT.ACT_NUM", "");
        //0 = Tiers déclarant, ici toujours zéro, le déclarant sera une personne du centre de santé
        mtc.put(this.tag + "ACT_NAI.BIRTH_DECL_TPML", "0");
        mtc.put(this.tag + "REGISTRE.REG_YEAR", getDateCourante().substring(0, 4));

    }


    private void populateNaissanceMetaEnfant() {
        String enfantTag = "CHILD.";
        PopulateMetaEnfant(this.mtc,this.enfant,enfantTag );
        mtc.put(this.tag+"SUIVI.ENFANT_SCOLARISE",transformIsScolarise(this.enfant));

        //Préparation date de naissance
        ExplodeDate dNai = new ExplodeDate();
        dNai.Explode(this.enfant.getDateNaissance(), this.enfant.getHeureNaissance());
        mtc.put(this.tag + "ACT.ACT_EVT_DATE", dNai.getDateExplodedEvelin());
        mtc.put(this.tag + "ACT.ACT_EVT_HOUR", dNai.getHeureExplodedEvelin());

    }

    /**
     * Gestion des métadonnées des personnes spécifiques aux actes de naissances
     * @param personne model BDD
     * @param personTag tag de la personne renseignée dans le template XML
     */
    private void populateNaissanceMetaPersonne(Personne personne, String personTag) {
        String globalTag = this.tag + personTag;
        PopulateMetaPersonne(this.mtc,personne,personTag);
        // Formatage de la date

        mtc.put(globalTag + "INFO_NAI.EVT_ADDRESS.CITY", StringEscapeUtils.escapeXml11(formatLieu(personne.getLocalisationByLocalisationNaissanceId(), personne.getNaissanceAutre())));
        mtc.put(globalTag + "TELEPHONE", personne.getTel());
        mtc.put(globalTag + "EMAIL", StringEscapeUtils.escapeXml11(personne.getEmail()));

        //Par défaut hasPere est false
        String hasPere = "0";
        if (personnes.get(TypePersonne.PERE) != null) {
            hasPere = "1";
        }

        mtc.put(this.tag + "ACT_NAI.INDICATE_FATHER_y8n", hasPere);

        if(personne.getTypePersonne().equals(TypePersonne.DEMANDEUR.getTypePersonne())){

            String qualite = personne.getQualite();
            mtc.put(this.tag + "SUIVI.DEMANDEUR_QUALITE", qualite);
            String qualiteAutre = ( (personne.getQualiteAutre() == null || "".contentEquals(personne.getQualiteAutre().trim()) ) ? "" : personne.getQualiteAutre() );
            mtc.put(this.tag + "SUIVI.DEMANDEUR_PARENTE",qualiteAutre);
        }



    }

    @Override
    public String getPersonTag(TypePersonne typePersonne) {

        String personTag="";

        if(typePersonne.equals(TypePersonne.DEMANDEUR)) {
            personTag = "DEMANDEUR.";
        }else if(typePersonne.equals(TypePersonne.INTERESSE)) {
            personTag = "CHILD.";
        }else if(typePersonne.equals(TypePersonne.PERE)) {
            personTag = "FATHER.";
        }else if(typePersonne.equals(TypePersonne.MERE)) {
            personTag = "MOTHER.";
        }else if(typePersonne.equals(TypePersonne.TEMOIN1)) {
            personTag ="WITNESS_1.";
        }else if(typePersonne.equals(TypePersonne.TEMOIN2)) {
            personTag ="WITNESS_2.";
        }
        return personTag;
    }

    @Override
    public Collection<Personne> getPersonnes() {
        return dossierService.getPersonnesFromDossier(dossier);
    }


    @Override
    public String getDateSaisie() {
        return this.dateSaisie;
    }


    /**
     * @return liste de balises xml communes concernant les personnes
     */
    public List<String> generatePersons()  {

        List<String> personsXML = new ArrayList<>();
        Enfant interesse = getInteresse();

        try{
            for (String lig : getPersonnesTemplate()) {
                String out = lig;
                if (lig.contains("@@ID@@")) {
                    out = lig.replace("@@ID@@",getIdActeInteresse());
                } else if (lig.contains("@@ROLE@@")) {
                    out = lig.replace("@@ROLE@@", "INT");

                } else if (lig.contains("@@NOM@@")) {
                    String postnom = interesse.getPostnom().equals("")?"": " "+interesse.getPostnom();
                    String rep = interesse.getNom() + postnom;
                    out = lig.replace("@@NOM@@", rep.trim());

                } else if (lig.contains("@@PRENOM@@")) {
                    String prenom = ( interesse.getPrenom()== null) ? "" : interesse.getPrenom().trim();
                    out = lig.replace("@@PRENOM@@", prenom);
                } else if (lig.contains("@@GENRE@@")) {
                    out = lig.replace("@@GENRE@@", interesse.getGenre().substring(0, 1));

                }
                // Ligne toujour en dernier -
                else if (lig.contains("@@")) {
                    out = "";
                }
                //Ajout de la balise de fermeture
                if (!"".contentEquals(out.trim()))
                    personsXML.add(out);
            }
        }catch (Exception e){
            String error = "Erreur pendant la création de l'intéressé " +e.getLocalizedMessage();
            log.error("***** {}", error);
            throw  new DataException(error);

        }

        try{
            for(Personne personne : getPersonnes()){
                String role = getEvelinRole(personne.getTypePersonne());
                if(role.equals("")){
                   continue;
                }

                personsXML.addAll(buildPersonXml(personne,getIdActePerson(personne),role));
                log.debug("construction XML de {}", role);
            }

        } catch (Exception e){
            log.error("Erreur pendant la construction du XML des personnes " );
        }
        return personsXML;
    }

    public String getIdActeInteresse(){
        return this.idActe + "_I";
    }

    /**
     * Génération ID pour la personne, pour Evelin. Il est composé de 3 éléments :
     * l'identifiant unique de l'école, de l'ID unique du dossier (du formulaire papier), de l'ID unique de l'enfant ou de la personne dans la base de donnée.
     * @param personne concernée pour la génération de l'id
     * @return l'ID indiqué dans l'acte ECW
     */
    @Override
    public String getIdActePerson( Personne personne) {
       dossier = this.enfant.getDossierByDossierId();
       return String.format("%07d_%s_%07d_%s",dossier.getEcoleByEcoleId().getId(),
                this.dossier.getNoDossier(),personne.getIdPersonne(), getEvelinTypePersonne(personne.getTypePersonne()));

    }

    /**
     * Formatage de l'ID pour l'acte, le met à jour dans l'acte en cours
     * Il est composé de l'identifiant de l'acte, de l'identifiant de l'enfant et de l'identifiant de la mère
     * Il sert d'external ID pour l'acte. Pour l'enfant, on suffixe ce numero par _I
     *
     */
    @Override
    public void buildIdActe(Enfant interesse) {

        try{
            this.idActe = String.format("%07d_%s_%07d", enfant.getDossierByDossierId().getEcoleByEcoleId().getId(),
                    enfant.getDossierByDossierId().getNoDossier(), enfant.getIdPersonne());

        }
        catch (NullPointerException e){
            String error = "Erreur lors de la génération de l'id Acte, donnée à null dossier Id et/ou enfant ID ";
            throw new DataException(error);
        }
        catch (Exception e){
            String error = "Erreur lors de la génération de l'id Acte "+ e.getLocalizedMessage();
            throw new DataException(error);
        }

    }

    @Override
    public TypeActe getTypeActe() {
        return this.typeActe;
    }


    @Override
    public Enfant getInteresse() {
        return this.enfant;
    }



    @Override
    public String getIdActe() {
        return this.idActe;
    }


    @Override
    public List<String> getBeanTemplate() throws IOException {
        return FileUtils.readLines (new ClassPathResource("evelinTemplate/naissance/template_birth.xml").getFile(), StandardCharsets.UTF_8);

    }

    @Override
    public List<String> getMetasTemplate() throws IOException {
        return FileUtils.readLines (new ClassPathResource("evelinTemplate/naissance/Metadata.xml").getFile(), StandardCharsets.UTF_8);

    }

    @Override
    public String getTag() {
        return this.tag;
    }


}
