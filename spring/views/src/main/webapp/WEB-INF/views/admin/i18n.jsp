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
<script type="text/javascript" src="<c:url value="/static/js/I18NController.js" />"></script>


<jsp:include page="../common/logotitle.jsp">
    <jsp:param name="title" value="I18N Settings<sup><small style='color:#f00;'>BETA</small></sup>" />
    <jsp:param name="leadParagraph" value="" />
</jsp:include>

<style type="text/css">
.i18ncontainer { margin-left: 2px; }
.multilingual input.text { width: 655px; }
.multilingual .control-label { font-weight: bold; }
</style>

<script type="text/javascript">
    function xml2string(xml) {
        return typeof(xml) === "string" ? xml : xml.xml ? xml.xml
                : new XMLSerializer().serializeToString(xml);
    }
</script>

<div id="current-setting"></div>

<ul class="nav nav-tabs">
    <li class="active"><a href="#i18n-procedures" data-toggle="tab">Procedures</a></li>
    <li><a href="#i18n-offerings" data-toggle="tab">Offerings</a></li>
    <li><a href="#i18n-observableProperties" data-toggle="tab">Observable Properties</a></li>
    <li><a href="#i18n-features" data-toggle="tab">Features</a></li>
</ul>
<div class="tab-content">
    <div class="active tab-pane" id="i18n-procedures"></div>
    <div class="tab-pane" id="i18n-offerings"></div>
    <div class="tab-pane" id="i18n-observableProperties"></div>
    <div class="tab-pane" id="i18n-features"></div>
</div>

<p id="back-top" style="display: none;"><a href="#top"><i class="icon-chevron-up"></i>Back to Top</a></p>
<script type="text/javascript">
</script>
<script type="text/javascript">
	new I18NController({
		name: "Procedure",
		div: "#i18n-procedures",
		identifiers: ${procedures},
		url: "<c:url value="/admin/i18n/ajax/procedures" />",
		properties: {
			name: "Name",
			description: "Description",
			shortName: "Short Name",
			longName: "Long Name"
		},
	});
	new I18NController({
		name: "Offering",
		div: "#i18n-offerings",
		identifiers: ${offerings},
		url: "<c:url value="/admin/i18n/ajax/offerings" />",
		properties: {
			name: "Name",
			description: "Description"
		},
	});
	new I18NController({
		name: "Observable Property",
		div: "#i18n-observableProperties",
		identifiers: ${observableProperties},
		url: "<c:url value="/admin/i18n/ajax/observableProperties" />",
		properties: {
			name: "Name",
			description: "Description"
		},
	});
	new I18NController({
		name: "Feature",
		div: "#i18n-features",
		identifiers: ${features},
		url: "<c:url value="/admin/i18n/ajax/features" />",
		properties: {
			name: "Name",
			description: "Description"
		},
	});

</script>

<jsp:include page="../common/footer.jsp" />