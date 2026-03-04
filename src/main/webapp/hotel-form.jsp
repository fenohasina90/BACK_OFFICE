<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="main.java.com.entity.Hotel" %>
<%@ page import="main.java.com.entity.Lieu" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulaire Hôtel - BackOffice</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/admin-style.css">
</head>
<body>
    <div class="app-container">
        <%@ include file="/WEB-INF/sidebar.jsp" %>

        <main class="main-content">
            <% 
                Hotel hotel = (Hotel) request.getAttribute("hotel");
                boolean isEdit = hotel != null;
                String action = isEdit ? request.getContextPath() + "/hotel/update" : request.getContextPath() + "/hotel/create";
                List<Lieu> lieux = (List<Lieu>) request.getAttribute("lieux");
            %>

            <div class="page-header">
                <h1 class="page-title"><%= isEdit ? "Modifier un hôtel" : "Ajouter un hôtel" %></h1>
                <p class="page-subtitle">Informations de l'hôtel</p>
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
                        <h2><%= isEdit ? "Édition" : "Nouveau" %></h2>
                    </div>
                    <div class="card-body">
                        <form action="<%= action %>" method="post" class="form">
                            <% if (isEdit) { %>
                                <input type="hidden" name="id" value="<%= hotel.getId() %>">
                            <% } %>

                            <div class="form-group">
                                <label class="form-label required">Nom</label>
                                <input type="text" name="nom" class="form-control" value="<%= isEdit ? hotel.getNom() : "" %>" required>
                            </div>

                            <div class="form-group">
                                <label class="form-label required">Adresse</label>
                                <input type="text" name="adresse" class="form-control" value="<%= isEdit ? hotel.getAdresse() : "" %>" required>
                            </div>

                            <div class="form-row">
                                <div class="form-group">
                                    <label class="form-label required">Ville</label>
                                    <input type="text" name="ville" class="form-control" value="<%= isEdit ? hotel.getVille() : "" %>" required>
                                </div>

                                <div class="form-group">
                                    <label class="form-label required">Pays</label>
                                    <input type="text" name="pays" class="form-control" value="<%= isEdit ? hotel.getPays() : "" %>" required>
                                </div>

                                <div class="form-group">
                                    <label class="form-label required">Lieu</label>
                                    <select name="idLieu" class="form-control" required>
                                        <option value="">Sélectionner</option>
                                        <% if (lieux != null) { for (Lieu l : lieux) { %>
                                            <option value="<%= l.getId() %>" <%= isEdit && hotel.getIdLieu() == l.getId() ? "selected" : "" %>><%= l.getLieu() %></option>
                                        <% }} %>
                                    </select>
                                </div>
                            </div>

                            <div class="form-actions">
                                <button type="submit" class="btn btn-primary"><%= isEdit ? "Enregistrer" : "Créer" %></button>
                                <a href="<%= request.getContextPath() %>/hotel/liste" class="btn btn-secondary">Annuler</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
