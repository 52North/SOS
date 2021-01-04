/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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

public class EntityBuilder {

    // public static ProxyServiceEntity createService(String name, String
    // description, String url, String version) {
    // ProxyServiceEntity service = new ProxyServiceEntity();
    // service.setName(name);
    // service.setDescription(description);
    // service.setVersion(version);
    // service.setType("SOS");
    // service.setUrl(url);
    // return service;
    // }
    //
    // public static ProcedureEntity createProcedure(Procedure sosProc,
    // ServiceEntity service, boolean processParents, boolean processChilds) {
    // ProcedureEntity procedure = new ProcedureEntity();
    // procedure.setService(service);
    // setIdentifierNameDesxription(sosProc, procedure);
    // procedure.setInsitu(sosProc.isInsitu());
    // procedure.setMobile(sosProc.isMobile());
    // if (sosProc.hasParents() && processParents) {
    // Set<ProcedureEntity> parents = new HashSet<>();
    // for (Procedure parent : sosProc.getParents()) {
    // parents.add(createProcedure(parent, service, true, false));
    // }
    // procedure.setParents(parents);
    // }
    // if (sosProc.hasChilds() && processChilds) {
    // Set<ProcedureEntity> childs = new HashSet<>();
    // for (Procedure child : sosProc.getChilds()) {
    // childs.add(createProcedure(child, service, false, true));
    // }
    // procedure.setChildren(childs);
    // }
    // procedure.setProcedureDescriptionFormat(sosProc.getProcedureDescriptionFormat()
    // .getProcedureDescriptionFormat());
    // return procedure;
    // }
    //
    // public static OfferingEntity createOffering(Offering sosOffering,
    // ServiceEntity service, boolean processParents, boolean processChilds) {
    // OfferingEntity offering = new OfferingEntity();
    // offering.setService(service);
    // setIdentifierNameDesxription(sosOffering, offering);
    // if (sosOffering.hasParents() && processParents) {
    // Set<OfferingEntity> parents = new HashSet<>();
    // for (Offering parent : sosOffering.getParents()) {
    // parents.add(createOffering(parent, service, true, false));
    // }
    // offering.setParents(parents);
    // }
    // if (sosOffering.hasChilds() && processChilds) {
    // Set<OfferingEntity> childs = new HashSet<>();
    // for (Offering child : sosOffering.getChilds()) {
    // childs.add(createOffering(child, service, false, true));
    // }
    // offering.setChildren(childs);
    // }
    // if (sosOffering.hasObservationTypes()) {
    // offering.setObservationTypes(sosOffering.getObservationTypes().stream().map(ot
    // -> ot.getObservationType())
    // .collect(Collectors.toSet()));
    // }
    // return offering;
    // }
    //
    // public static CategoryEntity createCategory(ObservableProperty
    // sosObsProp, ServiceEntity service) {
    // CategoryEntity category = new CategoryEntity();
    // category.setService(service);
    // setIdentifierNameDesxription(sosObsProp, category);
    // return category;
    // }
    //
    // public static PhenomenonEntity createPhenomenon(ObservableProperty
    // sosObsProp, ServiceEntity service, boolean processParents, boolean
    // processChilds) {
    // PhenomenonEntity phenomenon = new PhenomenonEntity();
    // phenomenon.setService(service);
    // setIdentifierNameDesxription(sosObsProp, phenomenon);
    // if (sosObsProp.hasParents() && processParents) {
    // Set<PhenomenonEntity> parents = new HashSet<>();
    // for (ObservableProperty parent : sosObsProp.getParents()) {
    // parents.add(createPhenomenon(parent, service, true, false));
    // }
    // phenomenon.setParents(parents);
    // }
    // if (sosObsProp.hasChilds() && processChilds) {
    // Set<PhenomenonEntity> childs = new HashSet<>();
    // for (ObservableProperty child : sosObsProp.getChilds()) {
    // childs.add(createPhenomenon(child, service, false, true));
    // }
    // phenomenon.setChildren(childs);
    // }
    // return phenomenon;
    // }
    //
    // public static FeatureEntity createFeature(AbstractFeatureOfInterest
    // sosFeature, ServiceEntity service, boolean processParents, boolean
    // processChilds) {
    // FeatureEntity feature = new FeatureEntity();
    // feature.setService(service);
    // setIdentifierNameDesxription(sosFeature, feature);
    // feature.setGeometryEntity(new
    // GeometryEntity().setGeometry(sosFeature.getGeom()));
    // if (sosFeature.hasParents() && processParents) {
    // Set<FeatureEntity> parents = new HashSet<>();
    // for (AbstractFeatureOfInterest parent : sosFeature.getParents()) {
    // parents.add(createFeature(parent, service, true, false));
    // }
    // feature.setParents(parents);
    // }
    // if (sosFeature.hasChilds() && processChilds) {
    // Set<FeatureEntity> childs = new HashSet<>();
    // for (AbstractFeatureOfInterest child : sosFeature.getChilds()) {
    // childs.add(createFeature(child, service, false, true));
    // }
    // feature.setChildren(childs);
    // }
    // return feature;
    // }
    //
    // public static UnitEntity createUnit(Unit unit, ServiceEntity service) {
    // UnitEntity entity = new UnitEntity();
    // entity.setName(unit.getUnit());
    // entity.setService(service);
    // return entity;
    // }
    //
    // public static DatasetEntity createDataset(Series series, ServiceEntity
    // service) {
    // DatasetEntity dataset = createDataset(series.getSeriesType());
    // if (dataset != null) {
    // dataset.setService(service);
    // setIdentifierNameDesxription(series, dataset);
    // dataset.setPublished(series.isPublished());
    // dataset.setDeleted(series.isDeleted());
    // dataset.setFirstValueAt(series.getFirstTimeStamp());
    // dataset.setLastValueAt(series.getLastTimeStamp());
    // dataset.setProcedure(createProcedure(series.getProcedure(), service,
    // true, true));
    // dataset.setCategory(createCategory(series.getObservableProperty(),
    // service));
    // dataset.setFeature(createFeature(series.getFeatureOfInterest(), service,
    // true, true));
    // dataset.setPhenomenon(createPhenomenon(series.getObservableProperty(),
    // service, true, true));
    // dataset.setOffering(createOffering(series.getOffering(), service, true,
    // true));
    // if (series.isSetUnit()) {
    // dataset.setUnit(createUnit(series.getUnit(), service));
    // }
    // }
    // return dataset;
    // }
    //
    // private static void
    // setIdentifierNameDesxription(AbstractIdentifierNameDescriptionEntity
    // sosEntity,
    // DescribableEntity entity) {
    // entity.setDomainId(sosEntity.getIdentifier());
    // entity.setName(sosEntity.getName());
    // entity.setDescription(sosEntity.getDescription());
    // }
    //
    // private static DatasetEntity createDataset(String seriesType) {
    // if (Strings.isNullOrEmpty(seriesType)) {
    // return null;
    // }
    // switch (seriesType.toLowerCase(Locale.ROOT)) {
    // case "quantity":
    // return new QuantityDatasetEntity();
    // case "text":
    // return new TextDatasetEntity();
    // case "count":
    // return new CountDatasetEntity();
    // default:
    // break;
    // }
    // return null;
    // }
    //
    // public static RelatedFeatureEntity createRelatedFeature(RelatedFeature
    // sosRelatedFeature, ServiceEntity service) {
    // RelatedFeatureEntity relatedFeature = new RelatedFeatureEntity();
    // relatedFeature.setService(service);
    // relatedFeature.setFeature(createFeature(sosRelatedFeature.getFeatureOfInterest(),
    // service, false, false));
    // Set<OfferingEntity> offerings = new HashSet<>();
    // for (Offering offering : sosRelatedFeature.getOfferings()) {
    // offerings.add(createOffering(offering, service, false, false));
    // }
    // relatedFeature.setOfferings(offerings);
    //
    // Set<RelatedFeatureRoleEntity> relatedFeatureRoles = new HashSet<>();
    // for (RelatedFeatureRole relatedFeatureRole :
    // sosRelatedFeature.getRelatedFeatureRoles()) {
    // relatedFeatureRoles.add(createRelatedFeatureRole(relatedFeatureRole));
    // }
    // relatedFeature.setRelatedFeatureRoles(relatedFeatureRoles);
    // return relatedFeature;
    // }
    //
    // private static RelatedFeatureRoleEntity
    // createRelatedFeatureRole(RelatedFeatureRole sosRelatedFeatureRole) {
    // RelatedFeatureRoleEntity relatedFeatureRole = new
    // RelatedFeatureRoleEntity();
    // relatedFeatureRole.setRelatedFeatureRole(sosRelatedFeatureRole.getRelatedFeatureRole());
    // return relatedFeatureRole;
    // }
}
