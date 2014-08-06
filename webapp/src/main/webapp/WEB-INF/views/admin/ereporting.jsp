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
<jsp:include page="../common/header.jsp">
    <jsp:param name="activeMenu" value="admin" />
</jsp:include>

<jsp:include page="../common/logotitle.jsp">
    <jsp:param name="title" value="eReporting Header" />
    <jsp:param name="leadParagraph" value="" />
</jsp:include>


<div id="erh"></div>

<style type="text/css">
    .dropdown-toggle {
        height: 30px;
    }
    .dropdown-toggle .caret {
        padding-bottom: 4px;
    }
    /*
    .string > .controls > input {
        width: 380px;
    }
    .string > .controls {
        max-width: 400px;
    }

    .list .item, .string, .nillable, .controls {
        display: inline-block;
        margin: 5px;
        vertical-align: top;
    }
    */
    /*
    .nillable,
    .list,
    .controls {
        margin: 5px;
        min-height: 20px;
        padding: 9px;
        margin-bottom: 20px;
        background-color: #f5f5f5;
        border: 1px solid #e3e3e3;
        -webkit-border-radius: 3px;
         -moz-border-radius: 3px;
              border-radius: 3px;
        -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.05);
         -moz-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.05);
              box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.05);
    }*/
    .list > button,
    .list-item > .btn-group button {
        margin-bottom: 20px;
    }
    .codetype-codespace {
        margin-right: 5px;
    }
    label.control-label {
        display: block;
        width: 100%;
        padding: 0;
        margin-bottom: 20px;
        font-size: 21px;
        line-height: 40px;
        color: #333333;
        border: 0;
        border-bottom: 1px solid #e5e5e5;
    }
</style>

<script type="text/javascript">

var util = (function(){
    var util = {
        global: window,
        empty: {},
        noop: function(){}
    };

    util.toArray = function (obj, offset){
        return Array.prototype.slice.call(obj || [], offset || 0);
    };

    util.isString = function(o) {
        return typeof o === "string" || o instanceof String;
    };

    util.hitch = function(scope, method) {
        var args;
        if (!method) { method = scope; scope = null; };
        if (util.isString(method)) { method = (scope || util.global)[method]; }
        if (arguments.length > 2) {
            args = util.toArray(arguments, 2);
            return function() {
                method.apply(scope || this, args.concat(util.toArray(arguments)));
            };
        }
        return !scope ? method : function() {
            return method.apply(scope, arguments || []);
        };
    };

    var mxn = function(dest,source){
        var name;
        for (name in source){
            if(!(name in dest) || (dest[name] !== source[name]
                && (!(name in util.empty) || util.empty[name] !== source[name]))){
                dest[name] = source[name];
            }
        }
        return dest;
    };

    util.mixin = function(dest, sources){
        if(!dest){ dest = {}; }
        for (var i = 1; i < arguments.length; ++i) {
            mxn(dest, arguments[i]);
        }
        return dest;
    };

    util.defer = function(fun) {
        setTimeout(fun, 0);
    };

    return util;
})();

function NillableController(options) {

    this.$div = $(options.div).addClass("nillable well well-small");
    var $controls = $("<div>").addClass("controls").appendTo(this.$div);

    var $header = $("<label>")
        .addClass("control-label")
        .text(options.label)
        .appendTo($controls);

    this.$nilReason = $("<input>")
            .addClass("span2")
            .attr("type", "text")
            .attr("placeholder", "Reason")
            .attr("disabled", "disabled")
            .hide();
    this.$nilButton = $("<button>")
            .addClass("btn")
            .text("Nil")
            .on("click", util.hitch(this, this._onNilButtonClick));
    this.$reasonsButton = $("<button>")
        .addClass("btn dropdown-toggle")
        .data("toggle", "dropdown")
        .append($("<span>").addClass("caret"))
        .dropdown();

    var $reasons = $("<ul>").addClass("dropdown-menu");

    [
        "inapplicable",
        "missing",
        "template",
        "unknown",
        "withheld"
    ].map(util.hitch(this, function(reason) {
        $reasons.append($("<li>")
            .append($("<a>")
                .attr("href", "#")
                .text(reason))
                .on("click", util.hitch(this, function(e) {
                    this.setNil(reason);
                    e.preventDefault();
                })));
    }));

    $("<div>")
        .addClass("float-right")
        .append($("<div>")
            .addClass("input-prepend input-append")
            .append(this.$nilButton)
            .append(this.$nilReason)
            .append($("<div>")
                .addClass("btn-group")
                .append(this.$reasonsButton)
                .append($reasons)))
        .appendTo($header);

    options.value = options.value || {nil: true, reason: "missing"};
    var isNil = !!options.value.nil;

    this.$delegateDiv = $("<div>").hide().appendTo($controls);
    this.delegate = new options.delegate(util.mixin(options.delegateOptions, {
        div: this.$delegateDiv,
        value: isNil ? null : options.value
    }));
    this.$delegateDiv.removeClass("well well-small");

    if (isNil) {
        this.$nilReason.show();
        this.setNil(options.value.reason || "missing");
    } else {
        this.$delegateDiv.show();
    }
}
util.mixin(NillableController.prototype, {
    val: function() {
        return this.nil ? {
            nil: true, reason: this.$nilReason.val()
        } : this.delegate.val();
    },
    _onNilButtonClick: function(){
        this.nil = !this.nil;
        this.$nilButton.toggleClass("btn-warning");
        this.$reasonsButton.toggleClass("btn-warning");
        this.$delegateDiv.slideToggle("fast");
        this.$nilReason.toggleAttr("disabled").verticalSlideToggle("fast");
    },
    setNil: function(reason) {
        this.nil = true;
        this.$nilButton.addClass("btn-warning");
        this.$reasonsButton.addClass("btn-warning");
        this.$nilReason.removeAttr("disabled").val(reason).slideRight();

        // if an animation is used the doesn't become hidden. hide() works...
        util.defer(util.hitch(this.$delegateDiv, "slideUp", "fast"));
    }
});

