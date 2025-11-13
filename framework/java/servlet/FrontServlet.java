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
            Object[] result = urlHandler.handleUrl(path);
            if (result != null) {
                // URL trouvée dans les annotations @Url
                String url = (String) result[0];
                Class<?> returnType = (Class<?>) result[1];
                String methodName = (String) result[2];
                Object returnValue = result[3];
                String controllerName = (String) result[4];
                
                // resp.getWriter().println(url + " -> " + methodName + "() [" + returnType.getSimpleName() + "] = " + returnValue + " (from " + controllerName + ")");
                resp.getWriter().println("url: " + url); //le url
                resp.getWriter().println("type de retour: " + returnType.getSimpleName()); //le type de retour
                resp.getWriter().println("nom de la methode: " + methodName); //le nom de la méthode
                resp.getWriter().println("valeur de retour: " + returnValue); //la valeur du return
                resp.getWriter().println("nom du controlleur: " + controllerName); //le nom du contrôleur
            } else {
                // URL non trouvée
                resp.getWriter().println(path + " -> nom trouvee");
            }
        }
    }
}