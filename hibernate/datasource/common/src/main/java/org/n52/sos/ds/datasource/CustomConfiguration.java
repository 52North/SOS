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
package org.n52.sos.ds.datasource;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeMap;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.IdentifierGeneratorAggregator;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.mapping.AuxiliaryDatabaseObject;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.IdentifierCollection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.TableMetadata;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class CustomConfiguration extends Configuration {
    private static final long serialVersionUID = 149360549522727961L;

    /**
     * Based on
     * {@link org.hibernate.cfg.Configuration#generateDropSchemaScript(Dialect)}
     * . Rewritten to only create drop commands for existing tables/sequences.
     * 
     * 
     * @param d
     * @param m
     * 
     * @return SQL script to drop schema as String array
     * 
     * @throws HibernateException
     */
    public String[] generateDropSchemaScript(final Dialect d, final DatabaseMetadata m) throws HibernateException {
        secondPassCompile();
        final String c = getProperties().getProperty(Environment.DEFAULT_CATALOG);
        final String s = getProperties().getProperty(Environment.DEFAULT_SCHEMA);
        final List<String> script = new LinkedList<String>();
        script.addAll(generateAuxiliaryDatabaseObjectDropScript(d, c, s));
        if (d.dropConstraints()) {
            script.addAll(generateConstraintDropScript(d, c, s, m));
        }
        script.addAll(generateTableDropScript(d, c, s, m));
        script.addAll(generateIdentifierGeneratorDropScript(d, c, s, m));
        return ArrayHelper.toStringArray(script);
    }

    /**
     * Copied from
     * {@link org.hibernate.cfg.Configuration#iterateGenerators(Dialect)}.
     */
    private Iterator<PersistentIdentifierGenerator> iterateGenerators(final Dialect d, final String c, final String s)
            throws MappingException {
        final TreeMap<Object, PersistentIdentifierGenerator> generators =
                new TreeMap<Object, PersistentIdentifierGenerator>();
        for (final PersistentClass pc : classes.values()) {
            if (!pc.isInherited()) {
                final IdentifierGenerator ig =
                        pc.getIdentifier().createIdentifierGenerator(getIdentifierGeneratorFactory(), d, c, s,
                                (RootClass) pc);
                if (ig instanceof PersistentIdentifierGenerator) {
                    final PersistentIdentifierGenerator pig = (PersistentIdentifierGenerator) ig;
                    generators.put(pig.generatorKey(), pig);
                } else if (ig instanceof IdentifierGeneratorAggregator) {
                    ((IdentifierGeneratorAggregator) ig).registerPersistentGenerators(generators);
                }
            }
        }
        for (final Collection collection : collections.values()) {
            if (collection.isIdentified()) {
                final IdentifierGenerator ig =
                        ((IdentifierCollection) collection).getIdentifier().createIdentifierGenerator(
                                getIdentifierGeneratorFactory(), d, c, s, null);
                if (ig instanceof PersistentIdentifierGenerator) {
                    final PersistentIdentifierGenerator pig = (PersistentIdentifierGenerator) ig;
                    generators.put(pig.generatorKey(), pig);
                }
            }
        }

        return generators.values().iterator();
    }

    protected List<String> generateConstraintDropScript(final Dialect d, final String c, final String s,
            final DatabaseMetadata m) throws HibernateException {
        final List<String> script = new LinkedList<String>();
        final Iterator<Table> itr = getTableMappings();
        while (itr.hasNext()) {
            final Table table = itr.next();
            // TODO remove because fails if table definition is quoted
//            final String tableName = table.getQualifiedName(d, c, s);
            if (checkTable(table, m)) {
                @SuppressWarnings("unchecked")
                final Iterator<ForeignKey> subItr = table.getForeignKeyIterator();
                final TableMetadata tableMeta = m.getTableMetadata(table.getName(), s, c, table.isQuoted());
                while (subItr.hasNext()) {
                    final ForeignKey fk = subItr.next();
                    if (fk.isPhysicalConstraint() && tableMeta.getForeignKeyMetadata(fk) != null) {
                        script.add(fk.sqlDropString(d, c, s));
                    }
                }
            }
        }
        return script;
    }

    protected List<String> generateTableDropScript(final Dialect d, final String c, final String s,
            final DatabaseMetadata m) throws HibernateException {
        final List<String> script = new LinkedList<String>();
        final Iterator<Table> itr = getTableMappings();
        while (itr.hasNext()) {
            final Table table = itr.next();
            // TODO remove because fails if table definition is quoted
//            final String tableName = table.getQualifiedName(d, c, s);
            if (checkTable(table, m)) {
                script.add(table.sqlDropString(d, c, s));
            }
        }
        return script;
    }
    
    protected boolean checkTable(Table table, DatabaseMetadata m) {
        return table.isPhysicalTable() && m.isTable(table.getQuotedName());
    }

    protected List<String> generateAuxiliaryDatabaseObjectDropScript(final Dialect d, final String c, final String s) {
        final List<String> script = new LinkedList<String>();
        final ListIterator<AuxiliaryDatabaseObject> itr =
                auxiliaryDatabaseObjects.listIterator(auxiliaryDatabaseObjects.size());
        while (itr.hasPrevious()) {
            // FIXME how to check if ADO exists?
            final AuxiliaryDatabaseObject object = itr.previous();
            if (object.appliesToDialect(d)) {
                script.add(object.sqlDropString(d, c, s));
            }
        }
        return script;
    }

    protected List<String> generateIdentifierGeneratorDropScript(final Dialect d, final String c, final String s,
            final DatabaseMetadata m) throws MappingException, HibernateException {
        final List<String> script = new LinkedList<String>();
        final Iterator<PersistentIdentifierGenerator> itr = iterateGenerators(d, c, s);
        while (itr.hasNext()) {
            final PersistentIdentifierGenerator pig = itr.next();
            if (pig instanceof SequenceGenerator) {
                final SequenceGenerator sg = (SequenceGenerator) pig;
                if (!m.isSequence(sg.getSequenceName())) {
                    continue;
                }
            }
            script.addAll(Arrays.asList(pig.sqlDropStrings(d)));
        }
        return script;
    }
}