function StringController(options) {
    this.$div = $(options.div)
        .addClass("string well well-small");
    var $controls = $("<div>")
        .addClass("controls")
        .appendTo(this.$div);
    if (options.label)
    $("<label>")
        .addClass("control-label")
        .text(options.label || "")
        .appendTo($controls);
    this.$input = $("<input>")
        .attr("type", "text")
        .attr("placeholder", options.placeholder || "")
        .val(options.value || "")
        .appendTo($controls);
    $("<span>")
        .addClass("help-block")
        .text(options.helpText)
        .appendTo($controls);
}
util.mixin(StringController.prototype, {
    val: function() {
        return this.$input.val();
    }
});

function CheckBoxController(options) {
    this.$div = $(options.div)
        .addClass("checkbox");
    var $controls = $("<div>")
        .addClass("controls")
        .appendTo(this.$div);
    $("<label>")
        .addClass("control-label")
        .text(options.label || "")
        .appendTo($controls);
    var $label = $("<label>")
        .addClass("checkbox")
        .text(options.description)
        .appendTo($controls);
    this.$input = $("<input>")
        .attr("type", "checkbox")
        .appendTo($label);

    if (options.value) {
        this.$input.checked();
    }

    $("<span>")
        .addClass("help-block")
        .text(options.helpText)
        .appendTo($controls);
}
util.mixin(CheckBoxController.prototype, {
    val: function() {
        return this.$input.attr("checked") === "checked";
    }
});


function InspireIdController(options) {
    this.$div = $(options.div)
        .addClass("inspire-id ");
    options.value = options.value || {};
    if (options.label)
        $("<label>")
            .addClass("control-label")
            .text(options.label || "Inpire ID")
            .appendTo(this.$div);
    var $controls = $("<div>")
        .addClass("controls")
        .appendTo(this.$div);
    if (options.helpText) {
        $("<span>")
            .addClass("help-block")
            .text(options.helpText)
            .appendTo($controls);
    }
    this.localId = new StringController({
        div: $("<div>").appendTo($controls),
        value: options.value.localId,
        placeholder: "Local ID",
        label: "Local ID",
        helpText: "A local identifier, assigned by the data provider. The local identifier is unique within the namespace, that is no other spatial object carries the same unique identifier. It is the responsibility of the data provider to guarantee uniqueness of the local identifier within the namespace."
    });
    this.namespace = new StringController({
        div: $("<div>").appendTo($controls),
        value: options.value.namespace,
        placeholder: "Namespace",
        label: "Namespace",
        helpText: "Namespace uniquely identifying the data source of the spatial object. The namespace value will be owned by the data provider of the spatial object and will be registered in the INSPIRE External Object Identifier Namespaces Register."
    });
    this.versionId = new NillableController({
        div: $("<div>").appendTo($controls),
        value: options.value.versionId,
        delegate: StringController,
        label: "Version ID",
        delegateOptions: {
            placeholder: "Version ID",
            helpText: 'The identifier of the particular version of the spatial object, with a maximum length of 25 characters. If the specification of a spatial object type with an external object identifier includes life-cycle information, the version identifier is used to distinguish between the different versions of a spatial object. Within the set of all versions of a spatial object, the version identifier is unique. The maximum length has been selected to allow for time stamps based on ISO 8601, for example, "2007-02-12T12:12:12+05:30" as the version identifier. The property is void, if the spatial data set does not distinguish between different versions of the spatial object. It is missing, if the spatial object type does not support any life-cycle information.'
        }
    });
}
util.mixin(InspireIdController.prototype, {
    val: function() {
        return {
            localId: this.localId.val(),
            namespace: this.namespace.val(),
            versionId: this.versionId.val()
        };
    }
});

