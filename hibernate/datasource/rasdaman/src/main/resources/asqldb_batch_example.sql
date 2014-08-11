
INSERT INTO codespace (codespaceid, codespace) VALUES
(
(1, 'http://www.opengis.net/def/nil/OGC/0/unknown')
);

INSERT INTO featureofinteresttype (featureofinteresttypeid, featureofinteresttype) VALUES
(
(1, 'http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint'),
(2, 'http://www.opengis.net/def/nil/OGC/0/unknown')
);

INSERT INTO featureofinterest (featureofinterestid, hibernatediscriminator, featureofinteresttypeid, identifier, codespaceid, name, geom, descriptionxml, url) VALUES
(
(1 , 'T', 2, 'http://www.52north.org/test/featureOfInterest/1', 1, 'con terra@@http://www.opengis.net/def/nil/OGC/0/unknown', '0101000020E61000003F726BD26DE91E407D5EF1D423F14940', NULL, NULL),
(2 , 'T', 2, 'http://www.52north.org/test/featureOfInterest/2', 1, 'ESRI@@http://www.opengis.net/def/nil/OGC/0/unknown', '0101000020E6100000EB1D6E87864C5DC08255F5F23B074140', NULL, NULL),
(3 , 'T', 2, 'http://www.52north.org/test/featureOfInterest/3', 1, 'Kisters@@http://www.opengis.net/def/nil/OGC/0/unknown', '0101000020E610000014AAB2C82E8718400576C70892644940', NULL, NULL),
(4 , 'T', 2, 'http://www.52north.org/test/featureOfInterest/4', 1, 'con terra@@http://www.opengis.net/def/nil/OGC/0/unknown', '0101000020E61000003F726BD26DE91E407D5EF1D423F14940', NULL, NULL),
(5 , 'T', 2, 'http://www.52north.org/test/featureOfInterest/5', 1, 'TU-Dresden@@http://www.opengis.net/def/nil/OGC/0/unknown', '0101000020E6100000404EB4AB90722B401DE6CB0BB0834940', NULL, NULL),
(6 , 'T', 2, 'http://www.52north.org/test/featureOfInterest/6', 1, 'Hochschule Bochum@@http://www.opengis.net/def/nil/OGC/0/unknown', '0101000020E6100000083E062B4E151D4090D959F44EB94940', NULL, NULL),
(7 , 'T', 2, 'http://www.52north.org/test/featureOfInterest/7', 1, 'ITC@@http://www.opengis.net/def/nil/OGC/0/unknown', '0101000020E610000000958FEE31221140E45F15B9F1054A40', NULL, NULL),
(8 , 'T', 2, 'http://www.52north.org/test/featureOfInterest/8', 1, 'DLZ-IT@@http://www.opengis.net/def/nil/OGC/0/unknown', '0101000020E61000000055A4C2D8E22540008C67D0D0574940', NULL, NULL),
(9 , 'T', 2, 'http://www.52north.org/test/featureOfInterest/Heiden', 1, 'Heiden@@http://www.opengis.net/def/nil/OGC/0/unknown', '0101000020E61000008C118942CBBA1B404D874ECFBBE94940', NULL, NULL),
(10, 'T', 2, 'http://www.52north.org/test/featureOfInterest/Münster/FE101', 1, 'Münster/FE101@@http://www.opengis.net/def/nil/OGC/0/unknown', '0101000020E610000099B9C0E5B1861E405473B9C150F94940', NULL, NULL),
(11, 'T', 2, 'http://www.52north.org/test/featureOfInterest/Portland', 1, 'Portland@@http://www.opengis.net/def/nil/OGC/0/unknown', '0101000020E61000001DC9E53FA4AB5EC0C3F5285C8FC24640', NULL, NULL),
(12, 'T', 2, 'http://www.52north.org/test/featureOfInterest/TODO', 1, 'TODO@@http://www.opengis.net/def/nil/OGC/0/unknown', '0101000020E610000000000000000000000000000000000000', NULL, NULL)
);

INSERT INTO unit (unitid, unit) VALUES
(
(1, 'test_unit_1'),
(2, 'test_unit_4'),
(3, 'test_unit_6'),
(4, 'test_unit_7'),
(5, 'test_unit_8')
);

INSERT INTO observableproperty (observablepropertyid, hibernatediscriminator, identifier, description) VALUES
(
(1 , 'F', 'http://www.52north.org/test/observableProperty/9_1', NULL),
(2 , 'F', 'http://www.52north.org/test/observableProperty/9_2', NULL),
(3 , 'F', 'http://www.52north.org/test/observableProperty/9_3', NULL),
(4 , 'F', 'http://www.52north.org/test/observableProperty/9_4', NULL),
(5 , 'F', 'http://www.52north.org/test/observableProperty/9_5', NULL),
(6 , 'F', 'http://www.52north.org/test/observableProperty/9_6', NULL),
(7 , 'F', 'http://www.52north.org/test/observableProperty/1', NULL),
(8 , 'F', 'http://www.52north.org/test/observableProperty/2', NULL),
(9 , 'F', 'http://www.52north.org/test/observableProperty/3', NULL),
(10, 'F', 'http://www.52north.org/test/observableProperty/4', NULL),
(11, 'F', 'http://www.52north.org/test/observableProperty/5', NULL),
(12, 'F', 'http://www.52north.org/test/observableProperty/6', NULL),
(13, 'F', 'http://www.52north.org/test/observableProperty/7', NULL),
(14, 'F', 'http://www.52north.org/test/observableProperty/8', NULL),
(15, 'F', 'http://www.52north.org/test/observableProperty/developer', NULL)
);

INSERT INTO proceduredescriptionformat (proceduredescriptionformatid, proceduredescriptionformat) VALUES
(
(1, 'http://www.opengis.net/sensorML/1.0.1')
);

INSERT INTO "PUBLIC"."procedure" (procedureid, hibernatediscriminator, proceduredescriptionformatid, identifier, deleted, descriptionfile, referenceflag) VALUES
(
(1, 'T', 1, 'http://www.52north.org/test/procedure/9', 'F', NULL, 'F'),
(2, 'T', 1, 'http://www.52north.org/test/procedure/1', 'F', NULL, 'F'),
(3, 'T', 1, 'http://www.52north.org/test/procedure/2', 'F', NULL, 'F'),
(4, 'T', 1, 'http://www.52north.org/test/procedure/3', 'F', NULL, 'F'),
(5, 'T', 1, 'http://www.52north.org/test/procedure/4', 'F', NULL, 'F'),
(6, 'T', 1, 'http://www.52north.org/test/procedure/5', 'F', NULL, 'F'),
(7, 'T', 1, 'http://www.52north.org/test/procedure/6', 'F', NULL, 'F'),
(8, 'T', 1, 'http://www.52north.org/test/procedure/7', 'F', NULL, 'F'),
(9, 'T', 1, 'http://www.52north.org/test/procedure/8', 'F', NULL, 'F'),
(10, 'T', 1, 'http://www.52north.org/test/procedure/developer', 'F', NULL, 'F')
);

