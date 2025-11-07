package controller;

import annotation.*;

@Controller
public class Test2Controller {

    @Url("/contact")
    public String contact() {
        return "Page contact - Test2Controller";
    }

    @Url("/serve")
    public String services() {
        return "Services - Cette méthode n'est pas mappée";
    }

    public String anotherMethod() {
        return "Autre méthode sans annotation";
    }
}