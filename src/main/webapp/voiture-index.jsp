<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gestion des Véhicules - BackOffice</title>
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
                <h1 class="page-title">Gestion des Véhicules</h1>
                <p class="page-subtitle">Module de gestion du parc automobile</p>
            </div>
            
            <div class="page-content">
                <div class="welcome-card">
                    <div class="welcome-icon">🚗</div>
                    <h2 class="welcome-title">Parc Automobile</h2>
                    <p class="welcome-text">Gérez vos véhicules avec différents types de carburant</p>
                    <div class="btn-group" style="justify-content: center;">
                        <a href="<%= request.getContextPath() %>/voiture/liste" class="btn btn-primary">📋 Voir la liste</a>
                        <a href="<%= request.getContextPath() %>/voiture/formulaire" class="btn btn-success">➕ Ajouter un véhicule</a>
                        <a href="<%= request.getContextPath() %>/" class="btn btn-outline">🏠 Retour au tableau de bord</a>
                    </div>
                </div>
                
                <div class="grid grid-4">
                    <div class="stat-card">
                        <div class="stat-label">Essence</div>
                        <div class="stat-value" style="color: #92400e;">⛽</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-label">Diesel</div>
                        <div class="stat-value" style="color: #1e40af;">⛽</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-label">Électrique</div>
                        <div class="stat-value" style="color: #065f46;">🔋</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-label">Hybride</div>
                        <div class="stat-value" style="color: #9a3412;">🔌</div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
