ALTER TABLE series ADD COLUMN firstTimeStamp timestamp;
ALTER TABLE series ADD COLUMN lastTimeStamp timestamp;
ALTER TABLE series ADD COLUMN firstNumericValue numeric(19, 2);
ALTER TABLE series ADD COLUMN lastNumericValue numeric(19, 2);
ALTER TABLE series ADD COLUMN unitId int8;

alter table series add constraint seriesUnitFk foreign key (unitId) references unit;

ALTER TABLE procedure ADD COLUMN referenceFlag char(1) default 'F' check (referenceFlag in ('T','F'));