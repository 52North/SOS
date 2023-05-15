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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sos" uri="http://52north.org/communities/sensorweb/sos/tags" %>

<jsp:include page="../common/header.jsp">
	<jsp:param name="activeMenu" value="admin" />
</jsp:include>

<link rel="stylesheet" href="<c:url value="/static/css/prettify.css" />" type="text/css" />
<link rel="stylesheet" href="<c:url value="/static/css/codemirror.css" />" type="text/css" />
<link rel="stylesheet" href="<c:url value="/static/css/codemirror.custom.css" />" type="text/css" />
<link rel="stylesheet" href="<c:url value="/static/css/sensors.css" />" type="text/css" />
<script type="text/javascript" src="<c:url value="/static/lib/codemirror.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/codemirror/xml.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/prettify.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/XmlBeautify.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/js/jquery.additions.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/js/EventMixin.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/jsxml-0.2.2.js" />"></script>

<script type="text/javascript" src="<c:url value="/static/js/admin/sensors.js" />"></script>

<jsp:include page="../common/logotitle.jsp">
	<jsp:param name="title" value="Procedure Descriptions" />
	<jsp:param name="leadParagraph" value="" />
</jsp:include>


<div id="procedure-container">
	<div class="row">
		<div class="col-lg-12 form-inline" style="margin-bottom: 5px;">
			<select id="id" class="col-lg-6">
				<option disabled="disabled" selected="selected" style="display: none;" value="">Procedures</option>
			</select>
			<div class="btn-group">
				<button id="save" title="Save Procedure Description" type="button" class="btn btn-icon stcaps-edit-button"><i class="icon-ok"></i></button>
				<button id="delete" title="Delete Procedure Description" type="button" class="btn btn-icon stcaps-edit-button"><i class="icon-remove"></i></button>
			</div>
		</div>
	</div>
	<textarea id="editor" class="col-lg-12"></textarea>
</div>

<p id="back-top" style="display: none;"><a href="#top"><i class="icon-chevron-up"></i>Back to Top</a></p>

<div id="sensors" data-value='${sos:collectionToJson(sensors)}'></div>
<div id="procedureFormatMap" data-value="${sos:mapToJson(procedureFormatMap)}"></div>
<div id="baseUrl" data-value='<c:url value="/"/>'></div>
<div id="describeSensorRequestMethod" data-value='${describeSensorRequestMethod}'></div>

<jsp:include page="../common/footer.jsp" />
