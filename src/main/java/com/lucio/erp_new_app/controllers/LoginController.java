package com.lucio.erp_new_app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lucio.erp_new_app.dtos.auth.LoginForm;
import com.lucio.erp_new_app.response.LoginResult;
import com.lucio.erp_new_app.services.AuthService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username, @RequestParam("password") String password, Model model, HttpSession session) {
        LoginResult result = authService.loginToERPNext(new LoginForm(username, password));

        if (result.isSuccess()) {
            String sessionCookie = result.getSessionCookie();
            session.setAttribute("sid", sessionCookie);
            System.out.println(sessionCookie);

            String loggedUser = authService.getLoggedUsername(sessionCookie);
            session.setAttribute("loggedUser", loggedUser);
            return "redirect:/fournisseur/liste";
        }
        else {
            model.addAttribute("error", result.getMessage());
            return "index";
        }
    }

    @GetMapping("/deconnexion")
    public String logout(HttpSession session) {
        session.removeAttribute("sid");
        session.removeAttribute("loggedUser");
        session.invalidate();

        return "redirect:/";
    }
}
