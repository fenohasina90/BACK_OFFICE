<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="main.java.com.service.PlanificationService" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Planification - BackOffice</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/admin-style.css">
</head>
<body>
    <div class="app-container">
        <%@ include file="/WEB-INF/sidebar.jsp" %>

        <main class="main-content">
            <div class="page-header">
                <h1 class="page-title">Planification</h1>
                <p class="page-subtitle">Assigner automatiquement une voiture aux réservations pour une date</p>
            </div>

            <div class="page-content">
                <div class="card">
                    <div class="card-header">
                        <h2>Lancer la planification</h2>
                    </div>
                    <div class="card-body">
                        <form action="<%= request.getContextPath() %>/planification/run" method="post" class="form">
                            <div class="form-group">
                                <label for="date" class="form-label required">Date</label>
                                <input type="date" id="date" name="date" class="form-control" required>
                                <small class="form-help">Toutes les réservations de cette date seront traitées</small>
                            </div>

                            <div class="form-actions">
                                <button type="submit" class="btn btn-primary">Planifier</button>
                                <a href="<%= request.getContextPath() %>/" class="btn btn-secondary">Retour</a>
                            </div>
                        </form>
                    </div>
                </div>

                <% 
                    Boolean success = (Boolean) request.getAttribute("success");
                    String error = (String) request.getAttribute("error");
                    PlanificationService.PlanificationResult result = (PlanificationService.PlanificationResult) request.getAttribute("result");
                %>

                <% if (error != null) { %>
                    <div class="alert alert-error" style="margin-top: 1.5rem;">
                        <div class="alert-icon">✕</div>
                        <div class="alert-content">
                            <h3 class="alert-title">Erreur</h3>
                            <p class="alert-message"><%= error %></p>
                        </div>
                    </div>
                <% } %>

                <% if (success != null && success && result != null) { %>
                    <div class="alert alert-success" style="margin-top: 1.5rem;">
                        <div class="alert-icon">✓</div>
                        <div class="alert-content">
                            <h3 class="alert-title">Planification terminée</h3>
                            <p class="alert-message">
                                Date: <strong><%= result.getDate() %></strong><br>
                                Réservations traitées: <strong><%= result.getReservationsTraitees() %></strong><br>
                                Clients traités: <strong><%= result.getClientsTraites() %></strong><br>
                                Assignations créées: <strong><%= result.getAssignmentsCrees() %></strong>
                            </p>
                        </div>
                    </div>

                    <% if (result.getWarnings() != null && !result.getWarnings().isEmpty()) { %>
                        <div class="alert alert-warning" style="margin-top: 1rem;">
                            <div class="alert-icon">⚠</div>
                            <div class="alert-content">
                                <h3 class="alert-title">Avertissements</h3>
                                <p class="alert-message">
                                    <% for (String w : result.getWarnings()) { %>
                                        <%= w %><br>
                                    <% } %>
                                </p>
                            </div>
                        </div>
                    <% } %>

                    <div class="result-actions" style="margin-top: 1.5rem;">
                        <a class="btn btn-primary" href="<%= request.getContextPath() %>/planification/liste?date=<%= result.getDate() %>">
                            Voir les réservations assignées
                        </a>
                    </div>
                <% } %>
            </div>
        </main>
    </div>
</body>
</html>
