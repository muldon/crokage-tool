--posts duplicados java contendo códigos e respostas
select * 
from postsmin po
WHERE po.tags like '<java%' and po.tags not like '%javascript%'
and po.body like '%<pre><code>%'
and po.answercount>0
and po.id in (
select postid
from postlinksmin p
where p.linktypeid =3
)
and po.closeddate is not null
order by id desc
limit 1000

 
--posts contendo codigo java
--select *
select * 
from postsmin po
where po.body like '%<pre><code>%'
--and po.creationdate > '2017-01-01' 
and po.posttypeid = 2
and po.body like '%HTTPClient%'
--and po.parentid is not null
--order by id desc
--limit 10


select po.id, po.title,po.body,po.processedtitle, po.processedbody,po.code 
from postsmin po
where 1=1 
and po.code != ''
order by id desc
limit 150

select po.id,po.body,po.code,u.reputation,po.commentcount,po.viewcount,po.score,parent.acceptedanswerid  
from postsmin po, usersmin u, postsmin parent  
where po.owneruserid=u.id
and po.parentid = parent.id
and po.posttypeid = 2
and po.code != ''
--and po.acceptedanswerid is not null
order by po.score desc
limit 150

select * from postsmin where processedcode is not null limit 100
  