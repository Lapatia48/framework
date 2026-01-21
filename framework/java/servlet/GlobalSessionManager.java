package servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestionnaire de session global cote serveur.
 * Les donnees sont partagees entre tous les navigateurs/utilisateurs.
 * Stockage en memoire (volatile - perdu au redemarrage du serveur).
 */
public class GlobalSessionManager {
    
    // Singleton instance
    private static GlobalSessionManager instance;
    
    // Stockage global des sessions - thread-safe
    private final Map<String, Object> globalSession;
    
    private GlobalSessionManager() {
        this.globalSession = new ConcurrentHashMap<>();
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
    
    /**
     * Obtenir une copie des donnees de session globale
     */
    public Map<String, Object> getSessionCopy() {
        return new HashMap<>(globalSession);
    }
    
    /**
     * Synchroniser les modifications du Map vers la session globale
     * Gere les ajouts, modifications et suppressions
     */
    public void synchronize(Map<String, Object> modifiedSession) {
        // Supprimer les cles qui n'existent plus dans le Map modifie
        for (String key : globalSession.keySet()) {
            if (!modifiedSession.containsKey(key)) {
                globalSession.remove(key);
            }
        }
        
        // Ajouter/mettre a jour les cles du Map modifie
        for (Map.Entry<String, Object> entry : modifiedSession.entrySet()) {
            globalSession.put(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * Obtenir une valeur de la session globale
     */
    public Object get(String key) {
        return globalSession.get(key);
    }
    
    /**
     * Definir une valeur dans la session globale
     */
    public void put(String key, Object value) {
        globalSession.put(key, value);
    }
    
    /**
     * Supprimer une valeur de la session globale
     */
    public void remove(String key) {
        globalSession.remove(key);
    }
    
    /**
     * Vider toute la session globale
     */
    public void clear() {
        globalSession.clear();
    }
    
    /**
     * Verifier si une cle existe dans la session globale
     */
    public boolean containsKey(String key) {
        return globalSession.containsKey(key);
    }
}
