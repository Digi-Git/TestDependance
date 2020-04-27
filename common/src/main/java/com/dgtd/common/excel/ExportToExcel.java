package com.dgtd.common.excel;


import com.dgtd.common.config.DigitechProperties;
import com.dgtd.common.exception.DataException;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;


@Service
public class ExportToExcel {
    private final Logger log = LoggerFactory.getLogger(ExportToExcel.class);

    DigitechProperties digitechProperties;
    ExportUtils exportUtils;


    public ExportToExcel(DigitechProperties digitechProperties, ExportUtils exportUtils){
        this.digitechProperties = digitechProperties;
        this.exportUtils = exportUtils;

    }


    /**
     *
     * @param autosize boolean pour définir si la taille des colonnes est ajustée au titre ou non
     * @param objects liste des données à intégrer dans le fichier excel
     * @param modeles données qui définissent la structure du fichier
     * @param classe qui permet de définir quelle paramètre de l'objet va dans quelle colonne du fichier excel
     * @return le fichier construit
     * @throws IOException
     * @throws IllegalAccessException
     * @throws IntrospectionException
     * @throws InvocationTargetException
     *
     * @TODO paramètre boolean enregistrement sur disque du fichier construit à ajouter
     */
    public File export(boolean autosize, List<? extends  Object> objects, List<MappingModele> modeles, Class classe) throws Exception {

        if(objects.size()==0){
            throw new DataException("Pas de données à exporter ");
        }
        for(MappingModele mappingModele : modeles){
            exportUtils.appendModele(mappingModele);
           // exportUtils.prepaExport(mappingModele);
        }

        Workbook workbook = exportUtils.processDataExcel(objects,classe,autosize,digitechProperties.getExcelLocalDir());
        String excelPath = exportUtils.export(workbook, classe);
        return new File(excelPath);
    }
}