INSERT INTO series (seriesid, featureofinterestid, observablepropertyid, procedureid, deleted, firsttimestamp, lasttimestamp, firstnumericvalue, lastnumericvalue, unitid) VALUES
(
(1, 1,  7, 2, 'F', '2012-11-19 13:00:00', '2012-11-19 13:09:00', 1.00, 1.90, 1),
(8, 8, 14, 9, 'F', '2012-11-19 13:00:00', '2012-11-19 13:49:59', 4.00, 4.90, 5),
(9, 9, 15, 10, 'F', '2008-10-29 00:00:00', '2008-10-29 00:00:00', NULL, NULL, NULL),
(10, 10, 15, 10, 'F', '2008-10-29 00:00:00', '2008-10-29 00:00:00', NULL, NULL, NULL),
(11, 11, 15, 10, 'F', '2008-10-29 00:00:00', '2008-10-29 00:00:00', NULL, NULL, NULL),
(2, 2, 8, 3, 'F', '2012-11-19 13:00:00', '2012-11-19 13:09:00', NULL, NULL, NULL),
(12, 12, 15, 10, 'F', '2012-12-31 23:00:00', '2012-12-31 23:00:00', NULL, NULL, NULL),
(3, 3, 9, 4, 'F', '2012-11-19 13:00:00', '2012-11-19 13:09:00', NULL, NULL, NULL),
(4, 4, 10, 5, 'F', '2012-11-19 13:00:00', '2012-11-19 13:09:00', NULL, NULL, NULL),
(5, 5, 11, 6, 'F', '2012-11-19 13:00:00', '2012-11-19 13:09:00', NULL, NULL, NULL),
(6, 6, 12, 7, 'F', '2012-11-19 13:00:00', '2012-11-19 13:09:00', 2.00, 2.90, 3),
(7, 7, 13, 8, 'F', '2012-11-19 13:00:00', '2012-11-19 13:09:00', 3.00, 3.90, 4)
);