function EReportingHeaderController(options) {
    this.$div = $(options.div)
        .addClass("reporting-header");
    options.value = options.value || {};
    if (options.label)
        $("<label>")
            .addClass("control-label")
            .text(options.label)
            .appendTo(this.$div);
    var $controls = $("<div>")
        .addClass("controls")
        .appendTo(this.$div);
    if (options.helpText) {
        $("<span>")
            .addClass("help-block")
            .text(options.helpText)
            .appendTo($controls);
    }
    this.inpireId = new InspireIdController({
        div: $("<div>").appendTo($controls),
        label: "Inspire ID",
        value: options.value.inspireId,
        helpText: "External object identifier of the spatial object. An external object identifier is a unique object identifier published by the responsible body, which may be used by external application to reference the spatial object. The identifier is an identifier of the spatial object, not an identifier of the real-world phenomenon."
    });
    this.change = new EReportingChangeController({
        div: $("<div>").appendTo($controls),
        value: options.value.change
    });
    this.reportingAuthority = new ReportingAuthorityController({
       div: $("<div>").appendTo($controls),
       label: "Reporting Authority",
       helpText: "Contact information for the Public Authority responsible for creating or collating the data that represents the Reporting Unit and submitting the data to relevant Authority.",
       value: options.value.reportingAuthority
    });

    /*
    this.reportingPeriod = new TimeController({
        div: $("<div>").appendTo($controls),
        label: "Reporting Period",
        helpText: "Date defining the reporting period. The reporting period may be represented as either a time period (e.g. 2011-01-01 to 2011-12-31) or as a single date representing the reporting year (e.g. 2011)."
        value: options.value.reportingPeriod
    });
    */

    this.content = new ListController({
        div: $("<div>").appendTo($controls),
        label: "Contents",
        delegate: NillableController,
        delegateOptions: {
            delegate: ReferenceController,
            label: "Content",
            delegateOptions: {
                helpText: "Content"
            }
        },
        value: options.value.content
    });
    this["delete"] = new ListController({
        div: $("<div>").appendTo($controls),
        label: "Deletes",
        delegate: NillableController,
        delegateOptions: {
            delegate: ReferenceController,
            label: "Delete",
            delegateOptions: {
                helpText: "Delete"
            }
        },
        value: options.value.content
    });
};
util.mixin(EReportingHeaderController.prototype, {
    val : function() {
        return {
            inspireId: this.inpireId.val(),
            change: this.change.val(),
            reportingAuthority: this.reportingAuthority.val(),
            "delete": this["delete"].val(),
            content: this.content.val()
        };
    }
});

function EReportingChangeController(options) {
    this.$div = $(options.div)
        .addClass("reporting-change");
    options.value = options.value || {};
    if (options.label)
        $("<label>")
            .addClass("control-label")
            .text(options.label || "Change")
            .appendTo(this.$div);
    var $controls = $("<div>")
        .addClass("controls")
        .appendTo(this.$div);
    if (options.helpText) {
        $("<span>")
            .addClass("help-block")
            .text(options.helpText)
            .appendTo($controls);
    }
    this.change = new CheckBoxController({
        div: $("<div>").appendTo($controls),
        label: "Change",
        description: "Change",
        helpText: '"true" if changes to previous submission, otherwise "false"',
        value: options.value.changed
    });
    this.description = new StringController({
        div: $("<div>").appendTo($controls),
        placeholder: "Description",
        label: "Change Description",
        helpText: "States if information has changed from that reported the previous year. If change='false', the information below can be skipped.",
        value: options.value.description
    });
}
util.mixin(EReportingChangeController.prototype,{
    val: function() {
        return {
            changed: this.change.val(),
            description: this.description.val()
        };
    }
});

function ListController(options) {
    this.$div = $(options.div)
        .addClass("list");
    if (options.label)
        $("<label>")
            .addClass("control-label")
            .text(options.label)
            .appendTo(this.$div);
    if (options.helpText) {
        $("<span>")
            .addClass("help-block")
            .text(options.helpText)
            .appendTo(this.$div);
    }
    this.options = options.delegateOptions;
    this.delegate = options.delegate;
    this.$items = $("<div>").addClass("list-items").appendTo(this.$div);
    this.children = [];
    $("<button>")
        .attr("type", "button")
        .addClass("btn")
        .on("click", util.hitch(this, this._onAdd))
        .append($("<i>").addClass("icon-plus"))
        .appendTo(this.$div);

    options.value = options.value || [];
    for (var i = 0; i < options.value.length; ++i) {
        this._add(util.mixin({}, this.options, { value: options.value[i] }));
    }
}
util.mixin(ListController.prototype, {
    val: function() {
        return this.children.map(function(c){ return c.val(); });
    },
    _add: function(options, index) {
        var $item = $("<div>").hide()
            .addClass("list-item")
            .append($("<div>")
                .addClass("btn-group")
                .append($("<button>")
                    .attr("type", "button")
                    .addClass("btn")
                    .on("click", util.hitch(this, this._onAdd))
                    .append($("<i>").addClass("icon-plus")))
                .append($("<button>")
                    .attr("type", "button")
                    .addClass("btn")
                    .on("click", util.hitch(this, this._onRemove))
                    .append($("<i>").addClass("icon-minus"))));

        var options = util.mixin({}, options, { div: $("<div>").appendTo($item) });
        var delegate = new this.delegate(options);
        index = index >= 0 ? index : this.children.length;
        this.children.splice(index, 0, delegate);
        this.$items.insertAt(index, $item);
        $item.slideDown("fast");
    },
    _remove: function(index) {
        this.children.splice(index, 1);
        this.$items.children().eq(index).slideRemove("fast");
    },
    _onAdd: function(e) {
        var $parent = $(e.delegateTarget).parent().parent();
        this._add(this.options, $parent.hasClass("list-item") ? $parent.index() : -1);
    },
    _onRemove: function(e) {
        this._remove($(e.delegateTarget).parent().parent().index());
    }
});

