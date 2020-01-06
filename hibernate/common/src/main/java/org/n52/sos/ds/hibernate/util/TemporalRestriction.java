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
package org.n52.sos.ds.hibernate.util;

import java.util.Date;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.PropertyExpression;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.util.DateTimeHelper;

/**
 * Implements the 13 temporal relationships identified by Allen as
 * {@link Criterion Criterions}. Specification can be found in:
 * <ul>
 * <li>ISO 19143:2009, Geographic information — Filter encoding</li>
 * <li>ISO 19108:2002, Geographic Information — Temporal schema, Section 5.2.3.5
 * </li>
 * </ul>
 *
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public abstract class TemporalRestriction {
    public static final String START = "start";
    public static final String END = "end";
    public static final String INSTANT = "instant";
    /**
     * ISO 19108:2002 states (&lt;, &gt;) and not (&le;, &ge;).
     *
     *
     * TODO make this configurable
     */
    private static final boolean ALLOW_EQUALITY = false;
    private Integer count;

    public TemporalRestriction() {
    }
    
    public TemporalRestriction(Integer count) {
        this.count = count;
    }
    
    /**
     * Creates a criterion from this restriction for the specified fields and
     * time.
     *
     * @param ref
     *            the descriptor holding the property name(s)
     * @param time
     *            the compared time
     *
     * @return a <tt>Criterion</tt> that describes this restriction
     *
     * @throws UnsupportedTimeException
     *             if the supplied time can not be used with this restriction
     */
    public Criterion get(TimePrimitiveFieldDescriptor ref, Time time) throws UnsupportedTimeException {
        Criterion c;
        if (time instanceof TimePeriod) {
            c = filterWithPeriod((TimePeriod) time, ref, false);
        } else if (time instanceof TimeInstant) {
            c = filterWithInstant((TimeInstant) time, ref);
        } else {
            throw new UnsupportedTimeException(time);
        }
        if (c == null) {
            throw new UnsupportedTimeException(time);
        }
        return c;
    }
    
    protected String getPlaceHolder(String placeHolder) {
        return isSetCount() ? ":" + placeHolder + count : ":" + placeHolder;
    }
    
    protected boolean isSetCount() {
        return count != null;
    }

    /**
     * Applies this restriction to the specified time periods.
     *
     * @param selfBegin
     *            the property name of the begin time stamp
     * @param selfEnd
     *            the property name of the end time stamp
     * @param otherBegin
     *            the begin instance of the compared time period
     * @param otherEnd
     *            the end instance of the compared time period
     *
     * @return the criterion for the temporal relation (or <tt>null</tt> if not
     *         applicable)
     */
    protected abstract Criterion filterPeriodWithPeriod(String selfBegin, String selfEnd, Date otherBegin,
            Date otherEnd);

    /**
     * Applies this restriction to the specified time instance and time period.
     *
     * @param selfPosition
     *            the property name of the instance
     * @param otherBegin
     *            the begin instance of the compared time period
     * @param otherEnd
     *            the end instance of the compared time period
     * @param isOtherPeriodFromReducedPrecisionInstant
     *            was the period interpreted from a reduced precision time?
     *            see DateTimeHelper.setDateTime2EndOfMostPreciseUnit4RequestedEndPosition
     *
     * @return the criterion for the temporal relation (or <tt>null</tt> if not
     *         applicable)
     */
    protected Criterion filterInstantWithPeriod(String selfPosition, Date otherBegin, Date otherEnd,
            boolean isOtherPeriodFromReducedPrecisionInstant) {
        return null;
    }
    
    protected Criterion filterInstantWithPeriod(String position, String endPosition, Date begin, Date end,
            boolean periodFromReducedPrecisionInstant) {
        return null;
    }

    /**
     * Applies this restriction to the specified time period and time instance.
     *
     * @param selfBegin
     *            the property name of the begin time stamp
     * @param selfEnd
     *            the property name of the end time stamp
     * @param otherPosition
     *            the position of the compared time instance
     *
     * @return the criterion for the temporal relation (or <tt>null</tt> if not
     *         applicable)
     */
    protected Criterion filterPeriodWithInstant(String selfBegin, String selfEnd, Date otherPosition) {
        return null;
    }

    /**
     * Applies this restriction to the specified time instances.
     *
     * @param selfPosition
     *            the property name of the time instance
     * @param otherPosition
     *            the position of the compared time instance
     *
     * @return the criterion for the temporal relation (or <tt>null</tt> if not
     *         applicable)
     */
    protected Criterion filterInstantWithInstant(String selfPosition, Date otherPosition) {
        return null;
    }

    /**
     * Create a filter for the specified period and fields. If the period is no
     * real period but a instance, the method will call
     * {@link #filterWithInstant(TimeInstant, TimePrimitiveFieldDescriptor)
     * filterWithInstant()}.
     *
     * @param time
     *            the time
     * @param r
     *            the property name(s)
     *
     * @return the <tt>Criterion</tt> that describes this restriction
     */
    private Criterion filterWithPeriod(TimePeriod time, TimePrimitiveFieldDescriptor r,
            boolean periodFromReducedPrecisionInstant) {
        Date begin = time.resolveStart().toDate();
        // FIXME should also incorporate reduced precision like getRequestedTimeLength()
        // (Partially?) fixed with use of periodFromReducedPrecisionInstant?
        Date end = time.resolveEnd().toDate();
        if (begin.equals(end)) {
            return filterWithInstant(new TimeInstant(time.resolveStart()), r);
        }
        if (r.isPeriod()) {
            return isSetCount()
                    ?   filterPeriodWithPeriod(r.getBeginPosition(), r.getEndPosition(), begin, end)
//                            getPropertyCheckingCriterion(
//                            filterPeriodWithPeriod(r.getBeginPosition(), r.getEndPosition(), begin, end),
//                            filterInstantWithPeriod(r.getPosition(), r.getEndPosition(), begin, end, periodFromReducedPrecisionInstant), r)
                    : 
                        filterPeriodWithPeriod(r.getBeginPosition(), r.getEndPosition(), begin, end);
//                        getPropertyCheckingCriterion(
//                            filterPeriodWithPeriod(r.getBeginPosition(), r.getEndPosition(), begin, end),
//                            filterInstantWithPeriod(r.getPosition(), begin, end, periodFromReducedPrecisionInstant), r);
        } else {
            return isSetCount()
                    ? filterInstantWithPeriod(r.getBeginPosition(), r.getEndPosition(), begin, end, periodFromReducedPrecisionInstant)
                    : filterInstantWithPeriod(r.getPosition(), begin, end, periodFromReducedPrecisionInstant);
        }

    }

    /**
     * Creates a filter for the specfied instant and fields. In case of a
     * instance with reduced precision a the method will call
     * {@link #filterWithPeriod(TimePeriod, TimePrimitiveFieldDescriptor)
     * filterWithPeriod()}.
     *
     * @param time
     *            the time
     * @param r
     *            the property name(s)
     *
     * @return the <tt>Criterion</tt> that describes this restriction
     */
    private Criterion filterWithInstant(TimeInstant time, TimePrimitiveFieldDescriptor r) {
        /*
         * Saved primitives can be periods, but can also be instants. As begin
         * &lt; end has to be true for all periods those are instants and have
         * to be treated as such. Also instants with reduced precision are
         * semantically periods and have to be handled like periods.
         */
        Date begin = time.resolveValue().toDate();
        Date end = checkInstantWithReducedPrecision(time);
        if (end != null) {
            return filterWithPeriod(new TimePeriod(new DateTime(begin), new DateTime(end)), r, true);
        }
        if (r.isPeriod()) {
            return filterPeriodWithInstant(r.getBeginPosition(), r.getEndPosition(), begin);
        } else {
            return filterInstantWithInstant(r.getPosition(), begin);
        }
    }

    /**
     * Check if <tt>time</tt> is a instance with reduces precision that
     * describes a period (a day, a hour, etc.).
     *
     * @param time
     *            the instant to check
     *
     * @return the end date of the period the instance with reduced precision
     *         started or <tt>null</tt> if there is no reduced precision
     */
    private Date checkInstantWithReducedPrecision(TimeInstant time) {
        DateTime end =
                DateTimeHelper.setDateTime2EndOfMostPreciseUnit4RequestedEndPosition(time.getValue(),
                        time.getRequestedTimeLength());
        return time.getValue().equals(end) ? null : end.toDate();
    }

    /**
     * Creates a <tt>Criterion</tt> that takes care of instants that are saved
     * as periods (<tt>begin == end</tt>). The method builds a composite that
     * applies <tt>periods</tt> to "real" periods and <tt>instants</tt> to
     * periods that are instants by definition.
     *
     * @param periods
     *            the <tt>Criterion</tt> for "real" periods (may be
     *            <tt>null</tt>)
     * @param instants
     *            the <tt>Criterion</tt> for periods with equal begin and end
     *            (may be <tt>null</tt>)
     * @param r
     *            the <tt>TimePrimitiveFieldDescriptor</tt> that holds the
     *            property names
     *
     * @return the composite criterion or <tt>null</tt> if no <tt>Criterion</tt>
     *         could be applied
     */
    protected Criterion getPropertyCheckingCriterion(Criterion periods, Criterion instants,
            TimePrimitiveFieldDescriptor r) {
        if (periods == null) {
            return instants;
//            return instants == null ? null : Restrictions.and(isInstant(r), instants);
        } else {
            return instants == null || periods.equals(instants) ? periods : Restrictions.or(periods, instants);
        }
    }

    /**
     * Creates a <tt>Criterion</tt> for the specified property. Used to easily
     * swap &lt; and &le;.
     *
     * @param property
     *            the property name
     * @param value
     *            the compared value
     *
     * @return the <tt>Criterion</tt>
     */
    protected Criterion lower(String property, Date value) {
        return ALLOW_EQUALITY
                ? Restrictions.le(property, value)
                : Restrictions.lt(property, value);
    }
    
    protected Criterion lower(String property, String value) {
        return ALLOW_EQUALITY
                ? Restrictions.le(property, getPlaceHolder(value))
                : Restrictions.lt(property, getPlaceHolder(value));
    }

    /**
     * Creates a <tt>Criterion</tt> for the specified property. Used to easily
     * swap &gt; and &ge;.
     *
     * @param property
     *            the property name
     * @param value
     *            the compared value
     *
     * @return the <tt>Criterion</tt>
     */
    protected Criterion greater(String property, Date value) {
        return ALLOW_EQUALITY
                ? Restrictions.ge(property, value)
                : Restrictions.gt(property, value);
    }
    
    protected Criterion greater(String property, String value) {
        return ALLOW_EQUALITY
                ? Restrictions.ge(property, getPlaceHolder(value))
                : Restrictions.gt(property, getPlaceHolder(value));
    }
    
    protected Criterion equal(String property, Date value) {
        return Restrictions.eq(property, value);
    }
    
    protected Criterion equal(String property, String value) {
        return Restrictions.eq(property, getPlaceHolder(value));
    }

    /**
     * Creates a <tt>Criterion</tt> that checks that the persisted period is a
     * "real" period (<tt>begin != end</tt>).
     *
     * @param r
     *            the property names
     *
     * @return the <tt>Criterion</tt>
     */
    protected PropertyExpression isPeriod(TimePrimitiveFieldDescriptor r) {
        return Restrictions.neProperty(r.getBeginPosition(), r.getEndPosition());
    }

    /**
     * Creates a <tt>Criterion</tt> that checks that the persisted period is a
     * instant period (<tt>begin == end</tt>).
     *
     * @param r
     *            the property names
     *
     * @return the <tt>Criterion</tt>
     */
    protected PropertyExpression isInstant(TimePrimitiveFieldDescriptor r) {
        return Restrictions.eqProperty(r.getBeginPosition(), r.getEndPosition());
    }

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td><tt>self.end &lt; other.begin</tt></td>
     * <td><tt>self.end &lt; other.position</tt></td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td><tt>self.position &lt; other.begin</tt></td>
     * <td><tt>self.position &lt; other.position</tt></td>
     * </tr>
     * </table>
     */
    public static class BeforeRestriction extends TemporalRestriction {
        
        public BeforeRestriction() {
            super();
        }
        
        public BeforeRestriction(int count) {
            super(count);
        }

        @Override
        protected Criterion filterPeriodWithPeriod(String selfBegin, String selfEnd, Date otherBegin, Date otherEnd) {
            return isSetCount() ? lower(selfBegin, START) : lower(selfBegin, otherBegin);
        }

        @Override
        protected Criterion filterInstantWithPeriod(String selfPosition, Date otherBegin, Date otherEnd,
                boolean isOtherPeriodFromReducedPrecisionInstant) {
            return isSetCount() ? lower(selfPosition, START) : lower(selfPosition, otherBegin);
        }
        
        @Override
        protected Criterion filterInstantWithPeriod(String selfBegin, String selfEnd, Date otherBegin, Date otherEnd,
                boolean isOtherPeriodFromReducedPrecisionInstant) {
            return isSetCount() ? lower(selfBegin, START) : lower(selfBegin, otherBegin);
        }

        @Override
        protected Criterion filterPeriodWithInstant(String selfBegin, String selfEnd, Date otherPosition) {
            return isSetCount() ? lower(selfBegin, INSTANT) : lower(selfBegin, otherPosition);
        }

        @Override
        protected Criterion filterInstantWithInstant(String selfPosition, Date otherPosition) {
            return isSetCount() ? lower(selfPosition, INSTANT) : lower(selfPosition, otherPosition);
        }
    };

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td><tt>self.begin &gt; other.end</tt></td>
     * <td><tt>self.begin &gt; other.position</tt></td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td><tt>self.position &gt; other.end</tt></td>
     * <td><tt>self.position &gt; other.position</tt></td>
     * </tr>
     * </table>
     */
    public static class AfterRestriction extends TemporalRestriction {
        
        public AfterRestriction() {
            super();
        }
        
        public AfterRestriction(int count) {
            super(count);
        }
        
        @Override
        protected Criterion filterPeriodWithPeriod(String selfBegin, String selfEnd, Date otherBegin, Date otherEnd) {
            return isSetCount() ? greater(selfEnd, END) : greater(selfEnd, otherEnd);
        }

        @Override
        protected Criterion filterInstantWithPeriod(String selfPosition, Date otherBegin, Date otherEnd,
                boolean periodFromReducedPrecisionInstant) {
            return isSetCount() ? greater(selfPosition, END) : greater(selfPosition, otherEnd);
        }
        
        @Override
        protected Criterion filterInstantWithPeriod(String selfBegin, String selfEnd, Date otherBegin, Date otherEnd,
                boolean isOtherPeriodFromReducedPrecisionInstant) {
            return isSetCount() ? greater(selfEnd, END) : greater(selfEnd, otherEnd);
        }

        @Override
        protected Criterion filterPeriodWithInstant(String selfBegin, String selfEnd, Date otherPosition) {
            return isSetCount() ? greater(selfEnd, INSTANT) : greater(selfEnd, otherPosition);
        }

        @Override
        protected Criterion filterInstantWithInstant(String selfPosition, Date otherPosition) {
            return isSetCount() ? greater(selfPosition, INSTANT) : greater(selfPosition, otherPosition);
        }
    };

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td><tt>self.begin = other.begin AND self.end &lt; other.end</tt></td>
     * <td><i>not defined</i></td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td><tt>self.position = other.begin</tt></td>
     * <td><i>not defined</i></td>
     * </tr>
     * </table>
     */
    public static class BeginsRestriction extends TemporalRestriction {
        
        public BeginsRestriction() {
            super();
        }
        
        public BeginsRestriction(int count) {
            super(count);
        }
        
        @Override
        protected Criterion filterPeriodWithPeriod(String selfBegin, String selfEnd, Date otherBegin, Date otherEnd) {
            return isSetCount() ? 
                    Restrictions.and(equal(selfBegin, START),  lower(selfEnd, END)) :
                    Restrictions.and(equal(selfBegin, otherBegin), lower(selfEnd, otherEnd));    
        }

        @Override
        protected Criterion filterInstantWithPeriod(String selfPosition, Date otherBegin, Date otherEnd,
                boolean periodFromReducedPrecisionInstant) {
            return isSetCount() ? equal(selfPosition, START) : equal(selfPosition, otherBegin);
        }
    };

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td><tt>self.begin &gt; other.begin AND self.end = other.end</tt></td>
     * <td><i>not defined</i></td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td><tt>self.position = other.end</tt></td>
     * <td><i>not defined</i></td>
     * </tr>
     * </table>
     */
    public static class EndsRestriction extends TemporalRestriction {
        
        public EndsRestriction() {
            super();
        }
        
        public EndsRestriction(int count) {
            super(count);
        }
        
        @Override
        protected Criterion filterPeriodWithPeriod(String selfBegin, String selfEnd, Date otherBegin, Date otherEnd) {
            return isSetCount()
                    ? Restrictions.and(greater(selfBegin, START), equal(selfEnd, END))
                    : Restrictions.and(greater(selfBegin, otherBegin), equal(selfEnd, otherEnd));
        }

        @Override
        protected Criterion filterInstantWithPeriod(String selfPosition, Date otherBegin, Date otherEnd,
                boolean periodFromReducedPrecisionInstant) {
            return isSetCount() 
                    ? equal(selfPosition, END)
                    : equal(selfPosition, otherEnd);
        }
    };

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td><tt>self.begin &lt; other.begin AND self.end = other.end</tt></td>
     * <td><tt>self.end = other.position</tt></td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td><i>not defined</i></td>
     * <td><i>not defined</i></td>
     * </tr>
     * </table>
     */
    public static class EndedByRestriction extends TemporalRestriction {
        
        public EndedByRestriction() {
            super();
        }
        
        public EndedByRestriction(int count) {
            super(count);
        }
        
        @Override
        protected Criterion filterPeriodWithPeriod(String selfBegin, String selfEnd, Date otherBegin, Date otherEnd) {
            return isSetCount() 
                    ? Restrictions.and(lower(selfBegin, START), equal(selfEnd, END))
                    : Restrictions.and(lower(selfBegin, otherBegin), equal(selfEnd, otherEnd));
        }

        @Override
        protected Criterion filterPeriodWithInstant(String selfBegin, String selfEnd, Date otherPosition) {
            return isSetCount() 
                    ? equal(selfEnd, INSTANT)
                    : equal(selfEnd, otherPosition);
        }
    };

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td><tt>self.begin = other.begin AND self.end &gt; other.end</tt></td>
     * <td><tt>self.begin = other.position</tt></td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td><i>not defined</i></td>
     * <td><i>not defined</i></td>
     * </tr>
     * </table>
     */
    public static class BegunByRestriction extends TemporalRestriction {
        
        public BegunByRestriction() {
            super();
        }
        
        public BegunByRestriction(int count) {
            super(count);
        }
        
        @Override
        protected Criterion filterPeriodWithPeriod(String selfBegin, String selfEnd, Date otherBegin, Date otherEnd) {
            return isSetCount() 
                    ? Restrictions.and(equal(selfBegin, START), greater(selfEnd, END))
                    : Restrictions.and(equal(selfBegin, otherBegin), greater(selfEnd, otherEnd));
        }

        @Override
        protected Criterion filterPeriodWithInstant(String selfBegin, String selfEnd, Date otherPosition) {
            return isSetCount() 
                    ? equal(selfBegin, INSTANT)
                    : equal(selfBegin, otherPosition);
        }
    };

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td><tt>self.begin &gt; other.begin AND self.end &lt; other.end</tt></td>
     * <td><i>not defined</i></td>
     * </tr>
     * <td><b>Instant</b></td>
     * <td>
     * <tr>
     * <tt>self.position &gt; other.begin AND self.position &lt; other.end</tt></td>
     * <td><i>not defined</i></td>
     * </tr>
     * </table>
     */
    public static class DuringRestriction extends TemporalRestriction {
        
        public DuringRestriction() {
            super();
        }
        
        public DuringRestriction(int count) {
            super(count);
        }
        
        @Override
        protected Criterion filterPeriodWithPeriod(String selfBegin, String selfEnd, Date otherBegin, Date otherEnd) {
            return isSetCount() 
                    ? Restrictions.and(greater(selfBegin, START), lower(selfEnd, END))
                    : Restrictions.and(greater(selfBegin, otherBegin), lower(selfEnd, otherEnd));
        }

        @Override
        protected Criterion filterInstantWithPeriod(String selfPosition, Date otherBegin, Date otherEnd,
                boolean isOtherPeriodFromReducedPrecisionInstant) {
            return isSetCount() 
                    ? Restrictions.and(greater(selfPosition, START), lower(selfPosition, END))
                    : Restrictions.and(greater(selfPosition, otherBegin), lower(selfPosition, otherEnd));
        }
        
        @Override
        protected Criterion filterInstantWithPeriod(String selfBegin, String selfEnd, Date otherBegin, Date otherEnd,
                boolean periodFromReducedPrecisionInstant) {
            return isSetCount() 
                    ? Restrictions.and(greater(selfBegin, START), lower(selfBegin, END))
                    : Restrictions.and(greater(selfBegin, otherBegin), lower(selfBegin, otherEnd));
        }
    };

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td><tt>self.begin = other.begin AND self.end = other.end</tt></td>
     * <td><i>if period is from a reduced precision instant, self.begin &ge; other.begin
     *        and self.end &le; other.end, otherwise not defined</i></td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td><i>not defined</i></td>
     * <td><tt>self.position = other.position</tt></td>
     * </tr>
     * </table>
     */
    public static class TEqualsRestriction extends TemporalRestriction {
        
        public TEqualsRestriction() {
            super();
        }
        
        public TEqualsRestriction(int count) {
            super(count);
        }
        
        @Override
        protected Criterion filterPeriodWithPeriod(String selfBegin, String selfEnd, Date otherBegin, Date otherEnd) {
            return isSetCount() 
                    ? Restrictions.and(equal(selfBegin, START), equal(selfEnd, END))
                    : Restrictions.and(equal(selfBegin, otherBegin), equal(selfEnd, otherEnd));
        }

        @Override
        protected Criterion filterInstantWithPeriod(String selfPosition, Date otherBegin, Date otherEnd,
                boolean isOtherPeriodFromReducedPrecisionInstant) {
            if (isOtherPeriodFromReducedPrecisionInstant) {
                //time period was created from a reduced precision instant
                return isSetCount() 
                        ? Restrictions.and(greater(selfPosition, START), lower(selfPosition, END))
                        : Restrictions.and(greater(selfPosition, otherBegin), lower(selfPosition, otherEnd));
            } else {
                return null;
            }
        }
        
        @Override
        protected Criterion filterInstantWithPeriod(String selfBegin, String selfEnd, Date otherBegin, Date otherEnd,
                boolean isOtherPeriodFromReducedPrecisionInstant) {
            if (isOtherPeriodFromReducedPrecisionInstant) {
                //time period was created from a reduced precision instant
                return isSetCount() 
                        ? Restrictions.and(greater(selfBegin, START), lower(selfBegin, END))
                        : Restrictions.and(greater(selfBegin, otherBegin), lower(selfBegin, otherEnd));
            } else {
                return null;
            }
        }

        @Override
        protected Criterion filterInstantWithInstant(String selfPosition, Date otherPosition) {
            return isSetCount() 
                    ? equal(selfPosition, INSTANT)
                    : equal(selfPosition, otherPosition);
        }
        
        @Override
        protected Criterion filterPeriodWithInstant(String selfBegin, String selfEnd, Date otherPosition) {
            return isSetCount() 
                    ? Restrictions.and(equal(selfBegin, INSTANT), equal(selfEnd, INSTANT))
                    : Restrictions.and(equal(selfBegin, otherPosition), equal(selfEnd, otherPosition));
        }
    };

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td><tt>self.begin &lt; other.begin AND self.end &gt; other.end</tt></td>
     * <td>
     * <tt>self.begin &lt; other.position AND self.end &gt; other.position</tt></td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td><i>not defined</i></td>
     * <td><i>not defined</i></td>
     * </tr>
     * </table>
     */
    public static class ContainsRestriction extends TemporalRestriction {
        
        public ContainsRestriction() {
            super();
        }
        
        public ContainsRestriction(int count) {
            super(count);
        }
        
        @Override
        protected Criterion filterPeriodWithPeriod(String selfBegin, String selfEnd, Date otherBegin, Date otherEnd) {
            return isSetCount() 
                    ? Restrictions.and(lower(selfBegin, START), greater(selfEnd, END))
                    : Restrictions.and(lower(selfBegin, otherBegin), greater(selfEnd, otherEnd));
        }

        @Override
        protected Criterion filterPeriodWithInstant(String selfBegin, String selfEnd, Date otherPosition) {
            return isSetCount() 
                    ? Restrictions.and(lower(selfBegin, INSTANT), greater(selfEnd, INSTANT))
                    : Restrictions.and(lower(selfBegin, otherPosition), greater(selfEnd, otherPosition));
        }
    };

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td>
     * <tt>self.begin &lt; other.begin AND self.end &gt; other.begin AND self.end &lt; other.end</tt>
     * </td>
     * <td><i>not defined</i></td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td><i>not defined</i></td>
     * <td><i>not defined</i></td>
     * </tr>
     * </table>
     */
    public static class OverlapsRestriction extends TemporalRestriction {
        
        public OverlapsRestriction() {
            super();
        }
        
        public OverlapsRestriction(int count) {
            super(count);
        }
        
        @Override
        protected Criterion filterPeriodWithPeriod(String selfBegin, String selfEnd, Date otherBegin, Date otherEnd) {
            return isSetCount() 
                    ?  Restrictions.and(lower(selfBegin, START), greater(selfEnd, START),
                            lower(selfEnd, END))
                    : Restrictions.and(lower(selfBegin, otherBegin), greater(selfEnd, otherBegin),
                            lower(selfEnd, otherEnd));
        }
    };

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td><tt>self.end = other.begin</tt></td>
     * <td><i>not defined</i></td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td><i>not defined</i></td>
     * <td><i>not defined</i></td>
     * </tr>
     * </table>
     */
    public static class MeetsRestriction extends TemporalRestriction {
        
        public MeetsRestriction() {
            super();
        }
        
        public MeetsRestriction(int count) {
            super(count);
        }
        
        @Override
        protected Criterion filterPeriodWithPeriod(String selfBegin, String selfEnd, Date otherBegin, Date otherEnd) {
            return isSetCount() 
                    ? equal(selfEnd, START)
                    : equal(selfEnd, otherBegin);
        }
    };

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td><tt>self.begin = other.end</tt></td>
     * <td><i>not defined</i></td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td><i>not defined</i></td>
     * <td><i>not defined</i></td>
     * </tr>
     * </table>
     */
    public static class MetByRestriction extends TemporalRestriction {
        
        public MetByRestriction() {
            super();
        }
        
        public MetByRestriction(int count) {
            super(count);
        }
        
        @Override
        protected Criterion filterPeriodWithPeriod(String selfBegin, String selfEnd, Date otherBegin, Date otherEnd) {
            return isSetCount() 
                    ? equal(selfBegin, START)
                    : equal(selfBegin, otherEnd);
        }
    };

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td>
     * <tt>self.begin &gt; other.begin AND self.begin &lt; other.end AND self.end &gt; other.end</tt>
     * </td>
     * <td><i>not defined</i></td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td><i>not defined</i></td>
     * <td><i>not defined</i></td>
     * </tr>
     * </table>
     */
    public static class OverlappedByRestriction extends TemporalRestriction {
        
        public OverlappedByRestriction() {
            super();
        }
        
        public OverlappedByRestriction(int count) {
            super(count);
        }
        
        @Override
        protected Criterion filterPeriodWithPeriod(String selfBegin, String selfEnd, Date otherBegin, Date otherEnd) {
            return isSetCount() 
                    ? Restrictions.and(greater(selfBegin, START), lower(selfBegin, END), greater(selfEnd, END))
                    : Restrictions.and(greater(selfBegin, otherBegin), lower(selfBegin, otherEnd),
                    greater(selfEnd, otherEnd));
        }
    };
}
