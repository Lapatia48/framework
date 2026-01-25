package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour restreindre l'accès aux méthodes du contrôleur.
 * 
 * Cas d'utilisation:
 * 1. @Role - Requiert un utilisateur authentifié (session contient "user" = true)
 * 2. @Role("manager") - Requiert un rôle spécifique (session contient "role" = "manager")
 * 3. @Role({"admin", "manager"}) - Requiert un des rôles spécifiés
 * 4. Sans annotation - Accessible par tous
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Role {
    /**
     * Les rôles autorisés à accéder à la méthode.
     * Si vide, seule l'authentification est requise (user = true dans la session).
     * Si spécifié, l'utilisateur doit avoir un de ces rôles dans la session.
     */
    String[] value() default {};
}
