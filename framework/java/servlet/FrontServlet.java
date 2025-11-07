package servlet;

import java.io.IOException;
import annotation.UrlHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;
import java.util.List;

public class FrontServlet extends HttpServlet {
    
    private UrlHandler urlHandler;
    
    @Override
    public void init() throws ServletException {
        try {
            System.out.println("=== INITIALISATION FRONT SERVLET ===");
            urlHandler = new UrlHandler();
            urlHandler.scanControllers("controller"); 
            System.out.println("Initialisation terminée - Controllers: " + urlHandler.getControllerCount() + 
                             ", URLs: " + urlHandler.getUrlMappingCount());
        } catch (Exception e) {
            System.err.println("ERREUR Initialisation FrontServlet: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Erreur initialisation", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        chercherRessource(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        chercherRessource(req, resp);
    }

    private void chercherRessource(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String path = req.getRequestURI().substring(req.getContextPath().length());
        
        // Page d'accueil
        if ("/".equals(path)) {
            resp.getWriter().println("/");
            return;
        }

        // Vérifier si c'est une ressource statique
        boolean resourceExists = getServletContext().getResource(path) != null;
        if (resourceExists) {
            // Déléguer au conteneur servlet par défaut pour les ressources statiques
            RequestDispatcher defaultDispatcher = getServletContext().getNamedDispatcher("default");
            defaultDispatcher.forward(req, resp);
        } else {
            // Utiliser UrlHandler comme dans Main.java
            String result = urlHandler.handleUrl(path);
            if (result != null) {
                // URL trouvée dans les annotations @Url
                resp.getWriter().println(path + " -> " + result);
            } else {
                // URL non trouvée
                resp.getWriter().println(path);
            }
        }
    }
}