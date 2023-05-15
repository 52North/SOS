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
    setup_datasource()
});

function setup_datasource() {
    var url = $("#url_sources").attr("data-value");
    
    warnIfNotHttps();
    $.getJSON(url, function (datasources) {
        var datasource, selected,
            $datasource = $("#datasource"),
            $settings = $("#settings"),
            $actions = $("#actions"),
            $create = $("#create"),
            $overwrite = $("#overwrite"),
            $update = $("#update");

        for (datasource in datasources) {
            if (datasources.hasOwnProperty(datasource)) {
                $datasource.append($("<option>").text(datasource));
                if (datasources[datasource].selected) {
                    selected = datasource;
                }
            }
        }
        datasource = null;

        $datasource.change(function () {
            var d = $(this).val();
            if (d !== datasource) {
                datasource = d;

                /* create settings */
                $settings.slideUp("fast", function () {
                    $settings.children().remove();
                    generateSettings(datasources[d].settings, {}, $settings, false);
                    /* save settings as default values
                    * to keep them between switches */
                    $settings.find(":input").on("change", function () {
                        var $this = $(this), name = $this.attr("name"),
                            i, settings = datasources[d]["settings"].sections;
                        for (i = 0; i < settings.length; ++i) {
                            if (settings[i].settings.hasOwnProperty(name)) {
                                settings[i]["settings"][name]["default"] = $this.val();
                            }
                        }
                    });
                    $settings.slideDown("fast");
                });

                /* adjust actions */
                $actions.slideUp("fast", function () {
                    var schema = datasources[d].needsSchema;
                    $create.hide();
                    $overwrite.hide();
                    $update.hide();
                    if (schema) {
                        $create.show();
                        $overwrite.show();
                        $update.show();
                        $actions.slideDown("fast");
                    }
                });
            }
        });

        $("input[name=create_tables]").change(function () {
            var $update_tables = $("input[name=update_tables]");
            if ($(this).attr("checked")) {
                if ($update_tables.attr("checked")) {
                    $update_tables.attr({
                        "checked": false
                    })
                        .parent().next().toggle("fast");
                }
            }
        });

        $("input[name=overwrite_tables]").click(function () {
            $(this).parent().next().toggle("fast");
        });

        $("input[name=overwrite_tables]").change(function () {
            var $create_tables = $("input[name=create_tables]");
            var $update_tables = $("input[name=update_tables]");
            if ($(this).attr("checked")) {
                $create_tables.attr({
                    "checked": "checked",
                    "disabled": true
                })
                    .parent("label").addClass("muted");
                if ($update_tables.attr("checked")) {
                    $update_tables.attr({
                        "checked": false
                    })
                        .parent().next().toggle("fast");
                }
            } else {
                $create_tables.removeAttr("disabled")
                    .parent("label").removeClass("muted");
            }
        });

        $("input[name=update_tables]").click(function () {
            $(this).parent().next().toggle("fast");
        });

        $("input[name=update_tables]").change(function () {
            var $create_tables = $("input[name=create_tables]");
            var $overwrite_tables = $("input[name=overwrite_tables]");
            if ($(this).attr("checked")) {
                $create_tables.attr({
                    "checked": false
                }).removeAttr("disabled")
                    .parent("label").removeClass("muted");
                if ($overwrite_tables.attr("checked")) {
                    $overwrite_tables.attr({
                        "checked": false
                    })
                        .parent().next().toggle("fast");
                }
            } else {

            }
        });

        if (selected) {
            $datasource.val(selected).trigger("change");
        }

    })
};