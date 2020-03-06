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
package org.n52.sos.ds.hibernate.create;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.n52.sos.ds.hibernate.entities.AbstractIdentifierNameDescriptionEntity;
import org.n52.sos.ds.hibernate.entities.feature.AbstractMonitoringFeature;
import org.n52.sos.ds.hibernate.entities.feature.gmd.AddressEntity;
import org.n52.sos.ds.hibernate.entities.feature.gmd.ContactEntity;
import org.n52.sos.ds.hibernate.entities.feature.gmd.ExExtentEntity;
import org.n52.sos.ds.hibernate.entities.feature.gmd.ExVerticalExtentEntity;
import org.n52.sos.ds.hibernate.entities.feature.gmd.OnlineResourceEntity;
import org.n52.sos.ds.hibernate.entities.feature.gmd.ResponsiblePartyEntity;
import org.n52.sos.ds.hibernate.entities.feature.gmd.RoleEntity;
import org.n52.sos.ds.hibernate.entities.feature.gmd.TelephoneEntity;
import org.n52.sos.ds.hibernate.entities.feature.gml.CoordinateSystemAxisEntity;
import org.n52.sos.ds.hibernate.entities.feature.gml.DomainOfValidityEntity;
import org.n52.sos.ds.hibernate.entities.feature.gml.VerticalCRSEntity;
import org.n52.sos.ds.hibernate.entities.feature.gml.VerticalCSEntity;
import org.n52.sos.ds.hibernate.entities.feature.gml.VerticalDatumEntity;
import org.n52.sos.iso.gco.Role;
import org.n52.sos.iso.gmd.CiAddress;
import org.n52.sos.iso.gmd.CiContact;
import org.n52.sos.iso.gmd.CiOnlineResource;
import org.n52.sos.iso.gmd.CiResponsibleParty;
import org.n52.sos.iso.gmd.CiTelephone;
import org.n52.sos.iso.gmd.EXExtent;
import org.n52.sos.iso.gmd.EXVerticalExtent;
import org.n52.sos.iso.gmd.ScCRS;
import org.n52.sos.ogc.gml.Aggregation;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.CoordinateSystemAxis;
import org.n52.sos.ogc.gml.DomainOfValidity;
import org.n52.sos.ogc.gml.VerticalCRS;
import org.n52.sos.ogc.gml.VerticalCS;
import org.n52.sos.ogc.gml.VerticalDatum;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.w3c.Nillable;
import org.n52.sos.w3c.xlink.Reference;
import org.n52.sos.w3c.xlink.Referenceable;

public abstract class AbstractMonitoringFeatureCreator<T extends AbstractMonitoringFeature> extends AbstractFeatureOfInerestCreator<T> {

    public AbstractMonitoringFeatureCreator(int storageEPSG, int storage3depsg) {
        super(storageEPSG, storage3depsg);
    }
    
    protected void addMonitoringFeatureData(org.n52.sos.ogc.series.AbstractMonitoringFeature amp, AbstractMonitoringFeature f, Session s) {
        addVerticalDatum(amp, f, s);
        addRelatedParty(amp, f, s);
    }

    
    private void addVerticalDatum(org.n52.sos.ogc.series.AbstractMonitoringFeature amp, AbstractMonitoringFeature f, Session s) {
        if (f.hasVerticalDatum()) {
            for (VerticalDatumEntity vde : f.getVerticalDatum()) {
                amp.addVerticalDatum(createVerticalDatum(vde));
            }
        }
    }
    
    private void addRelatedParty(org.n52.sos.ogc.series.AbstractMonitoringFeature amp, AbstractMonitoringFeature f, Session s) {
        if (f.hasRelatedParty()) {
            for (ResponsiblePartyEntity rpe : f.getRelatedParty()) {
                amp.addRelatedParty(createCiResponsibleParty(rpe));
            }
        }
    }

