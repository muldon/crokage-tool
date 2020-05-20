
--external questions by phase
select distinct(e.*) 
from externalquestion e, relatedpost rp, rank r
where e.id = rp.externalquestionid
and rp.id = r.relatedpostid
and r.phase = 42
order by e.filereferenceid

--related posts and rank
select r.*
from relatedpost rp, rank r
where rp.id=r.relatedpostid
and r.phase = 12
and rp.externalquestionid = 34

select e.*
from relatedpost rp, rank r, evaluation e
where rp.id=r.relatedpostid
and e.rankid = r.id
and r.phase = 12
and e.surveyuserid = 1
and rp.externalquestionid = 38

--and r.relatedpostid=135377

select e.*
from rank r, evaluation e
where r.id = e.rankid
and r.phase = 40



--Verificando numero de questoes por tipo 
select count(rp.id), rp.relationtypeid 
from relatedpost rp, rank r
where rp.id=r.relatedpostid
group by rp.relationtypeid


--Obtendo os ranks para uma externalquestion
select *
from relatedpost rp, rank r
where rp.id=r.relatedpostid
and r.phase=42
and rp.externalquestionid=34
order by r.rankorder
limit 10


--Verificando numero de questoes por tipo 
select *
from relatedpost rp, rank r
where rp.id=r.relatedpostid
and rp.relationtypeid=2
order by rp.externalquestionid,r.rankorder


--Numero de posts na tabela relationtype
ſselect count(rp.id)
from relatedpost rp



--Numero de posts na tabela rank por fase
select *
from rank r
where phase = 12
order by id desc

select distinct(phase)
from rank r
--where phase = 1

--update rank set phase = 42 where phase = 40

--Selecionando tipo especifico de post
select *
from relatedpost rp, rank r, postsmin p
where rp.id=r.relatedpostid
and p.id = rp.postid 
and rp.relationtypeid = 4

--verificando informacoes de um post especifico que veio na rank
select *
from relatedpost rp, rank r, postsmin p
where rp.id=r.relatedpostid
and p.id = rp.postid 
and p.id = 33344392
--and rp.id = 135314

--questoes que nao contem java recuperadas pela abordagem
select father.tags
from relatedpost rp, postsmin p, postsmin father
where rp.postid = p.id
and p.parentid = father.id
--and father.tags not like '%java%'


--excluindo posts ranqueados acima de ordem 10
--delete from rank r where rankorder >10


--verificando progresso das avaliacoes por usuario e por fase
--select rp.externalquestionid as "External Question Id", rp.relationtypeid, r.rankorder, e.likertscale, e.surveyuserid as "User", r.phase, e.ratingdate, rp.postid
select eq.filereferenceid , rp.relationtypeid, r.rankorder, e.likertscale, e.surveyuserid as "User", r.phase, e.ratingdate, rp.postid
from relatedpost rp, rank r, evaluation e, externalquestion eq
where rp.id = r.relatedpostid
and r.id = e.rankid
and eq.id = rp.externalquestionid
and r.phase = 42
order by e.surveyuserid desc, rp.externalquestionid asc, r.rankorder asc



--conferindo avaliacoes por post 
select rp.externalquestionid as "External Question Id", rp.relationtypeid, r.rankorder, e.likertscale, e.surveyuserid as "User", r.phase
from relatedpost rp, rank r, evaluation e
where rp.id = r.relatedpostid
and r.id = e.rankid
--and rp.postid= 19759564


select *
from relatedpost rp, rank r, evaluation e
where rp.id = r.relatedpostid
and r.id = e.rankid
and rp.id = 36


--selecionando avaliacoes de um usuario por fase
select *
from rank r, evaluation e
where r.id = e.rankid
and r.phase = 4
order by e.id desc

--número de posts na relatedposts
select count(id)
from relatedpost 
where externalquestionid = 2


--ranked list of remaining posts 
select r.*  
  from postsmin p, relatedpost rp, rank r  
  where p.id = rp.postid  
  and rp.id = r.relatedpostid	 
  and rp.externalquestionid =   62
  and r.phase =  12
  and r.id not in (
  --select *
  select r2.id
	  from relatedpost rp2, rank r2, evaluation e2
      where rp2.id = r2.relatedpostid	 
      and r2.id = e2.rankid
	  and rp2.externalquestionid =   62
      and r2.phase =  12
	  and e2.surveyuserid = 1
  )
  order by r.rankorder

