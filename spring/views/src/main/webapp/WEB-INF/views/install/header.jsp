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
		<link rel="stylesheet" href="<c:url value="/static/css/52n.cssmenu.no-hover.css" />" type="text/css"/>
		<link rel="stylesheet" href="<c:url value="/static/lib/bootstrap-2.3.1.min.css" />" type="text/css" />
		<link rel="stylesheet" href="<c:url value="/static/css/application.css" />" type="text/css" />
		<style type="text/css">
			#ja-cssmenu li {
				cursor: default !important;
			}
		</style>
		<script type="text/javascript" src="<c:url value="/static/lib/jquery-1.8.2.min.js" />"></script>
		<script type="text/javascript" src="<c:url value="/static/lib/bootstrap-2.3.1.min.js" />"></script>
		<script type="text/javascript" src="<c:url value="/static/js/application.js" />"></script>
		<title>52&deg;North SOS installer</title>
	</head>
	<body>
		<div id="wrap">
			<div id="main" class="cleafix">
			<div id="navigation_h">
				<div id="wopper" class="wopper">
					<div id="ja-mainnavwrap">
						<div id="ja-mainnav">
							<ul id="ja-cssmenu" class="clearfix">
								<li><a><span class="menu-title">Welcome</span></a></li>
                                <li><a><span class="menu-title">Datasource Configuration</span></a></li>
                                <li><a><span class="menu-title">Optional Settings</span></a></li>
								<li><a><span class="menu-title">Finish</span></a></li>
							</ul>
						</div>
					</div>
				</div>
			</div>
			<script type="text/javascript">
				$("#ja-cssmenu li:nth-child(${param.step}) a")
					.addClass("active");
				$("#ja-cssmenu li,#ja-cssmenu a,#ja-cssmenu span").hover(function(e){
					e.preventDefault();
				});
				$(function(){
					/* put warnings on empty fields */
					$("input[type=text], input[type=password], textarea").bind("keyup input", function() {
						var $this = $(this);
						if ($this.val() === "") {
							$this.parents(".control-group").addClass("warning");
						} else {
							$this.parents(".control-group").removeClass("warning");
						}
					}).trigger("input");
				});
            </script>
			<div class="container">
				<div id="content" class="span12">
					<c:if test="${not empty error}">
						<div class="alert alert-error">
							<strong>Error!</strong> ${fn:escapeXml(error)}
						</div>
						<c:remove var="error" scope="session" />
					</c:if>
                    <div id="noscript" class="alert alert-danger">
                        <strong>Warning!</strong> This page makes heavy use of JavaScript and is virtually unusable if JavaScript is disabled.
                    </div>
                    <script type="text/javascript">$("#noscript").hide();</script>
