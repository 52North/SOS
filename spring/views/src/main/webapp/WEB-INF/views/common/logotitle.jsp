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
<%-- include extra title jsp if file exists (used by custom builds) --%>
<c:if test="${sos:viewExists(pageContext.servletContext, 'common/extra-title.jsp')}">
    <jsp:include page="extra-title.jsp" />
</c:if>
<div class="row">
	<div class="span9">

		<h2>${param.title}</h2>
		<p class="lead">${param.leadParagraph}</p>
	</div>
	<div class="span3 header-img-span">
		<img src="<c:url value="/static/images/52n-logo-220x80.png"/>"/>
	</div>
</div>
