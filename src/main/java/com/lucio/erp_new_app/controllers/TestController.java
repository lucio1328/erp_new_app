package com.lucio.erp_new_app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lucio.erp_new_app.dtos.purchase.PurchaseOrderDTO;
import com.lucio.erp_new_app.services.FournisseurService;

@RestController
@RequestMapping("/api/fournisseur")
public class TestController {
    @Autowired
    private FournisseurService fournisseurService;

    @GetMapping("/purchase-orders")
    public ResponseEntity<List<PurchaseOrderDTO>> getPurchaseOrdersByStatut(
            @RequestParam String status,
            @RequestHeader("Cookie") String sessionCookie) {

                List<PurchaseOrderDTO> purchaseOrders = fournisseurService.getSupplierPurchaseOrders("Summit Traders Ltd.", sessionCookie);
        List<PurchaseOrderDTO> result = fournisseurService.getPurchaseOrderByStatuts(purchaseOrders, status, "Summit Traders Ltd.", sessionCookie);
        return ResponseEntity.ok(result);
    }

    // Endpoint pour tester getPurchaseOrderNames
    @GetMapping("/purchase-order-names")
    public ResponseEntity<List<String>> getPurchaseOrderNames(
            // @RequestParam String status,
            @RequestHeader("Cookie") String sessionCookie) {

        List<String> names = fournisseurService.getPurchaseOrderNames("status", "Summit Traders Ltd.", sessionCookie);
        return ResponseEntity.ok(names);
    }
}
