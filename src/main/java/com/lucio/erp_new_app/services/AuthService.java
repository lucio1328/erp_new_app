package com.lucio.erp_new_app.services;

import com.lucio.erp_new_app.config.ErpnextProperties;
import com.lucio.erp_new_app.dtos.auth.LoginForm;
import com.lucio.erp_new_app.response.LoginResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AuthService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ErpnextProperties erpnextProperties;


    public LoginResult loginToERPNext(LoginForm loginForm) {
        String url = erpnextProperties.getUrl() + "/api/method/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "usr=" + loginForm.getUsername() + "&pwd=" + loginForm.getPassword();
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                String sessionCookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
                System.out.println("Session ERPNext : " + sessionCookie);
                return new LoginResult(true, "Connexion réussie", sessionCookie);
            }
            else {
                return new LoginResult(false, "Erreur inconnue (code HTTP : " + response.getStatusCode() + ")");
            }

        }
        catch (HttpClientErrorException e) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(e.getResponseBodyAsString());
                String message = json.has("message") ? json.get("message").asText() : "Identifiants invalides";
                return new LoginResult(false, message);
            }
            catch (Exception ex) {
                return new LoginResult(false, "Erreur lors du traitement de la réponse ERPNext.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResult(false, "Erreur de connexion au serveur ERPNext.");
        }

    }

    //======================================================================================================================
    public String getLoggedUsername(String sessionCookie) {
        String url = erpnextProperties.getUrl() + "/api/method/frappe.auth.get_logged_user";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.getBody());

            return json.path("message").asText();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
