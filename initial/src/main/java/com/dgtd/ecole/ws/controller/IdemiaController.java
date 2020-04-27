package com.dgtd.ecole.ws.controller;

import com.dgtd.ecole.ws.domain.idemia.SendCreateUin;
import com.dgtd.ecole.ws.domain.idemia.SendNotify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Controller pour le mock IDEMIA. Les deux webservices ci-dessous imitent le comportement de la partie IDEMIA
 * telle que définie dans le document "CD-ID-New Civil Registry_ICD_20-02-2019.docx"
 */
@Controller
@RequestMapping("/v1")
public class IdemiaController {

    private static final Logger log = LoggerFactory.getLogger(IdemiaController.class);

    @PostMapping(value = "/uin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uin(@RequestBody SendCreateUin sendCreateUin){

        String rep = "";

        if ((sendCreateUin.getFirst_name() != null) && ("erreur".contentEquals(sendCreateUin.getFirst_name()))) {
            rep = String.format("{\"code\":\"1\",\"message\":\"blue screen of death\"}");
        }
        else {
            int randomNum = ThreadLocalRandom.current().nextInt(1, 999999999 + 1);
            rep = String.format("CD%d1",randomNum);
            //rep = "CD2358693986";
            //rep = String.format("{\"uin\":\"CD%d1\"}",randomNum);
        }

        return ResponseEntity.ok(rep);
    }


    @PostMapping(value = "/notify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity notify (@RequestParam("event") String event, @RequestBody SendNotify sendnotify){

        String titi = event;
        if ( (sendnotify.getUin() != null) && sendnotify.getUin().endsWith("5"))
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        else {
            return new ResponseEntity(HttpStatus.MULTI_STATUS.OK);
        }
    }
}
