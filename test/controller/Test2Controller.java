package controller;

import java.util.Map;

import annotation.Controller;
import annotation.Get;
import annotation.Post;
import annotation.Url;
import modelAndView.ModelAndView;

@Controller
public class Test2Controller {

    @Url("/contact")
    public String contact() {
        return "Page contact - Test2Controller";
    }

    @Url("/serve")
    public String services() {
        return "Services - Cette méthode n'est pas mappée";
    }

    public String anotherMethod() {
        return "Autre méthode sans annotation";
    }

    @Url("/formulaire")
    @Get
    public ModelAndView modelAndView() {
        ModelAndView mv = new ModelAndView("formulaire.jsp");
        return mv;
    }

    @Url("/formulaire")
    @Post
    public String handleForm(Map<String, String> formData, String nom) {
        StringBuilder response = new StringBuilder();
        response.append("datas :\n");
        
        if (formData != null) {
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                response.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        } else {
            response.append("null\n");
        }
        response.append("Nom: ").append(nom).append("\n");
        
        return response.toString();
    }

}