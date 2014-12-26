-- add published flag to series table
ALTER TABLE public.series ADD published char(1) NOT NULL default 'T' check (published in ('T','F'));