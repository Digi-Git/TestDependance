package com.dgtd.report.excel;

import com.dgtd.common.exception.WsBackOfficeException;
import com.dgtd.rdc.entity.Enfant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;


@Service
public class ExportExcelReportService {

    private final Logger log = LoggerFactory.getLogger(ExportExcelReportService.class);

    @PersistenceContext
    private EntityManager entityManager;


    @Autowired
    private ExcelProperties excelProperties;

    /**
     * Point d'entrée pour l'export Excel
     * @param autosize
     * @return
     * @throws InvocationTargetException
     * @throws IntrospectionException
     * @throws IllegalAccessException
     * @throws WsBackOfficeException
     * @throws IOException
     * @TODO Completer la critéria pour ajouter les personnes. L'école normalement remonte.
     * @TODO Pour les tests, l'export est limité aux premiers enfants (nb idem synchro Evelin)
     */
    public File export (boolean autosize) throws InvocationTargetException, IntrospectionException, IllegalAccessException, WsBackOfficeException, IOException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Enfant> query = cb.createQuery(Enfant.class);
        Root<Enfant> root = query.from(Enfant.class);
        query.select(root);
        root.fetch("dossierByDossierId");
        root.fetch("localisationByLocalisationNaissanceId");
        //root.fetch("personnesById");
        List<Enfant> enfants = entityManager.createQuery(query).setMaxResults(excelProperties.getMaxExport()).getResultList();

        return exportInternal(autosize, enfants);
    }

    /**
     * Export au format csv
     */
    private File exportInternal (boolean autosize, List<Enfant> enfants) throws WsBackOfficeException, IOException, IllegalAccessException, IntrospectionException, InvocationTargetException {

        if (enfants.isEmpty()) {
            throw new WsBackOfficeException("Pas de dossier à exporter");
        }

        ExportUtils exportUtils = new ExportUtils();

        // Préparation
        exportUtils.appendModele(new MappingModele(new ClassPathResource("exportTemplate/export_mapping.txt").getFile(), "", "Dossier", false));
        //exportUtils.appendModele(new MappingModele(new ClassPathResource("export_mapping_equip.txt").getFile(), "equipements", "Equipements", true));
        //exportUtils.appendModele(new MappingModele(new ClassPathResource("export_mapping_perso.txt").getFile(), "personnels", "Personnel", true));
        //exportUtils.appendModele(new MappingModele(new ClassPathResource("export_mapping_finance.txt").getFile(), "financementExts", "Financement Ext", true));
        //exportUtils.appendModele(new MappingModele(new ClassPathResource("export_mapping_piece.txt").getFile(), "piecesConsacrees", "Pieces EC", true));
        exportUtils.prepaExport();

        String excelPath = exportUtils.processDataExcel (enfants, autosize, excelProperties.getExcelLocalDir());
        return new File(excelPath);
    }



}
