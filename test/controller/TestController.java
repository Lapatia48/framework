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

    //sprint 3...(test du type de retour)
    @Url("/nombre")
    public Double nombre() {
        return 42.0;
    }

    //sprint 4...(test ModelAndView)
    @Url("/ma_vue")
    public ModelAndView vue() {
        return new ModelAndView("b.html");
    }
    
    //sprint 5...(test ModelAndView avec données)
    @Url("/vue_avec_donnees")
    public ModelAndView vueAvecDonnees() {
        Map<String, Object> data = new HashMap<>();
        data.put("titre", "Mon Titre");
        data.put("liste", Arrays.asList("item1", "item2", "item3"));
        return new ModelAndView("page.jsp", data);
    }

    //sprint 3bis(url dynamique)
    // 1. URL avec paramètre query string: /hello?id=2
    @Url("/hellow")
    public String helloWithQueryParam(Integer id) {
        return "Hello avec query id = " + id;
    }

    //sprint6...(url dynamique avec path variable)
    @Url("/hello/path")
    public String helloWithPathParam(Integer id) {
        return "Hello avec path id = " + id;
    }

    //sprint6bis (url dynamique avec request param)
    @Url("/hello/request")
    public String helloWithRequestParam(@RequestParam("id") String nombre) {
        return "Hello avec request nombre = " + nombre;
    }

    //sprint-6-ter (url dynamique avec accolades)
    @Url("/hello/braces/{id}")
    public String helloWithBraces(Integer id) {
        return "Hello avec braces id = " + id;
    }

        //test multiple path variables
        @Url("/test/multi/{var1}/path/{var2}")
        public String multiPathVariables(String var1, String var2) {
            return "Variables multiples: var1 = " + var1 + ", var2 = " + var2;
        }

        //test multi @RequestParam
        @Url("/test/multi/request")
        public String multiRequestParams(@RequestParam("param1") String param1, @RequestParam("param2") String param2) {
            return "Paramètres multiples: param1 = " + param1 + ", param2 = " + param2;
        }

        //test mixte path variable et request param
        @Url("/test/mix/{pathVar}")
        public String mixPathAndRequest(@RequestParam("reqParam") String reqParam, String pathVar) {
            return "Mixte: pathVar = " + pathVar + ", reqParam = " + reqParam;
        }


}