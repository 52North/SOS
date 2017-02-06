<%--

    Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
    Software GmbH

    This program is free software; you can redistribute it and/or modify it
    under the terms of the GNU General Public License version 2 as published
    by the Free Software Foundation.

    If the program is linked with libraries which are licensed under one of
    the following licenses, the combination of the program with the linked
    library is not considered a "derivative work" of the program:

        - Apache License, version 2.0
        - Apache Software License, version 1.0
        - GNU Lesser General Public License, version 3
        - Mozilla Public License, versions 1.0, 1.1 and 2.0
        - Common Development and Distribution License (CDDL), version 1.0

    Therefore the distribution of the program linked with libraries licensed
    under the aforementioned licenses, is permitted by the copyright holders
    if the distribution is compliant with both the GNU General Public
    License version 2 and the aforementioned licenses.

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
    Public License for more details.

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<jsp:include page="../common/header.jsp">
    <jsp:param name="activeMenu" value="admin" />
</jsp:include>

<link rel="stylesheet" href="<c:url value="/static/lib/prettify.css" />" type="text/css" />
<link rel="stylesheet" href="<c:url value="/static/lib/codemirror-2.34.css" />" type="text/css" />
<link rel="stylesheet" href="<c:url value="/static/css/codemirror.custom.css" />" type="text/css" />
<link rel="stylesheet" href="<c:url value="/static/lib/bootstrap-toggle-buttons.css" />" type="text/css" />
<script type="text/javascript" src="<c:url value="/static/lib/codemirror-2.34.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/codemirror-2.34-xml.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/prettify.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/vkbeautify-0.99.00.beta.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/jquery.toggle.buttons.js" />"></script>

<script type="text/javascript" src="<c:url value="/static/js/jquery.additions.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/js/EventMixin.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/js/capabilities/StaticCapabilitiesController.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/js/capabilities/OfferingExtensionController.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/js/capabilities/CapabilitiesExtensionController.js" />"></script>


<jsp:include page="../common/logotitle.jsp">
    <jsp:param name="title" value="Capabilities Settings<sup><small style='color:#f00;'>BETA</small></sup>" />
    <jsp:param name="leadParagraph" value="" />
</jsp:include>

<style type="text/css">
    .btn-icon { height: 30px; }
    .btn-icon i { margin-right: 0px !important; }
    .btn-single { margin-bottom: 1px;}
    #stcaps-publish { margin-bottom: -11px; }
    #back-top {
        position: fixed;
        bottom: 30px;
        margin-left: -150px;
    }
    #back-top a {
        width: 108px;
        display: block;
        text-align: center;
        font: 11px Arial, Helvetica, sans-serif;
        text-transform: uppercase;
        text-decoration: none;
        color: #bbb;
        /* background color transition */
        -webkit-transition: 1s;
        -moz-transition: 1s;
        transition: 1s;
    }
    #back-top a:hover {
        color: #000;
    }
    #back-top i {
        display: block;
        margin-bottom: 7px;
        margin-left: 48px;
        -webkit-border-radius: 15px;
        -moz-border-radius: 15px;
        border-radius: 15px;
        -webkit-transition: 1s;
        -moz-transition: 1s;
        transition: 1s;
    }
</style>

<script type="text/javascript">
    jQuery(document).ready(function($) {
        $(window).scroll(function () {
            var pos = $(this).scrollTop();
            if (pos > "fast" && pos < $(document).height() 
                                    - ($(this).height() + 400)) {
                $('#back-top').fadeIn();
            } else {
                $('#back-top').fadeOut();
            }
        });
        $('#back-top a').click(function () {
            $('body, html').animate({
                scrollTop: 0
            }, 400);
            return false;
        });
    });

    function xml2string(xml) {
        return typeof(xml) === "string" ? xml : xml.xml ? xml.xml 
                : new XMLSerializer().serializeToString(xml); 
    }
</script>

<div id="current-setting"></div>

<ul class="nav nav-tabs">
    <li class="active"><a href="#static-capabilities" data-toggle="tab">Static Capabilities</a></li>
    <li><a href="#capabilities-extensions" data-toggle="tab">Capabilities Extensions</a></li>
    <li><a href="#offering-extensions" data-toggle="tab">Offering Extensions</a></li>
