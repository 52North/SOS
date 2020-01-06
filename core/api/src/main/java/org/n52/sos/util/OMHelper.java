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
package org.n52.sos.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.gwml.GWMLConstants;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.ComplexValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.CvDiscretePointCoverage;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.HrefAttributeValue;
import org.n52.sos.ogc.om.values.MultiPointCoverage;
import org.n52.sos.ogc.om.values.NilTemplateValue;
import org.n52.sos.ogc.om.values.ProfileValue;
import org.n52.sos.ogc.om.values.QuantityRangeValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.RectifiedGridCoverage;
import org.n52.sos.ogc.om.values.ReferenceValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TLVTValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.UnknownValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.om.values.XmlValue;
import org.n52.sos.ogc.om.values.visitor.ValueVisitor;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.util.http.HTTPStatus;

/**
 * Utility class for Observation and Measurement
 *
 * @since 4.0.0
 *
 */
public final class OMHelper {
    private static final ValueVisitor<String> OBSERVATION_TYPE_VISITOR = new ObservationTypeVisitor();
    private OMHelper() {
    }

    public static String getNamespaceForFeatureType(final String featureType) {
        if (SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT.equals(featureType)
                || SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE.equals(featureType)
                || SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE.equals(featureType)) {
            return SfConstants.NS_SAMS;
        } else if (SfConstants.FT_SAMPLINGPOINT.equals(featureType)
                || SfConstants.FT_SAMPLINGCURVE.equals(featureType)
                || SfConstants.FT_SAMPLINGSURFACE.equals(featureType)) {
            return SfConstants.NS_SA;
        } else if (SfConstants.SAMPLING_FEAT_TYPE_SF_SPECIMEN.equals(featureType)) {
            return SfConstants.NS_SPEC;
        }
        return SfConstants.NS_SAMS;
    }

    public static String getObservationTypeFrom(final SweAbstractDataComponent component) throws OwsExceptionReport {
        if (component instanceof SweBoolean) {
            return OmConstants.OBS_TYPE_TRUTH_OBSERVATION;
        } else if (component instanceof SweQuantity) {
            return OmConstants.OBS_TYPE_MEASUREMENT;
        } else if (component instanceof SweText) {
            return OmConstants.OBS_TYPE_TEXT_OBSERVATION;
        } else if (component instanceof SweCount) {
            return OmConstants.OBS_TYPE_COUNT_OBSERVATION;
        } else if (component instanceof SweCategory) {
            return OmConstants.OBS_TYPE_CATEGORY_OBSERVATION;
        } else if (component instanceof SweDataRecord) {
            return OmConstants.OBS_TYPE_COMPLEX_OBSERVATION;
        } else if (component instanceof SweDataArray) {
            return OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION;
        }
        // TODO Check for missing types
        throw new NoApplicableCodeException().withMessage(
                "Not able to derive observation type from swe:AbstractDataComponent element '{}'.", component)
                .setStatus(HTTPStatus.BAD_REQUEST);
    }

    public static String getObservationTypeFor(Value<?> value)
            throws OwsExceptionReport {
        return value.accept(OBSERVATION_TYPE_VISITOR);
    }

    public static String getObservationTypeFor(final QName resultModel) {
        if (OmConstants.RESULT_MODEL_MEASUREMENT.equals(resultModel)) {
            return OmConstants.OBS_TYPE_MEASUREMENT;
        } else if (OmConstants.RESULT_MODEL_CATEGORY_OBSERVATION.equals(resultModel)) {
            return OmConstants.OBS_TYPE_CATEGORY_OBSERVATION;
        } else if (OmConstants.RESULT_MODEL_GEOMETRY_OBSERVATION.equals(resultModel)) {
            return OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION;
        } else if (OmConstants.RESULT_MODEL_COUNT_OBSERVATION.equals(resultModel)) {
            return OmConstants.OBS_TYPE_COUNT_OBSERVATION;
        } else if (OmConstants.RESULT_MODEL_TRUTH_OBSERVATION.equals(resultModel)) {
            return OmConstants.OBS_TYPE_TRUTH_OBSERVATION;
        } else if (OmConstants.RESULT_MODEL_TEXT_OBSERVATION.equals(resultModel)) {
            return OmConstants.OBS_TYPE_TEXT_OBSERVATION;
        } else if (OmConstants.RESULT_MODEL_TEXT_OBSERVATION.equals(resultModel)) {
            return OmConstants.OBS_TYPE_REFERENCE_OBSERVATION;
        } else if (OmConstants.RESULT_MODEL_COMPLEX_OBSERVATION.equals(resultModel)) {
            return OmConstants.OBS_TYPE_COMPLEX_OBSERVATION;
        }
        return OmConstants.OBS_TYPE_OBSERVATION;
    }

