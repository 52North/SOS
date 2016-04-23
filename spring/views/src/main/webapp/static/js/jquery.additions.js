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
 (function($) {
    $.scrollToTop = function(duration, complete) {
        $("html,body").animate({
            "scrollTop": 0
        }, duration, "swing", complete);
        return this;
    };
    $.fn.scrollTo = function(duration, complete) {
        $("html,body").animate({
            "scrollTop": this.offset().top
        }, duration, "swing", complete);
        return this;
    };

    $.exists = function(selector) {
        return ($(selector).length > 0);
    };

    $.fn.slideRemove = function(opt) {
        return this.slideUp(opt, function() {
            $(this).remove();
        });
    };

    $.fn.fadeRemove = function(opt) {
        return this.fadeOut(opt, function() {
            $(this).remove();
        });
    };

    $.fn.verticalSlideToggle = function(speed, callback) {
            this.animate({
                width: "toggle",
                paddingLeft: "toggle",
                paddingRight: "toggle",
                marginLeft: "toggle",
                marginRight: "toggle"
            }, speed, callback);
    };

    $.fn.type = function(type) {
        return this.attr("type", type);
    };


    $.fn.disabled = function(opts) {
		if (opts === false) {
			$(this).removeAttr("disabled");
		} else {
			$(this).attr("disabled", "disabled");
		}
		return this;
	};

    $.fn.checked = function(opts) {
		if (opts === false) {
			$(this).removeAttr("checked");
		} else {
			$(this).attr("checked", "checked");
		}
		return this;
	};

    $.fn.toggleAttr = function(attr, b) {
        var c = (b === undefined);
        return this.each(function() {
            var $el = $(this);
            if ((c && !$el.is("["+ attr +"]")) || (!c && b)) {
                $el.attr(attr, attr);
            }
            else $el.removeAttr(attr);
        });
    };

    $.fn.slideRight = function(speed, callback) {
		this.animate({
			width: "show",
			paddingLeft: "show",
			paddingRight: "show",
			marginLeft: "show",
			marginRight: "show"
		}, speed, callback);
    };

    $.fn.verticalSlideToggle = function(speed, callback) {
		this.animate({
			width: "toggle",
			paddingLeft: "toggle",
			paddingRight: "toggle",
			marginLeft: "toggle",
			marginRight: "toggle"
		}, speed, callback);
	};

    $.fn.slideLeft = function(speed, callback) {
		this.animate({
			width: "hide",
			paddingLeft: "hide",
			paddingRight: "hide",
			marginLeft: "hide",
			marginRight: "hide"
		}, speed, callback);
	};

    $.fn.insertAt = function(index, element) {
        var lastIndex = this.children().size();
        if (index < 0) {
            index = Math.max(0, lastIndex + 1 + index);
        }
        this.append(element);
        if (index < lastIndex) {
            this.children().eq(index).before(this.children().last());
        }
        return this;
    };

	$.fn.codeMirror = function(method) {
		var methods = {
			init: function(options) {
				return this.each(function(i,e) {
					if (e.tagName.toLowerCase() !== "textarea") {
						return false;
					} else {
						var cm = CodeMirror.fromTextArea(e, options);
						$(e).data("codeMirror", cm);
					}
				});
			},
			get: function() {
				var result = "";
				this.each(function(i,e) {
					var cm = $(e).data("codeMirror");
					if (cm) { cm.save(); result = cm.getValue(); }
				});
				return result;
			},
			set: function(value) {
				return this.each(function(i,e) {
					var cm = $(e).data("codeMirror");
					if (cm) { cm.setValue(value); }
				});
			},
			instance: function() {
				var cm;
				return this.each(function(i,e) {
					cm = $(e).data("codeMirror");
				});
				return cm;
			}
		};

		if (methods[method]) {
			 return methods[method].apply( this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || !method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist.');
		}
	};
})(jQuery);