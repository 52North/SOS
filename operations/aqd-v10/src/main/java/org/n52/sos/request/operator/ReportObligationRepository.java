/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
package org.n52.sos.request.operator;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.inject.Inject;

import org.n52.shetland.aqd.EReportObligationRepository;
import org.n52.shetland.aqd.EReportingHeader;
import org.n52.shetland.aqd.ReportObligation;
import org.n52.shetland.aqd.ReportObligationType;
import org.n52.shetland.inspire.base2.RelatedParty;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.config.json.JsonReportingHeaderDao;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public final class ReportObligationRepository implements EReportObligationRepository {
    private final ReadWriteLock reportingAuthorityLock = new ReentrantReadWriteLock();

    private final ReadWriteLock obligationsLock = new ReentrantReadWriteLock();

    private RelatedParty reportingAuthority;

    private final Map<ReportObligationType, ReportObligation> obligations;

    @Inject
    private JsonReportingHeaderDao jsonDao;

    private ReportObligationRepository() {
        this.obligations = new EnumMap<>(ReportObligationType.class);
    }

    @Override
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

    @Override
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

    public void saveReportObligation(ReportObligationType type, ReportObligation obligation) {
        Lock write = this.obligationsLock.writeLock();
        write.lock();
        try {
            this.obligations.put(type, obligation);
        } finally {
            write.unlock();
        }
        this._saveReportObligation(type, obligation);
    }

    public EReportingHeader createHeader(ReportObligationType flow) throws OwsExceptionReport {
        ReportObligation reportObligation = getReportObligation(flow);
        if (!reportObligation.isValid()) {
            throw new NoApplicableCodeException().at("AQD Repoting Header").withMessage(
                    "No AQD Repoting Header set for %s! Please go to the admin interface "
                    + "(Admin -> Settings -> eReporting) and configure the AQD Repoting Header!",
                    flow.name());
        }
        return new EReportingHeader().setChange(reportObligation.getChange())
                .setInspireID(reportObligation.getInspireID())
                .setReportingPeriod(reportObligation.getReportingPeriod())
                .setReportingAuthority(getReportingAuthority());
    }

    private void _saveReportingAuthority(RelatedParty relatedParty) {
        jsonDao.save(relatedParty);
    }

    private RelatedParty _getReportingAuthority() {
        return Optional.ofNullable(jsonDao.loadRelatedParty()).orElseGet(RelatedParty::new);
    }

    private void _saveReportObligation(ReportObligationType type, ReportObligation obligation) {
        jsonDao.save(type, obligation);
    }

    private ReportObligation _getReportObligation(ReportObligationType type) {
        return Optional.ofNullable(jsonDao.loadReportObligation(type)).orElseGet(ReportObligation::new);
    }
}