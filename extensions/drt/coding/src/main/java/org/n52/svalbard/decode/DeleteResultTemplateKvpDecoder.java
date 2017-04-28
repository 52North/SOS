/*
 * Copyright (C) 2017 52north.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.n52.svalbard.decode;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.n52.shetland.ogc.sos.drt.DeleteResultTemplateConstants;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.decode.OperationDecoderKey;
import org.n52.sos.decode.kvp.AbstractKvpDecoder;
import org.n52.sos.exception.ows.concrete.MissingRequestParameterException;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingVersionParameterException;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.DeleteResultTemplateRequest;
import org.n52.sos.util.KvpHelper;
import org.n52.sos.util.http.MediaTypes;

/**
 *
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 * J&uuml;rrens</a>
 *  @since 4.4.0
 */
public class DeleteResultTemplateKvpDecoder extends AbstractKvpDecoder {
    
    private final static DecoderKey KVP_DECODER_KEY_TYPE =
            new OperationDecoderKey(
                    SosConstants.SOS,
                    Sos2Constants.SERVICEVERSION,
                    DeleteResultTemplateConstants.OPERATION_NAME,
                    MediaTypes.APPLICATION_KVP
    );

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.singleton(KVP_DECODER_KEY_TYPE);
    }

    @Override
    public DeleteResultTemplateRequest decode(Map<String, String> objectToDecode) throws OwsExceptionReport {
        if (objectToDecode == null) {
            throw new UnsupportedDecoderInputException(this, objectToDecode);
        }
        DeleteResultTemplateRequest request = new DeleteResultTemplateRequest();
        CompositeOwsException exceptions = new CompositeOwsException();
        String offering = "", observedProperty = "";
        boolean foundRequest = false,
                foundService = false,
                foundVersion = false;

        for (String parameterName : objectToDecode.keySet()) {
            String parameterValues = objectToDecode.get(parameterName);
            try {
                if (parameterName.equalsIgnoreCase(OWSConstants.RequestParams.service.name())) {
                    request.setService(KvpHelper.checkParameterSingleValue(parameterValues,
                            parameterName));
                    foundService = true;
                } else if (parameterName.equalsIgnoreCase(OWSConstants.RequestParams.version.name())) {
                    request.setVersion(KvpHelper.checkParameterSingleValue(parameterValues,
                            parameterName));
                    foundVersion = true;
                } else if (parameterName.equalsIgnoreCase(OWSConstants.RequestParams.request.name())) {
                    KvpHelper.checkParameterSingleValue(parameterValues, parameterName);
                    foundRequest = true;
                } 
                // offering (optional)
                else if (parameterName.equalsIgnoreCase(DeleteResultTemplateConstants.PARAMETERS.offering.name())) {
                    offering = KvpHelper.checkParameterSingleValue(parameterValues, parameterName);
                    addValue(request, offering, observedProperty);
                }
                // observedProperty (optional)
                else if (parameterName.equalsIgnoreCase(DeleteResultTemplateConstants.PARAMETERS.observableProperty.name())) {
                    observedProperty = KvpHelper.checkParameterSingleValue(parameterValues, parameterName);
                    addValue(request, offering, observedProperty);
                }
                // resultTemplate (optional)
                else if (parameterName.equalsIgnoreCase(
                        DeleteResultTemplateConstants.PARAMETERS.resultTemplate.name())) {
                    for (String resultTemplate : KvpHelper.checkParameterMultipleValues(parameterValues, parameterName)) {
                        request.addResultTemplate(resultTemplate);
                    }
                }
                
            } catch (OwsExceptionReport owse) {
                exceptions.add(owse);
            }
        }

        if (!foundService) {
            exceptions.add(new MissingServiceParameterException());
        }

        if (!foundVersion) {
            exceptions.add(new MissingVersionParameterException());
        }

        if (!foundRequest) {
            exceptions.add(new MissingRequestParameterException());
        }
        
        exceptions.throwIfNotEmpty();

        return request;
    }

    private void addValue(DeleteResultTemplateRequest request, String offering, String observedProperty) {
        if (!offering.isEmpty() && !observedProperty.isEmpty()) {
            request.addObservedPropertyOfferingPair(observedProperty, offering);
        }
    }
}
