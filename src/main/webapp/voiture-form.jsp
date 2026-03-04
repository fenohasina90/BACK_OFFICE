<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="main.java.com.entity.Voiture" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulaire Véhicule - BackOffice</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/admin-style.css">
</head>
<body>
    <div class="app-container">
        <%@ include file="/WEB-INF/sidebar.jsp" %>
        
        <main class="main-content">
            <% 
                Voiture voiture = (Voiture) request.getAttribute("voiture");
                boolean isEdit = voiture != null;
                String action = isEdit ? 
                    request.getContextPath() + "/voiture/update" : 
                    request.getContextPath() + "/voiture/create";
            %>
            
            <div class="page-header">
                <h1 class="page-title"><%= isEdit ? "Modifier un véhicule" : "Ajouter un véhicule" %></h1>
                <p class="page-subtitle"><%= isEdit ? "Mettez à jour les informations du véhicule" : "Ajoutez un nouveau véhicule à votre flotte" %></p>
            </div>
            
            <div class="page-content">
                <div class="form-container">
                    <div class="card">
                        <div class="card-header">
                            <h2><%= isEdit ? "Informations du véhicule" : "Nouveau véhicule" %></h2>
                        </div>
                        <div class="card-body">
                            <form action="<%= action %>" method="post" class="form">
                                <% if (isEdit) { %>
                                <input type="hidden" name="idVoiture" value="<%= voiture.getIdVoiture() %>">
                                <% } %>
                                
                                <div class="form-group">
                                    <label class="form-label required">Type de Carburant</label>
                                    <div class="radio-group">
                                        <label class="radio-label">
                                            <input type="radio" name="typeCarburant" value="E" 
                                                   <%= isEdit && "E".equals(voiture.getTypeCarburant()) ? "checked" : (!isEdit ? "checked" : "") %>>
                                            <span class="radio-text">
                                                <strong>Essence</strong>
                                                <small>Moteur thermique essence</small>
                                            </span>
                                        </label>
                                        
                                        <label class="radio-label">
                                            <input type="radio" name="typeCarburant" value="D"
                                                   <%= isEdit && "D".equals(voiture.getTypeCarburant()) ? "checked" : "" %>>
                                            <span class="radio-text">
                                                <strong>Diesel</strong>
                                                <small>Moteur thermique diesel</small>
                                            </span>
                                        </label>
                                        
                                        <label class="radio-label">
                                            <input type="radio" name="typeCarburant" value="El"
                                                   <%= isEdit && "El".equals(voiture.getTypeCarburant()) ? "checked" : "" %>>
                                            <span class="radio-text">
                                                <strong>Électrique</strong>
                                                <small>100% électrique</small>
                                            </span>
                                        </label>
                                        
                                        <label class="radio-label">
                                            <input type="radio" name="typeCarburant" value="H"
                                                   <%= isEdit && "H".equals(voiture.getTypeCarburant()) ? "checked" : "" %>>
                                            <span class="radio-text">
                                                <strong>Hybride</strong>
                                                <small>Motorisation hybride</small>
                                            </span>
                                        </label>
                                    </div>
                                </div>
                                
                                <div class="form-group">
                                    <label for="nbPlace" class="form-label required">Nombre de Places</label>
                                    <input type="number" 
                                           id="nbPlace" 
                                           name="nbPlace" 
                                           class="form-control" 
                                           min="1" 
                                           max="32" 
                                           value="<%= isEdit ? voiture.getNbPlace() : 5 %>"
                                           required>
                                    <small class="form-help">Nombre de places passagers (entre 1 et 9)</small>
                                </div>
                                
                                <div class="form-actions">
                                    <button type="submit" class="btn btn-primary">
                                        <%= isEdit ? "Enregistrer les modifications" : "Ajouter le véhicule" %>
                                    </button>
                                    <a href="<%= request.getContextPath() %>/voiture/index" class="btn btn-secondary">
                                        Annuler
                                    </a>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
