package org.n52.sos.ds.hibernate.util.observation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.hibernate.Session;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSeries;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.quality.SosQuality;
import org.n52.sos.ogc.om.values.NilTemplateValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosProcedureDescription;

import com.google.common.collect.Lists;

public class EReportingSeriesOmObservationCreator extends SeriesOmObservationCreator {
    
    
    public EReportingSeriesOmObservationCreator(EReportingSeries series, String version, Session session) {
        super(series, version, session);
    }

    public EReportingSeriesOmObservationCreator(EReportingSeries series, String version, Locale language, Session session) {
        super(series, version, language, session);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<OmObservation> create() throws OwsExceptionReport, ConverterException {
        final List<OmObservation> observations = Lists.newLinkedList();
        if (series != null) {
            SosProcedureDescription procedure = getProcedure();
            OmObservableProperty obsProp = getObservableProperty();
            AbstractFeature feature = getFeatureOfInterest();

            final OmObservationConstellation obsConst = getObservationConstellation(procedure, obsProp, feature);

            final OmObservation sosObservation = new OmObservation();
            sosObservation.setNoDataValue(getNoDataValue());
            sosObservation.setTokenSeparator(getTokenSeparator());
            sosObservation.setTupleSeparator(getTupleSeparator());
            sosObservation.setObservationConstellation(obsConst);
            // set or add???
            sosObservation.setParameter(new EReportingObservationHelper().createSamplingPointParameter((EReportingSeries)series));
            final NilTemplateValue value = new NilTemplateValue();
            value.setUnit(obsProp.getUnit());
            sosObservation
                    .setValue(new SingleObservationValue(new TimeInstant(), value, new ArrayList<SosQuality>(0)));
            observations.add(sosObservation);
        }
        return observations;
    }
}
