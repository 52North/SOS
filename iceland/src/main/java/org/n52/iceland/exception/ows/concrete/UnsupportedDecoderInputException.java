/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.exception.ows.concrete;

import static org.n52.iceland.util.http.HTTPStatus.INTERNAL_SERVER_ERROR;

import org.apache.xmlbeans.XmlObject;
import org.n52.iceland.decode.Decoder;
import org.n52.iceland.exception.ows.NoApplicableCodeException;
import org.w3c.dom.Node;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class UnsupportedDecoderInputException extends NoApplicableCodeException {
    private static final long serialVersionUID = 5561451567407304739L;

    public UnsupportedDecoderInputException(final Decoder<?, ?> decoder, final Object o) {
        if (o == null) {
            withMessage("Decoder %s can not decode 'null'", decoder.getClass().getSimpleName());
        } else {
            String name;
            if (o instanceof XmlObject) {
                final Node n = ((XmlObject) o).getDomNode();
                name = n.getPrefix() != null ? n.getPrefix() + ":" + n.getLocalName() : n.getLocalName();
            } else {
                name = o.getClass().getName();
            }
            withMessage("%s can not be decoded by %s because it is not yet implemented!", name, decoder.getClass()
                    .getName());
        }
        setStatus(INTERNAL_SERVER_ERROR);
    }
}
