--getQuestionsIdsByFilters
select count(id) from posts  p  
WHERE   p.posttypeId = 1 
and tags like '%ruby%'

--getQuestionsByFilters original
select * from posts  p  
WHERE   p.posttypeId = 1  
and p.creationdate < '2011-10-01'   
order by p.creationdate 


--findClosedDuplicatedNonMasters
select * from posts  p  
WHERE   p.posttypeId = 1 
and p.closeddate is not null
and p.creationdate < '2011-10-01'   
and p.id in 
( select distinct(pl.postid)
from postlinks pl where pl.linktypeid = 3 ) 
order by p.creationdate 
--limit 1528

--preprocess.getDuplicatedQuestions
select * from posts  p  
WHERE   p.posttypeId = 1 
and p.id in 
( 
select distinct(pl.postid)
from postlinks pl where pl.linktypeid = 3

union 
select distinct(pl.relatedpostid)
from postlinks pl where pl.linktypeid = 3 ) 
--494956
limit 100



select distinct(pl.postid)
from postlinks pl where pl.linktypeid = 3
--429887
union 
select distinct(pl.relatedpostid)
from postlinks pl where pl.linktypeid = 3
--218957



select *  from 
postsmin p 
--where p.posttypeid = 1
--and tags like '%html%'
--and creationdate < '2014-08-01'
--2014: 397376
--2017: 384069

--order by creationdate desc
limit 10000

select count(id)
from postsmin


select * from posts  p  
WHERE   p.posttypeId = 1  
and p.creationdate < '2011-10-01'   
order by p.creationdate 
limit 1541


select * from posts  p  
WHERE   p.posttypeId = 1 
and tags like '%ruby%'
limit 100



select * from posts  p  
WHERE   p.posttypeId = 1 
--and p.closeddate is not null
and p.creationdate < '2011-10-01'   
and p.id in 
( select distinct(pl.postid)
from postlinks pl where pl.linktypeid = 3 ) 
--order by p.creationdate 
and p.id = 59


--delete from topicvector 
select * from topicvector where postid = 73713
limit 100



select * from posts  p  
WHERE   p.posttypeId = 1 
and p.closeddate is not null
and p.creationdate < '2011-10-01'   
and p.id = 17364127



select *
from postlinks pl 
where pl.linktypeid = 3
and pl.postid in 
(
select p.id from posts  p  
WHERE   p.posttypeId = 1 
and p.closeddate is not null
and p.creationdate < '2011-10-01' 
)
limit 5



--número de questões antes de 2011, data original
select count(id) from posts  p  
WHERE   p.posttypeId = 1  
and p.creationdate < '2017-10-01'   
--2011 - 1993483
--2012 - 3531422
--2013 - 5509318
--2014 - 7712606 
--2015 - 9900984
--2016 - 12340129
--2017 - 13472796






--findClosedDuplicatedNonMasters
select count(id) from posts  p  
WHERE   p.posttypeId = 1 
and p.closeddate is not null
and p.creationdate < '2017-10-01'   
and p.id in 
( select distinct(pl.postid)
from postlinks pl where pl.linktypeid = 3 ) 
--2011 - 22864 
--2012 - 44510
--2013 - 86861
--2014 - 134261 
--2015 - 183448
--2016 - 256706
--2017 - 293989



--verificando se todas as repetidas estao nesse espaco amostral



--getQuestionsByFilters 
select count(id) from posts  p  
WHERE   p.posttypeId = 1  
and p.creationdate < '2014-10-01'   
--order by p.creationdate 
--7712606  --2014
--9900984  --2015
--12340129 --2016 



--findClosedDuplicatedNonMasters
select count(id) from posts  p  
WHERE   p.posttypeId = 1 
and p.closeddate is not null
and p.creationdate < '2014-10-01'   
and p.id in 
( select distinct(pl.postid)
from postlinks pl where pl.linktypeid = 3 ) 
--order by p.creationdate 
--134261 --2014
--183448 --2015
--256706 --2016


