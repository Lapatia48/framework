package servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Gestionnaire de session cote serveur.
 * Supporte deux modes:
 * 1. Session GLOBALE: partagée entre tous les navigateurs/utilisateurs (cross-browser sans login)
 * 2. Session par UTILISATEUR: identifiée par cookie, cross-browser après login avec même username
 * 
 * Stockage en memoire (volatile - perdu au redemarrage du serveur).
 */
public class GlobalSessionManager {
    
    // Singleton instance
    private static GlobalSessionManager instance;
    
    // Nom du cookie pour l'identifiant de session
    public static final String SESSION_COOKIE_NAME = "FRAMEWORK_SESSION_ID";
    
    // Duree de vie du cookie en secondes (7 jours)
    private static final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60;
    
    // Session GLOBALE partagée entre tous les navigateurs
    private final Map<String, Object> globalSession;
    
    // Stockage des sessions par cookie - Map<cookieSessionId, Map<key, value>>
    private final Map<String, Map<String, Object>> cookieSessions;
    
    // Stockage des sessions par username (pour cross-browser après login) - Map<username, Map<key, value>>
    private final Map<String, Map<String, Object>> userSessions;
    
    private GlobalSessionManager() {
        this.globalSession = new ConcurrentHashMap<>();
        this.cookieSessions = new ConcurrentHashMap<>();
        this.userSessions = new ConcurrentHashMap<>();
    }
    
    /**
     * Obtenir l'instance unique du gestionnaire de session
     */
    public static synchronized GlobalSessionManager getInstance() {
        if (instance == null) {
            instance = new GlobalSessionManager();
        }
        return instance;
    }
    
    // ============= SESSION GLOBALE (cross-browser sans login) =============
    
    /**
     * Obtenir une copie de la session GLOBALE (partagée entre tous)
     */
    public Map<String, Object> getGlobalSessionCopy() {
        return new HashMap<>(globalSession);
    }
    
    /**
     * Synchroniser les modifications vers la session GLOBALE
     */
    public void synchronizeGlobal(Map<String, Object> modifiedSession) {
        // Supprimer les cles qui n'existent plus
        for (String key : globalSession.keySet()) {
            if (!modifiedSession.containsKey(key)) {
                globalSession.remove(key);
            }
        }
        // Ajouter/mettre a jour
        for (Map.Entry<String, Object> entry : modifiedSession.entrySet()) {
            globalSession.put(entry.getKey(), entry.getValue());
        }
    }
    
    // ============= SESSION PAR COOKIE =============
    
    /**
     * Obtenir ou creer l'ID de session pour un navigateur (cookie)
     */
    public String getOrCreateSessionId(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = getSessionIdFromCookie(request);
        
        if (sessionId == null || !cookieSessions.containsKey(sessionId)) {
            sessionId = UUID.randomUUID().toString();
            cookieSessions.put(sessionId, new ConcurrentHashMap<>());
            
            if (response != null) {
                Cookie cookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
                cookie.setPath("/");
                cookie.setMaxAge(COOKIE_MAX_AGE);
                cookie.setHttpOnly(true);
                response.addCookie(cookie);
            }
        }
        
        return sessionId;
    }
    
