<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Upload Fichier</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        .card {
            background: #fff;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
            width: 100%;
            max-width: 600px;
        }
        h1 { color: #333; margin-bottom: 20px; text-align: center; font-size: 26px; }
        .info {
            background: #e8f4fd;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border-left: 4px solid #2196F3;
        }
        .info p { margin: 6px 0; font-size: 14px; color: #555; }
        label { display: block; color: #555; font-weight: 600; margin: 10px 0 8px 0; font-size: 14px; }
        input[type="text"], input[type="file"] {
            width: 100%;
            padding: 12px 15px;
            border: 2px solid #e0e0e0;
            border-radius: 5px;
            font-size: 14px;
        }
        input:focus { outline: none; border-color: #667eea; box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1); }
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
            margin-top: 18px;
        }
        button:hover { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4); }
    </style>
</head>
<body>
    <div class="card">
        <h1>Upload Fichier</h1>

        <div class="info">
            <p><strong>Choisir un fichier</strong> via le bouton “upload file”.</p>
            <p>Le serveur le sauvegarde dans le dossier configuré (par défaut: <code>C:/telechargement</code>).</p>
        </div>

        <form method="post" action="${pageContext.request.contextPath}/fichier-attach" enctype="multipart/form-data">
            <label for="description">Description (optionnel)</label>
            <input type="text" id="description" name="description" value="Mon fichier" />

            <label for="file">Upload file</label>
            <input type="file" id="file" name="file" required />

            <button type="submit">Save</button>
        </form>
    </div>
</body>
</html>