</ul>
<div class="tab-content">
    <div class="active tab-pane" id="static-capabilities">
        <div class="row">
            <div class="span12 form-inline" style="margin-bottom: 5px;">
                <select id="stcaps-id">
                    <option disabled="disabled" selected="selected" style="display: none;" value="">
                        Static Capabilities
                    </option>
                </select>
                <div class="btn-group">
                    <button id="stcaps-save" title="Save Capabilities" type="button" class="btn btn-icon stcaps-edit-button"><i class="icon-ok"></i></button>
                    <button id="stcaps-delete" title="Delete Capabilities" type="button" class="btn btn-icon stcaps-edit-button"><i class="icon-remove"></i></button>
                    <button id="stcaps-load-current-button" title="Load current capabilities document into the editor" type="button" class="btn btn-icon stcaps-edit-button"><i class="icon-download-alt"></i></button>
                    <button id="stcaps-validate" title="Validate" type="button" class="btn btn-icon"><i class="icon-wrench"></i></button>
                </div>
                <button id="stcaps-addnew" title="Add a new static capabilities document" type="button" class="btn btn-icon btn-single"><i class="icon-plus"></i></button>
                <div id="stcaps-add-new-form" class="input-append input-prepend control-group" style="display: none;">
                    <input class="input-xlarge" id="stcaps-add-new-form-input" type="text" placeholder="Identifier"/>
                    <div class="btn-group">
                        <button type="button" title="Add" class="btn btn-icon stcaps-add-new-form-button" id="stcaps-add-new-form-ok"><i class="icon-ok"></i></button>
                        <button type="button" title="Dismiss" class="btn btn-icon stcaps-add-new-form-button" id="stcaps-add-new-form-cancel"><i class="icon-remove"></i></button>
                    </div>
                </div>
                <div id="stcaps-publish" title="Publish this document" class="pull-right" data-toggleButton-style-enabled="info" data-toggleButtons-height="29">
                    <input type="checkbox" disabled="disabled">
                </div>
            </div>
        </div>
        <textarea id="stcaps-editor" class="span12"></textarea>
    </div>
    <div class="tab-pane" id="capabilities-extensions">
        <div class="row">
            <div class="span12 form-inline" style="margin-bottom: 5px;">
                <select id="capext-id">
                    <option disabled="disabled" selected="selected" style="display: none;" value="">Capabilities Extensions</option>
                </select>
                <div class="btn-group">
                    <button id="capext-save" title="Save Extension" type="button" class="btn btn-icon capext-edit-button"><i class="icon-ok"></i></button>
                    <button id="capext-delete" title="Delete Extension" type="button" class="btn btn-icon capext-edit-button"><i class="icon-remove"></i></button>
                    <button id="capext-validate" title="Validate" type="button" class="btn btn-icon"><i class="icon-wrench"></i></button>
                </div>
                <button id="capext-addnew" title="Add new Extension" type="button" class="btn btn-icon btn-single"><i class="icon-plus"></i></button>
                <div id="capext-add-new-form" class="input-append input-prepend control-group" style="display: none;">
                    <input class="input-xlarge" id="capext-add-new-form-input" type="text" placeholder="Identifier"/>
                    <div class="btn-group">
                        <button type="button" title="Add" class="btn btn-icon capext-add-new-form-button" id="capext-add-new-form-ok"><i class="icon-ok"></i></button>
                        <button type="button" title="Dismiss" class="btn btn-icon capext-add-new-form-button" id="capext-add-new-form-cancel"><i class="icon-remove"></i></button>
                    </div>
                </div>
                <div class="pull-right">
                    <button id="capext-disable-all" title="Disable all" type="button" style="margin-top: -22px" class="btn btn-icon btn-single"><i class="icon-remove"></i></button>
                    <div id="capext-enabled" title="Publish this extension" data-toggleButton-style-enabled="info" data-toggleButtons-height="29">
                        <input type="checkbox" disabled="disabled">
                    </div>
                </div>
            </div>
        </div>
        <textarea id="capext-editor" class="span12"></textarea>
    </div>
    <div class="tab-pane" id="offering-extensions">
        <div class="row">
            <div class="span12 form-inline" style="margin-bottom: 5px;">
                <select id="offext-off-id">
                    <option disabled="disabled" selected="selected" style="display: none;" value="">Offering</option>
                </select>
                <select id="offext-id"></select>
                <div class="btn-group">
                    <button id="offext-save" title="Save Extension" type="button" class="btn btn-icon offext-edit-button"><i class="icon-ok"></i></button>
                    <button id="offext-delete" title="Delete Extension" type="button" class="btn btn-icon offext-edit-button"><i class="icon-remove"></i></button>
                    <button id="offext-validate" title="Validate" type="button" class="btn btn-icon"><i class="icon-wrench"></i></button>
                </div>
                <button id="offext-addnew" title="Add new Extension" type="button" class="btn btn-icon btn-single"><i class="icon-plus"></i></button>
                <div id="offext-add-new-form" class="input-append input-prepend control-group" style="display: none;">
                    <input class="input-xlarge" id="offext-add-new-form-input" type="text" placeholder="Identifier"/>
                    <div class="btn-group">
                        <button type="button" title="Add" class="btn btn-icon offext-add-new-form-button" id="offext-add-new-form-ok"><i class="icon-ok"></i></button>
                        <button type="button" title="Dismiss" class="btn btn-icon offext-add-new-form-button" id="offext-add-new-form-cancel"><i class="icon-remove"></i></button>
                    </div>
                </div>
                <div class="pull-right">
                    <button id="offext-disable-all" title="Disable all" type="button" style="margin-top: -22px" class="btn btn-icon btn-single"><i class="icon-remove"></i></button>
                    <div id="offext-enabled" title="Publish this extension" data-toggleButton-style-enabled="info" data-toggleButtons-height="29">
                        <input type="checkbox" disabled="disabled">
                    </div>
                </div>
            </div>
        </div>
        <textarea id="offext-editor" class="span12"></textarea>
    </div>
