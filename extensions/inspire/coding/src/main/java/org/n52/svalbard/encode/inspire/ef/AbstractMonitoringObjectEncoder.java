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

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.w3c.xlink.SimpleAttrs;
import org.n52.svalbard.inspire.base2.LegislationCitation;
import org.n52.svalbard.inspire.ef.AbstractMonitoringObject;
import org.n52.svalbard.inspire.ef.Hierarchy;
import org.n52.svalbard.inspire.ef.ObservingCapability;

import com.google.common.collect.Maps;

import eu.europa.ec.inspire.schemas.ef.x40.AbstractMonitoringObjectPropertyType;
import eu.europa.ec.inspire.schemas.ef.x40.AbstractMonitoringObjectType;
import eu.europa.ec.inspire.schemas.ef.x40.AbstractMonitoringObjectType.Broader;
import eu.europa.ec.inspire.schemas.ef.x40.AbstractMonitoringObjectType.LegalBackground;
import eu.europa.ec.inspire.schemas.ef.x40.AbstractMonitoringObjectType.Narrower;
import eu.europa.ec.inspire.schemas.ef.x40.AbstractMonitoringObjectType.Supersedes;

public abstract class AbstractMonitoringObjectEncoder extends AbstractEnvironmentalFaciltityEncoder<AbstractFeature> {
    
    protected abstract String generateGmlId();

    protected void encodeAbstractMonitoringObject(AbstractMonitoringObjectType amot,
            AbstractMonitoringObject abstractMonitoringObject) throws OwsExceptionReport {
        setGmlId(amot, abstractMonitoringObject);
        setInspireId(amot, abstractMonitoringObject);
        setName(amot, abstractMonitoringObject);
        setAdditionalDescription(amot, abstractMonitoringObject);
        setMediaMonitored(amot, abstractMonitoringObject);
        setLegalBackground(amot, abstractMonitoringObject);
        setResponsibleParty(amot, abstractMonitoringObject);
        setGeometry(amot, abstractMonitoringObject);
        setOnlineResource(amot, abstractMonitoringObject);
        setPurpose(amot, abstractMonitoringObject);
        setObservingCapability(amot, abstractMonitoringObject);
        setBroader(amot, abstractMonitoringObject);
        setNarrower(amot, abstractMonitoringObject);
        setSupersedes(amot, abstractMonitoringObject);
        setSupersededBy(amot, abstractMonitoringObject);
    }

    private void setGmlId(AbstractMonitoringObjectType amot, AbstractMonitoringObject abstractMonitoringObject) {
        if (!abstractMonitoringObject.isSetGmlID()) {
            if (!abstractMonitoringObject.isSetGmlID()) {
                abstractMonitoringObject.setGmlId(generateGmlId());
            }
        }
        amot.setId(abstractMonitoringObject.getGmlId());
    }

    private void setInspireId(AbstractMonitoringObjectType amot, AbstractMonitoringObject abstractMonitoringObject) throws OwsExceptionReport {
        amot.addNewInspireId().set(encodeBASEPropertyType(abstractMonitoringObject.getInspireId()));
    }

    private void setName(AbstractMonitoringObjectType amot, AbstractMonitoringObject abstractMonitoringObject) {
        if (abstractMonitoringObject.isSetName()) {
            for (CodeType name : abstractMonitoringObject.getName()) {
                if (name.isSetValue()) {
                    amot.addNewName2().setStringValue(name.getValue());
                }
            }
        }
    }

    private void setAdditionalDescription(AbstractMonitoringObjectType amot,
            AbstractMonitoringObject abstractMonitoringObject) {
        if (abstractMonitoringObject.isSetAdditionalDescription()) {
            amot.addNewAdditionalDescription().setStringValue(abstractMonitoringObject.getAdditionalDescription());
        }
    }

    private void setMediaMonitored(AbstractMonitoringObjectType amot,
            AbstractMonitoringObject abstractMonitoringObject) throws OwsExceptionReport {
        for (ReferenceType mediaMonitored : abstractMonitoringObject.getMediaMonitored()) {
            amot.addNewMediaMonitored().set(encodeGML32(mediaMonitored));
        }
    }

    private void setLegalBackground(AbstractMonitoringObjectType amot,
            AbstractMonitoringObject abstractMonitoringObject) throws OwsExceptionReport {
        if (abstractMonitoringObject.isSetLegalBackground()) {
            for (LegislationCitation legislationCitation : abstractMonitoringObject.getLegalBackground()) {
                if (legislationCitation.isSetSimpleAttrs()) {
                    SimpleAttrs simpleAttrs = legislationCitation.getSimpleAttrs();
                    if (simpleAttrs.isSetHref()) {
                        LegalBackground lb = amot.addNewLegalBackground();
                        lb.setHref(simpleAttrs.getHref());
                        if (simpleAttrs.isSetTitle()) {
                            lb.setTitle(simpleAttrs.getTitle());
                        }
                    }
                } else {
                    amot.addNewLegalBackground().addNewLegislationCitation().set(encodeEF(legislationCitation));
                }
            }
        }
        
    }

    private void setResponsibleParty(AbstractMonitoringObjectType amot,
            AbstractMonitoringObject abstractMonitoringObject) throws OwsExceptionReport {
        if (abstractMonitoringObject.isSetResponsibleParty()) {
            amot.addNewResponsibleParty().addNewRelatedParty().set(encodeBASE2(abstractMonitoringObject.getResponsibleParty()));
        }
    }

