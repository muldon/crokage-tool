select *
--select id,topk,hitk10,mrrk10,mapk10,mrk10,hitk1,mrrk1,mapk1,mrk1,approach,bm25topnresults 
--select id,topk,hitk1,mrrk1,mapk1,mrk1,approach,bm25topnresults 
from metricsresults m
where 1=1 
--and approach like '%luceneTopIdsMap%' 
--and approach like '%final relevance%' 
--and obs like '%bm25TopNResults%' 
--and obs like '%all%' 
--and bm25topnresults>100
--and id>53874
and date > '2018-12-16' 
order by id desc
limit 300

select * from metricsresults where id=51808


--select id,topk,hitk1,mrrk1,mapk1,mrk1, bm25topnresults,obs,classfreqweight,repweight,simweight,upweight,approach,topclasses,topsimilartitlespercent
--from metricsresults m
--where approach like '%lucene%' 
--order by id desc

--select *
--select id,topk,hitk1,mrrk1,mapk1,mrk1,obs, bm25topnsmallresults,bm25topnbigresults,topapisscoredpairspercent,approach,topclasses,topsimilartitlespercent,tfidfcossimweight 
select topk,hitk10,mrrk10,mapk10,mrk10,obs, bm25topnsmallresults,bm25topnbigresults,topapisscoredpairspercent,approach,topclasses,topsimilarcontentsasymrelevancenumber,tfidfcossimweight,date
from metricsresults m
where 1=1 
and approach like '%final relevance%' 
--and approach like '%topAsymIdsMap%' 
--and approach like '%topMergeIdsMap%' 
--and approach like '%lucene%' 
--and obs like '%numberOfPostsInfoToMatchTFIDF%' 
--and id>53050
and date > '2018-12-16' 
--and obs like '%(average): %' 
--and bm25topnresults=100
--order by mrrk10 desc,hitk10 desc, mapk10 desc,mrk10 desc, id desc
--order by mrk1 desc,hitk1 desc, mapk1 desc,mrk1 desc, id desc
order by hitk10 desc,mrrk10 desc, mapk10 desc,mrk10 desc, id desc
--order by hitk1 desc,mrrk1 desc, mapk1 desc,mrk1 desc, id desc
--order by mrk1 desc,hitk1 desc,mrrk1 desc, mapk1 desc, id desc
--select max(bm25topnresults) from metricsresults


--delete from metricsresults where id>53050

--order by id desc

--select * from metricsresults m where id = 1569 --and id<1571 order by upweight desc, repweight desc

--order by 

--select *
--select topk,hitk1,mrrk1,mapk1,mrk1 
--select id,hitk10,mrrk10,mapk10,mrk10 
--from metricsresults m
--where approach like '%final relevance%' 
--and obs like '%numberOfPostsInfoToMatch:5%'
--and m.upweight = 0.5
--order by mrrk10 desc,hitk10 desc, mapk10 desc,mrk10 desc, id desc

--update metricsresults set bm25topnbigresults = bm25topnsmallresults
--update metricsresults set bm25topnsmallresults= null


select *
--select id,topk,hitk10,mrrk10,mapk10,mrk10,approach,obs 
--select id,topk,hitk1,mrrk1,mapk1,mrk1,approach,bm25topnresults 
from metricsresults m
where 1=1 
and id>=53874
and topk=10
and approach like '%final%' 
order by id desc

