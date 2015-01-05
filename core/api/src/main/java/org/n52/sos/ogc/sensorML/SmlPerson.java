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
package org.n52.sos.ogc.sensorML;

/**
 * Implementation for sml:Person
 * 
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class SmlPerson extends SmlContact {

    private String affiliation;

    private String email;

    private String name;

    private String phoneNumber;

    private String surname;

    private String userID;

    public SmlPerson() {
    }

    public SmlPerson(final String surname, final String name, final String userID, final String affiliation,
            final String phoneNumber, final String email) {
        this.surname = surname;
        this.name = name;
        this.userID = userID;
        this.affiliation = affiliation;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getSurname() {
        return surname;
    }

    public String getUserID() {
        return userID;
    }

    public boolean isSetAffiliation() {
        return affiliation != null && !affiliation.isEmpty();
    }

    public boolean isSetEmail() {
        return email != null && !email.isEmpty();
    }

    public boolean isSetName() {
        return name != null && !name.isEmpty();
    }

    public boolean isSetPhoneNumber() {
        return phoneNumber != null && !phoneNumber.isEmpty();
    }

    public boolean isSetSurname() {
        return surname != null && !surname.isEmpty();
    }

    public boolean isSetUserID() {
        return userID != null && !userID.isEmpty();
    }

    public SmlContact setAffiliation(final String affiliation) {
        this.affiliation = affiliation;
        return this;
    }

    public SmlContact setEmail(final String email) {
        this.email = email;
        return this;
    }

    public SmlContact setName(final String name) {
        this.name = name;
        return this;
    }

    public SmlContact setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public SmlContact setSurname(final String surname) {
        this.surname = surname;
        return this;
    }

    public SmlContact setUserID(final String userID) {
        this.userID = userID;
        return this;
    }

}
