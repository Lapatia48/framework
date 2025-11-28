package controller;

import annotation.*;

@Controller
public class Controllermethod {

    @Url("/user")
    @Get
    public String getUser(Integer id) {
        return "GET User";
    }

    @Url("/user")
    @Post  
    public String createUser() {
        return "POST User";
    }

   
}