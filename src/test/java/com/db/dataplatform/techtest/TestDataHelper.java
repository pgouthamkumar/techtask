package com.db.dataplatform.techtest;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import org.apache.commons.codec.binary.Hex;

public class TestDataHelper {

    public static final String TEST_NAME = "Test";
    public static final String TEST_NAME_EMPTY = "";
    public static final String DUMMY_DATA = "AKCp5fU4WNWKBVvhXsbNhqk33tawri9iJUkA5o4A6YqpwvAoYjajVw8xdEw6r9796h1wEp29D";
    public static final String SMALL_DATA = "AKCp5fU4WNWKBVvhXsbNwdd";

    public static DataHeaderEntity createTestDataHeaderEntity(Instant expectedTimestamp) {
        DataHeaderEntity dataHeaderEntity = new DataHeaderEntity();
        dataHeaderEntity.setName(TEST_NAME);
        dataHeaderEntity.setBlocktype(BlockTypeEnum.BLOCKTYPEA);
        dataHeaderEntity.setCreatedTimestamp(expectedTimestamp);
        return dataHeaderEntity;
    }

    public static DataBodyEntity createTestDataBodyEntity(DataHeaderEntity dataHeaderEntity) {
        DataBodyEntity dataBodyEntity = new DataBodyEntity();
        dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);
        dataBodyEntity.setDataBody(DUMMY_DATA);
        return dataBodyEntity;
    }

    public static DataEnvelope createTestDataEnvelopeApiObject() {
        DataBody dataBody = new DataBody(DUMMY_DATA);
        DataHeader dataHeader = new DataHeader(TEST_NAME, BlockTypeEnum.BLOCKTYPEA);
        
        MessageDigest digest = null;
        byte[] md5checksum = null;
        try {
			digest = MessageDigest.getInstance("MD5");
			md5checksum = digest.digest(DUMMY_DATA.getBytes());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody,Hex.encodeHexString(md5checksum));
        return dataEnvelope;
    }
    
    public static DataEnvelope createTestDataEnvelopeApiObjectForSmallPayload() {
    	DataBody dataBody = new DataBody(SMALL_DATA);
        DataHeader dataHeader = new DataHeader(TEST_NAME, BlockTypeEnum.BLOCKTYPEA);
        
        MessageDigest digest = null;
        byte[] md5checksum = null;
        try {
			digest = MessageDigest.getInstance("MD5");
			md5checksum = digest.digest(SMALL_DATA.getBytes());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody,Hex.encodeHexString(md5checksum));
        return dataEnvelope;
    }

    public static DataEnvelope createTestDataEnvelopeApiObjectWithEmptyName() {
        DataBody dataBody = new DataBody(DUMMY_DATA);
        DataHeader dataHeader = new DataHeader(TEST_NAME_EMPTY, BlockTypeEnum.BLOCKTYPEA);

        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody,null);
        return dataEnvelope;
    }
}
