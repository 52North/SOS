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
$(function() {

    function Set() {
        this.data = {};
    }

    Set.prototype = {
        add: function(key, val) {
            if (val === undefined) {
                val = true;
            }
            if (key === undefined || key ===  null) {
                throw new Error("invalid key");
            }
            if (typeof key === "object") {
                if (key.length) {
                    for (var i = 0; i < key.length; i++) {
                        this.data[key[i]] = val;
                    }
                } else {
                    for (var index in key) {
                        if (key.hasOwnProperty(index)) {
                            this.add(index, key[index]);
                        }
                    }
                }
            } else {
                this.data[key] = val;
            }
        },
        get: function(key) {
            return this.data[key];
        },
        remove: function(key) {
            var item;
            for (var j = 0; j < arguments.length; j++) {
                item = arguments[j];
                if (typeof key === "string") {
                    delete this.data[item];
                } else if (item.length) {
                    for (var i = 0; i < item.length; i++) {
                        delete this.data[item[i]];
                    }
                }
            }
        },
        has: function(key) {
            return Object.prototype.hasOwnProperty.call(this, key);
        },
        isEmpty: function() {
            for (var key in this.data) {
                if (this.has(key)) {
                    return false;
                }
            }
            return true;
        },
        keys: function() {
            return Object.keys(this.data);
        },
        clear: function() {
            this.data = {};
        }
    };
    function Client(config) {
        var self = this;
        this.sosUrl = config.sosUrl;
        this.availableOperations = config.availableOperations;
        this.requests = this.filter(config.examples);
        this.$response = $("#response");
        this.$contentType = $("#content-type");
        this.$accept = $("#accept");
        this.$method = $("#method");

        this.$request = $("#request").on("change",function() {
            self.onRequestChange.apply(self, arguments);
        });
        this.$url = $("#url").val(this.sosUrl).on("change", function() {
            self.onUrlChange.apply(self, arguments);
        });
        this.$send = $("#send-button").on("click", function() {
            self.onSend.apply(self, arguments);
        });

        this.$contentType.on("change", function(){
            self.onContentTypeChange.apply(self, arguments);
        });

        this.editor = CodeMirror.fromTextArea($("#editor").get(0), {
            mode: "application/xml",
            tabSize: 2,
            autoCloseTags: true,
            lineNumbers: true,
            lineWrapping: true,
            foldGutter: {
                rangeFinder: new CodeMirror.fold.combine(
                    CodeMirror.fold.xml,
                    CodeMirror.fold.brace,
                    CodeMirror.fold.comment)
            },
            gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"]
        });

        // select the language for syntax highlighting
        $("#mode a").on("click", function(e) {
            e.preventDefault(); self.changeSyntax($(this).data("mode"));
        });
        $("#permalink").on("click", function() {
            window.prompt("Permalink", self.createPermalink());
        });
        this.createFilters("service", "version", "binding", "operation");
        this.updateExamples();
        this.$url.trigger("change");
        this.processQuery();
    }
    Client.prototype = {
        processQuery: function() {
            if ($.queryParam["load"]) {
                this.loadRequestById($.queryParam["load"]);
            }
            if ($.queryParam["contentType"]) {
                this.$contentType.val($.queryParam["contentType"]).trigger("change");
            }
            if ($.queryParam["accept"]) {
                this.$accept.val($.queryParam["accept"]).trigger("change");
            }
            if ($.queryParam["method"]) {
                this.$method.val($.queryParam["method"]).trigger("change");
            }
            if ($.queryParam["url"]) {
                this.$url.val($.queryParam["url"]).trigger("change");
            }
            if ($.queryParam["request"]) {
                this.editor.setValue($.queryParam["request"]);
            }
        },
        createPermalink: function() {
            var link = document.location.protocol
                    +  document.location.host
                    +  document.location.pathname;
            link += "?method=" + encodeURIComponent(this.$method.val());
            link += "&accept=" + encodeURIComponent(this.$accept.val());
            link += "&contentType=" + encodeURIComponent(this.$contentType.val());
            link += "&url=" + encodeURIComponent(this.$url.val());
            link += "&request=" + encodeURIComponent(this.editor.getValue());
            return link;
        },
        onContentTypeChange: function() {
            var ct = this.$contentType.val();
            if (ct.matches(/application\/json/)) {
                this.changeSyntax("application/json");
            } else if (ct.matches(/(application|text)\/(soap\+)?xml/)) {
                this.changeSyntax("application/xml");
            }
        },
        createFilters: function() {
            var self = this, e, pl, jq, id, i, j;
            this.filters = [];
            for (i = 0; i < arguments.length; ++i) {
				e = arguments[i],
				pl = e + "s",
				jq = "$" + e,
				id = "#" + e;
                // currently selected value
                this[e] = null;
                this.filters.push(e);
                // supported values
                this[pl] = new Set();
                this.availableOperations.forEach(function(r) {
					// check if we actually have a matching request example
					for (j = 0; j < self.requests.length; ++j) {
						if (self.requests[j][e] === r[e]) {
							self[pl].add(r[e]);
							break;
						}
					}
                });
                this[pl] = this[pl].keys();
                this[pl].sort();
                // JQuery object
                // javascripts great loop closures......
                this[jq] = $(id).on("change", (function(filter) {
                    return function() {
                        self.onFilterChange.apply(self, [filter]);
                    };
                })(e));
                // append valid values
                this[jq].append($("<option>").attr("value", "any")
                    .text("Any " + e.slice(0, 1).toUpperCase() + e.slice(1)));
                this[pl].forEach(function(s) {
                    self[jq].append($("<option>").attr("value", s)
							.text(self.toHumanReadableString(s)));
                });

                this[jq].trigger("change");
                this[jq].attr("disabled", this[jq].find("option").length <= 1);
            }
        },
		toHumanReadableString: function(contentType) {
			switch (contentType) {
				case "application/x-kvp":
					return "KVP";
				case "application/soap+xml":
					return "SOAP";
				case "text/xml":
				case "application/xml":
					return "POX";
				case "application/json":
					return "JSON";
				default:
					return contentType;
			}
		},
        filter: function(requests) {
            var filtered = [];
            for (var i = 0; i < requests.length; ++i) {
                if (this.isSupported(requests[i])) {
                    filtered.push(requests[i]);
                }
            }
            filtered.sort(function(a, b) {
                if (a.binding === b.binding) {
                    if (a.service === b.service) {
                        if (a.version === b.version) {
                            if (a.operation === b.operation) {
                                if (a.title === b.title) {
                                    return 0;
                                } else { return a.title < b.title ? -1 : 1; }
                            } else { return a.operation < b.operation ? -1 : 1; }
                        } else { return a.version < b.version ? -1 : 1; }
                    } else { return a.service < b.service ? -1 : 1; }
                } else { return a.binding < b.binding ? -1 : 1; }
            });
            return filtered;
        },
        changeSyntax: function(type) {
            this.editor.setOption("mode", type);
        },
        isSupported: function(request) {
            var i, ao;
            if (request.requiredOperations) {
                for (i = 0; i < request.requiredOperations.length; ++i) {
                    if (!this.isSupported(request.requiredOperations[i])) {
                        return false;
                    }
                }
            }
            for (i = 0; i < this.availableOperations.length; ++i) {
                ao = this.availableOperations[i];
                if (request.service !== ao.service) {
                    continue;
                }
                if (request.version !== ao.version) {
                    continue;
                }
                if (request.operation !== ao.operation) {
                    continue;
                }
                if (request.binding !== ao.binding) {
                    continue;
                }
                if (request.method !== ao.method) {
                    continue;
                }
                return true;
            }
            return false;
        },
        appendDefaultOption: function(text, $select) {
            $select.append($("<option>")
                    .attr({"disabled": true, "selected": true})
                    .hide().val("").html(text));
        },
        obj2param: function(obj) {
            var q = [];
            for (var key in obj)
                q.push(key + "=" + encodeURIComponent(
                        (obj[key] instanceof Array) ? obj[key].join(",") : obj[key]));
            return q.join("&");
        },
        xml2string: function(xml) {
            return typeof(xml) === "string" ? xml : xml.xml ? xml.xml : new XMLSerializer().serializeToString(xml);
        },
        onUrlChange: function() {
            var url = this.$url.val();
            //remove query string if present, we just want to examine the binding
            if (url.indexOf('?' > -1)) {
            	url = url.split("?")[0];
            }
            if (url.endsWith("json")) {
                this.$contentType.val("application/json");
                this.$method.val("POST");
            } else if (url.endsWith("pox")) {
                this.$contentType.val("application/xml");
                this.$method.val("POST");
            } else if (url.endsWith("soap")) {
                this.$contentType.val("application/soap+xml");
                this.$method.val("POST");
            } else if (url.endsWith("kvp")) {
                this.$method.val("GET");
                this.$contentType.val("");
            }
            this.$contentType.trigger("change");
            this.$method.trigger("change");

            if (url) {
                this.$send.removeAttr("disabled");
            } else {
                this.$send.attr("disabled", true);
            }
        },
        getContentTypeHeader: function() {
            var v = this.$contentType.val();
            return v ? v : null;
        },
        getAcceptHeader: function() {
            var v = this.$accept.val();
            return v ? v : null;
        },
        getMethod: function() {
            return this.$method.val();
        },
        showResponse: function(xhr) {
            var xml = this.xml2string(xhr.responseText);
            this.$response.fadeOut("fast").children().remove();
            this.$response.append($("<h3>").text("Response"));
            this.$response.append($("<pre>").text((xhr.status + " " + xhr.statusText + "\n" + xhr.getAllResponseHeaders()).trim()));
            this.$response.append($("<pre>").addClass("prettyprint").addClass("linenums").text(xml));
            prettyPrint();
            this.$response.fadeIn("fast");
            $("html, body").animate({ scrollTop: this.$response.offset().top }, "slow");
        },
        onServiceResponse: function(xhr, status) {
            var contentType;
            switch (status) {
                case "success":
                    this.showResponse(xhr);
                    break;
                case "notmodified":
                    this.showResponse(xhr);
                    break;
                case "error":
                    showError("Request failed: " + xhr.status + " " + xhr.statusText);
                    var contentType = xhr.getResponseHeader("Content-Type");
                    if (xhr.responseText && xhr.responseText.indexOf("ExceptionReport") >= 0) {
                        this.showResponse(xhr);
                    } else if (contentType && (contentType.startsWith("application/json") ||
                                               contentType.startsWith("text/xml") ||
                                               contentType.startsWith("application/xml") ||
                                               contentType.startsWith("application/soap+xml"))) {
                        this.showResponse(xhr);
                    }
                    break;
                case "timeout":
                    this.showError("Request timed out &hellip;");
                    break;
                case "abort":
                    this.showError("Request aborted &hellip;");
                    break;
                case "parsererror":
                    this.showError("Unparsable response &hellip;");
                    break;
            }
            this.$send.removeAttr("disabled");
        },
        onSend: function() {
            var self = this,
                method = this.getMethod(),
                request = $.trim(this.editor.getValue()),
                contentType = this.getContentTypeHeader(),
                accept = this.getAcceptHeader(),
                options;
			this.$send.attr("disabled", true);
			options = { headers: {}, type: method, complete: function() {
				self.onServiceResponse.apply(self, arguments);
			}};

			if (contentType) { options.headers["Content-Type"] = contentType; }
			if (accept) { options.headers["Accept"] = accept; }
			if (request) { options.data = request; }

			$.ajax(this.$url.val(), options);
        },
        onFilterChange: function(filter) {
            var old = this[filter];
            this[filter] = this["$" + filter].val();
            if (this[filter] === "any") {
                this[filter] = null;
            }
            if (this[filter] !== old) {
                this.updateExamples();
            }
        },
        updateExamples: function() {
            var def, text, id, example, examples = {};

            // apply current filters
            outer: for (var i = 0; i < this.requests.length; ++i) {
                example = this.requests[i];
                for (var j = 0; j < this.filters.length; ++j) {
                    var f = this.filters[j];
                    if (this[f] && this[f] !== example[f]) {
                        continue outer;
                    }
                }
                examples[i] = example;
            }
            // default option
            this.$request.children().remove();
            $("<option>")
                .attr("disabled", true)
                .attr("selected", true)
                .hide()
                .val("")
                .html("Load a example request &hellip;")
                .appendTo(this.$request);

            // fill the drop down
            for (id in examples) {
                if (examples.hasOwnProperty(id)){
                    def = examples[id];
                    text = "";
                    text += "[" + this.toHumanReadableString(def.binding) + "]";
                    text += " " + def.operation;
                    if (def.title) {
                        text += " - " + def.title;
                    }
                    text += " (" + def.service   + " " + def.version   + ")";
                    $("<option>").attr({ value: id }).html(text).appendTo(this.$request);
                }
            }
            // disable if no examples match
            this.$request.attr("disabled", this.$request.find("option").length <= 1);
        },
        onRequestChange: function() {
            var id = this.$request.val();
            if (id >= 0) {
                this.loadRequest(this.requests[id]);
            }
            this.updateExamples();
        },
        loadRequestById: function(id) {
            for (var i = 0; i < this.requests.length; ++i) {
                if (this.requests[i].id === $.queryParam["load"]) {
                    this.loadRequest(this.requests[i]);
                    return;
                }
            }
            showMessage("The specified request could not be found or is not supported.", "warning");
        },
        loadRequest: function(definition) {
            var self = this;
            this.$method.val(definition.method);


            var url = this.sosUrl;
            if (definition.param) {
                if (!url.endsWith("?")) {
                    url += "?";
                }
                url += this.obj2param(definition.param);
            }
            this.$url.val(url).trigger("change");

            if (definition.headers) {
                if (definition.headers["Content-Type"]) {
                    this.$contentType.val(definition.headers["Content-Type"]);
                } else {
                    this.$contentType.val("");
                }

                if (definition.headers["Accept"]) {
                    this.$accept.val(definition.headers["Accept"]);
                } else {
                    this.$accept.val("");
                }
            } else {
            	this.$contentType.val("");
            	this.$accept.val("");
            }
            this.$contentType.trigger("change");
            this.$accept.trigger("change");

            if (definition.request) {
                if (typeof(definition.request) === "string") {
                    if (definition.request.endsWith("xml")) {
                        $.get(definition.request, function(data) {
                            var xml = self.xml2string(data);
                            self.editor.setValue(vkbeautify.xml(xml));
                        });
                    } else if (definition.request.endsWith("json")) {
                        $.get(definition.request, function(data) {
                            self.editor.setValue(JSON.stringify(data, undefined, 2));
                        });
                    } else {
                        this.editor.setValue(definition.request);
                    }
                } else if (typeof(definition.request) === "object") {
                    this.editor.setValue(JSON.stringify(definition.request, undefined, 2));
                }
            } else {
                this.editor.setValue("");
            }
        }
    };
    window.Client = Client;
});