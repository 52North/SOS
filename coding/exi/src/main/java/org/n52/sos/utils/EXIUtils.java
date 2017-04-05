/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.faroe.Validation;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.janmayen.Producer;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.sos.exi.EXISettings;

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
public class EXIUtils implements Constructable, Producer<EXIFactory>{
    private static final Logger LOGGER = LoggerFactory.getLogger(EXIUtils.class);
    @Deprecated
    private static EXIUtils instance = null;

    private Grammars grammarSos20 = null;
    private Grammars grammarSos10 = null;
    private Grammars grammarBaseTypes = null;
    private boolean isSchemaLessGrammar;
    private boolean isXSBaseTypeGrammar;
    private boolean isSOS20Schema;
    private boolean isSOS10Schema;
    private CodingMode alignment;
    private boolean isStrict;
    private boolean isDefault;
    private boolean preserveComments;
    private boolean preserveProcessingInstructions;
    private boolean preserveDTD;
    private boolean preservePrefixes;
    private boolean preserveLexicalValue;
    private final Grammars grammarSchemaLess;
    private final GrammarFactory grammarFactory;

    public EXIUtils() {
        this.alignment = CodingMode.BIT_PACKED;
        this.grammarFactory = GrammarFactory.newInstance();
        this.grammarSchemaLess = this.grammarFactory.createSchemaLessGrammars();
    }

    @Override
    public void init() {
        EXIUtils.instance = this;

        try {
            // Pre-load Grammars from URL to save time
            // TODO does this result in any race conditions?
            if (!isSchemaLessGrammar()) {
                if (isXSBaseTypeGrammar()) {
                    grammarBaseTypes = grammarFactory.createXSDTypesOnlyGrammars();
                } else if (isSOS10Schema()) {
                    grammarSos10 = grammarFactory.createGrammars(Sos1Constants.SCHEMA_LOCATION_SOS);
                } else if (isSOS20Schema()) {
                    grammarSos20 = grammarFactory.createGrammars(Sos2Constants.SCHEMA_LOCATION_URL_SOS);
                }
            }

        } catch (EXIException e) {
            LOGGER.error("Could not load XSD schema for EXI binding. "
                    + "Using default schema less grammar. Please update your settings.", e);
        }
    }

    @Setting(EXISettings.EXI_FIDELITY_LEXICAL_VALUE)
    public void setFidelityLexicalValue(boolean preserveLexicalValue) {
        this.preserveLexicalValue = preserveLexicalValue;
    }

    @Setting(EXISettings.EXI_FIDELITY_PREFIXES)
    public void setFidelityPrefixes(boolean preservePrefixes) {
        this.preservePrefixes = preservePrefixes;
    }

    @Setting(EXISettings.EXI_FIDELITY_DTD)
    public void setFidelityDTD(boolean preserveDTD) {
        this.preserveDTD = preserveDTD;
    }

    @Setting(EXISettings.EXI_FIDELITY_PROCESSING_INSTRUCTIONS)
    public void setFidelityProcessingInstructions(boolean preserveProcessingInstructions) {
        this.preserveProcessingInstructions = preserveProcessingInstructions;
    }

    @Setting(EXISettings.EXI_FIDELITY_COMMENTS)
    public void setFidelityComments(boolean preserveComments) {
        this.preserveComments = preserveComments;
    }

    @Setting(EXISettings.EXI_FIDELITY)
    public void setStrictFidelity(String fidelity) {
        Validation.notNullOrEmpty(EXISettings.EXI_FIDELITY, fidelity);
        if (fidelity.equalsIgnoreCase(EXISettings.EXI_FIDELITY_STRICT)) {
            this.isStrict = true;
        } else if (fidelity.equalsIgnoreCase(EXISettings.EXI_FIDELITY_DEFAULT)) {
            this.isDefault = true;
        }
    }

    @Setting(EXISettings.EXI_ALIGNMENT)
    public void setCodingMode(String codingMode) {
        Validation.notNullOrEmpty(EXISettings.EXI_ALIGNMENT, codingMode);
        this.alignment = CodingMode.valueOf(codingMode);
    }

    @Setting(EXISettings.EXI_GRAMMAR)
    public void setGrammarType(String grammar) {
        Validation.notNullOrEmpty(EXISettings.EXI_GRAMMAR, grammar);
        if (grammar.equalsIgnoreCase(EXISettings.EXI_GRAMMAR_SCHEMALESS)) {
            setSchemaLessGrammar(true);
        } else if (grammar.equalsIgnoreCase(EXISettings.EXI_GRAMMAR_BASETYPES)) {
            setXSBaseTypeGrammar(true);
        }
    }

