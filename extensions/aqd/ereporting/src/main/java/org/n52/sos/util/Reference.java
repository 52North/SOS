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
package org.n52.sos.util;

import java.net.URI;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Strings;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class Reference {

    private URI href;
    private Optional<String> type = Optional.absent();
    private Optional<String> role = Optional.absent();
    private Optional<String> arcrole = Optional.absent();
    private Optional<String> title = Optional.absent();
    private Optional<String> show = Optional.absent();
    private Optional<String> actuate = Optional.absent();
    private Optional<String> remoteSchema = Optional.absent();

    public Optional<String> getType() {
        return type;
    }

    public Reference setType(String type) {
        this.type = Optional.fromNullable(Strings.emptyToNull(type));
        return this;
    }

    public URI getHref() {
        return href;
    }

    public Reference setHref(URI href) {
        this.href = href;
        return this;

    }

    public Optional<String> getRole() {
        return role;
    }

    public Reference setRole(String role) {
        this.role = Optional.fromNullable(Strings.emptyToNull(role));
        return this;
    }

    public Optional<String> getArcrole() {
        return arcrole;
    }

    public Reference setArcrole(String arcrole) {
        this.arcrole = Optional.fromNullable(Strings.emptyToNull(arcrole));
        return this;
    }

    public Optional<String> getTitle() {
        return title;
    }

    public Reference setTitle(String title) {
        this.title = Optional.fromNullable(Strings.emptyToNull(title));
        return this;
    }

    public Optional<String> getShow() {
        return show;
    }

    public Reference setShow(String show) {
        this.show = Optional.fromNullable(Strings.emptyToNull(show));
        return this;
    }

    public Optional<String> getActuate() {
        return actuate;
    }

    public Reference setActuate(String actuate) {
        this.actuate = Optional.fromNullable(Strings.emptyToNull(actuate));
        return this;
    }

    public Optional<String> getRemoteSchema() {
        return remoteSchema;
    }

    public Reference setRemoteSchema(String remoteSchema) {
        this.remoteSchema = Optional.fromNullable(Strings.emptyToNull(remoteSchema));
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getActuate(), getArcrole(), getHref(),
            getRemoteSchema(), getRole(), getShow(), getTitle(), getType());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Reference) {
            Reference that = (Reference) obj;
            return Objects.equal(getActuate(), that.getActuate())&&
                   Objects.equal(getArcrole(), that.getArcrole()) &&
                   Objects.equal(getHref(), that.getHref()) &&
                   Objects.equal(getRemoteSchema(), that.getRemoteSchema()) &&
                   Objects.equal(getRole(), that.getRole()) &&
                   Objects.equal(getShow(), that.getShow()) &&
                   Objects.equal(getTitle(), that.getTitle()) &&
                   Objects.equal(getType(), that.getType());
        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .omitNullValues()
                .add("actuate", getActuate().orNull())
                .add("arcrole", getArcrole().orNull())
                .add("href", getHref())
                .add("remoteSchema", getRemoteSchema().orNull())
                .add("role", getRole().orNull())
                .add("show", getShow().orNull())
                .add("title", getTitle().orNull())
                .add("type", getType().orNull())
                .toString();
    }



}
