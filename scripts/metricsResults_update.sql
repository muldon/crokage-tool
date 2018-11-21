ALTER TABLE public.metricsresults
    ADD COLUMN approach character varying(100);

update metricsresults set approach = 'crokage';


ALTER TABLE public.metricsresults
    ADD COLUMN topclasses integer;

update metricsresults set topclasses = 10;

ALTER TABLE public.metricsresults
    RENAME topsimilaranswersnumber TO topapisscoredpairspercent;

ALTER TABLE public.metricsresults
    ALTER COLUMN topapisscoredpairspercent TYPE integer;

ALTER TABLE public.metricsresults
    RENAME topsimilarquestionsnumber TO bm25topnresults;

ALTER TABLE public.metricsresults
    ALTER COLUMN bm25topnresults TYPE integer;

ALTER TABLE public.metricsresults
    ADD COLUMN topk integer;

ALTER TABLE public.metricsresults
    ADD COLUMN topsimilartitlespercent integer;
