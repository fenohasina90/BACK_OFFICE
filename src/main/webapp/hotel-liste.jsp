<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="main.java.com.entity.Hotel" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Hôtels - BackOffice</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/admin-style.css">
</head>
<body>
    <div class="app-container">
        <%@ include file="/WEB-INF/sidebar.jsp" %>

        <main class="main-content">
            <div class="page-header">
                <h1 class="page-title">Hôtels</h1>
                <p class="page-subtitle">Liste des hôtels</p>
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
                        <h2>Liste des hôtels</h2>
                        <a href="<%= request.getContextPath() %>/hotel/formulaire" class="btn btn-primary">➕ Ajouter</a>
                    </div>
                    <div class="card-body">
                        <% List<Hotel> hotels = (List<Hotel>) request.getAttribute("hotels"); %>
                        <% if (hotels != null && !hotels.isEmpty()) { %>
                            <div class="table-container">
                                <table class="data-table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Nom</th>
                                            <th>Adresse</th>
                                            <th>Ville</th>
                                            <th>Pays</th>
                                            <th>Lieu</th>
                                            <th class="text-right">Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (Hotel h : hotels) { %>
                                        <tr>
                                            <td><strong>#<%= h.getId() %></strong></td>
                                            <td><%= h.getNom() %></td>
                                            <td><%= h.getAdresse() %></td>
                                            <td><%= h.getVille() %></td>
                                            <td><%= h.getPays() %></td>
                                            <td><%= h.getLieuLabel() != null ? h.getLieuLabel() : ("#" + h.getIdLieu()) %></td>
                                            <td class="text-right">
                                                <div class="btn-group" style="justify-content:flex-end;">
                                                    <a class="btn btn-sm btn-secondary" href="<%= request.getContextPath() %>/hotel/edit?id=<%= h.getId() %>">Modifier</a>
                                                    <form action="<%= request.getContextPath() %>/hotel/delete" method="post" style="display:inline;">
                                                        <input type="hidden" name="id" value="<%= h.getId() %>">
                                                        <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Supprimer cet hôtel ?');">Supprimer</button>
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
                                <div class="empty-state-icon">🏨</div>
                                <h3>Aucun hôtel</h3>
                                <p>Commence par ajouter un hôtel.</p>
                                <a href="<%= request.getContextPath() %>/hotel/formulaire" class="btn btn-primary">Ajouter un hôtel</a>
                            </div>
                        <% } %>
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
