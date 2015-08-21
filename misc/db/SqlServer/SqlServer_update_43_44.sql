ALTER TABLE dbo.[procedure] ADD istype char(1) default 'T' check (istype in ('T','F'));
ALTER TABLE dbo.[procedure] ADD isaggregation char(1) default 'T' check (isaggregation in ('T','F'));

ALTER TABLE dbo.[procedure] ADD typeof bigint;

ALTER TABLE dbo.[procedure] add constraint typeoffk foreign key (typeof) references dbo.[procedure];