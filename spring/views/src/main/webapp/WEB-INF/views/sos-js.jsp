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
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="sos" uri="http://52north.org/communities/sensorweb/sos/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta http-equiv="Content-Language" content="en" />
        <meta name="author" content="d.nuest@52north.org" />
        <link href="<c:url value="/static/images/favicon.ico" />" rel="shortcut icon" type="image/x-icon" />        
        <title>52&deg;North SOS.js demo</title>
        
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
		
		<script type="text/javascript">
		    SOS.Proxy.disable();
		    
		    function initSOSjs() {
		        var options = {
		           url: 'http://localhost:8080<c:url value="/sos/kvp" />',
		           config: {
		        	   app: {
		        		   id: 'sosViewClient'
			           }
		           }
		        };
		        var app = new SOS.App(options);
		        
		        app.setAppOptions({
		            time: {ms: 1 * 8.64e7},
		            foi: {getTemporalCoverage: false}
		          });
		        
		        app.display();
		    }
		    
		    $(document).ready(initSOSjs);
		</script>
		
		<style type="text/css">
		    .sos-app-container {
		        position: relative;
			    height: 500px;
    			width: 900px;
		    }
		    .sos-app {
		        width: 688px;
		    }
		    .sos-menu-controls-container {
		        position: absolute;
		        top: 10px;
		    }
		    .sos-info-right-container {
    			left: 700px;
			}
			.sos-plot {
			    height: 340px;
			    width: 440px;
			}
			.sos-plot-overview {
			    height: 50px;
			    margin-left: 240px;
			    margin-top: -100px;
			    width: 440px;
			}
			table.sos-table-scrollable {
			    width: 660px;
			}
		</style>

    </head>
    <body>
		<!-- JavaScript magic -->
	</body>
</html>
