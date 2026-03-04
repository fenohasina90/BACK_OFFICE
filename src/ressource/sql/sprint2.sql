-- Script SQL pour ajouter les tables token et voiture

-- Table token
CREATE TABLE IF NOT EXISTS token (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(100) UNIQUE NOT NULL,
    date_expiration TIMESTAMP NOT NULL
);

-- Table voiture
CREATE TABLE IF NOT EXISTS voiture (
    id SERIAL PRIMARY KEY,
    type_carburant VARCHAR(10) NOT NULL CHECK (type_carburant IN ('E', 'D', 'El', 'H')),
    nb_place INT NOT NULL CHECK (nb_place > 0)
);

-- Insertion de quelques tokens de test (valides pour 1 an)
INSERT INTO token (reference, date_expiration) VALUES
('TOKEN_TEST_2026', '2027-12-31 23:59:59'),
('TOKEN_ADMIN_2026', '2027-06-30 23:59:59'),
('TOKEN_DEV_2026', '2026-12-31 23:59:59');

-- Insertion de quelques voitures de test
INSERT INTO voiture (type_carburant, nb_place) VALUES
('E', 5),    -- Essence, 5 places
('D', 7),    -- Diesel, 7 places
('El', 4),   -- Electrique, 4 places
('H', 5),    -- Hybride, 5 places
('E', 2),    -- Essence, 2 places
('D', 9);    -- Diesel, 9 places

-- Affichage des données insérées
SELECT 'Tokens insérés:' as info;
SELECT * FROM token;

SELECT 'Voitures insérées:' as info;
SELECT * FROM voiture;

SELECT 'Tables créées avec succès!' as resultat;
