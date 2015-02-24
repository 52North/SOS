/*
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
if (typeof String.prototype.startsWith !== "function") {
    String.prototype.startsWith = function(str) {
        "use strict";
        if (this === null)
            throw new TypeError();
        return this.slice(0, str.length) === str;
    };
}

if (typeof String.prototype.endsWith !== "function") {
    String.prototype.endsWith = function(str) {
        "use strict";
        if (this === null)
            throw new TypeError();
        return this.slice(-str.length) === str;
    };
}

if (!String.prototype.matches) {
    String.prototype.matches = function(regexp) {
        "use strict";
        if (this === null)
            throw new TypeError();
        return this.match(regexp) ? true : false;
    };
}

(function($) {
    $.queryParam = (function(a) {
        if (a === "") {
            return {};
        }
        var b = {};
        for (var i = 0; i < a.length; ++i) {
            var p = a[i].split('=');
            if (p.length !== 2)
                continue;
            b[p[0]] = decodeURIComponent(p[1].replace(/\+/g, " "));
        }
        return b;
    })(window.location.search.substr(1).split('&'));
})(jQuery);

function showMessage(text, type, autoclose) {
    function closeAlert(a) {
        a.fadeTo(500, 0).slideUp(500, function() {
            a.remove();
        });
    }
    var $alert = $("<div>");
    $alert.addClass("alert alert-" + type).append(text);
    $("<button>").attr("type", "button").addClass("close").click(function() {
        closeAlert($alert);
    }).html("&times;").prependTo($alert);
    $alert.hide().prependTo($("#content")).css("opacity", 0).slideDown(500).animate({
        opacity: 1
    }, {
        queue: false,
        duration: 1000
    });
    if (autoclose) {
        window.setTimeout(function() {
            closeAlert($alert);
        }, (typeof(autoclose) === "number") ? autoclose : 5000);
    }
}

function showError(error, autoclose) {
	if (autoclose === undefined) {
		autoclose = false; 
	}
    showMessage("<strong>Error!</strong> " + error, "error", autoclose);
}

function showSuccess(message) {
    showMessage("<strong>Success!</strong> " + message, "success", true);
}

function generateSettings(settings, container, tabbed) {
    function required() {
        var valid = $(this).val() === "";
        if (valid) {
            $(this).parents(".control-group").addClass("error");
        } else {
            $(this).parents(".control-group").removeClass("error");
        }
    }

    function generateSetting(setting) {
        var $setting = $("<div>").addClass("control-group").attr("id", setting.id.toLowerCase().replace(/\W/g, "_"));
        switch (setting.type) {
        //TODO add validation of parameters
        case "integer":
        case "number":
        case "password":
        case "text":
        case "string":
            var $label = $("<label>").addClass("control-label").attr("for", setting.id).html(setting.title);
            var $controls = $("<div>").addClass("controls");
            var $input = null;
            switch (setting.type) {
            case "integer":
                // TODO slider
            case "string":
                $input = $("<input>").attr("type", "text").attr("name", setting.id).addClass("span8");
                break;
            case "password":
                $input = $("<input>").attr("type", "password").attr("name", setting.id).addClass("span8");
                break;
            case "text":
                $input = $("<textarea>").attr("rows", 5) // TODO make this a setting
                .attr("name", setting.id).addClass("span8");
                break;
            }
            if (setting["default"] !== undefined && setting["default"] !== null) {
                $input.val(setting["default"]);
            }
            var $description = $("<span>").addClass("help-block").html(setting.description);
            if (setting.required) {
                var $required = $("<span>").addClass("label label-warning").text("required");
                $description.prepend(" ").prepend($required);
                $input.bind("keyup input", required);
                $input.addClass("required");
            } else {
                var $optional = $("<span>").addClass("label label-info").text("optional");
                $description.prepend(" ").prepend($optional);
            }
            $setting.append($label).append($controls.append($input).append($description));
            break;
        case "choice":
            var $controls = $("<div>").addClass("controls");
            var $label = $("<label>").attr("for", setting.id).addClass("control-label").text(setting.title);
            var $input = $("<select>").attr("name", setting.id).addClass("span8");
            $.each(setting.options, function(val, desc) {
                $("<option>").attr("value", val).text(desc).appendTo($input);
            });
            if (setting["default"] === undefined || setting["default"] === null) {
                var $option = $("<option>").attr("value", "").attr("selected", true);
                $input.prepend($option);
                if (setting.required) {
                    $input.addClass("required");
                    $option.attr("disabled", true).css("display", "none");
                } else {
                    var $optional = $("<span>").addClass("label label-info").text("optional");
                    $description.prepend(" ").prepend($optional);
                }
            } else {
                $input.val(setting["default"]);
            }
            var $description = $("<span>").addClass("help-block").html(setting.description);
            if (setting.required) {
                var $required = $("<span>").addClass("label label-warning").text("required");
                $description.prepend(" ").prepend($required);
                $input.bind("change", required);
            }
            $setting.append($label).append($controls.append($input).append($description));
            break;
        case "boolean":
            var $controls = $("<div>").addClass("controls");
            var $input = $("<input>").attr("type", "checkbox").attr("name", setting.id);
            var $label = $("<label>").addClass("checkbox").text(setting.title);
            var $description = $("<span>").addClass("help-block").html(setting.description);
            $setting.append($label).append($controls.append($label.prepend($input)).append($description));
            if (typeof setting["default"] === "boolean") {
                $input.attr("checked", setting["default"]);
            }
            break;
        }
        return $setting;
    }

    function generateTabbedSection(section, $tabTitles, $tabs) {
        if (!section.title) {
            return;
        } /* generate the tab title */
        section.id = section.title.toLowerCase().replace(/\W/g, "_");
        var $tabHead = $("<li>").append($("<a>").text(section.title).attr("href", "#" + section.id).attr("data-toggle", "tab")); /* generate the tab pane */
        var $tabPane = $("<div>").addClass("tab-pane").attr("id", section.id);
        if (section.description) {
            $("<p>").html(section.description).appendTo($tabPane);
        }
        $.each(section.settings, function(id, setting) {
            setting.id = id;
            $tabPane.append(generateSetting(setting));
        });
        $tabs.append($tabPane);
        $tabTitles.append($tabHead);

    }

    function generateSection(section, $container) {
        if (!section.title) {
            return;
        }
        $("<legend>").text(section.title).appendTo($container);
        if (section.description) {
            $("<p>").html(section.description).appendTo($container);
        }
        $.each(section.settings, function(id, setting) {
            setting.id = id;
            $container.append(generateSetting(setting));
        });
    }
    var $container = $(container);
    if (tabbed) {
        var $tabTitles = $("<ul>").addClass("nav nav-tabs");
        var $tabs = $("<div>").addClass("tab-content");
        $.each(settings.sections, function(_, section) {
            generateTabbedSection(section, $tabTitles, $tabs);
        });
        $tabs.children(":first").addClass("active");
        $tabTitles.children(":first").addClass("active");
        $container.append($tabTitles).append($tabs);
    } else {
        $.each(settings.sections, function(_, section) {
            generateSection(section, $container);
        });
    }
    $container.find(":input").trigger("input change");
}

