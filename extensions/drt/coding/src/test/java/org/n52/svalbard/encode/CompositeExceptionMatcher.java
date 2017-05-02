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
