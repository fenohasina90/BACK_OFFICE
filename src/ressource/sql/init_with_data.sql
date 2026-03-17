-- Script SQL pour initialiser la base de données hotel_reservation
-- avec des données de test

-- Création des tables
DROP TABLE IF EXISTS voyage_stop CASCADE;
DROP TABLE IF EXISTS voyage CASCADE;
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

CREATE TABLE voyage (
    id serial PRIMARY KEY,
    date_voyage date NOT NULL,
    heure_depart time NOT NULL,
    id_voiture int NOT NULL,
    duree_minutes int NOT NULL CHECK (duree_minutes >= 0),
    date_creation timestamp NOT NULL DEFAULT now(),
    FOREIGN KEY (id_voiture) REFERENCES voiture(id)
);

CREATE TABLE voyage_stop (
    id serial PRIMARY KEY,
    id_voyage int NOT NULL,
    ordre int NOT NULL CHECK (ordre > 0),
    id_reservation int NOT NULL,
    id_lieu_destination int NOT NULL,
    distance_km double precision NOT NULL CHECK (distance_km >= 0),
    FOREIGN KEY (id_voyage) REFERENCES voyage(id) ON DELETE CASCADE,
    FOREIGN KEY (id_reservation) REFERENCES reservation(id) ON DELETE CASCADE,
    FOREIGN KEY (id_lieu_destination) REFERENCES lieu(id),
    UNIQUE (id_voyage, ordre)
);

-- Insertion de données de test

-- Clients
INSERT INTO client (id, nom, prenom, email) VALUES
('C006', 'Kot', 'Jean', 'jean.dupont@email.com'),
('C007', 'Justine', 'Marie', 'marie.martin@email.com'),
('C008', 'Balita', 'Pierre', 'pierre.bernard@email.com'),
('C009', 'Randria', 'Sophie', 'sophie.durand@email.com'),
('C010', 'Bema', 'Thomas', 'thomas.lefebvre@email.com');

-- Tokens de test
INSERT INTO token (reference, date_expiration) VALUES
('TOKEN_TEST_2026', '2027-12-31 23:59:59'),
('TOKEN_ADMIN_2026', '2027-06-30 23:59:59'),
('TOKEN_DEV_2026', '2026-12-31 23:59:59');

-- Voitures de test

-- Paramètres (exemple)
INSERT INTO parametre (vitesse_moyenne_kmh, temps_attente_min) VALUES
(45, 30);

-- Distances (exemple)


-- Réservations de test (pour tester la planification)

-- Affichage des données insérées
SELECT 'Clients insérés:' as info;
SELECT * FROM client;

SELECT 'Hôtels insérés:' as info;
SELECT * FROM hotel;

SELECT 'Voitures insérées:' as info;
SELECT * FROM voiture;

SELECT 'Base de données initialisée avec succès!' as resultat;
