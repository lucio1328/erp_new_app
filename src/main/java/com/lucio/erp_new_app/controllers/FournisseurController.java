package com.lucio.erp_new_app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lucio.erp_new_app.dtos.SupplierDTO;
import com.lucio.erp_new_app.services.FournisseurService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/fournisseur")
public class FournisseurController {

    @Autowired
    private FournisseurService fournisseurService;

    @Autowired
    private HttpSession session;

    private static void changerInformation(ModelAndView modelAndView, String view, String title) {
        if (modelAndView != null) {
            modelAndView.addObject("view", view);
            modelAndView.addObject("title", title);
        }
        return;
    }

    @GetMapping("/liste")
    public ModelAndView liste() {
        ModelAndView modelAndView = new ModelAndView("pages/layout/modele");

        String sessionCookie = (String) session.getAttribute("sid");
        List<SupplierDTO> fournisseurs = fournisseurService.getAllFournisseurs(sessionCookie);

        modelAndView.addObject("fournisseurs", fournisseurs);

        String loggedUser = (String) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            loggedUser = "Utilisateur inconnu";
        }
        modelAndView.addObject("loggedUser", loggedUser);

        FournisseurController.changerInformation(modelAndView, "pages/fournisseur/liste", "Liste Fournisseur");

        return modelAndView;
    }
}
