/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
function xml2string(xml) {
    return typeof (xml) === "string" ? xml : xml.xml ? xml.xml
        : new XMLSerializer().serializeToString(xml);
}

$(function () {

    var procedures_identifiers = JSON.parse($("#procedures_identifier").attr("data-value"))
    var offerings_identifiers = JSON.parse($("#offerings_identifier").attr("data-value"))
    var observableProperties_identifiers = JSON.parse($("#observableProperties_identifier").attr("data-value"))
    var features_identifiers = JSON.parse($("#features_identifier").attr("data-value"))

    var url_procedures = $("#url_procedures").attr("data-value");
    var url_offerings = $("#url_offerings").attr("data-value");
    var url_observableProperties = $("#url_observableProperties").attr("data-value");
    var url_features = $("#url_features").attr("data-value");

    new I18NController({
        name: "Procedure",
        div: "#i18n-procedures",
        identifiers: procedures_identifiers,
        url: url_procedures,
        properties: {
            name: "Name",
            description: "Description",
            shortName: "Short Name",
            longName: "Long Name"
        },
    });
    new I18NController({
        name: "Offering",
        div: "#i18n-offerings",
        identifiers: offerings_identifiers,
        url: url_offerings,
        properties: {
            name: "Name",
            description: "Description"
        },
    });
    new I18NController({
        name: "Observable Property",
        div: "#i18n-observableProperties",
        identifiers: observableProperties_identifiers,
        url: url_observableProperties,
        properties: {
            name: "Name",
            description: "Description"
        },
    });
    new I18NController({
        name: "Feature",
        div: "#i18n-features",
        identifiers: features_identifiers,
        url: url_features,
        properties: {
            name: "Name",
            description: "Description"
        },
    });
});