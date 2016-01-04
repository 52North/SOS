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
</div>
</div>
</div>
</div>
<div id="footer">
    <div id="f_top">
        <div id="f_navigation">
            <div class="fn_box">
                <h3>Communities</h3>
                <ul class="fn_list">
                    <li><a target="_blank" href="http://52north.org/communities/sensorweb/">Sensor Web</a></li>
                    <li><a target="_blank" href="http://52north.org/communities/geoprocessing/">Geoprocessing</a></li>
                    <li><a target="_blank" href="http://52north.org/communities/ilwis/">ILWIS</a></li>
                    <li><a target="_blank" href="http://52north.org/communities/earth-observation/">Earth Observation</a></li>
                    <li><a target="_blank" href="http://52north.org/communities/security/">Security &amp; Geo-RM</a></li>
                    <li><a target="_blank" href="http://52north.org/communities/semantics/">Semantics</a></li>
                    <li><a target="_blank" href="http://52north.org/communities/geostatistics/">Geostatistics</a></li>
                    <li><a target="_blank" href="http://52north.org/communities/3d-community">3D Community</a></li>
                    <li><a target="_blank" href="http://52north.org/communities/metadata-management/">Metadata Management</a></li>
                </ul>
            </div>
            <div class="fn_box">
                <h3>Get Involved</h3>
                <ul class="fn_list">
                    <li><a href="http://52north.org/about/get-involved/partnership-levels" target="_blank">Partnership Levels</a></li>
                    <li><a href="http://52north.org/about/licensing/cla-guidelines" target="_blank">License Agreement Guidelines</a></li>
                </ul>
            </div>
            <div class="fn_box">
                <h3>Affiliations</h3>
                <p>The 52&deg;North affiliations:</p>
                <a href="http://www.opengeospatial.org/" target="_blank" title="OGC Assiciate Members"><img border="0" alt="" src="<c:url value="/static/images/logos/ogc.gif" />" /></a>
            </div>
            <div class="fn_box">
                <h3>Cooperation partners</h3>
                <p>The 52&deg;North principal cooperation partners</p>
                <ul>
                	<li><a href="http://ifgi.uni-muenster.de/" target="_blank" title="Institute for Geoinformatics">Institute for Geoinformatics</a></li>
                	<li><a href="http://www.conterra.de/" target="_blank" title="con terra GmbH">con terra GmbH</a></li>
                	<li><a href="http://www.esri.com/" target="_blank" title="ESRI">ESRI</a></li>
                    <li><a href="http://www.itc.nl/" target="_blank" title="International Institute for Geo-Information Science and Earth Observation">ITC</a></li>
                </ul>
            </div>
        </div>
    </div>
    <div id="f_bottom">
        <ul>
            <li class="ja-firstitem"><a href="<c:url value="/get-involved" />">Get Involved</a></li>
            <li><a href="<c:url value="/license" />">Licenses</a></li>
            <li><a href="http://52north.org/about/contact" target="_blank">Contact</a></li>
            <li><a href="http://52north.org/about/imprint" target="_blank">Imprint</a></li>
            <li><a id="scrollToTop" href="#">Top</a></li>
        </ul>
        <script type="text/javascript">
            $("#scrollToTop").click(function() {
                $("body,html").animate({ "scrollTop": 0 }, 800);
                return false;
            });
        </script>
        <small>Tested in Firefox 17.0.1, Google Chrome 23.0.1271.95, Safari 6, Internet Explorer 10</small>
        <br/>
        <small>Copyright &copy;
            <script type="text/javascript">document.write(new Date().getFullYear());</script>
            <noscript>2012</noscript>
            52&deg;North Initiative for Geospatial Open Source Software GmbH. All Rights Reserved.
        </small>
    </div>
</div>
</body>
</html>