</div>

<p id="back-top" style="display: none;"><a href="#top"><i class="icon-chevron-up"></i>Back to Top</a></p>

<script type="text/javascript">
    var baseUrl = "<c:url value="/"/>",
        scc = new StaticCapabilitiesController(baseUrl),
        cec = new CapabilitiesExtensionController(baseUrl),
        oec = new OfferingExtensionController(baseUrl);
    
    var changed = 0;

    function onChange() {
        if (++changed < 3) {
            return;
        }
        var code = function(e) {
                return "<code>" + e + "</code>"; 
            },
            toCodeList = function(a) { 
                return (a.length > 1 ? a.slice(0, -1).map(code).join(", ") + " and " : "") + code(a[a.length-1]); 
            },
            verb = function(col) { 
                return col.length === 1 ? "is" : "are"; 
            },
            ext = function(col) { 
                return "Extension" + (col.length === 1 ? "" : "s"); 
            },
            formatCapabilitiesExtensions = function() {
                var hasEnabled = cec.hasEnabledExtensions(),
                    hasDisabled = cec.hasDisabledExtensions(),
                    html = "";
                if (hasEnabled || hasDisabled) {
                    html += "<p>";
                    if (hasEnabled) {
                        var active = cec.getEnabledExtensions();
                        html += " The Capabilities "+ ext(active) + " " + toCodeList(active) + " " + verb(active) + " active";
                    }
                    if (hasDisabled) {
                        var inactive = cec.getDisabledExtensions();
                        html += (hasEnabled) ? " while" : ("The Capabilties " + ext(inactive)) + " ";
                        html += toCodeList(inactive) + " " + verb(inactive) + " inactive";
                    }
                    html + ".</p>";
                }
                return html;
            },
            formatOfferingExtensions = function() {
                var html = "", 
                    offs = {},
                    copy = function(name, col) {
                        for (var a in col) {
                            if (col.hasOwnProperty(a) && col[a].length > 0) {
                                (offs[a] = offs[a] || {})[name] = col[a];
                            }
                        }
                    };

                if (oec.hasEnabledExtensions()) 
                    copy("active", oec.getEnabledExtensions());
                if (oec.hasDisabledExtensions()) 
                    copy("inactive", oec.getDisabledExtensions());
                

                for (var key in offs) {
                    html += "<p>";
                    if (offs[key].active) {
                        html += " " + ext(offs[key].active) + " " 
                             + toCodeList(offs[key].active) + " " 
                             + verb(offs[key].active) 
                             + " active for Offering " + code(key);
                        if (offs[key].inactive) {
                            html += " while " + toCodeList(offs[key].inactive) 
                                 + " " + verb(offs[key].inactive) + " not";
                        }
                        html += ".";
                    } else if (offs[key].inactive) {
                        html += ext(offs[key].inactive) + " " 
                             + toCodeList(offs[key].inactive) + " " 
                             + verb(offs[key].inactive) 
                             + " inactive for Offering " + code(key) + ".";
                    }
                    html += "</p>";
                }
                return html;
            },
            html = "",
            lead = "",
            $cs = $("#current-setting");


        if (scc.isStatic()) {
            lead  = "The current Capabilities are based on the <strong>static</strong> capabilities " + code(scc.getCurrent()) + ".";
        } else {
            lead  = "The current Capabilities are based on the <strong>dynamic</strong> capabilities.";
            html += formatCapabilitiesExtensions();
            html += formatOfferingExtensions();
        }
        $("p.lead").fadeOut("fast", function() {
            $(this).children().remove().addBack().html(lead).fadeIn("fast");
        });
        if ($cs.children().length === 0) {
            $cs.hide().html(html).fadeIn();
        } else {
            $cs.fadeOut("fast", function() {
                $(this).children().remove().addBack().html(html).fadeIn("fast");  
            });
        }
    }

    scc.on("change", onChange).on("ready", onChange);
    cec.on("change", onChange).on("ready", onChange);
    oec.on("change", onChange).on("ready", onChange);

</script>

<jsp:include page="../common/footer.jsp" />