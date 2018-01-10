/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.config.sqlite;


import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.coding.encode.ProcedureDescriptionFormatKey;
import org.n52.iceland.coding.encode.ResponseFormatKey;
import org.n52.faroe.ConfigurationError;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.iceland.request.operator.RequestOperatorKey;
import org.n52.shetland.ogc.ows.service.OwsServiceKey;

import com.google.common.io.Files;

/**
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class SQLiteSettingsManagerTest {
    private static final Logger LOG = LoggerFactory.getLogger(SQLiteSettingsManagerTest.class);
    private static final String OPERATION_NAME = SosConstants.Operations.GetCapabilities.name();
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";
    private static final String VERSION = Sos2Constants.SERVICEVERSION;
    private static final String SERVICE = SosConstants.SOS;
    private static final String RESPONSE_FORMAT = "responseFormat";
    private static final String PROCEDURE_DESCRIPTION_FORMAT = "procedureDescriptionFormat";
    private static SQLiteSessionFactory sessionFactory;
    private static File databaseFile;
    private static final OwsServiceKey SOKT = new OwsServiceKey(SERVICE, VERSION);
    private static final RequestOperatorKey ROKT = new RequestOperatorKey(SOKT, OPERATION_NAME);
    private static final ResponseFormatKey RFKT = new ResponseFormatKey(SOKT, RESPONSE_FORMAT);
    private static final ProcedureDescriptionFormatKey PDFKT = new ProcedureDescriptionFormatKey(SOKT, PROCEDURE_DESCRIPTION_FORMAT);


    @BeforeClass
    public static void setUpClass() throws ConfigurationError, IOException {
        File directory = new File(System.getProperty("java.io.tmpdir"));
        databaseFile = File.createTempFile("configuration-test", ".db", directory);
        sessionFactory = new SQLiteSessionFactory();
        sessionFactory.setPath(directory.getAbsolutePath());
        sessionFactory.setDatabaseName(Files.getNameWithoutExtension(databaseFile.getName()));
        sessionFactory.init();
        LOG.info("using database file: {}", databaseFile.getAbsolutePath());
    }

    @AfterClass
    public static void tearDownClass() {
        if (sessionFactory != null) {
            sessionFactory.destroy();
        }
        if (databaseFile != null && databaseFile.exists()) {
            databaseFile.delete();
        }
    }
    private SQLiteSettingsDao settingsManager;

    @Before
    public void setUp() throws ConfigurationError {
        SQLiteSettingsDao manager = new SQLiteSettingsDao();
        manager.setSessionFactory(sessionFactory);
        this.settingsManager = manager;
    }

//    @Test
//    public void testBooleanSettings() throws ConfigurationException, ConnectionProviderException {
//        final BooleanSettingDefinition settingDefinition = new BooleanSettingDefinition().setKey(BOOLEAN_SETTING);
//        final SettingValue<Boolean> settingValue = new BooleanSettingValue().setKey(BOOLEAN_SETTING).setValue(
//                Boolean.TRUE);
//        final SettingValue<Boolean> newSettingValue = new BooleanSettingValue().setKey(BOOLEAN_SETTING)
//                .setValue(Boolean.FALSE);
//        testSaveGetAndDelete(settingDefinition, settingValue, newSettingValue);
//    }
//
//    @Test
//    public void testStringSettings() throws ConfigurationException, ConnectionProviderException {
//        final StringSettingDefinition settingDefinition = new StringSettingDefinition().setKey(STRING_SETTING);
//        final SettingValue<String> settingValue = new StringSettingValue().setKey(STRING_SETTING).setValue("string1");
//        final SettingValue<String> newSettingValue = new StringSettingValue().setKey(STRING_SETTING)
//                .setValue("string2");
//        testSaveGetAndDelete(settingDefinition, settingValue, newSettingValue);
//    }
//
//    @Test
//    public void testFileSettings() throws ConfigurationException, ConnectionProviderException {
//        final FileSettingDefinition settingDefinition = new FileSettingDefinition().setKey(FILE_SETTING);
//        final SettingValue<File> settingValue = new FileSettingValue().setKey(FILE_SETTING).setValue(new File(
//                "/home/auti/sos1"));
//        final SettingValue<File> newSettingValue = new FileSettingValue().setKey(FILE_SETTING).setValue(new File(
//                "/home/auti/sos2"));
//        testSaveGetAndDelete(settingDefinition, settingValue, newSettingValue);
//    }
//
//    @Test
//    public void testIntegerSettings() throws ConfigurationException, ConnectionProviderException {
//        final IntegerSettingDefinition settingDefinition = new IntegerSettingDefinition().setKey(INTEGER_SETTING);
//        final SettingValue<Integer> settingValue = new IntegerSettingValue().setKey(INTEGER_SETTING).setValue(12312);
//        final SettingValue<Integer> newSettingValue = new IntegerSettingValue().setKey(INTEGER_SETTING).setValue(12311);
//        testSaveGetAndDelete(settingDefinition, settingValue, newSettingValue);
//    }
//
//    @Test
//    public void testNumericSettings() throws ConfigurationException, ConnectionProviderException {
//        final NumericSettingDefinition settingDefinition = new NumericSettingDefinition().setKey(DOUBLE_SETTING);
//        final SettingValue<Double> settingValue = new NumericSettingValue().setKey(DOUBLE_SETTING).setValue(212.1213);
//        final SettingValue<Double> newSettingValue = new NumericSettingValue().setKey(DOUBLE_SETTING)
//                .setValue(212.1211);
//        testSaveGetAndDelete(settingDefinition, settingValue, newSettingValue);
//    }
//
//    @Test
//    public void testUriSettings() throws ConfigurationException, ConnectionProviderException {
//        final UriSettingDefinition settingDefinition = new UriSettingDefinition().setKey(URI_SETTING);
//        final SettingValue<URI> settingValue = new UriSettingValue().setKey(URI_SETTING).setValue(URI.create(
//                "http://localhost:8080/a"));
//        final SettingValue<URI> newSettingValue = new UriSettingValue().setKey(URI_SETTING).setValue(URI.create(
//                "http://localhost:8080/b"));
//        testSaveGetAndDelete(settingDefinition, settingValue, newSettingValue);
//    }

//    @Test(expected = IllegalArgumentException.class)
//    public void testChangedSettingsTypeForKey() throws ConfigurationException, ConnectionProviderException {
//        final SettingValue<Double> doubleValue = new NumericSettingValue().setKey(BOOLEAN_SETTING).setValue(212.1213);
//        settingsManager.changeSetting(doubleValue);
//    }
//
//    public <T> void testSaveGetAndDelete(final SettingDefinition<? extends SettingDefinition<?, T>, T> settingDefinition,
//                                         final SettingValue<T> settingValue, final SettingValue<T> newSettingValue)
//            throws ConfigurationException, ConnectionProviderException {
//
//        assertNotEquals(settingValue, newSettingValue);
//        settingsManager.changeSetting(settingValue);
//        assertEquals(settingValue, settingsManager.getSetting(settingDefinition));
//
//        settingsManager.changeSetting(newSettingValue);
//        final SettingValue<T> value = settingsManager.getSetting(settingDefinition);
//        assertEquals(newSettingValue, value);
//        assertNotEquals(settingValue, value);
//
//        settingsManager.deleteSetting(settingDefinition);
//        assertNull(settingsManager.getSetting(settingDefinition));
//    }
//
//    @Test
//    public void createAdminUserTest() throws ConnectionProviderException {
//        AdministratorUser au = settingsManager.createAdminUser(USERNAME, PASSWORD);
//        assertNotNull(au);
//        assertEquals(USERNAME, au.getUsername());
//        assertEquals(PASSWORD, au.getPassword());
//
//        AdministratorUser au2 = settingsManager.getAdminUser(USERNAME);
//        assertNotNull(au2);
//        assertEquals(au, au2);
//    }
//
//    @Test(expected = HibernateException.class)
//    public void createDuplicateAdminUser() throws ConnectionProviderException {
//        settingsManager.createAdminUser(USERNAME, PASSWORD);
//        settingsManager.createAdminUser(USERNAME, PASSWORD);
//    }
//
//    @Test
//    public void deleteAdminUserTest() throws ConnectionProviderException {
//        AdministratorUser au = settingsManager.getAdminUser(USERNAME);
//        if (au == null) {
//            au = settingsManager.createAdminUser(USERNAME, PASSWORD);
//
//        }
//        assertNotNull(au);
//        settingsManager.deleteAdminUser(au);
//        assertNull(settingsManager.getAdminUser(USERNAME));
//
//        settingsManager.createAdminUser(USERNAME, PASSWORD);
//        assertNotNull(settingsManager.getAdminUser(USERNAME));
//        settingsManager.deleteAdminUser(USERNAME);
//        assertNull(settingsManager.getAdminUser(USERNAME));
//    }
//
//    @Test
//    public void testActiveOperations() throws ConnectionProviderException {
//
//
//        assertThat(settingsManager.isRequestOperatorActive(ROKT), is(true));
//        settingsManager.setActive(ROKT, true);
//        assertThat(settingsManager.isRequestOperatorActive(ROKT), is(true));
//        settingsManager.setActive(ROKT, false);
//        assertThat(settingsManager.isRequestOperatorActive(ROKT), is(false));
//
//    }
//
//    @Test
//    public void testActiveResponseFormats() throws ConnectionProviderException {
//        assertThat(settingsManager.isResponseFormatActive(RFKT), is(true));
//        settingsManager.setActive(RFKT, true);
//        assertThat(settingsManager.isResponseFormatActive(RFKT), is(true));
//        settingsManager.setActive(RFKT, false);
//        assertThat(settingsManager.isResponseFormatActive(RFKT), is(false));
//    }
//
//    @Test
//    public void testActiveProcedureDescriptionFormats() throws ConnectionProviderException {
//        assertThat(settingsManager.isProcedureDescriptionFormatActive(PDFKT), is(true));
//        settingsManager.setActive(PDFKT, true);
//        assertThat(settingsManager.isProcedureDescriptionFormatActive(PDFKT), is(true));
//        settingsManager.setActive(PDFKT, false);
//        assertThat(settingsManager.isProcedureDescriptionFormatActive(PDFKT), is(false));

//        assertThat(settingsManager.isActive(PROCEDURE_DESCRIPTION_FORMAT), is(true));
//        settingsManager.setActive(PROCEDURE_DESCRIPTION_FORMAT, true);
//        assertThat(settingsManager.isActive(PROCEDURE_DESCRIPTION_FORMAT), is(true));
//        settingsManager.setActive(PROCEDURE_DESCRIPTION_FORMAT, false);
//        assertThat(settingsManager.isActive(PROCEDUColumnRE_DESCRIPTION_FORMAT), is(false));

//    }
//
//    @Test
//    public void testLocalizedStringSetting() throws ConfigurationException, ConnectionProviderException {
//        final MultilingualStringSettingDefinition settingDefinition = new MultilingualStringSettingDefinition().setKey(LOCALIZED_STRING_SETTING);
//        MultilingualString l1 = new MultilingualString().addLocalization(Locale.GERMAN, "Hallo").addLocalization(Locale.ENGLISH, "Hello");
//        MultilingualString l2 = new MultilingualString().addLocalization(Locale.GERMAN, "Hallo").addLocalization(Locale.ENGLISH, "Hello").addLocalization(Locale.CHINESE, "???");
//        SettingValue<MultilingualString> settingValue = new MultilingualStringSettingValue().setKey(LOCALIZED_STRING_SETTING).setValue(l1);
//        SettingValue<MultilingualString> newSettingValue = new MultilingualStringSettingValue().setKey(LOCALIZED_STRING_SETTING).setValue(l2);
//        testSaveGetAndDelete(settingDefinition, settingValue, newSettingValue);
//    }
//
//
//    @Test
//    public void testChoiceSettings() throws ConfigurationException, ConnectionProviderException {
//        final ChoiceSettingDefinition settingDefinition = new ChoiceSettingDefinition().setKey(CHOICE_SETTING);
//        final SettingValue<String> settingValue = new ChoiceSettingValue().setKey(CHOICE_SETTING).setValue("string1");
//        final SettingValue<String> newSettingValue = new ChoiceSettingValue().setKey(CHOICE_SETTING)
//                .setValue("string2");
//        testSaveGetAndDelete(settingDefinition, settingValue, newSettingValue);
//    }
}
