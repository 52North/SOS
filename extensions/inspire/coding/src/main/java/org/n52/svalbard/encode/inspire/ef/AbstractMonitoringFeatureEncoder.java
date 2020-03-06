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

import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.svalbard.inspire.ef.AbstractMonitoringFeature;
import org.n52.svalbard.inspire.ef.EnvironmentalMonitoringActivity;
import org.n52.svalbard.inspire.ef.ReportToLegalAct;

import eu.europa.ec.inspire.schemas.ef.x40.AbstractMonitoringFeatureType;
import eu.europa.ec.inspire.schemas.ef.x40.AbstractMonitoringFeatureType.InvolvedIn;

public abstract class AbstractMonitoringFeatureEncoder extends AbstractMonitoringObjectEncoder {

    protected void encodeAbstractMonitoringFeature(AbstractMonitoringFeatureType amft,
            AbstractMonitoringFeature abstractMonitoringFeature) throws OwsExceptionReport {
        encodeAbstractMonitoringObject(amft, abstractMonitoringFeature);
        setReportedTo(amft, abstractMonitoringFeature);
        setHasObservation(amft, abstractMonitoringFeature);
        setInvolvedIn(amft, abstractMonitoringFeature);
    }

    private void setReportedTo(AbstractMonitoringFeatureType amft,
            AbstractMonitoringFeature abstractMonitoringFeature) throws OwsExceptionReport {
        if (abstractMonitoringFeature.isSetReportedTo()) {
            for (ReportToLegalAct reportToLegalAct : abstractMonitoringFeature.getReportedTo()) {
                amft.addNewReportedTo().addNewReportToLegalAct().set(encodeEF(reportToLegalAct));
            }
        }
    }

    private void setHasObservation(AbstractMonitoringFeatureType amft,
            AbstractMonitoringFeature abstractMonitoringFeature) {
        if (abstractMonitoringFeature.isSetHasObservation()) {
            for (OmObservation omObservation : abstractMonitoringFeature.getHasObservation()) {
                if (omObservation.isSetSimpleAttrs()) {
                    amft.addNewHasObservation().setHref(omObservation.getSimpleAttrs().getHref());
                } else {
                    // TODO encode Observation or GET-Request via xlink:href or full observation
                }
            }
        }
    }

    private void setInvolvedIn(AbstractMonitoringFeatureType amft,
            AbstractMonitoringFeature abstractMonitoringFeature) throws OwsExceptionReport {
        if (abstractMonitoringFeature.isSetInvolvedIn()) {
            for (EnvironmentalMonitoringActivity environmentalMonitoringActivity : abstractMonitoringFeature
                    .getInvolvedIn()) {
                if (environmentalMonitoringActivity.isSetSimpleAttrs()) {
                    InvolvedIn ii =  amft.addNewInvolvedIn();
                    ii.setHref(environmentalMonitoringActivity.getSimpleAttrs().getHref());
                    if (environmentalMonitoringActivity.getSimpleAttrs().isSetTitle()) {
                        ii.setTitle(environmentalMonitoringActivity.getSimpleAttrs().getTitle());
                    }
                } else {
                    amft.addNewInvolvedIn().addNewEnvironmentalMonitoringActivity().set(encodeEF(environmentalMonitoringActivity));
                }
            }
        }
    }

}
