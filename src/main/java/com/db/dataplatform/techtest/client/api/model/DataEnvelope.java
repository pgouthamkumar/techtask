package com.db.dataplatform.techtest.client.api.model;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonSerialize(as = DataEnvelope.class)
@JsonDeserialize(as = DataEnvelope.class)
@Getter
@AllArgsConstructor
public class DataEnvelope {

	
    @NotNull
    private DataHeader dataHeader;

    @NotNull
    private DataBody dataBody;
    
    @NotNull
    private String checksum;
    
}
