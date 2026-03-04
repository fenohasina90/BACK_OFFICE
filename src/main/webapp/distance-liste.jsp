<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="main.java.com.entity.Distance" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Distances - BackOffice</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/admin-style.css">
</head>
<body>
    <div class="app-container">
        <%@ include file="/WEB-INF/sidebar.jsp" %>

        <main class="main-content">
            <div class="page-header">
                <h1 class="page-title">Distances</h1>
                <p class="page-subtitle">Liste des distances (from -> to)</p>
            </div>

            <div class="page-content">
                <% String error = (String) request.getAttribute("error"); %>
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
                    <div class="card-header" style="display:flex; align-items:center; justify-content:space-between; gap: 12px;">
                        <h2>Liste des distances</h2>
                        <a href="<%= request.getContextPath() %>/distance/formulaire" class="btn btn-primary">➕ Ajouter</a>
                    </div>
                    <div class="card-body">
                        <% List<Distance> distances = (List<Distance>) request.getAttribute("distances"); %>
                        <% if (distances != null && !distances.isEmpty()) { %>
                            <div class="table-container">
                                <table class="data-table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>De</th>
                                            <th>Vers</th>
                                            <th>Distance (km)</th>
                                            <th class="text-right">Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (Distance d : distances) { %>
                                        <tr>
                                            <td><strong>#<%= d.getId() %></strong></td>
                                            <td><%= d.getFromLieuLabel() != null ? d.getFromLieuLabel() : ("#" + d.getFromLieu()) %></td>
                                            <td><%= d.getToLieuLabel() != null ? d.getToLieuLabel() : ("#" + d.getToLieu()) %></td>
                                            <td><%= d.getDistanceKm() %></td>
                                            <td class="text-right">
                                                <div class="btn-group" style="justify-content:flex-end;">
                                                    <a class="btn btn-sm btn-secondary" href="<%= request.getContextPath() %>/distance/edit?id=<%= d.getId() %>">Modifier</a>
                                                    <form action="<%= request.getContextPath() %>/distance/delete" method="post" style="display:inline;">
                                                        <input type="hidden" name="id" value="<%= d.getId() %>">
                                                        <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Supprimer cette distance ?');">Supprimer</button>
                                                    </form>
                                                </div>
                                            </td>
                                        </tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>
                        <% } else { %>
                            <div class="empty-state">
                                <div class="empty-state-icon">📏</div>
                                <h3>Aucune distance</h3>
                                <p>Ajoute une distance entre deux lieux.</p>
                                <a href="<%= request.getContextPath() %>/distance/formulaire" class="btn btn-primary">Ajouter une distance</a>
                            </div>
                        <% } %>
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
