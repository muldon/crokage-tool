
ALTER TABLE public.postsmin
    ADD COLUMN processedbody text COLLATE pg_catalog."default";

ALTER TABLE public.postsmin
    ADD COLUMN processedtitle text COLLATE pg_catalog."default";
	
ALTER TABLE public.postsmin
    ADD COLUMN code text COLLATE pg_catalog."default";
	
ALTER TABLE public.postsmin
    ADD COLUMN processedcode text COLLATE pg_catalog."default";
		