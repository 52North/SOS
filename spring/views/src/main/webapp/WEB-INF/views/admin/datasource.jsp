<%--

    Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
    <jsp:param name="title" value="Datasource Maintenance" />
    <jsp:param name="leadParagraph" value="Here you can maintain the datasource." />
</jsp:include>
<div>
    <h3>Maintenance</h3>
    <div class="btn-group">
        <button data-target="#confirmDialogAddSampledata" data-toggle="modal" title="Insert sample data" class="btn ">Insert sample data</button>
        <button data-target="#confirmDialogDelete" data-toggle="modal" title="Delete deleted Data" class="btn btn-danger">Delete deleted Data</button>
        <button data-target="#confirmDialogClear" data-toggle="modal" title="Clear Datasource" class="btn btn-danger">Clear Datasource</button>
        <a href="<c:url value="/admin/reset" />" title="Reset Datasource Configuration" class="btn btn-warning">Reset Datasource Configuration</a>
    </div>
</div>

<div>
    <h3>Enhancement</h3>
    <p>In this section you can select and insert pre-defined sets of observableProperties or units of measurement into the database.</p>
    <p>NOTE: The insertion of pre-defined sets of observableProperties or units of measurement requires write access to the database!!!</p>
    <h4>ObservableProperty - Phenomena - Parameter</h4>
    <p>Select one of the predefined observableProperty vocabularies from the drop-down list and the press the 'Load' button to load the predefined data into the SOS.</p>
    <div id="obsprop-container">
	  <div class="row">
	   <div class="span12 form-inline" style="margin-bottom: 5px;">
        <select id="input-phenomena" class="span6">
            <option value="" disabled selected style="display: none;">Select a predefined phenomena vocabulary &hellip;</option>
        </select>
	      <div class="btn-group">
	        <button id="loadObsProps" title="Load ObservableProperties into the database!" type="button" name="" class="btn btn-icon stcaps-edit-button">Load</button>
	      </div>
	    </div>
	   </div>
    </div>

    <h4>UnitsOfMeasure</h4>
    <p>Select one of the predefined units vocabularies from the drop-down list and the press the 'Load' button to load the predefined data into the SOS.</p>
    <div id="units-container">
    <div class="row">
      <div class="span12 form-inline" style="margin-bottom: 5px;">
        <select id="input-units" class="span6">
            <option value="" disabled selected style="display: none;">Select a predefined units vocabulary &hellip;</option>
        </select>
        <div class="btn-group">
          <button id="loadUnits" title="Load Units into the database!" type="button" name="" class="btn btn-icon stcaps-edit-button">Load</button>
        </div>
      </div>
     </div>
    </div>
</div>

<div class="modal hide fade in" id="confirmDialogAddSampledata">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3>Insert sample data?</h3>
    </div>
    <div class="modal-body">
        <p>
            This process might take some time. Depending on the performance of your server/machine. For better 
            monitoring, you can change the logging level to debug for <code>org.n52</code> in the 
            <a href="<c:url value="/admin/logging" />">logging configuration</a> and follow the output of the process.
        </p>
    </div>
    <div class="modal-footer">
        <button type="button" class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
        <button type="button" id="addSampledata" class="btn btn-primary">Insert sample data!</button>
    </div>
</div>

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
        <p><span class="label label-important">Warning!</span> This will remove all deleted data from the datasource!</p>
    </div>
    <div class="modal-footer">
        <button type="button" class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
        <button type="button" id="delete" class="btn btn-danger">Do it!</button>
    </div>
</div>

<div id="wait" class="hide">
    <div id="wait-loader">
        <img src="<c:url value="/static/images/loader.gif"/>">
    </div>
</div>


<script type="text/javascript">
    $(function() {
        var $clearDialog = $("#confirmDialogClear");
        var $deleteDeletedDialog = $("#confirmDialogDelete");
        var $addSampledataDialog = $("#confirmDialogAddSampledata")
        var supportsClear = ${supportsClear};
        var supportsDeleteDeleted = ${supportsDeleteDeleted};
        /*var supportsAddSampledata = ${supportsAddSampledata};*/

        $('#wait').ajaxStart(function() {
            $(this).show();
        }).ajaxComplete(function() {
            $(this).hide();
        });

        if (supportsClear) {
            $("#clear").click(function() {
                $clearDialog.find("button").attr("disabled", true);
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

        /*if (supportsAddSampledata) {*/
            $("#addSampledata").click(function() {
                $addSampledataDialog.find("button").attr("disabled", true);
                $.ajax({
                    "url": "<c:url value="/admin/datasource/addSampledata" />",
                    "type": "POST"
                }).fail(function(xhr, status, error) {
                    showError("Inserting sample data failed: " + xhr.status + " " + xhr.statusText + "\n" + xhr.responseText);
                    $addSampledataDialog.find("button").removeAttr("disabled");
                    $addSampledataDialog.modal("hide");
                }).done(function() {
                    showSuccess("The sample data was inserted.");
                    $addSampledataDialog.find("button").removeAttr("disabled");
                    $addSampledataDialog.modal("hide");
                })
            });
            $("#addSampledata").ajaxStart(function() {
                $addSampledataDialog.modal("hide");
            });
        /*} else {
            $("button[data-target=#confirmDialogAddTestdata]").attr("disabled", true);
        }*/

        if (supportsDeleteDeleted) {
            $("#delete").click(function() {
                $deleteDeletedDialog.find("button").attr("disabled", true);
                $.ajax({
                    "url": "<c:url value="/admin/datasource/deleteDeletedData" />",
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
                    showSuccess("The deleted data were deleted.");
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
    var $select = $("#input-phenomena");

    <c:forEach items="${predefinedPhenomena}" var="p">
        $("<option>").text("${p}").appendTo($select);
    </c:forEach>
    $("#loadObsProps").click(function() {
        var value = $("#input-phenomena :selected").text()
        if (value === "" || value.startsWith("Select a predefined")) {
            showError("No vocabulary selected!");
        } else {
            $.ajax({
                "url": "<c:url value="/admin/datasource/loadPredefinedPhenomena" />",
                "type": "GET",
                "data": {
                	"name": value
                }
            }).fail(function(error){
                showError("Request failed: " + error.responseText);
            }).done(function(response){
            	 showSuccess(response);
            });
        }
    });
});
</script>


<script type="text/javascript">
$(function() {
    var $select = $("#input-units");

    <c:forEach items="${predefinedUnits}" var="p">
        $("<option>").text("${p}").appendTo($select);
    </c:forEach>
    $("#loadUnits").click(function() {
        var value = $("#input-units :selected").text()
        if (value === "" || value.startsWith("Select a predefined")) {
            showError("No vocabulary selected!");
        } else {
            $.ajax({
                "url": "<c:url value="/admin/datasource/loadPredefinedUnits" />",
                "type": "GET",
                "data": {
                  "name": value
                }
            }).fail(function(error){
                showError("Request failed: " + error.responseText);
            }).done(function(response){
            	showSuccess(response);
            });
        }
    });
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
