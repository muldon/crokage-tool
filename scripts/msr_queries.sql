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

select * 
from postsmin po
where po.processedcode != '' 
--d po.id in (2369483)  
limit 100


select p.* 
from postsmin p
where p.parentid = 40439065
and p.score>0  
and p.processedcode!= ''
order by p.id
--586182
 select *  from postsmin po where po.id in ( 11638123,7074402,7384908,15899699,19956225,16211139,10778744,8514046,9823503,10629803,7679819,586182,13776593,5061721,18148160,9008532,23160832,642897,8920654,22123489)
 
select p.* 
from postsmin p
where p.body like '%http%'
limit 10
 
 select * 
from metricsresults m
--where id > 25000
order by id desc

--getUpvotedAnswersIdsContentsAndParentContents
select po.id,parent.processedtitle,parent.processedbody as parentBody,parent.processedcode as parentCode,po.processedbody,po.processedcode,po.code,parent.id as parentId, parent.acceptedanswerid as acceptedanswerid 
 from postsmin po, postsmin parent
 where po.posttypeid=2
 and po.parentid = parent.id  
 and po.score>0
 and po.processedcode!=''
 and parent.score>0
 limit 100

 select * from postsmin where id in (9352608)
 
 
 select distinct(po.tags)
 from postsmin po
 where po.posttypeid=1
 and po.tags like '<java%' and po.tags not like '%javascript%' 
 order by po.tags
 
  --java tagged posts
select count(id) 
from postsmin po