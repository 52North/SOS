<%--

    Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
<jsp:include page="common/header.jsp">
    <jsp:param name="activeMenu" value="viewclient" />
</jsp:include>
<%--
    STYLES
--%>
<link rel="stylesheet" href="<c:url value="/static/css/sosjs/dependencies/jquery-ui.min.css" />" type="text/css"/>
<link rel="stylesheet" href="<c:url value="/static/css/sosjs/SOS.Styles.css" />" type="text/css"/>
<%--
    JavaScript
 --%>
<script type="text/javascript" src="<c:url value="/static/js/sosjs/dependencies/proj4js-combined.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/js/sosjs/dependencies/OpenLayers.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/js/sosjs/dependencies/jquery.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/js/sosjs/dependencies/jquery-ui.min.js" />"></script>
<!--[if lte IE 8]><script type="text/javascript" src="<c:url value="/static/js/sosjs/dependencies/excanvas.min.js" />"></script><![endif]-->
<script type="text/javascript" src="<c:url value="/static/js/sosjs/dependencies/jquery.flot.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/js/sosjs/dependencies/jquery.flot.selection.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/js/sosjs/dependencies/jquery.flot.axislabels.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/js/sosjs/dependencies/jquery.flot.navigate.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/js/sosjs/SOS.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/js/sosjs/SOS.Ui.js" />"></script>
<%--
    JSP parameters
 --%>
<jsp:include page="common/logotitle.jsp">
    <jsp:param name="title" value="52&deg;North SOS.js based View Client" />
    <jsp:param name="leadParagraph" value="A demonstration of the full sos-js application, a Javascript client to display and analyse time series data provided via standardized OGC Sensor Observation Service instances." />
</jsp:include>

<script type="text/javascript">
    /* redirect from "viewclient/"" to "viewclient" */
    if (window.location.pathname.slice(-1) === "/") {
        window.location.href = window.location.href.slice(0, -1);
    }

    SOS.Proxy.disable();
    
    /**
     * Initializes the page
     */
    function initSOSjs() {
        var options = {
           url: 'http://localhost:8080<c:url value="/sos/kvp" />'
        };
        var app = new SOS.App(options);
        app.display();
    }
    $(document).ready(initSOSjs);
</script>
<style>
    .sos-app-container {
        position: relative;
    }
    .sos-app {
        width: 100%;
    }
    .sos-menu-controls-container {
        position: absolute;
        left: 3em;
        top: 220px;
    }
</style>
<div onload="initSOSjs()">
    <p>To learn more about the project go to the project page: <a title="sos-js project page" href="https://github.com/52North/sos-js">https://github.com/52North/sos-js</a></p>
</div>
<div id="sos-js"></div>
<jsp:include page="common/footer.jsp" />
