ALTER TABLE public.metricsresults
    ADD COLUMN approach character varying(100);

update metricsresults set approach = 'crokage';
