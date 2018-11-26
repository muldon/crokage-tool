select * 
from metricsresults m
where id > 25000
order by id desc


--order by m.hitk10 desc, m.mrrk10 desc
select *
--select topk,hitk1,mrrk1,mapk1,mrk1 
--select id,hitk10,mrrk10,mapk10,mrk10 
from metricsresults m
where 1=1
and id > 25000
and approach like '%final relevance%' 
order by mrrk10 desc,hitk10 desc, mapk10 desc,mrk10 desc, id desc
--order by hitk10 desc,mrrk10 desc, mapk10 desc,mrk10 desc, id desc

--delete from metricsresults
