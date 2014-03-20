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
/**

 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.

 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH

 *

 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:

 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.

 *

 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0

 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:

 *

 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.

 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0

 *

 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.

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
package org.n52.sos.ds.hibernate.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasDeletedFlag;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasFeatureOfInterestGetter;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasObservablePropertyGetter;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasOfferings;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasProcedureGetter;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasUnit;
import org.n52.sos.util.StringHelper;

/**
 * Abstract Hibernate Observation entity class. Contains the default
 * getter/setter methods and constants for Criteria creation.
 * 
 * @since 4.0.0
 * 
 */
public abstract class AbstractObservation extends AbstractIdentifierNameDescriptionEntity implements HasDeletedFlag, HasObservablePropertyGetter,
        HasProcedureGetter, HasFeatureOfInterestGetter, HasOfferings,  HasUnit {

    private static final long serialVersionUID = 8860291031549060409L;

    public static final String ID = "observationId";

    public static final String PHENOMENON_TIME_START = "phenomenonTimeStart";

    public static final String PHENOMENON_TIME_END = "phenomenonTimeEnd";

    public static final String VALID_TIME_START = "validTimeStart";

    public static final String VALID_TIME_END = "validTimeEnd";

    public static final String RESULT_TIME = "resultTime";

    private long observationId;

    private Date phenomenonTimeStart;

    private Date phenomenonTimeEnd;

    private Date resultTime;

    private boolean deleted;

    private Date validTimeStart;

    private Date validTimeEnd;

    private Unit unit;

    private Set<Offering> offerings = new HashSet<Offering>(0);
    
    public long getObservationId() {
        return observationId;
    }

    public void setObservationId(final long observationId) {
        this.observationId = observationId;
    }


    public Date getPhenomenonTimeStart() {
        return phenomenonTimeStart;
    }

    public void setPhenomenonTimeStart(final Date phenomenonTimeStart) {
        this.phenomenonTimeStart = phenomenonTimeStart;
    }

    public Date getPhenomenonTimeEnd() {
        return phenomenonTimeEnd;
    }

    public void setPhenomenonTimeEnd(final Date phenomenonTimeEnd) {
        this.phenomenonTimeEnd = phenomenonTimeEnd;
    }

    public Date getResultTime() {
        return resultTime;
    }

    public void setResultTime(final Date resultTime) {
        this.resultTime = resultTime;
    }

    public Date getValidTimeStart() {
        return validTimeStart;
    }

    public void setValidTimeStart(final Date validTimeStart) {
        this.validTimeStart = validTimeStart;
    }

    public Date getValidTimeEnd() {
        return validTimeEnd;
    }

    public void setValidTimeEnd(final Date validTimeEnd) {
        this.validTimeEnd = validTimeEnd;
    }

    @Override
    public Unit getUnit() {
        return unit;
    }

    @Override
    public void setUnit(final Unit unit) {
        this.unit = unit;
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
            getOfferings().add((Offering)offerings);
        }
    }
    
