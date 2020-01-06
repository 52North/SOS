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
package org.n52.svalbard.encode.inspire.ef;

import java.util.Map;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.svalbard.inspire.ef.AnyDomainLink;
import org.n52.svalbard.inspire.ef.EnvironmentalMonitoringFacility;
import org.n52.svalbard.inspire.ef.NetworkFacility;
import org.n52.svalbard.inspire.ef.OperationalActivityPeriod;

import eu.europa.ec.inspire.schemas.ef.x40.EnvironmentalMonitoringFacilityDocument;
import eu.europa.ec.inspire.schemas.ef.x40.EnvironmentalMonitoringFacilityType;
import eu.europa.ec.inspire.schemas.ef.x40.EnvironmentalMonitoringFacilityType.BelongsTo;
import eu.europa.ec.inspire.schemas.ef.x40.EnvironmentalMonitoringFacilityType.RelatedTo;
import net.opengis.gml.x32.FeaturePropertyType;

public abstract class AbstractEnvironmentalMonitoringFaciltityEncoder extends AbstractMonitoringFeatureEncoder {

    //
    // private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES =
    // Collections.singletonMap(
    // SupportedTypeKey.FeatureType,
    // (Set<String>) Sets.newHashSet(OGCConstants.UNKNOWN,
    // SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT,
    // SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE,
    // SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE));
    //
    // @Override
    // public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
    // return Collections.unmodifiableMap(SUPPORTED_TYPES);
    // }

    @Override
    protected String generateGmlId() {
        return "emf_" + JavaHelper.generateID(Double.toString(System.currentTimeMillis() * Math.random()));
    }
    
    @Override
    protected XmlObject createFeature(FeaturePropertyType featurePropertyType, AbstractFeature abstractFeature,
            Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        if (additionalValues.containsKey(HelperValues.ENCODE)
                && additionalValues.get(HelperValues.ENCODE).equals("false")) {
            featurePropertyType.setHref(abstractFeature.getIdentifierCodeWithAuthority().getValue());
            if (abstractFeature.isSetName()) {
                featurePropertyType.setTitle(abstractFeature.getFirstName().getValue());
            }
            return featurePropertyType;
        }
        EnvironmentalMonitoringFacilityType emft =
                createEnvironmentalMonitoringFaciltityType((EnvironmentalMonitoringFacility) abstractFeature);
        EnvironmentalMonitoringFacilityDocument emfd = EnvironmentalMonitoringFacilityDocument.Factory
                .newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        emfd.setEnvironmentalMonitoringFacility(emft);
        return emfd;
    }

    protected EnvironmentalMonitoringFacilityType createEnvironmentalMonitoringFaciltityType(
            EnvironmentalMonitoringFacility environmentalMonitoringFacility) throws OwsExceptionReport {
        EnvironmentalMonitoringFacilityType emft = EnvironmentalMonitoringFacilityType.Factory.newInstance();
        return encodeEnvironmentalMonitoringFaciltityType(emft, environmentalMonitoringFacility);
    }

    protected EnvironmentalMonitoringFacilityType encodeEnvironmentalMonitoringFaciltityType(
            EnvironmentalMonitoringFacilityType emft, EnvironmentalMonitoringFacility environmentalMonitoringFacility)
                    throws OwsExceptionReport {
        encodeAbstractMonitoringFeature(emft, environmentalMonitoringFacility);
        setRepresentativePoint(emft, environmentalMonitoringFacility);
        setMeasurementRegime(emft, environmentalMonitoringFacility);
        setMobile(emft, environmentalMonitoringFacility);
        setResultAcquisitionSource(emft, environmentalMonitoringFacility);
        setSpecialisedEMFType(emft, environmentalMonitoringFacility);
        setOperationalActivityPeriod(emft, environmentalMonitoringFacility);
        setRelatedTo(emft, environmentalMonitoringFacility);
        setBelongsTo(emft, environmentalMonitoringFacility);
        return emft;
    }

    private void setRepresentativePoint(EnvironmentalMonitoringFacilityType emft,
            EnvironmentalMonitoringFacility environmentalMonitoringFacility) throws OwsExceptionReport {
        if (environmentalMonitoringFacility.isSetRepresentativePoint()) {
            emft.addNewRepresentativePoint().addNewPoint()
                    .set(encodeGML32(environmentalMonitoringFacility.getRepresentativePoint()));
        }
    }

