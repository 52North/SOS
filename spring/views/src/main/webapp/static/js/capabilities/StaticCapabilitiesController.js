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

	function StaticCapabilitiesController(baseUrl)  {
		var self = this;
		
		this.sc = { 
			caps: {}, 
			current: null 
		};
		
		this.baseUrl = baseUrl;
		this.previous = null;
		this.$container = $("#static-capabilities");
		this.$id = $("#stcaps-id");
		this.$save = $("#stcaps-save");
		this.$publish = $("#stcaps-publish");
		this.$editor = $("#stcaps-editor");
		this.$newName = $("#stcaps-add-new-form-input");
		this.$delete = $("#stcaps-delete");
		this.$loadCurrent = $("#stcaps-load-current-button");
		this.$addNew = $("#stcaps-addnew");
		this.$editButtons = $(".stcaps-edit-button");
		this.$addNewOk = $("#stcaps-add-new-form-ok");
		this.$addNewForm = $("#stcaps-add-new-form");
		this.$validate = $("#stcaps-validate");
		
		this.bind();
		
		$.getJSON(this.getStaticCapabilitiesUrl(), function() {
			self.init.apply(self, arguments);
		}).fail(function(e) {
			showError("Request failed: " + e.status + " " + e.statusText);
		});
	}
	$.extend(StaticCapabilitiesController.prototype, EventMixin);
	$.extend(StaticCapabilitiesController.prototype, {

		onValidate: function() {
			var xml = this.getEditorContent();
			this.$validate.disabled();
			$.ajax({
				type: "POST",
				url: this.baseUrl + "admin/capabilities/ajax/validation",
				contentType: "application/xml",
				dataType: "json",
				data: xml,
				context: this
			}).done(function(json) {
				this.$validate.disabled(false);
				this.displayValidationResults(json);
			}).fail(function() {
				showError("Error validating document: <code>" + e.status + " " 
					+ e.statusText + "</code> " + e.responseText);
				this.$validate.disabled(false);
			});
		},

		displayValidationResults: function(results) {
			var $results = this.$container.find(".validation-result");
			if ($results.length === 0) {
				$results = $("<div>").addClass("validation-result").prependTo(this.$container);
			}
			$results.slideUp(function() {
				$results.children().remove();
				$("<button>").attr({
					"type": "button", 
					"class": "close", 
					"data-dismiss": "alert"
				}).html("&times;").appendTo($results);

				$results.addClass("alert fade in")
						.removeClass("alert-block alert-success alert-error");

				if (results.valid) {
					$results.addClass("alert-success").append($("<h5>Validation succeeded</h5>"));
				} else {
					$results.addClass("alert-error").append($("<h5>Validation failed</h5>"));

					var $ul = $("<ul>");
					for (var i = 0; i < results.errors.length; ++i) {
						$("<li>").append($("<small>").text(results.errors[i])).appendTo($ul);
					}

					$results.append($ul);
				}
				$results.slideDown();
			});
		},

		getStaticCapabilitiesUrl: function(identifier) {
			return this.baseUrl + "admin/capabilities/ajax/staticCapabilities"
					+ ((identifier) ? "/" + encodeURIComponent(identifier) : "");
		},

		onToggle: function($el, status, _) {
			var name = this.getSelected();
			if (status && this.getCurrent() !== name) {
				if (!this.sc.caps[name] || !this.sc.caps[name].saved) {
					this.save(function(success) {
						if (success) { 
							this.setCurrent(name, function(success) { 
								if (!success) { 
									$el.toggleButtons("toggleState", true);	
								} 
							});
						} else { 
							$el.toggleButtons("toggleState", true); 
						}
					});
				} else {
					this.setCurrent(name, function(success) {
						if (!success) { 
							$el.toggleButtons("toggleState", true); 
						}
					});
				}
			} else if (!status && this.getCurrent() === name) {
				this.setCurrent(null, function(success) {
					if (!success) { 
						$el.toggleButtons("toggleState", true);	
					}
				});
			}
		},

		onEditorChange: function() {
			var name = this.getSelected();
			if (this.sc.caps[name]) {
				this.sc.caps[name].changed = true;
				this.$save.disabled(false);
			}
		},

		onIdChange: function() {
			var name = this.getSelected();
			if (name) {
				this.$publish.removeClass("deactivate");
				if (this.previous && this.sc.caps[this.previous]) {
					/* forget changes of previous selected */
					this.sc.caps[this.previous].changed = false;
				}

				if (this.previous !== name) {
					this.$publish.toggleButtons("setState", (this.getCurrent() === name));
				}

				/* load caps to editor */
				this.setEditorContent(this.getCapsAsString(name));
				this.$editButtons.disabled(false);
				this.$save.disabled(true);
				this.previous = name;
			} else {
				this.$editButtons.disabled(true);
				this.$editor.codeMirror("set", "");
			}
			if (this.$id.find("option").length > 1) {
				this.$id.disabled(false);	
			} else {
				this.$id.disabled(true);
				this.$publish.addClass("deactivate");
			}
		},

		save: function(callback) {
			var name = this.getSelected(), 
				xml = this.getEditorContent(),
				self = this;
			$.ajax({
				type: "POST",
				url: this.getStaticCapabilitiesUrl(name),
				contentType: "application/xml",
				data: xml
			}).done(function() {
				self.sc.caps[name].xml = xml;
				self.sc.caps[name].saved = true;
				self.sc.caps[name].changed = false;
				showSuccess("Static capabilities '" + name + "' saved.");
				if ($.isFunction(callback)) { 
					callback.call(self, true); 
				}
				self.fire("change");
			}).fail(function(e) {
				showError("Error setting static capabilities '" + name + "': <code>" 
					+ e.status + " " + e.statusText + "</code> " + e.responseText);
				if ($.isFunction(callback)) {
					callback.call(self, false); 
				}
			});
		},

		setCurrent: function(name, callback) {
			var name = name || null,
				self = this;
			if (this.sc.current !== name) {
				$.ajax({
					type: "POST",
					url:  this.getStaticCapabilitiesUrl(),
					contentType: "application/json",
					data: JSON.stringify({ current: name })
				}).done(function(e) {
					self.sc.current = name;
					if (name) {
						showSuccess("Static capabilities '" + name  + "' set.");	
					} else {
						showSuccess("Reverted to dynamic capabilities.");
					}
					if ($.isFunction(callback)) { 
						callback.call(self, true); 
					}
					self.fire("change");
				}).fail(function(e) {
					showError("Error setting static capabilities '" + name 
						+ "': <code>" + e.status + " " + e.statusText+"</code> " 
						+ e.responseText);
					if ($.isFunction(callback)) { 
						callback.call(self, false); 
					}
				});
			}
		},

		deleteStaticCapabilities: function(callback) {
			var name = this.getSelected(),
				self = this;
			if (confirm("Do you really want to delete the static capabilities '" + name + "'?")){ 
				$.ajax({
					type: "DELETE",
					url: this.getStaticCapabilitiesUrl(name)
				}).done(function(e) {
					self.sc.current = name;
					self.fire("change");
					showSuccess("Deleted Static capabilities '" + name + "'.");
					if ($.isFunction(callback)) { 
						callback.call(self, true); 
					}
					self.fire("change");
				}).fail(function(e) {
					showError("Error deleting static capabilities '" + name + "': <code>" 
							+ e.status + " " + e.statusText + "</code> " + e.responseText);
					if ($.isFunction(callback)) { 
						callback.call(self, false); 
					}
				});
			}
		},

		onDelete: function() {
			var name = this.getSelected();
			/* delete remotely */ 
			if (this.sc.caps[name].saved) {
				this.deleteStaticCapabilities(function(success) {
					/* remove from dropdown */
					if (success) {
						this.$id.find("option:selected").remove();
						this.$id.trigger("input");
						/* remove from 'cache' */
						delete this.sc.caps[name];
						if (this.sc.current === name) {
							this.sc.current = null;
						}
					}
				});
			}
		},

		loadCurrentCapabilities: function() {
			var self = this;
			this.$loadCurrent.disabled();
			$.ajax({
				url: this.baseUrl + "sos/kvp?",
				type: "GET",
				dataType: "xml",
				data: { service: "SOS", request: "GetCapabilities" }
			}).done(function(e) {
				self.setEditorContent(xml2string(e));
				self.$loadCurrent.disabled(false);
			}).fail(function(e) {
				showError("Request failed: " + e.status + " " + e.statusText);
				self.$loadCurrent.disabled(false);
			});
		},

		onSave: function() {
			this.$save.disabled(true);
			this.save(function(success) {
				this.$save.disabled(success);
			});
		},

		init: function(json) {
			for (var name in json.caps) {
				this.sc.caps[name] = { 
					xml: json.caps[name], 
					saved: true 
				};
				this.$id.append($("<option>").text(name));
			}
			this.$id.trigger("input");
			this.sc.current = json.current;
			this.fire("ready");
		},

		onNewNameChange: function() {
			var val = this.$newName.val(),
				disabled = !!(!val || this.sc.caps[val]) || val === "dynamic";
			/* invalid if identifier empty or already present */
			
			this.$addNewOk.disabled(disabled);
			if (disabled) {
				this.$newName.parents(".control-group").addClass("error");
			} else {
				this.$newName.parents(".control-group").removeClass("error");
			}
		},

		onNewNameOk: function() {
			var name = this.$newName.val();
			this.sc.caps[name] = { 
				saved: false, 
				xml: "" 
			};
			this.$id.append($("<option>").text(name)).val(name).trigger("input");
		},

		bind: function() {
			var self = this;
			this.$editor.codeMirror({ 
				mode: "xml", 
				lineNumbers: true, 
				lineWrapping: true, 
				onChange: function() {
					self.onEditorChange.apply(self, arguments); 
				} 
			});
			this.$addNew.on("click", function() {
				self.$addNew.slideLeft(100, function() {
					self.$addNewForm.slideRight();
				});
			});
			$(".stcaps-add-new-form-button").on("click", function() {
				self.$addNewForm.slideLeft(400, function() {
					self.$addNew.slideRight(100);
					self.$newName.val("").trigger("input");
				});
			});
			this.$id.on("input select change", function() { 
				self.onIdChange.apply(self, arguments); 
			}).trigger("input");
			this.$delete.on("click", function() { 
				self.onDelete.apply(self, arguments); 
			});
			this.$save.on("click", function() { 
				self.onSave.apply(self, arguments); 
			});
			this.$loadCurrent.on("click", function() { 
				self.loadCurrentCapabilities.apply(self, arguments); 
			});
			this.$publish.toggleButtons({ 
				onChange: function() { 
					self.onToggle.apply(self, arguments);
				}
			});
			this.$validate.on("click", function() {
				self.onValidate.apply(self, arguments);
			});
			this.$newName.on("input change keyup", function() { 
				self.onNewNameChange.apply(self, arguments); 
			}).trigger("input");
			this.$addNewOk.on("click", function() { 
				self.onNewNameOk.apply(self, arguments); 
			});
		},

		getSelected: function() { 
			return this.$id.val(); 
		},

		getCurrent: function() { 
			return this.sc.current; 
		},

		isStatic: function() {
			return !!(this.getCurrent());
		},

		getEditorContent: function() { 
			return this.$editor.codeMirror("get"); 
		},

		setEditorContent: function(x) { 
			this.$editor.codeMirror("set", x); 
		},

		getCapsAsString: function(name) { 
			return xml2string(this.sc.caps[name].xml); 
		},
	});
	

	window.StaticCapabilitiesController = StaticCapabilitiesController;

}(jQuery);