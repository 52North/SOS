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

    var url = $("#url_settingDefinitions").attr("data-value");
    var settings = JSON.parse($("#settings_data").attr("data-value"));
    var url_settings = $("#url_settings").attr("data-value");
    var admin_username = $("#admin_username").attr("data-value");

    $.getJSON(url, function (settingDefinitions) {
        var $container = $("#settings"),
            $button = $("<button>").attr("type", "button").addClass("btn btn-info").text("Save");

        $button.click(function () {
            $.post(url_settings, $container.serializeArray())
                .fail(function (e) {
                    showError("Failed to save settings: " + e.status + " " + e.statusText);
                    $("input#admin_password_facade,input[name=admin_password],input[name=current_password]").val("");
                })
                .done(function () {
                    $("html,body").animate({ "scrollTop": 0 }, "fast");
                    showSuccess("Settings saved!");
                    $("input#admin_password_facade,input[name=admin_password],input[name=current_password]").val("");
                });
        });

        settingDefinitions.sections.push({
            "id": "credentials",
            "title": "Credentials",
            "settings": {
                "admin_username": {
                    "type": "string",
                    "title": "Admin name",
                    "description": "The new administrator user name.",
                    "required": true,
                    "default": admin_username
                },
                "current_password": {
                    "type": "password",
                    "title": "Current Password",
                    "description": "The current administrator password."
                },
                "admin_password_facade": {
                    "type": "string",
                    "title": "New Password",
                    "description": "The new administrator password."
                }
            }
        });

        generateSettings(settingDefinitions, settings, $container, true);
        $("#service_identification .control-group:first").before("<legend>Standard Settings</legend>");
        $("#service_provider .control-group:first").before("<legend>Standard Settings</legend>");
        $("#service_identification .control-group:last").before("<legend>Extended Settings</legend>");
        $("#service_provider .control-group:last").before("<legend>Extended Settings</legend>");
        $("<div>").addClass("form-actions").append($button).appendTo($container);

        function setSosUrl() {
            $("input[name='service.serviceURL']").val(window.location.toString()
                .replace(/admin\/settings.*/, "service")).trigger("input");
        }

        if (!settings["service.serviceURL"]) {
            setSosUrl();
        }

        $(".required").bind("keyup input change", function () {
            var valid = true;
            $(".required").each(function () {
                var val = $(this).val();
                return valid = (val !== null && val !== undefined && val !== "");
            });
            if (valid) {
                $button.removeAttr("disabled");
            } else {
                $button.attr("disabled", true);
            }
        });

        $(".required:first").trigger("change");

        $("input[name=admin_password_facade]").removeAttr("name").attr("id", "admin_password_facade");
        $("form#settings").append($("<input>").attr({ "type": "hidden", "name": "admin_password" }));
        $("input#admin_password_facade").bind('focus', function () {
            $(this).val($("input[name=admin_password]").val());
        }).bind('blur', function () {
            $(this).val($(this).val().replace(/./g, String.fromCharCode(8226)));
        }).bind("keyup input", function () {
            $("input[name=admin_password]").val($(this).val());
        });

        var $defaultButton = $("<button>").attr("type", "button")
            .attr("disabled", true).css("margin-left", "5px").addClass("btn")
            .text("Defaults").click(function () {
                function getSettings(section) {
                    for (var i = 0; i < settingDefinitions.sections.length; ++i) {
                        if (settingDefinitions.sections[i].title === section) {
                            return settingDefinitions.sections[i].settings;
                        }
                    }
                }
                var activeId = $(".tab-pane.active").attr("id");
                var section = $(".nav.nav-tabs li a[href=#" + activeId + "]").text();
                var s = getSettings(section);
                for (var key in s) {
                    if (key === "service.serviceURL") {
                        setSosUrl();
                    } else {
                        setSetting(key, (s[key]["default"] !== undefined
                            && s[key]["default"] !== null) ? s[key]["default"] : "", settingDefinitions);
                    }
                }
                $(".required").trigger("input").trigger("change");
            });
        $("div.form-actions").append($defaultButton);

        $('a[data-toggle=tab]').on('shown', function (e) {
            var id = $(e.target).attr("href");
            if (id === "#service_settings" || id === "#miscellaneous_settings") {
                $defaultButton.removeAttr("disabled");
            } else {
                $defaultButton.attr("disabled", true);
            }
        });

        parsehash();
    });
});