function ReportingAuthorityController(options) {
    this.$div = $(options.div)
        .addClass("reporting-authority");
    options.value = options.value || {};
    if (options.label)
        $("<label>")
            .addClass("control-label")
            .text(options.label || "Reporting Authority")
            .appendTo(this.$div);
    var $controls = $("<div>")
        .addClass("controls")
        .appendTo(this.$div);
    if (options.helpText) {
        $("<span>")
            .addClass("help-block")
            .text(options.helpText)
            .appendTo($controls);
    }

    this.individualName = new NillableController({
        div: $("<div>").appendTo($controls),
        delegate: StringController,
        label: "Individual Name",
        delegateOptions: {
            placeholder: "Individual Name",
            helpText: "Name of the related person."
        },
        value: options.value.individualName
    });
    this.organisationName = new NillableController({
        div: $("<div>").appendTo($controls),
        delegate: StringController,
        label: "Organsiation Name",
        delegateOptions: {
            placeholder: "Organsiation Name",
            helpText: "Name of the related organisation."
        },
        value: options.value.organisationName
    });
    this.positionName = new NillableController({
        div: $("<div>").appendTo($controls),
        delegate: StringController,
        label: "Position Name",
        delegateOptions: {
            placeholder: "Position Name",
            helpText: "Position of the party in relation to a resource, such as head of department."
        },
        value: options.value.positionName
    });
    this.roles = new ListController({
        div: $("<div>").appendTo($controls),
        label: "Roles",
        helpText: "Role(s) of the party in relation to a resource, such as owner.",
        delegate: NillableController,
        delegateOptions: {
            delegate: ReferenceController,
            label: "Role",
            delegateOptions: {
                placeholder: "Role",
                helpText: "Role of the party in relation to a resource, such as owner."
            }
        },
        value: options.value.roles
    });
    this.contact = new ContactController({
        div: $("<div>").appendTo($controls),
        label: "Contact",
        value: options.value.contact,
        helpText: "Contact information for the related party."
    });
}
util.mixin(ReportingAuthorityController.prototype, {
    val: function() {
        return {
            contact: this.contact.val(),
            individualName: this.individualName.val(),
            organisationName: this.organisationName.val(),
            positionName: this.positionName.val(),
            roles: this.roles.val()
        };
    }
});

function ReferenceController(options) {
    this.$div = $(options.div)
        .addClass("reference well well-small");
    options.value = options.value || {};
    if (options.label)
        $("<label>")
            .addClass("control-label")
            .text(options.label || "Reference")
            .appendTo(this.$div);
    var $controls = $("<div>")
        .addClass("controls")
        .appendTo(this.$div);

    this.$href = $("<input>").type("text").attr("placeholder", "URL").val(options.value.href);
    this.$title = $("<input>").type("text").attr("placeholder", "Title").val(options.value.title);


    $("<div>")
        .addClass("controls-row")
        .append($("<label>").text("Title"))
        .append(this.$title)
        .append($("<label>").text("URL"))
        .append(this.$href)
        .appendTo($controls);

    if (options.helpText) {
        $("<span>")
            .addClass("help-block")
            .text(options.helpText)
            .appendTo($controls);
    }

}
util.mixin(ReferenceController.prototype, {
    val: function() {
        var href = {href: this.$href.val()};
        if (this.title.val()) { href.title = this.$title.val(); }
        return href;
    }
});

