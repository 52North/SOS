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
package org.n52.iceland.util;

/**
 * class represents a range
 * 
 * @since 4.0.0
 * 
 */
public class Range {

    /** from value */
    private int from = 0;

    /** to value */
    private int to = Integer.MAX_VALUE;

    /**
     * constructor
     * 
     * @param start
     *            Start value
     * @param end
     *            End value
     */
    public Range(int start, int end) {
        this.from = start;
        this.to = end;
    }

    /**
     * returns true if a given value is contained in range
     * 
     * @param value
     * @return boolean true if value is contained in range, else false
     */
    public boolean contains(int value) {
        return (from <= value && to >= value);
    }
}
