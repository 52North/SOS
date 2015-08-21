ALTER TABLE sos.`procedure` ADD COLUMN istype char(1) default 'F';
ALTER TABLE sos.`procedure` ADD COLUMN isaggregation char(1) default 'F';


ALTER TABLE sos.`procedure` ADD COLUMN typeof bigint;

alter table public."procedure" add constraint typeoffk foreign key (typeof) references public."procedure";




alter table sos.`procedure` add constraint typeoffk foreign key (typeof) references sos.`procedure` (procedureid);

