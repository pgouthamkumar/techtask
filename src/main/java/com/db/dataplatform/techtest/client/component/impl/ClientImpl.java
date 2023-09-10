package com.db.dataplatform.techtest.client.component.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.component.Client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Client code does not require any test coverage
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientImpl implements Client {

    public static final String URI_PUSHDATA = "http://localhost:8090/dataserver/pushdata";
    public static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8090/dataserver/data/{blockType}");
    public static final UriTemplate URI_PATCHDATA = new UriTemplate("http://localhost:8090/dataserver/update/{name}/{newBlockType}");
    public static final String ALGO = "MD5";
    
    
    
    private final RestTemplate restTemplate;
    
    @Override
	public void pushData(DataEnvelope dataEnvelope) {
		log.info("Pushing data {} to {}", dataEnvelope.getDataHeader().getName(), URI_PUSHDATA);
		Boolean responseValue = restTemplate.postForObject(URI_PUSHDATA, dataEnvelope, Boolean.class);
		log.info("The response after pushing data is {}", responseValue);		
	}

    @SuppressWarnings("unchecked")
	@Override
    public List<DataEnvelope> getData(String blockType) {
        log.info("Query for data with header block type {}", blockType);
        Map<String,String> uriVariable = new HashMap<>();
        uriVariable.put("blockType", blockType);
        ResponseEntity<List<DataEnvelope>> response = restTemplate.exchange(URI_GETDATA.expand(uriVariable),HttpMethod.GET, null, new ParameterizedTypeReference<List<DataEnvelope>>() {});
        return (List<DataEnvelope>)  response.getBody();
    }

    @Override
    public boolean updateData(String blockName, String newBlockType) {
        log.info("Updating blocktype to {} for block with name {}", newBlockType, blockName);
        Map<String,String> uriVariable = new HashMap<>();
        uriVariable.put("name",blockName);
        uriVariable.put("newBlockType", newBlockType);
        return restTemplate.patchForObject(URI_PATCHDATA.expand(uriVariable),null,Boolean.class);
    }
}
