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
package org.n52.sos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.spatial.dialect.h2geodb.GeoDBDialectSpatialIndex;
import org.hibernate.spatial.dialect.mysql.MySQLSpatial5InnoDBTimestampDialect;
import org.hibernate.spatial.dialect.postgis.PostgisDialectSpatialIndex;
import org.hibernate.spatial.dialect.sqlserver.SqlServer2008SpatialDialectSpatialIndex;
import org.n52.sos.ds.datasource.CustomConfiguration;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import hibernate.spatial.dialect.oracle.OracleSpatial10gDoubleFloatDialect;


/**
 * Class to generate the create and drop scripts for different databases.
 * Currently supported spatial databases to create scripts - PostgreSQL/PostGIS
 * - Oracle - H2/GeoDB
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 * 
 */
public class SQLScriptGenerator {

    private SQLScriptGenerator() {

    }

    private Dialect getDialect(int selection) throws Exception {
        switch (selection) {
        case 1:
            return new PostgisDialectSpatialIndex();
        case 2:
            try {
                return new OracleSpatial10gDoubleFloatDialect();
            } catch (ExceptionInInitializerError eiie) {
                printToScreen("The Oracle JDBC driver is missing!");
                printToScreen("To execute the SQL script generator for Oracle you have to uncomment the dependency in the pom.xml.");
                printToScreen("If the Oracle JDBC driver is not installed in your local Maven repository, ");
                printToScreen("follow the first steps describes here: ");
                printToScreen("https://wiki.52north.org/SensorWeb/SensorObservationServiceIVDocumentation#Oracle_support.");
                throw new MissingDriverException();
            }

        case 3:
            return new GeoDBDialectSpatialIndex();
        case 4:
            return new MySQLSpatial5InnoDBTimestampDialect();
        case 5:
            return new SqlServer2008SpatialDialectSpatialIndex();
        default:
            throw new Exception("The entered value is invalid!");
        }
    }

    private void setDirectoriesForModelSelection(int selection, int concept, Configuration configuration)
            throws Exception {
        switch (selection) {
        case 1:
            configuration.addDirectory(new File(SQLScriptGenerator.class.getResource("/mapping/core").toURI()));
            configuration.addDirectory(new File(SQLScriptGenerator.class.getResource("/mapping/parameter").toURI()));
            break;
        case 2:
            configuration.addDirectory(new File(SQLScriptGenerator.class.getResource("/mapping/core").toURI()));
            configuration
                    .addDirectory(new File(SQLScriptGenerator.class.getResource("/mapping/transactional").toURI()));
            configuration.addDirectory(new File(SQLScriptGenerator.class.getResource("/mapping/parameter").toURI()));
            break;
        case 3:
            configuration.addDirectory(new File(SQLScriptGenerator.class.getResource("/mapping/core").toURI()));
            configuration
                    .addDirectory(new File(SQLScriptGenerator.class.getResource("/mapping/transactional").toURI()));
            configuration.addDirectory(new File(SQLScriptGenerator.class.getResource("/mapping/parameter").toURI()));
            configuration.addDirectory(new File(SQLScriptGenerator.class.getResource("/mapping/i18n").toURI()));
            configuration.addDirectory(new File(SQLScriptGenerator.class.getResource("/mapping/feature").toURI()));
            break;
        default:
            throw new Exception("The entered value is invalid!");
        }
        addConceptDirectories(concept, configuration);
    }

    private void addConceptDirectories(int concept, Configuration configuration) throws Exception {
        switch (concept) {
        case 1:
            configuration.addDirectory(new File(SQLScriptGenerator.class.getResource("/mapping/old/observation")
                    .toURI()));
            break;
        case 2:
            configuration.addDirectory(new File(SQLScriptGenerator.class.getResource("/mapping/series/observation")
                    .toURI()));
            configuration.addDirectory(new File(SQLScriptGenerator.class.getResource("/mapping/metadata").toURI()));
            break;
        case 3:
            configuration.addDirectory(new File(SQLScriptGenerator.class.getResource("/mapping/ereporting").toURI()));
            break;
        default:
            throw new Exception("The entered value is invalid!");
        }
    }

