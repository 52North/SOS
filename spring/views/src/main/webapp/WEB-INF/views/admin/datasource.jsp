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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="activeMenu" value="admin"/>
</jsp:include>

<link rel="stylesheet" href="<c:url value="/static/css/prettify.css" />" type="text/css"/>
<link rel="stylesheet" href="<c:url value="/static/css/codemirror.css" />" type="text/css"/>
<link rel="stylesheet" href="<c:url value="/static/css/codemirror.custom.css" />" type="text/css"/>
<script type="text/javascript" src="<c:url value="/static/lib/codemirror.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/codemirror/sql.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/prettify.min.js" />"></script>

<jsp:include page="../common/logotitle.jsp">
    <jsp:param name="title" value="Datasource Maintenance"/>
    <jsp:param name="leadParagraph" value="Here you can maintain the datasource."/>
</jsp:include>

<script type="text/javascript" src="<c:url value="/static/js/admin/datasource.js" />"></script>

<div>
    <h3>Maintenance</h3>
    <div class="btn-group">
        <button data-target="#confirmDialogAddSampledata" data-toggle="modal" title="Insert sample data" class="btn ">
            Insert sample data
        </button>
        <button data-target="#confirmDialogDelete" data-toggle="modal" title="Delete deleted Data"
                class="btn btn-danger">Delete deleted Data
        </button>
        <button data-target="#confirmDialogClear" data-toggle="modal" title="Clear Datasource" class="btn btn-danger">
            Clear Datasource
        </button>
        <a href="<c:url value="/admin/reset" />" title="Reset Datasource Configuration" class="btn btn-warning">Reset
            Datasource Configuration</a>
    </div>
</div>

<div>
    <h3>Enhancement</h3>
    <p>In this section you can select and insert pre-defined sets of observableProperties or units of measurement into
        the database.</p>
    <p>NOTE: The insertion of pre-defined sets of observableProperties or units of measurement requires write access to
        the database!!!</p>
    <h4>ObservableProperty - Phenomena - Parameter</h4>
    <p>Select one of the predefined observableProperty vocabularies from the drop-down list and the press the 'Load'
        button to load the predefined data into the SOS.</p>
    <div id="obsprop-container">
        <div class="row">
            <div class="col-lg-12 form-inline" style="margin-bottom: 5px;">
                <select id="input-phenomena" class="col-lg-6">
                    <option value="" disabled selected style="display: none;">Select a predefined phenomena vocabulary
                        &hellip;
                    </option>
                    <c:forEach items="${predefinedPhenomena}" var="p">
                        <option>${p}</option>
                    </c:forEach>
                </select>
                <div class="btn-group">
                    <button id="loadObsProps" title="Load ObservableProperties into the database!" type="button" name=""
                            class="btn btn-icon stcaps-edit-button">Load
                    </button>
                </div>
            </div>
        </div>
    </div>

    <h4>UnitsOfMeasure</h4>
    <p>Select one of the predefined units vocabularies from the drop-down list and the press the 'Load' button to load
        the predefined data into the SOS.</p>
    <div id="units-container">
        <div class="row">
            <div class="col-lg-12 form-inline" style="margin-bottom: 5px;">
                <select id="input-units" class="col-lg-6">
                    <option value="" disabled selected style="display: none;">Select a predefined units vocabulary
                        &hellip;
                    </option>
                    <c:forEach items="${predefinedUnits}" var="p">
                        <option>${p}</option>
                    </c:forEach>
                </select>
                <div class="btn-group">
                    <button id="loadUnits" title="Load Units into the database!" type="button" name=""
                            class="btn btn-icon stcaps-edit-button">Load
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="modal hide fade in" id="confirmDialogAddSampledata">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Insert sample data?</h3>
            </div>
            <div class="modal-body">
                <p>
                    This process might take some time. Depending on the performance of your server/machine. For better
                    monitoring, you can change the logging level to debug for <code>org.n52</code> in the
                    <a href="<c:url value="/admin/logging" />">logging configuration</a> and follow the output of the
                    process.
                </p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
                <button type="button" id="addSampledata" class="btn btn-primary">Insert sample data!</button>
            </div>
        </div>
    </div>
</div>

<div class="modal hide fade in" id="confirmDialogClear">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Are you really sure?</h3>
            </div>
            <div class="modal-body">
                <p><span class="badge badge-danger">Warning!</span> This will remove all contents (except settings) from
                    the
                    datasource!</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
                <button type="button" id="clear" class="btn btn-danger">Do it!</button>
            </div>
        </div>
    </div>
</div>

<div class="modal hide fade in" id="confirmDialogDelete">

    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Are you really sure?</h3>
            </div>
            <div class="modal-body">
                <p><span class="badge badge-danger">Warning!</span> This will remove all deleted data from the
                    datasource!
                </p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
                <button type="button" id="delete" class="btn btn-danger">Do it!</button>
            </div>
        </div>
    </div>
</div>

<div id="supportsDeleteDeleted" data-value='${supportsDeleteDeleted}'></div>
<div id="supportsClear" data-value='${supportsClear}'></div>
<div id="url_addSampledata" data-value='<c:url value="/ admin / datasource / addSampledata" />'></div>
<div id="url_deleteDeletedData" data-value='<c:url value="/ admin / datasource / deleteDeletedData" />'></div>
<div id="url_clear" data-value='<c:url value=" / admin / datasource / clear" />'></div>
<div id="url_loadPredefinedUnits" data-value='<c:url value=" / admin / datasource / loadPredefinedUnits" />'></div>
<div id="url_datasource" data-value='<c:url value="/ admin / datasource" />'></div>

<br/>
<jsp:include page="../common/footer.jsp"/>
