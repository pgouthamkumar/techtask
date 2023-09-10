package com.db.dataplatform.techtest.server.component;

import java.util.List;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;

public interface Server {
    boolean saveDataEnvelope(DataEnvelope envelope);
    DataEnvelope updateDataEnvelopeBlockType(BlockTypeEnum blockType, String name);
	List<DataEnvelope> getDataEnvelopByBlockType(BlockTypeEnum blocktype);
}
