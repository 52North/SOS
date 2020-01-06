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
package org.n52.svalbard.encode;

import com.google.common.collect.Lists;
import java.util.List;
import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 *
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 * J&uuml;rrens</a>
 */
public class CompositeExceptionMatcher extends BaseMatcher<CompositeOwsException>{

    private int expectedSize;
    
    private List<Matcher<?>> exceptionMatcher;
    
    boolean classNotMatched, sizeNotMatched;
    int subMatcherNotMatched = -1;

    public CompositeExceptionMatcher() {
    }

    @Override
    public boolean matches(Object o) {
        if (o == null) {
            return false;
        }
        if (!o.getClass().isAssignableFrom(CompositeOwsException.class)) {
            classNotMatched = true;
            return false;
        }
        CompositeOwsException e = (CompositeOwsException) o;
        if (e.getExceptions().size() != expectedSize) {
            sizeNotMatched = true;
            return false;
        }
        for (int i = 0; i < expectedSize; i++) {
            if (!exceptionMatcher.get(i).matches(e.getExceptions().get(i))) {
                subMatcherNotMatched = i;
                return false;
            }
        }
        return true;
    }

    @Override
    public void describeTo(Description d) {
        if (classNotMatched) {
            d.appendText("type ").appendText(this.getClass().getName());
        } else if (sizeNotMatched) {
            d.appendText(" exceptions ").appendValue(expectedSize);
        } else if (subMatcherNotMatched != -1) {
            exceptionMatcher.get(subMatcherNotMatched).describeTo(d);
        }
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        if (classNotMatched) {
            description.appendText(item.getClass().getName());
        } else if (sizeNotMatched) {
            description.appendValue(((CompositeOwsException)item).getExceptions().size());
        } else if (subMatcherNotMatched != -1) {
            exceptionMatcher
                    .get(subMatcherNotMatched)
                    .describeMismatch(
                            ((CompositeOwsException)item)
                                    .getExceptions()
                                    .get(subMatcherNotMatched),
                            description);
        }
    }
    
    

    public CompositeExceptionMatcher with(Class<? extends OwsExceptionReport> exceptionClass) {
        if (exceptionClass != null) {
            if (exceptionMatcher == null) {
                exceptionMatcher = Lists.newArrayList();
            }
            exceptionMatcher.add(CoreMatchers.instanceOf(exceptionClass));
            expectedSize++;
        }
        return this;
    }

}
