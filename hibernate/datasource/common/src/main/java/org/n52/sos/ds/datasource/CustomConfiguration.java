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
package org.n52.sos.ds.datasource;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

/**
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public class CustomConfiguration extends Configuration {
    private static final long serialVersionUID = 149360549522727961L;

    // private transient Mapping mappingCC = buildMapping();
    //
    // @SuppressWarnings({ "unchecked" })
    // @Override
    // public String[] generateSchemaCreationScript(Dialect dialect) throws
    // HibernateException {
    // secondPassCompile();
    //
    // ArrayList<String> script = new ArrayList<>(50);
    // String defaultCatalog =
    // getProperties().getProperty(Environment.DEFAULT_CATALOG);
    // String defaultSchema =
    // getProperties().getProperty(Environment.DEFAULT_SCHEMA);
    //
    // Iterator<Table> iter = getTableMappings();
    // while (iter.hasNext()) {
    // Table table = iter.next();
    // if (table.isPhysicalTable()) {
    // script.add(table.sqlCreateString(dialect, mappingCC, defaultCatalog,
    // defaultSchema));
    // Iterator<String> comments = table.sqlCommentStrings(dialect,
    // defaultCatalog, defaultSchema);
    // while (comments.hasNext()) {
    // script.add(comments.next());
    // }
    // }
    // }
    //
    // iter = getTableMappings();
    // while (iter.hasNext()) {
    // Table table = iter.next();
    // if (table.isPhysicalTable()) {
    //
    // Iterator<UniqueKey> subIterUK = table.getUniqueKeyIterator();
    // while (subIterUK.hasNext()) {
    // UniqueKey uk = subIterUK.next();
    // String constraintString = uk.sqlCreateString(dialect, mappingCC,
    // defaultCatalog, defaultSchema);
    // if (constraintString != null) {
    // script.add(constraintString);
    // }
    // }
    //
    // Iterator<Index> subIterIdx = table.getIndexIterator();
    // while (subIterIdx.hasNext()) {
    // Index index = subIterIdx.next();
    // if (checkIndexForGeometry(index, dialect)) {
    // if (dialect instanceof SpatialIndexDialect) {
    // script.add((((SpatialIndexDialect)dialect).buildSqlCreateSpatialIndexString(index,
    // defaultCatalog, defaultSchema)));
    // }
    // } else {
    // script.add(index.sqlCreateString(dialect, mappingCC, defaultCatalog,
    // defaultSchema));
    // }
    // }
    // }
    // }
    //
    // // Foreign keys must be created *after* unique keys for numerous DBs.
    // // See HH-8390.
    // iter = getTableMappings();
    // while (iter.hasNext()) {
    // Table table = iter.next();
    // if (table.isPhysicalTable()) {
    //
    // if (dialect.hasAlterTable()) {
    // Iterator<ForeignKey> subIterFK = table.getForeignKeyIterator();
    // while (subIterFK.hasNext()) {
    // ForeignKey fk = subIterFK.next();
    // if (fk.isPhysicalConstraint()) {
    // script.add(fk.sqlCreateString(dialect, mappingCC, defaultCatalog,
    // defaultSchema));
    // }
    // }
    // }
    //
    // }
    // }
    //
    // Iterator<IdentifierGenerator> iterG = iterateGenerators(dialect);
    // while (iterG.hasNext()) {
    // String[] lines = ((PersistentIdentifierGenerator)
    // iterG.next()).sqlCreateStrings(dialect);
    // script.addAll(Arrays.asList(lines));
    // }
    //
    // auxiliaryDatabaseObjects.stream()
    // .filter(auxiliaryDatabaseObject ->
    // auxiliaryDatabaseObject.appliesToDialect(dialect))
    // .forEachOrdered(auxiliaryDatabaseObject ->
    // script.add(auxiliaryDatabaseObject.sqlCreateString(dialect, mappingCC,
    // defaultCatalog, defaultSchema))
    // );
    //
    // return ArrayHelper.toStringArray(script);
    // }
    //
    // @SuppressWarnings("unchecked")
    // @Override
    // public List<SchemaUpdateScript> generateSchemaUpdateScriptList(Dialect
    // dialect, DatabaseMetadata databaseMetadata)
    // throws HibernateException {
    // secondPassCompile();
    //
    // String defaultCatalog =
    // getProperties().getProperty(Environment.DEFAULT_CATALOG);
    // String defaultSchema =
    // getProperties().getProperty(Environment.DEFAULT_SCHEMA);
    // UniqueConstraintSchemaUpdateStrategy constraintMethod =
    // UniqueConstraintSchemaUpdateStrategy
    // .interpret(getProperties().get(Environment.UNIQUE_CONSTRAINT_SCHEMA_UPDATE_STRATEGY));
    //
    // List<SchemaUpdateScript> scripts = new ArrayList<>();
    //
    // Iterator<Table> iter = getTableMappings();
    // while (iter.hasNext()) {
    // Table table = iter.next();
    // String tableSchema = (table.getSchema() == null) ? defaultSchema :
    // table.getSchema();
    // String tableCatalog = (table.getCatalog() == null) ? defaultCatalog :
    // table.getCatalog();
    // if (table.isPhysicalTable()) {
    //
    // TableMetadata tableInfo =
    // databaseMetadata.getTableMetadata(table.getName(), tableSchema,
    // tableCatalog,
    // table.isQuoted());
    // if (tableInfo == null) {
    // scripts.add(new SchemaUpdateScript(
    // table.sqlCreateString(dialect, mappingCC, tableCatalog, tableSchema),
    // false));
    // } else {
    // Iterator<String> subiter =
    // table.sqlAlterStrings(dialect, mappingCC, tableInfo, tableCatalog,
    // tableSchema);
    // while (subiter.hasNext()) {
    // scripts.add(new SchemaUpdateScript(subiter.next(), false));
    // }
    // }
    //
    // Iterator<String> comments = table.sqlCommentStrings(dialect,
    // defaultCatalog, defaultSchema);
    // while (comments.hasNext()) {
    // scripts.add(new SchemaUpdateScript(comments.next(), false));
    // }
    //
    // }
    // }
    //
    // iter = getTableMappings();
    // while (iter.hasNext()) {
    // Table table = iter.next();
    // String tableSchema = (table.getSchema() == null) ? defaultSchema :
    // table.getSchema();
    // String tableCatalog = (table.getCatalog() == null) ? defaultCatalog :
    // table.getCatalog();
    // if (table.isPhysicalTable()) {
    //
    // TableMetadata tableInfo =
    // databaseMetadata.getTableMetadata(table.getName(), tableSchema,
    // tableCatalog,
    // table.isQuoted());
    //
    // if (!constraintMethod.equals(UniqueConstraintSchemaUpdateStrategy.SKIP))
    // {
    // Iterator<UniqueKey> uniqueIter = table.getUniqueKeyIterator();
    // while (uniqueIter.hasNext()) {
    // final UniqueKey uniqueKey = uniqueIter.next();
    // // Skip if index already exists. Most of the time, this
    // // won't work since most Dialects use Constraints.
    // // However,
    // // keep it for the few that do use Indexes.
    // if (tableInfo != null && !Strings.isNullOrEmpty(uniqueKey.getName())) {
    // final IndexMetadata meta =
    // tableInfo.getIndexMetadata(uniqueKey.getName());
    // if (meta != null) {
    // continue;
    // }
    // }
    // String constraintString =
    // uniqueKey.sqlCreateString(dialect, mappingCC, tableCatalog, tableSchema);
    // if (constraintString != null && !constraintString.isEmpty() &&
    // constraintMethod
    // .equals(UniqueConstraintSchemaUpdateStrategy.DROP_RECREATE_QUIETLY)) {
    // String constraintDropString = uniqueKey.sqlDropString(dialect,
    // tableCatalog, tableSchema);
    // scripts.add(new SchemaUpdateScript(constraintDropString, true));
    // }
    // scripts.add(new SchemaUpdateScript(constraintString, true));
    // }
    // }
    //
    // Iterator<Index> subIter = table.getIndexIterator();
    // while (subIter.hasNext()) {
    // final Index index = subIter.next();
    // // Skip if index already exists
    // if (tableInfo != null && !Strings.isNullOrEmpty(index.getName())) {
    // final IndexMetadata meta = tableInfo.getIndexMetadata(index.getName());
    // if (meta != null) {
    // continue;
    // }
    // }
    // if (checkIndexForGeometry(index, dialect)) {
    // if (dialect instanceof SpatialIndexDialect) {
    // scripts.add(new SchemaUpdateScript(((SpatialIndexDialect) dialect)
    // .buildSqlCreateSpatialIndexString(index, tableCatalog, tableSchema),
    // false));
    // }
    // } else {
    // scripts.add(new SchemaUpdateScript(
    // index.sqlCreateString(dialect, mappingCC, tableCatalog, tableSchema),
    // false));
    // }
    // }
    // }
    // }
    //
    // // Foreign keys must be created *after* unique keys for numerous DBs.
    // // See HH-8390.
    // iter = getTableMappings();
    // while (iter.hasNext()) {
    // Table table = iter.next();
    // String tableSchema = (table.getSchema() == null) ? defaultSchema :
    // table.getSchema();
    // String tableCatalog = (table.getCatalog() == null) ? defaultCatalog :
    // table.getCatalog();
    // if (table.isPhysicalTable()) {
    //
    // TableMetadata tableInfo =
    // databaseMetadata.getTableMetadata(table.getName(), tableSchema,
    // tableCatalog,
    // table.isQuoted());
    //
    // if (dialect.hasAlterTable()) {
    // Iterator<ForeignKey> subIter = table.getForeignKeyIterator();
    // while (subIter.hasNext()) {
    // ForeignKey fk = subIter.next();
    // if (fk.isPhysicalConstraint()) {
    // boolean create = tableInfo == null ||
    // (tableInfo.getForeignKeyMetadata(fk) == null && (
    // // Icky workaround for MySQL bug:
    // !(dialect instanceof MySQLDialect) ||
    // tableInfo.getIndexMetadata(fk.getName()) == null));
    // if (create) {
    // scripts.add(new SchemaUpdateScript(
    // fk.sqlCreateString(dialect, mappingCC, tableCatalog, tableSchema),
    // false));
    // }
    // }
    // }
    // }
    // }
    // }
    //
    // Iterator<IdentifierGenerator> iterG = iterateGenerators(dialect);
    // while (iterG.hasNext()) {
    // PersistentIdentifierGenerator generator = (PersistentIdentifierGenerator)
    // iterG.next();
    // Object key = generator.generatorKey();
    // if (!databaseMetadata.isSequence(key) && !databaseMetadata.isTable(key))
    // {
    // String[] lines = generator.sqlCreateStrings(dialect);
    // scripts.addAll(SchemaUpdateScript.fromStringArray(lines, false));
    // }
    // }
    //
    // return scripts;
    // }
    //
    //
    // private boolean checkIndexForGeometry(Index index, Dialect dialect) {
    // if (index.getColumnSpan() == 1) {
    // Iterator<Column> columnIterator = index.getColumnIterator();
    // while (columnIterator.hasNext()) {
    // Column column = columnIterator.next();
    // if (column.getSqlTypeCode(mappingCC) == 3000) {
    // return true;
    // }
    // }
    // }
    // return false;
    // }
    //
    // /**
    // * Based on
    // * {@link
    // org.hibernate.cfg.Configuration#generateDropSchemaScript(Dialect)}
    // * . Rewritten to only create drop commands for existing tables/sequences.
    // *
    // *
    // * @param d
    // * @param m
    // *
    // * @return SQL script to drop schema as String array
    // *
    // * @throws HibernateException
    // */
    // public String[] generateDropSchemaScript(final Dialect d, final
    // DatabaseMetadata m) throws HibernateException {
    // secondPassCompile();
    // final String c =
    // getProperties().getProperty(Environment.DEFAULT_CATALOG);
    // final String s = getProperties().getProperty(Environment.DEFAULT_SCHEMA);
    // final List<String> script = new LinkedList<>();
    // script.addAll(generateAuxiliaryDatabaseObjectDropScript(d, c, s));
    // if (d.dropConstraints()) {
    // script.addAll(generateConstraintDropScript(d, c, s, m));
    // }
    // script.addAll(generateTableDropScript(d, c, s, m));
    // script.addAll(generateIdentifierGeneratorDropScript(d, c, s, m));
    // return ArrayHelper.toStringArray(script);
    // }
    //
    // /**
    // * Copied from
    // * {@link org.hibernate.cfg.Configuration#iterateGenerators(Dialect)}.
    // */
    // private Iterator<PersistentIdentifierGenerator> iterateGenerators(final
    // Dialect d, final String c, final String s)
    // throws MappingException {
    // final TreeMap<Object, PersistentIdentifierGenerator> generators = new
    // TreeMap<>();
    // for (final PersistentClass pc : classes.values()) {
    // if (!pc.isInherited()) {
    // final IdentifierGenerator ig =
    // pc.getIdentifier().createIdentifierGenerator(getIdentifierGeneratorFactory(),
    // d, c, s,
    // (RootClass) pc);
    // if (ig instanceof PersistentIdentifierGenerator) {
    // final PersistentIdentifierGenerator pig = (PersistentIdentifierGenerator)
    // ig;
    // generators.put(pig.generatorKey(), pig);
    // } else if (ig instanceof IdentifierGeneratorAggregator) {
    // ((IdentifierGeneratorAggregator)
    // ig).registerPersistentGenerators(generators);
    // }
    // }
    // }
    // collections.values().stream()
    // .filter((collection) -> (collection.isIdentified()))
    // .map((collection) ->
    // ((IdentifierCollection)
    // collection).getIdentifier().createIdentifierGenerator(
    // getIdentifierGeneratorFactory(), d, c, s, null))
    // .filter((ig) -> (ig instanceof PersistentIdentifierGenerator))
    // .map((ig) -> (PersistentIdentifierGenerator) ig)
    // .forEachOrdered((pig) -> generators.put(pig.generatorKey(), pig));
    //
    // return generators.values().iterator();
    // }
    //
    // protected List<String> generateConstraintDropScript(final Dialect d,
    // final String c, final String s,
    // final DatabaseMetadata m) throws HibernateException {
    // final List<String> script = new LinkedList<>();
    // final Iterator<Table> itr = getTableMappings();
    // while (itr.hasNext()) {
    // final Table table = itr.next();
    // // TODO remove because fails if table definition is quoted
    //// final String tableName = table.getQualifiedName(d, c, s);
    // if (checkTable(table, m)) {
    // @SuppressWarnings("unchecked")
    // final Iterator<ForeignKey> subItr = table.getForeignKeyIterator();
    // final TableMetadata tableMeta = m.getTableMetadata(table.getName(), s, c,
    // table.isQuoted());
    // while (subItr.hasNext()) {
    // final ForeignKey fk = subItr.next();
    // if (fk.isPhysicalConstraint() && tableMeta.getForeignKeyMetadata(fk) !=
    // null) {
    // script.add(fk.sqlDropString(d, c, s));
    // }
    // }
    // }
    // }
    // return script;
    // }
    //
    // protected List<String> generateTableDropScript(final Dialect d, final
    // String c, final String s,
    // final DatabaseMetadata m) throws HibernateException {
    // final List<String> script = new LinkedList<>();
    // final Iterator<Table> itr = getTableMappings();
    // while (itr.hasNext()) {
    // final Table table = itr.next();
    // // TODO remove because fails if table definition is quoted
    //// final String tableName = table.getQualifiedName(d, c, s);
    // if (checkTable(table, m)) {
    // script.add(table.sqlDropString(d, c, s));
    // }
    // }
    // return script;
    // }
    //
    // protected boolean checkTable(Table table, DatabaseMetadata m) {
    // return table.isPhysicalTable() && m.isTable(table.getQuotedName());
    // }
    //
    // protected List<String> generateAuxiliaryDatabaseObjectDropScript(final
    // Dialect d, final String c, final String s) {
    // final List<String> script = new LinkedList<>();
    // final ListIterator<AuxiliaryDatabaseObject> itr =
    // auxiliaryDatabaseObjects.listIterator(auxiliaryDatabaseObjects.size());
    // while (itr.hasPrevious()) {
    // // FIXME how to check if ADO exists?
    // final AuxiliaryDatabaseObject object = itr.previous();
    // if (object.appliesToDialect(d)) {
    // script.add(object.sqlDropString(d, c, s));
    // }
    // }
    // return script;
    // }
    //
    // protected List<String> generateIdentifierGeneratorDropScript(final
    // Dialect d, final String c, final String s,
    // final DatabaseMetadata m) throws MappingException, HibernateException {
    // final List<String> script = new LinkedList<>();
    // final Iterator<PersistentIdentifierGenerator> itr = iterateGenerators(d,
    // c, s);
    // while (itr.hasNext()) {
    // final PersistentIdentifierGenerator pig = itr.next();
    // if (pig instanceof SequenceGenerator) {
    // final SequenceGenerator sg = (SequenceGenerator) pig;
    // if (!m.isSequence(sg.getSequenceName())) {
    // continue;
    // }
    // }
    // script.addAll(Arrays.asList(pig.sqlDropStrings(d)));
    // }
    // return script;
    // }

    @Override
    public StandardServiceRegistryBuilder getStandardServiceRegistryBuilder() {
        return super.getStandardServiceRegistryBuilder().applySettings(getProperties());
    }
}
