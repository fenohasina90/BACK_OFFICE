<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="main.java.com.entity.Voyage" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Voyages (V2) - BackOffice</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/admin-style.css">
</head>
<body>
<div class="app-container">
    <%@ include file="/WEB-INF/sidebar.jsp" %>

    <main class="main-content">
        <div class="page-header">
            <h1 class="page-title">Voyages (V2)</h1>
            <p class="page-subtitle">Liste des voyages créés par la planification V2</p>
        </div>

        <div class="page-content">
            <% 
                String error = (String) request.getAttribute("error");
                Object dateDebutObj = request.getAttribute("dateDebut");
                Object dateFinObj = request.getAttribute("dateFin");
                List<Voyage> voyages = (List<Voyage>) request.getAttribute("voyages");
                Map<Integer, Integer> voyageCountsByVoiture = (Map<Integer, Integer>) request.getAttribute("voyageCountsByVoiture");
            %>

            <% if (error != null) { %>
                <div class="alert alert-error">
                    <div class="alert-icon">✕</div>
                    <div class="alert-content">
                        <h3 class="alert-title">Erreur</h3>
                        <p class="alert-message"><%= error %></p>
                    </div>
                </div>
            <% } %>

            <div class="card">
                <div class="card-header">
                    <h2>Filtrer par période</h2>
                </div>
                <div class="card-body">
                    <form action="<%= request.getContextPath() %>/planification/voyages" method="get" class="form">
                        <div class="form-row">
                            <div class="form-group">
                                <label for="dateDebut" class="form-label required">Date début</label>
                                <input type="date" id="dateDebut" name="dateDebut" class="form-control" value="<%= dateDebutObj != null ? dateDebutObj.toString() : "" %>" required>
                            </div>
                            <div class="form-group">
                                <label for="dateFin" class="form-label required">Date fin</label>
                                <input type="date" id="dateFin" name="dateFin" class="form-control" value="<%= dateFinObj != null ? dateFinObj.toString() : "" %>" required>
                            </div>
                            <div class="form-group" style="align-self: flex-end;">
                                <button type="submit" class="btn btn-primary">Afficher</button>
                                <a href="<%= request.getContextPath() %>/planification/" class="btn btn-secondary">Planifier</a>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <div class="card" style="margin-top: 1.5rem;">
                <div class="card-header">
                    <h2>Résultats</h2>
                </div>
                <div class="card-body">
                    <% if (voyages != null && !voyages.isEmpty()) { %>
                        <div class="table-container">
                            <table class="data-table">
                                <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Date</th>
                                    <th>Heure départ</th>
                                    <th>Voiture</th>
                                    <th>Nb voyages (période)</th>
                                    <th>Durée (min)</th>
                                    <th>Actions</th>
                                </tr>
                                </thead>
                                <tbody>
                                <% for (Voyage v : voyages) { %>
                                    <tr>
                                        <td><strong>#<%= v.getId() %></strong></td>
                                        <td><%= v.getDateVoyage() != null ? v.getDateVoyage().toString() : "" %></td>
                                        <td><%= v.getHeureDepart() != null ? v.getHeureDepart().toString().substring(0,5) : "" %></td>
                                        <td>#<%= v.getIdVoiture() %></td>
                                        <td><%= voyageCountsByVoiture != null ? voyageCountsByVoiture.getOrDefault(v.getIdVoiture(), 0) : 0 %></td>
                                        <td><%= v.getDureeMinutes() %></td>
                                        <td>
                                            <a class="btn btn-secondary" href="<%= request.getContextPath() %>/planification/voyage?id=<%= v.getId() %>">Détails</a>
                                        </td>
                                    </tr>
                                <% } %>
                                </tbody>
                            </table>
                        </div>
                    <% } else { %>
                        <div class="empty-state">
                            <div class="empty-state-icon">📭</div>
                            <h3>Aucun voyage</h3>
                            <p>Lance une planification V2 pour générer des voyages.</p>
                        </div>
                    <% } %>
                </div>
            </div>
        </div>
    </main>
</div>
</body>
</html>
