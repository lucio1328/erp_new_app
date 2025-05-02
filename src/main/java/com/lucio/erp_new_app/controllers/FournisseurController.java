package com.lucio.erp_new_app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    private void afficherName(ModelAndView modelAndView) {
        String loggedUser = (String) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            loggedUser = "Utilisateur inconnu";
        }

        modelAndView.addObject("loggedUser", loggedUser);
    }

    @GetMapping("/liste")
    public ModelAndView liste() {
        ModelAndView modelAndView = new ModelAndView("pages/layout/modele");

        String sessionCookie = (String) session.getAttribute("sid");
        List<SupplierDTO> fournisseurs = fournisseurService.getAllFournisseurs(sessionCookie);

        modelAndView.addObject("fournisseurs", fournisseurs);
        this.afficherName(modelAndView);
        FournisseurController.changerInformation(modelAndView, "pages/fournisseur/liste", "Liste Fournisseur");

        return modelAndView;
    }

    @GetMapping("{name}")
    public ModelAndView details(@PathVariable("name") String name) {
        ModelAndView modelAndView = new ModelAndView("pages/layout/modele");

        this.afficherName(modelAndView);

        String sessionCookie = (String) session.getAttribute("sid");
        SupplierDTO fournisseur = fournisseurService.getFournisseurByName(name, sessionCookie);
        if (fournisseur == null) {
            modelAndView.addObject("error", "Fournisseur non trouvé");
            return modelAndView;
        }

        modelAndView.addObject("fournisseur", fournisseur);

        FournisseurController.changerInformation(modelAndView, "pages/fournisseur/details", "Details Fournisseur");

        return modelAndView;
    }

    @GetMapping("/fragment/{section}")
    public String fragment(@PathVariable("section") String section) {
        switch (section) {
            case "details":
                return "pages/fournisseur/tabs/information";
            case "achats":
                return "pages/fournisseur/tabs/demandes_devis";
            case "commentaires":
                return "pages/fournisseur/tabs/commandes";
            default:
                return "pages/fournisseur/tabs/information";
        }
    }

}
