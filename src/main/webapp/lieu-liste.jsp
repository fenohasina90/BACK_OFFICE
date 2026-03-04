<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="main.java.com.entity.Lieu" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Liste des Lieux - BackOffice</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/admin-style.css">
</head>
<body>
    <div class="app-container">
        <%@ include file="/WEB-INF/sidebar.jsp" %>

        <main class="main-content">
            <div class="page-header">
                <h1 class="page-title">Lieux</h1>
                <p class="page-subtitle">Liste des lieux disponibles</p>
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
                        <h2>Liste des lieux</h2>
                        <a href="<%= request.getContextPath() %>/lieu/formulaire" class="btn btn-primary">➕ Ajouter</a>
                    </div>
                    <div class="card-body">
                        <% List<Lieu> lieux = (List<Lieu>) request.getAttribute("lieux"); %>
                        <% if (lieux != null && !lieux.isEmpty()) { %>
                            <div class="table-container">
                                <table class="data-table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Libellé</th>
                                            <th class="text-right">Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (Lieu l : lieux) { %>
                                        <tr>
                                            <td><strong>#<%= l.getId() %></strong></td>
                                            <td><%= l.getLieu() %></td>
                                            <td class="text-right">
                                                <div class="btn-group" style="justify-content:flex-end;">
                                                    <a class="btn btn-sm btn-secondary" href="<%= request.getContextPath() %>/lieu/edit?id=<%= l.getId() %>">Modifier</a>
                                                    <form action="<%= request.getContextPath() %>/lieu/delete" method="post" style="display:inline;">
                                                        <input type="hidden" name="id" value="<%= l.getId() %>">
                                                        <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Supprimer ce lieu ?');">Supprimer</button>
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
                                <div class="empty-state-icon">📍</div>
                                <h3>Aucun lieu</h3>
                                <p>Commence par ajouter un lieu.</p>
                                <a href="<%= request.getContextPath() %>/lieu/formulaire" class="btn btn-primary">Ajouter un lieu</a>
                            </div>
                        <% } %>
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
