--
-- Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
-- Software GmbH
--
-- This program is free software; you can redistribute it and/or modify it
-- under the terms of the GNU General Public License version 2 as published
-- by the Free Software Foundation.
--
-- If the program is linked with libraries which are licensed under one of
-- the following licenses, the combination of the program with the linked
-- library is not considered a "derivative work" of the program:
--
--     - Apache License, version 2.0
--     - Apache Software License, version 1.0
--     - GNU Lesser General Public License, version 3
--     - Mozilla Public License, versions 1.0, 1.1 and 2.0
--     - Common Development and Distribution License (CDDL), version 1.0
--
-- Therefore the distribution of the program linked with libraries licensed
-- under the aforementioned licenses, is permitted by the copyright holders
-- if the distribution is compliant with both the GNU General Public
-- License version 2 and the aforementioned licenses.
--
-- This program is distributed in the hope that it will be useful, but
-- WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
-- Public License for more details.
--

--
-- PostgreSQL database dump
--

-- Dumped from database version 9.3.15
-- Dumped by pg_dump version 9.3.15
-- Started on 2017-01-26 16:25:13 CET

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 1 (class 3079 OID 11799)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 3694 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- TOC entry 2 (class 3079 OID 43311)
-- Name: postgis; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;


--
-- TOC entry 3695 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION postgis; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION postgis IS 'PostGIS geometry, geography, and raster spatial types and functions';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 186 (class 1259 OID 62357)
-- Name: blobvalue; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE blobvalue (
    observationid bigint NOT NULL,
    value oid
);


ALTER TABLE public.blobvalue OWNER TO "user";

--
-- TOC entry 3696 (class 0 OID 0)
-- Dependencies: 186
-- Name: TABLE blobvalue; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE blobvalue IS 'Value table for blob observation';


--
-- TOC entry 187 (class 1259 OID 62362)
-- Name: booleanvalue; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE booleanvalue (
    observationid bigint NOT NULL,
    value character(1),
    CONSTRAINT booleanvalue_value_check CHECK ((value = ANY (ARRAY['T'::bpchar, 'F'::bpchar]))),
    CONSTRAINT booleanvalue_value_check1 CHECK ((value = ANY (ARRAY['T'::bpchar, 'F'::bpchar])))
);


ALTER TABLE public.booleanvalue OWNER TO "user";

--
-- TOC entry 3697 (class 0 OID 0)
-- Dependencies: 187
-- Name: TABLE booleanvalue; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE booleanvalue IS 'Value table for boolean observation';


--
-- TOC entry 188 (class 1259 OID 62369)
-- Name: categoryvalue; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE categoryvalue (
    observationid bigint NOT NULL,
    value character varying(255)
);


ALTER TABLE public.categoryvalue OWNER TO "user";

--
-- TOC entry 3698 (class 0 OID 0)
-- Dependencies: 188
-- Name: TABLE categoryvalue; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE categoryvalue IS 'Value table for category observation';


--
-- TOC entry 189 (class 1259 OID 62374)
-- Name: codespace; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE codespace (
    codespaceid bigint NOT NULL,
    codespace character varying(255) NOT NULL
);


ALTER TABLE public.codespace OWNER TO "user";

--
-- TOC entry 3699 (class 0 OID 0)
-- Dependencies: 189
-- Name: TABLE codespace; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE codespace IS 'Table to store the gml:identifier and gml:name codespace information. Mapping file: mapping/core/Codespace.hbm.xml';


--
-- TOC entry 3700 (class 0 OID 0)
-- Dependencies: 189
-- Name: COLUMN codespace.codespaceid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN codespace.codespaceid IS 'Table primary key, used for relations';


--
-- TOC entry 3701 (class 0 OID 0)
-- Dependencies: 189
-- Name: COLUMN codespace.codespace; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN codespace.codespace IS 'The codespace value';


--
-- TOC entry 222 (class 1259 OID 62944)
-- Name: codespaceid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE codespaceid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.codespaceid_seq OWNER TO "user";

--
-- TOC entry 190 (class 1259 OID 62379)
-- Name: compositephenomenon; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE compositephenomenon (
    parentobservablepropertyid bigint NOT NULL,
    childobservablepropertyid bigint NOT NULL
);


ALTER TABLE public.compositephenomenon OWNER TO "user";

--
-- TOC entry 3702 (class 0 OID 0)
-- Dependencies: 190
-- Name: TABLE compositephenomenon; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE compositephenomenon IS 'NOT YET USED! Relation table to store observableProperty hierarchies, aka compositePhenomenon. E.g. define a parent in a query and all childs are also contained in the response. Mapping file: mapping/transactional/TObservableProperty.hbm.xml';


--
-- TOC entry 3703 (class 0 OID 0)
-- Dependencies: 190
-- Name: COLUMN compositephenomenon.parentobservablepropertyid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN compositephenomenon.parentobservablepropertyid IS 'Foreign Key (FK) to the related parent observableProperty. Contains "observableProperty".observablePropertyid';


--
-- TOC entry 3704 (class 0 OID 0)
-- Dependencies: 190
-- Name: COLUMN compositephenomenon.childobservablepropertyid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN compositephenomenon.childobservablepropertyid IS 'Foreign Key (FK) to the related child observableProperty. Contains "observableProperty".observablePropertyid';


--
-- TOC entry 191 (class 1259 OID 62384)
-- Name: countvalue; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE countvalue (
    observationid bigint NOT NULL,
    value integer
);


ALTER TABLE public.countvalue OWNER TO "user";

--
-- TOC entry 3705 (class 0 OID 0)
-- Dependencies: 191
-- Name: TABLE countvalue; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE countvalue IS 'Value table for count observation';


--
-- TOC entry 192 (class 1259 OID 62389)
-- Name: featureofinterest; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE featureofinterest (
    featureofinterestid bigint NOT NULL,
    hibernatediscriminator character(1) NOT NULL,
    featureofinteresttypeid bigint NOT NULL,
    identifier character varying(255),
    codespace bigint,
    name character varying(255),
    codespacename bigint,
    description character varying(255),
    geom geometry,
    descriptionxml text,
    url character varying(255)
);


ALTER TABLE public.featureofinterest OWNER TO "user";

--
-- TOC entry 3706 (class 0 OID 0)
-- Dependencies: 192
-- Name: TABLE featureofinterest; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE featureofinterest IS 'Table to store the FeatureOfInterest information. Mapping file: mapping/core/FeatureOfInterest.hbm.xml';


--
-- TOC entry 3707 (class 0 OID 0)
-- Dependencies: 192
-- Name: COLUMN featureofinterest.featureofinterestid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN featureofinterest.featureofinterestid IS 'Table primary key, used for relations';


--
-- TOC entry 3708 (class 0 OID 0)
-- Dependencies: 192
-- Name: COLUMN featureofinterest.featureofinteresttypeid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN featureofinterest.featureofinteresttypeid IS 'Relation/foreign key to the featureOfInterestType table. Describes the type of the featureOfInterest. Contains "featureOfInterestType".featureOfInterestTypeId';


--
-- TOC entry 3709 (class 0 OID 0)
-- Dependencies: 192
-- Name: COLUMN featureofinterest.identifier; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN featureofinterest.identifier IS 'The identifier of the featureOfInterest, gml:identifier. Used as parameter for queries. Optional but unique';


--
-- TOC entry 3710 (class 0 OID 0)
-- Dependencies: 192
-- Name: COLUMN featureofinterest.codespace; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN featureofinterest.codespace IS 'Relation/foreign key to the codespace table. Contains the gml:identifier codespace. Optional';


--
-- TOC entry 3711 (class 0 OID 0)
-- Dependencies: 192
-- Name: COLUMN featureofinterest.name; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN featureofinterest.name IS 'The name of the featureOfInterest, gml:name. Optional';


--
-- TOC entry 3712 (class 0 OID 0)
-- Dependencies: 192
-- Name: COLUMN featureofinterest.codespacename; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN featureofinterest.codespacename IS 'The name of the featureOfInterest, gml:name. Optional';


--
-- TOC entry 3713 (class 0 OID 0)
-- Dependencies: 192
-- Name: COLUMN featureofinterest.description; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN featureofinterest.description IS 'Description of the featureOfInterest, gml:description. Optional';


--
-- TOC entry 3714 (class 0 OID 0)
-- Dependencies: 192
-- Name: COLUMN featureofinterest.geom; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN featureofinterest.geom IS 'The geometry of the featureOfInterest (composed of the “latitude” and “longitude”) . Optional';


--
-- TOC entry 3715 (class 0 OID 0)
-- Dependencies: 192
-- Name: COLUMN featureofinterest.descriptionxml; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN featureofinterest.descriptionxml IS 'XML description of the feature, used when transactional profile is supported . Optional';


--
-- TOC entry 3716 (class 0 OID 0)
-- Dependencies: 192
-- Name: COLUMN featureofinterest.url; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN featureofinterest.url IS 'Reference URL to the feature if it is stored in another service, e.g. WFS. Optional but unique';


--
-- TOC entry 223 (class 1259 OID 62946)
-- Name: featureofinterestid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE featureofinterestid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.featureofinterestid_seq OWNER TO "user";

--
-- TOC entry 193 (class 1259 OID 62397)
-- Name: featureofinteresttype; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE featureofinteresttype (
    featureofinteresttypeid bigint NOT NULL,
    featureofinteresttype character varying(255) NOT NULL
);


ALTER TABLE public.featureofinteresttype OWNER TO "user";

--
-- TOC entry 3717 (class 0 OID 0)
-- Dependencies: 193
-- Name: TABLE featureofinteresttype; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE featureofinteresttype IS 'Table to store the FeatureOfInterestType information. Mapping file: mapping/core/FeatureOfInterestType.hbm.xml';


--
-- TOC entry 3718 (class 0 OID 0)
-- Dependencies: 193
-- Name: COLUMN featureofinteresttype.featureofinteresttypeid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN featureofinteresttype.featureofinteresttypeid IS 'Table primary key, used for relations';


--
-- TOC entry 3719 (class 0 OID 0)
-- Dependencies: 193
-- Name: COLUMN featureofinteresttype.featureofinteresttype; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN featureofinteresttype.featureofinteresttype IS 'The featureOfInterestType value, e.g. http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint (OGC OM 2.0 specification) for point features';


--
-- TOC entry 224 (class 1259 OID 62948)
-- Name: featureofinteresttypeid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE featureofinteresttypeid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.featureofinteresttypeid_seq OWNER TO "user";

--
-- TOC entry 194 (class 1259 OID 62402)
-- Name: featurerelation; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE featurerelation (
    parentfeatureid bigint NOT NULL,
    childfeatureid bigint NOT NULL
);


ALTER TABLE public.featurerelation OWNER TO "user";

--
-- TOC entry 3720 (class 0 OID 0)
-- Dependencies: 194
-- Name: TABLE featurerelation; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE featurerelation IS 'Relation table to store feature hierarchies. E.g. define a parent in a query and all childs are also contained in the response. Mapping file: mapping/transactional/TFeatureOfInterest.hbm.xml';


--
-- TOC entry 3721 (class 0 OID 0)
-- Dependencies: 194
-- Name: COLUMN featurerelation.parentfeatureid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN featurerelation.parentfeatureid IS 'Foreign Key (FK) to the related parent featureOfInterest. Contains "featureOfInterest".featureOfInterestid';


--
-- TOC entry 3722 (class 0 OID 0)
-- Dependencies: 194
-- Name: COLUMN featurerelation.childfeatureid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN featurerelation.childfeatureid IS 'Foreign Key (FK) to the related child featureOfInterest. Contains "featureOfInterest".featureOfInterestid';


--
-- TOC entry 195 (class 1259 OID 62407)
-- Name: geometryvalue; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE geometryvalue (
    observationid bigint NOT NULL,
    value geometry
);


ALTER TABLE public.geometryvalue OWNER TO "user";

--
-- TOC entry 3723 (class 0 OID 0)
-- Dependencies: 195
-- Name: TABLE geometryvalue; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE geometryvalue IS 'Value table for geometry observation';


--
-- TOC entry 196 (class 1259 OID 62415)
-- Name: i18nfeatureofinterest; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE i18nfeatureofinterest (
    id bigint NOT NULL,
    objectid bigint NOT NULL,
    locale character varying(255) NOT NULL,
    name character varying(255),
    description character varying(255)
);


ALTER TABLE public.i18nfeatureofinterest OWNER TO "user";

--
-- TOC entry 3724 (class 0 OID 0)
-- Dependencies: 196
-- Name: TABLE i18nfeatureofinterest; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE i18nfeatureofinterest IS 'Table to i18n metadata for the featureOfInterest. Mapping file: mapping/i18n/HibernateI18NFeatureOfInterestMetadata.hbm.xml';


--
-- TOC entry 3725 (class 0 OID 0)
-- Dependencies: 196
-- Name: COLUMN i18nfeatureofinterest.id; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18nfeatureofinterest.id IS 'Table primary key';


--
-- TOC entry 3726 (class 0 OID 0)
-- Dependencies: 196
-- Name: COLUMN i18nfeatureofinterest.objectid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18nfeatureofinterest.objectid IS 'Foreign Key (FK) to the related featureOfInterest. Contains "featureOfInterest".featureOfInterestid';


--
-- TOC entry 3727 (class 0 OID 0)
-- Dependencies: 196
-- Name: COLUMN i18nfeatureofinterest.locale; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18nfeatureofinterest.locale IS 'Locale/language identification, e.g. eng, ger';


--
-- TOC entry 3728 (class 0 OID 0)
-- Dependencies: 196
-- Name: COLUMN i18nfeatureofinterest.name; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18nfeatureofinterest.name IS 'Locale/language specific name of the featureOfInterest';


--
-- TOC entry 3729 (class 0 OID 0)
-- Dependencies: 196
-- Name: COLUMN i18nfeatureofinterest.description; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18nfeatureofinterest.description IS 'Locale/language specific description of the featureOfInterest';


--
-- TOC entry 228 (class 1259 OID 62956)
-- Name: i18nfeatureofinterestid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE i18nfeatureofinterestid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.i18nfeatureofinterestid_seq OWNER TO "user";

--
-- TOC entry 197 (class 1259 OID 62423)
-- Name: i18nobservableproperty; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE i18nobservableproperty (
    id bigint NOT NULL,
    objectid bigint NOT NULL,
    locale character varying(255) NOT NULL,
    name character varying(255),
    description character varying(255)
);


ALTER TABLE public.i18nobservableproperty OWNER TO "user";

--
-- TOC entry 3730 (class 0 OID 0)
-- Dependencies: 197
-- Name: TABLE i18nobservableproperty; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE i18nobservableproperty IS 'Table to i18n metadata for the observableProperty/phenomenon. Mapping file: mapping/i18n/HibernateI18NObservablePropertyMetadata.hbm.xml';


--
-- TOC entry 3731 (class 0 OID 0)
-- Dependencies: 197
-- Name: COLUMN i18nobservableproperty.id; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18nobservableproperty.id IS 'Table primary key';


--
-- TOC entry 3732 (class 0 OID 0)
-- Dependencies: 197
-- Name: COLUMN i18nobservableproperty.objectid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18nobservableproperty.objectid IS 'Foreign Key (FK) to the related observableProperty. Contains "observableProperty".observablePropertyid';


--
-- TOC entry 3733 (class 0 OID 0)
-- Dependencies: 197
-- Name: COLUMN i18nobservableproperty.locale; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18nobservableproperty.locale IS 'Locale/language identification, e.g. eng, ger';


--
-- TOC entry 3734 (class 0 OID 0)
-- Dependencies: 197
-- Name: COLUMN i18nobservableproperty.name; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18nobservableproperty.name IS 'Locale/language specific name of the observableProperty';


--
-- TOC entry 3735 (class 0 OID 0)
-- Dependencies: 197
-- Name: COLUMN i18nobservableproperty.description; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18nobservableproperty.description IS 'Locale/language specific description of the observableProperty';


--
-- TOC entry 225 (class 1259 OID 62950)
-- Name: i18nobspropid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE i18nobspropid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.i18nobspropid_seq OWNER TO "user";

--
-- TOC entry 198 (class 1259 OID 62431)
-- Name: i18noffering; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE i18noffering (
    id bigint NOT NULL,
    objectid bigint NOT NULL,
    locale character varying(255) NOT NULL,
    name character varying(255),
    description character varying(255)
);


ALTER TABLE public.i18noffering OWNER TO "user";

--
-- TOC entry 3736 (class 0 OID 0)
-- Dependencies: 198
-- Name: TABLE i18noffering; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE i18noffering IS 'Table to i18n metadata for the offering. Mapping file: mapping/i18n/HibernateI18NOfferingMetadata.hbm.xml';


--
-- TOC entry 3737 (class 0 OID 0)
-- Dependencies: 198
-- Name: COLUMN i18noffering.id; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18noffering.id IS 'Table primary key';


--
-- TOC entry 3738 (class 0 OID 0)
-- Dependencies: 198
-- Name: COLUMN i18noffering.objectid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18noffering.objectid IS 'Foreign Key (FK) to the related offering. Contains "offering".offeringid';


--
-- TOC entry 3739 (class 0 OID 0)
-- Dependencies: 198
-- Name: COLUMN i18noffering.locale; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18noffering.locale IS 'Locale/language identification, e.g. eng, ger';


--
-- TOC entry 3740 (class 0 OID 0)
-- Dependencies: 198
-- Name: COLUMN i18noffering.name; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18noffering.name IS 'Locale/language specific name of the offering';


--
-- TOC entry 3741 (class 0 OID 0)
-- Dependencies: 198
-- Name: COLUMN i18noffering.description; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18noffering.description IS 'Locale/language specific description of the offering';


--
-- TOC entry 226 (class 1259 OID 62952)
-- Name: i18nofferingid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE i18nofferingid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.i18nofferingid_seq OWNER TO "user";

--
-- TOC entry 199 (class 1259 OID 62439)
-- Name: i18nprocedure; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE i18nprocedure (
    id bigint NOT NULL,
    objectid bigint NOT NULL,
    locale character varying(255) NOT NULL,
    name character varying(255),
    description character varying(255),
    shortname character varying(255),
    longname character varying(255)
);


ALTER TABLE public.i18nprocedure OWNER TO "user";

--
-- TOC entry 3742 (class 0 OID 0)
-- Dependencies: 199
-- Name: TABLE i18nprocedure; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE i18nprocedure IS 'Table to i18n metadata for the procedure. Mapping file: mapping/i18n/HibernateI18NProcedureMetadata.hbm.xml';


--
-- TOC entry 3743 (class 0 OID 0)
-- Dependencies: 199
-- Name: COLUMN i18nprocedure.id; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18nprocedure.id IS 'Table primary key';


--
-- TOC entry 3744 (class 0 OID 0)
-- Dependencies: 199
-- Name: COLUMN i18nprocedure.objectid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18nprocedure.objectid IS 'Foreign Key (FK) to the related procedure. Contains "procedure".procedureid';


--
-- TOC entry 3745 (class 0 OID 0)
-- Dependencies: 199
-- Name: COLUMN i18nprocedure.locale; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18nprocedure.locale IS 'Locale/language identification, e.g. eng, ger';


--
-- TOC entry 3746 (class 0 OID 0)
-- Dependencies: 199
-- Name: COLUMN i18nprocedure.name; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18nprocedure.name IS 'Locale/language specific name of the procedure';


--
-- TOC entry 3747 (class 0 OID 0)
-- Dependencies: 199
-- Name: COLUMN i18nprocedure.description; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18nprocedure.description IS 'Locale/language specific description of the procedure';


--
-- TOC entry 3748 (class 0 OID 0)
-- Dependencies: 199
-- Name: COLUMN i18nprocedure.shortname; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18nprocedure.shortname IS 'Locale/language specific shortname of the procedure';


--
-- TOC entry 3749 (class 0 OID 0)
-- Dependencies: 199
-- Name: COLUMN i18nprocedure.longname; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN i18nprocedure.longname IS 'Locale/language specific longname of the procedure';


--
-- TOC entry 227 (class 1259 OID 62954)
-- Name: i18nprocedureid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE i18nprocedureid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.i18nprocedureid_seq OWNER TO "user";

--
-- TOC entry 200 (class 1259 OID 62447)
-- Name: numericvalue; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE numericvalue (
    observationid bigint NOT NULL,
    value double precision
);


ALTER TABLE public.numericvalue OWNER TO "user";

--
-- TOC entry 3750 (class 0 OID 0)
-- Dependencies: 200
-- Name: TABLE numericvalue; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE numericvalue IS 'Value table for numeric/Measurment observation';


--
-- TOC entry 201 (class 1259 OID 62452)
-- Name: observableproperty; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE observableproperty (
    observablepropertyid bigint NOT NULL,
    hibernatediscriminator character(1) NOT NULL,
    identifier character varying(255) NOT NULL,
    codespace bigint,
    name character varying(255),
    codespacename bigint,
    description character varying(255),
    disabled character(1) DEFAULT 'F'::bpchar NOT NULL,
    CONSTRAINT observableproperty_disabled_check CHECK ((disabled = ANY (ARRAY['T'::bpchar, 'F'::bpchar])))
);


ALTER TABLE public.observableproperty OWNER TO "user";

--
-- TOC entry 3751 (class 0 OID 0)
-- Dependencies: 201
-- Name: TABLE observableproperty; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE observableproperty IS 'Table to store the ObservedProperty/Phenomenon information. Mapping file: mapping/core/ObservableProperty.hbm.xml';


--
-- TOC entry 3752 (class 0 OID 0)
-- Dependencies: 201
-- Name: COLUMN observableproperty.observablepropertyid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN observableproperty.observablepropertyid IS 'Table primary key, used for relations';


--
-- TOC entry 3753 (class 0 OID 0)
-- Dependencies: 201
-- Name: COLUMN observableproperty.identifier; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN observableproperty.identifier IS 'The identifier of the observableProperty, gml:identifier. Used as parameter for queries. Unique';


--
-- TOC entry 3754 (class 0 OID 0)
-- Dependencies: 201
-- Name: COLUMN observableproperty.codespace; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN observableproperty.codespace IS 'Relation/foreign key to the codespace table. Contains the gml:identifier codespace. Optional';


--
-- TOC entry 3755 (class 0 OID 0)
-- Dependencies: 201
-- Name: COLUMN observableproperty.name; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN observableproperty.name IS 'The name of the observableProperty, gml:name. Optional';


--
-- TOC entry 3756 (class 0 OID 0)
-- Dependencies: 201
-- Name: COLUMN observableproperty.codespacename; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN observableproperty.codespacename IS 'Relation/foreign key to the codespace table. Contains the gml:name codespace. Optional';


--
-- TOC entry 3757 (class 0 OID 0)
-- Dependencies: 201
-- Name: COLUMN observableproperty.description; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN observableproperty.description IS 'Description of the observableProperty, gml:description. Optional';


--
-- TOC entry 3758 (class 0 OID 0)
-- Dependencies: 201
-- Name: COLUMN observableproperty.disabled; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN observableproperty.disabled IS 'For later use by the SOS. Indicator if this observableProperty should not be provided by the SOS.';


--
-- TOC entry 229 (class 1259 OID 62958)
-- Name: observablepropertyid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE observablepropertyid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.observablepropertyid_seq OWNER TO "user";

--
-- TOC entry 202 (class 1259 OID 62462)
-- Name: observation; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE observation (
    observationid bigint NOT NULL,
    seriesid bigint NOT NULL,
    phenomenontimestart timestamp without time zone NOT NULL,
    phenomenontimeend timestamp without time zone NOT NULL,
    resulttime timestamp without time zone NOT NULL,
    identifier character varying(255),
    codespace bigint,
    name character varying(255),
    codespacename bigint,
    description character varying(255),
    deleted character(1) DEFAULT 'F'::bpchar NOT NULL,
    validtimestart timestamp without time zone,
    validtimeend timestamp without time zone,
    unitid bigint,
    samplinggeometry geometry,
    CONSTRAINT observation_deleted_check CHECK ((deleted = ANY (ARRAY['T'::bpchar, 'F'::bpchar])))
);


ALTER TABLE public.observation OWNER TO "user";

--
-- TOC entry 3759 (class 0 OID 0)
-- Dependencies: 202
-- Name: TABLE observation; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE observation IS 'Stores the observations. Mapping file: mapping/series/observation/SeriesObservation.hbm.xml';


--
-- TOC entry 203 (class 1259 OID 62472)
-- Name: observationconstellation; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE observationconstellation (
    observationconstellationid bigint NOT NULL,
    observablepropertyid bigint NOT NULL,
    procedureid bigint NOT NULL,
    observationtypeid bigint,
    offeringid bigint NOT NULL,
    deleted character(1) DEFAULT 'F'::bpchar NOT NULL,
    hiddenchild character(1) DEFAULT 'F'::bpchar NOT NULL,
    CONSTRAINT observationconstellation_deleted_check CHECK ((deleted = ANY (ARRAY['T'::bpchar, 'F'::bpchar]))),
    CONSTRAINT observationconstellation_hiddenchild_check CHECK ((hiddenchild = ANY (ARRAY['T'::bpchar, 'F'::bpchar])))
);


ALTER TABLE public.observationconstellation OWNER TO "user";

--
-- TOC entry 3760 (class 0 OID 0)
-- Dependencies: 203
-- Name: TABLE observationconstellation; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE observationconstellation IS 'Table to store the ObservationConstellation information. Contains information about the constellation of observableProperty, procedure, offering and the observationType. Mapping file: mapping/core/ObservationConstellation.hbm.xml';


--
-- TOC entry 3761 (class 0 OID 0)
-- Dependencies: 203
-- Name: COLUMN observationconstellation.observationconstellationid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN observationconstellation.observationconstellationid IS 'Table primary key, used for relations';


--
-- TOC entry 3762 (class 0 OID 0)
-- Dependencies: 203
-- Name: COLUMN observationconstellation.observablepropertyid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN observationconstellation.observablepropertyid IS 'Foreign Key (FK) to the related observableProperty. Contains "observableproperty".observablepropertyid';


--
-- TOC entry 3763 (class 0 OID 0)
-- Dependencies: 203
-- Name: COLUMN observationconstellation.procedureid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN observationconstellation.procedureid IS 'Foreign Key (FK) to the related procedure. Contains "procedure".procedureid';


--
-- TOC entry 3764 (class 0 OID 0)
-- Dependencies: 203
-- Name: COLUMN observationconstellation.observationtypeid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN observationconstellation.observationtypeid IS 'Foreign Key (FK) to the related observableProperty. Contains "observationtype".observationtypeid';


--
-- TOC entry 3765 (class 0 OID 0)
-- Dependencies: 203
-- Name: COLUMN observationconstellation.offeringid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN observationconstellation.offeringid IS 'Foreign Key (FK) to the related observableProperty. Contains "offering".offeringid';


--
-- TOC entry 3766 (class 0 OID 0)
-- Dependencies: 203
-- Name: COLUMN observationconstellation.deleted; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN observationconstellation.deleted IS 'Flag to indicate that this observationConstellation is deleted or not. Set if the related procedure is deleted via DeleteSensor operation (OGC SWES 2.0 - DeleteSensor operation)';


--
-- TOC entry 3767 (class 0 OID 0)
-- Dependencies: 203
-- Name: COLUMN observationconstellation.hiddenchild; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN observationconstellation.hiddenchild IS 'Flag to indicate that this observationConstellations procedure is a child procedure of another procedure. If true, the related procedure is not contained in OGC SOS 2.0 Capabilities but in OGC SOS 1.0.0 Capabilities.';


--
-- TOC entry 230 (class 1259 OID 62960)
-- Name: observationconstellationid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE observationconstellationid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.observationconstellationid_seq OWNER TO "user";

--
-- TOC entry 204 (class 1259 OID 62481)
-- Name: observationhasoffering; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE observationhasoffering (
    observationid bigint NOT NULL,
    offeringid bigint NOT NULL
);


ALTER TABLE public.observationhasoffering OWNER TO "user";

--
-- TOC entry 3768 (class 0 OID 0)
-- Dependencies: 204
-- Name: TABLE observationhasoffering; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE observationhasoffering IS 'Table to store relations between observation and associated offerings. Mapping file: mapping/ereporting/EReportingObservation.hbm.xml';


--
-- TOC entry 231 (class 1259 OID 62962)
-- Name: observationid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE observationid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.observationid_seq OWNER TO "user";

--
-- TOC entry 205 (class 1259 OID 62486)
-- Name: observationtype; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE observationtype (
    observationtypeid bigint NOT NULL,
    observationtype character varying(255) NOT NULL
);


ALTER TABLE public.observationtype OWNER TO "user";

--
-- TOC entry 3769 (class 0 OID 0)
-- Dependencies: 205
-- Name: TABLE observationtype; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE observationtype IS 'Table to store the observationTypes. Mapping file: mapping/core/ObservationType.hbm.xml';


--
-- TOC entry 3770 (class 0 OID 0)
-- Dependencies: 205
-- Name: COLUMN observationtype.observationtypeid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN observationtype.observationtypeid IS 'Table primary key, used for relations';


--
-- TOC entry 3771 (class 0 OID 0)
-- Dependencies: 205
-- Name: COLUMN observationtype.observationtype; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN observationtype.observationtype IS 'The observationType value, e.g. http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement (OGC OM 2.0 specification) for OM_Measurement';


--
-- TOC entry 232 (class 1259 OID 62964)
-- Name: observationtypeid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE observationtypeid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.observationtypeid_seq OWNER TO "user";

--
-- TOC entry 206 (class 1259 OID 62491)
-- Name: offering; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE offering (
    offeringid bigint NOT NULL,
    hibernatediscriminator character(1) NOT NULL,
    identifier character varying(255) NOT NULL,
    codespace bigint,
    name character varying(255),
    codespacename bigint,
    description character varying(255),
    disabled character(1) DEFAULT 'F'::bpchar NOT NULL,
    CONSTRAINT offering_disabled_check CHECK ((disabled = ANY (ARRAY['T'::bpchar, 'F'::bpchar])))
);


ALTER TABLE public.offering OWNER TO "user";

--
-- TOC entry 3772 (class 0 OID 0)
-- Dependencies: 206
-- Name: TABLE offering; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE offering IS 'Table to store the offering information. Mapping file: mapping/core/Offering.hbm.xml';


--
-- TOC entry 3773 (class 0 OID 0)
-- Dependencies: 206
-- Name: COLUMN offering.offeringid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN offering.offeringid IS 'Table primary key, used for relations';


--
-- TOC entry 3774 (class 0 OID 0)
-- Dependencies: 206
-- Name: COLUMN offering.identifier; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN offering.identifier IS 'The identifier of the offering, gml:identifier. Used as parameter for queries. Unique';


--
-- TOC entry 3775 (class 0 OID 0)
-- Dependencies: 206
-- Name: COLUMN offering.codespace; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN offering.codespace IS 'Relation/foreign key to the codespace table. Contains the gml:identifier codespace. Optional';


--
-- TOC entry 3776 (class 0 OID 0)
-- Dependencies: 206
-- Name: COLUMN offering.name; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN offering.name IS 'The name of the offering, gml:name. If available, displyed in the contents of the Capabilites. Optional';


--
-- TOC entry 3777 (class 0 OID 0)
-- Dependencies: 206
-- Name: COLUMN offering.codespacename; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN offering.codespacename IS 'Relation/foreign key to the codespace table. Contains the gml:name codespace. Optional';


--
-- TOC entry 3778 (class 0 OID 0)
-- Dependencies: 206
-- Name: COLUMN offering.description; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN offering.description IS 'Description of the offering, gml:description. Optional';


--
-- TOC entry 3779 (class 0 OID 0)
-- Dependencies: 206
-- Name: COLUMN offering.disabled; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN offering.disabled IS 'For later use by the SOS. Indicator if this offering should not be provided by the SOS.';


--
-- TOC entry 207 (class 1259 OID 62501)
-- Name: offeringallowedfeaturetype; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE offeringallowedfeaturetype (
    offeringid bigint NOT NULL,
    featureofinteresttypeid bigint NOT NULL
);


ALTER TABLE public.offeringallowedfeaturetype OWNER TO "user";

--
-- TOC entry 3780 (class 0 OID 0)
-- Dependencies: 207
-- Name: TABLE offeringallowedfeaturetype; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE offeringallowedfeaturetype IS 'Table to store relations between offering and allowed featureOfInterestTypes, defined in InsertSensor request. Mapping file: mapping/transactional/TOffering.hbm.xml';


--
-- TOC entry 3781 (class 0 OID 0)
-- Dependencies: 207
-- Name: COLUMN offeringallowedfeaturetype.offeringid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN offeringallowedfeaturetype.offeringid IS 'Foreign Key (FK) to the related offering. Contains "offering".offeringid';


--
-- TOC entry 3782 (class 0 OID 0)
-- Dependencies: 207
-- Name: COLUMN offeringallowedfeaturetype.featureofinteresttypeid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN offeringallowedfeaturetype.featureofinteresttypeid IS 'Foreign Key (FK) to the related featureOfInterestTypeId. Contains "featureOfInterestType".featureOfInterestTypeId';


--
-- TOC entry 208 (class 1259 OID 62506)
-- Name: offeringallowedobservationtype; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE offeringallowedobservationtype (
    offeringid bigint NOT NULL,
    observationtypeid bigint NOT NULL
);


ALTER TABLE public.offeringallowedobservationtype OWNER TO "user";

--
-- TOC entry 3783 (class 0 OID 0)
-- Dependencies: 208
-- Name: TABLE offeringallowedobservationtype; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE offeringallowedobservationtype IS 'Table to store relations between offering and allowed observationTypes, defined in InsertSensor request. Mapping file: mapping/transactional/TOffering.hbm.xml';


--
-- TOC entry 3784 (class 0 OID 0)
-- Dependencies: 208
-- Name: COLUMN offeringallowedobservationtype.offeringid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN offeringallowedobservationtype.offeringid IS 'Foreign Key (FK) to the related offering. Contains "offering".offeringid';


--
-- TOC entry 3785 (class 0 OID 0)
-- Dependencies: 208
-- Name: COLUMN offeringallowedobservationtype.observationtypeid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN offeringallowedobservationtype.observationtypeid IS 'Foreign Key (FK) to the related observationType. Contains "observationType".observationTypeId';


--
-- TOC entry 209 (class 1259 OID 62511)
-- Name: offeringhasrelatedfeature; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE offeringhasrelatedfeature (
    offeringid bigint NOT NULL,
    relatedfeatureid bigint NOT NULL
);


ALTER TABLE public.offeringhasrelatedfeature OWNER TO "user";

--
-- TOC entry 3786 (class 0 OID 0)
-- Dependencies: 209
-- Name: TABLE offeringhasrelatedfeature; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE offeringhasrelatedfeature IS 'Relation table to store relatedFeatures and their associated offerings. Mapping file: mapping/transactionl/RelatedFeature.hbm.xml';


--
-- TOC entry 3787 (class 0 OID 0)
-- Dependencies: 209
-- Name: COLUMN offeringhasrelatedfeature.offeringid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN offeringhasrelatedfeature.offeringid IS 'Foreign Key (FK) to the related offering. Contains "offering".offeringid';


--
-- TOC entry 3788 (class 0 OID 0)
-- Dependencies: 209
-- Name: COLUMN offeringhasrelatedfeature.relatedfeatureid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN offeringhasrelatedfeature.relatedfeatureid IS 'Foreign Key (FK) to the related reladedFeature. Contains "reladedFeature".relatedFeatureId';


--
-- TOC entry 233 (class 1259 OID 62966)
-- Name: offeringid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE offeringid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.offeringid_seq OWNER TO "user";

--
-- TOC entry 210 (class 1259 OID 62516)
-- Name: parameter; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE parameter (
    parameterid bigint NOT NULL,
    observationid bigint NOT NULL,
    definition character varying(255) NOT NULL,
    title character varying(255),
    value oid NOT NULL
);


ALTER TABLE public.parameter OWNER TO "user";

--
-- TOC entry 3789 (class 0 OID 0)
-- Dependencies: 210
-- Name: TABLE parameter; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE parameter IS 'NOT YET USED! Table to store additional obervation information (om:parameter). Mapping file: mapping/transactional/Parameter.hbm.xml';


--
-- TOC entry 3790 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN parameter.parameterid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN parameter.parameterid IS 'Table primary key';


--
-- TOC entry 3791 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN parameter.observationid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN parameter.observationid IS 'Foreign Key (FK) to the related observation. Contains "observation".observationid';


--
-- TOC entry 3792 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN parameter.definition; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN parameter.definition IS 'Definition of the additional information';


--
-- TOC entry 3793 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN parameter.title; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN parameter.title IS 'optional title of the additional information. Optional';


--
-- TOC entry 3794 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN parameter.value; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN parameter.value IS 'Value of the additional information';


--
-- TOC entry 234 (class 1259 OID 62968)
-- Name: parameterid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE parameterid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.parameterid_seq OWNER TO "user";

--
-- TOC entry 235 (class 1259 OID 62970)
-- Name: procdescformatid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE procdescformatid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.procdescformatid_seq OWNER TO "user";

--
-- TOC entry 185 (class 1259 OID 62343)
-- Name: procedure; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE procedure (
    procedureid bigint NOT NULL,
    hibernatediscriminator character(1) NOT NULL,
    proceduredescriptionformatid bigint NOT NULL,
    identifier character varying(255) NOT NULL,
    codespace bigint,
    name character varying(255),
    codespacename bigint,
    description character varying(255),
    deleted character(1) DEFAULT 'F'::bpchar NOT NULL,
    disabled character(1) DEFAULT 'F'::bpchar NOT NULL,
    descriptionfile text,
    referenceflag character(1) DEFAULT 'F'::bpchar,
    CONSTRAINT procedure_deleted_check CHECK ((deleted = ANY (ARRAY['T'::bpchar, 'F'::bpchar]))),
    CONSTRAINT procedure_disabled_check CHECK ((disabled = ANY (ARRAY['T'::bpchar, 'F'::bpchar]))),
    CONSTRAINT procedure_referenceflag_check CHECK ((referenceflag = ANY (ARRAY['T'::bpchar, 'F'::bpchar])))
);


ALTER TABLE public.procedure OWNER TO "user";

--
-- TOC entry 3795 (class 0 OID 0)
-- Dependencies: 185
-- Name: TABLE procedure; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE procedure IS 'Table to store the procedure/sensor. Mapping file: mapping/core/Procedure.hbm.xml';


--
-- TOC entry 3796 (class 0 OID 0)
-- Dependencies: 185
-- Name: COLUMN procedure.procedureid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN procedure.procedureid IS 'Table primary key, used for relations';


--
-- TOC entry 3797 (class 0 OID 0)
-- Dependencies: 185
-- Name: COLUMN procedure.proceduredescriptionformatid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN procedure.proceduredescriptionformatid IS 'Relation/foreign key to the procedureDescriptionFormat table. Describes the format of the procedure description.';


--
-- TOC entry 3798 (class 0 OID 0)
-- Dependencies: 185
-- Name: COLUMN procedure.identifier; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN procedure.identifier IS 'The identifier of the procedure, gml:identifier. Used as parameter for queries. Unique';


--
-- TOC entry 3799 (class 0 OID 0)
-- Dependencies: 185
-- Name: COLUMN procedure.codespace; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN procedure.codespace IS 'Relation/foreign key to the codespace table. Contains the gml:identifier codespace. Optional';


--
-- TOC entry 3800 (class 0 OID 0)
-- Dependencies: 185
-- Name: COLUMN procedure.name; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN procedure.name IS 'The name of the procedure, gml:name. Optional';


--
-- TOC entry 3801 (class 0 OID 0)
-- Dependencies: 185
-- Name: COLUMN procedure.codespacename; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN procedure.codespacename IS 'Relation/foreign key to the codespace table. Contains the gml:name codespace. Optional';


--
-- TOC entry 3802 (class 0 OID 0)
-- Dependencies: 185
-- Name: COLUMN procedure.description; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN procedure.description IS 'Description of the procedure, gml:description. Optional';


--
-- TOC entry 3803 (class 0 OID 0)
-- Dependencies: 185
-- Name: COLUMN procedure.deleted; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN procedure.deleted IS 'Flag to indicate that this procedure is deleted or not (OGC SWES 2.0 - DeleteSensor operation)';


--
-- TOC entry 3804 (class 0 OID 0)
-- Dependencies: 185
-- Name: COLUMN procedure.disabled; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN procedure.disabled IS 'For later use by the SOS. Indicator if this procedure should not be provided by the SOS.';


--
-- TOC entry 3805 (class 0 OID 0)
-- Dependencies: 185
-- Name: COLUMN procedure.descriptionfile; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN procedure.descriptionfile IS 'Field for full (XML) encoded procedure description or link to a procedure description file. Optional';


--
-- TOC entry 3806 (class 0 OID 0)
-- Dependencies: 185
-- Name: COLUMN procedure.referenceflag; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN procedure.referenceflag IS 'Flag to indicate that this procedure is a reference procedure of another procedure. Not used by the SOS but by the Sensor Web REST-API';


--
-- TOC entry 211 (class 1259 OID 62524)
-- Name: proceduredescriptionformat; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE proceduredescriptionformat (
    proceduredescriptionformatid bigint NOT NULL,
    proceduredescriptionformat character varying(255) NOT NULL
);


ALTER TABLE public.proceduredescriptionformat OWNER TO "user";

--
-- TOC entry 3807 (class 0 OID 0)
-- Dependencies: 211
-- Name: TABLE proceduredescriptionformat; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE proceduredescriptionformat IS 'Table to store the ProcedureDescriptionFormat information of procedures. Mapping file: mapping/core/ProcedureDescriptionFormat.hbm.xml';


--
-- TOC entry 3808 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN proceduredescriptionformat.proceduredescriptionformatid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN proceduredescriptionformat.proceduredescriptionformatid IS 'Table primary key, used for relations';


--
-- TOC entry 3809 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN proceduredescriptionformat.proceduredescriptionformat; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN proceduredescriptionformat.proceduredescriptionformat IS 'The procedureDescriptionFormat value, e.g. http://www.opengis.net/sensorML/1.0.1 for procedures descriptions as specified in OGC SensorML 1.0.1';


--
-- TOC entry 236 (class 1259 OID 62972)
-- Name: procedureid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE procedureid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.procedureid_seq OWNER TO "user";

--
-- TOC entry 212 (class 1259 OID 62529)
-- Name: relatedfeature; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE relatedfeature (
    relatedfeatureid bigint NOT NULL,
    featureofinterestid bigint NOT NULL
);


ALTER TABLE public.relatedfeature OWNER TO "user";

--
-- TOC entry 3810 (class 0 OID 0)
-- Dependencies: 212
-- Name: TABLE relatedfeature; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE relatedfeature IS 'Table to store related feature information used in the OGC SOS 2.0 Capabilities (See also OGC SWES 2.0). Mapping file: mapping/transactionl/RelatedFeature.hbm.xml';


--
-- TOC entry 3811 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN relatedfeature.relatedfeatureid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN relatedfeature.relatedfeatureid IS 'Table primary key, used for relations';


--
-- TOC entry 3812 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN relatedfeature.featureofinterestid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN relatedfeature.featureofinterestid IS 'Foreign Key (FK) to the related featureOfInterest. Contains "featureOfInterest".featureOfInterestid';


--
-- TOC entry 213 (class 1259 OID 62534)
-- Name: relatedfeaturehasrole; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE relatedfeaturehasrole (
    relatedfeatureid bigint NOT NULL,
    relatedfeatureroleid bigint NOT NULL
);


ALTER TABLE public.relatedfeaturehasrole OWNER TO "user";

--
-- TOC entry 3813 (class 0 OID 0)
-- Dependencies: 213
-- Name: TABLE relatedfeaturehasrole; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE relatedfeaturehasrole IS 'Relation table to store relatedFeatures and their associated relatedFeatureRoles. Mapping file: mapping/transactionl/RelatedFeature.hbm.xml';


--
-- TOC entry 3814 (class 0 OID 0)
-- Dependencies: 213
-- Name: COLUMN relatedfeaturehasrole.relatedfeatureid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN relatedfeaturehasrole.relatedfeatureid IS 'Foreign Key (FK) to the related relatedFeature. Contains "relatedFeature".relatedFeatureid';


--
-- TOC entry 3815 (class 0 OID 0)
-- Dependencies: 213
-- Name: COLUMN relatedfeaturehasrole.relatedfeatureroleid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN relatedfeaturehasrole.relatedfeatureroleid IS 'Foreign Key (FK) to the related relatedFeatureRole. Contains "relatedFeatureRole".relatedFeatureRoleid';


--
-- TOC entry 237 (class 1259 OID 62974)
-- Name: relatedfeatureid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE relatedfeatureid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.relatedfeatureid_seq OWNER TO "user";

--
-- TOC entry 214 (class 1259 OID 62539)
-- Name: relatedfeaturerole; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE relatedfeaturerole (
    relatedfeatureroleid bigint NOT NULL,
    relatedfeaturerole character varying(255) NOT NULL
);


ALTER TABLE public.relatedfeaturerole OWNER TO "user";

--
-- TOC entry 3816 (class 0 OID 0)
-- Dependencies: 214
-- Name: TABLE relatedfeaturerole; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE relatedfeaturerole IS 'Table to store related feature role information used in the OGC SOS 2.0 Capabilities (See also OGC SWES 2.0). Mapping file: mapping/transactionl/RelatedFeatureRole.hbm.xml';


--
-- TOC entry 3817 (class 0 OID 0)
-- Dependencies: 214
-- Name: COLUMN relatedfeaturerole.relatedfeatureroleid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN relatedfeaturerole.relatedfeatureroleid IS 'Table primary key, used for relations';


--
-- TOC entry 3818 (class 0 OID 0)
-- Dependencies: 214
-- Name: COLUMN relatedfeaturerole.relatedfeaturerole; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN relatedfeaturerole.relatedfeaturerole IS 'The related feature role definition. See OGC SWES 2.0 specification';


--
-- TOC entry 238 (class 1259 OID 62976)
-- Name: relatedfeatureroleid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE relatedfeatureroleid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.relatedfeatureroleid_seq OWNER TO "user";

--
-- TOC entry 215 (class 1259 OID 62544)
-- Name: resulttemplate; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE resulttemplate (
    resulttemplateid bigint NOT NULL,
    offeringid bigint NOT NULL,
    observablepropertyid bigint NOT NULL,
    procedureid bigint NOT NULL,
    featureofinterestid bigint NOT NULL,
    identifier character varying(255) NOT NULL,
    resultstructure text NOT NULL,
    resultencoding text NOT NULL
);


ALTER TABLE public.resulttemplate OWNER TO "user";

--
-- TOC entry 3819 (class 0 OID 0)
-- Dependencies: 215
-- Name: TABLE resulttemplate; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE resulttemplate IS 'Table to store resultTemplates (OGC SOS 2.0 result handling profile). Mapping file: mapping/transactionl/ResultTemplate.hbm.xml';


--
-- TOC entry 3820 (class 0 OID 0)
-- Dependencies: 215
-- Name: COLUMN resulttemplate.resulttemplateid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN resulttemplate.resulttemplateid IS 'Table primary key';


--
-- TOC entry 3821 (class 0 OID 0)
-- Dependencies: 215
-- Name: COLUMN resulttemplate.offeringid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN resulttemplate.offeringid IS 'Foreign Key (FK) to the related offering. Contains "offering".offeringid';


--
-- TOC entry 3822 (class 0 OID 0)
-- Dependencies: 215
-- Name: COLUMN resulttemplate.observablepropertyid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN resulttemplate.observablepropertyid IS 'Foreign Key (FK) to the related observableProperty. Contains "observableProperty".observablePropertyId';


--
-- TOC entry 3823 (class 0 OID 0)
-- Dependencies: 215
-- Name: COLUMN resulttemplate.procedureid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN resulttemplate.procedureid IS 'Foreign Key (FK) to the related procedure. Contains "procedure".procedureId';


--
-- TOC entry 3824 (class 0 OID 0)
-- Dependencies: 215
-- Name: COLUMN resulttemplate.featureofinterestid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN resulttemplate.featureofinterestid IS 'Foreign Key (FK) to the related featureOfInterest. Contains "featureOfInterest".featureOfInterestid';


--
-- TOC entry 3825 (class 0 OID 0)
-- Dependencies: 215
-- Name: COLUMN resulttemplate.identifier; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN resulttemplate.identifier IS 'The resultTemplate identifier, required for InsertResult requests.';


--
-- TOC entry 3826 (class 0 OID 0)
-- Dependencies: 215
-- Name: COLUMN resulttemplate.resultstructure; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN resulttemplate.resultstructure IS 'The resultStructure as XML string. Describes the types and order of the values in a GetResultResponse/InsertResultRequest';


--
-- TOC entry 3827 (class 0 OID 0)
-- Dependencies: 215
-- Name: COLUMN resulttemplate.resultencoding; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN resulttemplate.resultencoding IS 'The resultEncoding as XML string. Describes the encoding of the values in a GetResultResponse/InsertResultRequest';


--
-- TOC entry 239 (class 1259 OID 62978)
-- Name: resulttemplateid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE resulttemplateid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.resulttemplateid_seq OWNER TO "user";

--
-- TOC entry 216 (class 1259 OID 62552)
-- Name: sensorsystem; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE sensorsystem (
    parentsensorid bigint NOT NULL,
    childsensorid bigint NOT NULL
);


ALTER TABLE public.sensorsystem OWNER TO "user";

--
-- TOC entry 3828 (class 0 OID 0)
-- Dependencies: 216
-- Name: TABLE sensorsystem; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE sensorsystem IS 'Relation table to store procedure hierarchies. E.g. define a parent in a query and all childs are also contained in the response. Mapping file: mapping/transactional/TProcedure.hbm.xml';


--
-- TOC entry 3829 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN sensorsystem.parentsensorid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN sensorsystem.parentsensorid IS 'Foreign Key (FK) to the related parent procedure. Contains "procedure".procedureid';


--
-- TOC entry 3830 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN sensorsystem.childsensorid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN sensorsystem.childsensorid IS 'Foreign Key (FK) to the related child procedure. Contains "procedure".procedureid';


--
-- TOC entry 217 (class 1259 OID 62557)
-- Name: series; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE series (
    seriesid bigint NOT NULL,
    featureofinterestid bigint NOT NULL,
    observablepropertyid bigint NOT NULL,
    procedureid bigint NOT NULL,
    offeringid bigint,
    deleted character(1) DEFAULT 'F'::bpchar NOT NULL,
    published character(1) DEFAULT 'T'::bpchar NOT NULL,
    firsttimestamp timestamp without time zone,
    lasttimestamp timestamp without time zone,
    firstnumericvalue double precision,
    lastnumericvalue double precision,
    unitid bigint,
    CONSTRAINT series_deleted_check CHECK ((deleted = ANY (ARRAY['T'::bpchar, 'F'::bpchar]))),
    CONSTRAINT series_published_check CHECK ((published = ANY (ARRAY['T'::bpchar, 'F'::bpchar])))
);


ALTER TABLE public.series OWNER TO "user";

--
-- TOC entry 3831 (class 0 OID 0)
-- Dependencies: 217
-- Name: TABLE series; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE series IS 'Table to store a (time-) series which consists of featureOfInterest, observableProperty, and procedure. Mapping file: mapping/series/Series.hbm.xml';


--
-- TOC entry 3832 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN series.seriesid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN series.seriesid IS 'Table primary key, used for relations';


--
-- TOC entry 3833 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN series.featureofinterestid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN series.featureofinterestid IS 'Foreign Key (FK) to the related featureOfInterest. Contains "featureOfInterest".featureOfInterestId';


--
-- TOC entry 3834 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN series.observablepropertyid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN series.observablepropertyid IS 'Foreign Key (FK) to the related observableProperty. Contains "observableproperty".observablepropertyid';


--
-- TOC entry 3835 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN series.procedureid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN series.procedureid IS 'Foreign Key (FK) to the related procedure. Contains "procedure".procedureid';


--
-- TOC entry 3836 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN series.offeringid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN series.offeringid IS 'Foreign Key (FK) to the related procedure. Contains "offering".offeringid';


--
-- TOC entry 3837 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN series.deleted; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN series.deleted IS 'Flag to indicate that this series is deleted or not. Set if the related procedure is deleted via DeleteSensor operation (OGC SWES 2.0 - DeleteSensor operation)';


--
-- TOC entry 3838 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN series.published; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN series.published IS 'Flag to indicate that this series is published or not. A not published series is not contained in GetObservation and GetDataAvailability responses';


--
-- TOC entry 3839 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN series.firsttimestamp; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN series.firsttimestamp IS 'The time stamp of the first (temporal) observation associated to this series';


--
-- TOC entry 3840 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN series.lasttimestamp; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN series.lasttimestamp IS 'The time stamp of the last (temporal) observation associated to this series';


--
-- TOC entry 3841 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN series.firstnumericvalue; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN series.firstnumericvalue IS 'The value of the first (temporal) observation associated to this series';


--
-- TOC entry 3842 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN series.lastnumericvalue; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN series.lastnumericvalue IS 'The value of the last (temporal) observation associated to this series';


--
-- TOC entry 3843 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN series.unitid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN series.unitid IS 'Foreign Key (FK) to the related unit of the first/last numeric values . Contains "unit".unitid';


--
-- TOC entry 240 (class 1259 OID 62980)
-- Name: seriesid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE seriesid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seriesid_seq OWNER TO "user";

--
-- TOC entry 218 (class 1259 OID 62566)
-- Name: swedataarrayvalue; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE swedataarrayvalue (
    observationid bigint NOT NULL,
    value text
);


ALTER TABLE public.swedataarrayvalue OWNER TO "user";

--
-- TOC entry 3844 (class 0 OID 0)
-- Dependencies: 218
-- Name: TABLE swedataarrayvalue; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE swedataarrayvalue IS 'Value table for SweDataArray observation';


--
-- TOC entry 219 (class 1259 OID 62574)
-- Name: textvalue; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE textvalue (
    observationid bigint NOT NULL,
    value text
);


ALTER TABLE public.textvalue OWNER TO "user";

--
-- TOC entry 3845 (class 0 OID 0)
-- Dependencies: 219
-- Name: TABLE textvalue; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE textvalue IS 'Value table for text observation';


--
-- TOC entry 220 (class 1259 OID 62582)
-- Name: unit; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE unit (
    unitid bigint NOT NULL,
    unit character varying(255) NOT NULL
);


ALTER TABLE public.unit OWNER TO "user";

--
-- TOC entry 3846 (class 0 OID 0)
-- Dependencies: 220
-- Name: TABLE unit; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE unit IS 'Table to store the unit of measure information, used in observations. Mapping file: mapping/core/Unit.hbm.xml';


--
-- TOC entry 3847 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN unit.unitid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN unit.unitid IS 'Table primary key, used for relations';


--
-- TOC entry 3848 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN unit.unit; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN unit.unit IS 'The unit of measure of observations. See http://unitsofmeasure.org/ucum.html';


--
-- TOC entry 241 (class 1259 OID 62982)
-- Name: unitid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE unitid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.unitid_seq OWNER TO "user";

--
-- TOC entry 221 (class 1259 OID 62587)
-- Name: validproceduretime; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE validproceduretime (
    validproceduretimeid bigint NOT NULL,
    procedureid bigint NOT NULL,
    proceduredescriptionformatid bigint NOT NULL,
    starttime timestamp without time zone NOT NULL,
    endtime timestamp without time zone,
    descriptionxml text NOT NULL
);


ALTER TABLE public.validproceduretime OWNER TO "user";

--
-- TOC entry 3849 (class 0 OID 0)
-- Dependencies: 221
-- Name: TABLE validproceduretime; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE validproceduretime IS 'Table to store procedure descriptions which were inserted or updated via the transactional Profile. Mapping file: mapping/transactionl/ValidProcedureTime.hbm.xml';


--
-- TOC entry 3850 (class 0 OID 0)
-- Dependencies: 221
-- Name: COLUMN validproceduretime.validproceduretimeid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN validproceduretime.validproceduretimeid IS 'Table primary key';


--
-- TOC entry 3851 (class 0 OID 0)
-- Dependencies: 221
-- Name: COLUMN validproceduretime.procedureid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN validproceduretime.procedureid IS 'Foreign Key (FK) to the related procedure. Contains "procedure".procedureid';


--
-- TOC entry 3852 (class 0 OID 0)
-- Dependencies: 221
-- Name: COLUMN validproceduretime.proceduredescriptionformatid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN validproceduretime.proceduredescriptionformatid IS 'Foreign Key (FK) to the related procedureDescriptionFormat. Contains "procedureDescriptionFormat".procedureDescriptionFormatid';


--
-- TOC entry 3853 (class 0 OID 0)
-- Dependencies: 221
-- Name: COLUMN validproceduretime.starttime; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN validproceduretime.starttime IS 'Timestamp since this procedure description is valid';


--
-- TOC entry 3854 (class 0 OID 0)
-- Dependencies: 221
-- Name: COLUMN validproceduretime.endtime; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN validproceduretime.endtime IS 'Timestamp since this procedure description is invalid';


--
-- TOC entry 3855 (class 0 OID 0)
-- Dependencies: 221
-- Name: COLUMN validproceduretime.descriptionxml; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN validproceduretime.descriptionxml IS 'Procedure description as XML string';


--
-- TOC entry 242 (class 1259 OID 62984)
-- Name: validproceduretimeid_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE validproceduretimeid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.validproceduretimeid_seq OWNER TO "user";

--
-- TOC entry 3630 (class 0 OID 62357)
-- Dependencies: 186
-- Data for Name: blobvalue; Type: TABLE DATA; Schema: public; Owner: user
--

COPY blobvalue (observationid, value) FROM stdin;
\.


--
-- TOC entry 3631 (class 0 OID 62362)
-- Dependencies: 187
-- Data for Name: booleanvalue; Type: TABLE DATA; Schema: public; Owner: user
--

COPY booleanvalue (observationid, value) FROM stdin;
\.


--
-- TOC entry 3632 (class 0 OID 62369)
-- Dependencies: 188
-- Data for Name: categoryvalue; Type: TABLE DATA; Schema: public; Owner: user
--

COPY categoryvalue (observationid, value) FROM stdin;
\.


--
-- TOC entry 3633 (class 0 OID 62374)
-- Dependencies: 189
-- Data for Name: codespace; Type: TABLE DATA; Schema: public; Owner: user
--

COPY codespace (codespaceid, codespace) FROM stdin;
1	http://www.opengis.net/def/nil/OGC/0/unknown
\.


--
-- TOC entry 3856 (class 0 OID 0)
-- Dependencies: 222
-- Name: codespaceid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('codespaceid_seq', 1, true);


--
-- TOC entry 3634 (class 0 OID 62379)
-- Dependencies: 190
-- Data for Name: compositephenomenon; Type: TABLE DATA; Schema: public; Owner: user
--

COPY compositephenomenon (parentobservablepropertyid, childobservablepropertyid) FROM stdin;
\.


--
-- TOC entry 3635 (class 0 OID 62384)
-- Dependencies: 191
-- Data for Name: countvalue; Type: TABLE DATA; Schema: public; Owner: user
--

COPY countvalue (observationid, value) FROM stdin;
\.


--
-- TOC entry 3636 (class 0 OID 62389)
-- Dependencies: 192
-- Data for Name: featureofinterest; Type: TABLE DATA; Schema: public; Owner: user
--

COPY featureofinterest (featureofinterestid, hibernatediscriminator, featureofinteresttypeid, identifier, codespace, name, codespacename, description, geom, descriptionxml, url) FROM stdin;
1	T	2	http://www.52north.org/test/featureOfInterest/world	1	\N	1	\N	\N	\N	\N
2	T	2	52NorthWS1	1	52North HWS	1	\N	0101000020E6100000BBB88D06F0E62A402332ACE28D484840	\N	\N
\.


--
-- TOC entry 3857 (class 0 OID 0)
-- Dependencies: 223
-- Name: featureofinterestid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('featureofinterestid_seq', 2, true);


--
-- TOC entry 3637 (class 0 OID 62397)
-- Dependencies: 193
-- Data for Name: featureofinteresttype; Type: TABLE DATA; Schema: public; Owner: user
--

COPY featureofinteresttype (featureofinteresttypeid, featureofinteresttype) FROM stdin;
1	http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint
2	http://www.opengis.net/def/nil/OGC/0/unknown
\.


--
-- TOC entry 3858 (class 0 OID 0)
-- Dependencies: 224
-- Name: featureofinteresttypeid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('featureofinteresttypeid_seq', 2, true);


--
-- TOC entry 3638 (class 0 OID 62402)
-- Dependencies: 194
-- Data for Name: featurerelation; Type: TABLE DATA; Schema: public; Owner: user
--

COPY featurerelation (parentfeatureid, childfeatureid) FROM stdin;
1	2
\.


--
-- TOC entry 3639 (class 0 OID 62407)
-- Dependencies: 195
-- Data for Name: geometryvalue; Type: TABLE DATA; Schema: public; Owner: user
--

COPY geometryvalue (observationid, value) FROM stdin;
\.


--
-- TOC entry 3640 (class 0 OID 62415)
-- Dependencies: 196
-- Data for Name: i18nfeatureofinterest; Type: TABLE DATA; Schema: public; Owner: user
--

COPY i18nfeatureofinterest (id, objectid, locale, name, description) FROM stdin;
4	2	ita	52Nortrh stazione meterologica	Questa è una configurazione stazione meteo WS2500 a 52North, Münster in Germania.
5	2	eng	52North weather station	This is a WS2500 weather station setup at 52North, Münster in Germany.
6	2	ger	52North Wetterstation	Dies ist eine WS2500 Wetterstation, aufgestellt bei 52North, Münster in Deutschland.
\.


--
-- TOC entry 3859 (class 0 OID 0)
-- Dependencies: 228
-- Name: i18nfeatureofinterestid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('i18nfeatureofinterestid_seq', 6, true);


--
-- TOC entry 3641 (class 0 OID 62423)
-- Dependencies: 197
-- Data for Name: i18nobservableproperty; Type: TABLE DATA; Schema: public; Owner: user
--

COPY i18nobservableproperty (id, objectid, locale, name, description) FROM stdin;
1	4	ita	pressione barometrica	\N
2	4	eng	air pressure	\N
3	4	ger	luftdruck	\N
4	6	ita	Illuminamento	\N
5	6	eng	Illuminance	\N
6	6	ger	Beleuchtungsstärke	\N
7	1	ita	Precipitazioni oraria	\N
8	1	eng	Hourly Precipitation	\N
9	1	ger	Stündlicher Niederschlag	\N
10	7	ita	umidità relativa	\N
11	7	eng	relative humidity	\N
12	7	ger	relative Luftfeuchtigkeit	\N
13	5	ita	temperatura	\N
14	5	eng	temperature	\N
15	5	ger	Temperatur	\N
16	2	ita	direzione del vento	\N
17	2	eng	wind direction	\N
18	2	ger	Windrichtung	\N
19	3	ita	velocità del vento	\N
20	3	eng	wind speed	\N
21	3	ger	Windgeschwindigkeit	\N
\.


--
-- TOC entry 3860 (class 0 OID 0)
-- Dependencies: 225
-- Name: i18nobspropid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('i18nobspropid_seq', 21, true);


--
-- TOC entry 3642 (class 0 OID 62431)
-- Dependencies: 198
-- Data for Name: i18noffering; Type: TABLE DATA; Schema: public; Owner: user
--

COPY i18noffering (id, objectid, locale, name, description) FROM stdin;
\.


--
-- TOC entry 3861 (class 0 OID 0)
-- Dependencies: 226
-- Name: i18nofferingid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('i18nofferingid_seq', 1, false);


--
-- TOC entry 3643 (class 0 OID 62439)
-- Dependencies: 199
-- Data for Name: i18nprocedure; Type: TABLE DATA; Schema: public; Owner: user
--

COPY i18nprocedure (id, objectid, locale, name, description, shortname, longname) FROM stdin;
\.


--
-- TOC entry 3862 (class 0 OID 0)
-- Dependencies: 227
-- Name: i18nprocedureid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('i18nprocedureid_seq', 1, false);


--
-- TOC entry 3644 (class 0 OID 62447)
-- Dependencies: 200
-- Data for Name: numericvalue; Type: TABLE DATA; Schema: public; Owner: user
--

COPY numericvalue (observationid, value) FROM stdin;
1	0
2	0
3	0
4	1600
5	6740
6	2750
7	0
8	0
9	0
10	0
11	0
12	0
13	5840
14	10700
15	16900
16	2670
17	7
18	0
19	0
20	0
21	0
22	0
23	12700
24	26400
25	21300
26	8
27	0
28	0
29	0
30	0
31	0
32	0
33	7390
34	14600
35	1740
36	147
37	0
38	0
39	0
40	0
41	0
42	1140
43	6790
44	11500
45	1940
46	0
47	0
48	0
49	0
50	0
51	0
52	7470
53	16900
54	18600
55	6
56	0
57	0
58	0
59	0
60	0
61	420
62	4660
63	12800
64	12800
65	0
66	0
67	0
68	0
69	0
70	0
71	8120
72	3220
73	7810
74	1290
75	0
76	0
77	0
78	0
79	0
80	8480
81	7860
82	1640
83	6090
84	0
85	0
86	0
87	0
88	0
89	0
90	6840
91	7690
92	11700
93	55
94	0
95	0
96	0
97	0
98	0
99	481
100	8860
101	2710
102	4780
103	0
104	0
105	0
106	0
107	0
108	0
109	4250
110	9570
111	9680
112	6080
113	0
114	0
115	0
116	0
117	0
118	0
119	4380
120	17300
121	22000
122	10500
123	0
124	0
125	0
126	0
127	0
128	28
129	11900
130	13300
131	5670
132	1310
133	0
134	0
135	0
136	0
137	0
138	343
139	842
140	808
141	5270
142	0
143	0
144	0
145	0
146	0
147	0
148	30300
149	4290
150	8050
151	4840
152	0
153	0
154	0
155	0
156	0
157	1050
158	6570
159	3850
160	483
161	222
162	0
163	0
164	0
165	0
166	408
167	84
168	11700
169	8030
170	13
171	0
172	0
173	0
174	0
175	0
176	0
177	13600
178	12900
179	2290
180	0
181	0
182	0
183	0
184	0
185	0
186	60
187	8910
188	25800
189	1
190	2
191	0
192	0
193	0
194	0
195	0
196	27500
197	31200
198	14300
199	410
200	0
201	0
202	0
203	0
204	134
205	470
206	36600
207	24900
208	12
209	11300
210	0
211	0
212	0
213	0
214	0
215	0
216	18400
217	19800
218	11900
219	0
220	0
221	0
222	0
223	0
224	1100
225	7630
226	8830
227	1430
228	5840
229	5
230	0
231	0
232	0
233	0
234	7380
235	8710
236	11600
237	837
238	0
239	0
240	0
241	0
242	0
243	2790
244	38300
245	39700
246	23700
247	27200
248	6
249	0
250	0
251	0
252	0
253	14600
254	21200
255	8310
256	13600
257	0
258	3
259	0
260	0
261	0
262	0
263	1930
264	15100
265	30900
266	0
267	164
268	0
269	0
270	0
271	0
272	0
273	3
274	6840
275	1400
276	6120
277	0
278	0
279	0
280	0
281	0
282	49900
283	0
284	45800
285	3320
286	0
287	10500
288	0
289	-1.80000000000000004
290	-1.89999999999999991
291	-2
292	-1.39999999999999991
293	-1.39999999999999991
294	-1.39999999999999991
295	-2.20000000000000018
296	-3.60000000000000009
297	-3
298	-3.70000000000000018
299	-3.89999999999999991
300	-4.70000000000000018
301	-4.09999999999999964
302	-1.80000000000000004
303	-1.5
304	-1.60000000000000009
305	-1
306	-0.800000000000000044
307	-0.800000000000000044
308	-0.800000000000000044
309	-4.09999999999999964
310	-5.5
311	-5.59999999999999964
312	-4.79999999999999982
313	-5.5
314	-4.70000000000000018
315	-4.40000000000000036
316	-4.59999999999999964
317	-5.5
318	-5.90000000000000036
319	-8
320	-8.30000000000000071
321	-6.40000000000000036
322	-4.40000000000000036
323	-4.20000000000000018
324	-4.40000000000000036
325	-4.09999999999999964
326	-3.60000000000000009
327	-3
328	-3
329	-3
330	-2.60000000000000009
331	-1.30000000000000004
332	-0.200000000000000011
333	-0.900000000000000022
334	-1.5
335	-2.39999999999999991
336	-2.89999999999999991
337	-3.20000000000000018
338	-4.70000000000000018
339	-5.29999999999999982
340	-5.29999999999999982
341	-3.29999999999999982
342	-1
343	-3.60000000000000009
344	-4.5
345	-5.09999999999999964
346	-4.5
347	-4.40000000000000036
348	-4.40000000000000036
349	-3.79999999999999982
350	-2.29999999999999982
351	-3.20000000000000018
352	-1.89999999999999991
353	-3.60000000000000009
354	-3.89999999999999991
355	-3.29999999999999982
356	-3.39999999999999991
357	-3.70000000000000018
358	-3.29999999999999982
359	-5.5
360	-5.90000000000000036
361	-3.39999999999999991
362	-4.20000000000000018
363	-3.89999999999999991
364	-3.29999999999999982
365	-2.79999999999999982
366	-3.79999999999999982
367	-3.79999999999999982
368	-3.60000000000000009
369	-3.20000000000000018
370	-1.19999999999999996
371	-1.30000000000000004
372	-1.5
373	-1.69999999999999996
374	-2
375	-2
376	-2.10000000000000009
377	-1.80000000000000004
378	-1.80000000000000004
379	-2
380	-1.5
381	-1.60000000000000009
382	-2.20000000000000018
383	-2.20000000000000018
384	-2.20000000000000018
385	-2.29999999999999982
386	-0.5
387	-1.10000000000000009
388	-0.900000000000000022
389	-0.800000000000000044
390	-1.80000000000000004
391	-1.30000000000000004
392	-2.5
393	-2.60000000000000009
394	-3.39999999999999991
395	-3.79999999999999982
396	-4.79999999999999982
397	-6.40000000000000036
398	-4.09999999999999964
399	-4.29999999999999982
400	-3.70000000000000018
401	-3.60000000000000009
402	-5.5
403	-6.20000000000000018
404	-6
405	-5.90000000000000036
406	-6
407	-6.09999999999999964
408	-4.90000000000000036
409	-3.10000000000000009
410	-3.10000000000000009
411	-3.79999999999999982
412	-3.79999999999999982
413	-3.29999999999999982
414	-2.20000000000000018
415	-1.80000000000000004
416	-0.800000000000000044
417	-1.39999999999999991
418	1.10000000000000009
419	1.19999999999999996
420	0.400000000000000022
421	0.5
422	0.299999999999999989
423	0.299999999999999989
424	0.400000000000000022
425	0.5
426	0.800000000000000044
427	0.900000000000000022
428	1.30000000000000004
429	1.5
430	1
431	1.10000000000000009
432	0.400000000000000022
433	0
434	-1
435	-0.5
436	-1.10000000000000009
437	1.89999999999999991
438	0.800000000000000044
439	-1.10000000000000009
440	-0.699999999999999956
441	-0.699999999999999956
442	-0.400000000000000022
443	-0.400000000000000022
444	1.19999999999999996
445	1.10000000000000009
446	2.20000000000000018
447	2.29999999999999982
448	2.29999999999999982
449	3.5
450	3.10000000000000009
451	3.10000000000000009
452	3.29999999999999982
453	3.10000000000000009
454	3.39999999999999991
455	3.39999999999999991
456	3.79999999999999982
457	3.79999999999999982
458	3.29999999999999982
459	3.5
460	4
461	4.20000000000000018
462	4.09999999999999964
463	4.29999999999999982
464	4.09999999999999964
465	4.09999999999999964
466	4.79999999999999982
467	4.70000000000000018
468	3.89999999999999991
469	3.89999999999999991
470	3
471	3.20000000000000018
472	2.79999999999999982
473	2.70000000000000018
474	2.10000000000000009
475	2.10000000000000009
476	3.60000000000000009
477	3.70000000000000018
478	3.29999999999999982
479	2.70000000000000018
480	2.5
481	2.29999999999999982
482	0.900000000000000022
483	0.599999999999999978
484	0.599999999999999978
485	1.60000000000000009
486	1.30000000000000004
487	-0.800000000000000044
488	-1.60000000000000009
489	-1.60000000000000009
490	-2.39999999999999991
491	-2.39999999999999991
492	-2.39999999999999991
493	-3.20000000000000018
494	2.60000000000000009
495	1.69999999999999996
496	1.5
497	-1.10000000000000009
498	-3.79999999999999982
499	-3.29999999999999982
500	-3.39999999999999991
501	-3.29999999999999982
502	-4.70000000000000018
503	-4.59999999999999964
504	-2.60000000000000009
505	-1
506	-0.699999999999999956
507	-3
508	-3.29999999999999982
509	-3.10000000000000009
510	-2.89999999999999991
511	-2.60000000000000009
512	-2.39999999999999991
513	-1
514	-1
515	-1
516	-1.19999999999999996
517	-1.69999999999999996
518	-1.89999999999999991
519	-2.39999999999999991
520	-4.20000000000000018
521	-3.60000000000000009
522	-4.29999999999999982
523	-2.89999999999999991
524	-4
525	-4.29999999999999982
526	-5.5
527	-5.70000000000000018
528	-8.19999999999999929
529	-10.0999999999999996
530	-9.80000000000000071
531	-11.8000000000000007
532	-9.69999999999999929
533	-4.20000000000000018
534	-4.09999999999999964
535	-4.29999999999999982
536	-10.4000000000000004
537	-9.80000000000000071
538	-12.0999999999999996
539	-12.8000000000000007
540	-10.8000000000000007
541	-9.19999999999999929
542	-10.0999999999999996
543	-5.90000000000000036
544	-2.5
545	-2.20000000000000018
546	-2.39999999999999991
547	-1.30000000000000004
548	0.699999999999999956
549	1.60000000000000009
550	1.60000000000000009
551	1.30000000000000004
552	1.89999999999999991
553	2.20000000000000018
554	2.70000000000000018
555	0.699999999999999956
556	0.599999999999999978
557	0.200000000000000011
558	0.100000000000000006
559	-0.200000000000000011
560	0.599999999999999978
561	-0.200000000000000011
562	0.299999999999999989
563	0.200000000000000011
564	0.5
565	-1.5
566	-0.200000000000000011
567	-1.10000000000000009
568	-0.599999999999999978
569	-1.80000000000000004
570	0
571	-1.80000000000000004
572	2.29999999999999982
573	1.10000000000000009
574	-0.200000000000000011
575	1.30000000000000004
576	-0.800000000000000044
577	23.5
578	22.3000000000000007
579	16.6000000000000014
580	20
581	16.6000000000000014
582	17.6000000000000014
583	16.1000000000000014
584	15.1999999999999993
585	10.3000000000000007
586	13.1999999999999993
587	16.8000000000000007
588	11.8000000000000007
589	10.5999999999999996
590	11.4000000000000004
591	11.6999999999999993
592	11.0999999999999996
593	12.4000000000000004
594	19.6000000000000014
595	11.9000000000000004
596	14.0999999999999996
597	8.59999999999999964
598	10.5999999999999996
599	11.1999999999999993
600	15.1999999999999993
601	9.59999999999999964
602	11.5
603	2.79999999999999982
604	8.80000000000000071
605	5.59999999999999964
606	6
607	0
608	8.19999999999999929
609	17.1999999999999993
610	21
611	22.1000000000000014
612	20.6000000000000014
613	21.6000000000000014
614	17.1000000000000014
615	26.6000000000000014
616	14.5
617	12.9000000000000004
618	10.0999999999999996
619	17.1999999999999993
620	19.6000000000000014
621	17.1000000000000014
622	14.3000000000000007
623	10.0999999999999996
624	0.900000000000000022
625	5.20000000000000018
626	6.5
627	14.6999999999999993
628	6.70000000000000018
629	14.0999999999999996
630	12.8000000000000007
631	7
632	5.20000000000000018
633	9.59999999999999964
634	4.59999999999999964
635	5
636	0.699999999999999956
637	0.299999999999999989
638	0.599999999999999978
639	3.20000000000000018
640	8.19999999999999929
641	3.20000000000000018
642	4.09999999999999964
643	4.20000000000000018
644	0
645	13.0999999999999996
646	12
647	15.0999999999999996
648	12.8000000000000007
649	18.1999999999999993
650	30.6000000000000014
651	25.8000000000000007
652	37.8999999999999986
653	37.2999999999999972
654	33.5
655	25.6000000000000014
656	30.6999999999999993
657	36.2999999999999972
658	27.8000000000000007
659	31
660	27.5
661	22.5
662	28.5
663	31.1999999999999993
664	17.8000000000000007
665	23.5
666	13
667	21.8000000000000007
668	16.1000000000000014
669	18.8999999999999986
670	15.8000000000000007
671	16
672	6.59999999999999964
673	9.59999999999999964
674	15.8000000000000007
675	13
676	12
677	14.1999999999999993
678	14.5
679	11
680	8.19999999999999929
681	11
682	5.09999999999999964
683	0
684	5.40000000000000036
685	6.79999999999999982
686	16.6000000000000014
687	12.0999999999999996
688	24.6000000000000014
689	23.6000000000000014
690	21.5
691	22.3000000000000007
692	18.3999999999999986
693	27.1999999999999993
694	27.3000000000000007
695	23.3000000000000007
696	29.1000000000000014
697	35.5
698	21.3999999999999986
699	21.6000000000000014
700	13.5999999999999996
701	13.1999999999999993
702	6.79999999999999982
703	12.3000000000000007
704	7.40000000000000036
705	8.40000000000000036
706	11.3000000000000007
707	9.40000000000000036
708	10
709	6.20000000000000018
710	7.90000000000000036
711	7.29999999999999982
712	7.20000000000000018
713	8.80000000000000071
714	2.10000000000000009
715	0.299999999999999989
716	0.699999999999999956
717	5.40000000000000036
718	4.09999999999999964
719	0.900000000000000022
720	6.09999999999999964
721	16
722	13.8000000000000007
723	14.5999999999999996
724	19.1000000000000014
725	18.1000000000000014
726	21.8999999999999986
727	19.1000000000000014
728	8.5
729	12.5
730	5.70000000000000018
731	6.90000000000000036
732	6
733	7.09999999999999964
734	12
735	11.0999999999999996
736	25.3999999999999986
737	23.1999999999999993
738	19
739	21.6999999999999993
740	14.5
741	17.3000000000000007
742	16.3999999999999986
743	15.3000000000000007
744	18.5
745	21
746	26
747	19
748	19.6999999999999993
749	23.6999999999999993
750	20.8000000000000007
751	12.8000000000000007
752	8.90000000000000036
753	14.8000000000000007
754	7.40000000000000036
755	21.3999999999999986
756	0
757	3.60000000000000009
758	0
759	0
760	4.70000000000000018
761	5.70000000000000018
762	13.1999999999999993
763	5.79999999999999982
764	11.8000000000000007
765	17.1000000000000014
766	14.5
767	17.5
768	12.9000000000000004
769	11.3000000000000007
770	15.5999999999999996
771	12.6999999999999993
772	15.0999999999999996
773	15.5
774	15.3000000000000007
775	14.6999999999999993
776	17.8000000000000007
777	10.5999999999999996
778	13
779	9.69999999999999929
780	13.3000000000000007
781	19
782	17.6000000000000014
783	11.8000000000000007
784	18.3999999999999986
785	13.5999999999999996
786	13.5999999999999996
787	14.9000000000000004
788	12
789	12
790	11.5999999999999996
791	11.8000000000000007
792	13.0999999999999996
793	12.5
794	18.8000000000000007
795	18.8000000000000007
796	16.1999999999999993
797	14.1999999999999993
798	11.5
799	12.5
800	8.59999999999999964
801	7.90000000000000036
802	10.0999999999999996
803	7.59999999999999964
804	5
805	12.8000000000000007
806	9.90000000000000036
807	8.5
808	9.40000000000000036
809	9.19999999999999929
810	13.0999999999999996
811	12.5999999999999996
812	13.5999999999999996
813	13.3000000000000007
814	12.6999999999999993
815	12
816	14.6999999999999993
817	13.0999999999999996
818	14
819	17.3000000000000007
820	11.5
821	13.5999999999999996
822	12.5
823	10.1999999999999993
824	8.69999999999999929
825	6.5
826	0
827	0
828	5.59999999999999964
829	5.90000000000000036
830	9.09999999999999964
831	43.7000000000000028
832	33.2000000000000028
833	46.5
834	43.1000000000000014
835	46.2999999999999972
836	41.1000000000000014
837	40.5
838	32
839	26.6999999999999993
840	23.1000000000000014
841	34.7000000000000028
842	25.1999999999999993
843	32.5
844	23.6000000000000014
845	24.5
846	23.1999999999999993
847	19.1999999999999993
848	23
849	20.8000000000000007
850	30.5
851	19.3000000000000007
852	29.5
853	23.1999999999999993
854	28.6999999999999993
855	27.1999999999999993
856	30
857	19
858	19
859	33.2000000000000028
860	36.7999999999999972
861	36.3999999999999986
862	27.3000000000000007
863	26.5
864	21.8999999999999986
865	986
866	986
867	986
868	987
869	988
870	988
871	992
872	993
873	994
874	998
875	1001
876	1001
877	1003
878	1005
879	1005
880	1006
881	1006
882	1007
883	1006
884	1007
885	1007
886	1007
887	1010
888	1010
889	1011
890	1012
891	1012
892	1012
893	1012
894	1011
895	1010
896	1010
897	1008
898	1006
899	1004
900	1003
901	1002
902	1001
903	1000
904	999
905	998
906	997
907	996
908	995
909	995
910	994
911	994
912	993
913	993
914	993
915	992
916	993
917	993
918	993
919	994
920	994
921	995
922	996
923	996
924	996
925	998
926	999
927	1000
928	1002
929	1002
930	1005
931	1005
932	1006
933	1007
934	1009
935	1009
936	1010
937	1009
938	1010
939	1010
940	1010
941	1010
942	1010
943	1009
944	1008
945	1008
946	1005
947	1005
948	1004
949	1004
950	1003
951	1003
952	1003
953	1003
954	1005
955	1005
956	1005
957	1005
958	1008
959	1007
960	1008
961	1008
962	1007
963	1007
964	1007
965	1008
966	1007
967	1007
968	1008
969	1008
970	1008
971	1008
972	1007
973	1007
974	1007
975	1006
976	1003
977	1003
978	1001
979	1001
980	999
981	998
982	997
983	997
984	997
985	996
986	996
987	996
988	997
989	997
990	997
991	997
992	997
993	999
994	999
995	999
996	1000
997	1001
998	1003
999	1005
1000	1006
1001	1007
1002	1009
1003	1010
1004	1013
1005	1014
1006	1015
1007	1015
1008	1016
1009	1016
1010	1013
1011	1013
1012	1011
1013	1008
1014	1004
1015	1005
1016	1001
1017	1000
1018	998
1019	997
1020	997
1021	998
1022	999
1023	1002
1024	1006
1025	1007
1026	1010
1027	1010
1028	1011
1029	1013
1030	1014
1031	1014
1032	1014
1033	1014
1034	1014
1035	1014
1036	1014
1037	1013
1038	1013
1039	1013
1040	1012
1041	1012
1042	1012
1043	1011
1044	1011
1045	1011
1046	1010
1047	1009
1048	1009
1049	1007
1050	1006
1051	1006
1052	1004
1053	1005
1054	1005
1055	1005
1056	1007
1057	1009
1058	1009
1059	1010
1060	1012
1061	1012
1062	1013
1063	1013
1064	1014
1065	1014
1066	1014
1067	1015
1068	1015
1069	1016
1070	1015
1071	1015
1072	1015
1073	1016
1074	1016
1075	1016
1076	1015
1077	1015
1078	1015
1079	1014
1080	1014
1081	1014
1082	1013
1083	1013
1084	1013
1085	1013
1086	1013
1087	1013
1088	1013
1089	1014
1090	1013
1091	1013
1092	1014
1093	1014
1094	1014
1095	1015
1096	1016
1097	1016
1098	1017
1099	1019
1100	1020
1101	1019
1102	1023
1103	1023
1104	1025
1105	1026
1106	1027
1107	1028
1108	1030
1109	1030
1110	1029
1111	1029
1112	1030
1113	1031
1114	1028
1115	1028
1116	1024
1117	1020
1118	1020
1119	1018
1120	1013
1121	1005
1122	1001
1123	1000
1124	996
1125	995
1126	995
1127	995
1128	995
1129	993
1130	992
1131	990
1132	990
1133	982
1134	988
1135	981
1136	986
1137	970
1138	976
1139	970
1140	974
1141	975
1142	973
1143	975
1144	973
1145	991
1146	990
1147	977
1148	991
1149	992
1150	994
1151	993
1152	995
1153	0
1154	0
1155	0
1156	0
1157	0
1158	0
1159	0
1160	0
1161	0
1162	0
1163	0
1164	0
1165	0
1166	0
1167	0
1168	0
1169	0
1170	0
1171	0
1172	0
1173	0
1174	0
1175	0
1176	0
1177	0
1178	0
1179	0
1180	0
1181	0
1182	0
1183	0
1184	0
1185	0
1186	0
1187	0
1188	0
1189	0
1190	0
1191	0
1192	0
1193	0
1194	0
1195	0
1196	0
1197	0
1198	0
1199	0
1200	0
1201	0
1202	0
1203	0
1204	0
1205	0
1206	0
1207	0
1208	0
1209	0
1210	0
1211	0
1212	0
1213	0
1214	0
1215	0
1216	0
1217	0
1218	0
1219	0
1220	0
1221	0
1222	0
1223	0
1224	0
1225	0
1226	0
1227	0
1228	0
1229	0
1230	0
1231	0
1232	0
1233	0
1234	0
1235	0
1236	0
1237	0
1238	0
1239	0
1240	0
1241	0
1242	0
1243	0
1244	0
1245	0
1246	0
1247	0
1248	0
1249	0
1250	0
1251	0
1252	0
1253	0
1254	0
1255	0
1256	0
1257	0
1258	0
1259	0
1260	0
1261	0
1262	0
1263	0
1264	0
1265	0
1266	0
1267	0
1268	0
1269	0
1270	0
1271	0
1272	0
1273	0
1274	0
1275	0
1276	0
1277	0
1278	0
1279	0
1280	0
1281	0
1282	0
1283	0
1284	0
1285	0
1286	0
1287	0
1288	0
1289	0
1290	0
1291	0
1292	0
1293	0
1294	0.299999999999999989
1295	0
1296	0
1297	0
1298	0
1299	0
1300	0
1301	0
1302	0
1303	0
1304	0
1305	0
1306	0
1307	0
1308	0
1309	0
1310	0.299999999999999989
1311	0
1312	0
1313	0
1314	0.299999999999999989
1315	0
1316	0
1317	0
1318	0
1319	0
1320	0
1321	0
1322	0
1323	0
1324	0
1325	0
1326	0
1327	0
1328	0
1329	0
1330	0
1331	0
1332	0
1333	0
1334	0
1335	0
1336	0
1337	0
1338	0
1339	0
1340	0
1341	0
1342	0
1343	0
1344	0
1345	0
1346	0
1347	0
1348	0
1349	0
1350	0
1351	0
1352	0
1353	0
1354	0
1355	0
1356	0
1357	0
1358	0
1359	0
1360	0
1361	0
1362	0
1363	0
1364	0
1365	0
1366	0
1367	0
1368	0
1369	0
1370	0
1371	0
1372	0
1373	0
1374	0
1375	0
1376	0
1377	0
1378	0
1379	0
1380	0
1381	0
1382	0
1383	0
1384	0
1385	0
1386	0
1387	0
1388	0
1389	0
1390	0
1391	0
1392	0
1393	0
1394	0
1395	0
1396	0
1397	0
1398	0
1399	0
1400	0
1401	0
1402	0
1403	0
1404	0
1405	0
1406	0
1407	0
1408	0
1409	0
1410	0
1411	0
1412	0.299999999999999989
1413	0
1414	0
1415	0
1416	0
1417	0.299999999999999989
1418	0
1419	0
1420	0
1421	0
1422	0
1423	0
1424	0
1425	0
1426	0.299999999999999989
1427	0
1428	0
1429	0
1430	0
1431	0
1432	0
1433	0
1434	0
1435	0
1436	0
1437	0
1438	0
1439	0
1440	0
1441	99
1442	99
1443	99
1444	95
1445	94
1446	94
1447	94
1448	98
1449	99
1450	98
1451	91
1452	91
1453	90
1454	89
1455	82
1456	89
1457	97
1458	99
1459	99
1460	99
1461	99
1462	99
1463	99
1464	99
1465	91
1466	99
1467	98
1468	95
1469	99
1470	99
1471	99
1472	99
1473	99
1474	94
1475	91
1476	93
1477	91
1478	93
1479	97
1480	98
1481	95
1482	98
1483	96
1484	93
1485	94
1486	97
1487	98
1488	99
1489	98
1490	99
1491	92
1492	89
1493	84
1494	80
1495	88
1496	92
1497	95
1498	96
1499	96
1500	96
1501	99
1502	98
1503	96
1504	96
1505	98
1506	98
1507	98
1508	98
1509	96
1510	94
1511	91
1512	97
1513	91
1514	94
1515	93
1516	88
1517	88
1518	92
1519	92
1520	95
1521	95
1522	97
1523	95
1524	84
1525	92
1526	86
1527	86
1528	92
1529	91
1530	97
1531	96
1532	94
1533	93
1534	95
1535	92
1536	99
1537	99
1538	99
1539	99
1540	98
1541	90
1542	92
1543	90
1544	89
1545	94
1546	98
1547	98
1548	99
1549	99
1550	89
1551	92
1552	88
1553	86
1554	90
1555	90
1556	90
1557	86
1558	86
1559	85
1560	79
1561	78
1562	86
1563	84
1564	91
1565	91
1566	91
1567	94
1568	94
1569	94
1570	91
1571	88
1572	90
1573	92
1574	93
1575	93
1576	93
1577	93
1578	95
1579	93
1580	92
1581	92
1582	95
1583	95
1584	96
1585	96
1586	95
1587	94
1588	94
1589	83
1590	83
1591	91
1592	89
1593	89
1594	99
1595	99
1596	99
1597	99
1598	99
1599	99
1600	99
1601	99
1602	99
1603	99
1604	99
1605	99
1606	99
1607	99
1608	99
1609	99
1610	99
1611	99
1612	99
1613	99
1614	99
1615	99
1616	99
1617	99
1618	99
1619	97
1620	99
1621	99
1622	99
1623	99
1624	99
1625	99
1626	99
1627	99
1628	92
1629	92
1630	90
1631	91
1632	93
1633	94
1634	94
1635	93
1636	86
1637	82
1638	82
1639	85
1640	94
1641	96
1642	97
1643	96
1644	98
1645	98
1646	99
1647	85
1648	86
1649	92
1650	98
1651	97
1652	98
1653	97
1654	97
1655	93
1656	90
1657	87
1658	86
1659	92
1660	93
1661	92
1662	91
1663	91
1664	91
1665	91
1666	89
1667	91
1668	99
1669	99
1670	99
1671	99
1672	99
1673	99
1674	93
1675	96
1676	85
1677	86
1678	85
1679	86
1680	88
1681	90
1682	93
1683	92
1684	90
1685	67
1686	64
1687	78
1688	80
1689	79
1690	85
1691	82
1692	88
1693	89
1694	81
1695	80
1696	68
1697	77
1698	99
1699	99
1700	99
1701	99
1702	95
1703	99
1704	99
1705	98
1706	97
1707	97
1708	98
1709	99
1710	99
1711	99
1712	99
1713	99
1714	99
1715	99
1716	99
1717	99
1718	98
1719	97
1720	99
1721	99
1722	96
1723	82
1724	89
1725	88
1726	94
1727	92
1728	97
1729	135
1730	135
1731	135
1732	135
1733	135
1734	135
1735	135
1736	135
1737	135
1738	135
1739	135
1740	135
1741	130
1742	135
1743	135
1744	135
1745	135
1746	135
1747	135
1748	135
1749	135
1750	135
1751	135
1752	135
1753	135
1754	135
1755	130
1756	135
1757	135
1758	135
1759	135
1760	135
1761	135
1762	135
1763	135
1764	135
1765	135
1766	130
1767	135
1768	135
1769	135
1770	135
1771	135
1772	320
1773	310
1774	310
1775	305
1776	305
1777	305
1778	310
1779	305
1780	305
1781	305
1782	310
1783	305
1784	305
1785	305
1786	305
1787	305
1788	310
1789	305
1790	305
1791	310
1792	35
1793	35
1794	35
1795	35
1796	35
1797	35
1798	35
1799	40
1800	35
1801	40
1802	45
1803	40
1804	45
1805	140
1806	140
1807	140
1808	105
1809	135
1810	130
1811	120
1812	150
1813	150
1814	140
1815	135
1816	115
1817	150
1818	140
1819	115
1820	130
1821	140
1822	130
1823	135
1824	90
1825	90
1826	95
1827	115
1828	95
1829	150
1830	160
1831	145
1832	145
1833	150
1834	140
1835	135
1836	145
1837	140
1838	160
1839	145
1840	150
1841	175
1842	160
1843	155
1844	145
1845	145
1846	145
1847	150
1848	160
1849	160
1850	155
1851	145
1852	165
1853	155
1854	170
1855	150
1856	155
1857	145
1858	145
1859	140
1860	150
1861	150
1862	150
1863	150
1864	155
1865	155
1866	155
1867	205
1868	250
1869	215
1870	240
1871	210
1872	230
1873	240
1874	150
1875	215
1876	170
1877	150
1878	155
1879	180
1880	205
1881	135
1882	235
1883	160
1884	185
1885	330
1886	335
1887	10
1888	350
1889	5
1890	335
1891	350
1892	335
1893	350
1894	335
1895	345
1896	340
1897	325
1898	310
1899	325
1900	330
1901	335
1902	350
1903	10
1904	20
1905	350
1906	360
1907	345
1908	60
1909	70
1910	30
1911	30
1912	145
1913	155
1914	160
1915	150
1916	160
1917	155
1918	155
1919	160
1920	160
1921	155
1922	150
1923	145
1924	150
1925	150
1926	150
1927	155
1928	150
1929	145
1930	155
1931	155
1932	155
1933	160
1934	170
1935	155
1936	155
1937	140
1938	165
1939	145
1940	160
1941	145
1942	155
1943	150
1944	165
1945	140
1946	155
1947	150
1948	170
1949	160
1950	150
1951	160
1952	140
1953	150
1954	145
1955	140
1956	150
1957	140
1958	95
1959	135
1960	90
1961	120
1962	110
1963	115
1964	90
1965	95
1966	140
1967	145
1968	145
1969	130
1970	95
1971	145
1972	125
1973	135
1974	135
1975	135
1976	150
1977	140
1978	70
1979	65
1980	285
1981	255
1982	285
1983	330
1984	345
1985	335
1986	340
1987	340
1988	25
1989	25
1990	5
1991	10
1992	360
1993	10
1994	10
1995	5
1996	360
1997	360
1998	335
1999	15
2000	340
2001	295
2002	315
2003	300
2004	335
2005	20
2006	5
2007	5
2008	5
2009	345
2010	345
2011	355
2012	355
2013	320
2014	330
2015	330
2016	320
\.


--
-- TOC entry 3645 (class 0 OID 62452)
-- Dependencies: 201
-- Data for Name: observableproperty; Type: TABLE DATA; Schema: public; Owner: user
--

COPY observableproperty (observablepropertyid, hibernatediscriminator, identifier, codespace, name, codespacename, description, disabled) FROM stdin;
1	F	Precipitation1Hour	1	\N	1	\N	F
2	F	WindDirection	1	\N	1	\N	F
3	F	WindSpeed	1	\N	1	\N	F
4	F	BarometricPressure	1	\N	1	\N	F
5	F	Temperature	1	\N	1	\N	F
6	F	Luminance	1	\N	1	\N	F
7	F	RelativeHumidity	1	\N	1	\N	F
\.


--
-- TOC entry 3863 (class 0 OID 0)
-- Dependencies: 229
-- Name: observablepropertyid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('observablepropertyid_seq', 7, true);


--
-- TOC entry 3646 (class 0 OID 62462)
-- Dependencies: 202
-- Data for Name: observation; Type: TABLE DATA; Schema: public; Owner: user
--

COPY observation (observationid, seriesid, phenomenontimestart, phenomenontimeend, resulttime, identifier, codespace, name, codespacename, description, deleted, validtimestart, validtimeend, unitid, samplinggeometry) FROM stdin;
1	1	2017-03-01 01:15:00	2017-03-01 01:15:00	2017-03-01 01:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
2	1	2017-03-01 05:15:00	2017-03-01 05:15:00	2017-03-01 05:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
3	1	2017-03-01 06:15:00	2017-03-01 06:15:00	2017-03-01 06:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
4	1	2017-03-01 10:15:00	2017-03-01 10:15:00	2017-03-01 10:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
5	1	2017-03-01 14:30:00	2017-03-01 14:30:00	2017-03-01 14:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
6	1	2017-03-01 16:00:00	2017-03-01 16:00:00	2017-03-01 16:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
7	1	2017-03-01 18:00:00	2017-03-01 18:00:00	2017-03-01 18:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
8	1	2017-03-01 18:45:00	2017-03-01 18:45:00	2017-03-01 18:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
9	1	2017-03-01 23:15:00	2017-03-01 23:15:00	2017-03-01 23:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
10	1	2017-03-02 01:45:00	2017-03-02 01:45:00	2017-03-02 01:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
11	1	2017-03-02 02:00:00	2017-03-02 02:00:00	2017-03-02 02:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
12	1	2017-03-02 05:30:00	2017-03-02 05:30:00	2017-03-02 05:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
13	1	2017-03-02 09:45:00	2017-03-02 09:45:00	2017-03-02 09:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
14	1	2017-03-02 10:30:00	2017-03-02 10:30:00	2017-03-02 10:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
15	1	2017-03-02 13:30:00	2017-03-02 13:30:00	2017-03-02 13:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
16	1	2017-03-02 15:30:00	2017-03-02 15:30:00	2017-03-02 15:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
17	1	2017-03-02 17:15:00	2017-03-02 17:15:00	2017-03-02 17:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
18	1	2017-03-02 21:00:00	2017-03-02 21:00:00	2017-03-02 21:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
19	1	2017-03-02 23:45:00	2017-03-02 23:45:00	2017-03-02 23:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
20	1	2017-03-03 01:45:00	2017-03-03 01:45:00	2017-03-03 01:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
21	1	2017-03-03 02:00:00	2017-03-03 02:00:00	2017-03-03 02:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
22	1	2017-03-03 06:45:00	2017-03-03 06:45:00	2017-03-03 06:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
23	1	2017-03-03 10:45:00	2017-03-03 10:45:00	2017-03-03 10:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
24	1	2017-03-03 13:30:00	2017-03-03 13:30:00	2017-03-03 13:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
25	1	2017-03-03 14:16:00	2017-03-03 14:16:00	2017-03-03 14:16:00	\N	1	\N	1	\N	F	\N	\N	1	\N
26	1	2017-03-03 17:16:00	2017-03-03 17:16:00	2017-03-03 17:16:00	\N	1	\N	1	\N	F	\N	\N	1	\N
27	1	2017-03-03 20:15:00	2017-03-03 20:15:00	2017-03-03 20:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
28	1	2017-03-03 21:00:00	2017-03-03 21:00:00	2017-03-03 21:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
29	1	2017-03-04 00:01:00	2017-03-04 00:01:00	2017-03-04 00:01:00	\N	1	\N	1	\N	F	\N	\N	1	\N
30	1	2017-03-04 03:16:00	2017-03-04 03:16:00	2017-03-04 03:16:00	\N	1	\N	1	\N	F	\N	\N	1	\N
31	1	2017-03-04 06:16:00	2017-03-04 06:16:00	2017-03-04 06:16:00	\N	1	\N	1	\N	F	\N	\N	1	\N
32	1	2017-03-04 07:01:00	2017-03-04 07:01:00	2017-03-04 07:01:00	\N	1	\N	1	\N	F	\N	\N	1	\N
33	1	2017-03-04 10:01:00	2017-03-04 10:01:00	2017-03-04 10:01:00	\N	1	\N	1	\N	F	\N	\N	1	\N
34	1	2017-03-04 13:16:00	2017-03-04 13:16:00	2017-03-04 13:16:00	\N	1	\N	1	\N	F	\N	\N	1	\N
35	1	2017-03-04 16:15:00	2017-03-04 16:15:00	2017-03-04 16:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
36	1	2017-03-04 17:00:00	2017-03-04 17:00:00	2017-03-04 17:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
37	1	2017-03-04 20:00:00	2017-03-04 20:00:00	2017-03-04 20:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
38	1	2017-03-04 23:16:00	2017-03-04 23:16:00	2017-03-04 23:16:00	\N	1	\N	1	\N	F	\N	\N	1	\N
39	1	2017-03-05 02:16:00	2017-03-05 02:16:00	2017-03-05 02:16:00	\N	1	\N	1	\N	F	\N	\N	1	\N
40	1	2017-03-05 03:01:00	2017-03-05 03:01:00	2017-03-05 03:01:00	\N	1	\N	1	\N	F	\N	\N	1	\N
41	1	2017-03-05 06:01:00	2017-03-05 06:01:00	2017-03-05 06:01:00	\N	1	\N	1	\N	F	\N	\N	1	\N
42	1	2017-03-05 09:15:00	2017-03-05 09:15:00	2017-03-05 09:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
43	1	2017-03-05 10:30:00	2017-03-05 10:30:00	2017-03-05 10:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
44	1	2017-03-05 13:15:00	2017-03-05 13:15:00	2017-03-05 13:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
45	1	2017-03-05 16:15:00	2017-03-05 16:15:00	2017-03-05 16:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
46	1	2017-03-05 18:45:00	2017-03-05 18:45:00	2017-03-05 18:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
47	1	2017-03-05 21:15:00	2017-03-05 21:15:00	2017-03-05 21:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
48	1	2017-03-05 23:45:00	2017-03-05 23:45:00	2017-03-05 23:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
49	1	2017-03-06 02:15:00	2017-03-06 02:15:00	2017-03-06 02:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
50	1	2017-03-06 04:45:00	2017-03-06 04:45:00	2017-03-06 04:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
51	1	2017-03-06 07:45:00	2017-03-06 07:45:00	2017-03-06 07:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
52	1	2017-03-06 09:45:00	2017-03-06 09:45:00	2017-03-06 09:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
53	1	2017-03-06 12:15:00	2017-03-06 12:15:00	2017-03-06 12:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
54	1	2017-03-06 14:45:00	2017-03-06 14:45:00	2017-03-06 14:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
55	1	2017-03-06 17:30:00	2017-03-06 17:30:00	2017-03-06 17:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
56	1	2017-03-06 19:14:00	2017-03-06 19:14:00	2017-03-06 19:14:00	\N	1	\N	1	\N	F	\N	\N	1	\N
57	1	2017-03-06 21:45:00	2017-03-06 21:45:00	2017-03-06 21:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
58	1	2017-03-07 01:15:00	2017-03-07 01:15:00	2017-03-07 01:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
59	1	2017-03-07 03:15:00	2017-03-07 03:15:00	2017-03-07 03:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
60	1	2017-03-07 05:15:00	2017-03-07 05:15:00	2017-03-07 05:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
61	1	2017-03-07 08:45:00	2017-03-07 08:45:00	2017-03-07 08:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
62	1	2017-03-07 09:45:00	2017-03-07 09:45:00	2017-03-07 09:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
63	1	2017-03-07 13:00:00	2017-03-07 13:00:00	2017-03-07 13:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
64	1	2017-03-07 13:30:00	2017-03-07 13:30:00	2017-03-07 13:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
65	1	2017-03-07 20:15:00	2017-03-07 20:15:00	2017-03-07 20:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
66	1	2017-03-07 18:15:00	2017-03-07 18:15:00	2017-03-07 18:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
67	1	2017-03-07 22:30:00	2017-03-07 22:30:00	2017-03-07 22:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
68	1	2017-03-08 01:45:00	2017-03-08 01:45:00	2017-03-08 01:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
69	1	2017-03-08 06:15:00	2017-03-08 06:15:00	2017-03-08 06:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
70	1	2017-03-08 05:30:00	2017-03-08 05:30:00	2017-03-08 05:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
71	1	2017-03-08 10:15:00	2017-03-08 10:15:00	2017-03-08 10:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
72	1	2017-03-08 09:30:00	2017-03-08 09:30:00	2017-03-08 09:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
73	1	2017-03-08 15:00:00	2017-03-08 15:00:00	2017-03-08 15:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
74	1	2017-03-08 16:15:00	2017-03-08 16:15:00	2017-03-08 16:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
75	1	2017-03-08 20:00:00	2017-03-08 20:00:00	2017-03-08 20:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
76	1	2017-03-09 01:00:00	2017-03-09 01:00:00	2017-03-09 01:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
77	1	2017-03-08 21:15:00	2017-03-08 21:15:00	2017-03-08 21:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
78	1	2017-03-09 05:30:00	2017-03-09 05:30:00	2017-03-09 05:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
79	1	2017-03-09 03:30:00	2017-03-09 03:30:00	2017-03-09 03:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
80	1	2017-03-09 10:30:00	2017-03-09 10:30:00	2017-03-09 10:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
81	1	2017-03-09 10:45:00	2017-03-09 10:45:00	2017-03-09 10:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
82	1	2017-03-09 16:00:00	2017-03-09 16:00:00	2017-03-09 16:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
83	1	2017-03-09 14:15:00	2017-03-09 14:15:00	2017-03-09 14:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
84	1	2017-03-09 19:00:00	2017-03-09 19:00:00	2017-03-09 19:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
85	1	2017-03-09 21:15:00	2017-03-09 21:15:00	2017-03-09 21:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
86	1	2017-03-10 00:15:00	2017-03-10 00:15:00	2017-03-10 00:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
87	1	2017-03-10 00:00:00	2017-03-10 00:00:00	2017-03-10 00:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
88	1	2017-03-10 06:30:00	2017-03-10 06:30:00	2017-03-10 06:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
89	1	2017-03-10 05:00:00	2017-03-10 05:00:00	2017-03-10 05:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
90	1	2017-03-10 11:30:00	2017-03-10 11:30:00	2017-03-10 11:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
91	1	2017-03-10 10:00:00	2017-03-10 10:00:00	2017-03-10 10:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
92	1	2017-03-10 13:15:00	2017-03-10 13:15:00	2017-03-10 13:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
93	1	2017-03-10 17:15:00	2017-03-10 17:15:00	2017-03-10 17:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
94	1	2017-03-10 17:45:00	2017-03-10 17:45:00	2017-03-10 17:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
95	1	2017-03-10 23:00:00	2017-03-10 23:00:00	2017-03-10 23:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
96	1	2017-03-10 22:45:00	2017-03-10 22:45:00	2017-03-10 22:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
97	1	2017-03-11 00:15:00	2017-03-11 00:15:00	2017-03-11 00:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
98	1	2017-03-11 04:15:00	2017-03-11 04:15:00	2017-03-11 04:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
99	1	2017-03-11 09:00:00	2017-03-11 09:00:00	2017-03-11 09:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
100	1	2017-03-11 13:00:00	2017-03-11 13:00:00	2017-03-11 13:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
101	1	2017-03-11 11:15:00	2017-03-11 11:15:00	2017-03-11 11:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
102	1	2017-03-11 14:30:00	2017-03-11 14:30:00	2017-03-11 14:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
103	1	2017-03-11 19:00:00	2017-03-11 19:00:00	2017-03-11 19:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
104	1	2017-03-11 21:45:00	2017-03-11 21:45:00	2017-03-11 21:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
105	1	2017-03-11 22:15:00	2017-03-11 22:15:00	2017-03-11 22:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
106	1	2017-03-12 02:45:00	2017-03-12 02:45:00	2017-03-12 02:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
107	1	2017-03-12 02:30:00	2017-03-12 02:30:00	2017-03-12 02:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
108	1	2017-03-12 04:30:00	2017-03-12 04:30:00	2017-03-12 04:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
109	1	2017-03-12 09:45:00	2017-03-12 09:45:00	2017-03-12 09:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
110	1	2017-03-12 13:30:00	2017-03-12 13:30:00	2017-03-12 13:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
111	1	2017-03-12 13:15:00	2017-03-12 13:15:00	2017-03-12 13:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
112	1	2017-03-12 15:00:00	2017-03-12 15:00:00	2017-03-12 15:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
113	1	2017-03-12 19:15:00	2017-03-12 19:15:00	2017-03-12 19:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
114	1	2017-03-12 22:30:00	2017-03-12 22:30:00	2017-03-12 22:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
115	1	2017-03-13 01:15:00	2017-03-13 01:15:00	2017-03-13 01:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
116	1	2017-03-13 03:15:00	2017-03-13 03:15:00	2017-03-13 03:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
117	1	2017-03-13 01:45:00	2017-03-13 01:45:00	2017-03-13 01:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
118	1	2017-03-13 08:00:00	2017-03-13 08:00:00	2017-03-13 08:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
119	1	2017-03-13 09:15:00	2017-03-13 09:15:00	2017-03-13 09:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
120	1	2017-03-13 11:30:00	2017-03-13 11:30:00	2017-03-13 11:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
121	1	2017-03-13 15:30:00	2017-03-13 15:30:00	2017-03-13 15:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
122	1	2017-03-13 16:00:00	2017-03-13 16:00:00	2017-03-13 16:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
123	1	2017-03-13 19:30:00	2017-03-13 19:30:00	2017-03-13 19:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
124	1	2017-03-14 00:30:00	2017-03-14 00:30:00	2017-03-14 00:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
125	1	2017-03-14 00:45:00	2017-03-14 00:45:00	2017-03-14 00:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
126	1	2017-03-14 04:15:00	2017-03-14 04:15:00	2017-03-14 04:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
127	1	2017-03-14 03:15:00	2017-03-14 03:15:00	2017-03-14 03:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
128	1	2017-03-14 08:15:00	2017-03-14 08:15:00	2017-03-14 08:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
129	1	2017-03-14 12:15:00	2017-03-14 12:15:00	2017-03-14 12:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
130	1	2017-03-14 12:46:00	2017-03-14 12:46:00	2017-03-14 12:46:00	\N	1	\N	1	\N	F	\N	\N	1	\N
131	1	2017-03-14 15:15:00	2017-03-14 15:15:00	2017-03-14 15:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
132	1	2017-03-14 16:30:00	2017-03-14 16:30:00	2017-03-14 16:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
133	1	2017-03-14 18:45:00	2017-03-14 18:45:00	2017-03-14 18:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
134	1	2017-03-14 23:45:00	2017-03-14 23:45:00	2017-03-14 23:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
135	1	2017-03-15 00:45:00	2017-03-15 00:45:00	2017-03-15 00:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
136	1	2017-03-15 06:00:00	2017-03-15 06:00:00	2017-03-15 06:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
137	1	2017-03-15 05:15:00	2017-03-15 05:15:00	2017-03-15 05:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
138	1	2017-03-15 08:45:00	2017-03-15 08:45:00	2017-03-15 08:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
139	1	2017-03-15 09:15:00	2017-03-15 09:15:00	2017-03-15 09:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
140	1	2017-03-15 16:00:00	2017-03-15 16:00:00	2017-03-15 16:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
141	1	2017-03-15 14:00:00	2017-03-15 14:00:00	2017-03-15 14:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
142	1	2017-03-15 19:00:00	2017-03-15 19:00:00	2017-03-15 19:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
143	1	2017-03-15 19:45:00	2017-03-15 19:45:00	2017-03-15 19:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
144	1	2017-03-16 00:30:00	2017-03-16 00:30:00	2017-03-16 00:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
145	1	2017-03-16 03:15:00	2017-03-16 03:15:00	2017-03-16 03:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
146	1	2017-03-16 06:30:00	2017-03-16 06:30:00	2017-03-16 06:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
147	1	2017-03-16 04:30:00	2017-03-16 04:30:00	2017-03-16 04:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
148	1	2017-03-16 11:15:00	2017-03-16 11:15:00	2017-03-16 11:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
149	1	2017-03-16 09:30:00	2017-03-16 09:30:00	2017-03-16 09:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
150	1	2017-03-16 15:30:00	2017-03-16 15:30:00	2017-03-16 15:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
151	1	2017-03-16 16:00:00	2017-03-16 16:00:00	2017-03-16 16:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
152	1	2017-03-16 20:15:00	2017-03-16 20:15:00	2017-03-16 20:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
153	1	2017-03-16 22:30:00	2017-03-16 22:30:00	2017-03-16 22:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
154	1	2017-03-17 02:30:00	2017-03-17 02:30:00	2017-03-17 02:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
155	1	2017-03-17 01:00:00	2017-03-17 01:00:00	2017-03-17 01:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
156	1	2017-03-17 05:00:00	2017-03-17 05:00:00	2017-03-17 05:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
157	1	2017-03-17 09:15:00	2017-03-17 09:15:00	2017-03-17 09:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
158	1	2017-03-17 14:00:00	2017-03-17 14:00:00	2017-03-17 14:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
159	1	2017-03-17 12:30:00	2017-03-17 12:30:00	2017-03-17 12:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
160	1	2017-03-17 16:45:00	2017-03-17 16:45:00	2017-03-17 16:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
161	1	2017-03-17 17:00:00	2017-03-17 17:00:00	2017-03-17 17:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
162	1	2017-03-17 22:45:00	2017-03-17 22:45:00	2017-03-17 22:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
163	1	2017-03-17 21:00:00	2017-03-17 21:00:00	2017-03-17 21:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
164	1	2017-03-18 01:45:00	2017-03-18 01:45:00	2017-03-18 01:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
165	1	2017-03-18 05:00:00	2017-03-18 05:00:00	2017-03-18 05:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
166	1	2017-03-18 08:30:00	2017-03-18 08:30:00	2017-03-18 08:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
167	1	2017-03-18 08:15:00	2017-03-18 08:15:00	2017-03-18 08:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
168	1	2017-03-18 11:30:00	2017-03-18 11:30:00	2017-03-18 11:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
169	1	2017-03-18 14:30:00	2017-03-18 14:30:00	2017-03-18 14:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
170	1	2017-03-18 17:30:00	2017-03-18 17:30:00	2017-03-18 17:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
171	1	2017-03-18 18:15:00	2017-03-18 18:15:00	2017-03-18 18:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
172	1	2017-03-18 23:45:00	2017-03-18 23:45:00	2017-03-18 23:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
173	1	2017-03-18 23:15:00	2017-03-18 23:15:00	2017-03-18 23:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
174	1	2017-03-19 03:30:00	2017-03-19 03:30:00	2017-03-19 03:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
175	1	2017-03-19 03:45:00	2017-03-19 03:45:00	2017-03-19 03:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
176	1	2017-03-19 07:15:00	2017-03-19 07:15:00	2017-03-19 07:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
177	1	2017-03-19 11:00:00	2017-03-19 11:00:00	2017-03-19 11:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
178	1	2017-03-19 12:00:00	2017-03-19 12:00:00	2017-03-19 12:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
179	1	2017-03-19 16:00:00	2017-03-19 16:00:00	2017-03-19 16:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
180	1	2017-03-19 19:30:00	2017-03-19 19:30:00	2017-03-19 19:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
181	1	2017-03-19 18:15:00	2017-03-19 18:15:00	2017-03-19 18:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
182	1	2017-03-20 02:00:00	2017-03-20 02:00:00	2017-03-20 02:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
183	1	2017-03-19 23:30:00	2017-03-19 23:30:00	2017-03-19 23:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
184	1	2017-03-20 06:30:00	2017-03-20 06:30:00	2017-03-20 06:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
185	1	2017-03-20 04:45:00	2017-03-20 04:45:00	2017-03-20 04:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
186	1	2017-03-20 08:15:00	2017-03-20 08:15:00	2017-03-20 08:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
187	1	2017-03-20 11:00:00	2017-03-20 11:00:00	2017-03-20 11:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
188	1	2017-03-20 12:30:00	2017-03-20 12:30:00	2017-03-20 12:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
189	1	2017-03-20 18:15:00	2017-03-20 18:15:00	2017-03-20 18:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
190	1	2017-03-20 18:00:00	2017-03-20 18:00:00	2017-03-20 18:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
191	1	2017-03-20 22:00:00	2017-03-20 22:00:00	2017-03-20 22:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
192	1	2017-03-20 23:15:00	2017-03-20 23:15:00	2017-03-20 23:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
193	1	2017-03-21 03:30:00	2017-03-21 03:30:00	2017-03-21 03:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
194	1	2017-03-21 05:00:00	2017-03-21 05:00:00	2017-03-21 05:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
195	1	2017-03-21 06:30:00	2017-03-21 06:30:00	2017-03-21 06:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
196	1	2017-03-21 11:00:00	2017-03-21 11:00:00	2017-03-21 11:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
197	1	2017-03-21 13:45:00	2017-03-21 13:45:00	2017-03-21 13:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
198	1	2017-03-21 15:30:00	2017-03-21 15:30:00	2017-03-21 15:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
199	1	2017-03-21 17:15:00	2017-03-21 17:15:00	2017-03-21 17:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
200	1	2017-03-21 18:30:00	2017-03-21 18:30:00	2017-03-21 18:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
201	1	2017-03-21 22:30:00	2017-03-21 22:30:00	2017-03-21 22:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
202	1	2017-03-22 02:45:00	2017-03-22 02:45:00	2017-03-22 02:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
203	1	2017-03-22 03:00:00	2017-03-22 03:00:00	2017-03-22 03:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
204	1	2017-03-22 08:15:00	2017-03-22 08:15:00	2017-03-22 08:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
205	1	2017-03-22 08:30:00	2017-03-22 08:30:00	2017-03-22 08:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
206	1	2017-03-22 11:45:00	2017-03-22 11:45:00	2017-03-22 11:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
207	1	2017-03-22 14:45:00	2017-03-22 14:45:00	2017-03-22 14:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
208	1	2017-03-22 17:45:00	2017-03-22 17:45:00	2017-03-22 17:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
209	1	2017-03-22 16:00:00	2017-03-22 16:00:00	2017-03-22 16:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
210	1	2017-03-22 23:15:00	2017-03-22 23:15:00	2017-03-22 23:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
211	1	2017-03-22 23:45:00	2017-03-22 23:45:00	2017-03-22 23:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
212	1	2017-03-23 03:00:00	2017-03-23 03:00:00	2017-03-23 03:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
213	1	2017-03-23 05:15:00	2017-03-23 05:15:00	2017-03-23 05:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
214	1	2017-03-23 07:30:00	2017-03-23 07:30:00	2017-03-23 07:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
215	1	2017-03-23 06:30:00	2017-03-23 06:30:00	2017-03-23 06:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
216	1	2017-03-23 11:30:00	2017-03-23 11:30:00	2017-03-23 11:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
217	1	2017-03-23 15:00:00	2017-03-23 15:00:00	2017-03-23 15:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
218	1	2017-03-23 15:45:00	2017-03-23 15:45:00	2017-03-23 15:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
219	1	2017-03-23 18:45:00	2017-03-23 18:45:00	2017-03-23 18:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
220	1	2017-03-23 23:00:00	2017-03-23 23:00:00	2017-03-23 23:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
221	1	2017-03-24 01:30:00	2017-03-24 01:30:00	2017-03-24 01:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
222	1	2017-03-24 04:31:00	2017-03-24 04:31:00	2017-03-24 04:31:00	\N	1	\N	1	\N	F	\N	\N	1	\N
223	1	2017-03-24 06:45:00	2017-03-24 06:45:00	2017-03-24 06:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
224	1	2017-03-24 09:00:00	2017-03-24 09:00:00	2017-03-24 09:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
225	1	2017-03-24 12:15:00	2017-03-24 12:15:00	2017-03-24 12:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
226	1	2017-03-24 14:15:00	2017-03-24 14:15:00	2017-03-24 14:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
227	1	2017-03-24 16:30:00	2017-03-24 16:30:00	2017-03-24 16:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
228	1	2017-03-24 15:30:00	2017-03-24 15:30:00	2017-03-24 15:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
229	1	2017-03-24 18:00:00	2017-03-24 18:00:00	2017-03-24 18:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
230	1	2017-03-24 21:00:00	2017-03-24 21:00:00	2017-03-24 21:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
231	1	2017-03-25 00:45:00	2017-03-25 00:45:00	2017-03-25 00:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
232	1	2017-03-25 02:00:00	2017-03-25 02:00:00	2017-03-25 02:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
233	1	2017-03-25 04:15:00	2017-03-25 04:15:00	2017-03-25 04:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
234	1	2017-03-25 10:00:00	2017-03-25 10:00:00	2017-03-25 10:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
235	1	2017-03-25 10:15:00	2017-03-25 10:15:00	2017-03-25 10:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
236	1	2017-03-25 15:00:00	2017-03-25 15:00:00	2017-03-25 15:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
237	1	2017-03-25 17:00:00	2017-03-25 17:00:00	2017-03-25 17:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
238	1	2017-03-25 19:30:00	2017-03-25 19:30:00	2017-03-25 19:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
239	1	2017-03-25 20:15:00	2017-03-25 20:15:00	2017-03-25 20:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
240	1	2017-03-26 00:45:00	2017-03-26 00:45:00	2017-03-26 00:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
241	1	2017-03-26 01:30:00	2017-03-26 01:30:00	2017-03-26 01:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
242	1	2017-03-26 04:00:00	2017-03-26 04:00:00	2017-03-26 04:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
243	1	2017-03-26 08:45:00	2017-03-26 08:45:00	2017-03-26 08:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
244	1	2017-03-26 11:45:00	2017-03-26 11:45:00	2017-03-26 11:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
245	1	2017-03-26 12:15:00	2017-03-26 12:15:00	2017-03-26 12:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
246	1	2017-03-26 15:30:00	2017-03-26 15:30:00	2017-03-26 15:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
247	1	2017-03-26 15:15:00	2017-03-26 15:15:00	2017-03-26 15:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
248	1	2017-03-26 18:00:00	2017-03-26 18:00:00	2017-03-26 18:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
249	1	2017-03-26 23:00:00	2017-03-26 23:00:00	2017-03-26 23:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
250	1	2017-03-26 23:30:00	2017-03-26 23:30:00	2017-03-26 23:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
251	1	2017-03-27 04:15:00	2017-03-27 04:15:00	2017-03-27 04:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
252	1	2017-03-27 04:45:00	2017-03-27 04:45:00	2017-03-27 04:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
253	1	2017-03-27 10:45:00	2017-03-27 10:45:00	2017-03-27 10:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
254	1	2017-03-27 11:15:00	2017-03-27 11:15:00	2017-03-27 11:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
255	1	2017-03-27 15:30:00	2017-03-27 15:30:00	2017-03-27 15:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
256	1	2017-03-27 14:45:00	2017-03-27 14:45:00	2017-03-27 14:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
257	1	2017-03-27 19:30:00	2017-03-27 19:30:00	2017-03-27 19:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
258	1	2017-03-27 18:15:00	2017-03-27 18:15:00	2017-03-27 18:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
259	1	2017-03-28 00:00:00	2017-03-28 00:00:00	2017-03-28 00:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
260	1	2017-03-28 02:00:00	2017-03-28 02:00:00	2017-03-28 02:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
261	1	2017-03-28 04:30:00	2017-03-28 04:30:00	2017-03-28 04:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
262	1	2017-03-28 06:45:00	2017-03-28 06:45:00	2017-03-28 06:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
263	1	2017-03-28 08:45:00	2017-03-28 08:45:00	2017-03-28 08:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
264	1	2017-03-28 13:30:00	2017-03-28 13:30:00	2017-03-28 13:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
265	1	2017-03-28 13:15:00	2017-03-28 13:15:00	2017-03-28 13:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
266	1	2017-03-28 18:15:00	2017-03-28 18:15:00	2017-03-28 18:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
267	1	2017-03-28 17:30:00	2017-03-28 17:30:00	2017-03-28 17:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
268	1	2017-03-28 21:45:00	2017-03-28 21:45:00	2017-03-28 21:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
269	1	2017-03-29 08:00:00	2017-03-29 08:00:00	2017-03-29 08:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
270	1	2017-03-29 02:45:00	2017-03-29 02:45:00	2017-03-29 02:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
271	1	2017-03-29 06:45:00	2017-03-29 06:45:00	2017-03-29 06:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
272	1	2017-03-29 01:30:00	2017-03-29 01:30:00	2017-03-29 01:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
273	1	2017-03-29 18:00:00	2017-03-29 18:00:00	2017-03-29 18:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
274	1	2017-03-29 12:45:00	2017-03-29 12:45:00	2017-03-29 12:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
275	1	2017-03-29 16:45:00	2017-03-29 16:45:00	2017-03-29 16:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
276	1	2017-03-29 11:30:00	2017-03-29 11:30:00	2017-03-29 11:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
277	1	2017-03-30 04:00:00	2017-03-30 04:00:00	2017-03-30 04:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
278	1	2017-03-29 22:45:00	2017-03-29 22:45:00	2017-03-29 22:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
279	1	2017-03-30 02:45:00	2017-03-30 02:45:00	2017-03-30 02:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
280	1	2017-03-29 21:30:00	2017-03-29 21:30:00	2017-03-29 21:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
281	1	2017-03-30 07:15:00	2017-03-30 07:15:00	2017-03-30 07:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
282	1	2017-03-31 12:45:00	2017-03-31 12:45:00	2017-03-31 12:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
283	1	2017-03-30 06:00:00	2017-03-30 06:00:00	2017-03-30 06:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
284	1	2017-03-31 11:30:00	2017-03-31 11:30:00	2017-03-31 11:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
285	1	2017-03-31 16:15:00	2017-03-31 16:15:00	2017-03-31 16:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
286	1	2017-03-31 20:15:00	2017-03-31 20:15:00	2017-03-31 20:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
287	1	2017-03-31 15:45:00	2017-03-31 15:45:00	2017-03-31 15:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
288	1	2017-03-31 22:31:00	2017-03-31 22:31:00	2017-03-31 22:31:00	\N	1	\N	1	\N	F	\N	\N	1	\N
289	2	2017-03-01 01:15:00	2017-03-01 01:15:00	2017-03-01 01:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
290	2	2017-03-01 05:30:00	2017-03-01 05:30:00	2017-03-01 05:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
291	2	2017-03-01 05:00:00	2017-03-01 05:00:00	2017-03-01 05:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
292	2	2017-03-01 10:30:00	2017-03-01 10:30:00	2017-03-01 10:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
293	2	2017-03-01 10:15:00	2017-03-01 10:15:00	2017-03-01 10:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
294	2	2017-03-01 11:45:00	2017-03-01 11:45:00	2017-03-01 11:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
295	2	2017-03-01 16:15:00	2017-03-01 16:15:00	2017-03-01 16:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
296	2	2017-03-01 21:15:00	2017-03-01 21:15:00	2017-03-01 21:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
297	2	2017-03-01 20:15:00	2017-03-01 20:15:00	2017-03-01 20:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
298	2	2017-03-02 01:45:00	2017-03-02 01:45:00	2017-03-02 01:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
299	2	2017-03-02 02:16:00	2017-03-02 02:16:00	2017-03-02 02:16:00	\N	1	\N	1	\N	F	\N	\N	2	\N
300	2	2017-03-02 06:00:00	2017-03-02 06:00:00	2017-03-02 06:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
301	2	2017-03-02 09:30:00	2017-03-02 09:30:00	2017-03-02 09:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
302	2	2017-03-02 11:00:00	2017-03-02 11:00:00	2017-03-02 11:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
303	2	2017-03-02 11:15:00	2017-03-02 11:15:00	2017-03-02 11:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
304	2	2017-03-02 16:00:00	2017-03-02 16:00:00	2017-03-02 16:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
305	2	2017-03-02 18:00:00	2017-03-02 18:00:00	2017-03-02 18:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
306	2	2017-03-02 19:30:00	2017-03-02 19:30:00	2017-03-02 19:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
307	2	2017-03-02 21:15:00	2017-03-02 21:15:00	2017-03-02 21:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
308	2	2017-03-03 01:15:00	2017-03-03 01:15:00	2017-03-03 01:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
309	2	2017-03-03 03:30:00	2017-03-03 03:30:00	2017-03-03 03:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
310	2	2017-03-03 07:00:00	2017-03-03 07:00:00	2017-03-03 07:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
311	2	2017-03-03 07:15:00	2017-03-03 07:15:00	2017-03-03 07:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
312	2	2017-03-03 10:15:00	2017-03-03 10:15:00	2017-03-03 10:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
313	2	2017-03-03 14:16:00	2017-03-03 14:16:00	2017-03-03 14:16:00	\N	1	\N	1	\N	F	\N	\N	2	\N
314	2	2017-03-03 17:01:00	2017-03-03 17:01:00	2017-03-03 17:01:00	\N	1	\N	1	\N	F	\N	\N	2	\N
315	2	2017-03-03 20:00:00	2017-03-03 20:00:00	2017-03-03 20:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
316	2	2017-03-03 21:15:00	2017-03-03 21:15:00	2017-03-03 21:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
317	2	2017-03-04 00:16:00	2017-03-04 00:16:00	2017-03-04 00:16:00	\N	1	\N	1	\N	F	\N	\N	2	\N
318	2	2017-03-04 03:01:00	2017-03-04 03:01:00	2017-03-04 03:01:00	\N	1	\N	1	\N	F	\N	\N	2	\N
319	2	2017-03-04 06:01:00	2017-03-04 06:01:00	2017-03-04 06:01:00	\N	1	\N	1	\N	F	\N	\N	2	\N
320	2	2017-03-04 07:16:00	2017-03-04 07:16:00	2017-03-04 07:16:00	\N	1	\N	1	\N	F	\N	\N	2	\N
321	2	2017-03-04 10:16:00	2017-03-04 10:16:00	2017-03-04 10:16:00	\N	1	\N	1	\N	F	\N	\N	2	\N
322	2	2017-03-04 13:01:00	2017-03-04 13:01:00	2017-03-04 13:01:00	\N	1	\N	1	\N	F	\N	\N	2	\N
323	2	2017-03-04 16:00:00	2017-03-04 16:00:00	2017-03-04 16:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
324	2	2017-03-04 17:15:00	2017-03-04 17:15:00	2017-03-04 17:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
325	2	2017-03-04 20:15:00	2017-03-04 20:15:00	2017-03-04 20:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
326	2	2017-03-04 23:00:00	2017-03-04 23:00:00	2017-03-04 23:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
327	2	2017-03-05 02:01:00	2017-03-05 02:01:00	2017-03-05 02:01:00	\N	1	\N	1	\N	F	\N	\N	2	\N
328	2	2017-03-05 03:16:00	2017-03-05 03:16:00	2017-03-05 03:16:00	\N	1	\N	1	\N	F	\N	\N	2	\N
329	2	2017-03-05 06:16:00	2017-03-05 06:16:00	2017-03-05 06:16:00	\N	1	\N	1	\N	F	\N	\N	2	\N
330	2	2017-03-05 09:00:00	2017-03-05 09:00:00	2017-03-05 09:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
331	2	2017-03-05 12:00:00	2017-03-05 12:00:00	2017-03-05 12:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
332	2	2017-03-05 13:45:00	2017-03-05 13:45:00	2017-03-05 13:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
333	2	2017-03-05 16:15:00	2017-03-05 16:15:00	2017-03-05 16:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
334	2	2017-03-05 18:30:00	2017-03-05 18:30:00	2017-03-05 18:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
335	2	2017-03-05 21:30:00	2017-03-05 21:30:00	2017-03-05 21:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
336	2	2017-03-05 23:45:00	2017-03-05 23:45:00	2017-03-05 23:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
337	2	2017-03-06 02:15:00	2017-03-06 02:15:00	2017-03-06 02:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
338	2	2017-03-06 04:45:00	2017-03-06 04:45:00	2017-03-06 04:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
339	2	2017-03-06 07:15:00	2017-03-06 07:15:00	2017-03-06 07:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
340	2	2017-03-06 09:45:00	2017-03-06 09:45:00	2017-03-06 09:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
341	2	2017-03-06 12:15:00	2017-03-06 12:15:00	2017-03-06 12:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
342	2	2017-03-06 14:00:00	2017-03-06 14:00:00	2017-03-06 14:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
343	2	2017-03-06 17:15:00	2017-03-06 17:15:00	2017-03-06 17:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
344	2	2017-03-06 19:30:00	2017-03-06 19:30:00	2017-03-06 19:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
345	2	2017-03-06 22:00:00	2017-03-06 22:00:00	2017-03-06 22:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
346	2	2017-03-07 00:45:00	2017-03-07 00:45:00	2017-03-07 00:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
347	2	2017-03-07 03:15:00	2017-03-07 03:15:00	2017-03-07 03:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
348	2	2017-03-07 06:00:00	2017-03-07 06:00:00	2017-03-07 06:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
349	2	2017-03-07 09:00:00	2017-03-07 09:00:00	2017-03-07 09:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
350	2	2017-03-07 10:45:00	2017-03-07 10:45:00	2017-03-07 10:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
351	2	2017-03-07 17:15:00	2017-03-07 17:15:00	2017-03-07 17:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
352	2	2017-03-07 13:45:00	2017-03-07 13:45:00	2017-03-07 13:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
353	2	2017-03-07 19:15:00	2017-03-07 19:15:00	2017-03-07 19:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
354	2	2017-03-07 21:15:00	2017-03-07 21:15:00	2017-03-07 21:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
355	2	2017-03-08 01:15:00	2017-03-08 01:15:00	2017-03-08 01:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
356	2	2017-03-08 01:00:00	2017-03-08 01:00:00	2017-03-08 01:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
357	2	2017-03-08 06:15:00	2017-03-08 06:15:00	2017-03-08 06:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
358	2	2017-03-08 04:45:00	2017-03-08 04:45:00	2017-03-08 04:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
359	2	2017-03-08 10:45:00	2017-03-08 10:45:00	2017-03-08 10:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
360	2	2017-03-08 09:00:00	2017-03-08 09:00:00	2017-03-08 09:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
361	2	2017-03-08 14:45:00	2017-03-08 14:45:00	2017-03-08 14:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
362	2	2017-03-08 17:45:00	2017-03-08 17:45:00	2017-03-08 17:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
363	2	2017-03-08 16:30:00	2017-03-08 16:30:00	2017-03-08 16:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
364	2	2017-03-08 23:00:00	2017-03-08 23:00:00	2017-03-08 23:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
365	2	2017-03-09 01:15:00	2017-03-09 01:15:00	2017-03-09 01:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
366	2	2017-03-09 05:15:00	2017-03-09 05:15:00	2017-03-09 05:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
367	2	2017-03-09 03:15:00	2017-03-09 03:15:00	2017-03-09 03:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
368	2	2017-03-09 06:45:00	2017-03-09 06:45:00	2017-03-09 06:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
369	2	2017-03-09 08:15:00	2017-03-09 08:15:00	2017-03-09 08:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
370	2	2017-03-09 14:45:00	2017-03-09 14:45:00	2017-03-09 14:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
371	2	2017-03-09 12:45:00	2017-03-09 12:45:00	2017-03-09 12:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
372	2	2017-03-09 20:45:00	2017-03-09 20:45:00	2017-03-09 20:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
373	2	2017-03-09 19:00:00	2017-03-09 19:00:00	2017-03-09 19:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
374	2	2017-03-10 00:15:00	2017-03-10 00:15:00	2017-03-10 00:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
375	2	2017-03-09 23:15:00	2017-03-09 23:15:00	2017-03-09 23:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
376	2	2017-03-10 06:30:00	2017-03-10 06:30:00	2017-03-10 06:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
377	2	2017-03-10 03:45:00	2017-03-10 03:45:00	2017-03-10 03:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
378	2	2017-03-10 11:30:00	2017-03-10 11:30:00	2017-03-10 11:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
379	2	2017-03-10 10:00:00	2017-03-10 10:00:00	2017-03-10 10:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
380	2	2017-03-10 15:00:00	2017-03-10 15:00:00	2017-03-10 15:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
381	2	2017-03-10 14:00:00	2017-03-10 14:00:00	2017-03-10 14:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
382	2	2017-03-10 18:30:00	2017-03-10 18:30:00	2017-03-10 18:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
383	2	2017-03-10 18:45:00	2017-03-10 18:45:00	2017-03-10 18:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
384	2	2017-03-11 00:30:00	2017-03-11 00:30:00	2017-03-11 00:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
385	2	2017-03-10 23:30:00	2017-03-10 23:30:00	2017-03-10 23:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
386	2	2017-03-11 06:00:00	2017-03-11 06:00:00	2017-03-11 06:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
387	2	2017-03-11 04:30:00	2017-03-11 04:30:00	2017-03-11 04:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
388	2	2017-03-11 11:30:00	2017-03-11 11:30:00	2017-03-11 11:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
389	2	2017-03-11 12:30:00	2017-03-11 12:30:00	2017-03-11 12:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
390	2	2017-03-11 16:30:00	2017-03-11 16:30:00	2017-03-11 16:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
391	2	2017-03-11 15:15:00	2017-03-11 15:15:00	2017-03-11 15:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
392	2	2017-03-11 19:45:00	2017-03-11 19:45:00	2017-03-11 19:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
393	2	2017-03-11 22:00:00	2017-03-11 22:00:00	2017-03-11 22:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
394	2	2017-03-12 00:45:00	2017-03-12 00:45:00	2017-03-12 00:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
395	2	2017-03-12 03:00:00	2017-03-12 03:00:00	2017-03-12 03:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
396	2	2017-03-12 04:30:00	2017-03-12 04:30:00	2017-03-12 04:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
397	2	2017-03-12 06:45:00	2017-03-12 06:45:00	2017-03-12 06:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
398	2	2017-03-12 11:00:00	2017-03-12 11:00:00	2017-03-12 11:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
399	2	2017-03-12 10:45:00	2017-03-12 10:45:00	2017-03-12 10:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
400	2	2017-03-12 18:00:00	2017-03-12 18:00:00	2017-03-12 18:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
401	2	2017-03-12 16:45:00	2017-03-12 16:45:00	2017-03-12 16:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
402	2	2017-03-12 20:15:00	2017-03-12 20:15:00	2017-03-12 20:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
403	2	2017-03-12 23:15:00	2017-03-12 23:15:00	2017-03-12 23:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
404	2	2017-03-13 01:45:00	2017-03-13 01:45:00	2017-03-13 01:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
405	2	2017-03-13 01:15:00	2017-03-13 01:15:00	2017-03-13 01:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
406	2	2017-03-13 09:45:00	2017-03-13 09:45:00	2017-03-13 09:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
407	2	2017-03-13 07:15:00	2017-03-13 07:15:00	2017-03-13 07:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
408	2	2017-03-13 10:30:00	2017-03-13 10:30:00	2017-03-13 10:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
409	2	2017-03-13 14:45:00	2017-03-13 14:45:00	2017-03-13 14:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
410	2	2017-03-13 16:00:00	2017-03-13 16:00:00	2017-03-13 16:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
411	2	2017-03-13 18:30:00	2017-03-13 18:30:00	2017-03-13 18:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
412	2	2017-03-13 20:45:00	2017-03-13 20:45:00	2017-03-13 20:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
413	2	2017-03-13 23:30:00	2017-03-13 23:30:00	2017-03-13 23:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
414	2	2017-03-14 02:45:00	2017-03-14 02:45:00	2017-03-14 02:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
415	2	2017-03-14 04:45:00	2017-03-14 04:45:00	2017-03-14 04:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
416	2	2017-03-14 09:00:00	2017-03-14 09:00:00	2017-03-14 09:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
417	2	2017-03-14 07:15:00	2017-03-14 07:15:00	2017-03-14 07:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
418	2	2017-03-14 16:00:00	2017-03-14 16:00:00	2017-03-14 16:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
419	2	2017-03-14 15:45:00	2017-03-14 15:45:00	2017-03-14 15:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
420	2	2017-03-14 20:00:00	2017-03-14 20:00:00	2017-03-14 20:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
421	2	2017-03-14 19:00:00	2017-03-14 19:00:00	2017-03-14 19:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
422	2	2017-03-15 01:30:00	2017-03-15 01:30:00	2017-03-15 01:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
423	2	2017-03-15 01:00:00	2017-03-15 01:00:00	2017-03-15 01:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
424	2	2017-03-15 03:30:00	2017-03-15 03:30:00	2017-03-15 03:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
425	2	2017-03-15 04:15:00	2017-03-15 04:15:00	2017-03-15 04:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
426	2	2017-03-15 10:30:00	2017-03-15 10:30:00	2017-03-15 10:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
427	2	2017-03-15 11:00:00	2017-03-15 11:00:00	2017-03-15 11:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
428	2	2017-03-15 14:00:00	2017-03-15 14:00:00	2017-03-15 14:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
429	2	2017-03-15 14:15:00	2017-03-15 14:15:00	2017-03-15 14:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
430	2	2017-03-15 20:00:00	2017-03-15 20:00:00	2017-03-15 20:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
431	2	2017-03-15 19:30:00	2017-03-15 19:30:00	2017-03-15 19:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
432	2	2017-03-16 01:30:00	2017-03-16 01:30:00	2017-03-16 01:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
433	2	2017-03-16 04:00:00	2017-03-16 04:00:00	2017-03-16 04:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
434	2	2017-03-16 06:45:00	2017-03-16 06:45:00	2017-03-16 06:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
435	2	2017-03-16 05:15:00	2017-03-16 05:15:00	2017-03-16 05:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
436	2	2017-03-16 08:30:00	2017-03-16 08:30:00	2017-03-16 08:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
437	2	2017-03-16 13:00:00	2017-03-16 13:00:00	2017-03-16 13:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
438	2	2017-03-16 14:45:00	2017-03-16 14:45:00	2017-03-16 14:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
439	2	2017-03-16 17:30:00	2017-03-16 17:30:00	2017-03-16 17:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
440	2	2017-03-16 21:15:00	2017-03-16 21:15:00	2017-03-16 21:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
441	2	2017-03-16 21:00:00	2017-03-16 21:00:00	2017-03-16 21:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
442	2	2017-03-17 01:15:00	2017-03-17 01:15:00	2017-03-17 01:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
443	2	2017-03-17 01:45:00	2017-03-17 01:45:00	2017-03-17 01:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
444	2	2017-03-17 08:15:00	2017-03-17 08:15:00	2017-03-17 08:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
445	2	2017-03-17 06:45:00	2017-03-17 06:45:00	2017-03-17 06:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
446	2	2017-03-17 12:00:00	2017-03-17 12:00:00	2017-03-17 12:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
447	2	2017-03-17 12:45:00	2017-03-17 12:45:00	2017-03-17 12:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
448	2	2017-03-17 14:30:00	2017-03-17 14:30:00	2017-03-17 14:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
449	2	2017-03-17 18:00:00	2017-03-17 18:00:00	2017-03-17 18:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
450	2	2017-03-17 21:45:00	2017-03-17 21:45:00	2017-03-17 21:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
451	2	2017-03-17 22:00:00	2017-03-17 22:00:00	2017-03-17 22:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
452	2	2017-03-18 03:45:00	2017-03-18 03:45:00	2017-03-18 03:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
453	2	2017-03-18 02:15:00	2017-03-18 02:15:00	2017-03-18 02:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
454	2	2017-03-18 07:15:00	2017-03-18 07:15:00	2017-03-18 07:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
455	2	2017-03-18 06:45:00	2017-03-18 06:45:00	2017-03-18 06:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
456	2	2017-03-18 11:30:00	2017-03-18 11:30:00	2017-03-18 11:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
457	2	2017-03-18 15:15:00	2017-03-18 15:15:00	2017-03-18 15:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
458	2	2017-03-18 17:00:00	2017-03-18 17:00:00	2017-03-18 17:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
459	2	2017-03-18 19:30:00	2017-03-18 19:30:00	2017-03-18 19:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
460	2	2017-03-18 23:00:00	2017-03-18 23:00:00	2017-03-18 23:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
461	2	2017-03-19 01:30:00	2017-03-19 01:30:00	2017-03-19 01:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
462	2	2017-03-19 05:30:00	2017-03-19 05:30:00	2017-03-19 05:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
463	2	2017-03-19 02:45:00	2017-03-19 02:45:00	2017-03-19 02:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
464	2	2017-03-19 07:15:00	2017-03-19 07:15:00	2017-03-19 07:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
465	2	2017-03-19 10:00:00	2017-03-19 10:00:00	2017-03-19 10:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
466	2	2017-03-19 13:15:00	2017-03-19 13:15:00	2017-03-19 13:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
467	2	2017-03-19 14:00:00	2017-03-19 14:00:00	2017-03-19 14:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
468	2	2017-03-19 18:30:00	2017-03-19 18:30:00	2017-03-19 18:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
469	2	2017-03-19 18:45:00	2017-03-19 18:45:00	2017-03-19 18:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
470	2	2017-03-19 23:00:00	2017-03-19 23:00:00	2017-03-19 23:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
471	2	2017-03-20 01:15:00	2017-03-20 01:15:00	2017-03-20 01:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
472	2	2017-03-20 05:00:00	2017-03-20 05:00:00	2017-03-20 05:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
473	2	2017-03-20 06:30:00	2017-03-20 06:30:00	2017-03-20 06:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
474	2	2017-03-20 09:15:00	2017-03-20 09:15:00	2017-03-20 09:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
475	2	2017-03-20 11:00:00	2017-03-20 11:00:00	2017-03-20 11:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
476	2	2017-03-20 16:00:00	2017-03-20 16:00:00	2017-03-20 16:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
477	2	2017-03-20 15:30:00	2017-03-20 15:30:00	2017-03-20 15:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
478	2	2017-03-20 18:30:00	2017-03-20 18:30:00	2017-03-20 18:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
479	2	2017-03-20 23:00:00	2017-03-20 23:00:00	2017-03-20 23:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
480	2	2017-03-21 01:15:00	2017-03-21 01:15:00	2017-03-21 01:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
481	2	2017-03-21 03:45:00	2017-03-21 03:45:00	2017-03-21 03:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
482	2	2017-03-21 07:30:00	2017-03-21 07:30:00	2017-03-21 07:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
483	2	2017-03-21 08:15:00	2017-03-21 08:15:00	2017-03-21 08:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
484	2	2017-03-21 09:30:00	2017-03-21 09:30:00	2017-03-21 09:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
485	2	2017-03-21 10:15:00	2017-03-21 10:15:00	2017-03-21 10:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
486	2	2017-03-21 17:00:00	2017-03-21 17:00:00	2017-03-21 17:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
487	2	2017-03-21 19:15:00	2017-03-21 19:15:00	2017-03-21 19:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
488	2	2017-03-21 21:00:00	2017-03-21 21:00:00	2017-03-21 21:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
489	2	2017-03-21 20:45:00	2017-03-21 20:45:00	2017-03-21 20:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
490	2	2017-03-22 02:45:00	2017-03-22 02:45:00	2017-03-22 02:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
491	2	2017-03-22 03:45:00	2017-03-22 03:45:00	2017-03-22 03:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
492	2	2017-03-22 04:00:00	2017-03-22 04:00:00	2017-03-22 04:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
493	2	2017-03-22 06:15:00	2017-03-22 06:15:00	2017-03-22 06:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
494	2	2017-03-22 12:15:00	2017-03-22 12:15:00	2017-03-22 12:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
495	2	2017-03-22 12:45:00	2017-03-22 12:45:00	2017-03-22 12:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
496	2	2017-03-22 15:00:00	2017-03-22 15:00:00	2017-03-22 15:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
497	2	2017-03-22 17:00:00	2017-03-22 17:00:00	2017-03-22 17:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
498	2	2017-03-22 22:45:00	2017-03-22 22:45:00	2017-03-22 22:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
499	2	2017-03-22 21:15:00	2017-03-22 21:15:00	2017-03-22 21:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
500	2	2017-03-23 02:45:00	2017-03-23 02:45:00	2017-03-23 02:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
501	2	2017-03-23 04:30:00	2017-03-23 04:30:00	2017-03-23 04:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
502	2	2017-03-23 08:30:00	2017-03-23 08:30:00	2017-03-23 08:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
503	2	2017-03-23 09:15:00	2017-03-23 09:15:00	2017-03-23 09:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
504	2	2017-03-23 11:30:00	2017-03-23 11:30:00	2017-03-23 11:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
505	2	2017-03-23 14:45:00	2017-03-23 14:45:00	2017-03-23 14:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
506	2	2017-03-23 15:00:00	2017-03-23 15:00:00	2017-03-23 15:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
507	2	2017-03-23 21:00:00	2017-03-23 21:00:00	2017-03-23 21:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
508	2	2017-03-24 00:15:00	2017-03-24 00:15:00	2017-03-24 00:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
509	2	2017-03-23 22:15:00	2017-03-23 22:15:00	2017-03-23 22:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
510	2	2017-03-24 04:00:00	2017-03-24 04:00:00	2017-03-24 04:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
511	2	2017-03-24 07:15:00	2017-03-24 07:15:00	2017-03-24 07:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
512	2	2017-03-24 07:30:00	2017-03-24 07:30:00	2017-03-24 07:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
513	2	2017-03-24 12:30:00	2017-03-24 12:30:00	2017-03-24 12:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
514	2	2017-03-24 13:00:00	2017-03-24 13:00:00	2017-03-24 13:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
515	2	2017-03-24 14:00:00	2017-03-24 14:00:00	2017-03-24 14:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
516	2	2017-03-24 16:30:00	2017-03-24 16:30:00	2017-03-24 16:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
517	2	2017-03-24 20:15:00	2017-03-24 20:15:00	2017-03-24 20:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
518	2	2017-03-24 22:00:00	2017-03-24 22:00:00	2017-03-24 22:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
519	2	2017-03-24 23:30:00	2017-03-24 23:30:00	2017-03-24 23:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
520	2	2017-03-25 05:15:00	2017-03-25 05:15:00	2017-03-25 05:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
521	2	2017-03-25 03:15:00	2017-03-25 03:15:00	2017-03-25 03:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
522	2	2017-03-25 10:15:00	2017-03-25 10:15:00	2017-03-25 10:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
523	2	2017-03-25 13:30:00	2017-03-25 13:30:00	2017-03-25 13:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
524	2	2017-03-25 16:15:00	2017-03-25 16:15:00	2017-03-25 16:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
525	2	2017-03-25 16:45:00	2017-03-25 16:45:00	2017-03-25 16:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
526	2	2017-03-25 18:15:00	2017-03-25 18:15:00	2017-03-25 18:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
527	2	2017-03-25 19:15:00	2017-03-25 19:15:00	2017-03-25 19:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
528	2	2017-03-26 00:30:00	2017-03-26 00:30:00	2017-03-26 00:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
529	2	2017-03-26 03:15:00	2017-03-26 03:15:00	2017-03-26 03:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
530	2	2017-03-26 02:15:00	2017-03-26 02:15:00	2017-03-26 02:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
531	2	2017-03-26 07:15:00	2017-03-26 07:15:00	2017-03-26 07:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
532	2	2017-03-26 10:00:00	2017-03-26 10:00:00	2017-03-26 10:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
533	2	2017-03-26 14:00:00	2017-03-26 14:00:00	2017-03-26 14:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
534	2	2017-03-26 15:45:00	2017-03-26 15:45:00	2017-03-26 15:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
535	2	2017-03-26 16:00:00	2017-03-26 16:00:00	2017-03-26 16:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
536	2	2017-03-26 21:45:00	2017-03-26 21:45:00	2017-03-26 21:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
537	2	2017-03-26 20:30:00	2017-03-26 20:30:00	2017-03-26 20:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
538	2	2017-03-26 23:45:00	2017-03-26 23:45:00	2017-03-26 23:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
539	2	2017-03-27 02:30:00	2017-03-27 02:30:00	2017-03-27 02:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
540	2	2017-03-27 07:45:00	2017-03-27 07:45:00	2017-03-27 07:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
541	2	2017-03-27 09:15:00	2017-03-27 09:15:00	2017-03-27 09:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
542	2	2017-03-27 08:15:00	2017-03-27 08:15:00	2017-03-27 08:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
543	2	2017-03-27 11:45:00	2017-03-27 11:45:00	2017-03-27 11:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
544	2	2017-03-27 15:45:00	2017-03-27 15:45:00	2017-03-27 15:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
545	2	2017-03-27 17:45:00	2017-03-27 17:45:00	2017-03-27 17:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
546	2	2017-03-27 21:00:00	2017-03-27 21:00:00	2017-03-27 21:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
547	2	2017-03-27 22:15:00	2017-03-27 22:15:00	2017-03-27 22:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
548	2	2017-03-28 01:00:00	2017-03-28 01:00:00	2017-03-28 01:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
549	2	2017-03-28 04:00:00	2017-03-28 04:00:00	2017-03-28 04:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
550	2	2017-03-28 07:30:00	2017-03-28 07:30:00	2017-03-28 07:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
551	2	2017-03-28 09:30:00	2017-03-28 09:30:00	2017-03-28 09:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
552	2	2017-03-28 10:00:00	2017-03-28 10:00:00	2017-03-28 10:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
553	2	2017-03-28 12:45:00	2017-03-28 12:45:00	2017-03-28 12:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
554	2	2017-03-28 14:30:00	2017-03-28 14:30:00	2017-03-28 14:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
555	2	2017-03-28 20:15:00	2017-03-28 20:15:00	2017-03-28 20:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
556	2	2017-03-28 21:45:00	2017-03-28 21:45:00	2017-03-28 21:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
557	2	2017-03-29 07:15:00	2017-03-29 07:15:00	2017-03-29 07:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
558	2	2017-03-29 02:00:00	2017-03-29 02:00:00	2017-03-29 02:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
559	2	2017-03-29 06:00:00	2017-03-29 06:00:00	2017-03-29 06:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
560	2	2017-03-29 03:15:00	2017-03-29 03:15:00	2017-03-29 03:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
561	2	2017-03-29 17:15:00	2017-03-29 17:15:00	2017-03-29 17:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
562	2	2017-03-29 12:00:00	2017-03-29 12:00:00	2017-03-29 12:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
563	2	2017-03-29 16:00:00	2017-03-29 16:00:00	2017-03-29 16:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
564	2	2017-03-29 13:15:00	2017-03-29 13:15:00	2017-03-29 13:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
565	2	2017-03-30 03:15:00	2017-03-30 03:15:00	2017-03-30 03:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
566	2	2017-03-29 22:00:00	2017-03-29 22:00:00	2017-03-29 22:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
567	2	2017-03-30 02:00:00	2017-03-30 02:00:00	2017-03-30 02:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
568	2	2017-03-29 23:15:00	2017-03-29 23:15:00	2017-03-29 23:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
569	2	2017-03-30 06:30:00	2017-03-30 06:30:00	2017-03-30 06:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
570	2	2017-03-31 09:30:00	2017-03-31 09:30:00	2017-03-31 09:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
571	2	2017-03-30 05:15:00	2017-03-30 05:15:00	2017-03-30 05:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
572	2	2017-03-31 10:45:00	2017-03-31 10:45:00	2017-03-31 10:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
573	2	2017-03-31 15:30:00	2017-03-31 15:30:00	2017-03-31 15:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
574	2	2017-03-31 19:30:00	2017-03-31 19:30:00	2017-03-31 19:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
575	2	2017-03-31 15:00:00	2017-03-31 15:00:00	2017-03-31 15:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
576	2	2017-03-31 21:46:00	2017-03-31 21:46:00	2017-03-31 21:46:00	\N	1	\N	1	\N	F	\N	\N	2	\N
577	3	2017-03-01 00:30:00	2017-03-01 00:30:00	2017-03-01 00:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
578	3	2017-03-01 04:45:00	2017-03-01 04:45:00	2017-03-01 04:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
579	3	2017-03-01 05:30:00	2017-03-01 05:30:00	2017-03-01 05:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
580	3	2017-03-01 09:45:00	2017-03-01 09:45:00	2017-03-01 09:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
581	3	2017-03-01 10:45:00	2017-03-01 10:45:00	2017-03-01 10:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
582	3	2017-03-01 15:00:00	2017-03-01 15:00:00	2017-03-01 15:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
583	3	2017-03-01 19:00:00	2017-03-01 19:00:00	2017-03-01 19:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
584	3	2017-03-01 20:30:00	2017-03-01 20:30:00	2017-03-01 20:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
585	3	2017-03-01 20:45:00	2017-03-01 20:45:00	2017-03-01 20:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
586	3	2017-03-02 01:00:00	2017-03-02 01:00:00	2017-03-02 01:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
587	3	2017-03-02 05:30:00	2017-03-02 05:30:00	2017-03-02 05:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
588	3	2017-03-02 05:15:00	2017-03-02 05:15:00	2017-03-02 05:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
589	3	2017-03-02 06:45:00	2017-03-02 06:45:00	2017-03-02 06:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
590	3	2017-03-02 10:15:00	2017-03-02 10:15:00	2017-03-02 10:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
591	3	2017-03-02 11:46:00	2017-03-02 11:46:00	2017-03-02 11:46:00	\N	1	\N	1	\N	F	\N	\N	3	\N
592	3	2017-03-02 15:15:00	2017-03-02 15:15:00	2017-03-02 15:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
593	3	2017-03-02 16:45:00	2017-03-02 16:45:00	2017-03-02 16:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
594	3	2017-03-02 20:00:00	2017-03-02 20:00:00	2017-03-02 20:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
595	3	2017-03-03 01:15:00	2017-03-03 01:15:00	2017-03-03 01:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
596	3	2017-03-02 23:15:00	2017-03-02 23:15:00	2017-03-02 23:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
597	3	2017-03-03 04:00:00	2017-03-03 04:00:00	2017-03-03 04:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
598	3	2017-03-03 06:00:00	2017-03-03 06:00:00	2017-03-03 06:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
599	3	2017-03-03 08:30:00	2017-03-03 08:30:00	2017-03-03 08:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
600	3	2017-03-03 11:30:00	2017-03-03 11:30:00	2017-03-03 11:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
601	3	2017-03-03 14:16:00	2017-03-03 14:16:00	2017-03-03 14:16:00	\N	1	\N	1	\N	F	\N	\N	3	\N
602	3	2017-03-03 17:01:00	2017-03-03 17:01:00	2017-03-03 17:01:00	\N	1	\N	1	\N	F	\N	\N	3	\N
603	3	2017-03-03 20:00:00	2017-03-03 20:00:00	2017-03-03 20:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
604	3	2017-03-03 21:15:00	2017-03-03 21:15:00	2017-03-03 21:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
605	3	2017-03-04 00:16:00	2017-03-04 00:16:00	2017-03-04 00:16:00	\N	1	\N	1	\N	F	\N	\N	3	\N
606	3	2017-03-04 03:01:00	2017-03-04 03:01:00	2017-03-04 03:01:00	\N	1	\N	1	\N	F	\N	\N	3	\N
607	3	2017-03-04 06:01:00	2017-03-04 06:01:00	2017-03-04 06:01:00	\N	1	\N	1	\N	F	\N	\N	3	\N
608	3	2017-03-04 07:16:00	2017-03-04 07:16:00	2017-03-04 07:16:00	\N	1	\N	1	\N	F	\N	\N	3	\N
609	3	2017-03-04 10:16:00	2017-03-04 10:16:00	2017-03-04 10:16:00	\N	1	\N	1	\N	F	\N	\N	3	\N
610	3	2017-03-04 13:01:00	2017-03-04 13:01:00	2017-03-04 13:01:00	\N	1	\N	1	\N	F	\N	\N	3	\N
611	3	2017-03-04 16:00:00	2017-03-04 16:00:00	2017-03-04 16:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
612	3	2017-03-04 17:15:00	2017-03-04 17:15:00	2017-03-04 17:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
613	3	2017-03-04 20:15:00	2017-03-04 20:15:00	2017-03-04 20:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
614	3	2017-03-04 23:00:00	2017-03-04 23:00:00	2017-03-04 23:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
615	3	2017-03-05 02:01:00	2017-03-05 02:01:00	2017-03-05 02:01:00	\N	1	\N	1	\N	F	\N	\N	3	\N
616	3	2017-03-05 03:16:00	2017-03-05 03:16:00	2017-03-05 03:16:00	\N	1	\N	1	\N	F	\N	\N	3	\N
617	3	2017-03-05 06:16:00	2017-03-05 06:16:00	2017-03-05 06:16:00	\N	1	\N	1	\N	F	\N	\N	3	\N
618	3	2017-03-05 09:00:00	2017-03-05 09:00:00	2017-03-05 09:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
619	3	2017-03-05 12:00:00	2017-03-05 12:00:00	2017-03-05 12:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
620	3	2017-03-05 13:45:00	2017-03-05 13:45:00	2017-03-05 13:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
621	3	2017-03-05 16:15:00	2017-03-05 16:15:00	2017-03-05 16:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
622	3	2017-03-05 18:30:00	2017-03-05 18:30:00	2017-03-05 18:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
623	3	2017-03-05 21:30:00	2017-03-05 21:30:00	2017-03-05 21:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
624	3	2017-03-05 23:45:00	2017-03-05 23:45:00	2017-03-05 23:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
625	3	2017-03-06 02:15:00	2017-03-06 02:15:00	2017-03-06 02:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
626	3	2017-03-06 04:45:00	2017-03-06 04:45:00	2017-03-06 04:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
627	3	2017-03-06 07:15:00	2017-03-06 07:15:00	2017-03-06 07:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
628	3	2017-03-06 09:45:00	2017-03-06 09:45:00	2017-03-06 09:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
629	3	2017-03-06 11:15:00	2017-03-06 11:15:00	2017-03-06 11:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
630	3	2017-03-06 14:45:00	2017-03-06 14:45:00	2017-03-06 14:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
631	3	2017-03-06 17:30:00	2017-03-06 17:30:00	2017-03-06 17:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
632	3	2017-03-06 19:14:00	2017-03-06 19:14:00	2017-03-06 19:14:00	\N	1	\N	1	\N	F	\N	\N	3	\N
633	3	2017-03-06 21:45:00	2017-03-06 21:45:00	2017-03-06 21:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
634	3	2017-03-07 01:15:00	2017-03-07 01:15:00	2017-03-07 01:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
635	3	2017-03-07 03:15:00	2017-03-07 03:15:00	2017-03-07 03:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
636	3	2017-03-07 05:15:00	2017-03-07 05:15:00	2017-03-07 05:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
637	3	2017-03-07 08:45:00	2017-03-07 08:45:00	2017-03-07 08:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
638	3	2017-03-07 09:45:00	2017-03-07 09:45:00	2017-03-07 09:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
639	3	2017-03-07 15:15:00	2017-03-07 15:15:00	2017-03-07 15:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
640	3	2017-03-07 13:45:00	2017-03-07 13:45:00	2017-03-07 13:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
641	3	2017-03-07 21:30:00	2017-03-07 21:30:00	2017-03-07 21:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
642	3	2017-03-07 19:45:00	2017-03-07 19:45:00	2017-03-07 19:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
643	3	2017-03-08 00:00:00	2017-03-08 00:00:00	2017-03-08 00:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
644	3	2017-03-08 01:30:00	2017-03-08 01:30:00	2017-03-08 01:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
645	3	2017-03-08 05:30:00	2017-03-08 05:30:00	2017-03-08 05:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
646	3	2017-03-08 04:00:00	2017-03-08 04:00:00	2017-03-08 04:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
647	3	2017-03-08 10:15:00	2017-03-08 10:15:00	2017-03-08 10:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
648	3	2017-03-08 08:45:00	2017-03-08 08:45:00	2017-03-08 08:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
649	3	2017-03-08 13:30:00	2017-03-08 13:30:00	2017-03-08 13:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
650	3	2017-03-08 18:15:00	2017-03-08 18:15:00	2017-03-08 18:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
651	3	2017-03-08 19:15:00	2017-03-08 19:15:00	2017-03-08 19:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
652	3	2017-03-09 01:45:00	2017-03-09 01:45:00	2017-03-09 01:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
653	3	2017-03-09 00:45:00	2017-03-09 00:45:00	2017-03-09 00:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
654	3	2017-03-09 02:00:00	2017-03-09 02:00:00	2017-03-09 02:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
655	3	2017-03-09 05:15:00	2017-03-09 05:15:00	2017-03-09 05:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
656	3	2017-03-09 10:15:00	2017-03-09 10:15:00	2017-03-09 10:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
657	3	2017-03-09 09:45:00	2017-03-09 09:45:00	2017-03-09 09:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
658	3	2017-03-09 14:00:00	2017-03-09 14:00:00	2017-03-09 14:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
659	3	2017-03-09 16:15:00	2017-03-09 16:15:00	2017-03-09 16:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
660	3	2017-03-09 18:45:00	2017-03-09 18:45:00	2017-03-09 18:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
661	3	2017-03-09 17:45:00	2017-03-09 17:45:00	2017-03-09 17:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
662	3	2017-03-10 02:00:00	2017-03-10 02:00:00	2017-03-10 02:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
663	3	2017-03-09 22:45:00	2017-03-09 22:45:00	2017-03-09 22:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
664	3	2017-03-10 05:45:00	2017-03-10 05:45:00	2017-03-10 05:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
665	3	2017-03-10 04:15:00	2017-03-10 04:15:00	2017-03-10 04:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
666	3	2017-03-10 10:45:00	2017-03-10 10:45:00	2017-03-10 10:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
667	3	2017-03-10 09:15:00	2017-03-10 09:15:00	2017-03-10 09:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
668	3	2017-03-10 17:01:00	2017-03-10 17:01:00	2017-03-10 17:01:00	\N	1	\N	1	\N	F	\N	\N	3	\N
669	3	2017-03-10 16:15:00	2017-03-10 16:15:00	2017-03-10 16:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
670	3	2017-03-10 19:00:00	2017-03-10 19:00:00	2017-03-10 19:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
671	3	2017-03-10 20:30:00	2017-03-10 20:30:00	2017-03-10 20:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
672	3	2017-03-11 01:15:00	2017-03-11 01:15:00	2017-03-11 01:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
673	3	2017-03-11 03:45:00	2017-03-11 03:45:00	2017-03-11 03:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
674	3	2017-03-11 07:00:00	2017-03-11 07:00:00	2017-03-11 07:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
675	3	2017-03-11 06:45:00	2017-03-11 06:45:00	2017-03-11 06:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
676	3	2017-03-11 11:00:00	2017-03-11 11:00:00	2017-03-11 11:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
677	3	2017-03-11 12:15:00	2017-03-11 12:15:00	2017-03-11 12:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
678	3	2017-03-11 18:00:00	2017-03-11 18:00:00	2017-03-11 18:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
679	3	2017-03-11 14:30:00	2017-03-11 14:30:00	2017-03-11 14:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
680	3	2017-03-11 19:45:00	2017-03-11 19:45:00	2017-03-11 19:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
681	3	2017-03-11 19:30:00	2017-03-11 19:30:00	2017-03-11 19:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
682	3	2017-03-12 03:15:00	2017-03-12 03:15:00	2017-03-12 03:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
683	3	2017-03-12 02:15:00	2017-03-12 02:15:00	2017-03-12 02:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
684	3	2017-03-12 08:15:00	2017-03-12 08:15:00	2017-03-12 08:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
685	3	2017-03-12 08:30:00	2017-03-12 08:30:00	2017-03-12 08:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
686	3	2017-03-12 13:00:00	2017-03-12 13:00:00	2017-03-12 13:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
687	3	2017-03-12 11:15:00	2017-03-12 11:15:00	2017-03-12 11:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
688	3	2017-03-12 18:30:00	2017-03-12 18:30:00	2017-03-12 18:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
689	3	2017-03-12 18:45:00	2017-03-12 18:45:00	2017-03-12 18:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
690	3	2017-03-12 19:30:00	2017-03-12 19:30:00	2017-03-12 19:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
691	3	2017-03-12 21:15:00	2017-03-12 21:15:00	2017-03-12 21:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
692	3	2017-03-13 02:15:00	2017-03-13 02:15:00	2017-03-13 02:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
693	3	2017-03-13 05:15:00	2017-03-13 05:15:00	2017-03-13 05:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
694	3	2017-03-13 06:45:00	2017-03-13 06:45:00	2017-03-13 06:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
695	3	2017-03-13 07:15:00	2017-03-13 07:15:00	2017-03-13 07:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
696	3	2017-03-13 15:00:00	2017-03-13 15:00:00	2017-03-13 15:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
697	3	2017-03-13 14:00:00	2017-03-13 14:00:00	2017-03-13 14:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
698	3	2017-03-13 17:00:00	2017-03-13 17:00:00	2017-03-13 17:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
699	3	2017-03-13 20:00:00	2017-03-13 20:00:00	2017-03-13 20:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
700	3	2017-03-13 23:30:00	2017-03-13 23:30:00	2017-03-13 23:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
701	3	2017-03-14 00:15:00	2017-03-14 00:15:00	2017-03-14 00:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
702	3	2017-03-14 05:00:00	2017-03-14 05:00:00	2017-03-14 05:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
703	3	2017-03-14 04:00:00	2017-03-14 04:00:00	2017-03-14 04:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
704	3	2017-03-14 11:00:00	2017-03-14 11:00:00	2017-03-14 11:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
705	3	2017-03-14 07:45:00	2017-03-14 07:45:00	2017-03-14 07:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
706	3	2017-03-14 12:15:00	2017-03-14 12:15:00	2017-03-14 12:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
707	3	2017-03-14 15:45:00	2017-03-14 15:45:00	2017-03-14 15:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
708	3	2017-03-14 19:45:00	2017-03-14 19:45:00	2017-03-14 19:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
709	3	2017-03-14 21:15:00	2017-03-14 21:15:00	2017-03-14 21:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
710	3	2017-03-15 00:45:00	2017-03-15 00:45:00	2017-03-15 00:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
711	3	2017-03-15 02:30:00	2017-03-15 02:30:00	2017-03-15 02:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
712	3	2017-03-15 03:00:00	2017-03-15 03:00:00	2017-03-15 03:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
713	3	2017-03-15 03:15:00	2017-03-15 03:15:00	2017-03-15 03:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
714	3	2017-03-15 10:00:00	2017-03-15 10:00:00	2017-03-15 10:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
715	3	2017-03-15 12:45:00	2017-03-15 12:45:00	2017-03-15 12:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
716	3	2017-03-15 12:15:00	2017-03-15 12:15:00	2017-03-15 12:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
717	3	2017-03-15 16:15:00	2017-03-15 16:15:00	2017-03-15 16:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
718	3	2017-03-15 19:00:00	2017-03-15 19:00:00	2017-03-15 19:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
719	3	2017-03-15 21:45:00	2017-03-15 21:45:00	2017-03-15 21:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
720	3	2017-03-16 01:15:00	2017-03-16 01:15:00	2017-03-16 01:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
721	3	2017-03-16 03:00:00	2017-03-16 03:00:00	2017-03-16 03:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
722	3	2017-03-16 07:15:00	2017-03-16 07:15:00	2017-03-16 07:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
723	3	2017-03-16 08:15:00	2017-03-16 08:15:00	2017-03-16 08:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
724	3	2017-03-16 10:15:00	2017-03-16 10:15:00	2017-03-16 10:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
725	3	2017-03-16 12:15:00	2017-03-16 12:15:00	2017-03-16 12:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
726	3	2017-03-16 16:00:00	2017-03-16 16:00:00	2017-03-16 16:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
727	3	2017-03-16 15:00:00	2017-03-16 15:00:00	2017-03-16 15:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
728	3	2017-03-16 22:00:00	2017-03-16 22:00:00	2017-03-16 22:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
729	3	2017-03-16 19:15:00	2017-03-16 19:15:00	2017-03-16 19:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
730	3	2017-03-17 03:45:00	2017-03-17 03:45:00	2017-03-17 03:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
731	3	2017-03-17 02:15:00	2017-03-17 02:15:00	2017-03-17 02:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
732	3	2017-03-17 06:15:00	2017-03-17 06:15:00	2017-03-17 06:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
733	3	2017-03-17 06:00:00	2017-03-17 06:00:00	2017-03-17 06:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
734	3	2017-03-17 12:15:00	2017-03-17 12:15:00	2017-03-17 12:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
735	3	2017-03-17 12:00:00	2017-03-17 12:00:00	2017-03-17 12:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
736	3	2017-03-17 15:00:00	2017-03-17 15:00:00	2017-03-17 15:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
737	3	2017-03-17 19:45:00	2017-03-17 19:45:00	2017-03-17 19:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
738	3	2017-03-17 23:30:00	2017-03-17 23:30:00	2017-03-17 23:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
739	3	2017-03-18 00:45:00	2017-03-18 00:45:00	2017-03-18 00:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
740	3	2017-03-18 03:00:00	2017-03-18 03:00:00	2017-03-18 03:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
741	3	2017-03-18 05:30:00	2017-03-18 05:30:00	2017-03-18 05:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
742	3	2017-03-18 05:45:00	2017-03-18 05:45:00	2017-03-18 05:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
743	3	2017-03-18 09:15:00	2017-03-18 09:15:00	2017-03-18 09:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
744	3	2017-03-18 10:30:00	2017-03-18 10:30:00	2017-03-18 10:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
745	3	2017-03-18 11:15:00	2017-03-18 11:15:00	2017-03-18 11:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
746	3	2017-03-18 16:30:00	2017-03-18 16:30:00	2017-03-18 16:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
747	3	2017-03-18 18:15:00	2017-03-18 18:15:00	2017-03-18 18:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
748	3	2017-03-18 21:15:00	2017-03-18 21:15:00	2017-03-18 21:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
749	3	2017-03-19 00:00:00	2017-03-19 00:00:00	2017-03-19 00:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
750	3	2017-03-19 02:30:00	2017-03-19 02:30:00	2017-03-19 02:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
751	3	2017-03-19 04:30:00	2017-03-19 04:30:00	2017-03-19 04:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
752	3	2017-03-19 08:45:00	2017-03-19 08:45:00	2017-03-19 08:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
753	3	2017-03-19 07:30:00	2017-03-19 07:30:00	2017-03-19 07:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
754	3	2017-03-19 13:45:00	2017-03-19 13:45:00	2017-03-19 13:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
755	3	2017-03-19 13:00:00	2017-03-19 13:00:00	2017-03-19 13:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
756	3	2017-03-19 19:30:00	2017-03-19 19:30:00	2017-03-19 19:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
757	3	2017-03-19 19:00:00	2017-03-19 19:00:00	2017-03-19 19:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
758	3	2017-03-19 22:15:00	2017-03-19 22:15:00	2017-03-19 22:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
759	3	2017-03-20 01:00:00	2017-03-20 01:00:00	2017-03-20 01:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
760	3	2017-03-20 05:30:00	2017-03-20 05:30:00	2017-03-20 05:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
761	3	2017-03-20 07:00:00	2017-03-20 07:00:00	2017-03-20 07:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
762	3	2017-03-20 10:15:00	2017-03-20 10:15:00	2017-03-20 10:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
763	3	2017-03-20 09:45:00	2017-03-20 09:45:00	2017-03-20 09:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
764	3	2017-03-20 12:15:00	2017-03-20 12:15:00	2017-03-20 12:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
765	3	2017-03-20 14:45:00	2017-03-20 14:45:00	2017-03-20 14:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
766	3	2017-03-20 16:45:00	2017-03-20 16:45:00	2017-03-20 16:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
767	3	2017-03-20 21:00:00	2017-03-20 21:00:00	2017-03-20 21:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
768	3	2017-03-21 00:30:00	2017-03-21 00:30:00	2017-03-21 00:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
769	3	2017-03-21 04:30:00	2017-03-21 04:30:00	2017-03-21 04:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
770	3	2017-03-21 04:00:00	2017-03-21 04:00:00	2017-03-21 04:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
771	3	2017-03-21 05:30:00	2017-03-21 05:30:00	2017-03-21 05:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
772	3	2017-03-21 11:15:00	2017-03-21 11:15:00	2017-03-21 11:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
773	3	2017-03-21 13:30:00	2017-03-21 13:30:00	2017-03-21 13:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
774	3	2017-03-21 16:00:00	2017-03-21 16:00:00	2017-03-21 16:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
775	3	2017-03-21 17:15:00	2017-03-21 17:15:00	2017-03-21 17:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
776	3	2017-03-21 19:45:00	2017-03-21 19:45:00	2017-03-21 19:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
777	3	2017-03-21 23:30:00	2017-03-21 23:30:00	2017-03-21 23:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
778	3	2017-03-22 00:45:00	2017-03-22 00:45:00	2017-03-22 00:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
779	3	2017-03-22 01:45:00	2017-03-22 01:45:00	2017-03-22 01:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
780	3	2017-03-22 06:15:00	2017-03-22 06:15:00	2017-03-22 06:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
781	3	2017-03-22 08:15:00	2017-03-22 08:15:00	2017-03-22 08:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
782	3	2017-03-22 09:30:00	2017-03-22 09:30:00	2017-03-22 09:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
783	3	2017-03-22 11:45:00	2017-03-22 11:45:00	2017-03-22 11:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
784	3	2017-03-22 14:30:00	2017-03-22 14:30:00	2017-03-22 14:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
785	3	2017-03-22 17:30:00	2017-03-22 17:30:00	2017-03-22 17:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
786	3	2017-03-22 22:00:00	2017-03-22 22:00:00	2017-03-22 22:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
787	3	2017-03-22 21:00:00	2017-03-22 21:00:00	2017-03-22 21:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
788	3	2017-03-23 01:30:00	2017-03-23 01:30:00	2017-03-23 01:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
789	3	2017-03-23 03:00:00	2017-03-23 03:00:00	2017-03-23 03:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
790	3	2017-03-23 07:45:00	2017-03-23 07:45:00	2017-03-23 07:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
791	3	2017-03-23 08:45:00	2017-03-23 08:45:00	2017-03-23 08:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
792	3	2017-03-23 10:45:00	2017-03-23 10:45:00	2017-03-23 10:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
793	3	2017-03-23 14:00:00	2017-03-23 14:00:00	2017-03-23 14:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
794	3	2017-03-23 18:30:00	2017-03-23 18:30:00	2017-03-23 18:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
795	3	2017-03-23 17:45:00	2017-03-23 17:45:00	2017-03-23 17:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
796	3	2017-03-23 21:00:00	2017-03-23 21:00:00	2017-03-23 21:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
797	3	2017-03-23 22:45:00	2017-03-23 22:45:00	2017-03-23 22:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
798	3	2017-03-24 03:30:00	2017-03-24 03:30:00	2017-03-24 03:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
799	3	2017-03-24 02:00:00	2017-03-24 02:00:00	2017-03-24 02:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
800	3	2017-03-24 10:15:00	2017-03-24 10:15:00	2017-03-24 10:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
801	3	2017-03-24 08:15:00	2017-03-24 08:15:00	2017-03-24 08:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
802	3	2017-03-24 13:30:00	2017-03-24 13:30:00	2017-03-24 13:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
803	3	2017-03-24 16:45:00	2017-03-24 16:45:00	2017-03-24 16:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
804	3	2017-03-24 17:00:00	2017-03-24 17:00:00	2017-03-24 17:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
805	3	2017-03-24 20:45:00	2017-03-24 20:45:00	2017-03-24 20:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
806	3	2017-03-24 22:30:00	2017-03-24 22:30:00	2017-03-24 22:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
807	3	2017-03-24 23:00:00	2017-03-24 23:00:00	2017-03-24 23:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
808	3	2017-03-25 01:30:00	2017-03-25 01:30:00	2017-03-25 01:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
809	3	2017-03-25 06:30:00	2017-03-25 06:30:00	2017-03-25 06:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
810	3	2017-03-25 10:45:00	2017-03-25 10:45:00	2017-03-25 10:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
811	3	2017-03-25 09:30:00	2017-03-25 09:30:00	2017-03-25 09:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
812	3	2017-03-25 11:45:00	2017-03-25 11:45:00	2017-03-25 11:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
813	3	2017-03-25 15:00:00	2017-03-25 15:00:00	2017-03-25 15:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
814	3	2017-03-25 20:15:00	2017-03-25 20:15:00	2017-03-25 20:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
815	3	2017-03-25 20:00:00	2017-03-25 20:00:00	2017-03-25 20:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
816	3	2017-03-26 00:15:00	2017-03-26 00:15:00	2017-03-26 00:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
817	3	2017-03-26 01:00:00	2017-03-26 01:00:00	2017-03-26 01:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
818	3	2017-03-26 05:00:00	2017-03-26 05:00:00	2017-03-26 05:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
819	3	2017-03-26 07:45:00	2017-03-26 07:45:00	2017-03-26 07:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
820	3	2017-03-26 09:15:00	2017-03-26 09:15:00	2017-03-26 09:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
821	3	2017-03-26 11:30:00	2017-03-26 11:30:00	2017-03-26 11:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
822	3	2017-03-26 16:15:00	2017-03-26 16:15:00	2017-03-26 16:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
823	3	2017-03-26 18:00:00	2017-03-26 18:00:00	2017-03-26 18:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
824	3	2017-03-26 22:15:00	2017-03-26 22:15:00	2017-03-26 22:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
825	3	2017-03-26 21:00:00	2017-03-26 21:00:00	2017-03-26 21:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
826	3	2017-03-27 02:00:00	2017-03-27 02:00:00	2017-03-27 02:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
827	3	2017-03-27 03:00:00	2017-03-27 03:00:00	2017-03-27 03:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
828	3	2017-03-27 07:00:00	2017-03-27 07:00:00	2017-03-27 07:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
829	3	2017-03-27 08:00:00	2017-03-27 08:00:00	2017-03-27 08:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
830	3	2017-03-27 09:30:00	2017-03-27 09:30:00	2017-03-27 09:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
831	3	2017-03-27 15:30:00	2017-03-27 15:30:00	2017-03-27 15:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
832	3	2017-03-27 16:15:00	2017-03-27 16:15:00	2017-03-27 16:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
833	3	2017-03-27 18:15:00	2017-03-27 18:15:00	2017-03-27 18:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
834	3	2017-03-27 23:00:00	2017-03-27 23:00:00	2017-03-27 23:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
835	3	2017-03-27 22:45:00	2017-03-27 22:45:00	2017-03-27 22:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
836	3	2017-03-28 02:15:00	2017-03-28 02:15:00	2017-03-28 02:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
837	3	2017-03-28 03:15:00	2017-03-28 03:15:00	2017-03-28 03:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
838	3	2017-03-28 06:45:00	2017-03-28 06:45:00	2017-03-28 06:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
839	3	2017-03-28 07:00:00	2017-03-28 07:00:00	2017-03-28 07:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
840	3	2017-03-28 12:30:00	2017-03-28 12:30:00	2017-03-28 12:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
841	3	2017-03-28 13:15:00	2017-03-28 13:15:00	2017-03-28 13:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
842	3	2017-03-28 16:00:00	2017-03-28 16:00:00	2017-03-28 16:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
843	3	2017-03-28 20:45:00	2017-03-28 20:45:00	2017-03-28 20:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
844	3	2017-03-28 21:45:00	2017-03-28 21:45:00	2017-03-28 21:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
845	3	2017-03-28 23:00:00	2017-03-28 23:00:00	2017-03-28 23:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
846	3	2017-03-29 05:30:00	2017-03-29 05:30:00	2017-03-29 05:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
847	3	2017-03-29 00:15:00	2017-03-29 00:15:00	2017-03-29 00:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
848	3	2017-03-29 04:15:00	2017-03-29 04:15:00	2017-03-29 04:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
849	3	2017-03-29 09:00:00	2017-03-29 09:00:00	2017-03-29 09:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
850	3	2017-03-29 15:30:00	2017-03-29 15:30:00	2017-03-29 15:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
851	3	2017-03-29 10:15:00	2017-03-29 10:15:00	2017-03-29 10:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
852	3	2017-03-29 14:15:00	2017-03-29 14:15:00	2017-03-29 14:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
853	3	2017-03-29 19:00:00	2017-03-29 19:00:00	2017-03-29 19:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
854	3	2017-03-30 01:30:00	2017-03-30 01:30:00	2017-03-30 01:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
855	3	2017-03-29 20:15:00	2017-03-29 20:15:00	2017-03-29 20:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
856	3	2017-03-30 00:15:00	2017-03-30 00:15:00	2017-03-30 00:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
857	3	2017-03-30 05:00:00	2017-03-30 05:00:00	2017-03-30 05:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
858	3	2017-03-30 04:45:00	2017-03-30 04:45:00	2017-03-30 04:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
859	3	2017-03-31 10:15:00	2017-03-31 10:15:00	2017-03-31 10:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
860	3	2017-03-31 11:45:00	2017-03-31 11:45:00	2017-03-31 11:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
861	3	2017-03-31 16:30:00	2017-03-31 16:30:00	2017-03-31 16:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
862	3	2017-03-31 23:00:00	2017-03-31 23:00:00	2017-03-31 23:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
863	3	2017-03-31 18:30:00	2017-03-31 18:30:00	2017-03-31 18:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
864	3	2017-03-31 22:46:00	2017-03-31 22:46:00	2017-03-31 22:46:00	\N	1	\N	1	\N	F	\N	\N	3	\N
865	4	2017-02-28 23:45:00	2017-02-28 23:45:00	2017-02-28 23:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
866	4	2017-03-01 02:45:00	2017-03-01 02:45:00	2017-03-01 02:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
867	4	2017-03-01 04:46:00	2017-03-01 04:46:00	2017-03-01 04:46:00	\N	1	\N	1	\N	F	\N	\N	4	\N
868	4	2017-03-01 07:45:00	2017-03-01 07:45:00	2017-03-01 07:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
869	4	2017-03-01 09:45:00	2017-03-01 09:45:00	2017-03-01 09:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
870	4	2017-03-01 10:15:00	2017-03-01 10:15:00	2017-03-01 10:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
871	4	2017-03-01 17:00:00	2017-03-01 17:00:00	2017-03-01 17:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
872	4	2017-03-01 18:30:00	2017-03-01 18:30:00	2017-03-01 18:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
873	4	2017-03-01 18:45:00	2017-03-01 18:45:00	2017-03-01 18:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
874	4	2017-03-01 23:30:00	2017-03-01 23:30:00	2017-03-01 23:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
875	4	2017-03-02 03:30:00	2017-03-02 03:30:00	2017-03-02 03:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
876	4	2017-03-02 04:30:00	2017-03-02 04:30:00	2017-03-02 04:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
877	4	2017-03-02 06:00:00	2017-03-02 06:00:00	2017-03-02 06:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
878	4	2017-03-02 09:30:00	2017-03-02 09:30:00	2017-03-02 09:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
879	4	2017-03-02 09:45:00	2017-03-02 09:45:00	2017-03-02 09:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
880	4	2017-03-02 14:30:00	2017-03-02 14:30:00	2017-03-02 14:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
881	4	2017-03-02 16:00:00	2017-03-02 16:00:00	2017-03-02 16:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
882	4	2017-03-02 20:15:00	2017-03-02 20:15:00	2017-03-02 20:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
883	4	2017-03-02 23:15:00	2017-03-02 23:15:00	2017-03-02 23:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
884	4	2017-03-03 01:45:00	2017-03-03 01:45:00	2017-03-03 01:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
885	4	2017-03-03 02:00:00	2017-03-03 02:00:00	2017-03-03 02:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
886	4	2017-03-03 05:30:00	2017-03-03 05:30:00	2017-03-03 05:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
887	4	2017-03-03 10:15:00	2017-03-03 10:15:00	2017-03-03 10:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
888	4	2017-03-03 11:00:00	2017-03-03 11:00:00	2017-03-03 11:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
889	4	2017-03-03 13:16:00	2017-03-03 13:16:00	2017-03-03 13:16:00	\N	1	\N	1	\N	F	\N	\N	4	\N
890	4	2017-03-03 16:01:00	2017-03-03 16:01:00	2017-03-03 16:01:00	\N	1	\N	1	\N	F	\N	\N	4	\N
891	4	2017-03-03 19:00:00	2017-03-03 19:00:00	2017-03-03 19:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
892	4	2017-03-03 20:15:00	2017-03-03 20:15:00	2017-03-03 20:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
893	4	2017-03-03 23:16:00	2017-03-03 23:16:00	2017-03-03 23:16:00	\N	1	\N	1	\N	F	\N	\N	4	\N
894	4	2017-03-04 02:01:00	2017-03-04 02:01:00	2017-03-04 02:01:00	\N	1	\N	1	\N	F	\N	\N	4	\N
895	4	2017-03-04 05:01:00	2017-03-04 05:01:00	2017-03-04 05:01:00	\N	1	\N	1	\N	F	\N	\N	4	\N
896	4	2017-03-04 06:16:00	2017-03-04 06:16:00	2017-03-04 06:16:00	\N	1	\N	1	\N	F	\N	\N	4	\N
897	4	2017-03-04 09:16:00	2017-03-04 09:16:00	2017-03-04 09:16:00	\N	1	\N	1	\N	F	\N	\N	4	\N
898	4	2017-03-04 12:01:00	2017-03-04 12:01:00	2017-03-04 12:01:00	\N	1	\N	1	\N	F	\N	\N	4	\N
899	4	2017-03-04 15:00:00	2017-03-04 15:00:00	2017-03-04 15:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
900	4	2017-03-04 16:15:00	2017-03-04 16:15:00	2017-03-04 16:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
901	4	2017-03-04 19:15:00	2017-03-04 19:15:00	2017-03-04 19:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
902	4	2017-03-04 22:00:00	2017-03-04 22:00:00	2017-03-04 22:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
903	4	2017-03-05 01:01:00	2017-03-05 01:01:00	2017-03-05 01:01:00	\N	1	\N	1	\N	F	\N	\N	4	\N
904	4	2017-03-05 02:16:00	2017-03-05 02:16:00	2017-03-05 02:16:00	\N	1	\N	1	\N	F	\N	\N	4	\N
905	4	2017-03-05 05:16:00	2017-03-05 05:16:00	2017-03-05 05:16:00	\N	1	\N	1	\N	F	\N	\N	4	\N
906	4	2017-03-05 08:00:00	2017-03-05 08:00:00	2017-03-05 08:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
907	4	2017-03-05 11:00:00	2017-03-05 11:00:00	2017-03-05 11:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
908	4	2017-03-05 12:45:00	2017-03-05 12:45:00	2017-03-05 12:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
909	4	2017-03-05 15:15:00	2017-03-05 15:15:00	2017-03-05 15:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
910	4	2017-03-05 17:30:00	2017-03-05 17:30:00	2017-03-05 17:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
911	4	2017-03-05 20:15:00	2017-03-05 20:15:00	2017-03-05 20:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
912	4	2017-03-05 22:45:00	2017-03-05 22:45:00	2017-03-05 22:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
913	4	2017-03-06 01:15:00	2017-03-06 01:15:00	2017-03-06 01:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
914	4	2017-03-06 03:45:00	2017-03-06 03:45:00	2017-03-06 03:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
915	4	2017-03-06 06:15:00	2017-03-06 06:15:00	2017-03-06 06:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
916	4	2017-03-06 08:45:00	2017-03-06 08:45:00	2017-03-06 08:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
917	4	2017-03-06 10:45:00	2017-03-06 10:45:00	2017-03-06 10:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
918	4	2017-03-06 13:30:00	2017-03-06 13:30:00	2017-03-06 13:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
919	4	2017-03-06 17:15:00	2017-03-06 17:15:00	2017-03-06 17:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
920	4	2017-03-06 18:45:00	2017-03-06 18:45:00	2017-03-06 18:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
921	4	2017-03-06 21:45:00	2017-03-06 21:45:00	2017-03-06 21:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
922	4	2017-03-07 00:30:00	2017-03-07 00:30:00	2017-03-07 00:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
923	4	2017-03-07 01:45:00	2017-03-07 01:45:00	2017-03-07 01:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
924	4	2017-03-07 04:30:00	2017-03-07 04:30:00	2017-03-07 04:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
925	4	2017-03-07 07:15:00	2017-03-07 07:15:00	2017-03-07 07:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
926	4	2017-03-07 10:00:00	2017-03-07 10:00:00	2017-03-07 10:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
927	4	2017-03-07 12:45:00	2017-03-07 12:45:00	2017-03-07 12:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
928	4	2017-03-07 16:15:00	2017-03-07 16:15:00	2017-03-07 16:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
929	4	2017-03-07 17:30:00	2017-03-07 17:30:00	2017-03-07 17:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
930	4	2017-03-07 21:15:00	2017-03-07 21:15:00	2017-03-07 21:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
931	4	2017-03-07 22:00:00	2017-03-07 22:00:00	2017-03-07 22:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
932	4	2017-03-07 23:00:00	2017-03-07 23:00:00	2017-03-07 23:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
933	4	2017-03-08 03:15:00	2017-03-08 03:15:00	2017-03-08 03:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
934	4	2017-03-08 07:00:00	2017-03-08 07:00:00	2017-03-08 07:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
935	4	2017-03-08 08:15:00	2017-03-08 08:15:00	2017-03-08 08:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
936	4	2017-03-08 09:00:00	2017-03-08 09:00:00	2017-03-08 09:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
937	4	2017-03-08 13:45:00	2017-03-08 13:45:00	2017-03-08 13:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
938	4	2017-03-08 20:15:00	2017-03-08 20:15:00	2017-03-08 20:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
939	4	2017-03-08 16:30:00	2017-03-08 16:30:00	2017-03-08 16:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
940	4	2017-03-08 23:30:00	2017-03-08 23:30:00	2017-03-08 23:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
941	4	2017-03-08 23:45:00	2017-03-08 23:45:00	2017-03-08 23:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
942	4	2017-03-09 01:15:00	2017-03-09 01:15:00	2017-03-09 01:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
943	4	2017-03-09 03:15:00	2017-03-09 03:15:00	2017-03-09 03:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
944	4	2017-03-09 08:30:00	2017-03-09 08:30:00	2017-03-09 08:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
945	4	2017-03-09 08:00:00	2017-03-09 08:00:00	2017-03-09 08:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
946	4	2017-03-09 13:15:00	2017-03-09 13:15:00	2017-03-09 13:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
947	4	2017-03-09 15:30:00	2017-03-09 15:30:00	2017-03-09 15:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
948	4	2017-03-09 18:00:00	2017-03-09 18:00:00	2017-03-09 18:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
949	4	2017-03-09 17:15:00	2017-03-09 17:15:00	2017-03-09 17:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
950	4	2017-03-10 01:15:00	2017-03-10 01:15:00	2017-03-10 01:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
951	4	2017-03-10 01:00:00	2017-03-10 01:00:00	2017-03-10 01:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
952	4	2017-03-10 03:45:00	2017-03-10 03:45:00	2017-03-10 03:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
953	4	2017-03-10 02:15:00	2017-03-10 02:15:00	2017-03-10 02:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
954	4	2017-03-10 10:00:00	2017-03-10 10:00:00	2017-03-10 10:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
955	4	2017-03-10 12:00:00	2017-03-10 12:00:00	2017-03-10 12:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
956	4	2017-03-10 13:30:00	2017-03-10 13:30:00	2017-03-10 13:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
957	4	2017-03-10 12:30:00	2017-03-10 12:30:00	2017-03-10 12:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
958	4	2017-03-10 21:15:00	2017-03-10 21:15:00	2017-03-10 21:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
959	4	2017-03-10 17:15:00	2017-03-10 17:15:00	2017-03-10 17:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
960	4	2017-03-11 01:00:00	2017-03-11 01:00:00	2017-03-11 01:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
961	4	2017-03-11 01:15:00	2017-03-11 01:15:00	2017-03-11 01:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
962	4	2017-03-11 04:30:00	2017-03-11 04:30:00	2017-03-11 04:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
963	4	2017-03-11 04:15:00	2017-03-11 04:15:00	2017-03-11 04:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
964	4	2017-03-11 09:15:00	2017-03-11 09:15:00	2017-03-11 09:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
965	4	2017-03-11 10:45:00	2017-03-11 10:45:00	2017-03-11 10:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
966	4	2017-03-11 17:00:00	2017-03-11 17:00:00	2017-03-11 17:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
967	4	2017-03-11 17:30:00	2017-03-11 17:30:00	2017-03-11 17:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
968	4	2017-03-11 18:45:00	2017-03-11 18:45:00	2017-03-11 18:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
969	4	2017-03-11 19:30:00	2017-03-11 19:30:00	2017-03-11 19:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
970	4	2017-03-12 02:30:00	2017-03-12 02:30:00	2017-03-12 02:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
971	4	2017-03-12 01:30:00	2017-03-12 01:30:00	2017-03-12 01:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
972	4	2017-03-12 07:30:00	2017-03-12 07:30:00	2017-03-12 07:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
973	4	2017-03-12 08:16:00	2017-03-12 08:16:00	2017-03-12 08:16:00	\N	1	\N	1	\N	F	\N	\N	4	\N
974	4	2017-03-12 09:30:00	2017-03-12 09:30:00	2017-03-12 09:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
975	4	2017-03-12 10:30:00	2017-03-12 10:30:00	2017-03-12 10:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
976	4	2017-03-12 16:30:00	2017-03-12 16:30:00	2017-03-12 16:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
977	4	2017-03-12 15:15:00	2017-03-12 15:15:00	2017-03-12 15:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
978	4	2017-03-12 20:00:00	2017-03-12 20:00:00	2017-03-12 20:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
979	4	2017-03-12 20:30:00	2017-03-12 20:30:00	2017-03-12 20:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
980	4	2017-03-13 00:15:00	2017-03-13 00:15:00	2017-03-13 00:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
981	4	2017-03-13 03:15:00	2017-03-13 03:15:00	2017-03-13 03:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
982	4	2017-03-13 06:00:00	2017-03-13 06:00:00	2017-03-13 06:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
983	4	2017-03-13 09:15:00	2017-03-13 09:15:00	2017-03-13 09:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
984	4	2017-03-13 09:30:00	2017-03-13 09:30:00	2017-03-13 09:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
985	4	2017-03-13 12:15:00	2017-03-13 12:15:00	2017-03-13 12:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
986	4	2017-03-13 17:00:00	2017-03-13 17:00:00	2017-03-13 17:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
987	4	2017-03-13 18:00:00	2017-03-13 18:00:00	2017-03-13 18:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
988	4	2017-03-13 23:15:00	2017-03-13 23:15:00	2017-03-13 23:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
989	4	2017-03-13 22:00:00	2017-03-13 22:00:00	2017-03-13 22:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
990	4	2017-03-14 00:00:00	2017-03-14 00:00:00	2017-03-14 00:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
991	4	2017-03-14 02:00:00	2017-03-14 02:00:00	2017-03-14 02:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
992	4	2017-03-14 06:15:00	2017-03-14 06:15:00	2017-03-14 06:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
993	4	2017-03-14 10:30:00	2017-03-14 10:30:00	2017-03-14 10:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
994	4	2017-03-14 11:30:00	2017-03-14 11:30:00	2017-03-14 11:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
995	4	2017-03-14 12:30:00	2017-03-14 12:30:00	2017-03-14 12:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
996	4	2017-03-14 16:45:00	2017-03-14 16:45:00	2017-03-14 16:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
997	4	2017-03-14 17:30:00	2017-03-14 17:30:00	2017-03-14 17:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
998	4	2017-03-14 21:30:00	2017-03-14 21:30:00	2017-03-14 21:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
999	4	2017-03-15 01:45:00	2017-03-15 01:45:00	2017-03-15 01:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1000	4	2017-03-15 02:00:00	2017-03-15 02:00:00	2017-03-15 02:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1001	4	2017-03-15 04:00:00	2017-03-15 04:00:00	2017-03-15 04:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1002	4	2017-03-15 06:30:00	2017-03-15 06:30:00	2017-03-15 06:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1003	4	2017-03-15 08:30:00	2017-03-15 08:30:00	2017-03-15 08:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1004	4	2017-03-15 14:00:00	2017-03-15 14:00:00	2017-03-15 14:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1005	4	2017-03-15 15:30:00	2017-03-15 15:30:00	2017-03-15 15:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1006	4	2017-03-15 20:45:00	2017-03-15 20:45:00	2017-03-15 20:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1007	4	2017-03-15 21:15:00	2017-03-15 21:15:00	2017-03-15 21:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1008	4	2017-03-15 22:30:00	2017-03-15 22:30:00	2017-03-15 22:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1009	4	2017-03-15 23:15:00	2017-03-15 23:15:00	2017-03-15 23:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1010	4	2017-03-16 05:15:00	2017-03-16 05:15:00	2017-03-16 05:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1011	4	2017-03-16 05:00:00	2017-03-16 05:00:00	2017-03-16 05:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1012	4	2017-03-16 08:15:00	2017-03-16 08:15:00	2017-03-16 08:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1013	4	2017-03-16 11:30:00	2017-03-16 11:30:00	2017-03-16 11:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1014	4	2017-03-16 15:15:00	2017-03-16 15:15:00	2017-03-16 15:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1015	4	2017-03-16 14:15:00	2017-03-16 14:15:00	2017-03-16 14:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1016	4	2017-03-16 19:45:00	2017-03-16 19:45:00	2017-03-16 19:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1017	4	2017-03-16 20:45:00	2017-03-16 20:45:00	2017-03-16 20:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1018	4	2017-03-17 01:45:00	2017-03-17 01:45:00	2017-03-17 01:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1019	4	2017-03-17 03:45:00	2017-03-17 03:45:00	2017-03-17 03:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1020	4	2017-03-17 05:30:00	2017-03-17 05:30:00	2017-03-17 05:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1021	4	2017-03-17 07:30:00	2017-03-17 07:30:00	2017-03-17 07:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1022	4	2017-03-17 09:15:00	2017-03-17 09:15:00	2017-03-17 09:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1023	4	2017-03-17 13:45:00	2017-03-17 13:45:00	2017-03-17 13:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1024	4	2017-03-17 17:00:00	2017-03-17 17:00:00	2017-03-17 17:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1025	4	2017-03-17 17:45:00	2017-03-17 17:45:00	2017-03-17 17:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1026	4	2017-03-17 21:30:00	2017-03-17 21:30:00	2017-03-17 21:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1027	4	2017-03-17 23:30:00	2017-03-17 23:30:00	2017-03-17 23:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1028	4	2017-03-18 01:00:00	2017-03-18 01:00:00	2017-03-18 01:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1029	4	2017-03-18 05:15:00	2017-03-18 05:15:00	2017-03-18 05:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1030	4	2017-03-18 08:45:00	2017-03-18 08:45:00	2017-03-18 08:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1031	4	2017-03-18 09:30:00	2017-03-18 09:30:00	2017-03-18 09:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1032	4	2017-03-18 10:45:00	2017-03-18 10:45:00	2017-03-18 10:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1033	4	2017-03-18 12:30:00	2017-03-18 12:30:00	2017-03-18 12:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1034	4	2017-03-18 17:00:00	2017-03-18 17:00:00	2017-03-18 17:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1035	4	2017-03-18 17:30:00	2017-03-18 17:30:00	2017-03-18 17:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1036	4	2017-03-18 20:30:00	2017-03-18 20:30:00	2017-03-18 20:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1037	4	2017-03-18 20:45:00	2017-03-18 20:45:00	2017-03-18 20:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1038	4	2017-03-19 00:30:00	2017-03-19 00:30:00	2017-03-19 00:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1039	4	2017-03-19 02:45:00	2017-03-19 02:45:00	2017-03-19 02:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1040	4	2017-03-19 08:00:00	2017-03-19 08:00:00	2017-03-19 08:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1041	4	2017-03-19 09:45:00	2017-03-19 09:45:00	2017-03-19 09:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1042	4	2017-03-19 11:45:00	2017-03-19 11:45:00	2017-03-19 11:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1043	4	2017-03-19 16:00:00	2017-03-19 16:00:00	2017-03-19 16:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1044	4	2017-03-19 17:00:00	2017-03-19 17:00:00	2017-03-19 17:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1045	4	2017-03-19 18:00:00	2017-03-19 18:00:00	2017-03-19 18:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1046	4	2017-03-19 21:30:00	2017-03-19 21:30:00	2017-03-19 21:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1047	4	2017-03-20 01:00:00	2017-03-20 01:00:00	2017-03-20 01:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1048	4	2017-03-20 01:15:00	2017-03-20 01:15:00	2017-03-20 01:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1049	4	2017-03-20 06:15:00	2017-03-20 06:15:00	2017-03-20 06:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1050	4	2017-03-20 07:45:00	2017-03-20 07:45:00	2017-03-20 07:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1051	4	2017-03-20 08:30:00	2017-03-20 08:30:00	2017-03-20 08:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1052	4	2017-03-20 15:00:00	2017-03-20 15:00:00	2017-03-20 15:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1053	4	2017-03-20 17:00:00	2017-03-20 17:00:00	2017-03-20 17:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1054	4	2017-03-20 16:00:00	2017-03-20 16:00:00	2017-03-20 16:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1055	4	2017-03-20 19:00:00	2017-03-20 19:00:00	2017-03-20 19:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1056	4	2017-03-20 22:30:00	2017-03-20 22:30:00	2017-03-20 22:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1057	4	2017-03-21 02:00:00	2017-03-21 02:00:00	2017-03-21 02:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1058	4	2017-03-21 03:15:00	2017-03-21 03:15:00	2017-03-21 03:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1059	4	2017-03-21 04:45:00	2017-03-21 04:45:00	2017-03-21 04:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1060	4	2017-03-21 09:45:00	2017-03-21 09:45:00	2017-03-21 09:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1061	4	2017-03-21 13:30:00	2017-03-21 13:30:00	2017-03-21 13:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1062	4	2017-03-21 15:45:00	2017-03-21 15:45:00	2017-03-21 15:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1063	4	2017-03-21 16:45:00	2017-03-21 16:45:00	2017-03-21 16:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1064	4	2017-03-21 20:45:00	2017-03-21 20:45:00	2017-03-21 20:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1065	4	2017-03-21 22:45:00	2017-03-21 22:45:00	2017-03-21 22:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1066	4	2017-03-22 00:15:00	2017-03-22 00:15:00	2017-03-22 00:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1067	4	2017-03-22 04:30:00	2017-03-22 04:30:00	2017-03-22 04:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1068	4	2017-03-22 04:15:00	2017-03-22 04:15:00	2017-03-22 04:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1069	4	2017-03-22 08:15:00	2017-03-22 08:15:00	2017-03-22 08:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1070	4	2017-03-22 11:45:00	2017-03-22 11:45:00	2017-03-22 11:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1071	4	2017-03-22 12:15:00	2017-03-22 12:15:00	2017-03-22 12:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1072	4	2017-03-22 12:45:00	2017-03-22 12:45:00	2017-03-22 12:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1073	4	2017-03-22 19:15:00	2017-03-22 19:15:00	2017-03-22 19:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1074	4	2017-03-22 20:00:00	2017-03-22 20:00:00	2017-03-22 20:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1075	4	2017-03-22 23:00:00	2017-03-22 23:00:00	2017-03-22 23:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1076	4	2017-03-23 02:30:00	2017-03-23 02:30:00	2017-03-23 02:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1077	4	2017-03-23 04:15:00	2017-03-23 04:15:00	2017-03-23 04:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1078	4	2017-03-23 05:45:00	2017-03-23 05:45:00	2017-03-23 05:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1079	4	2017-03-23 08:00:00	2017-03-23 08:00:00	2017-03-23 08:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1080	4	2017-03-23 12:45:00	2017-03-23 12:45:00	2017-03-23 12:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1081	4	2017-03-23 12:30:00	2017-03-23 12:30:00	2017-03-23 12:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1082	4	2017-03-23 16:30:00	2017-03-23 16:30:00	2017-03-23 16:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1083	4	2017-03-23 17:00:00	2017-03-23 17:00:00	2017-03-23 17:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1084	4	2017-03-23 20:15:00	2017-03-23 20:15:00	2017-03-23 20:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1085	4	2017-03-24 00:15:00	2017-03-24 00:15:00	2017-03-24 00:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1086	4	2017-03-24 02:45:00	2017-03-24 02:45:00	2017-03-24 02:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1087	4	2017-03-24 05:30:00	2017-03-24 05:30:00	2017-03-24 05:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1088	4	2017-03-24 08:30:00	2017-03-24 08:30:00	2017-03-24 08:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1089	4	2017-03-24 10:30:00	2017-03-24 10:30:00	2017-03-24 10:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1090	4	2017-03-24 13:15:00	2017-03-24 13:15:00	2017-03-24 13:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1091	4	2017-03-24 14:15:00	2017-03-24 14:15:00	2017-03-24 14:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1092	4	2017-03-24 17:00:00	2017-03-24 17:00:00	2017-03-24 17:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1093	4	2017-03-24 18:45:00	2017-03-24 18:45:00	2017-03-24 18:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1094	4	2017-03-24 20:15:00	2017-03-24 20:15:00	2017-03-24 20:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1095	4	2017-03-25 00:15:00	2017-03-25 00:15:00	2017-03-25 00:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1096	4	2017-03-25 05:00:00	2017-03-25 05:00:00	2017-03-25 05:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1097	4	2017-03-25 06:45:00	2017-03-25 06:45:00	2017-03-25 06:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1098	4	2017-03-25 07:45:00	2017-03-25 07:45:00	2017-03-25 07:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1099	4	2017-03-25 12:15:00	2017-03-25 12:15:00	2017-03-25 12:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1100	4	2017-03-25 14:45:00	2017-03-25 14:45:00	2017-03-25 14:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1101	4	2017-03-25 13:45:00	2017-03-25 13:45:00	2017-03-25 13:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1102	4	2017-03-25 19:30:00	2017-03-25 19:30:00	2017-03-25 19:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1103	4	2017-03-25 18:30:00	2017-03-25 18:30:00	2017-03-25 18:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1104	4	2017-03-25 23:00:00	2017-03-25 23:00:00	2017-03-25 23:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1105	4	2017-03-26 02:30:00	2017-03-26 02:30:00	2017-03-26 02:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1106	4	2017-03-26 03:45:00	2017-03-26 03:45:00	2017-03-26 03:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1107	4	2017-03-26 07:00:00	2017-03-26 07:00:00	2017-03-26 07:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1108	4	2017-03-26 09:45:00	2017-03-26 09:45:00	2017-03-26 09:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1109	4	2017-03-26 09:00:00	2017-03-26 09:00:00	2017-03-26 09:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1110	4	2017-03-26 13:15:00	2017-03-26 13:15:00	2017-03-26 13:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1111	4	2017-03-26 14:30:00	2017-03-26 14:30:00	2017-03-26 14:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1112	4	2017-03-26 16:30:00	2017-03-26 16:30:00	2017-03-26 16:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1113	4	2017-03-26 20:45:00	2017-03-26 20:45:00	2017-03-26 20:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1114	4	2017-03-27 01:15:00	2017-03-27 01:15:00	2017-03-27 01:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1115	4	2017-03-27 02:15:00	2017-03-27 02:15:00	2017-03-27 02:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1116	4	2017-03-27 06:15:00	2017-03-27 06:15:00	2017-03-27 06:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1117	4	2017-03-27 10:00:00	2017-03-27 10:00:00	2017-03-27 10:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1118	4	2017-03-27 09:30:00	2017-03-27 09:30:00	2017-03-27 09:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1119	4	2017-03-27 11:00:00	2017-03-27 11:00:00	2017-03-27 11:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1120	4	2017-03-27 13:15:00	2017-03-27 13:15:00	2017-03-27 13:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1121	4	2017-03-27 17:30:00	2017-03-27 17:30:00	2017-03-27 17:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1122	4	2017-03-27 19:30:00	2017-03-27 19:30:00	2017-03-27 19:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1123	4	2017-03-27 20:45:00	2017-03-27 20:45:00	2017-03-27 20:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1124	4	2017-03-28 00:15:00	2017-03-28 00:15:00	2017-03-28 00:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1125	4	2017-03-28 04:00:00	2017-03-28 04:00:00	2017-03-28 04:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1126	4	2017-03-28 06:00:00	2017-03-28 06:00:00	2017-03-28 06:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1127	4	2017-03-28 07:30:00	2017-03-28 07:30:00	2017-03-28 07:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1128	4	2017-03-28 10:00:00	2017-03-28 10:00:00	2017-03-28 10:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1129	4	2017-03-28 12:30:00	2017-03-28 12:30:00	2017-03-28 12:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1130	4	2017-03-28 15:30:00	2017-03-28 15:30:00	2017-03-28 15:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1131	4	2017-03-28 20:00:00	2017-03-28 20:00:00	2017-03-28 20:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1132	4	2017-03-28 20:45:00	2017-03-28 20:45:00	2017-03-28 20:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1133	4	2017-03-29 05:15:00	2017-03-29 05:15:00	2017-03-29 05:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1134	4	2017-03-29 00:00:00	2017-03-29 00:00:00	2017-03-29 00:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1135	4	2017-03-29 06:30:00	2017-03-29 06:30:00	2017-03-29 06:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1136	4	2017-03-29 01:15:00	2017-03-29 01:15:00	2017-03-29 01:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1137	4	2017-03-29 15:15:00	2017-03-29 15:15:00	2017-03-29 15:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1138	4	2017-03-29 10:00:00	2017-03-29 10:00:00	2017-03-29 10:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1139	4	2017-03-29 16:30:00	2017-03-29 16:30:00	2017-03-29 16:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1140	4	2017-03-29 11:15:00	2017-03-29 11:15:00	2017-03-29 11:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1141	4	2017-03-30 01:15:00	2017-03-30 01:15:00	2017-03-30 01:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1142	4	2017-03-29 20:00:00	2017-03-29 20:00:00	2017-03-29 20:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1143	4	2017-03-30 02:30:00	2017-03-30 02:30:00	2017-03-30 02:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1144	4	2017-03-29 21:15:00	2017-03-29 21:15:00	2017-03-29 21:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1145	4	2017-03-31 12:45:00	2017-03-31 12:45:00	2017-03-31 12:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1146	4	2017-03-31 07:30:00	2017-03-31 07:30:00	2017-03-31 07:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1147	4	2017-03-30 05:45:00	2017-03-30 05:45:00	2017-03-30 05:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1148	4	2017-03-31 08:45:00	2017-03-31 08:45:00	2017-03-31 08:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1149	4	2017-03-31 13:30:00	2017-03-31 13:30:00	2017-03-31 13:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1150	4	2017-03-31 20:00:00	2017-03-31 20:00:00	2017-03-31 20:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1151	4	2017-03-31 15:30:00	2017-03-31 15:30:00	2017-03-31 15:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1152	4	2017-03-31 22:15:00	2017-03-31 22:15:00	2017-03-31 22:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1153	5	2017-03-01 03:15:00	2017-03-01 03:15:00	2017-03-01 03:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1154	5	2017-03-01 04:00:00	2017-03-01 04:00:00	2017-03-01 04:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1155	5	2017-03-01 09:30:00	2017-03-01 09:30:00	2017-03-01 09:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1156	5	2017-03-01 09:00:00	2017-03-01 09:00:00	2017-03-01 09:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1157	5	2017-03-01 12:30:00	2017-03-01 12:30:00	2017-03-01 12:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1158	5	2017-03-01 15:30:00	2017-03-01 15:30:00	2017-03-01 15:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1159	5	2017-03-01 17:30:00	2017-03-01 17:30:00	2017-03-01 17:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1160	5	2017-03-01 19:00:00	2017-03-01 19:00:00	2017-03-01 19:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1161	5	2017-03-01 21:30:00	2017-03-01 21:30:00	2017-03-01 21:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1162	5	2017-03-02 01:15:00	2017-03-02 01:15:00	2017-03-02 01:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1163	5	2017-03-02 04:15:00	2017-03-02 04:15:00	2017-03-02 04:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1164	5	2017-03-02 05:00:00	2017-03-02 05:00:00	2017-03-02 05:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1165	5	2017-03-02 10:30:00	2017-03-02 10:30:00	2017-03-02 10:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1166	5	2017-03-02 10:00:00	2017-03-02 10:00:00	2017-03-02 10:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1167	5	2017-03-02 14:15:00	2017-03-02 14:15:00	2017-03-02 14:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1168	5	2017-03-02 15:00:00	2017-03-02 15:00:00	2017-03-02 15:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1169	5	2017-03-02 17:45:00	2017-03-02 17:45:00	2017-03-02 17:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1170	5	2017-03-02 20:30:00	2017-03-02 20:30:00	2017-03-02 20:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1171	5	2017-03-02 23:15:00	2017-03-02 23:15:00	2017-03-02 23:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1172	5	2017-03-03 02:30:00	2017-03-03 02:30:00	2017-03-03 02:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1173	5	2017-03-03 04:00:00	2017-03-03 04:00:00	2017-03-03 04:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1174	5	2017-03-03 06:45:00	2017-03-03 06:45:00	2017-03-03 06:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1175	5	2017-03-03 07:45:00	2017-03-03 07:45:00	2017-03-03 07:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1176	5	2017-03-03 12:45:00	2017-03-03 12:45:00	2017-03-03 12:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1177	5	2017-03-03 14:16:00	2017-03-03 14:16:00	2017-03-03 14:16:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1178	5	2017-03-03 16:46:00	2017-03-03 16:46:00	2017-03-03 16:46:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1179	5	2017-03-03 19:45:00	2017-03-03 19:45:00	2017-03-03 19:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1180	5	2017-03-03 21:00:00	2017-03-03 21:00:00	2017-03-03 21:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1181	5	2017-03-04 00:01:00	2017-03-04 00:01:00	2017-03-04 00:01:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1182	5	2017-03-04 02:46:00	2017-03-04 02:46:00	2017-03-04 02:46:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1183	5	2017-03-04 05:46:00	2017-03-04 05:46:00	2017-03-04 05:46:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1184	5	2017-03-04 07:01:00	2017-03-04 07:01:00	2017-03-04 07:01:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1185	5	2017-03-04 10:01:00	2017-03-04 10:01:00	2017-03-04 10:01:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1186	5	2017-03-04 12:46:00	2017-03-04 12:46:00	2017-03-04 12:46:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1187	5	2017-03-04 15:45:00	2017-03-04 15:45:00	2017-03-04 15:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1188	5	2017-03-04 17:00:00	2017-03-04 17:00:00	2017-03-04 17:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1189	5	2017-03-04 20:00:00	2017-03-04 20:00:00	2017-03-04 20:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1190	5	2017-03-04 22:45:00	2017-03-04 22:45:00	2017-03-04 22:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1191	5	2017-03-05 01:46:00	2017-03-05 01:46:00	2017-03-05 01:46:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1192	5	2017-03-05 03:01:00	2017-03-05 03:01:00	2017-03-05 03:01:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1193	5	2017-03-05 06:01:00	2017-03-05 06:01:00	2017-03-05 06:01:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1194	5	2017-03-05 08:45:00	2017-03-05 08:45:00	2017-03-05 08:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1195	5	2017-03-05 11:45:00	2017-03-05 11:45:00	2017-03-05 11:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1196	5	2017-03-05 14:00:00	2017-03-05 14:00:00	2017-03-05 14:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1197	5	2017-03-05 16:15:00	2017-03-05 16:15:00	2017-03-05 16:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1198	5	2017-03-05 18:45:00	2017-03-05 18:45:00	2017-03-05 18:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1199	5	2017-03-05 21:15:00	2017-03-05 21:15:00	2017-03-05 21:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1200	5	2017-03-05 23:45:00	2017-03-05 23:45:00	2017-03-05 23:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1201	5	2017-03-06 02:15:00	2017-03-06 02:15:00	2017-03-06 02:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1202	5	2017-03-06 04:45:00	2017-03-06 04:45:00	2017-03-06 04:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1203	5	2017-03-06 07:15:00	2017-03-06 07:15:00	2017-03-06 07:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1204	5	2017-03-06 09:45:00	2017-03-06 09:45:00	2017-03-06 09:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1205	5	2017-03-06 11:30:00	2017-03-06 11:30:00	2017-03-06 11:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1206	5	2017-03-06 15:00:00	2017-03-06 15:00:00	2017-03-06 15:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1207	5	2017-03-06 18:00:00	2017-03-06 18:00:00	2017-03-06 18:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1208	5	2017-03-06 19:30:00	2017-03-06 19:30:00	2017-03-06 19:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1209	5	2017-03-06 22:00:00	2017-03-06 22:00:00	2017-03-06 22:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1210	5	2017-03-07 00:45:00	2017-03-07 00:45:00	2017-03-07 00:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1211	5	2017-03-07 03:15:00	2017-03-07 03:15:00	2017-03-07 03:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1212	5	2017-03-07 06:00:00	2017-03-07 06:00:00	2017-03-07 06:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1213	5	2017-03-07 09:00:00	2017-03-07 09:00:00	2017-03-07 09:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1214	5	2017-03-07 10:45:00	2017-03-07 10:45:00	2017-03-07 10:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1215	5	2017-03-07 15:45:00	2017-03-07 15:45:00	2017-03-07 15:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1216	5	2017-03-07 15:15:00	2017-03-07 15:15:00	2017-03-07 15:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1217	5	2017-03-07 21:45:00	2017-03-07 21:45:00	2017-03-07 21:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1218	5	2017-03-07 19:45:00	2017-03-07 19:45:00	2017-03-07 19:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1219	5	2017-03-07 23:15:00	2017-03-07 23:15:00	2017-03-07 23:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1220	5	2017-03-08 00:15:00	2017-03-08 00:15:00	2017-03-08 00:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1221	5	2017-03-08 05:45:00	2017-03-08 05:45:00	2017-03-08 05:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1222	5	2017-03-08 04:45:00	2017-03-08 04:45:00	2017-03-08 04:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1223	5	2017-03-08 09:30:00	2017-03-08 09:30:00	2017-03-08 09:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1224	5	2017-03-08 13:15:00	2017-03-08 13:15:00	2017-03-08 13:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1225	5	2017-03-08 14:00:00	2017-03-08 14:00:00	2017-03-08 14:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1226	5	2017-03-08 19:45:00	2017-03-08 19:45:00	2017-03-08 19:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1227	5	2017-03-08 20:00:00	2017-03-08 20:00:00	2017-03-08 20:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1228	5	2017-03-09 00:30:00	2017-03-09 00:30:00	2017-03-09 00:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1229	5	2017-03-08 23:45:00	2017-03-08 23:45:00	2017-03-08 23:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1230	5	2017-03-09 05:00:00	2017-03-09 05:00:00	2017-03-09 05:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1231	5	2017-03-09 03:00:00	2017-03-09 03:00:00	2017-03-09 03:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1232	5	2017-03-09 11:15:00	2017-03-09 11:15:00	2017-03-09 11:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1233	5	2017-03-09 11:30:00	2017-03-09 11:30:00	2017-03-09 11:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1234	5	2017-03-09 12:00:00	2017-03-09 12:00:00	2017-03-09 12:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1235	5	2017-03-09 15:00:00	2017-03-09 15:00:00	2017-03-09 15:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1236	5	2017-03-09 19:45:00	2017-03-09 19:45:00	2017-03-09 19:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1237	5	2017-03-09 22:00:00	2017-03-09 22:00:00	2017-03-09 22:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1238	5	2017-03-10 01:00:00	2017-03-10 01:00:00	2017-03-10 01:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1239	5	2017-03-10 00:45:00	2017-03-10 00:45:00	2017-03-10 00:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1240	5	2017-03-10 07:15:00	2017-03-10 07:15:00	2017-03-10 07:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1241	5	2017-03-10 04:30:00	2017-03-10 04:30:00	2017-03-10 04:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1242	5	2017-03-10 12:15:00	2017-03-10 12:15:00	2017-03-10 12:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1243	5	2017-03-10 08:15:00	2017-03-10 08:15:00	2017-03-10 08:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1244	5	2017-03-10 13:30:00	2017-03-10 13:30:00	2017-03-10 13:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1245	5	2017-03-10 14:45:00	2017-03-10 14:45:00	2017-03-10 14:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1246	5	2017-03-10 20:30:00	2017-03-10 20:30:00	2017-03-10 20:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1247	5	2017-03-10 22:00:00	2017-03-10 22:00:00	2017-03-10 22:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1248	5	2017-03-11 00:45:00	2017-03-11 00:45:00	2017-03-11 00:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1249	5	2017-03-11 01:15:00	2017-03-11 01:15:00	2017-03-11 01:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1250	5	2017-03-11 04:15:00	2017-03-11 04:15:00	2017-03-11 04:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1251	5	2017-03-11 08:30:00	2017-03-11 08:30:00	2017-03-11 08:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1252	5	2017-03-11 12:45:00	2017-03-11 12:45:00	2017-03-11 12:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1253	5	2017-03-11 10:00:00	2017-03-11 10:00:00	2017-03-11 10:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1254	5	2017-03-11 13:30:00	2017-03-11 13:30:00	2017-03-11 13:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1255	5	2017-03-11 15:00:00	2017-03-11 15:00:00	2017-03-11 15:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1256	5	2017-03-11 19:15:00	2017-03-11 19:15:00	2017-03-11 19:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1257	5	2017-03-11 20:30:00	2017-03-11 20:30:00	2017-03-11 20:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1258	5	2017-03-12 03:30:00	2017-03-12 03:30:00	2017-03-12 03:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1259	5	2017-03-12 02:00:00	2017-03-12 02:00:00	2017-03-12 02:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1260	5	2017-03-12 07:15:00	2017-03-12 07:15:00	2017-03-12 07:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1261	5	2017-03-12 09:45:00	2017-03-12 09:45:00	2017-03-12 09:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1262	5	2017-03-12 13:00:00	2017-03-12 13:00:00	2017-03-12 13:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1263	5	2017-03-12 11:00:00	2017-03-12 11:00:00	2017-03-12 11:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1264	5	2017-03-12 17:00:00	2017-03-12 17:00:00	2017-03-12 17:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1265	5	2017-03-12 19:15:00	2017-03-12 19:15:00	2017-03-12 19:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1266	5	2017-03-12 20:46:00	2017-03-12 20:46:00	2017-03-12 20:46:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1267	5	2017-03-13 00:45:00	2017-03-13 00:45:00	2017-03-13 00:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1268	5	2017-03-13 02:45:00	2017-03-13 02:45:00	2017-03-13 02:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1269	5	2017-03-13 04:30:00	2017-03-13 04:30:00	2017-03-13 04:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1270	5	2017-03-13 06:15:00	2017-03-13 06:15:00	2017-03-13 06:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1271	5	2017-03-13 11:00:00	2017-03-13 11:00:00	2017-03-13 11:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1272	5	2017-03-13 13:30:00	2017-03-13 13:30:00	2017-03-13 13:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1273	5	2017-03-13 14:15:00	2017-03-13 14:15:00	2017-03-13 14:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1274	5	2017-03-13 18:15:00	2017-03-13 18:15:00	2017-03-13 18:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1275	5	2017-03-13 20:30:00	2017-03-13 20:30:00	2017-03-13 20:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1276	5	2017-03-13 22:00:00	2017-03-13 22:00:00	2017-03-13 22:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1277	5	2017-03-14 00:45:00	2017-03-14 00:45:00	2017-03-14 00:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1278	5	2017-03-14 05:00:00	2017-03-14 05:00:00	2017-03-14 05:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1279	5	2017-03-14 02:45:00	2017-03-14 02:45:00	2017-03-14 02:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1280	5	2017-03-14 10:00:00	2017-03-14 10:00:00	2017-03-14 10:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1281	5	2017-03-14 09:00:00	2017-03-14 09:00:00	2017-03-14 09:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1282	5	2017-03-14 11:30:00	2017-03-14 11:30:00	2017-03-14 11:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1283	5	2017-03-14 14:15:00	2017-03-14 14:15:00	2017-03-14 14:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1284	5	2017-03-14 17:30:00	2017-03-14 17:30:00	2017-03-14 17:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1285	5	2017-03-14 19:30:00	2017-03-14 19:30:00	2017-03-14 19:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1286	5	2017-03-14 21:30:00	2017-03-14 21:30:00	2017-03-14 21:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1287	5	2017-03-14 23:15:00	2017-03-14 23:15:00	2017-03-14 23:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1288	5	2017-03-15 04:30:00	2017-03-15 04:30:00	2017-03-15 04:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1289	5	2017-03-15 06:00:00	2017-03-15 06:00:00	2017-03-15 06:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1290	5	2017-03-15 07:45:00	2017-03-15 07:45:00	2017-03-15 07:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1291	5	2017-03-15 10:30:00	2017-03-15 10:30:00	2017-03-15 10:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1292	5	2017-03-15 14:45:00	2017-03-15 14:45:00	2017-03-15 14:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1293	5	2017-03-15 16:15:00	2017-03-15 16:15:00	2017-03-15 16:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1294	5	2017-03-15 17:46:00	2017-03-15 17:46:00	2017-03-15 17:46:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1295	5	2017-03-15 21:30:00	2017-03-15 21:30:00	2017-03-15 21:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1296	5	2017-03-16 00:30:00	2017-03-16 00:30:00	2017-03-16 00:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1297	5	2017-03-16 00:45:00	2017-03-16 00:45:00	2017-03-16 00:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1298	5	2017-03-16 03:15:00	2017-03-16 03:15:00	2017-03-16 03:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1299	5	2017-03-16 07:45:00	2017-03-16 07:45:00	2017-03-16 07:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1300	5	2017-03-16 12:00:00	2017-03-16 12:00:00	2017-03-16 12:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1301	5	2017-03-16 11:30:00	2017-03-16 11:30:00	2017-03-16 11:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1302	5	2017-03-16 15:00:00	2017-03-16 15:00:00	2017-03-16 15:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1303	5	2017-03-16 18:00:00	2017-03-16 18:00:00	2017-03-16 18:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1304	5	2017-03-16 19:30:00	2017-03-16 19:30:00	2017-03-16 19:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1305	5	2017-03-16 23:15:00	2017-03-16 23:15:00	2017-03-16 23:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1306	5	2017-03-17 02:00:00	2017-03-17 02:00:00	2017-03-17 02:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1307	5	2017-03-17 03:45:00	2017-03-17 03:45:00	2017-03-17 03:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1308	5	2017-03-17 04:30:00	2017-03-17 04:30:00	2017-03-17 04:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1309	5	2017-03-17 08:45:00	2017-03-17 08:45:00	2017-03-17 08:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1310	5	2017-03-17 13:30:00	2017-03-17 13:30:00	2017-03-17 13:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1311	5	2017-03-17 12:00:00	2017-03-17 12:00:00	2017-03-17 12:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1312	5	2017-03-17 15:15:00	2017-03-17 15:15:00	2017-03-17 15:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1313	5	2017-03-17 19:30:00	2017-03-17 19:30:00	2017-03-17 19:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1314	5	2017-03-17 22:30:00	2017-03-17 22:30:00	2017-03-17 22:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1315	5	2017-03-17 23:15:00	2017-03-17 23:15:00	2017-03-17 23:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1316	5	2017-03-18 01:15:00	2017-03-18 01:15:00	2017-03-18 01:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1317	5	2017-03-18 03:15:00	2017-03-18 03:15:00	2017-03-18 03:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1318	5	2017-03-18 09:15:00	2017-03-18 09:15:00	2017-03-18 09:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1319	5	2017-03-18 10:00:00	2017-03-18 10:00:00	2017-03-18 10:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1320	5	2017-03-18 11:00:00	2017-03-18 11:00:00	2017-03-18 11:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1321	5	2017-03-18 13:00:00	2017-03-18 13:00:00	2017-03-18 13:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1322	5	2017-03-18 16:00:00	2017-03-18 16:00:00	2017-03-18 16:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1323	5	2017-03-18 17:45:00	2017-03-18 17:45:00	2017-03-18 17:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1324	5	2017-03-18 20:45:00	2017-03-18 20:45:00	2017-03-18 20:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1325	5	2017-03-19 00:30:00	2017-03-19 00:30:00	2017-03-19 00:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1326	5	2017-03-19 04:15:00	2017-03-19 04:15:00	2017-03-19 04:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1327	5	2017-03-19 05:45:00	2017-03-19 05:45:00	2017-03-19 05:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1328	5	2017-03-19 10:45:00	2017-03-19 10:45:00	2017-03-19 10:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1329	5	2017-03-19 11:15:00	2017-03-19 11:15:00	2017-03-19 11:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1330	5	2017-03-19 12:45:00	2017-03-19 12:45:00	2017-03-19 12:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1331	5	2017-03-19 13:45:00	2017-03-19 13:45:00	2017-03-19 13:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1332	5	2017-03-19 18:30:00	2017-03-19 18:30:00	2017-03-19 18:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1333	5	2017-03-19 19:30:00	2017-03-19 19:30:00	2017-03-19 19:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1334	5	2017-03-20 01:15:00	2017-03-20 01:15:00	2017-03-20 01:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1335	5	2017-03-20 01:45:00	2017-03-20 01:45:00	2017-03-20 01:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1336	5	2017-03-20 04:00:00	2017-03-20 04:00:00	2017-03-20 04:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1337	5	2017-03-20 05:30:00	2017-03-20 05:30:00	2017-03-20 05:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1338	5	2017-03-20 11:00:00	2017-03-20 11:00:00	2017-03-20 11:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1339	5	2017-03-20 10:15:00	2017-03-20 10:15:00	2017-03-20 10:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1340	5	2017-03-20 15:30:00	2017-03-20 15:30:00	2017-03-20 15:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1341	5	2017-03-20 15:15:00	2017-03-20 15:15:00	2017-03-20 15:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1342	5	2017-03-20 18:30:00	2017-03-20 18:30:00	2017-03-20 18:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1343	5	2017-03-20 19:30:00	2017-03-20 19:30:00	2017-03-20 19:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1344	5	2017-03-21 00:00:00	2017-03-21 00:00:00	2017-03-21 00:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1345	5	2017-03-21 00:15:00	2017-03-21 00:15:00	2017-03-21 00:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1346	5	2017-03-21 04:30:00	2017-03-21 04:30:00	2017-03-21 04:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1347	5	2017-03-21 08:30:00	2017-03-21 08:30:00	2017-03-21 08:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1348	5	2017-03-21 10:30:00	2017-03-21 10:30:00	2017-03-21 10:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1349	5	2017-03-21 12:45:00	2017-03-21 12:45:00	2017-03-21 12:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1350	5	2017-03-21 17:00:00	2017-03-21 17:00:00	2017-03-21 17:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1351	5	2017-03-21 15:45:00	2017-03-21 15:45:00	2017-03-21 15:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1352	5	2017-03-21 19:15:00	2017-03-21 19:15:00	2017-03-21 19:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1353	5	2017-03-21 23:15:00	2017-03-21 23:15:00	2017-03-21 23:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1354	5	2017-03-22 02:15:00	2017-03-22 02:15:00	2017-03-22 02:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1355	5	2017-03-22 02:30:00	2017-03-22 02:30:00	2017-03-22 02:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1356	5	2017-03-22 07:30:00	2017-03-22 07:30:00	2017-03-22 07:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1357	5	2017-03-22 06:15:00	2017-03-22 06:15:00	2017-03-22 06:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1358	5	2017-03-22 09:45:00	2017-03-22 09:45:00	2017-03-22 09:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1359	5	2017-03-22 11:30:00	2017-03-22 11:30:00	2017-03-22 11:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1360	5	2017-03-22 14:45:00	2017-03-22 14:45:00	2017-03-22 14:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1361	5	2017-03-22 15:30:00	2017-03-22 15:30:00	2017-03-22 15:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1362	5	2017-03-22 19:00:00	2017-03-22 19:00:00	2017-03-22 19:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1363	5	2017-03-23 00:30:00	2017-03-23 00:30:00	2017-03-23 00:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1364	5	2017-03-23 02:30:00	2017-03-23 02:30:00	2017-03-23 02:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1365	5	2017-03-23 06:00:00	2017-03-23 06:00:00	2017-03-23 06:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1366	5	2017-03-23 09:15:00	2017-03-23 09:15:00	2017-03-23 09:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1367	5	2017-03-23 07:00:00	2017-03-23 07:00:00	2017-03-23 07:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1368	5	2017-03-23 11:00:00	2017-03-23 11:00:00	2017-03-23 11:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1369	5	2017-03-23 15:45:00	2017-03-23 15:45:00	2017-03-23 15:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1370	5	2017-03-23 18:45:00	2017-03-23 18:45:00	2017-03-23 18:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1371	5	2017-03-23 18:15:00	2017-03-23 18:15:00	2017-03-23 18:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1372	5	2017-03-23 21:15:00	2017-03-23 21:15:00	2017-03-23 21:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1373	5	2017-03-24 00:30:00	2017-03-24 00:30:00	2017-03-24 00:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1374	5	2017-03-24 02:45:00	2017-03-24 02:45:00	2017-03-24 02:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1375	5	2017-03-24 03:00:00	2017-03-24 03:00:00	2017-03-24 03:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1376	5	2017-03-24 08:30:00	2017-03-24 08:30:00	2017-03-24 08:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1377	5	2017-03-24 09:45:00	2017-03-24 09:45:00	2017-03-24 09:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1378	5	2017-03-24 14:15:00	2017-03-24 14:15:00	2017-03-24 14:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1379	5	2017-03-24 14:00:00	2017-03-24 14:00:00	2017-03-24 14:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1380	5	2017-03-24 17:30:00	2017-03-24 17:30:00	2017-03-24 17:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1381	5	2017-03-24 20:15:00	2017-03-24 20:15:00	2017-03-24 20:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1382	5	2017-03-24 21:45:00	2017-03-24 21:45:00	2017-03-24 21:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1383	5	2017-03-25 00:00:00	2017-03-25 00:00:00	2017-03-25 00:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1384	5	2017-03-25 02:45:00	2017-03-25 02:45:00	2017-03-25 02:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1385	5	2017-03-25 07:15:00	2017-03-25 07:15:00	2017-03-25 07:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1386	5	2017-03-25 08:00:00	2017-03-25 08:00:00	2017-03-25 08:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1387	5	2017-03-25 09:15:00	2017-03-25 09:15:00	2017-03-25 09:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1388	5	2017-03-25 15:30:00	2017-03-25 15:30:00	2017-03-25 15:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1389	5	2017-03-25 17:45:00	2017-03-25 17:45:00	2017-03-25 17:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1390	5	2017-03-25 18:15:00	2017-03-25 18:15:00	2017-03-25 18:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1391	5	2017-03-25 23:00:00	2017-03-25 23:00:00	2017-03-25 23:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1392	5	2017-03-25 23:15:00	2017-03-25 23:15:00	2017-03-25 23:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1393	5	2017-03-26 00:30:00	2017-03-26 00:30:00	2017-03-26 00:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1394	5	2017-03-26 03:30:00	2017-03-26 03:30:00	2017-03-26 03:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1395	5	2017-03-26 06:45:00	2017-03-26 06:45:00	2017-03-26 06:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1396	5	2017-03-26 09:00:00	2017-03-26 09:00:00	2017-03-26 09:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1397	5	2017-03-26 13:15:00	2017-03-26 13:15:00	2017-03-26 13:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1398	5	2017-03-26 13:45:00	2017-03-26 13:45:00	2017-03-26 13:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1399	5	2017-03-26 16:45:00	2017-03-26 16:45:00	2017-03-26 16:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1400	5	2017-03-26 21:00:00	2017-03-26 21:00:00	2017-03-26 21:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1401	5	2017-03-26 22:00:00	2017-03-26 22:00:00	2017-03-26 22:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1402	5	2017-03-27 00:15:00	2017-03-27 00:15:00	2017-03-27 00:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1403	5	2017-03-27 02:30:00	2017-03-27 02:30:00	2017-03-27 02:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1404	5	2017-03-27 06:30:00	2017-03-27 06:30:00	2017-03-27 06:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1405	5	2017-03-27 08:45:00	2017-03-27 08:45:00	2017-03-27 08:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1406	5	2017-03-27 10:30:00	2017-03-27 10:30:00	2017-03-27 10:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1407	5	2017-03-27 14:30:00	2017-03-27 14:30:00	2017-03-27 14:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1408	5	2017-03-27 15:30:00	2017-03-27 15:30:00	2017-03-27 15:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1409	5	2017-03-27 17:30:00	2017-03-27 17:30:00	2017-03-27 17:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1410	5	2017-03-27 20:00:00	2017-03-27 20:00:00	2017-03-27 20:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1411	5	2017-03-27 22:15:00	2017-03-27 22:15:00	2017-03-27 22:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1412	5	2017-03-28 03:45:00	2017-03-28 03:45:00	2017-03-28 03:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1413	5	2017-03-28 04:00:00	2017-03-28 04:00:00	2017-03-28 04:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1414	5	2017-03-28 06:45:00	2017-03-28 06:45:00	2017-03-28 06:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1415	5	2017-03-28 10:00:00	2017-03-28 10:00:00	2017-03-28 10:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1416	5	2017-03-28 13:30:00	2017-03-28 13:30:00	2017-03-28 13:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1417	5	2017-03-28 12:00:00	2017-03-28 12:00:00	2017-03-28 12:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1418	5	2017-03-28 17:15:00	2017-03-28 17:15:00	2017-03-28 17:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1419	5	2017-03-28 17:00:00	2017-03-28 17:00:00	2017-03-28 17:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1420	5	2017-03-28 21:45:00	2017-03-28 21:45:00	2017-03-28 21:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1421	5	2017-03-29 07:00:00	2017-03-29 07:00:00	2017-03-29 07:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1422	5	2017-03-29 01:45:00	2017-03-29 01:45:00	2017-03-29 01:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1423	5	2017-03-29 08:15:00	2017-03-29 08:15:00	2017-03-29 08:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1424	5	2017-03-29 03:00:00	2017-03-29 03:00:00	2017-03-29 03:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1425	5	2017-03-29 17:00:00	2017-03-29 17:00:00	2017-03-29 17:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1426	5	2017-03-29 11:45:00	2017-03-29 11:45:00	2017-03-29 11:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1427	5	2017-03-29 18:15:00	2017-03-29 18:15:00	2017-03-29 18:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1428	5	2017-03-29 13:00:00	2017-03-29 13:00:00	2017-03-29 13:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1429	5	2017-03-30 03:00:00	2017-03-30 03:00:00	2017-03-30 03:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1430	5	2017-03-29 21:45:00	2017-03-29 21:45:00	2017-03-29 21:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1431	5	2017-03-30 04:15:00	2017-03-30 04:15:00	2017-03-30 04:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1432	5	2017-03-29 23:00:00	2017-03-29 23:00:00	2017-03-29 23:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1433	5	2017-03-31 09:30:00	2017-03-31 09:30:00	2017-03-31 09:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1434	5	2017-03-30 05:15:00	2017-03-30 05:15:00	2017-03-30 05:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1435	5	2017-03-31 13:15:00	2017-03-31 13:15:00	2017-03-31 13:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1436	5	2017-03-31 10:30:00	2017-03-31 10:30:00	2017-03-31 10:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1437	5	2017-03-31 15:15:00	2017-03-31 15:15:00	2017-03-31 15:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1438	5	2017-03-31 19:15:00	2017-03-31 19:15:00	2017-03-31 19:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1439	5	2017-03-31 14:45:00	2017-03-31 14:45:00	2017-03-31 14:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1440	5	2017-03-31 21:31:00	2017-03-31 21:31:00	2017-03-31 21:31:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1441	6	2017-03-01 00:15:00	2017-03-01 00:15:00	2017-03-01 00:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1442	6	2017-03-01 04:30:00	2017-03-01 04:30:00	2017-03-01 04:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1443	6	2017-03-01 05:15:00	2017-03-01 05:15:00	2017-03-01 05:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1444	6	2017-03-01 09:30:00	2017-03-01 09:30:00	2017-03-01 09:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1445	6	2017-03-01 10:30:00	2017-03-01 10:30:00	2017-03-01 10:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1446	6	2017-03-01 12:00:00	2017-03-01 12:00:00	2017-03-01 12:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1447	6	2017-03-01 16:15:00	2017-03-01 16:15:00	2017-03-01 16:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1448	6	2017-03-01 19:15:00	2017-03-01 19:15:00	2017-03-01 19:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1449	6	2017-03-01 21:45:00	2017-03-01 21:45:00	2017-03-01 21:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1450	6	2017-03-02 00:00:00	2017-03-02 00:00:00	2017-03-02 00:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1451	6	2017-03-02 05:15:00	2017-03-02 05:15:00	2017-03-02 05:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1452	6	2017-03-02 05:00:00	2017-03-02 05:00:00	2017-03-02 05:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1453	6	2017-03-02 06:30:00	2017-03-02 06:30:00	2017-03-02 06:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1454	6	2017-03-02 10:00:00	2017-03-02 10:00:00	2017-03-02 10:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1455	6	2017-03-02 11:30:00	2017-03-02 11:30:00	2017-03-02 11:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1456	6	2017-03-02 15:00:00	2017-03-02 15:00:00	2017-03-02 15:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1457	6	2017-03-02 16:30:00	2017-03-02 16:30:00	2017-03-02 16:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1458	6	2017-03-02 21:45:00	2017-03-02 21:45:00	2017-03-02 21:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1459	6	2017-03-02 21:30:00	2017-03-02 21:30:00	2017-03-02 21:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1460	6	2017-03-03 00:15:00	2017-03-03 00:15:00	2017-03-03 00:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1461	6	2017-03-03 03:45:00	2017-03-03 03:45:00	2017-03-03 03:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1462	6	2017-03-03 07:15:00	2017-03-03 07:15:00	2017-03-03 07:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1463	6	2017-03-03 07:30:00	2017-03-03 07:30:00	2017-03-03 07:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1464	6	2017-03-03 09:15:00	2017-03-03 09:15:00	2017-03-03 09:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1465	6	2017-03-03 14:16:00	2017-03-03 14:16:00	2017-03-03 14:16:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1466	6	2017-03-03 17:01:00	2017-03-03 17:01:00	2017-03-03 17:01:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1467	6	2017-03-03 20:00:00	2017-03-03 20:00:00	2017-03-03 20:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1468	6	2017-03-03 20:45:00	2017-03-03 20:45:00	2017-03-03 20:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1469	6	2017-03-03 23:46:00	2017-03-03 23:46:00	2017-03-03 23:46:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1470	6	2017-03-04 03:01:00	2017-03-04 03:01:00	2017-03-04 03:01:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1471	6	2017-03-04 06:01:00	2017-03-04 06:01:00	2017-03-04 06:01:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1472	6	2017-03-04 06:46:00	2017-03-04 06:46:00	2017-03-04 06:46:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1473	6	2017-03-04 09:46:00	2017-03-04 09:46:00	2017-03-04 09:46:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1474	6	2017-03-04 13:01:00	2017-03-04 13:01:00	2017-03-04 13:01:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1475	6	2017-03-04 16:00:00	2017-03-04 16:00:00	2017-03-04 16:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1476	6	2017-03-04 16:45:00	2017-03-04 16:45:00	2017-03-04 16:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1477	6	2017-03-04 19:45:00	2017-03-04 19:45:00	2017-03-04 19:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1478	6	2017-03-04 23:00:00	2017-03-04 23:00:00	2017-03-04 23:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1479	6	2017-03-05 02:01:00	2017-03-05 02:01:00	2017-03-05 02:01:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1480	6	2017-03-05 02:46:00	2017-03-05 02:46:00	2017-03-05 02:46:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1481	6	2017-03-05 05:46:00	2017-03-05 05:46:00	2017-03-05 05:46:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1482	6	2017-03-05 09:00:00	2017-03-05 09:00:00	2017-03-05 09:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1483	6	2017-03-05 12:00:00	2017-03-05 12:00:00	2017-03-05 12:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1484	6	2017-03-05 13:45:00	2017-03-05 13:45:00	2017-03-05 13:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1485	6	2017-03-05 16:00:00	2017-03-05 16:00:00	2017-03-05 16:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1486	6	2017-03-05 18:30:00	2017-03-05 18:30:00	2017-03-05 18:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1487	6	2017-03-05 21:15:00	2017-03-05 21:15:00	2017-03-05 21:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1488	6	2017-03-05 23:45:00	2017-03-05 23:45:00	2017-03-05 23:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1489	6	2017-03-06 02:15:00	2017-03-06 02:15:00	2017-03-06 02:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1490	6	2017-03-06 04:45:00	2017-03-06 04:45:00	2017-03-06 04:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1491	6	2017-03-06 07:15:00	2017-03-06 07:15:00	2017-03-06 07:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1492	6	2017-03-06 09:45:00	2017-03-06 09:45:00	2017-03-06 09:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1493	6	2017-03-06 12:15:00	2017-03-06 12:15:00	2017-03-06 12:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1494	6	2017-03-06 14:45:00	2017-03-06 14:45:00	2017-03-06 14:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1495	6	2017-03-06 17:30:00	2017-03-06 17:30:00	2017-03-06 17:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1496	6	2017-03-06 19:14:00	2017-03-06 19:14:00	2017-03-06 19:14:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1497	6	2017-03-06 21:45:00	2017-03-06 21:45:00	2017-03-06 21:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1498	6	2017-03-07 01:15:00	2017-03-07 01:15:00	2017-03-07 01:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1499	6	2017-03-07 03:00:00	2017-03-07 03:00:00	2017-03-07 03:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1500	6	2017-03-07 05:45:00	2017-03-07 05:45:00	2017-03-07 05:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1501	6	2017-03-07 08:45:00	2017-03-07 08:45:00	2017-03-07 08:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1502	6	2017-03-07 09:45:00	2017-03-07 09:45:00	2017-03-07 09:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1503	6	2017-03-07 17:15:00	2017-03-07 17:15:00	2017-03-07 17:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1504	6	2017-03-07 17:00:00	2017-03-07 17:00:00	2017-03-07 17:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1505	6	2017-03-07 19:45:00	2017-03-07 19:45:00	2017-03-07 19:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1506	6	2017-03-07 20:15:00	2017-03-07 20:15:00	2017-03-07 20:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1507	6	2017-03-08 01:30:00	2017-03-08 01:30:00	2017-03-08 01:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1508	6	2017-03-08 01:15:00	2017-03-08 01:15:00	2017-03-08 01:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1509	6	2017-03-08 05:15:00	2017-03-08 05:15:00	2017-03-08 05:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1510	6	2017-03-08 06:15:00	2017-03-08 06:15:00	2017-03-08 06:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1511	6	2017-03-08 13:30:00	2017-03-08 13:30:00	2017-03-08 13:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1512	6	2017-03-08 09:15:00	2017-03-08 09:15:00	2017-03-08 09:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1513	6	2017-03-08 15:00:00	2017-03-08 15:00:00	2017-03-08 15:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1514	6	2017-03-08 16:45:00	2017-03-08 16:45:00	2017-03-08 16:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1515	6	2017-03-08 18:15:00	2017-03-08 18:15:00	2017-03-08 18:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1516	6	2017-03-09 01:15:00	2017-03-09 01:15:00	2017-03-09 01:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1517	6	2017-03-09 00:30:00	2017-03-09 00:30:00	2017-03-09 00:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1518	6	2017-03-09 04:15:00	2017-03-09 04:15:00	2017-03-09 04:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1519	6	2017-03-09 05:00:00	2017-03-09 05:00:00	2017-03-09 05:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1520	6	2017-03-09 11:15:00	2017-03-09 11:15:00	2017-03-09 11:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1521	6	2017-03-09 08:30:00	2017-03-09 08:30:00	2017-03-09 08:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1522	6	2017-03-09 15:00:00	2017-03-09 15:00:00	2017-03-09 15:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1523	6	2017-03-09 13:00:00	2017-03-09 13:00:00	2017-03-09 13:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1524	6	2017-03-09 21:00:00	2017-03-09 21:00:00	2017-03-09 21:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1525	6	2017-03-09 19:15:00	2017-03-09 19:15:00	2017-03-09 19:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1526	6	2017-03-09 23:15:00	2017-03-09 23:15:00	2017-03-09 23:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1527	6	2017-03-09 23:30:00	2017-03-09 23:30:00	2017-03-09 23:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1528	6	2017-03-10 06:45:00	2017-03-10 06:45:00	2017-03-10 06:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1529	6	2017-03-10 05:15:00	2017-03-10 05:15:00	2017-03-10 05:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1530	6	2017-03-10 11:45:00	2017-03-10 11:45:00	2017-03-10 11:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1531	6	2017-03-10 10:15:00	2017-03-10 10:15:00	2017-03-10 10:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1532	6	2017-03-10 15:15:00	2017-03-10 15:15:00	2017-03-10 15:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1533	6	2017-03-10 16:00:00	2017-03-10 16:00:00	2017-03-10 16:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1534	6	2017-03-10 21:45:00	2017-03-10 21:45:00	2017-03-10 21:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1535	6	2017-03-10 19:30:00	2017-03-10 19:30:00	2017-03-10 19:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1536	6	2017-03-11 01:30:00	2017-03-11 01:30:00	2017-03-11 01:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1537	6	2017-03-11 03:30:00	2017-03-11 03:30:00	2017-03-11 03:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1538	6	2017-03-11 05:30:00	2017-03-11 05:30:00	2017-03-11 05:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1539	6	2017-03-11 07:30:00	2017-03-11 07:30:00	2017-03-11 07:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1540	6	2017-03-11 10:45:00	2017-03-11 10:45:00	2017-03-11 10:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1541	6	2017-03-11 13:00:00	2017-03-11 13:00:00	2017-03-11 13:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1542	6	2017-03-11 16:15:00	2017-03-11 16:15:00	2017-03-11 16:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1543	6	2017-03-11 16:45:00	2017-03-11 16:45:00	2017-03-11 16:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1544	6	2017-03-11 21:00:00	2017-03-11 21:00:00	2017-03-11 21:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1545	6	2017-03-11 22:30:00	2017-03-11 22:30:00	2017-03-11 22:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1546	6	2017-03-12 01:00:00	2017-03-12 01:00:00	2017-03-12 01:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1547	6	2017-03-12 02:00:00	2017-03-12 02:00:00	2017-03-12 02:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1548	6	2017-03-12 08:00:00	2017-03-12 08:00:00	2017-03-12 08:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1549	6	2017-03-12 08:45:00	2017-03-12 08:45:00	2017-03-12 08:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1550	6	2017-03-12 12:45:00	2017-03-12 12:45:00	2017-03-12 12:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1551	6	2017-03-12 12:15:00	2017-03-12 12:15:00	2017-03-12 12:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1552	6	2017-03-12 19:00:00	2017-03-12 19:00:00	2017-03-12 19:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1553	6	2017-03-12 16:30:00	2017-03-12 16:30:00	2017-03-12 16:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1554	6	2017-03-12 20:30:00	2017-03-12 20:30:00	2017-03-12 20:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1555	6	2017-03-12 21:00:00	2017-03-12 21:00:00	2017-03-12 21:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1556	6	2017-03-13 02:00:00	2017-03-13 02:00:00	2017-03-13 02:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1557	6	2017-03-13 05:00:00	2017-03-13 05:00:00	2017-03-13 05:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1558	6	2017-03-13 09:00:00	2017-03-13 09:00:00	2017-03-13 09:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1559	6	2017-03-13 09:45:00	2017-03-13 09:45:00	2017-03-13 09:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1560	6	2017-03-13 14:15:00	2017-03-13 14:15:00	2017-03-13 14:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1561	6	2017-03-13 15:45:00	2017-03-13 15:45:00	2017-03-13 15:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1562	6	2017-03-13 19:30:00	2017-03-13 19:30:00	2017-03-13 19:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1563	6	2017-03-13 18:30:00	2017-03-13 18:30:00	2017-03-13 18:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1564	6	2017-03-14 00:30:00	2017-03-14 00:30:00	2017-03-14 00:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1565	6	2017-03-13 22:30:00	2017-03-13 22:30:00	2017-03-13 22:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1566	6	2017-03-14 01:45:00	2017-03-14 01:45:00	2017-03-14 01:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1567	6	2017-03-14 03:45:00	2017-03-14 03:45:00	2017-03-14 03:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1568	6	2017-03-14 08:30:00	2017-03-14 08:30:00	2017-03-14 08:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1569	6	2017-03-14 08:15:00	2017-03-14 08:15:00	2017-03-14 08:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1570	6	2017-03-14 12:00:00	2017-03-14 12:00:00	2017-03-14 12:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1571	6	2017-03-14 13:30:00	2017-03-14 13:30:00	2017-03-14 13:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1572	6	2017-03-14 16:30:00	2017-03-14 16:30:00	2017-03-14 16:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1573	6	2017-03-14 19:15:00	2017-03-14 19:15:00	2017-03-14 19:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1574	6	2017-03-15 00:30:00	2017-03-15 00:30:00	2017-03-15 00:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1575	6	2017-03-15 02:15:00	2017-03-15 02:15:00	2017-03-15 02:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1576	6	2017-03-15 05:15:00	2017-03-15 05:15:00	2017-03-15 05:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1577	6	2017-03-15 04:30:00	2017-03-15 04:30:00	2017-03-15 04:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1578	6	2017-03-15 09:45:00	2017-03-15 09:45:00	2017-03-15 09:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1579	6	2017-03-15 11:30:00	2017-03-15 11:30:00	2017-03-15 11:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1580	6	2017-03-15 14:15:00	2017-03-15 14:15:00	2017-03-15 14:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1581	6	2017-03-15 15:30:00	2017-03-15 15:30:00	2017-03-15 15:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1582	6	2017-03-15 19:15:00	2017-03-15 19:15:00	2017-03-15 19:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1583	6	2017-03-15 19:45:00	2017-03-15 19:45:00	2017-03-15 19:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1584	6	2017-03-16 01:00:00	2017-03-16 01:00:00	2017-03-16 01:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1585	6	2017-03-16 02:00:00	2017-03-16 02:00:00	2017-03-16 02:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1586	6	2017-03-16 03:45:00	2017-03-16 03:45:00	2017-03-16 03:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1587	6	2017-03-16 09:15:00	2017-03-16 09:15:00	2017-03-16 09:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1588	6	2017-03-16 08:30:00	2017-03-16 08:30:00	2017-03-16 08:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1589	6	2017-03-16 13:30:00	2017-03-16 13:30:00	2017-03-16 13:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1590	6	2017-03-16 13:45:00	2017-03-16 13:45:00	2017-03-16 13:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1591	6	2017-03-16 17:45:00	2017-03-16 17:45:00	2017-03-16 17:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1592	6	2017-03-16 21:30:00	2017-03-16 21:30:00	2017-03-16 21:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1593	6	2017-03-16 21:15:00	2017-03-16 21:15:00	2017-03-16 21:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1594	6	2017-03-17 01:30:00	2017-03-17 01:30:00	2017-03-17 01:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1595	6	2017-03-17 04:15:00	2017-03-17 04:15:00	2017-03-17 04:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1596	6	2017-03-17 06:00:00	2017-03-17 06:00:00	2017-03-17 06:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1597	6	2017-03-17 07:00:00	2017-03-17 07:00:00	2017-03-17 07:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1598	6	2017-03-17 12:15:00	2017-03-17 12:15:00	2017-03-17 12:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1599	6	2017-03-17 13:00:00	2017-03-17 13:00:00	2017-03-17 13:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1600	6	2017-03-17 14:45:00	2017-03-17 14:45:00	2017-03-17 14:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1601	6	2017-03-17 18:15:00	2017-03-17 18:15:00	2017-03-17 18:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1602	6	2017-03-17 20:00:00	2017-03-17 20:00:00	2017-03-17 20:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1603	6	2017-03-17 23:30:00	2017-03-17 23:30:00	2017-03-17 23:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1604	6	2017-03-18 04:00:00	2017-03-18 04:00:00	2017-03-18 04:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1605	6	2017-03-18 05:45:00	2017-03-18 05:45:00	2017-03-18 05:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1606	6	2017-03-18 09:45:00	2017-03-18 09:45:00	2017-03-18 09:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1607	6	2017-03-18 10:30:00	2017-03-18 10:30:00	2017-03-18 10:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1608	6	2017-03-18 13:30:00	2017-03-18 13:30:00	2017-03-18 13:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1609	6	2017-03-18 14:45:00	2017-03-18 14:45:00	2017-03-18 14:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1610	6	2017-03-18 15:45:00	2017-03-18 15:45:00	2017-03-18 15:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1611	6	2017-03-18 18:00:00	2017-03-18 18:00:00	2017-03-18 18:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1612	6	2017-03-18 21:00:00	2017-03-18 21:00:00	2017-03-18 21:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1613	6	2017-03-19 01:45:00	2017-03-19 01:45:00	2017-03-19 01:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1614	6	2017-03-19 04:45:00	2017-03-19 04:45:00	2017-03-19 04:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1615	6	2017-03-19 03:00:00	2017-03-19 03:00:00	2017-03-19 03:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1616	6	2017-03-19 08:30:00	2017-03-19 08:30:00	2017-03-19 08:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1617	6	2017-03-19 10:15:00	2017-03-19 10:15:00	2017-03-19 10:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1618	6	2017-03-19 14:30:00	2017-03-19 14:30:00	2017-03-19 14:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1619	6	2017-03-19 12:45:00	2017-03-19 12:45:00	2017-03-19 12:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1620	6	2017-03-19 20:15:00	2017-03-19 20:15:00	2017-03-19 20:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1621	6	2017-03-19 19:00:00	2017-03-19 19:00:00	2017-03-19 19:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1622	6	2017-03-19 23:15:00	2017-03-19 23:15:00	2017-03-19 23:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1623	6	2017-03-20 01:45:00	2017-03-20 01:45:00	2017-03-20 01:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1624	6	2017-03-20 06:00:00	2017-03-20 06:00:00	2017-03-20 06:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1625	6	2017-03-20 05:30:00	2017-03-20 05:30:00	2017-03-20 05:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1626	6	2017-03-20 07:30:00	2017-03-20 07:30:00	2017-03-20 07:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1627	6	2017-03-20 11:45:00	2017-03-20 11:45:00	2017-03-20 11:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1628	6	2017-03-20 14:00:00	2017-03-20 14:00:00	2017-03-20 14:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1629	6	2017-03-20 15:00:00	2017-03-20 15:00:00	2017-03-20 15:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1630	6	2017-03-20 21:00:00	2017-03-20 21:00:00	2017-03-20 21:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1631	6	2017-03-20 21:15:00	2017-03-20 21:15:00	2017-03-20 21:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1632	6	2017-03-21 01:30:00	2017-03-21 01:30:00	2017-03-21 01:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1633	6	2017-03-21 04:00:00	2017-03-21 04:00:00	2017-03-21 04:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1634	6	2017-03-21 03:45:00	2017-03-21 03:45:00	2017-03-21 03:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1635	6	2017-03-21 05:15:00	2017-03-21 05:15:00	2017-03-21 05:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1636	6	2017-03-21 11:45:00	2017-03-21 11:45:00	2017-03-21 11:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1637	6	2017-03-21 13:15:00	2017-03-21 13:15:00	2017-03-21 13:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1638	6	2017-03-21 14:15:00	2017-03-21 14:15:00	2017-03-21 14:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1639	6	2017-03-21 16:00:00	2017-03-21 16:00:00	2017-03-21 16:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1640	6	2017-03-21 19:15:00	2017-03-21 19:15:00	2017-03-21 19:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1641	6	2017-03-21 22:00:00	2017-03-21 22:00:00	2017-03-21 22:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1642	6	2017-03-22 01:45:00	2017-03-22 01:45:00	2017-03-22 01:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1643	6	2017-03-22 02:45:00	2017-03-22 02:45:00	2017-03-22 02:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1644	6	2017-03-22 07:15:00	2017-03-22 07:15:00	2017-03-22 07:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1645	6	2017-03-22 06:30:00	2017-03-22 06:30:00	2017-03-22 06:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1646	6	2017-03-22 10:15:00	2017-03-22 10:15:00	2017-03-22 10:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1647	6	2017-03-22 12:45:00	2017-03-22 12:45:00	2017-03-22 12:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1648	6	2017-03-22 14:15:00	2017-03-22 14:15:00	2017-03-22 14:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1649	6	2017-03-22 16:15:00	2017-03-22 16:15:00	2017-03-22 16:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1650	6	2017-03-22 23:15:00	2017-03-22 23:15:00	2017-03-22 23:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1651	6	2017-03-22 21:30:00	2017-03-22 21:30:00	2017-03-22 21:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1652	6	2017-03-23 04:15:00	2017-03-23 04:15:00	2017-03-23 04:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1653	6	2017-03-23 04:45:00	2017-03-23 04:45:00	2017-03-23 04:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1654	6	2017-03-23 05:45:00	2017-03-23 05:45:00	2017-03-23 05:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1655	6	2017-03-23 10:45:00	2017-03-23 10:45:00	2017-03-23 10:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1656	6	2017-03-23 11:45:00	2017-03-23 11:45:00	2017-03-23 11:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1657	6	2017-03-23 15:00:00	2017-03-23 15:00:00	2017-03-23 15:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1658	6	2017-03-23 15:15:00	2017-03-23 15:15:00	2017-03-23 15:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1659	6	2017-03-23 17:30:00	2017-03-23 17:30:00	2017-03-23 17:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1660	6	2017-03-23 21:30:00	2017-03-23 21:30:00	2017-03-23 21:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1661	6	2017-03-23 23:30:00	2017-03-23 23:30:00	2017-03-23 23:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1662	6	2017-03-24 03:15:00	2017-03-24 03:15:00	2017-03-24 03:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1663	6	2017-03-24 01:45:00	2017-03-24 01:45:00	2017-03-24 01:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1664	6	2017-03-24 05:45:00	2017-03-24 05:45:00	2017-03-24 05:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1665	6	2017-03-24 09:15:00	2017-03-24 09:15:00	2017-03-24 09:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1666	6	2017-03-24 13:15:00	2017-03-24 13:15:00	2017-03-24 13:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1667	6	2017-03-24 14:15:00	2017-03-24 14:15:00	2017-03-24 14:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1668	6	2017-03-24 19:45:00	2017-03-24 19:45:00	2017-03-24 19:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1669	6	2017-03-24 19:15:00	2017-03-24 19:15:00	2017-03-24 19:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1670	6	2017-03-25 01:00:00	2017-03-25 01:00:00	2017-03-25 01:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1671	6	2017-03-25 00:00:00	2017-03-25 00:00:00	2017-03-25 00:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1672	6	2017-03-25 02:30:00	2017-03-25 02:30:00	2017-03-25 02:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1673	6	2017-03-25 03:30:00	2017-03-25 03:30:00	2017-03-25 03:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1674	6	2017-03-25 10:45:00	2017-03-25 10:45:00	2017-03-25 10:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1675	6	2017-03-25 09:30:00	2017-03-25 09:30:00	2017-03-25 09:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1676	6	2017-03-25 14:45:00	2017-03-25 14:45:00	2017-03-25 14:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1677	6	2017-03-25 18:15:00	2017-03-25 18:15:00	2017-03-25 18:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1678	6	2017-03-25 19:15:00	2017-03-25 19:15:00	2017-03-25 19:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1679	6	2017-03-25 19:30:00	2017-03-25 19:30:00	2017-03-25 19:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1680	6	2017-03-25 23:30:00	2017-03-25 23:30:00	2017-03-25 23:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1681	6	2017-03-26 00:45:00	2017-03-26 00:45:00	2017-03-26 00:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1682	6	2017-03-26 05:30:00	2017-03-26 05:30:00	2017-03-26 05:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1683	6	2017-03-26 05:15:00	2017-03-26 05:15:00	2017-03-26 05:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1684	6	2017-03-26 07:45:00	2017-03-26 07:45:00	2017-03-26 07:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1685	6	2017-03-26 12:00:00	2017-03-26 12:00:00	2017-03-26 12:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1686	6	2017-03-26 15:30:00	2017-03-26 15:30:00	2017-03-26 15:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1687	6	2017-03-26 19:30:00	2017-03-26 19:30:00	2017-03-26 19:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1688	6	2017-03-26 22:00:00	2017-03-26 22:00:00	2017-03-26 22:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1689	6	2017-03-26 20:45:00	2017-03-26 20:45:00	2017-03-26 20:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1690	6	2017-03-27 02:15:00	2017-03-27 02:15:00	2017-03-27 02:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1691	6	2017-03-27 01:30:00	2017-03-27 01:30:00	2017-03-27 01:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1692	6	2017-03-27 06:45:00	2017-03-27 06:45:00	2017-03-27 06:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1693	6	2017-03-27 08:45:00	2017-03-27 08:45:00	2017-03-27 08:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1694	6	2017-03-27 13:00:00	2017-03-27 13:00:00	2017-03-27 13:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1695	6	2017-03-27 13:30:00	2017-03-27 13:30:00	2017-03-27 13:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1696	6	2017-03-27 16:01:00	2017-03-27 16:01:00	2017-03-27 16:01:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1697	6	2017-03-27 18:00:00	2017-03-27 18:00:00	2017-03-27 18:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1698	6	2017-03-27 21:15:00	2017-03-27 21:15:00	2017-03-27 21:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1699	6	2017-03-27 22:30:00	2017-03-27 22:30:00	2017-03-27 22:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1700	6	2017-03-28 02:00:00	2017-03-28 02:00:00	2017-03-28 02:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1701	6	2017-03-28 03:00:00	2017-03-28 03:00:00	2017-03-28 03:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1702	6	2017-03-28 07:45:00	2017-03-28 07:45:00	2017-03-28 07:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1703	6	2017-03-28 09:45:00	2017-03-28 09:45:00	2017-03-28 09:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1704	6	2017-03-28 11:45:00	2017-03-28 11:45:00	2017-03-28 11:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1705	6	2017-03-28 14:00:00	2017-03-28 14:00:00	2017-03-28 14:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1706	6	2017-03-28 15:30:00	2017-03-28 15:30:00	2017-03-28 15:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1707	6	2017-03-28 21:00:00	2017-03-28 21:00:00	2017-03-28 21:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1708	6	2017-03-28 21:45:00	2017-03-28 21:45:00	2017-03-28 21:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1709	6	2017-03-29 07:45:00	2017-03-29 07:45:00	2017-03-29 07:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1710	6	2017-03-29 02:30:00	2017-03-29 02:30:00	2017-03-29 02:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1711	6	2017-03-29 06:30:00	2017-03-29 06:30:00	2017-03-29 06:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1712	6	2017-03-29 01:15:00	2017-03-29 01:15:00	2017-03-29 01:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1713	6	2017-03-29 17:45:00	2017-03-29 17:45:00	2017-03-29 17:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1714	6	2017-03-29 12:30:00	2017-03-29 12:30:00	2017-03-29 12:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1715	6	2017-03-29 16:30:00	2017-03-29 16:30:00	2017-03-29 16:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1716	6	2017-03-29 11:15:00	2017-03-29 11:15:00	2017-03-29 11:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1717	6	2017-03-30 03:45:00	2017-03-30 03:45:00	2017-03-30 03:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1718	6	2017-03-29 22:30:00	2017-03-29 22:30:00	2017-03-29 22:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1719	6	2017-03-30 02:30:00	2017-03-30 02:30:00	2017-03-30 02:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1720	6	2017-03-29 21:15:00	2017-03-29 21:15:00	2017-03-29 21:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1721	6	2017-03-30 04:30:00	2017-03-30 04:30:00	2017-03-30 04:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1722	6	2017-03-31 10:00:00	2017-03-31 10:00:00	2017-03-31 10:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1723	6	2017-03-31 14:00:00	2017-03-31 14:00:00	2017-03-31 14:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1724	6	2017-03-31 11:15:00	2017-03-31 11:15:00	2017-03-31 11:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1725	6	2017-03-31 16:00:00	2017-03-31 16:00:00	2017-03-31 16:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1726	6	2017-03-31 20:00:00	2017-03-31 20:00:00	2017-03-31 20:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1727	6	2017-03-31 18:00:00	2017-03-31 18:00:00	2017-03-31 18:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1728	6	2017-03-31 22:16:00	2017-03-31 22:16:00	2017-03-31 22:16:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1729	7	2017-03-01 04:15:00	2017-03-01 04:15:00	2017-03-01 04:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1730	7	2017-03-01 05:00:00	2017-03-01 05:00:00	2017-03-01 05:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1731	7	2017-03-01 05:15:00	2017-03-01 05:15:00	2017-03-01 05:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1732	7	2017-03-01 10:00:00	2017-03-01 10:00:00	2017-03-01 10:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1733	7	2017-03-01 13:30:00	2017-03-01 13:30:00	2017-03-01 13:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1734	7	2017-03-01 15:15:00	2017-03-01 15:15:00	2017-03-01 15:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1735	7	2017-03-01 17:00:00	2017-03-01 17:00:00	2017-03-01 17:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1736	7	2017-03-01 19:30:00	2017-03-01 19:30:00	2017-03-01 19:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1737	7	2017-03-01 22:30:00	2017-03-01 22:30:00	2017-03-01 22:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1738	7	2017-03-02 01:00:00	2017-03-02 01:00:00	2017-03-02 01:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1739	7	2017-03-02 04:15:00	2017-03-02 04:15:00	2017-03-02 04:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1740	7	2017-03-02 06:00:00	2017-03-02 06:00:00	2017-03-02 06:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1741	7	2017-03-02 07:45:00	2017-03-02 07:45:00	2017-03-02 07:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1742	7	2017-03-02 11:00:00	2017-03-02 11:00:00	2017-03-02 11:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1743	7	2017-03-02 14:00:00	2017-03-02 14:00:00	2017-03-02 14:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1744	7	2017-03-02 16:00:00	2017-03-02 16:00:00	2017-03-02 16:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1745	7	2017-03-02 17:30:00	2017-03-02 17:30:00	2017-03-02 17:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1746	7	2017-03-02 20:15:00	2017-03-02 20:15:00	2017-03-02 20:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1747	7	2017-03-02 23:00:00	2017-03-02 23:00:00	2017-03-02 23:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1748	7	2017-03-03 01:30:00	2017-03-03 01:30:00	2017-03-03 01:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1749	7	2017-03-03 05:00:00	2017-03-03 05:00:00	2017-03-03 05:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1750	7	2017-03-03 07:30:00	2017-03-03 07:30:00	2017-03-03 07:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1751	7	2017-03-03 10:00:00	2017-03-03 10:00:00	2017-03-03 10:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1752	7	2017-03-03 11:30:00	2017-03-03 11:30:00	2017-03-03 11:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1753	7	2017-03-03 14:16:00	2017-03-03 14:16:00	2017-03-03 14:16:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1754	7	2017-03-03 17:16:00	2017-03-03 17:16:00	2017-03-03 17:16:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1755	7	2017-03-03 20:15:00	2017-03-03 20:15:00	2017-03-03 20:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1756	7	2017-03-03 21:00:00	2017-03-03 21:00:00	2017-03-03 21:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1757	7	2017-03-04 00:01:00	2017-03-04 00:01:00	2017-03-04 00:01:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1758	7	2017-03-04 03:16:00	2017-03-04 03:16:00	2017-03-04 03:16:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1759	7	2017-03-04 06:16:00	2017-03-04 06:16:00	2017-03-04 06:16:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1760	7	2017-03-04 07:01:00	2017-03-04 07:01:00	2017-03-04 07:01:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1761	7	2017-03-04 10:01:00	2017-03-04 10:01:00	2017-03-04 10:01:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1762	7	2017-03-04 13:16:00	2017-03-04 13:16:00	2017-03-04 13:16:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1763	7	2017-03-04 16:15:00	2017-03-04 16:15:00	2017-03-04 16:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1764	7	2017-03-04 17:00:00	2017-03-04 17:00:00	2017-03-04 17:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1765	7	2017-03-04 20:00:00	2017-03-04 20:00:00	2017-03-04 20:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1766	7	2017-03-04 23:16:00	2017-03-04 23:16:00	2017-03-04 23:16:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1767	7	2017-03-05 02:16:00	2017-03-05 02:16:00	2017-03-05 02:16:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1768	7	2017-03-05 03:01:00	2017-03-05 03:01:00	2017-03-05 03:01:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1769	7	2017-03-05 06:01:00	2017-03-05 06:01:00	2017-03-05 06:01:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1770	7	2017-03-05 09:15:00	2017-03-05 09:15:00	2017-03-05 09:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1771	7	2017-03-05 10:30:00	2017-03-05 10:30:00	2017-03-05 10:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1772	7	2017-03-05 13:15:00	2017-03-05 13:15:00	2017-03-05 13:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1773	7	2017-03-05 16:15:00	2017-03-05 16:15:00	2017-03-05 16:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1774	7	2017-03-05 18:45:00	2017-03-05 18:45:00	2017-03-05 18:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1775	7	2017-03-05 21:15:00	2017-03-05 21:15:00	2017-03-05 21:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1776	7	2017-03-05 23:45:00	2017-03-05 23:45:00	2017-03-05 23:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1777	7	2017-03-06 02:15:00	2017-03-06 02:15:00	2017-03-06 02:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1778	7	2017-03-06 04:45:00	2017-03-06 04:45:00	2017-03-06 04:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1779	7	2017-03-06 07:45:00	2017-03-06 07:45:00	2017-03-06 07:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1780	7	2017-03-06 09:45:00	2017-03-06 09:45:00	2017-03-06 09:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1781	7	2017-03-06 12:00:00	2017-03-06 12:00:00	2017-03-06 12:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1782	7	2017-03-06 14:30:00	2017-03-06 14:30:00	2017-03-06 14:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1783	7	2017-03-06 18:15:00	2017-03-06 18:15:00	2017-03-06 18:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1784	7	2017-03-06 19:45:00	2017-03-06 19:45:00	2017-03-06 19:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1785	7	2017-03-06 22:15:00	2017-03-06 22:15:00	2017-03-06 22:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1786	7	2017-03-07 01:30:00	2017-03-07 01:30:00	2017-03-07 01:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1787	7	2017-03-07 02:45:00	2017-03-07 02:45:00	2017-03-07 02:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1788	7	2017-03-07 05:30:00	2017-03-07 05:30:00	2017-03-07 05:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1789	7	2017-03-07 08:15:00	2017-03-07 08:15:00	2017-03-07 08:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1790	7	2017-03-07 11:00:00	2017-03-07 11:00:00	2017-03-07 11:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1791	7	2017-03-07 14:45:00	2017-03-07 14:45:00	2017-03-07 14:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1792	7	2017-03-07 15:15:00	2017-03-07 15:15:00	2017-03-07 15:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1793	7	2017-03-07 20:45:00	2017-03-07 20:45:00	2017-03-07 20:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1794	7	2017-03-07 20:15:00	2017-03-07 20:15:00	2017-03-07 20:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1795	7	2017-03-08 00:15:00	2017-03-08 00:15:00	2017-03-08 00:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1796	7	2017-03-08 01:00:00	2017-03-08 01:00:00	2017-03-08 01:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1797	7	2017-03-08 04:45:00	2017-03-08 04:45:00	2017-03-08 04:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1798	7	2017-03-08 04:30:00	2017-03-08 04:30:00	2017-03-08 04:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1799	7	2017-03-08 13:00:00	2017-03-08 13:00:00	2017-03-08 13:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1800	7	2017-03-08 13:30:00	2017-03-08 13:30:00	2017-03-08 13:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1801	7	2017-03-08 15:00:00	2017-03-08 15:00:00	2017-03-08 15:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1802	7	2017-03-08 20:45:00	2017-03-08 20:45:00	2017-03-08 20:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1803	7	2017-03-08 16:30:00	2017-03-08 16:30:00	2017-03-08 16:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1804	7	2017-03-08 23:00:00	2017-03-08 23:00:00	2017-03-08 23:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1805	7	2017-03-09 01:45:00	2017-03-09 01:45:00	2017-03-09 01:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1806	7	2017-03-09 04:45:00	2017-03-09 04:45:00	2017-03-09 04:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1807	7	2017-03-09 04:15:00	2017-03-09 04:15:00	2017-03-09 04:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1808	7	2017-03-09 09:45:00	2017-03-09 09:45:00	2017-03-09 09:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1809	7	2017-03-09 11:15:00	2017-03-09 11:15:00	2017-03-09 11:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1810	7	2017-03-09 13:00:00	2017-03-09 13:00:00	2017-03-09 13:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1811	7	2017-03-09 16:00:00	2017-03-09 16:00:00	2017-03-09 16:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1812	7	2017-03-09 20:45:00	2017-03-09 20:45:00	2017-03-09 20:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1813	7	2017-03-09 18:45:00	2017-03-09 18:45:00	2017-03-09 18:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1814	7	2017-03-09 22:45:00	2017-03-09 22:45:00	2017-03-09 22:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1815	7	2017-03-10 01:45:00	2017-03-10 01:45:00	2017-03-10 01:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1816	7	2017-03-10 07:00:00	2017-03-10 07:00:00	2017-03-10 07:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1817	7	2017-03-10 04:15:00	2017-03-10 04:15:00	2017-03-10 04:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1818	7	2017-03-10 12:00:00	2017-03-10 12:00:00	2017-03-10 12:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1819	7	2017-03-10 10:30:00	2017-03-10 10:30:00	2017-03-10 10:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1820	7	2017-03-10 16:45:00	2017-03-10 16:45:00	2017-03-10 16:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1821	7	2017-03-10 15:45:00	2017-03-10 15:45:00	2017-03-10 15:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1822	7	2017-03-10 20:15:00	2017-03-10 20:15:00	2017-03-10 20:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1823	7	2017-03-10 19:15:00	2017-03-10 19:15:00	2017-03-10 19:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1824	7	2017-03-11 01:45:00	2017-03-11 01:45:00	2017-03-11 01:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1825	7	2017-03-10 23:30:00	2017-03-10 23:30:00	2017-03-10 23:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1826	7	2017-03-11 05:15:00	2017-03-11 05:15:00	2017-03-11 05:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1827	7	2017-03-11 06:15:00	2017-03-11 06:15:00	2017-03-11 06:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1828	7	2017-03-11 08:30:00	2017-03-11 08:30:00	2017-03-11 08:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1829	7	2017-03-11 13:15:00	2017-03-11 13:15:00	2017-03-11 13:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1830	7	2017-03-11 14:30:00	2017-03-11 14:30:00	2017-03-11 14:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1831	7	2017-03-11 15:30:00	2017-03-11 15:30:00	2017-03-11 15:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1832	7	2017-03-11 21:00:00	2017-03-11 21:00:00	2017-03-11 21:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1833	7	2017-03-11 22:30:00	2017-03-11 22:30:00	2017-03-11 22:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1834	7	2017-03-12 03:15:00	2017-03-12 03:15:00	2017-03-12 03:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1835	7	2017-03-12 03:00:00	2017-03-12 03:00:00	2017-03-12 03:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1836	7	2017-03-12 05:00:00	2017-03-12 05:00:00	2017-03-12 05:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1837	7	2017-03-12 08:00:00	2017-03-12 08:00:00	2017-03-12 08:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1838	7	2017-03-12 12:45:00	2017-03-12 12:45:00	2017-03-12 12:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1839	7	2017-03-12 14:45:00	2017-03-12 14:45:00	2017-03-12 14:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1840	7	2017-03-12 16:45:00	2017-03-12 16:45:00	2017-03-12 16:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1841	7	2017-03-12 20:15:00	2017-03-12 20:15:00	2017-03-12 20:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1842	7	2017-03-12 21:45:00	2017-03-12 21:45:00	2017-03-12 21:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1843	7	2017-03-12 21:30:00	2017-03-12 21:30:00	2017-03-12 21:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1844	7	2017-03-13 03:45:00	2017-03-13 03:45:00	2017-03-13 03:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1845	7	2017-03-13 02:15:00	2017-03-13 02:15:00	2017-03-13 02:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1846	7	2017-03-13 07:15:00	2017-03-13 07:15:00	2017-03-13 07:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1847	7	2017-03-13 07:30:00	2017-03-13 07:30:00	2017-03-13 07:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1848	7	2017-03-13 11:45:00	2017-03-13 11:45:00	2017-03-13 11:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1849	7	2017-03-13 12:15:00	2017-03-13 12:15:00	2017-03-13 12:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1850	7	2017-03-13 16:30:00	2017-03-13 16:30:00	2017-03-13 16:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1851	7	2017-03-13 20:00:00	2017-03-13 20:00:00	2017-03-13 20:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1852	7	2017-03-13 21:00:00	2017-03-13 21:00:00	2017-03-13 21:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1853	7	2017-03-14 00:15:00	2017-03-14 00:15:00	2017-03-14 00:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1854	7	2017-03-14 04:45:00	2017-03-14 04:45:00	2017-03-14 04:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1855	7	2017-03-14 05:00:00	2017-03-14 05:00:00	2017-03-14 05:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1856	7	2017-03-14 07:00:00	2017-03-14 07:00:00	2017-03-14 07:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1857	7	2017-03-14 09:00:00	2017-03-14 09:00:00	2017-03-14 09:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1858	7	2017-03-14 13:15:00	2017-03-14 13:15:00	2017-03-14 13:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1859	7	2017-03-14 13:30:00	2017-03-14 13:30:00	2017-03-14 13:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1860	7	2017-03-14 16:30:00	2017-03-14 16:30:00	2017-03-14 16:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1861	7	2017-03-14 19:15:00	2017-03-14 19:15:00	2017-03-14 19:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1862	7	2017-03-14 21:15:00	2017-03-14 21:15:00	2017-03-14 21:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1863	7	2017-03-15 01:15:00	2017-03-15 01:15:00	2017-03-15 01:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1864	7	2017-03-15 05:15:00	2017-03-15 05:15:00	2017-03-15 05:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1865	7	2017-03-15 05:45:00	2017-03-15 05:45:00	2017-03-15 05:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1866	7	2017-03-15 08:00:00	2017-03-15 08:00:00	2017-03-15 08:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1867	7	2017-03-15 09:45:00	2017-03-15 09:45:00	2017-03-15 09:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1868	7	2017-03-15 14:30:00	2017-03-15 14:30:00	2017-03-15 14:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1869	7	2017-03-15 16:00:00	2017-03-15 16:00:00	2017-03-15 16:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1870	7	2017-03-15 20:15:00	2017-03-15 20:15:00	2017-03-15 20:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1871	7	2017-03-15 19:45:00	2017-03-15 19:45:00	2017-03-15 19:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1872	7	2017-03-15 23:15:00	2017-03-15 23:15:00	2017-03-15 23:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1873	7	2017-03-16 03:45:00	2017-03-16 03:45:00	2017-03-16 03:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1874	7	2017-03-16 07:00:00	2017-03-16 07:00:00	2017-03-16 07:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1875	7	2017-03-16 06:15:00	2017-03-16 06:15:00	2017-03-16 06:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1876	7	2017-03-16 11:30:00	2017-03-16 11:30:00	2017-03-16 11:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1877	7	2017-03-16 11:15:00	2017-03-16 11:15:00	2017-03-16 11:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1878	7	2017-03-16 14:45:00	2017-03-16 14:45:00	2017-03-16 14:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1879	7	2017-03-16 16:30:00	2017-03-16 16:30:00	2017-03-16 16:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1880	7	2017-03-16 21:30:00	2017-03-16 21:30:00	2017-03-16 21:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1881	7	2017-03-16 23:15:00	2017-03-16 23:15:00	2017-03-16 23:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1882	7	2017-03-17 03:00:00	2017-03-17 03:00:00	2017-03-17 03:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1883	7	2017-03-17 01:30:00	2017-03-17 01:30:00	2017-03-17 01:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1884	7	2017-03-17 05:30:00	2017-03-17 05:30:00	2017-03-17 05:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1885	7	2017-03-17 08:30:00	2017-03-17 08:30:00	2017-03-17 08:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1886	7	2017-03-17 11:30:00	2017-03-17 11:30:00	2017-03-17 11:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1887	7	2017-03-17 13:00:00	2017-03-17 13:00:00	2017-03-17 13:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1888	7	2017-03-17 17:45:00	2017-03-17 17:45:00	2017-03-17 17:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1889	7	2017-03-17 16:00:00	2017-03-17 16:00:00	2017-03-17 16:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1890	7	2017-03-17 22:15:00	2017-03-17 22:15:00	2017-03-17 22:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1891	7	2017-03-17 20:15:00	2017-03-17 20:15:00	2017-03-17 20:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1892	7	2017-03-18 01:00:00	2017-03-18 01:00:00	2017-03-18 01:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1893	7	2017-03-18 04:15:00	2017-03-18 04:15:00	2017-03-18 04:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1894	7	2017-03-18 07:15:00	2017-03-18 07:15:00	2017-03-18 07:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1895	7	2017-03-18 07:30:00	2017-03-18 07:30:00	2017-03-18 07:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1896	7	2017-03-18 10:30:00	2017-03-18 10:30:00	2017-03-18 10:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1897	7	2017-03-18 13:15:00	2017-03-18 13:15:00	2017-03-18 13:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1898	7	2017-03-18 17:00:00	2017-03-18 17:00:00	2017-03-18 17:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1899	7	2017-03-18 19:15:00	2017-03-18 19:15:00	2017-03-18 19:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1900	7	2017-03-18 20:30:00	2017-03-18 20:30:00	2017-03-18 20:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1901	7	2017-03-19 00:15:00	2017-03-19 00:15:00	2017-03-19 00:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1902	7	2017-03-19 04:00:00	2017-03-19 04:00:00	2017-03-19 04:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1903	7	2017-03-19 05:00:00	2017-03-19 05:00:00	2017-03-19 05:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1904	7	2017-03-19 06:30:00	2017-03-19 06:30:00	2017-03-19 06:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1905	7	2017-03-19 10:00:00	2017-03-19 10:00:00	2017-03-19 10:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1906	7	2017-03-19 14:15:00	2017-03-19 14:15:00	2017-03-19 14:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1907	7	2017-03-19 14:30:00	2017-03-19 14:30:00	2017-03-19 14:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1908	7	2017-03-19 18:15:00	2017-03-19 18:15:00	2017-03-19 18:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1909	7	2017-03-19 17:30:00	2017-03-19 17:30:00	2017-03-19 17:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1910	7	2017-03-20 01:45:00	2017-03-20 01:45:00	2017-03-20 01:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1911	7	2017-03-20 01:30:00	2017-03-20 01:30:00	2017-03-20 01:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1912	7	2017-03-20 04:30:00	2017-03-20 04:30:00	2017-03-20 04:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1913	7	2017-03-20 06:30:00	2017-03-20 06:30:00	2017-03-20 06:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1914	7	2017-03-20 10:45:00	2017-03-20 10:45:00	2017-03-20 10:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1915	7	2017-03-20 11:00:00	2017-03-20 11:00:00	2017-03-20 11:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1916	7	2017-03-20 13:45:00	2017-03-20 13:45:00	2017-03-20 13:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1917	7	2017-03-20 16:00:00	2017-03-20 16:00:00	2017-03-20 16:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1918	7	2017-03-20 16:45:00	2017-03-20 16:45:00	2017-03-20 16:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1919	7	2017-03-20 19:00:00	2017-03-20 19:00:00	2017-03-20 19:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1920	7	2017-03-20 23:45:00	2017-03-20 23:45:00	2017-03-20 23:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1921	7	2017-03-21 02:45:00	2017-03-21 02:45:00	2017-03-21 02:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1922	7	2017-03-21 04:15:00	2017-03-21 04:15:00	2017-03-21 04:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1923	7	2017-03-21 07:45:00	2017-03-21 07:45:00	2017-03-21 07:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1924	7	2017-03-21 09:15:00	2017-03-21 09:15:00	2017-03-21 09:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1925	7	2017-03-21 13:30:00	2017-03-21 13:30:00	2017-03-21 13:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1926	7	2017-03-21 14:45:00	2017-03-21 14:45:00	2017-03-21 14:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1927	7	2017-03-21 16:30:00	2017-03-21 16:30:00	2017-03-21 16:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1928	7	2017-03-21 22:00:00	2017-03-21 22:00:00	2017-03-21 22:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1929	7	2017-03-21 23:00:00	2017-03-21 23:00:00	2017-03-21 23:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1930	7	2017-03-22 02:00:00	2017-03-22 02:00:00	2017-03-22 02:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1931	7	2017-03-22 03:30:00	2017-03-22 03:30:00	2017-03-22 03:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1932	7	2017-03-22 06:30:00	2017-03-22 06:30:00	2017-03-22 06:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1933	7	2017-03-22 09:45:00	2017-03-22 09:45:00	2017-03-22 09:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1934	7	2017-03-22 12:15:00	2017-03-22 12:15:00	2017-03-22 12:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1935	7	2017-03-22 11:30:00	2017-03-22 11:30:00	2017-03-22 11:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1936	7	2017-03-22 16:30:00	2017-03-22 16:30:00	2017-03-22 16:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1937	7	2017-03-22 19:00:00	2017-03-22 19:00:00	2017-03-22 19:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1938	7	2017-03-22 20:00:00	2017-03-22 20:00:00	2017-03-22 20:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1939	7	2017-03-23 00:15:00	2017-03-23 00:15:00	2017-03-23 00:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1940	7	2017-03-23 02:15:00	2017-03-23 02:15:00	2017-03-23 02:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1941	7	2017-03-23 05:45:00	2017-03-23 05:45:00	2017-03-23 05:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1942	7	2017-03-23 07:30:00	2017-03-23 07:30:00	2017-03-23 07:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1943	7	2017-03-23 10:00:00	2017-03-23 10:00:00	2017-03-23 10:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1944	7	2017-03-23 12:00:00	2017-03-23 12:00:00	2017-03-23 12:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1945	7	2017-03-23 15:30:00	2017-03-23 15:30:00	2017-03-23 15:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1946	7	2017-03-23 19:15:00	2017-03-23 19:15:00	2017-03-23 19:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1947	7	2017-03-23 18:00:00	2017-03-23 18:00:00	2017-03-23 18:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1948	7	2017-03-23 23:45:00	2017-03-23 23:45:00	2017-03-23 23:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1949	7	2017-03-23 21:15:00	2017-03-23 21:15:00	2017-03-23 21:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1950	7	2017-03-24 03:45:00	2017-03-24 03:45:00	2017-03-24 03:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1951	7	2017-03-24 04:00:00	2017-03-24 04:00:00	2017-03-24 04:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1952	7	2017-03-24 07:15:00	2017-03-24 07:15:00	2017-03-24 07:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1953	7	2017-03-24 10:00:00	2017-03-24 10:00:00	2017-03-24 10:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1954	7	2017-03-24 14:00:00	2017-03-24 14:00:00	2017-03-24 14:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1955	7	2017-03-24 15:00:00	2017-03-24 15:00:00	2017-03-24 15:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1956	7	2017-03-24 16:00:00	2017-03-24 16:00:00	2017-03-24 16:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1957	7	2017-03-24 21:15:00	2017-03-24 21:15:00	2017-03-24 21:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1958	7	2017-03-25 00:15:00	2017-03-25 00:15:00	2017-03-25 00:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1959	7	2017-03-24 23:45:00	2017-03-24 23:45:00	2017-03-24 23:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1960	7	2017-03-25 04:30:00	2017-03-25 04:30:00	2017-03-25 04:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1961	7	2017-03-25 07:00:00	2017-03-25 07:00:00	2017-03-25 07:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1962	7	2017-03-25 07:45:00	2017-03-25 07:45:00	2017-03-25 07:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1963	7	2017-03-25 09:15:00	2017-03-25 09:15:00	2017-03-25 09:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1964	7	2017-03-25 16:00:00	2017-03-25 16:00:00	2017-03-25 16:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1965	7	2017-03-25 15:00:00	2017-03-25 15:00:00	2017-03-25 15:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1966	7	2017-03-25 18:15:00	2017-03-25 18:15:00	2017-03-25 18:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1967	7	2017-03-25 22:00:00	2017-03-25 22:00:00	2017-03-25 22:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1968	7	2017-03-25 22:45:00	2017-03-25 22:45:00	2017-03-25 22:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1969	7	2017-03-26 02:15:00	2017-03-26 02:15:00	2017-03-26 02:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1970	7	2017-03-26 04:30:00	2017-03-26 04:30:00	2017-03-26 04:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1971	7	2017-03-26 06:30:00	2017-03-26 06:30:00	2017-03-26 06:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1972	7	2017-03-26 07:30:00	2017-03-26 07:30:00	2017-03-26 07:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1973	7	2017-03-26 13:15:00	2017-03-26 13:15:00	2017-03-26 13:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1974	7	2017-03-26 17:00:00	2017-03-26 17:00:00	2017-03-26 17:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1975	7	2017-03-26 15:15:00	2017-03-26 15:15:00	2017-03-26 15:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1976	7	2017-03-26 20:45:00	2017-03-26 20:45:00	2017-03-26 20:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1977	7	2017-03-26 21:45:00	2017-03-26 21:45:00	2017-03-26 21:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1978	7	2017-03-26 23:45:00	2017-03-26 23:45:00	2017-03-26 23:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1979	7	2017-03-27 03:30:00	2017-03-27 03:30:00	2017-03-27 03:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1980	7	2017-03-27 05:15:00	2017-03-27 05:15:00	2017-03-27 05:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1981	7	2017-03-27 07:00:00	2017-03-27 07:00:00	2017-03-27 07:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1982	7	2017-03-27 10:30:00	2017-03-27 10:30:00	2017-03-27 10:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1983	7	2017-03-27 12:30:00	2017-03-27 12:30:00	2017-03-27 12:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1984	7	2017-03-27 14:15:00	2017-03-27 14:15:00	2017-03-27 14:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1985	7	2017-03-27 19:30:00	2017-03-27 19:30:00	2017-03-27 19:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1986	7	2017-03-27 20:00:00	2017-03-27 20:00:00	2017-03-27 20:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1987	7	2017-03-28 00:15:00	2017-03-28 00:15:00	2017-03-28 00:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1988	7	2017-03-28 02:30:00	2017-03-28 02:30:00	2017-03-28 02:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1989	7	2017-03-28 05:00:00	2017-03-28 05:00:00	2017-03-28 05:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1990	7	2017-03-28 06:30:00	2017-03-28 06:30:00	2017-03-28 06:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1991	7	2017-03-28 09:45:00	2017-03-28 09:45:00	2017-03-28 09:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1992	7	2017-03-28 13:15:00	2017-03-28 13:15:00	2017-03-28 13:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1993	7	2017-03-28 14:45:00	2017-03-28 14:45:00	2017-03-28 14:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1994	7	2017-03-28 18:00:00	2017-03-28 18:00:00	2017-03-28 18:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1995	7	2017-03-28 19:45:00	2017-03-28 19:45:00	2017-03-28 19:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1996	7	2017-03-28 21:45:00	2017-03-28 21:45:00	2017-03-28 21:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1997	7	2017-03-28 23:45:00	2017-03-28 23:45:00	2017-03-28 23:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1998	7	2017-03-29 03:45:00	2017-03-29 03:45:00	2017-03-29 03:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1999	7	2017-03-28 22:30:00	2017-03-28 22:30:00	2017-03-28 22:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2000	7	2017-03-29 05:00:00	2017-03-29 05:00:00	2017-03-29 05:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2001	7	2017-03-29 09:45:00	2017-03-29 09:45:00	2017-03-29 09:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2002	7	2017-03-29 13:45:00	2017-03-29 13:45:00	2017-03-29 13:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2003	7	2017-03-29 08:30:00	2017-03-29 08:30:00	2017-03-29 08:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2004	7	2017-03-29 15:00:00	2017-03-29 15:00:00	2017-03-29 15:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2005	7	2017-03-29 19:45:00	2017-03-29 19:45:00	2017-03-29 19:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2006	7	2017-03-29 23:45:00	2017-03-29 23:45:00	2017-03-29 23:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2007	7	2017-03-29 18:30:00	2017-03-29 18:30:00	2017-03-29 18:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2008	7	2017-03-30 01:00:00	2017-03-30 01:00:00	2017-03-30 01:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2009	7	2017-03-31 11:30:00	2017-03-31 11:30:00	2017-03-31 11:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2010	7	2017-03-31 11:15:00	2017-03-31 11:15:00	2017-03-31 11:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2011	7	2017-03-30 07:00:00	2017-03-30 07:00:00	2017-03-30 07:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2012	7	2017-03-31 12:30:00	2017-03-31 12:30:00	2017-03-31 12:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2013	7	2017-03-31 17:15:00	2017-03-31 17:15:00	2017-03-31 17:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2014	7	2017-03-31 23:45:00	2017-03-31 23:45:00	2017-03-31 23:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2015	7	2017-03-31 16:45:00	2017-03-31 16:45:00	2017-03-31 16:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2016	7	2017-03-31 23:30:00	2017-03-31 23:30:00	2017-03-31 23:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
\.


--
-- TOC entry 3647 (class 0 OID 62472)
-- Dependencies: 203
-- Data for Name: observationconstellation; Type: TABLE DATA; Schema: public; Owner: user
--

COPY observationconstellation (observationconstellationid, observablepropertyid, procedureid, observationtypeid, offeringid, deleted, hiddenchild) FROM stdin;
6	6	1	4	1	F	F
5	5	1	4	1	F	F
3	3	1	4	1	F	F
4	4	1	4	1	F	F
1	1	1	4	1	F	F
7	7	1	4	1	F	F
2	2	1	4	1	F	F
\.


--
-- TOC entry 3864 (class 0 OID 0)
-- Dependencies: 230
-- Name: observationconstellationid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('observationconstellationid_seq', 7, true);


--
-- TOC entry 3648 (class 0 OID 62481)
-- Dependencies: 204
-- Data for Name: observationhasoffering; Type: TABLE DATA; Schema: public; Owner: user
--

COPY observationhasoffering (observationid, offeringid) FROM stdin;
1	1
2	1
3	1
4	1
5	1
6	1
7	1
8	1
9	1
10	1
11	1
12	1
13	1
14	1
15	1
16	1
17	1
18	1
19	1
20	1
21	1
22	1
23	1
24	1
25	1
26	1
27	1
28	1
29	1
30	1
31	1
32	1
33	1
34	1
35	1
36	1
37	1
38	1
39	1
40	1
41	1
42	1
43	1
44	1
45	1
46	1
47	1
48	1
49	1
50	1
51	1
52	1
53	1
54	1
55	1
56	1
57	1
58	1
59	1
60	1
61	1
62	1
63	1
64	1
65	1
66	1
67	1
68	1
69	1
70	1
71	1
72	1
73	1
74	1
75	1
76	1
77	1
78	1
79	1
80	1
81	1
82	1
83	1
84	1
85	1
86	1
87	1
88	1
89	1
90	1
91	1
92	1
93	1
94	1
95	1
96	1
97	1
98	1
99	1
100	1
101	1
102	1
103	1
104	1
105	1
106	1
107	1
108	1
109	1
110	1
111	1
112	1
113	1
114	1
115	1
116	1
117	1
118	1
119	1
120	1
121	1
122	1
123	1
124	1
125	1
126	1
127	1
128	1
129	1
130	1
131	1
132	1
133	1
134	1
135	1
136	1
137	1
138	1
139	1
140	1
141	1
142	1
143	1
144	1
145	1
146	1
147	1
148	1
149	1
150	1
151	1
152	1
153	1
154	1
155	1
156	1
157	1
158	1
159	1
160	1
161	1
162	1
163	1
164	1
165	1
166	1
167	1
168	1
169	1
170	1
171	1
172	1
173	1
174	1
175	1
176	1
177	1
178	1
179	1
180	1
181	1
182	1
183	1
184	1
185	1
186	1
187	1
188	1
189	1
190	1
191	1
192	1
193	1
194	1
195	1
196	1
197	1
198	1
199	1
200	1
201	1
202	1
203	1
204	1
205	1
206	1
207	1
208	1
209	1
210	1
211	1
212	1
213	1
214	1
215	1
216	1
217	1
218	1
219	1
220	1
221	1
222	1
223	1
224	1
225	1
226	1
227	1
228	1
229	1
230	1
231	1
232	1
233	1
234	1
235	1
236	1
237	1
238	1
239	1
240	1
241	1
242	1
243	1
244	1
245	1
246	1
247	1
248	1
249	1
250	1
251	1
252	1
253	1
254	1
255	1
256	1
257	1
258	1
259	1
260	1
261	1
262	1
263	1
264	1
265	1
266	1
267	1
268	1
269	1
270	1
271	1
272	1
273	1
274	1
275	1
276	1
277	1
278	1
279	1
280	1
281	1
282	1
283	1
284	1
285	1
286	1
287	1
288	1
289	1
290	1
291	1
292	1
293	1
294	1
295	1
296	1
297	1
298	1
299	1
300	1
301	1
302	1
303	1
304	1
305	1
306	1
307	1
308	1
309	1
310	1
311	1
312	1
313	1
314	1
315	1
316	1
317	1
318	1
319	1
320	1
321	1
322	1
323	1
324	1
325	1
326	1
327	1
328	1
329	1
330	1
331	1
332	1
333	1
334	1
335	1
336	1
337	1
338	1
339	1
340	1
341	1
342	1
343	1
344	1
345	1
346	1
347	1
348	1
349	1
350	1
351	1
352	1
353	1
354	1
355	1
356	1
357	1
358	1
359	1
360	1
361	1
362	1
363	1
364	1
365	1
366	1
367	1
368	1
369	1
370	1
371	1
372	1
373	1
374	1
375	1
376	1
377	1
378	1
379	1
380	1
381	1
382	1
383	1
384	1
385	1
386	1
387	1
388	1
389	1
390	1
391	1
392	1
393	1
394	1
395	1
396	1
397	1
398	1
399	1
400	1
401	1
402	1
403	1
404	1
405	1
406	1
407	1
408	1
409	1
410	1
411	1
412	1
413	1
414	1
415	1
416	1
417	1
418	1
419	1
420	1
421	1
422	1
423	1
424	1
425	1
426	1
427	1
428	1
429	1
430	1
431	1
432	1
433	1
434	1
435	1
436	1
437	1
438	1
439	1
440	1
441	1
442	1
443	1
444	1
445	1
446	1
447	1
448	1
449	1
450	1
451	1
452	1
453	1
454	1
455	1
456	1
457	1
458	1
459	1
460	1
461	1
462	1
463	1
464	1
465	1
466	1
467	1
468	1
469	1
470	1
471	1
472	1
473	1
474	1
475	1
476	1
477	1
478	1
479	1
480	1
481	1
482	1
483	1
484	1
485	1
486	1
487	1
488	1
489	1
490	1
491	1
492	1
493	1
494	1
495	1
496	1
497	1
498	1
499	1
500	1
501	1
502	1
503	1
504	1
505	1
506	1
507	1
508	1
509	1
510	1
511	1
512	1
513	1
514	1
515	1
516	1
517	1
518	1
519	1
520	1
521	1
522	1
523	1
524	1
525	1
526	1
527	1
528	1
529	1
530	1
531	1
532	1
533	1
534	1
535	1
536	1
537	1
538	1
539	1
540	1
541	1
542	1
543	1
544	1
545	1
546	1
547	1
548	1
549	1
550	1
551	1
552	1
553	1
554	1
555	1
556	1
557	1
558	1
559	1
560	1
561	1
562	1
563	1
564	1
565	1
566	1
567	1
568	1
569	1
570	1
571	1
572	1
573	1
574	1
575	1
576	1
577	1
578	1
579	1
580	1
581	1
582	1
583	1
584	1
585	1
586	1
587	1
588	1
589	1
590	1
591	1
592	1
593	1
594	1
595	1
596	1
597	1
598	1
599	1
600	1
601	1
602	1
603	1
604	1
605	1
606	1
607	1
608	1
609	1
610	1
611	1
612	1
613	1
614	1
615	1
616	1
617	1
618	1
619	1
620	1
621	1
622	1
623	1
624	1
625	1
626	1
627	1
628	1
629	1
630	1
631	1
632	1
633	1
634	1
635	1
636	1
637	1
638	1
639	1
640	1
641	1
642	1
643	1
644	1
645	1
646	1
647	1
648	1
649	1
650	1
651	1
652	1
653	1
654	1
655	1
656	1
657	1
658	1
659	1
660	1
661	1
662	1
663	1
664	1
665	1
666	1
667	1
668	1
669	1
670	1
671	1
672	1
673	1
674	1
675	1
676	1
677	1
678	1
679	1
680	1
681	1
682	1
683	1
684	1
685	1
686	1
687	1
688	1
689	1
690	1
691	1
692	1
693	1
694	1
695	1
696	1
697	1
698	1
699	1
700	1
701	1
702	1
703	1
704	1
705	1
706	1
707	1
708	1
709	1
710	1
711	1
712	1
713	1
714	1
715	1
716	1
717	1
718	1
719	1
720	1
721	1
722	1
723	1
724	1
725	1
726	1
727	1
728	1
729	1
730	1
731	1
732	1
733	1
734	1
735	1
736	1
737	1
738	1
739	1
740	1
741	1
742	1
743	1
744	1
745	1
746	1
747	1
748	1
749	1
750	1
751	1
752	1
753	1
754	1
755	1
756	1
757	1
758	1
759	1
760	1
761	1
762	1
763	1
764	1
765	1
766	1
767	1
768	1
769	1
770	1
771	1
772	1
773	1
774	1
775	1
776	1
777	1
778	1
779	1
780	1
781	1
782	1
783	1
784	1
785	1
786	1
787	1
788	1
789	1
790	1
791	1
792	1
793	1
794	1
795	1
796	1
797	1
798	1
799	1
800	1
801	1
802	1
803	1
804	1
805	1
806	1
807	1
808	1
809	1
810	1
811	1
812	1
813	1
814	1
815	1
816	1
817	1
818	1
819	1
820	1
821	1
822	1
823	1
824	1
825	1
826	1
827	1
828	1
829	1
830	1
831	1
832	1
833	1
834	1
835	1
836	1
837	1
838	1
839	1
840	1
841	1
842	1
843	1
844	1
845	1
846	1
847	1
848	1
849	1
850	1
851	1
852	1
853	1
854	1
855	1
856	1
857	1
858	1
859	1
860	1
861	1
862	1
863	1
864	1
865	1
866	1
867	1
868	1
869	1
870	1
871	1
872	1
873	1
874	1
875	1
876	1
877	1
878	1
879	1
880	1
881	1
882	1
883	1
884	1
885	1
886	1
887	1
888	1
889	1
890	1
891	1
892	1
893	1
894	1
895	1
896	1
897	1
898	1
899	1
900	1
901	1
902	1
903	1
904	1
905	1
906	1
907	1
908	1
909	1
910	1
911	1
912	1
913	1
914	1
915	1
916	1
917	1
918	1
919	1
920	1
921	1
922	1
923	1
924	1
925	1
926	1
927	1
928	1
929	1
930	1
931	1
932	1
933	1
934	1
935	1
936	1
937	1
938	1
939	1
940	1
941	1
942	1
943	1
944	1
945	1
946	1
947	1
948	1
949	1
950	1
951	1
952	1
953	1
954	1
955	1
956	1
957	1
958	1
959	1
960	1
961	1
962	1
963	1
964	1
965	1
966	1
967	1
968	1
969	1
970	1
971	1
972	1
973	1
974	1
975	1
976	1
977	1
978	1
979	1
980	1
981	1
982	1
983	1
984	1
985	1
986	1
987	1
988	1
989	1
990	1
991	1
992	1
993	1
994	1
995	1
996	1
997	1
998	1
999	1
1000	1
1001	1
1002	1
1003	1
1004	1
1005	1
1006	1
1007	1
1008	1
1009	1
1010	1
1011	1
1012	1
1013	1
1014	1
1015	1
1016	1
1017	1
1018	1
1019	1
1020	1
1021	1
1022	1
1023	1
1024	1
1025	1
1026	1
1027	1
1028	1
1029	1
1030	1
1031	1
1032	1
1033	1
1034	1
1035	1
1036	1
1037	1
1038	1
1039	1
1040	1
1041	1
1042	1
1043	1
1044	1
1045	1
1046	1
1047	1
1048	1
1049	1
1050	1
1051	1
1052	1
1053	1
1054	1
1055	1
1056	1
1057	1
1058	1
1059	1
1060	1
1061	1
1062	1
1063	1
1064	1
1065	1
1066	1
1067	1
1068	1
1069	1
1070	1
1071	1
1072	1
1073	1
1074	1
1075	1
1076	1
1077	1
1078	1
1079	1
1080	1
1081	1
1082	1
1083	1
1084	1
1085	1
1086	1
1087	1
1088	1
1089	1
1090	1
1091	1
1092	1
1093	1
1094	1
1095	1
1096	1
1097	1
1098	1
1099	1
1100	1
1101	1
1102	1
1103	1
1104	1
1105	1
1106	1
1107	1
1108	1
1109	1
1110	1
1111	1
1112	1
1113	1
1114	1
1115	1
1116	1
1117	1
1118	1
1119	1
1120	1
1121	1
1122	1
1123	1
1124	1
1125	1
1126	1
1127	1
1128	1
1129	1
1130	1
1131	1
1132	1
1133	1
1134	1
1135	1
1136	1
1137	1
1138	1
1139	1
1140	1
1141	1
1142	1
1143	1
1144	1
1145	1
1146	1
1147	1
1148	1
1149	1
1150	1
1151	1
1152	1
1153	1
1154	1
1155	1
1156	1
1157	1
1158	1
1159	1
1160	1
1161	1
1162	1
1163	1
1164	1
1165	1
1166	1
1167	1
1168	1
1169	1
1170	1
1171	1
1172	1
1173	1
1174	1
1175	1
1176	1
1177	1
1178	1
1179	1
1180	1
1181	1
1182	1
1183	1
1184	1
1185	1
1186	1
1187	1
1188	1
1189	1
1190	1
1191	1
1192	1
1193	1
1194	1
1195	1
1196	1
1197	1
1198	1
1199	1
1200	1
1201	1
1202	1
1203	1
1204	1
1205	1
1206	1
1207	1
1208	1
1209	1
1210	1
1211	1
1212	1
1213	1
1214	1
1215	1
1216	1
1217	1
1218	1
1219	1
1220	1
1221	1
1222	1
1223	1
1224	1
1225	1
1226	1
1227	1
1228	1
1229	1
1230	1
1231	1
1232	1
1233	1
1234	1
1235	1
1236	1
1237	1
1238	1
1239	1
1240	1
1241	1
1242	1
1243	1
1244	1
1245	1
1246	1
1247	1
1248	1
1249	1
1250	1
1251	1
1252	1
1253	1
1254	1
1255	1
1256	1
1257	1
1258	1
1259	1
1260	1
1261	1
1262	1
1263	1
1264	1
1265	1
1266	1
1267	1
1268	1
1269	1
1270	1
1271	1
1272	1
1273	1
1274	1
1275	1
1276	1
1277	1
1278	1
1279	1
1280	1
1281	1
1282	1
1283	1
1284	1
1285	1
1286	1
1287	1
1288	1
1289	1
1290	1
1291	1
1292	1
1293	1
1294	1
1295	1
1296	1
1297	1
1298	1
1299	1
1300	1
1301	1
1302	1
1303	1
1304	1
1305	1
1306	1
1307	1
1308	1
1309	1
1310	1
1311	1
1312	1
1313	1
1314	1
1315	1
1316	1
1317	1
1318	1
1319	1
1320	1
1321	1
1322	1
1323	1
1324	1
1325	1
1326	1
1327	1
1328	1
1329	1
1330	1
1331	1
1332	1
1333	1
1334	1
1335	1
1336	1
1337	1
1338	1
1339	1
1340	1
1341	1
1342	1
1343	1
1344	1
1345	1
1346	1
1347	1
1348	1
1349	1
1350	1
1351	1
1352	1
1353	1
1354	1
1355	1
1356	1
1357	1
1358	1
1359	1
1360	1
1361	1
1362	1
1363	1
1364	1
1365	1
1366	1
1367	1
1368	1
1369	1
1370	1
1371	1
1372	1
1373	1
1374	1
1375	1
1376	1
1377	1
1378	1
1379	1
1380	1
1381	1
1382	1
1383	1
1384	1
1385	1
1386	1
1387	1
1388	1
1389	1
1390	1
1391	1
1392	1
1393	1
1394	1
1395	1
1396	1
1397	1
1398	1
1399	1
1400	1
1401	1
1402	1
1403	1
1404	1
1405	1
1406	1
1407	1
1408	1
1409	1
1410	1
1411	1
1412	1
1413	1
1414	1
1415	1
1416	1
1417	1
1418	1
1419	1
1420	1
1421	1
1422	1
1423	1
1424	1
1425	1
1426	1
1427	1
1428	1
1429	1
1430	1
1431	1
1432	1
1433	1
1434	1
1435	1
1436	1
1437	1
1438	1
1439	1
1440	1
1441	1
1442	1
1443	1
1444	1
1445	1
1446	1
1447	1
1448	1
1449	1
1450	1
1451	1
1452	1
1453	1
1454	1
1455	1
1456	1
1457	1
1458	1
1459	1
1460	1
1461	1
1462	1
1463	1
1464	1
1465	1
1466	1
1467	1
1468	1
1469	1
1470	1
1471	1
1472	1
1473	1
1474	1
1475	1
1476	1
1477	1
1478	1
1479	1
1480	1
1481	1
1482	1
1483	1
1484	1
1485	1
1486	1
1487	1
1488	1
1489	1
1490	1
1491	1
1492	1
1493	1
1494	1
1495	1
1496	1
1497	1
1498	1
1499	1
1500	1
1501	1
1502	1
1503	1
1504	1
1505	1
1506	1
1507	1
1508	1
1509	1
1510	1
1511	1
1512	1
1513	1
1514	1
1515	1
1516	1
1517	1
1518	1
1519	1
1520	1
1521	1
1522	1
1523	1
1524	1
1525	1
1526	1
1527	1
1528	1
1529	1
1530	1
1531	1
1532	1
1533	1
1534	1
1535	1
1536	1
1537	1
1538	1
1539	1
1540	1
1541	1
1542	1
1543	1
1544	1
1545	1
1546	1
1547	1
1548	1
1549	1
1550	1
1551	1
1552	1
1553	1
1554	1
1555	1
1556	1
1557	1
1558	1
1559	1
1560	1
1561	1
1562	1
1563	1
1564	1
1565	1
1566	1
1567	1
1568	1
1569	1
1570	1
1571	1
1572	1
1573	1
1574	1
1575	1
1576	1
1577	1
1578	1
1579	1
1580	1
1581	1
1582	1
1583	1
1584	1
1585	1
1586	1
1587	1
1588	1
1589	1
1590	1
1591	1
1592	1
1593	1
1594	1
1595	1
1596	1
1597	1
1598	1
1599	1
1600	1
1601	1
1602	1
1603	1
1604	1
1605	1
1606	1
1607	1
1608	1
1609	1
1610	1
1611	1
1612	1
1613	1
1614	1
1615	1
1616	1
1617	1
1618	1
1619	1
1620	1
1621	1
1622	1
1623	1
1624	1
1625	1
1626	1
1627	1
1628	1
1629	1
1630	1
1631	1
1632	1
1633	1
1634	1
1635	1
1636	1
1637	1
1638	1
1639	1
1640	1
1641	1
1642	1
1643	1
1644	1
1645	1
1646	1
1647	1
1648	1
1649	1
1650	1
1651	1
1652	1
1653	1
1654	1
1655	1
1656	1
1657	1
1658	1
1659	1
1660	1
1661	1
1662	1
1663	1
1664	1
1665	1
1666	1
1667	1
1668	1
1669	1
1670	1
1671	1
1672	1
1673	1
1674	1
1675	1
1676	1
1677	1
1678	1
1679	1
1680	1
1681	1
1682	1
1683	1
1684	1
1685	1
1686	1
1687	1
1688	1
1689	1
1690	1
1691	1
1692	1
1693	1
1694	1
1695	1
1696	1
1697	1
1698	1
1699	1
1700	1
1701	1
1702	1
1703	1
1704	1
1705	1
1706	1
1707	1
1708	1
1709	1
1710	1
1711	1
1712	1
1713	1
1714	1
1715	1
1716	1
1717	1
1718	1
1719	1
1720	1
1721	1
1722	1
1723	1
1724	1
1725	1
1726	1
1727	1
1728	1
1729	1
1730	1
1731	1
1732	1
1733	1
1734	1
1735	1
1736	1
1737	1
1738	1
1739	1
1740	1
1741	1
1742	1
1743	1
1744	1
1745	1
1746	1
1747	1
1748	1
1749	1
1750	1
1751	1
1752	1
1753	1
1754	1
1755	1
1756	1
1757	1
1758	1
1759	1
1760	1
1761	1
1762	1
1763	1
1764	1
1765	1
1766	1
1767	1
1768	1
1769	1
1770	1
1771	1
1772	1
1773	1
1774	1
1775	1
1776	1
1777	1
1778	1
1779	1
1780	1
1781	1
1782	1
1783	1
1784	1
1785	1
1786	1
1787	1
1788	1
1789	1
1790	1
1791	1
1792	1
1793	1
1794	1
1795	1
1796	1
1797	1
1798	1
1799	1
1800	1
1801	1
1802	1
1803	1
1804	1
1805	1
1806	1
1807	1
1808	1
1809	1
1810	1
1811	1
1812	1
1813	1
1814	1
1815	1
1816	1
1817	1
1818	1
1819	1
1820	1
1821	1
1822	1
1823	1
1824	1
1825	1
1826	1
1827	1
1828	1
1829	1
1830	1
1831	1
1832	1
1833	1
1834	1
1835	1
1836	1
1837	1
1838	1
1839	1
1840	1
1841	1
1842	1
1843	1
1844	1
1845	1
1846	1
1847	1
1848	1
1849	1
1850	1
1851	1
1852	1
1853	1
1854	1
1855	1
1856	1
1857	1
1858	1
1859	1
1860	1
1861	1
1862	1
1863	1
1864	1
1865	1
1866	1
1867	1
1868	1
1869	1
1870	1
1871	1
1872	1
1873	1
1874	1
1875	1
1876	1
1877	1
1878	1
1879	1
1880	1
1881	1
1882	1
1883	1
1884	1
1885	1
1886	1
1887	1
1888	1
1889	1
1890	1
1891	1
1892	1
1893	1
1894	1
1895	1
1896	1
1897	1
1898	1
1899	1
1900	1
1901	1
1902	1
1903	1
1904	1
1905	1
1906	1
1907	1
1908	1
1909	1
1910	1
1911	1
1912	1
1913	1
1914	1
1915	1
1916	1
1917	1
1918	1
1919	1
1920	1
1921	1
1922	1
1923	1
1924	1
1925	1
1926	1
1927	1
1928	1
1929	1
1930	1
1931	1
1932	1
1933	1
1934	1
1935	1
1936	1
1937	1
1938	1
1939	1
1940	1
1941	1
1942	1
1943	1
1944	1
1945	1
1946	1
1947	1
1948	1
1949	1
1950	1
1951	1
1952	1
1953	1
1954	1
1955	1
1956	1
1957	1
1958	1
1959	1
1960	1
1961	1
1962	1
1963	1
1964	1
1965	1
1966	1
1967	1
1968	1
1969	1
1970	1
1971	1
1972	1
1973	1
1974	1
1975	1
1976	1
1977	1
1978	1
1979	1
1980	1
1981	1
1982	1
1983	1
1984	1
1985	1
1986	1
1987	1
1988	1
1989	1
1990	1
1991	1
1992	1
1993	1
1994	1
1995	1
1996	1
1997	1
1998	1
1999	1
2000	1
2001	1
2002	1
2003	1
2004	1
2005	1
2006	1
2007	1
2008	1
2009	1
2010	1
2011	1
2012	1
2013	1
2014	1
2015	1
2016	1
\.


--
-- TOC entry 3865 (class 0 OID 0)
-- Dependencies: 231
-- Name: observationid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('observationid_seq', 2016, true);


--
-- TOC entry 3649 (class 0 OID 62486)
-- Dependencies: 205
-- Data for Name: observationtype; Type: TABLE DATA; Schema: public; Owner: user
--

COPY observationtype (observationtypeid, observationtype) FROM stdin;
1	http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_SWEArrayObservation
2	http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CategoryObservation
3	http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TruthObservation
4	http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement
5	http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CountObservation
6	http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_GeometryObservation
7	http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TextObservation
\.


--
-- TOC entry 3866 (class 0 OID 0)
-- Dependencies: 232
-- Name: observationtypeid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('observationtypeid_seq', 7, true);


--
-- TOC entry 3650 (class 0 OID 62491)
-- Dependencies: 206
-- Data for Name: offering; Type: TABLE DATA; Schema: public; Owner: user
--

COPY offering (offeringid, hibernatediscriminator, identifier, codespace, name, codespacename, description, disabled) FROM stdin;
1	T	weather-data-muenster	\N	Weatherdata	\N	\N	F
\.


--
-- TOC entry 3651 (class 0 OID 62501)
-- Dependencies: 207
-- Data for Name: offeringallowedfeaturetype; Type: TABLE DATA; Schema: public; Owner: user
--

COPY offeringallowedfeaturetype (offeringid, featureofinteresttypeid) FROM stdin;
1	1
\.


--
-- TOC entry 3652 (class 0 OID 62506)
-- Dependencies: 208
-- Data for Name: offeringallowedobservationtype; Type: TABLE DATA; Schema: public; Owner: user
--

COPY offeringallowedobservationtype (offeringid, observationtypeid) FROM stdin;
1	5
1	2
1	7
1	3
1	4
1	6
1	1
\.


--
-- TOC entry 3653 (class 0 OID 62511)
-- Dependencies: 209
-- Data for Name: offeringhasrelatedfeature; Type: TABLE DATA; Schema: public; Owner: user
--

COPY offeringhasrelatedfeature (offeringid, relatedfeatureid) FROM stdin;
\.


--
-- TOC entry 3867 (class 0 OID 0)
-- Dependencies: 233
-- Name: offeringid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('offeringid_seq', 1, true);


--
-- TOC entry 3654 (class 0 OID 62516)
-- Dependencies: 210
-- Data for Name: parameter; Type: TABLE DATA; Schema: public; Owner: user
--

COPY parameter (parameterid, observationid, definition, title, value) FROM stdin;
\.


--
-- TOC entry 3868 (class 0 OID 0)
-- Dependencies: 234
-- Name: parameterid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('parameterid_seq', 1, false);


--
-- TOC entry 3869 (class 0 OID 0)
-- Dependencies: 235
-- Name: procdescformatid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('procdescformatid_seq', 1, true);


--
-- TOC entry 3629 (class 0 OID 62343)
-- Dependencies: 185
-- Data for Name: procedure; Type: TABLE DATA; Schema: public; Owner: user
--

COPY procedure (procedureid, hibernatediscriminator, proceduredescriptionformatid, identifier, codespace, name, codespacename, description, deleted, disabled, descriptionfile, referenceflag) FROM stdin;
1	T	1	52NorthWS1	\N	\N	\N	\N	F	F	\N	F
\.


--
-- TOC entry 3655 (class 0 OID 62524)
-- Dependencies: 211
-- Data for Name: proceduredescriptionformat; Type: TABLE DATA; Schema: public; Owner: user
--

COPY proceduredescriptionformat (proceduredescriptionformatid, proceduredescriptionformat) FROM stdin;
1	http://www.opengis.net/sensorML/1.0.1
\.


--
-- TOC entry 3870 (class 0 OID 0)
-- Dependencies: 236
-- Name: procedureid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('procedureid_seq', 1, true);


--
-- TOC entry 3656 (class 0 OID 62529)
-- Dependencies: 212
-- Data for Name: relatedfeature; Type: TABLE DATA; Schema: public; Owner: user
--

COPY relatedfeature (relatedfeatureid, featureofinterestid) FROM stdin;
\.


--
-- TOC entry 3657 (class 0 OID 62534)
-- Dependencies: 213
-- Data for Name: relatedfeaturehasrole; Type: TABLE DATA; Schema: public; Owner: user
--

COPY relatedfeaturehasrole (relatedfeatureid, relatedfeatureroleid) FROM stdin;
\.


--
-- TOC entry 3871 (class 0 OID 0)
-- Dependencies: 237
-- Name: relatedfeatureid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('relatedfeatureid_seq', 1, false);


--
-- TOC entry 3658 (class 0 OID 62539)
-- Dependencies: 214
-- Data for Name: relatedfeaturerole; Type: TABLE DATA; Schema: public; Owner: user
--

COPY relatedfeaturerole (relatedfeatureroleid, relatedfeaturerole) FROM stdin;
\.


--
-- TOC entry 3872 (class 0 OID 0)
-- Dependencies: 238
-- Name: relatedfeatureroleid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('relatedfeatureroleid_seq', 1, false);


--
-- TOC entry 3659 (class 0 OID 62544)
-- Dependencies: 215
-- Data for Name: resulttemplate; Type: TABLE DATA; Schema: public; Owner: user
--

COPY resulttemplate (resulttemplateid, offeringid, observablepropertyid, procedureid, featureofinterestid, identifier, resultstructure, resultencoding) FROM stdin;
\.


--
-- TOC entry 3873 (class 0 OID 0)
-- Dependencies: 239
-- Name: resulttemplateid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('resulttemplateid_seq', 1, false);


--
-- TOC entry 3660 (class 0 OID 62552)
-- Dependencies: 216
-- Data for Name: sensorsystem; Type: TABLE DATA; Schema: public; Owner: user
--

COPY sensorsystem (parentsensorid, childsensorid) FROM stdin;
\.


--
-- TOC entry 3661 (class 0 OID 62557)
-- Dependencies: 217
-- Data for Name: series; Type: TABLE DATA; Schema: public; Owner: user
--

COPY series (seriesid, featureofinterestid, observablepropertyid, procedureid, offeringid, deleted, published, firsttimestamp, lasttimestamp, firstnumericvalue, lastnumericvalue, unitid) FROM stdin;
2	2	5	1	1	F	T	2017-03-01 01:15:00	2017-03-31 21:46:00	-1.80000000000000004	-0.800000000000000044	2
1	2	6	1	1	F	T	2017-03-01 01:15:00	2017-03-31 22:31:00	0	0	1
5	2	1	1	1	F	T	2017-03-01 03:15:00	2017-03-31 21:31:00	0	0	5
3	2	3	1	1	F	T	2017-03-01 00:30:00	2017-03-31 23:00:00	23.5	27.3000000000000007	3
6	2	7	1	1	F	T	2017-03-01 00:15:00	2017-03-31 22:16:00	99	97	6
7	2	2	1	1	F	T	2017-03-01 04:15:00	2017-03-31 23:45:00	135	330	7
4	2	4	1	1	F	T	2017-02-28 23:45:00	2017-03-31 22:15:00	986	995	4
\.


--
-- TOC entry 3874 (class 0 OID 0)
-- Dependencies: 240
-- Name: seriesid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('seriesid_seq', 7, true);


--
-- TOC entry 3301 (class 0 OID 43579)
-- Dependencies: 173
-- Data for Name: spatial_ref_sys; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY spatial_ref_sys  FROM stdin;
\.


--
-- TOC entry 3662 (class 0 OID 62566)
-- Dependencies: 218
-- Data for Name: swedataarrayvalue; Type: TABLE DATA; Schema: public; Owner: user
--

COPY swedataarrayvalue (observationid, value) FROM stdin;
\.


--
-- TOC entry 3663 (class 0 OID 62574)
-- Dependencies: 219
-- Data for Name: textvalue; Type: TABLE DATA; Schema: public; Owner: user
--

COPY textvalue (observationid, value) FROM stdin;
\.


--
-- TOC entry 3664 (class 0 OID 62582)
-- Dependencies: 220
-- Data for Name: unit; Type: TABLE DATA; Schema: public; Owner: user
--

COPY unit (unitid, unit) FROM stdin;
1	lx
2	Cel
3	m/s
4	hPa
5	mm
6	%
7	deg
\.


--
-- TOC entry 3875 (class 0 OID 0)
-- Dependencies: 241
-- Name: unitid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('unitid_seq', 7, true);


--
-- TOC entry 3665 (class 0 OID 62587)
-- Dependencies: 221
-- Data for Name: validproceduretime; Type: TABLE DATA; Schema: public; Owner: user
--

COPY validproceduretime (validproceduretimeid, procedureid, proceduredescriptionformatid, starttime, endtime, descriptionxml) FROM stdin;
1	1	1	2017-01-26 14:54:11.593	\N	<sml:SensorML xmlns:sml="http://www.opengis.net/sensorML/1.0.1" xmlns:sos="http://www.opengis.net/sos/1.0" version="1.0.1">\n  <sml:member>\n    <sml:System xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:gml="http://www.opengis.net/gml" xmlns:ows="http://www.opengeospatial.net/ows" xmlns:ogc="http://www.opengis.net/ogc" xmlns:om="http://www.opengis.net/om/1.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n      <sml:identification>\n        <sml:IdentifierList>\n          <sml:identifier name="uniqueID">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:uniqueID">\n              <sml:value>52NorthWS1</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="longName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:longName">\n              <sml:value>This is a WS2500 weather station setup at 52North, Münster in Germany. Moved to different locations for each OSGeo-Live release.</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="shortName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:shortName">\n              <sml:value>52North HWS 1</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="productName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:productName">\n              <sml:value>ELV Radio Weather Station WS 2500</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="modelNumber">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:modelNumber">\n              <sml:value>53759</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="manufacturer">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:manufacturer">\n              <sml:value>ELV Elektronik AG</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="operator">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:operator">\n              <sml:value>52North, Münster, Germany.</sml:value>\n            </sml:Term>\n          </sml:identifier>\n        </sml:IdentifierList>\n      </sml:identification>\n      <sml:classification>\n        <sml:ClassifierList>\n          <sml:classifier name="intendedApplication">\n            <sml:Term definition="urn:ogc:def:classifier:OGC:1.0:application">\n              <sml:value>weather</sml:value>\n            </sml:Term>\n          </sml:classifier>\n          <sml:classifier name="sensorType">\n            <sml:Term definition="urn:ogc:def:classifier:OGC:1.0:sensorType">\n              <sml:value>thermometer</sml:value>\n            </sml:Term>\n          </sml:classifier>\n          <sml:classifier name="sensorType">\n            <sml:Term definition="urn:ogc:def:classifier:OGC:1.0:sensorType">\n              <sml:value>rain gauge</sml:value>\n            </sml:Term>\n          </sml:classifier>\n          <sml:classifier name="sensorType">\n            <sml:Term definition="urn:ogc:def:classifier:OGC:1.0:sensorType">\n              <sml:value>barometer</sml:value>\n            </sml:Term>\n          </sml:classifier>\n          <sml:classifier name="sensorType">\n            <sml:Term definition="urn:ogc:def:classifier:OGC:1.0:sensorType">\n              <sml:value>anemometer</sml:value>\n            </sml:Term>\n          </sml:classifier>\n          <sml:classifier name="sensorType">\n            <sml:Term definition="urn:ogc:def:classifier:OGC:1.0:sensorType">\n              <sml:value>Luminance Sensor</sml:value>\n            </sml:Term>\n          </sml:classifier>\n        </sml:ClassifierList>\n      </sml:classification>\n      <sml:capabilities>\n        <swe:SimpleDataRecord definition="urn:ogc:def:property:status">\n          <!--station is collecting data-->\n          <swe:field name="status">\n            <swe:Boolean>\n              <swe:value>false</swe:value>\n            </swe:Boolean>\n          </swe:field>\n          <swe:field name="mobile">\n            <swe:Boolean>\n              <swe:value>false</swe:value>\n            </swe:Boolean>\n          </swe:field>\n          <swe:field name="measuringInterval">\n            <swe:Quantity definition="urn:ogc:def:property:OGC:1.0:measuringInterval">\n              <gml:description>The measuring interval of the weather station.</gml:description>\n              <swe:uom code="min" xlink:href="urn:x-ogc:def:uom:OGC:min"/>\n              <swe:value>3</swe:value>\n            </swe:Quantity>\n          </swe:field>\n          <swe:field name="transmissionFrequency">\n            <swe:Quantity definition="urn:ogc:def:property:OGC:1.0:transmissionFrequency">\n              <gml:description>The transmission frequency of the weather station.</gml:description>\n              <swe:uom code="MHz" xlink:href="urn:x-ogc:def:uom:OGC:MHz"/>\n              <swe:value>433.92</swe:value>\n            </swe:Quantity>\n          </swe:field>\n          <swe:field name="powerSupply">\n            <swe:Text definition="urn:ogc:def:property:OGC:1.0:powerSupply">\n              <gml:description>The power supply of the weather station.</gml:description>\n              <swe:value>4 Baby cells 1.5V = 7,5 V / 500 mA</swe:value>\n            </swe:Text>\n          </swe:field>\n        </swe:SimpleDataRecord>\n      </sml:capabilities>\n      <sml:contact>\n        <sml:ResponsibleParty gml:id="contact">\n          <sml:individualName>Jürrens, Eike Hinderk</sml:individualName>\n          <sml:organizationName>52North, Münster, Germany.</sml:organizationName>\n          <sml:contactInfo>\n            <sml:phone>\n              <sml:voice>+49 251 39 63 71 33</sml:voice>\n              <sml:facsimile>+49 251 39 63 71 11</sml:facsimile>\n            </sml:phone>\n            <sml:address>\n              <sml:deliveryPoint>Martin-Luther-King-Weg 24</sml:deliveryPoint>\n              <sml:city>Münster</sml:city>\n              <sml:administrativeArea>North Rhine-Westfalia</sml:administrativeArea>\n              <sml:postalCode>49151</sml:postalCode>\n              <sml:country>Germany</sml:country>\n              <sml:electronicMailAddress>e.h.juerrens@52north.org</sml:electronicMailAddress>\n            </sml:address>\n          </sml:contactInfo>\n        </sml:ResponsibleParty>\n      </sml:contact>\n      <sml:documentation xlink:role="urn:ogc:def:object:OGC:1.0:image">\n        <sml:Document>\n          <gml:description>photo of the weather station</gml:description>\n          <sml:format>image/jpg</sml:format>\n          <sml:onlineResource xlink:href="http://ifgi.uni-muenster.de/~e_juer01/WS2500.jpg"/>\n        </sml:Document>\n      </sml:documentation>\n      <sml:history>\n        <sml:EventList>\n          <sml:member name="deployDate">\n            <sml:Event>\n              <sml:date>2007-06-01</sml:date>\n              <gml:description>Event of deploying the weather station.</gml:description>\n              <sml:contact xlink:href="#contact"/>\n            </sml:Event>\n          </sml:member>\n        </sml:EventList>\n      </sml:history>\n      <sml:position name="stationPosition">\n        <swe:Position fixed="false" referenceFrame="urn:ogc:def:crs:EPSG::4326">\n          <swe:location>\n            <swe:Vector gml:id="STATION_LOCATION">\n              <swe:coordinate name="northing">\n                <swe:Quantity axisID="y">\n                  <swe:uom code="deg" xlink:href="urn:x-ogc:def:uom:OGC:deg"/>\n                  <swe:value>48.56683</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="easting">\n                <swe:Quantity axisID="x">\n                  <swe:uom code="deg" xlink:href="urn:x-ogc:def:uom:OGC:deg"/>\n                  <swe:value>13.45105</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="altitude">\n                <swe:Quantity axisID="z">\n                  <!--200-->\n                  <swe:uom code="m" xlink:href="urn:x-ogc:def:uom:OGC:m"/>\n                  <swe:value>320</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n            </swe:Vector>\n          </swe:location>\n        </swe:Position>\n      </sml:position>\n      <sml:inputs>\n        <sml:InputList>\n          <sml:input name="precipitation">\n            <swe:ObservableProperty definition="Precipitation"/>\n          </sml:input>\n          <sml:input name="atmosphericTemperature">\n            <swe:ObservableProperty definition="AirTemperature"/>\n          </sml:input>\n          <sml:input name="atmosphericPressure">\n            <swe:ObservableProperty definition="AtmosphericPressure"/>\n          </sml:input>\n          <sml:input name="wind">\n            <swe:ObservableProperty definition="Wind"/>\n          </sml:input>\n          <sml:input name="luminance">\n            <swe:ObservableProperty definition="Luminance"/>\n          </sml:input>\n          <sml:input name="humidity">\n            <swe:ObservableProperty definition="RelativeHumidity"/>\n          </sml:input>\n        </sml:InputList>\n      </sml:inputs>\n      <sml:outputs>\n        <sml:OutputList>\n          <sml:output name="precipitation">\n            <swe:Quantity definition="Precipitation1Hour">\n              <swe:uom code="mm" xlink:href="urn:x-ogc:def:uom:OGC:mm"/>\n            </swe:Quantity>\n          </sml:output>\n          <sml:output name="windDirection">\n            <swe:Quantity definition="WindDirection">\n              <swe:uom code="deg" xlink:href="urn:x-ogc:def:uom:OGC:deg"/>\n            </swe:Quantity>\n          </sml:output>\n          <sml:output name="windSpeed">\n            <swe:Quantity definition="WindSpeed">\n              <swe:uom code="m/s" xlink:href="urn:x-ogc:def:uom:OGC:m_s"/>\n            </swe:Quantity>\n          </sml:output>\n          <sml:output name="barometricPressure">\n            <swe:Quantity definition="BarometricPressure">\n              <swe:uom code="hPa" xlink:href="urn:x-ogc:def:uom:OGC:hPa"/>\n            </swe:Quantity>\n          </sml:output>\n          <sml:output name="temperature">\n            <swe:Quantity definition="Temperature">\n              <swe:uom code="Cel" xlink:href="urn:x-ogc:def:uom:OGC:degC"/>\n            </swe:Quantity>\n          </sml:output>\n          <sml:output name="luminance">\n            <swe:Quantity definition="Luminance">\n              <swe:uom code="lx" xlink:href="urn:x-ogc:def:uom:OGC:lx"/>\n            </swe:Quantity>\n          </sml:output>\n          <sml:output name="relativeHumidity">\n            <swe:Quantity definition="RelativeHumidity">\n              <swe:uom code="%" xlink:href="urn:x-ogc:def:uom:OGC:percent"/>\n            </swe:Quantity>\n          </sml:output>\n        </sml:OutputList>\n      </sml:outputs>\n      </sml:System>\n  </sml:member>\n</sml:SensorML>
\.


--
-- TOC entry 3876 (class 0 OID 0)
-- Dependencies: 242
-- Name: validproceduretimeid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('validproceduretimeid_seq', 1, true);


--
-- TOC entry 3329 (class 2606 OID 62361)
-- Name: blobvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY blobvalue
    ADD CONSTRAINT blobvalue_pkey PRIMARY KEY (observationid);


--
-- TOC entry 3331 (class 2606 OID 62368)
-- Name: booleanvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY booleanvalue
    ADD CONSTRAINT booleanvalue_pkey PRIMARY KEY (observationid);


--
-- TOC entry 3333 (class 2606 OID 62373)
-- Name: categoryvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY categoryvalue
    ADD CONSTRAINT categoryvalue_pkey PRIMARY KEY (observationid);


--
-- TOC entry 3335 (class 2606 OID 62378)
-- Name: codespace_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY codespace
    ADD CONSTRAINT codespace_pkey PRIMARY KEY (codespaceid);


--
-- TOC entry 3337 (class 2606 OID 62598)
-- Name: codespaceuk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY codespace
    ADD CONSTRAINT codespaceuk UNIQUE (codespace);


--
-- TOC entry 3339 (class 2606 OID 62383)
-- Name: compositephenomenon_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY compositephenomenon
    ADD CONSTRAINT compositephenomenon_pkey PRIMARY KEY (childobservablepropertyid, parentobservablepropertyid);


--
-- TOC entry 3341 (class 2606 OID 62388)
-- Name: countvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY countvalue
    ADD CONSTRAINT countvalue_pkey PRIMARY KEY (observationid);


--
-- TOC entry 3343 (class 2606 OID 62396)
-- Name: featureofinterest_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY featureofinterest
    ADD CONSTRAINT featureofinterest_pkey PRIMARY KEY (featureofinterestid);


--
-- TOC entry 3349 (class 2606 OID 62401)
-- Name: featureofinteresttype_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY featureofinteresttype
    ADD CONSTRAINT featureofinteresttype_pkey PRIMARY KEY (featureofinteresttypeid);


--
-- TOC entry 3353 (class 2606 OID 62406)
-- Name: featurerelation_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY featurerelation
    ADD CONSTRAINT featurerelation_pkey PRIMARY KEY (childfeatureid, parentfeatureid);


--
-- TOC entry 3351 (class 2606 OID 62604)
-- Name: featuretypeuk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY featureofinteresttype
    ADD CONSTRAINT featuretypeuk UNIQUE (featureofinteresttype);


--
-- TOC entry 3345 (class 2606 OID 62602)
-- Name: featureurl; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY featureofinterest
    ADD CONSTRAINT featureurl UNIQUE (url);


--
-- TOC entry 3347 (class 2606 OID 62600)
-- Name: foiidentifieruk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY featureofinterest
    ADD CONSTRAINT foiidentifieruk UNIQUE (identifier);


--
-- TOC entry 3355 (class 2606 OID 62414)
-- Name: geometryvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY geometryvalue
    ADD CONSTRAINT geometryvalue_pkey PRIMARY KEY (observationid);


--
-- TOC entry 3357 (class 2606 OID 62606)
-- Name: i18nfeatureidentity; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY i18nfeatureofinterest
    ADD CONSTRAINT i18nfeatureidentity UNIQUE (objectid, locale);


--
-- TOC entry 3360 (class 2606 OID 62422)
-- Name: i18nfeatureofinterest_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY i18nfeatureofinterest
    ADD CONSTRAINT i18nfeatureofinterest_pkey PRIMARY KEY (id);


--
-- TOC entry 3362 (class 2606 OID 62430)
-- Name: i18nobservableproperty_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY i18nobservableproperty
    ADD CONSTRAINT i18nobservableproperty_pkey PRIMARY KEY (id);


--
-- TOC entry 3364 (class 2606 OID 62609)
-- Name: i18nobspropidentity; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY i18nobservableproperty
    ADD CONSTRAINT i18nobspropidentity UNIQUE (objectid, locale);


--
-- TOC entry 3367 (class 2606 OID 62438)
-- Name: i18noffering_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY i18noffering
    ADD CONSTRAINT i18noffering_pkey PRIMARY KEY (id);


--
-- TOC entry 3369 (class 2606 OID 62612)
-- Name: i18nofferingidentity; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY i18noffering
    ADD CONSTRAINT i18nofferingidentity UNIQUE (objectid, locale);


--
-- TOC entry 3372 (class 2606 OID 62446)
-- Name: i18nprocedure_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY i18nprocedure
    ADD CONSTRAINT i18nprocedure_pkey PRIMARY KEY (id);


--
-- TOC entry 3374 (class 2606 OID 62615)
-- Name: i18nprocedureidentity; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY i18nprocedure
    ADD CONSTRAINT i18nprocedureidentity UNIQUE (objectid, locale);


--
-- TOC entry 3377 (class 2606 OID 62451)
-- Name: numericvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY numericvalue
    ADD CONSTRAINT numericvalue_pkey PRIMARY KEY (observationid);


--
-- TOC entry 3379 (class 2606 OID 62461)
-- Name: observableproperty_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY observableproperty
    ADD CONSTRAINT observableproperty_pkey PRIMARY KEY (observablepropertyid);


--
-- TOC entry 3383 (class 2606 OID 62471)
-- Name: observation_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY observation
    ADD CONSTRAINT observation_pkey PRIMARY KEY (observationid);


--
-- TOC entry 3394 (class 2606 OID 62480)
-- Name: observationconstellation_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY observationconstellation
    ADD CONSTRAINT observationconstellation_pkey PRIMARY KEY (observationconstellationid);


--
-- TOC entry 3398 (class 2606 OID 62485)
-- Name: observationhasoffering_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY observationhasoffering
    ADD CONSTRAINT observationhasoffering_pkey PRIMARY KEY (observationid, offeringid);


--
-- TOC entry 3385 (class 2606 OID 62620)
-- Name: observationidentity; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY observation
    ADD CONSTRAINT observationidentity UNIQUE (seriesid, phenomenontimestart, phenomenontimeend, resulttime);


--
-- TOC entry 3402 (class 2606 OID 62490)
-- Name: observationtype_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY observationtype
    ADD CONSTRAINT observationtype_pkey PRIMARY KEY (observationtypeid);


--
-- TOC entry 3404 (class 2606 OID 62633)
-- Name: observationtypeuk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY observationtype
    ADD CONSTRAINT observationtypeuk UNIQUE (observationtype);


--
-- TOC entry 3396 (class 2606 OID 62626)
-- Name: obsnconstellationidentity; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY observationconstellation
    ADD CONSTRAINT obsnconstellationidentity UNIQUE (observablepropertyid, procedureid, offeringid);


--
-- TOC entry 3381 (class 2606 OID 62618)
-- Name: obspropidentifieruk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY observableproperty
    ADD CONSTRAINT obspropidentifieruk UNIQUE (identifier);


--
-- TOC entry 3406 (class 2606 OID 62500)
-- Name: offering_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY offering
    ADD CONSTRAINT offering_pkey PRIMARY KEY (offeringid);


--
-- TOC entry 3410 (class 2606 OID 62505)
-- Name: offeringallowedfeaturetype_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY offeringallowedfeaturetype
    ADD CONSTRAINT offeringallowedfeaturetype_pkey PRIMARY KEY (offeringid, featureofinteresttypeid);


--
-- TOC entry 3412 (class 2606 OID 62510)
-- Name: offeringallowedobservationtype_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY offeringallowedobservationtype
    ADD CONSTRAINT offeringallowedobservationtype_pkey PRIMARY KEY (offeringid, observationtypeid);


--
-- TOC entry 3414 (class 2606 OID 62515)
-- Name: offeringhasrelatedfeature_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY offeringhasrelatedfeature
    ADD CONSTRAINT offeringhasrelatedfeature_pkey PRIMARY KEY (offeringid, relatedfeatureid);


--
-- TOC entry 3408 (class 2606 OID 62635)
-- Name: offidentifieruk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY offering
    ADD CONSTRAINT offidentifieruk UNIQUE (identifier);


--
-- TOC entry 3416 (class 2606 OID 62523)
-- Name: parameter_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY parameter
    ADD CONSTRAINT parameter_pkey PRIMARY KEY (parameterid);


--
-- TOC entry 3418 (class 2606 OID 62637)
-- Name: procdescformatuk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY proceduredescriptionformat
    ADD CONSTRAINT procdescformatuk UNIQUE (proceduredescriptionformat);


--
-- TOC entry 3325 (class 2606 OID 62356)
-- Name: procedure_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY procedure
    ADD CONSTRAINT procedure_pkey PRIMARY KEY (procedureid);


--
-- TOC entry 3420 (class 2606 OID 62528)
-- Name: proceduredescriptionformat_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY proceduredescriptionformat
    ADD CONSTRAINT proceduredescriptionformat_pkey PRIMARY KEY (proceduredescriptionformatid);


--
-- TOC entry 3327 (class 2606 OID 62596)
-- Name: procidentifieruk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY procedure
    ADD CONSTRAINT procidentifieruk UNIQUE (identifier);


--
-- TOC entry 3422 (class 2606 OID 62533)
-- Name: relatedfeature_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY relatedfeature
    ADD CONSTRAINT relatedfeature_pkey PRIMARY KEY (relatedfeatureid);


--
-- TOC entry 3424 (class 2606 OID 62538)
-- Name: relatedfeaturehasrole_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY relatedfeaturehasrole
    ADD CONSTRAINT relatedfeaturehasrole_pkey PRIMARY KEY (relatedfeatureid, relatedfeatureroleid);


--
-- TOC entry 3426 (class 2606 OID 62543)
-- Name: relatedfeaturerole_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY relatedfeaturerole
    ADD CONSTRAINT relatedfeaturerole_pkey PRIMARY KEY (relatedfeatureroleid);


--
-- TOC entry 3428 (class 2606 OID 62639)
-- Name: relfeatroleuk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY relatedfeaturerole
    ADD CONSTRAINT relfeatroleuk UNIQUE (relatedfeaturerole);


--
-- TOC entry 3432 (class 2606 OID 62551)
-- Name: resulttemplate_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY resulttemplate
    ADD CONSTRAINT resulttemplate_pkey PRIMARY KEY (resulttemplateid);


--
-- TOC entry 3436 (class 2606 OID 62556)
-- Name: sensorsystem_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY sensorsystem
    ADD CONSTRAINT sensorsystem_pkey PRIMARY KEY (childsensorid, parentsensorid);


--
-- TOC entry 3438 (class 2606 OID 62565)
-- Name: series_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY series
    ADD CONSTRAINT series_pkey PRIMARY KEY (seriesid);


--
-- TOC entry 3441 (class 2606 OID 62645)
-- Name: seriesidentity; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY series
    ADD CONSTRAINT seriesidentity UNIQUE (featureofinterestid, observablepropertyid, procedureid, offeringid);


--
-- TOC entry 3446 (class 2606 OID 62573)
-- Name: swedataarrayvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY swedataarrayvalue
    ADD CONSTRAINT swedataarrayvalue_pkey PRIMARY KEY (observationid);


--
-- TOC entry 3448 (class 2606 OID 62581)
-- Name: textvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY textvalue
    ADD CONSTRAINT textvalue_pkey PRIMARY KEY (observationid);


--
-- TOC entry 3450 (class 2606 OID 62586)
-- Name: unit_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY unit
    ADD CONSTRAINT unit_pkey PRIMARY KEY (unitid);


--
-- TOC entry 3452 (class 2606 OID 62651)
-- Name: unituk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY unit
    ADD CONSTRAINT unituk UNIQUE (unit);


--
-- TOC entry 3454 (class 2606 OID 62594)
-- Name: validproceduretime_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY validproceduretime
    ADD CONSTRAINT validproceduretime_pkey PRIMARY KEY (validproceduretimeid);


--
-- TOC entry 3358 (class 1259 OID 62607)
-- Name: i18nfeatureidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX i18nfeatureidx ON i18nfeatureofinterest USING btree (objectid);


--
-- TOC entry 3365 (class 1259 OID 62610)
-- Name: i18nobspropidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX i18nobspropidx ON i18nobservableproperty USING btree (objectid);


--
-- TOC entry 3370 (class 1259 OID 62613)
-- Name: i18nofferingidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX i18nofferingidx ON i18noffering USING btree (objectid);


--
-- TOC entry 3375 (class 1259 OID 62616)
-- Name: i18nprocedureidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX i18nprocedureidx ON i18nprocedure USING btree (objectid);


--
-- TOC entry 3390 (class 1259 OID 62627)
-- Name: obsconstobspropidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX obsconstobspropidx ON observationconstellation USING btree (observablepropertyid);


--
-- TOC entry 3391 (class 1259 OID 62629)
-- Name: obsconstofferingidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX obsconstofferingidx ON observationconstellation USING btree (offeringid);


--
-- TOC entry 3392 (class 1259 OID 62628)
-- Name: obsconstprocedureidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX obsconstprocedureidx ON observationconstellation USING btree (procedureid);


--
-- TOC entry 3399 (class 1259 OID 62630)
-- Name: obshasoffobservationidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX obshasoffobservationidx ON observationhasoffering USING btree (observationid);


--
-- TOC entry 3400 (class 1259 OID 62631)
-- Name: obshasoffofferingidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX obshasoffofferingidx ON observationhasoffering USING btree (offeringid);


--
-- TOC entry 3386 (class 1259 OID 62623)
-- Name: obsphentimeendidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX obsphentimeendidx ON observation USING btree (phenomenontimeend);


--
-- TOC entry 3387 (class 1259 OID 62622)
-- Name: obsphentimestartidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX obsphentimestartidx ON observation USING btree (phenomenontimestart);


--
-- TOC entry 3388 (class 1259 OID 62624)
-- Name: obsresulttimeidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX obsresulttimeidx ON observation USING btree (resulttime);


--
-- TOC entry 3389 (class 1259 OID 62621)
-- Name: obsseriesidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX obsseriesidx ON observation USING btree (seriesid);


--
-- TOC entry 3429 (class 1259 OID 62641)
-- Name: resulttempeobspropidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX resulttempeobspropidx ON resulttemplate USING btree (observablepropertyid);


--
-- TOC entry 3430 (class 1259 OID 62643)
-- Name: resulttempidentifieridx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX resulttempidentifieridx ON resulttemplate USING btree (identifier);


--
-- TOC entry 3433 (class 1259 OID 62640)
-- Name: resulttempofferingidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX resulttempofferingidx ON resulttemplate USING btree (offeringid);


--
-- TOC entry 3434 (class 1259 OID 62642)
-- Name: resulttempprocedureidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX resulttempprocedureidx ON resulttemplate USING btree (procedureid);


--
-- TOC entry 3439 (class 1259 OID 62646)
-- Name: seriesfeatureidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX seriesfeatureidx ON series USING btree (featureofinterestid);


--
-- TOC entry 3442 (class 1259 OID 62647)
-- Name: seriesobspropidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX seriesobspropidx ON series USING btree (observablepropertyid);


--
-- TOC entry 3443 (class 1259 OID 62649)
-- Name: seriesofferingidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX seriesofferingidx ON series USING btree (offeringid);


--
-- TOC entry 3444 (class 1259 OID 62648)
-- Name: seriesprocedureidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX seriesprocedureidx ON series USING btree (procedureid);


--
-- TOC entry 3455 (class 1259 OID 62653)
-- Name: validproceduretimeendtimeidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX validproceduretimeendtimeidx ON validproceduretime USING btree (endtime);


--
-- TOC entry 3456 (class 1259 OID 62652)
-- Name: validproceduretimestarttimeidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX validproceduretimestarttimeidx ON validproceduretime USING btree (starttime);


--
-- TOC entry 3467 (class 2606 OID 62704)
-- Name: featurecodespaceidentifierfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY featureofinterest
    ADD CONSTRAINT featurecodespaceidentifierfk FOREIGN KEY (codespace) REFERENCES codespace(codespaceid);


--
-- TOC entry 3468 (class 2606 OID 62709)
-- Name: featurecodespacenamefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY featureofinterest
    ADD CONSTRAINT featurecodespacenamefk FOREIGN KEY (codespacename) REFERENCES codespace(codespaceid);


--
-- TOC entry 3466 (class 2606 OID 62699)
-- Name: featurefeaturetypefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY featureofinterest
    ADD CONSTRAINT featurefeaturetypefk FOREIGN KEY (featureofinteresttypeid) REFERENCES featureofinteresttype(featureofinteresttypeid);


--
-- TOC entry 3469 (class 2606 OID 62714)
-- Name: featureofinterestchildfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY featurerelation
    ADD CONSTRAINT featureofinterestchildfk FOREIGN KEY (childfeatureid) REFERENCES featureofinterest(featureofinterestid);


--
-- TOC entry 3470 (class 2606 OID 62719)
-- Name: featureofinterestparentfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY featurerelation
    ADD CONSTRAINT featureofinterestparentfk FOREIGN KEY (parentfeatureid) REFERENCES featureofinterest(featureofinterestid);


--
-- TOC entry 3492 (class 2606 OID 62829)
-- Name: fk_6vvrdxvd406n48gkm706ow1pt; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY offeringallowedfeaturetype
    ADD CONSTRAINT fk_6vvrdxvd406n48gkm706ow1pt FOREIGN KEY (offeringid) REFERENCES offering(offeringid);


--
-- TOC entry 3499 (class 2606 OID 62864)
-- Name: fk_6ynwkk91xe8p1uibmjt98sog3; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY relatedfeaturehasrole
    ADD CONSTRAINT fk_6ynwkk91xe8p1uibmjt98sog3 FOREIGN KEY (relatedfeatureid) REFERENCES relatedfeature(relatedfeatureid);


--
-- TOC entry 3488 (class 2606 OID 62809)
-- Name: fk_9ex7hawh3dbplkllmw5w3kvej; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observationhasoffering
    ADD CONSTRAINT fk_9ex7hawh3dbplkllmw5w3kvej FOREIGN KEY (observationid) REFERENCES observation(observationid);


--
-- TOC entry 3494 (class 2606 OID 62839)
-- Name: fk_lkljeohulvu7cr26pduyp5bd0; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY offeringallowedobservationtype
    ADD CONSTRAINT fk_lkljeohulvu7cr26pduyp5bd0 FOREIGN KEY (offeringid) REFERENCES offering(offeringid);


--
-- TOC entry 3472 (class 2606 OID 62729)
-- Name: i18nfeaturefeaturefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY i18nfeatureofinterest
    ADD CONSTRAINT i18nfeaturefeaturefk FOREIGN KEY (objectid) REFERENCES featureofinterest(featureofinterestid);


--
-- TOC entry 3473 (class 2606 OID 62734)
-- Name: i18nobspropobspropfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY i18nobservableproperty
    ADD CONSTRAINT i18nobspropobspropfk FOREIGN KEY (objectid) REFERENCES observableproperty(observablepropertyid);


--
-- TOC entry 3474 (class 2606 OID 62739)
-- Name: i18nofferingofferingfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY i18noffering
    ADD CONSTRAINT i18nofferingofferingfk FOREIGN KEY (objectid) REFERENCES offering(offeringid);


--
-- TOC entry 3475 (class 2606 OID 62744)
-- Name: i18nprocedureprocedurefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY i18nprocedure
    ADD CONSTRAINT i18nprocedureprocedurefk FOREIGN KEY (objectid) REFERENCES procedure(procedureid);


--
-- TOC entry 3480 (class 2606 OID 62769)
-- Name: obscodespaceidentifierfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observation
    ADD CONSTRAINT obscodespaceidentifierfk FOREIGN KEY (codespace) REFERENCES codespace(codespaceid);


--
-- TOC entry 3481 (class 2606 OID 62774)
-- Name: obscodespacenamefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observation
    ADD CONSTRAINT obscodespacenamefk FOREIGN KEY (codespacename) REFERENCES codespace(codespaceid);


--
-- TOC entry 3485 (class 2606 OID 62794)
-- Name: obsconstobservationiypefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observationconstellation
    ADD CONSTRAINT obsconstobservationiypefk FOREIGN KEY (observationtypeid) REFERENCES observationtype(observationtypeid);


--
-- TOC entry 3483 (class 2606 OID 62784)
-- Name: obsconstobspropfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observationconstellation
    ADD CONSTRAINT obsconstobspropfk FOREIGN KEY (observablepropertyid) REFERENCES observableproperty(observablepropertyid);


--
-- TOC entry 3486 (class 2606 OID 62799)
-- Name: obsconstofferingfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observationconstellation
    ADD CONSTRAINT obsconstofferingfk FOREIGN KEY (offeringid) REFERENCES offering(offeringid);


--
-- TOC entry 3463 (class 2606 OID 62684)
-- Name: observablepropertychildfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY compositephenomenon
    ADD CONSTRAINT observablepropertychildfk FOREIGN KEY (childobservablepropertyid) REFERENCES observableproperty(observablepropertyid);


--
-- TOC entry 3464 (class 2606 OID 62689)
-- Name: observablepropertyparentfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY compositephenomenon
    ADD CONSTRAINT observablepropertyparentfk FOREIGN KEY (parentobservablepropertyid) REFERENCES observableproperty(observablepropertyid);


--
-- TOC entry 3460 (class 2606 OID 62669)
-- Name: observationblobvaluefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY blobvalue
    ADD CONSTRAINT observationblobvaluefk FOREIGN KEY (observationid) REFERENCES observation(observationid);


--
-- TOC entry 3461 (class 2606 OID 62674)
-- Name: observationbooleanvaluefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY booleanvalue
    ADD CONSTRAINT observationbooleanvaluefk FOREIGN KEY (observationid) REFERENCES observation(observationid);


--
-- TOC entry 3462 (class 2606 OID 62679)
-- Name: observationcategoryvaluefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY categoryvalue
    ADD CONSTRAINT observationcategoryvaluefk FOREIGN KEY (observationid) REFERENCES observation(observationid);


--
-- TOC entry 3465 (class 2606 OID 62694)
-- Name: observationcountvaluefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY countvalue
    ADD CONSTRAINT observationcountvaluefk FOREIGN KEY (observationid) REFERENCES observation(observationid);


--
-- TOC entry 3471 (class 2606 OID 62724)
-- Name: observationgeometryvaluefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY geometryvalue
    ADD CONSTRAINT observationgeometryvaluefk FOREIGN KEY (observationid) REFERENCES observation(observationid);


--
-- TOC entry 3476 (class 2606 OID 62749)
-- Name: observationnumericvaluefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY numericvalue
    ADD CONSTRAINT observationnumericvaluefk FOREIGN KEY (observationid) REFERENCES observation(observationid);


--
-- TOC entry 3487 (class 2606 OID 62804)
-- Name: observationofferingfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observationhasoffering
    ADD CONSTRAINT observationofferingfk FOREIGN KEY (offeringid) REFERENCES offering(offeringid);


--
-- TOC entry 3479 (class 2606 OID 62764)
-- Name: observationseriesfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observation
    ADD CONSTRAINT observationseriesfk FOREIGN KEY (seriesid) REFERENCES series(seriesid);


--
-- TOC entry 3511 (class 2606 OID 62924)
-- Name: observationswedataarrayvaluefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY swedataarrayvalue
    ADD CONSTRAINT observationswedataarrayvaluefk FOREIGN KEY (observationid) REFERENCES observation(observationid);


--
-- TOC entry 3512 (class 2606 OID 62929)
-- Name: observationtextvaluefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY textvalue
    ADD CONSTRAINT observationtextvaluefk FOREIGN KEY (observationid) REFERENCES observation(observationid);


--
-- TOC entry 3482 (class 2606 OID 62779)
-- Name: observationunitfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observation
    ADD CONSTRAINT observationunitfk FOREIGN KEY (unitid) REFERENCES unit(unitid);


--
-- TOC entry 3484 (class 2606 OID 62789)
-- Name: obsnconstprocedurefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observationconstellation
    ADD CONSTRAINT obsnconstprocedurefk FOREIGN KEY (procedureid) REFERENCES procedure(procedureid);


--
-- TOC entry 3477 (class 2606 OID 62754)
-- Name: obspropcodespaceidentifierfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observableproperty
    ADD CONSTRAINT obspropcodespaceidentifierfk FOREIGN KEY (codespace) REFERENCES codespace(codespaceid);


--
-- TOC entry 3478 (class 2606 OID 62759)
-- Name: obspropcodespacenamefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observableproperty
    ADD CONSTRAINT obspropcodespacenamefk FOREIGN KEY (codespacename) REFERENCES codespace(codespaceid);


--
-- TOC entry 3489 (class 2606 OID 62814)
-- Name: offcodespaceidentifierfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY offering
    ADD CONSTRAINT offcodespaceidentifierfk FOREIGN KEY (codespace) REFERENCES codespace(codespaceid);


--
-- TOC entry 3490 (class 2606 OID 62819)
-- Name: offcodespacenamefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY offering
    ADD CONSTRAINT offcodespacenamefk FOREIGN KEY (codespacename) REFERENCES codespace(codespaceid);


--
-- TOC entry 3491 (class 2606 OID 62824)
-- Name: offeringfeaturetypefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY offeringallowedfeaturetype
    ADD CONSTRAINT offeringfeaturetypefk FOREIGN KEY (featureofinteresttypeid) REFERENCES featureofinteresttype(featureofinteresttypeid);


--
-- TOC entry 3493 (class 2606 OID 62834)
-- Name: offeringobservationtypefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY offeringallowedobservationtype
    ADD CONSTRAINT offeringobservationtypefk FOREIGN KEY (observationtypeid) REFERENCES observationtype(observationtypeid);


--
-- TOC entry 3495 (class 2606 OID 62844)
-- Name: offeringrelatedfeaturefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY offeringhasrelatedfeature
    ADD CONSTRAINT offeringrelatedfeaturefk FOREIGN KEY (relatedfeatureid) REFERENCES relatedfeature(relatedfeatureid);


--
-- TOC entry 3458 (class 2606 OID 62659)
-- Name: proccodespaceidentifierfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY procedure
    ADD CONSTRAINT proccodespaceidentifierfk FOREIGN KEY (codespace) REFERENCES codespace(codespaceid);


--
-- TOC entry 3459 (class 2606 OID 62664)
-- Name: proccodespacenamefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY procedure
    ADD CONSTRAINT proccodespacenamefk FOREIGN KEY (codespacename) REFERENCES codespace(codespaceid);


--
-- TOC entry 3504 (class 2606 OID 62889)
-- Name: procedurechildfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY sensorsystem
    ADD CONSTRAINT procedurechildfk FOREIGN KEY (childsensorid) REFERENCES procedure(procedureid);


--
-- TOC entry 3505 (class 2606 OID 62894)
-- Name: procedureparenffk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY sensorsystem
    ADD CONSTRAINT procedureparenffk FOREIGN KEY (parentsensorid) REFERENCES procedure(procedureid);


--
-- TOC entry 3457 (class 2606 OID 62654)
-- Name: procprocdescformatfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY procedure
    ADD CONSTRAINT procprocdescformatfk FOREIGN KEY (proceduredescriptionformatid) REFERENCES proceduredescriptionformat(proceduredescriptionformatid);


--
-- TOC entry 3498 (class 2606 OID 62859)
-- Name: relatedfeatrelatedfeatrolefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY relatedfeaturehasrole
    ADD CONSTRAINT relatedfeatrelatedfeatrolefk FOREIGN KEY (relatedfeatureroleid) REFERENCES relatedfeaturerole(relatedfeatureroleid);


--
-- TOC entry 3497 (class 2606 OID 62854)
-- Name: relatedfeaturefeaturefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY relatedfeature
    ADD CONSTRAINT relatedfeaturefeaturefk FOREIGN KEY (featureofinterestid) REFERENCES featureofinterest(featureofinterestid);


--
-- TOC entry 3496 (class 2606 OID 62849)
-- Name: relatedfeatureofferingfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY offeringhasrelatedfeature
    ADD CONSTRAINT relatedfeatureofferingfk FOREIGN KEY (offeringid) REFERENCES offering(offeringid);


--
-- TOC entry 3503 (class 2606 OID 62884)
-- Name: resulttemplatefeatureidx; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY resulttemplate
    ADD CONSTRAINT resulttemplatefeatureidx FOREIGN KEY (featureofinterestid) REFERENCES featureofinterest(featureofinterestid);


--
-- TOC entry 3501 (class 2606 OID 62874)
-- Name: resulttemplateobspropfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY resulttemplate
    ADD CONSTRAINT resulttemplateobspropfk FOREIGN KEY (observablepropertyid) REFERENCES observableproperty(observablepropertyid);


--
-- TOC entry 3500 (class 2606 OID 62869)
-- Name: resulttemplateofferingidx; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY resulttemplate
    ADD CONSTRAINT resulttemplateofferingidx FOREIGN KEY (offeringid) REFERENCES offering(offeringid);


--
-- TOC entry 3502 (class 2606 OID 62879)
-- Name: resulttemplateprocedurefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY resulttemplate
    ADD CONSTRAINT resulttemplateprocedurefk FOREIGN KEY (procedureid) REFERENCES procedure(procedureid);


--
-- TOC entry 3506 (class 2606 OID 62899)
-- Name: seriesfeaturefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY series
    ADD CONSTRAINT seriesfeaturefk FOREIGN KEY (featureofinterestid) REFERENCES featureofinterest(featureofinterestid);


--
-- TOC entry 3507 (class 2606 OID 62904)
-- Name: seriesobpropfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY series
    ADD CONSTRAINT seriesobpropfk FOREIGN KEY (observablepropertyid) REFERENCES observableproperty(observablepropertyid);


--
-- TOC entry 3509 (class 2606 OID 62914)
-- Name: seriesofferingfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY series
    ADD CONSTRAINT seriesofferingfk FOREIGN KEY (offeringid) REFERENCES offering(offeringid);


--
-- TOC entry 3508 (class 2606 OID 62909)
-- Name: seriesprocedurefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY series
    ADD CONSTRAINT seriesprocedurefk FOREIGN KEY (procedureid) REFERENCES procedure(procedureid);


--
-- TOC entry 3510 (class 2606 OID 62919)
-- Name: seriesunitfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY series
    ADD CONSTRAINT seriesunitfk FOREIGN KEY (unitid) REFERENCES unit(unitid);


--
-- TOC entry 3513 (class 2606 OID 62934)
-- Name: validproceduretimeprocedurefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY validproceduretime
    ADD CONSTRAINT validproceduretimeprocedurefk FOREIGN KEY (procedureid) REFERENCES procedure(procedureid);


--
-- TOC entry 3514 (class 2606 OID 62939)
-- Name: validprocprocdescformatfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY validproceduretime
    ADD CONSTRAINT validprocprocdescformatfk FOREIGN KEY (proceduredescriptionformatid) REFERENCES proceduredescriptionformat(proceduredescriptionformatid);


--
-- TOC entry 3693 (class 0 OID 0)
-- Dependencies: 8
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2017-01-26 16:25:13 CET

--
-- PostgreSQL database dump complete
--

--
-- schema updates: SOS 4.3.x -> 4.4
--
--
-- Add and update other columns
--
alter table public."procedure" add column typeOf int8;
alter table public."procedure" add column isType char(1) default 'F' check(isType in ('T','F'));
alter table public."procedure" add column isAggregation char(1) default 'T' check(isAggregation in ('T','F'));
alter table public."procedure" add column mobile char(1) default 'F' check(mobile in ('T','F'));
alter table public."procedure" add column insitu char(1) default 'T' check(insitu in ('T','F'));
create table public.booleanfeatparamvalue (parameterId int8 not null, value char(1), primary key (parameterId), check (value in ('T','F')));
create table public.booleanparametervalue (parameterId int8 not null, value char(1), primary key (parameterId), check (value in ('T','F')));
create table public.categoryfeatparamvalue (parameterId int8 not null, value varchar(255), unitId int8, primary key (parameterId));
create table public.categoryparametervalue (parameterId int8 not null, value varchar(255), unitId int8, primary key (parameterId));
alter table public.categoryvalue add column identifier varchar(255);
alter table public.categoryvalue add column name varchar(255);
alter table public.categoryvalue add column description varchar(255);
create table public.complexvalue (observationId int8 not null, primary key (observationId));
create table public.compositeobservation (observationId int8 not null, childObservationId int8 not null, primary key (observationId, childObservationId));
create table public.countfeatparamvalue (parameterId int8 not null, value int4, primary key (parameterId));
create table public.countparametervalue (parameterId int8 not null, value int4, primary key (parameterId));
create table public.featureparameter (parameterId int8 not null, featureOfInterestId int8 not null, name varchar(255) not null, primary key (parameterId));
create table public.numericfeatparamvalue (parameterId int8 not null, value float8, unitId int8, primary key (parameterId));
create table public.numericparametervalue (parameterId int8 not null, value float8, unitId int8, primary key (parameterId));
alter table public.observableproperty add column hiddenChild char(1) default 'F' not null check(hiddenChild in ('T','F'));
alter table public.observation add column child char(1) default 'F' not null check(child in ('T','F'));
alter table public.observation add column parent char(1) default 'F' not null check(parent in ('T','F'));
alter table public.observationconstellation add column disabled char(1) default 'F' not null check(disabled in ('T','F'));
create table public.offeringrelation (parentOfferingId int8 not null, childOfferingId int8 not null, primary key (childOfferingId, parentOfferingId));
alter table public.parameter add column name varchar(255) not null;
create table public.profileobservation (observationId int8 not null, childObservationId int8 not null, primary key (observationId, childObservationId));
create table public.profilevalue (observationId int8 not null, fromlevel float8, tolevel float8, levelunitid int8, primary key (observationId));
create table public.relatedobservation (relatedObservationId int8 not null, observationId int8, relatedObservation int8, role varchar(255), relatedUrl varchar(255), primary key (relatedObservationId));
alter table public.series add column hiddenChild char(1) default 'F' not null check(hiddenChild in ('T','F'));
alter table public.series add column identifier varchar(255);
alter table public.series add column codespace int8;
alter table public.series add column name varchar(255);
alter table public.series add column codespaceName int8;
alter table public.series add column description varchar(255);
alter table public.series add column seriesType varchar(255);
create table public.textfeatparamvalue (parameterId int8 not null, value varchar(255), primary key (parameterId));
create table public.textparametervalue (parameterId int8 not null, value varchar(255), primary key (parameterId));
alter table public.textvalue add column identifier varchar(255);
alter table public.textvalue add column name varchar(255);
alter table public.textvalue add column description varchar(255);
alter table public.unit add column name varchar(255);
alter table public.unit add column link varchar(255);
create table public.xmlfeatparamvalue (parameterId int8 not null, value text, primary key (parameterId));
create table public.xmlparametervalue (parameterId int8 not null, value text, primary key (parameterId));
create index booleanFeatParamIdx on public.booleanfeatparamvalue (value);
create index booleanParamIdx on public.booleanparametervalue (value);
create index categoryFeatParamIdx on public.categoryfeatparamvalue (value);
create index categoryParamIdx on public.categoryparametervalue (value);
create index countFeatParamIdx on public.countfeatparamvalue (value);
create index countParamIdx on public.countparametervalue (value);
create index featureGeomIdx on public.featureofinterest USING GIST (geom);
create index featParamNameIdx on public.featureparameter (name);
create index quantityFeatParamIdx on public.numericfeatparamvalue (value);
create index quantityParamIdx on public.numericparametervalue (value);
create index samplingGeomIdx on public.observation USING GIST (samplingGeometry);
create index paramNameIdx on public.parameter (name);
create index relatedObsObsIdx on public.relatedobservation (observationId);
alter table public.series add constraint seriesIdentifierUK unique (identifier);
create index textFeatParamIdx on public.textfeatparamvalue (value);
create index textParamIdx on public.textparametervalue (value);
alter table public."procedure" add constraint typeOfFk foreign key (typeOf) references public."procedure";
alter table public.booleanfeatparamvalue add constraint featParamBooleanValueFk foreign key (parameterId) references public.featureparameter;
alter table public.booleanparametervalue add constraint parameterBooleanValueFk foreign key (parameterId) references public.parameter;
alter table public.categoryfeatparamvalue add constraint featParamCategoryValueFk foreign key (parameterId) references public.featureparameter;
alter table public.categoryfeatparamvalue add constraint catfeatparamvalueUnitFk foreign key (unitId) references public.unit;
alter table public.categoryparametervalue add constraint parameterCategoryValueFk foreign key (parameterId) references public.parameter;
alter table public.categoryparametervalue add constraint catParamValueUnitFk foreign key (unitId) references public.unit;
alter table public.complexvalue add constraint observationComplexValueFk foreign key (observationId) references public.observation;
alter table public.compositeobservation add constraint observationChildFk foreign key (childObservationId) references public.observation;
alter table public.compositeobservation add constraint observationParentFK foreign key (observationId) references public.complexvalue;
alter table public.countfeatparamvalue add constraint featParamCountValueFk foreign key (parameterId) references public.featureparameter;
alter table public.countparametervalue add constraint parameterCountValueFk foreign key (parameterId) references public.parameter;
alter table public.featureparameter add constraint FK_4ps6yv41rwnbu3q0let2v7 foreign key (featureOfInterestId) references public.featureofinterest;
alter table public.numericfeatparamvalue add constraint featParamNumericValueFk foreign key (parameterId) references public.featureparameter;
alter table public.numericfeatparamvalue add constraint quanfeatparamvalueUnitFk foreign key (unitId) references public.unit;
alter table public.numericparametervalue add constraint parameterNumericValueFk foreign key (parameterId) references public.parameter;
alter table public.numericparametervalue add constraint quanParamValueUnitFk foreign key (unitId) references public.unit;
alter table public.offeringrelation add constraint offeringChildFk foreign key (childOfferingId) references public.offering;
alter table public.offeringrelation add constraint offeringParenfFk foreign key (parentOfferingId) references public.offering;
alter table public.parameter add constraint FK_3v5iovcndi9w0hgh827hcvivw foreign key (observationId) references public.observation;
alter table public.profileobservation add constraint profileObsChildFk foreign key (childObservationId) references public.observation;
alter table public.profileobservation add constraint profileObsParentFK foreign key (observationId) references public.profilevalue;
alter table public.profilevalue add constraint observationProfileValueFk foreign key (observationId) references public.observation;
alter table public.profilevalue add constraint profileUnitFk foreign key (levelunitid) references public.unit;
alter table public.relatedobservation add constraint FK_g0f0mpuxn3co65uwud4pwxh4q foreign key (observationId) references public.observation;
alter table public.relatedobservation add constraint FK_m4nuof4x6w253biuu1r6ttnqc foreign key (relatedObservation) references public.observation;
alter table public.series add constraint seriesCodespaceIdentifierFk foreign key (codespace) references public.codespace;
alter table public.series add constraint seriesCodespaceNameFk foreign key (codespaceName) references public.codespace;
alter table public.textfeatparamvalue add constraint featParamTextValueFk foreign key (parameterId) references public.featureparameter;
alter table public.textparametervalue add constraint parameterTextValueFk foreign key (parameterId) references public.parameter;
alter table public.xmlfeatparamvalue add constraint featParamXmlValueFk foreign key (parameterId) references public.featureparameter;
alter table public.xmlparametervalue add constraint parameterXmlValueFk foreign key (parameterId) references public.parameter;
create sequence public.relatedObservationId_seq;

-- This update script fills the offeringid values in the series table.
-- !!! This script only works in the case that a each observation belongs to only one offering!!!
-- If the offeringid column is still filled with values, this statement should be omitted
update public.series ser set offeringId = (Select distinct off.offeringId from public.offering off, public.series ser, public.observation o, public.observationhasoffering ohof where ser.seriesid = o.seriesid AND o.observationid = ohof.observationid AND ohof.offeringId = off.offeringId);

-- Update offeringid column to NOT NULL
alter table public.series alter column offeringId set not null;

-- Set seriestype from value tables
update public.series set seriestype = 'quantity' where seriesid in (select distinct o.seriesid from public.observation o inner join public.numericvalue v on o.observationid = v.observationid);
update public.series set seriestype = 'count' where seriesid in (select distinct o.seriesid from public.observation o inner join public.countvalue v on o.observationid = v.observationid);
update public.series set seriestype = 'text' where seriesid in (select distinct o.seriesid from public.observation o inner join public.textvalue v on o.observationid = v.observationid);
update public.series set seriestype = 'category' where seriesid in (select distinct o.seriesid from public.observation o inner join public.categoryvalue v on o.observationid = v.observationid);
update public.series set seriestype = 'boolean' where seriesid in (select distinct o.seriesid from public.observation o inner join public.booleanvalue v on o.observationid = v.observationid);