    private Referenceable<VerticalDatum> createVerticalDatum(VerticalDatumEntity vde) {
        if (vde.isSetNilReason()) {
            return Referenceable.<VerticalDatum>of(Nillable.<VerticalDatum>nil(vde.getNilReason()));
        } else if (vde.isSetHref()) {
            Reference reference = vde.createReferenceValues();
            return  Referenceable.<VerticalDatum>of(reference);
        } else {
            List<String> scope = new ArrayList<>();
            if (vde.hasScope()) {
                scope.addAll(vde.getScope());
            }
            CodeWithAuthority identifier = getIdentifier(vde);
            VerticalDatum verticalDatum = new VerticalDatum(identifier, scope);
            verticalDatum.setGmlId("vd_" + vde.getPkid());
            if (vde.isSetName()) {
                CodeType name = new CodeType(vde.getName());
                if (vde.isSetCodespaceName()) {
                    name.setCodeSpace(vde.getCodespaceName().getCodespace());
                }
                verticalDatum.addName(name);
            }
            if (vde.isSetRemarks()) {
                verticalDatum.setRemarks(vde.getRemarks());
            }
            if (vde.isSetAnchorDefinition()) {
                CodeType anchorDefinition = new CodeType(vde.getAnchorDefinition());
                if (vde.isSetCodespaceAnchorDefinition()) {
                    anchorDefinition.setCodeSpace(vde.getCodespaceAnchorDef().getCodespace());
                }
                verticalDatum.setAnchorDefinition(anchorDefinition);
            }
            if (vde.isSetDomainOfValidity()) {
                verticalDatum.setDomainOfValidity(createDomainOfValidity(vde.getDomainOfValidity()));
            }
            if (vde.isSetRealizationEpoch()) {
                verticalDatum.setRealizationEpoch(DateTimeHelper.makeDateTime(vde.getRealizationEpoch()));
            }
            return Referenceable.<VerticalDatum>of(verticalDatum);
        }
    }

    private Referenceable<DomainOfValidity> createDomainOfValidity(DomainOfValidityEntity dov) {
        if (dov.isSetNilReason()) {
            return Referenceable.<DomainOfValidity>of(Nillable.<DomainOfValidity>nil(dov.getNilReason()));
        } else if (dov.isSetHref()) {
            Reference reference = dov.createReferenceValues();
            return Referenceable.<DomainOfValidity>of(reference);
        } else {
            DomainOfValidity domainOfValidity = new DomainOfValidity();
            if (dov.isSetExExtent()) {
                domainOfValidity.setExExtent(createExExtent(dov.getExExtent()));
            }
            return Referenceable.<DomainOfValidity>of(domainOfValidity);
        }
    }

    private EXExtent createExExtent(ExExtentEntity exe) {
        EXExtent exExtent = new EXExtent();
        if (exe.isSetId()) {
            exExtent.setId(exe.getId());
        }
        if (exe.isSetUuid()) {
            exExtent.setUuid(exe.getUuid());
        }
        if (exe.isSetDescription()) {
            exExtent.setDescription(exe.getDescription());
        }
        if (exe.hasVerticalExtent()) {
            for (ExVerticalExtentEntity exvee : exe.getVerticalExtent()) {
                exExtent.addVerticalExtent(createVerticalExtent(exvee));
            }
        }
        return exExtent;
    }

    private Referenceable<EXVerticalExtent> createVerticalExtent(ExVerticalExtentEntity exvee) {
        if (exvee.isSetNilReason()) {
           return Referenceable.<EXVerticalExtent>of(Nillable.<EXVerticalExtent>nil(exvee.getNilReason()));
        } else if (exvee.isSetHref()) {
            Reference reference = exvee.createReferenceValues();
            return Referenceable.<EXVerticalExtent>of(reference);
        } else {
            EXVerticalExtent exVerticalExtent = new EXVerticalExtent();
            // min value
            if (exvee.isSetMinimumValue()) {
                exVerticalExtent.setMinimumValue(Nillable.of(exvee.getMinimumValue()));
            } else if (exvee.isSetMinValuNilReason()) {
                exVerticalExtent.setMinimumValue(Nillable.<Double>nil(exvee.getMinValuNilReason()));
            } else {
                exVerticalExtent.setMinimumValue(Nillable.<Double>missing());
            }
            // max value
            if (exvee.isSetMaximumValue()) {
                exVerticalExtent.setMaximumValue(Nillable.of(exvee.getMaximumValue()));
            } else if (exvee.isSetMaxValuNilReason()) {
                exVerticalExtent.setMaximumValue(Nillable.<Double>nil(exvee.getMaxValuNilReason()));
            } else {
                exVerticalExtent.setMaximumValue(Nillable.<Double>missing());
            }
            if (exvee.isSetVerticalCRS()) {
                VerticalCRSEntity vcrs = exvee.getVerticalCRS();
                if (vcrs.isSetNilReason()) {
                    exVerticalExtent.setVerticalCRS(Referenceable.<ScCRS>of(Nillable.<ScCRS>nil(vcrs.getNilReason())));
                } else if (vcrs.isSetHref()) {
                    Reference reference = vcrs.createReferenceValues();
                    exVerticalExtent.setVerticalCRS(Referenceable.<ScCRS>of(reference));
                } else {
                    exVerticalExtent.setVerticalCRS(createVerticalCRS(vcrs));
                }
            }
            return Referenceable.<EXVerticalExtent>of(exVerticalExtent);
        }
    }

