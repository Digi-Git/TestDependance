package com.dgtd.common.excel;

import com.dgtd.common.config.DigitechProperties;
import com.dgtd.common.exception.DataException;
import com.dgtd.common.exception.WsBackOfficeException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ExportUtils {
    private final Logger log = LoggerFactory.getLogger(ExportUtils.class);

    // Liste des modèles
    private Map<String,MappingModele> _modeles;

    private final Element element;
    private final DigitechProperties digitechProperties;

    // Das constructor
    public ExportUtils(Element element, DigitechProperties digitechProperties) {
        this.element = element;
        this.digitechProperties = digitechProperties;
        this._modeles = new LinkedHashMap<>();
    }

    /**
     *
     * @param list des objets à traiter
     * @param classe type d'objet contenus dans la liste
     * @param autosize boolean - ajustement de la taille de la colonne au titre
     * @param targetPath lieu de stockage du fichier
     * @return chemin de stockage du fichier excel créé
     * @throws IOException pour le modèle mapping
     * @throws IllegalAccessException propriétés protégées
     * @throws IntrospectionException erreur lors de la récupération des données contenues dans l'objet
     * @throws InvocationTargetException problème d'accès au répertoire défini
     */
    public Workbook processDataExcel(List<? extends Object> list, Class classe, boolean autosize, String targetPath) throws Exception {
        if (_modeles.isEmpty()) {
            throw new DataException("Pas de modèle disponible pour effectuer le mapping");
        }

        Workbook workbook = new XSSFWorkbook();


        //Traitement onglet
        for (Map.Entry<String, MappingModele> mappingModeleEntry : _modeles.entrySet()) {
            MappingModele mappingModele = mappingModeleEntry.getValue();

            Sheet sheet = workbook.createSheet(mappingModele.getTitre());
            int row = 0;
            int col = 0;
            int nbcol = 0;
            int subrow = 1;

            Row headerRow = sheet.createRow(0);

            //Intégration des champs d'entête
            for (MappingExportChamp mi : mappingModele.getMappings()) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(mi.getColonne());
                cell.setCellStyle(getStyle(workbook));
                col++;
                nbcol++;
            }

            //Ajout des données relatives à l'Audit (généralisé avec DBEntity : possibilité d'étendre le code pour
            //gérer d'autres entités
            for (Object obj : list) {

                row++;
                col = 0;

                HashMap<String, String> propValue = new HashMap<>();
                try {
                    //Gestion de l'com.dgtd.rdc.localisation.entity et jointure sans retour de collection
                    if (!mappingModele.isSubLevel()) {
                        Row aRow = sheet.createRow(row);

                        //String tag = mappingModele.getTag();
                        Map<String, Object> amap;

                        amap = getDataValue("",obj.getClass() , obj, propValue);


                        List<String> lignes = getMappingData(mappingModele, amap);
                        int size = lignes.size();
                        for (int i = 0; i < size; i++) {

                            Cell cell = aRow.createCell(col);
                            cell.setCellValue(lignes.get(i));
                            col++;
                            log.debug("mapping model " + mappingModele.getTag() + " prop : " + lignes.get(i));

                        }
                        //Gestion des collections de jointures
                    } else {

                        List<List<String>> lst = getListValue(mappingModele, obj);
                        int size = lst.size();
                        List<String> liste;
                        for (int i = 0; i < size; i++) {
                            Row ligne = sheet.createRow(subrow);
                            subrow++;
                            col = 0;
                            liste = lst.get(i);

                            for (String va : liste) {
                                Cell cell = ligne.createCell(col);
                                cell.setCellValue(va);
                                col++;
                                log.debug("mapping model " + mappingModele.getTag() + " prop : " + va);
                            }
                        }
                    }


                } catch (Exception e) {
                    log.error("***** Erreur lors de la création du fichier Excel");
                    throw new Exception("Erreur lors de la création du fichier Excel " + e.getLocalizedMessage());
                }
            }

            if (autosize) {
                for (int i = 0; i < nbcol; i++) {
                    sheet.autoSizeColumn(i);
                }
            }

        }
        return workbook;
    }
    /*

        /**
         * Génération des onglets Excel
         * @param objects Le resultat de la recherche issue de la requete initiale (idem processDataExcel)
         * @param workbook Workbook excel courant
         * @throws IntrospectionException
         * @throws IllegalAccessException
         * @throws WsBackOfficeException
         * @throws InvocationTargetException
         */
    /*    private void genOngletExcel (List < ? extends Object > objects, Workbook workbook,boolean autoSize) throws
        IntrospectionException, IllegalAccessException, WsBackOfficeException, InvocationTargetException {

            for (MappingModele mo : _modeles) {

                if (!mo.isSubLevel()) continue;


                Sheet subsheet = workbook.createSheet(mo.getTitre());
                int subrow = 0;
                int subcol = 0;
                int nbcol = 0;
                Row aRow = subsheet.createRow(subrow);

                for (MappingExportChamp mi : mo.getMappings()) {
                    Cell cell = aRow.createCell(subcol);
                    cell.setCellValue(mi.getColonne());
                    subcol++;
                    nbcol++;
                }


                // Peuplement de l'onglet
                for (Object obj : objects) {

                    List<List<String>> lst = null;
                    try {
                        lst = getListValue(mo, obj);
                    } catch (ClassNotFoundException e) {
                        log.error("Impossible de retrouver la classe ");
                    }

                    for (List ls : lst) {

                        subrow++;
                        subcol = 0;
                        Row lRow = subsheet.createRow(subrow);

                        for (Object va : ls) {

                            Cell cell = lRow.createCell(subcol);
                            cell.setCellValue(va.toString());
                            subcol++;

                            //log.info("prop list : " + va.toString());
                        }
                    }
                }

                if (autoSize) {
                    for (int i = 0; i < nbcol; i++) {
                        subsheet.autoSizeColumn(i);
                    }
                }
            }
        }

     */

    public String export(Workbook workbook, Class classe){

        String targetPath = digitechProperties.getExcelLocalDir();
        String targetFile;

        //Si l'export excel concerne le récapitulatif des erreurs, pas de sauvegarde de l'historique sur le serveur
        //on met toujours le même nom de fichier qui écrase le précédent
        if(classe.getName().contains("error")){
            targetFile = FilenameUtils.concat(targetPath,"errors.xlsx");
            //autrement on précise la date et la classe génrérique et la date pour avoir un fichier unique
        }else{
            // Sauvegarde
            SimpleDateFormat sdfr = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
            String dateFic = sdfr.format(new Date());
            targetFile = FilenameUtils.concat(targetPath, "exports_" + classe.getName()+"_"+ dateFic + ".xlsx");
        }


        // Création du dossier de sortie
        try {
            FileUtils.forceMkdir(new File(targetPath ));
        }
        catch (Exception ex) {
            throw new SecurityException(ex.getLocalizedMessage());
        }

        FileOutputStream fileOut ;
        try {
            fileOut = new FileOutputStream(targetFile);
        } catch (FileNotFoundException e) {
            String message = "Fichier excel à exporter non disponible avec le chemin indiqué "+ targetFile;
            log.error(message);
            throw new DataException(message);
        }
        try {
            workbook.write(fileOut);
        } catch (IOException e) {
            String message = "Fichier excel à exporter non disponible";
            log.info(message);
            throw  new DataException(message);
        }
        try {
            fileOut.close();
        } catch (IOException e) {
            log.info("*** Erreur lors de la fermeture du workbook (File outputStream) ");
            e.printStackTrace();
        }
        try{
            workbook.close();
        }catch (IOException e){
            log.error("***** Erreur lors de la fermeture du workbook " + e.getLocalizedMessage());
        }
        return targetFile;
    }

        /**
         * Traitement spécifique pour les listes de valeurs (personnel, équipement, ...)
         * @param mo Modèle (mapping) concerné
         * @param element Objet concerné (objet audit)
         * @return Une liste de liste de chaine de caracteres. La List inférieure contient les champs, la liste supérieure correspond en fait aux lignes
         * @throws IntrospectionException
         * @throws InvocationTargetException
         * @throws IllegalAccessException
         * @throws WsBackOfficeException
         */
        private List<List<String>> getListValue (MappingModele mo, Object element) throws
                Exception {

            HashMap<String, String> propValue = new HashMap<>();
            BeanInfo beanInfo = Introspector.getBeanInfo(element.getClass());
            List<List<String>> glo = new ArrayList<>();

            for (PropertyDescriptor propertyDesc : beanInfo.getPropertyDescriptors()) {

                // Recherche de la propriété du sous modèle
                String propertyName = propertyDesc.getName();

                if (propertyName.contentEquals(mo.getTag())) {

                    if (propertyDesc.getPropertyType().getName() == "java.util.Collection") {

                        Object value = propertyDesc.getReadMethod().invoke(element);

                        // Traitement de la liste
                        for (Object o : (Collection) value) {

                            getDataValue("", o.getClass(), o, propValue);
                            glo.add(getMappingData(mo, propValue));
                        }
                    }
                }
            }

            return glo;
        }
