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
select *
--select count(id) 
from postsmin po
where po.body like '%<pre><code>%'
and po.creationdate > '2018-06-01' 
and po.posttypeid = 2
--and po.parentid is not null
--order by id desc
--limit 10


 
  