    private int getSelection() throws IOException {
        printToScreen("Create a all or a single selected script:");
        printToScreen("1   all");
        printToScreen("2   Select script");
        printToScreen("3   Misc scripts");
        printToScreen("");
        printToScreen("Enter your selection: ");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String selection = null;
        selection = br.readLine();
        return Integer.parseInt(selection);
    }

    private int getDialectSelection() throws IOException {
        printToScreen("This SQL script generator supports:");
        printToScreen("1   PostgreSQL/PostGIS");
        printToScreen("2   Oracle");
        printToScreen("3   H2/GeoDB");
        printToScreen("4   MySQL");
        printToScreen("5   SQL Server");
        printToScreen("");
        printToScreen("Enter your selection: ");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String selection = null;
        selection = br.readLine();
        return Integer.parseInt(selection);
    }

    private int getModelSelection() throws IOException {
        printToScreen("Which database model should be created:");
        printToScreen("1   Core");
        printToScreen("2   Transcational");
        printToScreen("3   All");
        printToScreen("");
        printToScreen("Enter your selection: ");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String selection = null;
        selection = br.readLine();
        return Integer.parseInt(selection);
    }

    private int getConceptSelection() throws IOException {
        printToScreen("Which observation concept should be created:");
        printToScreen("1   old");
        printToScreen("2   series");
        printToScreen("3   ereporting");
        printToScreen("");
        printToScreen("Enter your selection: ");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String selection = null;
        selection = br.readLine();
        return Integer.parseInt(selection);
    }

