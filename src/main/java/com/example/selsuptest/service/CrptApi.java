package service;

import dto.CreateDocumentRequest;
import dto.CreateDocumentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.TimeUnit;

@Service
public class CrptApi {
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final long requestLimit;
    private final long requestInterval;
    private long lastRequestTime = 0;
    private long requestCount = 0;

    public CrptApi(@Value("${api.url}") String apiUrl, @Value("${api.requestLimit}") int requestLimit, @Value("${api.requestInterval}") int requestInterval) {
        this.apiUrl = apiUrl;
        this.requestLimit = requestLimit;
        this.requestInterval = TimeUnit.SECONDS.toMillis(requestInterval);
        this.restTemplate = new RestTemplate();
    }

    @Scheduled(fixedRate = 1000) 
    private synchronized void resetRequestCount() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRequestTime > requestInterval) {
            requestCount = 0;
            lastRequestTime = currentTime;
        }
    }

    public void createDocument(String document, String signature) {
        if (requestCount >= requestLimit) {
            throw new IllegalStateException("Request limit exceeded.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        CreateDocumentRequest request = new CreateDocumentRequest(document, signature);
        HttpEntity<CreateDocumentRequest> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<CreateDocumentResponse> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, CreateDocumentResponse.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            CreateDocumentResponse response = responseEntity.getBody();
            System.out.println("API Response: " + response);
            requestCount++;
        } else {
            System.out.println("API Error: " + responseEntity.getStatusCode());
        }
    }
}
