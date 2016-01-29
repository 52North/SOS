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
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="sos" uri="http://52north.org/communities/sensorweb/sos/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta http-equiv="Content-Language" content="en" />
		<meta name="author" content="c.autermann@52north.org" />
		<meta name="Date-Creation-yyyymmdd" content="20120306" />
		<meta name="Date-Revision-yyyymmdd" content="20120307" />
		<link href="<c:url value="/static/images/favicon.ico" />" rel="shortcut icon" type="image/x-icon" />
		<link rel="stylesheet" href="<c:url value="/static/css/52n.css" />" type="text/css" />
		<link rel="stylesheet" href="<c:url value="/static/css/52n.cssmenu.css" />" type="text/css"/>
		<link rel="stylesheet" href="<c:url value="/static/lib/bootstrap-2.3.1.min.css" />" type="text/css" />
		<link rel="stylesheet" href="<c:url value="/static/css/application.css" />" type="text/css" />
		<script type="text/javascript" src="<c:url value="/static/js/arrays.js" />"></script>
		<script type="text/javascript" src="<c:url value="/static/lib/jquery-1.8.2.min.js" />"></script>
		<script type="text/javascript" src="<c:url value="/static/lib/bootstrap-2.3.1.min.js" />"></script>
        <script type="text/javascript" src="<c:url value="/static/js/jquery.additions.js" />"></script>
		<script type="text/javascript" src="<c:url value="/static/js/application.js" />"></script>
		<title>52&deg;North Sensor Observation Service</title>

        <c:if test="${sos:hasInstaller() and not sos:configurated(pageContext.servletContext)}">
            <script type="text/javascript">
				$(function() {
					showMessage('You first have to complete the installation process! Click <a href="<c:url value="/install/index" />"><strong>here</strong></a> to start it.', "error");
				});
			</script>
		</c:if>
	</head>
	<body>
		<div id="wrap">
			<div id="main" class="clearfix">
				<div id="navigation_h">
					<div id="wopper" class="wopper">
						<div id="ja-mainnavwrap">
							<div id="ja-mainnav">
								<ul id="ja-cssmenu" class="clearfix">
									<li>
										<a id="home-menuitem" class="menu-item0" href="<c:url value="/index" />">
											<span class="menu-title">Home</span>
										</a>
                                    </li>
                                    <c:if test="${sos:hasClient()}">
                                        <li>
                                            <a id="client-menuitem" class="menu-item1" href="<c:url value="/client" />">
                                                <span class="menu-title">Client</span>
                                            </a>
                                            <ul>
                                             	<c:if test="${sos:staticExtensionExists(pageContext.servletContext, 'client/jsClient/index.html')}">
			                                        <li>
			                                            <a id="jsclient-menuitem" target="_blank" href="<c:url value="/static/client/jsClient" />">
			                                                <span class="menu-title">Sensor Web JS Client</span>
			                                            </a>
			                                        </li>
		                                    	</c:if>
                                   			 </ul>
                                        </li>
                                    </c:if>
                                        <li>
                                            <a id="documentation-menuitem" class="menu-item2" href="<c:url value="/documentation" />">
                                                <span class="menu-title">Documentation</span>
                                            </a>
                                            <ul>
                                            	<c:if test="${sos:documentExtensionExists(pageContext.servletContext, 'api-doc/index.html')}">
			                                        <li>
			                                            <a id="rest-menuitem" target="_blank" href="<c:url value="/static/doc/api-doc" />">
			                                                <span class="menu-title">Sensor Web Client REST-API</span>
			                                            </a>
			                                        </li>
			                                    </c:if>
                                             </ul>
                                        </li>
                                    <c:if test="${sos:hasAdministrator()}">
                                        <li>
                                            <a id="admin-menuitem" class="menu-item3" href="<c:url value="/admin/index" />">
                                                <span class="menu-title">Admin</span>
                                            </a>
                                            <sec:authorize access="hasRole('ROLE_ADMIN')">
                                                <script type="text/javascript">
                                                    $("#admin-menuitem").addClass("havechild");
                                                </script>
                                                <ul>
                                                    <li>
                                                        <a class="first-item havesubchild"  href="<c:url value="/admin/settings" />">
                                                            <span class="menu-title">Settings</span>
                                                        </a>
                                                        <ul>
                                                            <li>
                                                                <a  class="first-item" href="<c:url value="/admin/logging" />">
                                                                    <span class="menu-title">Logging</span>
                                                                </a>
                                                            </li>
                                                            <li>
                                                                <a href="<c:url value="/admin/operations" />">
                                                                    <span class="menu-title">Operations</span>
                                                                </a>
                                                            </li>
                                                            <li>
                                                                <a href="<c:url value="/admin/encodings" />">
                                                                    <span class="menu-title">Encodings</span>
                                                                </a>
                                                            </li>
                                                            <li>
                                                                <a href="<c:url value="/admin/bindings" />">
                                                                    <span class="menu-title">Bindings</span>
                                                                </a>
                                                            </li>
                                                            <li>
                                                                <a href="<c:url value="/admin/extensions" />">
                                                                    <span class="menu-title">Extensions</span>
                                                                </a>
                                                            </li>
                                                            <li>
                                                                <a href="<c:url value="/admin/datasource/settings" />">
                                                                    <span class="menu-title">Datasource</span>
                                                                </a>
                                                            </li>
															<li>
                                                                <a href="<c:url value="/admin/sensors" />">
                                                                    <span class="menu-title">Procedure Descriptions</span>
                                                                </a>
                                                            </li>
                                                            <li>
                                                                <a href="<c:url value="/admin/capabilities" />">
                                                                    <span class="menu-title">Capabilities Settings</span>
                                                                </a>
                                                            </li>
                                                            <li>
                                                                <a href="<c:url value="/admin/observableProperties" />">
                                                                    <span class="menu-title">Observable Properties</span>
                                                                </a>
                                                            </li>
                                                            <li>
                                                                <a href="<c:url value="/admin/i18n" />">
                                                                    <span class="menu-title">I18N Settings</span>
                                                                </a>
                                                            </li>
                                                            <c:if test="${sos:hasClass('org.n52.sos.web.admin.AdminEReportingHeaderController')}">
                                                                <li>
                                                                    <a href="<c:url value="/admin/ereporting" />">
                                                                        <span class="menu-title">eReporting Header</span>
                                                                    </a>
                                                                </li>
                                                            </c:if>
                                                        </ul>
                                                    </li>
                                                    <li>
                                                        <a href="<c:url value="/admin/datasource" />">
                                                            <span class="menu-title">Datasource Maintenance</span>
                                                        </a>
                                                    </li>
                                                    <li>
                                                        <a href="<c:url value="/admin/cache" />">
                                                            <span class="menu-title">Cache Summary</span>
                                                        </a>
                                                    </li>
                                                    <li>
                                                        <a href="<c:url value="/admin/reset" />">
                                                            <span class="menu-title">Reset</span>
                                                        </a>
                                                    </li>
                                                    <%-- include extra admin menu items if file exists (used by custom builds) --%>
                                                    <c:if test="${sos:viewExists(pageContext.servletContext, 'common/extra-admin-menu-items.jsp')}">
                                                        <jsp:include page="extra-admin-menu-items.jsp" />
                                                    </c:if>
                                                </ul>
                                            </sec:authorize>
                                        </li>
                                    </c:if>
                                    <sec:authorize access="hasRole('ROLE_ADMIN')">
										<li style="float: right;">
											<a id="logout-menuitem" class="menu-item4" href="<c:url value="/j_spring_security_logout" />">
												<span class="menu-title">Logout</span>
											</a>
										</li>
									</sec:authorize>
								</ul>
							</div>
						</div>
					</div>
				</div>
				<script type="text/javascript">
					$("#ja-cssmenu li a#${param.activeMenu}-menuitem").addClass("active");
				</script>
				<div class="container">
					<div id="content" class="span12">
                        <div id="noscript" class="alert alert-danger">
                           <strong>Warning!</strong> This page makes heavy use of JavaScript and is virtually unusable if JavaScript is disabled.
                        </div>
                        <script type="text/javascript">$("#noscript").hide();</script>
