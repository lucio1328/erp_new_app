// package com.lucio.erp_new_app.services;

// import java.net.URLEncoder;
// import java.nio.charset.StandardCharsets;
// import java.util.Collections;
// import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.core.ParameterizedTypeReference;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.lucio.erp_new_app.config.ErpnextProperties;
// import com.lucio.erp_new_app.dtos.Account;
// import com.lucio.erp_new_app.dtos.ErpNextListResponse;

// // Updated ErpNextClient.java
// @Service
// public class ErpNextClient {
//     private final RestTemplate restTemplate;
//     private final ErpnextProperties erpnextProperties;
//     private final ObjectMapper objectMapper;

//     public ErpNextClient(ErpnextProperties erpnextProperties, ObjectMapper objectMapper) {
//         this.restTemplate = new RestTemplate();
//         this.erpnextProperties = erpnextProperties;
//         this.objectMapper = objectMapper;
//     }

//     private static final Logger logger = LoggerFactory.getLogger(ErpNextClient.class);

//     public List<Account> getAccountsByType(String sessionCookie, String accountType) {
//         try {
//             RestTemplate restTemplate = new RestTemplate();
            
//             HttpHeaders headers = new HttpHeaders();
//             headers.set("Cookie", "sid=" + sessionCookie);
//             headers.setContentType(MediaType.APPLICATION_JSON);

//             String filter = String.format("[[\"account_type\",\"=\",\"%s\"],[\"is_group\",\"=\",0]]", accountType);
//             String url = erpnextProperties.getUrl() + "/api/resource/Account?fields=[\"name\",\"account_type\",\"currency\",\"parent_account\"]&filters=" + 
//                        URLEncoder.encode(filter, StandardCharsets.UTF_8);

//             ResponseEntity<ErpNextListResponse<Account>> response = restTemplate.exchange(
//                 url,
//                 HttpMethod.GET,
//                 new HttpEntity<>(headers),
//                 new ParameterizedTypeReference<ErpNextListResponse<Account>>() {}
//             );

//             if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//                 return response.getBody().getData();
//             }
//         } catch (Exception e) {
//             logger.error("Error fetching accounts from ERPNext", e);
//         }
//         return Collections.emptyList();
//     }
// }