--masters
select distinct(pl.relatedpostid) 
from postlinks pl where pl.linktypeid = 3
and pl.postid in 
(
select id from posts  p  
WHERE   p.posttypeId = 1 
and p.closeddate is not null
and p.id in 
( select distinct(pl.postid)
from postlinks pl where pl.linktypeid = 3 ) 
)


select * from posts p  
WHERE p.closeddate is not null
and p.tags like '%<swing>%'
and trim(p.body)!='' 
and p.id in + 
 ( select pl.postid + 
 from postlinks pl + 
 where pl.linktypeid = 3 + 
 and pl.relatedpostid in  +
   (select id from processedposts p   + 
   WHERE  p.posttypeId = 1
    and tags like '%<swing>%'	
    and trim(p.body)!='' + 
				



--------------------queries--------------------




select * from posts  p  
WHERE   p.posttypeId = 1
and p.title is not null 
and p.body is not null
limit 10

select id,tags,tagssyn,code 
from posts p 
where p.posttypeid =1 
--and tags like '%ruby%' and tags not like '%ruby-on-rails%'
order by id desc
limit 1000

select postid
from postlinks p
where p.linktypeid =3
limit 10000

--montando a tabela posts com mix de posts
COPY ( 
select * 
from posts po
where po.id in (
select postid
from postlinks p
where p.linktypeid !=3
limit 50000
)
union
select * 
from posts po
where po.id in (
--master questions ids
select 
  distinct(relatedPostid) 
from postlinks pl, posts p
where p.id = pl.relatedPostid
and linktypeid = 3 -- duplicate
AND relatedPostid is not null 
limit 50000
)
union
select * 
from posts po
where po.id in (
--master questions ids
select 
  distinct(postid) 
from postlinks pl, posts p
where p.id = pl.relatedPostid
and linktypeid = 3 -- duplicate
limit 50000
)
)TO '/home/rodrigo/tmp/posts.csv';


select * 
from posts po
limit 100000

--limit 10

select * 
from posts po
where po.id = 3267877

--For instance, to get dupes of "How do I compare strings in Java?", question 513832, you would run the query using 513832 as the parameter.
select 
  postid 
from postlinks
where linktypeid = 3 -- duplicate
AND relatedPostid = 513832
ORDER BY creationdate DESC



--master questions ids
select 
  distinct(relatedPostid) 
from postlinks pl, posts p
where p.id = pl.relatedPostid
and linktypeid = 3 -- duplicate
AND relatedPostid is not null 



COPY (SELECT * FROM nyummy.cimory WHERE city = 'tokio') TO '/path/to/file.csv';


--duplicadas fechadas
select * 
from posts po
where po.id in (
select postid
from postlinks p
where p.linktypeid !=3
limit 1000
)
and po.closeddate is not null


select id, title, body, tags 
from posts p
where p.id in
(
select distinct(relatedpostid)
from postlinks pl
where pl.postid in 
(
select id
from posts 
WHERE  PostTypeId = 1 
 and closeddate is not null
 and id in 
 ( select distinct(pl.postid)
   from postlinks pl where pl.linktypeid = 3 )
 )
)

------------ queries

--findMastersOfClosedDuplicatedNonMasterQuestions
--106.7 k questions
select id, title, body, tags,null as master 
from posts p 
where 
  answercount > 0 and
  p.id in 
(select distinct(relatedpostid) 
from postlinks pl
where pl.postid in

    (select id 
     from posts
     WHERE  PostTypeId = 1 
	    and closeddate is not null
	    --and answercount > 0
	    and id in 
	    	(select distinct(pl.postid)
		 from postlinks pl where pl.linktypeid = 3 )))

		 


				                          

--findClosedDuplicatedNonMasterQuestions old
--130 k questions
select id,body,title,tags,null as master 
from posts 
WHERE  PostTypeId = 1 
and closeddate is not null
and id in 
( select distinct(pl.postid)
from postlinks pl where pl.linktypeid = 3 ) 
limit 100

--findClosedDuplicatedNonMasterQuestions new
--somente os posts da tag especifica
select *
from postsquestions 
where closeddate is not null
and id in 
( select distinct(pl.postid)
from postlinks pl where pl.linktypeid = 3 ) 
--limit 100


select * from postsquestions where id in (
select id
from posts 
WHERE  PostTypeId = 1 
and closeddate is not null
and id in 
( select distinct(pl.postid)
from postlinks pl where pl.linktypeid = 3 ) 
limit 100
)



--fetchQuestionsAtRandom
SELECT id, title, body, tags,null as master 
 FROM posts p, 
   (SELECT id as sid
    FROM posts p
    WHERE p.tags like '%<java>%'
    and p.postTypeId = 1
    and p.closeddate is null
    ORDER BY random()
   limit 1 ) tmp 
 WHERE p.id = tmp.sid



--findclosedDuplicatedNonMastersByTag old
select * from postsquestions where id in  
(select p.id 
from posts p 
WHERE  p.PostTypeId = 1 
and p.closeddate is not null
and p.tags like '%<ruby>%'
and p.id in 
( select distinct(pl.postid)
from postlinks pl where pl.linktypeid = 3 ))
limit 10

--findclosedDuplicatedNonMastersByTag 
select * from postsquestions p
WHERE  p.closeddate is not null
and p.tags like '%ruby%'
and p.id in 
( select distinct(pl.postid)
from postlinks pl where pl.linktypeid = 3 )
--limit 10



--fetchQuestionsAtRandom
select * from postsquestions where id in 
(SELECT id
FROM posts p
WHERE p.postTypeId = 1
and p.closeddate is null
and p.tags like '%<ruby>%'
ORDER BY random()
limit 100 )


--fetchQuestionsByTag
select * 
from 
postsquestions 
where originaltags like '%ruby%'





delete from closedDuplicatedQuestions;


select count(id) from closedDuplicatedQuestions;

select count(id) from closedDuplicatedQuestions where master = false limit 100
select count(id) from closedDuplicatedQuestions where master = false
select count(id) from closedDuplicatedQuestions where master = true

SELECT id, title, body, tags 
FROM closedDuplicatedQuestions 
where id = 570850

SELECT *
FROM postlinks 
where postid = 570850


SELECT *
FROM posts 
where id = 570850



SELECT id, title, body, tags, master FROM closedDuplicatedQuestions where tags like '%<java>%' and master = true limit 2

select count(id) from feature
select count(id) from pair where duplicated = false
select count(id) from pair where duplicated = true
select count(id) from pair




--count 
SELECT reltuples::bigint AS estimate FROM   pg_class WHERE  oid = 'postsquestions'::regclass;
--11025140

select * from postsquestions limit 300;

select count(id) from postsquestions;
--7990787 questions

select id,body from posts where postTypeId = 1 and body like '%<code>%' limit 50
select id,body from postsquestions where title like '%uplic' 
select id,body from postsquestions limit 1000
select id,body from posts where id = 16048588
select id,body from postsquestions where id = 16048829

select count(id) from posts where postTypeId = 1
select count(id) from postsquestions 



SELECT Id, Question1, Question2 FROM pair WHERE Duplicated = true AND MainTag like 'java' LIMIT 1
SELECT * FROM pair WHERE Duplicated = true AND MainTag like 'java' order by random() LIMIT 1


SELECT id, title, body, tags,null as master 
   FROM posts p
   WHERE p.tags like '%<java>%'
   and p.postTypeId = 1
   ORDER BY id




select * from pair where duplicated = true limit 100;
select * from feature  limit 100;
select * from feature 
select count(id) from pair where duplicated = true;
select count(id) from pair where duplicated = false;
select count(id) from feature 

select count(id) from pair where maintag like 'java' and duplicated = false;

delete from feature;
delete from pair;

select * from pair;


delete from feature where pairid in (select id from pair where maintag = 'ruby');
delete from pair where maintag = 'ruby';
--update feature set termoverlap = null where pairid in



select * from feature 
where type = 'CC' LIMIT 100

select *
from pair 
where id not in 
(select pairid from feature)


select * from postsquestions 
where body like '%blockquote%'
limit 50;

 select * from postsquestions where id in 
(  SELECT id
FROM posts p
WHERE p.tags like '%<java>%'
and p.postTypeId = 1
ORDER BY id )


(select count(p.id) 
  from posts p 
  WHERE  p.PostTypeId = 1 
  and p.closeddate is not null
  and p.tags like '%<java>%'
  and p.id in 
  ( select distinct(pl.postid)
  from postlinks pl where pl.linktypeid = 3 ))

select count(id) from pair
select count(id) from feature

select * from postlinks where relatedpostid = 1918196



select count(p.id) from posts p where p.parentid =21545399


select *
from comments c
where c.postid in
(
select p.id
from posts p
where p.tags like '%java%' and p.tags not like '%javascript%'
)
--order by p.id desc
--limit 100

select count(pl.id)
from posts p, postlinks pl
where p.id = pl.relatedpostid
and p.PostTypeId = 1 
and p.tags like '%java%' and p.tags not like '%javascript%'
and p.id in 
(select distinct(pl.relatedpostid) from postlinks pl)


select p.id
from posts p, postlinks pl
where p.id = pl.postid
and p.PostTypeId = 1 
and p.tags like '%java%' and p.tags not like '%javascript%'
and p.id in 
(select distinct(pl.postid) from postlinks pl
 union
 select distinct(pl.postid) from postlinks pl
)
order by p.id desc
limit 100


select * from postlinks where postid = 46898
union 
select * from postlinks where relatedpostid = 46898


select * from postlinks where postid in (456,4234985,123) union  select * from postlinks where relatedpostid in (456,4234985,123)

select * from postlinks where relatedpostid = 49215786
union
select * from postlinks where postid = 49215786

select * from posts p 
where p.body like '%</a>%' and p.body like '%stackoverflow.com%' and p.body like '%here%'
order by id desc
limit 10

select * from postlinks
select * from postlinks p where linktypeid = 3

select *
from posts p 
where tags like '<java>'
and body like '%</code></pre>%'
order by id desc
limit 10

--and p.id in (

--tratar tags ruby que nao estao como <ruby>
select * 
from postsquestions 
where tags like '%ruby%'
and id not in 
(
select id 
from postsquestions 
where tags like '%<ruby>%'
)




select id from posts p
where answercount > 0 
and p.PostTypeId = 1 
and p.tags like '%<ruby>%'
--limit 10



select * from posts where PostTypeId = 1  and title like '%uplic'
select * from postsquestions where title like '%duplic'

select id from posts where answercount > 0 limit 10

select answercount 
from posts 
where id in 
(16631961,
18936429,
8720677,
1575373,
2505104,
13589460,
17043840,
1815978,
5118745,
4981379,
11996326,
10799198,
11149837
)

select * from postsquestions where tags not like '%java%' limit 100

select * from postsquestions where body not like '<p>%'




---------------reset e analise qualitativa


delete from postsquestions where tags like '%ruby%';

--carrega tabela postsQuestions com perguntas apenas
insert into postsQuestions(Id, Body, Title, Tags, originaltags, closeddate)
SELECT Id, Body, Title, Tags, tags, closeddate
FROM   posts
WHERE  PostTypeId = 1
and tags like '%ruby%'
order by Id;


select count(*) from postsquestions;

select * from postsquestions where id = 10794828
select * from postsquestions where id = 4357895

select * from postsquestions 
where tags like '%rubygem%'
--and id = 4357895
ORDER BY id

select * from posts where tags like '%<ruby>%'  and body like '%<a href=%' limit 10

select * from posts
where tags like '%<ruby>%' 
and body like '%<code>%'
limit 100



select * from postsquestions where tags like '%ruby%' order by id desc limit 100

select * from postsquestions where id in (
651381,
929013,
971163,
1248558,
1284523,
2892719)


select *
--select sum(cosine) 
from pair p, feature f
where p.id = f.pairid
and p.question1= 7993915

---------------features


select * from feature
where 1=1
--and cosine = 
and type != 'TagTag' 
--and COSINE>0 
--and termoverlap is not null
--and termoverlap > 0
and pairid in 
(select id from pair where duplicated = TRUE )
order by cosine desc
--limit 5000

select * from pair where id = 286192

select * from pair where maintag = 'ruby'
select * from feature where pairid = 286192
--2.96

delete from feature where pairid in (select id from pair where maintag = 'ruby')
delete from pair where maintag = 'ruby'

--pairs duplicated
select pairid,SUM(f.cosine) as sum
from feature f, pair p
where f.pairid = p.id
and f.type not in ('TagTag')
and p.duplicated = true
group by pairid
--having SUM(f.cosine) > 0
--and pairid = 247871
order by sum desc

--pairs non duplicated
select pairid,SUM(f.cosine) as sum
from feature f, pair p
where f.pairid = p.id
and f.type not in ('TagTag')
and p.duplicated = false
group by pairid
--having SUM(f.cosine) > 0
order by sum asc


--duplicated
select * from pair p,
(
--preserva a ordem 
select sub.pairid as id
from 
(select pairid,SUM(f.cosine) as sum
from feature f, pair p
where f.pairid = p.id
and f.type not in ('TagTag')
and p.duplicated = true
and p.maintag = 'ruby'
group by pairid
having SUM(f.cosine) > 0
order by sum desc) as sub, pair p
where sub.pairid = p.id
--fim
) as subselect
where p.id = subselect.id

--limit 100


--non duplicated
select * from pair p,
(
--preserva a ordem 
select sub.pairid as id
from 
(select pairid,SUM(f.cosine) as sum
from feature f, pair p
where f.pairid = p.id
and f.type not in ('TagTag')
and p.duplicated = false
and p.maintag = 'ruby'
group by pairid
having SUM(f.cosine) > 0
order by sum asc) as sub, pair p
where sub.pairid = p.id
--fim
) as subselect
where p.id = subselect.id


--dup
235074
235030
235208
234861
236080
235802
235526
235725
235719
234935
234981
235014

select * from feature where cosine = 'NaN'



select * from feature where pairid = 275310 order by type
select * from feature where pairid = 275311 order by type



select concat(title,' ',body,' ',tags )  from postsquestions where id = 23769255
--"unabl activ becaus conflict rspec-2.14.1 rspec-core-3.0.0.rc1 rspec-core (~> 2.14.0) i am new rubi pleas bear me i am us rbenv i am try run test some practic rubi problem i get error i updat my gem still doesn t work i don t know what go what can i do fix  (...)"



select * from postsquestions where tags like '%ruby%'   ORDER BY id 
 
select * from postsquestions where originaltags like '%<ruby>%' limit 100

select * from postsquestions limit 100
select * from posts where tags like '%git%' and body like '%error%' limit 100


3167540
10455410

--266505 registros

select * from postsquestions where id = 24345118

select id,tags from posts where posttypeid = 1 and tags like '%ruby%' limit 1000
--266505 registros


select * from postsquestions where id = 7340215

select * from posts where id = 7340215

select tags from posts where posttypeid = 1 and tags limit 1000


select * from posts where id in 
(  SELECT id
FROM posts p
WHERE p.postTypeId = 1
and p.closeddate is null
and tags like '%C++%'
ORDER BY random()
limit 10)


select * from pair p, 
( select sub.pairid as id
from 
(select pairid,SUM(f.cosine) as sum
from feature f, pair p
where f.pairid = p.id
and p.duplicated = true
and p.maintag = 'tagFilter'
group by pairid 

order by sum desc) as sub, pair p
where sub.pairid = p.id
) as subselect 
where p.id = subselect.id




select * 
FROM posts p
WHERE p.postTypeId = 1
and p.closeddate is null
and tags like '%objective%'
limit 10

select distinct(relatedpostid)
from postlinks pl
where pl.postid = 26106208


select count(u.*)
from usersmin u
--556208

select count(id)
from postsmin
--3800361

select count(id)
from commentsmin
--3204920


select count(id)
from postlinksmin
--713246

select *
from posts p
where p.parentid = 910374


select * 
from posts p 
where p.id = 910522

delete from processedposts


select * from posts where id = 4234985

select count(p.id)
from postsmin p
where p.owneruserid is null

select *
from postsmin p
where p.id = 46107706


select *
from postsmin p
--where p.body like '%<code>%' and p.body not like '%<pre>%' 
where p.body like '%<blockquote>%'  
limit 10
