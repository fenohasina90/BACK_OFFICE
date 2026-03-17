<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.sql.Date" %>
<%@ page import="java.sql.Time" %>
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
                        <% if (rows != null && !rows.isEmpty()) { %>
                            <% 
                                Map<Integer, Integer> voyageCounts = new HashMap<>();
                                for (Map<String, Object> r : rows) {
                                    Integer vid = (Integer) r.get("voyageId");
                                    if (vid != null) {
                                        Integer c = voyageCounts.get(vid);
                                        voyageCounts.put(vid, c == null ? 1 : (c + 1));
                                    }
                                }
                            %>
                            <div class="table-container">
                                <table class="data-table">
                                    <thead>
                                        <tr>
                                            <th>Réservation</th>
                                            <th>Date</th>
                                            <th>Heure</th>
                                            <th>Client</th>
                                            <th>Hôtel</th>
                                            <th>Personnes</th>
                                            <th>Voyage</th>
                                            <th>Groupé</th>
                                            <th>Voiture</th>
                                            <th>Carburant</th>
                                            <th>Places</th>
                                            <th>Planifiée le</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (Map<String, Object> r : rows) { 
                                            Integer reservationId = (Integer) r.get("reservationId");
                                            Date dateReservation = (Date) r.get("dateReservation");
                                            Time heureReservation = (Time) r.get("heureReservation");
                                            String idClient = (String) r.get("idClient");
                                            String hotelNom = (String) r.get("hotelNom");
                                            Integer nbPersonnes = (Integer) r.get("nbPersonnes");
                                            Integer voyageId = (Integer) r.get("voyageId");
                                            Integer voyageCount = voyageId != null ? voyageCounts.get(voyageId) : null;
                                            Integer voitureId = (Integer) r.get("voitureId");
                                            String typeCarburant = (String) r.get("typeCarburant");
                                            Integer nbPlace = (Integer) r.get("nbPlace");
                                            Timestamp datePlanification = (Timestamp) r.get("datePlanification");

                                            String badgeClass = "badge-info";
                                            if (typeCarburant != null) {
                                                if ("D".equalsIgnoreCase(typeCarburant)) badgeClass = "badge-diesel";
                                                else if ("H".equalsIgnoreCase(typeCarburant)) badgeClass = "badge-hybride";
                                                else if ("El".equalsIgnoreCase(typeCarburant)) badgeClass = "badge-electrique";
                                                else if ("E".equalsIgnoreCase(typeCarburant)) badgeClass = "badge-essence";
                                            }
                                        %>
                                            <tr>
                                                <td><strong>#<%= reservationId %></strong></td>
                                                <td><%= dateReservation != null ? dateReservation.toString() : "" %></td>
                                                <td><%= heureReservation != null ? heureReservation.toString().substring(0,5) : "" %></td>
                                                <td><%= idClient %></td>
                                                <td><%= hotelNom %></td>
                                                <td><%= nbPersonnes %></td>
                                                <td>
                                                    <% if (voyageId != null) { %>
                                                        <a href="<%= request.getContextPath() %>/planification/voyage?id=<%= voyageId %>"><strong>#<%= voyageId %></strong></a>
                                                    <% } else { %>
                                                        -
                                                    <% } %>
                                                </td>
                                                <td>
                                                    <% if (voyageId != null && voyageCount != null && voyageCount > 1) { %>
                                                        <span class="badge badge-info">Oui (<%= voyageCount %>)</span>
                                                    <% } else if (voyageId != null) { %>
                                                        <span class="badge badge-info">Non</span>
                                                    <% } else { %>
                                                        -
                                                    <% } %>
                                                </td>
                                                <td>#<%= voitureId %></td>
                                                <td><span class="badge <%= badgeClass %>"><%= typeCarburant != null ? typeCarburant : "" %></span></td>
                                                <td><%= nbPlace %></td>
                                                <td><%= datePlanification != null ? datePlanification.toString() : "" %></td>
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
