/**
 * ﻿Copyright (C) 2017
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.sos.ds.hibernate.util.observation;

import java.util.Collections;
import java.util.Set;

import org.hibernate.Session;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesDAO;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.AbstractEReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.EReportingSeries;
import org.n52.sos.ds.hibernate.entities.observation.series.AbstractSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.exception.CodedException;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.series.wml.WaterMLConstants;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class WaterMLObservationCreator extends AbstractAdditionalObservationCreator<Series> {

    private static final Set<AdditionalObservationCreatorKey> KEYS =
            AdditionalObservationCreatorRepository.encoderKeysForElements(WaterMLConstants.NS_WML_20,
                    AbstractSeriesObservation.class,
                    AbstractEReportingObservation.class,
                    Series.class,
                    EReportingSeries.class);

    @Override
    public Set<AdditionalObservationCreatorKey> getKeys() {
        return Collections.unmodifiableSet(KEYS);
    }

    @Override
    public OmObservation create(OmObservation omObservation, Observation<?> observation, Session session)
            throws CodedException {
        create(omObservation, observation);
        if (observation instanceof AbstractSeriesObservation) {
            return addWaterMLMetadata(omObservation, ((AbstractSeriesObservation<?>)observation).getSeries(), session);
        }
        return omObservation;
    }

    @Override
    public OmObservation create(OmObservation omObservation, Series series, Session session) throws CodedException {
        create(omObservation, series);
        return addWaterMLMetadata(omObservation, series, session);
    }

    @Override
    public OmObservation add(OmObservation omObservation, Observation<?> observation, Session session)
            throws CodedException {
        add(omObservation, observation);
        if (observation instanceof AbstractSeriesObservation) {
            return addWaterMLMetadata(omObservation, ((AbstractSeriesObservation<?>)observation).getSeries(), session);
        }
        return omObservation;
    }

    private OmObservation addWaterMLMetadata(OmObservation omObservation, Series series, Session session)
            throws CodedException {
        return new WaterMLMetadataAdder(omObservation, series, session).add().result();
    }

}
