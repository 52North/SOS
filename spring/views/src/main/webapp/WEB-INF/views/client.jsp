<%--

    Copyright (C) 2012-2023 52°North Spatial Information Research GmbH

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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="./common/header.jsp">
    <jsp:param name="activeMenu" value="client"/>
</jsp:include>
<link rel="stylesheet" href="<c:url value="./static/css/prettify.css" />" type="text/css"/>
<link rel="stylesheet" href="<c:url value="./static/css/client.css" />" type="text/css"/>
<link rel="stylesheet" href="<c:url value="./static/css/codemirror.css" />" type="text/css"/>
<link rel="stylesheet" href="<c:url value="./static/css/codemirror.custom.css" />" type="text/css"/>
<link rel="stylesheet" href="<c:url value="./static/css/foldgutter.css" />" type="text/css"/>
<script type="text/javascript" src="<c:url value="./static/lib/codemirror.js" />"></script>
<script type="text/javascript" src="<c:url value="./static/lib/codemirror/foldcode.js" />"></script>
<script type="text/javascript" src="<c:url value="./static/lib/codemirror/foldgutter.js" />"></script>
<script type="text/javascript" src="<c:url value="./static/lib/codemirror/brace-fold.js" />"></script>
<script type="text/javascript" src="<c:url value="./static/lib/codemirror/xml-fold.js" />"></script>
<script type="text/javascript" src="<c:url value="./static/lib/codemirror/comment-fold.js" />"></script>
<script type="text/javascript" src="<c:url value="./static/lib/prettify.min.js" />"></script>
<script type="text/javascript" src="<c:url value="./static/lib/XmlBeautify.js" />"></script>
<script type="text/javascript" src="<c:url value="./static/js/client.js" />"></script>
<script type="text/javascript" src="<c:url value="./static/js/application.js" />"></script>
<jsp:include page="./common/logotitle.jsp">
    <jsp:param name="title" value="52&deg;North SOS Test Client"/>
    <jsp:param name="leadParagraph" value="Choose a request from the examples or write your own to test the SOS."/>
</jsp:include>


<div>
    <h3>Examples</h3>
    <p><b>NOTE:</b> Requests use example values and are not dynamically
        generated from values in this SOS. Construct valid requests by changing
        request values to match values in the Capabilities response.</p>
    <p><b>NOTE:</b> For security reasons, the transactional SOS operations
        are disabled by default and the <i>Transactional Security</i> is actived
        by default with allowed IPs <i>127.0.0.1</i>. The transactional
        operations can be activated in the <a href="<c:url value="/admin/operations" />">Operations settings</a>
        and the <i>Transactional Security</i> can be deactivated in the
        <a href="<c:url value="/admin/settings#transactional_security" />">Transactional
            Security tab of the settings</a>.</p>
    <div class="form-row">
        <div class="col">
            <select id="service" class="col"></select>
        </div>
        <div class="col">
            <select id="version" class="col"></select>
        </div>
        <div class="col">
            <select id="binding" class="col"></select>
        </div>
        <div class="col">
            <select id="operation" class="col"></select>
        </div>
    </div>
    <div class="form-row">
        <select id="request" class="col"></select>
    </div>
    <h3 id="top">Service URL</h3>
    <input id="url" class="col-lg-12" type="text" placeholder="Service URL" value=""/>
    <h3>Request</h3>
    <div class="form-row align-items-center">
        <div class="col">
            <select id="method" class="col">
                <option>GET</option>
                <option>HEAD</option>
                <option>POST</option>
                <option>PUT</option>
                <option>DELETE</option>
                <option>OPTIONS</option>
            </select>
        </div>
        <div class="col">
            <input id="content-type" type="text" class="col" placeholder="Content-Type"/>
        </div>
        <div class="col">
            <input id="accept" type="text" class="col" placeholder="Accept"/>
        </div>

        <div class="col">
            <div class="btn-group pull-right">
                <button id="permalink" class="btn btn-sm" type="button">Permalink</button>
                <button class="btn btn-sm dropdown-toggle" data-toggle="dropdown">
                    Syntax <span class="caret"></span>
                </button>
                <ul id="mode" class="dropdown-menu">
                    <li><a href="#" data-mode="application/xml">XML</a></li>
                    <li><a href="#" data-mode="application/json">JSON</a></li>
                </ul>
            </div>
        </div>
    </div>
    <textarea id="editor" name="request" class="col-lg-12"></textarea>
    <div id="send-group" class="pull-right control-group">
        <button id="send-button" type="button" class="btn btn-info inline">Send</button>
    </div>
    <div id="response" class="col-lg-12"></div>
</div>

<c:forEach items="${operations}" var="ao">
    <div class="operations" data-method="${ao.method}" data-binding="${ao.contentType}" data-service="${ao.service}"
         data-version="${ao.version}" data-operation="${ao.operation}"></div>
</c:forEach>

<div id="url_client_config" data-value='<c:url value="./static/conf/client-config.json" />'></div>
<div id="url_service" data-value='<c:url value="/service" />'></div>

<jsp:include page="./common/footer.jsp"/>
