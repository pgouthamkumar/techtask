package com.db.dataplatform.techtest.server.api.controller;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.codec.binary.Hex;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.exception.UnableToFetchException;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/dataserver")
@RequiredArgsConstructor
@Validated
public class ServerController {

    private final Server server;

    @PostMapping(value = "/pushdata", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> pushData(@Valid @RequestBody DataEnvelope dataEnvelope) throws IOException, NoSuchAlgorithmException {

        log.info("Data envelope received: {}", dataEnvelope.getDataHeader().getName());
        
        /**
         * Recomputing the digest to compute the MD5 hash again and comparing with one in body
         */
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] computestDigest = digest.digest(dataEnvelope.getDataBody().getDataBody().getBytes());
        
        //Applying to lowercase as MD5 can be passed in Upper as well based on generator
        boolean checksumPass = Hex.encodeHexString(computestDigest).equals(dataEnvelope.getChecksum().toLowerCase());
        		
        if(checksumPass) {
        	checksumPass = server.saveDataEnvelope(dataEnvelope);
        }
        if(checksumPass) {
        	log.info("Data envelope persisted sucessfully. Attribute name: {}", dataEnvelope.getDataHeader().getName());
        	return ResponseEntity.ok(checksumPass);
        }else {
        	log.warn("Data envelope persist failed. Attribute name: {}", dataEnvelope.getDataHeader().getName());
        	return ResponseEntity.unprocessableEntity().body(checksumPass);
        }        
    }
    
    @GetMapping(value = "/data/{blockType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DataEnvelope>> getData(@PathVariable("blockType") String blocktype) {
    	try {
    		long time = System.currentTimeMillis();
    		log.info("Search request received with blocktype: {}, requesttime:{}", blocktype,time);
    		BlockTypeEnum blockTypeEnum = BlockTypeEnum.valueOf(blocktype);
    		List<DataEnvelope> envelope = server.getDataEnvelopByBlockType(blockTypeEnum);
    		if(envelope.isEmpty()) {
    			log.info("Records not found for search blocktype:{}, requesttime:{}",blocktype,time);
    			return ResponseEntity.notFound().build();
    		}
    		log.info("Retuning result for search requesttime:{}",time);
    		return ResponseEntity.ok(envelope);
    	}catch (IllegalArgumentException e) {
			throw new UnableToFetchException("InvalidBlockType",422);
		}
    }
    
    @PatchMapping(value= "/update/{name}/{newBlockType}", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateData(@PathVariable(value="name", required = true) String name, @PathVariable("newBlockType") String blocktype) {
    	try {
    		long time = System.currentTimeMillis();
    		log.info("Update request received with name: {}, requesttime:{}", name, time);
    		BlockTypeEnum blockTypeEnum = BlockTypeEnum.valueOf(blocktype);
    		DataEnvelope envelop = server.updateDataEnvelopeBlockType(blockTypeEnum, name);
    		if(envelop == null) {
    			return ResponseEntity.notFound().build();
    		}
    		log.info("Update request complete for name: {}, requesttime:{}", name, time);
    		return ResponseEntity.ok(true);
    	} catch (IllegalArgumentException e) {
			throw new UnableToFetchException("InvalidBlockType",422);
		}
    }
}