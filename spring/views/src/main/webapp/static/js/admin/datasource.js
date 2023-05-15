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
    var $clearDialog = $("#confirmDialogClear");
    var $deleteDeletedDialog = $("#confirmDialogDelete");
    var $addSampledataDialog = $("#confirmDialogAddSampledata");

    var supportsClear = $("#supportsClear").attr("data-value");
    var supportsDeleteDeleted = $("#supportsDeleteDeleted").attr("data-value");

    var url_addSampledata = $("#url_addSampledata").attr("data-value");
    var url_deleteDeletedData = $("#url_deleteDeletedData").attr("data-value");
    var url_clear = $("#url_clear").attr("data-value");
    var url_loadPredefinedPhenomena = $("#url_loadPredefinedPhenomena").attr("data-value");
    var url_loadPredefinedUnits = $("#url_loadPredefinedUnits").attr("data-value");
    var url_datasource = $("#url_datasource").attr("data-value");

    if (supportsClear) {
        $("#clear").click(function () {
            $clearDialog.find("button").attr("disabled", true);
            $.ajax({
                "url": url_clear,
                "type": "POST"
            }).fail(function (error) {
                showError("Request failed: " + error.status + " " + error.statusText);
                $clearDialog.find("button").removeAttr("disabled");
                $clearDialog.modal("hide");
            }).done(function () {
                showSuccess("The datasource was cleared");
                $clearDialog.find("button").removeAttr("disabled");
                $clearDialog.modal("hide");
            });
        });
    } else {
        $("button[data-target=#confirmDialogClear]").attr("disabled", true);
    }

    /*if (supportsAddSampledata) {*/
    $("#addSampledata").click(function () {
        $addSampledataDialog.find("button").attr("disabled", true);
        $.ajax({
            "url": url_addSampledata,
            "type": "POST"
        }).fail(function (xhr, status, error) {
            showError("Inserting sample data failed: " + xhr.status + " " + xhr.statusText + "\n" + xhr.responseText);
            $addSampledataDialog.find("button").removeAttr("disabled");
            $addSampledataDialog.modal("hide");
        }).done(function () {
            showSuccess("The sample data was inserted.");
            $addSampledataDialog.find("button").removeAttr("disabled");
            $addSampledataDialog.modal("hide");
        })
    });
    $("#addSampledata").ajaxStart(function () {
        $addSampledataDialog.modal("hide");
    });
    /*} else {
        $("button[data-target=#confirmDialogAddTestdata]").attr("disabled", true);
    }*/

    if (supportsDeleteDeleted) {
        $("#delete").click(function () {
            $deleteDeletedDialog.find("button").attr("disabled", true);
            $.ajax({
                "url": url_deleteDeletedData,
                "type": "POST"
            }).fail(function (error) {
                if (error.responseText) {
                    showError(error.responseText);
                } else {
                    showError("Request failed: " + error.status + " " + error.statusText);
                }

                $deleteDeletedDialog.find("button").removeAttr("disabled");
                $deleteDeletedDialog.modal("hide");
            }).done(function () {
                showSuccess("The deleted data were deleted.");
                $deleteDeletedDialog.find("button").removeAttr("disabled");
                $deleteDeletedDialog.modal("hide");
            });
        });
    } else {
        $("button[data-target=#confirmDialogDelete]").attr("disabled", true);
    }

    $("#loadObsProps").click(function () {
        var value = $("#input-phenomena :selected").text()
        if (value === "" || value.startsWith("Select a predefined")) {
            showError("No vocabulary selected!");
        } else {
            $.ajax({
                "url": url_loadPredefinedPhenomena,
                "type": "GET",
                "data": {
                    "name": value
                }
            }).fail(function (error) {
                showError("Request failed: " + error.responseText);
            }).done(function (response) {
                showSuccess(response);
            });
        }
    });

    $("#loadUnits").click(function () {
        var value = $("#input-units :selected").text()
        if (value === "" || value.startsWith("Select a predefined")) {
            showError("No vocabulary selected!");
        } else {
            $.ajax({
                "url": url_loadPredefinedUnits,
                "type": "GET",
                "data": {
                    "name": value
                }
            }).fail(function (error) {
                showError("Request failed: " + error.responseText);
            }).done(function (response) {
                showSuccess(response);
            });
        }
    });
});
