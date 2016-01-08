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
	<jsp:param name="title" value="52&deg;SOS Administration Login" />
	<jsp:param name="leadParagraph" value="Please login to view the admin console." />
</jsp:include>
<hr/>
<form action="<c:url value="/j_spring_security_check" />" method="POST" class="form-horizontal">
	<div class="control-group">
		<label class="control-label" for="username">Username</label>
		<div class="controls">
			<input class="input-xlarge" type="text" name="username" autocomplete="off"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label" for="password">Password</label>
		<div class="controls">
			<input class="input-xlarge" type="password" name="password" autocomplete="off">
				<span id="passwordReset" class="help-block">You can reset your admin password by executing the file <code>sql/reset_admin.sql</code> (located inside the SOS installation directory in the webapps folder of your application server) on your database.</span>
			</input>
		</div>
	</div>
	<div class="form-actions">
		<button class="btn" type="submit">Login</button>
	</div>
</form>
<script type="text/javascript">
	$(function(){
        warnIfNotHttps();
		$("#passwordReset").hide();
		if ($.queryParam["error"]) {
			showError("Incorrect username/password. Please try again!");
			$("#passwordReset").fadeIn();
		}
		$("input[type=text],input[type=password]").bind("keyup input", function() {
			var empty = false;
			$("input[type=text], input[type=password]").each(function(i,e) {
				if ($(e).val() === "") { empty = true; }
			});
			$("button[type=submit]").attr("disabled", empty);	
		}).trigger("input");
		$("input[name=username]").focus();
	});
</script>
<jsp:include page="../common/footer.jsp" />
