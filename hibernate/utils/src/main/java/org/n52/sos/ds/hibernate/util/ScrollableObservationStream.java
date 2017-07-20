package org.n52.sos.ds.hibernate.util;

import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import org.n52.janmayen.function.ThrowingFunction;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ScrollableObservationStream
        extends ThrowingScrollableResultsIterator<OmObservation, OwsExceptionReport>
        implements ObservationStream {

    public ScrollableObservationStream(
            ScrollableResults results, Session session,
            ThrowingFunction<ScrollableResults, OmObservation, OwsExceptionReport> extractor) {
        super(results, session, extractor);
    }

}
