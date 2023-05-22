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
    show_dbconnection_check_success();
    generate_settings();
});

function show_dbconnection_check_success() {
    if (document.referrer) {
        if (document.referrer.matches(/install\/database/)) {
            showSuccess("Database configuration successfully tested.");
        }
    }
};

function generate_settings() {
    var url = $("#settings_url").attr("data-value");
    var settings = JSON.parse($("#settings_data").attr("data-value"));
    
    $.getJSON(url, function (settingDefinitions) {
        var $container = $("#settings");
        generateSettings(settingDefinitions, settings, $container, true);
        $("#service_identification .control-group:first").before("<legend>Standard Settings</legend>");
        $("#service_provider .control-group:first").before("<legend>Standard Settings</legend>");
        $("#service_identification .control-group:last").before("<legend>Extended Settings</legend>");
        $("#service_provider .control-group:last").before("<legend>Extended Settings</legend>");

        if (!settings["service.serviceURL"]) {
            $("input[name='service.serviceURL']").val(window.location.toString()
                .replace(/install\/settings.*/, "service")).trigger("input");
        }

        $(".required").bind("keyup input change", function () {
            var valid = true;
            $(".required").each(function () {
                var val = $(this).val();
                return valid = (val !== null && val !== undefined && val !== "");
            });
            if (valid) {
                $("button[type=submit]").removeAttr("disabled");
            } else {
                $("button[type=submit]").attr("disabled", true);
            }
        });

        $(".required:first").trigger("change");
        parsehash();
    });
};
