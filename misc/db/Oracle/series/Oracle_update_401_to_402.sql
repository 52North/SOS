ALTER TABLE series ADD firstTimeStamp timestamp;
ALTER TABLE series ADD lastTimeStamp timestamp;
ALTER TABLE series ADD firstNumericValue number(19,2);
ALTER TABLE series ADD lastNumericValue number(19,2);
ALTER TABLE series ADD unitId number(19,0);

alter table series add constraint seriesUnitFk foreign key (unitId) references unit;

ALTER TABLE procedure ADD referenceFlag char(1 char) default 'F' check (referenceFlag in ('T','F'));