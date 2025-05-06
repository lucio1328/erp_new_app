package com.lucio.erp_new_app.services;

import com.lucio.erp_new_app.dtos.purchase.PurchaseOrderDTO;
import com.lucio.erp_new_app.dtos.supplier.QuotationItemDTO;
import com.lucio.erp_new_app.dtos.supplier.SupplierDTO;
import com.lucio.erp_new_app.dtos.supplier.SupplierQuotationDTO;
import com.lucio.erp_new_app.utils.FournisseurUtils;
import com.lucio.erp_new_app.utils.PaiementUtils;

import jakarta.servlet.http.HttpSession;

import com.lucio.erp_new_app.config.ErpnextProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
        return fetchData(url, sessionCookie, FournisseurUtils::mapToSupplierDTO);
    }

    public SupplierDTO getFournisseurByName(String name, String sessionCookie) {
        String url = String.format("%s/api/resource/Supplier/%s",
                                erpnextProperties.getUrl(), name);
        return fetchSingleData(url, sessionCookie, FournisseurUtils::mapToSupplierDTO);
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
        return fetchSingleData(url, sessionCookie, FournisseurUtils::mapToSupplierQuotationDTO);
    }


    public List<PurchaseOrderDTO> getSupplierPurchaseOrders(String supplierName, String sessionCookie) {
        String url = String.format("%s/api/resource/Purchase Order?fields=[\"*\"]&filters=[[\"supplier\",\"=\",\"%s\"]]",
                                erpnextProperties.getUrl(), supplierName);
        List<PurchaseOrderDTO> orders = fetchData(url, sessionCookie, FournisseurUtils::mapToPurchaseOrderDTO);

        return orders;
    }

    public <T> List<T> fetchData(String url, String sessionCookie, Function<JsonNode, T> mapper) {
        try {
            ResponseEntity<String> response = executeRequest(url, sessionCookie);
            JsonNode json = objectMapper.readTree(response.getBody());
            return StreamSupport.stream(json.path("data").spliterator(), false)
                    .map(mapper)
                    .collect(Collectors.toList());
        }
        catch (Exception e) {
            FournisseurUtils.handleException("Error fetching data from " + url, e);
            return Collections.emptyList();
        }
    }

    private <T> T fetchSingleData(String url, String sessionCookie, Function<JsonNode, T> mapper) {
        try {
            ResponseEntity<String> response = executeRequest(url, sessionCookie);
            JsonNode json = objectMapper.readTree(response.getBody());
            return json.has("data") ? mapper.apply(json.path("data")) : null;
        }
        catch (Exception e) {
            FournisseurUtils.handleException("Error fetching single data from " + url, e);
            return null;
        }
    }

    public ResponseEntity<String> executeRequest(String url, String sessionCookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }

    public String buildUrl(String doctype, String[] fields) {
        return String.format("%s/api/resource/%s?fields=[\"%s\"]",
                        erpnextProperties.getUrl(),
                        doctype,
                        String.join("\",\"", fields));
    }

    public String submitSupplierQuotation(String supplierQuotationName) {
        String sid = PaiementUtils.getSessionId();
        String url = erpnextProperties.getUrl() + "/api/resource/Supplier Quotation/" + supplierQuotationName;
        Map<String, String> body = Collections.singletonMap("run_method", "submit");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, PaiementUtils.buildHeaders(sid, false, erpnextProperties));

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            PaiementUtils.checkResponse(response);
            return response.getBody();
        }
        catch (Exception e) {
            throw PaiementUtils.handleException("Modification status du supplier quotation", e);
        }
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
        this.submitSupplierQuotation(quotationId);
    }

    //============================================== GET purchase order payé ============================================================
    public List<String> getPurchaseOrderNames(String status, String supplierName, String sessionCookie) {
        String url = UriComponentsBuilder
                .fromHttpUrl(erpnextProperties.getUrl() + "/api/method/erpnext.eval.purchase_order.get_orders_by_status")
                .queryParam("status", status)
                .queryParam("supplier_name", supplierName)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", sessionCookie);
        headers.set("Authorization", "token " + erpnextProperties.getKey() + ":" + erpnextProperties.getSecret());

        HttpEntity<String> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    JsonNode.class
            );
            JsonNode root = response.getBody();
            if (root == null) {
                throw new RuntimeException("Réponse vide depuis ERPNext API");
            }
            JsonNode dataArray = root.path("message").path("data");
            List<String> orderNames = new ArrayList<>();
            if (dataArray.isArray()) {
                for (JsonNode order : dataArray) {
                    orderNames.add(order.path("name").asText());
                }
            }
            return orderNames;

        }
        catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<PurchaseOrderDTO> getPurchaseOrderByStatuts(List<PurchaseOrderDTO> purchaseOrderDTOs, String status, String supplierName, String sessionCookie) {
        List<String> names = this.getPurchaseOrderNames(status, supplierName, sessionCookie);
        return FournisseurUtils.getPurchaseByStatut(purchaseOrderDTOs, names);
    }

}