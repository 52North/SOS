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
package org.n52.sos.exi;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.BooleanSettingDefinition;
import org.n52.sos.config.settings.ChoiceSettingDefinition;

import com.google.common.collect.ImmutableSet;
import com.siemens.ct.exi.CodingMode;

/**
 * Configuration settings for {@link EXIBinding}.
 * 
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 * 
 * @since 4.2.0
 *
 */
public class EXISettings implements SettingDefinitionProvider {
	
	private static final SettingDefinitionGroup SETTINGS_GROUP = new SettingDefinitionGroup().
			setTitle("EXI Binding").
			setDescription("<abbr title=\"Efficient XML Interchange\">EXI</abbr>"
			    + " is a very compact representation for the <a "
			    + "href=\"http://www.w3.org/TR/exi/\" target=\"_blank\">"
			    + "<abbr title=\"Extensible Markup Language\">XML</abbr></a>"
			    + " Information Set that is intended to simultaneously "
			    + "optimize performance and the utilization of computational "
			    + "resources. EXI encoding is available via content type "
			    + "<code>application/exi</code>.").
			setOrder(4.22f);
	
	public static final String EXI_ALIGNMENT = "exi.alignment";
	public static final String EXI_FIDELITY = "exi.fidelity";
	public static final String EXI_FIDELITY_STRICT = "exi.fidelity.strict";
	public static final String EXI_FIDELITY_DEFAULT = "exi.fidelity.default";
	public static final String EXI_FIDELITY_SPECIFIC = "exi.fidelity.specific";
	public static final String EXI_FIDELITY_COMMENTS = "exi.fidelity.comments";
	public static final String EXI_FIDELITY_PROCESSING_INSTRUCTIONS = "exi.fidelity.processing.instructions";
	public static final String EXI_FIDELITY_DTD = "exi.fidelity.dtd";
	public static final String EXI_FIDELITY_PREFIXES = "exi.fidelity.prefixes";
	public static final String EXI_FIDELITY_LEXICAL_VALUE = "exi.fidelity.lexical.value";
	public static final String EXI_GRAMMAR = "exi.grammar";
	public static final String EXI_GRAMMAR_SCHEMALESS = "exi.grammar.schemaless";
	public static final String EXI_GRAMMAR_BASETYPES = "exi.grammar.basetypes";
	public static final String EXI_GRAMMAR_SCHEMABASED = "exi.grammar.schemabased";
	public static final String EXI_GRAMMAR_SCHEMA = "exi.grammar.schema";
	public static final String EXI_GRAMMAR_SCHEMA_SOS_10 = "exi.grammar.schema.sos.10";
	public static final String EXI_GRAMMAR_SCHEMA_SOS_20 = "exi.grammar.schema.sos.20";
	
