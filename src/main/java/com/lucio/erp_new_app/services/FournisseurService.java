package com.lucio.erp_new_app.services;

import com.lucio.erp_new_app.dtos.*;
import com.lucio.erp_new_app.config.ErpnextProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
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

    public List<SupplierQuotationDTO> getSupplierQuotations(String supplierName, String sessionCookie) {
        String url = String.format(
            "%s/api/resource/Supplier Quotation?fields=[\"*\"]&filters=[[\"supplier\",\"=\",\"%s\"]]",
            erpnextProperties.getUrl(), supplierName
        );
        return fetchData(url, sessionCookie, SupplierQuotationDTO::new);
    }

    public SupplierQuotationDTO getSupplierQuotationByName(String name, String sessionCookie) {
        String url = String.format("%s/api/resource/Supplier Quotation/%s",
                                 erpnextProperties.getUrl(), name);
        return fetchSingleData(url, sessionCookie, this::mapToSupplierQuotationDTO);
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

    private SupplierQuotationDTO mapToSupplierQuotationDTO(JsonNode node) {
        SupplierQuotationDTO dto = new SupplierQuotationDTO();

        dto.setId(node.path("name").asText());
        dto.setReference(node.path("name").asText());
        dto.setSupplierName(node.path("supplier_name").asText());
        dto.setCompany(node.path("company").asText());

        dto.setTransactionDate(LocalDate.parse(node.path("transaction_date").asText()));
        dto.setValidTill(LocalDate.parse(node.path("valid_till").asText()));

        dto.setStatus(node.path("status").asText());
        dto.setTotal(node.path("grand_total").asDouble());
        dto.setNetTotal(node.path("net_total").asDouble());
        dto.setCurrency(node.path("currency").asText());
        dto.setContactPerson(node.path("contact_display").asText(null));
        dto.setContactEmail(node.path("contact_email").asText(null));
        dto.setContactMobile(node.path("contact_mobile").asText(null));

        // Article
        if (node.has("items")) {
            List<QuotationItemDTO> items = new ArrayList<>();
            for (JsonNode itemNode : node.path("items")) {
                QuotationItemDTO item = new QuotationItemDTO();

                item.setName(itemNode.path("name").asText());
                item.setItemCode(itemNode.path("item_code").asText());
                item.setItemName(itemNode.path("item_name").asText());
                item.setQuantity(itemNode.path("qty").asDouble());
                item.setUom(itemNode.path("uom").asText());
                item.setRate(itemNode.path("rate").asDouble());
                item.setAmount(itemNode.path("amount").asDouble());
                item.setWarehouse(itemNode.path("warehouse").asText());

                items.add(item);
            }
            dto.setQuotationItemDTOs(items);
        }

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

    public void updateQuotationItems(String quotationId, List<QuotationItemDTO> modifiedItems, String sessionCookie) {
        SupplierQuotationDTO currentQuotation = getSupplierQuotationByName(quotationId, sessionCookie);
        List<QuotationItemDTO> existingItems = currentQuotation.getQuotationItemDTOs();

        List<Map<String, Object>> allItems = existingItems.stream()
            .map(existingItem -> {
                QuotationItemDTO modifiedItem = modifiedItems.stream()
                    .filter(item -> item.getName().equals(existingItem.getName()))
                    .findFirst()
                    .orElse(null);

                QuotationItemDTO finalItem = modifiedItem != null ? modifiedItem : existingItem;

                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("name", finalItem.getName());
                itemMap.put("item_code", finalItem.getItemCode());
                itemMap.put("qty", finalItem.getQuantity());
                itemMap.put("rate", finalItem.getRate());
                itemMap.put("amount", finalItem.getQuantity() * finalItem.getRate());
                itemMap.put("warehouse", finalItem.getWarehouse());
                itemMap.put("uom", finalItem.getUom());

                return itemMap;
            })
            .collect(Collectors.toList());

        Map<String, Object> payload = new HashMap<>();
        payload.put("items", allItems);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cookie", sessionCookie);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<String> response = restTemplate.exchange(
            String.format("%s/api/resource/Supplier Quotation/%s", erpnextProperties.getUrl(), quotationId),
            HttpMethod.PUT,
            request,
            String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Échec de la mise à jour: " + response.getBody());
        }
    }

}