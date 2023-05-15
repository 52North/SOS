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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="org.n52.sos.web.admin.AbstractLoggingConfigurator.Level"%>
<jsp:include page="../common/header.jsp">
    <jsp:param name="activeMenu" value="admin" />
</jsp:include>

<jsp:include page="../common/logotitle.jsp">
    <jsp:param name="title" value="Logging Configuration" />
    <jsp:param name="leadParagraph" value="Use this site to adjust the logging configuration." />
</jsp:include>
<hr/>

<link rel="stylesheet" href="<c:url value="/static/css/logging.css" />" type="text/css" />

<div id="error" data-error="${error}"></div>

<legend id="messages-header">Latest Log Messages</legend>
<div id="messages-table">
    <table class="table table-striped table-condensed">
        <tbody>
            <c:forEach items="${logMessages}" var="logMessage">
                <tr>
                    <td><small>${fn:escapeXml(logMessage)}<small></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <p class="pull-right"><small>You can download the complete log file <a href="<c:url value="/admin/logging/file"/>" target="_blank">here</a>.</small></p>
</div>

<form method="POST" class="">
    <legend>Log Levels</legend>
    <div class="control-group" id="rootLogger">
        <label class="col-form-label" for="rootLogLevel">Root Log Level</label>
        <div class="controls">
            <select name="rootLogLevel" class="input-xlarge">
                <c:forEach var="level" items="<%= Level.values()%>">
                    <c:choose>
                        <c:when test="${rootLogLevel eq level}">
                            <option selected>${level}</option>
                        </c:when>
                        <c:otherwise>
                            <option>${level}</option>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </select>
            <span class="form-text">The log level of the <code>root</code> logger. This level is applied to all not explicitly configured loggers.</span>
        </div>
    </div>
    <div id="level-container">
        <c:forEach var="logger" items="${loggerLevels}">
            <div class="control-group">
                <label class="col-form-label" for="${logger.key}"><code>${logger.key}</code></label>
                <div class="controls">
                    <select name="${logger.key}" class="input-xlarge logger">
                        <c:forEach var="level" items="<%= Level.values()%>">
                            <c:choose>
                                <c:when test="${logger.value eq level}">
                                    <option selected>${level}</option>
                                </c:when>
                                <c:otherwise>
                                    <option>${level}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
                    <a href="#" class="remove-logger" title="Remove Logger"><i class="icon-trash"></i></a>
                </div>
            </div>
        </c:forEach>
    </div>

    <div class="pull-right">
        <a href="#add-logger" data-toggle="modal" role="button" title="Add new Logger" class="btn btn-xs"><i class="icon-plus"></i></a>
    </div>

    <legend>General Configuration</legend>
    <div class="control-group">
        <div class="controls">
            <label class="checkbox">
                <c:choose>
                    <c:when test="${isConsoleEnabled}" >
                        <input type="checkbox" name="isConsoleEnabled" checked />
                    </c:when>
                    <c:otherwise>
                        <input type="checkbox" name="isConsoleEnabled" />
                    </c:otherwise>
                </c:choose>
                Shoud messages be logged to the standard output?
            </label>
        </div>
    </div>
    <div class="control-group">
        <div class="controls">
            <label class="checkbox">
                <c:choose>
                    <c:when test="${isFileEnabled}" >
                        <input type="checkbox" name="isFileEnabled" checked />
                    </c:when>
                    <c:otherwise>
                        <input type="checkbox" name="isFileEnabled" />
                    </c:otherwise>
                </c:choose>
                Shoud messages be logged to the log file?
            </label>
        </div>
    </div>
    <div class="control-group">
        <label class="col-form-label" for="daysToKeep">Days of log files to keep</label>
        <div class="controls">
            <input type="text" class="input-xlarge" name="daysToKeep" value="${daysToKeep}" />
            <span class="form-text">How many days of log files should be kept?</span>
        </div>
    </div>
    <div class="control-group">
        <label class="col-form-label" for"maxFileSize">Max file size</label>
        <div class="controls">
            <input type="text" class="input-xlarge"  name="maxFileSize" value="${maxFileSize}" />
            <span class="form-text">What should be the maximum size of a log file?</span>
        </div>
    </div>
    <div class="form-actions">
        <p><small>It will take some time till the changes take effect as the logging configuration is read asynchronously.</small></p>
        <button type="submit" class="btn btn-info">Save</button>
    </div>
</form>

<div id="add-logger" class="modal hide fade">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3>Add new Logger</h3>
    </div>
    <div class="modal-body">
        <div class="form ">
            <div class="control-group">
                <label class="col-form-label" for="new-logger-name">Logger</label>
                <div class="controls">
                    <input type="text" id="new-logger-name" class="input-xlarge" />
                    <span class="form-text">The Logger identification (usually a package name or prefix).</span>
                </div>
            </div>
            <div class="control-group">
                <label class="col-form-label" for="new-logger-level">Level</label>
                <div class="controls">
                    <select id="new-logger-level" class="input-xlarge">
                        <c:forEach var="level" items="<%= Level.values()%>">
                            <c:choose>
                                <c:when test="${logger.value eq level}">
                                    <option selected>${level}</option>
                                </c:when>
                                <c:otherwise>
                                    <option>${level}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
                    <span class="form-text">The Level that is applied to the Logger.</span>
                </div>
            </div>
        </div>
    </div>
    <div class="modal-footer">
        <button type="button" id="add-logger-button" class="btn btn-info">Add</button>
        <a href="#" role="button" data-dismiss="modal" aria-hidden="true" class="btn">Discard</a>
    </div>
</div>
<br/>
<jsp:include page="../common/footer.jsp" />
