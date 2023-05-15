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
<jsp:include page="../common/header.jsp">
    <jsp:param name="activeMenu" value="admin" />
</jsp:include>
<jsp:include page="../common/logotitle.jsp">
    <jsp:param name="title" value="Observable Properties" />
    <jsp:param name="leadParagraph" value="Rename Observable Properties" />
</jsp:include>

<div id="url_observableProperties" data-value='<c:url value="/admin/observableProperties" />'></div>

<script type="text/javascript" src="<c:url value="/static/js/admin/observableProperties.js" />"></script>

<ul class="unstyled" class="col-lg-12">
    <c:forEach items="${observableProperties}" var="op">
        <li>
            <div class="control-group">
                <div class="observableProperty input-group" style="margin-right: 84px; width:100%">
                    <input class="input-block-level" type="text" value="${op}"/>
                    <button class="btn" type="button">Change!</button>
                </div>
            </div>
        </li>
    </c:forEach>
</ul>
<jsp:include page="../common/footer.jsp" />
