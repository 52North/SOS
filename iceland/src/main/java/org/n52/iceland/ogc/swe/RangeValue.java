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
package org.n52.iceland.ogc.swe;

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
