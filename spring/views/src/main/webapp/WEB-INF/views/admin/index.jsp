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
<%@ taglib prefix="sos" uri="http://52north.org/communities/sensorweb/sos/tags" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="activeMenu" value="admin" />
</jsp:include>
<jsp:include page="../common/logotitle.jsp">
	<jsp:param name="title" value="Administration Panel" />
	<jsp:param name="leadParagraph" value="Use the admin menu above to select different administrative tasks." />
</jsp:include>
<p class="pull-right">
<jsp:include page="cache-reload.jsp" />
</p>

<c:if test="${warning}">
    <script type="text/javascript">
    showMessage('<b>Warning!</b> You are using the default credentials to log in. Please change them \
                   <a href="<c:url value="/admin/settings#credentials"/>">here</a> as soon as possible!');
    </script>
</c:if>


<c:if test="${sos:hasClient()}">
<div class="row" style="margin-top: 50px">
    <div class="span12">
    <p>A test data set can be inserted using the <a href="<c:url value="/client?load=exampleData"/>">Test client</a>. For this the JSON Binding and the <code>Batch</code>, <code>InsertSensor</code>, <code>InsertObservation</code> and <code>InsertResultTemplate</code> operations have to be active. Be aware that it only can be removed by cleaning the entire database.</p>
</c:if>


<div class="row" style="margin-top: 40px">
    <div class="span12">
        <c:if test="${not empty metadata.VERSION}">
            <p><strong>Version:</strong> ${fn:escapeXml(metadata.VERSION)}</p>
        </c:if>
        <c:if test="${not empty metadata.GIT_BRANCH}">
            <p><strong>Branch:</strong> ${fn:escapeXml(metadata.GIT_BRANCH)}</p>
        </c:if>
        <c:if test="${not empty metadata.GIT_COMMIT}">
            <p><strong>Revision:</strong><a href="https://github.com/52North/SOS/commit/${fn:escapeXml(metadata.GIT_COMMIT)}"> ${fn:escapeXml(metadata.GIT_COMMIT)}</a></p>
        </c:if>
        <c:if test="${not empty metadata.BUILD_DATE}">
            <p><strong>Build date:</strong> ${fn:escapeXml(metadata.BUILD_DATE)}</p>
        </c:if>
        <c:if test="${not empty metadata.INSTALL_DATE}">
            <p><strong>Installation date:</strong> ${fn:escapeXml(metadata.INSTALL_DATE)}</p>
        </c:if>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
