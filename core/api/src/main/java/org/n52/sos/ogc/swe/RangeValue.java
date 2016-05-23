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
package org.n52.sos.ogc.swe;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 4.0.0
 *
 * @param <T>
 */
public class RangeValue<T> {

    private T rangeStart;

    private T rangeEnd;

    public RangeValue(final T rangeStart, final T rangeEnd) {
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
    }

    public RangeValue() {
    }

    public T getRangeStart() {
        return rangeStart;
    }

    public T getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeStart(final T rangeStart) {
        this.rangeStart = rangeStart;
    }

    public void setRangeEnd(final T rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    public boolean isSetStartValue() {
        return rangeStart != null;
    }

    public boolean isSetEndValue() {
        return rangeEnd != null;
    }

    public List<T> getRangeAsList() {
        final List<T> list = new ArrayList<T>();
        list.add(rangeStart);
        list.add(rangeEnd);
        return list;
    }

    public List<String> getRangeAsStringList() {
        final List<String> list = new ArrayList<String>();
        list.add(rangeStart.toString());
        list.add(rangeEnd.toString());
        return list;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        if (isSetStartValue()) {
            builder.append(rangeStart);
        } else {
            builder.append("null");
        }
        if (isSetEndValue()) {
            builder.append(rangeEnd);
        } else {
            builder.append("null");
        }
        return builder.toString();
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((rangeEnd == null) ? 0 : rangeEnd.hashCode());
		result = prime * result
				+ ((rangeStart == null) ? 0 : rangeStart.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RangeValue)) {
			return false;
		}
		final RangeValue<?> other = (RangeValue<?>) obj;
		if (rangeEnd == null) {
			if (other.rangeEnd != null) {
				return false;
			}
		} else if (!rangeEnd.equals(other.rangeEnd)) {
			return false;
		}
		if (rangeStart == null) {
			if (other.rangeStart != null) {
				return false;
			}
		} else if (!rangeStart.equals(other.rangeStart)) {
			return false;
		}
		return true;
	}
}
