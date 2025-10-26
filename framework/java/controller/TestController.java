package controller;

import annotation.*;

@Controller
public class TestController {

    @Url("/home")
    public String home() {
        return "Page d'accueil - TestController";
    }

    @Url("/about")
    public String about() {
        return "À propos - TestController";
    }

    public String notMapped() {
        return "Cette méthode ne sera pas mappée";
    }
}