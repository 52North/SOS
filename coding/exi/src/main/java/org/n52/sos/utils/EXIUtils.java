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
package org.n52.sos.utils;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.exi.EXISettings;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.util.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.UnsupportedOption;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

/**
 * TODO JavaDoc
 *
 * @author Eike Hinderk J&uuml;rrens <e.h.juerrens@52north.org>
 * @since 4.2.0
 */
@Configurable
public class EXIUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(EXIUtils.class);

    private static Grammars GRAMMAR_SOS20 = null;

    private static Grammars GRAMMAR_SOS10 = null;

    private static Grammars GRAMMAR_BASETYPES = null;

    private CodingMode alignment = CodingMode.BIT_PACKED;

    private boolean isStrict;

    private boolean isDefault;

    private boolean preserveComments;

    private boolean preserveProcessingInstructions;

    private boolean preserveDTD;

    private boolean preservePrefixes;

    private boolean preserveLexicalValue;

    private static boolean isSchemaLessGrammar;

    private static boolean isXSBaseTypeGrammar;

    private static boolean isSOS20Schema;

    private static boolean isSOS10Schema;

    private static EXIUtils instance = null;

    private EXIUtils() {
    }

    public static synchronized EXIUtils getInstance() {
        if (instance == null) {
            instance = new EXIUtils();
            SettingsManager.getInstance().configure(instance);
            try {
                // Pre-load Grammars from URL to save time
                // TODO does this result in any race conditions?
                if (!isSchemaLessGrammar()) {
                    if (isXSBaseTypeGrammar()) {
                        GRAMMAR_BASETYPES = GrammarFactory.newInstance().createXSDTypesOnlyGrammars();
                    } else if (isSOS10Schema()) {
                        GRAMMAR_SOS10 = GrammarFactory.newInstance().createGrammars(Sos1Constants.SCHEMA_LOCATION_SOS);
                    } else if (isSOS20Schema()) {
                        GRAMMAR_SOS20 =
                                GrammarFactory.newInstance().createGrammars(Sos2Constants.SCHEMA_LOCATION_URL_SOS);
                    }
                }

            } catch (EXIException e) {
                LOGGER.error("Could not load XSD schema for EXI binding. "
                        + "Using default schema less grammar. Please update your settings.", e);
            }
        }
        return instance;
    }

    @Setting(EXISettings.EXI_FIDELITY_LEXICAL_VALUE)
    public void setFidelityLexicalValue(final boolean preserveLexicalValue) {
        this.preserveLexicalValue = preserveLexicalValue;
    }

    @Setting(EXISettings.EXI_FIDELITY_PREFIXES)
    public void setFidelityPrefixes(final boolean preservePrefixes) {
        this.preservePrefixes = preservePrefixes;
    }

    @Setting(EXISettings.EXI_FIDELITY_DTD)
    public void setFidelityDTD(final boolean preserveDTD) {
        this.preserveDTD = preserveDTD;
    }

    @Setting(EXISettings.EXI_FIDELITY_PROCESSING_INSTRUCTIONS)
    public void setFidelityProcessingInstructions(final boolean preserveProcessingInstructions) {
        this.preserveProcessingInstructions = preserveProcessingInstructions;
    }

    @Setting(EXISettings.EXI_FIDELITY_COMMENTS)
    public void setFidelityComments(final boolean preserveComments) {
        this.preserveComments = preserveComments;
    }

    @Setting(EXISettings.EXI_FIDELITY)
    public void setStrictFidelity(final String fidelity) {
        Validation.notNullOrEmpty(EXISettings.EXI_FIDELITY, fidelity);
        if (fidelity.equalsIgnoreCase(EXISettings.EXI_FIDELITY_STRICT)) {
            this.isStrict = true;
        } else if (fidelity.equalsIgnoreCase(EXISettings.EXI_FIDELITY_DEFAULT)) {
            this.isDefault = true;
        }
    }

    @Setting(EXISettings.EXI_ALIGNMENT)
    public void setCodingMode(final String codingMode) {
        Validation.notNullOrEmpty(EXISettings.EXI_ALIGNMENT, codingMode);
        this.alignment = CodingMode.valueOf(codingMode);
    }

    @Setting(EXISettings.EXI_GRAMMAR)
    public void setGrammarType(final String grammar) {
        Validation.notNullOrEmpty(EXISettings.EXI_GRAMMAR, grammar);
        if (grammar.equalsIgnoreCase(EXISettings.EXI_GRAMMAR_SCHEMALESS)) {
            setSchemaLessGrammar(true);
        } else if (grammar.equalsIgnoreCase(EXISettings.EXI_GRAMMAR_BASETYPES)) {
            setXSBaseTypeGrammar(true);
        }
    }

    @Setting(EXISettings.EXI_GRAMMAR_SCHEMA)
    public void setGrammarSchema(final String grammarSchema) {
        Validation.notNullOrEmpty(EXISettings.EXI_GRAMMAR_SCHEMA, grammarSchema);
        if (grammarSchema.equalsIgnoreCase(EXISettings.EXI_GRAMMAR_SCHEMA_SOS_20)) {
            setSOS20Schema(true);
        } else if (grammarSchema.equalsIgnoreCase(EXISettings.EXI_GRAMMAR_SCHEMA_SOS_10)) {
            setSOS10Schema(true);
        }
    }

    /**
     * @return the isSchemaLessGrammar
     */
    private static boolean isSchemaLessGrammar() {
        return isSchemaLessGrammar;
    }

    /**
     * @param isSchemaLessGrammar the isSchemaLessGrammar to set
     */
    private static void setSchemaLessGrammar(boolean isSchemaLessGrammar) {
        EXIUtils.isSchemaLessGrammar = isSchemaLessGrammar;
    }

    /**
     * @return the isXSBaseTypeGrammar
     */
    public static boolean isXSBaseTypeGrammar() {
        return isXSBaseTypeGrammar;
    }

    /**
     * @param isXSBaseTypeGrammar the isXSBaseTypeGrammar to set
     */
    public static void setXSBaseTypeGrammar(boolean isXSBaseTypeGrammar) {
        EXIUtils.isXSBaseTypeGrammar = isXSBaseTypeGrammar;
    }

    /**
     * @return the isSOS20Schema
     */
    public static boolean isSOS20Schema() {
        return isSOS20Schema;
    }

    /**
     * @param isSOS20Schema the isSOS20Schema to set
     */
    public static void setSOS20Schema(boolean isSOS20Schema) {
        EXIUtils.isSOS20Schema = isSOS20Schema;
    }

    /**
     * @return the isSOS10Schema
     */
    public static boolean isSOS10Schema() {
        return isSOS10Schema;
    }

    /**
     * @param isSOS10Schema the isSOS10Schema to set
     */
    public static void setSOS10Schema(boolean isSOS10Schema) {
        EXIUtils.isSOS10Schema = isSOS10Schema;
    }

    /**
     * @return An {@link EXIFactory} instance configured according the service
     *         configuration.
     * @throws UnsupportedOption
     *             if one of the fidelity options is not supported.
     */
    public EXIFactory newEXIFactory() throws UnsupportedOption {
        EXIFactory ef = DefaultEXIFactory.newInstance();
        //
        // GRAMMAR
        //
        // TODO How to identify the correct location: SOS 1.0 vs. 2.0 vs WFS vs
        // WPS ...
        Grammars g = null;
        if (!isSchemaLessGrammar()) {
            if (isXSBaseTypeGrammar()) {
                g = GRAMMAR_BASETYPES;
            } else {
                if (isSOS20Schema()) {
                    g = GRAMMAR_SOS20;
                } else if (isSOS10Schema()) {
                    g = GRAMMAR_SOS10;
                }
            }
        }
        // default to schema less grammar
        if (g == null) {
            g = GrammarFactory.newInstance().createSchemaLessGrammars();
        }
        ef.setGrammars(g);
        //
        // STRICT vs. OTHER fidelity options
        //
        // TODO is it possible to identify these options via any EXI header or
        // something else?
        if (isStrict) {
            ef.setFidelityOptions(FidelityOptions.createStrict());
        } else if (isDefault) {
            ef.setFidelityOptions(FidelityOptions.createDefault());
        } else {
            if (preserveComments) {
                ef.getFidelityOptions().setFidelity(FidelityOptions.FEATURE_COMMENT, true);
            }
            if (preserveProcessingInstructions) {
                ef.getFidelityOptions().setFidelity(FidelityOptions.FEATURE_PI, true);
            }
            if (preserveDTD) {
                ef.getFidelityOptions().setFidelity(FidelityOptions.FEATURE_DTD, true);
            }
            if (preservePrefixes) {
                ef.getFidelityOptions().setFidelity(FidelityOptions.FEATURE_PREFIX, true);
            }
            if (preserveLexicalValue) {
                ef.getFidelityOptions().setFidelity(FidelityOptions.FEATURE_LEXICAL_VALUE, true);
            }
        }
        ef.setCodingMode(alignment);
        //
        // TODO Implement usage and settings UI for Additional Values
        //
        // ef.setValueMaxLength(ANY_CONSTANT_OR_SETTING);
        // ef.setValuePartitionCapacity(ANY_CONSTANT_OR_SETTING);
        // if (cm.usesRechanneling()) {
        // ef.setBlockSize(ANY_CONSTANT_OR_SETTING);
        // }
        return ef;
    }

}
