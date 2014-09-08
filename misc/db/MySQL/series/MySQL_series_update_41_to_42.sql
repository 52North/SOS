--update numeric columns to double
ALTER TABLE numericValue MODIFY value DOUBLE PRECISION;
ALTER TABLE series MODIFY firstNumericValue DOUBLE PRECISION;
ALTER TABLE series MODIFY lastNumericValue DOUBLE PRECISION;