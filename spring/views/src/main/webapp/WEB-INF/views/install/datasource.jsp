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
    <jsp:param name="step" value="2" />
</jsp:include>
<jsp:include page="../common/logotitle.jsp">
    <jsp:param name="title" value="Datasource configuration" />
    <jsp:param name="leadParagraph" value="" />
</jsp:include>

<form action="<c:url value="/install/datasource" />" method="POST" class="form-horizontal">
    <fieldset>
        <div class="control-group">
            <label class="control-label" for="datasource">Datasource</label>
            <div class="controls">
                <select name="datasource" id="datasource">
                    <option disabled="true" selected="true" value="" style="display: none;"></option>
                </select>
                <span class="help-block">Select the datasource you want to use for the SOS.</span>
            </div>
        </div>
    </fieldset>
    <fieldset id="settings"></fieldset>
    <fieldset id="actions" style="display: none;">
        <legend>Actions</legend>
       		 <p><span class="label label-important">Note!</span></p> 
       		 <p>To support the requirements of the <em>INSPIRE SOS</em>, the database model was adjusted from 52N SOS version 4.1 to 4.2.</p>
       		 <p>This requires an update of existing database model but the <strong><em>Force updating existing tables</em></strong> function does not work.</p>
       		 <p>Please, update your database model manually with the appropirate update script provided in the SOS sources folder <strong>/misc/db/...</strong>.</p>
        <div class="control-group" id="create">
            <div class="controls">
                <label class="checkbox">
                    <input type="checkbox" name="create_tables" checked="checked" />
                    <strong>Create tables</strong> &mdash; This will create the necessary tables in the database.
                </label>
            </div>
        </div>
        <div class="control-group" id="overwrite">
            <div class="controls">
                <label class="checkbox">
                    <input type="checkbox" name="overwrite_tables" />
                    <strong>Delete existing tables</strong> &mdash; This will delete all existing tables in the database.
                </label>
                <span style="display: none;" class="help-block"><span class="label label-important">Warning!</span>
                    This will erase the entire database.</span>
            </div>
        </div>
        <div class="control-group" id="update">
            <div class="controls">
                <label class="checkbox">
                    <input type="checkbox" name="update_tables" />
                    <strong>Force updating existing tables</strong> &mdash; This will update all existing tables in the database if needed.
                </label>
                <span style="display: none;" class="help-block"><span class="label label-important">Warning!</span>
                     EXPERIMENTAL!!! This updates the entire database if needed. Or check /misc/db for an update script!</span>
            </div>
        </div>
    </fieldset>
    <hr/>
    <div>
        <a href="<c:url value="/install/index" />" class="btn">Back</a>
        <button id="next" type="submit" class="btn btn-info pull-right">Next</button>
    </div>
</form>


<script type="text/javascript">
    warnIfNotHttps();
    $.getJSON("<c:url value="/install/datasource/sources" />", function(datasources) {
        var datasource, selected,
            $datasource = $("#datasource"),
            $settings = $("#settings"),
            $actions = $("#actions"),
            $create = $("#create"),
            $overwrite = $("#overwrite"),
            $update = $("#update");

        for (datasource in datasources) {
            if (datasources.hasOwnProperty(datasource)) {
                $datasource.append($("<option>").text(datasource));
                if (datasources[datasource].selected) {
                    selected = datasource;
                }
            }
        }
        datasource = null;

        $datasource.change(function() {
            var d = $(this).val();
            if (d !== datasource) {
                datasource = d;

                /* create settings */
                $settings.slideUp("fast", function() {
                    $settings.children().remove();
                    generateSettings(datasources[d].settings, {}, $settings, false);
                    /* save settings as default values
                     * to keep them between switches */
                    $settings.find(":input").on("change", function() {
                        var $this = $(this), name = $this.attr("name"),
                            i, settings = datasources[d]["settings"].sections;
                        for (i = 0; i < settings.length; ++i) {
                            if (settings[i].settings.hasOwnProperty(name)) {
                                settings[i]["settings"][name]["default"] = $this.val();
                            }
                        }
                    });
                    $settings.slideDown("fast");
                });

                /* adjust actions */
                $actions.slideUp("fast", function() {
                    var schema = datasources[d].needsSchema;
                    $create.hide();
                    $overwrite.hide();
                    $update.hide();
                    if (schema) {
                        $create.show();
                        $overwrite.show();
                        $update.show();
                        $actions.slideDown("fast");}
                });
            }
        });

		$("input[name=create_tables]").change(function() {
			var $update_tables = $("input[name=update_tables]");
            if ($(this).attr("checked")) {
				if ($update_tables.attr("checked")) {
					$update_tables.attr({
						"checked": false})
						.parent().next().toggle("fast");
				}
            }
        });

        $("input[name=overwrite_tables]").click(function(){
            $(this).parent().next().toggle("fast");
        });

        $("input[name=overwrite_tables]").change(function() {
            var $create_tables = $("input[name=create_tables]");
			var $update_tables = $("input[name=update_tables]");
            if ($(this).attr("checked")) {
                $create_tables.attr({
                    "checked": "checked",
                    "disabled": true })
                .parent("label").addClass("muted");
				if ($update_tables.attr("checked")) {
					$update_tables.attr({
						"checked": false})
						.parent().next().toggle("fast");
				}
            } else {
                $create_tables.removeAttr("disabled")
                    .parent("label").removeClass("muted");
            }
        });

        $("input[name=update_tables]").click(function(){
            $(this).parent().next().toggle("fast");
        });

		$("input[name=update_tables]").change(function() {
            var $create_tables = $("input[name=create_tables]");
			var $overwrite_tables = $("input[name=overwrite_tables]");
            if ($(this).attr("checked")) {
                $create_tables.attr({
                    "checked": false}).removeAttr("disabled")
                    .parent("label").removeClass("muted");
				if ($overwrite_tables.attr("checked")) {
					$overwrite_tables.attr({
						"checked": false})
						.parent().next().toggle("fast");
				}
            } else {

            }
        });

        if (selected) {
            $datasource.val(selected).trigger("change");
        }

    });
</script>

<jsp:include page="../common/footer.jsp" />
