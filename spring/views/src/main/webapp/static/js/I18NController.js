/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
!function($) {

    function I18NController(options)  {
        this.identifiers = options.identifiers;
        this.selected = null;
        this.properties = options.properties;
        this.$div = $(options.div);
        this.$container = null;
        this.endpoint = options.url;
        this.name = options.name;
        this._init();

    }

    $.extend(I18NController.prototype, EventMixin);
    $.extend(I18NController.prototype, {
        _init: function() {
            this._createSelect();
            this._createContainer();
            this._bind();
        },
        _bind: function() {
            this.on("select", this._onSelect);
        },
        _createContainer: function() {
            this.$container = $("<div>").addClass("i18ncontainer");
            this.$div.append(this.$container);
        },
        _createSelect: function() {
            var i;
            var self = this;
            var $select = $("<select>").addClass("span12");

            $("<option>").hide().val("")
                    .attr({"disabled": true, "selected": true})
                    .html("Select a " + this.name + " &hellip;")
                    .appendTo($select);

            for (i = 0; i < this.identifiers.length; ++i) {
                $("<option>")
                    .val(this.identifiers[i])
                    .text(this.identifiers[i])
                    .appendTo($select);
            }
            $select.on("change", function(e) {
                var selected = $(this).val();
                if (selected !== self.selected) {
                    self.selected = selected;
                    self.fire("select", selected);
                }
            });
            this.$div.append($select);
        },
        _onSelect: function(id) {
            $.ajax({
                url: this.endpoint,
                data: { id: id },
                dataType: "json",
                context: this
            })
            .done(this._present)
            .fail(function() {
                showError("I18N for " + this.name + " <code> "
                    + data.id + "</code> could not be requested.")
            });;
        },
        _present: function(data) {
            var self = this;
            this.$container.fadeOut(function() {
                self.$container.children().remove();
                var property, settings = {};
                for (property in self.properties) {
                    if (self.properties.hasOwnProperty(property)) {
                        settings[property] = {
                            "title": self.properties[property],
                            "type": "multilingual",
                            "default": data[property]
                        }

                    }
                }
                generateSettings({sections: [{settings: settings}]}, {}, self.$container, false);
                var $btn = $("<button>").attr("type", "button").addClass("btn").text("Save")
                var $wrap = $("<div>").addClass("pull-right control-group").append($btn);

                $btn.on("click", function() {
                    self._onSave(data);
                })

                self.$container.append($wrap);
                self.$container.fadeIn();
            });
        },
        _onSave: function(data) {
            var val, property;
            for (property in this.properties) {
                if (this.properties.hasOwnProperty(property)) {
                    val = this.$container.find("input[name=" + property + "]").val();
                    if (!val)  {
                        val = {};
                    } else {
                        val = JSON.parse(val);
                    }
                    data[property] = val;
                }
            }
            this._save(data);
        },
        _save: function(data) {
            $.ajax({
                type: "POST",
                url: this.endpoint,
                contentType: "application/json",
                data: JSON.stringify(data),
                context: this
            }).done(function() {
                showSuccess("I18N for " + this.name + " <code> "
                    + data.id + "</code> saved.");
            }).fail(function() {
                showError("I18N for " + this.name + " <code> "
                    + data.id + "</code> could not be saved.")
            });
        }

    });

    window.I18NController = I18NController;

}(jQuery);