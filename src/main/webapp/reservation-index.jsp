<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gestion des Réservations - BackOffice</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/admin-style.css">
    <style>
        .welcome-card {
            background: var(--white);
            border: 1px solid var(--border-color);
            border-radius: 8px;
            padding: 48px 32px;
            text-align: center;
            margin-bottom: 32px;
        }
        .welcome-icon {
            font-size: 64px;
            margin-bottom: 20px;
        }
        .welcome-title {
            font-size: 28px;
            font-weight: 700;
            color: var(--text-dark);
            margin-bottom: 12px;
        }
        .welcome-text {
            color: var(--text-light);
            font-size: 16px;
            margin-bottom: 32px;
        }
    </style>
</head>
<body>
    <div class="app-container">
        <%@ include file="/WEB-INF/sidebar.jsp" %>
        
        <main class="main-content">
            <div class="page-header">
                <h1 class="page-title">Gestion des Réservations</h1>
                <p class="page-subtitle">Module de réservation d'hôtels</p>
            </div>
            
            <div class="page-content">
                <div class="welcome-card">
                    <div class="welcome-icon">🏨</div>
                    <h2 class="welcome-title">Système de Réservation d'Hôtel</h2>
                    <p class="welcome-text">Bienvenue sur notre plateforme de réservation en ligne</p>
                    <div class="btn-group" style="justify-content: center;">
                        <a href="<%= request.getContextPath() %>/reservation/formulaire" class="btn btn-primary">➕ Créer une réservation</a>
                        <a href="<%= request.getContextPath() %>/" class="btn btn-outline">🏠 Retour au tableau de bord</a>
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
