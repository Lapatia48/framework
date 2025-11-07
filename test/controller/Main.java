package controller;

import annotation.UrlHandler;

public class Main {
    public static void main(String[] args) {
        try {
            UrlHandler urlHandler = new UrlHandler();
            
            // Tester la nouvelle méthode universelle
            System.out.println("=== TEST SCAN UNIVERSEL ===");
            urlHandler.scanControllers("controller");
            
            // Afficher les mappings
            urlHandler.printAllMappings();
            
            // Tester les URLs
            System.out.println("\n=== TESTS DES URLs ===");
            testUrl(urlHandler, "/about");
            testUrl(urlHandler, "/contact");
            testUrl(urlHandler, "/services");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void testUrl(UrlHandler handler, String url) {
        String result = handler.handleUrl(url);
        if (result != null) {
            System.out.println(url + " -> " + result);
        } else {
            System.out.println(url + " -> NON TROUVÉ");
        }
    }
}