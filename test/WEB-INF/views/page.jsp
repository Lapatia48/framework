<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%
    String titre = (String) request.getAttribute("titre");
    List<String> liste = (List<String>) request.getAttribute("liste");
%>
<html>
<head>
    <title>Page avec données</title>
</head>
<body>
    <h1>Données reçues :</h1>
    
    <h2>Titre: <%= titre %></h2>
        
    <h3>Liste:</h3>
    <ul>
        <% for (String item : liste) { %>
            <li><%= item %></li>
        <% } %>
    </ul>
    
</body>
</html>