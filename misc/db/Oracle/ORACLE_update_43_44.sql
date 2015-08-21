ALTER TABLE procedure ADD istype char(1 char) default 'F' check (istype in ('T','F'));
ALTER TABLE procedure ADD isaggregation char(1 char) default 'F' check (isaggregation in ('T','F'));

ALTER TABLE procedure ADD typeof number(19,0);

alter table procedure add constraint typeoffk foreign key (procedureid) references procedure;