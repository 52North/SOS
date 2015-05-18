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
package org.n52.iceland.util.http;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class MediaType implements Comparable<MediaType> {
    private static final String WILDCARD_TYPE = "*";

    private static final String QUALITY_PARAMETER = "q";

    private static final ImmutableListMultimap<String, String> EMPTY_MULTI_MAP = ImmutableListMultimap.of();

    private final com.google.common.net.MediaType delegate;

    /**
     * Constructs a <code>*&#47;*</code> media type.
     */
    public MediaType() {
        this(WILDCARD_TYPE, WILDCARD_TYPE, EMPTY_MULTI_MAP);
    }

    /**
     * Constructs a <code>type&#47;*</code> media type.
     * 
     * @param type
     *            the type (may be <code>null</code> for a wild card)
     */
    public MediaType(String type) {
        this(type, WILDCARD_TYPE, EMPTY_MULTI_MAP);
    }

    /**
     * Constructs a <code>type&#47;subtype</code> media type.
     * 
     * @param type
     *            the type (may be <code>null</code> for a wild card)
     * @param subtype
     *            the subtype (may be <code>null</code> for a wild card)
     */
    public MediaType(String type, String subtype) {
        this(type, subtype, EMPTY_MULTI_MAP);
    }

    /**
     * Constructs a <code>type&#47;subtype;parameter="name"</code> media type.
     * 
     * @param type
     *            the type (may be <code>null</code> for a wild card)
     * @param subtype
     *            the subtype (may be <code>null</code> for a wild card)
     * @param parameter
     *            the parameter
     * @param parameterValue
     *            the parameter value
     */
    public MediaType(String type, String subtype, String parameter, String parameterValue) {
        this(type, subtype, ImmutableListMultimap.of(checkNotNull(parameter).toLowerCase(),
                checkNotNull(parameterValue)));
    }

    /**
     * Constructs a media type using the supplied parameters.
     * 
     * @param type
     *            the type (may be <code>null</code> for a wild card)
     * @param subtype
     *            the subtype (may be <code>null</code> for a wild card)
     * @param parameters
     *            the parameter map
     */
    public MediaType(String type, String subtype, Multimap<String, String> parameters) {
        this(com.google.common.net.MediaType.create(type, subtype).withParameters(parameters));
    }

    private MediaType(com.google.common.net.MediaType mediaType) {
        this.delegate = mediaType;
    }

    public String getType() {
        return getDelegate().type();
    }

    public String getSubtype() {
        return getDelegate().subtype();
    }

    public ImmutableListMultimap<String, String> getParameters() {
        return getDelegate().parameters();
    }

    public boolean isWildcard() {
        return isWildcardType() && isWildcardSubtype();
    }

    public boolean isWildcardType() {
        return getType().equals(WILDCARD_TYPE);
    }

    public boolean isWildcardSubtype() {
        return getSubtype().equals(WILDCARD_TYPE);
    }

    public boolean isCompatible(MediaType other) {        
        if (getDelegate().is(other.getDelegate())) {
            return true;
        }
        //check compatible types
        if (MediaTypes.COMPATIBLE_TYPES.containsKey(other)) {
            for (MediaType compatibleType : MediaTypes.COMPATIBLE_TYPES.get(other)) {
                if (getDelegate().is(compatibleType.getDelegate())) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> getParameter(String parameter) {
        return getParameters().get(parameter.toLowerCase());
    }

    public boolean hasParameter(String parameter) {
        return getParameters().containsKey(parameter.toLowerCase());
    }

    public float getQuality() {
        if (hasParameter(QUALITY_PARAMETER)) {
            return Float.valueOf(getParameter(QUALITY_PARAMETER).get(0));
        } else {
            return 1;
        }
    }

    public MediaType withType(String type) {
        return new MediaType(type, getSubtype(), getParameters());

    }

    public MediaType withSubType(String subtype) {
        return new MediaType(getType(), subtype, getParameters());
    }

    public boolean hasParameters() {
        return !getParameters().isEmpty();
    }

    public MediaType withParameter(String parameter, String value) {
        return new MediaType(getDelegate().withParameter(value, value));
    }

    public MediaType withParameters(Multimap<String, String> parameters) {
        return new MediaType(getType(), getSubtype(), parameters);
    }

    public MediaType withoutParameter(String parameter) {
        if (!hasParameter(parameter)) {
            return this;
        }
        ArrayListMultimap<String, String> parameters = ArrayListMultimap.create(getParameters());
        parameters.removeAll(parameter);
        return new MediaType(getDelegate().withParameters(parameters));
    }

    public MediaType withoutQuality() {
        return withoutParameter(QUALITY_PARAMETER);
    }
    
    public MediaType withoutParameters() {
        if (!hasParameters()) {
            return this;
        }
        return new MediaType(getDelegate().withoutParameters());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDelegate());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MediaType) {
            MediaType other = (MediaType) obj;
            return Objects.equal(getDelegate(), other.getDelegate());
        }
        return false;
    }

    @Override
    public int compareTo(MediaType o) {
        checkNotNull(o);
        return ComparisonChain.start().compare(getType(), o.getType()).compare(getSubtype(), o.getSubtype()).result();
    }

    @Override
    public String toString() {
        return getDelegate().toString();
    }

    public static MediaType parse(String string) {
        Preconditions.checkArgument(string != null);
        return new MediaType(com.google.common.net.MediaType.parse(string.trim()));
    }

    /**
     * Normalize mime type string by processing it through the MediaType parser.
     * Handles differing spaces between type and subtype, etc.
     * 
     * @param string
     *            Mime type string to normalize
     * @return Normalized mime type string
     */    
    public static String normalizeString(String string) {
        return parse(string).toString();
    }    
    
    private com.google.common.net.MediaType getDelegate() {
        return delegate;
    }
}
