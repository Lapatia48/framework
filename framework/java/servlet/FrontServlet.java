package servlet;

import java.io.IOException;
import java.util.Map;

import annotation.UrlHandler;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelAndView.ModelAndView;

@MultipartConfig
public class FrontServlet extends HttpServlet {
    
    private UrlHandler urlHandler;
    
    @Override
    public void init() throws ServletException {
        try {
            // System.out.println("=== INITIALISATION FRONT SERVLET ===");
            urlHandler = new UrlHandler();
            urlHandler.scanControllers("controller"); 

            String configuredUploadDir = getServletContext().getInitParameter("uploadDir");
            if (configuredUploadDir != null && !configuredUploadDir.trim().isEmpty()) {
                FrameworkConfig.setUploadDir(configuredUploadDir.trim());
            }
            // System.out.println("Initialisation terminée - Controllers: " + urlHandler.getControllerCount() + 
            //                  ", URLs: " + urlHandler.getUrlMappingCount());
        } catch (Exception e) {
            System.err.println("ERREUR Initialisation FrontServlet: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Erreur initialisation", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        chercherRessource(req, resp, "GET");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        chercherRessource(req, resp, "POST");
    }

    private void chercherRessource(HttpServletRequest req, HttpServletResponse resp, String httpMethod) 
            throws ServletException, IOException {
        String path = req.getRequestURI().substring(req.getContextPath().length());
        
        
        if ("/".equals(path)) {
            resp.getWriter().println("/");
            return;
        }

        
        boolean resourceExists = getServletContext().getResource(path) != null;
        if (resourceExists) {
            RequestDispatcher defaultDispatcher = getServletContext().getNamedDispatcher("default");
            defaultDispatcher.forward(req, resp);
        } else {

            Map<String, String[]> requestParams = req.getParameterMap();
            
            // Utiliser UrlHandler avec la requête et la réponse (support session cookies)
            Object[] result = urlHandler.handleUrl(path, httpMethod, requestParams, req, resp);
            if (result != null) {
                // Verifier si c'est une erreur d'autorisation
                if (result[3] instanceof UnauthorizedException) {
                    UnauthorizedException ex = (UnauthorizedException) result[3];
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    resp.setContentType("text/html;charset=UTF-8");
                    resp.getWriter().println("<html><body>");
                    resp.getWriter().println("<h1>401 - Non autorisé</h1>");
                    resp.getWriter().println("<p>" + ex.getMessage() + "</p>");
                    if (ex.isNeedsAuthentication()) {
                        resp.getWriter().println("<p>Veuillez vous <a href=\"/login\">connecter</a>.</p>");
                    } else {
                        resp.getWriter().println("<p>Rôle requis: " + ex.getRequiredRole() + "</p>");
                    }
                    resp.getWriter().println("</body></html>");
                    return;
                }
                
                String url = (String) result[0];
                Class<?> returnType = (Class<?>) result[1];
                String methodName = (String) result[2];
                Object returnValue = result[3];
                String controllerName = (String) result[4];
                Object[] methodArgs = (Object[]) result[5];
                
                if (returnValue instanceof ModelAndView) {
                    ModelAndView mv = (ModelAndView) returnValue;
                    String viewName = mv.getViewName();
                    
                    RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/" + viewName);
                    if (dispatcher != null) {
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
                    resp.getWriter().println("url: " + url);
                    resp.getWriter().println("type de retour: " + returnType.getSimpleName());
                    resp.getWriter().println("nom de la methode: " + methodName);
                    resp.getWriter().println("valeur de retour: " + returnValue);
                    resp.getWriter().println("nom du controlleur: " + controllerName);
                    resp.getWriter().println("arguments: " + java.util.Arrays.toString(methodArgs));
                }
            } else {
                resp.getWriter().println(path + " -> non trouve");
            }
        }
    }
}