-- Table: postsmin

-- DROP TABLE postsmin;

CREATE TABLE postsmin
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
  CONSTRAINT postsmin_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE postsmin
  OWNER TO postgres;

-- Index: postsmin_accepted_answer_id_idx

-- DROP INDEX postsmin_accepted_answer_id_idx;

CREATE INDEX postsmin_accepted_answer_id_idx
  ON postsmin
  USING btree
  (acceptedanswerid)
  WITH (FILLFACTOR=100);

-- Index: postsmin_answer_count_idx

-- DROP INDEX postsmin_answer_count_idx;

CREATE INDEX postsmin_answer_count_idx
  ON postsmin
  USING btree
  (answercount)
  WITH (FILLFACTOR=100);

-- Index: postsmin_comment_count_idx

-- DROP INDEX postsmin_comment_count_idx;

CREATE INDEX postsmin_comment_count_idx
  ON postsmin
  USING btree
  (commentcount)
  WITH (FILLFACTOR=100);

-- Index: postsmin_creation_date_idx

-- DROP INDEX postsmin_creation_date_idx;

CREATE INDEX postsmin_creation_date_idx
  ON postsmin
  USING btree
  (creationdate)
  WITH (FILLFACTOR=100);

-- Index: postsmin_favorite_count_idx

-- DROP INDEX postsmin_favorite_count_idx;

CREATE INDEX postsmin_favorite_count_idx
  ON postsmin
  USING btree
  (favoritecount)
  WITH (FILLFACTOR=100);

-- Index: postsmin_owner_user_id_idx

-- DROP INDEX postsmin_owner_user_id_idx;

CREATE INDEX postsmin_owner_user_id_idx
  ON postsmin
  USING hash
  (owneruserid)
  WITH (FILLFACTOR=100);

-- Index: postsmin_parent_id_idx

-- DROP INDEX postsmin_parent_id_idx;

CREATE INDEX postsmin_parent_id_idx
  ON postsmin
  USING btree
  (parentid)
  WITH (FILLFACTOR=100);

-- Index: postsmin_post_type_id_idx

-- DROP INDEX postsmin_post_type_id_idx;

CREATE INDEX postsmin_post_type_id_idx
  ON postsmin
  USING btree
  (posttypeid)
  WITH (FILLFACTOR=100);

-- Index: postsmin_score_idx

-- DROP INDEX postsmin_score_idx;

CREATE INDEX postsmin_score_idx
  ON postsmin
  USING btree
  (score)
  WITH (FILLFACTOR=100);

-- Index: postsmin_viewcount_idx

-- DROP INDEX postsmin_viewcount_idx;

CREATE INDEX postsmin_viewcount_idx
  ON postsmin
  USING btree
  (viewcount)
  WITH (FILLFACTOR=100);





-- Table: comments

-- DROP TABLE comments;

CREATE TABLE commentsmin
(
  id integer NOT NULL,
  postid integer NOT NULL,
  score integer NOT NULL,
  text text,
  creationdate timestamp without time zone NOT NULL,
  userid integer,
  CONSTRAINT commentsmin_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE commentsmin
  OWNER TO postgres;

-- Index: cmntsmin_creation_date_idx

-- DROP INDEX cmntsmin_creation_date_idx;

CREATE INDEX cmntsmin_creation_date_idx
  ON commentsmin
  USING btree
  (creationdate)
  WITH (FILLFACTOR=100);

-- Index: cmntsmin_postid_idx

-- DROP INDEX cmntsmin_postid_idx;

CREATE INDEX cmntsmin_postid_idx
  ON commentsmin
  USING hash
  (postid)
  WITH (FILLFACTOR=100);

-- Index: cmntsmin_score_idx

-- DROP INDEX cmntsmin_score_idx;

CREATE INDEX cmntsmin_score_idx
  ON commentsmin
  USING btree
  (score)
  WITH (FILLFACTOR=100);

-- Index: cmntsmin_userid_idx

-- DROP INDEX cmntsmin_userid_idx;

CREATE INDEX cmntsmin_userid_idx
  ON commentsmin
  USING btree
  (userid)
  WITH (FILLFACTOR=100);











-- Table: postlinksmin

-- DROP TABLE postlinksmin;

CREATE TABLE postlinksmin
(
  id integer NOT NULL,
  creationdate timestamp without time zone NOT NULL,
  postid integer NOT NULL,
  relatedpostid integer NOT NULL,
  linktypeid integer NOT NULL,
  CONSTRAINT postlinksmin_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE postlinksmin
  OWNER TO postgres;

-- Index: postlinksmin_post_id_idx

-- DROP INDEX postlinksmin_post_id_idx;

CREATE INDEX postlinksmin_post_id_idx
  ON postlinksmin
  USING btree
  (postid)
  WITH (FILLFACTOR=100);

-- Index: postlinksmin_related_post_id_idx

-- DROP INDEX postlinksmin_related_post_id_idx;

CREATE INDEX postlinksmin_related_post_id_idx
  ON postlinksmin
  USING btree
  (relatedpostid)
  WITH (FILLFACTOR=100);




















-- Table: usersmin

-- DROP TABLE usersmin;

CREATE TABLE usersmin
(
  id integer NOT NULL,
  reputation integer NOT NULL,
  creationdate timestamp without time zone NOT NULL,
  displayname character varying(40) NOT NULL,
  lastaccessdate timestamp without time zone,
  websiteurl text,
  location text,
  aboutme text,
  views integer NOT NULL,
  upvotes integer NOT NULL,
  downvotes integer NOT NULL,
  profileimageurl text,
  age integer,
  accountid integer,
  CONSTRAINT usersmin_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE usersmin
  OWNER TO postgres;

-- Index: usermin_acc_id_idx

-- DROP INDEX usermin_acc_id_idx;

CREATE INDEX usermin_acc_id_idx
  ON usersmin
  USING hash
  (accountid)
  WITH (FILLFACTOR=100);

-- Index: usermin_created_at_idx

-- DROP INDEX usermin_created_at_idx;

CREATE INDEX usermin_created_at_idx
  ON usersmin
  USING btree
  (creationdate)
  WITH (FILLFACTOR=100);

-- Index: usermin_display_idx

-- DROP INDEX usermin_display_idx;

CREATE INDEX usermin_display_idx
  ON usersmin
  USING hash
  (displayname COLLATE pg_catalog."default")
  WITH (FILLFACTOR=100);

-- Index: usermin_down_votes_idx

-- DROP INDEX usermin_down_votes_idx;

CREATE INDEX usermin_down_votes_idx
  ON usersmin
  USING btree
  (downvotes)
  WITH (FILLFACTOR=100);

-- Index: usermin_up_votes_idx

-- DROP INDEX usermin_up_votes_idx;

CREATE INDEX usermin_up_votes_idx
  ON usersmin
  USING btree
  (upvotes)
  WITH (FILLFACTOR=100);






