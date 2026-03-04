<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="main.java.com.entity.Lieu" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulaire Lieu - BackOffice</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/admin-style.css">
</head>
<body>
    <div class="app-container">
        <%@ include file="/WEB-INF/sidebar.jsp" %>

        <main class="main-content">
            <% 
                Lieu lieu = (Lieu) request.getAttribute("lieu");
                boolean isEdit = lieu != null;
                String action = isEdit ? request.getContextPath() + "/lieu/update" : request.getContextPath() + "/lieu/create";
            %>

            <div class="page-header">
                <h1 class="page-title"><%= isEdit ? "Modifier un lieu" : "Ajouter un lieu" %></h1>
                <p class="page-subtitle">Informations du lieu</p>
            </div>

            <div class="page-content">
                <div class="card">
                    <div class="card-header">
                        <h2><%= isEdit ? "Édition" : "Nouveau" %></h2>
                    </div>
                    <div class="card-body">
                        <form action="<%= action %>" method="post" class="form">
                            <% if (isEdit) { %>
                                <input type="hidden" name="id" value="<%= lieu.getId() %>">
                            <% } %>

                            <div class="form-group">
                                <label for="lieu" class="form-label required">Libellé</label>
                                <input type="text" id="lieu" name="lieu" class="form-control" value="<%= isEdit ? lieu.getLieu() : "" %>" required>
                            </div>

                            <div class="form-actions">
                                <button type="submit" class="btn btn-primary"><%= isEdit ? "Enregistrer" : "Créer" %></button>
                                <a href="<%= request.getContextPath() %>/lieu/liste" class="btn btn-secondary">Annuler</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
