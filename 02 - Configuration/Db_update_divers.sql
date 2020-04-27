--Commandes SQL pour Ecole Prod

alter table ecoles.personne
    rename column typepersonne to type_personne ;
alter table ecoles.dossier
    add column evelin_link character varying(50) ;
alter table ecoles.dossier
    add column export integer DEFAULT 0 ;
alter table ecoles.dossier
    add column date_export date;


update ecoles.enfant
set export = 0
where date_export >= '2019-09-26';


select * from ecoles.enfant
where date_export >= '2019-09-26'
  and date_export <= '2019-10-24';

select ecole.id, ecole.est_primaire, ecole.est_pre_primaire
from ecoles.ecole
where est_primaire = 'Oui'
  and est_pre_primaire = 'Oui'
order by id;


Alter table ecoles.dossier

    add column date_import_bo timestamp without time zone,
    add column    date_export_bo timestamp without time zone,
    add column    date_integration timestamp without time zone;
