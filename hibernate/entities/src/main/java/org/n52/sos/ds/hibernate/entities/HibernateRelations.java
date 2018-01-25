/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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

import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.hibernate.entities.feature.AbstractFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.RelatedObservation;
import org.n52.sos.ds.hibernate.entities.parameter.Parameter;

import com.google.common.base.Strings;
import org.locationtech.jts.geom.Geometry;

/**
 * Interfaces that entities can implement to share constants and to make clear which entities have which relations.
 * Allows to throw compile time errors for non existing relations.
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
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

        /**
         * Is description set
         *
         * @return <code>true</code>, if description is set
         */
        default boolean isSetDescription() {
            return getDescription() != null && !getDescription().isEmpty();
        }
    }

    interface HasCodespace {
        String CODESPACE = "codespace";

        Codespace getCodespace();

        void setCodespace(Codespace codespace);

        default boolean isSetCodespace() {
            return getCodespace() != null && getCodespace().isSetCodespace();
        }
    }

    interface HasCodespaceName {
        String CODESPACE = "codespace";

        Codespace getCodespaceName();

        void setCodespaceName(Codespace codespaceName);

        default boolean isSetCodespaceName() {
            return getCodespaceName() != null && getCodespaceName().isSetCodespace();
        }
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

        void setPublished(boolean published);

        boolean isPublished();
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

        AbstractFeatureOfInterest getFeatureOfInterest();

        default boolean hasFeatureOfInterest() {
            return getFeatureOfInterest() != null;
        }
    }

    interface HasFeatureOfInterest extends HasFeatureOfInterestGetter {

        void setFeatureOfInterest(AbstractFeatureOfInterest featureOfInterest);

    }

    interface HasReadableObservationContext
            extends HasObservablePropertyGetter,
                    HasProcedureGetter,
                    HasFeatureOfInterestGetter {
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

        default boolean isSetDescriptionXml() {
            return !Strings.isNullOrEmpty(getDescriptionXml());
        }
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
        default boolean isSetGeometry() {
            return getGeom() != null;
        }

        default boolean isSpatial() {
            return isSetGeometry();
        }
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
        default boolean isSetIdentifier() {
            return getIdentifier() != null && !getIdentifier().isEmpty();
        }
    }

    interface HasName {
        String NAME = "name";

        String getName();

        void setName(String name);

        default boolean isSetName() {
            return getName() != null && !getName().isEmpty();
        }

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

    interface HasObservationType {
        String OBSERVATION_TYPE = "observationType";

        ObservationType getObservationType();

        void setObservationType(ObservationType observationType);

        default boolean isSetObservationType() {
            return getObservationType() != null && getObservationType().isSetObservationType();
        }
    }

    interface HasObservationTypes {
        String OBSERVATION_TYPES = "observationTypes";

        Set<ObservationType> getObservationTypes();

        void setObservationTypes(Set<ObservationType> observationTypes);

        default boolean hasObservationTypes() {
            return getObservationTypes() != null && !getObservationTypes().isEmpty();
        }

    }

    interface HasOffering {
        String OFFERING = "offering";

        void setOffering(Offering offering);

        Offering getOffering();

        default boolean isSetOffering() {
            return getOffering() != null;
        }
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
         * @param phenomenonTimeStart Start phenomenon time to set
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
         * @param phenomenonTimeEnd End phenomenon time to set
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

        default boolean hasRelatedFeatures() {
            return getRelatedFeatures() != null && !getRelatedFeatures().isEmpty();
        }

    }

    interface HasResultEncoding {
        String RESULT_ENCODING = "resultEncoding";

        String getResultEncoding();

        void setResultEncoding(String resultEncoding);

        default boolean isSetResultEncoding() {
            return !Strings.isNullOrEmpty(getResultEncoding());
        }
    }

    interface HasResultStructure {
        String RESULT_STRUCTURE = "resultStructure";

        String getResultStructure();

        void setResultStructure(String resultStructure);

        default boolean isSetResultStructure() {
            return !Strings.isNullOrEmpty(getResultStructure());
        }
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
         * @param resultTime Result tiem to set
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
        default boolean isSetUnit() {
            return getUnit() != null && getUnit().isSetUnit();
        }
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
         * @param validTimeStart Start valid time to set
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
         * @param validTimeEnd End valid time to set
         */
        void setValidTimeEnd(Date validTimeEnd);

        default boolean isSetValidTime() {
            return getValidTimeStart() != null && getValidTimeEnd() != null;
        }
    }

    interface HasUrl {
        String URL = "url";

        String getUrl();

        void setUrl(String url);

        default boolean isSetUrl() {
            return !Strings.isNullOrEmpty(getUrl());
        }
    }

    interface GetStringValue {

        boolean isSetValue();

        String getValueAsString();

    }

    interface HasValue<T> extends GetStringValue {
        String VALUE = "value";

        T getValue();

        void setValue(T value);

        @Override
        public default boolean isSetValue() {
            return getValue() != null;
        }

        @Override
        public default String getValueAsString() {
            return isSetValue() ? getValue().toString() : null;
        }
    }

    interface HasUnitValue<T> extends HasUnit, HasValue<T> {
    }

    interface HasOfferings {
        String OFFERINGS = "offerings";

        Set<Offering> getOfferings();

//        Object getOffering();
        void setOfferings(Object offerings);

        default boolean isSetOfferings() {
            return getOfferings() != null && !getOfferings().isEmpty();
        }

    }

    interface HasParameters {
        String PARAMETERS = "parameters";

        Set<Parameter<?>> getParameters();

        void setParameters(Object parameters);

        default boolean hasParameters() {
            return CollectionHelper.isNotEmpty(getParameters());
        }

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

        void setSrid(int srid);

        /**
         * Is srid set
         *
         * @return <code>true</code>, if srid is set
         */
        default boolean isSetSrid() {
            return getSrid() > 0;
        }
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
        default boolean isSetLongLat() {
            return getLongitude() != null && getLatitude() != null;
        }

        Object getAltitude();

        HasCoordinate setAltitude(Object altitude);

        /**
         * Is altitude set
         *
         * @return <code>true</code>, if altitude is set
         */
        default boolean isSetAltitude() {
            return getAltitude() != null;
        }

        default boolean isSpatial() {
            return isSetLongLat() && isSetSrid();
        }
    }

    interface HasValidProcedureTimes {
        String VALID_PROCEDURE_TIMES = "validProcedureTimes";

        Set<ValidProcedureTime> getValidProcedureTimes();

        void setValidProcedureTimes(Set<ValidProcedureTime> validProcedureTimes);
    }

    interface HasParentChilds<T> {
        String PARENTS = "parents";

        String CHILDS = "childs";

        Set<T> getParents();

        void setParents(Set<T> parents);

        void addParent(T parent);

        default boolean hasParents() {
            return getParents() != null && !getParents().isEmpty();
        }

        Set<T> getChilds();

        void setChilds(Set<T> childs);

        void addChild(T child);

        default boolean hasChilds() {
            return getChilds() != null && !getChilds().isEmpty();
        }
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
         * @param observationId Observation id to set
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

        default boolean isSetLocale() {
            return getLocale() != null;
        }
    }

    interface HasSamplingGeometry {

        String SAMPLING_GEOMETRY = "samplingGeometry";

        Geometry getSamplingGeometry();

        void setSamplingGeometry(Geometry samplingGeometry);

        default boolean hasSamplingGeometry() {
            return getSamplingGeometry() != null && !getSamplingGeometry().isEmpty();
        }

    }
}
