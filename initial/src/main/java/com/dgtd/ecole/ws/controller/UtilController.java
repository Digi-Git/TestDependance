package com.dgtd.ecole.ws.controller;

import com.dgtd.ecole.ws.repository.DossierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UtilController {

    private static final Logger log = LoggerFactory.getLogger(UtilController.class);

    @Autowired
    DossierRepository dossierRepository;

    @Value("${info.project.version}")
    private String version;

    public UtilController(){

    }

    @RequestMapping(value = "/echo")
    public ResponseEntity<String> echo(){

        return ResponseEntity.ok("ok");
    }




    @GetMapping(value="/version")
    public ResponseEntity<String> getVersion(){

        return ResponseEntity.ok().body(version);
    }

}
