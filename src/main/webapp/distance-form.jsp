<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="main.java.com.entity.Distance" %>
<%@ page import="main.java.com.entity.Lieu" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulaire Distance - BackOffice</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/admin-style.css">
</head>
<body>
    <div class="app-container">
        <%@ include file="/WEB-INF/sidebar.jsp" %>

        <main class="main-content">
            <% 
                Distance distance = (Distance) request.getAttribute("distance");
                boolean isEdit = distance != null;
                String action = isEdit ? request.getContextPath() + "/distance/update" : request.getContextPath() + "/distance/create";
                List<Lieu> lieux = (List<Lieu>) request.getAttribute("lieux");
            %>

            <div class="page-header">
                <h1 class="page-title"><%= isEdit ? "Modifier une distance" : "Ajouter une distance" %></h1>
                <p class="page-subtitle">Distance entre deux lieux</p>
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
                    <div class="card-header">
                        <h2><%= isEdit ? "Édition" : "Nouvelle" %></h2>
                    </div>
                    <div class="card-body">
                        <form action="<%= action %>" method="post" class="form">
                            <% if (isEdit) { %>
                                <input type="hidden" name="id" value="<%= distance.getId() %>">
                            <% } %>

                            <div class="form-row">
                                <div class="form-group">
                                    <label class="form-label required">De</label>
                                    <select name="fromLieu" class="form-control" required>
                                        <option value="">Sélectionner</option>
                                        <% if (lieux != null) { for (Lieu l : lieux) { %>
                                            <option value="<%= l.getId() %>" <%= isEdit && distance.getFromLieu() == l.getId() ? "selected" : "" %>><%= l.getLieu() %></option>
                                        <% }} %>
                                    </select>
                                </div>

                                <div class="form-group">
                                    <label class="form-label required">Vers</label>
                                    <select name="toLieu" class="form-control" required>
                                        <option value="">Sélectionner</option>
                                        <% if (lieux != null) { for (Lieu l : lieux) { %>
                                            <option value="<%= l.getId() %>" <%= isEdit && distance.getToLieu() == l.getId() ? "selected" : "" %>><%= l.getLieu() %></option>
                                        <% }} %>
                                    </select>
                                </div>

                                <div class="form-group">
                                    <label class="form-label required">Distance (km)</label>
                                    <input type="number" step="0.01" min="0" name="distanceKm" class="form-control" value="<%= isEdit ? distance.getDistanceKm() : "" %>" required>
                                </div>
                            </div>

                            <div class="form-actions">
                                <button type="submit" class="btn btn-primary"><%= isEdit ? "Enregistrer" : "Créer" %></button>
                                <a href="<%= request.getContextPath() %>/distance/liste" class="btn btn-secondary">Annuler</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
