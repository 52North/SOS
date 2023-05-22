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
<%@ taglib prefix="sos" uri="http://52north.org/communities/sensorweb/sos/tags" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="activeMenu" value="admin" />
</jsp:include>
<jsp:include page="../common/logotitle.jsp">
    <jsp:param name="title" value="Datasource Settings" />
    <jsp:param name="leadParagraph" value="Here you can change basic datasource settings" />
</jsp:include>

<p>These settings are intended to be changed in case your datasource moved to a different server or your credentials changed. It will not check or create a database schema.</p>
<p>To change more advanced settings of the datasource configuration, for example the datasource driver or connection pool, please export your settings using <a href="<c:url value="/admin/settings.json"/>">this configuration file</a> and re-run the installer after <a href="<c:url value="/admin/reset"/>">resetting</a> this installation.</p>


<script type="text/javascript" src="<c:url value="/static/lib/parseuri.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/js/admin/datasource-settings.js" />"></script>

<div id="error_data" data-value="${error}"></div>
<div id="settings_data" data-value='${sos:htmlEscape(settings) }'></div>

<form action="<c:url value="/admin/datasource/settings"/>" method="POST" class="">
    <div id="settings"></div>
    <div class="form-actions">
        <button id="save" type="submit" class="btn btn-info">Save</button>
    </div>
</form>

<jsp:include page="../common/footer.jsp" />
