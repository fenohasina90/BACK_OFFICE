<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="main.java.com.entity.Reservation" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Résultat de la Réservation - BackOffice</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/admin-style.css">
</head>
<body>
    <div class="app-container">
        <%@ include file="/WEB-INF/sidebar.jsp" %>
        
        <main class="main-content">
            <% 
                String message = (String) request.getAttribute("message");
                String error = (String) request.getAttribute("error");
                Reservation reservation = (Reservation) request.getAttribute("reservation");
                boolean success = error == null;
            %>
            
            <div class="page-header">
                <h1 class="page-title">Résultat de la Réservation</h1>
                <p class="page-subtitle">Confirmation de l'opération de réservation</p>
            </div>
            
            <div class="page-content">
                <div class="result-container">
                    <% if (success) { %>
                    <!-- Message de succès -->
                    <div class="alert alert-success">
                        <div class="alert-icon">✓</div>
                        <div class="alert-content">
                            <h3 class="alert-title">Réservation créée avec succès</h3>
                            <p class="alert-message">
                                <%= message != null ? message : "La réservation a été enregistrée dans le système." %>
                            </p>
                        </div>
                    </div>
                    
                    <% if (reservation != null) { %>
                    <!-- Détails de la réservation -->
                    <div class="card">
                        <div class="card-header">
                            <h2>Détails de la réservation</h2>
                        </div>
                        <div class="card-body">
                            <div class="details-grid">
                                <div class="detail-item">
                                    <span class="detail-label">Numéro de Réservation</span>
                                    <span class="detail-value">
                                        <strong>#<%= reservation.getIdReservation() %></strong>
                                    </span>
                                </div>
                                
                                <div class="detail-item">
                                    <span class="detail-label">Client</span>
                                    <span class="detail-value">
                                        <% if (reservation.getClient() != null) { %>
                                            <%= reservation.getClient().getNom() %> <%= reservation.getClient().getPrenom() %>
                                        <% } else { %>
                                            ID Client: <%= reservation.getIdClient() %>
                                        <% } %>
                                    </span>
                                </div>
                                
                                <div class="detail-item">
                                    <span class="detail-label">Hôtel</span>
                                    <span class="detail-value">
                                        <% if (reservation.getHotel() != null) { %>
                                            <%= reservation.getHotel().getNom() %>
                                            <% if (reservation.getHotel().getVille() != null) { %>
                                                - <%= reservation.getHotel().getVille() %>
                                            <% } %>
                                        <% } else { %>
                                            ID Hôtel: <%= reservation.getIdHotel() %>
                                        <% } %>
                                    </span>
                                </div>
                                
                                <div class="detail-item">
                                    <span class="detail-label">Date de Réservation</span>
                                    <span class="detail-value">
                                        <% 
                                            if (reservation.getDateReservation() != null) {
                                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                                                out.print(reservation.getDateReservation().format(formatter));
                                            }
                                        %>
                                    </span>
                                </div>

                                <div class="detail-item">
                                    <span class="detail-label">Heure de Réservation</span>
                                    <span class="detail-value">
                                        <%
                                            if (reservation.getHeureReservation() != null) {
                                                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                                                out.print(reservation.getHeureReservation().format(timeFormatter));
                                            }
                                        %>
                                    </span>
                                </div>
                                
                                <div class="detail-item">
                                    <span class="detail-label">Nombre de Personnes</span>
                                    <span class="detail-value">
                                        <span class="badge badge-info"><%= reservation.getNbPersonnes() %> personne<%= reservation.getNbPersonnes() > 1 ? "s" : "" %></span>
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <% } %>
                    
                    <!-- Actions -->
                    <div class="result-actions">
                        <a href="<%= request.getContextPath() %>/reservation/index" class="btn btn-primary">
                            Retour aux réservations
                        </a>
                        <a href="<%= request.getContextPath() %>/reservation/form" class="btn btn-secondary">
                            Créer une autre réservation
                        </a>
                    </div>
                    
                    <% } else { %>
                    <!-- Message d'erreur -->
                    <div class="alert alert-error">
                        <div class="alert-icon">✕</div>
                        <div class="alert-content">
                            <h3 class="alert-title">Erreur lors de la création</h3>
                            <p class="alert-message">
                                <%= error != null ? error : "Une erreur s'est produite lors de la création de la réservation." %>
                            </p>
                        </div>
                    </div>
                    
                    <!-- Actions en cas d'erreur -->
                    <div class="result-actions">
                        <a href="<%= request.getContextPath() %>/reservation/form" class="btn btn-primary">
                            Réessayer
                        </a>
                        <a href="<%= request.getContextPath() %>/reservation/index" class="btn btn-secondary">
                            Retour aux réservations
                        </a>
                    </div>
                    <% } %>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
