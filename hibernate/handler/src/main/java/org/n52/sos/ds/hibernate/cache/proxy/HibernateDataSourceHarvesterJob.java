/*
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
package org.n52.sos.ds.hibernate.cache.proxy;

import org.n52.io.task.ScheduledJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class HibernateDataSourceHarvesterJob extends ScheduledJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // TODO Auto-generated method stub

    }

    @Override
    public JobDetail createJobDetails() {
        // TODO Auto-generated method stub
        return null;
    }

    // private final static Logger LOGGER =
    // LoggerFactory.getLogger(HibernateDataSourceHarvesterJob.class);
    //
    // @Inject
    // private InsertRepository insertRepository;
    // private HibernateSessionHolder sessionHolder;
    // private EventBus eventBus;
    // private DaoFactory daoFactory;
    //
    // @Inject
    // public void setConnectionProvider(ConnectionProvider connectionProvider)
    // {
    // this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    // }
    //
    // @Inject
    // public void setDaoFactory(DaoFactory daoFactory) {
    // this.daoFactory = daoFactory;
    // }
    //
    // public HibernateSessionHolder getConnectionProvider() {
    // return this.sessionHolder;
    // }
    //
    // public InsertRepository getInsertRepository() {
    // return insertRepository;
    // }
    //
    // public void setInsertRepository(InsertRepository insertRepository) {
    // this.insertRepository = insertRepository;
    // }
    //
    // @Inject
    // public void setServiceEventBus(EventBus eventBus) {
    // this.eventBus = eventBus;
    // }
    //
    // public EventBus getServiceEventBus() {
    // return eventBus;
    // }
    //
    // @Override
    // public JobDetail createJobDetails() {
    // return JobBuilder.newJob(HibernateDataSourceHarvesterJob.class)
    // .withIdentity(getJobName())
    // .build();
    // }
    //
    // @Override
    // public void execute(JobExecutionContext context) throws
    // JobExecutionException {
    // Session session = null;
    // try {
    // LOGGER.info(context.getJobDetail().getKey() + " execution starts.");
    // session = getConnectionProvider().getSession();
    // ProxyServiceEntity service =
    // insertRepository.insertService(EntityBuilder.createService("localDB",
    // "description of localDB", "localhost", "2.0.0"));
    //// insertRepository.cleanUp(service);
    //// insertRepository.prepareInserting(service);
    // harvestOfferings(service, session);
    // harvestSeries(service, session);
    // harvestRelatedFeartures(service, session);
    // LOGGER.info(context.getJobDetail().getKey() + " execution ends.");
    // getServiceEventBus().submit(new UpdateCache());
    // } catch (Exception ex) {
    // LOGGER.error("Error while harvesting cache!", ex);
    // } finally {
    // getConnectionProvider().returnSession(session);
    // }
    // }
    //
    // private void harvestOfferings(ServiceEntity service, Session session)
    // throws OwsExceptionReport {
    // Map<String, OfferingTimeExtrema> offeringTimeExtremas =
    // daoFactory.getOfferingDAO().getOfferingTimeExtrema(null, session);
    // for (Offering offering :
    // daoFactory.getOfferingDAO().getOfferings(session)) {
    // OfferingEntity offferingEntity = EntityBuilder.createOffering(offering,
    // service, true, true);
    // if (offeringTimeExtremas.containsKey(offering.getIdentifier())) {
    // OfferingTimeExtrema offeringTimeExtrema =
    // offeringTimeExtremas.get(offering.getIdentifier());
    // offferingEntity.setPhenomenonTimeStart(offeringTimeExtrema.getMinPhenomenonTime().toDate());
    // offferingEntity.setPhenomenonTimeEnd(offeringTimeExtrema.getMaxPhenomenonTime().toDate());
    // offferingEntity.setResultTimeStart(offeringTimeExtrema.getMinResultTime().toDate());
    // offferingEntity.setResultTimeEnd(offeringTimeExtrema.getMaxResultTime().toDate());
    // }
    // ReferencedEnvelope spatialFilteringProfileEnvelope =
    // daoFactory.getObservationDAO().getSpatialFilteringProfileEnvelopeForOfferingId(offering.getIdentifier(),
    // session);
    // if (spatialFilteringProfileEnvelope != null &&
    // spatialFilteringProfileEnvelope.isSetEnvelope()) {
    // offferingEntity.setEnvelope(new
    // GeometryFactory().toGeometry(JTSConverter.convert(spatialFilteringProfileEnvelope.getEnvelope())));
    // }
    // insertRepository.insertOffering(offferingEntity);
    // }
    // }
    //
    // private void harvestSeries(ServiceEntity service, Session session) throws
    // OwsExceptionReport {
    // AbstractSeriesDAO seriesDAO = daoFactory.getSeriesDAO();
    // for (Series series : seriesDAO.getSeries(session)) {
    // DatasetEntity<?> dataset = EntityBuilder.createDataset(series, service);
    // if (dataset != null) {
    // insertRepository.insertDataset(dataset);
    // }
    // }
    // }
    //
    // private void harvestRelatedFeartures(ServiceEntity service, Session
    // session) {
    // if (HibernateHelper.isEntitySupported(TOffering.class)) {
    // Set<RelatedFeatureEntity> relatedFeatures = new HashSet<>();
    // for (Offering offering :
    // daoFactory.getOfferingDAO().getOfferings(session)) {
    // if (offering instanceof TOffering && ((TOffering)
    // offering).hasRelatedFeatures()) {
    // for (RelatedFeature relatedFeatureEntity : ((TOffering)
    // offering).getRelatedFeatures()) {
    // relatedFeatures.add(EntityBuilder.createRelatedFeature(relatedFeatureEntity,
    // service));
    // }
    // }
    // }
    // insertRepository.insertRelatedFeature(relatedFeatures);
    // }
    // }

}
