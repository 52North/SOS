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

<link rel="stylesheet" href="<c:url value="/static/lib/prettify.css" />" type="text/css" />
<link rel="stylesheet" href="<c:url value="/static/lib/codemirror-2.34.css" />" type="text/css" />  
<link rel="stylesheet" href="<c:url value="/static/css/codemirror.custom.css" />" type="text/css" />
<script type="text/javascript" src="<c:url value="/static/lib/codemirror-2.34.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/codemirror-2.34-plsql.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/prettify.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/vkbeautify-0.99.00.beta.js" />"></script>

<jsp:include page="../common/logotitle.jsp">
    <jsp:param name="title" value="Datasource Panel" />
    <jsp:param name="leadParagraph" value="Here you can query the datasource directly." />
</jsp:include>

<div class="pull-right">
    <ul class="inline">
        <li><button data-target="#confirmDialogClear" data-toggle="modal" title="Clear Datasource" class="btn btn-danger">Clear Datasource</button></li>
        <li><button data-target="#confirmDialogDelete" data-toggle="modal" title="Delete deleted Observations" class="btn btn-danger">Delete deleted Observations</button></li>
    </ul>
</div>

<form id="form" action="" method="POST">
    <h3>Query</h3>
    <p>Here are some raw SQL query examples which can be copied and executed in the appropriate database tool, e.g. pgAdmin.</p>
    <p>The MySQL examples use 'sos' as schema. If you use another schema, please change the 'sos' definition to your schema. This can be easily done with a text editor by search for 'sos.' and replace with 'your_schema.'.</p>
    <div class="controls-row">
        <select id="input-query" class="span12 pull-right">
            <option value="" disabled selected style="display: none;">Select a example query &hellip;</option>
        </select>
    </div>
    <div class="controls-row">
        <textarea id="editor" class="span12" rows="15"></textarea>
    </div>
    <br />
    <%--<div class="controls-row">
        <div class="pull-right">
            <button id="send-button" type="button" class="btn btn-info inline">Send</button>
        </div>
    </div> --%>
</form>
<div id="result"></div>


<div class="modal hide fade in" id="confirmDialogClear">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3>Are you really sure?</h3>
    </div>
    <div class="modal-body">
        <p><span class="label label-important">Warning!</span> This will remove all contents (except settings) from the datasource!</p>
    </div>
    <div class="modal-footer">
        <button type="button" class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
        <button type="button" id="clear" class="btn btn-danger">Do it!</button>
    </div>
</div>

<div class="modal hide fade in" id="confirmDialogDelete">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3>Are you really sure?</h3>
    </div>
    <div class="modal-body">
        <p><span class="label label-important">Warning!</span> This will remove all deleted observations from the datasource!</p>
    </div>
    <div class="modal-footer">
        <button type="button" class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
        <button type="button" id="delete" class="btn btn-danger">Do it!</button>
    </div>
</div>

<script type="text/javascript">
    $(function() {
        var $clearDialog = $("#confirmDialogClear");
        var $deleteDeletedDialog = $("#confirmDialogDelete");        
        var supportsClear = ${supportsClear};
        var supportsDeleteDeleted = ${supportsDeleteDeleted};

        if (supportsClear) {
            $("#clear").click(function() {
                $clearDialog.find("button")/*.add($button)*/.attr("disabled", true);
                $.ajax({
                    "url": "<c:url value="/admin/datasource/clear" />",
                    "type": "POST"
                }).fail(function(error) {
                    showError("Request failed: " + error.status + " " + error.statusText);
                    $clearDialog.find("button").removeAttr("disabled");
                    $clearDialog.modal("hide");
                }).done(function() {
                    showSuccess("The datasource was cleared");
                    $clearDialog.find("button").removeAttr("disabled");
                    $clearDialog.modal("hide");
                });
            });
        } else {
            $("button[data-target=#confirmDialogClear]").attr("disabled", true);
        }

        if (supportsDeleteDeleted) {
            $("#delete").click(function() {
                $deleteDeletedDialog.find("button")/*.add($button)*/.attr("disabled", true);
                $.ajax({
                    "url": "<c:url value="/admin/datasource/deleteDeletedObservations" />",
                    "type": "POST"
                }).fail(function(error) {
                    if (error.responseText) {
                        showError(error.responseText);
                    } else {
                        showError("Request failed: " + error.status + " " + error.statusText);
                    }

                    $deleteDeletedDialog.find("button").removeAttr("disabled");
                    $deleteDeletedDialog.modal("hide");
                }).done(function() {
                    showSuccess("The deleted observation were deleted.");
                    $deleteDeletedDialog.find("button").removeAttr("disabled");
                    $deleteDeletedDialog.modal("hide");
                });
            });
        } else {
            $("button[data-target=#confirmDialogDelete]").attr("disabled", true);
        }
    });
</script>

<script type="text/javascript">
$(function() {
    var editor = CodeMirror.fromTextArea($("#editor").get(0), 
        { "mode": "text/x-plsql", "lineNumbers": true, "lineWrapping": true });

    $.get("<c:url value="/static/conf/sql-queries.json"/>", function(settings) {
        if ((typeof settings) === "string") {
            settings = JSON.parse(settings);
        }
        var $select = $("#input-query");

        for (var key in settings.queries) {
            $("<option>").text(key).appendTo($select);
        }
        $select.change(function() {
            var sql = settings.queries[$(this).val()];
            sql = vkbeautify.sql(sql, 2);
            editor.setValue(sql);
        });
        $("#send-button").click(function() {
            var query = editor.getValue();
            if (query === "") {
                showError("No query specified.");
            } else {
                var $result = $("#result");
                $result.slideUp("fast");
                $result.children().remove();
                $.ajax({
                    "url": "<c:url value="/admin/datasource" />",
                    "type": "POST",
                    "data": encodeURIComponent(query),
                    "contentType": "text/plain",
                    "dataType": "json"
                }).fail(function(error){
                    showError("Request failed: " + error.status + " " + error.statusText);
                }).done(function(response){
                    if (typeof(response) === "string") {
                        response = $.parseJSON(response);
                    }
                    if (response.error) {
                        showError(response.error);
                    } else if (response.message) {
                        showSuccess(response.message);
                    } else {
                        $("<h3>").text("Result").appendTo($result);
                        $("html, body").animate({ 
                            scrollTop: $("#result").offset().top
                         }, "slow");
                        var $table = $("<table>");
                        $table.addClass("table table-striped table-bordered table-condensed");
                        $table.appendTo($result);
                        if (response.names) {
                            var $tr = $("<tr>");
                            for (var i = 0; i < response.names.length; ++i) {
                                $("<th>").text(response.names[i]).appendTo($tr);
                            }
                            $tr.appendTo($table);
                        }
                        if (response.rows) {
                            for (var i = 0; i < response.rows.length; ++i) {
                                var row = response.rows[i];
                                var $row = $("<tr>");
                                for (var j = 0; j < row.length; ++j) {
                                    $("<td>").text(row[j]).appendTo($row);
                                }
                                $row.appendTo($table);
                            }
                        }
                        $result.slideDown();
                    }
                });
            }
        });
    });
});
</script>
<br/>
<jsp:include page="../common/footer.jsp" />