INSERT INTO observation (observationid, seriesid, phenomenontimestart, phenomenontimeend, resulttime, deleted, validtimestart, validtimeend, samplinggeometry, identifier, codespaceid, description, unitid) VALUES 
(
(1, 1, '2012-11-19 13:00:00', '2012-11-19 13:00:00', '2012-11-19 13:00:00', 'F', NULL, NULL, NULL, 'http://www.52north.org/test/observation/1', 1, NULL, 1),
(2, 1, '2012-11-19 13:01:00', '2012-11-19 13:01:00', '2012-11-19 13:01:00', 'F', NULL, NULL, NULL, 'http://www.52north.org/test/observation/2', 1, NULL, 1),
(3, 1, '2012-11-19 13:02:00', '2012-11-19 13:02:00', '2012-11-19 13:02:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 1),
(4, 1, '2012-11-19 13:03:00', '2012-11-19 13:03:00', '2012-11-19 13:03:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 1),
(5, 1, '2012-11-19 13:04:00', '2012-11-19 13:04:00', '2012-11-19 13:04:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 1),
(6, 1, '2012-11-19 13:05:00', '2012-11-19 13:05:00', '2012-11-19 13:05:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 1),
(7, 1, '2012-11-19 13:06:00', '2012-11-19 13:06:00', '2012-11-19 13:06:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 1),
(8, 1, '2012-11-19 13:07:00', '2012-11-19 13:07:00', '2012-11-19 13:07:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 1),
(9, 1, '2012-11-19 13:08:00', '2012-11-19 13:08:00', '2012-11-19 13:08:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 1),
(10, 1 , '2012-11-19 13:09:00', '2012-11-19 13:09:00', '2012-11-19 13:09:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 1),
(11, 2 , '2012-11-19 13:00:00', '2012-11-19 13:00:00', '2012-11-19 13:00:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(12, 2 , '2012-11-19 13:01:00', '2012-11-19 13:01:00', '2012-11-19 13:01:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(13, 2 , '2012-11-19 13:02:00', '2012-11-19 13:02:00', '2012-11-19 13:02:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(14, 2 , '2012-11-19 13:03:00', '2012-11-19 13:03:00', '2012-11-19 13:03:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(15, 2 , '2012-11-19 13:04:00', '2012-11-19 13:04:00', '2012-11-19 13:04:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(16, 2 , '2012-11-19 13:05:00', '2012-11-19 13:05:00', '2012-11-19 13:05:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(17, 2 , '2012-11-19 13:06:00', '2012-11-19 13:06:00', '2012-11-19 13:06:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(18, 2 , '2012-11-19 13:07:00', '2012-11-19 13:07:00', '2012-11-19 13:07:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(19, 2 , '2012-11-19 13:08:00', '2012-11-19 13:08:00', '2012-11-19 13:08:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(20, 2 , '2012-11-19 13:09:00', '2012-11-19 13:09:00', '2012-11-19 13:09:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(21, 3 , '2012-11-19 13:00:00', '2012-11-19 13:00:00', '2012-11-19 13:00:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(22, 3 , '2012-11-19 13:01:00', '2012-11-19 13:01:00', '2012-11-19 13:01:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(23, 3 , '2012-11-19 13:02:00', '2012-11-19 13:02:00', '2012-11-19 13:02:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(24, 3 , '2012-11-19 13:03:00', '2012-11-19 13:03:00', '2012-11-19 13:03:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(25, 3 , '2012-11-19 13:04:00', '2012-11-19 13:04:00', '2012-11-19 13:04:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(26, 3 , '2012-11-19 13:05:00', '2012-11-19 13:05:00', '2012-11-19 13:05:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(27, 3 , '2012-11-19 13:06:00', '2012-11-19 13:06:00', '2012-11-19 13:06:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(28, 3 , '2012-11-19 13:07:00', '2012-11-19 13:07:00', '2012-11-19 13:07:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(29, 3 , '2012-11-19 13:08:00', '2012-11-19 13:08:00', '2012-11-19 13:08:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(30, 3 , '2012-11-19 13:09:00', '2012-11-19 13:09:00', '2012-11-19 13:09:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(31, 4 , '2012-11-19 13:00:00', '2012-11-19 13:00:00', '2012-11-19 13:00:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 2),
(32, 4 , '2012-11-19 13:01:00', '2012-11-19 13:01:00', '2012-11-19 13:01:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 2),
(33, 4 , '2012-11-19 13:02:00', '2012-11-19 13:02:00', '2012-11-19 13:02:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 2),
(34, 4 , '2012-11-19 13:03:00', '2012-11-19 13:03:00', '2012-11-19 13:03:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 2),
(35, 4 , '2012-11-19 13:04:00', '2012-11-19 13:04:00', '2012-11-19 13:04:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 2),
(36, 4 , '2012-11-19 13:05:00', '2012-11-19 13:05:00', '2012-11-19 13:05:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 2),
(37, 4 , '2012-11-19 13:06:00', '2012-11-19 13:06:00', '2012-11-19 13:06:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 2),
(38, 4 , '2012-11-19 13:07:00', '2012-11-19 13:07:00', '2012-11-19 13:07:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 2),
(39, 4 , '2012-11-19 13:08:00', '2012-11-19 13:08:00', '2012-11-19 13:08:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 2),
(40, 4 , '2012-11-19 13:09:00', '2012-11-19 13:09:00', '2012-11-19 13:09:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 2),
(41, 5 , '2012-11-19 13:00:00', '2012-11-19 13:00:00', '2012-11-19 13:00:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(42, 5 , '2012-11-19 13:01:00', '2012-11-19 13:01:00', '2012-11-19 13:01:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(43, 5 , '2012-11-19 13:02:00', '2012-11-19 13:02:00', '2012-11-19 13:02:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(44, 5 , '2012-11-19 13:03:00', '2012-11-19 13:03:00', '2012-11-19 13:03:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(45, 5 , '2012-11-19 13:04:00', '2012-11-19 13:04:00', '2012-11-19 13:04:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(46, 5 , '2012-11-19 13:05:00', '2012-11-19 13:05:00', '2012-11-19 13:05:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(47, 5 , '2012-11-19 13:06:00', '2012-11-19 13:06:00', '2012-11-19 13:06:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(48, 5 , '2012-11-19 13:07:00', '2012-11-19 13:07:00', '2012-11-19 13:07:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(49, 5 , '2012-11-19 13:08:00', '2012-11-19 13:08:00', '2012-11-19 13:08:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(50, 5 , '2012-11-19 13:09:00', '2012-11-19 13:09:00', '2012-11-19 13:09:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(51, 6 , '2012-11-19 13:00:00', '2012-11-19 13:00:00', '2012-11-19 13:00:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 3),
(52, 6 , '2012-11-19 13:01:00', '2012-11-19 13:01:00', '2012-11-19 13:01:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 3),
(53, 6 , '2012-11-19 13:02:00', '2012-11-19 13:02:00', '2012-11-19 13:02:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 3),
(54, 6 , '2012-11-19 13:03:00', '2012-11-19 13:03:00', '2012-11-19 13:03:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 3),
(55, 6 , '2012-11-19 13:04:00', '2012-11-19 13:04:00', '2012-11-19 13:04:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 3),
(56, 6 , '2012-11-19 13:05:00', '2012-11-19 13:05:00', '2012-11-19 13:05:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 3),
(57, 6 , '2012-11-19 13:06:00', '2012-11-19 13:06:00', '2012-11-19 13:06:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 3),
(58, 6 , '2012-11-19 13:07:00', '2012-11-19 13:07:00', '2012-11-19 13:07:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 3),
(59, 6 , '2012-11-19 13:08:00', '2012-11-19 13:08:00', '2012-11-19 13:08:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 3),
(60, 6 , '2012-11-19 13:09:00', '2012-11-19 13:09:00', '2012-11-19 13:09:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 3),
(61, 7 , '2012-11-19 13:00:00', '2012-11-19 13:00:00', '2012-11-19 13:00:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 4),
(62, 7 , '2012-11-19 13:01:00', '2012-11-19 13:01:00', '2012-11-19 13:01:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 4),
(63, 7 , '2012-11-19 13:02:00', '2012-11-19 13:02:00', '2012-11-19 13:02:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 4),
(64, 7 , '2012-11-19 13:03:00', '2012-11-19 13:03:00', '2012-11-19 13:03:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 4),
(65, 7 , '2012-11-19 13:04:00', '2012-11-19 13:04:00', '2012-11-19 13:04:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 4),
(66, 7 , '2012-11-19 13:05:00', '2012-11-19 13:05:00', '2012-11-19 13:05:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 4),
(67, 7 , '2012-11-19 13:06:00', '2012-11-19 13:06:00', '2012-11-19 13:06:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 4),
(68, 7 , '2012-11-19 13:07:00', '2012-11-19 13:07:00', '2012-11-19 13:07:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 4),
(69, 7 , '2012-11-19 13:08:00', '2012-11-19 13:08:00', '2012-11-19 13:08:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 4),
(70, 7 , '2012-11-19 13:09:00', '2012-11-19 13:09:00', '2012-11-19 13:09:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 4),
(71, 8 , '2012-11-19 13:00:00', '2012-11-19 13:04:59', '2012-11-19 13:05:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 5),
(72, 8 , '2012-11-19 13:05:00', '2012-11-19 13:09:59', '2012-11-19 13:10:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 5),
(73, 8 , '2012-11-19 13:10:00', '2012-11-19 13:14:59', '2012-11-19 13:15:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 5),
(74, 8 , '2012-11-19 13:15:00', '2012-11-19 13:19:59', '2012-11-19 13:20:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 5),
(75, 8 , '2012-11-19 13:20:00', '2012-11-19 13:24:59', '2012-11-19 13:25:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 5),
(76, 8 , '2012-11-19 13:25:00', '2012-11-19 13:29:59', '2012-11-19 13:30:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 5),
(77, 8 , '2012-11-19 13:30:00', '2012-11-19 13:34:59', '2012-11-19 13:35:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 5),
(78, 8 , '2012-11-19 13:35:00', '2012-11-19 13:39:59', '2012-11-19 13:40:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 5),
(79, 8 , '2012-11-19 13:40:00', '2012-11-19 13:44:59', '2012-11-19 13:45:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 5),
(80, 8 , '2012-11-19 13:45:00', '2012-11-19 13:49:59', '2012-11-19 13:50:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, 5),
(81, 9 , '2008-10-29 00:00:00', '2008-10-29 00:00:00', '2008-10-29 00:00:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(82, 10, '2008-10-29 00:00:00', '2008-10-29 00:00:00', '2008-10-29 00:00:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(83, 11, '2008-10-29 00:00:00', '2008-10-29 00:00:00', '2008-10-29 00:00:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL),
(84, 12, '2012-12-31 23:00:00', '2012-12-31 23:00:00', '2012-12-31 22:01:00', 'F', NULL, NULL, NULL, NULL, 1, NULL, NULL)
);

INSERT INTO offering (offeringid, hibernatediscriminator, identifier, name) VALUES
(
(1, 'T', 'http://www.52north.org/test/offering/9', 'Offering for sensor 9'),
(2, 'T', 'http://www.52north.org/test/offering/1', 'Offering for sensor 1'),
(3, 'T', 'http://www.52north.org/test/offering/2', 'Offering for sensor 2'),
(4, 'T', 'http://www.52north.org/test/offering/3', 'Offering for sensor 3'),
(5, 'T', 'http://www.52north.org/test/offering/4', 'Offering for sensor 2'),
(6, 'T', 'http://www.52north.org/test/offering/5', 'Offering for sensor 5'),
(7, 'T', 'http://www.52north.org/test/offering/6', 'Offering for sensor 6'),
(8, 'T', 'http://www.52north.org/test/offering/7', 'Offering for sensor 7'),
(9, 'T', 'http://www.52north.org/test/offering/8', 'Offering for sensor 8'),
(10, 'T', 'http://www.52north.org/test/offering/developer', 'Offering for procedure developer')
);

INSERT INTO observationtype (observationtypeid, observationtype) VALUES
(
(1, 'http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CountObservation'),
(2, 'http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CategoryObservation'),
(3, 'http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TextObservation'),
(4, 'http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement'),
(5, 'http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TruthObservation'),
(6, 'http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_GeometryObservation')
);

INSERT INTO observationconstellation (observationconstellationid, observablepropertyid, procedureid, observationtypeid, offeringid, deleted, hiddenchild) VALUES
(
(1 ,  1 , 1 ,  NULL,  1 , 'F', 'F'),
(2 ,  2 , 1 ,  NULL,  1 , 'F', 'F'),
(3 ,  3 , 1 ,  NULL,  1 , 'F', 'F'),
(4 ,  4 , 1 ,  NULL,  1 , 'F', 'F'),
(5 ,  5 , 1 ,  NULL,  1 , 'F', 'F'),
(6 ,  6 , 1 ,  NULL,  1 , 'F', 'F'),
(7 ,  7 , 2 ,  4 ,  2 , 'F', 'F'),
(8 ,  8 , 3 ,  1 ,  3 , 'F', 'F'),
(9 ,  9 , 4 ,  5 ,  4 , 'F', 'F'),
(10,  10, 5 ,  2 ,  5 , 'F', 'F'),
(11,  11, 6 ,  3 ,  6 , 'F', 'F'),
(12,  12, 7 ,  4 ,  7 , 'F', 'F'),
(13,  13, 8 ,  4 ,  8 , 'F', 'F'),
(14,  14, 9 ,  4 ,  9 , 'F', 'F'),
(15,  15, 10,  3 ,  10, 'F', 'F')
);

INSERT INTO observationhasoffering (observationid, offeringid) VALUES
(
(1 , 2),
(2 , 2),
(3 , 2),
(4 , 2),
(5 , 2),
(6 , 2),
(7 , 2),
(8 , 2),
(9 , 2),
(10, 2),
(11, 3),
(12, 3),
(13, 3),
(14, 3),
(15, 3),
(16, 3),
(17, 3),
(18, 3),
(19, 3),
(20, 3),
(21, 4),
(22, 4),
(23, 4),
(24, 4),
(25, 4),
(26, 4),
(27, 4),
(28, 4),
(29, 4),
(30, 4),
(31, 5),
(32, 5),
(33, 5),
(34, 5),
(35, 5),
(36, 5),
(37, 5),
(38, 5),
(39, 5),
(40, 5),
(41, 6),
(42, 6),
(43, 6),
(44, 6),
(45, 6),
(46, 6),
(47, 6),
(48, 6),
(49, 6),
(50, 6),
(51, 7),
(52, 7),
(53, 7),
(54, 7),
(55, 7),
(56, 7),
(57, 7),
(58, 7),
(59, 7),
(60, 7),
(61, 8),
(62, 8),
(63, 8),
(64, 8),
(65, 8),
(66, 8),
(67, 8),
(68, 8),
(69, 8),
(70, 8),
(71, 9),
(72, 9),
(73, 9),
(74, 9),
(75, 9),
(76, 9),
(77, 9),
(78, 9),
(79, 9),
(80, 9),
(81, 10),
(82, 10),
(83, 10),
(84, 10)
);

INSERT INTO offeringallowedfeaturetype (offeringid, featureofinteresttypeid) VALUES
(
(1 , 1),
(2 , 1),
(3 , 1),
(4 , 1),
(5 , 1),
(6 , 1),
(7 , 1),
(8 , 1),
(9 , 1),
(10, 1)
);


INSERT INTO offeringallowedobservationtype (offeringid, observationtypeid) VALUES
(
(1 , 4),
(1 , 1),
(1 , 6),
(1 , 3),
(1 , 5),
(1 , 2),
(2 , 4),
(3 , 1),
(4 , 5),
(5 , 2),
(6 , 3),
(7 , 4),
(8 , 4),
(9 , 4),
(10, 3)
);

INSERT INTO resulttemplate (resulttemplateid, offeringid, observablepropertyid, procedureid, featureofinterestid, identifier, resultstructure, resultencoding) VALUES
(
(1, 7, 12, 7, 6, 'http://www.52north.org/test/procedure/6/template/1', '<swe:DataRecord xmlns:swe="http://www.opengis.net/swe/2.0" xmlns:xlink="http://www.w3.org/1999/xlink">\n  <swe:field name="phenomenonTime">\n    <swe:TimeRange xmlns:ns="http://www.opengis.net/swe/2.0" definition="http://www.opengis.net/def/property/OGC/0/PhenomenonTime">\n      <ns:uom xlink:href="http://www.opengis.net/def/uom/ISO-8601/0/Gregorian"/>\n    </swe:TimeRange>\n  </swe:field>\n  <swe:field name="resultTime">\n    <swe:Time xmlns:ns="http://www.opengis.net/swe/2.0" definition="http://www.opengis.net/def/property/OGC/0/ResultTime">\n      <ns:uom code="testunit1"/>\n    </swe:Time>\n  </swe:field>\n  <swe:field name="observable_property_6">\n    <swe:Quantity xmlns:ns="http://www.opengis.net/swe/2.0" definition="http://www.52north.org/test/observableProperty/6">\n      <ns:uom code="test_unit_6"/>\n    </swe:Quantity>\n  </swe:field>\n</swe:DataRecord>', '<swe:TextEncoding xmlns:swe="http://www.opengis.net/swe/2.0" blockSeparator="#" tokenSeparator=","/>')
);

INSERT INTO booleanvalue (observationid, value) VALUES 
(
(21, 'T'), 
(22, 'T'),
(23, 'F'),
(24, 'T'),
(25, 'F'),
(26, 'F'),
(27, 'T'),
(28, 'T'),
(29, 'F'),
(30, 'T')
);

INSERT INTO numericvalue (observationid, value) VALUES
(
(1 , 1.00),
(2 , 1.10),
(3 , 1.20),
(4 , 1.30),
(5 , 1.40),
(6 , 1.50),
(7 , 1.60),
(8 , 1.70),
(9 , 1.80),
(10, 1.90),
(51, 2.00),
(52, 2.10),
(53, 2.20),
(54, 2.30),
(55, 2.40),
(56, 2.50),
(57, 2.60),
(58, 2.70),
(59, 2.80),
(60, 2.90),
(61, 3.00),
(62, 3.10),
(63, 3.20),
(64, 3.30),
(65, 3.40),
(66, 3.50),
(67, 3.60),
(68, 3.70),
(69, 3.80),
(70, 3.90),
(71, 4.00),
(72, 4.10),
(73, 4.20),
(74, 4.30),
(75, 4.40),
(76, 4.50),
(77, 4.60),
(78, 4.70),
(79, 4.80),
(80, 4.90)
);

INSERT INTO countvalue (observationid, value) VALUES
(
(11, 0),
(12, 1),
(13, 2),
(14, 3),
(15, 4),
(16, 5),
(17, 6),
(18, 7),
(19, 8),
(20, 9)
);

INSERT INTO categoryvalue (observationid, value) VALUES
(
(31, 'test_category_0'),
(32, 'test_category_1'),
(33, 'test_category_2'),
(34, 'test_category_3'),
(35, 'test_category_4'),
(36, 'test_category_5'),
(37, 'test_category_6'),
(38, 'test_category_7'),
(39, 'test_category_8'),
(40, 'test_category_9')
);

INSERT INTO textvalue (observationid, value) VALUES
(
(41, 'test_text_0'),
(42, 'test_text_1'),
(43, 'test_text_2'),
(44, 'test_text_3'),
(45, 'test_text_4'),
(46, 'test_text_5'),
(47, 'test_text_6'),
(48, 'test_text_7'),
(49, 'test_text_8'),
(50, 'test_text_9'),
(81, 'Carsten Hollmann'),
(82, 'Christian Autermann'),
(83, 'Shane StClair'),
(84, 'John/Jane Doe')
);

INSERT INTO validproceduretime (validproceduretimeid, procedureid, proceduredescriptionformatid, starttime, endtime, descriptionxml) VALUES
(
(1 , 1 , 1, '2014-08-10 22:04:53.62', NULL, '<sml:SensorML xmlns:sml="http://www.opengis.net/sensorML/1.0.1" xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" version="1.0.1">\n  <sml:member>\n    <sml:System>\n      <!--optional; generated if not present-->\n      <sml:identification>\n        <sml:IdentifierList>\n          <sml:identifier name="uniqueID">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:uniqueID">\n              <sml:value>http://www.52north.org/test/procedure/9</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="longName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:longName">\n              <sml:value>52°North Initiative for Geospatial Open Source Software GmbH (http://52north.org)</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="shortName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:shortName">\n              <sml:value>52°North GmbH</sml:value>\n            </sml:Term>\n          </sml:identifier>\n        </sml:IdentifierList>\n      </sml:identification>\n      <sml:capabilities name="featuresOfInterest">\n        <!--Special capabilities used to specify features of interest.-->\n        <!--Parsed and removed during InsertSensor/UpdateSensorDescription, added during DescribeSensor.-->\n        <swe:SimpleDataRecord>\n          <swe:field name="featureOfInterestID">\n            <swe:Text>\n              <swe:value>http://www.52north.org/test/featureOfInterest/9</swe:value>\n            </swe:Text>\n          </swe:field>\n        </swe:SimpleDataRecord>\n      </sml:capabilities>\n      <sml:position name="sensorPosition">\n        <swe:Position referenceFrame="urn:ogc:def:crs:EPSG::4326">\n          <swe:location>\n            <swe:Vector gml:id="STATION_LOCATION">\n              <swe:coordinate name="easting">\n                <swe:Quantity axisID="x">\n                  <swe:uom code="degree"/>\n                  <swe:value>7.651968812254194</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="northing">\n                <swe:Quantity axisID="y">\n                  <swe:uom code="degree"/>\n                  <swe:value>51.935101100104916</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="altitude">\n                <swe:Quantity axisID="z">\n                  <swe:uom code="m"/>\n                  <swe:value>52.0</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n            </swe:Vector>\n          </swe:location>\n        </swe:Position>\n      </sml:position>\n      <sml:inputs>\n        <sml:InputList>\n          <sml:input name="test_observable_property_9">\n            <swe:ObservableProperty definition="http://www.52north.org/test/observableProperty/9"/>\n          </sml:input>\n        </sml:InputList>\n      </sml:inputs>\n      <sml:outputs>\n        <sml:OutputList>\n          <sml:output name="test_observable_property_9_1">\n            <swe:Category definition="http://www.52north.org/test/observableProperty/9_1">\n              <swe:codeSpace xlink:href="NOT_DEFINED"/>\n            </swe:Category>\n          </sml:output>\n          <sml:output name="test_observable_property_9_2">\n            <swe:Count definition="http://www.52north.org/test/observableProperty/9_2"/>\n          </sml:output>\n          <sml:output name="test_observable_property_9_3">\n            <swe:Quantity definition="http://www.52north.org/test/observableProperty/9_3">\n              <swe:uom code="NOT_DEFINED"/>\n            </swe:Quantity>\n          </sml:output>\n          <sml:output name="test_observable_property_9_4">\n            <swe:Text definition="http://www.52north.org/test/observableProperty/9_4"/>\n          </sml:output>\n          <sml:output name="test_observable_property_9_5">\n            <swe:Boolean definition="http://www.52north.org/test/observableProperty/9_5"/>\n          </sml:output>\n        </sml:OutputList>\n      </sml:outputs>\n    </sml:System>\n  </sml:member>\n</sml:SensorML>'),
(2 , 2 , 1, '2014-08-10 22:05:22.522', NULL, '<sml:SensorML xmlns:sml="http://www.opengis.net/sensorML/1.0.1" version="1.0.1">\n  <sml:member>\n    <sml:System xmlns:swes="http://www.opengis.net/swes/2.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:gml="http://www.opengis.net/gml" xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:sos="http://www.opengis.net/sos/2.0">\n      <sml:identification>\n        <sml:IdentifierList>\n          <sml:identifier name="uniqueID">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:uniqueID">\n              <sml:value>http://www.52north.org/test/procedure/1</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="longName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:longName">\n              <sml:value>con terra GmbH (www.conterra.de)</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="shortName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:shortName">\n              <sml:value>con terra</sml:value>\n            </sml:Term>\n          </sml:identifier>\n        </sml:IdentifierList>\n      </sml:identification>\n      <sml:capabilities name="featuresOfInterest">\n        <swe:SimpleDataRecord>\n          <swe:field name="featureOfInterestID">\n            <swe:Text>\n              <swe:value>http://www.52north.org/test/featureOfInterest/1</swe:value>\n            </swe:Text>\n          </swe:field>\n        </swe:SimpleDataRecord>\n      </sml:capabilities>\n      <sml:position name="sensorPosition">\n        <swe:Position referenceFrame="urn:ogc:def:crs:EPSG::4326">\n          <swe:location>\n            <swe:Vector gml:id="STATION_LOCATION">\n              <swe:coordinate name="easting">\n                <swe:Quantity axisID="x">\n                  <swe:uom code="degree"/>\n                  <swe:value>7.727958</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="northing">\n                <swe:Quantity axisID="y">\n                  <swe:uom code="degree"/>\n                  <swe:value>51.883906</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="altitude">\n                <swe:Quantity axisID="z">\n                  <swe:uom code="m"/>\n                  <swe:value>52.0</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n            </swe:Vector>\n          </swe:location>\n        </swe:Position>\n      </sml:position>\n      <sml:inputs>\n        <sml:InputList>\n          <sml:input name="test_observable_property_1">\n            <swe:ObservableProperty definition="http://www.52north.org/test/observableProperty/1"/>\n          </sml:input>\n        </sml:InputList>\n      </sml:inputs>\n      <sml:outputs>\n        <sml:OutputList>\n          <sml:output name="test_observable_property_1">\n            <swe:Category definition="http://www.52north.org/test/observableProperty/1">\n              <swe:codeSpace xlink:href="test_unit_1"/>\n            </swe:Category>\n          </sml:output>\n        </sml:OutputList>\n      </sml:outputs>\n    </sml:System>\n  </sml:member>\n</sml:SensorML>'),
(3 , 3 , 1, '2014-08-10 22:05:23.304', NULL, '<sml:SensorML xmlns:sml="http://www.opengis.net/sensorML/1.0.1" version="1.0.1">\n  <sml:member>\n    <sml:System xmlns:swes="http://www.opengis.net/swes/2.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:gml="http://www.opengis.net/gml" xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:sos="http://www.opengis.net/sos/2.0">\n      <sml:identification>\n        <sml:IdentifierList>\n          <sml:identifier name="uniqueID">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:uniqueID">\n              <sml:value>http://www.52north.org/test/procedure/2</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="longName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:longName">\n              <sml:value>ESRI (www.esri.com)</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="shortName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:shortName">\n              <sml:value>ESRI</sml:value>\n            </sml:Term>\n          </sml:identifier>\n        </sml:IdentifierList>\n      </sml:identification>\n      <sml:capabilities name="featuresOfInterest">\n        <swe:SimpleDataRecord>\n          <swe:field name="featureOfInterestID">\n            <swe:Text>\n              <swe:value>http://www.52north.org/test/featureOfInterest/2</swe:value>\n            </swe:Text>\n          </swe:field>\n        </swe:SimpleDataRecord>\n      </sml:capabilities>\n      <sml:position name="sensorPosition">\n        <swe:Position referenceFrame="urn:ogc:def:crs:EPSG::4326">\n          <swe:location>\n            <swe:Vector gml:id="STATION_LOCATION">\n              <swe:coordinate name="easting">\n                <swe:Quantity axisID="x">\n                  <swe:uom code="degree"/>\n                  <swe:value>-117.195711</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="northing">\n                <swe:Quantity axisID="y">\n                  <swe:uom code="degree"/>\n                  <swe:value>34.056517</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="altitude">\n                <swe:Quantity axisID="z">\n                  <swe:uom code="m"/>\n                  <swe:value>52.0</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n            </swe:Vector>\n          </swe:location>\n        </swe:Position>\n      </sml:position>\n      <sml:inputs>\n        <sml:InputList>\n          <sml:input name="test_observable_property_2">\n            <swe:ObservableProperty definition="http://www.52north.org/test/observableProperty/2"/>\n          </sml:input>\n        </sml:InputList>\n      </sml:inputs>\n      <sml:outputs>\n        <sml:OutputList>\n          <sml:output name="test_observable_property_2">\n            <swe:Count definition="http://www.52north.org/test/observableProperty/2"/>\n          </sml:output>\n        </sml:OutputList>\n      </sml:outputs>\n    </sml:System>\n  </sml:member>\n</sml:SensorML>'),
(4 , 4 , 1, '2014-08-10 22:05:23.535', NULL, '<sml:SensorML xmlns:sml="http://www.opengis.net/sensorML/1.0.1" version="1.0.1">\n  <sml:member>\n    <sml:System xmlns:swes="http://www.opengis.net/swes/2.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:gml="http://www.opengis.net/gml" xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:sos="http://www.opengis.net/sos/2.0">\n      <sml:identification>\n        <sml:IdentifierList>\n          <sml:identifier name="uniqueID">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:uniqueID">\n              <sml:value>http://www.52north.org/test/procedure/3</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="longName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:longName">\n              <sml:value>Kisters AG (www.kisters.de)</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="shortName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:shortName">\n              <sml:value>Kisters</sml:value>\n            </sml:Term>\n          </sml:identifier>\n        </sml:IdentifierList>\n      </sml:identification>\n      <sml:capabilities name="featuresOfInterest">\n        <swe:SimpleDataRecord>\n          <swe:field name="featureOfInterestID">\n            <swe:Text>\n              <swe:value>http://www.52north.org/test/featureOfInterest/3</swe:value>\n            </swe:Text>\n          </swe:field>\n        </swe:SimpleDataRecord>\n      </sml:capabilities>\n      <sml:position name="sensorPosition">\n        <swe:Position referenceFrame="urn:ogc:def:crs:EPSG::4326">\n          <swe:location>\n            <swe:Vector gml:id="STATION_LOCATION">\n              <swe:coordinate name="easting">\n                <swe:Quantity axisID="x">\n                  <swe:uom code="degree"/>\n                  <swe:value>6.1320144042060925</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="northing">\n                <swe:Quantity axisID="y">\n                  <swe:uom code="degree"/>\n                  <swe:value>50.78570661296184</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="altitude">\n                <swe:Quantity axisID="z">\n                  <swe:uom code="m"/>\n                  <swe:value>52.0</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n            </swe:Vector>\n          </swe:location>\n        </swe:Position>\n      </sml:position>\n      <sml:inputs>\n        <sml:InputList>\n          <sml:input name="test_observable_property_3">\n            <swe:ObservableProperty definition="http://www.52north.org/test/observableProperty/3"/>\n          </sml:input>\n        </sml:InputList>\n      </sml:inputs>\n      <sml:outputs>\n        <sml:OutputList>\n          <sml:output name="test_observable_property_3">\n            <swe:Boolean definition="http://www.52north.org/test/observableProperty/3"/>\n          </sml:output>\n        </sml:OutputList>\n      </sml:outputs>\n    </sml:System>\n  </sml:member>\n</sml:SensorML>'),
(5 , 5 , 1, '2014-08-10 22:05:23.793', NULL, '<sml:SensorML xmlns:sml="http://www.opengis.net/sensorML/1.0.1" version="1.0.1">\n  <sml:member>\n    <sml:System xmlns:swes="http://www.opengis.net/swes/2.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:gml="http://www.opengis.net/gml" xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:sos="http://www.opengis.net/sos/2.0">\n      <sml:identification>\n        <sml:IdentifierList>\n          <sml:identifier name="uniqueID">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:uniqueID">\n              <sml:value>http://www.52north.org/test/procedure/4</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="longName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:longName">\n              <sml:value>Institute for Geoinformatics (http://ifgi.uni-muenster.de/en)</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="shortName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:shortName">\n              <sml:value>IfGI</sml:value>\n            </sml:Term>\n          </sml:identifier>\n        </sml:IdentifierList>\n      </sml:identification>\n      <sml:capabilities name="featuresOfInterest">\n        <swe:SimpleDataRecord>\n          <swe:field name="featureOfInterestID">\n            <swe:Text>\n              <swe:value>http://www.52north.org/test/featureOfInterest/4</swe:value>\n            </swe:Text>\n          </swe:field>\n        </swe:SimpleDataRecord>\n      </sml:capabilities>\n      <sml:position name="sensorPosition">\n        <swe:Position referenceFrame="urn:ogc:def:crs:EPSG::4326">\n          <swe:location>\n            <swe:Vector gml:id="STATION_LOCATION">\n              <swe:coordinate name="easting">\n                <swe:Quantity axisID="x">\n                  <swe:uom code="degree"/>\n                  <swe:value>7.593655600000034</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="northing">\n                <swe:Quantity axisID="y">\n                  <swe:uom code="degree"/>\n                  <swe:value>51.9681661</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="altitude">\n                <swe:Quantity axisID="z">\n                  <swe:uom code="m"/>\n                  <swe:value>52.0</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n            </swe:Vector>\n          </swe:location>\n        </swe:Position>\n      </sml:position>\n      <sml:inputs>\n        <sml:InputList>\n          <sml:input name="test_observable_property_4">\n            <swe:ObservableProperty definition="http://www.52north.org/test/observableProperty/4"/>\n          </sml:input>\n        </sml:InputList>\n      </sml:inputs>\n      <sml:outputs>\n        <sml:OutputList>\n          <sml:output name="test_observable_property_4">\n            <swe:Quantity definition="http://www.52north.org/test/observableProperty/4">\n              <swe:uom code="test_unit_4"/>\n            </swe:Quantity>\n          </sml:output>\n        </sml:OutputList>\n      </sml:outputs>\n    </sml:System>\n  </sml:member>\n</sml:SensorML>'),
(6 , 6 , 1, '2014-08-10 22:05:24.037', NULL, '<sml:SensorML xmlns:sml="http://www.opengis.net/sensorML/1.0.1" version="1.0.1">\n  <sml:member>\n    <sml:System xmlns:swes="http://www.opengis.net/swes/2.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:gml="http://www.opengis.net/gml" xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:sos="http://www.opengis.net/sos/2.0">\n      <sml:identification>\n        <sml:IdentifierList>\n          <sml:identifier name="uniqueID">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:uniqueID">\n              <sml:value>http://www.52north.org/test/procedure/5</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="longName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:longName">\n              <sml:value>Technical University Dresden (http://tu-dresden.de/en)</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="shortName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:shortName">\n              <sml:value>TU-Dresden</sml:value>\n            </sml:Term>\n          </sml:identifier>\n        </sml:IdentifierList>\n      </sml:identification>\n      <sml:capabilities name="featuresOfInterest">\n        <swe:SimpleDataRecord>\n          <swe:field name="featureOfInterestID">\n            <swe:Text>\n              <swe:value>http://www.52north.org/test/featureOfInterest/5</swe:value>\n            </swe:Text>\n          </swe:field>\n        </swe:SimpleDataRecord>\n      </sml:capabilities>\n      <sml:position name="sensorPosition">\n        <swe:Position referenceFrame="urn:ogc:def:crs:EPSG::4326">\n          <swe:location>\n            <swe:Vector gml:id="STATION_LOCATION">\n              <swe:coordinate name="easting">\n                <swe:Quantity axisID="x">\n                  <swe:uom code="degree"/>\n                  <swe:value>13.72375999999997</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="northing">\n                <swe:Quantity axisID="y">\n                  <swe:uom code="degree"/>\n                  <swe:value>51.02881</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="altitude">\n                <swe:Quantity axisID="z">\n                  <swe:uom code="m"/>\n                  <swe:value>52.0</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n            </swe:Vector>\n          </swe:location>\n        </swe:Position>\n      </sml:position>\n      <sml:inputs>\n        <sml:InputList>\n          <sml:input name="test_observable_property_5">\n            <swe:ObservableProperty definition="http://www.52north.org/test/observableProperty/5"/>\n          </sml:input>\n        </sml:InputList>\n      </sml:inputs>\n      <sml:outputs>\n        <sml:OutputList>\n          <sml:output name="test_observable_property_5">\n            <swe:Text definition="http://www.52north.org/test/observableProperty/5"/>\n          </sml:output>\n        </sml:OutputList>\n      </sml:outputs>\n    </sml:System>\n  </sml:member>\n</sml:SensorML>'),
(7 , 7 , 1, '2014-08-10 22:05:24.23', NULL, '<sml:SensorML xmlns:sml="http://www.opengis.net/sensorML/1.0.1" version="1.0.1">\n  <sml:member>\n    <sml:System xmlns:swes="http://www.opengis.net/swes/2.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:gml="http://www.opengis.net/gml" xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:sos="http://www.opengis.net/sos/2.0">\n      <sml:identification>\n        <sml:IdentifierList>\n          <sml:identifier name="uniqueID">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:uniqueID">\n              <sml:value>http://www.52north.org/test/procedure/6</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="longName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:longName">\n              <sml:value>Hochschule Bochum - Bochum University of Applied Sciences (http://www.hochschule-bochum.de/en/)</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="shortName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:shortName">\n              <sml:value>Hochschule Bochum</sml:value>\n            </sml:Term>\n          </sml:identifier>\n        </sml:IdentifierList>\n      </sml:identification>\n      <sml:capabilities name="featuresOfInterest">\n        <swe:SimpleDataRecord>\n          <swe:field name="featureOfInterestID">\n            <swe:Text>\n              <swe:value>http://www.52north.org/test/featureOfInterest/6</swe:value>\n            </swe:Text>\n          </swe:field>\n        </swe:SimpleDataRecord>\n      </sml:capabilities>\n      <sml:position name="sensorPosition">\n        <swe:Position referenceFrame="urn:ogc:def:crs:EPSG::4326">\n          <swe:location>\n            <swe:Vector gml:id="STATION_LOCATION">\n              <swe:coordinate name="easting">\n                <swe:Quantity axisID="x">\n                  <swe:uom code="degree"/>\n                  <swe:value>7.270806</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="northing">\n                <swe:Quantity axisID="y">\n                  <swe:uom code="degree"/>\n                  <swe:value>51.447722</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="altitude">\n                <swe:Quantity axisID="z">\n                  <swe:uom code="m"/>\n                  <swe:value>52.0</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n            </swe:Vector>\n          </swe:location>\n        </swe:Position>\n      </sml:position>\n      <sml:inputs>\n        <sml:InputList>\n          <sml:input name="test_observable_property_6">\n            <swe:ObservableProperty definition="http://www.52north.org/test/observableProperty/6"/>\n          </sml:input>\n        </sml:InputList>\n      </sml:inputs>\n      <sml:outputs>\n        <sml:OutputList>\n          <sml:output name="test_observable_property_6">\n            <swe:Category definition="http://www.52north.org/test/observableProperty/6">\n              <swe:codeSpace xlink:href="test_unit_6"/>\n            </swe:Category>\n          </sml:output>\n        </sml:OutputList>\n      </sml:outputs>\n    </sml:System>\n  </sml:member>\n</sml:SensorML>'),
(8 , 8 , 1, '2014-08-10 22:05:24.611', NULL, '<sml:SensorML xmlns:sml="http://www.opengis.net/sensorML/1.0.1" version="1.0.1">\n  <sml:member>\n    <sml:System xmlns:swes="http://www.opengis.net/swes/2.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:gml="http://www.opengis.net/gml" xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:sos="http://www.opengis.net/sos/2.0">\n      <sml:identification>\n        <sml:IdentifierList>\n          <sml:identifier name="uniqueID">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:uniqueID">\n              <sml:value>http://www.52north.org/test/procedure/7</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="longName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:longName">\n              <sml:value>ITC - University of Twente (http://www.itc.nl/)</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="shortName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:shortName">\n              <sml:value>ITC</sml:value>\n            </sml:Term>\n          </sml:identifier>\n        </sml:IdentifierList>\n      </sml:identification>\n      <sml:capabilities name="featuresOfInterest">\n        <swe:SimpleDataRecord>\n          <swe:field name="featureOfInterestID">\n            <swe:Text>\n              <swe:value>http://www.52north.org/test/featureOfInterest/7</swe:value>\n            </swe:Text>\n          </swe:field>\n        </swe:SimpleDataRecord>\n      </sml:capabilities>\n      <sml:position name="sensorPosition">\n        <swe:Position referenceFrame="urn:ogc:def:crs:EPSG::4326">\n          <swe:location>\n            <swe:Vector gml:id="STATION_LOCATION">\n              <swe:coordinate name="easting">\n                <swe:Quantity axisID="x">\n                  <swe:uom code="degree"/>\n                  <swe:value>4.283393599999954</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="northing">\n                <swe:Quantity axisID="y">\n                  <swe:uom code="degree"/>\n                  <swe:value>52.0464393</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="altitude">\n                <swe:Quantity axisID="z">\n                  <swe:uom code="m"/>\n                  <swe:value>52.0</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n            </swe:Vector>\n          </swe:location>\n        </swe:Position>\n      </sml:position>\n      <sml:inputs>\n        <sml:InputList>\n          <sml:input name="test_observable_property_7">\n            <swe:ObservableProperty definition="http://www.52north.org/test/observableProperty/7"/>\n          </sml:input>\n        </sml:InputList>\n      </sml:inputs>\n      <sml:outputs>\n        <sml:OutputList>\n          <sml:output name="test_observable_property_7">\n            <swe:Category definition="http://www.52north.org/test/observableProperty/7">\n              <swe:codeSpace xlink:href="test_unit_7"/>\n            </swe:Category>\n          </sml:output>\n        </sml:OutputList>\n      </sml:outputs>\n    </sml:System>\n  </sml:member>\n</sml:SensorML>'),
(9 , 9 , 1, '2014-08-10 22:05:24.789', NULL, '<sml:SensorML xmlns:sml="http://www.opengis.net/sensorML/1.0.1" version="1.0.1">\n  <sml:member>\n    <sml:System xmlns:swes="http://www.opengis.net/swes/2.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:gml="http://www.opengis.net/gml" xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:sos="http://www.opengis.net/sos/2.0">\n      <sml:identification>\n        <sml:IdentifierList>\n          <sml:identifier name="uniqueID">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:uniqueID">\n              <sml:value>http://www.52north.org/test/procedure/8</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="longName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:longName">\n              <sml:value>Bundesanstalt für IT-Dienstleistungen im Geschäftsbereich des BMVBS (http://www.dlz-it.de)</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="shortName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:shortName">\n              <sml:value>DLZ-IT</sml:value>\n            </sml:Term>\n          </sml:identifier>\n        </sml:IdentifierList>\n      </sml:identification>\n      <sml:capabilities name="featuresOfInterest">\n        <swe:SimpleDataRecord>\n          <swe:field name="featureOfInterestID">\n            <swe:Text>\n              <swe:value>http://www.52north.org/test/featureOfInterest/8</swe:value>\n            </swe:Text>\n          </swe:field>\n        </swe:SimpleDataRecord>\n      </sml:capabilities>\n      <sml:position name="sensorPosition">\n        <swe:Position referenceFrame="urn:ogc:def:crs:EPSG::4326">\n          <swe:location>\n            <swe:Vector gml:id="STATION_LOCATION">\n              <swe:coordinate name="easting">\n                <swe:Quantity axisID="x">\n                  <swe:uom code="degree"/>\n                  <swe:value>10.94306000000006</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="northing">\n                <swe:Quantity axisID="y">\n                  <swe:uom code="degree"/>\n                  <swe:value>50.68606</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="altitude">\n                <swe:Quantity axisID="z">\n                  <swe:uom code="m"/>\n                  <swe:value>52.0</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n            </swe:Vector>\n          </swe:location>\n        </swe:Position>\n      </sml:position>\n      <sml:inputs>\n        <sml:InputList>\n          <sml:input name="test_observable_property_8">\n            <swe:ObservableProperty definition="http://www.52north.org/test/observableProperty/8"/>\n          </sml:input>\n        </sml:InputList>\n      </sml:inputs>\n      <sml:outputs>\n        <sml:OutputList>\n          <sml:output name="test_observable_property_8">\n            <swe:Category definition="http://www.52north.org/test/observableProperty/8">\n              <swe:codeSpace xlink:href="test_unit_8"/>\n            </swe:Category>\n          </sml:output>\n        </sml:OutputList>\n      </sml:outputs>\n    </sml:System>\n  </sml:member>\n</sml:SensorML>'),
(10, 10, 1, '2014-08-10 22:05:24.965', NULL, '<sml:SensorML xmlns:sml="http://www.opengis.net/sensorML/1.0.1" version="1.0.1">\n  <sml:member>\n    <sml:ProcessModel xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:gml="http://www.opengis.net/gml" xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" gml:id="developer">\n      <gml:description>52°North developer process</gml:description>\n      <gml:name>http://www.52north.org/test/procedure/developer</gml:name>\n      <sml:identification>\n        <sml:IdentifierList>\n          <sml:identifier name="uniqueID">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:uniqueID">\n              <sml:value>http://www.52north.org/test/procedure/developer</sml:value>\n            </sml:Term>\n          </sml:identifier>\n        </sml:IdentifierList>\n      </sml:identification>\n      <sml:inputs>\n        <sml:InputList>\n          <sml:input name="community">\n            <swe:Text definition="http://www.52north.org/test/observableProperty/community"/>\n          </sml:input>\n        </sml:InputList>\n      </sml:inputs>\n      <sml:outputs>\n        <sml:OutputList>\n          <sml:output name="developer">\n            <swe:Text definition="http://www.52north.org/test/observableProperty/developer"/>\n          </sml:output>\n        </sml:OutputList>\n      </sml:outputs>\n      <sml:method>\n        <sml:ProcessMethod>\n          <gml:description>52deg;North Sensor Observation Service development method</gml:description>\n          <sml:contact xlink:arcrole="author">\n            <sml:ResponsibleParty>\n              <sml:individualName>Carsten Hollmann</sml:individualName>\n              <sml:organizationName>52deg;North Initiative for Geospatial Open Source Software GmbH</sml:organizationName>\n              <sml:positionName>code manager</sml:positionName>\n              <sml:contactInfo>\n                <sml:phone>\n                  <sml:voice>+49 (0)251 396371 0</sml:voice>\n                </sml:phone>\n                <sml:address>\n                  <sml:deliveryPoint>Martin-Luther-King-Weg 24</sml:deliveryPoint>\n                  <sml:city>Muenster</sml:city>\n                  <sml:administrativeArea>North Rhine-Westphalia</sml:administrativeArea>\n                  <sml:postalCode>48155</sml:postalCode>\n                  <sml:country>Germany</sml:country>\n                  <sml:electronicMailAddress>info@52north.org</sml:electronicMailAddress>\n                </sml:address>\n              </sml:contactInfo>\n            </sml:ResponsibleParty>\n          </sml:contact>\n          <sml:rules>\n            <sml:RulesDefinition>\n              <gml:description>Inputs are the specifications and the idea, Output is the SOS service</gml:description>\n            </sml:RulesDefinition>\n          </sml:rules>\n          <sml:algorithm>\n            <sml:AlgorithmDefinition>\n              <gml:description>Spec/Idea to code</gml:description>\n            </sml:AlgorithmDefinition>\n          </sml:algorithm>\n        </sml:ProcessMethod>\n      </sml:method>\n    </sml:ProcessModel>\n  </sml:member>\n</sml:SensorML>')
);