function ContactController(options) {
    this.$div = $(options.div)
        .addClass("contact");
    options.value = options.value || {};
    if (options.label)
        $("<label>")
            .addClass("control-label")
            .text(options.label || "Contact")
            .appendTo(this.$div);
    var $controls = $("<div>")
        .addClass("controls")
        .appendTo(this.$div);
    if (options.helpText) {
        $("<span>")
            .addClass("help-block")
            .text(options.helpText)
            .appendTo($controls);
    }

    this.contactInstructions = new NillableController({
        div: $("<div>").appendTo($controls),
        delegate: StringController,
        label: "Contact Instructions",
        delegateOptions: {
            placeholder: "Contact Instructions",
            helpText: "Supplementary instructions on how or when to contact an individual or organisation."
        },
        value: options.value.contactInstructions
    });
    this.electronicMailAddress = new NillableController({
        div: $("<div>").appendTo($controls),
        delegate: StringController,
        label: "E-Mail",
        delegateOptions: {
            placeholder: "E-Mail",
            helpText: "An address of the organisation's or individual's electronic mailbox."
        },
        value: options.value.electronicMailAddress
    });
    this.hoursOfService = new NillableController({
        div: $("<div>").appendTo($controls),
        delegate: StringController,
        label: "Hours of Service",
        delegateOptions: {
            placeholder: "Hours of Service",
            helpText: "Periods of time when the organisation or individual can be contacted."
        },
        value: options.value.hoursOfService
    });

    this.telephoneVoice = new ListController({
        div: $("<div>").appendTo($controls),
        label: "Telephone Voice",
        delegate: NillableController,
        delegateOptions: {
            delegate: StringController,
            label: "Telephone Voice",
            delegateOptions: {
                placeholder: "Telephone Voice",
                helpText: "Telephone number of the organisation or individual."
            }
        },
        value: options.value.telephoneVoice
    });

    this.telephoneFacsimile = new ListController({
        div: $("<div>").appendTo($controls),
        label: "Telephone Facsimile",
        delegate: NillableController,
        delegateOptions: {
            delegate: StringController,
            label: "Telephone Facsimile",
            delegateOptions: {
                placeholder: "Telephone Facsimile",
                helpText: "Number of a facsimile machine of the organisation or individual."
            }
        },
        value: options.value.telephoneFacsimile
    });

    this.website = new NillableController({
        div: $("<div>").appendTo($controls),
        delegate: StringController,
        label: "Website",
        delegateOptions: {
            placeholder: "Website",
            helpText: "Pages provided on the World Wide Web by the organisation or individual."
        },
        value: options.value.website
    });

    this.address = new AddressController({
        div: $("<div>").appendTo($controls),
        label: "Address",
        helpText: "Representation of an address spatial object for use in external application schemas that need to include the basic, address information in a readable way. The data type includes the all necessary readable address components as well as the address locator(s), which allows the identification of the address spatial objects, e.g., country, region, municipality, address area, post code, street name and address number. It also includes an optional reference to the full address spatial object. The datatype could be used in application schemas that wish to include address information e.g. in a dataset that registers buildings or properties.",
        value: options.value.address
    });
}
util.mixin(ContactController.prototype, {
    val: function() {
        return {
            contactInstructions: this.contactInstructions.val(),
            electronicMailAddress: this.electronicMailAddress.val(),
            hoursOfService: this.hoursOfService.val(),
            telephoneVoice: this.telephoneVoice.val(),
            telephoneFacsimile: this.telephoneFacsimile.val(),
            address: this.address.val(),
            website: this.website.val()
        };
    }
});

function AddressController(options) {
    this.$div = $(options.div)
        .addClass("address");
    options.value = options.value || {};
    if (options.label)
        $("<label>")
            .addClass("control-label")
            .text(options.label || "Address")
            .appendTo(this.$div);
    var $controls = $("<div>")
        .addClass("controls")
        .appendTo(this.$div);
    if (options.helpText) {
        $("<span>")
            .addClass("help-block")
            .text(options.helpText)
            .appendTo($controls);
    }


    this.locatorDesignators = new ListController({
        div: $("<div>").appendTo($controls),
        delegate: StringController,
        label: "Locator Designators",
        delegateOptions: {
            label: "Locator Designator",
            placeholder: "Locator Designator",
            helpText: "A number or a sequence of characters which allows a user or an application to interpret, parse and format the locator within the relevant scope. A locator may include more locator designators."
        },
        value: options.value.locatorDesignators,
        helpText: "A number or a sequence of characters which allows a user or an application to interpret, parse and format the locator within the relevant scope. A locator may include more locator designators."
    });
    this.postCode = new NillableController({
        div: $("<div>").appendTo($controls),
        delegate: StringController,
        label: "Post Code",
        delegateOptions: {
            placeholder: "Post Code",
            helpText: "A code created and maintained for postal purposes to identify a subdivision of addresses and postal delivery points."
        },
        value: options.value.postCode,
        helpText: "A code created and maintained for postal purposes to identify a subdivision of addresses and postal delivery points."
    });

    this.adminUnits = new ListController({
        div: $("<div>").appendTo($controls),
        delegate: GeographicalNameController,
        label: "Administrative Units",
        delegateOptions: {
            label: "Administrative Unit",
            helpText: "The name of a unit of administration where a Member State has and/or exercises jurisdictional rights, for local, regional and national governance."
        },
        helpText: "The name or names of a unit of administration where a Member State has and/or exercises jurisdictional rights, for local, regional and national governance.",
        value: options.value.adminUnits
    });
    this.locatorNames = new ListController({
        div: $("<div>").appendTo($controls),
        delegate: GeographicalNameController,
        label: "Locator Names",
        delegateOptions: {
            label: "Locator Name",
            helpText: "Proper noun applied to the real world entity identified by the locator."
        },
        value: options.value.locatorNames,
        helpText: "Proper noun(s) applied to the real world entity identified by the locator."
    });
    this.addressAreas = new ListController({
        div: $("<div>").appendTo($controls),
        delegate: NillableController,
        label: "Address Areas",
        delegateOptions: {
            delegate: GeographicalNameController,
            label: "Address Area",
            helpText: "The name or names of a geographic area or locality that groups a number of addressable objects for addressing purposes, without being an administrative unit."
        },
        value: options.value.addressAreas,
        helpText: "The name or names of a geographic area or locality that groups a number of addressable objects for addressing purposes, without being an administrative unit."
    });
    this.postNames = new ListController({
        div: $("<div>").appendTo($controls),
        delegate: NillableController,
        label: "Post Names",
        delegateOptions: {
            delegate: GeographicalNameController,
            label: "Post Name",
            helpText: "Name created and maintained for postal purposes to identify a subdivision of addresses and postal delivery points."
        },
        value: options.value.postNames,
        helpText: "One or more names created and maintained for postal purposes to identify a subdivision of addresses and postal delivery points."

    });
    this.thoroughfares = new ListController({
        div: $("<div>").appendTo($controls),
        delegate: NillableController,
        label: "Thoroughfares",
        delegateOptions: {
            delegate: GeographicalNameController,
            label: "Thoroughfare",
            helpText: "The name of a passage or way through from one location to another like a road or a waterway."
        },
        value: options.value.thoroughfares,
        helpText: "The name or names of a passage or way through from one location to another like a road or a waterway."
    });
    this.addressFeature = new NillableController({
        div: $("<div>").appendTo($controls),
        delegate: ReferenceController,
        label: "Address Feature",
        delegateOptions: {
            placeholder: "Address Feature",
            helpText: "Address Feature"
        },
        value: options.value.addressFeature
    });
}
util.mixin(AddressController.prototype, {
    val: function() {
        return {
            addressAreas: this.addressAreas.val(),
            addressFeature: this.addressFeature.val(),
            adminUnits: this.adminUnits.val(),
            locatorDesignators: this.locatorDesignators.val(),
            locatorNames: this.locatorNames.val(),
            postCode: this.postCode.val(),
            postNames: this.postNames.val(),
            thoroughfares: this.thoroughfares.val()
        };
    }
});

