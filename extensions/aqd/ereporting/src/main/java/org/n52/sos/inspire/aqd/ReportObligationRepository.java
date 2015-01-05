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
package org.n52.sos.inspire.aqd;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.n52.sos.aqd.ReportObligationType;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.inspire.aqd.persistence.ReportingHeaderSQLiteManager;
import org.n52.sos.service.SosContextListener;

public class ReportObligationRepository {
    private final ReadWriteLock reportingAuthorityLock = new ReentrantReadWriteLock();
    private final ReadWriteLock obligationsLock = new ReentrantReadWriteLock();
    private RelatedParty reportingAuthority;
    private final Map<ReportObligationType, ReportObligation> obligations;
    private final ReportingHeaderSQLiteManager sqlite = new ReportingHeaderSQLiteManager();

    private ReportObligationRepository() {
        this.obligations = new EnumMap<>(ReportObligationType.class);
        SosContextListener.registerShutdownHook(sqlite);
    }

    public RelatedParty getReportingAuthority() {
        Lock read = this.reportingAuthorityLock.readLock();
        Lock write = this.reportingAuthorityLock.writeLock();
        read.lock();
        if (this.reportingAuthority == null) {
            read.unlock();
            write.lock();
            try {
                if (this.reportingAuthority == null) {
                    this.reportingAuthority = _getReportingAuthority();
                }
                read.lock();
            } finally {
                write.unlock();
            }
        }
        try {
            return this.reportingAuthority;
        } finally {
            read.unlock();
        }
    }

    public ReportObligation getReportObligation(ReportObligationType type) {
        Lock read = this.obligationsLock.readLock();
        Lock write = this.obligationsLock.writeLock();
        read.lock();
        if (!this.obligations.containsKey(type)) {
            read.unlock();
            write.lock();
            try {
                if (!this.obligations.containsKey(type)) {
                    this.obligations.put(type, this._getReportObligation(type));
                }
                read.lock();
            } finally {
                write.unlock();
            }
        }
        try {
            return this.obligations.get(type);
        } finally {
            read.unlock();
        }
    }

    public void saveReportingAuthority(RelatedParty relatedParty) {
        Lock write = this.reportingAuthorityLock.writeLock();
        write.lock();
        try {
            this.reportingAuthority = relatedParty;
        } finally {
            write.unlock();
        }
        this._saveReportingAuthority(relatedParty);
    }

    public void saveReportObligation(ReportObligationType type,
                                     ReportObligation obligation) {
        Lock write = this.obligationsLock.writeLock();
        write.lock();
        try {
            this.obligations.put(type, obligation);
        } finally {
            write.unlock();
        }
        this._saveReportObligation(type, obligation);
    }

    public EReportingHeader createHeader(ReportObligationType flow) throws CodedException {
        ReportObligation reportObligation = getReportObligation(flow);
        if (reportObligation.isValid()) {
	        return new EReportingHeader()
	                .setChange(reportObligation.getChange())
	                .setInspireID(reportObligation.getInspireID())
	                .setReportingPeriod(reportObligation.getReportingPeriod())
	                .setReportingAuthority(getReportingAuthority());
        }
		throw new NoApplicableCodeException()
					.at("AQD Repoting Header")
					.withMessage(
							"No AQD Repoting Header set for %s! Please go to the admin interface (Admin -> Settings -> eReporting) and configure the AQD Repoting Header!", flow.name());
    }

    private void _saveReportingAuthority(RelatedParty relatedParty) {
        sqlite.save(relatedParty);
    }

    private RelatedParty _getReportingAuthority() {
        RelatedParty reportingAuthority = sqlite.loadRelatedParty();
        if (reportingAuthority == null) {
            reportingAuthority = new RelatedParty();
        }
        return reportingAuthority;
    }

    private void _saveReportObligation(ReportObligationType type,
                                       ReportObligation obligation) {
        sqlite.save(type, obligation);
    }

    private ReportObligation _getReportObligation(ReportObligationType type) {
        ReportObligation reportObligation = sqlite.loadReportObligation(type);
        if (reportObligation == null) {
            reportObligation = new ReportObligation();
        }
        return reportObligation;
    }

    public static ReportObligationRepository getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static ReportObligationRepository INSTANCE
                = new ReportObligationRepository();

        private InstanceHolder() {
        }
    }
}
