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
<link rel="stylesheet" href="<c:url value="/static/lib/prettify.css" />" type="text/css" />
<link rel="stylesheet" href="<c:url value="/static/lib/codemirror-3.16.css" />" type="text/css" />
<link rel="stylesheet" href="<c:url value="/static/css/codemirror.custom.css" />" type="text/css" />
<%--
    SOS.js specific styles
--%>
<link rel="stylesheet" href="<c:url value="/static/css/sosjs/dependencies/jquery-ui.min.css" />" type="text/css"/>
<link rel="stylesheet" href="<c:url value="/static/css/sosjs/SOS.Styles.css" />" type="text/css"/>
<%--
    JavaScript
 --%>
<script type="text/javascript" src="<c:url value="/static/lib/codemirror-3.16.js" />" ></script>
<script type="text/javascript" src="<c:url value="/static/lib/prettify.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/vkbeautify-0.99.00.beta.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/js/client.js" />"></script>
<%--
    SOS.js specific JavaScript
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
    <jsp:param name="leadParagraph" value="Need better text here!" />
</jsp:include>

<script type="text/javascript">
    /* redirect from "viewclient/"" to "viewclient" */
    if (window.location.pathname.slice(-1) === "/") {
        window.location.href = window.location.href.slice(0, -1);
    }
</script>
<script type="text/javascript">
      SOS.Proxy.disable();
      
      /**
       * Initializes the page
       */
      function init() {
        var options = {
          url: 'http://localhost:8080/sos/sos/kvp'
        };
        var app = new SOS.App(options);
        app.display();
      }
</script>
<style>
      body {
        font-family: Lucida Grande, Lucida Sans, Arial, sans-serif;
        margin: 3em;
      }
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
      
      /* fork me banner: */
    .github-fork-ribbon {
        /* The right and left classes determine the side we attach our banner to */
        position: absolute;
        /* Add a bit of padding to give some substance outside the "stitching" */
        padding: 2px 0;
        /* Set the base colour */
        background-color: #66C5E4;
        /* Set a gradient: transparent black at the top to almost-transparent black at the bottom */
        background-image: -webkit-gradient(linear, left top, left bottom, from(rgba(0, 0, 0, 0)),
            to(rgba(0, 0, 0, 0.15)));
        background-image: -webkit-linear-gradient(top, rgba(0, 0, 0, 0),
            rgba(0, 0, 0, 0.15));
        background-image: -moz-linear-gradient(top, rgba(0, 0, 0, 0),
            rgba(0, 0, 0, 0.15));
        background-image: -ms-linear-gradient(top, rgba(0, 0, 0, 0),
            rgba(0, 0, 0, 0.15));
        background-image: -o-linear-gradient(top, rgba(0, 0, 0, 0),
            rgba(0, 0, 0, 0.15));
        background-image: linear-gradient(to bottom, rgba(0, 0, 0, 0),
            rgba(0, 0, 0, 0.15));
        /* Add a drop shadow */
        -webkit-box-shadow: 0 2px 3px 0 rgba(0, 0, 0, 0.5);
        -moz-box-shadow: 0 2px 3px 0 rgba(0, 0, 0, 0.5);
        box-shadow: 0 2px 3px 0 rgba(0, 0, 0, 0.5);
        z-index: 9999;
        pointer-events: auto;
    }

    .github-fork-ribbon a,.github-fork-ribbon a:hover {
        /* Set the font */
        font: 700 13px "Helvetica Neue", Helvetica, Arial, sans-serif;
        color: #fff;
        /* Set the text properties */
        text-decoration: none;
        text-shadow: 0 -1px rgba(0, 0, 0, 0.5);
        text-align: center;
        /* Set the geometry. If you fiddle with these you'll also need
         to tweak the top and right values in .github-fork-ribbon. */
        width: 200px;
        line-height: 20px;
        /* Set the layout properties */
        display: inline-block;
        padding: 2px 0;
        /* Add "stitching" effect */
        border-width: 1px 0;
        border-style: dotted;
        border-color: #fff;
        border-color: rgba(255, 255, 255, 0.7);
    }

    .github-fork-ribbon-wrapper {
        width: 150px;
        height: 150px;
        position: absolute;
        overflow: hidden;
        top: 0;
        z-index: 9999;
        pointer-events: none;
    }

    .github-fork-ribbon-wrapper.right {
        right: 0;
    }

    .github-fork-ribbon-wrapper.right .github-fork-ribbon {
        top: 42px;
        right: -43px;
        -webkit-transform: rotate(45deg);
        -moz-transform: rotate(45deg);
        -ms-transform: rotate(45deg);
        -o-transform: rotate(45deg);
        transform: rotate(45deg);
    }
</style>

<div class="github-fork-ribbon-wrapper right">
    <div class="github-fork-ribbon">
        <a href="https://github.com/52North/sos-js">Fork me on GitHub</a>
    </div>
</div>

<div>
    <p>A demonstration of the full sos-js application, a Javascript client to display and analyse time series data provided via standardized OGC Sensor Observation Service instances.</p>
    <p>To learn more about the project go to the project page: <a title="sos-js project page" href="https://github.com/52North/sos-js">https://github.com/52North/sos-js</a></p>
</div>

<jsp:include page="common/footer.jsp" />