function GeographicalNameController(options) {
    this.$div = $(options.div)
        .addClass("geographical-name");
    options.value = options.value || {};
    if (options.label)
        $("<label>")
            .addClass("control-label")
            .text(options.label || "Geographical Name")
            .appendTo(this.$div);
    var $controls = $("<div>")
        .addClass("controls")
        .appendTo(this.$div);
    if (options.helpText) {
        $("<span>")
            .addClass("help-block")
            .text(options.helpText)
            .appendTo($controls);
    }

    this.grammaticalGender = new NillableController({
        div: $("<div>").appendTo($controls),
        delegate: CodeTypeController,
        label: "Grammatical Gender",
        delegateOptions: {
            helpText: "Class of nouns reflected in the behaviour of associated words."
        },
        value: options.value.grammaticalGender
    });
    this.grammaticalNumber = new NillableController({
        div: $("<div>").appendTo($controls),
        delegate: CodeTypeController,
        label: "Grammatical Number",
        delegateOptions: {
            helpText: "Grammatical category of nouns that expresses count distinctions."
        },
        value: options.value.grammaticalNumber
    });
    this.language = new NillableController({
        div: $("<div>").appendTo($controls),
        delegate: StringController,
        label: "Language",
        delegateOptions: {
            helpText: 'Language of the name, given as a three letters code, in accordance with either ISO 639-3 or ISO 639-5. More precisely, this definition refers to the language used by the community that uses the name. The code "mul" for "multilingual" should not be used in general. However it can be used in rare cases like official names composed of two names in different languages. For example, "Vitoria-Gasteiz" is such a multilingual official name in Spain. Even if this attribute is "voidable" for pragmatic reasons, it is of first importance in several use cases in the multi-language context of Europe.'
        },
        value: options.value.language
    });
    this.nameStatus = new NillableController({
        div: $("<div>").appendTo($controls),
        delegate: CodeTypeController,
        label: "Name Status",
        delegateOptions: {
            helpText: 'Qualitative information enabling to discern which credit should be given to the name with respect to its standardisation and/or its topicality. The Geographical Names application schema does not explicitly make a preference between different names (e.g. official endonyms) of a specific real world entity. The necessary information for making the preference (e.g. the linguistic status of the administrative or geographic area in question), for a certain use case, must be obtained from other data or information sources. For example, the status of the language of the name may be known through queries on the geometries of named places against the geometry of administrative units recorded in a certain source with the language statuses information.'
        },
        value: options.value.nameStatus
    });
    this.nativeness = new NillableController({
        div: $("<div>").appendTo($controls),
        delegate: CodeTypeController,
        label: "Nativeness",
        delegateOptions: {
            helpText: "Information enabling to acknowledge if the name is the one that is/was used in the area where the spatial object is situated at the instant when the name is/was in use."
        },
        value: options.value.nativeness
    });
    this.pronunciation = new NillableController({
        div: $("<div>").appendTo($controls),
        delegate: PronunciationController,
        label: "Pronunciation",
        delegateOptions: {
            helpText: "Proper, correct or standard (standard within the linguistic community concerned) pronunciation of the geographical name."
        },
        value: options.value.pronunciation
    });
    this.spelling = new NillableController({
        div: $("<div>").appendTo($controls),
        delegate: SpellingController,
        label: "Spelling",
        delegateOptions: {
            helpText: "A proper way of writing the geographical name. Different spellings should only be used for names rendered in different scripts. While a particular GeographicalName should only have one spelling in a given script, providing different spellings in the same script should be done through the provision of different geographical names associated with the same named place."
        },
        value: options.value.spelling
    });
    this.sourceOfName = new NillableController({
        div: $("<div>").appendTo($controls),
        delegate: StringController,
        label: "Source of Name",
        delegateOptions: {
            helpText: "Original data source from which the geographical name is taken from and integrated in the data set providing/publishing it. For some named spatial objects it might refer again to the publishing data set if no other information is available (e.g. Gazetteer, geographical names data set)."
        },
        value: options.value.sourceOfName
    });
}
util.mixin(GeographicalNameController.prototype, {
    val: function() {
        return {
            grammaticalGender: this.grammaticalGender.val(),
            grammaticalNumber: this.grammaticalNumber.val(),
            language: this.language.val(),
            nameStatus: this.nameStatus.val(),
            nativeness: this.nativeness.val(),
            pronunciation: this.pronunciation.val(),
            sourceOfName: this.sourceOfName.val(),
            spelling: this.spelling.val()
        };
    }
});