//    
//    @Override
//    public Offering getOffering() {
//        return this.offering;
//    }
//    
//    @Override
//    public void setOfferings(final Offering offering) {
//        if (getOfferings() == null)  {
//            setOfferings( new HashSet<Offering>(0));
//        }
//        getOfferings().add(offering);
//        this.offering = offering;
//    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }
    
    @Override
    public boolean getDeleted() {
        return deleted;
    }

    @Override
    public AbstractObservation setDeleted(final boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public boolean isSetIdentifier() {
        return getIdentifier() != null && !getIdentifier().isEmpty();
    }

    @Override
    public boolean isSetCodespace() {
        return getCodespace() != null && getCodespace().isSetCodespace();
    }

    public boolean isSetUnit() {
        return getUnit() != null && getUnit().isSetUnit();
    }
    
    @Override
    public boolean isSetDescription() {
        return StringHelper.isNotEmpty(getDescription());
    }
    
//    public abstract class AbstractObservation implements HasIdentifier, HasDeletedFlag, HasFeatureOfInterestGetter,
//    HasObservablePropertyGetter, HasProcedureGetter, HasOfferings, HasCodespace, HasUnit {
//
//public static final String ID = "observationId";
//
//public static final String PHENOMENON_TIME_START = "phenomenonTimeStart";
//
//public static final String PHENOMENON_TIME_END = "phenomenonTimeEnd";
//
//public static final String VALID_TIME_START = "validTimeStart";
//
//public static final String VALID_TIME_END = "validTimeEnd";
//
//public static final String RESULT_TIME = "resultTime";
//
//public static final String DESCRIPTION = "description";
//
//private long observationId;
//
//private Date phenomenonTimeStart;
//
//private Date phenomenonTimeEnd;
//
//private Date resultTime;
//
//private String identifier;
//
//private Codespace codespace;
//
//private String description;
//
//private boolean deleted;
//
//private Date validTimeStart;
//
//private Date validTimeEnd;
//
//private Unit unit;
//
//private Set<Offering> offerings = new HashSet<Offering>(0);
//
///**
// * Get observation id
// * 
// * @return observation id
// */
//public long getObservationId() {
//    return observationId;
//}
//
///**
// * Set observation id
// * 
// * @param observationId
// *            Observation id to set
// */
//public void setObservationId(final long observationId) {
//    this.observationId = observationId;
//}
//
//@Override
//public String getIdentifier() {
//    return identifier;
//}
//
//@Override
//public AbstractObservation setIdentifier(final String identifier) {
//    this.identifier = identifier;
//    return this;
//}
//
//@Override
//public Codespace getCodespace() {
//    return codespace;
//}
//
//@Override
//public void setCodespace(final Codespace codespace) {
//    this.codespace = codespace;
//}
//
//public String getDescription() {
//    return description;
//}
//
//public AbstractObservation setDescription(final String description) {
//    this.description = description;
//    return this;
//}
//
//public Date getPhenomenonTimeStart() {
//    return phenomenonTimeStart;
//}
//
//public void setPhenomenonTimeStart(final Date phenomenonTimeStart) {
//    this.phenomenonTimeStart = phenomenonTimeStart;
//}
//
//public Date getPhenomenonTimeEnd() {
//    return phenomenonTimeEnd;
//}
//
//public void setPhenomenonTimeEnd(final Date phenomenonTimeEnd) {
//    this.phenomenonTimeEnd = phenomenonTimeEnd;
//}
//
//public Date getResultTime() {
//    return resultTime;
//}
//
//public void setResultTime(final Date resultTime) {
//    this.resultTime = resultTime;
//}
//
//public Date getValidTimeStart() {
//    return validTimeStart;
//}
//
//public void setValidTimeStart(final Date validTimeStart) {
//    this.validTimeStart = validTimeStart;
//}
//
//public Date getValidTimeEnd() {
//    return validTimeEnd;
//}
//
//public void setValidTimeEnd(final Date validTimeEnd) {
//    this.validTimeEnd = validTimeEnd;
//}
//
//@Override
//public Unit getUnit() {
//    return unit;
//}
//
//@Override
//public void setUnit(final Unit unit) {
//    this.unit = unit;
//}
//
//@Override
//public Set<Offering> getOfferings() {
//    return offerings;
//}
//
//@SuppressWarnings("unchecked")
//@Override
//public void setOfferings(final Object offerings) {
//    if (offerings instanceof Set<?>) {
//        this.offerings = (Set<Offering>) offerings;
//    } else {
//        getOfferings().add((Offering) offerings);
//    }
//}
//
//@Override
//public boolean isDeleted() {
//    return deleted;
//}
//
//@Override
//public AbstractObservation setDeleted(final boolean deleted) {
//    this.deleted = deleted;
//    return this;
//}
//
///**
// * Is identifier set
// * 
// * @return <code>true</code>, if identifier is set
// */
//public boolean isSetIdentifier() {
//    return getIdentifier() != null && !getIdentifier().isEmpty();
//}
//
//@Override
//public boolean isSetCodespace() {
//    return getCodespace() != null && getCodespace().isSetCodespace();
//}
//
///**
// * Is description set
// * 
// * @return <code>true</code>, if description is set
// */
//public boolean isSetDescription() {
//    return getDescription() != null && !getDescription().isEmpty();
//}
//
///**
// * Is unit set
// * 
// * @return <code>true</code>, if unit is set
// */
//public boolean isSetUnit() {
//    return getUnit() != null && getUnit().isSetUnit();
//}
}
