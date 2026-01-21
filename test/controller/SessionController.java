package controller;

import java.util.HashMap;
import java.util.Map;

import annotation.Controller;
import annotation.Get;
import annotation.Post;
import annotation.RequestParam;
import annotation.Session;
import annotation.Url;
import modelAndView.ModelAndView;

@Controller
public class SessionController {

    // Afficher le formulaire de couleur préférée
    @Url("/session/color-form")
    @Get
    public ModelAndView showColorForm(@Session Map<String, Object> session) {
        Map<String, Object> data = new HashMap<>();
        
        // Récupérer la couleur préférée depuis la session
        String favoriteColor = (String) session.get("favoriteColor");
        data.put("favoriteColor", favoriteColor);
        
        return new ModelAndView("colorForm.jsp", data);
    }
    
    // Enregistrer la couleur préférée dans la session
    @Url("/session/save-color")
    @Post
    public ModelAndView saveColor(@RequestParam("color") String color, @Session Map<String, Object> session) {
        // Sauvegarder la couleur dans la session
        session.put("favoriteColor", color);
        
        // Rediriger vers le formulaire pour afficher le résultat
        Map<String, Object> data = new HashMap<>();
        data.put("favoriteColor", color);
        data.put("message", "Couleur enregistrée avec succès !");
        
        return new ModelAndView("colorForm.jsp", data);
    }
    
    // Supprimer la couleur préférée de la session
    @Url("/session/delete-color")
    @Post
    public ModelAndView deleteColor(@Session Map<String, Object> session) {
        // Supprimer la couleur de la session
        session.remove("favoriteColor");
        
        // Rediriger vers le formulaire
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Couleur supprimée !");
        
        return new ModelAndView("colorForm.jsp", data);
    }
}