function PronunciationController(options) {
    this.$div = $(options.div)
        .addClass("pronunciation");
    options.value = options.value || {};
    if (options.label)
        $("<label>")
            .addClass("control-label")
            .text(options.label)
            .appendTo(this.$div);
    var $controls = $("<div>")
        .addClass("controls")
        .appendTo(this.$div);
    if (options.helpText) {
        $("<span>")
            .addClass("help-block")
            .text(options.helpText)
            .appendTo($controls);
    }
    this.ipa = new StringController({
        div: $("<div>").appendTo($controls),
        label: "IPA",
        placeholder: "IPA",
        helpText: "Proper, correct or standard (standard within the linguistic community concerned) pronunciation of a name, expressed in International Phonetic Alphabet (IPA).",
        value: options.value.ipa
    });
    this.soundLink = new StringController({
        div: $("<div>").appendTo($controls),
        label: "Sound Link",
        placeholder: "Sound Link",
        helpText: "Proper, correct or standard (standard within the linguistic community concerned) pronunciation of a name, expressed by a link to any sound file.",
        value: options.value.soundLink
    });
}
util.mixin(PronunciationController.prototype, {
    val: function() {
        return {
            ipa: this.ipa.val(),
            soundLink: this.soundLink.val()
        };
    }
});

function SpellingController(options) {
    this.$div = $(options.div)
        .addClass("spelling");
    options.value = options.value || {};
    if (options.label)
        $("<label>")
            .addClass("control-label")
            .text(options.label)
            .appendTo(this.$div);
    var $controls = $("<div>")
        .addClass("controls")
        .appendTo(this.$div);
    if (options.helpText) {
        $("<span>")
            .addClass("help-block")
            .text(options.helpText)
            .appendTo($controls);
    }
    this.text = new StringController({
        div: $("<div>").appendTo($controls),
        label: "Text",
        placeholder: "Text",
        helpText: "Way the name is written.",
        value: options.value.text
    });
    this.script = new NillableController({
        div: $("<div>").appendTo($controls),
        delegate: StringController,
        label: "Script",
        delegateOptions: {
            placeholder: "Script",
            helpText: 'Set of graphic symbols (for example an alphabet) employed in writing the name, expressed using the four letters codes defined in ISO 15924, where applicable (e.g. Cyrillic, Greek, Roman/Latin scripts). The four letter codes for Latin (Roman), Cyrillic and Greek script are "Latn", "Cyrl" and "Grek", respectively. In rare cases other codes could be used (for other scripts than Latin, Greek and Cyrillic). However, this should mainly apply for historical names in historical scripts. This attribute is of first importance in the multi-scriptual context of Europe.'
        },
        value: options.value.script
    });
    this.transliterationScheme = new NillableController({
        div: $("<div>").appendTo($controls),
        delegate: StringController,
        label: "Transliteration Scheme",
        delegateOptions: {
            placeholder: "Transliteration Scheme",
            helpText: "Method used for the names conversion between different scripts. This attribute should be filled for any transliterated spellings. If the transliteration scheme used is recorded in codelists maintained by ISO or UN, those codes should be preferred."
        },
        value: options.value.transliterationScheme
    });
}
util.mixin(SpellingController.prototype, {
    val: function() {
        return {
            text: this.text.val(),
            script: this.script.val(),
            transliterationScheme: this.transliterationScheme.val()
        };
    }
});

function CodeTypeController(options) {
    this.$div = $(options.div)
        .addClass("code-type well well-small");
    options.value = options.value || {};
    options.value = util.isString(options.value) ? {value: options.value} : options.value;

    var $controls = $("<div>")
        .addClass("controls")
        .appendTo(this.$div);

    if (options.label) {
        $("<label>")
            .addClass("control-label codetype-label")
            .text(options.label)
            .appendTo($controls);
    }

    this.$codespace = $("<input>")
        .addClass("codetype-codespace")
        .attr("type", "text")
        .attr("placeholder", "Codespace")
        .val(options.value.codespace);
    this.$value = $("<input>")
        .addClass("codetype-value")
        .attr("type", "text")
        .attr("placeholder", "Value")
        .val(options.value.codespace);

    $("<div>")
        .addClass("controls controls-row")
        //.append($("<label>").text("Codespace"))
        .append(this.$codespace)
        //.append($("<label>").text("Value"))
        .append(this.$value)
        .appendTo($controls);

    if (options.helpText) {
        $("<span>")
            .addClass("help-block codetype-helptext")
            .text(options.helpText)
            .appendTo($controls);
    }
}
util.mixin(CodeTypeController.prototype, {
    val: function() {
        var codespace = this.$codespace.val();
        return codespace ? {
            codespace: codespace,
            value: this.$value.val()
        } : this.$value.val();
    }
});


