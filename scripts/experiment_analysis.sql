
--select * from metricsresults where id=51808


--select id,topk,hitk1,mrrk1,mapk1,mrk1, bm25topnresults,obs,classfreqweight,repweight,simweight,upweight,approach,topclasses,topsimilartitlespercent
--from metricsresults m
--where approach like '%lucene%' 
--order by id desc

select id,hitk1,mrrk1,mapk1,mrk1,obs
--select *
--select id,topk,hitk1,mrrk1,mapk1,mrk1,obs, bm25topnsmallresults,bm25topnbigresults,topapisscoredpairspercent,approach,topclasses,topsimilartitlespercent,tfidfcossimweight 
--select topk,hitk10,mrrk10,mapk10,mrk10,obs, bm25topnsmallresults,bm25topnbigresults,topapisscoredpairspercent,approach,topclasses,topsimilarcontentsasymrelevancenumber,tfidfcossimweight,date
from metricsresults m
where 1=1 
--and approach like '%final relevance%' 
--and approach like '%topAsymIdsMap%' 
--and approach like '%filteredAnswersWithApisIdsMap%' 
--and approach like '%lucene%' 
and approach like '%luceneTopIdsMap%' 
and obs not like '%bm25TopNSmallLimit: 100%' 
--and id>53050
--and m.classfreqweight=0
--and m.simweight=0
--and date > '2018-12-26' 
--and obs like '%(average): %' 
--and bm25topnresults=100
--order by mrrk10 desc,hitk10 desc, mapk10 desc,mrk10 desc, id desc
--order by mrk1 desc,hitk1 desc, mapk1 desc,mrk1 desc, id desc
--order by hitk10 desc,mrrk10 desc, mapk10 desc,mrk10 desc, id desc
order by hitk1 desc,mrrk1 desc, mapk1 desc,mrk1 desc, id desc
--order by mrk1 desc,hitk1 desc,mrrk1 desc, mapk1 desc, id desc
--select max(bm25topnresults) from metricsresults
--order by id desc

--delete from metricsresults where id>53050


