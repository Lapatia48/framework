package servlet;

/**
 * Exception levée quand l'accès à une ressource est non autorisé.
 */
public class UnauthorizedException extends RuntimeException {
    
    private final boolean needsAuthentication;
    private final String requiredRole;
    
    /**
     * Constructeur pour accès non authentifié
     */
    public UnauthorizedException(String message) {
        super(message);
        this.needsAuthentication = true;
        this.requiredRole = null;
    }
    
    /**
     * Constructeur pour accès avec rôle insuffisant
     */
    public UnauthorizedException(String message, String requiredRole) {
        super(message);
        this.needsAuthentication = false;
        this.requiredRole = requiredRole;
    }
    
    public boolean isNeedsAuthentication() {
        return needsAuthentication;
    }
    
    public String getRequiredRole() {
        return requiredRole;
    }
}
