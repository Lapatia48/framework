package servlet;

import java.io.IOException;
import annotation.UrlHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelAndView.ModelAndView;
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
            // Utiliser UrlHandler avec retour structuré
            Object[] result = urlHandler.handleUrl(path);
            if (result != null) {
                String url = (String) result[0];
                Class<?> returnType = (Class<?>) result[1];
                String methodName = (String) result[2];
                Object returnValue = result[3];
                String controllerName = (String) result[4];
                
                // Vérifier si c'est un ModelAndView
                if (returnValue instanceof ModelAndView) {
                    ModelAndView mv = (ModelAndView) returnValue;
                    String viewName = mv.getViewName();
                    
                    // Dispatcher vers la vue JSP/HTML
                    RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/" + viewName);
                    if (dispatcher != null) {
                        // Passer les données à la vue si elles existent
                        if (mv.getData() instanceof java.util.Map) {
                            java.util.Map<String, Object> data = (java.util.Map<String, Object>) mv.getData();
                            for (java.util.Map.Entry<String, Object> entry : data.entrySet()) {
                                req.setAttribute(entry.getKey(), entry.getValue());
                            }
                        }
                        dispatcher.forward(req, resp);
                    } else {
                        resp.getWriter().println("Vue non trouvée: " + viewName);
                    }
                } else {
                    // Affichage normal pour les autres types
                    resp.getWriter().println("url: " + url);
                    resp.getWriter().println("type de retour: " + returnType.getSimpleName());
                    resp.getWriter().println("nom de la methode: " + methodName);
                    resp.getWriter().println("valeur de retour: " + returnValue);
                    resp.getWriter().println("nom du controlleur: " + controllerName);
                }
            } else {
                // URL non trouvée
                resp.getWriter().println(path + " -> nom trouvee");
            }
        }
    }
}