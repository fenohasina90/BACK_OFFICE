-- Script SQL pour initialiser la base de données hotel_reservation
-- avec des données de test

-- Création des tables
DROP TABLE IF EXISTS reservation_planification CASCADE;
DROP TABLE IF EXISTS distance CASCADE;
DROP TABLE IF EXISTS parametre CASCADE;
DROP TABLE IF EXISTS voiture CASCADE;
DROP TABLE IF EXISTS token CASCADE;
DROP TABLE IF EXISTS reservation CASCADE;
DROP TABLE IF EXISTS hotel CASCADE;
DROP TABLE IF EXISTS lieu CASCADE;
DROP TABLE IF EXISTS client CASCADE;

-- Table client
CREATE TABLE client (
    id varchar PRIMARY KEY,
    nom varchar NOT NULL,
    prenom varchar NOT NULL,
    email varchar NOT NULL
);

CREATE TABLE lieu (
    id serial PRIMARY KEY,
    lieu varchar
);

-- Table hotel
CREATE TABLE hotel (
    id serial PRIMARY KEY,
    nom varchar NOT NULL,
    adresse varchar NOT NULL,
    ville varchar NOT NULL,
    pays varchar NOT NULL,
    id_lieu int REFERENCES lieu(id) NOT NULL
);

-- Table reservation
CREATE TABLE reservation (
    id serial PRIMARY KEY,
    date_reservation date NOT NULL, 
    heure_reservation time NOT NULL,
    nb_personnes int NOT NULL,
    id_client varchar NOT NULL,
    id_hotel int NOT NULL,
    FOREIGN KEY (id_client) REFERENCES client(id),
    FOREIGN KEY (id_hotel) REFERENCES hotel(id)
);

-- Table token
CREATE TABLE token (
    id serial PRIMARY KEY,
    reference varchar(100) UNIQUE NOT NULL,
    date_expiration timestamp NOT NULL
);

-- Table voiture
CREATE TABLE voiture (
    id serial PRIMARY KEY,
    type_carburant varchar(10) NOT NULL CHECK (type_carburant IN ('E', 'D', 'El', 'H')),
    nb_place int NOT NULL CHECK (nb_place > 0)
);

-- Table distance
CREATE TABLE distance (
    id serial PRIMARY KEY,
    from_lieu int REFERENCES lieu(id) NOT NULL,
    to_lieu int REFERENCES lieu(id) NOT NULL,
    distance_km double precision NOT NULL CHECK (distance_km >= 0)
);

-- Table parametre
CREATE TABLE parametre (
    id serial PRIMARY KEY,
    vitesse_moyenne_kmh double precision NOT NULL CHECK (vitesse_moyenne_kmh > 0),
    temps_attente_min int NOT NULL CHECK (temps_attente_min >= 0)
);

-- Table reservation_planification
CREATE TABLE reservation_planification (
    id serial PRIMARY KEY,
    id_reservation int NOT NULL UNIQUE,
    id_voiture int NOT NULL,
    date_planification timestamp NOT NULL DEFAULT now(),
    FOREIGN KEY (id_reservation) REFERENCES reservation(id) ON DELETE CASCADE,
    FOREIGN KEY (id_voiture) REFERENCES voiture(id)
);

-- Insertion de données de test

-- Clients
INSERT INTO client (id, nom, prenom, email) VALUES
('C001', 'Dupont', 'Jean', 'jean.dupont@email.com'),
('C002', 'Martin', 'Marie', 'marie.martin@email.com'),
('C003', 'Bernard', 'Pierre', 'pierre.bernard@email.com'),
('C004', 'Durand', 'Sophie', 'sophie.durand@email.com'),
('C005', 'Lefebvre', 'Thomas', 'thomas.lefebvre@email.com');

-- Hôtels
INSERT INTO lieu (lieu) VALUES
('Aeroport'),
('Paris'),
('Lyon'),
('Nice'),
('Marseille'),
('Grenoble');

INSERT INTO hotel (nom, adresse, ville, pays, id_lieu) VALUES
('Hôtel du Parc', '123 Avenue des Champs', 'Paris', 'France', 2),
('Grand Hôtel de Lyon', '456 Rue Principale', 'Lyon', 'France', 3),
('Hôtel de la Plage', '789 Boulevard de la Mer', 'Nice', 'France', 4),
('Palace de Marseille', '321 Vieux Port', 'Marseille', 'France', 5),
('Hôtel des Alpes', '654 Route de la Montagne', 'Grenoble', 'France', 6);

-- Tokens de test
INSERT INTO token (reference, date_expiration) VALUES
('TOKEN_TEST_2026', '2027-12-31 23:59:59'),
('TOKEN_ADMIN_2026', '2027-06-30 23:59:59'),
('TOKEN_DEV_2026', '2026-12-31 23:59:59');

-- Voitures de test
INSERT INTO voiture (type_carburant, nb_place) VALUES
('E', 5),
('D', 7),
('El', 4),
('H', 5),
('E', 2),
('D', 9);

-- Paramètres (exemple)
INSERT INTO parametre (vitesse_moyenne_kmh, temps_attente_min) VALUES
(45, 10);

-- Distances (exemple)
INSERT INTO distance (from_lieu, to_lieu, distance_km) VALUES
(1, 2, 12.5),
(1, 3, 8.2);

-- Réservations de test (pour tester la planification)
INSERT INTO reservation (date_reservation, heure_reservation, nb_personnes, id_client, id_hotel) VALUES
('2026-03-05', '10:00:00', 2, 'C001', 1),
('2026-03-05', '11:30:00', 4, 'C002', 2),
('2026-03-05', '09:15:00', 3, 'C001', 3),
('2026-03-06', '14:00:00', 1, 'C003', 4);

-- Affichage des données insérées
SELECT 'Clients insérés:' as info;
SELECT * FROM client;

SELECT 'Hôtels insérés:' as info;
SELECT * FROM hotel;

SELECT 'Voitures insérées:' as info;
SELECT * FROM voiture;

SELECT 'Base de données initialisée avec succès!' as resultat;
