--delete from processedposts;
delete from usersmin;
delete from postsmin;
delete from postlinksmin;
delete from commentsmin;

--java posts only
INSERT INTO postsmin
(select *
from posts p
where p.tags like '%java%' and p.tags not like '%javascript%');


--children of java posts 
INSERT INTO postsmin
(
select p.*
from posts p, postsmin pmin
where p.parentid = pmin.id
);

--select count(p.id) from postsmin p
--3800361

INSERT INTO commentsmin
(select c.*
from comments c, postsmin p
where c.postid = p.id 
);

--select count(c.id) from comments c
--64510258

--delete from postlinksmin

INSERT INTO postlinksmin
(
select pl.*
from postlinks pl, postsmin p
where pl.postid = p.id

union

select pl.*
from postlinks pl, postsmin p
where pl.relatedpostid = p.id
);

--select count(pl.id) from postlinks pl
--4879002

--delete from usersmin

--select count(u.id) from users u, postsmin p where p.owneruserid = u.id; 
--1397246

--select count(u.*) from users u, commentsmin c where c.userid = u.id
--3176012


insert into usersmin
(
select u.*
from users u, postsmin p
where p.owneruserid = u.id

union

select u.*
from users u, commentsmin c
where c.userid = u.id
);

--select count(u.*) from usersmin u
--729689

--select count(id) from postsmin
--3800361

--select count(id) from commentsmin
--6508911


--select count(id) from postlinksmin
--713246