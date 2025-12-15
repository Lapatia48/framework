package annotation;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class UrlHandler {
    private Map<String, Map<String, Method>> urlMappings = new HashMap<>();
    private List<Class<?>> controllers = new ArrayList<>();
    private Map<String, String[]> urlPatterns = new HashMap<>(); // Pour les URLs avec {param}

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
                            // Ignorer les classes non trouvées
                        }
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
                        
                    }
                }
            }
        }
    }

    private void scanUrlAnnotations() throws Exception {
        for (Class<?> controller : controllers) {
            for (Method method : controller.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Url.class)) {
                    Url urlAnnotation = method.getAnnotation(Url.class);
                    String url = urlAnnotation.value();
                    
                    // Détection des URLs avec paramètres {param}
                    if (url.contains("{")) {
                        urlPatterns.put(url, extractParamNames(url));
                    }
                    
                    // Déterminer la méthode HTTP
                    String httpMethod = "GET"; // Par défaut
                    if (method.isAnnotationPresent(Post.class)) {
                        httpMethod = "POST";
                    } else if (method.isAnnotationPresent(Get.class)) {
                        httpMethod = "GET";
                    }
                    
                    // Initialiser la map pour cette URL si nécessaire
                    if (!urlMappings.containsKey(url)) {
                        urlMappings.put(url, new HashMap<>());
                    }
                    
                    Map<String, Method> methodsForUrl = urlMappings.get(url);
                    
                    if (methodsForUrl.containsKey(httpMethod)) {
                        Method existingMethod = methodsForUrl.get(httpMethod);
                        System.err.println("CONFLIT D'URL: " + httpMethod + " " + url + " existe déjà dans " + 
                            existingMethod.getDeclaringClass().getSimpleName() + "." + existingMethod.getName() +
                            " et sera écrasé par " + controller.getSimpleName() + "." + method.getName());
                    }
                    
                    methodsForUrl.put(httpMethod, method);
                }
            }
        }
    }

    private String[] extractParamNames(String url) {
        List<String> params = new ArrayList<>();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{([^}]+)\\}");
        java.util.regex.Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            params.add(matcher.group(1));
        }
        return params.toArray(new String[0]);
    }

    public Object[] handleUrl(String url, String httpMethod, Map<String, String[]> requestParams) {
        try {
            Method method = findMatchingMethod(url, httpMethod);
            if (method != null) {
                Class<?> controllerClass = method.getDeclaringClass();
                Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
                
                // Préparer les arguments pour la méthode
                Object[] args = prepareMethodArguments(method, url, requestParams);
                
                Object resultValue = method.invoke(controllerInstance, args);
                Class<?> returnType = method.getReturnType();
                
                // Vérifier si la méthode est annotée @Api
                boolean isApiMethod = method.isAnnotationPresent(Api.class);
                
                // Si @Api, convertir la réponse en JSON (sauf ModelAndView)
                if (isApiMethod && !(resultValue instanceof modelAndView.ModelAndView)) {
                    resultValue = convertToJson(resultValue, returnType);
                }
                
                return new Object[] {
                    url,
                    returnType,
                    method.getName(),
                    resultValue,
                    controllerClass.getSimpleName(),
                    args // [5] = arguments passés (bonus pour debug)
                };
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            
            // Si c'est une méthode @Api, retourner une erreur en JSON
            try {
                Method method = findMatchingMethod(url, httpMethod);
                if (method != null && method.isAnnotationPresent(Api.class)) {
                    String errorJson = createErrorJson(e.getMessage());
                    return new Object[] {
                        url,
                        String.class,
                        null,
                        errorJson,
                        null,
                        null
                    };
                }
            } catch (Exception ignored) {}
            
            return new Object[] {
                url,
                null,
                null,
                "Erreur: " + e.getMessage(),
                null,
                null
            };
        }
    }

    private Method findMatchingMethod(String url, String httpMethod) {
        String pathWithoutQuery = url.contains("?") ? url.split("\\?")[0] : url;
        
        try {
            pathWithoutQuery = java.net.URLDecoder.decode(pathWithoutQuery, "UTF-8");
        } catch (Exception e) {}
        
        // 1. Recherche exacte
        if (urlMappings.containsKey(pathWithoutQuery)) {
            Map<String, Method> methods = urlMappings.get(pathWithoutQuery);
            if (methods.containsKey(httpMethod)) {
                return methods.get(httpMethod);
            }
        }
        
        // 2. Recherche pattern
        for (Map.Entry<String, String[]> entry : urlPatterns.entrySet()) {
            String pattern = entry.getKey();
            String regex = pattern.replaceAll("\\{[^}]+\\}", "([^/]+)");
            if (pathWithoutQuery.matches(regex)) {
                if (urlMappings.containsKey(pattern)) {
                    Map<String, Method> methods = urlMappings.get(pattern);
                    if (methods.containsKey(httpMethod)) {
                        return methods.get(httpMethod);
                    }
                }
            }
        }
        
        // 3. Recherche par base
        if (!url.contains("?")) {
            String baseUrl = extractBaseUrl(pathWithoutQuery);
            if (baseUrl != null && urlMappings.containsKey(baseUrl)) {
                Map<String, Method> methods = urlMappings.get(baseUrl);
                if (methods.containsKey(httpMethod)) {
                    return methods.get(httpMethod);
                }
            }
        }
        
        return null;
    }

    private String extractBaseUrl(String url) {
        int lastSlash = url.lastIndexOf('/');
        if (lastSlash > 0) {
            return url.substring(0, lastSlash);
        }
        return null;
    }

    private Object[] prepareMethodArguments(Method method, String url, Map<String, String[]> requestParams) {
        Class<?>[] paramTypes = method.getParameterTypes();
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[paramTypes.length];
        
        String pathWithoutQuery = url.contains("?") ? url.split("\\?")[0] : url;
        
        try {
            pathWithoutQuery = java.net.URLDecoder.decode(pathWithoutQuery, "UTF-8");
        } catch (Exception e) {}
        
        // Convertir les keys de requestParams en liste pour accès par index
        List<String> paramKeys = new ArrayList<>(requestParams.keySet());
        
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];
            Parameter parameter = parameters[i];
            args[i] = null;
            
            // Map<String, Object> - Injection automatique des paramètres du formulaire
            if (paramType == Map.class || Map.class.isAssignableFrom(paramType)) {
                // Vérifier les types génériques: doit être Map<String, Object>
                java.lang.reflect.Type genericType = parameter.getParameterizedType();
                if (genericType instanceof java.lang.reflect.ParameterizedType) {
                    java.lang.reflect.ParameterizedType parameterizedType = (java.lang.reflect.ParameterizedType) genericType;
                    java.lang.reflect.Type[] typeArguments = parameterizedType.getActualTypeArguments();
                    
                    // Vérifier que c'est Map<String, Object>
                    if (typeArguments.length == 2 && 
                        typeArguments[0].equals(String.class) && 
                        typeArguments[1].equals(Object.class)) {
                        
                        Map<String, Object> formData = new HashMap<>();
                        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
                            String key = entry.getKey();
                            String[] values = entry.getValue();
                            if (values != null && values.length > 0) {
                                // Si un seul élément, stocker directement la valeur
                                if (values.length == 1) {
                                    formData.put(key, values[0]);
                                } else {
                                    // Si plusieurs éléments, stocker le tableau
                                    formData.put(key, values);
                                }
                            }
                        }
                        args[i] = formData;
                    }
                }
                // C'est un Map, on ne tente pas les autres stratégies
                continue;
            }
            
            // STRATÉGIE 0.4: Injection automatique de List<Object> depuis le formulaire
            if (paramType == List.class || List.class.isAssignableFrom(paramType)) {
                Type genericType = parameter.getParameterizedType();
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericType;
                    Type[] typeArguments = parameterizedType.getActualTypeArguments();
                    
                    if (typeArguments.length == 1 && typeArguments[0] instanceof Class) {
                        Class<?> elementType = (Class<?>) typeArguments[0];
                        List<Object> list = injectListFromForm(elementType, parameter.getName(), requestParams);
                        if (list != null && !list.isEmpty()) {
                            args[i] = list;
                            continue;
                        }
                    }
                }
            }
            
            // STRATÉGIE 0.5: Injection automatique d'objets personnalisés
            if (!paramType.isPrimitive() && paramType != String.class && !Map.class.isAssignableFrom(paramType) && !List.class.isAssignableFrom(paramType)) {
                try {
                    Object injectedObject = injectObjectFromForm(paramType, parameter.getName(), requestParams);
                    if (injectedObject != null) {
                        args[i] = injectedObject;
                        continue;
                    }
                } catch (Exception e) {
                    // Si l'injection échoue, continuer avec les autres stratégies
                }
            }
            
            // @RequestParam annotation 
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                RequestParam annotation = parameter.getAnnotation(RequestParam.class);
                String paramName = annotation.value().isEmpty() ? parameter.getName() : annotation.value();
                String[] values = requestParams.get(paramName);
                if (values != null && values.length > 0) {
                    args[i] = convertValue(values[0], paramType);
                    continue;
                }
            }
            
            // Correspondance par nom exact
            String[] values = requestParams.get(parameter.getName());
            if (values != null && values.length > 0) {
                args[i] = convertValue(values[0], paramType);
                continue;
            }
            
            // Si le paramètre s'appelle arg0, arg1, etc., utiliser l'index
            if (parameter.getName().startsWith("arg") && !paramKeys.isEmpty()) {
                try {
                    int argIndex = Integer.parseInt(parameter.getName().substring(3));
                    if (argIndex < paramKeys.size()) {
                        String key = paramKeys.get(argIndex);
                        values = requestParams.get(key);
                        if (values != null && values.length > 0) {
                            args[i] = convertValue(values[0], paramType);
                            continue;
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignorer si ce n'est pas un arg numérique
                }
            }
            
            // Prendre le premier paramètre disponible (fallback)
            if (args[i] == null && !paramKeys.isEmpty() && i < paramKeys.size()) {
                String key = paramKeys.get(i);
                values = requestParams.get(key);
                if (values != null && values.length > 0) {
                    args[i] = convertValue(values[0], paramType);
                    continue;
                }
            }
            
            // Paramètres d'URL pattern
            if (args[i] == null && urlPatterns.containsKey(findPatternUrl(pathWithoutQuery))) {
                String patternUrl = findPatternUrl(pathWithoutQuery);
                Map<String, String> urlParams = extractUrlParams(patternUrl, pathWithoutQuery);
                String paramValue = urlParams.get(parameter.getName());
                if (paramValue != null) {
                    args[i] = convertValue(paramValue, paramType);
                    continue;
                }
            }
            
            // Paramètres de path simple
            if (args[i] == null && pathWithoutQuery.contains("/") && !pathWithoutQuery.contains("{") && !pathWithoutQuery.contains("?")) {
                String[] urlParts = pathWithoutQuery.split("/");
                String lastPart = urlParts[urlParts.length - 1];
                if (lastPart.matches("\\d+") && isNumericType(paramType)) {
                    args[i] = convertValue(lastPart, paramType);
                    continue;
                }
            }
        }
        
        return args;
    }

    //méthode helper
    private boolean isNumericType(Class<?> type) {
        return type == Integer.class || type == int.class || 
            type == Long.class || type == long.class ||
            type == Double.class || type == double.class ||
            type == Float.class || type == float.class;
    }

    private String findPatternUrl(String url) {
        for (String pattern : urlPatterns.keySet()) {
                String regex = pattern.replaceAll("\\{[^}]+\\}", "([^/]+)");
                if (url.matches(regex)) {
                    return pattern;
                }
            }
            return null;
        }

        private Map<String, String> extractUrlParams(String pattern, String url) {
        Map<String, String> params = new HashMap<>();
        String[] patternParts = pattern.split("/");
        String[] urlParts = url.split("/");
        
        for (int i = 0; i < patternParts.length && i < urlParts.length; i++) {
            if (patternParts[i].startsWith("{") && patternParts[i].endsWith("}")) {
                String paramName = patternParts[i].substring(1, patternParts[i].length() - 1);
                params.put(paramName, urlParts[i]);
            }
        }
        return params;
    }

    private Object convertValue(String value, Class<?> targetType) {
        if (value == null) return null;
        
        try {
            if (targetType == String.class) return value;
            if (targetType == Integer.class || targetType == int.class) return Integer.parseInt(value);
            if (targetType == Long.class || targetType == long.class) return Long.parseLong(value);
            if (targetType == Double.class || targetType == double.class) return Double.parseDouble(value);
            if (targetType == Boolean.class || targetType == boolean.class) return Boolean.parseBoolean(value);
            // Ajouter d'autres types au besoin
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }

    public Object[] handleUrl(String url) {
        try {
            Method method = findMatchingMethod(url, "GET"); // GET par défaut
            if (method != null) {
                Class<?> controllerClass = method.getDeclaringClass();
                Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
                Object resultValue = method.invoke(controllerInstance);
                Class<?> returnType = method.getReturnType();
                
                return new Object[] {
                    url,                    // [0] = l'argument path
                    returnType,             // [1] = le type de retour (Class)
                    method.getName(),       // [2] = le nom de la méthode
                    resultValue,            // [3] = la valeur du return
                    controllerClass.getSimpleName() // [4] = nom du contrôleur
                };
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[] {
                url,
                null,
                null,
                "Erreur: " + e.getMessage(),
                null
            };
        }
    }
    
    public void printAllMappings() {
        System.out.println("=== TOUTES LES URLS MAPPÉES ===");
        for (String url : urlMappings.keySet()) {
            Map<String, Method> methodsForUrl = urlMappings.get(url);
            for (Map.Entry<String, Method> entry : methodsForUrl.entrySet()) {
                String httpMethod = entry.getKey();
                Method method = entry.getValue();
                System.out.println(httpMethod + " " + url + " -> " + 
                    method.getDeclaringClass().getSimpleName() + "." + method.getName());
            }
        }
    }

    private Object injectObjectFromForm(Class<?> objectType, String paramName, Map<String, String[]> requestParams) {
        try {
            // Créer une instance de l'objet
            Object instance = objectType.getDeclaredConstructor().newInstance();
            
            // Préfixe à rechercher (nom du paramètre + ".")
            String prefix = paramName + ".";
            
            // Collecter les champs simples et les champs imbriqués
            Map<String, String> simpleFields = new HashMap<>();
            Map<String, Map<String, String[]>> nestedFields = new HashMap<>();
            
            for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();
                
                if (key.startsWith(prefix) && values != null && values.length > 0) {
                    String fieldPath = key.substring(prefix.length());
                    
                    // Vérifier si c'est un champ imbriqué (contient un point ou des crochets)
                    if (fieldPath.contains(".") || fieldPath.contains("[")) {
                        // Extraire le nom du champ parent
                        String parentField;
                        String remainingPath;
                        
                        if (fieldPath.contains("[")) {
                            int bracketIndex = fieldPath.indexOf('[');
                            parentField = fieldPath.substring(0, bracketIndex);
                            remainingPath = fieldPath.substring(bracketIndex);
                        } else {
                            int dotIndex = fieldPath.indexOf('.');
                            parentField = fieldPath.substring(0, dotIndex);
                            remainingPath = fieldPath.substring(dotIndex + 1);
                        }
                        
                        nestedFields.computeIfAbsent(parentField, k -> new HashMap<>())
                                    .put(remainingPath, values);
                    } else {
                        // Champ simple
                        simpleFields.put(fieldPath, values[0]);
                    }
                }
            }
            
            // Injecter les champs simples
            for (Map.Entry<String, String> fieldEntry : simpleFields.entrySet()) {
                String fieldName = fieldEntry.getKey();
                String value = fieldEntry.getValue();
                
                String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                try {
                    java.lang.reflect.Method setter = objectType.getMethod(setterName, String.class);
                    setter.invoke(instance, value);
                } catch (NoSuchMethodException e) {
                    try {
                        java.lang.reflect.Method setter = objectType.getMethod(setterName, int.class);
                        setter.invoke(instance, Integer.parseInt(value));
                    } catch (Exception e2) {
                        try {
                            java.lang.reflect.Method setter = objectType.getMethod(setterName, double.class);
                            setter.invoke(instance, Double.parseDouble(value));
                        } catch (Exception e3) {
                            // Champ ignoré
                        }
                    }
                }
            }
            
            // Injecter les champs imbriqués (objets ou listes)
            for (Map.Entry<String, Map<String, String[]>> nestedEntry : nestedFields.entrySet()) {
                String fieldName = nestedEntry.getKey();
                Map<String, String[]> nestedParams = nestedEntry.getValue();
                
                // Trouver le getter pour déterminer le type du champ
                String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                try {
                    java.lang.reflect.Method getter = objectType.getMethod(getterName);
                    Class<?> fieldType = getter.getReturnType();
                    Type genericType = getter.getGenericReturnType();
                    
                    if (List.class.isAssignableFrom(fieldType)) {
                        // C'est une liste - extraire le type d'élément
                        if (genericType instanceof ParameterizedType) {
                            ParameterizedType paramType = (ParameterizedType) genericType;
                            Type[] typeArgs = paramType.getActualTypeArguments();
                            if (typeArgs.length == 1 && typeArgs[0] instanceof Class) {
                                Class<?> elementType = (Class<?>) typeArgs[0];
                                
                                // Reconstruire les paramètres pour injectListFromForm
                                Map<String, String[]> listParams = new HashMap<>();
                                for (Map.Entry<String, String[]> np : nestedParams.entrySet()) {
                                    listParams.put(fieldName + np.getKey(), np.getValue());
                                }
                                
                                List<Object> nestedList = injectListFromForm(elementType, fieldName, listParams);
                                if (nestedList != null) {
                                    String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                                    java.lang.reflect.Method setter = objectType.getMethod(setterName, List.class);
                                    setter.invoke(instance, nestedList);
                                }
                            }
                        }
                    } else if (!fieldType.isPrimitive() && fieldType != String.class) {
                        // C'est un objet imbriqué
                        Map<String, String[]> nestedObjParams = new HashMap<>();
                        for (Map.Entry<String, String[]> np : nestedParams.entrySet()) {
                            nestedObjParams.put(fieldName + "." + np.getKey(), np.getValue());
                        }
                        Object nestedObj = injectObjectFromForm(fieldType, fieldName, nestedObjParams);
                        if (nestedObj != null) {
                            String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                            java.lang.reflect.Method setter = objectType.getMethod(setterName, fieldType);
                            setter.invoke(instance, nestedObj);
                        }
                    }
                } catch (NoSuchMethodException e) {
                    // Getter non trouvé, ignorer
                }
            }
            
            System.out.println("Injection terminée");
            return instance;
            
        } catch (Exception e) {
            System.out.println("Erreur lors de l'injection: " + e.getMessage());
            e.printStackTrace();
            // Si l'injection échoue, retourner null
            return null;
        }
    }

    /**
     * Injecte une liste d'objets depuis les paramètres du formulaire
     * Format attendu: paramName[0].field, paramName[1].field, etc.
     * Supporte aussi les objets imbriqués: paramName[0].nestedObj.field ou paramName[0].nestedList[0].field
     */
    private List<Object> injectListFromForm(Class<?> elementType, String paramName, Map<String, String[]> requestParams) {
        try {
            // Trouver tous les indices utilisés dans le formulaire
            Map<Integer, Map<String, String[]>> indexedParams = new HashMap<>();
            
            // Pattern: paramName[index].field (peut contenir des sous-champs)
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                java.util.regex.Pattern.quote(paramName) + "\\[(\\d+)\\]\\.(.+)"
            );
            
            for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();
                
                java.util.regex.Matcher matcher = pattern.matcher(key);
                if (matcher.matches() && values != null && values.length > 0) {
                    int index = Integer.parseInt(matcher.group(1));
                    String fieldPath = matcher.group(2);
                    
                    indexedParams.computeIfAbsent(index, k -> new HashMap<>())
                                 .put(fieldPath, values);
                }
            }
            
            if (indexedParams.isEmpty()) {
                return null;
            }
            
            // Créer les objets pour chaque index
            List<Object> result = new ArrayList<>();
            int maxIndex = indexedParams.keySet().stream().max(Integer::compare).orElse(-1);
            
            for (int i = 0; i <= maxIndex; i++) {
                Map<String, String[]> fields = indexedParams.get(i);
                if (fields != null && !fields.isEmpty()) {
                    // Reconstruire les paramètres pour injectObjectFromForm
                    Map<String, String[]> objParams = new HashMap<>();
                    String objPrefix = "obj";
                    for (Map.Entry<String, String[]> fieldEntry : fields.entrySet()) {
                        objParams.put(objPrefix + "." + fieldEntry.getKey(), fieldEntry.getValue());
                    }
                    
                    Object instance = injectObjectFromForm(elementType, objPrefix, objParams);
                    if (instance != null) {
                        result.add(instance);
                    }
                }
            }
            
            System.out.println("Injection de liste terminée: " + result.size() + " éléments");
            return result;
            
        } catch (Exception e) {
            System.out.println("Erreur lors de l'injection de liste: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convertit un objet en JSON pour les contrôleurs annotés @Api
     */
    private String convertToJson(Object resultValue, Class<?> returnType) {
        try {
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"status\":\"success\",");
            json.append("\"code\":200,");
            json.append("\"data\":");
            
            if (resultValue == null) {
                json.append("null");
            } else if (resultValue instanceof String) {
                json.append(objectToJson(resultValue));
            } else {
                // Pour les objets personnalisés (Employe, etc.)
                json.append(objectToJson(resultValue));
            }
            
            json.append("}");
            return json.toString();
        } catch (Exception e) {
            return createErrorJson("Erreur lors de la conversion JSON: " + e.getMessage());
        }
    }

    /**
     * Crée un message d'erreur au format JSON
     */
    private String createErrorJson(String errorMessage) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"status\":\"error\",");
        json.append("\"code\":500,");
        json.append("\"message\":").append(escapeJson(errorMessage));
        json.append("}");
        return json.toString();
    }

    /**
     * Convertit un objet en JSON de manière récursive
     */
    private String objectToJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        
        if (obj instanceof String) {
            return escapeJson((String) obj);
        }
        
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        
        if (obj instanceof Collection) {
            StringBuilder json = new StringBuilder("[");
            Collection<?> collection = (Collection<?>) obj;
            boolean first = true;
            for (Object item : collection) {
                if (!first) json.append(",");
                json.append(objectToJson(item));
                first = false;
            }
            json.append("]");
            return json.toString();
        }
        
        if (obj instanceof Map) {
            StringBuilder json = new StringBuilder("{");
            Map<?, ?> map = (Map<?, ?>) obj;
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) json.append(",");
                json.append(escapeJson(entry.getKey().toString())).append(":");
                json.append(objectToJson(entry.getValue()));
                first = false;
            }
            json.append("}");
            return json.toString();
        }
        
        // Pour les objets personnalisés, utiliser la réflexion
        try {
            StringBuilder json = new StringBuilder("{");
            Class<?> clazz = obj.getClass();
            java.lang.reflect.Method[] methods = clazz.getMethods();
            boolean first = true;
            
            for (java.lang.reflect.Method method : methods) {
                if (method.getName().startsWith("get") && 
                    method.getParameterCount() == 0 && 
                    !method.getName().equals("getClass")) {
                    
                    String fieldName = method.getName().substring(3);
                    fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
                    
                    Object value = method.invoke(obj);
                    
                    if (!first) json.append(",");
                    json.append(escapeJson(fieldName)).append(":");
                    json.append(objectToJson(value));
                    first = false;
                }
            }
            
            json.append("}");
            return json.toString();
        } catch (Exception e) {
            return escapeJson(obj.toString());
        }
    }

    /**
     * Échappe une chaîne pour le format JSON
     */
    private String escapeJson(String str) {
        if (str == null) {
            return "null";
        }
        
        StringBuilder escaped = new StringBuilder("\"");
        for (char c : str.toCharArray()) {
            switch (c) {
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '\b':
                    escaped.append("\\b");
                    break;
                case '\f':
                    escaped.append("\\f");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    if (c < ' ') {
                        escaped.append(String.format("\\u%04x", (int) c));
                    } else {
                        escaped.append(c);
                    }
            }
        }
        escaped.append("\"");
        return escaped.toString();
    }
}