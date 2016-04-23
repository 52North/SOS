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
	<jsp:param name="title" value="Reset SOS" />
	<jsp:param name="leadParagraph" value="Use this functionality with care!" />
</jsp:include>
<hr/>
<p>If you click on the Reset button, the database access configuration of this SOS instance will be deleted. The database will stay intact and can be used for a new SOS install.</p>

<p>If you merely want to remove test data, use the test data removal function on the database admin page. If you want to overwrite the existing database, please select the option to delete existing tables in the installation wizard.</p>

<form id="reset-form" method="POST" action="reset" />
<div class="modal hide fade in" id="confirmDialog">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    <h3>Are you really sure?</h3>
  </div>
  <div class="modal-body">
     <p><span class="label label-important">Warning!</span> This will delete the database configuration of the this SOS instance.</p>
  </div>
  <div class="modal-footer">
    <button type="button" class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
    <button type="button" id="reset" class="btn btn-danger">Reset</button>
  </div>
</div>
<div class="pagination-centered">
    <button type="button" id="showDialog" class="btn btn-danger btn-large">Reset</button>
</div>
<script type="text/javascript">
    $("#confirmDialog").modal({
        "keyboard": true,
        "show": false
    });
    $("#showDialog").click(function(){
        $("#confirmDialog").modal("show");
    });
    $(function() {
        $("#reset").click(function() {
            $("#reset-form").submit();
        });
    });
</script>
<br/>
<jsp:include page="../common/footer.jsp" />
