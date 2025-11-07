package annotation;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class UrlHandler {
    private Map<String, Method> urlMappings = new HashMap<>();
    private List<Class<?>> controllers = new ArrayList<>();

    public void scanControllers(String basePackage) throws Exception {
        controllers.clear();
        urlMappings.clear();
        
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String packagePath = basePackage.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(packagePath);
        
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            
            if ("file".equals(resource.getProtocol())) {
                scanFileSystem(resource, basePackage);
            } else if ("jar".equals(resource.getProtocol())) {
                scanJar(resource, basePackage, classLoader);
            }
        }
        
        scanUrlAnnotations();
    }

    private void scanFileSystem(URL resource, String basePackage) throws Exception {
        String filePath = resource.getFile();
        
        if (filePath.contains("WEB-INF/classes") || filePath.contains("webapps")) {
            String decodedPath = URLDecoder.decode(filePath, "UTF-8");
            File directory = new File(decodedPath);
            
            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".class")) {
                            String className = basePackage + '.' + file.getName().substring(0, file.getName().length() - 6);
                            try {
                                Class<?> clazz = Class.forName(className);
                                if (clazz.isAnnotationPresent(Controller.class)) {
                                    controllers.add(clazz);
                                }
                            } catch (ClassNotFoundException e) {
                                // Ignorer
                            }
                        }
                    }
                }
            }
        } else {
            File directory = new File(filePath);
            if (directory.exists()) {
                List<Class<?>> classes = findClasses(directory, basePackage);
                for (Class<?> clazz : classes) {
                    if (clazz.isAnnotationPresent(Controller.class)) {
                        controllers.add(clazz);
                    }
                }
            }
        }
    }

    private void scanJar(URL jarUrl, String basePackage, ClassLoader classLoader) throws Exception {
        String jarPath = jarUrl.getPath();
        if (jarPath.startsWith("file:")) {
            jarPath = jarPath.substring(5);
        }
        if (jarPath.indexOf('!') > 0) {
            jarPath = jarPath.substring(0, jarPath.indexOf('!'));
        }
        jarPath = URLDecoder.decode(jarPath, "UTF-8");
        
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            String packagePath = basePackage.replace('.', '/');
            
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                
                if (entryName.startsWith(packagePath) && entryName.endsWith(".class") && !entryName.contains("$")) {
                    String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);
                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        if (clazz.isAnnotationPresent(Controller.class)) {
                            controllers.add(clazz);
                        }
                    } catch (ClassNotFoundException | NoClassDefFoundError e) {
                        // Ignorer
                    }
                }
            }
        }
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
                    // Ignorer
                }
            }
        }
        return classes;
    }

    public void scanUrlAnnotations() throws Exception {
        for (Class<?> controller : controllers) {
            for (Method method : controller.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Url.class)) {
                    Url urlAnnotation = method.getAnnotation(Url.class);
                    String url = urlAnnotation.value();
                    
                    if (urlMappings.containsKey(url)) {
                        Method existingMethod = urlMappings.get(url);
                        System.err.println("CONFLIT D'URL: " + url + " existe déjà dans " + 
                            existingMethod.getDeclaringClass().getSimpleName() + "." + existingMethod.getName() +
                            " et sera écrasé par " + controller.getSimpleName() + "." + method.getName());
                    }
                    
                    urlMappings.put(url, method);
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
            return null;
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
        }
    }
}