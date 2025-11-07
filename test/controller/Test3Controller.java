package controller;

import annotation.*;

public class Test3Controller {

    @Url("/products")
    public String products() {
        return "Liste des produits - Test3Controller (NE SERA PAS MAPPÉ)";
    }

    @Url("/services")
    public String services() {
        return "Nos services - Test3Controller (NE SERA PAS MAPPÉ)";
    }

    public String otherMethod() {
        return "Autre méthode";
    }
}