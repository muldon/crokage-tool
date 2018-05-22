

--DROP SEQUENCE recallrate_id;
CREATE SEQUENCE recallrate_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;



--DROP SEQUENCE experiment_id;

CREATE SEQUENCE experiment_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;


--DROP TABLE experiment;


CREATE TABLE experiment
(
  id integer NOT NULL,
  tag character varying(50),
  numberoftestedquestions integer,
  date character varying(30),
  ttweight numeric(3,2),
  ccweight numeric(3,2),
  bbweight numeric(3,2),
  btweight numeric(3,2),
  topictopicweight numeric(3,2),
  tbweight numeric(3,2),
  aaweight numeric(3,2),
  bm25k numeric(3,2),
  bm25b numeric(3,2),
  observacao character varying(500),
  app character varying(100),
  base character varying(200),
  maxresultsize integer,
  lote integer,
  estimateWeights boolean,
  duration character varying(50),
  trm character varying(30),
  tagtagweight numeric(3,2),
  CONSTRAINT experiment_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE experiment
  OWNER TO postgres;




--DROP TABLE recallrate;
CREATE TABLE recallrate
(
  id integer NOT NULL,
  origem character varying(50),
  hits50000 integer,
  hits10000 integer,
  hits1000 integer,
  hits100 integer,
  hits50 integer,
  hits20 integer,
  hits10 integer,
  hits5 integer,
  hits1 integer,
  recallrate_50000 numeric(5,2),
  recallrate_10000 numeric(5,2),
  recallrate_1000 numeric(5,2),
  recallrate_100 numeric(5,2),
  recallrate_50 numeric(5,2),
  recallrate_20 numeric(5,2),
  recallrate_10 numeric(5,2),
  recallrate_5 numeric(5,2),
  recallrate_1 numeric(5,2),
  experiment_id integer,
  CONSTRAINT recall_pk PRIMARY KEY (id),
  CONSTRAINT experiment_fk FOREIGN KEY (experiment_id)
      REFERENCES experiment (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE recallrate
  OWNER TO postgres;




CREATE TABLE processedposts
(
  id integer NOT NULL,
  posttypeid integer NOT NULL,
  acceptedanswerid integer,
  parentid integer,
  creationdate timestamp without time zone NOT NULL,
  score integer,
  viewcount integer,
  body text,
  owneruserid integer,
  lasteditoruserid integer,
  lasteditordisplayname text,
  lasteditdate timestamp without time zone,
  lastactivitydate timestamp without time zone,
  title text,
  tags text,
  answercount integer,
  commentcount integer,
  favoritecount integer,
  closeddate timestamp without time zone,
  communityowneddate timestamp without time zone,
  tagssyn text,
  code text,
  CONSTRAINT processedposts_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE processedposts
  OWNER TO postgres;

-- Index: posts_accepted_answer_id_idx

-- DROP INDEX posts_accepted_answer_id_idx;

CREATE INDEX processedposts_accepted_answer_id_idx
  ON processedposts
  USING btree
  (acceptedanswerid)
  WITH (FILLFACTOR=100);

-- Index: processedposts_answer_count_idx

-- DROP INDEX processedposts_answer_count_idx;

CREATE INDEX processedposts_answer_count_idx
  ON processedposts
  USING btree
  (answercount)
  WITH (FILLFACTOR=100);

-- Index: processedposts_comment_count_idx

-- DROP INDEX processedposts_comment_count_idx;

CREATE INDEX processedposts_comment_count_idx
  ON processedposts
  USING btree
  (commentcount)
  WITH (FILLFACTOR=100);

-- Index: processedposts_creation_date_idx

-- DROP INDEX processedposts_creation_date_idx;

CREATE INDEX processedposts_creation_date_idx
  ON processedposts
  USING btree
  (creationdate)
  WITH (FILLFACTOR=100);

-- Index: processedposts_favorite_count_idx

-- DROP INDEX processedposts_favorite_count_idx;

CREATE INDEX processedposts_favorite_count_idx
  ON processedposts
  USING btree
  (favoritecount)
  WITH (FILLFACTOR=100);

-- Index: processedposts_owner_user_id_idx

-- DROP INDEX processedposts_owner_user_id_idx;

CREATE INDEX processedposts_owner_user_id_idx
  ON processedposts
  USING hash
  (owneruserid)
  WITH (FILLFACTOR=100);

-- Index: processedposts_parent_id_idx

-- DROP INDEX processedposts_parent_id_idx;

CREATE INDEX processedposts_parent_id_idx
  ON processedposts
  USING btree
  (parentid)
  WITH (FILLFACTOR=100);

-- Index: processedposts_post_type_id_idx

-- DROP INDEX processedposts_post_type_id_idx;

CREATE INDEX processedposts_post_type_id_idx
  ON processedposts
  USING btree
  (posttypeid)
  WITH (FILLFACTOR=100);

-- Index: processedposts_score_idx

-- DROP INDEX processedposts_score_idx;

CREATE INDEX processedposts_score_idx
  ON processedposts
  USING btree
  (score)
  WITH (FILLFACTOR=100);

-- Index: processedposts_viewcount_idx

-- DROP INDEX processedposts_viewcount_idx;

CREATE INDEX processedposts_viewcount_idx
  ON processedposts
  USING btree
  (viewcount)
  WITH (FILLFACTOR=100);