	private static final Set<SettingDefinition<?, ?>> DEFINITIONS = ImmutableSet.<SettingDefinition<?,?>>of(
			new ChoiceSettingDefinition().
			addOption(CodingMode.BIT_PACKED.name(), "Bit packed").
			addOption(CodingMode.BYTE_PACKED.name(), "Byte packed").
			addOption(CodingMode.COMPRESSION.name(), "Compression").
			addOption(CodingMode.PRE_COMPRESSION.name(), "Pre-Compression").
			setGroup(SETTINGS_GROUP).
			setKey(EXI_ALIGNMENT).
			setTitle("Alignment").
			setDefaultValue(CodingMode.BIT_PACKED.name()).
			setDescription("The alignment option is used to control the "
					+ "alignment of event codes and content items.<ul><li>"
					+ "<b>Bit packed</b>: indicates that the event codes and "
					+ "associated content are packed in bits without any "
					+ "padding in-between.</li><li><b>Byte packed</b>: "
					+ "indicates that the event codes and associated content "
					+ "are aligned on byte boundaries.</li><li><b>"
					+ "Compression</b>: increases compactness using additional "
					+ "computational resources by applying the DEFLATE "
					+ "algorithm.</li><li><b>Pre-Compression</b>: indicates "
					+ "that all steps involved in "
					+ "compression are to be done with the exception of the "
					+ "final step of applying the DEFLATE algorithm (the "
					+ "primary use case of pre-compression is to avoid a "
					+ "duplicate compression step when compression capability "
					+ "is built into the transport protocol).</li><ul>").
			setOptional(false).
			setOrder(ORDER_0),
			
			new ChoiceSettingDefinition().
			addOption(EXI_FIDELITY_DEFAULT, "Default").
			addOption(EXI_FIDELITY_STRICT, "Strict").
			addOption(EXI_FIDELITY_SPECIFIC, "Specific").
			setGroup(SETTINGS_GROUP).
			setKey(EXI_FIDELITY).
			setTitle("Fidelity").
			setDefaultValue(EXI_FIDELITY_SPECIFIC).
			setDescription("Fidelity options allow to preserve some features "
					+ "that might be removed by EXI because of better "
					+ "performance and compression.<ul><li><b>Default"
					+ "</b>: uses some default options. If any of the other"
					+ " options is selected, default options is skipped."
					+ "</li><li><b>Specific</b>: uses the options activated"
					+ "further down.</li><li><b>Strict</b>: "
					+ "no namespace prefixes, comments etc are preserved nor"
					+ " schema deviations are allowed.</li></ul>").
			setOptional(false).
			setOrder(ORDER_1),
			
			new BooleanSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(EXI_FIDELITY_COMMENTS).
			setTitle("Preserve comments?").
			setDefaultValue(false).
			setDescription("Should the EXI encoder preserve comments and "
					+ "the decoder expect comments."
					+ "<br /><i><b>Note</b>: Requires fidelity <b>specific</b>"
					+ ".</i>").
			setOptional(true).
			setOrder(ORDER_2),
			
			new BooleanSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(EXI_FIDELITY_PROCESSING_INSTRUCTIONS).
			setTitle("Preserve processing instructions?").
			setDefaultValue(false).
			setDescription("Should the EXI encoder preserve processing "
					+ "instructions and the decoder expect them."
					+ "<br /><i><b>Note</b>: Requires fidelity <b>specific</b>"
					+ ".</i>").
			setOptional(true).
			setOrder(ORDER_3),
			
			new BooleanSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(EXI_FIDELITY_DTD).
			setTitle("Preserve DTDs?").
			setDefaultValue(false).
			setDescription("Should the EXI encoder preserve <abbr title=\""
					+ "Document Type Definition\">DTD</abbr>s "
					+ "and the decoder expect them."
					+ "<br /><i><b>Note</b>: Requires fidelity <b>specific</b>"
					+ ".</i>").
			setOptional(true).
			setOrder(ORDER_4),
			
			new BooleanSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(EXI_FIDELITY_PREFIXES).
			setTitle("Preserve prefixes?").
			setDefaultValue(true).
			setDescription("Should the EXI encoder preserve prefixes and the "
					+ "decoder expect them."
					+ "<br /><i><b>Note</b>: Requires fidelity <b>specific</b>"
					+ ".</i>").
			setOptional(true).
			setOrder(ORDER_5),
			
			new BooleanSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(EXI_FIDELITY_LEXICAL_VALUE).
			setTitle("Preserve lexical form?").
			setDefaultValue(false).
			setDescription("Should the EXI encoder preserve the lexical form "
					+ "of element and attribute values and the decoder expect"
					+ " them."
					+ "<br /><i><b>Note</b>: Requires fidelity <b>specific</b>"
					+ ".</i>").
			setOptional(true).
			setOrder(ORDER_6),
			
			new ChoiceSettingDefinition().
			addOption(EXI_GRAMMAR_BASETYPES, "XML Schema Types Only").
			addOption(EXI_GRAMMAR_SCHEMALESS, "Schemaless").
			addOption(EXI_GRAMMAR_SCHEMABASED, "Schema based on XSD").
			setGroup(SETTINGS_GROUP).
			setKey(EXI_GRAMMAR).
			setTitle("Grammar").
			setDefaultValue(EXI_GRAMMAR_SCHEMALESS).
			setDescription("Grammar options define if the en/decoder use any "
					+ "grammar while processing XML streams."
					+ "<ul><li><b>Schema based on XSD</b>: schema"
					+ " information will be used while processing the EXI body"
					+ " (best results (compression)). Available schema can be "
					+ " selected further down.</li><li><b>schemaless</b>: no schema "
					+ "information will be used to process the EXI stream "
					+ "(worst results (compression)).</li><li><b>XSD Base "
					+ "Types</b>: No user defined schema information is "
					+ "generated for processing the EXI body; however, the "
					+ "built-in XML schema types are available for use in the"
					+ " EXI  body.</li></ul>").
			setOptional(false).
			setOrder(ORDER_7),
			
			new ChoiceSettingDefinition().
			addOption(EXI_GRAMMAR_SCHEMA_SOS_10, "OGC SOS 1.0.0").
			addOption(EXI_GRAMMAR_SCHEMA_SOS_20, "OGC SOS 2.0.0").
			setGroup(SETTINGS_GROUP).
			setKey(EXI_GRAMMAR_SCHEMA).
			setTitle("Grammar Schema").
			setDefaultValue(EXI_GRAMMAR_SCHEMA_SOS_20).
			setDescription("Which XSD to use for processing the EXI body. "
					+ "<br /><i><b>Note</b>: Requires grammar <b>schema based"
					+ " on XSD</b>.</i>").
			setOptional(true).
			setOrder(ORDER_8)
			);

	@Override
	public Set<SettingDefinition<?, ?>> getSettingDefinitions()
	{
		return Collections.unmodifiableSet(DEFINITIONS);
	}

}
