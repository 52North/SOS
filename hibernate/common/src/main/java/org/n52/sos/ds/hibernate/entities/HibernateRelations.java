/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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

import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.n52.sos.ds.hibernate.entities.feature.AbstractFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.RelatedObservation;
import org.n52.sos.ds.hibernate.entities.parameter.Parameter;

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

        void setObservationConstellation(
                ObservationConstellation observationConstellation);
    }

    interface HasObservationConstellations {
        String OBSERVATION_CONSTELLATIONS = "observationConstellations";

        Set<ObservationConstellation> getObservationConstellations();

        void setObservationConstellations(
                Set<ObservationConstellation> observationConstellations);
    }

    interface HasDescription {
        String DESCRIPTION = "description";

        String getDescription();

        void setDescription(String description);

        /**
         * Is description set
         *
         * @return <code>true</code>, if description is set
         */
        boolean isSetDescription();
    }

    interface HasCodespace {
        String CODESPACE = "codespace";

        Codespace getCodespace();

        void setCodespace(Codespace codespace);

        boolean isSetCodespace();
    }

    interface HasCodespaceName {
        String CODESPACE = "codespace";

        Codespace getCodespaceName();

        void setCodespaceName(Codespace codespaceName);

        boolean isSetCodespaceName();
    }

    interface HasDisabledFlag {
        String DIABLED = "disabled";

        void setDisabled(boolean disabled);

        boolean getDisabled();

        boolean isDisabled();
    }

    interface HasDeletedFlag {
        String DELETED = "deleted";

        void setDeleted(boolean deleted);

        boolean getDeleted();

        boolean isDeleted();
    }
    
    interface HasPublishedFlag {
        String PUBLISHED = "published";

        HasPublishedFlag setPublished(boolean published);

        boolean isPublished();
    }

    interface HasFeatureOfInterestType {
        String FEATURE_OF_INTEREST_TYPE = "featureOfInterestType";

        FeatureOfInterestType getFeatureOfInterestType();

        void setFeatureOfInterestType(
                FeatureOfInterestType featureOfInterestType);
    }

    interface HasFeatureOfInterestTypes {
        String FEATURE_OF_INTEREST_TYPES = "featureOfInterestTypes";

        Set<FeatureOfInterestType> getFeatureOfInterestTypes();

        void setFeatureOfInterestTypes(
                Set<FeatureOfInterestType> featureOfInterestTypes);
    }

    interface HasFeatureOfInterestGetter {
        String FEATURE_OF_INTEREST = "featureOfInterest";

        AbstractFeatureOfInterest getFeatureOfInterest();
    }

    interface HasFeatureOfInterest extends HasFeatureOfInterestGetter {

        void setFeatureOfInterest(AbstractFeatureOfInterest featureOfInterest);
    }

    interface HasReadableObservationContext
            extends HasObservablePropertyGetter,
                    HasProcedureGetter,
                    HasFeatureOfInterestGetter,
                    HasOfferingGetter {
    }

    interface HasWriteableObservationContext
            extends HasReadableObservationContext,
                    HasObservableProperty,
                    HasProcedure,
                    HasFeatureOfInterest,
                    HasOffering {
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

        void setGeom(Geometry geom);

        /**
         * Is geometry set
         *
         * @return <code>true</code>, if geometry is set
         */
        boolean isSetGeometry();
    }

    interface HasHiddenChildFlag {
        String HIDDEN_CHILD = "hiddenChild";

        void setHiddenChild(boolean hiddenChild);

        boolean isHiddenChild();
    }

    interface HasChildFlag {
        String CHILD = "child";
        void setChild(boolean child);
        boolean isChild();
    }

    interface HasParentFlag {
        String PARENT = "parent";
        void setParent(boolean parent);
        boolean isParent();
    }

    interface HasIdentifier {
        String IDENTIFIER = "identifier";

        String getIdentifier();

        void setIdentifier(String identifier);

        /**
         * Is identifier set
         *
         * @return <code>true</code>, if identifier is set
         */
        boolean isSetIdentifier();
    }

    interface HasName {
        String NAME = "name";

        String getName();

        void setName(String name);

        boolean isSetName();

    }

    @Deprecated
    interface HasObservation {
        String OBSERVATION = "observation";

        Observation<?> getObservation();

        void setObservation(Observation<?> observation);
    }

    interface HasObservablePropertyGetter {

        String OBSERVABLE_PROPERTY = "observableProperty";

        ObservableProperty getObservableProperty();
    }

    interface HasObservableProperty extends HasObservablePropertyGetter {

        void setObservableProperty(ObservableProperty observableProperty);
    }
    
    interface HasOfferingGetter {

        String OFFERING = "offering";

        Offering getOffering();
    }

    interface HasOffering extends HasOfferingGetter{
    
        void setOffering(Offering offering);
    
        boolean isSetOffering();
    }

    interface HasObservationType {
        String OBSERVATION_TYPE = "observationType";

        ObservationType getObservationType();

        void setObservationType(ObservationType observationType);

        boolean isSetObservationType();
    }

    interface HasObservationTypes {
        String OBSERVATION_TYPES = "observationTypes";

        Set<ObservationType> getObservationTypes();

        void setObservationTypes(Set<ObservationType> observationTypes);
    }

    interface HasPhenomenonTime {

        String PHENOMENON_TIME_START = "phenomenonTimeStart";

        String PHENOMENON_TIME_END = "phenomenonTimeEnd";

        /**
         * Get the start phenomenon time
         *
         * @return Start phenomenon time
         */
        Date getPhenomenonTimeStart();

        /**
         * Set the start phenomenon time
         *
         * @param phenomenonTimeStart
         *                            Start phenomenon time to set
         */
        void setPhenomenonTimeStart(Date phenomenonTimeStart);

        /**
         * Get the end phenomenon time
         *
         * @return End phenomenon time
         */
        Date getPhenomenonTimeEnd();

        /**
         * Set the end phenomenon time
         *
         * @param phenomenonTimeEnd
         *                          End phenomenon time to set
         */
        void setPhenomenonTimeEnd(Date phenomenonTimeEnd);
    }

    interface HasProcedureGetter {
        String PROCEDURE = "procedure";

        Procedure getProcedure();
    }

    interface HasProcedure extends HasProcedureGetter {

        void setProcedure(Procedure procedure);
    }
    
    interface HasSeriesType {

        void setSeriesType(String seriesType);
        
        String getSeriesType();
        
        boolean isSetSeriesType();
    }
    
    interface HasProcedureDescriptionFormat {
        String PROCEDURE_DESCRIPTION_FORMAT = "procedureDescriptionFormat";

        ProcedureDescriptionFormat getProcedureDescriptionFormat();

        void setProcedureDescriptionFormat(
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

    interface HasResultTime {

        String RESULT_TIME = "resultTime";

        /**
         * Get the result time
         *
         * @return Result time
         */
        Date getResultTime();

        /**
         * Set the result tiem
         *
         * @param resultTime
         *                   Result tiem to set
         */
        void setResultTime(Date resultTime);
    }

    interface HasUnit {
        String UNIT = "unit";

        Unit getUnit();

        void setUnit(Unit unit);

        /**
         * Is unit set
         *
         * @return <code>true</code>, if unit is set
         */
        boolean isSetUnit();
    }

    interface HasValidTime {

        String VALID_TIME_START = "validTimeStart";

        String VALID_TIME_END = "validTimeEnd";

        /**
         * Get the start valid time
         *
         * @return Start valid time
         */
        Date getValidTimeStart();

        /**
         * Set the start valid time
         *
         * @param validTimeStart
         *                       Start valid time to set
         */
        void setValidTimeStart(Date validTimeStart);

        /**
         * Get the end valid time
         *
         * @return End valid time
         */
        Date getValidTimeEnd();

        /**
         * Set the end valid time
         *
         * @param validTimeEnd
         *                     End valid time to set
         */
        void setValidTimeEnd(Date validTimeEnd);

        boolean isSetValidTime();
    }

    interface HasUrl {
        String URL = "url";

        String getUrl();

        void setUrl(String url);
        
        boolean isSetUrl();
    }
    
    
    interface GetStringValue {

        boolean isSetValue();

        String getValueAsString();

    }

    interface HasValue<T> extends GetStringValue {
        String VALUE = "value";

        T getValue();

        void setValue(T value);

    }

    interface HasUnitValue<T> extends HasUnit, HasValue<T> {}

    interface HasOfferings {
        String OFFERINGS = "offerings";

        Set<Offering> getOfferings();

//        Object getOffering();
        void setOfferings(Object offerings);
        
        boolean isSetOfferings();

    }
    
    interface HasParameters {
        String PARAMETERS = "parameters";

        Set<Parameter> getParameters();

        void setParameters(Object parameters);
        
        boolean hasParameters();

    }
    
    interface HasRelatedObservations {
        String PARAMETERS = "relatedObservations";

        Set<RelatedObservation> getRelatedObservations();

        void setRelatedObservations(Set<RelatedObservation> relatedObservations);
        
        boolean hasRelatedObservations();

    }

    interface HasObservableProperties {
        String OBSERVABLE_PROPERTIES = "observableProperties";

        Set<ObservableProperty> getObservableProperties();

        void setObservableProperties(
                Set<ObservableProperty> observableProperties);
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

        /**
         * Is srid set
         *
         * @return <code>true</code>, if srid is set
         */
        boolean isSetSrid();
    }

    interface HasCoordinate extends HasSrid {
        String LONGITUDE = "longitude";

        String LATITUDE = "latitude";

        String ALTITUDE = "altitude";

        Object getLongitude();

        HasCoordinate setLongitude(Object longitude);

        Object getLatitude();

        HasCoordinate setLatitude(Object latitude);

        /**
         * Are longitude and latitude set
         *
         * @return <code>true</code>, if longitude and latitude are set
         */
        boolean isSetLongLat();

        Object getAltitude();

        HasCoordinate setAltitude(Object altitude);

        /**
         * Is altitude set
         *
         * @return <code>true</code>, if altitude is set
         */
        boolean isSetAltitude();

        boolean isSpatial();
    }

    interface HasValidProcedureTimes {
        String VALID_PROCEDURE_TIMES = "validProcedureTimes";

        Set<ValidProcedureTime> getValidProcedureTimes();

        void setValidProcedureTimes(
                Set<ValidProcedureTime> validProcedureTimes);
    }

    interface HasParentChilds<T> {
        String PARENTS = "parents";

        String CHILDS = "childs";

        Set<T> getParents();

        void setParents(Set<T> parents);

        void addParent(T parent);
        
        boolean hasParents();

        Set<T> getChilds();

        void setChilds(Set<T> childs);

        void addChild(T child);
        
        boolean hasChilds();
    }

    interface HasObservationId {
        String OBS_ID = "observationId";

        /**
         * Get the observation id
         *
         * @return Observation id
         */
        long getObservationId();

        /**
         * Set the observation id
         *
         * @param observationId
         *                      Observation id to set
         */
        void setObservationId(final long observationId);
    }
    
    interface HasFeatureOfInterestId {
        String FEAT_ID = "featureOfInterestId";

        /**
         * Get the featureOfInterest id
         *
         * @return FeatureOfInterest id
         */
        long getFeatureOfInterestId();

        /**
         * Set the featureOfInterest id
         *
         * @param featureOfInterestId
         *                      FeatureOfInterest id to set
         */
        void setFeatureOfInterestId(final long featureOfInterestId);
    }
    
    interface HasParamerterId {
        String ID = "parameterId";

        /**
         * Get the parameter id
         *
         * @return parameter id
         */
        long getParameterId();

        /**
         * Set the parameter id
         *
         * @param parameterId
         *                      ParameterId id to set
         */
        void setParameterId(final long parameterId);
    }
    
    interface HasLocale {
        String LOCALE = "locale";

        Locale getLocale();

        void setLocale(Locale locale);

        boolean isSetLocale();
    }

    interface HasSamplingGeometry {

        String SAMPLING_GEOMETRY = "samplingGeometry";

        Geometry getSamplingGeometry();

        void setSamplingGeometry(Geometry samplingGeometry);

        boolean hasSamplingGeometry();

    }
    
    interface HasVertical {
        
        Double getVerticalFrom();

        void setVerticalTo(Double verticalFrom);

        boolean hasVerticalTo();

        Double getVerticalTo();

        void setVerticalFrom(Double verticalTo);

        boolean hasVerticalFrom();

        String getVerticalFromName();

        void setVerticalFromName(String name);

        boolean hasVerticalFromName();

        String getVerticalToName();

        void setVerticalToName(String name);

        boolean hasVerticalToName();

        Unit getVerticalUnit();

        void setVerticalUnit(Unit unit);

        boolean hasVerticalUnit();
    }
}
