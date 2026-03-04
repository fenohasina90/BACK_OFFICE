<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<aside class="sidebar">
    <div class="sidebar-header">
        <a href="/BackOffice/" class="sidebar-logo">
            <span>📊</span>
            <span>BackOffice</span>
        </a>
    </div>
    
    <nav class="sidebar-nav">
        <div class="nav-section">
            <div class="nav-section-title">Principal</div>
            <ul class="nav-menu">
                <li class="nav-item">
                    <a href="/BackOffice/" class="nav-link">
                        <span class="nav-icon">🏠</span>
                        <span>Tableau de bord</span>
                    </a>
                </li>
            </ul>
        </div>
        
        <div class="nav-section">
            <div class="nav-section-title">Réservations</div>
            <ul class="nav-menu">
                <li class="nav-item">
                    <a href="/BackOffice/reservation/" class="nav-link">
                        <span class="nav-icon">📋</span>
                        <span>Accueil Réservations</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/BackOffice/reservation/formulaire" class="nav-link">
                        <span class="nav-icon">➕</span>
                        <span>Nouvelle Réservation</span>
                    </a>
                </li>
            </ul>
        </div>
        
        <div class="nav-section">
            <div class="nav-section-title">Véhicules</div>
            <ul class="nav-menu">
                <li class="nav-item">
                    <a href="/BackOffice/voiture/" class="nav-link">
                        <span class="nav-icon">🚗</span>
                        <span>Accueil Véhicules</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/BackOffice/voiture/liste" class="nav-link">
                        <span class="nav-icon">📋</span>
                        <span>Liste des Véhicules</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/BackOffice/voiture/formulaire" class="nav-link">
                        <span class="nav-icon">➕</span>
                        <span>Ajouter un Véhicule</span>
                    </a>
                </li>
            </ul>
        </div>

        <div class="nav-section">
            <div class="nav-section-title">Planification</div>
            <ul class="nav-menu">
                <li class="nav-item">
                    <a href="/BackOffice/planification/" class="nav-link">
                        <span class="nav-icon">🗓️</span>
                        <span>Lancer la planification</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/BackOffice/planification/liste?date=2026-03-05" class="nav-link">
                        <span class="nav-icon">📌</span>
                        <span>Réservations assignées</span>
                    </a>
                </li>
            </ul>
        </div>

        <div class="nav-section">
            <div class="nav-section-title">Référentiels</div>
            <ul class="nav-menu">
                <li class="nav-item">
                    <a href="/BackOffice/lieu/" class="nav-link">
                        <span class="nav-icon">📍</span>
                        <span>Lieux</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/BackOffice/distance/" class="nav-link">
                        <span class="nav-icon">📏</span>
                        <span>Distances</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="/BackOffice/hotel/" class="nav-link">
                        <span class="nav-icon">🏨</span>
                        <span>Hôtels</span>
                    </a>
                </li>
            </ul>
        </div>
        
        <div class="nav-section">
            <div class="nav-section-title">Système</div>
            <ul class="nav-menu">
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <span class="nav-icon">⚙️</span>
                        <span>Paramètres</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <span class="nav-icon">👤</span>
                        <span>Mon Profil</span>
                    </a>
                </li>
            </ul>
        </div>
    </nav>
</aside>
