/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.ds.hibernate.entities;

import java.util.Set;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Interfaces that entities can implement to share constants and to make clear
 * which entities have which relations. Allows to throw compile time errors for
 * non existing relations.
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public interface HibernateRelations {

    interface HasObservationConstellation {
        String OBSERVATION_CONSTELLATION = "observationConstellation";

        ObservationConstellation getObservationConstellation();

        void setObservationConstellation(ObservationConstellation observationConstellation);
    }

    interface HasObservationConstellations {
        String OBSERVATION_CONSTELLATIONS = "observationConstellations";

        Set<ObservationConstellation> getObservationConstellations();

        void setObservationConstellations(Set<ObservationConstellation> observationConstellations);
    }

    interface HasDescription {
        String DESCRIPTION = "description";

        String getDescription();

        void setDescription(String description);

        boolean isSetDescription();
    }

    interface HasCodespace {
        String CODESPACE = "codespace";

        Codespace getCodespace();

        void setCodespace(Codespace codespace);

        boolean isSetCodespace();
    }

    interface HasDeletedFlag {
        String DELETED = "deleted";

        HasDeletedFlag setDeleted(boolean deleted);

        boolean isDeleted();
    }

    interface HasFeatureOfInterestType {
        String FEATURE_OF_INTEREST_TYPE = "featureOfInterestType";

        FeatureOfInterestType getFeatureOfInterestType();

        void setFeatureOfInterestType(FeatureOfInterestType featureOfInterestType);
    }

    interface HasFeatureOfInterestTypes {
        String FEATURE_OF_INTEREST_TYPES = "featureOfInterestTypes";

        Set<FeatureOfInterestType> getFeatureOfInterestTypes();

        void setFeatureOfInterestTypes(Set<FeatureOfInterestType> featureOfInterestTypes);
    }
    
    interface HasFeatureOfInterestGetter {
        String FEATURE_OF_INTEREST = "featureOfInterest";

        FeatureOfInterest getFeatureOfInterest();
    }

    interface HasFeatureOfInterest extends HasFeatureOfInterestGetter {

        void setFeatureOfInterest(FeatureOfInterest featureOfInterest);
    }

    interface HasDescriptionXml {
        String DESCRIPTION_XML = "descriptionXml";

        String getDescriptionXml();

        void setDescriptionXml(String descriptionXml);

        boolean isSetDescriptionXml();
    }

    interface HasGeometry {
        String GEOMETRY = "geom";

        Geometry getGeom();

        HasGeometry setGeom(Geometry geom);
    }

    interface HasHiddenChildFlag {
        String HIDDEN_CHILD = "hiddenChild";

        HasHiddenChildFlag setHiddenChild(boolean hiddenChild);

        boolean isHiddenChild();
    }

    interface HasIdentifier {
        String IDENTIFIER = "identifier";

        String getIdentifier();

        HasIdentifier setIdentifier(String identifier);
    }

    interface HasName {
        String NAME = "name";

        String getName();

        void setName(String name);
    }

    interface HasStringOrClobNames {
        String NAME = "name";

        String getName();

        void setName(String name);

        boolean isSetName();
    }

    interface HasObservation {
        String OBSERVATION = "observation";

        Observation getObservation();

        HasObservation setObservation(Observation observation);
    }

    interface HasObservablePropertyGetter {
        
        String OBSERVABLE_PROPERTY = "observableProperty";
        
        ObservableProperty getObservableProperty();
    }
    
    interface HasObservableProperty extends HasObservablePropertyGetter {
        
        void setObservableProperty(ObservableProperty observableProperty);
    }

    interface HasObservationType {
        String OBSERVATION_TYPE = "observationType";

        ObservationType getObservationType();

        void setObservationType(ObservationType observationType);
    }

    interface HasObservationTypes {
        String OBSERVATION_TYPES = "observationTypes";

        Set<ObservationType> getObservationTypes();

        void setObservationTypes(Set<ObservationType> observationTypes);
    }

    interface HasOffering {
        String OFFERING = "offering";

        void setOffering(Offering offering);

        Offering getOffering();
    }
    
    interface HasProcedureGetter {
        String PROCEDURE = "procedure";

        Procedure getProcedure();
    }

    interface HasProcedure extends HasProcedureGetter {

        void setProcedure(Procedure procedure);
    }

    interface HasProcedureDescriptionFormat {
        String PROCEDURE_DESCRIPTION_FORMAT = "procedureDescriptionFormat";

        ProcedureDescriptionFormat getProcedureDescriptionFormat();

        HasProcedureDescriptionFormat setProcedureDescriptionFormat(
                ProcedureDescriptionFormat procedureDescriptionFormat);
    }

    interface HasRelatedFeatureRoles {
        String RELATED_FEATURE_ROLES = "relatedFeatureRoles";

        Set<RelatedFeatureRole> getRelatedFeatureRoles();

        void setRelatedFeatureRoles(Set<RelatedFeatureRole> relatedFeatureRoles);
    }

    interface HasRelatedFeatures {
        String RELATED_FEATURES = "relatedFeatures";

        Set<RelatedFeature> getRelatedFeatures();

        void setRelatedFeatures(Set<RelatedFeature> relatedFeatures);
    }

    interface HasResultEncoding {
        String RESULT_ENCODING = "resultEncoding";

        String getResultEncoding();

        void setResultEncoding(String resultEncoding);

        boolean isSetResultEncoding();
    }

    interface HasResultStructure {
        String RESULT_STRUCTURE = "resultStructure";

        String getResultStructure();

        void setResultStructure(String resultStructure);

        boolean isSetResultStructure();
    }

    interface HasUnit {
        String UNIT = "unit";

        Unit getUnit();

        void setUnit(Unit unit);
    }

    interface HasUrl {
        String URL = "url";

        String getUrl();

        void setUrl(String url);
    }

    interface HasValue<T> {
        String VALUE = "value";

        T getValue();

        void setValue(T value);

        boolean isSetValue();
    }

    interface HasOfferings {
        String OFFERINGS = "offerings";

        Set<Offering> getOfferings();
        
//        Object getOffering();
        
        void setOfferings(Object offerings);
        
    }

    interface HasObservableProperties {
        String OBSERVABLE_PROPERTIES = "observableProperties";

        Set<ObservableProperty> getObservableProperties();

        void setObservableProperties(Set<ObservableProperty> observableProperties);
    }

    interface GeoColumnsId {
        String COORD_DIMENSION = "coordDimension";

        String SRID = "srid";

        String TABLE_CATALOG = "FTableCatalog";

        String TABLE_NAME = "FTableName";

        String TABLE_SCHEMA = "FTableSchema";

        String TYPE = "type";

        Integer getCoordDimension();

        void setCoordDimension(Integer coordDimension);

        String getFTableCatalog();

        void setFTableCatalog(String fTableCatalog);

        String getFTableName();

        void setFTableName(String fTableName);

        String getFTableSchema();

        void setFTableSchema(String fTableSchema);

        Integer getSrid();

        void setSrid(Integer srid);

        String getType();

        void setType(String type);
    }

    interface HasSrid {
        String SRID = "srid";

        int getSrid();

        HasSrid setSrid(int srid);
    }

    interface HasCoordinate extends HasSrid {
        String LONGITUDE = "longitude";

        String LATITUDE = "latitude";

        String ALTITUDE = "altitude";

        Object getLongitude();

        HasCoordinate setLongitude(Object longitude);

        Object getLatitude();

        HasCoordinate setLatitude(Object latitude);

        Object getAltitude();

        HasCoordinate setAltitude(Object altitude);
    }

    interface HasValidProcedureTimes {
        String VALID_PROCEDURE_TIMES = "validProcedureTimes";

        Set<ValidProcedureTime> getValidProcedureTimes();

        HasValidProcedureTimes setValidProcedureTimes(Set<ValidProcedureTime> validProcedureTimes);
    }

    interface HasParentChilds<T, S> {
        String PARENTS = "parents";

        String CHILDS = "childs";

        Set<T> getParents();

        HasParentChilds<T, S> setParents(Set<T> parents);

        Set<T> getChilds();

        HasParentChilds<T, S> setChilds(Set<T> childs);
    }
}
