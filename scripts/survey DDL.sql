

DROP SEQUENCE surveyuser_id_seq;
DROP SEQUENCE evaluation_id_seq;
DROP SEQUENCE experiment_id_seq;
DROP SEQUENCE externalquestion_id_seq;
DROP SEQUENCE relatedpost_id_seq;
DROP SEQUENCE survey_id_seq;


DROP TABLE result;
DROP TABLE experiment;
DROP TABLE evaluation;
DROP TABLE surveyuser;
DROP TABLE relatedpost;
DROP TABLE externalquestion;
DROP TABLE survey;


CREATE SEQUENCE surveyuser_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE surveyuser_id_seq
  OWNER TO postgres;



CREATE SEQUENCE evaluation_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE evaluation_id_seq
  OWNER TO postgres;


-- Sequence: experiment_id_seq


CREATE SEQUENCE experiment_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE experiment_id_seq
  OWNER TO postgres;


-- Sequence: externalquestion_id_seq

CREATE SEQUENCE externalquestion_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE externalquestion_id_seq
  OWNER TO postgres;



-- Sequence: relatedpost_id_seq


CREATE SEQUENCE relatedpost_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE relatedpost_id_seq
  OWNER TO postgres;


-- Sequence: result_id_seq

DROP SEQUENCE result_id_seq;

CREATE SEQUENCE result_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE result_id_seq
  OWNER TO postgres;


-- Sequence: survey_id_seq



CREATE SEQUENCE survey_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 3
  CACHE 1;
ALTER TABLE survey_id_seq
  OWNER TO postgres;


-- Table: survey



CREATE TABLE survey
(
  id integer NOT NULL,
  name character varying(100),
  description character varying(500),
  active boolean,
  CONSTRAINT survey_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE survey
  OWNER TO postgres;


-- Table: surveyuser


CREATE TABLE surveyuser
(
  nick character varying(40),
  id integer NOT NULL,
  login character varying(40),
  CONSTRAINT survey_user_pk PRIMARY KEY (id),
  CONSTRAINT survey_user_unique UNIQUE (login)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE surveyuser
  OWNER TO postgres;


-- Table: externalquestion



CREATE TABLE externalquestion
(
  id integer NOT NULL,
  surveyid integer,
  googlequery text,
  classes character varying(100),
  userack boolean,
  answerbotqueryid integer,
  obs character varying(400),
  url character varying(800),
  externalid integer,
  rawquery text,
  CONSTRAINT externalquestion_pk PRIMARY KEY (id),
  CONSTRAINT survey_fk FOREIGN KEY (surveyid)
      REFERENCES survey (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE externalquestion
  OWNER TO postgres;


-- Table: relatedpost



CREATE TABLE relatedpost
(
  id integer NOT NULL,
  externalquestionid integer,
  postid integer,
  CONSTRAINT relatedpost_pk PRIMARY KEY (id),
  CONSTRAINT relatedpost_fk1 FOREIGN KEY (externalquestionid)
      REFERENCES externalquestion (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT relatedpost_fk2 FOREIGN KEY (postid)
      REFERENCES postsmin (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE relatedpost
  OWNER TO postgres;


-- Table: evaluation



CREATE TABLE evaluation
(
  id integer NOT NULL,
  externalquestionid integer,
  postid integer,
  surveyuserid integer,
  likertscale integer,
  ratingdate timestamp without time zone,
  internalevaluation boolean,
  CONSTRAINT evaluation_pk PRIMARY KEY (id),
  CONSTRAINT evaluation_fk1 FOREIGN KEY (externalquestionid)
      REFERENCES externalquestion (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT evaluation_fk2 FOREIGN KEY (postid)
      REFERENCES postsmin (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT evaluation_fk3 FOREIGN KEY (surveyuserid)
      REFERENCES surveyuser (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE evaluation
  OWNER TO postgres;


-- Table: experiment



CREATE TABLE experiment
(
  id integer NOT NULL,
  externalquestionid integer,
  alpha numeric(3,2),
  beta numeric(3,2),
  gamma numeric(3,2),
  delta numeric(3,2),
  epsilon numeric(3,2),
  date timestamp without time zone,
  duration character varying(40),
  obs character varying(200),
  CONSTRAINT experiment_pk PRIMARY KEY (id),
  CONSTRAINT experiment_fk1 FOREIGN KEY (externalquestionid)
      REFERENCES externalquestion (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE experiment
  OWNER TO postgres;



-- Table: result



CREATE TABLE result
(
  id integer NOT NULL,
  experimentid integer,
  recallrate1 numeric(4,3),
  recallrate5 numeric(4,3),
  recallrate10 numeric(4,3),
  mrr numeric(4,3),
  minlikertscale integer,
  obs character varying(200),
  internalevaluation boolean,
  CONSTRAINT result_pk PRIMARY KEY (id),
  CONSTRAINT result_fk1 FOREIGN KEY (experimentid)
      REFERENCES experiment (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE result
  OWNER TO postgres;



INSERT INTO survey(id,name, description, active) VALUES (1,'Internal Survey', 'This survey aims to evaluate default composer weights', true);
INSERT INTO survey(id,name, description, active) VALUES (2,'External Survey', 'This survey aims to evaluate the approach', false);

INSERT INTO surveyuser(id,nick, login) VALUES (1,'rodrigo', 'digao');
INSERT INTO surveyuser(id,nick, login) VALUES (2,'klerisson', 'klerisson');