    private void setMeasurementRegime(EnvironmentalMonitoringFacilityType emft,
            EnvironmentalMonitoringFacility environmentalMonitoringFacility) throws OwsExceptionReport {
        if (environmentalMonitoringFacility.isSetMeasurementRegime()) {
            emft.addNewMeasurementRegime().set(encodeGML32(environmentalMonitoringFacility.getMeasurementRegime()));
        } else {
            emft.addNewMeasurementRegime().setNil();
        }
    }

    private void setMobile(EnvironmentalMonitoringFacilityType emft,
            EnvironmentalMonitoringFacility environmentalMonitoringFacility) {
        if (environmentalMonitoringFacility.isSetMobile()) {
            emft.addNewMobile().setBooleanValue(environmentalMonitoringFacility.isMobile());
        } else {
            emft.addNewMobile().setNil();
        }
    }

    private void setResultAcquisitionSource(EnvironmentalMonitoringFacilityType emft,
            EnvironmentalMonitoringFacility environmentalMonitoringFacility) throws OwsExceptionReport {
        if (environmentalMonitoringFacility.isSetResultAcquisitionSource()) {
            for (ReferenceType resultAcquisitionSource : environmentalMonitoringFacility
                    .getResultAcquisitionSource()) {
                emft.addNewResultAcquisitionSource().set(encodeGML32(resultAcquisitionSource));
            }
        }
    }

    private void setSpecialisedEMFType(EnvironmentalMonitoringFacilityType emft,
            EnvironmentalMonitoringFacility environmentalMonitoringFacility) throws OwsExceptionReport {
        if (environmentalMonitoringFacility.isSetSpecialisedEMFType()) {
            emft.addNewSpecialisedEMFType().set(encodeGML32(environmentalMonitoringFacility.getSpecialisedEMFType()));
        }
    }

    private void setOperationalActivityPeriod(EnvironmentalMonitoringFacilityType emft,
            EnvironmentalMonitoringFacility environmentalMonitoringFacility) throws OwsExceptionReport {
        if (environmentalMonitoringFacility.isSetOperationalActivityPeriod()) {
            for (OperationalActivityPeriod operationalActivityPeriod : environmentalMonitoringFacility
                    .getOperationalActivityPeriod()) {
                if (operationalActivityPeriod.isSetSimpleAttrs()) {
                    eu.europa.ec.inspire.schemas.ef.x40.EnvironmentalMonitoringFacilityType.OperationalActivityPeriod oap =
                            emft.addNewOperationalActivityPeriod();
                    oap.setHref(operationalActivityPeriod.getSimpleAttrs().getHref());
                    if (operationalActivityPeriod.getSimpleAttrs().isSetTitle()) {
                        oap.setTitle(operationalActivityPeriod.getSimpleAttrs().getTitle());
                    }
                } else {
                    emft.addNewOperationalActivityPeriod().set(encodeEF(operationalActivityPeriod));
                }
            }
        } else {
            emft.addNewOperationalActivityPeriod().setNil();
        }
    }

    private void setRelatedTo(EnvironmentalMonitoringFacilityType emft,
            EnvironmentalMonitoringFacility environmentalMonitoringFacility) throws OwsExceptionReport {
        if (environmentalMonitoringFacility.isSetRelatedTo()) {
            for (AnyDomainLink relatedTo : environmentalMonitoringFacility.getRelatedTo()) {
                if (relatedTo.isSetSimpleAttrs()) {
                    RelatedTo rt = emft.addNewRelatedTo();
                    rt.setHref(relatedTo.getSimpleAttrs().getHref());
                    if (relatedTo.getSimpleAttrs().isSetTitle()) {
                        rt.setTitle(relatedTo.getSimpleAttrs().getTitle());
                    }
                } else {
                    emft.addNewRelatedTo().addNewAnyDomainLink().set(encodeEF(relatedTo));
                }
            }
        }
    }

    private void setBelongsTo(EnvironmentalMonitoringFacilityType emft,
            EnvironmentalMonitoringFacility environmentalMonitoringFacility) throws OwsExceptionReport {
        if (environmentalMonitoringFacility.isSetBelongsTo()) {
            for (NetworkFacility belongsTo : environmentalMonitoringFacility.getBelongsTo()) {
                if (belongsTo.isSetSimpleAttrs()) {
                    BelongsTo bt = emft.addNewBelongsTo();
                    bt.setHref(belongsTo.getSimpleAttrs().getHref());
                    if (belongsTo.getSimpleAttrs().isSetTitle()) {
                        bt.setTitle(belongsTo.getSimpleAttrs().getTitle());
                    }
                } else {
                    emft.addNewBelongsTo().addNewNetworkFacility().set(encodeEF(belongsTo));
                }
            }
        }
    }

}
