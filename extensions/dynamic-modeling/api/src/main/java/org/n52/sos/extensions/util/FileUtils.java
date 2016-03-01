/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.extensions.util;

import java.net.URISyntaxException;
import java.io.File;

/**
 * Utility class with generic helper File functions.
 */
public class FileUtils 
{
    /** No constructor available. */
    protected FileUtils()
    {
        super();
    }
    
    /** 
     * Returns whether the specified FileName is valid and exist.
     */
    public static boolean safeExistFile(String fileName)
    {
        try
        {
            File file = new File(fileName);
            return file!=null && file.exists();
        }
        catch (Exception e)
        {
            return false;
        }
    }
    
    /**
     * Calculates the valid absolute URI of the specified fileName, likely referred to other reference fileName.
     */
    public static java.net.URI resolveAbsoluteURI(String fileName, String referenceFileName)
    {
        if (safeExistFile(fileName)) return new File(fileName).toURI();
        
        try
        {
            File referenceFile = new File(referenceFileName);
            File file = new File(referenceFile.getParent(), fileName);
            if (file!=null && file.exists()) return file.toURI();
        }
        catch (Exception e) {}
        
        java.net.URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
        if (url!=null) try { return url.toURI(); } catch (URISyntaxException e) { }
        
        return null;
    }
    /**
     * Calculates the valid absolute URI of the specified fileName, likely referred to a resource class loader.
     */
    public static java.net.URI resolveAbsoluteURI(String fileName, ClassLoader classLoader)
    {
        if (safeExistFile(fileName)) return new File(fileName).toURI();
        
        java.net.URL url = classLoader.getResource(fileName);
        if (url!=null) try { return url.toURI(); } catch (URISyntaxException e) { }
        
        url = Thread.currentThread().getContextClassLoader().getResource(fileName);
        if (url!=null) try { return url.toURI(); } catch (URISyntaxException e) { }
        
        return null;
    }
}
