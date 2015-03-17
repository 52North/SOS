/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.encode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.n52.sos.exi.EXIObject;
import org.n52.sos.exi.EXISettings;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.utils.EXIUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.exceptions.EXIException;

/**
 * Writer class for {@link EXIObject}
 * 
 * Converts XML documents via EXI encoding using {@link EXISettings}.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class EXIResponseWriter extends AbstractResponseWriter<EXIObject> {
	
	private final static EXIUtils EXI_UTILS = EXIUtils.getInstance(); 

    @Override
    public void write(EXIObject exiObject, OutputStream out, ResponseProxy responseProxy) throws IOException {
        try (InputStream is =
                new ByteArrayInputStream(exiObject.getDoc().xmlText(XmlOptionsHelper.getInstance().getXmlOptions())
                        .getBytes("UTF-8"))) {
        	
        	EXIFactory ef = EXI_UTILS.newEXIFactory();
            
            EXIResult exiResult = new EXIResult(ef);
            exiResult.setOutputStream(out);
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            xmlReader.setContentHandler(exiResult.getHandler());
            xmlReader.parse(new InputSource(is));
        } catch (EXIException | SAXException e) {
            throw new IOException(e);
        }
    }

    @Override
    public MediaType getContentType() {
        return MediaTypes.APPLICATION_EXI;
    }

    @Override
    public void setContentType(MediaType contentType) {

    }

    @Override
    public boolean supportsGZip(EXIObject t) {
        return false;
    }

}