    private Referenceable<ScCRS> createVerticalCRS(VerticalCRSEntity vcrs) {
        if (vcrs.isSetNilReason()) {
            return Referenceable.<ScCRS>of(Nillable.<ScCRS>nil(vcrs.getNilReason()));
        } else if (vcrs.isSetHref()) {
            Reference reference = vcrs.createReferenceValues();
            return Referenceable.<ScCRS>of(reference);
        } else {
            List<String> scope = new ArrayList<>();
            if (vcrs.hasScope()) {
                scope.addAll(vcrs.getScope());
            }
            Referenceable<VerticalCS> verticalCS;
            if (vcrs.isSetVerticalCS()) {
                verticalCS = createVerticalCS(vcrs.getVerticalCS());
            } else {
                verticalCS = Referenceable.<VerticalCS>of(Nillable.<VerticalCS>missing());
            }
            Referenceable<VerticalDatum> verticalDatum;
            if (vcrs.isSetVerticalDatum()) {
                verticalDatum = createVerticalDatum(vcrs.getVerticalDatum());
            } else {
                verticalDatum = Referenceable.<VerticalDatum>of(Nillable.<VerticalDatum>missing());
            }
            VerticalCRS verticalCRS = new VerticalCRS(getIdentifier(vcrs), scope, verticalCS, verticalDatum);
            verticalCRS.setGmlId("vcrs_" + vcrs.getPkid());
            if (vcrs.isSetRemarks()) {
                verticalCRS.setRemarks(vcrs.getRemarks());
            }
            if (vcrs.hasDomainOfValidity()) {
                for (DomainOfValidityEntity dov : vcrs.getDomainOfValidity()) {
                    verticalCRS.addDomainOfValidity(createDomainOfValidity(dov));
                }
            }
            return Referenceable.<ScCRS>of(new ScCRS().setAbstractCrs(verticalCRS));
        }
    }

    private Referenceable<VerticalCS> createVerticalCS(VerticalCSEntity vcs) {
        if (vcs.isSetNilReason()) {
            return Referenceable.<VerticalCS>of(Nillable.<VerticalCS>nil(vcs.getNilReason()));
        } else if (vcs.isSetHref()) {
            Reference reference = vcs.createReferenceValues();
            return Referenceable.<VerticalCS>of(reference);
        } else {
            List<Referenceable<CoordinateSystemAxis>> coordinateSystemAxis = new ArrayList<>();;
            if (vcs.hasCoordinateSystemAxis()) {
                for (CoordinateSystemAxisEntity csae : vcs.getCoordinateSystemAxis()) {
                    coordinateSystemAxis.add(createCoordinateSystemAxis(csae));
                }
            } else {
                coordinateSystemAxis.add(Referenceable.<CoordinateSystemAxis>of(Nillable.<CoordinateSystemAxis>missing()));
            }
            VerticalCS verticalCS = new VerticalCS(getIdentifier(vcs), coordinateSystemAxis);
            verticalCS.setGmlId("vcs_" + vcs.getPkid());
            if (vcs.isSetRemarks()) {
                verticalCS.setRemarks(vcs.getRemarks());
            }
            if (vcs.isSetAggregation()) {
                verticalCS.setAggregation(Aggregation.valueOf(vcs.getAggregation()));
            }
            return Referenceable.<VerticalCS>of(verticalCS);
        }
    }

    
    private Referenceable<CoordinateSystemAxis> createCoordinateSystemAxis(CoordinateSystemAxisEntity csae) {
        if (csae.isSetNilReason()) {
            return Referenceable.<CoordinateSystemAxis>of(Nillable.<CoordinateSystemAxis>nil(csae.getNilReason()));
        } else if (csae.isSetHref()) {
            return Referenceable.<CoordinateSystemAxis>of(csae.createReferenceValues());
        } else {
            CodeType axisAbbrev = new CodeType(csae.getAxisAbbrev());
            if (csae.isSetCodespaceAxisAbbrev()) {
                axisAbbrev.setCodeSpace(csae.getCodespaceAxisAbbrev().getCodespace());
            }
            CodeWithAuthority axisDirection = new CodeWithAuthority(csae.getAxisDirection());
            if (csae.isSetCodespaceAxisDirection()) {
                axisDirection.setCodeSpace(csae.getCodespaceAxisDirection().getCodespace());
            }
            String uom = csae.getUom() != null ? csae.getUom().getUnit() : "unknown";
            CoordinateSystemAxis coordinateSystemAxis = new CoordinateSystemAxis(getIdentifier(csae), axisAbbrev, axisDirection, uom);
            coordinateSystemAxis.setGmlId("csa" + csae.getPkid());
            if (csae.isSetRemarks()) {
                coordinateSystemAxis.setRemarks(csae.getRemarks());
            }
            if (csae.isSetMinimumValue()) {
                coordinateSystemAxis.setMinimumValue(csae.getMinimumValue());
            }
            if (csae.isSetMaximumValue()) {
                coordinateSystemAxis.setMaximumValue(csae.getMaximumValue());
            }
            if (csae.isSetRangeMeaning()) {
                CodeWithAuthority rangeMeaning = new CodeWithAuthority(csae.getRangeMeaning());
                if (csae.isSetCodespaceRangeMeaning()) {
                    rangeMeaning.setCodeSpace(csae.getCodespaceRangeMeaning().getCodespace());
                }
                coordinateSystemAxis.setRangeMeaning(rangeMeaning);
            }
            return Referenceable.<CoordinateSystemAxis>of(coordinateSystemAxis);
        }
    }

