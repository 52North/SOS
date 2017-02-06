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
<jsp:include page="header.jsp">
	<jsp:param name="step" value="1" />
</jsp:include>
<jsp:include page="../common/logotitle.jsp">
	<jsp:param name="title" value="52&deg;North SOS Installation Wizard" />
	<jsp:param name="leadParagraph" value="Welcome to installation wizard." />
</jsp:include>

<p>This software is licensed under the <a target="_blank" href="http://www.gnu.org/licenses/gpl-2.0.html">GNU General Public License v2</a> and by installing the 52&deg;North SOS you agree to adhere to the license's terms and conditions. This wizard will guide you through the installation, database setup and initial configuration of SOS. Click the "Start" button below to get your SOS up and running.</p>

<h3>Requirements</h3>

<p>This installer requires a running DBMS installation with an accessible data base. This version supports the following DBMS:</p>
<ul>
  <li>PostgreSQL with PostGIS</li>
  <li>Oracle / Oracle Spatial</li>
  <li>MySQL</li>
  <li>SQL Server</li>
  <li>H2/GeoDB (file / in memory)</li>
</ul>

<p>In the following sections you can find some information about the supported databases</p>
	
<p>If you like to use <strong>PostgreSQL 9.x</strong> you can find here a detailed installation guide for the different platforms
	in the <a target="_blank" href="http://wiki.postgresql.org/wiki/Detailed_installation_guides">PostgreSQL wiki</a>.
	The database also needs the <strong>PostGIS</strong> extension in <strong>version 1.5 or 2.0</strong> enabled (see
	the <a target="_blank" href="http://postgis.refractions.net/documentation/manual-2.0/postgis_installation.html">PostGIS documentation</a>
	for a description how to install and enable it for your database).
</p>
	
<p>Documentation for <strong>Oracle</strong> can be found <a target="_blank" href="http://www.oracle.com/technetwork/database/enterprise-edition/documentation/index.html">here</a>.
   How to install the 52&deg;North SOS with Oracle is described in the <a target="_blank" href="https://wiki.52north.org/bin/view/SensorWeb/SensorObservationServiceIVDocumentation#Oracle_installation">SOS wiki</a>
</p>

<p> If you like to use <strong>MySQL 5.6.x</strong> you can find here a detailed installation guide for the different platforms
	in the <a target="_blank" href="http://dev.mysql.com/doc/">MySQL Documentation page</a>.
</p>

<p> If you like to use <strong>Microsoft SQL Server 2012</strong> you can find detailed installation information on the <a target="_blank" href="http://msdn.microsoft.com/en-us/library/bb545450%28v=sql.10%29.aspx">Microsoft SQL Server Developer Network page</a>.
	How to install the 52&deg;North SOS with SQL Server is described in the <a target="_blank" href="https://wiki.52north.org/bin/view/SensorWeb/SensorObservationServiceIVDocumentation#SQL_Server_installation">SOS wiki</a>
</p>

<div id="uploadForm">
	<h3>Upload a previous configuration file</h3>
	<p>You can upload the exported configuration of a previous SOS installation:</p>
	<input type="file" id="file" style="display: none;" />
	<div class="input-append">
		<span id="fileCover" class="input-large uneditable-input disabled"></span>
		<button type="button" class="btn" onclick="$('input[id=file]').click();">Browse</button>
		<button type="button" id="upload" class="btn" disabled="disabled">Upload</button>
	</div>
</div>

<script type="text/javascript">
	if (!window.File || !window.FileReader) {
		$("#uploadForm").remove();
	} else {
		$('#file').change(function() {
			$('#fileCover').text($(this).val().replace("C:\\fakepath\\", ""));
			$('#upload').removeAttr("disabled");
		});
		$('#upload').click(function() {
			var file= $('#file').get(0).files[0];
			var reader = new FileReader();
			reader.onload = function(e) {
				var contents = e.target.result;
				try {
					contents = JSON.parse(contents);
				} catch(e) {
					showError("Couldn't load settings... No valid JSON.");
					return;
				}
				$.ajax('<c:url value="/install/load" />',{
					"type": "POST",
					"contentType": "application/json",
					"data": JSON.stringify(contents)
				}).fail(function(e) {
					showError("Couldn't load settings...");
				}).done(function() {
					showSuccess("Settings have been loaded. Please revise them in the next steps.");
				});
			};
			reader.readAsText(file);
		});		
	}
</script>

<hr />

<a href="<c:url value="/install/datasource" />" class="btn btn-info pull-right">Start</a>

<jsp:include page="../common/footer.jsp" />
