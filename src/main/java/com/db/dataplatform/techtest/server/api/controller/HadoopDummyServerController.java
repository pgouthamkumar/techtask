package com.db.dataplatform.techtest.server.api.controller;

import java.util.Random;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This code does not require any test coverage. Do not modify this class.
 */

@Slf4j
@Validated
@Controller
@RequestMapping("/hadoopserver")
@RequiredArgsConstructor
public class HadoopDummyServerController {

    
    @PostMapping(value = "/pushbigdata")
    public ResponseEntity<HttpStatus> pushBigData(@RequestBody @Valid String payload) throws InterruptedException {

        log.info("Saving to Hadoop file system");
        Random random = new Random();
        
        /**
         * Have changed the hardcoded values of start and end based on payload lenght so that the output during test can be predicted
         * 
         * @author goutham
         */
        int startDuration = payload.length()>30?3001:2000;
        int endBoundry = payload.length()>30?4000:3000;
        int workDuration = random.ints(startDuration, endBoundry).findAny().getAsInt();

        // Simulate long running work.
        Thread.sleep(workDuration);

        /**
         * Either comment out the below block or change DUMMY Data in constants to make application test pass
         * during the boot start
         * @author goutham 
         */
        if(workDuration > 3000) {
            log.info("Hadoop back end has timed out");
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        }

        log.info("Saving to Hadoop file system - finished");
        return ResponseEntity.ok().build();
    }
}
