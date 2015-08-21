ALTER TABLE public."procedure" ADD COLUMN istype char(1) default 'F' check (istype in ('T','F'));
ALTER TABLE public."procedure" ADD COLUMN isaggregation char(1) default 'F' check (isaggregation in ('T','F'));

ALTER TABLE public."procedure" ADD COLUMN typeof int8;

alter table public."procedure" add constraint typeoffk foreign key (procedureid) references public."procedure";