<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tableau de bord - BackOffice</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/admin-style.css">
</head>
<body>
    <div class="app-container">
        <%@ include file="/WEB-INF/sidebar.jsp" %>
        
        <main class="main-content">
            <div class="page-header">
                <h1 class="page-title">Tableau de bord</h1>
                <p class="page-subtitle">Vue d'ensemble de votre système de gestion</p>
            </div>
            
            <div class="page-content">
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-icon">📋</div>
                        <div class="stat-label">Réservations actives</div>
                        <div class="stat-value">-</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">🚗</div>
                        <div class="stat-label">Véhicules disponibles</div>
                        <div class="stat-value">-</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">👥</div>
                        <div class="stat-label">Clients</div>
                        <div class="stat-value">-</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">🏨</div>
                        <div class="stat-label">Hôtels partenaires</div>
                        <div class="stat-value">-</div>
                    </div>
                </div>
                
                <div class="grid grid-2">
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">Gestion des Réservations</h3>
                        </div>
                        <div class="card-body">
                            <p style="color: var(--text-light); margin-bottom: 20px;">Gérez l'ensemble des réservations d'hôtels de vos clients.</p>
                            <div class="btn-group">
                                <a href="<%= request.getContextPath() %>/reservation/" class="btn btn-primary">📋 Accéder aux réservations</a>
                                <a href="<%= request.getContextPath() %>/reservation/formulaire" class="btn btn-outline">➕ Nouvelle réservation</a>
                            </div>
                        </div>
                    </div>
                    
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">Gestion des Véhicules</h3>
                        </div>
                        <div class="card-body">
                            <p style="color: var(--text-light); margin-bottom: 20px;">Gérez votre parc automobile avec tous les types de carburants.</p>
                            <div class="btn-group">
                                <a href="<%= request.getContextPath() %>/voiture/liste" class="btn btn-primary">🚗 Liste des véhicules</a>
                                <a href="<%= request.getContextPath() %>/voiture/formulaire" class="btn btn-outline">➕ Ajouter un véhicule</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