    private CodeWithAuthority getIdentifier(AbstractIdentifierNameDescriptionEntity ainde) {
        if (ainde.isSetIdentifier()) {
            CodeWithAuthority identifier = new CodeWithAuthority(ainde.getIdentifier());
            if (ainde.isSetCodespace()) {
                identifier.setCodeSpace(ainde.getCodespace().getCodespace());
            }
            return identifier;
        } else {
            return new CodeWithAuthority("unknown");
        }
    }

    private Referenceable<CiResponsibleParty> createCiResponsibleParty(ResponsiblePartyEntity rpe) {
        if (rpe.isSetNilReason()) {
            return Referenceable.<CiResponsibleParty>of(Nillable.<CiResponsibleParty>nil(rpe.getNilReason()));
        } else if (rpe.isSetHref()) {
            return Referenceable.<CiResponsibleParty>of(rpe.createReferenceValues());
        } else {
            Nillable<Role> role = rpe.getCiRole() != null ? createRole(rpe.getCiRole()) : Nillable.<Role>of(new Role(""));
            CiResponsibleParty responsibleParty = new CiResponsibleParty(role);
            if (rpe.isSetId()) {
                responsibleParty.setId(rpe.getId());
            }
            if (rpe.isSetUuid()) {
                responsibleParty.setUuid(rpe.getUuid());
            }
            if (rpe.isSetIndividualName()) {
                responsibleParty.setIndividualName(rpe.getIndividualName());
            }
            if (rpe.isSetOrganizationName()) {
                responsibleParty.setOrganizationName(rpe.getOrganizationName());
            }
            if (rpe.isSetPositionName()) {
                responsibleParty.setPositionName(rpe.getPositionName());
            }
            if (rpe.isSetContactInfo()) {
                responsibleParty.setContactInfo(createContactInfo(rpe.getContactInfo()));
            }
            
            return Referenceable.<CiResponsibleParty>of(responsibleParty);
        }
    }

    private Referenceable<CiContact> createContactInfo(ContactEntity ce) {
        if (ce.isSetNilReason()) {
            return Referenceable.<CiContact>of(Nillable.<CiContact>nil(ce.getNilReason()));
        } else if (ce.isSetHref()) {
            return Referenceable.<CiContact>of(ce.createReferenceValues());
        } else {
            CiContact contact = new CiContact();
            if (ce.isSetPhone()) {
                contact.setPhone(createPhone(ce.getPhone()));
            } else {
                contact.setPhone(Referenceable.<CiTelephone>of(Nillable.<CiTelephone>missing()));
            }
            if (ce.isSetAddress()) {
                contact.setAddress(createAddress(ce.getAddress()));
            } else {
                contact.setAddress(Referenceable.<CiAddress>of(Nillable.<CiAddress>missing()));
            }
            if (ce.isSetOnlineResource()) {
                contact.setOnlineResource(createOnlineResource(ce.getOnlineResource()));
            } else {
                contact.setOnlineResource(Referenceable.<CiOnlineResource>of(Nillable.<CiOnlineResource>missing()));
            }
            if (ce.isSetHoursOfService()) {
                contact.setHoursOfService(Nillable.<String>of(ce.getHoursOfService())); 
            } else {
                contact.setHoursOfService(Nillable.<String>missing()); 
            }
            if (ce.isSetContactInstructions()) {
                contact.setContactInstructions(Nillable.<String>of(ce.getContactInstructions()));
            } else {
                contact.setContactInstructions(Nillable.<String>missing());
            }
            return Referenceable.<CiContact>of(contact);
        }
    }