var h = {
    "change": {
        "changed": true,
        "description": "Changed because... you know"
    },
    "delete": [],
    "content": [],
    "inspireId": {
        "localId": "id",
        "namespace": "namespace",
        "versionId": {
            "nil": true,
            "reason": "missing"
        }
    },
    "reportingAuthority": {
        "contact": {
            "address": {
                "addressAreas": [
                    {
                        "nil": true,
                        "reason": "withheld"
                    },
                    {
                        "grammaticalGender": {
                            "codespace": "b",
                            "value": "a"
                        },
                        "grammaticalNumber": {
                            "codespace": "d",
                            "value": "c"
                        },
                        "language": "eng",
                        "nameStatus": {
                            "nil": true,
                            "reason": "unknown"
                        },
                        "nativeness": "<asdfasdf",
                        "pronunciation": {
                            "ipa": "asdfasdf",
                            "soundLink": "http://asdfasdf"
                        },
                        "sourceOfName": {
                            "nil": true,
                            "reason": "missing"
                        },
                        "spelling": {
                            "text": "asdfasdf",
                            "script": "asdfasdf",
                            "transliterationScheme": "asdfasdfasdf"
                        }
                    }
                ],
                "addressFeature": {
                    "href": "http://asdfasdf"
                },
                "adminUnits": [
                    {
                        "grammaticalGender": {
                            "codespace": "b",
                            "value": "a"
                        },
                        "grammaticalNumber": {
                            "codespace": "d",
                            "value": "c"
                        },
                        "language": "eng",
                        "nameStatus": {
                            "nil": true,
                            "reason": "unknown"
                        },
                        "nativeness": "<asdfasdf",
                        "pronunciation": {
                            "ipa": "asdfasdf",
                            "soundLink": "http://asdfasdf"
                        },
                        "sourceOfName": {
                            "nil": true,
                            "reason": "missing"
                        },
                        "spelling": {
                            "text": "asdfasdf",
                            "script": "asdfasdf",
                            "transliterationScheme": "asdfasdfasdf"
                        }
                    }
                ],
                "locatorDesignators": [
                    "localtor"
                ],
                "locatorNames": [],
                "postCode": "12341234",
                "postNames": [
                    {
                        "nil": true,
                        "reason": "withheld"
                    },
                    {
                        "grammaticalGender": {
                            "codespace": "b",
                            "value": "a"
                        },
                        "grammaticalNumber": {
                            "codespace": "d",
                            "value": "c"
                        },
                        "language": "eng",
                        "nameStatus": {
                            "nil": true,
                            "reason": "unknown"
                        },
                        "nativeness": "<asdfasdf",
                        "pronunciation": {
                            "ipa": "asdfasdf",
                            "soundLink": "http://asdfasdf"
                        },
                        "sourceOfName": {
                            "nil": true,
                            "reason": "missing"
                        },
                        "spelling": {
                            "text": "asdfasdf",
                            "script": "asdfasdf",
                            "transliterationScheme": "asdfasdfasdf"
                        }
                    }
                ],
                "thoroughfares": [
                    {
                        "nil": true,
                        "reason": "withheld"
                    },
                    {
                        "grammaticalGender": {
                            "codespace": "b",
                            "value": "a"
                        },
                        "grammaticalNumber": {
                            "codespace": "d",
                            "value": "c"
                        },
                        "language": "eng",
                        "nameStatus": {
                            "nil": true,
                            "reason": "unknown"
                        },
                        "nativeness": "<asdfasdf",
                        "pronunciation": {
                            "ipa": "asdfasdf",
                            "soundLink": "http://asdfasdf"
                        },
                        "sourceOfName": {
                            "nil": true,
                            "reason": "missing"
                        },
                        "spelling": {
                            "text": "asdfasdf",
                            "script": "asdfasdf",
                            "transliterationScheme": "asdfasdfasdf"
                        }
                    }
                ]
            },
            "contactInstructions": {
                "nil": true,
                "reason": "missing"
            },
            "electronicMailAddress": {
                "nil": true,
                "reason": "unknown"
            },
            "hoursOfService": "asdfasdf",
            "telephoneFacsimile": [
                "1234",
                {
                    "nil": true,
                    "reason": "missing"
                }
            ],
            "telephoneVoice": [
                "asdfasdf"
            ],
            "website": {
                "nil": true,
                "reason": "unknown"
            }
        },
        "individualName": {
            "nil": true,
            "reason": "missing"
        },
        "organisationName": "Organisation",
        "positionName": "Postionti",
        "roles": [
            {
                "href": "http://hallo"
            },
            {
                "nil": true,
                "reason": "withheld"
            }
        ]
    },
    "reportingPeriod": "2014-08-04T09:24:07.957+02:00"
};


var erhc = new EReportingHeaderController({ label: "eReporting Header", div: $("#erh"), value: {} });

</script>


<jsp:include page="../common/footer.jsp" />
