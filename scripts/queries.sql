--Verificando numero de questoes por tipo 
select count(rp.id), rp.relationtypeid 
from relatedpost rp, rank r
where rp.id=r.relatedpostid
group by rp.relationtypeid


--Verificando numero de questoes por tipo 
select *
from relatedpost rp, rank r
where rp.id=r.relatedpostid
and rp.relationtypeid=2
order by rp.externalquestionid,r.rankorder


--Numero de posts na tabela relationtype
ſselect count(rp.id)
from relatedpost rp



--Numero de posts na tabela rank
select *
from rank r



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
and p.id = 19759564


--questoes que nao contem java recuperadas pela abordagem
select father.tags
from relatedpost rp, postsmin p, postsmin father
where rp.postid = p.id
and p.parentid = father.id
--and father.tags not like '%java%'


--excluindo posts ranqueados acima de ordem 10
delete from rank r where rankorder >10


--verificando progresso das avaliacoes por usuario 
select rp.externalquestionid as "External Question Id", rp.relationtypeid, r.rankorder, e.likertscale, e.surveyuserid as "User"
from relatedpost rp, rank r, evaluation e
where rp.id = r.relatedpostid
and r.id = e.rankid


--conferindo avaliacoes por post 
select rp.externalquestionid as "External Question Id", rp.relationtypeid, r.rankorder, e.likertscale, e.surveyuserid as "User"
from relatedpost rp, rank r, evaluation e
where rp.id = r.relatedpostid
and r.id = e.rankid
and rp.postid= 19759564