    private Referenceable<CiTelephone> createPhone(TelephoneEntity te) {
        if (te.isSetNilReason()) {
            return Referenceable.<CiTelephone>of(Nillable.<CiTelephone>nil(te.getNilReason()));
        } else if (te.isSetHref()) {
            return Referenceable.<CiTelephone>of(te.createReferenceValues());
        } else {
            CiTelephone telephone = new CiTelephone();
            if (te.isSetId()) {
                telephone.setId(te.getId());
            }
            if (te.isSetUuid()) {
                telephone.setUuid(te.getUuid());
            }
            if (te.hasVoice()) {
                telephone.setVoice(te.getVoice());
            }
            if (te.hasFacsimile()) {
                telephone.setFacsimile(te.getFacsimile());
            }
            return Referenceable.<CiTelephone>of(telephone);
        }
    }

    private Referenceable<CiAddress> createAddress(AddressEntity ae) {
        if (ae.isSetNilReason()) {
            return Referenceable.<CiAddress>of(Nillable.<CiAddress>nil(ae.getNilReason()));
        } else if (ae.isSetHref()) {
            return Referenceable.<CiAddress>of(ae.createReferenceValues());
        } else {
            CiAddress address = new CiAddress();
            if (ae.isSetId()) {
                address.setId(ae.getId());
            }
            if (ae.isSetUuid()) {
                address.setUuid(ae.getUuid());
            }
            if (ae.hasSetDeliveryPoint()) {
                address.setDeliveryPoints(ae.getDeliveryPoint());
            }
            if (ae.isSetCity()){
                address.setCity(ae.getCity());
            }
            if (ae.isSetAdministrativeArea()) {
                address.setAdministrativeArea(ae.getAdministrativeArea());
            }
            if (ae.isSetPostalCode()) {
                address.setPostalCode(ae.getPostalCode());
            }
            if (ae.isSetCountry()) {
                address.setCountry(ae.getCountry());
            }
            if (ae.hasElectronicMailAddress()) {
                address.setElectronicMailAddresses(ae.getElectronicMailAddress());
            }
            return Referenceable.<CiAddress>of(address);
        }
    }

    private Referenceable<CiOnlineResource> createOnlineResource(OnlineResourceEntity ore) {
        if (ore.isSetNilReason()) {
            return Referenceable.<CiOnlineResource>of(Nillable.<CiOnlineResource>nil(ore.getNilReason()));
        } else if (ore.isSetHref()) {
            return Referenceable.<CiOnlineResource>of(ore.createReferenceValues());
        } else {
            Nillable<URI> linkage = null;
            try {
                linkage = ore.isSetLinkage() ? Nillable.<URI>of(new URI(ore.getLinkage())) : Nillable.<URI>missing();
            } catch (URISyntaxException e) {
                
            }
            CiOnlineResource onlineResource = new CiOnlineResource(linkage);
            if (ore.isSetId()) {
                onlineResource.setId(ore.getId());
            }
            if (ore.isSetUuid()) {
                onlineResource.setUuid(ore.getUuid());
            }
            if (ore.isSetProtocol()) {
                onlineResource.setProtocol(Nillable.<String>of(ore.getProtocol()));
            } else {
                onlineResource.setProtocol(Nillable.<String>missing());
            }
            if (ore.isSetApplicationProfile()) {
                onlineResource.setApplicationProfile(ore.getApplicationProfile());
            }
            if (ore.isSetName()) {
                onlineResource.setName(ore.getName());
            }
            if (ore.isSetDescription()) {
                onlineResource.setDescription(ore.getDescription());
            }
            if (ore.isSetFunction()) {
                onlineResource.setFunction(ore.getFunction());
            }
            return Referenceable.<CiOnlineResource>of(onlineResource);
        }
    }

    private Nillable<Role> createRole(RoleEntity cir) {
        Role role = new Role(cir.getCodeListValue());
        role.setCodeList(cir.getCodeList());
        role.setCodeListValue(cir.getCodeListValue());
        return Nillable.<Role>of(role);
    }
}
