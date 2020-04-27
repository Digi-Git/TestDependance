package com.dgtd.report.excel;

import com.dgtd.common.exception.WsBackOfficeException;
import com.dgtd.rdc.entity.Enfant;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExportUtils {

    private final Logger log = LoggerFactory.getLogger(ExportUtils.class);

    // Liste des modèles
    private List<MappingModele> _modeles;

    // Das constructor
    public ExportUtils() {
        this._modeles = new ArrayList<>();
    }

    /**
     *
     * @param enfants Enfant a exporter sur Excel
     * @param autoSize
     * @param targetPath
     * @return
     * @throws IllegalAccessException
     * @throws WsBackOfficeException
     * @throws InvocationTargetException
     * @throws IntrospectionException
     * @throws IOException
     */
    public String processDataExcel (List<Enfant> enfants, boolean autoSize, String targetPath) throws IllegalAccessException, WsBackOfficeException, InvocationTargetException, IntrospectionException, IOException {


        if (_modeles.isEmpty())
            return null;

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(_modeles.get(0).getTitre());

        // Onglet principal
        int row = 0;
        int col = 0;
        int nbcol = 0;

        Row headerRow = sheet.createRow(0);
        for ( MappingExportChamp mi : _modeles.get(0).getMappings()) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(mi.getColonne());
            col++;
            nbcol++;
        }

        // Génération onglet 0
        for (Enfant enfant : enfants) {

            row++;
            col=0;
            Row aRow = sheet.createRow(row);

            HashMap<String, String> propValue = new HashMap<>();

            getDataValue("", enfant, propValue);

            for ( MappingModele mo : _modeles ) {

                // Traitement du premier niveau
                if (!mo.isSubLevel()) {
                    List<String> lst = getMappingData (mo, propValue);

                    for (String va : lst) {
                        Cell cell = aRow.createCell(col);
                        cell.setCellValue(va);
                        col++;
                        //log.info("prop : " + va);
                    }
                }
            }
        }

        if (autoSize) {
            for (int i = 0; i < nbcol ; i++) {
                sheet.autoSizeColumn(i);
            }
        }


        // Génération des onglets
        genOngletExcel(enfants, workbook, autoSize);

        // Sauvegarde
        SimpleDateFormat sdfr = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        String dateFic = sdfr.format(new Date());
        String targetFile = FilenameUtils.concat(targetPath, "exports_" + dateFic + ".xlsx");

        // Création / purge du dossier de sortie
        try {
            FileUtils.forceMkdir(new File(targetPath));
        }
        catch (Exception ex) {
            throw new SecurityException(ex.getLocalizedMessage());
        }

        FileOutputStream fileOut = new FileOutputStream(targetFile);
        workbook.write(fileOut);
        fileOut.close();
        return targetFile;
    }

    /**
     * Génération des onglets Excel
     * @param enfants Le resultat de la recherche issue de la requete initiale (idem processDataExcel)
     * @param workbook Workbook excel courant
     * @throws IntrospectionException
     * @throws IllegalAccessException
     * @throws WsBackOfficeException
     * @throws InvocationTargetException
     */
    private void genOngletExcel (List<Enfant> enfants, Workbook workbook, boolean autoSize) throws IntrospectionException, IllegalAccessException, WsBackOfficeException, InvocationTargetException {

        for ( MappingModele mo : _modeles ) {

            if (!mo.isSubLevel()) continue;


            Sheet subsheet = workbook.createSheet(mo.getTitre());
            int subrow = 0;
            int subcol = 0;
            int nbcol = 0;
            Row aRow = subsheet.createRow(subrow);

            for ( MappingExportChamp mi : mo.getMappings()) {
                Cell cell = aRow.createCell(subcol);
                cell.setCellValue(mi.getColonne());
                subcol++;
                nbcol++;
            }

            // Peuplement de l'onglet
            for (Enfant enfant : enfants) {

                List<List<String>> lst = getListValue(mo, enfant);

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
                for (int i = 0; i < nbcol ; i++) {
                    subsheet.autoSizeColumn(i);
                }
            }
        }
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
    private List<List<String>> getListValue (MappingModele mo, Object element) throws IntrospectionException, InvocationTargetException, IllegalAccessException, WsBackOfficeException {

        HashMap<String, String> propValue = new HashMap<>();
        BeanInfo beanInfo = Introspector.getBeanInfo(element.getClass());
        List<List<String>> glo = new ArrayList<>();

        for (PropertyDescriptor propertyDesc : beanInfo.getPropertyDescriptors()) {

            // Recherche de la propriété du sous modèle
            String propertyName = propertyDesc.getName();

            if ( propertyName.contentEquals(mo.getTag()) ) {

                if (propertyDesc.getPropertyType().getName() == "java.util.Collection") {

                    Object value = propertyDesc.getReadMethod().invoke (element);

                    // Traitement de la liste
                    for (Object o : (Collection)value) {

                        getDataValue("",o, propValue);
                        glo.add(getMappingData(mo, propValue));
                    }
                }
            }
        }

        return glo;
    }

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
    private void getDataValue (String prefix, Object element, HashMap amap) throws IntrospectionException, InvocationTargetException, IllegalAccessException {

        BeanInfo beanInfo = Introspector.getBeanInfo(element.getClass());

        for (PropertyDescriptor propertyDesc : beanInfo.getPropertyDescriptors()) {

            String propertyName = prefix + propertyDesc.getName();
            Object value = propertyDesc.getReadMethod().invoke(element);

            String tp = propertyDesc.getPropertyType().getName();

            // Si sous classe
            if (tp.contains("domain.postgres.")) {

                // Classe retenue. Attention, il faut qu'elles soient fetchées via la requete
                // Les autres sont ignorées.
                if (    "".contentEquals(prefix) && tp.endsWith("Localisation") ||
                        "".contentEquals(prefix) && tp.endsWith("Dossier") ||
                        "".contentEquals(prefix) && tp.endsWith("Utilisateur") ||
                        "dossierByDossierId".contentEquals(prefix) && tp.endsWith("Ecole")

                ) {
                    getDataValue (propertyName + ".", value, amap);
                }

                // Object de base - modification et ajout dans la hashmap
            } else {

                String valeur = "";

                if ( (value != null) && (tp != "java.util.Collection") ) {

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

                amap.put(propertyName, valeur);
            }
        }
    }

    /**
     * Création d'une ligne export par match de la map des données extraites pour un objet et de la configuration du mapping
     * @param modele
     * @param amap
     * @return
     * @throws WsBackOfficeException
     */
    private List<String> getMappingData (MappingModele modele, HashMap amap) throws WsBackOfficeException {

        List<String> ligne = new ArrayList<>();

        // Par propriét, recheche dans le mapping (on pourrait faire l'inverse)
        for (MappingExportChamp mapping : modele.getMappings()) {

            String valeur = (String) amap.get(mapping.getPropriete());
            if (valeur != null)
                ligne.add(valeur);
            else {
                log.error("export : propriété non retrouvée dans la map : " + modele.getTitre() + " -> " + mapping.getPropriete());
                throw new WsBackOfficeException("export : propriété non retrouvée dans la map : " + modele.getTitre() + " -> " + mapping.getPropriete());
            }
        }

        return ligne;
    }

    /**
     * Construction de la liste des modèles à exporter.
     * @param modele
     */
    public void appendModele (MappingModele modele) {
        _modeles.add(modele);
    }

    /**
     * Construction de la liste des modèles.
     * @throws WsBackOfficeException
     * @throws IOException
     */
    public void prepaExport () throws WsBackOfficeException, IOException {

        for (MappingModele modele : _modeles) {

            // Vérification existance fichier
            if (!modele.getFile().exists()) {
                log.error("Fichier modele " + modele.getFile().getName() + " inexistant");
                throw new WsBackOfficeException("Fichier modele " + modele.getFile().getName() + " inexistant");
            }

            // Chargement du fichier module
            BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(modele.getFile()), "UTF-8"));
            String line = "";
            while ((line = buffer.readLine()) != null) {

                String[] str = line.split("\t");
                if (str.length == 2) {

                    MappingExportChamp mec = new MappingExportChamp(str[0].trim(), str[1].trim());
                    modele.getMappings().add(mec);
                }
            }

            buffer.close();
        }
    }


}