    /**
     * Obtenir l'ID de session depuis le cookie
     */
    public String getSessionIdFromCookie(HttpServletRequest request) {
        if (request == null) return null;
        
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (SESSION_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    
    /**
     * Obtenir une copie de la session utilisateur.
     * Si l'utilisateur est connecté (a un username), retourne la session liée au username (cross-browser).
     * Sinon, retourne la session liée au cookie (par navigateur).
     */
    public Map<String, Object> getSessionCopy(String sessionId) {
        if (sessionId == null) {
            return new HashMap<>();
        }
        
        // Vérifier si cet utilisateur est connecté (a un username)
        Map<String, Object> cookieSession = cookieSessions.get(sessionId);
        if (cookieSession != null) {
            String username = (String) cookieSession.get("username");
            if (username != null && !username.isEmpty()) {
                // Utilisateur connecté: retourner la session liée au username (cross-browser)
                Map<String, Object> userSession = userSessions.computeIfAbsent(username, k -> new ConcurrentHashMap<>());
                // Fusionner les données d'auth du cookie
                Map<String, Object> merged = new HashMap<>(userSession);
                merged.put("user", cookieSession.get("user"));
                merged.put("username", username);
                merged.put("role", cookieSession.get("role"));
                return merged;
            }
        }
        
        // Pas connecté: retourner la session du cookie
        if (cookieSession == null) {
            return new HashMap<>();
        }
        return new HashMap<>(cookieSession);
    }
    
    /**
     * Synchroniser les modifications de session.
     * Si l'utilisateur est connecté, synchronise vers la session username (cross-browser).
     */
    public void synchronize(String sessionId, Map<String, Object> modifiedSession) {
        if (sessionId == null) return;
        
        Map<String, Object> cookieSession = cookieSessions.computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>());
        
        // Toujours stocker les infos d'auth dans la session cookie
        if (modifiedSession.containsKey("user")) {
            cookieSession.put("user", modifiedSession.get("user"));
        } else {
            cookieSession.remove("user");
        }
        if (modifiedSession.containsKey("username")) {
            cookieSession.put("username", modifiedSession.get("username"));
        } else {
            cookieSession.remove("username");
        }
        if (modifiedSession.containsKey("role")) {
            cookieSession.put("role", modifiedSession.get("role"));
        } else {
            cookieSession.remove("role");
        }
        
        // Si l'utilisateur est connecté, synchroniser les autres données vers userSessions
        String username = (String) modifiedSession.get("username");
        if (username != null && !username.isEmpty()) {
            Map<String, Object> userSession = userSessions.computeIfAbsent(username, k -> new ConcurrentHashMap<>());
            
            // Supprimer les clés qui n'existent plus (sauf les clés d'auth)
            for (String key : userSession.keySet()) {
                if (!modifiedSession.containsKey(key) && !isAuthKey(key)) {
                    userSession.remove(key);
                }
            }
            
            // Ajouter/mettre à jour (sauf les clés d'auth qui restent dans cookieSession)
            for (Map.Entry<String, Object> entry : modifiedSession.entrySet()) {
                if (!isAuthKey(entry.getKey())) {
                    userSession.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            // Pas connecté: synchroniser vers cookieSession normalement
            for (String key : cookieSession.keySet()) {
                if (!modifiedSession.containsKey(key) && !isAuthKey(key)) {
                    cookieSession.remove(key);
                }
            }
            for (Map.Entry<String, Object> entry : modifiedSession.entrySet()) {
                if (!isAuthKey(entry.getKey())) {
                    cookieSession.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }
    
    private boolean isAuthKey(String key) {
        return "user".equals(key) || "username".equals(key) || "role".equals(key);
    }
    
    /**
     * Compatibilite avec l'ancienne API
     */
    @Deprecated
    public Map<String, Object> getSessionCopy() {
        return getGlobalSessionCopy();
    }
    
    @Deprecated
    public void synchronize(Map<String, Object> modifiedSession) {
        synchronizeGlobal(modifiedSession);
    }
    
    // ============= MÉTHODES D'AUTHENTIFICATION =============
    
    /**
     * Verifier si l'utilisateur est authentifie
     */
    public boolean isAuthenticated(String sessionId) {
        if (sessionId == null) return false;
        Map<String, Object> session = cookieSessions.get(sessionId);
        if (session == null) return false;
        Object user = session.get("user");
        return user != null && Boolean.TRUE.equals(user);
    }
    
    /**
     * Obtenir le role de l'utilisateur
     */
    public String getUserRole(String sessionId) {
        if (sessionId == null) return null;
        Map<String, Object> session = cookieSessions.get(sessionId);
        if (session == null) return null;
        Object role = session.get("role");
        return role != null ? role.toString() : null;
    }
    
    /**
     * Vider la session d'un utilisateur
     */
    public void clear(String sessionId) {
        Map<String, Object> session = cookieSessions.get(sessionId);
        if (session != null) {
            session.clear();
        }
    }
    
    /**
     * Supprimer completement une session
     */
    public void destroySession(String sessionId) {
        cookieSessions.remove(sessionId);
    }
    
    /**
     * Nombre de sessions actives
     */
    public int getActiveSessionCount() {
        return cookieSessions.size();
    }
}
