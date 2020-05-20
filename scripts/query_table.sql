

-- SEQUENCE: public.query_id_seq

-- DROP SEQUENCE public.query_id_seq;

CREATE SEQUENCE public.query_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE public.query_id_seq
    OWNER TO postgres;


CREATE SEQUENCE public.resulteval_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE public.resulteval_id_seq
    OWNER TO postgres;


-- Table: public.query

-- DROP TABLE public.query;

CREATE TABLE public.query
(
    id integer NOT NULL,
    querytext character varying(300) COLLATE pg_catalog."default",
    ipaddress character varying(50) COLLATE pg_catalog."default",
    date timestamp without time zone,
    numberofcomposedanswers integer,
    CONSTRAINT query_pk PRIMARY KEY (id)
        USING INDEX TABLESPACE tablespaceso
)
WITH (
    OIDS = FALSE
)
TABLESPACE tablespaceso;

ALTER TABLE public.query
    OWNER to postgres;

-- Table: public.resultevaluation

-- DROP TABLE public.resultevaluation;

CREATE TABLE public.resultevaluation
(
    id integer NOT NULL,
    likertvalue integer,
    date timestamp without time zone,
    postsids text COLLATE pg_catalog."default",
    queryid integer,
    CONSTRAINT resulteval_pk PRIMARY KEY (id)
        USING INDEX TABLESPACE tablespaceso,
    CONSTRAINT queryidfk1 FOREIGN KEY (queryid)
        REFERENCES public.query (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE tablespaceso;

ALTER TABLE public.resultevaluation
    OWNER to postgres;
