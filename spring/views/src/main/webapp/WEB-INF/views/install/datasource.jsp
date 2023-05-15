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

<jsp:include page="header.jsp">
    <jsp:param name="step" value="2" />
</jsp:include>
<jsp:include page="../common/logotitle.jsp">
    <jsp:param name="title" value="Datasource configuration" />
    <jsp:param name="leadParagraph" value="" />
</jsp:include>

<script type="text/javascript" src="<c:url value="/static/js/install/datasource.js" />"></script>

<div id="url_sources" data-value='<c:url value="/install/datasource/sources" />'></div>

<form action="<c:url value="/install/datasource" />" method="POST" class="form">
    <fieldset>
        <div class="form-group">
            <div class="control-group">
                <div class="form-row">
                    <div class="col-md-2 textright">
                        <label class="col-form-label" for="datasource">Datasource</label>
                    </div>
                    <div class="col">
                        <select name="datasource" id="datasource">
                            <option disabled="true" selected="true" value="" class="nodisplay"></option>
                        </select>
                        <br />
                        <span class="text-muted"><span class="badge badge-warning">required</span> Select the datasource you want to use for the SOS.</span>
                    </div>
                </div>
            </div>
        </div>
    </fieldset>
    <fieldset id="settings"></fieldset>
    <fieldset id="actions" class="nodisplay">
        <legend>Actions</legend>
       		 <p><span class="badge badge-danger">Note!</span></p> 
        <div class="form-group" id="create">
            <div class="form-control">
                <label class="checkbox">
                    <input type="checkbox" name="create_tables" checked="checked" />
                    <strong>Create tables</strong> &mdash; This will create the necessary tables in the database.
                </label>
            </div>
        </div>
        <div class="form-group" id="overwrite">
            <div class="form-control">
                <label class="checkbox">
                    <input type="checkbox" name="overwrite_tables" />
                    <strong>Delete existing tables</strong> &mdash; This will delete all existing tables in the database.
                </label>
                <span class="nodisplay form-text"><span class="badge badge-danger">Warning!</span>
                    This will erase the entire database.</span>
            </div>
        </div>
        <div class="form-group" id="update">
            <div class="form-control">
                <label class="checkbox">
                    <input type="checkbox" name="update_tables" />
                    <strong>Force updating existing tables</strong> &mdash; This will update all existing tables in the database if needed.
                </label>
                <span class="nodisplay form-text"><span class="badge badge-danger">Warning!</span>
                     EXPERIMENTAL!!! This updates the entire database if needed. Or check /misc/db for an update script!</span>
            </div>
        </div>
    </fieldset>
    <hr/>
    <div>
        <a href="<c:url value="/install/index" />" class="btn">Back</a>
        <button id="next" type="submit" class="btn btn-info pull-right">Next</button>
    </div>
</form>

<jsp:include page="../common/footer.jsp" />