function setSetting(id, val, settings) {
    for (var section in settings.sections) {
        for (var setting in settings.sections[section].settings) {
            if (setting === id) {
                switch (settings.sections[section].settings[setting].type) {
                case "integer":
                case "string":
                case "number":
                case "password":
                    $("input[name='" + setting + "']").val(val);
                    break;
                case "text":
                    $("textarea[name='" + setting + "']").val(val);
                    break;
                case "choice":
                    $("select[name='" + setting + "']").val(val);
                    break;
                case "boolean":
                    if (val === "true" || val === true) {
                        $("input[name='" + setting + "']").attr("checked", true);
                    } else {
                        $("input[name='" + setting + "']").removeAttr("checked");
                    }
                    
                    break;
                }
                return;
            }
        }
    }
}

var jdbc = {};

function buildJdbcString(j) {
    var string = j.scheme + "://" + j.host;
    if (j.port !== undefined) {
        string += ":" + j.port;
    }
    string += "/" + encodeURIComponent(j.db);
    if (j.user || j.pass) {
         string += "?";
    }
    if (j.user) {
        string += "user=" + encodeURIComponent(j.user);
    }
    if (j.pass) {
        if (j.user) string += "&";
        string += "password=" + encodeURIComponent(j.pass);
    }
    return encodeURI(string);
}

function parseJdbcString(j) {
    var parsed = parseUri(j.replace("jdbc:",""));
    return {
        scheme: parsed.protocol,
        port: parsed.port,
        user: parsed.queryKey.user,
        pass: parsed.queryKey.password,
        host: parsed.host,
        db: parsed.path.slice(1)
    };
}

function setJdbcString() {
    var $this = $(this);
    var id = $this.attr("id").replace(/-input/, "");
    jdbc[id] = $this.val();
    $("#jdbc-input").val(buildJdbcString(jdbc));
}

function setJdbcInputs() {
    jdbc = parseJdbcString($(this).val());
    for (key in jdbc) {
        $("#" + key + "-input").val(jdbc[key]);
    }
}

function warnIfNotHttps() {
    if (!document.location.protocol.matches("^https")) {
        showMessage(
                "<b>Warning!</b> The password will be transferred in clear text. "
                + "Use this site only in safe environments (i.e. <b>NOT</b> on "
                + "public Wi-Fi or internet caf&eacute;s)!");
    }
}

function parsehash() {
    var h = document.location.hash;
    if (h) {
        var $t = $(h);
        if ($t.hasClass("tab-pane")) {
            $("a[href=" + h + "]").tab("show");
        } else if ($t.hasClass("control-group")) {
            $("a[href=#" + $t.parents(".tab-pane").attr("id") + "]").tab("show");
            $("html body").animate({scrollTop: $t.offset().top - 10}, 200);
        }
    }
}