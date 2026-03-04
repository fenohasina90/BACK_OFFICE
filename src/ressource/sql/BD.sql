
create table client (
    id varchar primary key,
    nom varchar not null,
    prenom varchar not null,
    email varchar not null
);

create table hotel (
    id serial primary key,
    nom varchar not null,
    adresse varchar not null,
    ville varchar not null,
    pays varchar not null
);

create table reservation (
    id serial primary key,
    date_reservation date not null, 
    nb_personnes int not null,
    id_client varchar not null,
    id_hotel int not null
);