--update numeric columns to double
ALTER TABLE numericvalue ALTER COLUMN value TYPE double precision;
ALTER TABLE series ALTER COLUMN firstnumericvalue TYPE double precision;
ALTER TABLE series ALTER COLUMN lastnumericvalue TYPE double precision;