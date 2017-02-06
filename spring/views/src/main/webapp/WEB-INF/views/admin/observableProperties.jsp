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
<jsp:include page="../common/header.jsp">
    <jsp:param name="activeMenu" value="admin" />
</jsp:include>
<jsp:include page="../common/logotitle.jsp">
    <jsp:param name="title" value="Observable Properties" />
    <jsp:param name="leadParagraph" value="Rename Observable Properties" />
</jsp:include>
<ul class="unstyled" class="span12">
    <c:forEach items="${observableProperties}" var="op">
        <li>
            <div class="control-group">
                <div class="observableProperty input-append" style="margin-right: 84px; width:100%">
                    <input class="input-block-level" type="text" value="${op}"/>
                    <button class="btn" type="button">Change!</button>
                </div>
            </div>
        </li>
    </c:forEach>
</ul>
<script type="text/javascript">
    jQuery(document).ready(function($) {
        $(".observableProperty").each(function(_,e) {
            var $input = $(e).children("input"),
                $button = $(e).children("button").attr("disabled", true),
                $cg = $input.parents(".control-group");
            $input.data("original", $input.val());
            $input.on("change input", function () {
                var val = $input.val(),
                    orig = $input.data("original"),
                    valid = val !== null && val !== "";
                $(".observableProperty input").each(function(_,i) {
                    if ($input.get(0) !== i && $(i).data("original") === val) {
                        valid = false;
                    }
                });
                if (!valid) {
                    $button.attr("disabled", true);
                    $cg.addClass("error");
                } else {
                    $cg.removeClass("error");
                    if (val !== orig) {
                        $button.removeAttr("disabled");
                    } else {
                        $button.attr("disabled", true);
                    }
                }
            });
            $button.on("click", function() {
                var oldName = $input.data("original"),
                    newName = $input.val();
                $.ajax({
                    "url": "<c:url value="/admin/observableProperties"/>",
                    "type": "POST",
                    "data": {
                        "old": oldName,
                        "new": newName
                    }
                }).done(function(e){
                    showSuccess("Renamed <code>" + oldName + "</code> to <code>" + newName + "</code>");
                    $input.data("original", newName);
                    $button.attr("disabled", true);
                    $(".observableProperty input").trigger("change");
                }).fail(function(e){
                    showError(e.responseText);
                });
            });
        });
    });
</script>
<jsp:include page="../common/footer.jsp" />
