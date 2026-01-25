<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login - Framework Demo</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input, select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
        }
        button {
            background-color: #4CAF50;
            color: white;
            padding: 12px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            width: 100%;
        }
        button:hover {
            background-color: #45a049;
        }
        .message {
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 4px;
        }
        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .info {
            background-color: #cce5ff;
            color: #004085;
            border: 1px solid #b8daff;
        }
        .links {
            margin-top: 20px;
            padding: 15px;
            background-color: #f8f9fa;
            border-radius: 4px;
        }
        .links h3 {
            margin-top: 0;
        }
        .links a {
            display: block;
            margin: 5px 0;
            color: #007bff;
        }
        .status {
            margin-top: 20px;
            padding: 15px;
            background-color: #e9ecef;
            border-radius: 4px;
        }
    </style>
</head>
<body>
    <h1> Connexion</h1>
    
    <% if (request.getAttribute("message") != null) { %>
        <div class="message success"><%= request.getAttribute("message") %></div>
    <% } %>
    
    <% if (request.getAttribute("error") != null) { %>
        <div class="message error"><%= request.getAttribute("error") %></div>
    <% } %>
    
    <% 
        Boolean isLoggedIn = (Boolean) request.getAttribute("isLoggedIn");
        String userRole = (String) request.getAttribute("userRole");
    %>
    
    <% if (isLoggedIn != null && isLoggedIn) { %>
        <div class="message info">
            <strong>Vous êtes connecté!</strong><br>
            Rôle: <%= userRole != null ? userRole : "Aucun rôle spécifique" %>
        </div>
        <a href="logout"><button type="button">Se déconnecter</button></a>
    <% } else { %>
        <form action="login" method="post">
            <div class="form-group">
                <label for="username">Nom d'utilisateur:</label>
                <input type="text" id="username" name="username" required placeholder="Entrez votre nom d'utilisateur">
            </div>
            
            <div class="form-group">
                <label for="password">Mot de passe:</label>
                <input type="password" id="password" name="password" required placeholder="Entrez votre mot de passe">
            </div>
            
            <div class="form-group">
                <label for="role">Rôle (pour démo):</label>
                <select id="role" name="role">
                    <option value="">Aucun rôle spécifique</option>
                    <option value="user">Utilisateur</option>
                    <option value="manager">Manager</option>
                    <option value="admin">Admin</option>
                </select>
            </div>
            
            <button type="submit">Se connecter</button>
        </form>
    <% } %>
    
    <div class="links">
        <h3> Test</h3>
        <p><strong>Pages publiques (sans @Role):</strong></p>
        <a href="home"> Page d'accueil publique</a>
        <a href="status"> Vérifier statut session</a>
        
        <p><strong>Pages protégées (@Role - authentification requise):</strong></p>
        <a href="profile"> Mon profil</a>
        <a href="dashboard"> Tableau de bord</a>
        
        <p><strong>Pages avec rôle spécifique:</strong></p>
        <a href="admin"> Page Admin (@Role("admin"))</a>
        <a href="manager"> Page Manager (@Role("manager"))</a>
        <a href="reports"> Rapports (@Role({"admin", "manager"}))</a>
    </div>
    
</body>
</html>
