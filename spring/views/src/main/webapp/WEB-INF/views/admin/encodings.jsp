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
    <jsp:param name="title" value="Configure Encodings" />
    <jsp:param name="leadParagraph" value="Disable or enable encodings" />
</jsp:include>

<link rel="stylesheet" href="<c:url value='/static/lib/jquery.tablesorter-bootstrap-2.712.min.css' />">
<script type="text/javascript" src="<c:url value='/static/lib/jquery.tablesorter-2.7.12.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/static/lib/jquery.tablesorter.widgets-2.7.12.min.js'/>"></script>

<table id="observationEncodings" class="table table-striped table-bordered">
    <caption>Observation Encodings</caption>
    <thead>
        <tr>
            <th>Service</th>
            <th>Version</th>
            <th>Encoding</th>
            <th>Status</th>
        </tr>
    </thead>
    <tbody></tbody>
</table>

<table id="procedureEncodings" class="table table-striped table-bordered">
    <caption>Procedure Encodings</caption>
    <thead>
        <tr>
            <th>Service</th>
            <th>Version</th>
            <th>Encoding</th>
            <th>Status</th>
        </tr>
    </thead>
    <tbody></tbody>
</table>

<script type="text/javascript">
jQuery(document).ready(function($) {

    $.extend($.tablesorter.themes.bootstrap, {
        table: "table table-bordered",
        header: "bootstrap-header",
        sortNone: "bootstrap-icon-unsorted",
        sortAsc: "icon-chevron-up",
        sortDesc: "icon-chevron-down"
    });

    function observationEncodings(encodings) {
        var $tbody = $("#observationEncodings tbody"), i, o, $row, $button;
        for (i = 0; i < encodings.length; ++i) {
            o = encodings[i];
            $row = $("<tr>");
            $("<td>").addClass("service").text(o.service).appendTo($row);
            $("<td>").addClass("version").text(o.version).appendTo($row);
            $("<td>").addClass("encoding").text(o.responseFormat).appendTo($row);
            $button = $("<button>").attr("type", "button")
                    .addClass("btn btn-small btn-block").on("click", function() {
                var $b = $(this),
                    $tr = $b.parents("tr"),
                    active = !$b.hasClass("btn-success"),
                    j = {
                        service: $tr.find(".service").text(),
                        version: $tr.find(".version").text(),
                        responseFormat: $tr.find(".encoding").text(),
                        active: active
                    };
                $b.prop("disabled", true);
                $.ajax("<c:url value='/admin/encodings/json'/>", {
                    type: "POST",
                    contentType: "application/json",
                    data: JSON.stringify(j)
                }).fail(function(e) {
                    showError("Failed to save observation encoding: " 
                        + e.status + " " + e.statusText);
                    $b.prop("disabled", false);
                }).done(function() {
                    $b.toggleClass("btn-danger btn-success")
                      .text(active ? "active" : "inactive")
                      .prop("disabled", false);
                    
                });
            });
            if (o.active) { 
                $button.addClass("btn-success").text("active"); 
            } else {
                $button.addClass("btn-danger").text("inactive"); 
                
            }
            $("<td>").addClass("status").append($button).appendTo($row);
            
            $tbody.append($row);    
        }
        
        $("#observationEncodings").tablesorter({
            theme : "bootstrap",
            widgets : [ "uitheme", "zebra" ],
            headerTemplate: "{content} {icon}",
            widthFixed: true,
            headers: { 
                0: { sorter: "text" },
                1: { sorter: "text" },
                2: { sorter: "text" },
                3: { sorter: false } 
            },
            sortList: [ [0,0], [1,1], [2,0] ]
        });
    }

    function procedureEncodings(encodings) {
        var $tbody = $("#procedureEncodings tbody"), i, o, $row, $button;
        for (i = 0; i < encodings.length; ++i) {
            o = encodings[i];
            $row = $("<tr>");
            $("<td>").addClass("service").text(o.service).appendTo($row);
            $("<td>").addClass("version").text(o.version).appendTo($row);
            $("<td>").addClass("encoding").text(o.procedureDescriptionFormat).appendTo($row);
            $button = $("<button>").attr("type", "button")
                    .addClass("btn btn-small btn-block").on("click", function() {
                var $b = $(this),
                    $tr = $b.parents("tr"),
                    active = !$b.hasClass("btn-success"),
                    j = {
                        service: $tr.find(".service").text(),
                        version: $tr.find(".version").text(),
                        procedureDescriptionFormat: $tr.find(".encoding").text(),
                        active: active
                    };
                $b.prop("disabled", true);
                $.ajax("<c:url value='/admin/encodings/json'/>", {
                    type: "POST",
                    contentType: "application/json",
                    data: JSON.stringify(j)
                }).fail(function(e) {
                    showError("Failed to save procedure description encoding: " 
                        + e.status + " " + e.statusText);
                    $b.prop("disabled", false);
                }).done(function() {
                    $b.toggleClass("btn-danger btn-success")
                      .text(active ? "active" : "inactive")
                      .prop("disabled", false);
                    
                });
            });
            if (o.active) { 
                $button.addClass("btn-success").text("active"); 
            } else {
                $button.addClass("btn-danger").text("inactive"); 
                
            }
            $("<td>").addClass("status").append($button).appendTo($row);
            
            $tbody.append($row);    
        }
                $("#procedureEncodings").tablesorter({
            theme : "bootstrap",
            widgets : [ "uitheme", "zebra" ],
            headerTemplate: "{content} {icon}",
            widthFixed: true,
            headers: { 
                0: { sorter: "text" },
                1: { sorter: "text" },
                2: { sorter: "text" },
                3: { sorter: false } 
            },
            sortList: [ [0,0], [1,1], [2,0] ]
        });
    }

    $.getJSON("<c:url value='/admin/encodings/json'/>", function(j) {
        observationEncodings(j.observationEncodings);
        procedureEncodings(j.procedureEncodings);
    });
});
</script>

<jsp:include page="../common/footer.jsp" />
