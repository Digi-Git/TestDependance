--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.9
-- Dumped by pg_dump version 9.6.9

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;

--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET default_tablespace = '';

SET default_with_oids = false;

-- -----------------------------------------------------------------
-- LES SEQUENCES (création) ----------------------------------------
-- -----------------------------------------------------------------

--
-- Name: dossier_id_seq; Type: SEQUENCE; Schema: ecoles; Owner: -
--

CREATE SEQUENCE ecoles.dossier_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: personne_id_seq; Type: SEQUENCE; Schema: ecoles; Owner: -
--

CREATE SEQUENCE ecoles.personne_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: enfant_id_seq; Type: SEQUENCE; Schema: ecoles; Owner: -
--

CREATE SEQUENCE ecoles.enfant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: utilisateur_id_seq; Type: SEQUENCE; Schema: ecoles; Owner: -
--

CREATE SEQUENCE ecoles.utilisateur_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

	--
-- Name: profil_utilisateur_id_seq; Type: SEQUENCE; Schema: ecoles; Owner: -
--

CREATE SEQUENCE ecoles.profil_utilisateur_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


-- -----------------------------------------------------------------
-- LES TABLES ------------------------------------------------------
-- -----------------------------------------------------------------

--
-- Name: profil; Type: TABLE; Schema: ecoles; Owner: -
--

CREATE TABLE ecoles.profil (
    id integer NOT NULL,
    intitule character varying(50) NOT NULL
);

-- Clef principale
ALTER TABLE ONLY ecoles.profil ADD CONSTRAINT profil_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ecoles.profil ADD CONSTRAINT profil_intitule UNIQUE (intitule);

-- Commentaires
COMMENT ON TABLE ecoles.profil is 'Profil : type de profil utilisé pour la gestion des droits dans l''application. Attentiion à ne pas changer les ID par rapport à l''intitulé : seul des ajouts sont envisageables.';
COMMENT ON COLUMN ecoles.profil.id IS 'Id unique et clef';
COMMENT ON COLUMN ecoles.profil.intitule IS 'Libellé de l''intitulé';

--
-- Name: profil_utilisateur; Type: TABLE; Schema: ecoles; Owner: -
--

CREATE TABLE ecoles.profil_utilisateur (
    id integer NOT NULL,
    utilisateur_id integer NOT NULL,
    profil_id integer NOT NULL
);

-- Sequences
ALTER SEQUENCE ecoles.profil_utilisateur_id_seq OWNED BY ecoles.profil_utilisateur.id;
ALTER TABLE ONLY ecoles.profil_utilisateur ALTER COLUMN id SET DEFAULT nextval('ecoles.profil_utilisateur_id_seq'::regclass);

-- Clef principale
ALTER TABLE ONLY ecoles.profil_utilisateur ADD CONSTRAINT profil_utilisateur_pkey PRIMARY KEY (id);

-- Index
CREATE UNIQUE INDEX nodbl ON ecoles.profil_utilisateur USING btree (utilisateur_id ASC NULLS LAST, profil_id ASC NULLS LAST);

-- Commentaires
COMMENT ON TABLE ecoles.profil_utilisateur is 'Table pour la gestion des droits, l''association entre un utilisateur et un ou plusieurs profil. Cette table détermine si l''utilProfil : type de profil utilisé pour la gestion des droits dans l''application. Attentiion à ne pas changer les ID par rapport à l''utilisateur est un superviseur et/ou un administrateur, ...';
COMMENT ON COLUMN ecoles.profil_utilisateur.utilisateur_id IS 'Id d''un utilisateur';
COMMENT ON COLUMN ecoles.profil_utilisateur.profil_id IS 'Id d''un profil';

--
-- Name: utilisateur; Type: TABLE; Schema: ecoles; Owner: -
--

