package com.dgtd.report.excel;

import com.dgtd.ecole.ws.service.DBentities.EcoleService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;

@RestController
public class ExportController {

    private EcoleService ecoleService;
    private ExportExcelReportService exportService;

    public ExportController(EcoleService ecoleService, ExportExcelReportService exportService)
    {
        this.ecoleService = ecoleService;
        this.exportService = exportService;
    }

    private final Logger log = LoggerFactory.getLogger(ExportController.class);

    @ApiOperation(value = "Export des informations enregistrées en base de données sous forme de tableur excel")
    @GetMapping(value = "/export")
    @Secured(value = {"Administrateur", "Superviseur"})
    public ResponseEntity download () {

        log.debug("REST export Excel");

        try {
            File file = exportService.export(true);
            String nom = file.getName();
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Description", "File Transfer");
            headers.set("Content-Disposition", "attachment; " + nom);
            headers.set("Content-Transfer-Encoding", "binary" );

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getLocalizedMessage());
        }

    }

}
