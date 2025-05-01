package com.lucio.erp_new_app.services;

import com.lucio.erp_new_app.dtos.SupplierDTO;
import com.lucio.erp_new_app.config.ErpnextProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

@Service
public class FournisseurService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ErpnextProperties erpnextProperties;

    public List<SupplierDTO> getAllFournisseurs(String sessionCookie) {
        String url = erpnextProperties.getUrl() + "/api/resource/Supplier?fields=[\"name\",\"supplier_name\",\"supplier_group\",\"supplier_type\",\"country\",\"default_currency\"]";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.getBody());

            List<SupplierDTO> fournisseurs = new ArrayList<>();
            JsonNode data = json.get("data");
            if (data.isArray()) {
                for (JsonNode supplierNode : data) {
                    String name = supplierNode.get("name").asText();
                    String supplierName = supplierNode.get("supplier_name").asText();
                    String supplierGroup = supplierNode.get("supplier_group").asText();
                    String supplierType = supplierNode.get("supplier_type").asText();
                    String country = supplierNode.get("country").asText();
                    String defaultCurrency = supplierNode.get("default_currency").asText();

                    SupplierDTO supplierDTO = new SupplierDTO(name, supplierName, supplierGroup, supplierType, country, defaultCurrency);
                    fournisseurs.add(supplierDTO);
                }
            }
            return fournisseurs;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
