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
function confirm(logger, callback) {

    var $modal = $("<div>").addClass("modal fade hide confirm");
    var $header = $("<div>").addClass("modal-header");
    $("<button>").addClass("close").attr("type", "button")
        .html("&times;")
        .on("click", function () {
            $modal.modal("hide");
            $modal.remove();
            callback(false);
        }).appendTo($header);
    $("<h3>").text("Are you really sure?").appendTo($header);
    var $body = $("<div>").addClass("modal-body");
    $("<p>")
        .html("This will remove the logger <code>" + logger + "</code>.")
        .appendTo($body);
    var $footer = $("<div>").addClass("modal-footer");
    $("<button>").addClass("btn").text("Cancel")
        .on("click", function () {
            $modal.modal("hide");
            $modal.remove();
            callback(false);
        }).appendTo($footer);
    $("<button>")
        .addClass("btn btn-danger")
        .text("Remove")
        .on("click", function () {
            $modal.modal("hide");
            $modal.remove();
            callback(true);
        }).appendTo($footer);
    $modal.append($header).append($body).append($footer).appendTo($("body"));
    $modal.modal({ "keyboards": true, "show": true });
}

jQuery(document).ready(function ($) {

    var err = $("#error").attr("data-error")
    if (err != "") {
        showError(err);
    }

    $("#messages-table").hide();
    $("#messages-header").wrapInner($("<a>").attr("href", "#"));
    $("#messages-header").children("a").prepend($("<i>").addClass("icon-chevron-right"));
    $("#messages-header").click(function (e) {
        e.preventDefault();
        $(this).find("i").toggleClass("icon-chevron-right icon-chevron-down");
        $("#messages-table").slideToggle();
    });
    $(".remove-logger").live("click", function (e) {
        e.preventDefault();
        var $this = $(this),
            name = $this.parents(".controls").find("select").attr("name");
        confirm(name, function (confirmed) {
            if (confirmed) {
                $this.parents(".control-group").slideUp(function () {
                    $this.remove();
                });
            }
        });
    });
    $("#new-logger-name").on("input keyup", function () {
        var val = $(this).val();
        var loggers = [];
        $("select.logger").each(function () {
            loggers.push($(this).attr("name"));
        });

        if (!val || loggers.contains(val)) {
            $(this).parents(".control-group").addClass("error");
            $("#add-logger-button").attr("disabled", true);
        } else {
            $(this).parents(".control-group").removeClass("error");
            $("#add-logger-button").removeAttr("disabled");
        }
    }).trigger("input");

    $("#add-logger").on("hidden", function () {
        $(this).find("input").val("");
    });

    var fileSizePattern = /^\s*[0-9]+\s*((k|m|g)b?s?)?\s*$/i;
    $("input[name=maxFileSize]").on("input keyup", function () {
        if (fileSizePattern.test($(this).val())) {
            $(this).parents(".control-group").removeClass("error");
        } else {
            $(this).parents(".control-group").addClass("error");
        }
    });

    $("#add-logger-button").on("click", function (e) {
        e.preventDefault();
        var $dialog = $(this).parents(".modal");
        var logger = $("#new-logger-name").val();
        var level = $("#new-logger-level").val();
        var $group = $("<div>").addClass("control-group").hide();
        $("<label>").addClass("col-form-label").attr("for", logger)
            .append($("<code>").text(logger)).appendTo($group);
        $("<div>").addClass("controls")
            .append($("#rootLogger select").clone().attr("name", logger).addClass("logger").val(level))
            .append($("<a>").addClass("remove-logger").attr({ "title": "Remove Logger", "href": "#" })
                .append($("<i>").addClass("icon-trash")))
            .appendTo($group);
        $("#level-container").append($group);
        $group.slideDown();
        $dialog.modal("hide");
    });
});