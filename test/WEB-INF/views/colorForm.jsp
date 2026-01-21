<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String favoriteColor = (String) request.getAttribute("favoriteColor");
    String message = (String) request.getAttribute("message");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gestion de Couleur Préférée - Session</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            text-align: center;
        }
        .message {
            padding: 10px;
            margin: 15px 0;
            border-radius: 5px;
            text-align: center;
            font-weight: bold;
        }
        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .info {
            background-color: #d1ecf1;
            color: #0c5460;
            border: 1px solid #bee5eb;
        }
        .color-display {
            padding: 30px;
            margin: 20px 0;
            border-radius: 5px;
            text-align: center;
            font-size: 18px;
            font-weight: bold;
            border: 2px solid #ddd;
        }
        form {
            margin: 20px 0;
        }
        label {
            display: block;
            margin-bottom: 8px;
            color: #555;
            font-weight: bold;
        }
        input[type="text"] {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 16px;
            box-sizing: border-box;
        }
        button {
            padding: 12px 30px;
            margin: 10px 5px;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        .btn-primary {
            background-color: #007bff;
            color: white;
        }
        .btn-primary:hover {
            background-color: #0056b3;
        }
        .btn-danger {
            background-color: #dc3545;
            color: white;
        }
        .btn-danger:hover {
            background-color: #c82333;
        }
        .instructions {
            background-color: #fff3cd;
            padding: 15px;
            border-radius: 5px;
            border: 1px solid #ffc107;
            margin-top: 30px;
        }
        .instructions h3 {
            margin-top: 0;
            color: #856404;
        }
        .instructions p {
            margin: 5px 0;
            color: #856404;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🎨 Gestion de Couleur Préférée</h1>
        
        <% if (message != null) { %>
            <div class="message success"><%= message %></div>
        <% } %>
        
        <% if (favoriteColor != null && !favoriteColor.isEmpty()) { %>
            <div class="color-display" style="background-color: <%= favoriteColor %>; color: white; text-shadow: 1px 1px 2px rgba(0,0,0,0.5);">
                Votre couleur préférée est: <%= favoriteColor %>
            </div>
            
            <form action="<%= request.getContextPath() %>/session/delete-color" method="post" style="text-align: center;">
                <button type="submit" class="btn-danger">🗑️ Supprimer la couleur</button>
            </form>
        <% } else { %>
            <div class="message info">Aucune couleur préférée enregistrée</div>
        <% } %>
        
        <form action="<%= request.getContextPath() %>/session/save-color" method="post">
            <label for="color">Choisir une nouvelle couleur préférée:</label>
            <input 
                type="text" 
                id="color" 
                name="color" 
                placeholder="Ex: red, blue, #ff5733, rgb(255, 87, 51)" 
                value="<%= favoriteColor != null ? favoriteColor : "" %>"
                required
            />
            <div style="text-align: center; margin-top: 15px;">
                <button type="submit" class="btn-primary">💾 Enregistrer</button>
            </div>
        </form>
        
        <div class="instructions">
            <h3>📋 Instructions de test:</h3>
            <p>1. Entrez une couleur (ex: red, blue, green, #ff0000)</p>
            <p>2. Ouvrez un autre navigateur (ou mode privé)</p>
            <p>3. Accédez à la même URL dans le 2ème navigateur</p>
            <p>4. Vous devriez voir la même couleur (session partagée)</p>
            <p>5. Supprimez la couleur dans un navigateur</p>
            <p>6. Rafraîchissez l'autre navigateur pour voir la suppression</p>
        </div>
    </div>
</body>
</html>
