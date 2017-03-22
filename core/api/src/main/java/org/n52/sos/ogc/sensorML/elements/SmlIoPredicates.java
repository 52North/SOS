/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ogc.sensorML.elements;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class SmlIoPredicates {

    private SmlIoPredicates() {
    }

    public static Predicate<SmlIo<?>> identifier(String identifier) {
        return new IdentifierPredicate(identifier);
    }
    
    public static Predicate<SmlIo<?>> name(String name) {
        return new NamePredicate(name);
    }

    public static Predicate<SmlIo<?>> definition(String definition) {
        return new DefinitionPredicate(definition);
    }

    public static Predicate<SmlIo<?>> nameOrDefinition(String name,
                                                            String definition) {
        return Predicates.or(name(name), definition(definition));
    }
    
    @SuppressWarnings("unchecked")
    public static Predicate<SmlIo<?>> identifierOrNameOrDefinition(String identifier, String name, String definition) {
        return Predicates.or(identifier(identifier), name(name), definition(definition));
    }

    public static Predicate<SmlIo<?>> nameAndDefinition(String name,
                                                             String definition) {
        return Predicates.and(name(name), definition(definition));
    }

    private static class DefinitionPredicate implements Predicate<SmlIo<?>> {
        private final String definition;

        DefinitionPredicate(String definition) {
            this.definition = definition;
        }

        @Override
        public boolean apply(SmlIo<?> input) {
            return input.getIoValue().isSetDefinition() &&
                   input.getIoValue().getDefinition().equalsIgnoreCase(definition);
        }
    }

    private static class NamePredicate implements Predicate<SmlIo<?>> {
        private final String name;

        NamePredicate(String name) {
            this.name = name;
        }

        @Override
        public boolean apply(SmlIo<?> input) {
            return (input.isSetName() && input.getIoName().equalsIgnoreCase(name)) || (input.getIoValue().isSetName()
                    && input.getIoValue().getName().getValue().equalsIgnoreCase(name));
        }
    }
    
    private static class IdentifierPredicate implements Predicate<SmlIo<?>> {
        private final String identifier;

        IdentifierPredicate(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public boolean apply(SmlIo<?> input) {
            return input.isSetHref() || (input.getIoValue().isSetIdentifier()
                    && input.getIoValue().getIdentifier().equalsIgnoreCase(identifier));
        }
    }
}
