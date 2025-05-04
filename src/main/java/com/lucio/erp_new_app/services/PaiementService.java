package com.lucio.erp_new_app.services;

import com.lucio.erp_new_app.config.ErpnextProperties;
import com.lucio.erp_new_app.dtos.payment.PaymentDTO;
import com.lucio.erp_new_app.dtos.payment.PaymentResponseGroupDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.time.LocalDate;
import java.util.*;

import static com.lucio.erp_new_app.utils.PaiementUtils.*;

@Service
public class PaiementService {

    private final RestTemplate restTemplate;
    private final ErpnextProperties erpnextProperties;

    @Autowired
    public PaiementService(RestTemplate restTemplate, ErpnextProperties erpnextProperties) {
        this.restTemplate = restTemplate;
        this.erpnextProperties = erpnextProperties;
    }

    public PaymentResponseGroupDTO processPayment(PaymentDTO paymentDTO) {
        String sid = getSessionId();

        if (paymentDTO.getPostingDate() == null) {
            paymentDTO.setPostingDate(LocalDate.now());
        }
        if (paymentDTO.getReferenceDate() == null) {
            paymentDTO.setReferenceDate(paymentDTO.getPostingDate());
        }

        String url = erpnextProperties.getUrl() + "/api/resource/Payment Entry";
        HttpEntity<PaymentDTO> request = new HttpEntity<>(paymentDTO, buildHeaders(sid, true, erpnextProperties));
        try {
            ResponseEntity<PaymentResponseGroupDTO> response = restTemplate.postForEntity(url, request, PaymentResponseGroupDTO.class);
            checkResponse(response);
            return response.getBody();
        } catch (Exception e) {
            throw handleException("traitement du paiement", e);
        }
    }

    public String submitPaymentEntry(String paymentEntryName) {
        String sid = getSessionId();
        String url = erpnextProperties.getUrl() + "/api/resource/Payment Entry/" + paymentEntryName;
        Map<String, String> body = Collections.singletonMap("run_method", "submit");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, buildHeaders(sid, false, erpnextProperties));

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            checkResponse(response);
            return response.getBody();
        } catch (Exception e) {
            throw handleException("validation du paiement", e);
        }
    }
}

