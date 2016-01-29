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

	function OfferingExtensionController(baseUrl) {
		var self = this;
		this.baseUrl = baseUrl;
		this.endpoint = this.baseUrl + "admin/capabilities/ajax/offeringExtensions";
		this.oe = {};
		this.previous = null;
		this.previousOffering = null;
		this.$container = $("#offering-extensions");
		this.$id = $("#offext-id");
		this.$offId = $("#offext-off-id");
		this.$editor = $("#offext-editor");
		this.$save = $("#offext-save");
		this.$publish = $("#offext-enabled");
		this.$delete = $("#offext-delete");
		this.$addNew = $("#offext-addnew");
		this.$newName = $("#offext-add-new-form-input");
		this.$addNewForm = $("#offext-add-new-form");
		this.$addNewOk = $("#offext-add-new-form-ok");
		this.$editButtons = $(".offext-edit-button");
		this.$validate = $("#offext-validate");
		this.$disableAll = $("#offext-disable-all");

		this.bind();
		/* load existing offering extensions */
		$.getJSON(this.endpoint, function() {
			self.init.apply(self, arguments);
		}).fail(function(e) {
			showError("Request failed: " + e.status + " " + e.statusText);
		});
	}


	$.extend(OfferingExtensionController.prototype, EventMixin);
	$.extend(OfferingExtensionController.prototype, {
		
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

		getEnabledExtensions: function() {
			var r = {};
			for (var o in this.oe) {
				r[o] = [];
				for (var e in this.oe[o]) {
					if (this.oe[o][e].saved 
						&& !this.oe[o][e].disabled) {
						r[o].push(e);
					}
				}
			}
			return r;
		},

		getDisabledExtensions: function() {
			var r = {};
			for (var o in this.oe) {
				r[o] = [];
				for (var e in this.oe[o]) {
					if (this.oe[o][e].saved 
						&& this.oe[o][e].disabled) {
						r[o].push(e);
					}
				}
			}
			return r;
		},

		hasDisabledExtensions: function() {
			for (var o in this.oe) {
				for (var e in this.oe[o]) {
					if (this.oe[o][e].saved 
						&& this.oe[o][e].disabled) {
						return true;
					}
				}
			}
			return false;
		},

		hasEnabledExtensions: function() {
			for (var o in this.oe) {
				for (var e in this.oe[o]) {
					if (this.oe[o][e].saved 
						&& !this.oe[o][e].disabled) {
						return true;
					}
				}
			}
			return false;
		},

		getId: function() {
			return this.$id.val();
		},

		getOfferingId: function() {
			return this.$offId.val();
		},
		
		deleteOfferingExtension: function(callback)  {
			var name = this.getId(),
				offering = this.getOfferingId(),
				self = this;
			$.ajax({
				type: "DELETE",
				url: this.endpoint + "/delete",
				contentType: "application/json",
				data: JSON.stringify({
					"identifier" : name,
					"offeringId" : offering})
			}).done(function(e) {
				showSuccess("Deleted Extension '" + name + "' for Offering '" + offering + "'.");
				if ($.isFunction(callback)) { 
					callback.call(self, true); 
				}
				self.fire("change");
			}).fail(function(e) {
				showError("Error deleting Extension '" + name + "' for Offering '" 
					+ offering + "': <code>" + e.status + " " + e.statusText 
					+ "</code> " + e.responseText);
				if ($.isFunction(callback)) {
					callback.call(self, false);
				}
			});
		},

		getEditorContent: function() {
			return this.$editor.codeMirror("get");
		},

		publish: function(publish) {
			this.$publish.toggleButtons("setState", publish);
		},

		saveOfferingExtension: function(callback) {
			var name = this.getId(),
				offering = this.getOfferingId(),
				xml = this.getEditorContent(),
				self = this;
			$.ajax({
				type: "POST",
				url: this.endpoint + "/save",
				contentType: "application/json",
				data: JSON.stringify({
					"identifier" : name,
					"offeringId" : offering,
					"extensionContent" : xml
					})
			}).done(function() {
				self.oe[offering][name].extensionContent = xml;
				self.oe[offering][name].saved = true;
				self.oe[offering][name].changed = false;
				self.disablePublish(false);
				self.publish(!self.oe[offering][name].disabled);
				showSuccess("Extension '" + name + "' for Offering '" + offering + "' saved.");
				if ($.isFunction(callback)) { 
					callback.call(self, true);
				}
				self.fire("change");
			}).fail(function(e) {
				showError("Error saving Extension '" + name + "' for Offering '" + offering 
					+ "': <code>" + e.status + " " + e.statusText + "</code> " + e.responseText);
				if ($.isFunction(callback)) { 
					callback.call(self, false); 
				}
			});
		},

		enabledOfferingExtension: function(enabled, callback) {
			var name = this.getId(),
				offering = this.getOfferingId(),
				self = this;
			$.ajax({
				type: "POST",
				url: this.endpoint + "/edit",
				contentType: "application/json",
				data: JSON.stringify({
					"identifier": name,
					"offeringId": offering,
					"disabled": !enabled
					})
			}).done(function() {
				self.oe[offering][name].disabled = !enabled;
				showSuccess("Extension '" + name + "' for Offering '" + offering 
					+ "' " + (enabled ? "enabled" : "disabled") + ".");
				if ($.isFunction(callback)) { 
					callback.call(self, true);
				}
				self.fire("change");
			}).fail(function(e) {
				showError("Error " + (enabled ? "enabling" : "disabling") 
					+ " Extension '" + name + "' for Offering '" + offering 
					+ "': <code>" + e.status + " " + e.statusText + "</code> " 
					+ e.responseText);
				if ($.isFunction(callback)) {
					callback.call(self, false);
				}
			});
		},

		onOfferingChange: function() {
			var offering = this.getOfferingId();

			if (offering) {
				this.$addNew.disabled(false);
				if (this.previousOffering !== offering) {
					this.previousOffering = offering;
					// TODO fill extensions
					this.$id.find("option").remove();
					this.$id.append($("<option>").attr({
						disabled: "disabled", selected: "selected",
						value: "" }).text("Extension").hide());
					for (var name in this.oe[offering]) {
						this.$id.append($("<option>").text(name));
					}
					this.$id.trigger("input");
					this.previous = null;
				}
			} else {
				this.$addNew.disabled(true);
			}

			if (this.$id.find("option").length > 1) {
				this.$id.disabled(false);	
			} else {
				this.$id.disabled(true);
			}
		},

		onEditorChange: function(cm) {
			var offering = this.getOfferingId();
			var name = this.getId();
			if (this.oe[offering] && this.oe[offering][name]) {
				this.oe[offering][name].changed = true;
				this.$save.disabled(false);
			}
			if (!this.getEditorContent()) {
				this.$save.disabled(true);
			}
		},

		onToggleChange: function($el, status, e) {
			var name = this.getId(), 
				offering = this.getOfferingId();
			if (!this.oe[offering][name].saved) {
				this.saveOfferingExtension(function(s1) {
					if (s1) {
					   this.enabledOfferingExtension(status, function(s2){
							if (!s2) { 
								$el.toggleButtons("toggleState", true);	
							}
						});
					} else {
						$el.toggleButtons("toggleState", true);
					}
				});
			} else {
				this.enabledOfferingExtension(status, function(success){
					if (!success) { 
						$el.toggleButtons("toggleState", true);	
					}
				});
			}
		},

		setEditorContent: function(x) {
			this.$editor.codeMirror("set", x);
		},

		disablePublish: function(disabled) {
			if (disabled) {
				this.$publish.addClass("deactivate");
			} else {
				this.$publish.removeClass("deactivate");
			}
		},

		onIdChange: function() {
			var name = this.getId(),
				offering = this.getOfferingId();
			if (offering && name) {
				if (this.previous && this.oe[offering] && this.oe[offering][this.previous]) {
					/* forget changes of previous selected */
					this.oe[offering][this.previous].changed = false;
				}

				/* load capsext to editor */
				this.setEditorContent(xml2string(this.oe[offering][name].extensionContent));

				this.$editButtons.disabled(false);
				this.$publish.toggleButtons("setState", !this.oe[offering][name].disabled, true);
				this.disablePublish(!this.oe[offering][name].saved);
				this.$save.disabled(true);
				this.previous = name;
			} else {
				this.disablePublish(true);
				this.$editButtons.disabled(true);
				this.setEditorContent("");
			}
			this.$id.disabled(!(this.$id.find("option").length > 1));	
		},

		onDelete: function() {
			var name = this.getId(),
				offering = this.getOfferingId();
			
			/* delete remotely */ 
			if (confirm("Do you really want to delete extension '" + name + "' of offering '" + offering + "'?") && 
					this.oe[offering] &&
					this.oe[offering][name].saved) {
				this.deleteOfferingExtension(function(success) {
					/* remove from dropdown */
					if (success) {
						this.$id.find("option:selected").remove();
						this.$id.trigger("input");
						/* remove from 'cache' */
						delete this.oe[offering][name];
					}
				});
			}
		},

		onSave: function() {
			this.$save.disabled(true);
			this.saveOfferingExtension(function(success) {
//				this.$save.disabled(success);
			});
		},

		init: function(json) {
			this.oe = json;
			for (var offering in this.oe) {
				this.$offId.append($("<option>").text(offering));
				for (var name in this.oe[offering]) {
					this.oe[offering][name].saved= true;
				}
			}
			this.$offId.trigger("input");
			this.fire("ready");
		},

		onNewNameChange: function() {
			var val = this.$newName.val(),
				offering = this.getOfferingId(),
				disabled = !!(!val || this.oe[offering][val]);
			/* invalid if identifier empty or already present */
			this.$addNewOk.disabled(disabled);
			if (disabled) {
				this.$newName.parents(".control-group").addClass("error");
			} else {
				this.$newName.parents(".control-group").removeClass("error");
			}
		},

		onNewNameOk: function() {
			var name = this.$newName.val(),
				offering = this.getOfferingId();
			this.oe[offering][name] = { saved: false, extensionContent: "" };
			this.$id.append($("<option>").text(name)).val(name).trigger("input");
		},

		onDisableAll: function() {
			var offering, 
				name, 
				self = this,
				currentName = this.getId(),
				currentOffering = this.getOfferingId(),
				success = function (offering,name) {
					return function() {
						self.oe[offering][name].disabled = true;
						if (name === currentName && offering === currentOffering) {
							self.publish(false);
						}
						showSuccess("Extension '" + name + "' for Offering '" + offering + "' disabled.");
						self.fire("change");
					};
				},
				fail = function(offering,name) {
					return function(e) {
						showError("Error disabling Extension '" + name + "' for Offering '" + offering 
							+ "': <code>" + e.status + " " + e.statusText + "</code> " + e.responseText);	
					};
				};

			for (offering in this.oe) {
				if (this.oe.hasOwnProperty(offering)) {
					for (name in this.oe[offering]) {
						if (this.oe[offering].hasOwnProperty(name)) {
							if (!this.oe[offering][name].disabled) {
								$.ajax({
									type: "POST",
									url: this.endpoint + "/edit",
									contentType: "application/json",
									data: JSON.stringify({ 
										"identifier" : name,
										"offeringId" : offering,
										"disabled" : true })
								}).done(success(offering, name))
								  .fail(fail(offering,name));
							}
						}
					}
				}
			}
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
			
			this.$publish.toggleButtons({
				onChange: function() {
					self.onToggleChange.apply(self, arguments);
				}
			});
			
			this.$delete.on("click", function() {
				self.onDelete.apply(self, arguments);
			});
			this.$offId.on("input select change", function() {
				self.onOfferingChange.apply(self, arguments);
			});
			this.$id.on("input select change", function() {
				self.onIdChange.apply(self, arguments);
			}).trigger("input");

			/* fade in form */
			this.$addNew.on("click", function() {
				self.$addNew.slideLeft(100, function() {
					self.$addNewForm.slideRight();
				});
			});

			/* fade out form */
			$(".offext-add-new-form-button").on("click", function() {
				self.$addNewForm.slideLeft(400, function() {
					self.$addNew.slideRight(100);
					self.$newName.val("").trigger("input");
				});
			});

			this.$validate.on("click", function() {
				self.onValidate.apply(self, arguments);
			});

			/* create new input */
			this.$addNewOk.on("click", function() {
				self.onNewNameOk.apply(self, arguments);
			});

			this.$save.on("click", function() {
				self.onSave.apply(self, arguments);
			});

			this.$disableAll.on("click", function() {
				self.onDisableAll.apply(self, arguments);
			});

			this.$newName.on("input change keyup", function() {
				self.onNewNameChange.apply(self, arguments);
			}).trigger("input");
		}
	});
	window.OfferingExtensionController = OfferingExtensionController;
}(jQuery);
