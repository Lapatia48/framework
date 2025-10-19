// TestController.java
package annotation;

public class TestController {

    @Url("/hello")
    public String sayHello() {
        return "Bonjour depuis l'annotation @Url !";
    }

    @Url("/users")
    public String getUsers() {
        return "Liste des utilisateurs";
    }

    @Url("/test")
    public String testMethod() {
        return "Test réussi avec @Url !";
    }

    // Méthode sans annotation - ne sera pas mappée
    public String methodSansAnnotation() {
        return "Méthode ignorée";
    }
}