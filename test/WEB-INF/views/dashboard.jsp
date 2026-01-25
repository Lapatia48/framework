<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard - Framework Demo</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
        }
        .welcome-box {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            border-radius: 10px;
            margin-bottom: 20px;
        }
        .welcome-box h1 {
            margin: 0 0 10px 0;
        }
        .info-card {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 15px;
            border-left: 4px solid #667eea;
        }
        .nav-links {
            margin-top: 20px;
        }
        .nav-links a {
            display: inline-block;
            margin-right: 15px;
            padding: 10px 20px;
            background-color: #667eea;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }
        .nav-links a:hover {
            background-color: #5a6fd6;
        }
        .nav-links a.logout {
            background-color: #dc3545;
        }
        .nav-links a.logout:hover {
            background-color: #c82333;
        }
    </style>
</head>
<body>
    <div class="welcome-box">
        <h1> <%= request.getAttribute("message") %></h1>
        <p>Utilisateur: <strong><%= request.getAttribute("username") %></strong></p>
        <p>Rôle: <strong><%= request.getAttribute("role") != null ? request.getAttribute("role") : "Aucun" %></strong></p>
    </div>
    

    
    <div class="nav-links">
        <a href="login"> Page de connexion</a>
        <a href="profile"> Mon profil</a>
        <a href="status"> Statut session</a>
        <a href="logout" class="logout"> Déconnexion</a>
    </div>
</body>
</html>
