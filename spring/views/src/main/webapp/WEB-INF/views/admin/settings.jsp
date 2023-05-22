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
<%@ taglib prefix="sos" uri="http://52north.org/communities/sensorweb/sos/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="activeMenu" value="admin" />
</jsp:include>


<div id="url_settings" data-value='<c:url value="/admin/settings" />'></div>
<div id="url_settingDefinitions" data-value='<c:url value="/settingDefinitions.json" />'></div>
<div id="settings_data" data-value='${sos:htmlEscape(settings) }'></div>
<div id="admin_username" data-value='${admin_username}'></div>

<script type="text/javascript" src="<c:url value="/static/js/admin/settings.js" />"></script>


<div class="row">
    <div class="col-lg-9">
        <h2>Change SOS Configuration</h2>
        <p class="lead">You can change the current SOS settings or export the settings to back them up and use them in another installation.</p>
    </div>
    <div class="col-lg-3 header-img-span">
        <div class="row">
            <div class="col-lg-3">
                <img src="<c:url value="/static/images/52n-logo-220x80.png"/>" />
            </div>
        </div>
        <div class="row">
            <div class="col-lg-3">
                <a id="export" class="btn btn-block btn-info" href="settings.json" target="_blank">Export Settings</a>
            </div>
        </div>
    </div>
</div>

<form id="settings" class=""></form>

<br/>
<jsp:include page="../common/footer.jsp" />
