select *
from externalquestion eq
where eq.id not in 
( select e.id
  from evaluation e
  where e.surveyuserid = 1
  )
order by externalid  
limit 2