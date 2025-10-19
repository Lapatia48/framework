package annotation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class UrlHandler {
    private Map<String, Method> urlMappings = new HashMap<>();

    public void scanUrlAnnotations() throws Exception {
        // appel de test controller pour l'exemple
        Class<?> controller = TestController.class;
        // Ou bien : Class<?> controller = Class.forName("annotation.TestController");
        
        for (Method method : controller.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Url.class)) {
                Url urlAnnotation = method.getAnnotation(Url.class);
                String url = urlAnnotation.value();
                urlMappings.put(url, method);
                System.out.println("URL mappée: " + url + " -> " + method.getName());
            }
        }
    }

    public String handleUrl(String url) {
        try {
            Method method = urlMappings.get(url);
            if (method != null) {
                Class<?> controllerClass = method.getDeclaringClass();
                Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
                Object result = method.invoke(controllerInstance);
                return result.toString();
            }
            return null; // URL non trouvée
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur: " + e.getMessage();
        }
    }

    public void printAllMappings() {
        System.out.println("=== TOUTES LES URLS MAPPÉES ===");
        for (String url : urlMappings.keySet()) {
            System.out.println(url + " -> " + urlMappings.get(url).getName());
        }
    }
}