package com.lucio.erp_new_app.services;

import com.lucio.erp_new_app.config.ErpnextProperties;
import com.lucio.erp_new_app.dtos.LoginForm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ErpnextProperties erpnextProperties;


    public boolean loginToERPNext(LoginForm loginForm) {
        String url = erpnextProperties.getUrl() + "/api/method/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "usr=" + loginForm.getUsername() + "&pwd=" + loginForm.getPassword();
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                HttpHeaders responseHeaders = response.getHeaders();
                String sessionCookie = responseHeaders.getFirst(HttpHeaders.SET_COOKIE);
                System.out.println("Session ERPNext : " + sessionCookie);
                return true;
            }
            else {
                System.out.println("Login failed, code: " + response.getStatusCode());
                return false;
            }

        }
        catch (Exception e) {
            System.err.println("Erreur lors de la connexion à ERPNext : " + e.getMessage());
            return false;
        }
    }
}
