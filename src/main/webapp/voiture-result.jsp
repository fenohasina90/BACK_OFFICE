<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="main.java.com.entity.Voiture" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Résultat de l'opération - BackOffice</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/admin-style.css">
</head>
<body>
    <div class="app-container">
        <%@ include file="/WEB-INF/sidebar.jsp" %>
        
        <main class="main-content">
            <% 
                String message = (String) request.getAttribute("message");
                String error = (String) request.getAttribute("error");
                Voiture voiture = (Voiture) request.getAttribute("voiture");
                String operation = (String) request.getAttribute("operation"); // create, update, delete
                boolean success = error == null;
            %>
            
            <div class="page-header">
                <h1 class="page-title">Résultat de l'opération</h1>
                <p class="page-subtitle">
                    <% 
                        if (operation != null) {
                            if ("create".equals(operation)) {
                                out.print("Ajout d'un véhicule");
                            } else if ("update".equals(operation)) {
                                out.print("Modification d'un véhicule");
                            } else if ("delete".equals(operation)) {
                                out.print("Suppression d'un véhicule");
                            }
                        }
                    %>
                </p>
            </div>
            
            <div class="page-content">
                <div class="result-container">
                    <% if (success) { %>
                    <!-- Message de succès -->
                    <div class="alert alert-success">
                        <div class="alert-icon">✓</div>
                        <div class="alert-content">
                            <h3 class="alert-title">Opération réussie</h3>
                            <p class="alert-message">
                                <%= message != null ? message : "Le véhicule a été traité avec succès." %>
                            </p>
                        </div>
                    </div>
                    
                    <% if (voiture != null && !"delete".equals(operation)) { %>
                    <!-- Détails du véhicule -->
                    <div class="card">
                        <div class="card-header">
                            <h2>Détails du véhicule</h2>
                        </div>
                        <div class="card-body">
                            <div class="details-grid">
                                <div class="detail-item">
                                    <span class="detail-label">ID Véhicule</span>
                                    <span class="detail-value">#<%= voiture.getIdVoiture() %></span>
                                </div>
                                
                                <div class="detail-item">
                                    <span class="detail-label">Type de Carburant</span>
                                    <span class="detail-value">
                                        <% 
                                            String typeCarburant = voiture.getTypeCarburant();
                                            String badgeClass = "badge-default";
                                            String badgeLabel = typeCarburant;
                                            
                                            if ("E".equals(typeCarburant)) {
                                                badgeClass = "badge-success";
                                                badgeLabel = "Essence";
                                            } else if ("D".equals(typeCarburant)) {
                                                badgeClass = "badge-info";
                                                badgeLabel = "Diesel";
                                            } else if ("El".equals(typeCarburant)) {
                                                badgeClass = "badge-warning";
                                                badgeLabel = "Électrique";
                                            } else if ("H".equals(typeCarburant)) {
                                                badgeClass = "badge-primary";
                                                badgeLabel = "Hybride";
                                            }
                                        %>
                                        <span class="badge <%= badgeClass %>"><%= badgeLabel %></span>
                                    </span>
                                </div>
                                
                                <div class="detail-item">
                                    <span class="detail-label">Nombre de Places</span>
                                    <span class="detail-value"><%= voiture.getNbPlace() %> places</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <% } %>
                    
                    <!-- Actions -->
                    <div class="result-actions">
                        <a href="<%= request.getContextPath() %>/voiture/index" class="btn btn-primary">
                            Retour à la liste
                        </a>
                        <a href="<%= request.getContextPath() %>/voiture/form" class="btn btn-secondary">
                            Ajouter un autre véhicule
                        </a>
                    </div>
                    
                    <% } else { %>
                    <!-- Message d'erreur -->
                    <div class="alert alert-error">
                        <div class="alert-icon">✕</div>
                        <div class="alert-content">
                            <h3 class="alert-title">Erreur</h3>
                            <p class="alert-message">
                                <%= error != null ? error : "Une erreur s'est produite lors du traitement." %>
                            </p>
                        </div>
                    </div>
                    
                    <!-- Actions en cas d'erreur -->
                    <div class="result-actions">
                        <a href="<%= request.getContextPath() %>/voiture/form" class="btn btn-primary">
                            Réessayer
                        </a>
                        <a href="<%= request.getContextPath() %>/voiture/index" class="btn btn-secondary">
                            Retour à la liste
                        </a>
                    </div>
                    <% } %>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
