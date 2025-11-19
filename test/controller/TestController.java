package controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import annotation.*;
import modelAndView.ModelAndView;

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

    @Url("/nombre")
    public Double nombre() {
        return 42.0;
    }

    
    @Url("/ma_vue")
    public ModelAndView vue() {
        return new ModelAndView("b.html");
    }
    
    @Url("/vue_avec_donnees")
    public ModelAndView vueAvecDonnees() {
        Map<String, Object> data = new HashMap<>();
        data.put("titre", "Mon Titre");
        data.put("liste", Arrays.asList("item1", "item2", "item3"));
        return new ModelAndView("page.jsp", data);
    }
}