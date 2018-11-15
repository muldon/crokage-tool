-- SEQUENCE: public.metricsresults_id_seq

-- DROP SEQUENCE public.metricsresults_id_seq;

CREATE SEQUENCE public.metricsresults_id_seq;

ALTER SEQUENCE public.metricsresults_id_seq
    OWNER TO postgres;
	
-- Table: public.metricsresults

--DROP TABLE public.metricsresults;

CREATE TABLE public.metricsresults
(
    id integer NOT NULL,
    hitk10 double precision,
    hitk5 double precision,
    hitk1 double precision,
    mrrk10 double precision,
    mrrk5 double precision,
    mrrk1 double precision,
    mapk10 double precision,
    mapk5 double precision,
    mapk1 double precision,
    mrk10 double precision,
    mrk5 double precision,
    mrk1 double precision,
    topsimilarquestionsnumber integer,
    topsimilaranswersnumber integer,
    usecodeinsimcalculus boolean,
    cutoff integer,
    obs text COLLATE pg_catalog."default",
    classfreqweight double precision,
    methodfreqweight double precision,
    repweight double precision,
    simweight double precision,
    upweight double precision,
    CONSTRAINT metricsresults_pkey PRIMARY KEY (id)
        USING INDEX TABLESPACE tablespaceso
)
WITH (
    OIDS = FALSE
)
TABLESPACE tablespaceso;

ALTER TABLE public.metricsresults
    OWNER to postgres;