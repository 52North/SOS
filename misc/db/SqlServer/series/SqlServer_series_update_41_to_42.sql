--update numeric columns to double
ALTER TABLE numericvalue ALTER COLUMN value double precision;
ALTER TABLE series ALTER COLUMN firstNumericValue double precision;
ALTER TABLE series ALTER COLUMN lastNumericValue double precision;