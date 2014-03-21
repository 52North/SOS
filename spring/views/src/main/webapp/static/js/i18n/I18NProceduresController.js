/*
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

	function I18NProceduresController(baseUrl)  {
		var self = this;
		this.baseUrl = baseUrl;
		this.endpoint = this.baseUrl + "/admin/i18n/ajax/procedures";
		this.oe = {};
		this.previous = null;
		this.previousOffering = null;
		this.$container = $("#i18n-procedures");
		this.$identifier = $("#procedure-identifier");
		this.$lanugage = $("#procedure-language");
		this.$name = $("#procedure-name");
		this.$desciption = $("#procedure-desciption");
		this.$editor = $("#procedure-editor");
		this.$save = $("#procedure-save");
		this.$publish = $("#procedure-enabled");
		this.$delete = $("#procedure-delete");
		this.$addNew = $("#procedure-addnew");
		this.$newName = $("#procedure-add-new-form-input");
		this.$addNewForm = $("#procedure-add-new-form");
		this.$addNewOk = $("#procedure-add-new-form-ok");
		this.$editButtons = $(".procedure-edit-button");

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


		getIdentifier: function() {
			return this.$identifier.val();
		},

		getLanguage: function() {
			return this.$language.val();
		},
		
		getName: function() {
			return this.$name.val();
		},
		
		getDescription: function() {
			return this.$description.val();
		},
		
		deleteOfferingExtension: function(callback)  {
			var name = this.getIdentifer(),
				language = this.getLanguage(),
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
			var language = this.getLanguage(),
				identifier = this.getIdentifier,
				xml = this.getEditorContent(),
				self = this;
			$.ajax({
				type: "POST",
				url: this.endpoint + "/save",
				contentType: "application/json",
				data: JSON.stringify({
					"objectId" :  identifier,
					"languageObject" :
						[{
							"language" : language,
							"name" : name,
							"description" : language}]
				})
			}).done(function() {
				self.oe[identifier][language].extensionContent = xml;
				self.oe[identifier][language].saved = true;
				self.oe[identifier][language].changed = false;
				self.disablePublish(false);
				self.publish(!self.oe[identifier][language].disabled);
				showSuccess("New language '" + language + "' for Procedure '" + identifier + "' saved.");
				if ($.isFunction(callback)) { 
					callback.call(self, true);
				}
				self.fire("change");
			}).fail(function(e) {
				showError("Error saving Extension '" + language + "' for Procedure '" + identifier 
					+ "': <code>" + e.status + " " + e.statusText + "</code> " + e.responseText);
				if ($.isFunction(callback)) { 
					callback.call(self, false); 
				}
			});
		},


		onOfferingChange: function() {
			var identifier = this.getIdentifier;

			if (identifier) {
				this.$addNew.disabled(false);
				if (this.previousIdentifier !== identifier) {
					this.previousIdentifier = identifier;
					// TODO fill extensions
					this.$id.find("option").remove();
					this.$id.append($("<option>").attr({
						disabled: "disabled", selected: "selected",
						value: "" }).text("Extension").hide());
					for (var name in this.oe[identifier]) {
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
				identifier = this.getIdentifier;
			if (identifier && name) {
				if (this.previous && this.oe[identifier] && this.oe[identifier][this.previous]) {
					/* forget changes of previous selected */
					this.oe[identifier][this.previous].changed = false;
				}

				/* load capsext to editor */
				this.setEditorContent(xml2string(this.oe[identifier][name].extensionContent));

				this.$editButtons.disabled(false);
				this.$publish.toggleButtons("setState", !this.oe[identifier][name].disabled, true);
				this.disablePublish(!this.oe[identifier][name].saved);
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

		onNewLanguageChange: function() {
			var val = this.$newLanguage.val(),
				identifier = this.getIdentifier,
				disabled = !!(!val || this.oe[identifier][val]);
			/* invalid if identifier empty or already present */
			this.$addNewOk.disabled(disabled);
			if (disabled) {
				this.$newName.parents(".control-group").addClass("error");
			} else {
				this.$newName.parents(".control-group").removeClass("error");
			}
		},

		onNewLanguageOk: function() {
			var language = this.$newLanguage.val(),
			identifier = this.getIdentifier;
			this.oe[identifier][language] = { saved: false, extensionContent: "" };
			this.$id.append($("<option>").text(language)).val(language).trigger("input");
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
				self.onNewLanguageOk.apply(self, arguments);
			});

			this.$save.on("click", function() {
				self.onSave.apply(self, arguments);
			});

			this.$disableAll.on("click", function() {
				self.onDisableAll.apply(self, arguments);
			});

			this.$newName.on("input change keyup", function() {
				self.onNewLanguageChange.apply(self, arguments);
			}).trigger("input");
		}
	});

	window.I18NProceduresController = I18NProceduresController;

}(jQuery);