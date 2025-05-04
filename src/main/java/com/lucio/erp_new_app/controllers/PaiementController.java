package com.lucio.erp_new_app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lucio.erp_new_app.dtos.payment.PaymentDTO;
import com.lucio.erp_new_app.dtos.payment.PaymentResponseGroupDTO;
import com.lucio.erp_new_app.services.PaiementService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/paiement")
public class PaiementController {

    @Autowired
    private PaiementService paiementService;

    @Autowired
    private FournisseurController fournisseurController;

    @PostMapping("/process")
    public String processPayment(
            @ModelAttribute PaymentDTO paymentDTO,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            if(paymentDTO.getReferenceNo().isBlank() || paymentDTO.getReferenceDate() == null){
                throw new IllegalArgumentException("Le N° de Référence et la Date de Référence sont nécessaires pour une Transaction Bancaire");
            }
            if (paymentDTO.getPaidAmount() == null || paymentDTO.getPaidAmount().compareTo(0.0) <= 0) {
                throw new IllegalArgumentException("Le montant payé doit être supérieur à zéro");
            }

            paymentDTO.setReceivedAmount(paymentDTO.getPaidAmount());
            paymentDTO.setSourceExchangeRate(1.0);
            paymentDTO.setAllocatedAmount(paymentDTO.getPaidAmount());
            paymentDTO.setDifferenceAmount(0.0);
            PaymentDTO.afficherPaymentDTO(paymentDTO);
            if(paymentDTO.getReferences().size()<=0){
                throw new IllegalArgumentException("Le montant reference doit être definis");
            }
            paymentDTO.getReferences().get(0).setAllocatedAmount(paymentDTO.getPaidAmount());
            PaymentResponseGroupDTO paymentResult = paiementService.processPayment(paymentDTO);

            redirectAttributes.addFlashAttribute("paymentName", paymentResult.getData().getName());
            redirectAttributes.addFlashAttribute("paymentDTO", paymentDTO);
            return "redirect:/paiement/submit?invoice=" + paymentDTO.getInvoiceName();

        }
        catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/factures/fournisseurs/" + paymentDTO.getInvoiceName();
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du traitement du paiement: " + e.getMessage());
            return "redirect:/factures/fournisseurs/" + paymentDTO.getInvoiceName();
        }
    }

    @GetMapping("/submit")
    public ModelAndView submitPaymentForm(
            @RequestParam String invoice,
            @ModelAttribute("paymentDTO") PaymentDTO paymentDTO,
            HttpSession session) {

        ModelAndView modelAndView = new ModelAndView("pages/layout/modele");

        fournisseurController.afficherName(modelAndView);

        modelAndView.addObject("view", "pages/facture/payment_submit");
        modelAndView.addObject("invoice", invoice);
        modelAndView.addObject("paymentDTO", paymentDTO);
        session.setAttribute("paymentDTO", paymentDTO);
        return modelAndView;
    }

    @PostMapping("/submit")
    public String submitPayment(
            @RequestParam String paymentName,
            RedirectAttributes redirectAttributes,
            HttpSession session
            ) {

        try {
            paiementService.submitPaymentEntry(paymentName);
            redirectAttributes.addFlashAttribute("success", "Paiement validé avec succès");
            return "redirect:/paiement/success?invoice=" + paymentName;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la soumission du paiement: " + e.getMessage());
            return "redirect:/paiement/submit?invoice=" + paymentName;
        }
    }

    @GetMapping("/success")
    public ModelAndView paymentSuccess(@RequestParam String invoice) {
        ModelAndView modelAndView = new ModelAndView("pages/layout/modele");
        modelAndView.addObject("view", "pages/facture/payment_success");
        modelAndView.addObject("invoiceNumber", invoice);
        return modelAndView;
    }
}
