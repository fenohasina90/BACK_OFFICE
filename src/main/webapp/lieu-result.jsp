<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="main.java.com.entity.Lieu" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Résultat - Lieu</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/admin-style.css">
</head>
<body>
    <div class="app-container">
        <%@ include file="/WEB-INF/sidebar.jsp" %>

        <main class="main-content">
            <% 
                String message = (String) request.getAttribute("message");
                String error = (String) request.getAttribute("error");
                String operation = (String) request.getAttribute("operation");
                Lieu lieu = (Lieu) request.getAttribute("lieu");
                boolean success = error == null;
            %>

            <div class="page-header">
                <h1 class="page-title">Résultat</h1>
                <p class="page-subtitle"><%= operation != null ? operation : "" %></p>
            </div>

            <div class="page-content">
                <% if (success) { %>
                    <div class="alert alert-success">
                        <div class="alert-icon">✓</div>
                        <div class="alert-content">
                            <h3 class="alert-title">OK</h3>
                            <p class="alert-message"><%= message != null ? message : "Opération réussie" %></p>
                        </div>
                    </div>

                    <% if (lieu != null && !"delete".equals(operation)) { %>
                        <div class="card">
                            <div class="card-header"><h2>Détails</h2></div>
                            <div class="card-body">
                                <div class="details-grid">
                                    <div class="detail-item"><span class="detail-label">ID</span><span class="detail-value">#<%= lieu.getId() %></span></div>
                                    <div class="detail-item"><span class="detail-label">Libellé</span><span class="detail-value"><%= lieu.getLieu() %></span></div>
                                </div>
                            </div>
                        </div>
                    <% } %>

                    <div class="result-actions">
                        <a href="<%= request.getContextPath() %>/lieu/liste" class="btn btn-primary">Retour à la liste</a>
                        <a href="<%= request.getContextPath() %>/lieu/formulaire" class="btn btn-secondary">Ajouter</a>
                    </div>
                <% } else { %>
                    <div class="alert alert-error">
                        <div class="alert-icon">✕</div>
                        <div class="alert-content">
                            <h3 class="alert-title">Erreur</h3>
                            <p class="alert-message"><%= error %></p>
                        </div>
                    </div>
                    <div class="result-actions">
                        <a href="<%= request.getContextPath() %>/lieu/liste" class="btn btn-secondary">Retour</a>
                    </div>
                <% } %>
            </div>
        </main>
    </div>
</body>
</html>
