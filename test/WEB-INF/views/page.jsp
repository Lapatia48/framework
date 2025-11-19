<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%
    Map<String, Object> data = (Map<String, Object>) request.getAttribute("data");
    String titre = "";
    List<String> liste = new ArrayList<>();
    
    if (data != null) {
        titre = (String) data.get("titre");
        Object listeObj = data.get("liste");
        if (listeObj instanceof List) {
            liste = (List<String>) listeObj;
        }
    }
%>
<html>
<head>
    <title>Page avec données</title>
</head>
<body>
    <h1>Données reçues :</h1>
    
    <% if (data != null) { %>
        <h2>Titre: <%= titre%></h2>
        
        <h3>Liste:</h3>
        <ul>
            <% for (String item : liste) { %>
                <li><%= item %></li>
            <% } %>
        </ul>
    <% } else { %>
        <p>Aucune donnée reçue</p>
    <% } %>
    
</body>
</html>