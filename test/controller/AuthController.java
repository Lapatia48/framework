package controller;

import java.util.HashMap;
import java.util.Map;

import annotation.Controller;
import annotation.Get;
import annotation.Post;
import annotation.RequestParam;
import annotation.Role;
import annotation.Session;
import annotation.Url;
import modelAndView.ModelAndView;

/**
 * Contrôleur de démonstration pour l'annotation @Role
 * 
 * Cas d'utilisation:
 * 1. Sans @Role - Accessible par tous
 * 2. @Role - Requiert authentification (user=true dans session)
 * 3. @Role("manager") - Requiert le rôle "manager"
 * 4. @Role({"admin", "manager"}) - Requiert le rôle "admin" OU "manager"
 */
@Controller
public class AuthController {

    // ============= Pages publiques (sans @Role) =============
    
    /**
     * Page d'accueil publique - accessible par tous
     */
    @Url("/auth/home")
    @Get
    public String publicHome() {
        return "Bienvenue sur la page publique!";
    }
    
    /**
     * Formulaire de login - accessible par tous
     */
    @Url("/auth/login")
    @Get
    public ModelAndView showLoginForm(@Session Map<String, Object> session) {
        Map<String, Object> data = new HashMap<>();
        data.put("isLoggedIn", session.get("user") != null && Boolean.TRUE.equals(session.get("user")));
        data.put("userRole", session.get("role"));
        return new ModelAndView("login.jsp", data);
    }
    
    /**
     * Traitement du login - définit user=true et role dans la session
     */
    @Url("/auth/login")
    @Post
    public ModelAndView doLogin(
            @RequestParam("username") String username, 
            @RequestParam("password") String password,
            @RequestParam("role") String role,
            @Session Map<String, Object> session) {
        
        Map<String, Object> data = new HashMap<>();
        
        // Simulation de vérification login (en production: vérifier en BDD)
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            // Définir l'utilisateur comme authentifié
            session.put("user", true);
            session.put("username", username);
            
            // Définir le rôle si spécifié
            if (role != null && !role.isEmpty()) {
                session.put("role", role);
            }
            
            data.put("message", "Connexion réussie! Bienvenue " + username);
            data.put("isLoggedIn", true);
            data.put("userRole", role);
        } else {
            data.put("error", "Nom d'utilisateur et mot de passe requis");
            data.put("isLoggedIn", false);
        }
        
        return new ModelAndView("login.jsp", data);
    }
    
    /**
     * Déconnexion - supprime les infos de session
     */
    @Url("/auth/logout")
    @Get
    public ModelAndView logout(@Session Map<String, Object> session) {
        session.remove("user");
        session.remove("username");
        session.remove("role");
        
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Vous avez été déconnecté");
        data.put("isLoggedIn", false);
        
        return new ModelAndView("login.jsp", data);
    }
    
    // ============= Pages protégées - authentification requise (@Role) =============
    
    /**
     * Page protégée - nécessite seulement d'être authentifié (user=true)
     */
    @Url("/auth/profile")
    @Get
    @Role
    public String viewProfile(@Session Map<String, Object> session) {
        String username = (String) session.get("username");
        String role = (String) session.get("role");
        return "Profil de " + username + " (rôle: " + (role != null ? role : "aucun") + ")";
    }
    
    /**
     * Page protégée - nécessite seulement d'être authentifié
     */
    @Url("/auth/dashboard")
    @Get
    @Role
    public ModelAndView viewDashboard(@Session Map<String, Object> session) {
        Map<String, Object> data = new HashMap<>();
        data.put("username", session.get("username"));
        data.put("role", session.get("role"));
        data.put("message", "Bienvenue sur votre tableau de bord!");
        return new ModelAndView("dashboard.jsp", data);
    }
    
    // ============= Pages avec rôle spécifique =============
    
    /**
     * Page admin - nécessite le rôle "admin"
     */
    @Url("/auth/admin")
    @Get
    @Role("admin")
    public String adminPage(@Session Map<String, Object> session) {
        return "Page d'administration - Réservée aux admins";
    }
    
    /**
     * Page manager - nécessite le rôle "manager"
     */
    @Url("/auth/manager")
    @Get
    @Role("manager")
    public String managerPage(@Session Map<String, Object> session) {
        return "Page Manager - Réservée aux managers";
    }
    
    /**
     * Page accessible aux admins OU managers
     */
    @Url("/auth/reports")
    @Get
    @Role({"admin", "manager"})
    public String reportsPage(@Session Map<String, Object> session) {
        String username = (String) session.get("username");
        String role = (String) session.get("role");
        return "Rapports confidentiels - Utilisateur: " + username + " (rôle: " + role + ")";
    }
    
    // ============= API de test pour vérifier le statut =============
    
    /**
     * API pour vérifier le statut de la session
     */
    @Url("/auth/status")
    @Get
    public String checkStatus(@Session Map<String, Object> session) {
        boolean isLoggedIn = session.get("user") != null && Boolean.TRUE.equals(session.get("user"));
        String username = (String) session.get("username");
        String role = (String) session.get("role");
        
        return "Statut: " + (isLoggedIn ? "Connecté" : "Non connecté") + 
               ", Utilisateur: " + (username != null ? username : "N/A") +
               ", Rôle: " + (role != null ? role : "N/A");
    }
}
