<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gestion des Distances - BackOffice</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/admin-style.css">
</head>
<body>
    <div class="app-container">
        <%@ include file="/WEB-INF/sidebar.jsp" %>

        <main class="main-content">
            <div class="page-header">
                <h1 class="page-title">Gestion des Distances</h1>
                <p class="page-subtitle">Gérez les distances entre deux lieux</p>
            </div>

            <div class="page-content">
                <div class="card">
                    <div class="card-header">
                        <h2>Actions</h2>
                    </div>
                    <div class="card-body">
                        <div class="btn-group">
                            <a href="<%= request.getContextPath() %>/distance/liste" class="btn btn-primary">📋 Voir la liste</a>
                            <a href="<%= request.getContextPath() %>/distance/formulaire" class="btn btn-success">➕ Ajouter une distance</a>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
