package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour injecter la session GLOBALE partagée entre tous les utilisateurs/navigateurs.
 * Utilisez cette annotation quand vous voulez que les données soient partagées cross-navigateur
 * sans nécessiter de login.
 * 
 * Différence avec @Session:
 * - @Session: session par utilisateur (identifiée par cookie, nécessite login pour cross-navigateur)
 * - @GlobalSession: session globale partagée entre TOUS les navigateurs/utilisateurs
 * 
 * Exemple d'utilisation:
 * public void maMethode(@GlobalSession Map<String, Object> session) {
 *     // Cette session est partagée entre Firefox, Opera, Chrome, etc.
 * }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface GlobalSession {
}
