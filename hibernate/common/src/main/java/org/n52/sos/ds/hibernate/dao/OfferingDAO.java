/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.hibernate.dao;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FormatEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.RelatedFeatureEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosOffering;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesObservationDAO;

/**
 * Hibernate data access class for offering
 *
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class OfferingDAO extends AbstractIdentifierNameDescriptionDAO implements HibernateSqlQueryConstants {

    public OfferingDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    /**
     * Get transactional offering object for identifier
     *
     * @param identifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return Transactional offering object
     */
    public OfferingEntity getTOfferingForIdentifier(final String identifier, final Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<OfferingEntity> query = cb.createQuery(OfferingEntity.class);
        Root<OfferingEntity> root = query.from(OfferingEntity.class);
        query.where(cb.equal(root.get(OfferingEntity.IDENTIFIER), identifier));
        return session.createQuery(query).uniqueResult();
    }

    /**
     * Get all offering objects
     *
     * @param session
     *            Hibernate session
     * @return Offering objects
     */
    public List<OfferingEntity> getOfferings(final Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<OfferingEntity> query = cb.createQuery(OfferingEntity.class);
        query.from(OfferingEntity.class);
        return session.createQuery(query).list();
    }

    /**
     * Get Offering object for identifier
     *
     * @param identifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return Offering object
     */
    public OfferingEntity getOfferingForIdentifier(final String identifier, final Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<OfferingEntity> query = cb.createQuery(OfferingEntity.class);
        Root<OfferingEntity> root = query.from(OfferingEntity.class);
        query.where(cb.equal(root.get(OfferingEntity.IDENTIFIER), identifier));
        return session.createQuery(query).uniqueResult();
    }

    /**
     * Get offering identifiers for procedure identifier
     *
     * @param procedureIdentifier
     *            Procedure identifier
     * @param session
     *            Hibernate session
     * @return Offering identifiers
     * @throws OwsExceptionReport If an error occurs
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<String> getOfferingIdentifiersForProcedure(final String procedureIdentifier, final Session session)
            throws OwsExceptionReport {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<OfferingEntity> root = query.from(OfferingEntity.class);
        Subquery<Long> subquery = query.subquery(Long.class);
        Root seriesRoot = subquery.from(DatasetEntity.class);
        subquery.select(seriesRoot.get(DatasetEntity.PROPERTY_OFFERING).get(OfferingEntity.PROPERTY_ID)).distinct(true)
                .where(cb.equal(seriesRoot.get(DatasetEntity.PROPERTY_DELETED), false),
                        cb.equal(seriesRoot.get(DatasetEntity.PROPERTY_PROCEDURE).get(ProcedureEntity.IDENTIFIER),
                                procedureIdentifier));
        query.select(root.get(OfferingEntity.IDENTIFIER)).distinct(true)
                .where(cb.in(root.get(OfferingEntity.PROPERTY_ID)).value(subquery));
        return session.createQuery(query).list();
    }

    /**
     * Insert or update and get offering
     *
     * @param assignedOffering
     *            SosOffering to insert, update or get
     * @param relatedFeatures
     *            Related feature objects
     * @param observationTypes
     *            Allowed observation type objects
     * @param featureOfInterestTypes
     *            Allowed featureOfInterest type objects
     * @param session
     *            Hibernate session
     * @return Offering object
     */
    public OfferingEntity getAndUpdateOrInsert(SosOffering assignedOffering,
            Collection<RelatedFeatureEntity> relatedFeatures, Collection<FormatEntity> observationTypes,
            Collection<FormatEntity> featureOfInterestTypes, Session session) {
        OfferingEntity offering = getTOfferingForIdentifier(assignedOffering.getIdentifier(), session);
        if (offering == null) {
            offering = new OfferingEntity();
            offering.setIdentifier(assignedOffering.getIdentifier(), getDaoFactory().isStaSupportsUrls());
            if (assignedOffering.isSetName()) {
                offering.setName(assignedOffering.getFirstName().getValue());
            } else {
                offering.setName("Offering for the procedure " + assignedOffering.getIdentifier());
            }
            if (assignedOffering.isSetDescription()) {
                offering.setDescription(assignedOffering.getDescription());
            }
        }
        if (!relatedFeatures.isEmpty()) {
            offering.setRelatedFeatures(new HashSet<>(relatedFeatures));
        } else {
            offering.setRelatedFeatures(new HashSet<RelatedFeatureEntity>(0));
        }
        if (!observationTypes.isEmpty()) {
            offering.setObservationTypes(new HashSet<>(observationTypes));
        } else {
            offering.setObservationTypes(new HashSet<FormatEntity>(0));
        }
        if (!featureOfInterestTypes.isEmpty()) {
            offering.setFeatureTypes(new HashSet<>(featureOfInterestTypes));
        } else {
            offering.setFeatureTypes(new HashSet<FormatEntity>(0));
        }
        session.saveOrUpdate(offering);
        session.flush();
        session.refresh(offering);
        return offering;
    }

    public void updateParentOfferings(Set<String> parentOfferings, OfferingEntity hOffering, Session session) {
        for (String identifier : parentOfferings) {
            OfferingEntity offering = getOfferingForIdentifier(identifier, session);
            if (!offering.getChildren().contains(hOffering)) {
                offering.addChild(hOffering);
                session.saveOrUpdate(offering);
                session.flush();
                session.refresh(offering);
            }
        }
    }

    public OfferingEntity updateOfferingMetadata(OfferingEntity offering, DataEntity<?> observation, Session session) {
        if (offering.getSamplingTimeStart() == null
                || offering.getSamplingTimeStart() != null && observation.getSamplingTimeStart() != null
                        && offering.getSamplingTimeStart().after(observation.getSamplingTimeStart())) {
            offering.setSamplingTimeStart(observation.getSamplingTimeStart());
        }
        if (offering.getSamplingTimeEnd() == null
                || offering.getSamplingTimeEnd() != null && observation.getSamplingTimeEnd() != null
                        && offering.getSamplingTimeEnd().before(observation.getSamplingTimeEnd())) {
            offering.setSamplingTimeEnd(observation.getSamplingTimeEnd());
        }
        if (offering.getResultTimeStart() == null
                || offering.getResultTimeStart() != null && observation.getResultTime() != null
                        && offering.getResultTimeStart().after(observation.getResultTime())) {
            offering.setResultTimeStart(observation.getResultTime());
        }
        if (offering.getResultTimeEnd() == null
                || offering.getResultTimeEnd() != null && observation.getResultTime() != null
                        && offering.getResultTimeEnd().before(observation.getResultTime())) {
            offering.setResultTimeEnd(observation.getResultTime());
        }
        if (offering.getValidTimeStart() == null
                || offering.getValidTimeStart() != null && observation.getValidTimeStart() != null
                        && offering.getValidTimeStart().after(observation.getValidTimeStart())) {
            offering.setValidTimeStart(observation.getValidTimeStart());
        }
        if (offering.getValidTimeEnd() == null
                || offering.getValidTimeEnd() != null && observation.getValidTimeEnd() != null
                        && offering.getValidTimeEnd().before(observation.getValidTimeEnd())) {
            offering.setValidTimeEnd(observation.getValidTimeEnd());
        }
        if (observation.isSetGeometryEntity()) {
            if (offering.isSetGeometry()) {
                offering.getGeometryEntity().expand(observation.getGeometryEntity());
            } else {
                offering.setGeometryEntity(observation.getGeometryEntity().copy());
            }
        } else if (observation.getDataset().isSetFeature() && observation.getDataset().getFeature().isSetGeometry()) {
            if (offering.isSetGeometry()) {
                offering.getGeometryEntity().expand(observation.getDataset().getFeature().getGeometryEntity());
            } else {
                offering.setGeometryEntity(
                        observation.getDataset().getFeature().getGeometryEntity().copy());
            }
        }
        session.saveOrUpdate(offering);
        return offering;
    }

    public void updateAfterObservationDeletion(org.n52.series.db.beans.OfferingEntity offering,
            DataEntity<?> observation, Session session) {
        SeriesObservationDAO seriesObservationDAO = new SeriesObservationDAO(getDaoFactory());
        if (offering.hasSamplingTimeStart()
                && offering.getSamplingTimeStart().equals(observation.getSamplingTimeStart())) {
            DataEntity<?> firstDataEntity =
                    seriesObservationDAO.getFirstObservationFor(observation.getDataset(), session);
            if (firstDataEntity != null) {
                offering.setSamplingTimeStart(firstDataEntity.getSamplingTimeStart());
            }
        }
        if (offering.hasSamplingTimeEnd()
                && offering.getSamplingTimeEnd().equals(observation.getSamplingTimeEnd())) {
            DataEntity<?> latestDataEntity =
                    seriesObservationDAO.getLastObservationFor(observation.getDataset(), session);
            if (latestDataEntity != null) {
                offering.setSamplingTimeEnd(latestDataEntity.getSamplingTimeEnd());
            }
        }
    }

    public void delete(Collection<OfferingEntity> offerings, Session session) throws OwsExceptionReport {
        if (offerings != null && !offerings.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append("delete ");
            builder.append(OfferingEntity.class.getSimpleName());
            builder.append(" where ").append(OfferingEntity.PROPERTY_ID).append(" in :")
                    .append(OfferingEntity.PROPERTY_ID);
            Query<?> q = session.createQuery(builder.toString());
            q.setParameter(OfferingEntity.PROPERTY_ID,
                    offerings.stream().map(OfferingEntity::getId).collect(Collectors.toSet()));
            q.executeUpdate();
            session.flush();
        }
    }

}
