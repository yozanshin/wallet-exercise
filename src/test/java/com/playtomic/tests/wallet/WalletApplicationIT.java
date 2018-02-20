package com.playtomic.tests.wallet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
@Transactional
public class WalletApplicationIT {

    private static final String CONTEXT = "/wallet/{id}";

    @Autowired
	private TestRestTemplate restTemplate;

    @Test
	public void balanceEndpoint() {

	    String url = CONTEXT +"/balance";

        Map<String, Object> params = new HashMap<>();
        params.put("id", 1L);
	    ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class, params);
	    assertTrue(response.getStatusCode() == HttpStatus.OK);

	    params.put("id", -1L);
	    response = restTemplate.getForEntity(url, Void.class, params);
	    assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND);
	}

    @Test
    public void makePaymentEndpoint() {

	    String url = CONTEXT +"/payment?amount=";

        Map<String, Object> params = new HashMap<>();
        params.put("id", 1L);

        long amount = 100L;

        ResponseEntity<Void> response =
                restTemplate.postForEntity( url +amount,
                        null, Void.class, params);
        assertTrue(response.getStatusCode() == HttpStatus.OK);


        long badAmount = 200L;
        response = restTemplate.postForEntity(url +badAmount,
                    null, Void.class, params);
        assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);

        params.put("id", -1L);
        response = restTemplate.postForEntity(url +amount,
                    null, Void.class, params);
        assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    @Test
    public void makeReturnEndpoint() {

	    String url = CONTEXT +"/return?amount=";

        Map<String, Object> params = new HashMap<>();
        params.put("id", 1L);

        long amount = 100L;

        ResponseEntity<Void> response =
                restTemplate.postForEntity(url +amount,
                        null, Void.class, params);
        assertTrue(response.getStatusCode() == HttpStatus.OK);

        params.put("id", -1L);
        response = restTemplate.postForEntity(url +amount,
                    null, Void.class, params);
        assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    @Test
    public void makeChargeEndpoint() {

        String url = CONTEXT +"/charge?amount=";

        Map<String, Object> params = new HashMap<>();
        params.put("id", 1L);

        long amount = 100L;

        ResponseEntity<Void> response =
                restTemplate.postForEntity(url +amount,
                        null, Void.class, params);
        assertTrue(response.getStatusCode() == HttpStatus.OK);

        long badAmount = 3L;

        response = restTemplate.postForEntity(url +badAmount,
                    null, Void.class, params);
        assertTrue(response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR);

        params.put("id", -1L);
        response = restTemplate.postForEntity(url +amount,
                    null, Void.class, params);
        assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }
}
