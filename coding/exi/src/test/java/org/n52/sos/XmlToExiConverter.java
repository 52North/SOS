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
package org.n52.sos;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.api.sax.EXISource;

public class XmlToExiConverter {
    
    private static final String EXI_EXTENSION = ".exi";

    private static final String XML_EXTENSION = ".xml";
    
    private static final String XML_EXTENSION_2 = ".xml_";

    protected void encode(String fileName) {
        try (InputStream exiIS = FileUtils.openInputStream(getFile(fileName, XML_EXTENSION));
                OutputStream exiOS = FileUtils.openOutputStream(getFile(fileName, EXI_EXTENSION))) {
            EXIResult exiResult = new EXIResult();
            exiResult.setOutputStream(exiOS);
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            xmlReader.setContentHandler(exiResult.getHandler());
            xmlReader.parse(new InputSource(exiIS));
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    protected void decode(String fileName) {
        try (InputStream exiIS = FileUtils.openInputStream(getFile(fileName, EXI_EXTENSION));
                OutputStream os = FileUtils.openOutputStream(getFile(fileName, XML_EXTENSION_2))) {
            
            Reader reader = new InputStreamReader(exiIS,"ISO-8859-1");
            InputSource is = new InputSource(reader);
            is.setEncoding("ISO-8859-1");
            
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();

            EXISource exiSource = new EXISource();
            XMLReader exiReader = exiSource.getXMLReader();
            SAXSource saxSource = new SAXSource(is);
//            SAXSource saxSource = new SAXSource(new InputSource(exiIS));
            exiSource.setXMLReader(exiReader);
            transformer.transform(saxSource, new StreamResult(os));
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    protected File getFile(String fileName, String extension) {
        String folder = "xml";
        if (EXI_EXTENSION.equals(extension)) {
            folder = "exi";
        }
        return FileUtils.getFile("src", "test", "resources", folder, fileName + extension);
    }

    public static void main(String[] args) {
        XmlToExiConverter c = new XmlToExiConverter();
        System.out.println("Encode");
        c.encode("notebook");
//        c.encode("GetCapabilities");
//        c.encode("DescribeSensor");
//        c.encode("GetObservation");
//        c.encode("GetFeatureOfInterest");
//        c.encode("GetDataAvailability");
        System.out.println("Decode");
        c.decode("notebook");
//        c.decode("GetCapabilities");
//        c.decode("DescribeSensor");
//        c.decode("GetObservation");
//        c.decode("GetFeatureOfInterest");
//        c.decode("GetDataAvailability");
    }

}