/*
        /**
         * Convertit un objet (audit ou l'une de ses sous classe) vers une map de type nom propriété / valeur.
         *
         * @param prefix Vide pour la classe principal. Nom de la propriété dans le cas contraire, par exemple localisation.niv_1 si la propriété de classe Localisation s'appelle localisation.
         * @param element Objet à inspecter.
         * @param amap Map container.
         * @throws IntrospectionException
         * @throws InvocationTargetException
         * @throws IllegalAccessException
         * @TODO Le filtre sur les sous classes est rudimentaire. Voir pour améliorer le principe.
         * @TODO Problèmes d'obtention des propriétés du au EAGER. Améliorer la requete "criteria" pour flusher les sous classes nécéssaire.
         */
        /*private void getDataValue (String prefix, Class classe, Object element, HashMap amap) throws
        IntrospectionException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {

            BeanInfo beanInfo = Introspector.getBeanInfo(Class.forName(classe.getName()));

            for (PropertyDescriptor propertyDesc : beanInfo.getPropertyDescriptors()) {

                String propertyName = prefix + propertyDesc.getName();
                Object value = propertyDesc.getReadMethod().invoke(element);

                String tp = propertyDesc.getPropertyType().getName();

                // Si sous classe
                if (tp.contains("domain.postgres.")) {
                    //TODO ajouter List de paramètres pour rendre générique
                    // Classe retenue. Attention, il faut qu'elles soient fetchées via la requete
                    // Les autres sont ignorées.
                    if ("".contentEquals(prefix) && tp.endsWith("Localisation") ||
                            "".contentEquals(prefix) && tp.endsWith("Dossier") ||
                            "".contentEquals(prefix) && tp.endsWith("Utilisateur") ||
                            "dossierByDossierId".contentEquals(prefix) && tp.endsWith("Ecole")

                    ) {
                        getDataValue(propertyName + ".", value.getClass(), value, amap);
                    }

                    // Object de base - modification et ajout dans la hashmap
                } else {

                    String valeur = "";

                    if ((value != null) && (tp != "java.util.Collection")) {

                        // Traitement pour du joli
                        if (tp == "java.util.Date") {
                            SimpleDateFormat sdfr = new SimpleDateFormat("dd/MM/yyyy");
                            valeur = sdfr.format(value);
                        } else if ((tp.contentEquals("java.lang.Double")) || (tp.contentEquals("java.lang.Float"))) {
                            valeur = String.format("%f", value);
                        } else {
                            valeur = value.toString();
                        }
                    }

                    amap.put(propertyName, valeur);
                }
            }
        }
        */
    /**
     * Convertit un objet (audit ou l'une de ses sous classe) vers une map de type nom propriété / valeur.
     *
     * @param prefix Vide pour la classe principal. Nom de la propriété dans le cas contraire, par exemple localisation.niv_1 si la propriété de classe Localisation s'appelle localisation.
     * @param object Objet à inspecter.
     * @param amap Map container.
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * TODO faire une fonction de comparaison si class et objet prefix sont identique enlever object
     */
    private Map<String,Object> getDataValue ( String prefix ,  Class classe,Object object, Map amap) throws Exception,
            IntrospectionException, InvocationTargetException, IllegalAccessException {


        if(object !=null){
            BeanInfo beanInfo = Introspector.getBeanInfo(Class.forName(classe.getName()));
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            prefix = beanInfo.getBeanDescriptor().getName();
            element.setClassPrefix(prefix);

            for (PropertyDescriptor propertyDesc : propertyDescriptors) {
                if(propertyDesc.getPropertyType().equals(Element.getBaseClass())){
                    element.eraseObjectPrefix();
                }
                String currentTp = beanInfo.getBeanDescriptor().getName();
                if(!currentTp.equals(prefix)){
                    prefix = beanInfo.getBeanDescriptor().getName();
                    element.setClassPrefix(prefix);
                    element.eraseObjectPrefix();
                }
                String propertyName =  propertyDesc.getName();

                Object value = propertyDesc.getReadMethod().invoke(object);
                String tp = propertyDesc.getPropertyType().getName();
                //Class<?> tp = propertyDesc.getPropertyType();
                element.setProperty(tp);
                Boolean primitive = propertyDesc.getPropertyType().isPrimitive();
                if(primitive || tp.endsWith("String")|| tp.endsWith("Date")|| tp.endsWith("Integer")
                        ||tp.endsWith("Short")|| tp.endsWith("Double") || tp.contains("Type")){
                    element.setProperty(propertyName);
                    insertPrimitiveValue(element.getElement(),tp,value,amap);
                    continue;
                }




                //String currentEntity[] = propertyName.split("\\.");
                String currentEntity[] = propertyDesc.getReadMethod().toString().split("\\.");
                int size = currentEntity.length;
                String currentClass = currentEntity[size-2];
               /*if(!currentClass.equals(element.getClassPrefix())){
                   getDataValue(prefix,com.dgtd.rdc.localisation.entity,amap);
               }

                         */
                // Si sous classe
                if(tp.endsWith("Collection")||tp.endsWith("Class") ){

                    continue;

                }
                else if ((tp.endsWith("com.dgtd.rdc.localisation.entity.Localisation"))||
                        (tp.endsWith("com.dgtd.rdc.localisation.entity.Utilisateur")) ||
                        (tp.endsWith("com.dgtd.rdc.localisation.entity.Audit")) ||
                        (tp.endsWith("com.dgtd.rdc.localisation.entity.Centre")) ||
                        (tp.endsWith("com.dgtd.rdc.localisation.entity.TypeCentre"))||
                        (tp.endsWith("com.dgtd.rdc.localisation.entity.LocaliteRattachment")) ||
                        (tp.endsWith("com.dgtd.rdc.localisation.entity.BsecStructureSanitaire")) ||
                        (tp.endsWith("TypeError"))){


                    if(!element.getClassPrefix().equals(Element.getBaseClass()) && !currentClass.equals(element.getClassPrefix())){
                        element.setClassPrefix(Element.getBaseClass());
                        element.setObjectPrefix(propertyName);
                        String prefixTab[] =  propertyDesc.getPropertyType().getName().split("\\.");
                        int sizePrefixTab = prefixTab.length;
                        prefix = prefixTab[sizePrefixTab-1];
                    }//remise à zéro de la propriété
                    element.setProperty("");
                    getDataValue(prefix,value.getClass(), value, amap);




                    // Object de base - modification et ajout dans la hashmap
                } else {
                    log.info("****** Je passe dans le else  " + currentClass + " non retrouvée");



                }
            }

        }
        return amap;
    }


        /**
         * Création d'une ligne export par match de la map des données extraites pour un objet et de la configuration du mapping
         * @param modele
         * @param amap
         * @return
         * @throws WsBackOfficeException
         */
        private List<String> getMappingData (MappingModele modele, Map amap) throws DataException {

            List<String> ligne = new ArrayList<>();

            // Par propriét, recheche dans le mapping (on pourrait faire l'inverse)
            for (MappingExportChamp mapping : modele.getMappings()) {
                String key = mapping.getPropriete();
               // String valeur = (String) amap.get(mapping.getPropriete());
                String valeur = (String) amap.get(key);
                if (valeur != null)
                    ligne.add(valeur);
                else {
                    String message = "Propriété " + mapping.getPropriete() +
                            " non retrouvée dans la map : " + modele.getTitre();
                    log.error("***** "+ message);
                    throw new DataException(message);
                }
            }

            return ligne;
        }

        /**
         * Construction de la liste des modèles à exporter.
         * @param modele
         */
        public void appendModele (MappingModele modele){
            String key = modele.getTag();
            try {
                MappingModele mappingModele = _modeles.get(key);
                if (mappingModele == null) {
                    log.info("Ajout du mapping " + modele.getTitre());
                    _modeles.put(key, modele);
                    prepaExport(modele);
                }

            } catch (Exception e) {
                String error = "Erreur lors de l'import du modèle dédié à l'export sur excel " + e.getLocalizedMessage();
                log.error("***** " + error);
                throw new DataException(error);
            }
        }

        /**
         * Construction de la liste des modèles.
         * @throws WsBackOfficeException
         * @throws IOException
         */
        private void prepaExport (MappingModele mappingModele) throws WsBackOfficeException, IOException {

            /*if (_modeles.size() > 0) {
                for(Map.Entry<String, MappingModele> mappingModeleEntry : _modeles.entrySet()){
                    if(mappingModeleEntry.getKey().equals(mappingModele.getTag())){
                        return;
                    }

            }
               /* for (Map.Entry<String, MappingModele> mappingModeleEntry : _modeles.entrySet()) {
                    MappingModele modele = mappingModeleEntry.getValue();

                */
                    // Vérification existance fichier
                    if (!mappingModele.getFile().exists()) {
                        log.error("Fichier modele " + mappingModele.getFile().getName() + " inexistant");
                        throw new WsBackOfficeException("Fichier modele " + mappingModele.getFile().getName() + " inexistant");
                    }

                    // Chargement du fichier module
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(mappingModele.getFile()), "UTF-8"));
                    String line = "";
                    while ((line = buffer.readLine()) != null) {

                        String[] str = line.split("\t");
                        if (str.length == 2) {

                            MappingExportChamp mec = new MappingExportChamp(str[0].trim(), str[1].trim());
                            mappingModele.getMappings().add(mec);
                        }
                    }

                    buffer.close();
             //   }
            //}
        }

        public CellStyle getStyle(Workbook workbook){
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setFontName("Arial");
            style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_50_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            font.setBold(true);
            font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
            style.setFont(font);
            return style;
        }

    private Map<String,Object> insertPrimitiveValue(String prefix, String tp,Object value, Map amap){

        String valeur = "";

        if (value != null) {

            // Traitement pour du joli
            if (tp == "java.util.Date") {
                SimpleDateFormat sdfr = new SimpleDateFormat("dd/MM/yyyy");
                valeur = sdfr.format(value);
            }
            else if ( (tp.contentEquals("java.lang.Double")) || (tp.contentEquals("java.lang.Float")) ) {
                valeur = String.format("%f", value);
            }
            else {
                valeur = value.toString();
            }
        }

        amap.put(prefix, valeur);


        return amap;

    }
}
