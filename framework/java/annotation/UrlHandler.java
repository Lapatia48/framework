package annotation;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrlHandler {
    private Map<String, Method> urlMappings = new HashMap<>();
    private List<Class<?>> controllers = new ArrayList<>();

    public void scanControllers(String basePackage) throws Exception {
        // System.out.println("Scanning package: " + basePackage);
        
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = basePackage.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        
        List<File> directories = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            directories.add(new File(resource.getFile()));
        }
        
        List<Class<?>> classes = new ArrayList<>();
        for (File directory : directories) {
            classes.addAll(findClasses(directory, basePackage));
        }
        
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Controller.class)) {
                controllers.add(clazz);
                // System.out.println("Controller trouvé: " + clazz.getName());
            }
        }
        
        scanUrlAnnotations();
    }
    
    private List<Class<?>> findClasses(File directory, String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        
        File[] files = directory.listFiles();
        if (files == null) return classes;
        
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    classes.add(clazz);
                } catch (ClassNotFoundException e) {
                    System.err.println("Classe non trouvée: " + className);
                }
            }
        }
        return classes;
    }

    public void scanUrlAnnotations() throws Exception {
        for (Class<?> controller : controllers) {
            // System.out.println("Scanning URLs dans: " + controller.getSimpleName());
            
            for (Method method : controller.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Url.class)) {
                    Url urlAnnotation = method.getAnnotation(Url.class);
                    String url = urlAnnotation.value();
                    
                    // Vérifier les conflits d'URLs
                    if (urlMappings.containsKey(url)) {
                        Method existingMethod = urlMappings.get(url);
                        System.err.println("CONFLIT D'URL: " + url + " existe déjà dans " + 
                            existingMethod.getDeclaringClass().getSimpleName() + "." + existingMethod.getName() +
                            " et sera écrasé par " + controller.getSimpleName() + "." + method.getName());
                    }
                    
                    urlMappings.put(url, method);
                    // System.out.println("  URL mappée: " + url + " -> " + method.getName());
                }
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
            Method method = urlMappings.get(url);
            System.out.println(url + " -> " + method.getDeclaringClass().getSimpleName() + "." + method.getName());
        }
    }
    
    public int getControllerCount() {
        return controllers.size();
    }
    
    public int getUrlMappingCount() {
        return urlMappings.size();
    }

    public void addController(Class<?> controllerClass) {
        if (controllerClass.isAnnotationPresent(Controller.class)) {
            controllers.add(controllerClass);
            System.out.println("Contrôleur ajouté: " + controllerClass.getName());
        }
    }
}