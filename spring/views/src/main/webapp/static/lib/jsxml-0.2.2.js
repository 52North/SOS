/*
 * Copyright (C) 2013
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
 // TODO fix duplicate license header!
/*
JSXML 0.2.2 (2012-07-18)

JavaScript XML/XSLT Library
 
Copyright (c) 2007-2012, Anton Zorko
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the <organization> nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
 
(function(cfg){
    var defaults = {
        context: window,
        name: 'jsxml',
        file_cache: true,
        errors: 'alert' // throw, ignore
    };
    if (!cfg) cfg = {};
    for (var i in defaults)
        if (cfg[i]) defaults[i] = cfg[i];
    cfg = defaults;
    
    // conveyor is used to build and execute callback chains
    var conveyor = function(cb){
        if (cb) this.cb = cb;
        this.tasks = [];
    };
    conveyor.prototype.add = function(task){
        this.tasks.push(task);
    };
    conveyor.prototype.exe = function(i){
        if (typeof(i) == 'function') {
            this.cb = i;
            i = 0;
        };
        i = i ? i : 0;
        var $this = this;
        if (this.tasks.length > i) this.tasks[i](function(){
            $this.exe(i+1);
        }); else if (typeof(this.cb) == 'function') this.cb();
    };
    
    var lib = function(){},
        pro = {
            ajax: function(cfg){
                if (this._ajax === null) {
                    if (window.jQuery) {
                        this._ajax = jQuery.ajax;
                    } else if (window.Ext && window.Ext.Ajax) { // ExtJS
                        this._ajax = function(cfg){
                            Ext.Ajax.request({
                                url: cfg.url,
                                success: function(x){
                                    cfg.success(x.responseXML);
                                },
                                failure: cfg.error
                            });
                        };
                    } else if (window.Ajax && Ajax.Request) { // Prototype
                        this._ajax = function(cfg){
                            new Ajax.Request(cfg.url, {
                                method:'get',
                                onSuccess: function(x){
                                    cfg.success(x.responseXML);
                                },
                                onFailure: cfg.error,
                                onException: function(r, e) {
                                    cfg.error(e);
                                }
                            });
                        };
                    } else if (window.YAHOO) { // YAHOO
                        this._ajax = function(cfg){
                            YAHOO.util.Connect.asyncRequest('GET', cfg.url, {
                                success: function(x){
                                    cfg.success(x.responseXML);
                                },
                                failure: cfg.error
                            });
                        };
                    } else {
                        this._ajax = false;
                    };
                };
                if (this._ajax) {
                    try {
                        this._ajax(cfg);
                    } catch (e) {
                        cfg.error(e);
                    };
                } else this._throw(this._msgs.noajax);
            },
            
            /* The following two pieces of code are taken from http://www.alistapart.com/articles/crossbrowserscripting */
            nodeTypes: {
                ELEMENT_NODE: 1,
                ATTRIBUTE_NODE: 2,
                TEXT_NODE: 3,
                CDATA_SECTION_NODE: 4,
                ENTITY_REFERENCE_NODE: 5,
                ENTITY_NODE: 6,
                PROCESSING_INSTRUCTION_NODE: 7,
                COMMENT_NODE: 8,
                DOCUMENT_NODE: 9,
                DOCUMENT_TYPE_NODE: 10,
                DOCUMENT_FRAGMENT_NODE: 11,
                NOTATION_NODE: 12
            },
            
            importNode: function(document, node, allChildren) {
                /* find the node type to import */
                switch (node.nodeType) {
                    case this.nodeTypes.ELEMENT_NODE:
                        /* create a new element */
                        var newNode = document.createElement(node.nodeName);
                        /* does the node have any attributes to add? */
                        if (node.attributes && node.attributes.length > 0)
                            /* add all of the attributes */
                            for (var i = 0, il = node.attributes.length; i < il;)
                                newNode.setAttribute(node.attributes[i].nodeName, node.getAttribute(node.attributes[i++].nodeName));
                        /* are we going after children too, and does the node have any? */
                        if (allChildren && node.childNodes && node.childNodes.length > 0)
                            /* recursively get all of the child nodes */
                            for (var i = 0, il = node.childNodes.length; i < il;)
                                newNode.appendChild(this.importNode(document, node.childNodes[i++], allChildren));
                        return newNode;
                        break;
                    case this.nodeTypes.TEXT_NODE:
                    case this.nodeTypes.CDATA_SECTION_NODE:
                    case this.nodeTypes.COMMENT_NODE:
                        return document.createTextNode(node.nodeValue);
                        break;
                }
            },
            
            getMsDom: function () {
                // create IE xml DOM without ActiveX, submitted by alfalabs.net@gmail.com
                var xml = document.createElement('xml');
                xml.src = '<?xml version="1.0" encoding="UTF-8"?>';
                document.body.appendChild(xml);
                var xmlDocument = xml.XMLDocument;
                document.body.removeChild(xml);
                return xmlDocument;
            },
            
            newDoc: function(node){
                var d = window.ActiveXObject ? this.getMsDom() : document.implementation.createDocument("", node && !node.tagName ? node : 'test', null);
                if (window.ActiveXObject) {
                    if (node) {
                        if (node.tagName) {
                            d.appendChild(this.importNode(d, node, true));
                        } else {
                            d.appendChild(d.createElement(node));
                        };
                    };
                } else {
                    if (node.tagName) {
                        d.replaceChild(d.importNode(node, true), d.documentElement);
                    }
                };
                return d;
            },
            
            copy: function(obj){
                return this.fromString(this.toXml(obj));
            },
            
            fromStringOrObject: function(src) {
                return this.fromString(src) || this._fromObject(src);
            },
            
            fromString: function(str, checkXmlDeclaration) {
                if (typeof(str) != 'string') return false;
                checkXmlDeclaration = checkXmlDeclaration == undefined ? true : checkXmlDeclaration;
                if (checkXmlDeclaration && !/^<\?xml/.test(str)) return false;
                if (window.ActiveXObject) {
                    var o = this.newDoc();
                    o.loadXML(str);
                    if (!o.documentElement) {
                        this._throw(this._msgs.broken);
                        return false;
                    };
                    return o;
                } else {
                    var parser = new DOMParser(),
                        o = parser.parseFromString(str, 'text/xml'),
                        e = o.getElementsByTagName('parsererror');
                    if (e.length > 0) {
                        this._throw(this._msgs.broken);
                        return false;
                    } else return o;
                };
            },
            
            fromFile: function(file, callback, scope) {
                var cl = this._cache_loading,
                    c = this._cache,
                    $this = this;
                if (cl[file]) { // i.e. the same file is currently loading
                    if (cl[file] === true) cl[file] = new conveyor;
                    cl[file].add(function(cb){
                        callback.call(scope ? scope : c[file], c[file]);
                        cb();
                    });
                } else if (c[file]) {
                    callback.call(scope ? scope : c[file], c[file]);
                } else {
                    cl[file] = true;
                    this.ajax({
                        url: file,
                        success: function(xml){
                            if (!xml.documentElement) {
                                $this._throw($this._msgs.brokenfile + ': ' + file);
                                return;
                            };
                            c[file] = xml;
                            callback.call(scope ? scope : xml, xml);
                            if (typeof(cl[file]) == 'object') cl[file].exe();
                            if (cl[file]) delete cl[file];
                        },
                        error: function(e){
                            cl[file] = false;
                            if (e) {
                                var m = [];
                                m.push($this._msgs.exception + ":\r\n" + file);
                                if (e.name) m.push(e.name);
                                if (e.message) m.push(e.message);
                                $this._throw(m.join("\r\n"));
                            } else $this._throw($this._msgs.brokenfile + ': ' + file);
                        }
                    });
                };
            },

            load: function(source, callback, scope) {
                var o = this.fromStringOrObject(source);
                if (o) callback.call(scope ? scope : o, o);
                else this.fromFile(source, callback, scope);
            },
            
            toXml: function(source, xmlHeaderNeeded){
                xmlHeaderNeeded = xmlHeaderNeeded == false ? false : true;
                var xml = typeof(source) == 'string' ? source : (source.xml ? source.xml : new XMLSerializer().serializeToString(source)),
                    xmlHeaderPresent = /^<\?xml/.test(xml);
                if (xmlHeaderNeeded && /\="UTF\-16"\?/.test(xml)) xml = xml.replace(/\=\"UTF\-16\"\?/, '="UTF-8"?');
                if (xmlHeaderNeeded && !xmlHeaderPresent) return this._xmlHeader + xml;
                if (!xmlHeaderNeeded && xmlHeaderPresent) return xml.replace(/^<\?xml[^<]+/, '');
                return xml;
            },
            
            _borrowRootName: function(names){
                return names ? ( this._isA(names) ? names.shift() : names ) : 'root';
            },
            
            toDom: function(o, names, parentNode){
                rootName = this._borrowRootName(names);
                if (parentNode) parentNode = parentNode.appendChild(parentNode.ownerDocument.createElement(rootName));
                else parentNode = this.newDoc(rootName).documentElement;
                var t;
                if (this._isA(o)) {
                    for (var i = 0; i < o.length; i++ )
                        this.toDom(o[i], this._copy(names), parentNode);
                } else {
                    for (var i in o) {
                        t = typeof o[i];
                        switch (t) {
                            case 'string':
                            case 'number':
                            case 'boolean':
                                try {
                                    var v = t == 'boolean' ? (o[i]?1:0) : o[i];
                                    parentNode.setAttribute(i, String(v));
                                } catch(e) {
                                    this._throw('Unable to set attribute named "'+i+'"; ' + e.message);
                                };
                            break;
                            case 'object':
                                if (o[i] !== null)
                                    this.toDom(o[i], this._isA(names) ? [i].concat(names) : i, parentNode);
                            break;
                            default:
                                this._throw('Unsuitable type of index "' + i + '" for converting to XML: ' + t);
                                return false;
                            break;
                        };
                    };
                };
                return parentNode.ownerDocument.documentElement == parentNode ? parentNode.ownerDocument : parentNode;
            },
            
            trans: function(xmlSrc, xslSrc, callback, nativeResult, doc){
                var $this = this;
                this.load(xmlSrc, function(xml){
                    $this._trans2(xml, xslSrc, callback, nativeResult, doc);
                });
            },
            
            transReady: function(xmlSrc, xslSrc, nativeResult, doc){
                var xmlSrc = this.fromStringOrObject(xmlSrc),
                    xslSrc = this.fromStringOrObject(xslSrc),
                    r;
                if (!xmlSrc || !xslSrc) return false;
                try {
                    if (window.ActiveXObject) {
                        r = xmlSrc.transformNode(xslSrc);
                        r = this.fromStringOrObject(r) || r;
                    } else {
                        var processor = new XSLTProcessor();
                        processor.importStylesheet(xslSrc);
                        r = doc ? processor.transformToDocument(xmlSrc) : processor.transformToFragment(xmlSrc, document);
                    };
                } catch (e) {
                    this._throw(this._msgs.brokenxslt);
                    return false;
                };
                return nativeResult ? r : this.toXml(r, false);
            },
            
            getXslWrap: function(cfg){
                cfg = cfg || {};
                cfg.indent = cfg.indent ? cfg.indent : 'yes';
                cfg.method = cfg.method ? cfg.method : 'html';
                return ['<?xml version="1.0" encoding="UTF-8"?><xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"><xsl:output encoding="UTF-8" indent="' + cfg.indent + '" method="' + cfg.method + '" />', '</xsl:stylesheet>'];
            },
            
            _trans2: function(xml, xslSrc, callback, nativeResult, doc){
                this.load(xslSrc, function(xsl){
                    var scope;
                    if (callback && typeof callback.length == "number" && typeof callback.splice == "function") {
                        scope = callback[1];
                        callback = callback[0];
                    } else scope = callback;
                    callback.call(scope, this.transReady(xml, xsl, nativeResult, doc));
                }, this);
            },
            
            _throw: function(msg){
                var sourceAppName = 'JSXML';
                switch (cfg.errors) {
                    case 'alert':
                        alert(sourceAppName + ":\r\n" + msg);
                    break;
                    case 'throw':
                        throw sourceAppName + ":\r\n" + msg;
                    break;
                };
            },
            _ajax: null,
            _fromObject: function(el) {
                if (!el.documentElement) {
                    if (el.tagName)  {
                        return this.newDoc(el);
                    }
                    return false;
                };
                return el;
            },
            _xmlHeader: '<?xml version="1.0" encoding="UTF-8"?>\r\n',
            _cache: {},
            _isA: function(v){
                return v && typeof v.length == "number" && typeof v.splice == "function";
            },
            _copy: function(o){
                if (typeof o != 'object' || o === null) return o;
                var r;
                if (this._isA(o)) {
                    r = [];
                    for (var i = 0; i < o.length; i++) r.push(o[i]);
                } else {
                    r = {};
                    for (var i in o) r[i] = o[i]
                };
                return r;
            },
            _cache_loading: {},
            _msgs: {
                broken: 'Broken XML string',
                brokenfile: 'Broken XML file',
                brokenxslt: 'Broken XSLT',
                noajax: 'No AJAX library found. See docs for supported libraries.',
                exception: 'An exception happened while trying to load url'
            }
        };
    for (var i in pro) lib.prototype[i] = pro[i];
    cfg.context[cfg.name] = new lib;
})();
