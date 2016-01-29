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
<%@ taglib prefix="sos" uri="http://52north.org/communities/sensorweb/sos/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="common/header.jsp">
    <jsp:param name="activeMenu" value="documentation" />
</jsp:include>
<jsp:include page="common/logotitle.jsp">
	<jsp:param name="title" value="52&deg;North SOS Documentation" />
	<jsp:param name="leadParagraph" value="Documentation of the 52&deg;North Sensor Observation Service" />
</jsp:include>
<hr/>
<p>The 52&deg;North SOS documentation is available in the <a href="https://52north.org/twiki/bin/view/Main/WebHome" target="_blank" title="52&deg;North Wiki">52&deg;North Wiki</a>.</p>

<p>The user guide, including installation and configuration documentation, can be found <a href="https://wiki.52north.org/bin/view/SensorWeb/SensorObservationServiceIVDocumentation" target="_blank" title="User guide">here</a>.</p>

<p>The developer guide can be found <a href="https://wiki.52north.org/bin/view/SensorWeb/SensorObservationServiceDeveloperGuide" target="_blank" title="Developer guide">here</a>.</p>

<h4>Other Documentations</h4>

<p>Links to documentations of included independent software components:</p>
<c:if test="${sos:documentExtensionExists(pageContext.servletContext, 'api-doc/index.html')}">
    <li>
    	Sensor Web Client REST-API
        <a id="rest-menuitem" class="menu-item4" target="_blank" href="<c:url value="/static/doc/api-doc" />">
            <span class="menu-title">documentation</span>
        </a>
    </li>
</c:if>


<jsp:include page="common/footer.jsp" />
