package com.db.dataplatform.techtest.server.component.impl;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.exception.UnableToFetchException;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerImpl implements Server {

	private final DataBodyService dataBodyServiceImpl;
	private final ModelMapper modelMapper;
	private final RestTemplate restTemplate;

	private static final String BIG_DATA_URI = "http://localhost:8090/hadoopserver/pushbigdata";

	/**
	 * Using custom Threadpool for calling the Hadoop Service to ensure you dont
	 * need to wait on default ForkJoinThreadpool
	 */
	private ExecutorService service = Executors.newFixedThreadPool(5);

	/**
	 * @param envelope
	 * @return true if there is a match with the client provided checksum.
	 */
	@Override
	public boolean saveDataEnvelope(DataEnvelope envelope) {
		// Calling the Bigdata Persistence before actual persistence and then return
		// once both the outputs are ready
		try {
			CompletableFuture<ResponseEntity<HttpStatus>> completableFuture = CompletableFuture.supplyAsync(() -> {
				ResponseEntity<HttpStatus> response = restTemplate.postForEntity(BIG_DATA_URI,
						envelope.getDataBody().getDataBody(), HttpStatus.class);
				return response;
			}, service);
			// Save to persistence.
			persist(envelope);
			log.info("Data persisted successfully, data name: {}", envelope.getDataHeader().getName());

			ResponseEntity<HttpStatus> response = completableFuture.get();
			return response.getStatusCode() == HttpStatus.OK;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new UnableToFetchException("ErrorDuringHadoopSave", 500);
		}
	}

	private void persist(DataEnvelope envelope) {
		log.info("Persisting data with attribute name: {}", envelope.getDataHeader().getName());
		DataHeaderEntity dataHeaderEntity = modelMapper.map(envelope.getDataHeader(), DataHeaderEntity.class);
		DataBodyEntity dataBodyEntity = modelMapper.map(envelope.getDataBody(), DataBodyEntity.class);
		dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);
		saveData(dataBodyEntity);
	}

	private void saveData(DataBodyEntity dataBodyEntity) {
		dataBodyServiceImpl.saveDataBody(dataBodyEntity);
	}

	@Override
	public DataEnvelope updateDataEnvelopeBlockType(BlockTypeEnum blockType, String name) {
		Optional<DataBodyEntity> body = dataBodyServiceImpl.getDataByBlockName(name);
		if (body.isPresent()) {
			body.get().getDataHeaderEntity().setBlocktype(blockType);
			dataBodyServiceImpl.saveDataBody(body.get());
			return convertEntity(body.get());
		} else {
			return null;
		}
	}

	@Override
	public List<DataEnvelope> getDataEnvelopByBlockType(BlockTypeEnum blocktype) {
		List<DataBodyEntity> listOfDataBodyEntity = dataBodyServiceImpl.getDataByBlockType(blocktype);
		List<DataEnvelope> outputList = listOfDataBodyEntity.parallelStream().map(this::convertEntity)
				.collect(Collectors.toList());
		return outputList;
	}

	private DataEnvelope convertEntity(DataBodyEntity body) {
		DataHeader dataHeader = new DataHeader(body.getDataHeaderEntity().getName(),
				body.getDataHeaderEntity().getBlocktype());
		DataBody dataBody = new DataBody(body.getDataBody());
		
		return new DataEnvelope(dataHeader, dataBody, "");
	}

}