    private void setGeometry(AbstractMonitoringObjectType amot, AbstractMonitoringObject abstractMonitoringObject) throws OwsExceptionReport {
        if (abstractMonitoringObject.isSetGeometry()) {
            if (abstractMonitoringObject.isSetGmlID()) {
                Map<HelperValues, String> additionalValues = Maps.newHashMap();
                additionalValues.put(HelperValues.GMLID, abstractMonitoringObject.getGmlId());
                amot.addNewGeometry().set(encodeGML32(abstractMonitoringObject.getGeometry(), additionalValues));
            } else {
                amot.addNewGeometry().set(encodeGML32(abstractMonitoringObject.getGeometry()));
            }
        }
    }

    private void setOnlineResource(AbstractMonitoringObjectType amot,
            AbstractMonitoringObject abstractMonitoringObject) {
        if (abstractMonitoringObject.isSetOnlineResources()) {
            for (String onlineResource : abstractMonitoringObject.getOnlineResource()) {
                amot.addNewOnlineResource().setStringValue(onlineResource);
            }
        }
    }

    private void setPurpose(AbstractMonitoringObjectType amot, AbstractMonitoringObject abstractMonitoringObject) throws OwsExceptionReport {
        if (abstractMonitoringObject.isSetPurpose()) {
            for (ReferenceType purpose : abstractMonitoringObject.getPurpose()) {
                amot.addNewPurpose().set(encodeGML32(purpose));
            }
        }
    }

    private void setObservingCapability(AbstractMonitoringObjectType amot,
            AbstractMonitoringObject abstractMonitoringObject) throws OwsExceptionReport {
        if (abstractMonitoringObject.isSetObservingCapability()) {
            for (ObservingCapability observingCapability : abstractMonitoringObject.getObservingCapability()) {
                if (observingCapability.isSetHref()) {
                    eu.europa.ec.inspire.schemas.ef.x40.AbstractMonitoringObjectType.ObservingCapability oc = amot.addNewObservingCapability();
                    oc.setHref(observingCapability.getHref());
                    if (observingCapability.isSetTitle()) {
                        oc.setTitle(observingCapability.getTitle());
                    }
                } else {
                    amot.addNewObservingCapability().addNewObservingCapability().set(encodeEF(observingCapability));
                }
            }
        }
    }

    private void setBroader(AbstractMonitoringObjectType amot, AbstractMonitoringObject abstractMonitoringObject) throws OwsExceptionReport {
        if (abstractMonitoringObject.isSetBroader()) {
            Hierarchy broader = abstractMonitoringObject.getBroader();
            if (broader.isSetSimpleAttrs()) {
                Broader b = amot.addNewBroader();
                b.setHref(broader.getSimpleAttrs().getHref());
                if (broader.getSimpleAttrs().isSetTitle()) {
                    b.setTitle(broader.getSimpleAttrs().getTitle());
                }
            } else {
                amot.addNewBroader().addNewHierarchy().set(encodeEF(broader));
            }
        }
    }

    private void setNarrower(AbstractMonitoringObjectType amot, AbstractMonitoringObject abstractMonitoringObject) throws OwsExceptionReport {
        if (abstractMonitoringObject.isSetNarrower()) {
            for (Hierarchy narrower : abstractMonitoringObject.getNarrower()) {
                if (narrower.isSetSimpleAttrs()) {
                    Narrower n = amot.addNewNarrower();
                    n.setHref(narrower.getSimpleAttrs().getHref());
                    if (narrower.getSimpleAttrs().isSetTitle()) {
                        n.setTitle(narrower.getSimpleAttrs().getTitle());
                    }
                } else {
                    amot.addNewNarrower().addNewHierarchy().set(encodeEF(narrower));
                }
            }
        }
    }

    private void setSupersedes(AbstractMonitoringObjectType amot, AbstractMonitoringObject abstractMonitoringObject) throws OwsExceptionReport {
        if (abstractMonitoringObject.isSetSupersedes()) {
            for (AbstractMonitoringObject supersedes : abstractMonitoringObject.getSupersedes()) {
                if (supersedes.isSetSimpleAttrs()) {
                    Supersedes s = amot.addNewSupersedes();
                    s.setHref(supersedes.getSimpleAttrs().getHref());
                    if (supersedes.getSimpleAttrs().isSetTitle()) {
                        s.setTitle(supersedes.getSimpleAttrs().getTitle());
                    }
                } else {
                    amot.addNewSupersedes().addNewAbstractMonitoringObject().set(encodeEF(supersedes));
                }
            }
        }
    }

    private void setSupersededBy(AbstractMonitoringObjectType amot,
            AbstractMonitoringObject abstractMonitoringObject) throws OwsExceptionReport {
        if (abstractMonitoringObject.isSetSupersededBy()) {
            for (AbstractMonitoringObject supersededBy : abstractMonitoringObject.getSupersededBy()) {
                if (supersededBy.isSetSimpleAttrs()) {
                    AbstractMonitoringObjectPropertyType sb = amot.addNewSupersededBy();
                    sb.setHref(supersededBy.getSimpleAttrs().getHref());
                    if (supersededBy.getSimpleAttrs().isSetTitle()) {
                        sb.setTitle(supersededBy.getSimpleAttrs().getTitle());
                    }
                } else {
                    amot.addNewSupersededBy().addNewAbstractMonitoringObject().set(encodeEF(supersededBy));
                }
            }
        }
    }
}
