<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.sql.Time" %>
<%@ page import="java.sql.Date" %>
<%@ page import="java.sql.Timestamp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Réservations Assignées - BackOffice</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/admin-style.css">
</head>
<body>
    <div class="app-container">
        <%@ include file="/WEB-INF/sidebar.jsp" %>

        <main class="main-content">
            <div class="page-header">
                <h1 class="page-title">Réservations assignées</h1>
                <p class="page-subtitle">Liste des réservations planifiées avec la voiture assignée</p>
            </div>

            <div class="page-content">
                <% 
                    String error = (String) request.getAttribute("error");
                    Object dateDebutObj = request.getAttribute("dateDebut");
                    Object dateFinObj = request.getAttribute("dateFin");
                    List<Map<String, Object>> rows = (List<Map<String, Object>>) request.getAttribute("rows");
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
                        <form action="<%= request.getContextPath() %>/planification/liste" method="get" class="form">
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
                        <% 
                            List<Map<String, Object>> creneauRows = (List<Map<String, Object>>) request.getAttribute("creneauRows");
                        %>

                        <% if (creneauRows != null && !creneauRows.isEmpty()) { %>
                            <div class="table-container">
                                <table class="data-table">
                                    <thead>
                                        <tr>
                                            <th>Voiture</th>
                                            <th>Capacité</th>
                                            <th>Carburant</th>
                                            <th>Planification</th>
                                            <th>Hôtel</th>
                                            <th>Réservation</th>
                                            <th>Créneau</th>
                                            <th>Heure départ</th>
                                            <th>Durée (min)</th>
                                            <th>Heure arrivée aéroport</th>
                                            <th>Non assigné</th>
                                            <th>Détails</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (Map<String, Object> r : creneauRows) {
                                            Integer voitureId = (Integer) r.get("voitureId");
                                            Object nbPlace = r.get("nbPlace");
                                            Object typeCarburant = r.get("typeCarburant");
                                            Object planificationLabel = r.get("planificationLabel");
                                            Object hotelsLabel = r.get("hotelsLabel");
                                            Object reservationsLabel = r.get("reservationsLabel");
                                            Object creneau = r.get("creneau");
                                            Object heureDepart = r.get("heureDepart");
                                            Object dureeMinutes = r.get("dureeMinutes");
                                            Object heureRetour = r.get("heureRetour");
                                            Object nonAssignesLabel = r.get("nonAssignesLabel");
                                            Integer voyageId = (Integer) r.get("voyageId");
                                        %>
                                            <tr>
                                                <td>
                                                    <%= voitureId != null ? ("#" + voitureId) : "" %>
                                                </td>
                                                <td><%= nbPlace != null ? nbPlace.toString() : "" %></td>
                                                <td><%= typeCarburant != null ? typeCarburant.toString() : "" %></td>
                                                <td><%= planificationLabel != null ? planificationLabel.toString() : "" %></td>
                                                <td><%= hotelsLabel != null ? hotelsLabel.toString() : "" %></td>
                                                <td><%= reservationsLabel != null ? reservationsLabel.toString() : "" %></td>
                                                <td><%= creneau != null ? creneau.toString() : "" %></td>
                                                <td><%= heureDepart != null ? heureDepart.toString() : "" %></td>
                                                <td><%= dureeMinutes != null ? dureeMinutes.toString() : "" %></td>
                                                <td><%= heureRetour != null ? heureRetour.toString() : "" %></td>
                                                <td><%= nonAssignesLabel != null ? nonAssignesLabel.toString() : "" %></td>
                                                <td>
                                                    <% if (voyageId != null) { %>
                                                        <a class="btn btn-sm btn-secondary" href="<%= request.getContextPath() %>/planification/voyage?id=<%= voyageId %>">Voyage</a>
                                                    <% } %>
                                                    <% if (voitureId != null) { %>
                                                        <a class="btn btn-sm btn-secondary" href="<%= request.getContextPath() %>/voiture/edit/<%= voitureId %>">Voiture</a>
                                                    <% } %>
                                                </td>
                                            </tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>
                        <% } else { %>
                            <div class="empty-state">
                                <div class="empty-state-icon">📭</div>
                                <h3>Aucune planification</h3>
                                <p>Choisis une date et lance une planification pour voir les résultats.</p>
                            </div>
                        <% } %>
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
