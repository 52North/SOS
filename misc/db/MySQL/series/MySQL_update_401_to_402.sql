ALTER TABLE series ADD COLUMN firstTimeStamp datetime;
ALTER TABLE series ADD COLUMN lastTimeStamp datetime;
ALTER TABLE series ADD COLUMN firstNumericValue decimal(19,2);
ALTER TABLE series ADD COLUMN lastNumericValue decimal(19,2);
ALTER TABLE series ADD COLUMN unitId bigint;

alter table series add constraint seriesUnitFk foreign key (unitId) references unit;

ALTER TABLE `procedure` ADD COLUMN referenceFlag char(1) default 'F';