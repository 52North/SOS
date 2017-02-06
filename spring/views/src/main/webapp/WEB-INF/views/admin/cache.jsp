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
<jsp:include page="../common/header.jsp">
    <jsp:param name="activeMenu" value="admin" />
</jsp:include>
<jsp:include page="../common/logotitle.jsp">
	<jsp:param name="title" value="Cache Summary" />
	<jsp:param name="leadParagraph" value="Summary information for the SOS cache." />
</jsp:include>
<p class="pull-right">
<jsp:include page="cache-reload.jsp" />
</p>

<script type="text/javascript">
    var loadCacheSummary = function() {
        $.ajax({
            url: "<c:url value="/admin/cache/summary"/>",
            type: "GET",
            dataType: "json"
        }).done(function(data) {
            var $cacheSummaryDiv = $("#cacheSummary");
            $cacheSummaryDiv.empty();
            var $table = $("<table />").appendTo($cacheSummaryDiv); 
            $.each(data, function (key, val) {
                var $tr = $("<tr />").appendTo($table);
                $("<td />").appendTo($tr).text(key);
                $("<td />").appendTo($tr).text(val);
            });
        }).fail(function(error){
            showError("Capabilites cache summary request failed: " + error.responseText);
        });
    };

    $(document).on("cache-loading-complete", function(){
    	loadCacheSummary();
    });

    //document ready
    $(function() {
        loadCacheSummary();
    });
</script>

<style>
div#cacheSummary table {
  border-collapse:collapse;
  
}

div#cacheSummary table td {
  text-align: left;
  padding: 3px;
}

div#cacheSummary table td:first-child{
  font-weight: bold;
}
</style>

<div id="cacheSummary" class="row"></div>
        
<jsp:include page="../common/footer.jsp" />
