<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="main.java.com.entity.VoyageStop" %>
<%@ page import="main.java.com.entity.Voyage" %>
<%@ page import="main.java.com.service.PlanificationService.VoyageLeg" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Détails Voyage - BackOffice</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/admin-style.css">
</head>
<body>
<div class="app-container">
    <%@ include file="/WEB-INF/sidebar.jsp" %>

    <main class="main-content">
        <div class="page-header">
            <h1 class="page-title">Détails Voyage</h1>
            <p class="page-subtitle">Stops du voyage</p>
        </div>

        <div class="page-content">
            <% 
                String error = (String) request.getAttribute("error");
                Object voyageIdObj = request.getAttribute("voyageId");
                Object distanceTotalKmObj = request.getAttribute("distanceTotalKm");
                Voyage voyage = (Voyage) request.getAttribute("voyage");
                Object nbVoyagesVoitureDateObj = request.getAttribute("nbVoyagesVoitureDate");
                LocalDateTime departAeroport = (LocalDateTime) request.getAttribute("departAeroport");
                List<VoyageStop> stops = (List<VoyageStop>) request.getAttribute("stops");
                Map<Integer, LocalDateTime> arrivalByStopIdDT = (Map<Integer, LocalDateTime>) request.getAttribute("arrivalByStopIdDT");
                LocalDateTime retourAeroport = (LocalDateTime) request.getAttribute("retourAeroport");
                List<VoyageLeg> legs = (List<VoyageLeg>) request.getAttribute("legs");
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
                    <h2>Voyage #<%= voyageIdObj != null ? voyageIdObj.toString() : "" %></h2>
                </div>
                <div class="card-body">
                    <p style="margin-bottom: 1rem;">
                        Distance totale: <strong><%= distanceTotalKmObj != null ? distanceTotalKmObj.toString() : "0" %> km</strong>
                    </p>
                    <p style="margin-bottom: 1rem;">
                        Nombre de voyages effectués (voiture/date): <strong><%= nbVoyagesVoitureDateObj != null ? nbVoyagesVoitureDateObj.toString() : "0" %></strong>
                    </p>
                    <p style="margin-bottom: 1rem;">
                        Départ aéroport: <strong><%= departAeroport != null ? departAeroport.toString() : "" %></strong>
                    </p>
                    <p style="margin-bottom: 1rem;">
                        Durée du voyage: <strong><%= voyage != null ? voyage.getDureeMinutes() : 0 %> min</strong>
                    </p>
                    <p style="margin-bottom: 1rem;">
                        Retour à l'aéroport: <strong><%= retourAeroport != null ? retourAeroport.toString() : "" %></strong>
                    </p>

                    <% if (legs != null && !legs.isEmpty()) { %>
                        <h3 style="margin: 1rem 0;">Trajets</h3>
                        <div class="table-container">
                            <table class="data-table">
                                <thead>
                                <tr>
                                    <th>De</th>
                                    <th>Vers</th>
                                    <th>Distance (km)</th>
                                    <th>Départ</th>
                                    <th>Arrivée</th>
                                </tr>
                                </thead>
                                <tbody>
                                <% for (VoyageLeg l : legs) { %>
                                    <tr>
                                        <td><%= l.getFromLabel() %></td>
                                        <td><%= l.getToLabel() %></td>
                                        <td><%= l.getDistanceKm() %></td>
                                        <td><%= l.getDepart() != null ? l.getDepart().toString() : "" %></td>
                                        <td><%= l.getArrive() != null ? l.getArrive().toString() : "" %></td>
                                    </tr>
                                <% } %>
                                </tbody>
                            </table>
                        </div>
                    <% } %>

                    <% if (stops != null && !stops.isEmpty()) { %>
                        <h3 style="margin: 1rem 0;">Stops</h3>
                        <div class="table-container">
                            <table class="data-table">
                                <thead>
                                <tr>
                                    <th>Ordre</th>
                                    <th>Réservation</th>
                                    <th>Lieu destination</th>
                                    <th>Distance (km)</th>
                                    <th>Date/Heure d'arrivée</th>
                                </tr>
                                </thead>
                                <tbody>
                                <% for (VoyageStop s : stops) { %>
                                    <tr>
                                        <td><strong><%= s.getOrdre() %></strong></td>
                                        <td>#<%= s.getIdReservation() %></td>
                                        <td><%= s.getLieuLabel() != null ? s.getLieuLabel() : ("#" + s.getIdLieuDestination()) %></td>
                                        <td><%= s.getDistanceKm() %></td>
                                        <td><%= (arrivalByStopIdDT != null && arrivalByStopIdDT.get(s.getId()) != null) ? arrivalByStopIdDT.get(s.getId()).toString() : "" %></td>
                                    </tr>
                                <% } %>
                                </tbody>
                            </table>
                        </div>
                    <% } else { %>
                        <div class="empty-state">
                            <div class="empty-state-icon">📭</div>
                            <h3>Aucun stop</h3>
                            <p>Ce voyage ne contient aucun stop.</p>
                        </div>
                    <% } %>

                    <div class="result-actions" style="margin-top: 1.5rem;">
                        <a class="btn btn-secondary" href="<%= request.getContextPath() %>/planification/voyages">Retour à la liste</a>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>
</body>
</html>
