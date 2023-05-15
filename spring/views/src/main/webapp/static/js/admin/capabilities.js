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
$(function () {
    $(window).scroll(function () {
        var pos = $(this).scrollTop();
        if (pos > "fast" && pos < $(document).height()
            - ($(this).height() + 400)) {
            $('#back-top').fadeIn();
        } else {
            $('#back-top').fadeOut();
        }
    });
    $('#back-top a').click(function () {
        $('body, html').animate({
            scrollTop: 0
        }, 400);
        return false;
    });

    const baseUrl = $("#url_base").attr("data-value")

    var scc = new StaticCapabilitiesController(baseUrl),
        cec = new CapabilitiesExtensionController(baseUrl),
        oec = new OfferingExtensionController(baseUrl);

    var changed = 0;

    function onChange() {
        if (++changed < 3) {
            return;
        }
        var code = function (e) {
                return "<code>" + e + "</code>";
            },
            toCodeList = function (a) {
                return (a.length > 1 ? a.slice(0, -1).map(code).join(", ") + " and " : "") + code(a[a.length - 1]);
            },
            verb = function (col) {
                return col.length === 1 ? "is" : "are";
            },
            ext = function (col) {
                return "Extension" + (col.length === 1 ? "" : "s");
            },
            formatCapabilitiesExtensions = function () {
                var hasEnabled = cec.hasEnabledExtensions(),
                    hasDisabled = cec.hasDisabledExtensions(),
                    html = "";
                if (hasEnabled || hasDisabled) {
                    html += "<p>";
                    if (hasEnabled) {
                        var active = cec.getEnabledExtensions();
                        html += " The Capabilities " + ext(active) + " " + toCodeList(active) + " " + verb(active) + " active";
                    }
                    if (hasDisabled) {
                        var inactive = cec.getDisabledExtensions();
                        html += (hasEnabled) ? " while" : ("The Capabilties " + ext(inactive)) + " ";
                        html += toCodeList(inactive) + " " + verb(inactive) + " inactive";
                    }
                    html + ".</p>";
                }
                return html;
            },
            formatOfferingExtensions = function () {
                var html = "",
                    offs = {},
                    copy = function (name, col) {
                        for (var a in col) {
                            if (col.hasOwnProperty(a) && col[a].length > 0) {
                                (offs[a] = offs[a] || {})[name] = col[a];
                            }
                        }
                    };

                if (oec.hasEnabledExtensions())
                    copy("active", oec.getEnabledExtensions());
                if (oec.hasDisabledExtensions())
                    copy("inactive", oec.getDisabledExtensions());


                for (var key in offs) {
                    html += "<p>";
                    if (offs[key].active) {
                        html += " " + ext(offs[key].active) + " "
                            + toCodeList(offs[key].active) + " "
                            + verb(offs[key].active)
                            + " active for Offering " + code(key);
                        if (offs[key].inactive) {
                            html += " while " + toCodeList(offs[key].inactive)
                                + " " + verb(offs[key].inactive) + " not";
                        }
                        html += ".";
                    } else if (offs[key].inactive) {
                        html += ext(offs[key].inactive) + " "
                            + toCodeList(offs[key].inactive) + " "
                            + verb(offs[key].inactive)
                            + " inactive for Offering " + code(key) + ".";
                    }
                    html += "</p>";
                }
                return html;
            },
            html = "",
            lead = "",
            $cs = $("#current-setting");


        if (scc.isStatic()) {
            lead = "The current Capabilities are based on the <strong>static</strong> capabilities " + code(scc.getCurrent()) + ".";
        } else {
            lead = "The current Capabilities are based on the <strong>dynamic</strong> capabilities.";
            html += formatCapabilitiesExtensions();
            html += formatOfferingExtensions();
        }
        $("p.lead").fadeOut("fast", function () {
            $(this).children().remove().addBack().html(lead).fadeIn("fast");
        });
        if ($cs.children().length === 0) {
            $cs.hide().html(html).fadeIn();
        } else {
            $cs.fadeOut("fast", function () {
                $(this).children().remove().addBack().html(html).fadeIn("fast");
            });
        }
    }

    scc.on("change", onChange).on("ready", onChange);
    cec.on("change", onChange).on("ready", onChange);
    oec.on("change", onChange).on("ready", onChange);
});

function xml2string(xml) {
    return typeof (xml) === "string" ? xml : xml.xml ? xml.xml
        : new XMLSerializer().serializeToString(xml);
}



