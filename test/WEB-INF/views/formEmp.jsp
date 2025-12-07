<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulaire Employé</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }

        .form-container {
            background: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
            width: 100%;
            max-width: 500px;
        }

        h1 {
            color: #333;
            margin-bottom: 30px;
            text-align: center;
            font-size: 28px;
        }

        .form-group {
            margin-bottom: 25px;
        }

        label {
            display: block;
            color: #555;
            font-weight: 600;
            margin-bottom: 8px;
            font-size: 14px;
        }

        input[type="text"],
        input[type="number"],
        input[type="email"],
        input[type="tel"],
        input[type="date"],
        select,
        textarea {
            width: 100%;
            padding: 12px 15px;
            border: 2px solid #e0e0e0;
            border-radius: 5px;
            font-size: 14px;
            transition: border-color 0.3s ease;
        }

        input[type="text"]:focus,
        input[type="number"]:focus,
        input[type="email"]:focus,
        input[type="tel"]:focus,
        input[type="date"]:focus,
        select:focus,
        textarea:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }

        textarea {
            resize: vertical;
            min-height: 80px;
        }

        .radio-group {
            display: flex;
            gap: 20px;
            margin-top: 10px;
        }

        .radio-option {
            display: flex;
            align-items: center;
            gap: 8px;
            cursor: pointer;
        }

        input[type="radio"] {
            width: 18px;
            height: 18px;
            cursor: pointer;
            accent-color: #667eea;
        }

        .radio-option label {
            margin: 0;
            font-weight: 500;
            color: #666;
            cursor: pointer;
        }

        button {
            width: 100%;
            padding: 12px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
            margin-top: 10px;
        }

        button:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
        }

        button:active {
            transform: translateY(0);
        }

        .info {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border-left: 4px solid #667eea;
        }

        .info p {
            margin: 5px 0;
            font-size: 14px;
            color: #666;
        }

        .info strong {
            color: #333;
        }
    </style>
</head>
<body>
    <div class="form-container">
        <h1>Formulaire Employé</h1>


        <form method="post" action="${pageContext.request.contextPath}/form-emp">
            <div class="form-group">
                <label for="nom">Nom de l'employé:</label>
                <input type="text" id="nom" name="e.nom" value="Dupont" required>
            </div>

            <div class="form-group">
                <label>Genre:</label>
                <div class="radio-group">
                    <div class="radio-option">
                        <input type="radio" id="male" name="e.genre" value="Homme" checked required>
                        <label for="male">Homme</label>
                    </div>
                    <div class="radio-option">
                        <input type="radio" id="female" name="e.genre" value="Femme">
                        <label for="female">Femme</label>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <label for="departement">ID Département:</label>
                <input type="number" id="departement" name="e.idDepartement" value="1" required min="1">
            </div>

            <!-- Champs qui seront ignorés par l'injection automatique -->
            <div class="form-group">
                <label for="email">Email (ignoré par Employe):</label>
                <input type="email" id="email" name="email" value="dupont@company.com">
            </div>

            <div class="form-group">
                <label for="telephone">Téléphone (ignoré par Employe):</label>
                <input type="tel" id="telephone" name="telephone" value="0123456789">
            </div>

            <div class="form-group">
                <label for="salaire">Salaire annuel (ignoré par Employe):</label>
                <input type="number" id="salaire" name="salaire" value="45000" min="0" step="1000">
            </div>

            <div class="form-group">
                <label for="dateEmbauche">Date d'embauche (ignoré par Employe):</label>
                <input type="date" id="dateEmbauche" name="dateEmbauche" value="2024-01-15">
            </div>

            <div class="form-group">
                <label for="commentaires">Commentaires (ignoré par Employe):</label>
                <textarea id="commentaires" name="commentaires" rows="3">Nouveau employé à former</textarea>
            </div>

            <button type="submit">Créer Employé</button>
        </form>
    </div>
</body>
</html>
