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
    <jsp:param name="title" value="Configure Bindings" />
    <jsp:param name="leadParagraph" value="Disable or enable bindings" />
</jsp:include>


<div id="url_bindings_json" data-value='<c:url value="/admin/bindings/json" />'></div>
<script type="text/javascript" src="<c:url value="/static/js/admin/bindings.js" />"></script>

<div class="btn-group pull-right">
    <button id="activateAll" class="btn btn-success"><i class="icon-ok-circle icon-white" style="color:#fff;"></i></button>
    <button id="disableAll" class="btn btn-danger"><i class="icon-ban-circle icon-white"></i></button>
</div>

<table id="bindings" class="table table-striped table-bordered">
    <colgroup>
        <col style="width: 80%;"/>
        <col style="width: 20%;"/>
    </colgroup>
    <thead>
        <tr>
            <th>Path</th>
            <th>Status</th>
        </tr>
    </thead>
    <tbody></tbody>
</table>

<jsp:include page="../common/footer.jsp" />
