package com.lucio.erp_new_app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lucio.erp_new_app.dtos.LoginForm;
import com.lucio.erp_new_app.exception.LoginResult;
import com.lucio.erp_new_app.services.AuthService;

@Controller
public class LoginController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {
        LoginResult result = authService.loginToERPNext(new LoginForm(username, password));
        if (result.isSuccess()) {
            return "redirect:/fournisseur/accueil";
        }
        else {
            model.addAttribute("error", result.getMessage());
            return "index";
        }
    }
}
