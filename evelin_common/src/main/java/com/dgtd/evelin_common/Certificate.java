package com.dgtd.evelin_common;

import com.dgtd.common.error.ErrorObject;
import com.dgtd.common.error.ErrorService;
import com.dgtd.common.error.TypeError;
import com.dgtd.common.exception.DataException;
import com.dgtd.common.exception.WsBackOfficeException;
import com.dgtd.common.type.TypeActe;
import com.dgtd.common.type.TypePersonne;
import com.dgtd.common.util.Misc;
import com.dgtd.evelin_common.config.EvelinProperties;
import com.dgtd.evelin_common.config.ExplodeDate;
import com.dgtd.rdc.entity.Dossier;
import com.dgtd.rdc.entity.Ecole;
import com.dgtd.rdc.entity.Enfant;
import com.dgtd.rdc.entity.Personne;
import com.dgtd.rdc.localisation.entity.Localisation;
import com.dgtd.rdc.localisation.service.LocalisationService;
import com.dgtd.security.domain.entity.Utilisateur;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class Certificate<T>  implements CertificateDAO<T>, HandleCertificate{
    private static String TAG_ID = "@@ID@@";

    private final Logger log = LoggerFactory.getLogger(Certificate.class);
    protected final EvelinProperties evelinProperties;
    protected final ErrorService errorService;
    protected final LocalisationService localisationService;

    protected int currentNbActes;
    protected String idActe;
    protected TypeActe typeActe;
    protected String dateCourante;
    protected String heureCourante;

    protected String dateSaisie;

    protected List<String> beanTemplate;
    protected List<String> personTemplate;
    protected List<String> metaTemplate;

    protected Map<TypePersonne, Personne> personnes;




    public Certificate(EvelinProperties evelinProperties, ErrorService errorService,
                       LocalisationService localisationService) throws Exception {

        this.evelinProperties = evelinProperties;
        this.errorService = errorService;
        this.localisationService = localisationService;
        this.currentNbActes = 0;
        this.typeActe = null;
        this.dateCourante =generateEventDate();
        this.heureCourante=generateEventHeure();

        this.personTemplate = getPersonnesTemplate();
        this.beanTemplate = new ArrayList<>();
        this.metaTemplate = new ArrayList<>();

        this.idActe="";
        this.personnes = new HashMap<>();


    }

    public abstract List<String> getBeanTemplate() throws IOException;

    public abstract List<String> getMetasTemplate() throws IOException;

    public abstract String getTag();

    public abstract Collection<Personne> getPersonnes();

    public abstract String getDateSaisie();

    /**
     * méthode gérée au niveau de l'objet certificat final pour permettre de gérer l'ordonnancement
     * en fonction du contexte
     */
    protected abstract void PopulateMeta();


    @Override
    public String getDateCourante() {
        return this.dateCourante;
    }

    @Override
    public String generateBigMeta(Map<String, String> mtc) throws IOException {
        //Traitement des métas
        String bigMeta="";

        for(String mt : getMetasTemplate()) {
            if (mt.trim().length() == 0) continue;
            if (mt.startsWith("#")) continue;
            if (!mt.contains("=")) continue;

            //Récupération de la partie gauche des métas
            String[] parties = mt.split("=");
            if (mtc.containsKey(parties[0])) {
                String valeur = mtc.get(parties[0]);
                String app = parties[0] + "=" + valeur;

                if (bigMeta.equals("")) {
                    bigMeta = bigMeta + app;
                } else {
                    bigMeta = bigMeta + "|" + app;
                }
            }
        }
        return bigMeta;
    }

    public String generateBaseEventDate(){
        // 2 - Formatage de la date de l'évènement
        String pattern = "yyyyMMddHHmm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dte = simpleDateFormat.format(new Date());
        return dte;
    }

    public String generateEventDate(){
        String dte = generateBaseEventDate();
        this.dateCourante = dte.substring(0,8);
        return dateCourante;
    }

    protected String generateEventHeure(){
        String dte = generateBaseEventDate();
        this.heureCourante = dte.substring(8,12);
        return heureCourante;
    }

    protected List<String> getPersonnesTemplate() throws IOException {
        return FileUtils.readLines (new ClassPathResource("evelinTemplate/common/template_person.xml").getFile(), StandardCharsets.UTF_8);
    }

    /**
     *
     * @return liste de personnes reliées à l'acte dans une map avec pour clé TypePersonne
     */
    public Map<TypePersonne, Personne> generatePersonnesMap(){
        boolean isMother = false;
        boolean isDemandeur = false;
        Collection<Personne> collection = getPersonnes();
        for(Personne p :collection){
            String typePersonne = p.getTypePersonne();
            if (typePersonne.equals(TypePersonne.INTERESSE.getTypePersonne())){
                this.personnes.put(TypePersonne.INTERESSE, p);
            }else if(typePersonne.equals(TypePersonne.MERE.getTypePersonne())){
                this.personnes.put(TypePersonne.MERE, p);
                isMother = true;
            }else  if(typePersonne.equals(TypePersonne.PERE.getTypePersonne())){
                this.personnes.put(TypePersonne.PERE, p);
            }else if(typePersonne.equals(TypePersonne.DECLARANT.getTypePersonne())){
                this.personnes.put(TypePersonne.DECLARANT, p);
            }else if(typePersonne.equals(TypePersonne.CONJOINT.getTypePersonne())){
                this.personnes.put(TypePersonne.CONJOINT,p);
            }else if(typePersonne.equals(TypePersonne.TEMOIN1.getTypePersonne())){
                this.personnes.put(TypePersonne.TEMOIN1,p);
            }else if(typePersonne.equals(TypePersonne.TEMOIN2.getTypePersonne())){
                this.personnes.put(TypePersonne.TEMOIN2,p);
            }else if(typePersonne.equals(TypePersonne.DEMANDEUR.getTypePersonne())) {
                this.personnes.put(TypePersonne.DEMANDEUR, p);
                isDemandeur = true;
            }
        }
            if(!isMother){
                throw new DataException("Pas de mère enregistrée dans ce dossier ");
            }
            if(!isDemandeur){
                throw new DataException("Pas de demandeur enregistré dans ce dossier ");
            }

        return personnes;

    }

    protected void PopulateMetaCommon (Map<String,String> mtc,String tag, Dossier dossier)  {

        String year = this.dateCourante.substring(0,4);
        String dossierID = dossier.getNoDossier();
        mtc.put(tag + "SUIVI.NUM_DOSSIER_RN",  dossierID);

        String dateCreation = buildDate(dossier.getDateCreation(), "yyyyMMdd");
        mtc.put(tag + "SUIVI.DATE_DOSSIER_RN",dateCreation );
        mtc.put(tag+ "SUIVI.COM_DOSSIER_RN",StringEscapeUtils.escapeXml11(dossier.getCommentaire()));
        mtc.put(tag + "ACT.ACT_DECL_DATE", this.dateCourante);
        mtc.put(tag + "ACT.ACT_DECL_HOUR",this.heureCourante);
        mtc.put(tag + "ACT.ACT_NUM", "");
        mtc.put(tag + "REGISTRE.REG_YEAR",year );
        String agent = dossier.getNomPrenomUtilisateurAgent() == null ? "":dossier.getNomPrenomUtilisateurAgent();
        mtc.put(tag + "SUIVI.AGENT", StringEscapeUtils.escapeXml11(agent));
        mtc.put(tag+"SUIVI.DOSSIER","1");
        mtc.put(tag + "SUIVI.ECOLE", "1");
        log.debug("Meta common NoDossier :" + dossierID+" date courante "+ this.dateCourante +
                " heure courante " + this.heureCourante + " year " + year + " agent"+ agent);

    }

    protected void  PopulateMetaEcole(Map<String,String> mtc, String tag, Ecole ecole){
        mtc.put(tag + "SUIVI.NOM_ECOLE", StringEscapeUtils.escapeXml11(ecole.getNom()));
        mtc.put(tag + "SUIVI.INFO_ECOLE.CITY", StringEscapeUtils.escapeXml11(formatLieu(ecole.getLocalisationByLocalisationId(), ecole.getLocalisationAutre())));
        mtc.put(tag + "SUIVI.PROV_EDUC_ECOLE", StringEscapeUtils.escapeXml11(ecole.getEducationnelleByEducationnelleId().getProvince()));
        mtc.put(tag + "SUIVI.SOUS_DIV_ECOLE", StringEscapeUtils.escapeXml11(ecole.getEducationnelleByEducationnelleId().getSousDivision()));
        String typeEcole = getTypeEcole(ecole);
        mtc.put(tag + "SUIVI.TYPE_ECOLE", typeEcole);
        mtc.put(tag + "SUIVI.DISTRICT_ECOLE", StringEscapeUtils.escapeXml11(ecole.getDistrict()));
        mtc.put(tag + "SUIVI.QUARTIER_ECOLE", StringEscapeUtils.escapeXml11(ecole.getQuartier()));
        Utilisateur utilisateur = ecole.getUtilisateurByUtilisateurSuperId();
        String superviseur = "";
        if(utilisateur != null){
            superviseur = utilisateur.getIdentifiant();
        }

        mtc.put(tag + "SUIVI.SUPERVISEUR", StringEscapeUtils.escapeXml11(superviseur));
        String latitude = String.format("%3.6f",ecole.getLatitude());
        mtc.put(tag + "SUIVI.INFO_ECOLE.LATITUDE", StringEscapeUtils.escapeXml11(latitude));
        String longitude = String.format("%3.6f",ecole.getLongitude());
        mtc.put(tag + "SUIVI.INFO_ECOLE.LONGITUDE", StringEscapeUtils.escapeXml11(longitude));

    }

    /**
     * Ajoute dans le dictionnaire des valeurs les valeurs pour les différents types de personne.
     * @param enfant modèle de données de la BDD
     * @param enfantTag tag général indiquée dans les métadonnées du certificat pour l'enfant concerné
     */
    protected void PopulateMetaEnfant (Map<String,String> mtc,Enfant enfant, String enfantTag) {
        String tag = getTag() + enfantTag;
        ExplodeDate dNai = new ExplodeDate();
        dNai.Explode(enfant.getDateNaissance(), enfant.getHeureNaissance());

        String rep = enfant.getNom() + " " + enfant.getPostnom();
        mtc.put(tag+ "NAME", rep.trim());

        String prenom = (enfant.getPrenom() == null) ? "" : enfant.getPrenom().trim();
        mtc.put(tag + "FIRSTNAME", prenom);

        mtc.put(tag + "SEXE", enfant.getGenre().substring(0,1));
        mtc.put(tag + "IND_SEX", enfant.getGenre().substring(0,1));

        mtc.put(tag + "INFO_NAI.EVT_ADDRESS.CITY", formatLieu(enfant.getLocalisationByLocalisationNaissanceId(), enfant.getNaissanceAutre()));
        mtc.put(tag + "INFO_NAI.EVT_DATE", dNai.getDateExplodedEvelin());
        mtc.put(tag + "INFO_NAI.EVT_HOUR", dNai.getHeureExplodedEvelin());




    }

    /**
     * Ajoute dans le dictionnaire des valeurs les valeurs pour les différents types de personne.
     *
     //* @param mtc Dictionnaire contenaire tag/valeur
     * @param personne objet bdd
     * @param personTag Tag générique par rapport aux métas-données
     */
    protected void PopulateMetaPersonne (Map<String,String> mtc,Personne personne,String personTag ) {
        String tag = getTag()+personTag;
        mtc.put(tag + "NAME", StringEscapeUtils.escapeXml11(personne.getNom() + personne.getPostnom()));
        mtc.put(tag + "FIRSTNAME", StringEscapeUtils.escapeXml11(personne.getPrenom()));

        String job = ( (personne.getProfessionAutre() == null || "".contentEquals(personne.getProfessionAutre().trim()) ) ? personne.getProfession() : personne.getProfessionAutre());

        mtc.put(tag + "OCCUPATION", StringEscapeUtils.escapeXml11(job));
        mtc.put(tag + "INFO_DOM.CITY", StringEscapeUtils.escapeXml11(formatLieu(personne.getLocalisationByLocalisationResidenceId(), personne.getResidenceAutre() )));

        ExplodeDate dNai = new ExplodeDate();
        dNai.ExplodeDate(personne.getDateNaissance());

        mtc.put(tag + "INFO_NAI.EVT_DATE", dNai.getDateExplodedEvelin());

        String address = buildAddress(personne.getNumero(),personne.getAvenue(),personne.getQuartier());
        mtc.put(tag + "INFO_NAI.EVT_ADDRESS.CITY", formatLieu(personne.getLocalisationByLocalisationNaissanceId(),personne.getNaissanceAutre()));

        mtc.put(tag + "INFO_DOM.FORWARDING_ADDRESS", StringEscapeUtils.escapeXml11(address));

        String nationalite = (personne.getNationaliteAutre()==null || "".contentEquals(personne.getNationaliteAutre().trim()) ) ? personne.getNationalite() : personne.getNationaliteAutre();

        mtc.put(tag + "NATIONALITY", StringEscapeUtils.escapeXml11(nationalite));

        String sexe = personne.getGenre().equals("")?"": personne.getGenre().substring(0,1).toUpperCase();
        if(personne.getTypePersonne().equals(TypePersonne.MERE.getTypePersonne())|| personne.getQualite().contains("pere")){
           sexe = "F";
        }
        if(personne.getTypePersonne().equals(TypePersonne.PERE.getTypePersonne()) || personne.getQualite().contains("pere")){
           sexe = "M";
        }
        mtc.put(tag + "SEXE", sexe);
        mtc.put(tag + "IND_SEX", sexe);
        mtc.put(tag+ "TELEPHONE",personne.getTel());
        mtc.put(tag+ "EMAIL", personne.getEmail());

    }

    /**
     * Pilote la gestion des meta
     * Populate meta gère les spécificités de l'ordonnement de la création des méta au sein de chaque certificat final
     * @param mtc map des propriétés
     * @return un string contenant les métadonnées à intégrer tel quel dans l'acte brouillon
     */
    protected String handleMeta(Map<String, String> mtc) {
        String bigMeta = "";
        try{
            PopulateMeta();
            bigMeta = generateBigMeta(mtc);
            if(bigMeta.equals("")){
                throw new DataException("Aucune meta renseignée ");
            }

        }catch (Exception e){
            throw new DataException("Erreur lors de la construction des metadonnées " + e.getLocalizedMessage());
        }

        return bigMeta;
    }




    /**
     *
     * @param personne personne
     * @return partie XML concernant une personne
     * @throws IOException en cas d'erreur lors de la récupération du template
     */
    protected List<String> buildPersonXml (Personne personne, String id, String role) throws IOException {


        List<String> persons = new ArrayList<>();

        // l'interressé
        for (String lig :getPersonnesTemplate()) {

            String out = lig;

            if (lig.contains(TAG_ID)) {
                out = lig.replace(TAG_ID, id );
            }
            else if (lig.contains("@@ROLE@@")) {
                out = lig.replace("@@ROLE@@", role.toUpperCase());
            }
            else if (lig.contains("@@NOM@@")) {
                String rep = personne.getNom() + " " + personne.getPostnom();
                out = lig.replace("@@NOM@@",rep.trim());
            }
            else if (lig.contains("@@PRENOM@@")) {
                String prenom = (personne.getPrenom() == null) ? "" : personne.getPrenom().trim();
                out= lig.replace("@@PRENOM@@", prenom);
            }
            else if (lig.contains("@@GENRE@@")) {
                String genre =( personne.getGenre()==null || personne.getGenre().equals(""))?"":personne.getGenre().substring(0,1).toUpperCase();
                out = lig.replace("@@GENRE@@",genre);
            }
            // Ligne toujour en dernier -
            else if (lig.contains("@@")) {
                out = "";
            }

            if (! "".contentEquals(out.trim()) )
                persons.add(out);
        }

        return persons;
    }

    /**
     *
     * @param persons liste de personnes
     * @param bigmeta string de metadonnées construit
     * @param idActe id de l'évènement
     * @param eventDate date de l'événement
     * @param office numéro office qui permet de faire un tri dans Evelin
     * @return un corps de certificat XML
     * @throws IOException en cas d'erreur lors de la récupération du template principal
     */
    protected List<String> buildCertificateXML(List<String> persons, String bigmeta, String idActe,
                                               String eventDate, String office, String province) throws IOException {

        List<String> ret = new ArrayList<>();
        for (String lig : getBeanTemplate()) {

            String out = lig;

            // Liste de peronnes
            if (lig.contains("@@PERSONS@@")) {
                ret.addAll(persons);
                out = "";
            }
            // Le bloc meta
            else if (lig.contains("@@METADATAS@@")) {
                out = lig.replace("@@METADATAS@@",bigmeta);
            }
            // Autre zone
            else if (lig.contains(TAG_ID)) {
                out = lig.replace(TAG_ID, idActe);
            }
            else if (lig.contains("@@EVENT_DATE@@")) {

                out = lig.replace("@@EVENT_DATE@@", eventDate);
            }
            else if (lig.contains("@@EVENT_ANNEE@@")) {
                out = lig.replace("@@EVENT_ANNEE@@", eventDate.substring(0,4));
            }
            else if (lig.contains("@@OFFICE@@")) {
                out = lig.replace("@@OFFICE@@", office);
            }
            else if(lig.contains("@@PROVINCE_TITLE@@")){
                out = lig.replace("@@PROVINCE_TITLE@@", province.toUpperCase());
            }
            else if(lig.contains("@@PROVINCE_COUNTY@@")){
                out = lig.replace("@@PROVINCE_COUNTY@@", province.toLowerCase());
            }

            if (!"".contentEquals(out.trim()) )
                ret.add(out);
        }
        return ret;
    }



    /**
     * Formatage de la meta EVT adresse / lieu où se situe l'école suivant convention CityWeb
     * @param localisation Enregistrement localisation concerné
     * @param autreLocalisation complément texte pour la localisation
     * @return La chaine formatée
     */
    public String formatLieu(Localisation localisation, String autreLocalisation) {


        String ret = "";

        try {
            String autre = (autreLocalisation == null) ? "" : autreLocalisation.trim();

            // Check etranger
            // Dans ce mode, la chaine contient normalement VILLE (PAYS) dans la zone autreLocalisation
            if (localisation.getId() == evelinProperties.getIdEtranger()) {

                int po = autre.indexOf('(');
                int pf = autre.indexOf(')');
                if ((po != -1) && (pf != -1) && po > 0) {
                    ret = "##" + autre.substring(0, po-1) + "##" + autre.substring(po, pf);
                }
            }
            // Autre cas
            else {
                // Utilisation de "<autre à préciser>"
                if ("".contentEquals(localisation.getNiv2().trim()) ) {
                    ret = "##" + autre + "##" + evelinProperties.getTextePays();
                }
                // et finalement le cas standard
                else {
                    if (localisation.getId() == 0) {
                        //ret = evelinProperties.getTexteNonDefini() + "##" + "##" + evelinProperties.getTexteRdc();
                        //ret = "##" + "##" + evelinProperties.getTexteRdc();
                        if ("".contentEquals(autre))
                            ret = evelinProperties.getTexteNonDefini() + "##" + evelinProperties.getTexteNonDefini() + "##" +evelinProperties.getTextePays();
                        else {
                            //ret = autre + "##" + evelinProperties.getTexteNonDefini() + "##" + evelinProperties.getTexteRdc();
                            ret = autre + "####";
                        }
                    }
                    else {
                        String p1 = localisation.getNiv3();
                        int po = localisation.getNiv3().indexOf('(');
                        if (po > 0) {
                            p1 = localisation.getNiv3().substring(0, po);
                        }
                        ret = p1 + "##" + localisation.getNiv2() + "##" + evelinProperties.getTextePays();
                    }
                }
            }
        }
        catch (Exception ex) {
            String error =  "Erreur de formatage de localisation | id dossier = " +  " => " + " err=" + ex.getLocalizedMessage();
            errorService.insertError(new ErrorObject(TypeError.TRANSFERT_EVELIN, error),false);

            throw new WsBackOfficeException("Erreur de formatage de localisation err=" + ex.getLocalizedMessage());
        }

        return StringEscapeUtils.escapeXml11(ret);
    }


    protected String checkNonDefini(String nom) {

        if(nom==null || nom.equals("")){
            return "";
        }

        String cleanedNom = nom.trim().toLowerCase();
        String tocheck = Misc.getStringToCompare(cleanedNom);
        if(tocheck.contains("nondefinie")|| tocheck.startsWith("autre choix")){
            nom = "";
        }
        if(tocheck.startsWith("autre choix:")){
            return tocheck.replace("autre choix:","");
        }
        return nom;
    }


    /**
     * Gère les différences d'accent, d'espace et de casse
     * @param value chaine de caractère pour oui/non
     * @return 1 quand oui est trouvé et 0 dans les autres cas
     */
    protected String checkOuiNon(String value){
        String cleanedValue =Misc.getStringToCompare(value);
        if(cleanedValue.equals("oui")){
            return "1";
        }else return "0";
    }

    /**
     * Gère les différences d'accent, d'espace et de casse
     * @param value chaine de caractère pour connue, estimée, inconnu
     * @return 0 pour connue, 1 pour estimée et 2 pour le reste
     */
    protected String checkNiveauConnaissance(String value){
        String cleanedValue = Misc.getStringToCompare(value);
        if(cleanedValue.equals("connue")){
            return "0";
        }else if(cleanedValue.equals("estimée")){
            return "1";
        }else return "2";
    }

    /**
     * Gère les différences d'accent, d'espace et de casse
     * @param genre chaine de caractère Masculin / Féminin
     * @return chaine vide ou M / F
     */
    protected String checkGenre(String genre){
        String cleanedGenre = Misc.getStringToCompare(genre);
        if(cleanedGenre.equals("masculin")){
            return "M";

        }
        if(cleanedGenre.equals("feminin")){
            return "F";
        }
        return "";

    }

    /**
     *
     * Correspondance pour Evelin : Situation civile du défunt : 0=Célibataire 1=Autre 2=Sans réponse
     * @param value textuelle à comparer
     * @return code nécessaire pour Evelin 0=Célibataire 1=Autre 2=Sans réponse
     */
    protected String checkSituationCivile(String value){
        String cleanedValue = Misc.getStringToCompare(value);
        if(cleanedValue.equals("celibataire")){
            return "0";
        }
        if(!cleanedValue.equals("") || !cleanedValue.equals("sansreponse") || cleanedValue != null){
            return "2";

        }else return "1";
    }

    /**
     * Correspondance pour Evelin
     * @param value situation matrimoniale
     * @return  0=Marié 1=Veuf 2=Divorcé
     */
    public String checkSituationMatrimoniale(String value) {
        String cleanedValue = Misc.getStringToCompare(value);
        if (cleanedValue.equals("marie")) {
            return "0";
        } else if (cleanedValue.equals("veuf") || cleanedValue.equals("veuve")) {
            return "1";
        } else if (cleanedValue.equals("divorce")) {
            return "2";
        }
        return "";
    }

    /**
     * Transforme l'information répertoriée en BDD, en champ valide pour intégration dans Evelin
     * @param enfant modèle de données ou se trouve l'info
     * @return 1 scolarise, 0 non scolarise, "" si non renseigné
     */
    protected String transformIsScolarise(Enfant enfant){
        String isScolarise = enfant.getScolarise().trim();
        String response="";
        switch (isScolarise){
            case "Oui": response = "1";
                break;
            case "Non": response = "0";
                break;
                default: response = "";

        }
        return response;
    }

    /**
     * Translate le type d'école en code pour Evelin
     * 0=Maternelle 1=Primaire 2=Les deux
     * @param ecole ecole
     * @return 0, 1 ou 2
     */
    protected String getTypeEcole(Ecole ecole) {
        String isPrimaire = ecole.getEstPrimaire().trim();
        String isPrePrimaire = ecole.getEstPrePrimaire().trim();

        if(isPrimaire.equals("Oui") && isPrePrimaire.contains("Oui")){
            return "2";
        }else if(isPrimaire.equals("Non") && isPrePrimaire.equals("Oui")){
            return "0";
        }else return "1";
    }

    protected String getEvelinTypePersonne(String typePersonne){
        String tp;
        switch (typePersonne){
            case "Père" : tp= "F"; break;
            case "Mère" : tp = "M"; break;
            case "Conjoint" : tp = "C"; break;
            default: tp = ""; break;
        }
        return tp;
    }

    protected String getEvelinRole(String typePersonne){
        String tp;
        switch (typePersonne){
            case "Père" : tp= "FATHER"; break;
            case "Mère" : tp = "MOTHER"; break;
            case "Conjoint" : tp = "SPOUSE"; break;
            default: tp = ""; break;
        }
        return tp;
    }

    /**
     * Construction de la date de saisie en chaine de caractère suivant format utile pour Evelin
     * Pattern  "yyyyMMdd"
     * @param dateSaisie renseignée dans le dossier ou à défaut la date du jour
     * @return date de saisie
     */
    public String buildDate(Date dateSaisie, String pattern){
        String date="";
        // Préparation - date de saisie du dossier

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        if (dateSaisie != null)
            date = simpleDateFormat.format(dateSaisie);
        else
            date = simpleDateFormat.format(new Date().getTime());
        return date;
    }

    public String buildAddress(String numero,String rue, String quartier){
        String address = "";
        if(!numero.equals("")){
            address= numero;
        }
        if(!address.equals("")){
            address+= " ";
        }
        if(!rue.equals("")){
            address += rue ;
            if(!quartier.equals("")){
                address+= " "+ quartier;
            }
        }else{
            if(!quartier.equals("")){
                address+=quartier;
            }
        }


        return address;
    }




}
