

CREATE SEQUENCE public.query_id_seq;

ALTER SEQUENCE public.query_id_seq
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
