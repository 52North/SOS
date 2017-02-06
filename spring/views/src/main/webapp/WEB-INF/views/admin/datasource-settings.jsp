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

<form action="<c:url value="/admin/datasource/settings"/>" method="POST" class="form-horizontal">
    <div id="settings"></div>
    <div class="form-actions">
        <button id="save" type="submit" class="btn btn-info">Save</button>
    </div>
</form>


<script type="text/javascript">
    $(function(){
        warnIfNotHttps();
        if ('${error}' !== '')  {
            showError('${error}');
        }
        var settings;

            <c:if test="${not empty settings}">
        settings = ${settings}
            </c:if>

        if (settings) {
            generateSettings(settings.settings, {}, "#settings", false);
        } else {
            $("#save").attr("disabled", true);
            showWarning("There are no settings to change");
        }

        $("input[type=text],input[type=password],textarea").trigger("input");
        $(".required").bind("keyup input change", function() {
            var valid = true;
            $(".required").each(function(){
                var val = $(this).val();
                return valid = (val !== null && val !== undefined && val !== "");
            });

            var val = $(this).val();
            if (val !== null && val !== undefined && val !== "") {
                $(this).parents(".control-group").removeClass("error");
            } else {
                $(this).parents(".control-group").addClass("error");
            }

            if (valid) {
                $("#save").removeAttr("disabled");
            } else {
                $("#save").attr("disabled", true);
            }
        });
    });
</script>

<jsp:include page="../common/footer.jsp" />
