<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="main.java.com.entity.Client" %>
<%@ page import="main.java.com.entity.Hotel" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Nouvelle Réservation - BackOffice</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/admin-style.css">
</head>
<body>
    <div class="app-container">
        <%@ include file="/WEB-INF/sidebar.jsp" %>
        
        <main class="main-content">
            <div class="page-header">
                <h1 class="page-title">Nouvelle Réservation</h1>
                <p class="page-subtitle">Créez une nouvelle réservation pour un client</p>
            </div>
            
            <div class="page-content">
                <div class="form-container">
                    <div class="card">
                        <div class="card-header">
                            <h2>Informations de la réservation</h2>
                        </div>
                        <div class="card-body">
                            <form action="<%= request.getContextPath() %>/reservation/create" method="post" class="form">
                                <div class="form-group">
                                    <label for="client" class="form-label required">Client</label>
                                    <select id="client" name="idClient" class="form-control" required>
                                        <option value="">Sélectionnez un client</option>
                                        <% 
                                            List<Client> clients = (List<Client>) request.getAttribute("clients");
                                            if (clients != null) {
                                                for (Client client : clients) {
                                        %>
                                        <option value="<%= client.getId() %>">
                                            <%= client.getNom() %> <%= client.getPrenom() %>
                                            <% if (client.getEmail() != null) { %>
                                                - <%= client.getEmail() %>
                                            <% } %>
                                        </option>
                                        <% 
                                                }
                                            }
                                        %>
                                    </select>
                                    <small class="form-help">Sélectionnez le client qui effectue la réservation</small>
                                </div>
                                
                                <div class="form-group">
                                    <label for="hotel" class="form-label required">Hôtel</label>
                                    <select id="hotel" name="idHotel" class="form-control" required>
                                        <option value="">Sélectionnez un hôtel</option>
                                        <% 
                                            List<Hotel> hotels = (List<Hotel>) request.getAttribute("hotels");
                                            if (hotels != null) {
                                                for (Hotel hotel : hotels) {
                                        %>
                                        <option value="<%= hotel.getId() %>">
                                            <%= hotel.getNom() %>
                                            <% if (hotel.getVille() != null) { %>
                                                - <%= hotel.getVille() %>
                                            <% } %>
                                        </option>
                                        <% 
                                                }
                                            }
                                        %>
                                    </select>
                                    <small class="form-help">Choisissez l'hôtel pour la réservation</small>
                                </div>
                                
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="dateReservation" class="form-label required">Date de Réservation</label>
                                        <input type="date" 
                                               id="dateReservation" 
                                               name="dateReservation" 
                                               class="form-control" 
                                               required>
                                        <small class="form-help">Date de début du séjour</small>
                                    </div>

                                    <div class="form-group">
                                        <label for="heureReservation" class="form-label required">Heure de Réservation</label>
                                        <input type="time"
                                               id="heureReservation"
                                               name="heureReservation"
                                               class="form-control"
                                               required>
                                        <small class="form-help">Heure d'arrivée/prise en charge</small>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label for="nbPersonnes" class="form-label required">Nombre de Personnes</label>
                                        <input type="number" 
                                               id="nbPersonnes" 
                                               name="nbPersonnes" 
                                               class="form-control" 
                                               min="1" 
                                               max="10" 
                                               value="2"
                                               required>
                                        <small class="form-help">Nombre de personnes (1-10)</small>
                                    </div>
                                </div>
                                
                                <div class="form-actions">
                                    <button type="submit" class="btn btn-primary">
                                        Créer la réservation
                                    </button>
                                    <a href="<%= request.getContextPath() %>/reservation/index" class="btn btn-secondary">
                                        Annuler
                                    </a>
                                </div>
                            </form>
                        </div>
                    </div>
                    
                    <% 
                        if ((clients == null || clients.isEmpty()) || (hotels == null || hotels.isEmpty())) {
                    %>
                    <div class="alert alert-warning" style="margin-top: 1.5rem;">
                        <div class="alert-icon">⚠</div>
                        <div class="alert-content">
                            <h3 class="alert-title">Attention</h3>
                            <p class="alert-message">
                                <% if (clients == null || clients.isEmpty()) { %>
                                Aucun client disponible. Veuillez ajouter des clients avant de créer une réservation.<br>
                                <% } %>
                                <% if (hotels == null || hotels.isEmpty()) { %>
                                Aucun hôtel disponible. Veuillez ajouter des hôtels avant de créer une réservation.
                                <% } %>
                            </p>
                        </div>
                    </div>
                    <% } %>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
