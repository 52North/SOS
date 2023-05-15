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
    warnIfNotHttps();
    var err = $("#error_data").attr("data-value");
    var settings = JSON.parse($("#settings_data").attr("data-value"));

    if (err !== '') {
        showError(err);
    }

    if (settings) {
        generateSettings(settings.settings, {}, "#settings", false);
    } else {
        $("#save").attr("disabled", true);
        showWarning("There are no settings to change");
    }

    $("input[type=text],input[type=password],textarea").trigger("input");
    $(".required").bind("keyup input change", function () {
        var valid = true;
        $(".required").each(function () {
            var val = $(this).val();
            return valid = (val !== null && val !== undefined && val !== "");
        });

        var val = $(this).val();
        if (val !== null && val !== undefined && val !== "") {
            $(this).parents(".control-group").removeClass("error");
        } else {
            $(this).parents(".control-group").addClass("error");
        }

        if (valid) {
            $("#save").removeAttr("disabled");
        } else {
            $("#save").attr("disabled", true);
        }
    });
});
