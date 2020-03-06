<%--

    Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="header.jsp">
	<jsp:param name="step" value="3" />
</jsp:include>
<jsp:include page="../common/logotitle.jsp">
	<jsp:param name="title" value="Settings - change with care!" />
	<jsp:param name="leadParagraph" value="You can change these settings later in the administrative backend." />
</jsp:include>

<script type="text/javascript">
    if (document.referrer) {
        if (document.referrer.matches(/install\/database/)) {
            showSuccess("Database configuration successfully tested.");
        }
    }
</script>

<%
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("HTTP_CLIENT_IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getRemoteAddr();
    }
    pageContext.setAttribute("clientIp", ip);
%>

<script type="text/javascript">
    function waitForElementToDisplayAfter(selector, time, content) {
        if($(selector).length > 0) {
            $(selector).after(content);
            return;
        }
        else {
            setTimeout(function() {
                waitForElementToDisplayAfter(selector, time, content);
            }, time);
        }
    }

    $( document ).ready(function() {
        waitForElementToDisplayAfter(
            "#service_transactionalallowedips > div.controls > span.help-block",
            1000,
            "<br /><span class='alert alert-info'>" +
            "Consider entering the IP of your machine: " +
            "<code>" +
            "<c:out value="${clientIp}" escapeXml="false"/>" +
            "</code>" +
            "</span>"
        );
    });
</script>

<form action="<c:url value="/install/settings" />" method="POST" class="form-horizontal">
	<div id="settings"></div>
	<script type="text/javascript">
	$.getJSON('<c:url value="/settingDefinitions.json" />', function(settingDefinitions) {
 		var $container = $("#settings");
		var settings = ${settings};
		generateSettings(settingDefinitions, settings, $container, true);
		$("#service_identification .control-group:first").before("<legend>Standard Settings</legend>");
		$("#service_provider .control-group:first").before("<legend>Standard Settings</legend>");
		$("#service_identification .control-group:last").before("<legend>Extended Settings</legend>");
		$("#service_provider .control-group:last").before("<legend>Extended Settings</legend>");

		if (!settings["service.sosUrl"]) {
			$("input[name='service.sosUrl']").val(window.location.toString()
				.replace(/install\/settings.*/, "service")).trigger("input");
		}

		$(".required").bind("keyup input change", function() {
            var valid = true;
            $(".required").each(function(){
                var val = $(this).val();
                return valid = (val !== null && val !== undefined && val !== "");
            });
            if (valid) {
                $("button[type=submit]").removeAttr("disabled");
            } else {
                $("button[type=submit]").attr("disabled", true);
            }
        });

        $(".required:first").trigger("change");
        parsehash();
	});
    </script>

	<hr/>
	<div>
        <a href="<c:url value="/install/datasource" />" class="btn">Back</a>
        <button type="submit" class="btn btn-info pull-right">Next</button>
	</div>
</form>

<jsp:include page="../common/footer.jsp" />
