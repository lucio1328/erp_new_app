package com.lucio.erp_new_app.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lucio.erp_new_app.dtos.supplier.QuotationItemDTO;
import com.lucio.erp_new_app.dtos.supplier.SupplierQuotationDTO;
import com.lucio.erp_new_app.services.FournisseurService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/fournisseur/quotation")
public class QuotationController {

    private final FournisseurService fournisseurService;

    @Autowired
    public QuotationController(FournisseurService fournisseurService) {
        this.fournisseurService = fournisseurService;
    }

    @PostMapping("/update/{id}")
    public ModelAndView updateQuotationItems(
            @PathVariable("id") String quotationId,
            @ModelAttribute("supplierQuotation") SupplierQuotationDTO supplierQuotation,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        ModelAndView modelAndView = new ModelAndView("redirect:/fournisseur/quotation/edit/" + quotationId);
        String sessionCookie = (String) session.getAttribute("sid");

        try {
            SupplierQuotationDTO currentQuotation = fournisseurService.getSupplierQuotationByName(quotationId, sessionCookie);
            if (!"Draft".equalsIgnoreCase(currentQuotation.getStatus())) {
                throw new IllegalStateException("Seuls les devis en statut Draft peuvent être modifiés");
            }

            List<QuotationItemDTO> itemsToUpdate = new ArrayList<>();
            for (QuotationItemDTO formItem : supplierQuotation.getQuotationItemDTOs()) {
                QuotationItemDTO originalItem = currentQuotation.getQuotationItemDTOs().stream()
                        .filter(item -> item.getName().equals(formItem.getName()))
                        .findFirst()
                        .orElse(null);

                if (originalItem != null &&
                    (!originalItem.getQuantity().equals(formItem.getQuantity()) ||
                    !originalItem.getRate().equals(formItem.getRate()))) {

                    QuotationItemDTO updatedItem = new QuotationItemDTO();
                    updatedItem.setName(formItem.getName());
                    updatedItem.setItemCode(originalItem.getItemCode());
                    updatedItem.setQuantity(formItem.getQuantity());
                    updatedItem.setRate(formItem.getRate());
                    updatedItem.setWarehouse(originalItem.getWarehouse());
                    updatedItem.setUom(originalItem.getUom());

                    itemsToUpdate.add(updatedItem);
                }
            }

            if (!itemsToUpdate.isEmpty()) {
                fournisseurService.updateQuotationItems(quotationId, itemsToUpdate, sessionCookie);
                redirectAttributes.addFlashAttribute("success", "Devis mis à jour avec succès");
            }
            else {
                redirectAttributes.addFlashAttribute("info", "Aucune modification détectée");
            }
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour: " + e.getMessage());
        }

        return modelAndView;
    }

}