    @Setting(EXISettings.EXI_GRAMMAR_SCHEMA)
    public void setGrammarSchema(String grammarSchema) {
        Validation.notNullOrEmpty(EXISettings.EXI_GRAMMAR_SCHEMA, grammarSchema);
        if (grammarSchema.equalsIgnoreCase(EXISettings.EXI_GRAMMAR_SCHEMA_SOS_20)) {
            setSOS20Schema(true);
        } else if (grammarSchema.equalsIgnoreCase(EXISettings.EXI_GRAMMAR_SCHEMA_SOS_10)) {
            setSOS10Schema(true);
        }
    }

    @Override
    public EXIFactory get() {
        try {
            return newEXIFactory();
        } catch (UnsupportedOption ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @return An {@link EXIFactory} instance configured according the service
     *         configuration.
     * @throws UnsupportedOption
     *             if one of the fidelity options is not supported.
     */
    public EXIFactory newEXIFactory() throws UnsupportedOption {
        EXIFactory factory = DefaultEXIFactory.newInstance();
        factory.setGrammars(getGrammars());
        //
        // STRICT vs. OTHER fidelity options
        //
        // TODO is it possible to identify these options via any EXI header or
        // something else?
        if (this.isStrict) {
            factory.setFidelityOptions(FidelityOptions.createStrict());
        } else if (this.isDefault) {
            factory.setFidelityOptions(FidelityOptions.createDefault());
        } else {
            FidelityOptions options = factory.getFidelityOptions();
            options.setFidelity(FidelityOptions.FEATURE_COMMENT, this.preserveComments);
            options.setFidelity(FidelityOptions.FEATURE_PI, this.preserveProcessingInstructions);
            options.setFidelity(FidelityOptions.FEATURE_DTD, this.preserveDTD);
            options.setFidelity(FidelityOptions.FEATURE_PREFIX, this.preservePrefixes);
            options.setFidelity(FidelityOptions.FEATURE_LEXICAL_VALUE, this.preserveLexicalValue);
        }
        factory.setCodingMode(alignment);
        //
        // TODO Implement usage and settings UI for Additional Values
        //
        // ef.setValueMaxLength(ANY_CONSTANT_OR_SETTING);
        // ef.setValuePartitionCapacity(ANY_CONSTANT_OR_SETTING);
        // if (cm.usesRechanneling()) {
        // ef.setBlockSize(ANY_CONSTANT_OR_SETTING);
        // }
        return factory;
    }

    private Grammars getGrammars() {
        //
        // GRAMMAR
        //
        // TODO How to identify the correct location: SOS 1.0 vs. 2.0 vs WFS vs
        // WPS ...
        if (isSchemaLessGrammar()) {
            return grammarSchemaLess;
        }
        if (isXSBaseTypeGrammar()) {
            return grammarBaseTypes;
        }
        if (isSOS20Schema()) {
            return grammarSos20;
        }
        if (isSOS10Schema()) {
            return grammarSos10;
        }
        // default to schema less grammar
        return grammarSchemaLess;
    }

    /**
     * @return the isSchemaLessGrammar
     */
    private boolean isSchemaLessGrammar() {
        return this.isSchemaLessGrammar;
    }

    /**
     * @param isSchemaLessGrammar the isSchemaLessGrammar to set
     */
    private void setSchemaLessGrammar(boolean isSchemaLessGrammar) {
        this.isSchemaLessGrammar = isSchemaLessGrammar;
    }

    /**
     * @return the isXSBaseTypeGrammar
     */
    public boolean isXSBaseTypeGrammar() {
        return this.isXSBaseTypeGrammar;
    }

    /**
     * @param isXSBaseTypeGrammar the isXSBaseTypeGrammar to set
     */
    public void setXSBaseTypeGrammar(boolean isXSBaseTypeGrammar) {
        this.isXSBaseTypeGrammar = isXSBaseTypeGrammar;
    }

    /**
     * @return the isSOS20Schema
     */
    public boolean isSOS20Schema() {
        return this.isSOS20Schema;
    }

    /**
     * @param isSOS20Schema the isSOS20Schema to set
     */
    public void setSOS20Schema(boolean isSOS20Schema) {
        this.isSOS20Schema = isSOS20Schema;
    }

    /**
     * @return the isSOS10Schema
     */
    public boolean isSOS10Schema() {
        return this.isSOS10Schema;
    }

    /**
     * @param isSOS10Schema the isSOS10Schema to set
     */
    public void setSOS10Schema(boolean isSOS10Schema) {
        this.isSOS10Schema = isSOS10Schema;
    }

    @Deprecated
    public static EXIUtils getInstance() {
        return instance;
    }

}