    private String getSchema() throws IOException {
        printToScreen("For which schema should the database model be created?");
        printToScreen("No schema is also valid!");
        printToScreen("");
        printToScreen("Enter your selection: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String selection = null;
        selection = br.readLine();
        return selection;
    }

    public static void printToScreen(String lineToPrint) {
        System.out.println(lineToPrint);
    }

    private Set<String> checkSchema(Dialect dia, String[] create) {
        String hexStringToCheck =
                new StringBuilder("FK").append(Integer.toHexString("observationHasOffering".hashCode()).toUpperCase())
                        .toString();
        boolean duplicate = false;
        List<String> checkedSchema = Lists.newLinkedList();
        Set<String> set = new HashSet<>();
        for (String string : create) {
            if (string.contains(hexStringToCheck)) {
                if (!duplicate) {
                    if (set.add(string)) {
                        checkedSchema.add(string);
                    }
                    duplicate = true;
                }
            } else {
                if (set.add(string)) {
                    checkedSchema.add(string);
                }
            }
        }
        return Sets.newLinkedHashSet(checkedSchema);
    }

    public static void main(String[] args) {
        try {
            SQLScriptGenerator sqlScriptGenerator = new SQLScriptGenerator();
            int select = sqlScriptGenerator.getSelection();
            if (select == 1) {
                String schema = "public";
                // dialectSelection
                for (int i = 1; i < 6; i++) {
                    schema = getSchema(i);
                    // modelSelection
                    for (int j = 1; j < 4; j++) {
                        // concept
                        for (int k = 1; k < 4; k++) {
                            try {
                                execute(sqlScriptGenerator, i, j, k, schema);
                            } catch (MissingDriverException mde) {
                                System.exit(1);
                            } catch (Exception e) {
                                printToScreen("ERROR: " + e.getMessage());
                                System.exit(1);
                            }
                        }   
                    }
                }
            } else if (select == 3) {
                String schema = "public";
                // dialectSelection
                for (int i = 1; i < 6; i++) {
                    schema = getSchema(i);
                    try {
                        execute(sqlScriptGenerator, i, 2, 2, schema);
                    } catch (MissingDriverException mde) {
                        System.exit(1);
                    } catch (Exception e) {
                        printToScreen("ERROR: " + e.getMessage());
                        System.exit(1);
                    }
                }
            } else {
                try {
                    int dialectSelection = sqlScriptGenerator.getDialectSelection();
                    int modelSelection = sqlScriptGenerator.getModelSelection();
                    int concept = sqlScriptGenerator.getConceptSelection();
                    String schema = sqlScriptGenerator.getSchema();
                    execute(sqlScriptGenerator, dialectSelection, modelSelection, concept, schema);
                } catch (IOException ioe) {
                    printToScreen("ERROR: IO error trying to read your input!");
                    System.exit(1);
                } catch (MissingDriverException mde) {
                    System.exit(1);
                } catch (Exception e) {
                    printToScreen("ERROR: " + e.getMessage());
                    System.exit(1);
                }
            }
            
        } catch (IOException ioe) {
            printToScreen("ERROR: IO error trying to read your input!");
            System.exit(1);
        }
    }

    private static String getSchema(int i) {
        switch (i) {
        case 1:
            return "public";
        case 2:
            return "oracle";
        case 3:
            return null;
        case 4:
            return "sos";
        case 5:
            return "dbo";
        default:
            return null;
        }
    }

    private static void execute(SQLScriptGenerator sqlScriptGenerator, int dialectSelection, int modelSelection, int concept, String schema) throws Exception {
        FileWriter writer = null;
        try {
            Configuration configuration = new CustomConfiguration().configure("/sos-hibernate.cfg.xml");
            Dialect dia = sqlScriptGenerator.getDialect(dialectSelection);
            String fileName = "target/" + sqlScriptGenerator.getDialectSelection(dialectSelection) + "_" + sqlScriptGenerator.getModelSelection(modelSelection) + "_" + sqlScriptGenerator.getConceptSelection(concept) + ".sql";
            writer = new FileWriter(fileName);
            if (schema != null && !schema.isEmpty()) {
                Properties p = new Properties();
                p.put("hibernate.default_schema", schema);
                configuration.addProperties(p);
            }
            sqlScriptGenerator.setDirectoriesForModelSelection(modelSelection, concept, configuration);
            // create script
            String[] create = configuration.generateSchemaCreationScript(dia);
            Set<String> checkedSchema = sqlScriptGenerator.checkSchema(dia, create);
            writer.write("Scripts are created for: " + dia.toString() + "\n");
            writer.write("\n");
            writer.write("#######################################\n");
            writer.write("##           Create-Script           ##\n");
            writer.write("#######################################\n");
            writer.write("\n");
            for (String t : checkedSchema) {
                writer.write(t + ";\n");
            }
            // drop script
            String[] drop = configuration.generateDropSchemaScript(dia);
            Set<String> checkedDrop = sqlScriptGenerator.checkSchema(dia, drop);
            writer.write("\n");
            writer.write("#######################################\n");
            writer.write("##            Drop-Script            ##\n");
            writer.write("#######################################\n");
            writer.write("\n");
            for (String t : checkedDrop) {
                writer.write(t + ";\n");
            }
            writer.write("\n");
            writer.write("#######################################\n");
            printToScreen("Finished! Check for file: " + fileName + "\n");
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
    
    private String getDialectSelection(int i) {
        if (i == 1) {
            return "postgre";
        } else if (i == 2) {
            return "oracle";
        } else if (i == 3) {
            return "h2";
        } else if (i == 4) {
            return "mysql";
        } else if (i == 5) {
            return "sqlServer";
        }
        return Integer.toString(i);
    }
    
    private String getModelSelection(int i) {
        if (i == 1) {
            return "core";
        } else if (i == 2) {
            return "transactional";
        } else if (i == 3) {
            return "all";
        }
        return Integer.toString(i);
    }
    
    private String getConceptSelection(int i) {
        if (i == 1) {
            return "old";
        } else if (i == 2) {
            return "series";
        } else if (i == 3) {
            return "ereporting";
        }
        return Integer.toString(i);
    }

    private class MissingDriverException extends Exception {

        private static final long serialVersionUID = -5681526838468633998L;

        public MissingDriverException() {
            super();
        }

    }
}
