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
	function CapabilitiesExtensionController(baseUrl) {
		var self = this;
		this.ce = {};
		this.baseUrl = baseUrl;
		this.previous = null;
		this.$container = $("#capabilities-extensions");
		this.$id = $("#capext-id");
		this.$editor = $("#capext-editor");
		this.$publish = $("#capext-enabled");
		this.$save = $("#capext-save");
		this.$newName = $("#capext-add-new-form-input");
		this.$delete = $("#capext-delete");
		this.$addNew = $("#capext-addnew");
		this.$addNewOk = $("#capext-add-new-form-ok");
		this.$addNewForm = $("#capext-add-new-form");
		this.$validate = $("#capext-validate");
		this.$disableAll = $("#capext-disable-all");
		this.bind();
		
		$.getJSON(this.getCapabilitiesExtensionUrl(), function() {
			self.init.apply(self, arguments);
		}).fail(function(e) {
			showError("Request failed: " + e.status + " " + e.statusText);
		});
	}


	$.extend(CapabilitiesExtensionController.prototype, EventMixin);
	$.extend(CapabilitiesExtensionController.prototype, {
		getCapabilitiesExtensionUrl: function(identifier) {
			return this.baseUrl + "admin/capabilities/ajax/capabilitiesExtensions"
				+ ((identifier) ? "/" + encodeURIComponent(identifier) : "");
		},

		getEnabledExtensions: function() {
			var r = [];
			for (var e in this.ce) {
				if (this.ce[e].saved && !this.ce[e].disabled) {
					r.push(e);
				}
			}
			return r;
		},

		getDisabledExtensions: function() {
			var r = [];
			for (var e in this.ce) {
				if (this.ce[e].saved && this.ce[e].disabled) {
					r.push(e);
				}
			}
			return r;
		},

		hasEnabledExtensions: function() {
			for (var e in this.ce) {
				if (this.ce[e].saved && !this.ce[e].disabled) {
					return true;
				}
			}
			return false;
		},

		hasDisabledExtensions: function() {
			for (var e in this.ce) {
				if (this.ce[e].saved && this.ce[e].disabled) {
					return true;
				}
			}
			return false;
		},

		getSelected: function() {
			return this.$id.val();
		},

		deleteCapabilitiesExtension: function(callback) {
			var name = this.getSelected(),
				self = this;
			if (confirm("Do you really want to delete the capabilities extension '" + name + "'?")){ 
				$.ajax({
					"type": "DELETE",
					"url": this.getCapabilitiesExtensionUrl(name)
				}).done(function(e) {
					showSuccess("Deleted Capabilities Extension '" + name + "'.");
					if ($.isFunction(callback)) { 
						callback.call(self, true); 
					}
					self.fire("change");
				}).fail(function(e) {
					showError("Error deleting Capabilities Extension '" + name 
							+ "': <code>" + e.status + " " + e.statusText + "</code> " 
							+ e.responseText);
					if ($.isFunction(callback)) { 
						callback.call(self, false); 
					}
				});
			}
		},

		getEditorContent: function() {
			return this.$editor.codeMirror("get");
		},

		setEditorContent: function(x) {
			return this.$editor.codeMirror("set", x);
		},

		saveCapabilitiesExtension: function(callback) {
			var name = this.getSelected(),
            	xml = this.getEditorContent(),
				self = this;
			$.ajax({
				type: "POST",
				url: this.getCapabilitiesExtensionUrl(name),
				contentType: "application/xml",
				data: xml
			}).done(function() {
				self.ce[name].extensionContent = xml;
				self.ce[name].saved = true;
				self.ce[name].changed = false;
				self.$publish.removeClass("deactivate");
				self.$publish.toggleButtons("setState", !self.ce[name].disabled);
				showSuccess("Capabilities Extension '" + name + "' saved.");
				if ($.isFunction(callback)) { 
					callback.call(self, true); 
				}
				self.fire("change");
			}).fail(function(e) {
				showError("Error saving Capabilities Extension '" + name 
					+ "': <code>" + e.status + " " + e.statusText + "</code> " 
					+ e.responseText);
				if ($.isFunction(callback)) { 
					callback.call(self, false); 
				}
			});
		},

		enableCapabilitiesExtension: function(enabled, callback) {
			var name = this.getSelected(),
				self = this;
			$.ajax({
				type: "POST",
				url: this.getCapabilitiesExtensionUrl(name),
				contentType: "application/json",
				data: JSON.stringify({ "disabled": !enabled })
			}).done(function() {
				self.ce[name].disabled = !enabled;
				showSuccess("Extension '" + name + "' " + (enabled ? "enabled" : "disabled") + ".");
				if ($.isFunction(callback)) { 
					callback.call(self, true); 
				}
				self.fire("change");
			}).fail(function(e) {
				showError("Error " + (enabled ? "enabling" : "disabling") + " Extension '" + name 
					+ "'': <code>" + e.status + " " + e.statusText + "</code> " + e.responseText);
				if ($.isFunction(callback)) { 
					callback.call(self, false); 
				}
			});
		},

		onEditorChange: function(cm) {
			var name = this.getSelected();
			if (this.ce[name]) {
				this.ce[name].changed = true;
				this.$save.disabled(false);
			}
			if (!cm.getValue()) {
				this.$save.disabled(true);
			}
		},
		
		onToggleChange: function($el, status, e) {
			if (!this.ce[this.getSelected()].saved) {
				this.saveCapabilitiesExtension(function(success) {
					if (success) {
						this.enableCapabilitiesExtension(status, function(success){
							if (!success) { 
								$el.toggleButtons("toggleState", true);	
							}
						});
					} else {
						$el.toggleButtons("toggleState", true);
					}
				});
			} else {
				this.enableCapabilitiesExtension(status, function(success){
					if (!success) { 
						$el.toggleButtons("toggleState", true);	
					}
				});
			}
		},

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

		onIdChange: function() {
			var name = this.getSelected();
			if (name) {
				if (this.previous && this.ce[this.previous]) {
					/* forget changes of previous selected */
					this.ce[this.previous].changed = false;
				}

				/* load capsext to editor */
				this.setEditorContent(xml2string(this.ce[name].extensionContent));
				$(".capext-edit-button").disabled(false);
				this.$publish.toggleButtons("setState", !this.ce[name].disabled, true);
				if (this.ce[name].saved) {
					this.$publish.removeClass("deactivate");
				} else {
					this.$publish.addClass("deactivate");
				}
				this.$save.disabled(true);
				this.previous = name;
			} else {
				this.$publish.addClass("deactivate");
				$(".capext-edit-button").disabled(true);
				this.setEditorContent("");
			}
			if (this.$id.find("option").length > 1) {
				this.$id.disabled(false);	
			} else {
				this.$id.disabled(true);
			}
		},

		onDelete: function() {
			var name = this.getSelected();
			/* delete remotely */ 
			if (this.ce[name].saved) {
				this.deleteCapabilitiesExtension(function(success) {
					/* remove from dropdown */
					if (success) {
						this.$id.find("option:selected").remove();
						this.$id.trigger("input");
						/* remove from 'cache' */
						delete this.ce[name];
					}
				});
			}
		},

		onSave: function() {
			this.$save.disabled(true);
			this.saveCapabilitiesExtension(function(success) {
//				this.$save.disabled(success);
			});
		},

		init: function(json) {
			this.ce = json;
			for (var name in this.ce) {
				this.ce[name].saved = true;
				this.$id.append($("<option>").text(name));
			}
			this.$id.trigger("input");
			this.fire("ready");
		},

		onNewNameOk: function() {
			var name = this.$newName.val();
			this.ce[name] = {};
			this.ce[name].saved = false;
			this.ce[name].extensionContent = "";
			this.$id.append($("<option>").text(name)).val(name).trigger("input");
		},

		onNewNameChange: function() {
			var val = this.$newName.val(),
				disabled = !!(!val || this.ce[val]);
			/* invalid if identifier empty or already present */
			this.$addNewOk.disabled(disabled);
			if (disabled) {
				this.$newName.parents(".control-group").addClass("error");
			} else {
				this.$newName.parents(".control-group").removeClass("error");
			}
		},

		onDisableAll: function() {
			var name, 
				self = this,
				currentName = this.getSelected(),
				success = function(name) {
					return function() {
						self.ce[name].disabled = true;
						if (name === currentName) {
							self.$publish.toggleButtons("setState", false, true);
						}
                    showSuccess("Extension '" + name + "' disabled.");
						self.fire("change");
					};
				},
				error = function(name){
					return function(e) {
						showError("Error disabling Extension '" + name 
							+ "'': <code>" + e.status + " " + e.statusText 
							+ "</code> " + e.responseText);
					};
            };
			for (name in this.ce) {
				if (this.ce.hasOwnProperty(name)) {
					if (!this.ce[name].disabled) {
						$.ajax({
							type: "POST",
							url: this.getCapabilitiesExtensionUrl(name),
							contentType: "application/json",
							data: JSON.stringify({ disabled: true })
						}).done(success(name))
						  .fail(error(name));
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

			this.$id.on("input select change", function() {
				self.onIdChange.apply(self, arguments);
			}).trigger("input");

			this.$delete.on("click", function() {
				self.onDelete.apply(self, arguments);
			});

			this.$save.on("click", function() {
				self.onSave.apply(self, arguments);
			});
			
			this.$addNew.on("click", function() {
				self.$addNew.slideLeft(100, function() {
					self.$addNewForm.slideRight();
				});
			});

			this.$validate.on("click", function() {
				self.onValidate.apply(self, arguments);
			});

			this.$disableAll.on("click", function() {
				self.onDisableAll.apply(self, arguments);
			});
			
			$(".capext-add-new-form-button").on("click", function() {
				self.$addNewForm.slideLeft(400, function() {
					self.$addNew.slideRight(100);
					self.$newName.val("").trigger("input");
				});
			});

			this.$addNewOk.on("click", function() {
				self.onNewNameOk.apply(self, arguments);
			});

			this.$newName.on("input change keyup", function() {
				self.onNewNameChange.apply(self, arguments);
			}).trigger("input");
		}
	});
	window.CapabilitiesExtensionController = CapabilitiesExtensionController;
}(jQuery);