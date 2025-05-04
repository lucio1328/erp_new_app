package com.lucio.erp_new_app.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app.config.ErpnextProperties;
import com.lucio.erp_new_app.dtos.PaymentDTO;
import com.lucio.erp_new_app.dtos.PaymentResponseGroupDTO;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaiementService {

    private final RestTemplate restTemplate;
    private final ErpnextProperties erpnextProperties;
    private final ObjectMapper objectMapper;

    public PaiementService(ErpnextProperties erpnextProperties, ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.erpnextProperties = erpnextProperties;
        this.objectMapper = objectMapper;
    }

    @Autowired
    public PaiementService(RestTemplate restTemplate,
                         ErpnextProperties erpnextProperties,
                         ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.erpnextProperties = erpnextProperties;
        this.objectMapper = objectMapper;
    }

    public PaymentResponseGroupDTO processPayment(PaymentDTO paymentDTO) {
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getSession();
        String sid = (String) session.getAttribute("sid");

        if (sid == null) {
            throw new RuntimeException("Session non authentifiée");
        }

        if (paymentDTO.getPostingDate() == null) {
            paymentDTO.setPostingDate(LocalDate.now());
        }
        if (paymentDTO.getReferenceDate() == null) {
            paymentDTO.setReferenceDate(paymentDTO.getPostingDate());
        }

        String url = erpnextProperties.getUrl() + "/api/resource/Payment Entry";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "token " + erpnextProperties.getKey() + ":" + erpnextProperties.getSecret());
        headers.set("Cookie", "sid=" + sid);

        HttpEntity<PaymentDTO> request = new HttpEntity<>(paymentDTO, headers);

        try {
            ResponseEntity<PaymentResponseGroupDTO> response = restTemplate.postForEntity(url, request, PaymentResponseGroupDTO.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Erreur ERPNext - Code HTTP: " + response.getStatusCode() + " - Message: " + response.getBody());
            }

            return response.getBody();

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Erreur côté client (4xx): " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Erreur côté serveur ERPNext (5xx): " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Erreur d’accès au serveur ERPNext: " + e.getMessage(), e);
        } catch (RestClientException e) {
            throw new RuntimeException("Erreur lors de l’appel ERPNext: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur inattendue lors du traitement du paiement: " + e.getMessage(), e);
        }
    }

    public String submitPaymentEntry(String paymentEntryName) {
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getSession();
        String sid = (String) session.getAttribute("sid");

        if (sid == null) {
            throw new RuntimeException("Session non authentifiée");
        }

        String url = erpnextProperties.getUrl() + "/api/resource/Payment Entry/" + paymentEntryName ;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "token " + erpnextProperties.getKey() + ":" + erpnextProperties.getSecret());
        headers.set("Cookie", "sid=" + sid);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("run_method", "submit");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Erreur lors de la validation du Payment Entry - HTTP " + response.getStatusCode() + " - " + response.getBody());
            }

            return response.getBody();

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Erreur côté client (4xx): " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Erreur côté serveur ERPNext (5xx): " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Erreur d’accès au serveur ERPNext: " + e.getMessage(), e);
        } catch (RestClientException e) {
            throw new RuntimeException("Erreur lors de l’appel ERPNext: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur inattendue lors de la validation du paiement: " + e.getMessage(), e);
        }
    }
}
