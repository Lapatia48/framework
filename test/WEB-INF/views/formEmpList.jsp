<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulaire Liste Employés</title>
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
            max-width: 800px;
        }

        h1 {
            color: #333;
            margin-bottom: 30px;
            text-align: center;
            font-size: 28px;
        }

        h2 {
            color: #667eea;
            margin: 20px 0 15px 0;
            font-size: 20px;
            border-bottom: 2px solid #667eea;
            padding-bottom: 5px;
        }

        h3 {
            color: #764ba2;
            margin: 15px 0 10px 0;
            font-size: 16px;
        }

        .employee-section {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            border-left: 4px solid #667eea;
        }

        .departement-section {
            background: #fff;
            padding: 15px;
            border-radius: 5px;
            margin: 10px 0;
            border: 1px solid #e0e0e0;
        }

        .form-group {
            margin-bottom: 20px;
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
        select {
            width: 100%;
            padding: 12px 15px;
            border: 2px solid #e0e0e0;
            border-radius: 5px;
            font-size: 14px;
            transition: border-color 0.3s ease;
        }

        input[type="text"]:focus,
        input[type="number"]:focus,
        select:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
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
            background: #e8f4fd;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border-left: 4px solid #2196F3;
        }

        .info p {
            margin: 5px 0;
            font-size: 14px;
            color: #666;
        }

        .info code {
            background: #f0f0f0;
            padding: 2px 6px;
            border-radius: 3px;
            font-family: monospace;
        }

        .row {
            display: flex;
            gap: 15px;
        }

        .row .form-group {
            flex: 1;
        }
    </style>
</head>
<body>
    <div class="form-container">
        <h1>Formulaire Liste Employés</h1>

        <div class="info">
            <p><strong>Format des champs:</strong></p>
            <p>Employé: <code>e[index].champ</code> - Ex: <code>e[0].nom</code></p>
            <p>Département: <code>e[index].departements[index].champ</code> - Ex: <code>e[0].departements[0].libelle</code></p>
        </div>

        <form method="post" action="${pageContext.request.contextPath}/form-emp-list">
            
            <!-- Employé 1 -->
            <div class="employee-section">
                <h2>Employé 1</h2>
                
                <div class="form-group">
                    <label for="nom0">Nom de l'employé:</label>
                    <input type="text" id="nom0" name="e[0].nom" value="Dupont" required>
                </div>

                <div class="form-group">
                    <label>Genre:</label>
                    <div class="radio-group">
                        <div class="radio-option">
                            <input type="radio" id="male0" name="e[0].genre" value="Homme" checked required>
                            <label for="male0">Homme</label>
                        </div>
                        <div class="radio-option">
                            <input type="radio" id="female0" name="e[0].genre" value="Femme">
                            <label for="female0">Femme</label>
                        </div>
                    </div>
                </div>

                <!-- Départements de l'employé 1 -->
                <h3>Départements</h3>
                
                <div class="departement-section">
                    <h4>Département 1</h4>
                    <div class="row">
                        <div class="form-group">
                            <label for="dept0_0_id">ID:</label>
                            <input type="number" id="dept0_0_id" name="e[0].departements[0].id" value="1" required min="1">
                        </div>
                        <div class="form-group">
                            <label for="dept0_0_libelle">Libellé:</label>
                            <input type="text" id="dept0_0_libelle" name="e[0].departements[0].libelle" value="Informatique" required>
                        </div>
                    </div>
                </div>

                <div class="departement-section">
                    <h4>Département 2</h4>
                    <div class="row">
                        <div class="form-group">
                            <label for="dept0_1_id">ID:</label>
                            <input type="number" id="dept0_1_id" name="e[0].departements[1].id" value="2" required min="1">
                        </div>
                        <div class="form-group">
                            <label for="dept0_1_libelle">Libellé:</label>
                            <input type="text" id="dept0_1_libelle" name="e[0].departements[1].libelle" value="RH" required>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Employé 2 -->
            <div class="employee-section">
                <h2>Employé 2</h2>
                
                <div class="form-group">
                    <label for="nom1">Nom de l'employé:</label>
                    <input type="text" id="nom1" name="e[1].nom" value="Martin" required>
                </div>

                <div class="form-group">
                    <label>Genre:</label>
                    <div class="radio-group">
                        <div class="radio-option">
                            <input type="radio" id="male1" name="e[1].genre" value="Homme" required>
                            <label for="male1">Homme</label>
                        </div>
                        <div class="radio-option">
                            <input type="radio" id="female1" name="e[1].genre" value="Femme" checked>
                            <label for="female1">Femme</label>
                        </div>
                    </div>
                </div>

                <!-- Départements de l'employé 2 -->
                <h3>Départements</h3>
                
                <div class="departement-section">
                    <h4>Département 1</h4>
                    <div class="row">
                        <div class="form-group">
                            <label for="dept1_0_id">ID:</label>
                            <input type="number" id="dept1_0_id" name="e[1].departements[0].id" value="3" required min="1">
                        </div>
                        <div class="form-group">
                            <label for="dept1_0_libelle">Libellé:</label>
                            <input type="text" id="dept1_0_libelle" name="e[1].departements[0].libelle" value="Marketing" required>
                        </div>
                    </div>
                </div>
            </div>

            <button type="submit">Créer les Employés</button>
        </form>
    </div>
</body>
</html>
