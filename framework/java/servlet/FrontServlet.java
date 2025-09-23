package servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.RequestDispatcher;

public class FrontServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // On pointe vers la vue JSP
        RequestDispatcher dispatcher = req.getRequestDispatcher("/views/index.jsp");
        
        // On transfère la requête/réponse vers la JSP
        dispatcher.forward(req, resp);
    }
}
