/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.entities.series.values;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasDeletedFlag;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasOfferings;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.entities.series.HibernateSeriesRelations.HasSeries;

public class SeriesValueTime implements Serializable, HasSeries, HasDeletedFlag, HasOfferings {

        private static final long serialVersionUID = -6266104149753313552L;

        public static final String ID = "observationId";

        public static final String PHENOMENON_TIME_START = "phenomenonTimeStart";

        public static final String PHENOMENON_TIME_END = "phenomenonTimeEnd";

        public static final String VALID_TIME_START = "validTimeStart";

        public static final String VALID_TIME_END = "validTimeEnd";

        public static final String RESULT_TIME = "resultTime";
        
        private long observationId;

        private Series series;

        private Date phenomenonTimeStart;

        private Date phenomenonTimeEnd;

        private Date resultTime;

        private Date validTimeStart;

        private Date validTimeEnd;
        
        private Set<Offering> offerings = new HashSet<Offering>(0);
        
        private boolean deleted;

        /**
         * Get the observation id
         * 
         * @return Observation id
         */
        public long getObservationId() {
            return observationId;
        }

        /**
         * Set the observation id
         * 
         * @param observationId
         *            Observation id to set
         */
        public void setObservationId(final long observationId) {
            this.observationId = observationId;
        }

        @Override
        public Series getSeries() {
            return series;
        }

        @Override
        public void setSeries(Series series) {
            this.series = series;
        }

        @Override
        public boolean isSetSeries() {
            return getSeries() != null;
        }

        /**
         * Get the start phenomenon time
         * 
         * @return Start phenomenon time
         */
        public Date getPhenomenonTimeStart() {
            return phenomenonTimeStart;
        }

        /**
         * Set the start phenomenon time
         * 
         * @param phenomenonTimeStart
         *            Start phenomenon time to set
         */
        public void setPhenomenonTimeStart(final Date phenomenonTimeStart) {
            this.phenomenonTimeStart = phenomenonTimeStart;
        }

        /**
         * Get the end phenomenon time
         * 
         * @return End phenomenon time
         */
        public Date getPhenomenonTimeEnd() {
            return phenomenonTimeEnd;
        }

        /**
         * Set the end phenomenon time
         * 
         * @param phenomenonTimeEnd
         *            End phenomenon time to set
         */
        public void setPhenomenonTimeEnd(final Date phenomenonTimeEnd) {
            this.phenomenonTimeEnd = phenomenonTimeEnd;
        }

        /**
         * Get the result time
         * 
         * @return Result time
         */
        public Date getResultTime() {
            return resultTime;
        }

        /**
         * Set the result tiem
         * 
         * @param resultTime
         *            Result tiem to set
         */
        public void setResultTime(final Date resultTime) {
            this.resultTime = resultTime;
        }

        /**
         * Get the start valid time
         * 
         * @return Start valid time
         */
        public Date getValidTimeStart() {
            return validTimeStart;
        }

        /**
         * Set the start valid time
         * 
         * @param validTimeStart
         *            Start valid time to set
         */
        public void setValidTimeStart(final Date validTimeStart) {
            this.validTimeStart = validTimeStart;
        }

        /**
         * Get the end valid time
         * 
         * @return End valid time
         */
        public Date getValidTimeEnd() {
            return validTimeEnd;
        }

        /**
         * Set the end valid time
         * 
         * @param validTimeEnd
         *            End valid time to set
         */
        public void setValidTimeEnd(final Date validTimeEnd) {
            this.validTimeEnd = validTimeEnd;
        }

        @Override
        public HasDeletedFlag setDeleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        @Override
        public boolean isDeleted() {
            return deleted;
        }

        @Override
        public Set<Offering> getOfferings() {
            return offerings;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void setOfferings(final Object offerings) {
            if (offerings instanceof Set<?>) {
                this.offerings = (Set<Offering>) offerings;
            } else {
                getOfferings().add((Offering) offerings);
            }
        }

}
