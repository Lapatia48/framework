package controller;

import java.util.Map;

import annotation.Controller;
import annotation.Get;
import annotation.Post;
import annotation.Url;
import model.Employe;
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
    public String handleForm(Map<String, Object> formData, String nom) {
        StringBuilder response = new StringBuilder();
        response.append("datas :\n");
        
        if (formData != null) {
            for (Map.Entry<String, Object> entry : formData.entrySet()) {
                response.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        } else {
            response.append("formData est null\n");
        }
        response.append("Nom: ").append(nom).append("\n");
        
        return response.toString();
    }

    @Url("/form-emp")
    @Get
    public ModelAndView showFormEmp() {
        ModelAndView mv = new ModelAndView("formEmp.jsp");
        return mv;
    }

    @Url("/form-emp")
    @Post
    public String saveEmploye(Employe e) {
        StringBuilder response = new StringBuilder();
        response.append("Employe cree avec succes!\n\n");
        response.append("Details de l'employe:\n");
        response.append("- Nom: ").append(e.getNom()).append("\n");
        response.append("- Genre: ").append(e.getGenre()).append("\n");
        response.append("- ID Departement: ").append(e.getIdDepartement()).append("\n");

        return response.toString();
    }

}