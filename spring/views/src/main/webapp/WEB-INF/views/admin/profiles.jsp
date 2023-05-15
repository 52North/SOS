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

<jsp:include page="../common/header.jsp">
    <jsp:param name="activeMenu" value="admin"/>
</jsp:include>

<link rel="stylesheet" href="<c:url value="/static/css/prettify.css" />" type="text/css"/>
<link rel="stylesheet" href="<c:url value="/static/css/codemirror.css" />" type="text/css"/>
<link rel="stylesheet" href="<c:url value="/static/css/codemirror.custom.css" />" type="text/css"/>
<link rel="stylesheet" href="<c:url value="/static/css/profiles.css" />" type="text/css"/>
<script type="text/javascript" src="<c:url value="/static/lib/codemirror.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/prettify.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/XmlBeautify.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/js/jquery.additions.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/js/EventMixin.js" />"></script>

<jsp:include page="../common/logotitle.jsp">
    <jsp:param name="title" value="Profiles Settings"/>
    <jsp:param name="leadParagraph" value=""/>
</jsp:include>

<script type="text/javascript" src="<c:url value="/static/js/admin/profiles.js" />"></script>

<div id="profiles-container">
    <div class="form-inline">
        <div class="col">
            <select id="id" class="col">
                <option disabled="disabled" selected="selected" style="display: none;" value="">${active}</option>
            </select>
        </div>

        <div class="col">
            <button id="activate" title="Activate selected profile" type="button"
                    class="btn stcaps-edit-button">Activate Profile!
            </button>
            <button id="reload" title="Reload profiles" type="button" name=""
                    class="btn stcaps-edit-button">
                Reload Profiles!
            </button>
        </div>
    </div>
    <%--<textarea id="description" readonly="readonly" class="col-lg-12"></textarea> --%>
</div>

<p id="back-top" style="display: none;"><a href="#top"><i class="icon-chevron-up"></i>Back to Top</a></p>

<div id="url_activate" data-value='<c:url value="/admin/profiles/activate" />'></div>
<div id="url_reload" data-value='<c:url value="/admin/profiles/reload" />'></div>
<div id="url_base" data-value='<c:url value="/" />'></div>

<c:forEach items="${profiles}" var="p">
    <div class="profile" data-value="${p}"></div>
</c:forEach>

<jsp:include page="../common/footer.jsp"/>
