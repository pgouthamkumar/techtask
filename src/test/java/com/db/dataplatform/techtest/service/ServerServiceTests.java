package com.db.dataplatform.techtest.service;

import static com.db.dataplatform.techtest.TestDataHelper.createTestDataEnvelopeApiObject;
import static com.db.dataplatform.techtest.TestDataHelper.createTestDataEnvelopeApiObjectForSmallPayload;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.db.dataplatform.techtest.server.api.controller.HadoopDummyServerController;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.component.impl.ServerImpl;
import com.db.dataplatform.techtest.server.mapper.ServerMapperConfiguration;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;

@RunWith(MockitoJUnitRunner.class)
public class ServerServiceTests {

    @Mock
    private DataBodyService dataBodyServiceImplMock;

    private ModelMapper modelMapper;

    private DataBodyEntity expectedDataBodyEntity;
    private DataEnvelope testDataEnvelope;
    private DataEnvelope testDataEnvelope2ForSmallPayload;
    
    
    private Server server;
    
    @Mock
    private RestTemplate restTemplate;

    @Before
    public void setup() {
        ServerMapperConfiguration serverMapperConfiguration = new ServerMapperConfiguration();
        modelMapper = serverMapperConfiguration.createModelMapperBean();

        testDataEnvelope = createTestDataEnvelopeApiObject();
        testDataEnvelope2ForSmallPayload = createTestDataEnvelopeApiObjectForSmallPayload();
        expectedDataBodyEntity = modelMapper.map(testDataEnvelope.getDataBody(), DataBodyEntity.class);
        expectedDataBodyEntity.setDataHeaderEntity(modelMapper.map(testDataEnvelope.getDataHeader(), DataHeaderEntity.class));
        HadoopDummyServerController controller = new HadoopDummyServerController();
        Mockito.when(restTemplate.postForEntity(Mockito.eq("http://localhost:8090/hadoopserver/pushbigdata"), Mockito.anyString(), Mockito.eq(HttpStatus.class))).then(new Answer<ResponseEntity<HttpStatus>>() {
        	@Override
        	public ResponseEntity<HttpStatus> answer(InvocationOnMock invocation) throws Throwable {
        		return controller.pushBigData(invocation.getArgument(1));
        	}
        });
        server = new ServerImpl(dataBodyServiceImplMock, modelMapper, restTemplate);
    }

   

	@Test
    public void shouldSaveDataEnvelopeAsExpected() throws NoSuchAlgorithmException, IOException {
        boolean success = server.saveDataEnvelope(testDataEnvelope);
        assertThat(success).isFalse();
        success = server.saveDataEnvelope(testDataEnvelope2ForSmallPayload);
        assertThat(success).isTrue();
        Mockito.verify(dataBodyServiceImplMock, Mockito.times(1)).saveDataBody(Mockito.eq(expectedDataBodyEntity));
    }	
}
