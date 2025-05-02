package com.lucio.erp_new_app.services;

import com.lucio.erp_new_app.dtos.*;
import com.lucio.erp_new_app.config.ErpnextProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class FournisseurService {
    private static final String[] SUPPLIER_FIELDS = {
        "name", "supplier_name", "supplier_group", "supplier_type",
        "country", "default_currency", "disabled", "is_frozen", "on_hold"
    };

    private final RestTemplate restTemplate;
    private final ErpnextProperties erpnextProperties;
    private final ObjectMapper objectMapper;

    @Autowired
    public FournisseurService(RestTemplate restTemplate,
                            ErpnextProperties erpnextProperties,
                            ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.erpnextProperties = erpnextProperties;
        this.objectMapper = objectMapper;
    }

    public List<SupplierDTO> getAllFournisseurs(String sessionCookie) {
        String url = buildUrl("Supplier", SUPPLIER_FIELDS);
        return fetchData(url, sessionCookie, this::mapToSupplierDTO);
    }

    public SupplierDTO getFournisseurByName(String name, String sessionCookie) {
        String url = String.format("%s/api/resource/Supplier/%s",
                                 erpnextProperties.getUrl(), name);
        return fetchSingleData(url, sessionCookie, this::mapToSupplierDTO);
    }

    public List<QuotationDTO> getSupplierQuotations(String supplierName, String sessionCookie) {
        String url = String.format("%s/api/resource/Quotation?fields=[\"*\"]&filters=[[\"supplier\",\"=\",\"%s\"]]",
                                 erpnextProperties.getUrl(), supplierName);
        return fetchData(url, sessionCookie, QuotationDTO::new);
    }

    public List<PurchaseOrderDTO> getSupplierPurchaseOrders(String supplierName, String sessionCookie) {
        String url = String.format("%s/api/resource/Purchase Order?fields=[\"*\"]&filters=[[\"supplier\",\"=\",\"%s\"]]",
                                 erpnextProperties.getUrl(), supplierName);
        return fetchData(url, sessionCookie, this::mapToPurchaseOrderDTO);
    }

    private <T> List<T> fetchData(String url, String sessionCookie, Function<JsonNode, T> mapper) {
        try {
            ResponseEntity<String> response = executeRequest(url, sessionCookie);
            JsonNode json = objectMapper.readTree(response.getBody());
            return StreamSupport.stream(json.path("data").spliterator(), false)
                    .map(mapper)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            handleException("Error fetching data from " + url, e);
            return Collections.emptyList();
        }
    }

    private <T> T fetchSingleData(String url, String sessionCookie, Function<JsonNode, T> mapper) {
        try {
            ResponseEntity<String> response = executeRequest(url, sessionCookie);
            JsonNode json = objectMapper.readTree(response.getBody());
            return json.has("data") ? mapper.apply(json.path("data")) : null;
        } catch (Exception e) {
            handleException("Error fetching single data from " + url, e);
            return null;
        }
    }

    private ResponseEntity<String> executeRequest(String url, String sessionCookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }

    private String buildUrl(String doctype, String[] fields) {
        return String.format("%s/api/resource/%s?fields=[\"%s\"]",
                           erpnextProperties.getUrl(),
                           doctype,
                           String.join("\",\"", fields));
    }

    private SupplierDTO mapToSupplierDTO(JsonNode node) {
        SupplierDTO dto = new SupplierDTO();
        dto.setName(node.path("name").asText());
        dto.setSupplierName(node.path("supplier_name").asText());
        dto.setSupplierGroup(node.path("supplier_group").asText());
        dto.setSupplierType(node.path("supplier_type").asText());
        dto.setCountry(node.path("country").asText());
        dto.setDefaultCurrency(node.path("default_currency").asText());
        dto.setStatus(determineSupplierStatus(node));
        return dto;
    }

    private PurchaseOrderDTO mapToPurchaseOrderDTO(JsonNode node) {
        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setId(node.path("name").asText());
        dto.setReference(node.path("name").asText());
        dto.setSupplierName(node.path("supplier_name").asText());
        dto.setTransactionDate(node.path("transaction_date").asText());
        dto.setScheduleDate(node.path("schedule_date").asText());
        dto.setStatus(node.path("status").asText());
        dto.setTotal(node.path("total").asDouble());
        dto.setNetTotal(node.path("net_total").asDouble());
        dto.setCurrency(node.path("currency").asText());
        dto.setPercentBilled(node.path("per_billed").asDouble());
        dto.setPercentReceived(node.path("per_received").asDouble());
        return dto;
    }

    private String determineSupplierStatus(JsonNode node) {
        if (node.path("disabled").asInt() == 1) return "Désactivé";
        if (node.path("is_frozen").asInt() == 1) return "Gelé";
        if (node.path("on_hold").asInt() == 1) return "En attente";
        return "Actif";
    }

    private void handleException(String message, Exception e) {
        System.err.println(message);
        e.printStackTrace();
    }
}