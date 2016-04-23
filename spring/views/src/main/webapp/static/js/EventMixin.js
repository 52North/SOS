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
window.EventMixin = window.EventMixin || (function() {
	var key = "SOS_EVENTS";
	function on() {
		var events = this[key] = this[key] || {};
		if (arguments.length == 2) {
			var e = arguments[0], l = arguments[1];
			if (!$.isFunction(l)) {
				throw new Error("invalid function argument");
			}
			(events[e] = events[e] || []).push(l);
		} else if (arguments.length == 1) {
			for (var e in arguments[0]) {
				if (arguments[0].hasOwnProperty(e)) {
					this.on(e, arguments[0][e]);	
				}
			}
		} else {
			throw new Error("invalid argument lenght");
		}
		return this;
	}
	function off() {
		var events = this[key] = this[key] || {};
		if (arguments.length == 2) {
			var e = arguments[0], l = arguments[1];
			if (l && events[e]) {
				for (var i = 0; i < events[e].length; ++i) {
					if (events[e][i] == l) {
						delete events[e][i];
					}
				}
			}
		} else if (arguments.length == 1) {
			for (var e in arguments[0]) {
				this.off(e, arguments[0][e]);
			}
		} else {
			throw new Error("invalid argument lenght");
		}
		return this;
	}
	function fire(e) {
		var events = this[key] = this[key] || {},
			listeners = events[e] = events[e] || [];
		for (var i = 0; i < listeners.length; ++i) {
			listeners[i].apply(this, Array.prototype.slice.call(arguments, 1));
		}
		return this;
	}
	return {
		on: on, off: off, fire: fire, trigger: fire,
		addEventListener: on, removeEventListener: off
	};
})();

