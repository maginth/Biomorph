SET NAMES utf8;


DROP TABLE IF EXISTS Biomorph;
DROP TABLE IF EXISTS Utilisateur;
DROP TABLE IF EXISTS Note;



CREATE TABLE Biomorph (
  id_user int unsigned NOT NULL,
  id int unsigned NOT NULL AUTO_INCREMENT,
  nb_chargement int unsigned,
  nb_note int unsigned,
  note decimal(1,1) KEY unsigned DEFAULT NULL,
  nom varchar(50) KEY NOT NULL,
  date_insertion datetime DEFAULT NULL,
  commentaire TEXT,
  _public tinyint(1) DEFAULT 0,
  _data blob NOT NULL,
  PRIMARY KEY (id_createur,id)
) ENGINE=InnoDB AUTO_INCREMENT=1;

CREATE TABLE Utilisateur (
  id_user int unsigned NOT NULL AUTO_INCREMENT,
  nom varchar(50) NOT NULL,
  cpw MEDIUMINT NOT NULL,
  h_pw TINYBLOB,
  temp_pw TINYBLOB,
  email varchar(100),
  octet_restant int unsigned DEFAULT 10000000,
  PRIMARY KEY (id_createur,nom)
) ENGINE=InnoDB AUTO_INCREMENT=1;

CREATE TABLE Note (
  id_user int unsigned NOT NULL,
  id int unsigned NOT NULL,
  note decimal(1,1) unsigned NOT NULL,
  PRIMARY KEY (id_user,id),
  INDEX (id_user,note)
);
