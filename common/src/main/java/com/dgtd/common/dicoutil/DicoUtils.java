package com.dgtd.common.dicoutil;


import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Singleton pour la gestion des listes de valeur du questionnaire.
 */

public class DicoUtils {


    private final Logger log = LoggerFactory.getLogger(DicoUtils.class);

    // Les tags pour les listes
    public static String OUI_NON = "OUINON";
    public static String TYPE_QUALITE = "TYPE_QUALITE";
    public static String TYPE_SEXE = "TYPE_SEXE";
    public static String TYPE_PROFESSION = "TYPE_PROFESSION";
    public static String TYPE_NATIONALITE = "TYPE_NATIONALITE";
    public static String TYPE_PERSONNE = "TYPE_PERSONNE";
    public static String TYPE_MATRIMONIAL = "TYPE_MATRIMONIAL";
    public static String TYPE_FORMULAIRE = "TYPE_FORMULAIRE";

    private static ArrayList tableauDesTypes = new ArrayList();
    private static ListValeur listValeur = null;
    private static DicoUtils instance = new DicoUtils();

    public static DicoUtils getInstance() {
        return instance;
    }
    public ListValeurItemGroupe getListValeurItemGroupe(String tag) {
        return listValeur.GetListValeurGroupe(tag);
    }

    private boolean initOk;
    private String lastErr;

    public String getLastErr() {
        return lastErr;
    }

    public boolean isInitOk() {
        return initOk;
    }

    public void setLastErr(String lastErr) {
        this.lastErr = lastErr;
    }

    /**
     * Procede à l'initialisation du dico util et charge la structure ListValeur globale.
     *
     * La initOk du singleton informe de la bonne initialisation (ou non). Si non, la valeur "lastErr" contient l'erreur
     * ou l'exception.
     */


    private DicoUtils() {

        init();

        lastErr = "";
        initOk = false;

        loadDico();
    }

    /**
     * Initialisation
     */
    private void init () {
        Collections.addAll(tableauDesTypes,
                OUI_NON,
                TYPE_QUALITE,
                TYPE_SEXE,
                TYPE_PROFESSION,
                TYPE_NATIONALITE,
                TYPE_PERSONNE,
                TYPE_MATRIMONIAL,
                TYPE_FORMULAIRE);

        for (int i = 0; i < tableauDesTypes.size(); i++) {
            tableauDesTypes.set(i, tableauDesTypes.get(i).toString().toLowerCase());
        }

        listValeur = new ListValeur();
    }

    /**
     * Chargement du dico
     */
    private void loadDico() {

        try {
            File file = new ClassPathResource("definitions/dicoutil.JSON").getFile();
            String content = FileUtils.readFileToString(file, "UTF8");

            JSONObject jsonObj = new JSONObject(content);
            JSONArray type;

            for (int i = 0; i < tableauDesTypes.size(); i++) {
                type = jsonObj.getJSONArray(tableauDesTypes.get(i).toString());
                ListValeurItemGroupe listeValeur = new ListValeurItemGroupe(tableauDesTypes.get(i).toString().toUpperCase());
                for (int j = 0; j < type.length(); j++) {
                    JSONObject noeud = type.getJSONObject(j);

                    int id = noeud.getInt("id");
                    String nom = noeud.getString("nom");
                    boolean defaut = noeud.getBoolean("defaut");
                    int ordre = noeud.getInt("ordre");
                    int devTagInt = noeud.getInt("devtag");
                    listeValeur.AddListValeurItem(new ListValeurItem(id,nom,defaut,ordre,devTagInt));

                }
                listValeur.AddNewListItemGroupe(tableauDesTypes.get(i).toString().toUpperCase(), listeValeur);
            }

            initOk = true;
        }

        catch (Exception ex) {
            initOk = false;
            lastErr = ex.getLocalizedMessage();
            log.error("****** "+ lastErr);
        }
    }

    /**
     * Fonction de récupération de l'intitulé correspondant à l'id en fonction du type (tag)
     * @param tag type de champ concerné
     * @param id du type de champ
     * @return intitulé correspondant dans la liste dicoutil.json
     */
    public String getIntituleFromDicoutil(String tag, int id){

        ListValeurItem lvForm= null;
        try{
            // DicoUtils dicoUtils = getInstance();
            lvForm = getInstance().getListValeurItemGroupe(tag)
                    .getListValeurItemFromId(id);

        }catch (Exception e){
            log.error("Pas de correspondance de l'identifiant avec le tag DicoUtil " + getLastErr());
            return e.getMessage();
        }

        String intitule = lvForm.getIntitule();
        return intitule;
    }


}