    /**
     * Get the QName for resultModels from observationType constant
     *
     * @param resultModels4Offering
     *            Observation types
     * @return QNames for resultModel parameter
     */
    public static Collection<QName> getQNamesForResultModel(final Collection<String> resultModels4Offering) {
        final List<QName> resultModels = new ArrayList<>(resultModels4Offering.size());
        for (final String string : resultModels4Offering) {
            resultModels.add(getQNameFor(string));
        }
        return resultModels;
    }

    public static QName getQNameFor(final String observationType) {
        if (null != observationType) {
            switch (observationType) {
                case OmConstants.OBS_TYPE_MEASUREMENT:
                    return OmConstants.RESULT_MODEL_MEASUREMENT;
                case OmConstants.OBS_TYPE_CATEGORY_OBSERVATION:
                    return OmConstants.RESULT_MODEL_CATEGORY_OBSERVATION;
                case OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION:
                    return OmConstants.RESULT_MODEL_GEOMETRY_OBSERVATION;
                case OmConstants.OBS_TYPE_COUNT_OBSERVATION:
                    return OmConstants.RESULT_MODEL_COUNT_OBSERVATION;
                case OmConstants.OBS_TYPE_TRUTH_OBSERVATION:
                    return OmConstants.RESULT_MODEL_TRUTH_OBSERVATION;
                case OmConstants.OBS_TYPE_TEXT_OBSERVATION:
                    return OmConstants.RESULT_MODEL_TEXT_OBSERVATION;
                case OmConstants.OBS_TYPE_REFERENCE_OBSERVATION:
                    return OmConstants.RESULT_MODEL_REFERENCE_OBSERVATION;
                case OmConstants.OBS_TYPE_COMPLEX_OBSERVATION:
                    return OmConstants.RESULT_MODEL_COMPLEX_OBSERVATION;
            }
        }
        return OmConstants.RESULT_MODEL_OBSERVATION;
    }

    public static Object getEncodedResultModelFor(final String resultModel) {
        final QName qNameFor = getQNameFor(resultModel);
        final StringBuilder builder = new StringBuilder();
        builder.append(qNameFor.getPrefix());
        builder.append(":");
        builder.append(qNameFor.getLocalPart());
        return builder.toString();
    }

    private static class ObservationTypeVisitor implements ValueVisitor<String> {
        @Override
        public String visit(BooleanValue value) {
            return OmConstants.OBS_TYPE_TRUTH_OBSERVATION;
        }

        @Override
        public String visit(CategoryValue value) {
            return OmConstants.OBS_TYPE_CATEGORY_OBSERVATION;
        }

        @Override
        public String visit(ComplexValue value) {
            return OmConstants.OBS_TYPE_COMPLEX_OBSERVATION;
        }

        @Override
        public String visit(CountValue value) {
            return OmConstants.OBS_TYPE_COUNT_OBSERVATION;
        }

        @Override
        public String visit(GeometryValue value) {
            return OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION;
        }

        @Override
        public String visit(HrefAttributeValue value) {
            return defaultValue();
        }

        @Override
        public String visit(NilTemplateValue value) {
            return defaultValue();
        }

        @Override
        public String visit(QuantityValue value) {
            return OmConstants.OBS_TYPE_MEASUREMENT;
        }

        @Override
        public String visit(ReferenceValue value) {
            return OmConstants.OBS_TYPE_REFERENCE_OBSERVATION;
        }

        @Override
        public String visit(SweDataArrayValue value) {
            return OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION;
        }

        @Override
        public String visit(TVPValue value) {
            return defaultValue();
        }
        
        @Override
        public String visit(TLVTValue value) {
            return defaultValue();
        }

        @Override
        public String visit(TextValue value) {
            return OmConstants.OBS_TYPE_TEXT_OBSERVATION;
        }

        @Override
        public String visit(UnknownValue value) {
            return defaultValue();
        }

        @Override
        public String visit(XmlValue value)
                throws OwsExceptionReport {
            return defaultValue();
        }

        private static String defaultValue() {
            return OmConstants.OBS_TYPE_OBSERVATION;
        }

        @Override
        public String visit(CvDiscretePointCoverage value) throws OwsExceptionReport {
            return defaultValue();
        }

        @Override
        public String visit(MultiPointCoverage value) throws OwsExceptionReport {
            return defaultValue();
        }

        @Override
        public String visit(RectifiedGridCoverage value) throws OwsExceptionReport {
            return defaultValue();
        }

        @Override
        public String visit(ProfileValue value) throws OwsExceptionReport {
            return GWMLConstants.OBS_TYPE_GEOLOGY_LOG;
        }

        @Override
        public String visit(QuantityRangeValue value) throws OwsExceptionReport {
            return defaultValue();
        }
    }
}
