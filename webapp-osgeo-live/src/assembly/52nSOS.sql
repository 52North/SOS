--
-- Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

-- Dumped from database version 9.3.12
-- Dumped by pg_dump version 9.3.12
-- Started on 2016-05-24 15:55:25 CEST

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
-- TOC entry 186 (class 1259 OID 59992)
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
-- TOC entry 187 (class 1259 OID 59997)
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
-- TOC entry 188 (class 1259 OID 60004)
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
-- TOC entry 189 (class 1259 OID 60009)
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
-- TOC entry 222 (class 1259 OID 60575)
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
-- TOC entry 190 (class 1259 OID 60014)
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
-- TOC entry 191 (class 1259 OID 60019)
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
-- TOC entry 192 (class 1259 OID 60024)
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
-- TOC entry 223 (class 1259 OID 60577)
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
-- TOC entry 193 (class 1259 OID 60032)
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
-- TOC entry 224 (class 1259 OID 60579)
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
-- TOC entry 194 (class 1259 OID 60037)
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
-- TOC entry 195 (class 1259 OID 60042)
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
-- TOC entry 196 (class 1259 OID 60050)
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
-- TOC entry 228 (class 1259 OID 60587)
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
-- TOC entry 197 (class 1259 OID 60058)
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
-- TOC entry 225 (class 1259 OID 60581)
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
-- TOC entry 198 (class 1259 OID 60066)
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
-- TOC entry 226 (class 1259 OID 60583)
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
-- TOC entry 199 (class 1259 OID 60074)
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
-- TOC entry 227 (class 1259 OID 60585)
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
-- TOC entry 200 (class 1259 OID 60082)
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
-- TOC entry 201 (class 1259 OID 60087)
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
-- TOC entry 229 (class 1259 OID 60589)
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
-- TOC entry 202 (class 1259 OID 60097)
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
-- TOC entry 203 (class 1259 OID 60107)
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
-- TOC entry 230 (class 1259 OID 60591)
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
-- TOC entry 204 (class 1259 OID 60116)
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
-- TOC entry 231 (class 1259 OID 60593)
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
-- TOC entry 205 (class 1259 OID 60121)
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
-- TOC entry 232 (class 1259 OID 60595)
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
-- TOC entry 206 (class 1259 OID 60126)
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
-- TOC entry 207 (class 1259 OID 60136)
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
-- TOC entry 208 (class 1259 OID 60141)
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
-- TOC entry 209 (class 1259 OID 60146)
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
-- TOC entry 233 (class 1259 OID 60597)
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
-- TOC entry 210 (class 1259 OID 60151)
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
-- TOC entry 234 (class 1259 OID 60599)
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
-- TOC entry 235 (class 1259 OID 60601)
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
-- TOC entry 185 (class 1259 OID 59978)
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
-- TOC entry 211 (class 1259 OID 60159)
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
-- TOC entry 236 (class 1259 OID 60603)
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
-- TOC entry 212 (class 1259 OID 60164)
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
-- TOC entry 213 (class 1259 OID 60169)
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
-- TOC entry 237 (class 1259 OID 60605)
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
-- TOC entry 214 (class 1259 OID 60174)
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
-- TOC entry 238 (class 1259 OID 60607)
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
-- TOC entry 215 (class 1259 OID 60179)
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
-- TOC entry 239 (class 1259 OID 60609)
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
-- TOC entry 216 (class 1259 OID 60187)
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
-- TOC entry 217 (class 1259 OID 60192)
-- Name: series; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE series (
    seriesid bigint NOT NULL,
    featureofinterestid bigint NOT NULL,
    observablepropertyid bigint NOT NULL,
    procedureid bigint NOT NULL,
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
-- Name: COLUMN series.deleted; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN series.deleted IS 'Flag to indicate that this series is deleted or not. Set if the related procedure is deleted via DeleteSensor operation (OGC SWES 2.0 - DeleteSensor operation)';


--
-- TOC entry 3837 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN series.published; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN series.published IS 'Flag to indicate that this series is published or not. A not published series is not contained in GetObservation and GetDataAvailability responses';


--
-- TOC entry 3838 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN series.firsttimestamp; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN series.firsttimestamp IS 'The time stamp of the first (temporal) observation associated to this series';


--
-- TOC entry 3839 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN series.lasttimestamp; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN series.lasttimestamp IS 'The time stamp of the last (temporal) observation associated to this series';


--
-- TOC entry 3840 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN series.firstnumericvalue; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN series.firstnumericvalue IS 'The value of the first (temporal) observation associated to this series';


--
-- TOC entry 3841 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN series.lastnumericvalue; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN series.lastnumericvalue IS 'The value of the last (temporal) observation associated to this series';


--
-- TOC entry 3842 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN series.unitid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN series.unitid IS 'Foreign Key (FK) to the related unit of the first/last numeric values . Contains "unit".unitid';


--
-- TOC entry 240 (class 1259 OID 60611)
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
-- TOC entry 218 (class 1259 OID 60201)
-- Name: swedataarrayvalue; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE swedataarrayvalue (
    observationid bigint NOT NULL,
    value text
);


ALTER TABLE public.swedataarrayvalue OWNER TO "user";

--
-- TOC entry 3843 (class 0 OID 0)
-- Dependencies: 218
-- Name: TABLE swedataarrayvalue; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE swedataarrayvalue IS 'Value table for SweDataArray observation';


--
-- TOC entry 219 (class 1259 OID 60209)
-- Name: textvalue; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE textvalue (
    observationid bigint NOT NULL,
    value text
);


ALTER TABLE public.textvalue OWNER TO "user";

--
-- TOC entry 3844 (class 0 OID 0)
-- Dependencies: 219
-- Name: TABLE textvalue; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE textvalue IS 'Value table for text observation';


--
-- TOC entry 220 (class 1259 OID 60217)
-- Name: unit; Type: TABLE; Schema: public; Owner: user; Tablespace: 
--

CREATE TABLE unit (
    unitid bigint NOT NULL,
    unit character varying(255) NOT NULL
);


ALTER TABLE public.unit OWNER TO "user";

--
-- TOC entry 3845 (class 0 OID 0)
-- Dependencies: 220
-- Name: TABLE unit; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE unit IS 'Table to store the unit of measure information, used in observations. Mapping file: mapping/core/Unit.hbm.xml';


--
-- TOC entry 3846 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN unit.unitid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN unit.unitid IS 'Table primary key, used for relations';


--
-- TOC entry 3847 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN unit.unit; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN unit.unit IS 'The unit of measure of observations. See http://unitsofmeasure.org/ucum.html';


--
-- TOC entry 241 (class 1259 OID 60613)
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
-- TOC entry 221 (class 1259 OID 60222)
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
-- TOC entry 3848 (class 0 OID 0)
-- Dependencies: 221
-- Name: TABLE validproceduretime; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON TABLE validproceduretime IS 'Table to store procedure descriptions which were inserted or updated via the transactional Profile. Mapping file: mapping/transactionl/ValidProcedureTime.hbm.xml';


--
-- TOC entry 3849 (class 0 OID 0)
-- Dependencies: 221
-- Name: COLUMN validproceduretime.validproceduretimeid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN validproceduretime.validproceduretimeid IS 'Table primary key';


--
-- TOC entry 3850 (class 0 OID 0)
-- Dependencies: 221
-- Name: COLUMN validproceduretime.procedureid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN validproceduretime.procedureid IS 'Foreign Key (FK) to the related procedure. Contains "procedure".procedureid';


--
-- TOC entry 3851 (class 0 OID 0)
-- Dependencies: 221
-- Name: COLUMN validproceduretime.proceduredescriptionformatid; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN validproceduretime.proceduredescriptionformatid IS 'Foreign Key (FK) to the related procedureDescriptionFormat. Contains "procedureDescriptionFormat".procedureDescriptionFormatid';


--
-- TOC entry 3852 (class 0 OID 0)
-- Dependencies: 221
-- Name: COLUMN validproceduretime.starttime; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN validproceduretime.starttime IS 'Timestamp since this procedure description is valid';


--
-- TOC entry 3853 (class 0 OID 0)
-- Dependencies: 221
-- Name: COLUMN validproceduretime.endtime; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN validproceduretime.endtime IS 'Timestamp since this procedure description is invalid';


--
-- TOC entry 3854 (class 0 OID 0)
-- Dependencies: 221
-- Name: COLUMN validproceduretime.descriptionxml; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON COLUMN validproceduretime.descriptionxml IS 'Procedure description as XML string';


--
-- TOC entry 242 (class 1259 OID 60615)
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
-- TOC entry 3630 (class 0 OID 59992)
-- Dependencies: 186
-- Data for Name: blobvalue; Type: TABLE DATA; Schema: public; Owner: user
--

COPY blobvalue (observationid, value) FROM stdin;
\.


--
-- TOC entry 3631 (class 0 OID 59997)
-- Dependencies: 187
-- Data for Name: booleanvalue; Type: TABLE DATA; Schema: public; Owner: user
--

COPY booleanvalue (observationid, value) FROM stdin;
\.


--
-- TOC entry 3632 (class 0 OID 60004)
-- Dependencies: 188
-- Data for Name: categoryvalue; Type: TABLE DATA; Schema: public; Owner: user
--

COPY categoryvalue (observationid, value) FROM stdin;
\.


--
-- TOC entry 3633 (class 0 OID 60009)
-- Dependencies: 189
-- Data for Name: codespace; Type: TABLE DATA; Schema: public; Owner: user
--

COPY codespace (codespaceid, codespace) FROM stdin;
1	http://www.opengis.net/def/nil/OGC/0/unknown
\.


--
-- TOC entry 3855 (class 0 OID 0)
-- Dependencies: 222
-- Name: codespaceid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('codespaceid_seq', 1, true);


--
-- TOC entry 3634 (class 0 OID 60014)
-- Dependencies: 190
-- Data for Name: compositephenomenon; Type: TABLE DATA; Schema: public; Owner: user
--

COPY compositephenomenon (parentobservablepropertyid, childobservablepropertyid) FROM stdin;
\.


--
-- TOC entry 3635 (class 0 OID 60019)
-- Dependencies: 191
-- Data for Name: countvalue; Type: TABLE DATA; Schema: public; Owner: user
--

COPY countvalue (observationid, value) FROM stdin;
\.


--
-- TOC entry 3636 (class 0 OID 60024)
-- Dependencies: 192
-- Data for Name: featureofinterest; Type: TABLE DATA; Schema: public; Owner: user
--

COPY featureofinterest (featureofinterestid, hibernatediscriminator, featureofinteresttypeid, identifier, codespace, name, codespacename, description, geom, descriptionxml, url) FROM stdin;
1	T	2	http://www.52north.org/test/featureOfInterest/world	1	\N	1	\N	\N	\N	\N
2	T	2	52NorthWS1	1	52North HWS	1	\N	0101000020E610000040A4DFBE0E9C1E408126C286A7F74940	\N	\N
\.


--
-- TOC entry 3856 (class 0 OID 0)
-- Dependencies: 223
-- Name: featureofinterestid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('featureofinterestid_seq', 2, true);


--
-- TOC entry 3637 (class 0 OID 60032)
-- Dependencies: 193
-- Data for Name: featureofinteresttype; Type: TABLE DATA; Schema: public; Owner: user
--

COPY featureofinteresttype (featureofinteresttypeid, featureofinteresttype) FROM stdin;
1	http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint
2	http://www.opengis.net/def/nil/OGC/0/unknown
\.


--
-- TOC entry 3857 (class 0 OID 0)
-- Dependencies: 224
-- Name: featureofinteresttypeid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('featureofinteresttypeid_seq', 2, true);


--
-- TOC entry 3638 (class 0 OID 60037)
-- Dependencies: 194
-- Data for Name: featurerelation; Type: TABLE DATA; Schema: public; Owner: user
--

COPY featurerelation (parentfeatureid, childfeatureid) FROM stdin;
1	2
\.


--
-- TOC entry 3639 (class 0 OID 60042)
-- Dependencies: 195
-- Data for Name: geometryvalue; Type: TABLE DATA; Schema: public; Owner: user
--

COPY geometryvalue (observationid, value) FROM stdin;
\.


--
-- TOC entry 3640 (class 0 OID 60050)
-- Dependencies: 196
-- Data for Name: i18nfeatureofinterest; Type: TABLE DATA; Schema: public; Owner: user
--

COPY i18nfeatureofinterest (id, objectid, locale, name, description) FROM stdin;
1	2	ita	52North stazione metereologica	Questa è una configurazione stazione meteo WS2500 a 52North, Münster in Germania.
2	2	eng	52North weather station	This is a WS2500 weather station setup at 52North, Münster in Germany.
3	2	ger	52North Wetterstation	Dies ist eine WS2500-Wetterstation, aufgestellt bei 52North, Münster in Deutschland.
\.


--
-- TOC entry 3858 (class 0 OID 0)
-- Dependencies: 228
-- Name: i18nfeatureofinterestid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('i18nfeatureofinterestid_seq', 3, true);


--
-- TOC entry 3641 (class 0 OID 60058)
-- Dependencies: 197
-- Data for Name: i18nobservableproperty; Type: TABLE DATA; Schema: public; Owner: user
--

COPY i18nobservableproperty (id, objectid, locale, name, description) FROM stdin;
1	4	ita	pressione barometrica	\N
2	4	eng	air pressure	\N
3	4	ger	Luftdruck	\N
4	6	ita	Illuminamento	\N
5	6	eng	Illuminance	\N
6	6	ger	Beleuchtungsstärke	\N
7	1	ita	Precipitazioni oraria	\N
8	1	eng	Hourly Precipitation	\N
9	1	ger	Stündlicher Niederschlag	\N
10	7	ita	umidità relativa	\N
11	7	eng	relative Humidity	\N
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
-- TOC entry 3859 (class 0 OID 0)
-- Dependencies: 225
-- Name: i18nobspropid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('i18nobspropid_seq', 21, true);


--
-- TOC entry 3642 (class 0 OID 60066)
-- Dependencies: 198
-- Data for Name: i18noffering; Type: TABLE DATA; Schema: public; Owner: user
--

COPY i18noffering (id, objectid, locale, name, description) FROM stdin;
\.


--
-- TOC entry 3860 (class 0 OID 0)
-- Dependencies: 226
-- Name: i18nofferingid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('i18nofferingid_seq', 1, false);


--
-- TOC entry 3643 (class 0 OID 60074)
-- Dependencies: 199
-- Data for Name: i18nprocedure; Type: TABLE DATA; Schema: public; Owner: user
--

COPY i18nprocedure (id, objectid, locale, name, description, shortname, longname) FROM stdin;
\.


--
-- TOC entry 3861 (class 0 OID 0)
-- Dependencies: 227
-- Name: i18nprocedureid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('i18nprocedureid_seq', 1, false);


--
-- TOC entry 3644 (class 0 OID 60082)
-- Dependencies: 200
-- Data for Name: numericvalue; Type: TABLE DATA; Schema: public; Owner: user
--

COPY numericvalue (observationid, value) FROM stdin;
1	986
2	986
3	986
4	987
5	988
6	988
7	992
8	993
9	994
10	998
11	1001
12	1001
13	1003
14	1005
15	1005
16	1006
17	1006
18	1007
19	1006
20	1007
21	1007
22	1007
23	1010
24	1010
25	1011
26	1012
27	1012
28	1012
29	1012
30	1011
31	1010
32	1010
33	1008
34	1006
35	1004
36	1003
37	1002
38	1001
39	1000
40	999
41	998
42	997
43	996
44	995
45	995
46	994
47	994
48	993
49	993
50	993
51	992
52	993
53	993
54	993
55	994
56	994
57	995
58	996
59	996
60	996
61	998
62	999
63	1000
64	1002
65	1002
66	1005
67	1005
68	1006
69	1007
70	1009
71	1009
72	1010
73	1009
74	1010
75	1010
76	1010
77	1010
78	1010
79	1009
80	1008
81	1008
82	1005
83	1005
84	1004
85	1004
86	1003
87	1003
88	1003
89	1003
90	1005
91	1005
92	1005
93	1005
94	1008
95	1007
96	1008
97	1008
98	1007
99	1007
100	1007
101	1008
102	1007
103	1007
104	1008
105	1008
106	1008
107	1008
108	1007
109	1007
110	1007
111	1006
112	1003
113	1003
114	1001
115	1001
116	999
117	998
118	997
119	997
120	997
121	996
122	996
123	996
124	997
125	997
126	997
127	997
128	997
129	999
130	999
131	999
132	1000
133	1001
134	1003
135	1005
136	1006
137	1007
138	1009
139	1010
140	1013
141	1014
142	1015
143	1015
144	1016
145	1016
146	1013
147	1013
148	1011
149	1008
150	1004
151	1005
152	1001
153	1000
154	998
155	997
156	997
157	998
158	999
159	1002
160	1006
161	1007
162	1010
163	1010
164	1011
165	1013
166	1014
167	1014
168	1014
169	1014
170	1014
171	1014
172	1014
173	1013
174	1013
175	1013
176	1012
177	1012
178	1012
179	1011
180	1011
181	1011
182	1010
183	1009
184	1009
185	1007
186	1006
187	1006
188	1004
189	1005
190	1005
191	1005
192	1007
193	1009
194	1009
195	1010
196	1012
197	1012
198	1013
199	1013
200	1014
201	1014
202	1014
203	1015
204	1015
205	1016
206	1015
207	1015
208	1015
209	1016
210	1016
211	1016
212	1015
213	1015
214	1015
215	1014
216	1014
217	1014
218	1013
219	1013
220	1013
221	1013
222	1013
223	1013
224	1013
225	1014
226	1013
227	1013
228	1014
229	1014
230	1014
231	1015
232	1016
233	1016
234	1017
235	1019
236	1020
237	1019
238	1023
239	1023
240	1025
241	1026
242	1027
243	1028
244	1030
245	1030
246	1029
247	1029
248	1030
249	1031
250	1028
251	1028
252	1024
253	1020
254	1020
255	1018
256	1013
257	1005
258	1001
259	1000
260	996
261	995
262	995
263	995
264	995
265	993
266	992
267	990
268	990
269	982
270	988
271	981
272	986
273	970
274	976
275	970
276	974
277	975
278	973
279	975
280	973
281	991
282	990
283	977
284	991
285	992
286	994
287	993
288	995
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
577	0
578	0
579	0
580	0
581	0
582	0
583	0
584	0
585	0
586	0
587	0
588	0
589	0
590	0
591	0
592	0
593	0
594	0
595	0
596	0
597	0
598	0
599	0
600	0
601	0
602	0
603	0
604	0
605	0
606	0
607	0
608	0
609	0
610	0
611	0
612	0
613	0
614	0
615	0
616	0
617	0
618	0
619	0
620	0
621	0
622	0
623	0
624	0
625	0
626	0
627	0
628	0
629	0
630	0
631	0
632	0
633	0
634	0
635	0
636	0
637	0
638	0
639	0
640	0
641	0
642	0
643	0
644	0
645	0
646	0
647	0
648	0
649	0
650	0
651	0
652	0
653	0
654	0
655	0
656	0
657	0
658	0
659	0
660	0
661	0
662	0
663	0
664	0
665	0
666	0
667	0
668	0
669	0
670	0
671	0
672	0
673	0
674	0
675	0
676	0
677	0
678	0
679	0
680	0
681	0
682	0
683	0
684	0
685	0
686	0
687	0
688	0
689	0
690	0
691	0
692	0
693	0
694	0
695	0
696	0
697	0
698	0
699	0
700	0
701	0
702	0
703	0
704	0
705	0
706	0
707	0
708	0
709	0
710	0
711	0
712	0
713	0
714	0
715	0
716	0
717	0
718	0.299999999999999989
719	0
720	0
721	0
722	0
723	0
724	0
725	0
726	0
727	0
728	0
729	0
730	0
731	0
732	0
733	0
734	0.299999999999999989
735	0
736	0
737	0
738	0.299999999999999989
739	0
740	0
741	0
742	0
743	0
744	0
745	0
746	0
747	0
748	0
749	0
750	0
751	0
752	0
753	0
754	0
755	0
756	0
757	0
758	0
759	0
760	0
761	0
762	0
763	0
764	0
765	0
766	0
767	0
768	0
769	0
770	0
771	0
772	0
773	0
774	0
775	0
776	0
777	0
778	0
779	0
780	0
781	0
782	0
783	0
784	0
785	0
786	0
787	0
788	0
789	0
790	0
791	0
792	0
793	0
794	0
795	0
796	0
797	0
798	0
799	0
800	0
801	0
802	0
803	0
804	0
805	0
806	0
807	0
808	0
809	0
810	0
811	0
812	0
813	0
814	0
815	0
816	0
817	0
818	0
819	0
820	0
821	0
822	0
823	0
824	0
825	0
826	0
827	0
828	0
829	0
830	0
831	0
832	0
833	0
834	0
835	0
836	0.299999999999999989
837	0
838	0
839	0
840	0
841	0.299999999999999989
842	0
843	0
844	0
845	0
846	0
847	0
848	0
849	0
850	0.299999999999999989
851	0
852	0
853	0
854	0
855	0
856	0
857	0
858	0
859	0
860	0
861	0
862	0
863	0
864	0
865	99
866	99
867	99
868	95
869	94
870	94
871	94
872	98
873	99
874	98
875	91
876	91
877	90
878	89
879	82
880	89
881	97
882	99
883	99
884	99
885	99
886	99
887	99
888	99
889	91
890	99
891	98
892	95
893	99
894	99
895	99
896	99
897	99
898	94
899	91
900	93
901	91
902	93
903	97
904	98
905	95
906	98
907	96
908	93
909	94
910	97
911	98
912	99
913	98
914	99
915	92
916	89
917	84
918	80
919	88
920	92
921	95
922	96
923	96
924	96
925	99
926	98
927	96
928	96
929	98
930	98
931	98
932	98
933	96
934	94
935	91
936	97
937	91
938	94
939	93
940	88
941	88
942	92
943	92
944	95
945	95
946	97
947	95
948	84
949	92
950	86
951	86
952	92
953	91
954	97
955	96
956	94
957	93
958	95
959	92
960	99
961	99
962	99
963	99
964	98
965	90
966	92
967	90
968	89
969	94
970	98
971	98
972	99
973	99
974	89
975	92
976	88
977	86
978	90
979	90
980	90
981	86
982	86
983	85
984	79
985	78
986	86
987	84
988	91
989	91
990	91
991	94
992	94
993	94
994	91
995	88
996	90
997	92
998	93
999	93
1000	93
1001	93
1002	95
1003	93
1004	92
1005	92
1006	95
1007	95
1008	96
1009	96
1010	95
1011	94
1012	94
1013	83
1014	83
1015	91
1016	89
1017	89
1018	99
1019	99
1020	99
1021	99
1022	99
1023	99
1024	99
1025	99
1026	99
1027	99
1028	99
1029	99
1030	99
1031	99
1032	99
1033	99
1034	99
1035	99
1036	99
1037	99
1038	99
1039	99
1040	99
1041	99
1042	99
1043	97
1044	99
1045	99
1046	99
1047	99
1048	99
1049	99
1050	99
1051	99
1052	92
1053	92
1054	90
1055	91
1056	93
1057	94
1058	94
1059	93
1060	86
1061	82
1062	82
1063	85
1064	94
1065	96
1066	97
1067	96
1068	98
1069	98
1070	99
1071	85
1072	86
1073	92
1074	98
1075	97
1076	98
1077	97
1078	97
1079	93
1080	90
1081	87
1082	86
1083	92
1084	93
1085	92
1086	91
1087	91
1088	91
1089	91
1090	89
1091	91
1092	99
1093	99
1094	99
1095	99
1096	99
1097	99
1098	93
1099	96
1100	85
1101	86
1102	85
1103	86
1104	88
1105	90
1106	93
1107	92
1108	90
1109	67
1110	64
1111	78
1112	80
1113	79
1114	85
1115	82
1116	88
1117	89
1118	81
1119	80
1120	68
1121	77
1122	99
1123	99
1124	99
1125	99
1126	95
1127	99
1128	99
1129	98
1130	97
1131	97
1132	98
1133	99
1134	99
1135	99
1136	99
1137	99
1138	99
1139	99
1140	99
1141	99
1142	98
1143	97
1144	99
1145	99
1146	96
1147	82
1148	89
1149	88
1150	94
1151	92
1152	97
1153	135
1154	135
1155	135
1156	135
1157	135
1158	135
1159	135
1160	135
1161	135
1162	135
1163	135
1164	135
1165	130
1166	135
1167	135
1168	135
1169	135
1170	135
1171	135
1172	135
1173	135
1174	135
1175	135
1176	135
1177	135
1178	135
1179	130
1180	135
1181	135
1182	135
1183	135
1184	135
1185	135
1186	135
1187	135
1188	135
1189	135
1190	130
1191	135
1192	135
1193	135
1194	135
1195	135
1196	320
1197	310
1198	310
1199	305
1200	305
1201	305
1202	310
1203	305
1204	305
1205	305
1206	310
1207	305
1208	305
1209	305
1210	305
1211	305
1212	310
1213	305
1214	305
1215	310
1216	35
1217	35
1218	35
1219	35
1220	35
1221	35
1222	35
1223	40
1224	35
1225	40
1226	45
1227	40
1228	45
1229	140
1230	140
1231	140
1232	105
1233	135
1234	130
1235	120
1236	150
1237	150
1238	140
1239	135
1240	115
1241	150
1242	140
1243	115
1244	130
1245	140
1246	130
1247	135
1248	90
1249	90
1250	95
1251	115
1252	95
1253	150
1254	160
1255	145
1256	145
1257	150
1258	140
1259	135
1260	145
1261	140
1262	160
1263	145
1264	150
1265	175
1266	160
1267	155
1268	145
1269	145
1270	145
1271	150
1272	160
1273	160
1274	155
1275	145
1276	165
1277	155
1278	170
1279	150
1280	155
1281	145
1282	145
1283	140
1284	150
1285	150
1286	150
1287	150
1288	155
1289	155
1290	155
1291	205
1292	250
1293	215
1294	240
1295	210
1296	230
1297	240
1298	150
1299	215
1300	170
1301	150
1302	155
1303	180
1304	205
1305	135
1306	235
1307	160
1308	185
1309	330
1310	335
1311	10
1312	350
1313	5
1314	335
1315	350
1316	335
1317	350
1318	335
1319	345
1320	340
1321	325
1322	310
1323	325
1324	330
1325	335
1326	350
1327	10
1328	20
1329	350
1330	360
1331	345
1332	60
1333	70
1334	30
1335	30
1336	145
1337	155
1338	160
1339	150
1340	160
1341	155
1342	155
1343	160
1344	160
1345	155
1346	150
1347	145
1348	150
1349	150
1350	150
1351	155
1352	150
1353	145
1354	155
1355	155
1356	155
1357	160
1358	170
1359	155
1360	155
1361	140
1362	165
1363	145
1364	160
1365	145
1366	155
1367	150
1368	165
1369	140
1370	155
1371	150
1372	170
1373	160
1374	150
1375	160
1376	140
1377	150
1378	145
1379	140
1380	150
1381	140
1382	95
1383	135
1384	90
1385	120
1386	110
1387	115
1388	90
1389	95
1390	140
1391	145
1392	145
1393	130
1394	95
1395	145
1396	125
1397	135
1398	135
1399	135
1400	150
1401	140
1402	70
1403	65
1404	285
1405	255
1406	285
1407	330
1408	345
1409	335
1410	340
1411	340
1412	25
1413	25
1414	5
1415	10
1416	360
1417	10
1418	10
1419	5
1420	360
1421	360
1422	335
1423	15
1424	340
1425	295
1426	315
1427	300
1428	335
1429	20
1430	5
1431	5
1432	5
1433	345
1434	345
1435	355
1436	355
1437	320
1438	330
1439	330
1440	320
1441	0
1442	0
1443	0
1444	1600
1445	6740
1446	2750
1447	0
1448	0
1449	0
1450	0
1451	0
1452	0
1453	5840
1454	10700
1455	16900
1456	2670
1457	7
1458	0
1459	0
1460	0
1461	0
1462	0
1463	12700
1464	26400
1465	21300
1466	8
1467	0
1468	0
1469	0
1470	0
1471	0
1472	0
1473	7390
1474	14600
1475	1740
1476	147
1477	0
1478	0
1479	0
1480	0
1481	0
1482	1140
1483	6790
1484	11500
1485	1940
1486	0
1487	0
1488	0
1489	0
1490	0
1491	0
1492	7470
1493	16900
1494	18600
1495	6
1496	0
1497	0
1498	0
1499	0
1500	0
1501	420
1502	4660
1503	12800
1504	12800
1505	0
1506	0
1507	0
1508	0
1509	0
1510	0
1511	8120
1512	3220
1513	7810
1514	1290
1515	0
1516	0
1517	0
1518	0
1519	0
1520	8480
1521	7860
1522	1640
1523	6090
1524	0
1525	0
1526	0
1527	0
1528	0
1529	0
1530	6840
1531	7690
1532	11700
1533	55
1534	0
1535	0
1536	0
1537	0
1538	0
1539	481
1540	8860
1541	2710
1542	4780
1543	0
1544	0
1545	0
1546	0
1547	0
1548	0
1549	4250
1550	9570
1551	9680
1552	6080
1553	0
1554	0
1555	0
1556	0
1557	0
1558	0
1559	4380
1560	17300
1561	22000
1562	10500
1563	0
1564	0
1565	0
1566	0
1567	0
1568	28
1569	11900
1570	13300
1571	5670
1572	1310
1573	0
1574	0
1575	0
1576	0
1577	0
1578	343
1579	842
1580	808
1581	5270
1582	0
1583	0
1584	0
1585	0
1586	0
1587	0
1588	30300
1589	4290
1590	8050
1591	4840
1592	0
1593	0
1594	0
1595	0
1596	0
1597	1050
1598	6570
1599	3850
1600	483
1601	222
1602	0
1603	0
1604	0
1605	0
1606	408
1607	84
1608	11700
1609	8030
1610	13
1611	0
1612	0
1613	0
1614	0
1615	0
1616	0
1617	13600
1618	12900
1619	2290
1620	0
1621	0
1622	0
1623	0
1624	0
1625	0
1626	60
1627	8910
1628	25800
1629	1
1630	2
1631	0
1632	0
1633	0
1634	0
1635	0
1636	27500
1637	31200
1638	14300
1639	410
1640	0
1641	0
1642	0
1643	0
1644	134
1645	470
1646	36600
1647	24900
1648	12
1649	11300
1650	0
1651	0
1652	0
1653	0
1654	0
1655	0
1656	18400
1657	19800
1658	11900
1659	0
1660	0
1661	0
1662	0
1663	0
1664	1100
1665	7630
1666	8830
1667	1430
1668	5840
1669	5
1670	0
1671	0
1672	0
1673	0
1674	7380
1675	8710
1676	11600
1677	837
1678	0
1679	0
1680	0
1681	0
1682	0
1683	2790
1684	38300
1685	39700
1686	23700
1687	27200
1688	6
1689	0
1690	0
1691	0
1692	0
1693	14600
1694	21200
1695	8310
1696	13600
1697	0
1698	3
1699	0
1700	0
1701	0
1702	0
1703	1930
1704	15100
1705	30900
1706	0
1707	164
1708	0
1709	0
1710	0
1711	0
1712	0
1713	3
1714	6840
1715	1400
1716	6120
1717	0
1718	0
1719	0
1720	0
1721	0
1722	49900
1723	0
1724	45800
1725	3320
1726	0
1727	10500
1728	0
1729	23.5
1730	22.3000000000000007
1731	16.6000000000000014
1732	20
1733	16.6000000000000014
1734	17.6000000000000014
1735	16.1000000000000014
1736	15.1999999999999993
1737	10.3000000000000007
1738	13.1999999999999993
1739	16.8000000000000007
1740	11.8000000000000007
1741	10.5999999999999996
1742	11.4000000000000004
1743	11.6999999999999993
1744	11.0999999999999996
1745	12.4000000000000004
1746	19.6000000000000014
1747	11.9000000000000004
1748	14.0999999999999996
1749	8.59999999999999964
1750	10.5999999999999996
1751	11.1999999999999993
1752	15.1999999999999993
1753	9.59999999999999964
1754	11.5
1755	2.79999999999999982
1756	8.80000000000000071
1757	5.59999999999999964
1758	6
1759	0
1760	8.19999999999999929
1761	17.1999999999999993
1762	21
1763	22.1000000000000014
1764	20.6000000000000014
1765	21.6000000000000014
1766	17.1000000000000014
1767	26.6000000000000014
1768	14.5
1769	12.9000000000000004
1770	10.0999999999999996
1771	17.1999999999999993
1772	19.6000000000000014
1773	17.1000000000000014
1774	14.3000000000000007
1775	10.0999999999999996
1776	0.900000000000000022
1777	5.20000000000000018
1778	6.5
1779	14.6999999999999993
1780	6.70000000000000018
1781	14.0999999999999996
1782	12.8000000000000007
1783	7
1784	5.20000000000000018
1785	9.59999999999999964
1786	4.59999999999999964
1787	5
1788	0.699999999999999956
1789	0.299999999999999989
1790	0.599999999999999978
1791	3.20000000000000018
1792	8.19999999999999929
1793	3.20000000000000018
1794	4.09999999999999964
1795	4.20000000000000018
1796	0
1797	13.0999999999999996
1798	12
1799	15.0999999999999996
1800	12.8000000000000007
1801	18.1999999999999993
1802	30.6000000000000014
1803	25.8000000000000007
1804	37.8999999999999986
1805	37.2999999999999972
1806	33.5
1807	25.6000000000000014
1808	30.6999999999999993
1809	36.2999999999999972
1810	27.8000000000000007
1811	31
1812	27.5
1813	22.5
1814	28.5
1815	31.1999999999999993
1816	17.8000000000000007
1817	23.5
1818	13
1819	21.8000000000000007
1820	16.1000000000000014
1821	18.8999999999999986
1822	15.8000000000000007
1823	16
1824	6.59999999999999964
1825	9.59999999999999964
1826	15.8000000000000007
1827	13
1828	12
1829	14.1999999999999993
1830	14.5
1831	11
1832	8.19999999999999929
1833	11
1834	5.09999999999999964
1835	0
1836	5.40000000000000036
1837	6.79999999999999982
1838	16.6000000000000014
1839	12.0999999999999996
1840	24.6000000000000014
1841	23.6000000000000014
1842	21.5
1843	22.3000000000000007
1844	18.3999999999999986
1845	27.1999999999999993
1846	27.3000000000000007
1847	23.3000000000000007
1848	29.1000000000000014
1849	35.5
1850	21.3999999999999986
1851	21.6000000000000014
1852	13.5999999999999996
1853	13.1999999999999993
1854	6.79999999999999982
1855	12.3000000000000007
1856	7.40000000000000036
1857	8.40000000000000036
1858	11.3000000000000007
1859	9.40000000000000036
1860	10
1861	6.20000000000000018
1862	7.90000000000000036
1863	7.29999999999999982
1864	7.20000000000000018
1865	8.80000000000000071
1866	2.10000000000000009
1867	0.299999999999999989
1868	0.699999999999999956
1869	5.40000000000000036
1870	4.09999999999999964
1871	0.900000000000000022
1872	6.09999999999999964
1873	16
1874	13.8000000000000007
1875	14.5999999999999996
1876	19.1000000000000014
1877	18.1000000000000014
1878	21.8999999999999986
1879	19.1000000000000014
1880	8.5
1881	12.5
1882	5.70000000000000018
1883	6.90000000000000036
1884	6
1885	7.09999999999999964
1886	12
1887	11.0999999999999996
1888	25.3999999999999986
1889	23.1999999999999993
1890	19
1891	21.6999999999999993
1892	14.5
1893	17.3000000000000007
1894	16.3999999999999986
1895	15.3000000000000007
1896	18.5
1897	21
1898	26
1899	19
1900	19.6999999999999993
1901	23.6999999999999993
1902	20.8000000000000007
1903	12.8000000000000007
1904	8.90000000000000036
1905	14.8000000000000007
1906	7.40000000000000036
1907	21.3999999999999986
1908	0
1909	3.60000000000000009
1910	0
1911	0
1912	4.70000000000000018
1913	5.70000000000000018
1914	13.1999999999999993
1915	5.79999999999999982
1916	11.8000000000000007
1917	17.1000000000000014
1918	14.5
1919	17.5
1920	12.9000000000000004
1921	11.3000000000000007
1922	15.5999999999999996
1923	12.6999999999999993
1924	15.0999999999999996
1925	15.5
1926	15.3000000000000007
1927	14.6999999999999993
1928	17.8000000000000007
1929	10.5999999999999996
1930	13
1931	9.69999999999999929
1932	13.3000000000000007
1933	19
1934	17.6000000000000014
1935	11.8000000000000007
1936	18.3999999999999986
1937	13.5999999999999996
1938	13.5999999999999996
1939	14.9000000000000004
1940	12
1941	12
1942	11.5999999999999996
1943	11.8000000000000007
1944	13.0999999999999996
1945	12.5
1946	18.8000000000000007
1947	18.8000000000000007
1948	16.1999999999999993
1949	14.1999999999999993
1950	11.5
1951	12.5
1952	8.59999999999999964
1953	7.90000000000000036
1954	10.0999999999999996
1955	7.59999999999999964
1956	5
1957	12.8000000000000007
1958	9.90000000000000036
1959	8.5
1960	9.40000000000000036
1961	9.19999999999999929
1962	13.0999999999999996
1963	12.5999999999999996
1964	13.5999999999999996
1965	13.3000000000000007
1966	12.6999999999999993
1967	12
1968	14.6999999999999993
1969	13.0999999999999996
1970	14
1971	17.3000000000000007
1972	11.5
1973	13.5999999999999996
1974	12.5
1975	10.1999999999999993
1976	8.69999999999999929
1977	6.5
1978	0
1979	0
1980	5.59999999999999964
1981	5.90000000000000036
1982	9.09999999999999964
1983	43.7000000000000028
1984	33.2000000000000028
1985	46.5
1986	43.1000000000000014
1987	46.2999999999999972
1988	41.1000000000000014
1989	40.5
1990	32
1991	26.6999999999999993
1992	23.1000000000000014
1993	34.7000000000000028
1994	25.1999999999999993
1995	32.5
1996	23.6000000000000014
1997	24.5
1998	23.1999999999999993
1999	19.1999999999999993
2000	23
2001	20.8000000000000007
2002	30.5
2003	19.3000000000000007
2004	29.5
2005	23.1999999999999993
2006	28.6999999999999993
2007	27.1999999999999993
2008	30
2009	19
2010	19
2011	33.2000000000000028
2012	36.7999999999999972
2013	36.3999999999999986
2014	27.3000000000000007
2015	26.5
2016	21.8999999999999986
\.


--
-- TOC entry 3645 (class 0 OID 60087)
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
-- TOC entry 3862 (class 0 OID 0)
-- Dependencies: 229
-- Name: observablepropertyid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('observablepropertyid_seq', 7, true);


--
-- TOC entry 3646 (class 0 OID 60097)
-- Dependencies: 202
-- Data for Name: observation; Type: TABLE DATA; Schema: public; Owner: user
--

COPY observation (observationid, seriesid, phenomenontimestart, phenomenontimeend, resulttime, identifier, codespace, name, codespacename, description, deleted, validtimestart, validtimeend, unitid, samplinggeometry) FROM stdin;
1	1	2016-06-30 23:45:00	2016-06-30 23:45:00	2016-06-30 23:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
2	1	2016-07-01 02:45:00	2016-07-01 02:45:00	2016-07-01 02:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
3	1	2016-07-01 04:46:00	2016-07-01 04:46:00	2016-07-01 04:46:00	\N	1	\N	1	\N	F	\N	\N	1	\N
4	1	2016-07-01 07:45:00	2016-07-01 07:45:00	2016-07-01 07:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
5	1	2016-07-01 09:45:00	2016-07-01 09:45:00	2016-07-01 09:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
6	1	2016-07-01 10:15:00	2016-07-01 10:15:00	2016-07-01 10:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
7	1	2016-07-01 17:00:00	2016-07-01 17:00:00	2016-07-01 17:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
8	1	2016-07-01 18:30:00	2016-07-01 18:30:00	2016-07-01 18:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
9	1	2016-07-01 18:45:00	2016-07-01 18:45:00	2016-07-01 18:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
10	1	2016-07-01 23:30:00	2016-07-01 23:30:00	2016-07-01 23:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
11	1	2016-07-02 03:30:00	2016-07-02 03:30:00	2016-07-02 03:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
12	1	2016-07-02 04:30:00	2016-07-02 04:30:00	2016-07-02 04:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
13	1	2016-07-02 06:00:00	2016-07-02 06:00:00	2016-07-02 06:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
14	1	2016-07-02 09:30:00	2016-07-02 09:30:00	2016-07-02 09:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
15	1	2016-07-02 09:45:00	2016-07-02 09:45:00	2016-07-02 09:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
16	1	2016-07-02 14:30:00	2016-07-02 14:30:00	2016-07-02 14:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
17	1	2016-07-02 16:00:00	2016-07-02 16:00:00	2016-07-02 16:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
18	1	2016-07-02 20:15:00	2016-07-02 20:15:00	2016-07-02 20:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
19	1	2016-07-02 23:15:00	2016-07-02 23:15:00	2016-07-02 23:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
20	1	2016-07-03 01:45:00	2016-07-03 01:45:00	2016-07-03 01:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
21	1	2016-07-03 02:00:00	2016-07-03 02:00:00	2016-07-03 02:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
22	1	2016-07-03 05:30:00	2016-07-03 05:30:00	2016-07-03 05:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
23	1	2016-07-03 10:15:00	2016-07-03 10:15:00	2016-07-03 10:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
24	1	2016-07-03 11:00:00	2016-07-03 11:00:00	2016-07-03 11:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
25	1	2016-07-03 13:16:00	2016-07-03 13:16:00	2016-07-03 13:16:00	\N	1	\N	1	\N	F	\N	\N	1	\N
26	1	2016-07-03 16:01:00	2016-07-03 16:01:00	2016-07-03 16:01:00	\N	1	\N	1	\N	F	\N	\N	1	\N
27	1	2016-07-03 19:00:00	2016-07-03 19:00:00	2016-07-03 19:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
28	1	2016-07-03 20:15:00	2016-07-03 20:15:00	2016-07-03 20:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
29	1	2016-07-03 23:16:00	2016-07-03 23:16:00	2016-07-03 23:16:00	\N	1	\N	1	\N	F	\N	\N	1	\N
30	1	2016-07-04 02:01:00	2016-07-04 02:01:00	2016-07-04 02:01:00	\N	1	\N	1	\N	F	\N	\N	1	\N
31	1	2016-07-04 05:01:00	2016-07-04 05:01:00	2016-07-04 05:01:00	\N	1	\N	1	\N	F	\N	\N	1	\N
32	1	2016-07-04 06:16:00	2016-07-04 06:16:00	2016-07-04 06:16:00	\N	1	\N	1	\N	F	\N	\N	1	\N
33	1	2016-07-04 09:16:00	2016-07-04 09:16:00	2016-07-04 09:16:00	\N	1	\N	1	\N	F	\N	\N	1	\N
34	1	2016-07-04 12:01:00	2016-07-04 12:01:00	2016-07-04 12:01:00	\N	1	\N	1	\N	F	\N	\N	1	\N
35	1	2016-07-04 15:00:00	2016-07-04 15:00:00	2016-07-04 15:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
36	1	2016-07-04 16:15:00	2016-07-04 16:15:00	2016-07-04 16:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
37	1	2016-07-04 19:15:00	2016-07-04 19:15:00	2016-07-04 19:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
38	1	2016-07-04 22:00:00	2016-07-04 22:00:00	2016-07-04 22:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
39	1	2016-07-05 01:01:00	2016-07-05 01:01:00	2016-07-05 01:01:00	\N	1	\N	1	\N	F	\N	\N	1	\N
40	1	2016-07-05 02:16:00	2016-07-05 02:16:00	2016-07-05 02:16:00	\N	1	\N	1	\N	F	\N	\N	1	\N
41	1	2016-07-05 05:16:00	2016-07-05 05:16:00	2016-07-05 05:16:00	\N	1	\N	1	\N	F	\N	\N	1	\N
42	1	2016-07-05 08:00:00	2016-07-05 08:00:00	2016-07-05 08:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
43	1	2016-07-05 11:00:00	2016-07-05 11:00:00	2016-07-05 11:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
44	1	2016-07-05 12:45:00	2016-07-05 12:45:00	2016-07-05 12:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
45	1	2016-07-05 15:15:00	2016-07-05 15:15:00	2016-07-05 15:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
46	1	2016-07-05 17:30:00	2016-07-05 17:30:00	2016-07-05 17:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
47	1	2016-07-05 20:15:00	2016-07-05 20:15:00	2016-07-05 20:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
48	1	2016-07-05 22:45:00	2016-07-05 22:45:00	2016-07-05 22:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
49	1	2016-07-06 01:15:00	2016-07-06 01:15:00	2016-07-06 01:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
50	1	2016-07-06 03:45:00	2016-07-06 03:45:00	2016-07-06 03:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
51	1	2016-07-06 06:15:00	2016-07-06 06:15:00	2016-07-06 06:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
52	1	2016-07-06 08:45:00	2016-07-06 08:45:00	2016-07-06 08:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
53	1	2016-07-06 10:45:00	2016-07-06 10:45:00	2016-07-06 10:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
54	1	2016-07-06 13:30:00	2016-07-06 13:30:00	2016-07-06 13:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
55	1	2016-07-06 17:15:00	2016-07-06 17:15:00	2016-07-06 17:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
56	1	2016-07-06 18:45:00	2016-07-06 18:45:00	2016-07-06 18:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
57	1	2016-07-06 21:45:00	2016-07-06 21:45:00	2016-07-06 21:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
58	1	2016-07-07 00:30:00	2016-07-07 00:30:00	2016-07-07 00:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
59	1	2016-07-07 01:45:00	2016-07-07 01:45:00	2016-07-07 01:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
60	1	2016-07-07 04:30:00	2016-07-07 04:30:00	2016-07-07 04:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
61	1	2016-07-07 07:15:00	2016-07-07 07:15:00	2016-07-07 07:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
62	1	2016-07-07 10:00:00	2016-07-07 10:00:00	2016-07-07 10:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
63	1	2016-07-07 12:45:00	2016-07-07 12:45:00	2016-07-07 12:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
64	1	2016-07-07 16:15:00	2016-07-07 16:15:00	2016-07-07 16:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
65	1	2016-07-07 17:30:00	2016-07-07 17:30:00	2016-07-07 17:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
66	1	2016-07-07 21:15:00	2016-07-07 21:15:00	2016-07-07 21:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
67	1	2016-07-07 22:00:00	2016-07-07 22:00:00	2016-07-07 22:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
68	1	2016-07-07 23:00:00	2016-07-07 23:00:00	2016-07-07 23:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
69	1	2016-07-08 03:15:00	2016-07-08 03:15:00	2016-07-08 03:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
70	1	2016-07-08 07:00:00	2016-07-08 07:00:00	2016-07-08 07:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
71	1	2016-07-08 08:15:00	2016-07-08 08:15:00	2016-07-08 08:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
72	1	2016-07-08 09:00:00	2016-07-08 09:00:00	2016-07-08 09:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
73	1	2016-07-08 13:45:00	2016-07-08 13:45:00	2016-07-08 13:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
74	1	2016-07-08 20:15:00	2016-07-08 20:15:00	2016-07-08 20:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
75	1	2016-07-08 16:30:00	2016-07-08 16:30:00	2016-07-08 16:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
76	1	2016-07-08 23:30:00	2016-07-08 23:30:00	2016-07-08 23:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
77	1	2016-07-08 23:45:00	2016-07-08 23:45:00	2016-07-08 23:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
78	1	2016-07-09 01:15:00	2016-07-09 01:15:00	2016-07-09 01:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
79	1	2016-07-09 03:15:00	2016-07-09 03:15:00	2016-07-09 03:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
80	1	2016-07-09 08:30:00	2016-07-09 08:30:00	2016-07-09 08:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
81	1	2016-07-09 08:00:00	2016-07-09 08:00:00	2016-07-09 08:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
82	1	2016-07-09 13:15:00	2016-07-09 13:15:00	2016-07-09 13:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
83	1	2016-07-09 15:30:00	2016-07-09 15:30:00	2016-07-09 15:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
84	1	2016-07-09 18:00:00	2016-07-09 18:00:00	2016-07-09 18:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
85	1	2016-07-09 17:15:00	2016-07-09 17:15:00	2016-07-09 17:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
86	1	2016-07-10 01:15:00	2016-07-10 01:15:00	2016-07-10 01:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
87	1	2016-07-10 01:00:00	2016-07-10 01:00:00	2016-07-10 01:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
88	1	2016-07-10 03:45:00	2016-07-10 03:45:00	2016-07-10 03:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
89	1	2016-07-10 02:15:00	2016-07-10 02:15:00	2016-07-10 02:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
90	1	2016-07-10 10:00:00	2016-07-10 10:00:00	2016-07-10 10:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
91	1	2016-07-10 12:00:00	2016-07-10 12:00:00	2016-07-10 12:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
92	1	2016-07-10 13:30:00	2016-07-10 13:30:00	2016-07-10 13:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
93	1	2016-07-10 12:30:00	2016-07-10 12:30:00	2016-07-10 12:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
94	1	2016-07-10 21:15:00	2016-07-10 21:15:00	2016-07-10 21:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
95	1	2016-07-10 17:15:00	2016-07-10 17:15:00	2016-07-10 17:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
96	1	2016-07-11 01:00:00	2016-07-11 01:00:00	2016-07-11 01:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
97	1	2016-07-11 01:15:00	2016-07-11 01:15:00	2016-07-11 01:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
98	1	2016-07-11 04:30:00	2016-07-11 04:30:00	2016-07-11 04:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
99	1	2016-07-11 04:15:00	2016-07-11 04:15:00	2016-07-11 04:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
100	1	2016-07-11 09:15:00	2016-07-11 09:15:00	2016-07-11 09:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
101	1	2016-07-11 10:45:00	2016-07-11 10:45:00	2016-07-11 10:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
102	1	2016-07-11 17:00:00	2016-07-11 17:00:00	2016-07-11 17:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
103	1	2016-07-11 17:30:00	2016-07-11 17:30:00	2016-07-11 17:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
104	1	2016-07-11 18:45:00	2016-07-11 18:45:00	2016-07-11 18:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
105	1	2016-07-11 19:30:00	2016-07-11 19:30:00	2016-07-11 19:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
106	1	2016-07-12 02:30:00	2016-07-12 02:30:00	2016-07-12 02:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
107	1	2016-07-12 01:30:00	2016-07-12 01:30:00	2016-07-12 01:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
108	1	2016-07-12 07:30:00	2016-07-12 07:30:00	2016-07-12 07:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
109	1	2016-07-12 08:16:00	2016-07-12 08:16:00	2016-07-12 08:16:00	\N	1	\N	1	\N	F	\N	\N	1	\N
110	1	2016-07-12 09:30:00	2016-07-12 09:30:00	2016-07-12 09:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
111	1	2016-07-12 10:30:00	2016-07-12 10:30:00	2016-07-12 10:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
112	1	2016-07-12 16:30:00	2016-07-12 16:30:00	2016-07-12 16:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
113	1	2016-07-12 15:15:00	2016-07-12 15:15:00	2016-07-12 15:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
114	1	2016-07-12 20:00:00	2016-07-12 20:00:00	2016-07-12 20:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
115	1	2016-07-12 20:30:00	2016-07-12 20:30:00	2016-07-12 20:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
116	1	2016-07-13 00:15:00	2016-07-13 00:15:00	2016-07-13 00:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
117	1	2016-07-13 03:15:00	2016-07-13 03:15:00	2016-07-13 03:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
118	1	2016-07-13 06:00:00	2016-07-13 06:00:00	2016-07-13 06:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
119	1	2016-07-13 09:15:00	2016-07-13 09:15:00	2016-07-13 09:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
120	1	2016-07-13 09:30:00	2016-07-13 09:30:00	2016-07-13 09:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
121	1	2016-07-13 12:15:00	2016-07-13 12:15:00	2016-07-13 12:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
122	1	2016-07-13 17:00:00	2016-07-13 17:00:00	2016-07-13 17:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
123	1	2016-07-13 18:00:00	2016-07-13 18:00:00	2016-07-13 18:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
124	1	2016-07-13 23:15:00	2016-07-13 23:15:00	2016-07-13 23:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
125	1	2016-07-13 22:00:00	2016-07-13 22:00:00	2016-07-13 22:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
126	1	2016-07-14 00:00:00	2016-07-14 00:00:00	2016-07-14 00:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
127	1	2016-07-14 02:00:00	2016-07-14 02:00:00	2016-07-14 02:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
128	1	2016-07-14 06:15:00	2016-07-14 06:15:00	2016-07-14 06:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
129	1	2016-07-14 10:30:00	2016-07-14 10:30:00	2016-07-14 10:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
130	1	2016-07-14 11:30:00	2016-07-14 11:30:00	2016-07-14 11:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
131	1	2016-07-14 12:30:00	2016-07-14 12:30:00	2016-07-14 12:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
132	1	2016-07-14 16:45:00	2016-07-14 16:45:00	2016-07-14 16:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
133	1	2016-07-14 17:30:00	2016-07-14 17:30:00	2016-07-14 17:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
134	1	2016-07-14 21:30:00	2016-07-14 21:30:00	2016-07-14 21:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
135	1	2016-07-15 01:45:00	2016-07-15 01:45:00	2016-07-15 01:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
136	1	2016-07-15 02:00:00	2016-07-15 02:00:00	2016-07-15 02:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
137	1	2016-07-15 04:00:00	2016-07-15 04:00:00	2016-07-15 04:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
138	1	2016-07-15 06:30:00	2016-07-15 06:30:00	2016-07-15 06:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
139	1	2016-07-15 08:30:00	2016-07-15 08:30:00	2016-07-15 08:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
140	1	2016-07-15 14:00:00	2016-07-15 14:00:00	2016-07-15 14:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
141	1	2016-07-15 15:30:00	2016-07-15 15:30:00	2016-07-15 15:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
142	1	2016-07-15 20:45:00	2016-07-15 20:45:00	2016-07-15 20:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
143	1	2016-07-15 21:15:00	2016-07-15 21:15:00	2016-07-15 21:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
144	1	2016-07-15 22:30:00	2016-07-15 22:30:00	2016-07-15 22:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
145	1	2016-07-15 23:15:00	2016-07-15 23:15:00	2016-07-15 23:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
146	1	2016-07-16 05:15:00	2016-07-16 05:15:00	2016-07-16 05:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
147	1	2016-07-16 05:00:00	2016-07-16 05:00:00	2016-07-16 05:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
148	1	2016-07-16 08:15:00	2016-07-16 08:15:00	2016-07-16 08:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
149	1	2016-07-16 11:30:00	2016-07-16 11:30:00	2016-07-16 11:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
150	1	2016-07-16 15:15:00	2016-07-16 15:15:00	2016-07-16 15:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
151	1	2016-07-16 14:15:00	2016-07-16 14:15:00	2016-07-16 14:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
152	1	2016-07-16 19:45:00	2016-07-16 19:45:00	2016-07-16 19:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
153	1	2016-07-16 20:45:00	2016-07-16 20:45:00	2016-07-16 20:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
154	1	2016-07-17 01:45:00	2016-07-17 01:45:00	2016-07-17 01:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
155	1	2016-07-17 03:45:00	2016-07-17 03:45:00	2016-07-17 03:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
156	1	2016-07-17 05:30:00	2016-07-17 05:30:00	2016-07-17 05:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
157	1	2016-07-17 07:30:00	2016-07-17 07:30:00	2016-07-17 07:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
158	1	2016-07-17 09:15:00	2016-07-17 09:15:00	2016-07-17 09:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
159	1	2016-07-17 13:45:00	2016-07-17 13:45:00	2016-07-17 13:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
160	1	2016-07-17 17:00:00	2016-07-17 17:00:00	2016-07-17 17:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
161	1	2016-07-17 17:45:00	2016-07-17 17:45:00	2016-07-17 17:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
162	1	2016-07-17 21:30:00	2016-07-17 21:30:00	2016-07-17 21:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
163	1	2016-07-17 23:30:00	2016-07-17 23:30:00	2016-07-17 23:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
164	1	2016-07-18 01:00:00	2016-07-18 01:00:00	2016-07-18 01:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
165	1	2016-07-18 05:15:00	2016-07-18 05:15:00	2016-07-18 05:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
166	1	2016-07-18 08:45:00	2016-07-18 08:45:00	2016-07-18 08:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
167	1	2016-07-18 09:30:00	2016-07-18 09:30:00	2016-07-18 09:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
168	1	2016-07-18 10:45:00	2016-07-18 10:45:00	2016-07-18 10:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
169	1	2016-07-18 12:30:00	2016-07-18 12:30:00	2016-07-18 12:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
170	1	2016-07-18 17:00:00	2016-07-18 17:00:00	2016-07-18 17:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
171	1	2016-07-18 17:30:00	2016-07-18 17:30:00	2016-07-18 17:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
172	1	2016-07-18 20:30:00	2016-07-18 20:30:00	2016-07-18 20:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
173	1	2016-07-18 20:45:00	2016-07-18 20:45:00	2016-07-18 20:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
174	1	2016-07-19 00:30:00	2016-07-19 00:30:00	2016-07-19 00:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
175	1	2016-07-19 02:45:00	2016-07-19 02:45:00	2016-07-19 02:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
176	1	2016-07-19 08:00:00	2016-07-19 08:00:00	2016-07-19 08:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
177	1	2016-07-19 09:45:00	2016-07-19 09:45:00	2016-07-19 09:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
178	1	2016-07-19 11:45:00	2016-07-19 11:45:00	2016-07-19 11:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
179	1	2016-07-19 16:00:00	2016-07-19 16:00:00	2016-07-19 16:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
180	1	2016-07-19 17:00:00	2016-07-19 17:00:00	2016-07-19 17:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
181	1	2016-07-19 18:00:00	2016-07-19 18:00:00	2016-07-19 18:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
182	1	2016-07-19 21:30:00	2016-07-19 21:30:00	2016-07-19 21:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
183	1	2016-07-20 01:00:00	2016-07-20 01:00:00	2016-07-20 01:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
184	1	2016-07-20 01:15:00	2016-07-20 01:15:00	2016-07-20 01:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
185	1	2016-07-20 06:15:00	2016-07-20 06:15:00	2016-07-20 06:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
186	1	2016-07-20 07:45:00	2016-07-20 07:45:00	2016-07-20 07:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
187	1	2016-07-20 08:30:00	2016-07-20 08:30:00	2016-07-20 08:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
188	1	2016-07-20 15:00:00	2016-07-20 15:00:00	2016-07-20 15:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
189	1	2016-07-20 17:00:00	2016-07-20 17:00:00	2016-07-20 17:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
190	1	2016-07-20 16:00:00	2016-07-20 16:00:00	2016-07-20 16:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
191	1	2016-07-20 19:00:00	2016-07-20 19:00:00	2016-07-20 19:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
192	1	2016-07-20 22:30:00	2016-07-20 22:30:00	2016-07-20 22:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
193	1	2016-07-21 02:00:00	2016-07-21 02:00:00	2016-07-21 02:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
194	1	2016-07-21 03:15:00	2016-07-21 03:15:00	2016-07-21 03:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
195	1	2016-07-21 04:45:00	2016-07-21 04:45:00	2016-07-21 04:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
196	1	2016-07-21 09:45:00	2016-07-21 09:45:00	2016-07-21 09:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
197	1	2016-07-21 13:30:00	2016-07-21 13:30:00	2016-07-21 13:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
198	1	2016-07-21 15:45:00	2016-07-21 15:45:00	2016-07-21 15:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
199	1	2016-07-21 16:45:00	2016-07-21 16:45:00	2016-07-21 16:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
200	1	2016-07-21 20:45:00	2016-07-21 20:45:00	2016-07-21 20:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
201	1	2016-07-21 22:45:00	2016-07-21 22:45:00	2016-07-21 22:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
202	1	2016-07-22 00:15:00	2016-07-22 00:15:00	2016-07-22 00:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
203	1	2016-07-22 04:30:00	2016-07-22 04:30:00	2016-07-22 04:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
204	1	2016-07-22 04:15:00	2016-07-22 04:15:00	2016-07-22 04:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
205	1	2016-07-22 08:15:00	2016-07-22 08:15:00	2016-07-22 08:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
206	1	2016-07-22 11:45:00	2016-07-22 11:45:00	2016-07-22 11:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
207	1	2016-07-22 12:15:00	2016-07-22 12:15:00	2016-07-22 12:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
208	1	2016-07-22 12:45:00	2016-07-22 12:45:00	2016-07-22 12:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
209	1	2016-07-22 19:15:00	2016-07-22 19:15:00	2016-07-22 19:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
210	1	2016-07-22 20:00:00	2016-07-22 20:00:00	2016-07-22 20:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
211	1	2016-07-22 23:00:00	2016-07-22 23:00:00	2016-07-22 23:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
212	1	2016-07-23 02:30:00	2016-07-23 02:30:00	2016-07-23 02:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
213	1	2016-07-23 04:15:00	2016-07-23 04:15:00	2016-07-23 04:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
214	1	2016-07-23 05:45:00	2016-07-23 05:45:00	2016-07-23 05:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
215	1	2016-07-23 08:00:00	2016-07-23 08:00:00	2016-07-23 08:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
216	1	2016-07-23 12:45:00	2016-07-23 12:45:00	2016-07-23 12:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
217	1	2016-07-23 12:30:00	2016-07-23 12:30:00	2016-07-23 12:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
218	1	2016-07-23 16:30:00	2016-07-23 16:30:00	2016-07-23 16:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
219	1	2016-07-23 17:00:00	2016-07-23 17:00:00	2016-07-23 17:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
220	1	2016-07-23 20:15:00	2016-07-23 20:15:00	2016-07-23 20:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
221	1	2016-07-24 00:15:00	2016-07-24 00:15:00	2016-07-24 00:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
222	1	2016-07-24 02:45:00	2016-07-24 02:45:00	2016-07-24 02:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
223	1	2016-07-24 05:30:00	2016-07-24 05:30:00	2016-07-24 05:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
224	1	2016-07-24 08:30:00	2016-07-24 08:30:00	2016-07-24 08:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
225	1	2016-07-24 10:30:00	2016-07-24 10:30:00	2016-07-24 10:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
226	1	2016-07-24 13:15:00	2016-07-24 13:15:00	2016-07-24 13:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
227	1	2016-07-24 14:15:00	2016-07-24 14:15:00	2016-07-24 14:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
228	1	2016-07-24 17:00:00	2016-07-24 17:00:00	2016-07-24 17:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
229	1	2016-07-24 18:45:00	2016-07-24 18:45:00	2016-07-24 18:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
230	1	2016-07-24 20:15:00	2016-07-24 20:15:00	2016-07-24 20:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
231	1	2016-07-25 00:15:00	2016-07-25 00:15:00	2016-07-25 00:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
232	1	2016-07-25 05:00:00	2016-07-25 05:00:00	2016-07-25 05:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
233	1	2016-07-25 06:45:00	2016-07-25 06:45:00	2016-07-25 06:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
234	1	2016-07-25 07:45:00	2016-07-25 07:45:00	2016-07-25 07:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
235	1	2016-07-25 12:15:00	2016-07-25 12:15:00	2016-07-25 12:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
236	1	2016-07-25 14:45:00	2016-07-25 14:45:00	2016-07-25 14:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
237	1	2016-07-25 13:45:00	2016-07-25 13:45:00	2016-07-25 13:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
238	1	2016-07-25 19:30:00	2016-07-25 19:30:00	2016-07-25 19:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
239	1	2016-07-25 18:30:00	2016-07-25 18:30:00	2016-07-25 18:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
240	1	2016-07-25 23:00:00	2016-07-25 23:00:00	2016-07-25 23:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
241	1	2016-07-26 02:30:00	2016-07-26 02:30:00	2016-07-26 02:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
242	1	2016-07-26 03:45:00	2016-07-26 03:45:00	2016-07-26 03:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
243	1	2016-07-26 07:00:00	2016-07-26 07:00:00	2016-07-26 07:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
244	1	2016-07-26 09:45:00	2016-07-26 09:45:00	2016-07-26 09:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
245	1	2016-07-26 09:00:00	2016-07-26 09:00:00	2016-07-26 09:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
246	1	2016-07-26 13:15:00	2016-07-26 13:15:00	2016-07-26 13:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
247	1	2016-07-26 14:30:00	2016-07-26 14:30:00	2016-07-26 14:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
248	1	2016-07-26 16:30:00	2016-07-26 16:30:00	2016-07-26 16:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
249	1	2016-07-26 20:45:00	2016-07-26 20:45:00	2016-07-26 20:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
250	1	2016-07-27 01:15:00	2016-07-27 01:15:00	2016-07-27 01:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
251	1	2016-07-27 02:15:00	2016-07-27 02:15:00	2016-07-27 02:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
252	1	2016-07-27 06:15:00	2016-07-27 06:15:00	2016-07-27 06:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
253	1	2016-07-27 10:00:00	2016-07-27 10:00:00	2016-07-27 10:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
254	1	2016-07-27 09:30:00	2016-07-27 09:30:00	2016-07-27 09:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
255	1	2016-07-27 11:00:00	2016-07-27 11:00:00	2016-07-27 11:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
256	1	2016-07-27 13:15:00	2016-07-27 13:15:00	2016-07-27 13:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
257	1	2016-07-27 17:30:00	2016-07-27 17:30:00	2016-07-27 17:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
258	1	2016-07-27 19:30:00	2016-07-27 19:30:00	2016-07-27 19:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
259	1	2016-07-27 20:45:00	2016-07-27 20:45:00	2016-07-27 20:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
260	1	2016-07-28 00:15:00	2016-07-28 00:15:00	2016-07-28 00:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
261	1	2016-07-28 04:00:00	2016-07-28 04:00:00	2016-07-28 04:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
262	1	2016-07-28 06:00:00	2016-07-28 06:00:00	2016-07-28 06:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
263	1	2016-07-28 07:30:00	2016-07-28 07:30:00	2016-07-28 07:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
264	1	2016-07-28 10:00:00	2016-07-28 10:00:00	2016-07-28 10:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
265	1	2016-07-28 12:30:00	2016-07-28 12:30:00	2016-07-28 12:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
266	1	2016-07-28 15:30:00	2016-07-28 15:30:00	2016-07-28 15:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
267	1	2016-07-28 20:00:00	2016-07-28 20:00:00	2016-07-28 20:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
268	1	2016-07-28 20:45:00	2016-07-28 20:45:00	2016-07-28 20:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
269	1	2016-07-29 05:15:00	2016-07-29 05:15:00	2016-07-29 05:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
270	1	2016-07-29 00:00:00	2016-07-29 00:00:00	2016-07-29 00:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
271	1	2016-07-29 06:30:00	2016-07-29 06:30:00	2016-07-29 06:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
272	1	2016-07-29 01:15:00	2016-07-29 01:15:00	2016-07-29 01:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
273	1	2016-07-29 15:15:00	2016-07-29 15:15:00	2016-07-29 15:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
274	1	2016-07-29 10:00:00	2016-07-29 10:00:00	2016-07-29 10:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
275	1	2016-07-29 16:30:00	2016-07-29 16:30:00	2016-07-29 16:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
276	1	2016-07-29 11:15:00	2016-07-29 11:15:00	2016-07-29 11:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
277	1	2016-07-30 01:15:00	2016-07-30 01:15:00	2016-07-30 01:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
278	1	2016-07-29 20:00:00	2016-07-29 20:00:00	2016-07-29 20:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
279	1	2016-07-30 02:30:00	2016-07-30 02:30:00	2016-07-30 02:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
280	1	2016-07-29 21:15:00	2016-07-29 21:15:00	2016-07-29 21:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
281	1	2016-07-31 12:45:00	2016-07-31 12:45:00	2016-07-31 12:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
282	1	2016-07-31 07:30:00	2016-07-31 07:30:00	2016-07-31 07:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
283	1	2016-07-30 05:45:00	2016-07-30 05:45:00	2016-07-30 05:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
284	1	2016-07-31 08:45:00	2016-07-31 08:45:00	2016-07-31 08:45:00	\N	1	\N	1	\N	F	\N	\N	1	\N
285	1	2016-07-31 13:30:00	2016-07-31 13:30:00	2016-07-31 13:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
286	1	2016-07-31 20:00:00	2016-07-31 20:00:00	2016-07-31 20:00:00	\N	1	\N	1	\N	F	\N	\N	1	\N
287	1	2016-07-31 15:30:00	2016-07-31 15:30:00	2016-07-31 15:30:00	\N	1	\N	1	\N	F	\N	\N	1	\N
288	1	2016-07-31 22:15:00	2016-07-31 22:15:00	2016-07-31 22:15:00	\N	1	\N	1	\N	F	\N	\N	1	\N
289	2	2016-07-01 01:15:00	2016-07-01 01:15:00	2016-07-01 01:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
290	2	2016-07-01 05:30:00	2016-07-01 05:30:00	2016-07-01 05:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
291	2	2016-07-01 05:00:00	2016-07-01 05:00:00	2016-07-01 05:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
292	2	2016-07-01 10:30:00	2016-07-01 10:30:00	2016-07-01 10:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
293	2	2016-07-01 10:15:00	2016-07-01 10:15:00	2016-07-01 10:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
294	2	2016-07-01 11:45:00	2016-07-01 11:45:00	2016-07-01 11:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
295	2	2016-07-01 16:15:00	2016-07-01 16:15:00	2016-07-01 16:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
296	2	2016-07-01 21:15:00	2016-07-01 21:15:00	2016-07-01 21:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
297	2	2016-07-01 20:15:00	2016-07-01 20:15:00	2016-07-01 20:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
298	2	2016-07-02 01:45:00	2016-07-02 01:45:00	2016-07-02 01:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
299	2	2016-07-02 02:16:00	2016-07-02 02:16:00	2016-07-02 02:16:00	\N	1	\N	1	\N	F	\N	\N	2	\N
300	2	2016-07-02 06:00:00	2016-07-02 06:00:00	2016-07-02 06:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
301	2	2016-07-02 09:30:00	2016-07-02 09:30:00	2016-07-02 09:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
302	2	2016-07-02 11:00:00	2016-07-02 11:00:00	2016-07-02 11:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
303	2	2016-07-02 11:15:00	2016-07-02 11:15:00	2016-07-02 11:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
304	2	2016-07-02 16:00:00	2016-07-02 16:00:00	2016-07-02 16:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
305	2	2016-07-02 18:00:00	2016-07-02 18:00:00	2016-07-02 18:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
306	2	2016-07-02 19:30:00	2016-07-02 19:30:00	2016-07-02 19:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
307	2	2016-07-02 21:15:00	2016-07-02 21:15:00	2016-07-02 21:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
308	2	2016-07-03 01:15:00	2016-07-03 01:15:00	2016-07-03 01:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
309	2	2016-07-03 03:30:00	2016-07-03 03:30:00	2016-07-03 03:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
310	2	2016-07-03 07:00:00	2016-07-03 07:00:00	2016-07-03 07:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
311	2	2016-07-03 07:15:00	2016-07-03 07:15:00	2016-07-03 07:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
312	2	2016-07-03 10:15:00	2016-07-03 10:15:00	2016-07-03 10:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
313	2	2016-07-03 14:16:00	2016-07-03 14:16:00	2016-07-03 14:16:00	\N	1	\N	1	\N	F	\N	\N	2	\N
314	2	2016-07-03 17:01:00	2016-07-03 17:01:00	2016-07-03 17:01:00	\N	1	\N	1	\N	F	\N	\N	2	\N
315	2	2016-07-03 20:00:00	2016-07-03 20:00:00	2016-07-03 20:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
316	2	2016-07-03 21:15:00	2016-07-03 21:15:00	2016-07-03 21:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
317	2	2016-07-04 00:16:00	2016-07-04 00:16:00	2016-07-04 00:16:00	\N	1	\N	1	\N	F	\N	\N	2	\N
318	2	2016-07-04 03:01:00	2016-07-04 03:01:00	2016-07-04 03:01:00	\N	1	\N	1	\N	F	\N	\N	2	\N
319	2	2016-07-04 06:01:00	2016-07-04 06:01:00	2016-07-04 06:01:00	\N	1	\N	1	\N	F	\N	\N	2	\N
320	2	2016-07-04 07:16:00	2016-07-04 07:16:00	2016-07-04 07:16:00	\N	1	\N	1	\N	F	\N	\N	2	\N
321	2	2016-07-04 10:16:00	2016-07-04 10:16:00	2016-07-04 10:16:00	\N	1	\N	1	\N	F	\N	\N	2	\N
322	2	2016-07-04 13:01:00	2016-07-04 13:01:00	2016-07-04 13:01:00	\N	1	\N	1	\N	F	\N	\N	2	\N
323	2	2016-07-04 16:00:00	2016-07-04 16:00:00	2016-07-04 16:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
324	2	2016-07-04 17:15:00	2016-07-04 17:15:00	2016-07-04 17:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
325	2	2016-07-04 20:15:00	2016-07-04 20:15:00	2016-07-04 20:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
326	2	2016-07-04 23:00:00	2016-07-04 23:00:00	2016-07-04 23:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
327	2	2016-07-05 02:01:00	2016-07-05 02:01:00	2016-07-05 02:01:00	\N	1	\N	1	\N	F	\N	\N	2	\N
328	2	2016-07-05 03:16:00	2016-07-05 03:16:00	2016-07-05 03:16:00	\N	1	\N	1	\N	F	\N	\N	2	\N
329	2	2016-07-05 06:16:00	2016-07-05 06:16:00	2016-07-05 06:16:00	\N	1	\N	1	\N	F	\N	\N	2	\N
330	2	2016-07-05 09:00:00	2016-07-05 09:00:00	2016-07-05 09:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
331	2	2016-07-05 12:00:00	2016-07-05 12:00:00	2016-07-05 12:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
332	2	2016-07-05 13:45:00	2016-07-05 13:45:00	2016-07-05 13:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
333	2	2016-07-05 16:15:00	2016-07-05 16:15:00	2016-07-05 16:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
334	2	2016-07-05 18:30:00	2016-07-05 18:30:00	2016-07-05 18:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
335	2	2016-07-05 21:30:00	2016-07-05 21:30:00	2016-07-05 21:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
336	2	2016-07-05 23:45:00	2016-07-05 23:45:00	2016-07-05 23:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
337	2	2016-07-06 02:15:00	2016-07-06 02:15:00	2016-07-06 02:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
338	2	2016-07-06 04:45:00	2016-07-06 04:45:00	2016-07-06 04:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
339	2	2016-07-06 07:15:00	2016-07-06 07:15:00	2016-07-06 07:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
340	2	2016-07-06 09:45:00	2016-07-06 09:45:00	2016-07-06 09:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
341	2	2016-07-06 12:15:00	2016-07-06 12:15:00	2016-07-06 12:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
342	2	2016-07-06 14:00:00	2016-07-06 14:00:00	2016-07-06 14:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
343	2	2016-07-06 17:15:00	2016-07-06 17:15:00	2016-07-06 17:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
344	2	2016-07-06 19:30:00	2016-07-06 19:30:00	2016-07-06 19:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
345	2	2016-07-06 22:00:00	2016-07-06 22:00:00	2016-07-06 22:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
346	2	2016-07-07 00:45:00	2016-07-07 00:45:00	2016-07-07 00:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
347	2	2016-07-07 03:15:00	2016-07-07 03:15:00	2016-07-07 03:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
348	2	2016-07-07 06:00:00	2016-07-07 06:00:00	2016-07-07 06:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
349	2	2016-07-07 09:00:00	2016-07-07 09:00:00	2016-07-07 09:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
350	2	2016-07-07 10:45:00	2016-07-07 10:45:00	2016-07-07 10:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
351	2	2016-07-07 17:15:00	2016-07-07 17:15:00	2016-07-07 17:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
352	2	2016-07-07 13:45:00	2016-07-07 13:45:00	2016-07-07 13:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
353	2	2016-07-07 19:15:00	2016-07-07 19:15:00	2016-07-07 19:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
354	2	2016-07-07 21:15:00	2016-07-07 21:15:00	2016-07-07 21:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
355	2	2016-07-08 01:15:00	2016-07-08 01:15:00	2016-07-08 01:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
356	2	2016-07-08 01:00:00	2016-07-08 01:00:00	2016-07-08 01:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
357	2	2016-07-08 06:15:00	2016-07-08 06:15:00	2016-07-08 06:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
358	2	2016-07-08 04:45:00	2016-07-08 04:45:00	2016-07-08 04:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
359	2	2016-07-08 10:45:00	2016-07-08 10:45:00	2016-07-08 10:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
360	2	2016-07-08 09:00:00	2016-07-08 09:00:00	2016-07-08 09:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
361	2	2016-07-08 14:45:00	2016-07-08 14:45:00	2016-07-08 14:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
362	2	2016-07-08 17:45:00	2016-07-08 17:45:00	2016-07-08 17:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
363	2	2016-07-08 16:30:00	2016-07-08 16:30:00	2016-07-08 16:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
364	2	2016-07-08 23:00:00	2016-07-08 23:00:00	2016-07-08 23:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
365	2	2016-07-09 01:15:00	2016-07-09 01:15:00	2016-07-09 01:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
366	2	2016-07-09 05:15:00	2016-07-09 05:15:00	2016-07-09 05:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
367	2	2016-07-09 03:15:00	2016-07-09 03:15:00	2016-07-09 03:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
368	2	2016-07-09 06:45:00	2016-07-09 06:45:00	2016-07-09 06:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
369	2	2016-07-09 08:15:00	2016-07-09 08:15:00	2016-07-09 08:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
370	2	2016-07-09 14:45:00	2016-07-09 14:45:00	2016-07-09 14:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
371	2	2016-07-09 12:45:00	2016-07-09 12:45:00	2016-07-09 12:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
372	2	2016-07-09 20:45:00	2016-07-09 20:45:00	2016-07-09 20:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
373	2	2016-07-09 19:00:00	2016-07-09 19:00:00	2016-07-09 19:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
374	2	2016-07-10 00:15:00	2016-07-10 00:15:00	2016-07-10 00:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
375	2	2016-07-09 23:15:00	2016-07-09 23:15:00	2016-07-09 23:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
376	2	2016-07-10 06:30:00	2016-07-10 06:30:00	2016-07-10 06:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
377	2	2016-07-10 03:45:00	2016-07-10 03:45:00	2016-07-10 03:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
378	2	2016-07-10 11:30:00	2016-07-10 11:30:00	2016-07-10 11:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
379	2	2016-07-10 10:00:00	2016-07-10 10:00:00	2016-07-10 10:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
380	2	2016-07-10 15:00:00	2016-07-10 15:00:00	2016-07-10 15:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
381	2	2016-07-10 14:00:00	2016-07-10 14:00:00	2016-07-10 14:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
382	2	2016-07-10 18:30:00	2016-07-10 18:30:00	2016-07-10 18:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
383	2	2016-07-10 18:45:00	2016-07-10 18:45:00	2016-07-10 18:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
384	2	2016-07-11 00:30:00	2016-07-11 00:30:00	2016-07-11 00:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
385	2	2016-07-10 23:30:00	2016-07-10 23:30:00	2016-07-10 23:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
386	2	2016-07-11 06:00:00	2016-07-11 06:00:00	2016-07-11 06:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
387	2	2016-07-11 04:30:00	2016-07-11 04:30:00	2016-07-11 04:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
388	2	2016-07-11 11:30:00	2016-07-11 11:30:00	2016-07-11 11:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
389	2	2016-07-11 12:30:00	2016-07-11 12:30:00	2016-07-11 12:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
390	2	2016-07-11 16:30:00	2016-07-11 16:30:00	2016-07-11 16:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
391	2	2016-07-11 15:15:00	2016-07-11 15:15:00	2016-07-11 15:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
392	2	2016-07-11 19:45:00	2016-07-11 19:45:00	2016-07-11 19:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
393	2	2016-07-11 22:00:00	2016-07-11 22:00:00	2016-07-11 22:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
394	2	2016-07-12 00:45:00	2016-07-12 00:45:00	2016-07-12 00:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
395	2	2016-07-12 03:00:00	2016-07-12 03:00:00	2016-07-12 03:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
396	2	2016-07-12 04:30:00	2016-07-12 04:30:00	2016-07-12 04:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
397	2	2016-07-12 06:45:00	2016-07-12 06:45:00	2016-07-12 06:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
398	2	2016-07-12 11:00:00	2016-07-12 11:00:00	2016-07-12 11:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
399	2	2016-07-12 10:45:00	2016-07-12 10:45:00	2016-07-12 10:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
400	2	2016-07-12 18:00:00	2016-07-12 18:00:00	2016-07-12 18:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
401	2	2016-07-12 16:45:00	2016-07-12 16:45:00	2016-07-12 16:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
402	2	2016-07-12 20:15:00	2016-07-12 20:15:00	2016-07-12 20:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
403	2	2016-07-12 23:15:00	2016-07-12 23:15:00	2016-07-12 23:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
404	2	2016-07-13 01:45:00	2016-07-13 01:45:00	2016-07-13 01:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
405	2	2016-07-13 01:15:00	2016-07-13 01:15:00	2016-07-13 01:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
406	2	2016-07-13 09:45:00	2016-07-13 09:45:00	2016-07-13 09:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
407	2	2016-07-13 07:15:00	2016-07-13 07:15:00	2016-07-13 07:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
408	2	2016-07-13 10:30:00	2016-07-13 10:30:00	2016-07-13 10:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
409	2	2016-07-13 14:45:00	2016-07-13 14:45:00	2016-07-13 14:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
410	2	2016-07-13 16:00:00	2016-07-13 16:00:00	2016-07-13 16:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
411	2	2016-07-13 18:30:00	2016-07-13 18:30:00	2016-07-13 18:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
412	2	2016-07-13 20:45:00	2016-07-13 20:45:00	2016-07-13 20:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
413	2	2016-07-13 23:30:00	2016-07-13 23:30:00	2016-07-13 23:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
414	2	2016-07-14 02:45:00	2016-07-14 02:45:00	2016-07-14 02:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
415	2	2016-07-14 04:45:00	2016-07-14 04:45:00	2016-07-14 04:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
416	2	2016-07-14 09:00:00	2016-07-14 09:00:00	2016-07-14 09:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
417	2	2016-07-14 07:15:00	2016-07-14 07:15:00	2016-07-14 07:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
418	2	2016-07-14 16:00:00	2016-07-14 16:00:00	2016-07-14 16:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
419	2	2016-07-14 15:45:00	2016-07-14 15:45:00	2016-07-14 15:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
420	2	2016-07-14 20:00:00	2016-07-14 20:00:00	2016-07-14 20:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
421	2	2016-07-14 19:00:00	2016-07-14 19:00:00	2016-07-14 19:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
422	2	2016-07-15 01:30:00	2016-07-15 01:30:00	2016-07-15 01:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
423	2	2016-07-15 01:00:00	2016-07-15 01:00:00	2016-07-15 01:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
424	2	2016-07-15 03:30:00	2016-07-15 03:30:00	2016-07-15 03:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
425	2	2016-07-15 04:15:00	2016-07-15 04:15:00	2016-07-15 04:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
426	2	2016-07-15 10:30:00	2016-07-15 10:30:00	2016-07-15 10:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
427	2	2016-07-15 11:00:00	2016-07-15 11:00:00	2016-07-15 11:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
428	2	2016-07-15 14:00:00	2016-07-15 14:00:00	2016-07-15 14:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
429	2	2016-07-15 14:15:00	2016-07-15 14:15:00	2016-07-15 14:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
430	2	2016-07-15 20:00:00	2016-07-15 20:00:00	2016-07-15 20:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
431	2	2016-07-15 19:30:00	2016-07-15 19:30:00	2016-07-15 19:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
432	2	2016-07-16 01:30:00	2016-07-16 01:30:00	2016-07-16 01:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
433	2	2016-07-16 04:00:00	2016-07-16 04:00:00	2016-07-16 04:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
434	2	2016-07-16 06:45:00	2016-07-16 06:45:00	2016-07-16 06:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
435	2	2016-07-16 05:15:00	2016-07-16 05:15:00	2016-07-16 05:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
436	2	2016-07-16 08:30:00	2016-07-16 08:30:00	2016-07-16 08:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
437	2	2016-07-16 13:00:00	2016-07-16 13:00:00	2016-07-16 13:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
438	2	2016-07-16 14:45:00	2016-07-16 14:45:00	2016-07-16 14:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
439	2	2016-07-16 17:30:00	2016-07-16 17:30:00	2016-07-16 17:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
440	2	2016-07-16 21:15:00	2016-07-16 21:15:00	2016-07-16 21:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
441	2	2016-07-16 21:00:00	2016-07-16 21:00:00	2016-07-16 21:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
442	2	2016-07-17 01:15:00	2016-07-17 01:15:00	2016-07-17 01:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
443	2	2016-07-17 01:45:00	2016-07-17 01:45:00	2016-07-17 01:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
444	2	2016-07-17 08:15:00	2016-07-17 08:15:00	2016-07-17 08:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
445	2	2016-07-17 06:45:00	2016-07-17 06:45:00	2016-07-17 06:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
446	2	2016-07-17 12:00:00	2016-07-17 12:00:00	2016-07-17 12:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
447	2	2016-07-17 12:45:00	2016-07-17 12:45:00	2016-07-17 12:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
448	2	2016-07-17 14:30:00	2016-07-17 14:30:00	2016-07-17 14:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
449	2	2016-07-17 18:00:00	2016-07-17 18:00:00	2016-07-17 18:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
450	2	2016-07-17 21:45:00	2016-07-17 21:45:00	2016-07-17 21:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
451	2	2016-07-17 22:00:00	2016-07-17 22:00:00	2016-07-17 22:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
452	2	2016-07-18 03:45:00	2016-07-18 03:45:00	2016-07-18 03:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
453	2	2016-07-18 02:15:00	2016-07-18 02:15:00	2016-07-18 02:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
454	2	2016-07-18 07:15:00	2016-07-18 07:15:00	2016-07-18 07:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
455	2	2016-07-18 06:45:00	2016-07-18 06:45:00	2016-07-18 06:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
456	2	2016-07-18 11:30:00	2016-07-18 11:30:00	2016-07-18 11:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
457	2	2016-07-18 15:15:00	2016-07-18 15:15:00	2016-07-18 15:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
458	2	2016-07-18 17:00:00	2016-07-18 17:00:00	2016-07-18 17:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
459	2	2016-07-18 19:30:00	2016-07-18 19:30:00	2016-07-18 19:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
460	2	2016-07-18 23:00:00	2016-07-18 23:00:00	2016-07-18 23:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
461	2	2016-07-19 01:30:00	2016-07-19 01:30:00	2016-07-19 01:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
462	2	2016-07-19 05:30:00	2016-07-19 05:30:00	2016-07-19 05:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
463	2	2016-07-19 02:45:00	2016-07-19 02:45:00	2016-07-19 02:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
464	2	2016-07-19 07:15:00	2016-07-19 07:15:00	2016-07-19 07:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
465	2	2016-07-19 10:00:00	2016-07-19 10:00:00	2016-07-19 10:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
466	2	2016-07-19 13:15:00	2016-07-19 13:15:00	2016-07-19 13:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
467	2	2016-07-19 14:00:00	2016-07-19 14:00:00	2016-07-19 14:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
468	2	2016-07-19 18:30:00	2016-07-19 18:30:00	2016-07-19 18:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
469	2	2016-07-19 18:45:00	2016-07-19 18:45:00	2016-07-19 18:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
470	2	2016-07-19 23:00:00	2016-07-19 23:00:00	2016-07-19 23:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
471	2	2016-07-20 01:15:00	2016-07-20 01:15:00	2016-07-20 01:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
472	2	2016-07-20 05:00:00	2016-07-20 05:00:00	2016-07-20 05:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
473	2	2016-07-20 06:30:00	2016-07-20 06:30:00	2016-07-20 06:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
474	2	2016-07-20 09:15:00	2016-07-20 09:15:00	2016-07-20 09:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
475	2	2016-07-20 11:00:00	2016-07-20 11:00:00	2016-07-20 11:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
476	2	2016-07-20 16:00:00	2016-07-20 16:00:00	2016-07-20 16:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
477	2	2016-07-20 15:30:00	2016-07-20 15:30:00	2016-07-20 15:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
478	2	2016-07-20 18:30:00	2016-07-20 18:30:00	2016-07-20 18:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
479	2	2016-07-20 23:00:00	2016-07-20 23:00:00	2016-07-20 23:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
480	2	2016-07-21 01:15:00	2016-07-21 01:15:00	2016-07-21 01:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
481	2	2016-07-21 03:45:00	2016-07-21 03:45:00	2016-07-21 03:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
482	2	2016-07-21 07:30:00	2016-07-21 07:30:00	2016-07-21 07:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
483	2	2016-07-21 08:15:00	2016-07-21 08:15:00	2016-07-21 08:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
484	2	2016-07-21 09:30:00	2016-07-21 09:30:00	2016-07-21 09:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
485	2	2016-07-21 10:15:00	2016-07-21 10:15:00	2016-07-21 10:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
486	2	2016-07-21 17:00:00	2016-07-21 17:00:00	2016-07-21 17:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
487	2	2016-07-21 19:15:00	2016-07-21 19:15:00	2016-07-21 19:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
488	2	2016-07-21 21:00:00	2016-07-21 21:00:00	2016-07-21 21:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
489	2	2016-07-21 20:45:00	2016-07-21 20:45:00	2016-07-21 20:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
490	2	2016-07-22 02:45:00	2016-07-22 02:45:00	2016-07-22 02:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
491	2	2016-07-22 03:45:00	2016-07-22 03:45:00	2016-07-22 03:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
492	2	2016-07-22 04:00:00	2016-07-22 04:00:00	2016-07-22 04:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
493	2	2016-07-22 06:15:00	2016-07-22 06:15:00	2016-07-22 06:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
494	2	2016-07-22 12:15:00	2016-07-22 12:15:00	2016-07-22 12:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
495	2	2016-07-22 12:45:00	2016-07-22 12:45:00	2016-07-22 12:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
496	2	2016-07-22 15:00:00	2016-07-22 15:00:00	2016-07-22 15:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
497	2	2016-07-22 17:00:00	2016-07-22 17:00:00	2016-07-22 17:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
498	2	2016-07-22 22:45:00	2016-07-22 22:45:00	2016-07-22 22:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
499	2	2016-07-22 21:15:00	2016-07-22 21:15:00	2016-07-22 21:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
500	2	2016-07-23 02:45:00	2016-07-23 02:45:00	2016-07-23 02:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
501	2	2016-07-23 04:30:00	2016-07-23 04:30:00	2016-07-23 04:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
502	2	2016-07-23 08:30:00	2016-07-23 08:30:00	2016-07-23 08:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
503	2	2016-07-23 09:15:00	2016-07-23 09:15:00	2016-07-23 09:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
504	2	2016-07-23 11:30:00	2016-07-23 11:30:00	2016-07-23 11:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
505	2	2016-07-23 14:45:00	2016-07-23 14:45:00	2016-07-23 14:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
506	2	2016-07-23 15:00:00	2016-07-23 15:00:00	2016-07-23 15:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
507	2	2016-07-23 21:00:00	2016-07-23 21:00:00	2016-07-23 21:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
508	2	2016-07-24 00:15:00	2016-07-24 00:15:00	2016-07-24 00:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
509	2	2016-07-23 22:15:00	2016-07-23 22:15:00	2016-07-23 22:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
510	2	2016-07-24 04:00:00	2016-07-24 04:00:00	2016-07-24 04:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
511	2	2016-07-24 07:15:00	2016-07-24 07:15:00	2016-07-24 07:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
512	2	2016-07-24 07:30:00	2016-07-24 07:30:00	2016-07-24 07:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
513	2	2016-07-24 12:30:00	2016-07-24 12:30:00	2016-07-24 12:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
514	2	2016-07-24 13:00:00	2016-07-24 13:00:00	2016-07-24 13:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
515	2	2016-07-24 14:00:00	2016-07-24 14:00:00	2016-07-24 14:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
516	2	2016-07-24 16:30:00	2016-07-24 16:30:00	2016-07-24 16:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
517	2	2016-07-24 20:15:00	2016-07-24 20:15:00	2016-07-24 20:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
518	2	2016-07-24 22:00:00	2016-07-24 22:00:00	2016-07-24 22:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
519	2	2016-07-24 23:30:00	2016-07-24 23:30:00	2016-07-24 23:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
520	2	2016-07-25 05:15:00	2016-07-25 05:15:00	2016-07-25 05:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
521	2	2016-07-25 03:15:00	2016-07-25 03:15:00	2016-07-25 03:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
522	2	2016-07-25 10:15:00	2016-07-25 10:15:00	2016-07-25 10:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
523	2	2016-07-25 13:30:00	2016-07-25 13:30:00	2016-07-25 13:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
524	2	2016-07-25 16:15:00	2016-07-25 16:15:00	2016-07-25 16:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
525	2	2016-07-25 16:45:00	2016-07-25 16:45:00	2016-07-25 16:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
526	2	2016-07-25 18:15:00	2016-07-25 18:15:00	2016-07-25 18:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
527	2	2016-07-25 19:15:00	2016-07-25 19:15:00	2016-07-25 19:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
528	2	2016-07-26 00:30:00	2016-07-26 00:30:00	2016-07-26 00:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
529	2	2016-07-26 03:15:00	2016-07-26 03:15:00	2016-07-26 03:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
530	2	2016-07-26 02:15:00	2016-07-26 02:15:00	2016-07-26 02:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
531	2	2016-07-26 07:15:00	2016-07-26 07:15:00	2016-07-26 07:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
532	2	2016-07-26 10:00:00	2016-07-26 10:00:00	2016-07-26 10:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
533	2	2016-07-26 14:00:00	2016-07-26 14:00:00	2016-07-26 14:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
534	2	2016-07-26 15:45:00	2016-07-26 15:45:00	2016-07-26 15:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
535	2	2016-07-26 16:00:00	2016-07-26 16:00:00	2016-07-26 16:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
536	2	2016-07-26 21:45:00	2016-07-26 21:45:00	2016-07-26 21:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
537	2	2016-07-26 20:30:00	2016-07-26 20:30:00	2016-07-26 20:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
538	2	2016-07-26 23:45:00	2016-07-26 23:45:00	2016-07-26 23:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
539	2	2016-07-27 02:30:00	2016-07-27 02:30:00	2016-07-27 02:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
540	2	2016-07-27 07:45:00	2016-07-27 07:45:00	2016-07-27 07:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
541	2	2016-07-27 09:15:00	2016-07-27 09:15:00	2016-07-27 09:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
542	2	2016-07-27 08:15:00	2016-07-27 08:15:00	2016-07-27 08:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
543	2	2016-07-27 11:45:00	2016-07-27 11:45:00	2016-07-27 11:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
544	2	2016-07-27 15:45:00	2016-07-27 15:45:00	2016-07-27 15:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
545	2	2016-07-27 17:45:00	2016-07-27 17:45:00	2016-07-27 17:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
546	2	2016-07-27 21:00:00	2016-07-27 21:00:00	2016-07-27 21:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
547	2	2016-07-27 22:15:00	2016-07-27 22:15:00	2016-07-27 22:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
548	2	2016-07-28 01:00:00	2016-07-28 01:00:00	2016-07-28 01:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
549	2	2016-07-28 04:00:00	2016-07-28 04:00:00	2016-07-28 04:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
550	2	2016-07-28 07:30:00	2016-07-28 07:30:00	2016-07-28 07:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
551	2	2016-07-28 09:30:00	2016-07-28 09:30:00	2016-07-28 09:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
552	2	2016-07-28 10:00:00	2016-07-28 10:00:00	2016-07-28 10:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
553	2	2016-07-28 12:45:00	2016-07-28 12:45:00	2016-07-28 12:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
554	2	2016-07-28 14:30:00	2016-07-28 14:30:00	2016-07-28 14:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
555	2	2016-07-28 20:15:00	2016-07-28 20:15:00	2016-07-28 20:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
556	2	2016-07-28 21:45:00	2016-07-28 21:45:00	2016-07-28 21:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
557	2	2016-07-29 07:15:00	2016-07-29 07:15:00	2016-07-29 07:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
558	2	2016-07-29 02:00:00	2016-07-29 02:00:00	2016-07-29 02:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
559	2	2016-07-29 06:00:00	2016-07-29 06:00:00	2016-07-29 06:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
560	2	2016-07-29 03:15:00	2016-07-29 03:15:00	2016-07-29 03:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
561	2	2016-07-29 17:15:00	2016-07-29 17:15:00	2016-07-29 17:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
562	2	2016-07-29 12:00:00	2016-07-29 12:00:00	2016-07-29 12:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
563	2	2016-07-29 16:00:00	2016-07-29 16:00:00	2016-07-29 16:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
564	2	2016-07-29 13:15:00	2016-07-29 13:15:00	2016-07-29 13:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
565	2	2016-07-30 03:15:00	2016-07-30 03:15:00	2016-07-30 03:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
566	2	2016-07-29 22:00:00	2016-07-29 22:00:00	2016-07-29 22:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
567	2	2016-07-30 02:00:00	2016-07-30 02:00:00	2016-07-30 02:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
568	2	2016-07-29 23:15:00	2016-07-29 23:15:00	2016-07-29 23:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
569	2	2016-07-30 06:30:00	2016-07-30 06:30:00	2016-07-30 06:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
570	2	2016-07-31 09:30:00	2016-07-31 09:30:00	2016-07-31 09:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
571	2	2016-07-30 05:15:00	2016-07-30 05:15:00	2016-07-30 05:15:00	\N	1	\N	1	\N	F	\N	\N	2	\N
572	2	2016-07-31 10:45:00	2016-07-31 10:45:00	2016-07-31 10:45:00	\N	1	\N	1	\N	F	\N	\N	2	\N
573	2	2016-07-31 15:30:00	2016-07-31 15:30:00	2016-07-31 15:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
574	2	2016-07-31 19:30:00	2016-07-31 19:30:00	2016-07-31 19:30:00	\N	1	\N	1	\N	F	\N	\N	2	\N
575	2	2016-07-31 15:00:00	2016-07-31 15:00:00	2016-07-31 15:00:00	\N	1	\N	1	\N	F	\N	\N	2	\N
576	2	2016-07-31 21:46:00	2016-07-31 21:46:00	2016-07-31 21:46:00	\N	1	\N	1	\N	F	\N	\N	2	\N
577	3	2016-07-01 03:15:00	2016-07-01 03:15:00	2016-07-01 03:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
578	3	2016-07-01 04:00:00	2016-07-01 04:00:00	2016-07-01 04:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
579	3	2016-07-01 09:30:00	2016-07-01 09:30:00	2016-07-01 09:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
580	3	2016-07-01 09:00:00	2016-07-01 09:00:00	2016-07-01 09:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
581	3	2016-07-01 12:30:00	2016-07-01 12:30:00	2016-07-01 12:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
582	3	2016-07-01 15:30:00	2016-07-01 15:30:00	2016-07-01 15:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
583	3	2016-07-01 17:30:00	2016-07-01 17:30:00	2016-07-01 17:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
584	3	2016-07-01 19:00:00	2016-07-01 19:00:00	2016-07-01 19:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
585	3	2016-07-01 21:30:00	2016-07-01 21:30:00	2016-07-01 21:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
586	3	2016-07-02 01:15:00	2016-07-02 01:15:00	2016-07-02 01:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
587	3	2016-07-02 04:15:00	2016-07-02 04:15:00	2016-07-02 04:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
588	3	2016-07-02 05:00:00	2016-07-02 05:00:00	2016-07-02 05:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
589	3	2016-07-02 10:30:00	2016-07-02 10:30:00	2016-07-02 10:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
590	3	2016-07-02 10:00:00	2016-07-02 10:00:00	2016-07-02 10:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
591	3	2016-07-02 14:15:00	2016-07-02 14:15:00	2016-07-02 14:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
592	3	2016-07-02 15:00:00	2016-07-02 15:00:00	2016-07-02 15:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
593	3	2016-07-02 17:45:00	2016-07-02 17:45:00	2016-07-02 17:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
594	3	2016-07-02 20:30:00	2016-07-02 20:30:00	2016-07-02 20:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
595	3	2016-07-02 23:15:00	2016-07-02 23:15:00	2016-07-02 23:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
596	3	2016-07-03 02:30:00	2016-07-03 02:30:00	2016-07-03 02:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
597	3	2016-07-03 04:00:00	2016-07-03 04:00:00	2016-07-03 04:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
598	3	2016-07-03 06:45:00	2016-07-03 06:45:00	2016-07-03 06:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
599	3	2016-07-03 07:45:00	2016-07-03 07:45:00	2016-07-03 07:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
600	3	2016-07-03 12:45:00	2016-07-03 12:45:00	2016-07-03 12:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
601	3	2016-07-03 14:16:00	2016-07-03 14:16:00	2016-07-03 14:16:00	\N	1	\N	1	\N	F	\N	\N	3	\N
602	3	2016-07-03 16:46:00	2016-07-03 16:46:00	2016-07-03 16:46:00	\N	1	\N	1	\N	F	\N	\N	3	\N
603	3	2016-07-03 19:45:00	2016-07-03 19:45:00	2016-07-03 19:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
604	3	2016-07-03 21:00:00	2016-07-03 21:00:00	2016-07-03 21:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
605	3	2016-07-04 00:01:00	2016-07-04 00:01:00	2016-07-04 00:01:00	\N	1	\N	1	\N	F	\N	\N	3	\N
606	3	2016-07-04 02:46:00	2016-07-04 02:46:00	2016-07-04 02:46:00	\N	1	\N	1	\N	F	\N	\N	3	\N
607	3	2016-07-04 05:46:00	2016-07-04 05:46:00	2016-07-04 05:46:00	\N	1	\N	1	\N	F	\N	\N	3	\N
608	3	2016-07-04 07:01:00	2016-07-04 07:01:00	2016-07-04 07:01:00	\N	1	\N	1	\N	F	\N	\N	3	\N
609	3	2016-07-04 10:01:00	2016-07-04 10:01:00	2016-07-04 10:01:00	\N	1	\N	1	\N	F	\N	\N	3	\N
610	3	2016-07-04 12:46:00	2016-07-04 12:46:00	2016-07-04 12:46:00	\N	1	\N	1	\N	F	\N	\N	3	\N
611	3	2016-07-04 15:45:00	2016-07-04 15:45:00	2016-07-04 15:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
612	3	2016-07-04 17:00:00	2016-07-04 17:00:00	2016-07-04 17:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
613	3	2016-07-04 20:00:00	2016-07-04 20:00:00	2016-07-04 20:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
614	3	2016-07-04 22:45:00	2016-07-04 22:45:00	2016-07-04 22:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
615	3	2016-07-05 01:46:00	2016-07-05 01:46:00	2016-07-05 01:46:00	\N	1	\N	1	\N	F	\N	\N	3	\N
616	3	2016-07-05 03:01:00	2016-07-05 03:01:00	2016-07-05 03:01:00	\N	1	\N	1	\N	F	\N	\N	3	\N
617	3	2016-07-05 06:01:00	2016-07-05 06:01:00	2016-07-05 06:01:00	\N	1	\N	1	\N	F	\N	\N	3	\N
618	3	2016-07-05 08:45:00	2016-07-05 08:45:00	2016-07-05 08:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
619	3	2016-07-05 11:45:00	2016-07-05 11:45:00	2016-07-05 11:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
620	3	2016-07-05 14:00:00	2016-07-05 14:00:00	2016-07-05 14:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
621	3	2016-07-05 16:15:00	2016-07-05 16:15:00	2016-07-05 16:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
622	3	2016-07-05 18:45:00	2016-07-05 18:45:00	2016-07-05 18:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
623	3	2016-07-05 21:15:00	2016-07-05 21:15:00	2016-07-05 21:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
624	3	2016-07-05 23:45:00	2016-07-05 23:45:00	2016-07-05 23:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
625	3	2016-07-06 02:15:00	2016-07-06 02:15:00	2016-07-06 02:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
626	3	2016-07-06 04:45:00	2016-07-06 04:45:00	2016-07-06 04:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
627	3	2016-07-06 07:15:00	2016-07-06 07:15:00	2016-07-06 07:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
628	3	2016-07-06 09:45:00	2016-07-06 09:45:00	2016-07-06 09:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
629	3	2016-07-06 11:30:00	2016-07-06 11:30:00	2016-07-06 11:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
630	3	2016-07-06 15:00:00	2016-07-06 15:00:00	2016-07-06 15:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
631	3	2016-07-06 18:00:00	2016-07-06 18:00:00	2016-07-06 18:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
632	3	2016-07-06 19:30:00	2016-07-06 19:30:00	2016-07-06 19:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
633	3	2016-07-06 22:00:00	2016-07-06 22:00:00	2016-07-06 22:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
634	3	2016-07-07 00:45:00	2016-07-07 00:45:00	2016-07-07 00:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
635	3	2016-07-07 03:15:00	2016-07-07 03:15:00	2016-07-07 03:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
636	3	2016-07-07 06:00:00	2016-07-07 06:00:00	2016-07-07 06:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
637	3	2016-07-07 09:00:00	2016-07-07 09:00:00	2016-07-07 09:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
638	3	2016-07-07 10:45:00	2016-07-07 10:45:00	2016-07-07 10:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
639	3	2016-07-07 15:45:00	2016-07-07 15:45:00	2016-07-07 15:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
640	3	2016-07-07 15:15:00	2016-07-07 15:15:00	2016-07-07 15:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
641	3	2016-07-07 21:45:00	2016-07-07 21:45:00	2016-07-07 21:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
642	3	2016-07-07 19:45:00	2016-07-07 19:45:00	2016-07-07 19:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
643	3	2016-07-07 23:15:00	2016-07-07 23:15:00	2016-07-07 23:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
644	3	2016-07-08 00:15:00	2016-07-08 00:15:00	2016-07-08 00:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
645	3	2016-07-08 05:45:00	2016-07-08 05:45:00	2016-07-08 05:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
646	3	2016-07-08 04:45:00	2016-07-08 04:45:00	2016-07-08 04:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
647	3	2016-07-08 09:30:00	2016-07-08 09:30:00	2016-07-08 09:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
648	3	2016-07-08 13:15:00	2016-07-08 13:15:00	2016-07-08 13:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
649	3	2016-07-08 14:00:00	2016-07-08 14:00:00	2016-07-08 14:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
650	3	2016-07-08 19:45:00	2016-07-08 19:45:00	2016-07-08 19:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
651	3	2016-07-08 20:00:00	2016-07-08 20:00:00	2016-07-08 20:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
652	3	2016-07-09 00:30:00	2016-07-09 00:30:00	2016-07-09 00:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
653	3	2016-07-08 23:45:00	2016-07-08 23:45:00	2016-07-08 23:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
654	3	2016-07-09 05:00:00	2016-07-09 05:00:00	2016-07-09 05:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
655	3	2016-07-09 03:00:00	2016-07-09 03:00:00	2016-07-09 03:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
656	3	2016-07-09 11:15:00	2016-07-09 11:15:00	2016-07-09 11:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
657	3	2016-07-09 11:30:00	2016-07-09 11:30:00	2016-07-09 11:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
658	3	2016-07-09 12:00:00	2016-07-09 12:00:00	2016-07-09 12:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
659	3	2016-07-09 15:00:00	2016-07-09 15:00:00	2016-07-09 15:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
660	3	2016-07-09 19:45:00	2016-07-09 19:45:00	2016-07-09 19:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
661	3	2016-07-09 22:00:00	2016-07-09 22:00:00	2016-07-09 22:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
662	3	2016-07-10 01:00:00	2016-07-10 01:00:00	2016-07-10 01:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
663	3	2016-07-10 00:45:00	2016-07-10 00:45:00	2016-07-10 00:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
664	3	2016-07-10 07:15:00	2016-07-10 07:15:00	2016-07-10 07:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
665	3	2016-07-10 04:30:00	2016-07-10 04:30:00	2016-07-10 04:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
666	3	2016-07-10 12:15:00	2016-07-10 12:15:00	2016-07-10 12:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
667	3	2016-07-10 08:15:00	2016-07-10 08:15:00	2016-07-10 08:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
668	3	2016-07-10 13:30:00	2016-07-10 13:30:00	2016-07-10 13:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
669	3	2016-07-10 14:45:00	2016-07-10 14:45:00	2016-07-10 14:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
670	3	2016-07-10 20:30:00	2016-07-10 20:30:00	2016-07-10 20:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
671	3	2016-07-10 22:00:00	2016-07-10 22:00:00	2016-07-10 22:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
672	3	2016-07-11 00:45:00	2016-07-11 00:45:00	2016-07-11 00:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
673	3	2016-07-11 01:15:00	2016-07-11 01:15:00	2016-07-11 01:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
674	3	2016-07-11 04:15:00	2016-07-11 04:15:00	2016-07-11 04:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
675	3	2016-07-11 08:30:00	2016-07-11 08:30:00	2016-07-11 08:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
676	3	2016-07-11 12:45:00	2016-07-11 12:45:00	2016-07-11 12:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
677	3	2016-07-11 10:00:00	2016-07-11 10:00:00	2016-07-11 10:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
678	3	2016-07-11 13:30:00	2016-07-11 13:30:00	2016-07-11 13:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
679	3	2016-07-11 15:00:00	2016-07-11 15:00:00	2016-07-11 15:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
680	3	2016-07-11 19:15:00	2016-07-11 19:15:00	2016-07-11 19:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
681	3	2016-07-11 20:30:00	2016-07-11 20:30:00	2016-07-11 20:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
682	3	2016-07-12 03:30:00	2016-07-12 03:30:00	2016-07-12 03:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
683	3	2016-07-12 02:00:00	2016-07-12 02:00:00	2016-07-12 02:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
684	3	2016-07-12 07:15:00	2016-07-12 07:15:00	2016-07-12 07:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
685	3	2016-07-12 09:45:00	2016-07-12 09:45:00	2016-07-12 09:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
686	3	2016-07-12 13:00:00	2016-07-12 13:00:00	2016-07-12 13:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
687	3	2016-07-12 11:00:00	2016-07-12 11:00:00	2016-07-12 11:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
688	3	2016-07-12 17:00:00	2016-07-12 17:00:00	2016-07-12 17:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
689	3	2016-07-12 19:15:00	2016-07-12 19:15:00	2016-07-12 19:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
690	3	2016-07-12 20:46:00	2016-07-12 20:46:00	2016-07-12 20:46:00	\N	1	\N	1	\N	F	\N	\N	3	\N
691	3	2016-07-13 00:45:00	2016-07-13 00:45:00	2016-07-13 00:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
692	3	2016-07-13 02:45:00	2016-07-13 02:45:00	2016-07-13 02:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
693	3	2016-07-13 04:30:00	2016-07-13 04:30:00	2016-07-13 04:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
694	3	2016-07-13 06:15:00	2016-07-13 06:15:00	2016-07-13 06:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
695	3	2016-07-13 11:00:00	2016-07-13 11:00:00	2016-07-13 11:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
696	3	2016-07-13 13:30:00	2016-07-13 13:30:00	2016-07-13 13:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
697	3	2016-07-13 14:15:00	2016-07-13 14:15:00	2016-07-13 14:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
698	3	2016-07-13 18:15:00	2016-07-13 18:15:00	2016-07-13 18:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
699	3	2016-07-13 20:30:00	2016-07-13 20:30:00	2016-07-13 20:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
700	3	2016-07-13 22:00:00	2016-07-13 22:00:00	2016-07-13 22:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
701	3	2016-07-14 00:45:00	2016-07-14 00:45:00	2016-07-14 00:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
702	3	2016-07-14 05:00:00	2016-07-14 05:00:00	2016-07-14 05:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
703	3	2016-07-14 02:45:00	2016-07-14 02:45:00	2016-07-14 02:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
704	3	2016-07-14 10:00:00	2016-07-14 10:00:00	2016-07-14 10:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
705	3	2016-07-14 09:00:00	2016-07-14 09:00:00	2016-07-14 09:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
706	3	2016-07-14 11:30:00	2016-07-14 11:30:00	2016-07-14 11:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
707	3	2016-07-14 14:15:00	2016-07-14 14:15:00	2016-07-14 14:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
708	3	2016-07-14 17:30:00	2016-07-14 17:30:00	2016-07-14 17:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
709	3	2016-07-14 19:30:00	2016-07-14 19:30:00	2016-07-14 19:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
710	3	2016-07-14 21:30:00	2016-07-14 21:30:00	2016-07-14 21:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
711	3	2016-07-14 23:15:00	2016-07-14 23:15:00	2016-07-14 23:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
712	3	2016-07-15 04:30:00	2016-07-15 04:30:00	2016-07-15 04:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
713	3	2016-07-15 06:00:00	2016-07-15 06:00:00	2016-07-15 06:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
714	3	2016-07-15 07:45:00	2016-07-15 07:45:00	2016-07-15 07:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
715	3	2016-07-15 10:30:00	2016-07-15 10:30:00	2016-07-15 10:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
716	3	2016-07-15 14:45:00	2016-07-15 14:45:00	2016-07-15 14:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
717	3	2016-07-15 16:15:00	2016-07-15 16:15:00	2016-07-15 16:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
718	3	2016-07-15 17:46:00	2016-07-15 17:46:00	2016-07-15 17:46:00	\N	1	\N	1	\N	F	\N	\N	3	\N
719	3	2016-07-15 21:30:00	2016-07-15 21:30:00	2016-07-15 21:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
720	3	2016-07-16 00:30:00	2016-07-16 00:30:00	2016-07-16 00:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
721	3	2016-07-16 00:45:00	2016-07-16 00:45:00	2016-07-16 00:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
722	3	2016-07-16 03:15:00	2016-07-16 03:15:00	2016-07-16 03:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
723	3	2016-07-16 07:45:00	2016-07-16 07:45:00	2016-07-16 07:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
724	3	2016-07-16 12:00:00	2016-07-16 12:00:00	2016-07-16 12:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
725	3	2016-07-16 11:30:00	2016-07-16 11:30:00	2016-07-16 11:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
726	3	2016-07-16 15:00:00	2016-07-16 15:00:00	2016-07-16 15:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
727	3	2016-07-16 18:00:00	2016-07-16 18:00:00	2016-07-16 18:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
728	3	2016-07-16 19:30:00	2016-07-16 19:30:00	2016-07-16 19:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
729	3	2016-07-16 23:15:00	2016-07-16 23:15:00	2016-07-16 23:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
730	3	2016-07-17 02:00:00	2016-07-17 02:00:00	2016-07-17 02:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
731	3	2016-07-17 03:45:00	2016-07-17 03:45:00	2016-07-17 03:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
732	3	2016-07-17 04:30:00	2016-07-17 04:30:00	2016-07-17 04:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
733	3	2016-07-17 08:45:00	2016-07-17 08:45:00	2016-07-17 08:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
734	3	2016-07-17 13:30:00	2016-07-17 13:30:00	2016-07-17 13:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
735	3	2016-07-17 12:00:00	2016-07-17 12:00:00	2016-07-17 12:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
736	3	2016-07-17 15:15:00	2016-07-17 15:15:00	2016-07-17 15:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
737	3	2016-07-17 19:30:00	2016-07-17 19:30:00	2016-07-17 19:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
738	3	2016-07-17 22:30:00	2016-07-17 22:30:00	2016-07-17 22:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
739	3	2016-07-17 23:15:00	2016-07-17 23:15:00	2016-07-17 23:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
740	3	2016-07-18 01:15:00	2016-07-18 01:15:00	2016-07-18 01:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
741	3	2016-07-18 03:15:00	2016-07-18 03:15:00	2016-07-18 03:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
742	3	2016-07-18 09:15:00	2016-07-18 09:15:00	2016-07-18 09:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
743	3	2016-07-18 10:00:00	2016-07-18 10:00:00	2016-07-18 10:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
744	3	2016-07-18 11:00:00	2016-07-18 11:00:00	2016-07-18 11:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
745	3	2016-07-18 13:00:00	2016-07-18 13:00:00	2016-07-18 13:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
746	3	2016-07-18 16:00:00	2016-07-18 16:00:00	2016-07-18 16:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
747	3	2016-07-18 17:45:00	2016-07-18 17:45:00	2016-07-18 17:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
748	3	2016-07-18 20:45:00	2016-07-18 20:45:00	2016-07-18 20:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
749	3	2016-07-19 00:30:00	2016-07-19 00:30:00	2016-07-19 00:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
750	3	2016-07-19 04:15:00	2016-07-19 04:15:00	2016-07-19 04:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
751	3	2016-07-19 05:45:00	2016-07-19 05:45:00	2016-07-19 05:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
752	3	2016-07-19 10:45:00	2016-07-19 10:45:00	2016-07-19 10:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
753	3	2016-07-19 11:15:00	2016-07-19 11:15:00	2016-07-19 11:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
754	3	2016-07-19 12:45:00	2016-07-19 12:45:00	2016-07-19 12:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
755	3	2016-07-19 13:45:00	2016-07-19 13:45:00	2016-07-19 13:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
756	3	2016-07-19 18:30:00	2016-07-19 18:30:00	2016-07-19 18:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
757	3	2016-07-19 19:30:00	2016-07-19 19:30:00	2016-07-19 19:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
758	3	2016-07-20 01:15:00	2016-07-20 01:15:00	2016-07-20 01:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
759	3	2016-07-20 01:45:00	2016-07-20 01:45:00	2016-07-20 01:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
760	3	2016-07-20 04:00:00	2016-07-20 04:00:00	2016-07-20 04:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
761	3	2016-07-20 05:30:00	2016-07-20 05:30:00	2016-07-20 05:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
762	3	2016-07-20 11:00:00	2016-07-20 11:00:00	2016-07-20 11:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
763	3	2016-07-20 10:15:00	2016-07-20 10:15:00	2016-07-20 10:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
764	3	2016-07-20 15:30:00	2016-07-20 15:30:00	2016-07-20 15:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
765	3	2016-07-20 15:15:00	2016-07-20 15:15:00	2016-07-20 15:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
766	3	2016-07-20 18:30:00	2016-07-20 18:30:00	2016-07-20 18:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
767	3	2016-07-20 19:30:00	2016-07-20 19:30:00	2016-07-20 19:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
768	3	2016-07-21 00:00:00	2016-07-21 00:00:00	2016-07-21 00:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
769	3	2016-07-21 00:15:00	2016-07-21 00:15:00	2016-07-21 00:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
770	3	2016-07-21 04:30:00	2016-07-21 04:30:00	2016-07-21 04:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
771	3	2016-07-21 08:30:00	2016-07-21 08:30:00	2016-07-21 08:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
772	3	2016-07-21 10:30:00	2016-07-21 10:30:00	2016-07-21 10:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
773	3	2016-07-21 12:45:00	2016-07-21 12:45:00	2016-07-21 12:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
774	3	2016-07-21 17:00:00	2016-07-21 17:00:00	2016-07-21 17:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
775	3	2016-07-21 15:45:00	2016-07-21 15:45:00	2016-07-21 15:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
776	3	2016-07-21 19:15:00	2016-07-21 19:15:00	2016-07-21 19:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
777	3	2016-07-21 23:15:00	2016-07-21 23:15:00	2016-07-21 23:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
778	3	2016-07-22 02:15:00	2016-07-22 02:15:00	2016-07-22 02:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
779	3	2016-07-22 02:30:00	2016-07-22 02:30:00	2016-07-22 02:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
780	3	2016-07-22 07:30:00	2016-07-22 07:30:00	2016-07-22 07:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
781	3	2016-07-22 06:15:00	2016-07-22 06:15:00	2016-07-22 06:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
782	3	2016-07-22 09:45:00	2016-07-22 09:45:00	2016-07-22 09:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
783	3	2016-07-22 11:30:00	2016-07-22 11:30:00	2016-07-22 11:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
784	3	2016-07-22 14:45:00	2016-07-22 14:45:00	2016-07-22 14:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
785	3	2016-07-22 15:30:00	2016-07-22 15:30:00	2016-07-22 15:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
786	3	2016-07-22 19:00:00	2016-07-22 19:00:00	2016-07-22 19:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
787	3	2016-07-23 00:30:00	2016-07-23 00:30:00	2016-07-23 00:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
788	3	2016-07-23 02:30:00	2016-07-23 02:30:00	2016-07-23 02:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
789	3	2016-07-23 06:00:00	2016-07-23 06:00:00	2016-07-23 06:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
790	3	2016-07-23 09:15:00	2016-07-23 09:15:00	2016-07-23 09:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
791	3	2016-07-23 07:00:00	2016-07-23 07:00:00	2016-07-23 07:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
792	3	2016-07-23 11:00:00	2016-07-23 11:00:00	2016-07-23 11:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
793	3	2016-07-23 15:45:00	2016-07-23 15:45:00	2016-07-23 15:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
794	3	2016-07-23 18:45:00	2016-07-23 18:45:00	2016-07-23 18:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
795	3	2016-07-23 18:15:00	2016-07-23 18:15:00	2016-07-23 18:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
796	3	2016-07-23 21:15:00	2016-07-23 21:15:00	2016-07-23 21:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
797	3	2016-07-24 00:30:00	2016-07-24 00:30:00	2016-07-24 00:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
798	3	2016-07-24 02:45:00	2016-07-24 02:45:00	2016-07-24 02:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
799	3	2016-07-24 03:00:00	2016-07-24 03:00:00	2016-07-24 03:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
800	3	2016-07-24 08:30:00	2016-07-24 08:30:00	2016-07-24 08:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
801	3	2016-07-24 09:45:00	2016-07-24 09:45:00	2016-07-24 09:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
802	3	2016-07-24 14:15:00	2016-07-24 14:15:00	2016-07-24 14:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
803	3	2016-07-24 14:00:00	2016-07-24 14:00:00	2016-07-24 14:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
804	3	2016-07-24 17:30:00	2016-07-24 17:30:00	2016-07-24 17:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
805	3	2016-07-24 20:15:00	2016-07-24 20:15:00	2016-07-24 20:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
806	3	2016-07-24 21:45:00	2016-07-24 21:45:00	2016-07-24 21:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
807	3	2016-07-25 00:00:00	2016-07-25 00:00:00	2016-07-25 00:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
808	3	2016-07-25 02:45:00	2016-07-25 02:45:00	2016-07-25 02:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
809	3	2016-07-25 07:15:00	2016-07-25 07:15:00	2016-07-25 07:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
810	3	2016-07-25 08:00:00	2016-07-25 08:00:00	2016-07-25 08:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
811	3	2016-07-25 09:15:00	2016-07-25 09:15:00	2016-07-25 09:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
812	3	2016-07-25 15:30:00	2016-07-25 15:30:00	2016-07-25 15:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
813	3	2016-07-25 17:45:00	2016-07-25 17:45:00	2016-07-25 17:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
814	3	2016-07-25 18:15:00	2016-07-25 18:15:00	2016-07-25 18:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
815	3	2016-07-25 23:00:00	2016-07-25 23:00:00	2016-07-25 23:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
816	3	2016-07-25 23:15:00	2016-07-25 23:15:00	2016-07-25 23:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
817	3	2016-07-26 00:30:00	2016-07-26 00:30:00	2016-07-26 00:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
818	3	2016-07-26 03:30:00	2016-07-26 03:30:00	2016-07-26 03:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
819	3	2016-07-26 06:45:00	2016-07-26 06:45:00	2016-07-26 06:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
820	3	2016-07-26 09:00:00	2016-07-26 09:00:00	2016-07-26 09:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
821	3	2016-07-26 13:15:00	2016-07-26 13:15:00	2016-07-26 13:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
822	3	2016-07-26 13:45:00	2016-07-26 13:45:00	2016-07-26 13:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
823	3	2016-07-26 16:45:00	2016-07-26 16:45:00	2016-07-26 16:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
824	3	2016-07-26 21:00:00	2016-07-26 21:00:00	2016-07-26 21:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
825	3	2016-07-26 22:00:00	2016-07-26 22:00:00	2016-07-26 22:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
826	3	2016-07-27 00:15:00	2016-07-27 00:15:00	2016-07-27 00:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
827	3	2016-07-27 02:30:00	2016-07-27 02:30:00	2016-07-27 02:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
828	3	2016-07-27 06:30:00	2016-07-27 06:30:00	2016-07-27 06:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
829	3	2016-07-27 08:45:00	2016-07-27 08:45:00	2016-07-27 08:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
830	3	2016-07-27 10:30:00	2016-07-27 10:30:00	2016-07-27 10:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
831	3	2016-07-27 14:30:00	2016-07-27 14:30:00	2016-07-27 14:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
832	3	2016-07-27 15:30:00	2016-07-27 15:30:00	2016-07-27 15:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
833	3	2016-07-27 17:30:00	2016-07-27 17:30:00	2016-07-27 17:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
834	3	2016-07-27 20:00:00	2016-07-27 20:00:00	2016-07-27 20:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
835	3	2016-07-27 22:15:00	2016-07-27 22:15:00	2016-07-27 22:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
836	3	2016-07-28 03:45:00	2016-07-28 03:45:00	2016-07-28 03:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
837	3	2016-07-28 04:00:00	2016-07-28 04:00:00	2016-07-28 04:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
838	3	2016-07-28 06:45:00	2016-07-28 06:45:00	2016-07-28 06:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
839	3	2016-07-28 10:00:00	2016-07-28 10:00:00	2016-07-28 10:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
840	3	2016-07-28 13:30:00	2016-07-28 13:30:00	2016-07-28 13:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
841	3	2016-07-28 12:00:00	2016-07-28 12:00:00	2016-07-28 12:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
842	3	2016-07-28 17:15:00	2016-07-28 17:15:00	2016-07-28 17:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
843	3	2016-07-28 17:00:00	2016-07-28 17:00:00	2016-07-28 17:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
844	3	2016-07-28 21:45:00	2016-07-28 21:45:00	2016-07-28 21:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
845	3	2016-07-29 07:00:00	2016-07-29 07:00:00	2016-07-29 07:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
846	3	2016-07-29 01:45:00	2016-07-29 01:45:00	2016-07-29 01:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
847	3	2016-07-29 08:15:00	2016-07-29 08:15:00	2016-07-29 08:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
848	3	2016-07-29 03:00:00	2016-07-29 03:00:00	2016-07-29 03:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
849	3	2016-07-29 17:00:00	2016-07-29 17:00:00	2016-07-29 17:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
850	3	2016-07-29 11:45:00	2016-07-29 11:45:00	2016-07-29 11:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
851	3	2016-07-29 18:15:00	2016-07-29 18:15:00	2016-07-29 18:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
852	3	2016-07-29 13:00:00	2016-07-29 13:00:00	2016-07-29 13:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
853	3	2016-07-30 03:00:00	2016-07-30 03:00:00	2016-07-30 03:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
854	3	2016-07-29 21:45:00	2016-07-29 21:45:00	2016-07-29 21:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
855	3	2016-07-30 04:15:00	2016-07-30 04:15:00	2016-07-30 04:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
856	3	2016-07-29 23:00:00	2016-07-29 23:00:00	2016-07-29 23:00:00	\N	1	\N	1	\N	F	\N	\N	3	\N
857	3	2016-07-31 09:30:00	2016-07-31 09:30:00	2016-07-31 09:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
858	3	2016-07-30 05:15:00	2016-07-30 05:15:00	2016-07-30 05:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
859	3	2016-07-31 13:15:00	2016-07-31 13:15:00	2016-07-31 13:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
860	3	2016-07-31 10:30:00	2016-07-31 10:30:00	2016-07-31 10:30:00	\N	1	\N	1	\N	F	\N	\N	3	\N
861	3	2016-07-31 15:15:00	2016-07-31 15:15:00	2016-07-31 15:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
862	3	2016-07-31 19:15:00	2016-07-31 19:15:00	2016-07-31 19:15:00	\N	1	\N	1	\N	F	\N	\N	3	\N
863	3	2016-07-31 14:45:00	2016-07-31 14:45:00	2016-07-31 14:45:00	\N	1	\N	1	\N	F	\N	\N	3	\N
864	3	2016-07-31 21:31:00	2016-07-31 21:31:00	2016-07-31 21:31:00	\N	1	\N	1	\N	F	\N	\N	3	\N
865	4	2016-07-01 00:15:00	2016-07-01 00:15:00	2016-07-01 00:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
866	4	2016-07-01 04:30:00	2016-07-01 04:30:00	2016-07-01 04:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
867	4	2016-07-01 05:15:00	2016-07-01 05:15:00	2016-07-01 05:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
868	4	2016-07-01 09:30:00	2016-07-01 09:30:00	2016-07-01 09:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
869	4	2016-07-01 10:30:00	2016-07-01 10:30:00	2016-07-01 10:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
870	4	2016-07-01 12:00:00	2016-07-01 12:00:00	2016-07-01 12:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
871	4	2016-07-01 16:15:00	2016-07-01 16:15:00	2016-07-01 16:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
872	4	2016-07-01 19:15:00	2016-07-01 19:15:00	2016-07-01 19:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
873	4	2016-07-01 21:45:00	2016-07-01 21:45:00	2016-07-01 21:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
874	4	2016-07-02 00:00:00	2016-07-02 00:00:00	2016-07-02 00:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
875	4	2016-07-02 05:15:00	2016-07-02 05:15:00	2016-07-02 05:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
876	4	2016-07-02 05:00:00	2016-07-02 05:00:00	2016-07-02 05:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
877	4	2016-07-02 06:30:00	2016-07-02 06:30:00	2016-07-02 06:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
878	4	2016-07-02 10:00:00	2016-07-02 10:00:00	2016-07-02 10:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
879	4	2016-07-02 11:30:00	2016-07-02 11:30:00	2016-07-02 11:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
880	4	2016-07-02 15:00:00	2016-07-02 15:00:00	2016-07-02 15:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
881	4	2016-07-02 16:30:00	2016-07-02 16:30:00	2016-07-02 16:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
882	4	2016-07-02 21:45:00	2016-07-02 21:45:00	2016-07-02 21:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
883	4	2016-07-02 21:30:00	2016-07-02 21:30:00	2016-07-02 21:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
884	4	2016-07-03 00:15:00	2016-07-03 00:15:00	2016-07-03 00:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
885	4	2016-07-03 03:45:00	2016-07-03 03:45:00	2016-07-03 03:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
886	4	2016-07-03 07:15:00	2016-07-03 07:15:00	2016-07-03 07:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
887	4	2016-07-03 07:30:00	2016-07-03 07:30:00	2016-07-03 07:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
888	4	2016-07-03 09:15:00	2016-07-03 09:15:00	2016-07-03 09:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
889	4	2016-07-03 14:16:00	2016-07-03 14:16:00	2016-07-03 14:16:00	\N	1	\N	1	\N	F	\N	\N	4	\N
890	4	2016-07-03 17:01:00	2016-07-03 17:01:00	2016-07-03 17:01:00	\N	1	\N	1	\N	F	\N	\N	4	\N
891	4	2016-07-03 20:00:00	2016-07-03 20:00:00	2016-07-03 20:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
892	4	2016-07-03 20:45:00	2016-07-03 20:45:00	2016-07-03 20:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
893	4	2016-07-03 23:46:00	2016-07-03 23:46:00	2016-07-03 23:46:00	\N	1	\N	1	\N	F	\N	\N	4	\N
894	4	2016-07-04 03:01:00	2016-07-04 03:01:00	2016-07-04 03:01:00	\N	1	\N	1	\N	F	\N	\N	4	\N
895	4	2016-07-04 06:01:00	2016-07-04 06:01:00	2016-07-04 06:01:00	\N	1	\N	1	\N	F	\N	\N	4	\N
896	4	2016-07-04 06:46:00	2016-07-04 06:46:00	2016-07-04 06:46:00	\N	1	\N	1	\N	F	\N	\N	4	\N
897	4	2016-07-04 09:46:00	2016-07-04 09:46:00	2016-07-04 09:46:00	\N	1	\N	1	\N	F	\N	\N	4	\N
898	4	2016-07-04 13:01:00	2016-07-04 13:01:00	2016-07-04 13:01:00	\N	1	\N	1	\N	F	\N	\N	4	\N
899	4	2016-07-04 16:00:00	2016-07-04 16:00:00	2016-07-04 16:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
900	4	2016-07-04 16:45:00	2016-07-04 16:45:00	2016-07-04 16:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
901	4	2016-07-04 19:45:00	2016-07-04 19:45:00	2016-07-04 19:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
902	4	2016-07-04 23:00:00	2016-07-04 23:00:00	2016-07-04 23:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
903	4	2016-07-05 02:01:00	2016-07-05 02:01:00	2016-07-05 02:01:00	\N	1	\N	1	\N	F	\N	\N	4	\N
904	4	2016-07-05 02:46:00	2016-07-05 02:46:00	2016-07-05 02:46:00	\N	1	\N	1	\N	F	\N	\N	4	\N
905	4	2016-07-05 05:46:00	2016-07-05 05:46:00	2016-07-05 05:46:00	\N	1	\N	1	\N	F	\N	\N	4	\N
906	4	2016-07-05 09:00:00	2016-07-05 09:00:00	2016-07-05 09:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
907	4	2016-07-05 12:00:00	2016-07-05 12:00:00	2016-07-05 12:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
908	4	2016-07-05 13:45:00	2016-07-05 13:45:00	2016-07-05 13:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
909	4	2016-07-05 16:00:00	2016-07-05 16:00:00	2016-07-05 16:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
910	4	2016-07-05 18:30:00	2016-07-05 18:30:00	2016-07-05 18:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
911	4	2016-07-05 21:15:00	2016-07-05 21:15:00	2016-07-05 21:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
912	4	2016-07-05 23:45:00	2016-07-05 23:45:00	2016-07-05 23:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
913	4	2016-07-06 02:15:00	2016-07-06 02:15:00	2016-07-06 02:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
914	4	2016-07-06 04:45:00	2016-07-06 04:45:00	2016-07-06 04:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
915	4	2016-07-06 07:15:00	2016-07-06 07:15:00	2016-07-06 07:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
916	4	2016-07-06 09:45:00	2016-07-06 09:45:00	2016-07-06 09:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
917	4	2016-07-06 12:15:00	2016-07-06 12:15:00	2016-07-06 12:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
918	4	2016-07-06 14:45:00	2016-07-06 14:45:00	2016-07-06 14:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
919	4	2016-07-06 17:30:00	2016-07-06 17:30:00	2016-07-06 17:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
920	4	2016-07-06 19:14:00	2016-07-06 19:14:00	2016-07-06 19:14:00	\N	1	\N	1	\N	F	\N	\N	4	\N
921	4	2016-07-06 21:45:00	2016-07-06 21:45:00	2016-07-06 21:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
922	4	2016-07-07 01:15:00	2016-07-07 01:15:00	2016-07-07 01:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
923	4	2016-07-07 03:00:00	2016-07-07 03:00:00	2016-07-07 03:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
924	4	2016-07-07 05:45:00	2016-07-07 05:45:00	2016-07-07 05:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
925	4	2016-07-07 08:45:00	2016-07-07 08:45:00	2016-07-07 08:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
926	4	2016-07-07 09:45:00	2016-07-07 09:45:00	2016-07-07 09:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
927	4	2016-07-07 17:15:00	2016-07-07 17:15:00	2016-07-07 17:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
928	4	2016-07-07 17:00:00	2016-07-07 17:00:00	2016-07-07 17:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
929	4	2016-07-07 19:45:00	2016-07-07 19:45:00	2016-07-07 19:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
930	4	2016-07-07 20:15:00	2016-07-07 20:15:00	2016-07-07 20:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
931	4	2016-07-08 01:30:00	2016-07-08 01:30:00	2016-07-08 01:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
932	4	2016-07-08 01:15:00	2016-07-08 01:15:00	2016-07-08 01:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
933	4	2016-07-08 05:15:00	2016-07-08 05:15:00	2016-07-08 05:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
934	4	2016-07-08 06:15:00	2016-07-08 06:15:00	2016-07-08 06:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
935	4	2016-07-08 13:30:00	2016-07-08 13:30:00	2016-07-08 13:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
936	4	2016-07-08 09:15:00	2016-07-08 09:15:00	2016-07-08 09:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
937	4	2016-07-08 15:00:00	2016-07-08 15:00:00	2016-07-08 15:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
938	4	2016-07-08 16:45:00	2016-07-08 16:45:00	2016-07-08 16:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
939	4	2016-07-08 18:15:00	2016-07-08 18:15:00	2016-07-08 18:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
940	4	2016-07-09 01:15:00	2016-07-09 01:15:00	2016-07-09 01:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
941	4	2016-07-09 00:30:00	2016-07-09 00:30:00	2016-07-09 00:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
942	4	2016-07-09 04:15:00	2016-07-09 04:15:00	2016-07-09 04:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
943	4	2016-07-09 05:00:00	2016-07-09 05:00:00	2016-07-09 05:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
944	4	2016-07-09 11:15:00	2016-07-09 11:15:00	2016-07-09 11:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
945	4	2016-07-09 08:30:00	2016-07-09 08:30:00	2016-07-09 08:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
946	4	2016-07-09 15:00:00	2016-07-09 15:00:00	2016-07-09 15:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
947	4	2016-07-09 13:00:00	2016-07-09 13:00:00	2016-07-09 13:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
948	4	2016-07-09 21:00:00	2016-07-09 21:00:00	2016-07-09 21:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
949	4	2016-07-09 19:15:00	2016-07-09 19:15:00	2016-07-09 19:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
950	4	2016-07-09 23:15:00	2016-07-09 23:15:00	2016-07-09 23:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
951	4	2016-07-09 23:30:00	2016-07-09 23:30:00	2016-07-09 23:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
952	4	2016-07-10 06:45:00	2016-07-10 06:45:00	2016-07-10 06:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
953	4	2016-07-10 05:15:00	2016-07-10 05:15:00	2016-07-10 05:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
954	4	2016-07-10 11:45:00	2016-07-10 11:45:00	2016-07-10 11:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
955	4	2016-07-10 10:15:00	2016-07-10 10:15:00	2016-07-10 10:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
956	4	2016-07-10 15:15:00	2016-07-10 15:15:00	2016-07-10 15:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
957	4	2016-07-10 16:00:00	2016-07-10 16:00:00	2016-07-10 16:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
958	4	2016-07-10 21:45:00	2016-07-10 21:45:00	2016-07-10 21:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
959	4	2016-07-10 19:30:00	2016-07-10 19:30:00	2016-07-10 19:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
960	4	2016-07-11 01:30:00	2016-07-11 01:30:00	2016-07-11 01:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
961	4	2016-07-11 03:30:00	2016-07-11 03:30:00	2016-07-11 03:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
962	4	2016-07-11 05:30:00	2016-07-11 05:30:00	2016-07-11 05:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
963	4	2016-07-11 07:30:00	2016-07-11 07:30:00	2016-07-11 07:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
964	4	2016-07-11 10:45:00	2016-07-11 10:45:00	2016-07-11 10:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
965	4	2016-07-11 13:00:00	2016-07-11 13:00:00	2016-07-11 13:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
966	4	2016-07-11 16:15:00	2016-07-11 16:15:00	2016-07-11 16:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
967	4	2016-07-11 16:45:00	2016-07-11 16:45:00	2016-07-11 16:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
968	4	2016-07-11 21:00:00	2016-07-11 21:00:00	2016-07-11 21:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
969	4	2016-07-11 22:30:00	2016-07-11 22:30:00	2016-07-11 22:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
970	4	2016-07-12 01:00:00	2016-07-12 01:00:00	2016-07-12 01:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
971	4	2016-07-12 02:00:00	2016-07-12 02:00:00	2016-07-12 02:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
972	4	2016-07-12 08:00:00	2016-07-12 08:00:00	2016-07-12 08:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
973	4	2016-07-12 08:45:00	2016-07-12 08:45:00	2016-07-12 08:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
974	4	2016-07-12 12:45:00	2016-07-12 12:45:00	2016-07-12 12:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
975	4	2016-07-12 12:15:00	2016-07-12 12:15:00	2016-07-12 12:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
976	4	2016-07-12 19:00:00	2016-07-12 19:00:00	2016-07-12 19:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
977	4	2016-07-12 16:30:00	2016-07-12 16:30:00	2016-07-12 16:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
978	4	2016-07-12 20:30:00	2016-07-12 20:30:00	2016-07-12 20:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
979	4	2016-07-12 21:00:00	2016-07-12 21:00:00	2016-07-12 21:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
980	4	2016-07-13 02:00:00	2016-07-13 02:00:00	2016-07-13 02:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
981	4	2016-07-13 05:00:00	2016-07-13 05:00:00	2016-07-13 05:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
982	4	2016-07-13 09:00:00	2016-07-13 09:00:00	2016-07-13 09:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
983	4	2016-07-13 09:45:00	2016-07-13 09:45:00	2016-07-13 09:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
984	4	2016-07-13 14:15:00	2016-07-13 14:15:00	2016-07-13 14:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
985	4	2016-07-13 15:45:00	2016-07-13 15:45:00	2016-07-13 15:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
986	4	2016-07-13 19:30:00	2016-07-13 19:30:00	2016-07-13 19:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
987	4	2016-07-13 18:30:00	2016-07-13 18:30:00	2016-07-13 18:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
988	4	2016-07-14 00:30:00	2016-07-14 00:30:00	2016-07-14 00:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
989	4	2016-07-13 22:30:00	2016-07-13 22:30:00	2016-07-13 22:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
990	4	2016-07-14 01:45:00	2016-07-14 01:45:00	2016-07-14 01:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
991	4	2016-07-14 03:45:00	2016-07-14 03:45:00	2016-07-14 03:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
992	4	2016-07-14 08:30:00	2016-07-14 08:30:00	2016-07-14 08:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
993	4	2016-07-14 08:15:00	2016-07-14 08:15:00	2016-07-14 08:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
994	4	2016-07-14 12:00:00	2016-07-14 12:00:00	2016-07-14 12:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
995	4	2016-07-14 13:30:00	2016-07-14 13:30:00	2016-07-14 13:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
996	4	2016-07-14 16:30:00	2016-07-14 16:30:00	2016-07-14 16:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
997	4	2016-07-14 19:15:00	2016-07-14 19:15:00	2016-07-14 19:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
998	4	2016-07-15 00:30:00	2016-07-15 00:30:00	2016-07-15 00:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
999	4	2016-07-15 02:15:00	2016-07-15 02:15:00	2016-07-15 02:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1000	4	2016-07-15 05:15:00	2016-07-15 05:15:00	2016-07-15 05:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1001	4	2016-07-15 04:30:00	2016-07-15 04:30:00	2016-07-15 04:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1002	4	2016-07-15 09:45:00	2016-07-15 09:45:00	2016-07-15 09:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1003	4	2016-07-15 11:30:00	2016-07-15 11:30:00	2016-07-15 11:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1004	4	2016-07-15 14:15:00	2016-07-15 14:15:00	2016-07-15 14:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1005	4	2016-07-15 15:30:00	2016-07-15 15:30:00	2016-07-15 15:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1006	4	2016-07-15 19:15:00	2016-07-15 19:15:00	2016-07-15 19:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1007	4	2016-07-15 19:45:00	2016-07-15 19:45:00	2016-07-15 19:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1008	4	2016-07-16 01:00:00	2016-07-16 01:00:00	2016-07-16 01:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1009	4	2016-07-16 02:00:00	2016-07-16 02:00:00	2016-07-16 02:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1010	4	2016-07-16 03:45:00	2016-07-16 03:45:00	2016-07-16 03:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1011	4	2016-07-16 09:15:00	2016-07-16 09:15:00	2016-07-16 09:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1012	4	2016-07-16 08:30:00	2016-07-16 08:30:00	2016-07-16 08:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1013	4	2016-07-16 13:30:00	2016-07-16 13:30:00	2016-07-16 13:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1014	4	2016-07-16 13:45:00	2016-07-16 13:45:00	2016-07-16 13:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1015	4	2016-07-16 17:45:00	2016-07-16 17:45:00	2016-07-16 17:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1016	4	2016-07-16 21:30:00	2016-07-16 21:30:00	2016-07-16 21:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1017	4	2016-07-16 21:15:00	2016-07-16 21:15:00	2016-07-16 21:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1018	4	2016-07-17 01:30:00	2016-07-17 01:30:00	2016-07-17 01:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1019	4	2016-07-17 04:15:00	2016-07-17 04:15:00	2016-07-17 04:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1020	4	2016-07-17 06:00:00	2016-07-17 06:00:00	2016-07-17 06:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1021	4	2016-07-17 07:00:00	2016-07-17 07:00:00	2016-07-17 07:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1022	4	2016-07-17 12:15:00	2016-07-17 12:15:00	2016-07-17 12:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1023	4	2016-07-17 13:00:00	2016-07-17 13:00:00	2016-07-17 13:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1024	4	2016-07-17 14:45:00	2016-07-17 14:45:00	2016-07-17 14:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1025	4	2016-07-17 18:15:00	2016-07-17 18:15:00	2016-07-17 18:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1026	4	2016-07-17 20:00:00	2016-07-17 20:00:00	2016-07-17 20:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1027	4	2016-07-17 23:30:00	2016-07-17 23:30:00	2016-07-17 23:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1028	4	2016-07-18 04:00:00	2016-07-18 04:00:00	2016-07-18 04:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1029	4	2016-07-18 05:45:00	2016-07-18 05:45:00	2016-07-18 05:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1030	4	2016-07-18 09:45:00	2016-07-18 09:45:00	2016-07-18 09:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1031	4	2016-07-18 10:30:00	2016-07-18 10:30:00	2016-07-18 10:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1032	4	2016-07-18 13:30:00	2016-07-18 13:30:00	2016-07-18 13:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1033	4	2016-07-18 14:45:00	2016-07-18 14:45:00	2016-07-18 14:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1034	4	2016-07-18 15:45:00	2016-07-18 15:45:00	2016-07-18 15:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1035	4	2016-07-18 18:00:00	2016-07-18 18:00:00	2016-07-18 18:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1036	4	2016-07-18 21:00:00	2016-07-18 21:00:00	2016-07-18 21:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1037	4	2016-07-19 01:45:00	2016-07-19 01:45:00	2016-07-19 01:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1038	4	2016-07-19 04:45:00	2016-07-19 04:45:00	2016-07-19 04:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1039	4	2016-07-19 03:00:00	2016-07-19 03:00:00	2016-07-19 03:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1040	4	2016-07-19 08:30:00	2016-07-19 08:30:00	2016-07-19 08:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1041	4	2016-07-19 10:15:00	2016-07-19 10:15:00	2016-07-19 10:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1042	4	2016-07-19 14:30:00	2016-07-19 14:30:00	2016-07-19 14:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1043	4	2016-07-19 12:45:00	2016-07-19 12:45:00	2016-07-19 12:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1044	4	2016-07-19 20:15:00	2016-07-19 20:15:00	2016-07-19 20:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1045	4	2016-07-19 19:00:00	2016-07-19 19:00:00	2016-07-19 19:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1046	4	2016-07-19 23:15:00	2016-07-19 23:15:00	2016-07-19 23:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1047	4	2016-07-20 01:45:00	2016-07-20 01:45:00	2016-07-20 01:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1048	4	2016-07-20 06:00:00	2016-07-20 06:00:00	2016-07-20 06:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1049	4	2016-07-20 05:30:00	2016-07-20 05:30:00	2016-07-20 05:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1050	4	2016-07-20 07:30:00	2016-07-20 07:30:00	2016-07-20 07:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1051	4	2016-07-20 11:45:00	2016-07-20 11:45:00	2016-07-20 11:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1052	4	2016-07-20 14:00:00	2016-07-20 14:00:00	2016-07-20 14:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1053	4	2016-07-20 15:00:00	2016-07-20 15:00:00	2016-07-20 15:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1054	4	2016-07-20 21:00:00	2016-07-20 21:00:00	2016-07-20 21:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1055	4	2016-07-20 21:15:00	2016-07-20 21:15:00	2016-07-20 21:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1056	4	2016-07-21 01:30:00	2016-07-21 01:30:00	2016-07-21 01:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1057	4	2016-07-21 04:00:00	2016-07-21 04:00:00	2016-07-21 04:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1058	4	2016-07-21 03:45:00	2016-07-21 03:45:00	2016-07-21 03:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1059	4	2016-07-21 05:15:00	2016-07-21 05:15:00	2016-07-21 05:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1060	4	2016-07-21 11:45:00	2016-07-21 11:45:00	2016-07-21 11:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1061	4	2016-07-21 13:15:00	2016-07-21 13:15:00	2016-07-21 13:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1062	4	2016-07-21 14:15:00	2016-07-21 14:15:00	2016-07-21 14:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1063	4	2016-07-21 16:00:00	2016-07-21 16:00:00	2016-07-21 16:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1064	4	2016-07-21 19:15:00	2016-07-21 19:15:00	2016-07-21 19:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1065	4	2016-07-21 22:00:00	2016-07-21 22:00:00	2016-07-21 22:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1066	4	2016-07-22 01:45:00	2016-07-22 01:45:00	2016-07-22 01:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1067	4	2016-07-22 02:45:00	2016-07-22 02:45:00	2016-07-22 02:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1068	4	2016-07-22 07:15:00	2016-07-22 07:15:00	2016-07-22 07:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1069	4	2016-07-22 06:30:00	2016-07-22 06:30:00	2016-07-22 06:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1070	4	2016-07-22 10:15:00	2016-07-22 10:15:00	2016-07-22 10:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1071	4	2016-07-22 12:45:00	2016-07-22 12:45:00	2016-07-22 12:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1072	4	2016-07-22 14:15:00	2016-07-22 14:15:00	2016-07-22 14:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1073	4	2016-07-22 16:15:00	2016-07-22 16:15:00	2016-07-22 16:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1074	4	2016-07-22 23:15:00	2016-07-22 23:15:00	2016-07-22 23:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1075	4	2016-07-22 21:30:00	2016-07-22 21:30:00	2016-07-22 21:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1076	4	2016-07-23 04:15:00	2016-07-23 04:15:00	2016-07-23 04:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1077	4	2016-07-23 04:45:00	2016-07-23 04:45:00	2016-07-23 04:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1078	4	2016-07-23 05:45:00	2016-07-23 05:45:00	2016-07-23 05:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1079	4	2016-07-23 10:45:00	2016-07-23 10:45:00	2016-07-23 10:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1080	4	2016-07-23 11:45:00	2016-07-23 11:45:00	2016-07-23 11:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1081	4	2016-07-23 15:00:00	2016-07-23 15:00:00	2016-07-23 15:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1082	4	2016-07-23 15:15:00	2016-07-23 15:15:00	2016-07-23 15:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1083	4	2016-07-23 17:30:00	2016-07-23 17:30:00	2016-07-23 17:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1084	4	2016-07-23 21:30:00	2016-07-23 21:30:00	2016-07-23 21:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1085	4	2016-07-23 23:30:00	2016-07-23 23:30:00	2016-07-23 23:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1086	4	2016-07-24 03:15:00	2016-07-24 03:15:00	2016-07-24 03:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1087	4	2016-07-24 01:45:00	2016-07-24 01:45:00	2016-07-24 01:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1088	4	2016-07-24 05:45:00	2016-07-24 05:45:00	2016-07-24 05:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1089	4	2016-07-24 09:15:00	2016-07-24 09:15:00	2016-07-24 09:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1090	4	2016-07-24 13:15:00	2016-07-24 13:15:00	2016-07-24 13:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1091	4	2016-07-24 14:15:00	2016-07-24 14:15:00	2016-07-24 14:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1092	4	2016-07-24 19:45:00	2016-07-24 19:45:00	2016-07-24 19:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1093	4	2016-07-24 19:15:00	2016-07-24 19:15:00	2016-07-24 19:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1094	4	2016-07-25 01:00:00	2016-07-25 01:00:00	2016-07-25 01:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1095	4	2016-07-25 00:00:00	2016-07-25 00:00:00	2016-07-25 00:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1096	4	2016-07-25 02:30:00	2016-07-25 02:30:00	2016-07-25 02:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1097	4	2016-07-25 03:30:00	2016-07-25 03:30:00	2016-07-25 03:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1098	4	2016-07-25 10:45:00	2016-07-25 10:45:00	2016-07-25 10:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1099	4	2016-07-25 09:30:00	2016-07-25 09:30:00	2016-07-25 09:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1100	4	2016-07-25 14:45:00	2016-07-25 14:45:00	2016-07-25 14:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1101	4	2016-07-25 18:15:00	2016-07-25 18:15:00	2016-07-25 18:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1102	4	2016-07-25 19:15:00	2016-07-25 19:15:00	2016-07-25 19:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1103	4	2016-07-25 19:30:00	2016-07-25 19:30:00	2016-07-25 19:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1104	4	2016-07-25 23:30:00	2016-07-25 23:30:00	2016-07-25 23:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1105	4	2016-07-26 00:45:00	2016-07-26 00:45:00	2016-07-26 00:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1106	4	2016-07-26 05:30:00	2016-07-26 05:30:00	2016-07-26 05:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1107	4	2016-07-26 05:15:00	2016-07-26 05:15:00	2016-07-26 05:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1108	4	2016-07-26 07:45:00	2016-07-26 07:45:00	2016-07-26 07:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1109	4	2016-07-26 12:00:00	2016-07-26 12:00:00	2016-07-26 12:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1110	4	2016-07-26 15:30:00	2016-07-26 15:30:00	2016-07-26 15:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1111	4	2016-07-26 19:30:00	2016-07-26 19:30:00	2016-07-26 19:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1112	4	2016-07-26 22:00:00	2016-07-26 22:00:00	2016-07-26 22:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1113	4	2016-07-26 20:45:00	2016-07-26 20:45:00	2016-07-26 20:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1114	4	2016-07-27 02:15:00	2016-07-27 02:15:00	2016-07-27 02:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1115	4	2016-07-27 01:30:00	2016-07-27 01:30:00	2016-07-27 01:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1116	4	2016-07-27 06:45:00	2016-07-27 06:45:00	2016-07-27 06:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1117	4	2016-07-27 08:45:00	2016-07-27 08:45:00	2016-07-27 08:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1118	4	2016-07-27 13:00:00	2016-07-27 13:00:00	2016-07-27 13:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1119	4	2016-07-27 13:30:00	2016-07-27 13:30:00	2016-07-27 13:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1120	4	2016-07-27 16:01:00	2016-07-27 16:01:00	2016-07-27 16:01:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1121	4	2016-07-27 18:00:00	2016-07-27 18:00:00	2016-07-27 18:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1122	4	2016-07-27 21:15:00	2016-07-27 21:15:00	2016-07-27 21:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1123	4	2016-07-27 22:30:00	2016-07-27 22:30:00	2016-07-27 22:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1124	4	2016-07-28 02:00:00	2016-07-28 02:00:00	2016-07-28 02:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1125	4	2016-07-28 03:00:00	2016-07-28 03:00:00	2016-07-28 03:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1126	4	2016-07-28 07:45:00	2016-07-28 07:45:00	2016-07-28 07:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1127	4	2016-07-28 09:45:00	2016-07-28 09:45:00	2016-07-28 09:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1128	4	2016-07-28 11:45:00	2016-07-28 11:45:00	2016-07-28 11:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1129	4	2016-07-28 14:00:00	2016-07-28 14:00:00	2016-07-28 14:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1130	4	2016-07-28 15:30:00	2016-07-28 15:30:00	2016-07-28 15:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1131	4	2016-07-28 21:00:00	2016-07-28 21:00:00	2016-07-28 21:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1132	4	2016-07-28 21:45:00	2016-07-28 21:45:00	2016-07-28 21:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1133	4	2016-07-29 07:45:00	2016-07-29 07:45:00	2016-07-29 07:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1134	4	2016-07-29 02:30:00	2016-07-29 02:30:00	2016-07-29 02:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1135	4	2016-07-29 06:30:00	2016-07-29 06:30:00	2016-07-29 06:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1136	4	2016-07-29 01:15:00	2016-07-29 01:15:00	2016-07-29 01:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1137	4	2016-07-29 17:45:00	2016-07-29 17:45:00	2016-07-29 17:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1138	4	2016-07-29 12:30:00	2016-07-29 12:30:00	2016-07-29 12:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1139	4	2016-07-29 16:30:00	2016-07-29 16:30:00	2016-07-29 16:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1140	4	2016-07-29 11:15:00	2016-07-29 11:15:00	2016-07-29 11:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1141	4	2016-07-30 03:45:00	2016-07-30 03:45:00	2016-07-30 03:45:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1142	4	2016-07-29 22:30:00	2016-07-29 22:30:00	2016-07-29 22:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1143	4	2016-07-30 02:30:00	2016-07-30 02:30:00	2016-07-30 02:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1144	4	2016-07-29 21:15:00	2016-07-29 21:15:00	2016-07-29 21:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1145	4	2016-07-30 04:30:00	2016-07-30 04:30:00	2016-07-30 04:30:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1146	4	2016-07-31 10:00:00	2016-07-31 10:00:00	2016-07-31 10:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1147	4	2016-07-31 14:00:00	2016-07-31 14:00:00	2016-07-31 14:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1148	4	2016-07-31 11:15:00	2016-07-31 11:15:00	2016-07-31 11:15:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1149	4	2016-07-31 16:00:00	2016-07-31 16:00:00	2016-07-31 16:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1150	4	2016-07-31 20:00:00	2016-07-31 20:00:00	2016-07-31 20:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1151	4	2016-07-31 18:00:00	2016-07-31 18:00:00	2016-07-31 18:00:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1152	4	2016-07-31 22:16:00	2016-07-31 22:16:00	2016-07-31 22:16:00	\N	1	\N	1	\N	F	\N	\N	4	\N
1153	5	2016-07-01 04:15:00	2016-07-01 04:15:00	2016-07-01 04:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1154	5	2016-07-01 05:00:00	2016-07-01 05:00:00	2016-07-01 05:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1155	5	2016-07-01 05:15:00	2016-07-01 05:15:00	2016-07-01 05:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1156	5	2016-07-01 10:00:00	2016-07-01 10:00:00	2016-07-01 10:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1157	5	2016-07-01 13:30:00	2016-07-01 13:30:00	2016-07-01 13:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1158	5	2016-07-01 15:15:00	2016-07-01 15:15:00	2016-07-01 15:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1159	5	2016-07-01 17:00:00	2016-07-01 17:00:00	2016-07-01 17:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1160	5	2016-07-01 19:30:00	2016-07-01 19:30:00	2016-07-01 19:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1161	5	2016-07-01 22:30:00	2016-07-01 22:30:00	2016-07-01 22:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1162	5	2016-07-02 01:00:00	2016-07-02 01:00:00	2016-07-02 01:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1163	5	2016-07-02 04:15:00	2016-07-02 04:15:00	2016-07-02 04:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1164	5	2016-07-02 06:00:00	2016-07-02 06:00:00	2016-07-02 06:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1165	5	2016-07-02 07:45:00	2016-07-02 07:45:00	2016-07-02 07:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1166	5	2016-07-02 11:00:00	2016-07-02 11:00:00	2016-07-02 11:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1167	5	2016-07-02 14:00:00	2016-07-02 14:00:00	2016-07-02 14:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1168	5	2016-07-02 16:00:00	2016-07-02 16:00:00	2016-07-02 16:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1169	5	2016-07-02 17:30:00	2016-07-02 17:30:00	2016-07-02 17:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1170	5	2016-07-02 20:15:00	2016-07-02 20:15:00	2016-07-02 20:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1171	5	2016-07-02 23:00:00	2016-07-02 23:00:00	2016-07-02 23:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1172	5	2016-07-03 01:30:00	2016-07-03 01:30:00	2016-07-03 01:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1173	5	2016-07-03 05:00:00	2016-07-03 05:00:00	2016-07-03 05:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1174	5	2016-07-03 07:30:00	2016-07-03 07:30:00	2016-07-03 07:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1175	5	2016-07-03 10:00:00	2016-07-03 10:00:00	2016-07-03 10:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1176	5	2016-07-03 11:30:00	2016-07-03 11:30:00	2016-07-03 11:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1177	5	2016-07-03 14:16:00	2016-07-03 14:16:00	2016-07-03 14:16:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1178	5	2016-07-03 17:16:00	2016-07-03 17:16:00	2016-07-03 17:16:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1179	5	2016-07-03 20:15:00	2016-07-03 20:15:00	2016-07-03 20:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1180	5	2016-07-03 21:00:00	2016-07-03 21:00:00	2016-07-03 21:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1181	5	2016-07-04 00:01:00	2016-07-04 00:01:00	2016-07-04 00:01:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1182	5	2016-07-04 03:16:00	2016-07-04 03:16:00	2016-07-04 03:16:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1183	5	2016-07-04 06:16:00	2016-07-04 06:16:00	2016-07-04 06:16:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1184	5	2016-07-04 07:01:00	2016-07-04 07:01:00	2016-07-04 07:01:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1185	5	2016-07-04 10:01:00	2016-07-04 10:01:00	2016-07-04 10:01:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1186	5	2016-07-04 13:16:00	2016-07-04 13:16:00	2016-07-04 13:16:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1187	5	2016-07-04 16:15:00	2016-07-04 16:15:00	2016-07-04 16:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1188	5	2016-07-04 17:00:00	2016-07-04 17:00:00	2016-07-04 17:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1189	5	2016-07-04 20:00:00	2016-07-04 20:00:00	2016-07-04 20:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1190	5	2016-07-04 23:16:00	2016-07-04 23:16:00	2016-07-04 23:16:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1191	5	2016-07-05 02:16:00	2016-07-05 02:16:00	2016-07-05 02:16:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1192	5	2016-07-05 03:01:00	2016-07-05 03:01:00	2016-07-05 03:01:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1193	5	2016-07-05 06:01:00	2016-07-05 06:01:00	2016-07-05 06:01:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1194	5	2016-07-05 09:15:00	2016-07-05 09:15:00	2016-07-05 09:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1195	5	2016-07-05 10:30:00	2016-07-05 10:30:00	2016-07-05 10:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1196	5	2016-07-05 13:15:00	2016-07-05 13:15:00	2016-07-05 13:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1197	5	2016-07-05 16:15:00	2016-07-05 16:15:00	2016-07-05 16:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1198	5	2016-07-05 18:45:00	2016-07-05 18:45:00	2016-07-05 18:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1199	5	2016-07-05 21:15:00	2016-07-05 21:15:00	2016-07-05 21:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1200	5	2016-07-05 23:45:00	2016-07-05 23:45:00	2016-07-05 23:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1201	5	2016-07-06 02:15:00	2016-07-06 02:15:00	2016-07-06 02:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1202	5	2016-07-06 04:45:00	2016-07-06 04:45:00	2016-07-06 04:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1203	5	2016-07-06 07:45:00	2016-07-06 07:45:00	2016-07-06 07:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1204	5	2016-07-06 09:45:00	2016-07-06 09:45:00	2016-07-06 09:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1205	5	2016-07-06 12:00:00	2016-07-06 12:00:00	2016-07-06 12:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1206	5	2016-07-06 14:30:00	2016-07-06 14:30:00	2016-07-06 14:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1207	5	2016-07-06 18:15:00	2016-07-06 18:15:00	2016-07-06 18:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1208	5	2016-07-06 19:45:00	2016-07-06 19:45:00	2016-07-06 19:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1209	5	2016-07-06 22:15:00	2016-07-06 22:15:00	2016-07-06 22:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1210	5	2016-07-07 01:30:00	2016-07-07 01:30:00	2016-07-07 01:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1211	5	2016-07-07 02:45:00	2016-07-07 02:45:00	2016-07-07 02:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1212	5	2016-07-07 05:30:00	2016-07-07 05:30:00	2016-07-07 05:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1213	5	2016-07-07 08:15:00	2016-07-07 08:15:00	2016-07-07 08:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1214	5	2016-07-07 11:00:00	2016-07-07 11:00:00	2016-07-07 11:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1215	5	2016-07-07 14:45:00	2016-07-07 14:45:00	2016-07-07 14:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1216	5	2016-07-07 15:15:00	2016-07-07 15:15:00	2016-07-07 15:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1217	5	2016-07-07 20:45:00	2016-07-07 20:45:00	2016-07-07 20:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1218	5	2016-07-07 20:15:00	2016-07-07 20:15:00	2016-07-07 20:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1219	5	2016-07-08 00:15:00	2016-07-08 00:15:00	2016-07-08 00:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1220	5	2016-07-08 01:00:00	2016-07-08 01:00:00	2016-07-08 01:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1221	5	2016-07-08 04:45:00	2016-07-08 04:45:00	2016-07-08 04:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1222	5	2016-07-08 04:30:00	2016-07-08 04:30:00	2016-07-08 04:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1223	5	2016-07-08 13:00:00	2016-07-08 13:00:00	2016-07-08 13:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1224	5	2016-07-08 13:30:00	2016-07-08 13:30:00	2016-07-08 13:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1225	5	2016-07-08 15:00:00	2016-07-08 15:00:00	2016-07-08 15:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1226	5	2016-07-08 20:45:00	2016-07-08 20:45:00	2016-07-08 20:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1227	5	2016-07-08 16:30:00	2016-07-08 16:30:00	2016-07-08 16:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1228	5	2016-07-08 23:00:00	2016-07-08 23:00:00	2016-07-08 23:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1229	5	2016-07-09 01:45:00	2016-07-09 01:45:00	2016-07-09 01:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1230	5	2016-07-09 04:45:00	2016-07-09 04:45:00	2016-07-09 04:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1231	5	2016-07-09 04:15:00	2016-07-09 04:15:00	2016-07-09 04:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1232	5	2016-07-09 09:45:00	2016-07-09 09:45:00	2016-07-09 09:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1233	5	2016-07-09 11:15:00	2016-07-09 11:15:00	2016-07-09 11:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1234	5	2016-07-09 13:00:00	2016-07-09 13:00:00	2016-07-09 13:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1235	5	2016-07-09 16:00:00	2016-07-09 16:00:00	2016-07-09 16:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1236	5	2016-07-09 20:45:00	2016-07-09 20:45:00	2016-07-09 20:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1237	5	2016-07-09 18:45:00	2016-07-09 18:45:00	2016-07-09 18:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1238	5	2016-07-09 22:45:00	2016-07-09 22:45:00	2016-07-09 22:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1239	5	2016-07-10 01:45:00	2016-07-10 01:45:00	2016-07-10 01:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1240	5	2016-07-10 07:00:00	2016-07-10 07:00:00	2016-07-10 07:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1241	5	2016-07-10 04:15:00	2016-07-10 04:15:00	2016-07-10 04:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1242	5	2016-07-10 12:00:00	2016-07-10 12:00:00	2016-07-10 12:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1243	5	2016-07-10 10:30:00	2016-07-10 10:30:00	2016-07-10 10:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1244	5	2016-07-10 16:45:00	2016-07-10 16:45:00	2016-07-10 16:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1245	5	2016-07-10 15:45:00	2016-07-10 15:45:00	2016-07-10 15:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1246	5	2016-07-10 20:15:00	2016-07-10 20:15:00	2016-07-10 20:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1247	5	2016-07-10 19:15:00	2016-07-10 19:15:00	2016-07-10 19:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1248	5	2016-07-11 01:45:00	2016-07-11 01:45:00	2016-07-11 01:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1249	5	2016-07-10 23:30:00	2016-07-10 23:30:00	2016-07-10 23:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1250	5	2016-07-11 05:15:00	2016-07-11 05:15:00	2016-07-11 05:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1251	5	2016-07-11 06:15:00	2016-07-11 06:15:00	2016-07-11 06:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1252	5	2016-07-11 08:30:00	2016-07-11 08:30:00	2016-07-11 08:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1253	5	2016-07-11 13:15:00	2016-07-11 13:15:00	2016-07-11 13:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1254	5	2016-07-11 14:30:00	2016-07-11 14:30:00	2016-07-11 14:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1255	5	2016-07-11 15:30:00	2016-07-11 15:30:00	2016-07-11 15:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1256	5	2016-07-11 21:00:00	2016-07-11 21:00:00	2016-07-11 21:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1257	5	2016-07-11 22:30:00	2016-07-11 22:30:00	2016-07-11 22:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1258	5	2016-07-12 03:15:00	2016-07-12 03:15:00	2016-07-12 03:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1259	5	2016-07-12 03:00:00	2016-07-12 03:00:00	2016-07-12 03:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1260	5	2016-07-12 05:00:00	2016-07-12 05:00:00	2016-07-12 05:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1261	5	2016-07-12 08:00:00	2016-07-12 08:00:00	2016-07-12 08:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1262	5	2016-07-12 12:45:00	2016-07-12 12:45:00	2016-07-12 12:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1263	5	2016-07-12 14:45:00	2016-07-12 14:45:00	2016-07-12 14:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1264	5	2016-07-12 16:45:00	2016-07-12 16:45:00	2016-07-12 16:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1265	5	2016-07-12 20:15:00	2016-07-12 20:15:00	2016-07-12 20:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1266	5	2016-07-12 21:45:00	2016-07-12 21:45:00	2016-07-12 21:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1267	5	2016-07-12 21:30:00	2016-07-12 21:30:00	2016-07-12 21:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1268	5	2016-07-13 03:45:00	2016-07-13 03:45:00	2016-07-13 03:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1269	5	2016-07-13 02:15:00	2016-07-13 02:15:00	2016-07-13 02:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1270	5	2016-07-13 07:15:00	2016-07-13 07:15:00	2016-07-13 07:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1271	5	2016-07-13 07:30:00	2016-07-13 07:30:00	2016-07-13 07:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1272	5	2016-07-13 11:45:00	2016-07-13 11:45:00	2016-07-13 11:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1273	5	2016-07-13 12:15:00	2016-07-13 12:15:00	2016-07-13 12:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1274	5	2016-07-13 16:30:00	2016-07-13 16:30:00	2016-07-13 16:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1275	5	2016-07-13 20:00:00	2016-07-13 20:00:00	2016-07-13 20:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1276	5	2016-07-13 21:00:00	2016-07-13 21:00:00	2016-07-13 21:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1277	5	2016-07-14 00:15:00	2016-07-14 00:15:00	2016-07-14 00:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1278	5	2016-07-14 04:45:00	2016-07-14 04:45:00	2016-07-14 04:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1279	5	2016-07-14 05:00:00	2016-07-14 05:00:00	2016-07-14 05:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1280	5	2016-07-14 07:00:00	2016-07-14 07:00:00	2016-07-14 07:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1281	5	2016-07-14 09:00:00	2016-07-14 09:00:00	2016-07-14 09:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1282	5	2016-07-14 13:15:00	2016-07-14 13:15:00	2016-07-14 13:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1283	5	2016-07-14 13:30:00	2016-07-14 13:30:00	2016-07-14 13:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1284	5	2016-07-14 16:30:00	2016-07-14 16:30:00	2016-07-14 16:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1285	5	2016-07-14 19:15:00	2016-07-14 19:15:00	2016-07-14 19:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1286	5	2016-07-14 21:15:00	2016-07-14 21:15:00	2016-07-14 21:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1287	5	2016-07-15 01:15:00	2016-07-15 01:15:00	2016-07-15 01:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1288	5	2016-07-15 05:15:00	2016-07-15 05:15:00	2016-07-15 05:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1289	5	2016-07-15 05:45:00	2016-07-15 05:45:00	2016-07-15 05:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1290	5	2016-07-15 08:00:00	2016-07-15 08:00:00	2016-07-15 08:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1291	5	2016-07-15 09:45:00	2016-07-15 09:45:00	2016-07-15 09:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1292	5	2016-07-15 14:30:00	2016-07-15 14:30:00	2016-07-15 14:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1293	5	2016-07-15 16:00:00	2016-07-15 16:00:00	2016-07-15 16:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1294	5	2016-07-15 20:15:00	2016-07-15 20:15:00	2016-07-15 20:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1295	5	2016-07-15 19:45:00	2016-07-15 19:45:00	2016-07-15 19:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1296	5	2016-07-15 23:15:00	2016-07-15 23:15:00	2016-07-15 23:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1297	5	2016-07-16 03:45:00	2016-07-16 03:45:00	2016-07-16 03:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1298	5	2016-07-16 07:00:00	2016-07-16 07:00:00	2016-07-16 07:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1299	5	2016-07-16 06:15:00	2016-07-16 06:15:00	2016-07-16 06:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1300	5	2016-07-16 11:30:00	2016-07-16 11:30:00	2016-07-16 11:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1301	5	2016-07-16 11:15:00	2016-07-16 11:15:00	2016-07-16 11:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1302	5	2016-07-16 14:45:00	2016-07-16 14:45:00	2016-07-16 14:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1303	5	2016-07-16 16:30:00	2016-07-16 16:30:00	2016-07-16 16:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1304	5	2016-07-16 21:30:00	2016-07-16 21:30:00	2016-07-16 21:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1305	5	2016-07-16 23:15:00	2016-07-16 23:15:00	2016-07-16 23:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1306	5	2016-07-17 03:00:00	2016-07-17 03:00:00	2016-07-17 03:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1307	5	2016-07-17 01:30:00	2016-07-17 01:30:00	2016-07-17 01:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1308	5	2016-07-17 05:30:00	2016-07-17 05:30:00	2016-07-17 05:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1309	5	2016-07-17 08:30:00	2016-07-17 08:30:00	2016-07-17 08:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1310	5	2016-07-17 11:30:00	2016-07-17 11:30:00	2016-07-17 11:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1311	5	2016-07-17 13:00:00	2016-07-17 13:00:00	2016-07-17 13:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1312	5	2016-07-17 17:45:00	2016-07-17 17:45:00	2016-07-17 17:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1313	5	2016-07-17 16:00:00	2016-07-17 16:00:00	2016-07-17 16:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1314	5	2016-07-17 22:15:00	2016-07-17 22:15:00	2016-07-17 22:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1315	5	2016-07-17 20:15:00	2016-07-17 20:15:00	2016-07-17 20:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1316	5	2016-07-18 01:00:00	2016-07-18 01:00:00	2016-07-18 01:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1317	5	2016-07-18 04:15:00	2016-07-18 04:15:00	2016-07-18 04:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1318	5	2016-07-18 07:15:00	2016-07-18 07:15:00	2016-07-18 07:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1319	5	2016-07-18 07:30:00	2016-07-18 07:30:00	2016-07-18 07:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1320	5	2016-07-18 10:30:00	2016-07-18 10:30:00	2016-07-18 10:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1321	5	2016-07-18 13:15:00	2016-07-18 13:15:00	2016-07-18 13:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1322	5	2016-07-18 17:00:00	2016-07-18 17:00:00	2016-07-18 17:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1323	5	2016-07-18 19:15:00	2016-07-18 19:15:00	2016-07-18 19:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1324	5	2016-07-18 20:30:00	2016-07-18 20:30:00	2016-07-18 20:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1325	5	2016-07-19 00:15:00	2016-07-19 00:15:00	2016-07-19 00:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1326	5	2016-07-19 04:00:00	2016-07-19 04:00:00	2016-07-19 04:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1327	5	2016-07-19 05:00:00	2016-07-19 05:00:00	2016-07-19 05:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1328	5	2016-07-19 06:30:00	2016-07-19 06:30:00	2016-07-19 06:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1329	5	2016-07-19 10:00:00	2016-07-19 10:00:00	2016-07-19 10:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1330	5	2016-07-19 14:15:00	2016-07-19 14:15:00	2016-07-19 14:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1331	5	2016-07-19 14:30:00	2016-07-19 14:30:00	2016-07-19 14:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1332	5	2016-07-19 18:15:00	2016-07-19 18:15:00	2016-07-19 18:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1333	5	2016-07-19 17:30:00	2016-07-19 17:30:00	2016-07-19 17:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1334	5	2016-07-20 01:45:00	2016-07-20 01:45:00	2016-07-20 01:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1335	5	2016-07-20 01:30:00	2016-07-20 01:30:00	2016-07-20 01:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1336	5	2016-07-20 04:30:00	2016-07-20 04:30:00	2016-07-20 04:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1337	5	2016-07-20 06:30:00	2016-07-20 06:30:00	2016-07-20 06:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1338	5	2016-07-20 10:45:00	2016-07-20 10:45:00	2016-07-20 10:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1339	5	2016-07-20 11:00:00	2016-07-20 11:00:00	2016-07-20 11:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1340	5	2016-07-20 13:45:00	2016-07-20 13:45:00	2016-07-20 13:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1341	5	2016-07-20 16:00:00	2016-07-20 16:00:00	2016-07-20 16:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1342	5	2016-07-20 16:45:00	2016-07-20 16:45:00	2016-07-20 16:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1343	5	2016-07-20 19:00:00	2016-07-20 19:00:00	2016-07-20 19:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1344	5	2016-07-20 23:45:00	2016-07-20 23:45:00	2016-07-20 23:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1345	5	2016-07-21 02:45:00	2016-07-21 02:45:00	2016-07-21 02:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1346	5	2016-07-21 04:15:00	2016-07-21 04:15:00	2016-07-21 04:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1347	5	2016-07-21 07:45:00	2016-07-21 07:45:00	2016-07-21 07:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1348	5	2016-07-21 09:15:00	2016-07-21 09:15:00	2016-07-21 09:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1349	5	2016-07-21 13:30:00	2016-07-21 13:30:00	2016-07-21 13:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1350	5	2016-07-21 14:45:00	2016-07-21 14:45:00	2016-07-21 14:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1351	5	2016-07-21 16:30:00	2016-07-21 16:30:00	2016-07-21 16:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1352	5	2016-07-21 22:00:00	2016-07-21 22:00:00	2016-07-21 22:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1353	5	2016-07-21 23:00:00	2016-07-21 23:00:00	2016-07-21 23:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1354	5	2016-07-22 02:00:00	2016-07-22 02:00:00	2016-07-22 02:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1355	5	2016-07-22 03:30:00	2016-07-22 03:30:00	2016-07-22 03:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1356	5	2016-07-22 06:30:00	2016-07-22 06:30:00	2016-07-22 06:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1357	5	2016-07-22 09:45:00	2016-07-22 09:45:00	2016-07-22 09:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1358	5	2016-07-22 12:15:00	2016-07-22 12:15:00	2016-07-22 12:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1359	5	2016-07-22 11:30:00	2016-07-22 11:30:00	2016-07-22 11:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1360	5	2016-07-22 16:30:00	2016-07-22 16:30:00	2016-07-22 16:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1361	5	2016-07-22 19:00:00	2016-07-22 19:00:00	2016-07-22 19:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1362	5	2016-07-22 20:00:00	2016-07-22 20:00:00	2016-07-22 20:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1363	5	2016-07-23 00:15:00	2016-07-23 00:15:00	2016-07-23 00:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1364	5	2016-07-23 02:15:00	2016-07-23 02:15:00	2016-07-23 02:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1365	5	2016-07-23 05:45:00	2016-07-23 05:45:00	2016-07-23 05:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1366	5	2016-07-23 07:30:00	2016-07-23 07:30:00	2016-07-23 07:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1367	5	2016-07-23 10:00:00	2016-07-23 10:00:00	2016-07-23 10:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1368	5	2016-07-23 12:00:00	2016-07-23 12:00:00	2016-07-23 12:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1369	5	2016-07-23 15:30:00	2016-07-23 15:30:00	2016-07-23 15:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1370	5	2016-07-23 19:15:00	2016-07-23 19:15:00	2016-07-23 19:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1371	5	2016-07-23 18:00:00	2016-07-23 18:00:00	2016-07-23 18:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1372	5	2016-07-23 23:45:00	2016-07-23 23:45:00	2016-07-23 23:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1373	5	2016-07-23 21:15:00	2016-07-23 21:15:00	2016-07-23 21:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1374	5	2016-07-24 03:45:00	2016-07-24 03:45:00	2016-07-24 03:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1375	5	2016-07-24 04:00:00	2016-07-24 04:00:00	2016-07-24 04:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1376	5	2016-07-24 07:15:00	2016-07-24 07:15:00	2016-07-24 07:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1377	5	2016-07-24 10:00:00	2016-07-24 10:00:00	2016-07-24 10:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1378	5	2016-07-24 14:00:00	2016-07-24 14:00:00	2016-07-24 14:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1379	5	2016-07-24 15:00:00	2016-07-24 15:00:00	2016-07-24 15:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1380	5	2016-07-24 16:00:00	2016-07-24 16:00:00	2016-07-24 16:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1381	5	2016-07-24 21:15:00	2016-07-24 21:15:00	2016-07-24 21:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1382	5	2016-07-25 00:15:00	2016-07-25 00:15:00	2016-07-25 00:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1383	5	2016-07-24 23:45:00	2016-07-24 23:45:00	2016-07-24 23:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1384	5	2016-07-25 04:30:00	2016-07-25 04:30:00	2016-07-25 04:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1385	5	2016-07-25 07:00:00	2016-07-25 07:00:00	2016-07-25 07:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1386	5	2016-07-25 07:45:00	2016-07-25 07:45:00	2016-07-25 07:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1387	5	2016-07-25 09:15:00	2016-07-25 09:15:00	2016-07-25 09:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1388	5	2016-07-25 16:00:00	2016-07-25 16:00:00	2016-07-25 16:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1389	5	2016-07-25 15:00:00	2016-07-25 15:00:00	2016-07-25 15:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1390	5	2016-07-25 18:15:00	2016-07-25 18:15:00	2016-07-25 18:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1391	5	2016-07-25 22:00:00	2016-07-25 22:00:00	2016-07-25 22:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1392	5	2016-07-25 22:45:00	2016-07-25 22:45:00	2016-07-25 22:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1393	5	2016-07-26 02:15:00	2016-07-26 02:15:00	2016-07-26 02:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1394	5	2016-07-26 04:30:00	2016-07-26 04:30:00	2016-07-26 04:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1395	5	2016-07-26 06:30:00	2016-07-26 06:30:00	2016-07-26 06:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1396	5	2016-07-26 07:30:00	2016-07-26 07:30:00	2016-07-26 07:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1397	5	2016-07-26 13:15:00	2016-07-26 13:15:00	2016-07-26 13:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1398	5	2016-07-26 17:00:00	2016-07-26 17:00:00	2016-07-26 17:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1399	5	2016-07-26 15:15:00	2016-07-26 15:15:00	2016-07-26 15:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1400	5	2016-07-26 20:45:00	2016-07-26 20:45:00	2016-07-26 20:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1401	5	2016-07-26 21:45:00	2016-07-26 21:45:00	2016-07-26 21:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1402	5	2016-07-26 23:45:00	2016-07-26 23:45:00	2016-07-26 23:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1403	5	2016-07-27 03:30:00	2016-07-27 03:30:00	2016-07-27 03:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1404	5	2016-07-27 05:15:00	2016-07-27 05:15:00	2016-07-27 05:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1405	5	2016-07-27 07:00:00	2016-07-27 07:00:00	2016-07-27 07:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1406	5	2016-07-27 10:30:00	2016-07-27 10:30:00	2016-07-27 10:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1407	5	2016-07-27 12:30:00	2016-07-27 12:30:00	2016-07-27 12:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1408	5	2016-07-27 14:15:00	2016-07-27 14:15:00	2016-07-27 14:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1409	5	2016-07-27 19:30:00	2016-07-27 19:30:00	2016-07-27 19:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1410	5	2016-07-27 20:00:00	2016-07-27 20:00:00	2016-07-27 20:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1411	5	2016-07-28 00:15:00	2016-07-28 00:15:00	2016-07-28 00:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1412	5	2016-07-28 02:30:00	2016-07-28 02:30:00	2016-07-28 02:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1413	5	2016-07-28 05:00:00	2016-07-28 05:00:00	2016-07-28 05:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1414	5	2016-07-28 06:30:00	2016-07-28 06:30:00	2016-07-28 06:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1415	5	2016-07-28 09:45:00	2016-07-28 09:45:00	2016-07-28 09:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1416	5	2016-07-28 13:15:00	2016-07-28 13:15:00	2016-07-28 13:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1417	5	2016-07-28 14:45:00	2016-07-28 14:45:00	2016-07-28 14:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1418	5	2016-07-28 18:00:00	2016-07-28 18:00:00	2016-07-28 18:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1419	5	2016-07-28 19:45:00	2016-07-28 19:45:00	2016-07-28 19:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1420	5	2016-07-28 21:45:00	2016-07-28 21:45:00	2016-07-28 21:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1421	5	2016-07-28 23:45:00	2016-07-28 23:45:00	2016-07-28 23:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1422	5	2016-07-29 03:45:00	2016-07-29 03:45:00	2016-07-29 03:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1423	5	2016-07-28 22:30:00	2016-07-28 22:30:00	2016-07-28 22:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1424	5	2016-07-29 05:00:00	2016-07-29 05:00:00	2016-07-29 05:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1425	5	2016-07-29 09:45:00	2016-07-29 09:45:00	2016-07-29 09:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1426	5	2016-07-29 13:45:00	2016-07-29 13:45:00	2016-07-29 13:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1427	5	2016-07-29 08:30:00	2016-07-29 08:30:00	2016-07-29 08:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1428	5	2016-07-29 15:00:00	2016-07-29 15:00:00	2016-07-29 15:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1429	5	2016-07-29 19:45:00	2016-07-29 19:45:00	2016-07-29 19:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1430	5	2016-07-29 23:45:00	2016-07-29 23:45:00	2016-07-29 23:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1431	5	2016-07-29 18:30:00	2016-07-29 18:30:00	2016-07-29 18:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1432	5	2016-07-30 01:00:00	2016-07-30 01:00:00	2016-07-30 01:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1433	5	2016-07-31 11:30:00	2016-07-31 11:30:00	2016-07-31 11:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1434	5	2016-07-31 11:15:00	2016-07-31 11:15:00	2016-07-31 11:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1435	5	2016-07-30 07:00:00	2016-07-30 07:00:00	2016-07-30 07:00:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1436	5	2016-07-31 12:30:00	2016-07-31 12:30:00	2016-07-31 12:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1437	5	2016-07-31 17:15:00	2016-07-31 17:15:00	2016-07-31 17:15:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1438	5	2016-07-31 23:45:00	2016-07-31 23:45:00	2016-07-31 23:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1439	5	2016-07-31 16:45:00	2016-07-31 16:45:00	2016-07-31 16:45:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1440	5	2016-07-31 23:30:00	2016-07-31 23:30:00	2016-07-31 23:30:00	\N	1	\N	1	\N	F	\N	\N	5	\N
1441	6	2016-07-01 01:15:00	2016-07-01 01:15:00	2016-07-01 01:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1442	6	2016-07-01 05:15:00	2016-07-01 05:15:00	2016-07-01 05:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1443	6	2016-07-01 06:15:00	2016-07-01 06:15:00	2016-07-01 06:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1444	6	2016-07-01 10:15:00	2016-07-01 10:15:00	2016-07-01 10:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1445	6	2016-07-01 14:30:00	2016-07-01 14:30:00	2016-07-01 14:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1446	6	2016-07-01 16:00:00	2016-07-01 16:00:00	2016-07-01 16:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1447	6	2016-07-01 18:00:00	2016-07-01 18:00:00	2016-07-01 18:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1448	6	2016-07-01 18:45:00	2016-07-01 18:45:00	2016-07-01 18:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1449	6	2016-07-01 23:15:00	2016-07-01 23:15:00	2016-07-01 23:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1450	6	2016-07-02 01:45:00	2016-07-02 01:45:00	2016-07-02 01:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1451	6	2016-07-02 02:00:00	2016-07-02 02:00:00	2016-07-02 02:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1452	6	2016-07-02 05:30:00	2016-07-02 05:30:00	2016-07-02 05:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1453	6	2016-07-02 09:45:00	2016-07-02 09:45:00	2016-07-02 09:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1454	6	2016-07-02 10:30:00	2016-07-02 10:30:00	2016-07-02 10:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1455	6	2016-07-02 13:30:00	2016-07-02 13:30:00	2016-07-02 13:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1456	6	2016-07-02 15:30:00	2016-07-02 15:30:00	2016-07-02 15:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1457	6	2016-07-02 17:15:00	2016-07-02 17:15:00	2016-07-02 17:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1458	6	2016-07-02 21:00:00	2016-07-02 21:00:00	2016-07-02 21:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1459	6	2016-07-02 23:45:00	2016-07-02 23:45:00	2016-07-02 23:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1460	6	2016-07-03 01:45:00	2016-07-03 01:45:00	2016-07-03 01:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1461	6	2016-07-03 02:00:00	2016-07-03 02:00:00	2016-07-03 02:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1462	6	2016-07-03 06:45:00	2016-07-03 06:45:00	2016-07-03 06:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1463	6	2016-07-03 10:45:00	2016-07-03 10:45:00	2016-07-03 10:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1464	6	2016-07-03 13:30:00	2016-07-03 13:30:00	2016-07-03 13:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1465	6	2016-07-03 14:16:00	2016-07-03 14:16:00	2016-07-03 14:16:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1466	6	2016-07-03 17:16:00	2016-07-03 17:16:00	2016-07-03 17:16:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1467	6	2016-07-03 20:15:00	2016-07-03 20:15:00	2016-07-03 20:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1468	6	2016-07-03 21:00:00	2016-07-03 21:00:00	2016-07-03 21:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1469	6	2016-07-04 00:01:00	2016-07-04 00:01:00	2016-07-04 00:01:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1470	6	2016-07-04 03:16:00	2016-07-04 03:16:00	2016-07-04 03:16:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1471	6	2016-07-04 06:16:00	2016-07-04 06:16:00	2016-07-04 06:16:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1472	6	2016-07-04 07:01:00	2016-07-04 07:01:00	2016-07-04 07:01:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1473	6	2016-07-04 10:01:00	2016-07-04 10:01:00	2016-07-04 10:01:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1474	6	2016-07-04 13:16:00	2016-07-04 13:16:00	2016-07-04 13:16:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1475	6	2016-07-04 16:15:00	2016-07-04 16:15:00	2016-07-04 16:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1476	6	2016-07-04 17:00:00	2016-07-04 17:00:00	2016-07-04 17:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1477	6	2016-07-04 20:00:00	2016-07-04 20:00:00	2016-07-04 20:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1478	6	2016-07-04 23:16:00	2016-07-04 23:16:00	2016-07-04 23:16:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1479	6	2016-07-05 02:16:00	2016-07-05 02:16:00	2016-07-05 02:16:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1480	6	2016-07-05 03:01:00	2016-07-05 03:01:00	2016-07-05 03:01:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1481	6	2016-07-05 06:01:00	2016-07-05 06:01:00	2016-07-05 06:01:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1482	6	2016-07-05 09:15:00	2016-07-05 09:15:00	2016-07-05 09:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1483	6	2016-07-05 10:30:00	2016-07-05 10:30:00	2016-07-05 10:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1484	6	2016-07-05 13:15:00	2016-07-05 13:15:00	2016-07-05 13:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1485	6	2016-07-05 16:15:00	2016-07-05 16:15:00	2016-07-05 16:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1486	6	2016-07-05 18:45:00	2016-07-05 18:45:00	2016-07-05 18:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1487	6	2016-07-05 21:15:00	2016-07-05 21:15:00	2016-07-05 21:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1488	6	2016-07-05 23:45:00	2016-07-05 23:45:00	2016-07-05 23:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1489	6	2016-07-06 02:15:00	2016-07-06 02:15:00	2016-07-06 02:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1490	6	2016-07-06 04:45:00	2016-07-06 04:45:00	2016-07-06 04:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1491	6	2016-07-06 07:45:00	2016-07-06 07:45:00	2016-07-06 07:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1492	6	2016-07-06 09:45:00	2016-07-06 09:45:00	2016-07-06 09:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1493	6	2016-07-06 12:15:00	2016-07-06 12:15:00	2016-07-06 12:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1494	6	2016-07-06 14:45:00	2016-07-06 14:45:00	2016-07-06 14:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1495	6	2016-07-06 17:30:00	2016-07-06 17:30:00	2016-07-06 17:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1496	6	2016-07-06 19:14:00	2016-07-06 19:14:00	2016-07-06 19:14:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1497	6	2016-07-06 21:45:00	2016-07-06 21:45:00	2016-07-06 21:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1498	6	2016-07-07 01:15:00	2016-07-07 01:15:00	2016-07-07 01:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1499	6	2016-07-07 03:15:00	2016-07-07 03:15:00	2016-07-07 03:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1500	6	2016-07-07 05:15:00	2016-07-07 05:15:00	2016-07-07 05:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1501	6	2016-07-07 08:45:00	2016-07-07 08:45:00	2016-07-07 08:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1502	6	2016-07-07 09:45:00	2016-07-07 09:45:00	2016-07-07 09:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1503	6	2016-07-07 13:00:00	2016-07-07 13:00:00	2016-07-07 13:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1504	6	2016-07-07 13:30:00	2016-07-07 13:30:00	2016-07-07 13:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1505	6	2016-07-07 20:15:00	2016-07-07 20:15:00	2016-07-07 20:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1506	6	2016-07-07 18:15:00	2016-07-07 18:15:00	2016-07-07 18:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1507	6	2016-07-07 22:30:00	2016-07-07 22:30:00	2016-07-07 22:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1508	6	2016-07-08 01:45:00	2016-07-08 01:45:00	2016-07-08 01:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1509	6	2016-07-08 06:15:00	2016-07-08 06:15:00	2016-07-08 06:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1510	6	2016-07-08 05:30:00	2016-07-08 05:30:00	2016-07-08 05:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1511	6	2016-07-08 10:15:00	2016-07-08 10:15:00	2016-07-08 10:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1512	6	2016-07-08 09:30:00	2016-07-08 09:30:00	2016-07-08 09:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1513	6	2016-07-08 15:00:00	2016-07-08 15:00:00	2016-07-08 15:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1514	6	2016-07-08 16:15:00	2016-07-08 16:15:00	2016-07-08 16:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1515	6	2016-07-08 20:00:00	2016-07-08 20:00:00	2016-07-08 20:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1516	6	2016-07-09 01:00:00	2016-07-09 01:00:00	2016-07-09 01:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1517	6	2016-07-08 21:15:00	2016-07-08 21:15:00	2016-07-08 21:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1518	6	2016-07-09 05:30:00	2016-07-09 05:30:00	2016-07-09 05:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1519	6	2016-07-09 03:30:00	2016-07-09 03:30:00	2016-07-09 03:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1520	6	2016-07-09 10:30:00	2016-07-09 10:30:00	2016-07-09 10:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1521	6	2016-07-09 10:45:00	2016-07-09 10:45:00	2016-07-09 10:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1522	6	2016-07-09 16:00:00	2016-07-09 16:00:00	2016-07-09 16:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1523	6	2016-07-09 14:15:00	2016-07-09 14:15:00	2016-07-09 14:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1524	6	2016-07-09 19:00:00	2016-07-09 19:00:00	2016-07-09 19:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1525	6	2016-07-09 21:15:00	2016-07-09 21:15:00	2016-07-09 21:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1526	6	2016-07-10 00:15:00	2016-07-10 00:15:00	2016-07-10 00:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1527	6	2016-07-10 00:00:00	2016-07-10 00:00:00	2016-07-10 00:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1528	6	2016-07-10 06:30:00	2016-07-10 06:30:00	2016-07-10 06:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1529	6	2016-07-10 05:00:00	2016-07-10 05:00:00	2016-07-10 05:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1530	6	2016-07-10 11:30:00	2016-07-10 11:30:00	2016-07-10 11:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1531	6	2016-07-10 10:00:00	2016-07-10 10:00:00	2016-07-10 10:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1532	6	2016-07-10 13:15:00	2016-07-10 13:15:00	2016-07-10 13:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1533	6	2016-07-10 17:15:00	2016-07-10 17:15:00	2016-07-10 17:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1534	6	2016-07-10 17:45:00	2016-07-10 17:45:00	2016-07-10 17:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1535	6	2016-07-10 23:00:00	2016-07-10 23:00:00	2016-07-10 23:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1536	6	2016-07-10 22:45:00	2016-07-10 22:45:00	2016-07-10 22:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1537	6	2016-07-11 00:15:00	2016-07-11 00:15:00	2016-07-11 00:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1538	6	2016-07-11 04:15:00	2016-07-11 04:15:00	2016-07-11 04:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1539	6	2016-07-11 09:00:00	2016-07-11 09:00:00	2016-07-11 09:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1540	6	2016-07-11 13:00:00	2016-07-11 13:00:00	2016-07-11 13:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1541	6	2016-07-11 11:15:00	2016-07-11 11:15:00	2016-07-11 11:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1542	6	2016-07-11 14:30:00	2016-07-11 14:30:00	2016-07-11 14:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1543	6	2016-07-11 19:00:00	2016-07-11 19:00:00	2016-07-11 19:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1544	6	2016-07-11 21:45:00	2016-07-11 21:45:00	2016-07-11 21:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1545	6	2016-07-11 22:15:00	2016-07-11 22:15:00	2016-07-11 22:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1546	6	2016-07-12 02:45:00	2016-07-12 02:45:00	2016-07-12 02:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1547	6	2016-07-12 02:30:00	2016-07-12 02:30:00	2016-07-12 02:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1548	6	2016-07-12 04:30:00	2016-07-12 04:30:00	2016-07-12 04:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1549	6	2016-07-12 09:45:00	2016-07-12 09:45:00	2016-07-12 09:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1550	6	2016-07-12 13:30:00	2016-07-12 13:30:00	2016-07-12 13:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1551	6	2016-07-12 13:15:00	2016-07-12 13:15:00	2016-07-12 13:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1552	6	2016-07-12 15:00:00	2016-07-12 15:00:00	2016-07-12 15:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1553	6	2016-07-12 19:15:00	2016-07-12 19:15:00	2016-07-12 19:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1554	6	2016-07-12 22:30:00	2016-07-12 22:30:00	2016-07-12 22:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1555	6	2016-07-13 01:15:00	2016-07-13 01:15:00	2016-07-13 01:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1556	6	2016-07-13 03:15:00	2016-07-13 03:15:00	2016-07-13 03:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1557	6	2016-07-13 01:45:00	2016-07-13 01:45:00	2016-07-13 01:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1558	6	2016-07-13 08:00:00	2016-07-13 08:00:00	2016-07-13 08:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1559	6	2016-07-13 09:15:00	2016-07-13 09:15:00	2016-07-13 09:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1560	6	2016-07-13 11:30:00	2016-07-13 11:30:00	2016-07-13 11:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1561	6	2016-07-13 15:30:00	2016-07-13 15:30:00	2016-07-13 15:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1562	6	2016-07-13 16:00:00	2016-07-13 16:00:00	2016-07-13 16:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1563	6	2016-07-13 19:30:00	2016-07-13 19:30:00	2016-07-13 19:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1564	6	2016-07-14 00:30:00	2016-07-14 00:30:00	2016-07-14 00:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1565	6	2016-07-14 00:45:00	2016-07-14 00:45:00	2016-07-14 00:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1566	6	2016-07-14 04:15:00	2016-07-14 04:15:00	2016-07-14 04:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1567	6	2016-07-14 03:15:00	2016-07-14 03:15:00	2016-07-14 03:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1568	6	2016-07-14 08:15:00	2016-07-14 08:15:00	2016-07-14 08:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1569	6	2016-07-14 12:15:00	2016-07-14 12:15:00	2016-07-14 12:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1570	6	2016-07-14 12:46:00	2016-07-14 12:46:00	2016-07-14 12:46:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1571	6	2016-07-14 15:15:00	2016-07-14 15:15:00	2016-07-14 15:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1572	6	2016-07-14 16:30:00	2016-07-14 16:30:00	2016-07-14 16:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1573	6	2016-07-14 18:45:00	2016-07-14 18:45:00	2016-07-14 18:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1574	6	2016-07-14 23:45:00	2016-07-14 23:45:00	2016-07-14 23:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1575	6	2016-07-15 00:45:00	2016-07-15 00:45:00	2016-07-15 00:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1576	6	2016-07-15 06:00:00	2016-07-15 06:00:00	2016-07-15 06:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1577	6	2016-07-15 05:15:00	2016-07-15 05:15:00	2016-07-15 05:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1578	6	2016-07-15 08:45:00	2016-07-15 08:45:00	2016-07-15 08:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1579	6	2016-07-15 09:15:00	2016-07-15 09:15:00	2016-07-15 09:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1580	6	2016-07-15 16:00:00	2016-07-15 16:00:00	2016-07-15 16:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1581	6	2016-07-15 14:00:00	2016-07-15 14:00:00	2016-07-15 14:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1582	6	2016-07-15 19:00:00	2016-07-15 19:00:00	2016-07-15 19:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1583	6	2016-07-15 19:45:00	2016-07-15 19:45:00	2016-07-15 19:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1584	6	2016-07-16 00:30:00	2016-07-16 00:30:00	2016-07-16 00:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1585	6	2016-07-16 03:15:00	2016-07-16 03:15:00	2016-07-16 03:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1586	6	2016-07-16 06:30:00	2016-07-16 06:30:00	2016-07-16 06:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1587	6	2016-07-16 04:30:00	2016-07-16 04:30:00	2016-07-16 04:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1588	6	2016-07-16 11:15:00	2016-07-16 11:15:00	2016-07-16 11:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1589	6	2016-07-16 09:30:00	2016-07-16 09:30:00	2016-07-16 09:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1590	6	2016-07-16 15:30:00	2016-07-16 15:30:00	2016-07-16 15:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1591	6	2016-07-16 16:00:00	2016-07-16 16:00:00	2016-07-16 16:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1592	6	2016-07-16 20:15:00	2016-07-16 20:15:00	2016-07-16 20:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1593	6	2016-07-16 22:30:00	2016-07-16 22:30:00	2016-07-16 22:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1594	6	2016-07-17 02:30:00	2016-07-17 02:30:00	2016-07-17 02:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1595	6	2016-07-17 01:00:00	2016-07-17 01:00:00	2016-07-17 01:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1596	6	2016-07-17 05:00:00	2016-07-17 05:00:00	2016-07-17 05:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1597	6	2016-07-17 09:15:00	2016-07-17 09:15:00	2016-07-17 09:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1598	6	2016-07-17 14:00:00	2016-07-17 14:00:00	2016-07-17 14:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1599	6	2016-07-17 12:30:00	2016-07-17 12:30:00	2016-07-17 12:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1600	6	2016-07-17 16:45:00	2016-07-17 16:45:00	2016-07-17 16:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1601	6	2016-07-17 17:00:00	2016-07-17 17:00:00	2016-07-17 17:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1602	6	2016-07-17 22:45:00	2016-07-17 22:45:00	2016-07-17 22:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1603	6	2016-07-17 21:00:00	2016-07-17 21:00:00	2016-07-17 21:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1604	6	2016-07-18 01:45:00	2016-07-18 01:45:00	2016-07-18 01:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1605	6	2016-07-18 05:00:00	2016-07-18 05:00:00	2016-07-18 05:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1606	6	2016-07-18 08:30:00	2016-07-18 08:30:00	2016-07-18 08:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1607	6	2016-07-18 08:15:00	2016-07-18 08:15:00	2016-07-18 08:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1608	6	2016-07-18 11:30:00	2016-07-18 11:30:00	2016-07-18 11:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1609	6	2016-07-18 14:30:00	2016-07-18 14:30:00	2016-07-18 14:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1610	6	2016-07-18 17:30:00	2016-07-18 17:30:00	2016-07-18 17:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1611	6	2016-07-18 18:15:00	2016-07-18 18:15:00	2016-07-18 18:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1612	6	2016-07-18 23:45:00	2016-07-18 23:45:00	2016-07-18 23:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1613	6	2016-07-18 23:15:00	2016-07-18 23:15:00	2016-07-18 23:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1614	6	2016-07-19 03:30:00	2016-07-19 03:30:00	2016-07-19 03:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1615	6	2016-07-19 03:45:00	2016-07-19 03:45:00	2016-07-19 03:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1616	6	2016-07-19 07:15:00	2016-07-19 07:15:00	2016-07-19 07:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1617	6	2016-07-19 11:00:00	2016-07-19 11:00:00	2016-07-19 11:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1618	6	2016-07-19 12:00:00	2016-07-19 12:00:00	2016-07-19 12:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1619	6	2016-07-19 16:00:00	2016-07-19 16:00:00	2016-07-19 16:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1620	6	2016-07-19 19:30:00	2016-07-19 19:30:00	2016-07-19 19:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1621	6	2016-07-19 18:15:00	2016-07-19 18:15:00	2016-07-19 18:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1622	6	2016-07-20 02:00:00	2016-07-20 02:00:00	2016-07-20 02:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1623	6	2016-07-19 23:30:00	2016-07-19 23:30:00	2016-07-19 23:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1624	6	2016-07-20 06:30:00	2016-07-20 06:30:00	2016-07-20 06:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1625	6	2016-07-20 04:45:00	2016-07-20 04:45:00	2016-07-20 04:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1626	6	2016-07-20 08:15:00	2016-07-20 08:15:00	2016-07-20 08:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1627	6	2016-07-20 11:00:00	2016-07-20 11:00:00	2016-07-20 11:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1628	6	2016-07-20 12:30:00	2016-07-20 12:30:00	2016-07-20 12:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1629	6	2016-07-20 18:15:00	2016-07-20 18:15:00	2016-07-20 18:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1630	6	2016-07-20 18:00:00	2016-07-20 18:00:00	2016-07-20 18:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1631	6	2016-07-20 22:00:00	2016-07-20 22:00:00	2016-07-20 22:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1632	6	2016-07-20 23:15:00	2016-07-20 23:15:00	2016-07-20 23:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1633	6	2016-07-21 03:30:00	2016-07-21 03:30:00	2016-07-21 03:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1634	6	2016-07-21 05:00:00	2016-07-21 05:00:00	2016-07-21 05:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1635	6	2016-07-21 06:30:00	2016-07-21 06:30:00	2016-07-21 06:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1636	6	2016-07-21 11:00:00	2016-07-21 11:00:00	2016-07-21 11:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1637	6	2016-07-21 13:45:00	2016-07-21 13:45:00	2016-07-21 13:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1638	6	2016-07-21 15:30:00	2016-07-21 15:30:00	2016-07-21 15:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1639	6	2016-07-21 17:15:00	2016-07-21 17:15:00	2016-07-21 17:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1640	6	2016-07-21 18:30:00	2016-07-21 18:30:00	2016-07-21 18:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1641	6	2016-07-21 22:30:00	2016-07-21 22:30:00	2016-07-21 22:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1642	6	2016-07-22 02:45:00	2016-07-22 02:45:00	2016-07-22 02:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1643	6	2016-07-22 03:00:00	2016-07-22 03:00:00	2016-07-22 03:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1644	6	2016-07-22 08:15:00	2016-07-22 08:15:00	2016-07-22 08:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1645	6	2016-07-22 08:30:00	2016-07-22 08:30:00	2016-07-22 08:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1646	6	2016-07-22 11:45:00	2016-07-22 11:45:00	2016-07-22 11:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1647	6	2016-07-22 14:45:00	2016-07-22 14:45:00	2016-07-22 14:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1648	6	2016-07-22 17:45:00	2016-07-22 17:45:00	2016-07-22 17:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1649	6	2016-07-22 16:00:00	2016-07-22 16:00:00	2016-07-22 16:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1650	6	2016-07-22 23:15:00	2016-07-22 23:15:00	2016-07-22 23:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1651	6	2016-07-22 23:45:00	2016-07-22 23:45:00	2016-07-22 23:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1652	6	2016-07-23 03:00:00	2016-07-23 03:00:00	2016-07-23 03:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1653	6	2016-07-23 05:15:00	2016-07-23 05:15:00	2016-07-23 05:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1654	6	2016-07-23 07:30:00	2016-07-23 07:30:00	2016-07-23 07:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1655	6	2016-07-23 06:30:00	2016-07-23 06:30:00	2016-07-23 06:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1656	6	2016-07-23 11:30:00	2016-07-23 11:30:00	2016-07-23 11:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1657	6	2016-07-23 15:00:00	2016-07-23 15:00:00	2016-07-23 15:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1658	6	2016-07-23 15:45:00	2016-07-23 15:45:00	2016-07-23 15:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1659	6	2016-07-23 18:45:00	2016-07-23 18:45:00	2016-07-23 18:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1660	6	2016-07-23 23:00:00	2016-07-23 23:00:00	2016-07-23 23:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1661	6	2016-07-24 01:30:00	2016-07-24 01:30:00	2016-07-24 01:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1662	6	2016-07-24 04:31:00	2016-07-24 04:31:00	2016-07-24 04:31:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1663	6	2016-07-24 06:45:00	2016-07-24 06:45:00	2016-07-24 06:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1664	6	2016-07-24 09:00:00	2016-07-24 09:00:00	2016-07-24 09:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1665	6	2016-07-24 12:15:00	2016-07-24 12:15:00	2016-07-24 12:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1666	6	2016-07-24 14:15:00	2016-07-24 14:15:00	2016-07-24 14:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1667	6	2016-07-24 16:30:00	2016-07-24 16:30:00	2016-07-24 16:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1668	6	2016-07-24 15:30:00	2016-07-24 15:30:00	2016-07-24 15:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1669	6	2016-07-24 18:00:00	2016-07-24 18:00:00	2016-07-24 18:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1670	6	2016-07-24 21:00:00	2016-07-24 21:00:00	2016-07-24 21:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1671	6	2016-07-25 00:45:00	2016-07-25 00:45:00	2016-07-25 00:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1672	6	2016-07-25 02:00:00	2016-07-25 02:00:00	2016-07-25 02:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1673	6	2016-07-25 04:15:00	2016-07-25 04:15:00	2016-07-25 04:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1674	6	2016-07-25 10:00:00	2016-07-25 10:00:00	2016-07-25 10:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1675	6	2016-07-25 10:15:00	2016-07-25 10:15:00	2016-07-25 10:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1676	6	2016-07-25 15:00:00	2016-07-25 15:00:00	2016-07-25 15:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1677	6	2016-07-25 17:00:00	2016-07-25 17:00:00	2016-07-25 17:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1678	6	2016-07-25 19:30:00	2016-07-25 19:30:00	2016-07-25 19:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1679	6	2016-07-25 20:15:00	2016-07-25 20:15:00	2016-07-25 20:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1680	6	2016-07-26 00:45:00	2016-07-26 00:45:00	2016-07-26 00:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1681	6	2016-07-26 01:30:00	2016-07-26 01:30:00	2016-07-26 01:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1682	6	2016-07-26 04:00:00	2016-07-26 04:00:00	2016-07-26 04:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1683	6	2016-07-26 08:45:00	2016-07-26 08:45:00	2016-07-26 08:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1684	6	2016-07-26 11:45:00	2016-07-26 11:45:00	2016-07-26 11:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1685	6	2016-07-26 12:15:00	2016-07-26 12:15:00	2016-07-26 12:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1686	6	2016-07-26 15:30:00	2016-07-26 15:30:00	2016-07-26 15:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1687	6	2016-07-26 15:15:00	2016-07-26 15:15:00	2016-07-26 15:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1688	6	2016-07-26 18:00:00	2016-07-26 18:00:00	2016-07-26 18:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1689	6	2016-07-26 23:00:00	2016-07-26 23:00:00	2016-07-26 23:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1690	6	2016-07-26 23:30:00	2016-07-26 23:30:00	2016-07-26 23:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1691	6	2016-07-27 04:15:00	2016-07-27 04:15:00	2016-07-27 04:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1692	6	2016-07-27 04:45:00	2016-07-27 04:45:00	2016-07-27 04:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1693	6	2016-07-27 10:45:00	2016-07-27 10:45:00	2016-07-27 10:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1694	6	2016-07-27 11:15:00	2016-07-27 11:15:00	2016-07-27 11:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1695	6	2016-07-27 15:30:00	2016-07-27 15:30:00	2016-07-27 15:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1696	6	2016-07-27 14:45:00	2016-07-27 14:45:00	2016-07-27 14:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1697	6	2016-07-27 19:30:00	2016-07-27 19:30:00	2016-07-27 19:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1698	6	2016-07-27 18:15:00	2016-07-27 18:15:00	2016-07-27 18:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1699	6	2016-07-28 00:00:00	2016-07-28 00:00:00	2016-07-28 00:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1700	6	2016-07-28 02:00:00	2016-07-28 02:00:00	2016-07-28 02:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1701	6	2016-07-28 04:30:00	2016-07-28 04:30:00	2016-07-28 04:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1702	6	2016-07-28 06:45:00	2016-07-28 06:45:00	2016-07-28 06:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1703	6	2016-07-28 08:45:00	2016-07-28 08:45:00	2016-07-28 08:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1704	6	2016-07-28 13:30:00	2016-07-28 13:30:00	2016-07-28 13:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1705	6	2016-07-28 13:15:00	2016-07-28 13:15:00	2016-07-28 13:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1706	6	2016-07-28 18:15:00	2016-07-28 18:15:00	2016-07-28 18:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1707	6	2016-07-28 17:30:00	2016-07-28 17:30:00	2016-07-28 17:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1708	6	2016-07-28 21:45:00	2016-07-28 21:45:00	2016-07-28 21:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1709	6	2016-07-29 08:00:00	2016-07-29 08:00:00	2016-07-29 08:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1710	6	2016-07-29 02:45:00	2016-07-29 02:45:00	2016-07-29 02:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1711	6	2016-07-29 06:45:00	2016-07-29 06:45:00	2016-07-29 06:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1712	6	2016-07-29 01:30:00	2016-07-29 01:30:00	2016-07-29 01:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1713	6	2016-07-29 18:00:00	2016-07-29 18:00:00	2016-07-29 18:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1714	6	2016-07-29 12:45:00	2016-07-29 12:45:00	2016-07-29 12:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1715	6	2016-07-29 16:45:00	2016-07-29 16:45:00	2016-07-29 16:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1716	6	2016-07-29 11:30:00	2016-07-29 11:30:00	2016-07-29 11:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1717	6	2016-07-30 04:00:00	2016-07-30 04:00:00	2016-07-30 04:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1718	6	2016-07-29 22:45:00	2016-07-29 22:45:00	2016-07-29 22:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1719	6	2016-07-30 02:45:00	2016-07-30 02:45:00	2016-07-30 02:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1720	6	2016-07-29 21:30:00	2016-07-29 21:30:00	2016-07-29 21:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1721	6	2016-07-30 07:15:00	2016-07-30 07:15:00	2016-07-30 07:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1722	6	2016-07-31 12:45:00	2016-07-31 12:45:00	2016-07-31 12:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1723	6	2016-07-30 06:00:00	2016-07-30 06:00:00	2016-07-30 06:00:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1724	6	2016-07-31 11:30:00	2016-07-31 11:30:00	2016-07-31 11:30:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1725	6	2016-07-31 16:15:00	2016-07-31 16:15:00	2016-07-31 16:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1726	6	2016-07-31 20:15:00	2016-07-31 20:15:00	2016-07-31 20:15:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1727	6	2016-07-31 15:45:00	2016-07-31 15:45:00	2016-07-31 15:45:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1728	6	2016-07-31 22:31:00	2016-07-31 22:31:00	2016-07-31 22:31:00	\N	1	\N	1	\N	F	\N	\N	6	\N
1729	7	2016-07-01 00:30:00	2016-07-01 00:30:00	2016-07-01 00:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1730	7	2016-07-01 04:45:00	2016-07-01 04:45:00	2016-07-01 04:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1731	7	2016-07-01 05:30:00	2016-07-01 05:30:00	2016-07-01 05:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1732	7	2016-07-01 09:45:00	2016-07-01 09:45:00	2016-07-01 09:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1733	7	2016-07-01 10:45:00	2016-07-01 10:45:00	2016-07-01 10:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1734	7	2016-07-01 15:00:00	2016-07-01 15:00:00	2016-07-01 15:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1735	7	2016-07-01 19:00:00	2016-07-01 19:00:00	2016-07-01 19:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1736	7	2016-07-01 20:30:00	2016-07-01 20:30:00	2016-07-01 20:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1737	7	2016-07-01 20:45:00	2016-07-01 20:45:00	2016-07-01 20:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1738	7	2016-07-02 01:00:00	2016-07-02 01:00:00	2016-07-02 01:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1739	7	2016-07-02 05:30:00	2016-07-02 05:30:00	2016-07-02 05:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1740	7	2016-07-02 05:15:00	2016-07-02 05:15:00	2016-07-02 05:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1741	7	2016-07-02 06:45:00	2016-07-02 06:45:00	2016-07-02 06:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1742	7	2016-07-02 10:15:00	2016-07-02 10:15:00	2016-07-02 10:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1743	7	2016-07-02 11:46:00	2016-07-02 11:46:00	2016-07-02 11:46:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1744	7	2016-07-02 15:15:00	2016-07-02 15:15:00	2016-07-02 15:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1745	7	2016-07-02 16:45:00	2016-07-02 16:45:00	2016-07-02 16:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1746	7	2016-07-02 20:00:00	2016-07-02 20:00:00	2016-07-02 20:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1747	7	2016-07-03 01:15:00	2016-07-03 01:15:00	2016-07-03 01:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1748	7	2016-07-02 23:15:00	2016-07-02 23:15:00	2016-07-02 23:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1749	7	2016-07-03 04:00:00	2016-07-03 04:00:00	2016-07-03 04:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1750	7	2016-07-03 06:00:00	2016-07-03 06:00:00	2016-07-03 06:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1751	7	2016-07-03 08:30:00	2016-07-03 08:30:00	2016-07-03 08:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1752	7	2016-07-03 11:30:00	2016-07-03 11:30:00	2016-07-03 11:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1753	7	2016-07-03 14:16:00	2016-07-03 14:16:00	2016-07-03 14:16:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1754	7	2016-07-03 17:01:00	2016-07-03 17:01:00	2016-07-03 17:01:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1755	7	2016-07-03 20:00:00	2016-07-03 20:00:00	2016-07-03 20:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1756	7	2016-07-03 21:15:00	2016-07-03 21:15:00	2016-07-03 21:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1757	7	2016-07-04 00:16:00	2016-07-04 00:16:00	2016-07-04 00:16:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1758	7	2016-07-04 03:01:00	2016-07-04 03:01:00	2016-07-04 03:01:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1759	7	2016-07-04 06:01:00	2016-07-04 06:01:00	2016-07-04 06:01:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1760	7	2016-07-04 07:16:00	2016-07-04 07:16:00	2016-07-04 07:16:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1761	7	2016-07-04 10:16:00	2016-07-04 10:16:00	2016-07-04 10:16:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1762	7	2016-07-04 13:01:00	2016-07-04 13:01:00	2016-07-04 13:01:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1763	7	2016-07-04 16:00:00	2016-07-04 16:00:00	2016-07-04 16:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1764	7	2016-07-04 17:15:00	2016-07-04 17:15:00	2016-07-04 17:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1765	7	2016-07-04 20:15:00	2016-07-04 20:15:00	2016-07-04 20:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1766	7	2016-07-04 23:00:00	2016-07-04 23:00:00	2016-07-04 23:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1767	7	2016-07-05 02:01:00	2016-07-05 02:01:00	2016-07-05 02:01:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1768	7	2016-07-05 03:16:00	2016-07-05 03:16:00	2016-07-05 03:16:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1769	7	2016-07-05 06:16:00	2016-07-05 06:16:00	2016-07-05 06:16:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1770	7	2016-07-05 09:00:00	2016-07-05 09:00:00	2016-07-05 09:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1771	7	2016-07-05 12:00:00	2016-07-05 12:00:00	2016-07-05 12:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1772	7	2016-07-05 13:45:00	2016-07-05 13:45:00	2016-07-05 13:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1773	7	2016-07-05 16:15:00	2016-07-05 16:15:00	2016-07-05 16:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1774	7	2016-07-05 18:30:00	2016-07-05 18:30:00	2016-07-05 18:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1775	7	2016-07-05 21:30:00	2016-07-05 21:30:00	2016-07-05 21:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1776	7	2016-07-05 23:45:00	2016-07-05 23:45:00	2016-07-05 23:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1777	7	2016-07-06 02:15:00	2016-07-06 02:15:00	2016-07-06 02:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1778	7	2016-07-06 04:45:00	2016-07-06 04:45:00	2016-07-06 04:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1779	7	2016-07-06 07:15:00	2016-07-06 07:15:00	2016-07-06 07:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1780	7	2016-07-06 09:45:00	2016-07-06 09:45:00	2016-07-06 09:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1781	7	2016-07-06 11:15:00	2016-07-06 11:15:00	2016-07-06 11:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1782	7	2016-07-06 14:45:00	2016-07-06 14:45:00	2016-07-06 14:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1783	7	2016-07-06 17:30:00	2016-07-06 17:30:00	2016-07-06 17:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1784	7	2016-07-06 19:14:00	2016-07-06 19:14:00	2016-07-06 19:14:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1785	7	2016-07-06 21:45:00	2016-07-06 21:45:00	2016-07-06 21:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1786	7	2016-07-07 01:15:00	2016-07-07 01:15:00	2016-07-07 01:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1787	7	2016-07-07 03:15:00	2016-07-07 03:15:00	2016-07-07 03:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1788	7	2016-07-07 05:15:00	2016-07-07 05:15:00	2016-07-07 05:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1789	7	2016-07-07 08:45:00	2016-07-07 08:45:00	2016-07-07 08:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1790	7	2016-07-07 09:45:00	2016-07-07 09:45:00	2016-07-07 09:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1791	7	2016-07-07 15:15:00	2016-07-07 15:15:00	2016-07-07 15:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1792	7	2016-07-07 13:45:00	2016-07-07 13:45:00	2016-07-07 13:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1793	7	2016-07-07 21:30:00	2016-07-07 21:30:00	2016-07-07 21:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1794	7	2016-07-07 19:45:00	2016-07-07 19:45:00	2016-07-07 19:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1795	7	2016-07-08 00:00:00	2016-07-08 00:00:00	2016-07-08 00:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1796	7	2016-07-08 01:30:00	2016-07-08 01:30:00	2016-07-08 01:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1797	7	2016-07-08 05:30:00	2016-07-08 05:30:00	2016-07-08 05:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1798	7	2016-07-08 04:00:00	2016-07-08 04:00:00	2016-07-08 04:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1799	7	2016-07-08 10:15:00	2016-07-08 10:15:00	2016-07-08 10:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1800	7	2016-07-08 08:45:00	2016-07-08 08:45:00	2016-07-08 08:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1801	7	2016-07-08 13:30:00	2016-07-08 13:30:00	2016-07-08 13:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1802	7	2016-07-08 18:15:00	2016-07-08 18:15:00	2016-07-08 18:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1803	7	2016-07-08 19:15:00	2016-07-08 19:15:00	2016-07-08 19:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1804	7	2016-07-09 01:45:00	2016-07-09 01:45:00	2016-07-09 01:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1805	7	2016-07-09 00:45:00	2016-07-09 00:45:00	2016-07-09 00:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1806	7	2016-07-09 02:00:00	2016-07-09 02:00:00	2016-07-09 02:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1807	7	2016-07-09 05:15:00	2016-07-09 05:15:00	2016-07-09 05:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1808	7	2016-07-09 10:15:00	2016-07-09 10:15:00	2016-07-09 10:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1809	7	2016-07-09 09:45:00	2016-07-09 09:45:00	2016-07-09 09:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1810	7	2016-07-09 14:00:00	2016-07-09 14:00:00	2016-07-09 14:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1811	7	2016-07-09 16:15:00	2016-07-09 16:15:00	2016-07-09 16:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1812	7	2016-07-09 18:45:00	2016-07-09 18:45:00	2016-07-09 18:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1813	7	2016-07-09 17:45:00	2016-07-09 17:45:00	2016-07-09 17:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1814	7	2016-07-10 02:00:00	2016-07-10 02:00:00	2016-07-10 02:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1815	7	2016-07-09 22:45:00	2016-07-09 22:45:00	2016-07-09 22:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1816	7	2016-07-10 05:45:00	2016-07-10 05:45:00	2016-07-10 05:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1817	7	2016-07-10 04:15:00	2016-07-10 04:15:00	2016-07-10 04:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1818	7	2016-07-10 10:45:00	2016-07-10 10:45:00	2016-07-10 10:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1819	7	2016-07-10 09:15:00	2016-07-10 09:15:00	2016-07-10 09:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1820	7	2016-07-10 17:01:00	2016-07-10 17:01:00	2016-07-10 17:01:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1821	7	2016-07-10 16:15:00	2016-07-10 16:15:00	2016-07-10 16:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1822	7	2016-07-10 19:00:00	2016-07-10 19:00:00	2016-07-10 19:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1823	7	2016-07-10 20:30:00	2016-07-10 20:30:00	2016-07-10 20:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1824	7	2016-07-11 01:15:00	2016-07-11 01:15:00	2016-07-11 01:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1825	7	2016-07-11 03:45:00	2016-07-11 03:45:00	2016-07-11 03:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1826	7	2016-07-11 07:00:00	2016-07-11 07:00:00	2016-07-11 07:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1827	7	2016-07-11 06:45:00	2016-07-11 06:45:00	2016-07-11 06:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1828	7	2016-07-11 11:00:00	2016-07-11 11:00:00	2016-07-11 11:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1829	7	2016-07-11 12:15:00	2016-07-11 12:15:00	2016-07-11 12:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1830	7	2016-07-11 18:00:00	2016-07-11 18:00:00	2016-07-11 18:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1831	7	2016-07-11 14:30:00	2016-07-11 14:30:00	2016-07-11 14:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1832	7	2016-07-11 19:45:00	2016-07-11 19:45:00	2016-07-11 19:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1833	7	2016-07-11 19:30:00	2016-07-11 19:30:00	2016-07-11 19:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1834	7	2016-07-12 03:15:00	2016-07-12 03:15:00	2016-07-12 03:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1835	7	2016-07-12 02:15:00	2016-07-12 02:15:00	2016-07-12 02:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1836	7	2016-07-12 08:15:00	2016-07-12 08:15:00	2016-07-12 08:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1837	7	2016-07-12 08:30:00	2016-07-12 08:30:00	2016-07-12 08:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1838	7	2016-07-12 13:00:00	2016-07-12 13:00:00	2016-07-12 13:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1839	7	2016-07-12 11:15:00	2016-07-12 11:15:00	2016-07-12 11:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1840	7	2016-07-12 18:30:00	2016-07-12 18:30:00	2016-07-12 18:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1841	7	2016-07-12 18:45:00	2016-07-12 18:45:00	2016-07-12 18:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1842	7	2016-07-12 19:30:00	2016-07-12 19:30:00	2016-07-12 19:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1843	7	2016-07-12 21:15:00	2016-07-12 21:15:00	2016-07-12 21:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1844	7	2016-07-13 02:15:00	2016-07-13 02:15:00	2016-07-13 02:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1845	7	2016-07-13 05:15:00	2016-07-13 05:15:00	2016-07-13 05:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1846	7	2016-07-13 06:45:00	2016-07-13 06:45:00	2016-07-13 06:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1847	7	2016-07-13 07:15:00	2016-07-13 07:15:00	2016-07-13 07:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1848	7	2016-07-13 15:00:00	2016-07-13 15:00:00	2016-07-13 15:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1849	7	2016-07-13 14:00:00	2016-07-13 14:00:00	2016-07-13 14:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1850	7	2016-07-13 17:00:00	2016-07-13 17:00:00	2016-07-13 17:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1851	7	2016-07-13 20:00:00	2016-07-13 20:00:00	2016-07-13 20:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1852	7	2016-07-13 23:30:00	2016-07-13 23:30:00	2016-07-13 23:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1853	7	2016-07-14 00:15:00	2016-07-14 00:15:00	2016-07-14 00:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1854	7	2016-07-14 05:00:00	2016-07-14 05:00:00	2016-07-14 05:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1855	7	2016-07-14 04:00:00	2016-07-14 04:00:00	2016-07-14 04:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1856	7	2016-07-14 11:00:00	2016-07-14 11:00:00	2016-07-14 11:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1857	7	2016-07-14 07:45:00	2016-07-14 07:45:00	2016-07-14 07:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1858	7	2016-07-14 12:15:00	2016-07-14 12:15:00	2016-07-14 12:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1859	7	2016-07-14 15:45:00	2016-07-14 15:45:00	2016-07-14 15:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1860	7	2016-07-14 19:45:00	2016-07-14 19:45:00	2016-07-14 19:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1861	7	2016-07-14 21:15:00	2016-07-14 21:15:00	2016-07-14 21:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1862	7	2016-07-15 00:45:00	2016-07-15 00:45:00	2016-07-15 00:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1863	7	2016-07-15 02:30:00	2016-07-15 02:30:00	2016-07-15 02:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1864	7	2016-07-15 03:00:00	2016-07-15 03:00:00	2016-07-15 03:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1865	7	2016-07-15 03:15:00	2016-07-15 03:15:00	2016-07-15 03:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1866	7	2016-07-15 10:00:00	2016-07-15 10:00:00	2016-07-15 10:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1867	7	2016-07-15 12:45:00	2016-07-15 12:45:00	2016-07-15 12:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1868	7	2016-07-15 12:15:00	2016-07-15 12:15:00	2016-07-15 12:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1869	7	2016-07-15 16:15:00	2016-07-15 16:15:00	2016-07-15 16:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1870	7	2016-07-15 19:00:00	2016-07-15 19:00:00	2016-07-15 19:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1871	7	2016-07-15 21:45:00	2016-07-15 21:45:00	2016-07-15 21:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1872	7	2016-07-16 01:15:00	2016-07-16 01:15:00	2016-07-16 01:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1873	7	2016-07-16 03:00:00	2016-07-16 03:00:00	2016-07-16 03:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1874	7	2016-07-16 07:15:00	2016-07-16 07:15:00	2016-07-16 07:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1875	7	2016-07-16 08:15:00	2016-07-16 08:15:00	2016-07-16 08:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1876	7	2016-07-16 10:15:00	2016-07-16 10:15:00	2016-07-16 10:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1877	7	2016-07-16 12:15:00	2016-07-16 12:15:00	2016-07-16 12:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1878	7	2016-07-16 16:00:00	2016-07-16 16:00:00	2016-07-16 16:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1879	7	2016-07-16 15:00:00	2016-07-16 15:00:00	2016-07-16 15:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1880	7	2016-07-16 22:00:00	2016-07-16 22:00:00	2016-07-16 22:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1881	7	2016-07-16 19:15:00	2016-07-16 19:15:00	2016-07-16 19:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1882	7	2016-07-17 03:45:00	2016-07-17 03:45:00	2016-07-17 03:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1883	7	2016-07-17 02:15:00	2016-07-17 02:15:00	2016-07-17 02:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1884	7	2016-07-17 06:15:00	2016-07-17 06:15:00	2016-07-17 06:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1885	7	2016-07-17 06:00:00	2016-07-17 06:00:00	2016-07-17 06:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1886	7	2016-07-17 12:15:00	2016-07-17 12:15:00	2016-07-17 12:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1887	7	2016-07-17 12:00:00	2016-07-17 12:00:00	2016-07-17 12:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1888	7	2016-07-17 15:00:00	2016-07-17 15:00:00	2016-07-17 15:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1889	7	2016-07-17 19:45:00	2016-07-17 19:45:00	2016-07-17 19:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1890	7	2016-07-17 23:30:00	2016-07-17 23:30:00	2016-07-17 23:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1891	7	2016-07-18 00:45:00	2016-07-18 00:45:00	2016-07-18 00:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1892	7	2016-07-18 03:00:00	2016-07-18 03:00:00	2016-07-18 03:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1893	7	2016-07-18 05:30:00	2016-07-18 05:30:00	2016-07-18 05:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1894	7	2016-07-18 05:45:00	2016-07-18 05:45:00	2016-07-18 05:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1895	7	2016-07-18 09:15:00	2016-07-18 09:15:00	2016-07-18 09:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1896	7	2016-07-18 10:30:00	2016-07-18 10:30:00	2016-07-18 10:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1897	7	2016-07-18 11:15:00	2016-07-18 11:15:00	2016-07-18 11:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1898	7	2016-07-18 16:30:00	2016-07-18 16:30:00	2016-07-18 16:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1899	7	2016-07-18 18:15:00	2016-07-18 18:15:00	2016-07-18 18:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1900	7	2016-07-18 21:15:00	2016-07-18 21:15:00	2016-07-18 21:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1901	7	2016-07-19 00:00:00	2016-07-19 00:00:00	2016-07-19 00:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1902	7	2016-07-19 02:30:00	2016-07-19 02:30:00	2016-07-19 02:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1903	7	2016-07-19 04:30:00	2016-07-19 04:30:00	2016-07-19 04:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1904	7	2016-07-19 08:45:00	2016-07-19 08:45:00	2016-07-19 08:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1905	7	2016-07-19 07:30:00	2016-07-19 07:30:00	2016-07-19 07:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1906	7	2016-07-19 13:45:00	2016-07-19 13:45:00	2016-07-19 13:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1907	7	2016-07-19 13:00:00	2016-07-19 13:00:00	2016-07-19 13:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1908	7	2016-07-19 19:30:00	2016-07-19 19:30:00	2016-07-19 19:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1909	7	2016-07-19 19:00:00	2016-07-19 19:00:00	2016-07-19 19:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1910	7	2016-07-19 22:15:00	2016-07-19 22:15:00	2016-07-19 22:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1911	7	2016-07-20 01:00:00	2016-07-20 01:00:00	2016-07-20 01:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1912	7	2016-07-20 05:30:00	2016-07-20 05:30:00	2016-07-20 05:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1913	7	2016-07-20 07:00:00	2016-07-20 07:00:00	2016-07-20 07:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1914	7	2016-07-20 10:15:00	2016-07-20 10:15:00	2016-07-20 10:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1915	7	2016-07-20 09:45:00	2016-07-20 09:45:00	2016-07-20 09:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1916	7	2016-07-20 12:15:00	2016-07-20 12:15:00	2016-07-20 12:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1917	7	2016-07-20 14:45:00	2016-07-20 14:45:00	2016-07-20 14:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1918	7	2016-07-20 16:45:00	2016-07-20 16:45:00	2016-07-20 16:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1919	7	2016-07-20 21:00:00	2016-07-20 21:00:00	2016-07-20 21:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1920	7	2016-07-21 00:30:00	2016-07-21 00:30:00	2016-07-21 00:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1921	7	2016-07-21 04:30:00	2016-07-21 04:30:00	2016-07-21 04:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1922	7	2016-07-21 04:00:00	2016-07-21 04:00:00	2016-07-21 04:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1923	7	2016-07-21 05:30:00	2016-07-21 05:30:00	2016-07-21 05:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1924	7	2016-07-21 11:15:00	2016-07-21 11:15:00	2016-07-21 11:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1925	7	2016-07-21 13:30:00	2016-07-21 13:30:00	2016-07-21 13:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1926	7	2016-07-21 16:00:00	2016-07-21 16:00:00	2016-07-21 16:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1927	7	2016-07-21 17:15:00	2016-07-21 17:15:00	2016-07-21 17:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1928	7	2016-07-21 19:45:00	2016-07-21 19:45:00	2016-07-21 19:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1929	7	2016-07-21 23:30:00	2016-07-21 23:30:00	2016-07-21 23:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1930	7	2016-07-22 00:45:00	2016-07-22 00:45:00	2016-07-22 00:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1931	7	2016-07-22 01:45:00	2016-07-22 01:45:00	2016-07-22 01:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1932	7	2016-07-22 06:15:00	2016-07-22 06:15:00	2016-07-22 06:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1933	7	2016-07-22 08:15:00	2016-07-22 08:15:00	2016-07-22 08:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1934	7	2016-07-22 09:30:00	2016-07-22 09:30:00	2016-07-22 09:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1935	7	2016-07-22 11:45:00	2016-07-22 11:45:00	2016-07-22 11:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1936	7	2016-07-22 14:30:00	2016-07-22 14:30:00	2016-07-22 14:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1937	7	2016-07-22 17:30:00	2016-07-22 17:30:00	2016-07-22 17:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1938	7	2016-07-22 22:00:00	2016-07-22 22:00:00	2016-07-22 22:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1939	7	2016-07-22 21:00:00	2016-07-22 21:00:00	2016-07-22 21:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1940	7	2016-07-23 01:30:00	2016-07-23 01:30:00	2016-07-23 01:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1941	7	2016-07-23 03:00:00	2016-07-23 03:00:00	2016-07-23 03:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1942	7	2016-07-23 07:45:00	2016-07-23 07:45:00	2016-07-23 07:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1943	7	2016-07-23 08:45:00	2016-07-23 08:45:00	2016-07-23 08:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1944	7	2016-07-23 10:45:00	2016-07-23 10:45:00	2016-07-23 10:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1945	7	2016-07-23 14:00:00	2016-07-23 14:00:00	2016-07-23 14:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1946	7	2016-07-23 18:30:00	2016-07-23 18:30:00	2016-07-23 18:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1947	7	2016-07-23 17:45:00	2016-07-23 17:45:00	2016-07-23 17:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1948	7	2016-07-23 21:00:00	2016-07-23 21:00:00	2016-07-23 21:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1949	7	2016-07-23 22:45:00	2016-07-23 22:45:00	2016-07-23 22:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1950	7	2016-07-24 03:30:00	2016-07-24 03:30:00	2016-07-24 03:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1951	7	2016-07-24 02:00:00	2016-07-24 02:00:00	2016-07-24 02:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1952	7	2016-07-24 10:15:00	2016-07-24 10:15:00	2016-07-24 10:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1953	7	2016-07-24 08:15:00	2016-07-24 08:15:00	2016-07-24 08:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1954	7	2016-07-24 13:30:00	2016-07-24 13:30:00	2016-07-24 13:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1955	7	2016-07-24 16:45:00	2016-07-24 16:45:00	2016-07-24 16:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1956	7	2016-07-24 17:00:00	2016-07-24 17:00:00	2016-07-24 17:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1957	7	2016-07-24 20:45:00	2016-07-24 20:45:00	2016-07-24 20:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1958	7	2016-07-24 22:30:00	2016-07-24 22:30:00	2016-07-24 22:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1959	7	2016-07-24 23:00:00	2016-07-24 23:00:00	2016-07-24 23:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1960	7	2016-07-25 01:30:00	2016-07-25 01:30:00	2016-07-25 01:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1961	7	2016-07-25 06:30:00	2016-07-25 06:30:00	2016-07-25 06:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1962	7	2016-07-25 10:45:00	2016-07-25 10:45:00	2016-07-25 10:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1963	7	2016-07-25 09:30:00	2016-07-25 09:30:00	2016-07-25 09:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1964	7	2016-07-25 11:45:00	2016-07-25 11:45:00	2016-07-25 11:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1965	7	2016-07-25 15:00:00	2016-07-25 15:00:00	2016-07-25 15:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1966	7	2016-07-25 20:15:00	2016-07-25 20:15:00	2016-07-25 20:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1967	7	2016-07-25 20:00:00	2016-07-25 20:00:00	2016-07-25 20:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1968	7	2016-07-26 00:15:00	2016-07-26 00:15:00	2016-07-26 00:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1969	7	2016-07-26 01:00:00	2016-07-26 01:00:00	2016-07-26 01:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1970	7	2016-07-26 05:00:00	2016-07-26 05:00:00	2016-07-26 05:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1971	7	2016-07-26 07:45:00	2016-07-26 07:45:00	2016-07-26 07:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1972	7	2016-07-26 09:15:00	2016-07-26 09:15:00	2016-07-26 09:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1973	7	2016-07-26 11:30:00	2016-07-26 11:30:00	2016-07-26 11:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1974	7	2016-07-26 16:15:00	2016-07-26 16:15:00	2016-07-26 16:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1975	7	2016-07-26 18:00:00	2016-07-26 18:00:00	2016-07-26 18:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1976	7	2016-07-26 22:15:00	2016-07-26 22:15:00	2016-07-26 22:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1977	7	2016-07-26 21:00:00	2016-07-26 21:00:00	2016-07-26 21:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1978	7	2016-07-27 02:00:00	2016-07-27 02:00:00	2016-07-27 02:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1979	7	2016-07-27 03:00:00	2016-07-27 03:00:00	2016-07-27 03:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1980	7	2016-07-27 07:00:00	2016-07-27 07:00:00	2016-07-27 07:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1981	7	2016-07-27 08:00:00	2016-07-27 08:00:00	2016-07-27 08:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1982	7	2016-07-27 09:30:00	2016-07-27 09:30:00	2016-07-27 09:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1983	7	2016-07-27 15:30:00	2016-07-27 15:30:00	2016-07-27 15:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1984	7	2016-07-27 16:15:00	2016-07-27 16:15:00	2016-07-27 16:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1985	7	2016-07-27 18:15:00	2016-07-27 18:15:00	2016-07-27 18:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1986	7	2016-07-27 23:00:00	2016-07-27 23:00:00	2016-07-27 23:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1987	7	2016-07-27 22:45:00	2016-07-27 22:45:00	2016-07-27 22:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1988	7	2016-07-28 02:15:00	2016-07-28 02:15:00	2016-07-28 02:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1989	7	2016-07-28 03:15:00	2016-07-28 03:15:00	2016-07-28 03:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1990	7	2016-07-28 06:45:00	2016-07-28 06:45:00	2016-07-28 06:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1991	7	2016-07-28 07:00:00	2016-07-28 07:00:00	2016-07-28 07:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1992	7	2016-07-28 12:30:00	2016-07-28 12:30:00	2016-07-28 12:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1993	7	2016-07-28 13:15:00	2016-07-28 13:15:00	2016-07-28 13:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1994	7	2016-07-28 16:00:00	2016-07-28 16:00:00	2016-07-28 16:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1995	7	2016-07-28 20:45:00	2016-07-28 20:45:00	2016-07-28 20:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1996	7	2016-07-28 21:45:00	2016-07-28 21:45:00	2016-07-28 21:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1997	7	2016-07-28 23:00:00	2016-07-28 23:00:00	2016-07-28 23:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1998	7	2016-07-29 05:30:00	2016-07-29 05:30:00	2016-07-29 05:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
1999	7	2016-07-29 00:15:00	2016-07-29 00:15:00	2016-07-29 00:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2000	7	2016-07-29 04:15:00	2016-07-29 04:15:00	2016-07-29 04:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2001	7	2016-07-29 09:00:00	2016-07-29 09:00:00	2016-07-29 09:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2002	7	2016-07-29 15:30:00	2016-07-29 15:30:00	2016-07-29 15:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2003	7	2016-07-29 10:15:00	2016-07-29 10:15:00	2016-07-29 10:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2004	7	2016-07-29 14:15:00	2016-07-29 14:15:00	2016-07-29 14:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2005	7	2016-07-29 19:00:00	2016-07-29 19:00:00	2016-07-29 19:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2006	7	2016-07-30 01:30:00	2016-07-30 01:30:00	2016-07-30 01:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2007	7	2016-07-29 20:15:00	2016-07-29 20:15:00	2016-07-29 20:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2008	7	2016-07-30 00:15:00	2016-07-30 00:15:00	2016-07-30 00:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2009	7	2016-07-30 05:00:00	2016-07-30 05:00:00	2016-07-30 05:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2010	7	2016-07-30 04:45:00	2016-07-30 04:45:00	2016-07-30 04:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2011	7	2016-07-31 10:15:00	2016-07-31 10:15:00	2016-07-31 10:15:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2012	7	2016-07-31 11:45:00	2016-07-31 11:45:00	2016-07-31 11:45:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2013	7	2016-07-31 16:30:00	2016-07-31 16:30:00	2016-07-31 16:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2014	7	2016-07-31 23:00:00	2016-07-31 23:00:00	2016-07-31 23:00:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2015	7	2016-07-31 18:30:00	2016-07-31 18:30:00	2016-07-31 18:30:00	\N	1	\N	1	\N	F	\N	\N	7	\N
2016	7	2016-07-31 22:46:00	2016-07-31 22:46:00	2016-07-31 22:46:00	\N	1	\N	1	\N	F	\N	\N	7	\N
\.


--
-- TOC entry 3647 (class 0 OID 60107)
-- Dependencies: 203
-- Data for Name: observationconstellation; Type: TABLE DATA; Schema: public; Owner: user
--

COPY observationconstellation (observationconstellationid, observablepropertyid, procedureid, observationtypeid, offeringid, deleted, hiddenchild) FROM stdin;
4	4	1	4	1	F	F
5	5	1	4	1	F	F
1	1	1	4	1	F	F
7	7	1	4	1	F	F
2	2	1	4	1	F	F
6	6	1	4	1	F	F
3	3	1	4	1	F	F
\.


--
-- TOC entry 3863 (class 0 OID 0)
-- Dependencies: 230
-- Name: observationconstellationid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('observationconstellationid_seq', 7, true);


--
-- TOC entry 3648 (class 0 OID 60116)
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
-- TOC entry 3864 (class 0 OID 0)
-- Dependencies: 231
-- Name: observationid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('observationid_seq', 2016, true);


--
-- TOC entry 3649 (class 0 OID 60121)
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
-- TOC entry 3865 (class 0 OID 0)
-- Dependencies: 232
-- Name: observationtypeid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('observationtypeid_seq', 7, true);


--
-- TOC entry 3650 (class 0 OID 60126)
-- Dependencies: 206
-- Data for Name: offering; Type: TABLE DATA; Schema: public; Owner: user
--

COPY offering (offeringid, hibernatediscriminator, identifier, codespace, name, codespacename, description, disabled) FROM stdin;
1	T	weather-data-muenster	\N	Weatherdata	\N	\N	F
\.


--
-- TOC entry 3651 (class 0 OID 60136)
-- Dependencies: 207
-- Data for Name: offeringallowedfeaturetype; Type: TABLE DATA; Schema: public; Owner: user
--

COPY offeringallowedfeaturetype (offeringid, featureofinteresttypeid) FROM stdin;
1	1
\.


--
-- TOC entry 3652 (class 0 OID 60141)
-- Dependencies: 208
-- Data for Name: offeringallowedobservationtype; Type: TABLE DATA; Schema: public; Owner: user
--

COPY offeringallowedobservationtype (offeringid, observationtypeid) FROM stdin;
1	7
1	2
1	5
1	1
1	3
1	6
1	4
\.


--
-- TOC entry 3653 (class 0 OID 60146)
-- Dependencies: 209
-- Data for Name: offeringhasrelatedfeature; Type: TABLE DATA; Schema: public; Owner: user
--

COPY offeringhasrelatedfeature (offeringid, relatedfeatureid) FROM stdin;
\.


--
-- TOC entry 3866 (class 0 OID 0)
-- Dependencies: 233
-- Name: offeringid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('offeringid_seq', 1, true);


--
-- TOC entry 3654 (class 0 OID 60151)
-- Dependencies: 210
-- Data for Name: parameter; Type: TABLE DATA; Schema: public; Owner: user
--

COPY parameter (parameterid, observationid, definition, title, value) FROM stdin;
\.


--
-- TOC entry 3867 (class 0 OID 0)
-- Dependencies: 234
-- Name: parameterid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('parameterid_seq', 1, false);


--
-- TOC entry 3868 (class 0 OID 0)
-- Dependencies: 235
-- Name: procdescformatid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('procdescformatid_seq', 1, true);


--
-- TOC entry 3629 (class 0 OID 59978)
-- Dependencies: 185
-- Data for Name: procedure; Type: TABLE DATA; Schema: public; Owner: user
--

COPY procedure (procedureid, hibernatediscriminator, proceduredescriptionformatid, identifier, codespace, name, codespacename, description, deleted, disabled, descriptionfile, referenceflag) FROM stdin;
1	T	1	52NorthWS1	\N	\N	\N	\N	F	F	\N	F
\.


--
-- TOC entry 3655 (class 0 OID 60159)
-- Dependencies: 211
-- Data for Name: proceduredescriptionformat; Type: TABLE DATA; Schema: public; Owner: user
--

COPY proceduredescriptionformat (proceduredescriptionformatid, proceduredescriptionformat) FROM stdin;
1	http://www.opengis.net/sensorML/1.0.1
\.


--
-- TOC entry 3869 (class 0 OID 0)
-- Dependencies: 236
-- Name: procedureid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('procedureid_seq', 1, true);


--
-- TOC entry 3656 (class 0 OID 60164)
-- Dependencies: 212
-- Data for Name: relatedfeature; Type: TABLE DATA; Schema: public; Owner: user
--

COPY relatedfeature (relatedfeatureid, featureofinterestid) FROM stdin;
\.


--
-- TOC entry 3657 (class 0 OID 60169)
-- Dependencies: 213
-- Data for Name: relatedfeaturehasrole; Type: TABLE DATA; Schema: public; Owner: user
--

COPY relatedfeaturehasrole (relatedfeatureid, relatedfeatureroleid) FROM stdin;
\.


--
-- TOC entry 3870 (class 0 OID 0)
-- Dependencies: 237
-- Name: relatedfeatureid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('relatedfeatureid_seq', 1, false);


--
-- TOC entry 3658 (class 0 OID 60174)
-- Dependencies: 214
-- Data for Name: relatedfeaturerole; Type: TABLE DATA; Schema: public; Owner: user
--

COPY relatedfeaturerole (relatedfeatureroleid, relatedfeaturerole) FROM stdin;
\.


--
-- TOC entry 3871 (class 0 OID 0)
-- Dependencies: 238
-- Name: relatedfeatureroleid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('relatedfeatureroleid_seq', 1, false);


--
-- TOC entry 3659 (class 0 OID 60179)
-- Dependencies: 215
-- Data for Name: resulttemplate; Type: TABLE DATA; Schema: public; Owner: user
--

COPY resulttemplate (resulttemplateid, offeringid, observablepropertyid, procedureid, featureofinterestid, identifier, resultstructure, resultencoding) FROM stdin;
\.


--
-- TOC entry 3872 (class 0 OID 0)
-- Dependencies: 239
-- Name: resulttemplateid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('resulttemplateid_seq', 1, false);


--
-- TOC entry 3660 (class 0 OID 60187)
-- Dependencies: 216
-- Data for Name: sensorsystem; Type: TABLE DATA; Schema: public; Owner: user
--

COPY sensorsystem (parentsensorid, childsensorid) FROM stdin;
\.


--
-- TOC entry 3661 (class 0 OID 60192)
-- Dependencies: 217
-- Data for Name: series; Type: TABLE DATA; Schema: public; Owner: user
--

COPY series (seriesid, featureofinterestid, observablepropertyid, procedureid, deleted, published, firsttimestamp, lasttimestamp, firstnumericvalue, lastnumericvalue, unitid) FROM stdin;
2	2	5	1	F	T	2016-07-01 01:15:00	2016-07-31 21:46:00	-1.80000000000000004	-0.800000000000000044	2
6	2	6	1	F	T	2016-07-01 01:15:00	2016-07-31 22:31:00	0	0	6
1	2	4	1	F	T	2016-06-30 23:45:00	2016-07-31 22:15:00	986	995	1
7	2	3	1	F	T	2016-07-01 00:30:00	2016-07-31 23:00:00	23.5	27.3000000000000007	7
3	2	1	1	F	T	2016-07-01 03:15:00	2016-07-31 21:31:00	0	0	3
4	2	7	1	F	T	2016-07-01 00:15:00	2016-07-31 22:16:00	99	97	4
5	2	2	1	F	T	2016-07-01 04:15:00	2016-07-31 23:45:00	135	330	5
\.


--
-- TOC entry 3873 (class 0 OID 0)
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
-- TOC entry 3662 (class 0 OID 60201)
-- Dependencies: 218
-- Data for Name: swedataarrayvalue; Type: TABLE DATA; Schema: public; Owner: user
--

COPY swedataarrayvalue (observationid, value) FROM stdin;
\.


--
-- TOC entry 3663 (class 0 OID 60209)
-- Dependencies: 219
-- Data for Name: textvalue; Type: TABLE DATA; Schema: public; Owner: user
--

COPY textvalue (observationid, value) FROM stdin;
\.


--
-- TOC entry 3664 (class 0 OID 60217)
-- Dependencies: 220
-- Data for Name: unit; Type: TABLE DATA; Schema: public; Owner: user
--

COPY unit (unitid, unit) FROM stdin;
1	hPa
2	Cel
3	mm
4	%
5	deg
6	lx
7	m/s
\.


--
-- TOC entry 3874 (class 0 OID 0)
-- Dependencies: 241
-- Name: unitid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('unitid_seq', 7, true);


--
-- TOC entry 3665 (class 0 OID 60222)
-- Dependencies: 221
-- Data for Name: validproceduretime; Type: TABLE DATA; Schema: public; Owner: user
--

COPY validproceduretime (validproceduretimeid, procedureid, proceduredescriptionformatid, starttime, endtime, descriptionxml) FROM stdin;
1	1	1	2016-05-24 12:59:14.595	\N	<sml:SensorML xmlns:sml="http://www.opengis.net/sensorML/1.0.1" xmlns:sos="http://www.opengis.net/sos/1.0" version="1.0.1">\n  <sml:member>\n    <sml:System xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:gml="http://www.opengis.net/gml" xmlns:ows="http://www.opengeospatial.net/ows" xmlns:ogc="http://www.opengis.net/ogc" xmlns:om="http://www.opengis.net/om/1.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n      <sml:identification>\n        <sml:IdentifierList>\n          <sml:identifier name="uniqueID">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:uniqueID">\n              <sml:value>52NorthWS1</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="longName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:longName">\n              <sml:value>This is a WS2500 weather station setup at 52North, Münster in Germany.</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="shortName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:shortName">\n              <sml:value>52North HWS 1</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="productName">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:productName">\n              <sml:value>ELV Radio Weather Station WS 2500</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="modelNumber">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:modelNumber">\n              <sml:value>53759</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="manufacturer">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:manufacturer">\n              <sml:value>ELV Elektronik AG</sml:value>\n            </sml:Term>\n          </sml:identifier>\n          <sml:identifier name="operator">\n            <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:operator">\n              <sml:value>52North, Münster, Germany.</sml:value>\n            </sml:Term>\n          </sml:identifier>\n        </sml:IdentifierList>\n      </sml:identification>\n      <sml:classification>\n        <sml:ClassifierList>\n          <sml:classifier name="intendedApplication">\n            <sml:Term definition="urn:ogc:def:classifier:OGC:1.0:application">\n              <sml:value>weather</sml:value>\n            </sml:Term>\n          </sml:classifier>\n          <sml:classifier name="sensorType">\n            <sml:Term definition="urn:ogc:def:classifier:OGC:1.0:sensorType">\n              <sml:value>thermometer</sml:value>\n            </sml:Term>\n          </sml:classifier>\n          <sml:classifier name="sensorType">\n            <sml:Term definition="urn:ogc:def:classifier:OGC:1.0:sensorType">\n              <sml:value>rain gauge</sml:value>\n            </sml:Term>\n          </sml:classifier>\n          <sml:classifier name="sensorType">\n            <sml:Term definition="urn:ogc:def:classifier:OGC:1.0:sensorType">\n              <sml:value>barometer</sml:value>\n            </sml:Term>\n          </sml:classifier>\n          <sml:classifier name="sensorType">\n            <sml:Term definition="urn:ogc:def:classifier:OGC:1.0:sensorType">\n              <sml:value>anemometer</sml:value>\n            </sml:Term>\n          </sml:classifier>\n          <sml:classifier name="sensorType">\n            <sml:Term definition="urn:ogc:def:classifier:OGC:1.0:sensorType">\n              <sml:value>Luminance Sensor</sml:value>\n            </sml:Term>\n          </sml:classifier>\n        </sml:ClassifierList>\n      </sml:classification>\n      <sml:capabilities>\n        <swe:SimpleDataRecord definition="urn:ogc:def:property:status">\n          <!--station is collecting data-->\n          <swe:field name="status">\n            <swe:Boolean>\n              <swe:value>false</swe:value>\n            </swe:Boolean>\n          </swe:field>\n          <swe:field name="mobile">\n            <swe:Boolean>\n              <swe:value>false</swe:value>\n            </swe:Boolean>\n          </swe:field>\n          <swe:field name="measuringInterval">\n            <swe:Quantity definition="urn:ogc:def:property:OGC:1.0:measuringInterval">\n              <gml:description>The measuring interval of the weather station.</gml:description>\n              <swe:uom code="min" xlink:href="urn:x-ogc:def:uom:OGC:min"/>\n              <swe:value>3</swe:value>\n            </swe:Quantity>\n          </swe:field>\n          <swe:field name="transmissionFrequency">\n            <swe:Quantity definition="urn:ogc:def:property:OGC:1.0:transmissionFrequency">\n              <gml:description>The transmission frequency of the weather station.</gml:description>\n              <swe:uom code="MHz" xlink:href="urn:x-ogc:def:uom:OGC:MHz"/>\n              <swe:value>433.92</swe:value>\n            </swe:Quantity>\n          </swe:field>\n          <swe:field name="powerSupply">\n            <swe:Text definition="urn:ogc:def:property:OGC:1.0:powerSupply">\n              <gml:description>The power supply of the weather station.</gml:description>\n              <swe:value>4 Baby cells 1.5V = 7,5 V / 500 mA</swe:value>\n            </swe:Text>\n          </swe:field>\n        </swe:SimpleDataRecord>\n      </sml:capabilities>\n      <sml:contact>\n        <sml:ResponsibleParty gml:id="contact">\n          <sml:individualName>Jürrens, Eike Hinderk</sml:individualName>\n          <sml:organizationName>52North, Münster, Germany.</sml:organizationName>\n          <sml:contactInfo>\n            <sml:phone>\n              <sml:voice>+49 251 39 63 71 33</sml:voice>\n              <sml:facsimile>+49 251 39 63 71 11</sml:facsimile>\n            </sml:phone>\n            <sml:address>\n              <sml:deliveryPoint>Martin-Luther-King-Weg 24</sml:deliveryPoint>\n              <sml:city>Münster</sml:city>\n              <sml:administrativeArea>North Rhine-Westfalia</sml:administrativeArea>\n              <sml:postalCode>49151</sml:postalCode>\n              <sml:country>Germany</sml:country>\n              <sml:electronicMailAddress>e.h.juerrens@52north.org</sml:electronicMailAddress>\n            </sml:address>\n          </sml:contactInfo>\n        </sml:ResponsibleParty>\n      </sml:contact>\n      <sml:documentation xlink:role="urn:ogc:def:object:OGC:1.0:image">\n        <sml:Document>\n          <gml:description>photo of the weather station</gml:description>\n          <sml:format>image/jpg</sml:format>\n          <sml:onlineResource xlink:href="http://ifgi.uni-muenster.de/~e_juer01/WS2500.jpg"/>\n        </sml:Document>\n      </sml:documentation>\n      <sml:history>\n        <sml:EventList>\n          <sml:member name="deployDate">\n            <sml:Event>\n              <sml:date>2007-06-01</sml:date>\n              <gml:description>Event of deploying the weather station.</gml:description>\n              <sml:contact xlink:href="#contact"/>\n            </sml:Event>\n          </sml:member>\n        </sml:EventList>\n      </sml:history>\n      <sml:spatialReferenceFrame>\n        <gml:EngineeringCRS gml:id="STATION_FRAME">\n          <gml:srsName>Spatial Reference System of station</gml:srsName>\n          <gml:usesCS xlink:href="urn:ogc:def:cs:OGC:1.0:Grid2dSquareCS"/>\n          <gml:usesEngineeringDatum>\n            <gml:EngineeringDatum gml:id="STATION_DATUM">\n              <gml:datumName>Spatial Datum of station</gml:datumName>\n              <gml:anchorPoint>Origin is the base of the station.\n                                    Z is vertical.\n                                    X and Y are orthogonal to Z.\n                                    X is orthogonal to Y.\n                                    Y is parallel to the North-Axis and points to North.\n                                    X is orthogonal to the North-Axis and points to East.</gml:anchorPoint>\n            </gml:EngineeringDatum>\n          </gml:usesEngineeringDatum>\n        </gml:EngineeringCRS>\n      </sml:spatialReferenceFrame>\n      <sml:position name="stationPosition">\n        <swe:Position fixed="false" referenceFrame="urn:ogc:def:crs:EPSG:4326">\n          <swe:location>\n            <swe:Vector gml:id="STATION_LOCATION">\n              <swe:coordinate name="latitude">\n                <swe:Quantity axisID="y">\n                  <swe:uom code="deg" xlink:href="urn:x-ogc:def:uom:OGC:deg"/>\n                  <swe:value>51.9348</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="longitude">\n                <swe:Quantity axisID="x">\n                  <swe:uom code="deg" xlink:href="urn:x-ogc:def:uom:OGC:deg"/>\n                  <swe:value>7.6524</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n              <swe:coordinate name="altitude">\n                <swe:Quantity axisID="z">\n                  <!--200-->\n                  <swe:uom code="m" xlink:href="urn:x-ogc:def:uom:OGC:m"/>\n                  <swe:value>200</swe:value>\n                </swe:Quantity>\n              </swe:coordinate>\n            </swe:Vector>\n          </swe:location>\n        </swe:Position>\n      </sml:position>\n      <sml:inputs>\n        <sml:InputList>\n          <sml:input name="precipitation">\n            <swe:ObservableProperty definition="Precipitation"/>\n          </sml:input>\n          <sml:input name="atmosphericTemperature">\n            <swe:ObservableProperty definition="AirTemperature"/>\n          </sml:input>\n          <sml:input name="atmosphericPressure">\n            <swe:ObservableProperty definition="AtmosphericPressure"/>\n          </sml:input>\n          <sml:input name="wind">\n            <swe:ObservableProperty definition="Wind"/>\n          </sml:input>\n          <sml:input name="luminance">\n            <swe:ObservableProperty definition="Luminance"/>\n          </sml:input>\n          <sml:input name="humidity">\n            <swe:ObservableProperty definition="RelativeHumidity"/>\n          </sml:input>\n        </sml:InputList>\n      </sml:inputs>\n      <sml:outputs>\n        <sml:OutputList>\n          <sml:output name="precipitation">\n            <swe:Quantity definition="Precipitation1Hour">\n              <swe:uom code="mm" xlink:href="urn:x-ogc:def:uom:OGC:mm"/>\n            </swe:Quantity>\n          </sml:output>\n          <sml:output name="windDirection">\n            <swe:Quantity definition="WindDirection">\n              <swe:uom code="deg" xlink:href="urn:x-ogc:def:uom:OGC:deg"/>\n            </swe:Quantity>\n          </sml:output>\n          <sml:output name="windSpeed">\n            <swe:Quantity definition="WindSpeed">\n              <swe:uom code="m/s" xlink:href="urn:x-ogc:def:uom:OGC:m_s"/>\n            </swe:Quantity>\n          </sml:output>\n          <sml:output name="barometricPressure">\n            <swe:Quantity definition="BarometricPressure">\n              <swe:uom code="hPa" xlink:href="urn:x-ogc:def:uom:OGC:hPa"/>\n            </swe:Quantity>\n          </sml:output>\n          <sml:output name="temperature">\n            <swe:Quantity definition="Temperature">\n              <swe:uom code="Cel" xlink:href="urn:x-ogc:def:uom:OGC:degC"/>\n            </swe:Quantity>\n          </sml:output>\n          <sml:output name="luminance">\n            <swe:Quantity definition="Luminance">\n              <swe:uom code="lx" xlink:href="urn:x-ogc:def:uom:OGC:lx"/>\n            </swe:Quantity>\n          </sml:output>\n          <sml:output name="relativeHumidity">\n            <swe:Quantity definition="RelativeHumidity">\n              <swe:uom code="%" xlink:href="urn:x-ogc:def:uom:OGC:percent"/>\n            </swe:Quantity>\n          </sml:output>\n        </sml:OutputList>\n      </sml:outputs>\n      <sml:components>\n        <sml:ComponentList>\n          <sml:component name="rain-gauge">\n            <sml:Component>\n              <sml:identification>\n                <sml:IdentifierList>\n                  <sml:identifier>\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:uniqueID">\n                      <sml:value>urn:ogc:object:feature:Sensor:OSIRIS-HWS:rain-gauge-3d3b239f-7696-4864-9d07-15447eae2b93</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                  <sml:identifier>\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:longName">\n                      <sml:value>OSIRIS Rain-Gauge at weather station 52N HWS 1</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                  <sml:identifier>\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:shortName">\n                      <sml:value>OSIRIS Rain-Gauge 52N HWS 1</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                  <sml:identifier name="productName">\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:productName">\n                      <sml:value>S2000R-1 precipitation sensor</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                </sml:IdentifierList>\n              </sml:identification>\n              <sml:classification>\n                <sml:ClassifierList>\n                  <sml:classifier name="sensorType">\n                    <sml:Term definition="urn:ogc:def:classifier:OGC:1.0:sensorType">\n                      <sml:value>Precipitation</sml:value>\n                    </sml:Term>\n                  </sml:classifier>\n                </sml:ClassifierList>\n              </sml:classification>\n              <sml:capabilities>\n                <swe:SimpleDataRecord definition="urn:ogc:def:property:status">\n                  <swe:field name="status">\n                    <swe:Text definition="urn:ogc:def:property:OGC:1.0:status">\n                      <gml:description>The operating status of the sensor.</gml:description>\n                      <swe:value>active</swe:value>\n                    </swe:Text>\n                  </swe:field>\n                </swe:SimpleDataRecord>\n              </sml:capabilities>\n              <sml:position name="rainGaugePosition">\n                <swe:Position>\n                  <swe:location>\n                    <swe:Vector>\n                      <swe:coordinate name="easting">\n                        <swe:Quantity axisID="x">\n                          <swe:uom code="m" xlink:href="urn:x-ogc:def:uom:OGC:m"/>\n                          <swe:value>0</swe:value>\n                        </swe:Quantity>\n                      </swe:coordinate>\n                      <swe:coordinate name="northing">\n                        <swe:Quantity axisID="y">\n                          <swe:uom code="m" xlink:href="urn:x-ogc:def:uom:OGC:m"/>\n                          <swe:value>0</swe:value>\n                        </swe:Quantity>\n                      </swe:coordinate>\n                      <swe:coordinate name="altitude">\n                        <swe:Quantity axisID="z">\n                          <swe:uom code="m" xlink:href="urn:x-ogc:def:uom:OGC:m"/>\n                          <swe:value>0</swe:value>\n                        </swe:Quantity>\n                      </swe:coordinate>\n                    </swe:Vector>\n                  </swe:location>\n                </swe:Position>\n              </sml:position>\n              <sml:inputs>\n                <sml:InputList>\n                  <sml:input name="precipitation">\n                    <swe:ObservableProperty definition="Precipitation"/>\n                  </sml:input>\n                </sml:InputList>\n              </sml:inputs>\n              <sml:outputs>\n                <sml:OutputList>\n                  <sml:output name="precipitation">\n                    <swe:Quantity definition="Precipitation1Hour">\n                      <swe:uom code="mm" xlink:href="urn:x-ogc:def:uom:OGC:mm"/>\n                    </swe:Quantity>\n                  </sml:output>\n                </sml:OutputList>\n              </sml:outputs>\n            </sml:Component>\n          </sml:component>\n          <sml:component name="radiometer">\n            <sml:Component>\n              <sml:identification>\n                <sml:IdentifierList>\n                  <sml:identifier>\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:uniqueID">\n                      <sml:value>urn:ogc:object:feature:Sensor:OSIRIS-HWS:radiometer-3d3b239f-7696-4864-9d07-15447eae2b93</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                  <sml:identifier name="longName">\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:longName">\n                      <sml:value>OSIRIS Radiometer at weather station 52N HWS 1</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                  <sml:identifier name="shortName">\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:shortName">\n                      <sml:value>OSIRIS Radiometer 52N HWS 1</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                  <sml:identifier name="productName">\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:productName">\n                      <sml:value>S2500H luminance sensor</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                </sml:IdentifierList>\n              </sml:identification>\n              <sml:classification>\n                <sml:ClassifierList>\n                  <sml:classifier name="sensorType">\n                    <sml:Term definition="urn:ogc:def:classifier:OGC:1.0:sensorType">\n                      <sml:value>radiometer</sml:value>\n                    </sml:Term>\n                  </sml:classifier>\n                </sml:ClassifierList>\n              </sml:classification>\n              <sml:capabilities>\n                <swe:SimpleDataRecord definition="urn:ogc:def:property:status">\n                  <swe:field name="status">\n                    <swe:Text definition="urn:ogc:def:property:OGC:1.0:status">\n                      <gml:description>The operating status of the sensor.</gml:description>\n                      <swe:value>active</swe:value>\n                    </swe:Text>\n                  </swe:field>\n                </swe:SimpleDataRecord>\n              </sml:capabilities>\n              <sml:position name="radiometerPosition">\n                <swe:Position referenceFrame="#STATION_FRAME">\n                  <swe:location>\n                    <swe:Vector>\n                      <swe:coordinate name="easting">\n                        <swe:Quantity axisID="x">\n                          <swe:uom code="m" xlink:href="urn:x-ogc:def:uom:OGC:m"/>\n                          <swe:value>0</swe:value>\n                        </swe:Quantity>\n                      </swe:coordinate>\n                      <swe:coordinate name="northing">\n                        <swe:Quantity axisID="y">\n                          <swe:uom code="m" xlink:href="urn:x-ogc:def:uom:OGC:m"/>\n                          <swe:value>0</swe:value>\n                        </swe:Quantity>\n                      </swe:coordinate>\n                      <swe:coordinate name="altitude">\n                        <swe:Quantity axisID="z">\n                          <swe:uom code="m" xlink:href="urn:x-ogc:def:uom:OGC:m"/>\n                          <swe:value>0</swe:value>\n                        </swe:Quantity>\n                      </swe:coordinate>\n                    </swe:Vector>\n                  </swe:location>\n                </swe:Position>\n              </sml:position>\n              <sml:inputs>\n                <sml:InputList>\n                  <sml:input name="luminance">\n                    <swe:ObservableProperty definition="Luminance"/>\n                  </sml:input>\n                </sml:InputList>\n              </sml:inputs>\n              <sml:outputs>\n                <sml:OutputList>\n                  <sml:output name="luminance">\n                    <swe:Quantity definition="Luminance">\n                      <swe:uom code="lx" xlink:href="urn:x-ogc:def:uom:OGC:lx"/>\n                    </swe:Quantity>\n                  </sml:output>\n                </sml:OutputList>\n              </sml:outputs>\n            </sml:Component>\n          </sml:component>\n          <sml:component name="hygrometer">\n            <sml:Component>\n              <sml:identification>\n                <sml:IdentifierList>\n                  <sml:identifier>\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:uniqueID">\n                      <sml:value>urn:ogc:object:feature:Sensor:OSIRIS-HWS:hygrometer-3d3b239f-7696-4864-9d07-15447eae2b93</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                  <sml:identifier name="longName">\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:longName">\n                      <sml:value>OSIRIS Hygrometer at weather station 52N HWS 1</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                  <sml:identifier name="shortName">\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:shortName">\n                      <sml:value>OSIRIS Hygrometer 52N HWS 1</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                  <sml:identifier name="productName">\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:productName">\n                      <sml:value>S2500H humidity sensor</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                </sml:IdentifierList>\n              </sml:identification>\n              <sml:classification>\n                <sml:ClassifierList>\n                  <sml:classifier name="sensorType">\n                    <sml:Term definition="urn:ogc:def:classifier:OGC:1.0:sensorType">\n                      <sml:value>hygrometer</sml:value>\n                    </sml:Term>\n                  </sml:classifier>\n                </sml:ClassifierList>\n              </sml:classification>\n              <sml:capabilities>\n                <swe:SimpleDataRecord definition="urn:ogc:def:property:status">\n                  <swe:field name="status">\n                    <swe:Text definition="urn:ogc:def:property:OGC:1.0:status">\n                      <gml:description>The operating status of the sensor.</gml:description>\n                      <swe:value>active</swe:value>\n                    </swe:Text>\n                  </swe:field>\n                </swe:SimpleDataRecord>\n              </sml:capabilities>\n              <sml:position name="hygrometerPosition">\n                <swe:Position referenceFrame="#STATION_FRAME">\n                  <swe:location>\n                    <swe:Vector>\n                      <swe:coordinate name="easting">\n                        <swe:Quantity axisID="x">\n                          <swe:uom code="m" xlink:href="urn:x-ogc:def:uom:OGC:m"/>\n                          <swe:value>0</swe:value>\n                        </swe:Quantity>\n                      </swe:coordinate>\n                      <swe:coordinate name="northing">\n                        <swe:Quantity axisID="y">\n                          <swe:uom code="m" xlink:href="urn:x-ogc:def:uom:OGC:m"/>\n                          <swe:value>0</swe:value>\n                        </swe:Quantity>\n                      </swe:coordinate>\n                      <swe:coordinate name="altitude">\n                        <swe:Quantity axisID="z">\n                          <swe:uom code="m" xlink:href="urn:x-ogc:def:uom:OGC:m"/>\n                          <swe:value>0</swe:value>\n                        </swe:Quantity>\n                      </swe:coordinate>\n                    </swe:Vector>\n                  </swe:location>\n                </swe:Position>\n              </sml:position>\n              <sml:inputs>\n                <sml:InputList>\n                  <sml:input name="humidity">\n                    <swe:ObservableProperty definition="Humidity"/>\n                  </sml:input>\n                </sml:InputList>\n              </sml:inputs>\n              <sml:outputs>\n                <sml:OutputList>\n                  <sml:output name="relativeHumidity">\n                    <swe:Quantity definition="RelativeHumidity">\n                      <swe:uom code="%" xlink:href="urn:x-ogc:def:uom:OGC:percent"/>\n                    </swe:Quantity>\n                  </sml:output>\n                </sml:OutputList>\n              </sml:outputs>\n            </sml:Component>\n          </sml:component>\n          <sml:component name="aenometer">\n            <sml:Component>\n              <sml:identification>\n                <sml:IdentifierList>\n                  <sml:identifier>\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:uniqueID">\n                      <sml:value>urn:ogc:object:feature:Sensor:OSIRIS-HWS:aenometer-3d3b239f-7696-4864-9d07-15447eae2b93</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                  <sml:identifier>\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:longName">\n                      <sml:value>OSIRIS Aenometer at weather station 52N HWS 1</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                  <sml:identifier>\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:shortName">\n                      <sml:value>OSIRIS Aenometer 52N HWS 1</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                  <sml:identifier name="productName">\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:productName">\n                      <sml:value>S2000W windsensor</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                </sml:IdentifierList>\n              </sml:identification>\n              <sml:classification>\n                <sml:ClassifierList>\n                  <!--FIXME kann es 2 classifier fÃƒÂ¼r eine Component geben?-->\n                  <sml:classifier name="sensorType">\n                    <sml:Term definition="urn:ogc:def:classifier:OGC:1.0:sensorType">\n                      <sml:value>windSpeed</sml:value>\n                    </sml:Term>\n                  </sml:classifier>\n                  <sml:classifier name="sensorType">\n                    <sml:Term definition="urn:ogc:def:classifier:OGC:1.0:sensorType">\n                      <sml:value>windDirection</sml:value>\n                    </sml:Term>\n                  </sml:classifier>\n                </sml:ClassifierList>\n              </sml:classification>\n              <sml:capabilities>\n                <swe:SimpleDataRecord definition="urn:ogc:def:property:status">\n                  <swe:field name="status">\n                    <swe:Text definition="urn:ogc:def:property:OGC:1.0:status">\n                      <gml:description>The operating status of the sensor.</gml:description>\n                      <swe:value>active</swe:value>\n                    </swe:Text>\n                  </swe:field>\n                </swe:SimpleDataRecord>\n              </sml:capabilities>\n              <sml:position name="aenometerPosition">\n                <swe:Position>\n                  <swe:location>\n                    <swe:Vector>\n                      <swe:coordinate name="easting">\n                        <swe:Quantity axisID="x">\n                          <swe:uom code="m" xlink:href="urn:x-ogc:def:uom:OGC:m"/>\n                          <swe:value>0</swe:value>\n                        </swe:Quantity>\n                      </swe:coordinate>\n                      <swe:coordinate name="northing">\n                        <swe:Quantity axisID="y">\n                          <swe:uom code="m" xlink:href="urn:x-ogc:def:uom:OGC:m"/>\n                          <swe:value>0</swe:value>\n                        </swe:Quantity>\n                      </swe:coordinate>\n                      <swe:coordinate name="altitude">\n                        <swe:Quantity axisID="z">\n                          <swe:uom code="m" xlink:href="urn:x-ogc:def:uom:OGC:m"/>\n                          <swe:value>0</swe:value>\n                        </swe:Quantity>\n                      </swe:coordinate>\n                    </swe:Vector>\n                  </swe:location>\n                </swe:Position>\n              </sml:position>\n              <sml:inputs>\n                <sml:InputList>\n                  <sml:input name="wind">\n                    <swe:ObservableProperty definition="Wind"/>\n                  </sml:input>\n                </sml:InputList>\n              </sml:inputs>\n              <sml:outputs>\n                <sml:OutputList>\n                  <sml:output name="windSpeed">\n                    <swe:Quantity definition="WindSpeed">\n                      <gml:metaDataProperty>\n                        <sos:offering>\n                          <sos:id>WIND_SPEED</sos:id>\n                          <sos:name>The speed of the measured wind.</sos:name>\n                        </sos:offering>\n                      </gml:metaDataProperty>\n                      <swe:uom code="m/s" xlink:href="urn:x-ogc:def:uom:OGC:m_s"/>\n                    </swe:Quantity>\n                  </sml:output>\n                  <sml:output name="windDirection">\n                    <swe:Quantity definition="WindDirection">\n                      <swe:uom code="deg" xlink:href="urn:x-ogc:def:uom:OGC:deg"/>\n                    </swe:Quantity>\n                  </sml:output>\n                </sml:OutputList>\n              </sml:outputs>\n            </sml:Component>\n          </sml:component>\n          <sml:component name="barmometer">\n            <sml:Component>\n              <sml:identification>\n                <sml:IdentifierList>\n                  <sml:identifier>\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:uniqueID">\n                      <sml:value>urn:ogc:object:feature:Sensor:OSIRIS-HWS:barometer-3d3b239f-7696-4864-9d07-15447eae2b93</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                  <sml:identifier name="longName">\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:longName">\n                      <sml:value>OSIRIS Barometer at weather station 52N HWS 1</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                  <sml:identifier name="shortName">\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:shortName">\n                      <sml:value>OSIRIS Barometer 52N HWS 1</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                  <sml:identifier name="productName">\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:productName">\n                      <sml:value>S2000B pressure sensor</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                </sml:IdentifierList>\n              </sml:identification>\n              <sml:classification>\n                <sml:ClassifierList>\n                  <sml:classifier name="sensorType">\n                    <sml:Term definition="urn:ogc:def:classifier:OGC:1.0:sensorType">\n                      <sml:value>barometer</sml:value>\n                    </sml:Term>\n                  </sml:classifier>\n                </sml:ClassifierList>\n              </sml:classification>\n              <sml:capabilities>\n                <swe:SimpleDataRecord definition="urn:ogc:def:property:status">\n                  <swe:field name="status">\n                    <swe:Text definition="urn:ogc:def:property:OGC:1.0:status">\n                      <gml:description>The operating status of the sensor.</gml:description>\n                      <swe:value>active</swe:value>\n                    </swe:Text>\n                  </swe:field>\n                </swe:SimpleDataRecord>\n              </sml:capabilities>\n              <sml:position name="thermometerPosition">\n                <swe:Position referenceFrame="#STATION_FRAME">\n                  <swe:location>\n                    <swe:Vector>\n                      <swe:coordinate name="easting">\n                        <swe:Quantity axisID="x">\n                          <swe:uom code="m" xlink:href="urn:x-ogc:def:uom:OGC:m"/>\n                          <swe:value>0</swe:value>\n                        </swe:Quantity>\n                      </swe:coordinate>\n                      <swe:coordinate name="northing">\n                        <swe:Quantity axisID="y">\n                          <swe:uom code="m" xlink:href="urn:x-ogc:def:uom:OGC:m"/>\n                          <swe:value>0</swe:value>\n                        </swe:Quantity>\n                      </swe:coordinate>\n                      <swe:coordinate name="altitude">\n                        <swe:Quantity axisID="z">\n                          <swe:uom code="m" xlink:href="urn:x-ogc:def:uom:OGC:m"/>\n                          <swe:value>0</swe:value>\n                        </swe:Quantity>\n                      </swe:coordinate>\n                    </swe:Vector>\n                  </swe:location>\n                </swe:Position>\n              </sml:position>\n              <sml:inputs>\n                <sml:InputList>\n                  <sml:input name="atmosphericPressure">\n                    <swe:ObservableProperty definition="AtmospericPressure"/>\n                  </sml:input>\n                </sml:InputList>\n              </sml:inputs>\n              <sml:outputs>\n                <sml:OutputList>\n                  <sml:output name="barometricPressure">\n                    <swe:Quantity definition="BarometricPressure">\n                      <swe:uom code="hPa" xlink:href="urn:x-ogc:def:uom:OGC:hPa"/>\n                    </swe:Quantity>\n                  </sml:output>\n                </sml:OutputList>\n              </sml:outputs>\n            </sml:Component>\n          </sml:component>\n          <sml:component name="thermometer">\n            <sml:Component>\n              <sml:identification>\n                <sml:IdentifierList>\n                  <sml:identifier>\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:uniqueID">\n                      <sml:value>urn:ogc:object:feature:Sensor:OSIRIS-HWS:thermometer-3d3b239f-7696-4864-9d07-15447eae2b93</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                  <sml:identifier name="longName">\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:longName">\n                      <sml:value>OSIRIS Thermometer at weather station 52N HWS 1</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                  <sml:identifier name="shortName">\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:shortName">\n                      <sml:value>OSIRIS Thermometer 52N HWS 1</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                  <sml:identifier name="productName">\n                    <sml:Term definition="urn:ogc:def:identifier:OGC:1.0:productName">\n                      <sml:value>S2001IA temperature sensor</sml:value>\n                    </sml:Term>\n                  </sml:identifier>\n                </sml:IdentifierList>\n              </sml:identification>\n              <sml:classification>\n                <sml:ClassifierList>\n                  <sml:classifier name="sensorType">\n                    <sml:Term definition="urn:ogc:def:classifier:OGC:1.0:sensorType">\n                      <sml:value>thermometer</sml:value>\n                    </sml:Term>\n                  </sml:classifier>\n                </sml:ClassifierList>\n              </sml:classification>\n              <sml:capabilities>\n                <swe:SimpleDataRecord definition="urn:ogc:def:property:status">\n                  <swe:field name="status">\n                    <swe:Text definition="urn:ogc:def:property:OGC:1.0:status">\n                      <gml:description>The operating status of the sensor.</gml:description>\n                      <swe:value>active</swe:value>\n                    </swe:Text>\n                  </swe:field>\n                </swe:SimpleDataRecord>\n              </sml:capabilities>\n              <sml:position name="thermometerPosition">\n                <swe:Position referenceFrame="#STATION_FRAME">\n                  <swe:location>\n                    <swe:Vector>\n                      <swe:coordinate name="easting">\n                        <swe:Quantity axisID="x">\n                          <swe:uom code="m" xlink:href="urn:x-ogc:def:uom:OGC:m"/>\n                          <swe:value>0</swe:value>\n                        </swe:Quantity>\n                      </swe:coordinate>\n                      <swe:coordinate name="northing">\n                        <swe:Quantity axisID="y">\n                          <swe:uom code="m" xlink:href="urn:x-ogc:def:uom:OGC:m"/>\n                          <swe:value>0</swe:value>\n                        </swe:Quantity>\n                      </swe:coordinate>\n                      <swe:coordinate name="altitude">\n                        <swe:Quantity axisID="z">\n                          <swe:uom code="m" xlink:href="urn:x-ogc:def:uom:OGC:m"/>\n                          <swe:value>0</swe:value>\n                        </swe:Quantity>\n                      </swe:coordinate>\n                    </swe:Vector>\n                  </swe:location>\n                </swe:Position>\n              </sml:position>\n              <sml:inputs>\n                <sml:InputList>\n                  <sml:input name="atmosphericTemperature">\n                    <swe:ObservableProperty definition="AirTemperature"/>\n                  </sml:input>\n                </sml:InputList>\n              </sml:inputs>\n              <sml:outputs>\n                <sml:OutputList>\n                  <sml:output name="temperature">\n                    <swe:Quantity definition="Temperature">\n                      <swe:uom code="Cel" xlink:href="urn:x-ogc:def:uom:OGC:degC"/>\n                    </swe:Quantity>\n                  </sml:output>\n                </sml:OutputList>\n              </sml:outputs>\n            </sml:Component>\n          </sml:component>\n        </sml:ComponentList>\n      </sml:components>\n    </sml:System>\n  </sml:member>\n</sml:SensorML>
\.


--
-- TOC entry 3875 (class 0 OID 0)
-- Dependencies: 242
-- Name: validproceduretimeid_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('validproceduretimeid_seq', 1, true);


--
-- TOC entry 3329 (class 2606 OID 59996)
-- Name: blobvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY blobvalue
    ADD CONSTRAINT blobvalue_pkey PRIMARY KEY (observationid);


--
-- TOC entry 3331 (class 2606 OID 60003)
-- Name: booleanvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY booleanvalue
    ADD CONSTRAINT booleanvalue_pkey PRIMARY KEY (observationid);


--
-- TOC entry 3333 (class 2606 OID 60008)
-- Name: categoryvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY categoryvalue
    ADD CONSTRAINT categoryvalue_pkey PRIMARY KEY (observationid);


--
-- TOC entry 3335 (class 2606 OID 60013)
-- Name: codespace_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY codespace
    ADD CONSTRAINT codespace_pkey PRIMARY KEY (codespaceid);


--
-- TOC entry 3337 (class 2606 OID 60233)
-- Name: codespaceuk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY codespace
    ADD CONSTRAINT codespaceuk UNIQUE (codespace);


--
-- TOC entry 3339 (class 2606 OID 60018)
-- Name: compositephenomenon_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY compositephenomenon
    ADD CONSTRAINT compositephenomenon_pkey PRIMARY KEY (childobservablepropertyid, parentobservablepropertyid);


--
-- TOC entry 3341 (class 2606 OID 60023)
-- Name: countvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY countvalue
    ADD CONSTRAINT countvalue_pkey PRIMARY KEY (observationid);


--
-- TOC entry 3343 (class 2606 OID 60031)
-- Name: featureofinterest_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY featureofinterest
    ADD CONSTRAINT featureofinterest_pkey PRIMARY KEY (featureofinterestid);


--
-- TOC entry 3349 (class 2606 OID 60036)
-- Name: featureofinteresttype_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY featureofinteresttype
    ADD CONSTRAINT featureofinteresttype_pkey PRIMARY KEY (featureofinteresttypeid);


--
-- TOC entry 3353 (class 2606 OID 60041)
-- Name: featurerelation_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY featurerelation
    ADD CONSTRAINT featurerelation_pkey PRIMARY KEY (childfeatureid, parentfeatureid);


--
-- TOC entry 3351 (class 2606 OID 60239)
-- Name: featuretypeuk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY featureofinteresttype
    ADD CONSTRAINT featuretypeuk UNIQUE (featureofinteresttype);


--
-- TOC entry 3345 (class 2606 OID 60237)
-- Name: featureurl; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY featureofinterest
    ADD CONSTRAINT featureurl UNIQUE (url);


--
-- TOC entry 3347 (class 2606 OID 60235)
-- Name: foiidentifieruk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY featureofinterest
    ADD CONSTRAINT foiidentifieruk UNIQUE (identifier);


--
-- TOC entry 3355 (class 2606 OID 60049)
-- Name: geometryvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY geometryvalue
    ADD CONSTRAINT geometryvalue_pkey PRIMARY KEY (observationid);


--
-- TOC entry 3357 (class 2606 OID 60241)
-- Name: i18nfeatureidentity; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY i18nfeatureofinterest
    ADD CONSTRAINT i18nfeatureidentity UNIQUE (objectid, locale);


--
-- TOC entry 3360 (class 2606 OID 60057)
-- Name: i18nfeatureofinterest_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY i18nfeatureofinterest
    ADD CONSTRAINT i18nfeatureofinterest_pkey PRIMARY KEY (id);


--
-- TOC entry 3362 (class 2606 OID 60065)
-- Name: i18nobservableproperty_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY i18nobservableproperty
    ADD CONSTRAINT i18nobservableproperty_pkey PRIMARY KEY (id);


--
-- TOC entry 3364 (class 2606 OID 60244)
-- Name: i18nobspropidentity; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY i18nobservableproperty
    ADD CONSTRAINT i18nobspropidentity UNIQUE (objectid, locale);


--
-- TOC entry 3367 (class 2606 OID 60073)
-- Name: i18noffering_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY i18noffering
    ADD CONSTRAINT i18noffering_pkey PRIMARY KEY (id);


--
-- TOC entry 3369 (class 2606 OID 60247)
-- Name: i18nofferingidentity; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY i18noffering
    ADD CONSTRAINT i18nofferingidentity UNIQUE (objectid, locale);


--
-- TOC entry 3372 (class 2606 OID 60081)
-- Name: i18nprocedure_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY i18nprocedure
    ADD CONSTRAINT i18nprocedure_pkey PRIMARY KEY (id);


--
-- TOC entry 3374 (class 2606 OID 60250)
-- Name: i18nprocedureidentity; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY i18nprocedure
    ADD CONSTRAINT i18nprocedureidentity UNIQUE (objectid, locale);


--
-- TOC entry 3377 (class 2606 OID 60086)
-- Name: numericvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY numericvalue
    ADD CONSTRAINT numericvalue_pkey PRIMARY KEY (observationid);


--
-- TOC entry 3379 (class 2606 OID 60096)
-- Name: observableproperty_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY observableproperty
    ADD CONSTRAINT observableproperty_pkey PRIMARY KEY (observablepropertyid);


--
-- TOC entry 3383 (class 2606 OID 60106)
-- Name: observation_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY observation
    ADD CONSTRAINT observation_pkey PRIMARY KEY (observationid);


--
-- TOC entry 3396 (class 2606 OID 60115)
-- Name: observationconstellation_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY observationconstellation
    ADD CONSTRAINT observationconstellation_pkey PRIMARY KEY (observationconstellationid);


--
-- TOC entry 3400 (class 2606 OID 60120)
-- Name: observationhasoffering_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY observationhasoffering
    ADD CONSTRAINT observationhasoffering_pkey PRIMARY KEY (observationid, offeringid);


--
-- TOC entry 3385 (class 2606 OID 60255)
-- Name: observationidentity; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY observation
    ADD CONSTRAINT observationidentity UNIQUE (seriesid, phenomenontimestart, phenomenontimeend, resulttime);


--
-- TOC entry 3404 (class 2606 OID 60125)
-- Name: observationtype_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY observationtype
    ADD CONSTRAINT observationtype_pkey PRIMARY KEY (observationtypeid);


--
-- TOC entry 3406 (class 2606 OID 60270)
-- Name: observationtypeuk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY observationtype
    ADD CONSTRAINT observationtypeuk UNIQUE (observationtype);


--
-- TOC entry 3387 (class 2606 OID 60257)
-- Name: obsidentifieruk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY observation
    ADD CONSTRAINT obsidentifieruk UNIQUE (identifier);


--
-- TOC entry 3398 (class 2606 OID 60263)
-- Name: obsnconstellationidentity; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY observationconstellation
    ADD CONSTRAINT obsnconstellationidentity UNIQUE (observablepropertyid, procedureid, offeringid);


--
-- TOC entry 3381 (class 2606 OID 60253)
-- Name: obspropidentifieruk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY observableproperty
    ADD CONSTRAINT obspropidentifieruk UNIQUE (identifier);


--
-- TOC entry 3408 (class 2606 OID 60135)
-- Name: offering_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY offering
    ADD CONSTRAINT offering_pkey PRIMARY KEY (offeringid);


--
-- TOC entry 3412 (class 2606 OID 60140)
-- Name: offeringallowedfeaturetype_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY offeringallowedfeaturetype
    ADD CONSTRAINT offeringallowedfeaturetype_pkey PRIMARY KEY (offeringid, featureofinteresttypeid);


--
-- TOC entry 3414 (class 2606 OID 60145)
-- Name: offeringallowedobservationtype_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY offeringallowedobservationtype
    ADD CONSTRAINT offeringallowedobservationtype_pkey PRIMARY KEY (offeringid, observationtypeid);


--
-- TOC entry 3416 (class 2606 OID 60150)
-- Name: offeringhasrelatedfeature_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY offeringhasrelatedfeature
    ADD CONSTRAINT offeringhasrelatedfeature_pkey PRIMARY KEY (offeringid, relatedfeatureid);


--
-- TOC entry 3410 (class 2606 OID 60272)
-- Name: offidentifieruk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY offering
    ADD CONSTRAINT offidentifieruk UNIQUE (identifier);


--
-- TOC entry 3418 (class 2606 OID 60158)
-- Name: parameter_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY parameter
    ADD CONSTRAINT parameter_pkey PRIMARY KEY (parameterid);


--
-- TOC entry 3420 (class 2606 OID 60274)
-- Name: procdescformatuk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY proceduredescriptionformat
    ADD CONSTRAINT procdescformatuk UNIQUE (proceduredescriptionformat);


--
-- TOC entry 3325 (class 2606 OID 59991)
-- Name: procedure_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY procedure
    ADD CONSTRAINT procedure_pkey PRIMARY KEY (procedureid);


--
-- TOC entry 3422 (class 2606 OID 60163)
-- Name: proceduredescriptionformat_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY proceduredescriptionformat
    ADD CONSTRAINT proceduredescriptionformat_pkey PRIMARY KEY (proceduredescriptionformatid);


--
-- TOC entry 3327 (class 2606 OID 60231)
-- Name: procidentifieruk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY procedure
    ADD CONSTRAINT procidentifieruk UNIQUE (identifier);


--
-- TOC entry 3424 (class 2606 OID 60168)
-- Name: relatedfeature_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY relatedfeature
    ADD CONSTRAINT relatedfeature_pkey PRIMARY KEY (relatedfeatureid);


--
-- TOC entry 3426 (class 2606 OID 60173)
-- Name: relatedfeaturehasrole_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY relatedfeaturehasrole
    ADD CONSTRAINT relatedfeaturehasrole_pkey PRIMARY KEY (relatedfeatureid, relatedfeatureroleid);


--
-- TOC entry 3428 (class 2606 OID 60178)
-- Name: relatedfeaturerole_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY relatedfeaturerole
    ADD CONSTRAINT relatedfeaturerole_pkey PRIMARY KEY (relatedfeatureroleid);


--
-- TOC entry 3430 (class 2606 OID 60276)
-- Name: relfeatroleuk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY relatedfeaturerole
    ADD CONSTRAINT relfeatroleuk UNIQUE (relatedfeaturerole);


--
-- TOC entry 3434 (class 2606 OID 60186)
-- Name: resulttemplate_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY resulttemplate
    ADD CONSTRAINT resulttemplate_pkey PRIMARY KEY (resulttemplateid);


--
-- TOC entry 3438 (class 2606 OID 60191)
-- Name: sensorsystem_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY sensorsystem
    ADD CONSTRAINT sensorsystem_pkey PRIMARY KEY (childsensorid, parentsensorid);


--
-- TOC entry 3440 (class 2606 OID 60200)
-- Name: series_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY series
    ADD CONSTRAINT series_pkey PRIMARY KEY (seriesid);


--
-- TOC entry 3443 (class 2606 OID 60282)
-- Name: seriesidentity; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY series
    ADD CONSTRAINT seriesidentity UNIQUE (featureofinterestid, observablepropertyid, procedureid);


--
-- TOC entry 3447 (class 2606 OID 60208)
-- Name: swedataarrayvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY swedataarrayvalue
    ADD CONSTRAINT swedataarrayvalue_pkey PRIMARY KEY (observationid);


--
-- TOC entry 3449 (class 2606 OID 60216)
-- Name: textvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY textvalue
    ADD CONSTRAINT textvalue_pkey PRIMARY KEY (observationid);


--
-- TOC entry 3451 (class 2606 OID 60221)
-- Name: unit_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY unit
    ADD CONSTRAINT unit_pkey PRIMARY KEY (unitid);


--
-- TOC entry 3453 (class 2606 OID 60287)
-- Name: unituk; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY unit
    ADD CONSTRAINT unituk UNIQUE (unit);


--
-- TOC entry 3455 (class 2606 OID 60229)
-- Name: validproceduretime_pkey; Type: CONSTRAINT; Schema: public; Owner: user; Tablespace: 
--

ALTER TABLE ONLY validproceduretime
    ADD CONSTRAINT validproceduretime_pkey PRIMARY KEY (validproceduretimeid);


--
-- TOC entry 3358 (class 1259 OID 60242)
-- Name: i18nfeatureidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX i18nfeatureidx ON i18nfeatureofinterest USING btree (objectid);


--
-- TOC entry 3365 (class 1259 OID 60245)
-- Name: i18nobspropidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX i18nobspropidx ON i18nobservableproperty USING btree (objectid);


--
-- TOC entry 3370 (class 1259 OID 60248)
-- Name: i18nofferingidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX i18nofferingidx ON i18noffering USING btree (objectid);


--
-- TOC entry 3375 (class 1259 OID 60251)
-- Name: i18nprocedureidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX i18nprocedureidx ON i18nprocedure USING btree (objectid);


--
-- TOC entry 3392 (class 1259 OID 60264)
-- Name: obsconstobspropidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX obsconstobspropidx ON observationconstellation USING btree (observablepropertyid);


--
-- TOC entry 3393 (class 1259 OID 60266)
-- Name: obsconstofferingidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX obsconstofferingidx ON observationconstellation USING btree (offeringid);


--
-- TOC entry 3394 (class 1259 OID 60265)
-- Name: obsconstprocedureidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX obsconstprocedureidx ON observationconstellation USING btree (procedureid);


--
-- TOC entry 3401 (class 1259 OID 60267)
-- Name: obshasoffobservationidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX obshasoffobservationidx ON observationhasoffering USING btree (observationid);


--
-- TOC entry 3402 (class 1259 OID 60268)
-- Name: obshasoffofferingidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX obshasoffofferingidx ON observationhasoffering USING btree (offeringid);


--
-- TOC entry 3388 (class 1259 OID 60260)
-- Name: obsphentimeendidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX obsphentimeendidx ON observation USING btree (phenomenontimeend);


--
-- TOC entry 3389 (class 1259 OID 60259)
-- Name: obsphentimestartidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX obsphentimestartidx ON observation USING btree (phenomenontimestart);


--
-- TOC entry 3390 (class 1259 OID 60261)
-- Name: obsresulttimeidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX obsresulttimeidx ON observation USING btree (resulttime);


--
-- TOC entry 3391 (class 1259 OID 60258)
-- Name: obsseriesidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX obsseriesidx ON observation USING btree (seriesid);


--
-- TOC entry 3431 (class 1259 OID 60278)
-- Name: resulttempeobspropidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX resulttempeobspropidx ON resulttemplate USING btree (observablepropertyid);


--
-- TOC entry 3432 (class 1259 OID 60280)
-- Name: resulttempidentifieridx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX resulttempidentifieridx ON resulttemplate USING btree (identifier);


--
-- TOC entry 3435 (class 1259 OID 60277)
-- Name: resulttempofferingidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX resulttempofferingidx ON resulttemplate USING btree (offeringid);


--
-- TOC entry 3436 (class 1259 OID 60279)
-- Name: resulttempprocedureidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX resulttempprocedureidx ON resulttemplate USING btree (procedureid);


--
-- TOC entry 3441 (class 1259 OID 60283)
-- Name: seriesfeatureidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX seriesfeatureidx ON series USING btree (featureofinterestid);


--
-- TOC entry 3444 (class 1259 OID 60284)
-- Name: seriesobspropidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX seriesobspropidx ON series USING btree (observablepropertyid);


--
-- TOC entry 3445 (class 1259 OID 60285)
-- Name: seriesprocedureidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX seriesprocedureidx ON series USING btree (procedureid);


--
-- TOC entry 3456 (class 1259 OID 60289)
-- Name: validproceduretimeendtimeidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX validproceduretimeendtimeidx ON validproceduretime USING btree (endtime);


--
-- TOC entry 3457 (class 1259 OID 60288)
-- Name: validproceduretimestarttimeidx; Type: INDEX; Schema: public; Owner: user; Tablespace: 
--

CREATE INDEX validproceduretimestarttimeidx ON validproceduretime USING btree (starttime);


--
-- TOC entry 3468 (class 2606 OID 60340)
-- Name: featurecodespaceidentifierfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY featureofinterest
    ADD CONSTRAINT featurecodespaceidentifierfk FOREIGN KEY (codespace) REFERENCES codespace(codespaceid);


--
-- TOC entry 3469 (class 2606 OID 60345)
-- Name: featurecodespacenamefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY featureofinterest
    ADD CONSTRAINT featurecodespacenamefk FOREIGN KEY (codespacename) REFERENCES codespace(codespaceid);


--
-- TOC entry 3467 (class 2606 OID 60335)
-- Name: featurefeaturetypefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY featureofinterest
    ADD CONSTRAINT featurefeaturetypefk FOREIGN KEY (featureofinteresttypeid) REFERENCES featureofinteresttype(featureofinteresttypeid);


--
-- TOC entry 3470 (class 2606 OID 60350)
-- Name: featureofinterestchildfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY featurerelation
    ADD CONSTRAINT featureofinterestchildfk FOREIGN KEY (childfeatureid) REFERENCES featureofinterest(featureofinterestid);


--
-- TOC entry 3471 (class 2606 OID 60355)
-- Name: featureofinterestparentfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY featurerelation
    ADD CONSTRAINT featureofinterestparentfk FOREIGN KEY (parentfeatureid) REFERENCES featureofinterest(featureofinterestid);


--
-- TOC entry 3493 (class 2606 OID 60465)
-- Name: fk_6vvrdxvd406n48gkm706ow1pt; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY offeringallowedfeaturetype
    ADD CONSTRAINT fk_6vvrdxvd406n48gkm706ow1pt FOREIGN KEY (offeringid) REFERENCES offering(offeringid);


--
-- TOC entry 3500 (class 2606 OID 60500)
-- Name: fk_6ynwkk91xe8p1uibmjt98sog3; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY relatedfeaturehasrole
    ADD CONSTRAINT fk_6ynwkk91xe8p1uibmjt98sog3 FOREIGN KEY (relatedfeatureid) REFERENCES relatedfeature(relatedfeatureid);


--
-- TOC entry 3489 (class 2606 OID 60445)
-- Name: fk_9ex7hawh3dbplkllmw5w3kvej; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observationhasoffering
    ADD CONSTRAINT fk_9ex7hawh3dbplkllmw5w3kvej FOREIGN KEY (observationid) REFERENCES observation(observationid);


--
-- TOC entry 3495 (class 2606 OID 60475)
-- Name: fk_lkljeohulvu7cr26pduyp5bd0; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY offeringallowedobservationtype
    ADD CONSTRAINT fk_lkljeohulvu7cr26pduyp5bd0 FOREIGN KEY (offeringid) REFERENCES offering(offeringid);


--
-- TOC entry 3473 (class 2606 OID 60365)
-- Name: i18nfeaturefeaturefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY i18nfeatureofinterest
    ADD CONSTRAINT i18nfeaturefeaturefk FOREIGN KEY (objectid) REFERENCES featureofinterest(featureofinterestid);


--
-- TOC entry 3474 (class 2606 OID 60370)
-- Name: i18nobspropobspropfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY i18nobservableproperty
    ADD CONSTRAINT i18nobspropobspropfk FOREIGN KEY (objectid) REFERENCES observableproperty(observablepropertyid);


--
-- TOC entry 3475 (class 2606 OID 60375)
-- Name: i18nofferingofferingfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY i18noffering
    ADD CONSTRAINT i18nofferingofferingfk FOREIGN KEY (objectid) REFERENCES offering(offeringid);


--
-- TOC entry 3476 (class 2606 OID 60380)
-- Name: i18nprocedureprocedurefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY i18nprocedure
    ADD CONSTRAINT i18nprocedureprocedurefk FOREIGN KEY (objectid) REFERENCES procedure(procedureid);


--
-- TOC entry 3481 (class 2606 OID 60405)
-- Name: obscodespaceidentifierfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observation
    ADD CONSTRAINT obscodespaceidentifierfk FOREIGN KEY (codespace) REFERENCES codespace(codespaceid);


--
-- TOC entry 3482 (class 2606 OID 60410)
-- Name: obscodespacenamefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observation
    ADD CONSTRAINT obscodespacenamefk FOREIGN KEY (codespacename) REFERENCES codespace(codespaceid);


--
-- TOC entry 3486 (class 2606 OID 60430)
-- Name: obsconstobservationiypefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observationconstellation
    ADD CONSTRAINT obsconstobservationiypefk FOREIGN KEY (observationtypeid) REFERENCES observationtype(observationtypeid);


--
-- TOC entry 3484 (class 2606 OID 60420)
-- Name: obsconstobspropfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observationconstellation
    ADD CONSTRAINT obsconstobspropfk FOREIGN KEY (observablepropertyid) REFERENCES observableproperty(observablepropertyid);


--
-- TOC entry 3487 (class 2606 OID 60435)
-- Name: obsconstofferingfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observationconstellation
    ADD CONSTRAINT obsconstofferingfk FOREIGN KEY (offeringid) REFERENCES offering(offeringid);


--
-- TOC entry 3464 (class 2606 OID 60320)
-- Name: observablepropertychildfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY compositephenomenon
    ADD CONSTRAINT observablepropertychildfk FOREIGN KEY (childobservablepropertyid) REFERENCES observableproperty(observablepropertyid);


--
-- TOC entry 3465 (class 2606 OID 60325)
-- Name: observablepropertyparentfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY compositephenomenon
    ADD CONSTRAINT observablepropertyparentfk FOREIGN KEY (parentobservablepropertyid) REFERENCES observableproperty(observablepropertyid);


--
-- TOC entry 3461 (class 2606 OID 60305)
-- Name: observationblobvaluefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY blobvalue
    ADD CONSTRAINT observationblobvaluefk FOREIGN KEY (observationid) REFERENCES observation(observationid);


--
-- TOC entry 3462 (class 2606 OID 60310)
-- Name: observationbooleanvaluefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY booleanvalue
    ADD CONSTRAINT observationbooleanvaluefk FOREIGN KEY (observationid) REFERENCES observation(observationid);


--
-- TOC entry 3463 (class 2606 OID 60315)
-- Name: observationcategoryvaluefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY categoryvalue
    ADD CONSTRAINT observationcategoryvaluefk FOREIGN KEY (observationid) REFERENCES observation(observationid);


--
-- TOC entry 3466 (class 2606 OID 60330)
-- Name: observationcountvaluefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY countvalue
    ADD CONSTRAINT observationcountvaluefk FOREIGN KEY (observationid) REFERENCES observation(observationid);


--
-- TOC entry 3472 (class 2606 OID 60360)
-- Name: observationgeometryvaluefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY geometryvalue
    ADD CONSTRAINT observationgeometryvaluefk FOREIGN KEY (observationid) REFERENCES observation(observationid);


--
-- TOC entry 3477 (class 2606 OID 60385)
-- Name: observationnumericvaluefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY numericvalue
    ADD CONSTRAINT observationnumericvaluefk FOREIGN KEY (observationid) REFERENCES observation(observationid);


--
-- TOC entry 3488 (class 2606 OID 60440)
-- Name: observationofferingfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observationhasoffering
    ADD CONSTRAINT observationofferingfk FOREIGN KEY (offeringid) REFERENCES offering(offeringid);


--
-- TOC entry 3480 (class 2606 OID 60400)
-- Name: observationseriesfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observation
    ADD CONSTRAINT observationseriesfk FOREIGN KEY (seriesid) REFERENCES series(seriesid);


--
-- TOC entry 3511 (class 2606 OID 60555)
-- Name: observationswedataarrayvaluefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY swedataarrayvalue
    ADD CONSTRAINT observationswedataarrayvaluefk FOREIGN KEY (observationid) REFERENCES observation(observationid);


--
-- TOC entry 3512 (class 2606 OID 60560)
-- Name: observationtextvaluefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY textvalue
    ADD CONSTRAINT observationtextvaluefk FOREIGN KEY (observationid) REFERENCES observation(observationid);


--
-- TOC entry 3483 (class 2606 OID 60415)
-- Name: observationunitfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observation
    ADD CONSTRAINT observationunitfk FOREIGN KEY (unitid) REFERENCES unit(unitid);


--
-- TOC entry 3485 (class 2606 OID 60425)
-- Name: obsnconstprocedurefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observationconstellation
    ADD CONSTRAINT obsnconstprocedurefk FOREIGN KEY (procedureid) REFERENCES procedure(procedureid);


--
-- TOC entry 3478 (class 2606 OID 60390)
-- Name: obspropcodespaceidentifierfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observableproperty
    ADD CONSTRAINT obspropcodespaceidentifierfk FOREIGN KEY (codespace) REFERENCES codespace(codespaceid);


--
-- TOC entry 3479 (class 2606 OID 60395)
-- Name: obspropcodespacenamefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY observableproperty
    ADD CONSTRAINT obspropcodespacenamefk FOREIGN KEY (codespacename) REFERENCES codespace(codespaceid);


--
-- TOC entry 3490 (class 2606 OID 60450)
-- Name: offcodespaceidentifierfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY offering
    ADD CONSTRAINT offcodespaceidentifierfk FOREIGN KEY (codespace) REFERENCES codespace(codespaceid);


--
-- TOC entry 3491 (class 2606 OID 60455)
-- Name: offcodespacenamefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY offering
    ADD CONSTRAINT offcodespacenamefk FOREIGN KEY (codespacename) REFERENCES codespace(codespaceid);


--
-- TOC entry 3492 (class 2606 OID 60460)
-- Name: offeringfeaturetypefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY offeringallowedfeaturetype
    ADD CONSTRAINT offeringfeaturetypefk FOREIGN KEY (featureofinteresttypeid) REFERENCES featureofinteresttype(featureofinteresttypeid);


--
-- TOC entry 3494 (class 2606 OID 60470)
-- Name: offeringobservationtypefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY offeringallowedobservationtype
    ADD CONSTRAINT offeringobservationtypefk FOREIGN KEY (observationtypeid) REFERENCES observationtype(observationtypeid);


--
-- TOC entry 3496 (class 2606 OID 60480)
-- Name: offeringrelatedfeaturefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY offeringhasrelatedfeature
    ADD CONSTRAINT offeringrelatedfeaturefk FOREIGN KEY (relatedfeatureid) REFERENCES relatedfeature(relatedfeatureid);


--
-- TOC entry 3459 (class 2606 OID 60295)
-- Name: proccodespaceidentifierfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY procedure
    ADD CONSTRAINT proccodespaceidentifierfk FOREIGN KEY (codespace) REFERENCES codespace(codespaceid);


--
-- TOC entry 3460 (class 2606 OID 60300)
-- Name: proccodespacenamefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY procedure
    ADD CONSTRAINT proccodespacenamefk FOREIGN KEY (codespacename) REFERENCES codespace(codespaceid);


--
-- TOC entry 3505 (class 2606 OID 60525)
-- Name: procedurechildfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY sensorsystem
    ADD CONSTRAINT procedurechildfk FOREIGN KEY (childsensorid) REFERENCES procedure(procedureid);


--
-- TOC entry 3506 (class 2606 OID 60530)
-- Name: procedureparenffk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY sensorsystem
    ADD CONSTRAINT procedureparenffk FOREIGN KEY (parentsensorid) REFERENCES procedure(procedureid);


--
-- TOC entry 3458 (class 2606 OID 60290)
-- Name: procprocdescformatfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY procedure
    ADD CONSTRAINT procprocdescformatfk FOREIGN KEY (proceduredescriptionformatid) REFERENCES proceduredescriptionformat(proceduredescriptionformatid);


--
-- TOC entry 3499 (class 2606 OID 60495)
-- Name: relatedfeatrelatedfeatrolefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY relatedfeaturehasrole
    ADD CONSTRAINT relatedfeatrelatedfeatrolefk FOREIGN KEY (relatedfeatureroleid) REFERENCES relatedfeaturerole(relatedfeatureroleid);


--
-- TOC entry 3498 (class 2606 OID 60490)
-- Name: relatedfeaturefeaturefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY relatedfeature
    ADD CONSTRAINT relatedfeaturefeaturefk FOREIGN KEY (featureofinterestid) REFERENCES featureofinterest(featureofinterestid);


--
-- TOC entry 3497 (class 2606 OID 60485)
-- Name: relatedfeatureofferingfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY offeringhasrelatedfeature
    ADD CONSTRAINT relatedfeatureofferingfk FOREIGN KEY (offeringid) REFERENCES offering(offeringid);


--
-- TOC entry 3504 (class 2606 OID 60520)
-- Name: resulttemplatefeatureidx; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY resulttemplate
    ADD CONSTRAINT resulttemplatefeatureidx FOREIGN KEY (featureofinterestid) REFERENCES featureofinterest(featureofinterestid);


--
-- TOC entry 3502 (class 2606 OID 60510)
-- Name: resulttemplateobspropfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY resulttemplate
    ADD CONSTRAINT resulttemplateobspropfk FOREIGN KEY (observablepropertyid) REFERENCES observableproperty(observablepropertyid);


--
-- TOC entry 3501 (class 2606 OID 60505)
-- Name: resulttemplateofferingidx; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY resulttemplate
    ADD CONSTRAINT resulttemplateofferingidx FOREIGN KEY (offeringid) REFERENCES offering(offeringid);


--
-- TOC entry 3503 (class 2606 OID 60515)
-- Name: resulttemplateprocedurefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY resulttemplate
    ADD CONSTRAINT resulttemplateprocedurefk FOREIGN KEY (procedureid) REFERENCES procedure(procedureid);


--
-- TOC entry 3507 (class 2606 OID 60535)
-- Name: seriesfeaturefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY series
    ADD CONSTRAINT seriesfeaturefk FOREIGN KEY (featureofinterestid) REFERENCES featureofinterest(featureofinterestid);


--
-- TOC entry 3508 (class 2606 OID 60540)
-- Name: seriesobpropfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY series
    ADD CONSTRAINT seriesobpropfk FOREIGN KEY (observablepropertyid) REFERENCES observableproperty(observablepropertyid);


--
-- TOC entry 3509 (class 2606 OID 60545)
-- Name: seriesprocedurefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY series
    ADD CONSTRAINT seriesprocedurefk FOREIGN KEY (procedureid) REFERENCES procedure(procedureid);


--
-- TOC entry 3510 (class 2606 OID 60550)
-- Name: seriesunitfk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY series
    ADD CONSTRAINT seriesunitfk FOREIGN KEY (unitid) REFERENCES unit(unitid);


--
-- TOC entry 3513 (class 2606 OID 60565)
-- Name: validproceduretimeprocedurefk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY validproceduretime
    ADD CONSTRAINT validproceduretimeprocedurefk FOREIGN KEY (procedureid) REFERENCES procedure(procedureid);


--
-- TOC entry 3514 (class 2606 OID 60570)
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


-- Completed on 2016-05-24 15:55:25 CEST

--
-- PostgreSQL database dump complete
--

