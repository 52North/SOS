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
package org.n52.iceland.ogc.filter;

import javax.xml.namespace.QName;

import org.n52.iceland.ogc.OGCConstants;
import org.n52.iceland.w3c.SchemaLocation;

/**
 * Constants interface for <a
 * href="http://www.opengeospatial.org/standards/filter">OGC Filter Encoding</a>
 * 
 * @since 4.0.0
 */
public interface FilterConstants {
    String NS_FES_2 = "http://www.opengis.net/fes/2.0";

    String NS_FES_2_PREFIX = "fes";

    String SCHEMA_LOCATION_URL_FES_20 = "http://schemas.opengis.net/filter/2.0/filterAll.xsd";

    SchemaLocation FES_20_SCHEMA_LOCATION = new SchemaLocation(NS_FES_2, SCHEMA_LOCATION_URL_FES_20);

    String FILTER_LANGUAGE_FES_FILTER = OGCConstants.QUERY_LANGUAGE_PREFIX + "OGC-FES:Filter";

    /*
     * element names
     */
    String EN_TEQUALS = "TEquals";
    
    String EN_VALUE_REFERENCE = "ValueReference";

    String EN_LITERAL = "Literal";
    
    String EN_PROPERTY_IS_EQUAL_TO = "PropertyIsEqualTo";
    
    String EN_FILTER = "Filter";
    
    /*
     * QNames
     */
    QName QN_FILTER = new QName(FilterConstants.NS_FES_2, FilterConstants.EN_FILTER, FilterConstants.NS_FES_2_PREFIX);
    
    QName QN_VALUE_REFERENCE = new QName(FilterConstants.NS_FES_2, FilterConstants.EN_VALUE_REFERENCE, FilterConstants.NS_FES_2_PREFIX);
    
    QName QN_LITERAL = new QName(FilterConstants.NS_FES_2, FilterConstants.EN_LITERAL, FilterConstants.NS_FES_2_PREFIX);

    QName QN_PROPERTY_IS_EQUAL_TO = new QName(FilterConstants.NS_FES_2, FilterConstants.EN_PROPERTY_IS_EQUAL_TO, FilterConstants.NS_FES_2_PREFIX);
    
    
    /**
     * Enumeration for conformance class constraint names
     */
    enum ConformanceClassConstraintNames {
        ImplementsQuery, ImplementsAdHocQuery, ImplementsFunctions, ImplementsResourceld, ImplementsMinStandardFilter, ImplementsStandardFilter, ImplementsMinSpatialFilter, ImplementsSpatialFilter, ImplementsMinTemporalFilter, ImplementsTemporalFilter, ImplementsVersionNav, ImplementsSorting, ImplementsExtendedOperators, ImplementsMinimumXPath, ImplementsSchemaElementFunc
    }

    /**
     * Enumeration for temporal operators
     */
    enum TimeOperator {
        TM_Before, TM_After, TM_Begins, TM_Ends, TM_EndedBy, TM_BegunBy, TM_During, TM_Equals, TM_Contains, TM_Overlaps, TM_Meets, TM_MetBy, TM_OverlappedBy;
        
        public static TimeOperator from(String s) {
            for (TimeOperator to : TimeOperator.values()) {
                if (to.name().equalsIgnoreCase(s)) {
                    return to;
                }
            }
            throw new IllegalArgumentException(s);
        }
        
        public static TimeOperator from(TimeOperator2 to2) {
            switch (to2) {
            case After:
                return TimeOperator.TM_After;
             case Before:
                 return TimeOperator.TM_Before;
             case Begins:
                 return TimeOperator.TM_Begins;
             case BegunBy:
                 return TimeOperator.TM_BegunBy;
             case During:
                 return TimeOperator.TM_During;
             case EndedBy:
                 return TimeOperator.TM_EndedBy;
             case Ends:
                 return TimeOperator.TM_Ends;
             case Meets:
                 return TimeOperator.TM_Meets;
             case MetBy:
                 return TimeOperator.TM_MetBy;
             case OverlappedBy:
                 return TimeOperator.TM_OverlappedBy;
             case TContains:
                 return TimeOperator.TM_Contains;
             case TEquals:
                 return TimeOperator.TM_Equals;
             case TOverlaps:
                 return TimeOperator.TM_Overlaps;
            default:
                throw new IllegalArgumentException(to2.name());
            }
        }
    }

    /**
     * Enumeration for FES 2.0 temporal operators
     */
    enum TimeOperator2 {
        Before, After, Begins, Ends, EndedBy, BegunBy, During, TEquals, TContains, TOverlaps, Meets, MetBy, OverlappedBy;
        
        public static TimeOperator2 from(String s) {
            for (TimeOperator2 to : TimeOperator2.values()) {
                if (to.name().equalsIgnoreCase(s)) {
                    return to;
                }
            }
            throw new IllegalArgumentException(s);
        }
        
        public static TimeOperator2 from(TimeOperator to) {
            switch (to) {
            case TM_After:
                return TimeOperator2.After;
             case TM_Before:
                 return TimeOperator2.Before;
             case TM_Begins:
                 return TimeOperator2.Begins;
             case TM_BegunBy:
                 return TimeOperator2.BegunBy;
             case TM_During:
                 return TimeOperator2.During;
             case TM_EndedBy:
                 return TimeOperator2.EndedBy;
             case TM_Ends:
                 return TimeOperator2.Ends;
             case TM_Meets:
                 return TimeOperator2.Meets;
             case TM_MetBy:
                 return TimeOperator2.MetBy;
             case TM_OverlappedBy:
                 return TimeOperator2.OverlappedBy;
             case TM_Contains:
                 return TimeOperator2.TContains;
             case TM_Equals:
                 return TimeOperator2.TEquals;
             case TM_Overlaps:
                 return TimeOperator2.TOverlaps;
            default:
                throw new IllegalArgumentException(to.name());
            }
        }
    }

    /**
     * Enumeration for spatial operators
     */
    enum SpatialOperator {
        Equals, Disjoint, Touches, Within, Overlaps, Crosses, Intersects, Contains, DWithin, Beyond, BBOX
    }

    /**
     * Enumeration for comparison operators
     */
    enum ComparisonOperator {
        PropertyIsEqualTo, PropertyIsNotEqualTo, PropertyIsLessThan, PropertyIsGreaterThan, PropertyIsLessThanOrEqualTo, PropertyIsGreaterThanOrEqualTo, PropertyIsLike, PropertyIsNil, PropertyIsNull, PropertyIsBetween
    }

    /**
     * Enumeration for binary logic operators
     * 
     * @since 4.0.0
     * 
     */
    enum BinaryLogicOperator {
        And, Or
    }

    /**
     * Enumeration for unary logic operators
     * 
     * @since 4.0.0
     * 
     */
    enum UnaryLogicOperator {
        Not
    }

    /**
     * Enumeration for AdHoc query parameter
     * 
     * @since 4.0.0
     * 
     */
    enum AdHocQueryParams {
        TypeNames, Aliases, PropertyName, Filter, Filter_Language, ResourceId, BBox, SortBy
        /*
         * TypeNames is mandatory but "Standards that reference this
         * International Standard may change the requirement for the TYPENAME
         * parameter. In such cases, the referencing standard shall document
         * whether the TYPENAME parameter is mandatory, optional or mandatory in
         * some cases and optional in others."
         */
    }

    /**
     * Enumeration for sort order
     * 
     * @since 4.0.0
     * 
     */
    enum SortOrder {
        ASC, DESC
    }
}
