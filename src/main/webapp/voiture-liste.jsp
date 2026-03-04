<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="main.java.com.entity.Voiture" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Liste des Véhicules - BackOffice</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/admin-style.css">
</head>
<body>
    <div class="app-container">
        <%@ include file="/WEB-INF/sidebar.jsp" %>
        
        <main class="main-content">
            <div class="page-header">
                <h1 class="page-title">Gestion des Véhicules</h1>
                <p class="page-subtitle">Consultez et gérez l'ensemble de votre flotte automobile</p>
            </div>
            
            <div class="page-content">
                <div class="content-header">
                    <div class="content-header-left">
                        <h2>Liste des véhicules</h2>
                    </div>
                    <div class="content-header-right">
                        <a href="<%= request.getContextPath() %>/voiture/form" class="btn btn-primary">
                            <span>+</span> Ajouter un véhicule
                        </a>
                    </div>
                </div>
                
                <div class="card">
                    <% 
                        List<Voiture> voitures = (List<Voiture>) request.getAttribute("voitures");
                        if (voitures != null && !voitures.isEmpty()) {
                    %>
                    <div class="table-container">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Type de Carburant</th>
                                    <th>Nombre de Places</th>
                                    <th class="text-right">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Voiture voiture : voitures) { %>
                                <tr>
                                    <td><strong>#<%= voiture.getIdVoiture() %></strong></td>
                                    <td>
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
                                    </td>
                                    <td><%= voiture.getNbPlace() %> places</td>
                                    <td class="text-right">
                                        <div class="btn-group">
                                            <a href="<%= request.getContextPath() %>/voiture/edit/<%= voiture.getIdVoiture() %>" 
                                               class="btn btn-sm btn-secondary">Modifier</a>
                                            <a href="<%= request.getContextPath() %>/voiture/delete/<%= voiture.getIdVoiture() %>" 
                                               class="btn btn-sm btn-danger"
                                               onclick="return confirm('Êtes-vous sûr de vouloir supprimer ce véhicule ?');">Supprimer</a>
                                        </div>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                    <% } else { %>
                    <div class="empty-state">
                        <div class="empty-state-icon">🚗</div>
                        <h3>Aucun véhicule</h3>
                        <p>Commencez par ajouter votre premier véhicule à la flotte</p>
                        <a href="<%= request.getContextPath() %>/voiture/form" class="btn btn-primary">
                            Ajouter un véhicule
                        </a>
                    </div>
                    <% } %>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
