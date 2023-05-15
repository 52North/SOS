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
    var url_bindings_json = $("#url_bindings_json").attr("data-value");

    $("#activateAll").on("click", function () {
        $("tbody > tr > td.status > button.btn-danger").each(function () {
            $(this).click();
        });
    });
    $("#disableAll").on("click", function () {
        $("tbody > tr > td.status > button.btn-success").each(function () {
            $(this).click();
        });
    });

    function bindings(encodings) {
        var $tbody = $("#bindings tbody"), i, o, $row, $button;
        for (i = 0; i < encodings.length; ++i) {
            o = encodings[i];
            $row = $("<tr>");
            $("<td>").addClass("binding").text(o.binding).appendTo($row);
            $button = $("<button>").attr("type", "button")
                .addClass("btn btn-sm btn-block").on("click", function () {
                    var $b = $(this),
                        $tr = $b.parents("tr"),
                        active = !$b.hasClass("btn-success"),
                        j = {
                            binding: $tr.find(".binding").text(),
                            active: active
                        };
                    $b.prop("disabled", true);
                    $.ajax(url_bindings_json, {
                        type: "POST",
                        contentType: "application/json",
                        data: JSON.stringify(j)
                    }).fail(function (e) {
                        showError("Failed to save binding: "
                            + e.status + " " + e.statusText);
                        $b.prop("disabled", false);
                    }).done(function () {
                        $b.toggleClass("btn-danger btn-success")
                            .text(active ? "active" : "inactive")
                            .prop("disabled", false);

                    });
                });
            if (o.active) {
                $button.addClass("btn-success").text("active");
            } else {
                $button.addClass("btn-danger").text("inactive");

            }
            $("<td>").addClass("status").append($button).appendTo($row);

            $tbody.append($row);
        }
    }

    $.getJSON(url_bindings_json, function (j) {
        bindings(j.bindings);
    });
});
