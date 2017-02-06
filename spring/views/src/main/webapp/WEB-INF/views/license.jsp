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
<jsp:include page="common/header.jsp">
	<jsp:param name="activeMenu" value="home" />
</jsp:include>
<jsp:include page="common/logotitle.jsp">
	<jsp:param name="title" value="52&deg;North SOS" />
	<jsp:param name="leadParagraph" value="Open Source Sensor Observation Service" />
</jsp:include>
<hr/>
<table class="table table-striped table-condensed table-hover">
	<thead>
		<tr>
			<th>Library</th>
			<th>Version</th>
			<th>License</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td><a href='http://twitter.github.com/bootstrap/'>Bootstrap</a></td>
			<td>2.3.1</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://codemirror.net/'>CodeMirror</a></td>
			<td>2.34</td>
			<td><a href='http://opensource.org/licenses/mit-license.php'>MIT</a></td>
		</tr>
		<tr>
			<td><a href='https://github.com/Mottie/tablesorter'>JQuery Tablesorter Plugin</a></td>
			<td>2.7.12</td>
			<td><a href='http://www.opensource.org/licenses/mit-license.php'>MIT</a></td>
		</tr>
		<tr>
			<td><a href='http://jquery.com/'>JQuery</a></td>
			<td>2.34</td>
			<td><a href='http://jquery.org/license/'>MIT</a></td>
		</tr>
		<tr>
			<td><a href='http://blog.stevenlevithan.com/'>parseuri</a></td>
			<td>1.8.2</td>
			<td><a href='http://opensource.org/licenses/mit-license.php'>MIT</a></td>
		</tr>
		<tr>
			<td><a href='http://code.google.com/p/google-code-prettify/'>Google Code Prettify</a></td>
			<td>1.2.2</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://code.google.com/p/vkbeautify/'>vkbeautify</a></td>
			<td>0.99.00.beta</td>
			<td><a href='http://opensource.org/licenses/mit-license.php'>MIT</a></td>
		</tr>
	</tbody>
	<tbody>
		<tr>
			<td><a href='http://www.antlr.org/'>antlr:antlr</a></td>
			<td>2.7.7</td>
			<td><a href='http://www.antlr.org/license.html'>BSD License</a></td>
		</tr>
		<tr>
			<td><a href='http://aopalliance.sourceforge.net/'>aopalliance:aopalliance</a></td>
			<td>1.0</td>
			<td><a href='-'>Public Domain</a></td>
		</tr>
		<tr>
			<td><a href='http://www.mchange.com/projects/c3p0/'>c3p0:c3p0</a></td>
			<td>0.9.2.1</td>
			<td><a href='http://www.gnu.org/licenses/lgpl-2.1.html'>LGPLv2.1</a></td>
		</tr>
		<tr>
			<td><a href='http://logback.qos.ch/'>ch.qos.logback:logback-classic</a></td>
			<td>1.0.13</td>
			<td>
				<a href='http://www.eclipse.org/legal/epl-v10.html'>EPLv1</a>
				/<a href="http://www.gnu.org/licenses/lgpl-2.1.html">LGPL</a>
			</td>
		</tr>
		<tr>
			<td><a href='http://logback.qos.ch/'>ch.qos.logback:logback-core</a></td>
			<td>1.0.13</td>
			<td>
				<a href='http://www.eclipse.org/legal/epl-v10.html'>EPLv1</a>
				/<a href="http://www.gnu.org/licenses/lgpl-2.1.html">LGPL</a>
			</td>
		</tr>
		<tr>
			<td><a href='https://bitbucket.org/vdzhuvinov/java-property-utils'>com.thetransactioncompany:java-property-utils</a></td>
			<td>1.6</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://www.vividsolutions.com/jts/jtshome.htm'>com.vividsolutions:jts</a></td>
			<td>1.13</td>
			<td><a href='http://www.gnu.org/licenses/lgpl-2.1.html'>LGPL</a></td>
		</tr>
		<tr>
			<td><a href='http://commons.apache.org/proper/commons-codec//'>commons-codec:commons-codec</a></td>
			<td>1.6</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://commons.apache.org/proper/commons-io//'>commons-io:commons-io</a></td>
			<td>2.4</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://dom4j.sourceforge.net/dom4j-1.6.1/'>dom4j:dom4j</a></td>
			<td>1.6.1</td>
			<td><a href='http://dom4j.sourceforge.net/dom4j-1.6.1/license.html'>BSD Style</a></td>
		</tr>
		<tr>
			<td><a href='http://www.jcp.org/en/jsr/detail?id=154'>javax.servlet:servlet-api</a></td>
			<td>2.5</td>
			<td><a href='https://glassfish.dev.java.net/nonav/public/CDDL+GPL.html'>CDDL + GPLv2 with classpath exception</a></td>
		</tr>
		<tr>
			<td><a href='http://www.oracle.com/technetwork/java/javaee/jsp/index.html'>javax.servlet:jsp-api</a></td>
			<td>2.5</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://joda-time.sourceforge.net/'>joda-time:joda-time</a></td>
			<td>2.3</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache License, Version 2.0</a></td>
		</tr>
		<tr>
			<td><a href='http://jstl.java.net/'>jstl:jstl</a></td>
			<td>1.2</td>
			<td><a href='https://glassfish.dev.java.net/nonav/public/CDDL+GPL.html'>CDDL + GPLv2 with classpath exception</a></td>
		</tr>
		<tr>
			<td><a href='http://junit.org/'>junit:junit</a></td>
			<td>4.11</td>
			<td><a href='http://www.ibm.com/developerworks/library/os-cpl.html#ECLIPS20'>CPL</a></td>
		</tr>
		<tr>
			<td><a href='http://hc.apache.org/'>org.apache.httpcomponents:httpclient</a></td>
			<td>4.2.5</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://hc.apache.org/'>org.apache.httpcomponents:httpcore</a></td>
			<td>4.2.4</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://xmlbeans.apache.org/'>org.apache.xmlbeans:xmlbeans</a></td>
			<td>2.6.0</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://jettison.codehaus.org/'>org.codehaus.jettison:jettison</a></td>
			<td>1.3.2</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://code.google.com/p/hamcrest/'>org.hamcrest:hamcrest-core</a></td>
			<td>1.3</td>
			<td><a href='http://opensource.org/licenses/BSD-3-Clause'>New BSD</a></td>
		</tr>
		<tr>
			<td><a href='http://www.hibernatespatial.org/'>org.hibernate:hibernate-spatial</a></td>
			<td>4.3-52N</td>
			<td><a href='http://www.gnu.org/licenses/lgpl-2.1.html'>LGPLv2.1</a></td>
		</tr>
		<tr>
			<td><a href='http://www.hibernate.org/'>org.hibernate:hibernate-c3p0</a></td>
			<td>4.3.5.Final</td>
			<td><a href='http://www.gnu.org/licenses/lgpl-2.1.html'>LGPLv2.1</a></td>
		</tr>
		<tr>
			<td><a href='http://www.hibernate.org/'>org.hibernate:hibernate-core</a></td>
			<td>4.3.5.Final</td>
			<td><a href='http://www.gnu.org/licenses/lgpl-2.1.html'>LGPLv2.1</a></td>
		</tr>
		<tr>
			<td><a href='http://www.hibernate.org/'>org.hibernate.common:hibernate-commons-annotations</a></td>
			<td>4.0.4.Final</td>
			<td><a href='http://www.gnu.org/licenses/lgpl-2.1.html'>LGPLv2.1</a></td>
		</tr>
		<tr>
			<td><a href='http://www.hibernate.org/'>org.hibernate.javax.persistence:hibernate-jpa-2.1-api</a></td>
			<td>1.0.0.Final</td>
			<td><a href='http://www.eclipse.org/org/documents/edl-v10.php'>Eclipse Distribution License - v 1.0</a></td>
		</tr>
		<tr>
			<td><a href='http://www.csg.ci.i.u-tokyo.ac.jp/~chiba/javassist/'>org.javassist:javassist</a></td>
			<td>3.18.1-GA</td>
			<td>
				<a href='http://www.mozilla.org/MPL/1.1/index.txt'>MPL 1.1</a>
				/<a href="http://www.gnu.org/licenses/lgpl-2.1.html">LGPLv2.1</a>
				/<a href="http://www.apache.org/licenses/LICENSE-2.0.html">Apache</a>
			</td>
		</tr>
		<tr>
			<td><a href='http://www.jboss.org/'>org.jboss.logging:jboss-logging</a></td>
			<td>3.1.3.GA</td>
			<td><a href='http://www.gnu.org/licenses/lgpl-2.1.html'>LGPLv2.1</a></td>
		</tr>
		<tr>
			<td><a href='https://github.com/jboss/jboss-transaction-api_spec'>org.jboss.spec.javax.transaction:jboss-transaction-api_1.2_spec</a></td>
			<td>1.0.0.Final</td>
			<td><a href='https://glassfish.dev.java.net/nonav/public/CDDL+GPL.html'>CDDL + GPLv2 with classpath exception</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb:52n-xml-filter-v20</a></td>
			<td>1.1.5</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb:52n-xml-gml-v311</a></td>
			<td>1.1.5</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb:52n-xml-gml-v321</a></td>
			<td>1.1.5</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb:52n-xml-om-v100</a></td>
			<td>1.1.5</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb:52n-xml-om-v20</a></td>
			<td>1.1.5</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb:52n-xml-ows-v110</a></td>
			<td>1.1.5</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb:52n-xml-sampling-v100</a></td>
			<td>1.1.5</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb:52n-xml-sampling-v20</a></td>
			<td>1.1.5</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb:52n-xml-sensorML-v101</a></td>
			<td>1.1.5</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb:52n-xml-soap-v12</a></td>
			<td>1.1.5</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb:52n-xml-sos-v100</a></td>
			<td>1.1.5</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb:52n-xml-sos-v20</a></td>
			<td>1.1.5</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb:52n-xml-sweCommon-v101</a></td>
			<td>1.1.5</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb:52n-xml-sweCommon-v20</a></td>
			<td>1.1.5</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb:52n-xml-swes-v20</a></td>
			<td>1.1.5</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb:52n-xml-xlink-v110</a></td>
			<td>1.1.5</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-admin</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-api</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-cache</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-coding-ows-v110</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-coding-sensorML-v101</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-coding-sos-v100</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-coding-sos-v20</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-config-sqlite</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-core-v100</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-core-v20</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-hibernate-api</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-hibernate-common</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-hibernate-core</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-hibernate-dialect</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-hibernate-feature</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-profile</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-spring-admin-controller</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-spring-admin-views</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-spring-common-controller</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-spring-common-views</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-spring-installer-controller</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://52north.org/'>org.n52.sensorweb.sos:52n-sos-spring-installer-views</a></td>
			<td>4.1.0</td>
			<td><a href='http://www.gnu.org/licenses/gpl-2.0.html'>GPLv2</a></td>
		</tr>
		<tr>
			<td><a href='http://postgis.net/'>org.postgis:postgis-jdbc</a></td>
			<td>2.1.3</td>
			<td><a href='http://www.gnu.org/licenses/lgpl-2.1.html'>LGPLv2.1</a></td>
		</tr>
		<tr>
			<td><a href='http://www.slf4j.org/legacy.html#log4j-over-slf4j'>org.slf4j:log4j-over-slf4j</a></td>
			<td>1.7.2</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://www.slf4j.org/legacy.html#jclOverSLF4J'>org.slf4j:jcl-over-slf4j</a></td>
			<td>1.7.2</td>
			<td><a href='http://opensource.org/licenses/mit-license.php'>MIT</a></td>
		</tr>
		<tr>
			<td><a href='http://www.slf4j.org/legacy.html#jul-to-slf4j'>org.slf4j:jul-to-slf4j</a></td>
			<td>1.7.2</td>
			<td><a href='http://opensource.org/licenses/mit-license.php'>MIT</a></td>
		</tr>
		<tr>
			<td><a href='http://www.slf4j.org/'>org.slf4j:slf4j-api</a></td>
			<td>1.7.2</td>
			<td><a href='http://opensource.org/licenses/mit-license.php'>MIT</a></td>
		</tr>
		<tr>
			<td><a href='http://www.springsource.org/'>org.springframework:spring-aop</a></td>
			<td>3.2.6.RELEASE</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://www.springsource.org/'>org.springframework:spring-asm</a></td>
			<td>3.2.6.RELEASE</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://www.springsource.org/'>org.springframework:spring-beans</a></td>
			<td>3.2.6.RELEASE</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://www.springsource.org/'>org.springframework:spring-context</a></td>
			<td>3.2.6.RELEASE</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://www.springsource.org/'>org.springframework:spring-context-support</a></td>
			<td>3.2.6.RELEASE</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://www.springsource.org/'>org.springframework:spring-core</a></td>
			<td>3.2.6.RELEASE</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://www.springsource.org/'>org.springframework:spring-expression</a></td>
			<td>3.2.6.RELEASE</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://www.springsource.org/'>org.springframework:spring-jdbc</a></td>
			<td>3.0.7.RELEASE</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://www.springsource.org/'>org.springframework:spring-tx</a></td>
			<td>3.0.7.RELEASE</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://www.springsource.org/'>org.springframework:spring-web</a></td>
			<td>3.2.6.RELEASE</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://www.springsource.org/'>org.springframework:spring-webmvc</a></td>
			<td>3.2.6.RELEASE</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://www.springsource.org/'>org.springframework.security:spring-security-acl</a></td>
			<td>3.1.4.RELEASE</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://www.springsource.org/'>org.springframework.security:spring-security-config</a></td>
			<td>3.1.4.RELEASE</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://www.springsource.org/'>org.springframework.security:spring-security-core</a></td>
			<td>3.1.4.RELEASE</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://www.springsource.org/'>org.springframework.security:spring-security-taglibs</a></td>
			<td>3.1.4.RELEASE</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://www.springsource.org/'>org.springframework.security:spring-security-web</a></td>
			<td>3.1.4.RELEASE</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='https://bitbucket.org/xerial/sqlite-jdbc'>org.xerial:sqlite-jdbc</a></td>
			<td>3.7.2</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://jdbc.postgresql.org/index.html'>postgresql:postgresql</a></td>
			<td>9.1-901.jdbc4</td>
			<td><a href='http://jdbc.postgresql.org/license.html'>BSD License</a></td>
		</tr>
		<tr>
			<td><a href='http://stax.codehaus.org/'>stax:stax-api</a></td>
			<td>1.0.1</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://xerces.apache.org/'>xerces:xercesImpl</a></td>
			<td>2.9.1</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
		<tr>
			<td><a href='http://xerces.apache.org/xml-commons/'>xml-apis:xml-apis</a></td>
			<td>1.3.04</td>
			<td><a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache</a></td>
		</tr>
	</tbody>
</table>

<jsp:include page="common/footer.jsp" />