CREATE TABLE ecoles.utilisateur (
    id integer NOT NULL,
    identifiant character varying(50) NOT NULL,
    is_affecte integer NOT NULL,
    mot_de_passe character varying(60) NOT NULL,
    nom character varying(50),
    prenom character varying(50),
	parent_id integer NOT NULL default '-1'::integer
);

-- Sequences
ALTER SEQUENCE ecoles.utilisateur_id_seq OWNED BY ecoles.utilisateur.id;
ALTER TABLE ONLY ecoles.utilisateur ALTER COLUMN id SET DEFAULT nextval('ecoles.utilisateur_id_seq'::regclass);

-- Clef principale
ALTER TABLE ONLY ecoles.utilisateur ADD CONSTRAINT utilisateur_pkey PRIMARY KEY (id);

-- Indexe secondaire (anti-doublon sur l'identifiant)
ALTER TABLE ONLY ecoles.utilisateur ADD CONSTRAINT utilisateur_identifiant_pkey UNIQUE (identifiant);

-- Commentaires
COMMENT ON TABLE ecoles.utilisateur is 'Type de centre : hérité du modèle MALI, pour la ecoles, seul le type 1 est utilisé (BPEC). Ne pas supprimer cet ID 1';
COMMENT ON COLUMN ecoles.utilisateur.id IS 'Id unique et clef';
COMMENT ON COLUMN ecoles.utilisateur.identifiant IS 'Correspondant à au login. Cet identifiant doit être unique pour la table des utilisateurs. Remappé en login dans l''entité correspondante (compatibilité système d''authentication de spring).';
COMMENT ON COLUMN ecoles.utilisateur.is_affecte IS 'Pour le module web de gestion des tournées. Vaut 1 si l''utilisateur est associé à une tournée, sinon 0. A noter qu''utilisé uniquement pour les utilisateurs de type agent. Non Utilisé pour la ecoles.';
COMMENT ON COLUMN ecoles.utilisateur.mot_de_passe IS 'Mot de passe crypté via Spring securty.';
COMMENT ON COLUMN ecoles.utilisateur.nom IS 'Nom de l''utilisateur. Remappé en lastname dans l''entité correspondante (compatibilité système d''authentication de spring).';
COMMENT ON COLUMN ecoles.utilisateur.prenom IS 'Prenom de l''utilisateur. Remappé en firstname dans l''entité correspondante (compatibilité système d''authentication de spring).';
COMMENT ON COLUMN ecoles.utilisateur.parent_id IS 'Pour le module web de gestion des tournées et pour les utilisateurs de type agent afin d''associer agent et superviseur. Correspond donc à l''ID utilisateur d''un superviseur ou -1 si pas de lien. Non utilisé pour la ecoles.';

--
-- Name: localisation; Type: TABLE; Schema: ecoles; Owner: -
--

CREATE TABLE ecoles.localisation (
    id integer NOT NULL,
    niv_1 character varying(50),
    niv_2 character varying(50),
    niv_3 character varying(50),
    niv_4 character varying(50),
    niv_5 character varying(50),
    niv_6 character varying(50),
    niv_7 character varying(50),
    niv_8 character varying(50),
    filtre_1 character varying(50),
    filtre_2 character varying(50),
    filtre_3 character varying(50),
    filtre_4 character varying(50),
    filtre_5 character varying(50),
    type_entite smallint DEFAULT 0 NOT NULL
);

-- Clef principale
ALTER TABLE ONLY ecoles.localisation ADD CONSTRAINT pk_localisation PRIMARY KEY (id);

-- Commentaires
COMMENT ON TABLE ecoles.localisation is 'Localisation : la découpe en entité territoriale ecoles';
COMMENT ON COLUMN ecoles.localisation.id IS 'Id unique et clef';
COMMENT ON COLUMN ecoles.localisation.niv_1 IS 'Province';
COMMENT ON COLUMN ecoles.localisation.niv_2 IS 'Ville si type_enttite = 0 ou Territoire si type_entite = 1';
COMMENT ON COLUMN ecoles.localisation.niv_3 IS 'Commune urbaine si type_enttite = 0 ou commune rurale si type_entite = 1';
COMMENT ON COLUMN ecoles.localisation.niv_4 IS 'inutilisé';
COMMENT ON COLUMN ecoles.localisation.niv_5 IS 'inutilisé';
COMMENT ON COLUMN ecoles.localisation.niv_6 IS 'inutilisé';
COMMENT ON COLUMN ecoles.localisation.niv_7 IS 'inutilisé';
COMMENT ON COLUMN ecoles.localisation.niv_8 IS 'inutilisé';
COMMENT ON COLUMN ecoles.localisation.filtre_1 IS 'Code province';
COMMENT ON COLUMN ecoles.localisation.filtre_2 IS 'Optionnel : code ville (type_entite=0) ou territoire (type_entite=1)';
COMMENT ON COLUMN ecoles.localisation.filtre_3 IS 'Optionnel : code commune urbaine (type_entite=0) ou code commune rurale (type_entite=1)';
COMMENT ON COLUMN ecoles.localisation.filtre_4 IS 'inutilisé';
COMMENT ON COLUMN ecoles.localisation.filtre_5 IS 'inutilisé';
COMMENT ON COLUMN ecoles.localisation.type_entite IS 'Type d''entité territoriale : 0 si ville (urbaine) ou 1 si rurale';

--
-- Name: educationnelle; Type: TABLE; Schema: ecoles; Owner: -
--

CREATE TABLE ecoles.educationnelle (
    id integer NOT NULL,
    province character varying(50),
    sous_division character varying(50)
);

-- Clef principale
ALTER TABLE ONLY ecoles.educationnelle ADD CONSTRAINT pk_educationnelle PRIMARY KEY (id);

-- Commentaires
COMMENT ON TABLE ecoles.educationnelle is 'Educationnelle : province educationnelle de l''école';
COMMENT ON COLUMN ecoles.educationnelle.id IS 'Id unique et clef';
COMMENT ON COLUMN ecoles.educationnelle.province IS 'Province educationnelle';
COMMENT ON COLUMN ecoles.educationnelle.sous_division IS 'Sous division educationnelle';

--
-- Name: centre; Type: TABLE; Schema: ecoles; Owner: -
--

CREATE TABLE ecoles.ecole (
    id integer NOT NULL,
    nom character varying(100),
    district character varying(100),
    quartier character varying(100),
    latitude double precision,
    longitude double precision,
    utilisateur_super_id integer,
    localisation_id integer DEFAULT 0,
	localisation_autre character varying(100) default '',
	educationnelle_id integer DEFAULT 0,
	lastversion integer NOT NULL DEFAULT 0,
	date_creation_bo timestamp without time zone,
    date_import_bo timestamp without time zone,
    date_export_bo timestamp without time zone,
    date_integration timestamp without time zone,
    est_pre_primaire character varying(10) default '',
    est_primaire character varying(10) default '',
    nbEnfantSansActe integer DEFAULT 0,
	type_ecole character varying(50) default '',
	unic_name character varying(100) COLLATE pg_catalog."default"
);

-- Clef principale
ALTER TABLE ONLY ecoles.ecole ADD CONSTRAINT ecole_pkey PRIMARY KEY (id);

-- fk - pour une localisation standard.
ALTER TABLE ONLY ecoles.ecole ADD CONSTRAINT fk_ecole_localisation_id FOREIGN KEY (localisation_id) REFERENCES ecoles.localisation(id);

-- fk - pour province educationnelle.
ALTER TABLE ONLY ecoles.ecole ADD CONSTRAINT fk_ecole_educationnelle_id FOREIGN KEY (educationnelle_id) REFERENCES ecoles.educationnelle(id);

-- fk - sur l'utilisateur superviseur
ALTER TABLE ONLY ecoles.ecole ADD CONSTRAINT fk_centre_superviseur FOREIGN KEY (utilisateur_super_id) REFERENCES ecoles.utilisateur(id);

-- Commentaires
COMMENT ON TABLE ecoles.ecole is 'Ecole : correspond à l''entité de collecte.';
COMMENT ON COLUMN ecoles.ecole.id IS 'Id unique et clef. Cet ID, fourni par le JSON (idbo) est unique et correspond obligatoireement à une entrée dans la liste (ecoles.txt)';
COMMENT ON COLUMN ecoles.ecole.nom IS 'Nom de l''école';
COMMENT ON COLUMN ecoles.ecole.district IS 'District';
COMMENT ON COLUMN ecoles.ecole.quartier IS 'Quartier';
COMMENT ON COLUMN ecoles.ecole.latitude IS 'Latitude ou NULL si non connu';
COMMENT ON COLUMN ecoles.ecole.longitude IS 'Longitude ou NULL si non connu';
COMMENT ON COLUMN ecoles.ecole.utilisateur_super_id IS 'Id de l''utilisateur superviseur qui est responsable des tournées pour l''école.';
COMMENT ON COLUMN ecoles.ecole.localisation_id IS 'Id de localisation pour indiquer l''entité territoriale. 0 si non connu.';
COMMENT ON COLUMN ecoles.ecole.localisation_autre IS 'Complément information localisation';
COMMENT ON COLUMN ecoles.ecole.educationnelle_id IS 'Id de localisation pour indiquer l''entité territoriale. 0 si non connu.';
COMMENT ON COLUMN ecoles.ecole.lastversion IS '1 si la derniere version de l''ecole, sinon 0';
COMMENT ON COLUMN ecoles.ecole.date_creation_bo IS 'Date de création du dossier dans le BO';
COMMENT ON COLUMN ecoles.ecole.date_import_bo IS 'Date de l''import tablette vers le BO';
COMMENT ON COLUMN ecoles.ecole.date_export_bo IS 'Date de l''export depuis le BO';
COMMENT ON COLUMN ecoles.ecole.date_integration IS 'Date d''intégration de l''export dans la base centrale';
COMMENT ON COLUMN ecoles.ecole.est_pre_primaire IS 'Oui/Non si l''école est une maternelle';
COMMENT ON COLUMN ecoles.ecole.est_primaire IS 'Oui/Non si l''école est une primaire';
COMMENT ON COLUMN ecoles.ecole.unic_name IS 'Version du nom du l''école recalculée (sans caracteres spéciaux, espace, en majuscule) pour limiter les doublons de noms des écoles.';

--
-- Name: tournee; Type: TABLE; Schema: ecoles; Owner: -
--

CREATE TABLE ecoles.dossier (
	id integer NOT NULL,
	ecole_id integer NOT NULL,
	no_dossier character varying(15),
	date_creation date,
	date_signature date,
	nom_prenom_utilisateur_agent character varying(255),
	commentaire character varying(255),
	nom_dossier_source character varying(100),
    date_creation_bo timestamp without time zone,
    date_import_bo timestamp without time zone,
    date_export_bo timestamp without time zone,
    date_integration timestamp without time zone,
    evelin_link character varying(50),
    export integer DEFAULT 0,
    date_export date
);

-- Sequence
ALTER SEQUENCE ecoles.dossier_id_seq OWNED BY ecoles.dossier.id;
ALTER TABLE ONLY ecoles.dossier ALTER COLUMN id SET DEFAULT nextval('ecoles.dossier_id_seq'::regclass);

-- Clef principale
ALTER TABLE ONLY ecoles.dossier ADD CONSTRAINT dossier_pkey PRIMARY KEY (id);

-- fk - sur l'école
ALTER TABLE ONLY ecoles.dossier ADD CONSTRAINT fk_dossier_ecole FOREIGN KEY (ecole_id) REFERENCES ecoles.ecole(id);

-- Commentaires
COMMENT ON TABLE ecoles.dossier is 'Dossier : racine du dossier famille, correspond à la partie identification du formulaire papier.';
COMMENT ON COLUMN ecoles.dossier.id IS 'Id unique et clef';
COMMENT ON COLUMN ecoles.dossier.ecole_id IS 'Référence à l''école';
COMMENT ON COLUMN ecoles.dossier.no_dossier IS 'Numero du dossier identification - numero unique cross ecole';
COMMENT ON COLUMN ecoles.dossier.date_creation IS 'Date du formulaire (inscrite)';
COMMENT ON COLUMN ecoles.dossier.date_signature IS 'Date de signature - inutilisé';
COMMENT ON COLUMN ecoles.dossier.nom_prenom_utilisateur_agent IS 'Agent ayant collecté l''information';
COMMENT ON COLUMN ecoles.dossier.commentaire IS 'Commentaires';
COMMENT ON COLUMN ecoles.dossier.nom_dossier_source IS 'Nom de dossier à concaténer avec le chemin par défaut de stockage indiqué dans development-release.properties "file.upload.dir", ce chemin indique ou se trouve le dossier zip d''envoi depuis le backoffice pc';
COMMENT ON COLUMN ecoles.dossier.evelin_link IS 'ID externe de l''acte de type ordonnance (fratrie dans un seul acte, limité à 5 enfants) dans Evelin-Cityweb.';
COMMENT ON COLUMN ecoles.dossier.export IS 'Si 1, le dossier a été exporté vers Evelin, sinon 0';
COMMENT ON COLUMN ecoles.dossier.date_export IS 'Date de l''export vers Evelin, sinon null';
COMMENT ON COLUMN ecoles.dossier.date_creation_bo IS 'Date de création du dossier dans le BO';
COMMENT ON COLUMN ecoles.dossier.date_import_bo IS 'Date de l''import tablette vers le BO';
COMMENT ON COLUMN ecoles.dossier.date_export_bo IS 'Date de l''export depuis le BO';
COMMENT ON COLUMN ecoles.dossier.date_integration IS 'Date d''intégration de l''export dans la base centrale';
-- Name: equipement; Type: TABLE; Schema: ecoles; Owner: -
--

CREATE TABLE ecoles.personne (
  id integer NOT NULL,
	type_personne character varying(50),
	qualite character varying(50) default '',
	qualite_autre character varying(50) default '',
	nom character varying(255) default '',
	postnom character varying(255) default '',
	prenom character varying(255) default '',
	genre character varying(20) default '',
	localisation_naissance_id integer DEFAULT 0,
	naissance_autre character varying(50) default '',
	date_naissance character varying(10),
	profession character varying(100),
	profession_autre character varying(100),
	nationalite character varying(50),
	nationalite_autre character varying(50),
	numero character varying(50),
	avenue character varying(50),
	quartier character varying(50),
	localisation_residence_id integer DEFAULT 0,
	residence_autre character varying(50) default '',
	tel character varying(50) default '',
	email character varying(100) default '',
	dossier_id integer NOT null
);

-- Sequences
ALTER SEQUENCE ecoles.personne_id_seq OWNED BY ecoles.personne.id;
ALTER TABLE ONLY ecoles.personne ALTER COLUMN id SET DEFAULT nextval('ecoles.personne_id_seq'::regclass);

-- Clef principale
ALTER TABLE ONLY ecoles.personne ADD CONSTRAINT personne_pkey PRIMARY KEY (id);

-- fk - sur le dossier.
ALTER TABLE ONLY ecoles.personne ADD CONSTRAINT fk_personne_dossier FOREIGN KEY (dossier_id) REFERENCES ecoles.dossier(id);

-- fk - sur l'ID de localisation de naissance.
ALTER TABLE ONLY ecoles.personne ADD CONSTRAINT fk_personne_naissance FOREIGN KEY (localisation_naissance_id) REFERENCES ecoles.localisation(id);

-- fk - sur l'ID de localisation de résidence.
ALTER TABLE ONLY ecoles.personne ADD CONSTRAINT fk_personne_residence FOREIGN KEY (localisation_residence_id) REFERENCES ecoles.localisation(id);

-- Commentaires
COMMENT ON TABLE ecoles.personne is 'Personne : personnes(s), c.a.d, père, mère, témoins, demandeur du dossier.';
COMMENT ON COLUMN ecoles.personne.id IS 'Id unique et clef';
COMMENT ON COLUMN ecoles.personne.type_personne IS 'Liste demandeur, père, mère, temoin 1, témoin 2 issu de type_personne';
COMMENT ON COLUMN ecoles.personne.qualite IS 'Qualité pour la personne (pour le demandeur uniquement).';
COMMENT ON COLUMN ecoles.personne.qualite_autre IS 'Saisie qualite si choix autre.';
COMMENT ON COLUMN ecoles.personne.nom IS 'Nom de la personne.';
COMMENT ON COLUMN ecoles.personne.postnom IS 'Post nom de la personne.';
COMMENT ON COLUMN ecoles.personne.prenom IS 'Prénom.';
COMMENT ON COLUMN ecoles.personne.localisation_naissance_id IS 'Id pour la localisation de naissance.';
COMMENT ON COLUMN ecoles.personne.naissance_autre IS 'Complément information de naissance.';
COMMENT ON COLUMN ecoles.personne.date_naissance IS 'Sous la forme de chaine AAAA/MM/JJ et avec 00 possible pour le jour et le mois.';
COMMENT ON COLUMN ecoles.personne.profession IS 'profession isssu de type_profession.';
COMMENT ON COLUMN ecoles.personne.profession_autre IS 'profession isssu de type_profession.';
COMMENT ON COLUMN ecoles.personne.nationalite IS 'Nationalité issue de type_nationalité.';
COMMENT ON COLUMN ecoles.personne.nationalite_autre IS 'Nationalité issue de type_nationalité.';
COMMENT ON COLUMN ecoles.personne.numero IS 'Numero dans le lieu de résidence.';
COMMENT ON COLUMN ecoles.personne.avenue IS '1ere ligne d''adresse.';
COMMENT ON COLUMN ecoles.personne.quartier IS 'Quartier.';
COMMENT ON COLUMN ecoles.personne.localisation_residence_id IS 'Id pour la localisation de naissance.';
COMMENT ON COLUMN ecoles.personne.residence_autre IS 'Complément information de résidence.';
COMMENT ON COLUMN ecoles.personne.tel IS 'Téléphone.';
COMMENT ON COLUMN ecoles.personne.email IS 'email.';
COMMENT ON COLUMN ecoles.personne.dossier_id IS 'Jonction avec la table dossier';

--
-- Name: financementext; Type: TABLE; Schema: ecoles; Owner: -
--

CREATE TABLE ecoles.enfant (
  id integer NOT NULL,
	nom character varying(255) default '',
	postnom character varying(255) default '',
	prenom character varying(255) default '',
	genre character varying(50) default '',
	localisation_naissance_id integer DEFAULT 0,
	naissance_autre character varying(50) default '',
	date_naissance character varying(10),
	heure_naissance character varying(10),
	scolarise character varying(10),
	dossier_id integer NOT null,
	evelin_link character varying(50),
	export integer DEFAULT 0,
	date_export date,
	type_personne character varying(20)
);

ALTER SEQUENCE ecoles.enfant_id_seq OWNED BY ecoles.enfant.id;
ALTER TABLE ONLY ecoles.enfant ALTER COLUMN id SET DEFAULT nextval('ecoles.enfant_id_seq'::regclass);

-- Clef principale
ALTER TABLE ONLY ecoles.enfant ADD CONSTRAINT enfant_pkey PRIMARY KEY (id);

-- fk - sur le dossier.
ALTER TABLE ONLY ecoles.enfant ADD CONSTRAINT fk_enfant_dossier FOREIGN KEY (dossier_id) REFERENCES ecoles.dossier(id);

-- fk - sur l'ID de localisation de naissance.
ALTER TABLE ONLY ecoles.enfant ADD CONSTRAINT fk_enfant_naissance FOREIGN KEY (localisation_naissance_id) REFERENCES ecoles.localisation(id);

-- Commentaires
COMMENT ON TABLE ecoles.enfant is 'Enfant : enfant(s) lié(s) au dossier.';
COMMENT ON COLUMN ecoles.enfant.id IS 'Id unique et clef';
COMMENT ON COLUMN ecoles.enfant.nom IS 'Nom de l''enfant';
COMMENT ON COLUMN ecoles.enfant.postnom IS 'PostNom de l''enfant';
COMMENT ON COLUMN ecoles.enfant.prenom IS 'Prénom(s) de l''enfant';
COMMENT ON COLUMN ecoles.enfant.genre IS 'genre (masculin/féminin) issu type_sexe';
COMMENT ON COLUMN ecoles.enfant.localisation_naissance_id IS 'Lieu de naissance';
COMMENT ON COLUMN ecoles.enfant.naissance_autre IS 'Complément lieu de naissance';
COMMENT ON COLUMN ecoles.enfant.date_naissance IS 'Date de naissance, format AAAA/MM/JJ avec possibilité de AAAA/00/00';
COMMENT ON COLUMN ecoles.enfant.heure_naissance IS 'Heure de naissance, format HH:MM.';
COMMENT ON COLUMN ecoles.enfant.scolarise IS 'Enfant scolarisé (oui/non)';
COMMENT ON COLUMN ecoles.enfant.dossier_id IS 'Jonction avec la table des dossiers';
COMMENT ON COLUMN ecoles.enfant.evelin_link IS 'ID externe de l''acte dans Evelin-Cityweb.';
COMMENT ON COLUMN ecoles.enfant.export IS 'Si 1, le dossier a été exporté vers Evelin, sinon 0';
COMMENT ON COLUMN ecoles.enfant.date_export IS 'Date de l''export vers Evelin, sinon null';

-- -----------------------------------------------------------------
-- Autres éléments post-création
-- -----------------------------------------------------------------

-- fk - relation profil_utilisateur avec utilisateur.
ALTER TABLE ONLY ecoles.profil_utilisateur ADD CONSTRAINT fk_profil_utilisateur_uti FOREIGN KEY (utilisateur_id) REFERENCES ecoles.utilisateur(id);

-- fk - relation profil_utilisateur avec profil
ALTER TABLE ONLY ecoles.profil_utilisateur ADD CONSTRAINT fk_profil_utilisateur_pro FOREIGN KEY (profil_id) REFERENCES ecoles.profil(id);

CREATE SEQUENCE ecoles.mail_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE ecoles.mail (
                             id integer NOT NULL primary key ,
                             mail_id varchar NOT NULL ,
                             attachment_id varchar NOT NULL unique ,
                             date_import varchar (255)
);

ALTER SEQUENCE ecoles.mail_id_seq OWNED BY ecoles.mail.id;
ALTER TABLE ONLY ecoles.mail ALTER COLUMN id SET DEFAULT nextval('ecoles.mail_id_seq'::regclass);
-- -----------------------------------------------------------------
-- Mémo commentaires
-- -----------------------------------------------------------------

-- Sequences
-- Clef principale
-- Indexe secondaire (anti-doublon sur le nom du centre)
-- fk - sur le type de centre.
-- Name: financementext financementext_pkey; Type: CONSTRAINT; Schema: ecoles; Owner: -


