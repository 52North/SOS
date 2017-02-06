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
<button type="button" id="reloadCapsCache" class="btn">Reload Capabilities Cache</button>

<script type="text/javascript">
	var setReloadCapsCacheButtonLoading = function(loading){
	    var $b = $("#reloadCapsCache");
	    if (loading) {
	        $b.attr("disabled", true);
	        $b.text("Loading Cache...")
	    } else {
	        $b.removeAttr("disabled");
	        $b.text("Reload Capabilities Cache");
	        //trigger event on loading completion
	        $(document).trigger("cache-loading-complete");
	    }
	};
	
	$("#reloadCapsCache").click(function() {
	    setReloadCapsCacheButtonLoading(true);
	    $.ajax({
	        url: "<c:url value="/admin/cache/reload"/>",
	        type: "POST"
	    }).done(function(e) {
	        showSuccess("Capabilties cache reload initiated.");
	        startCacheReloadCheck();
	    }).fail(function(error){
	        showError("Capabilites cache reload failed: " + error.responseText);
	        setReloadCapsCacheButtonLoading(false);
	    });
	});
	
	var cacheReloadCheckInterval;
	var cacheReloadCheckInProgress = false;
	
	var checkCacheReload = function() {
	    if (cacheReloadCheckInProgress){
	        //cache reload check already in progress 
	        return;
	    }
	
	    cacheReloadCheckInProgress = true;
	    $.ajax({
	        url: "<c:url value="/admin/cache/loading"/>",
	        type: "GET"
	    }).done(function(data) {
	        if (data.loading){
	            setReloadCapsCacheButtonLoading(true);              
	        } else {
	            stopCacheReloadCheck();
	            setReloadCapsCacheButtonLoading(false);             
	        }
	    }).fail(function(error){
	        showError("Couldn't check capabilites cache reload status: " + error.responseText);
	        stopCacheReloadCheck();
	        setReloadCapsCacheButtonLoading(false);
	    }).always(function(){
	        cacheReloadCheckInProgress = false;
	    });
	            
	};
	
	var startCacheReloadCheck = function() {
	    cacheReloadCheckInterval = setInterval(checkCacheReload, 2000);
	};
	
	var stopCacheReloadCheck = function() {
	    if (cacheReloadCheckInterval !== undefined) {
	        clearInterval(cacheReloadCheckInterval);
	        cacheReloadCheckInterval = undefined;
	    }
	};    
</script>