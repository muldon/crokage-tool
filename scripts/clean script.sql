--level 1
delete from evaluation;

DROP SEQUENCE evaluation_id_seq;

CREATE SEQUENCE evaluation_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE evaluation_id_seq
  OWNER TO postgres;

--level 2
delete from rank;
delete from relatedpost;
delete from externalquestion;



DROP SEQUENCE rank_id_seq;

CREATE SEQUENCE rank_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE rank_id_seq
  OWNER TO postgres;


DROP SEQUENCE relatedpost_id_seq;

CREATE SEQUENCE relatedpost_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE relatedpost_id_seq
  OWNER TO postgres;

DROP SEQUENCE externalquestion_id_seq;

CREATE SEQUENCE externalquestion_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE externalquestion_id_seq
  OWNER TO postgres;




--select * from rank;
--select * from relatedpost;