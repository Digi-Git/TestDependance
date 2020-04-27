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