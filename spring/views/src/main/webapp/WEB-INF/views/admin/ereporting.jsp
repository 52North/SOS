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
    <jsp:param name="title" value="eReporting Header" />
    <jsp:param name="leadParagraph" value="" />
</jsp:include>

<style type="text/css">
    .dropdown-toggle {
        height: 30px;
    }
    .dropdown-toggle .caret {
        padding-bottom: 4px;
    }
    .list > button,
    .list-item > .btn-group button {
        margin-bottom: 20px;
    }
    .codetype-codespace {
        margin-right: 5px;
    }
    .to-top { margin-top: -20px; }
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
    .nav.nav-list {
        padding-left: 10px;
        padding-right: 10px;
    }

    .nav-list>li>a {
        margin-right: -10px;
        margin-left: -10px;
    }
</style>

<div id="e"></div>

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

    util.isArray = function(o) {
        return o && (o instanceof Array || typeof o === "array");
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

var menu = (function(){

    function clickHandler(target) {
        return function(e) {
            $(target).scrollTo(400);
            e.preventDefault();
        };
    }

    function CompositeMenuItem(target, label, parent) {
        this.length = 2;
        this.$header = $("<li>")
            .addClass("nav-header")
            .append($("<a>")
            .attr("href", "#")
            .text(label)
            .on("click", clickHandler(target)));
        this.$sublist = $("<ul>").addClass("nav nav-list");
        this.$list = $("<li>").append(this.$sublist);

        if (parent) {
            this.addTo(parent);
        }
    }
    CompositeMenuItem.prototype.add = function(el, idx) {
        idx = idx >= 0 ? idx : this.$sublist.children().length;
        this.$sublist.insertAt(idx, $(el));
    };
    CompositeMenuItem.prototype.addTo = function(menu, index) {
        menu.add(this.$header, index);
        menu.add(this.$list, index >= 0 ? index + 1 : -1);
    };
    CompositeMenuItem.prototype.remove = function() {
        this.$header.remove();
        this.$list.remove();
    };
    CompositeMenuItem.prototype.hide = function() {
        this.$header.hide();
        this.$list.hide();
    };
    CompositeMenuItem.prototype.show = function() {
        this.$header.show();
        this.$list.show();
    };

    function SimpleMenuItem(target, label, parent) {
        this.length = 1;
        this.$el = $("<li>")
            .append($("<a>")
            .attr("href", "#")
            .text(label)
            .on("click", clickHandler(target)));

        if (parent) {
            this.addTo(parent);
        }
    }
    SimpleMenuItem.prototype.addTo = function(menu, index) {
        menu.add(this.$el, index);
    };
    SimpleMenuItem.prototype.remove = function() {
        this.$el.remove();
    };
    SimpleMenuItem.prototype.hide = function() {
        this.$el.hide();
    };
    SimpleMenuItem.prototype.show = function() {
        this.$el.show();
    };

    function RootMenuItem(el) {
        this.length = 0;
        this.$list = $(el).addClass("nav nav-list");
    }
    RootMenuItem.prototype.remove = function() {
        this.$list.remove();
    };
    RootMenuItem.prototype.add = function(el) {
        this.$list.append($(el));
    };
    RootMenuItem.prototype.hide = function() {
        this.$list.hide();
    };
    RootMenuItem.prototype.show = function() {
        this.$list.show();
    };

    return {
        composite: function(controller, options) {
            //if (!options.label) return;
            return new CompositeMenuItem(
                controller.$div,
                options.label,
                options.menu
            );
        },
        simple: function(controller, options) {
            //if (!options.label) return;
            return new SimpleMenuItem(
                controller.$div,
                options.label,
                options.menu
            );
        },
        root: function(el) {
            return new RootMenuItem(el);
        }
    };
})();

var BaseController = function(options) {
    this.$div = $(options.div).addClass(options.class);
    if (options.label && options.showLabel !== false) {
        this.$label = $("<label>")
            .addClass("control-label")
            .text(options.label)
            .appendTo(this.$div);
        $("<div>")
            .addClass("to-top")
            .addClass("pull-right")
            .append($("<a>")
                .attr("href", "#")
                .append($("<small>)")
                    .text("top"))
                .on("click", function(e) {
                    $.scrollToTop(); e.preventDefault();
                }))
            .appendTo(this.$div);
    }
    this.$controls = $("<div>").addClass("controls").appendTo(this.$div);
};
BaseController.prototype = {
    _addHelpText: function(options) {
        if (options.helpText) {
            $("<span>")
                .addClass("help-block")
                .text(options.helpText)
                .appendTo(this.$controls);
        }
    }
};

var CompositeController = function(options) {
    BaseController.call(this, options);
    this.menu = menu.composite(this, options);
    options.value = options.value || {};
    this._addHelpText(options);
    this.delegates = {};
};
CompositeController.prototype = Object.create(BaseController.prototype);

CompositeController.prototype.val = function() {
    var ret = {}, key;
    for (key in this.delegates) {
        if (this.delegates.hasOwnProperty(key)) {
            ret[key] = this.delegates[key].val();
        }
    }
    return ret;
};

var SimpleController = function(options) {
    BaseController.call(this, options);
    this.$div.addClass("well well-small");
    this.menu = menu.simple(this, options);
};
SimpleController.prototype = Object.create(BaseController.prototype);

function TimeController(options) {
    SimpleController.call(this, util.mixin(options, {class: "time"}));
    options.value = options.value || {};

    if (util.isString(options.value)) {
        this.type = "instant";
    } else if (util.isArray(options.value)) {
        this.type = "period";
    } else if (options.value.href) {
        this.type = "reference";
    } else {
        this.type = null;
    }

    this.$periodBegin = $("<input>").type("text").attr("placeholder", "Begin Time");
    this.$periodEnd = $("<input>").type("text").attr("placeholder", "End Time");
    this.$instant = $("<input>").type("text").attr("placeholder", "Time");

    var $period = $("<div>")
        .addClass("tab-pane time-period")
        .append($("<div>")
            .addClass("controls")
            .append($("<label>").text("Begin Time"))
            .append(this.$periodBegin)
            .append($("<label>").text("End Time"))
            .append(this.$periodEnd));

    var $instant = $("<div>")
        .addClass("tab-pane time-instant")
        .append($("<div>")
            .addClass("controls")
            .append($("<label>").text("Time"))
            .append(this.$instant));

    var $reference = $("<div>");
    this.reference = new ReferenceController({
        div: $reference,
        value: this.type === "reference" ? option.value : null
    });
    $reference.addClass("tab-pane time-reference")
        .removeClass("well well-small");

    this.$controls
        .addClass("tabbable tabs-left")
        .append($("<ul>")
            .addClass("nav nav-tabs")
            .append($("<li>")
                .append($("<a>")
                    .attr("href", "#")
                    .text("Period")
                    .data("type", "period")
                    .data("target", $period)
                    .on("click", util.hitch(this, this._activateTab))))
            .append($("<li>")
                .append($("<a>")
                    .attr("href", "#")
                    .text("Instant")
                    .data("type", "instant")
                    .data("target", $instant)
                    .on("click", util.hitch(this, this._activateTab))))
            .append($("<li>")
                .append($("<a>")
                    .attr("href", "#")
                    .text("Reference")
                    .data("type", "reference")
                    .data("target", $reference)
                    .on("click", util.hitch(this, this._activateTab)))))
        .append($("<div>")
            .addClass("tab-content")
            .append($period)
            .append($instant)
            .append($reference));
}

TimeController.prototype = Object.create(SimpleController.prototype);
TimeController.prototype._activateTab = function(e) {
    e.preventDefault();
    var $this = $(e.delegateTarget);
    var $target = $this.data("target");
    this.type = $this.data("type");
    var $li = $this.parent("li");
    var $ul = $li.parent("ul");
    if ($li.hasClass("active")) return;
    $ul.find(".active").removeClass("active");
    $ul.next(".tab-content").find(".active").removeClass("active");
    $li.addClass("active");
    $target.addClass("active");
};
TimeController.prototype.val = function(e) {
    switch(this.type) {
        case "instant":
            return this.$instant.val();
        case "period":
            return [
                this.$periodBegin.val(),
                this.$periodEnd.val()
            ];
        case "reference":
            return this.reference.val();
        default:
            return null;
    }
};

function NillableController(options) {
    options.delegateOptions = options.delegateOptions || {};
    options.label = options.label || options.delegateOptions.label;
    options.delegateOptions.label = options.delegateOptions.label || options.label;

    BaseController.call(this, util.mixin(options, {class: "nillable"}));

    this.$div.addClass("well well-small");


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
        .appendTo(this.$label);

    options.value = options.value || {nil: true, reason: "missing"};


    function CustomMenu() {
        var parent = arguments[0];
        this.menus = util.toArray(arguments);
        this.menus.shift();

        this.hide = function() {
            for (var i = 0; i < this.menus.length; ++i) {
                this.menus[i].hide();
            }
        };
        this.remove = function() {
            for (var i = 0; i < this.menus.length; ++i) {
                this.menus[i].remove();
            }

        };
        this.show = function() {
            for (var i = 0; i < this.menus.length; ++i) {
                this.menus[i].show();
            }
        };
        this.addTo = function(menu, index) {
            if (index >= 0) {
                for (var i = 0; i < this.menus.length; ++i) {
                    this.menus[i].addTo(menu, index);
                    index += this.menus[i].length;
                }
            } else {
                for (var i = 0; i < this.menus.length; ++i) {
                    this.menus[i].addTo(menu);
                }
            }
        };

        this.length = 0;
        for (var i = 0; i < this.menus.length; ++i) {
            this.length += this.menus[i].length;
        }
        if (parent) {
            this.addTo(parent);
        }
    }
    this.isNil = !!options.value.nil;

    this.$delegateDiv = $("<div>").hide().appendTo(this.$controls);
    this.delegate = new options.delegate(util.mixin(options.delegateOptions, {
        div: this.$delegateDiv,
        value: this.isNil ? null : options.value,
        showLabel: false
    }));
    this.$delegateDiv.removeClass("well well-small");

    this.nilmenu = menu.simple(this, options);
    this.nilmenu.$el.find("a").addClass("muted");
    this.menu = new CustomMenu(options.menu, this.nilmenu, this.delegate.menu);
    this._setMenu();

    if (this.isNil) {
        this.$nilReason.show();
        this.setNil(options.value.reason || "missing");
    } else {
        this.$delegateDiv.show();
    }
}

NillableController.prototype = Object.create(BaseController.prototype);
NillableController.prototype.val =  function() {
    return this.nil ? {
        nil: true, reason: this.$nilReason.val()
    } : this.delegate.val();
};
NillableController.prototype._onNilButtonClick = function(){
    this.nil = !this.nil;
    this.$nilButton.toggleClass("btn-warning");
    this.$reasonsButton.toggleClass("btn-warning");
    this.$delegateDiv.slideToggle("fast");
    this.$nilReason.toggleAttr("disabled").verticalSlideToggle("fast");
    this._setMenu();
};
NillableController.prototype._setMenu = function() {
    if (this.nil) {
        this.delegate.menu.hide();
        this.nilmenu.show();
    } else {
        this.nilmenu.hide();
        this.delegate.menu.show();
    }
};
NillableController.prototype.setNil = function(reason) {
    this.nil = true;
    this.$nilButton.addClass("btn-warning");
    this.$reasonsButton.addClass("btn-warning");
    this.$nilReason.removeAttr("disabled").val(reason).slideRight();
    this._setMenu();
    // if an animation is used the doesn't become hidden. hide() works...
    util.defer(util.hitch(this.$delegateDiv, "slideUp", "fast"));
};

function ListController(options) {
    BaseController.call(this, util.mixin(options, {class: "list"}));
    this._addHelpText(options);

    this.options = options.delegateOptions;
    this.menu = menu.composite(this, options);
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
ListController.prototype = Object.create(BaseController.prototype);
ListController.prototype.val = function() {
    return this.children.map(function(c){ return c.val(); });
};
ListController.prototype._add = function(options, index) {
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

    var options = util.mixin({}, options, {div: $("<div>").appendTo($item)});
    var delegate = new this.delegate(options);
    index = index >= 0 ? index : this.children.length;
    var menuIndex = 0;
    for (var i = 0; i < index; ++i) {
        if (this.children[i] && this.children[i].menu) {
            menuIndex += this.children[i].menu.length;
        }
    }
    delegate.menu.addTo(this.menu, menuIndex);
    this.children.splice(index, 0, delegate);
    this.$items.insertAt(index, $item);
    $item.slideDown("fast");
};
ListController.prototype._remove = function(index) {
    var child = this.children.splice(index, 1)[0];
    child && child.menu && child.menu.remove();
    this.$items.children().eq(index).slideRemove("fast");
};
ListController.prototype._onAdd = function(e) {
    var $parent = $(e.delegateTarget).parent().parent();
    this._add(this.options, $parent.hasClass("list-item") ? $parent.index() : -1);
};
ListController.prototype._onRemove = function(e) {
    this._remove($(e.delegateTarget).parent().parent().index());
};

function StringController(options) {
    SimpleController.call(this, util.mixin(options, {class:"string"}));
    this.$input = $("<input>")
        .attr("type", "text")
        .attr("placeholder", options.placeholder)
        .val(options.value)
        .appendTo(this.$controls);
    this._addHelpText(options);
}
StringController.prototype = Object.create(SimpleController.prototype);
StringController.prototype.val = function() {
    return this.$input.val();
};

function CheckBoxController(options) {
    SimpleController.call(this, util.mixin(options, {class: "checkbox"}));
    var $label = $("<label>")
        .addClass("checkbox")
        .text(options.description)
        .appendTo(this.$controls);
    this.$input = $("<input>")
        .attr("type", "checkbox")
        .appendTo($label);

    if (options.value) {
        this.$input.checked();
    }
    this._addHelpText(options);
}
CheckBoxController.prototype = Object.create(SimpleController.prototype);
CheckBoxController.prototype.val = function() {
    return this.$input.attr("checked") === "checked";
};

function ReferenceController(options) {
    SimpleController.call(this, util.mixin(options, {class: "reference"}));
    options.value = options.value || {};
    this.$href = $("<input>").type("text").attr("placeholder", "URL").val(options.value.href);
    this.$title = $("<input>").type("text").attr("placeholder", "Title").val(options.value.title);
    $("<div>")
        .addClass("controls-row")
        .append($("<label>").text("Title"))
        .append(this.$title)
        .append($("<label>").text("URL"))
        .append(this.$href)
        .appendTo(this.$controls);

    this._addHelpText(options);
}
ReferenceController.prototype = Object.create(SimpleController.prototype);
ReferenceController.prototype.val = function() {
    var href = {href: this.$href.val()};
    var title = this.$title.val();
    if (title) {href.title = title;}
    return href;
};

function CodeTypeController(options) {
    SimpleController.call(this, util.mixin(options, {class: "code-type"}));
    options.value = options.value || {};
    options.value = util.isString(options.value) ? {value: options.value} : options.value;
    this.$codespace = $("<input>")
        .addClass("codetype-codespace")
        .attr("type", "text")
        .attr("placeholder", "Codespace")
        .val(options.value.codespace);
    this.$value = $("<input>")
        .addClass("codetype-value")
        .attr("type", "text")
        .attr("placeholder", "Value")
        .val(options.value.value);

    $("<div>")
        .addClass("controls-row")
        .append(this.$codespace)
        .append(this.$value)
        .appendTo(this.$controls);

    this._addHelpText(options);
}
CodeTypeController.prototype = Object.create(SimpleController.prototype);
CodeTypeController.prototype.val = function() {
    var codespace = this.$codespace.val();
    return codespace ? {
        codespace: codespace,
        value: this.$value.val()
    } : this.$value.val();
};

function InspireIdController(options) {
    CompositeController.call(this, util.mixin(options, {class: "inpire-id"}));
    this.delegates = {
        localId: new StringController({
            div: $("<div>").appendTo(this.$controls),
            value: options.value.localId,
            menu: this.menu,
            placeholder: "Local ID",
            label: "Local ID",
            helpText: "A local identifier, assigned by the data provider. The local identifier is unique within the namespace, that is no other spatial object carries the same unique identifier. It is the responsibility of the data provider to guarantee uniqueness of the local identifier within the namespace."
        }),
        namespace: new StringController({
            div: $("<div>").appendTo(this.$controls),
            value: options.value.namespace,
            menu: this.menu,
            placeholder: "Namespace",
            label: "Namespace",
            helpText: "Namespace uniquely identifying the data source of the spatial object. The namespace value will be owned by the data provider of the spatial object and will be registered in the INSPIRE External Object Identifier Namespaces Register."
        }),
        versionId: new NillableController({
            div: $("<div>").appendTo(this.$controls),
            value: options.value.versionId,
            menu: this.menu,
            delegate: StringController,
            delegateOptions: {
                label: "Version ID",
                placeholder: "Version ID",
                helpText: 'The identifier of the particular version of the spatial object, with a maximum length of 25 characters. If the specification of a spatial object type with an external object identifier includes life-cycle information, the version identifier is used to distinguish between the different versions of a spatial object. Within the set of all versions of a spatial object, the version identifier is unique. The maximum length has been selected to allow for time stamps based on ISO 8601, for example, "2007-02-12T12:12:12+05:30" as the version identifier. The property is void, if the spatial data set does not distinguish between different versions of the spatial object. It is missing, if the spatial object type does not support any life-cycle information.'
            }
        })
    };
}
InspireIdController.prototype = Object.create(CompositeController.prototype);

function EReportingChangeController(options) {
    CompositeController.call(this, util.mixin(options, {class: "reporting-change"}));
    this.delegates = {
        changed: new CheckBoxController({
            div: $("<div>").appendTo(this.$controls),
            label: "Change",
            menu: this.menu,
            description: "Change",
            helpText: '"true" if changes to previous submission, otherwise "false"',
            value: options.value.changed
        }),
        description: new StringController({
            div: $("<div>").appendTo(this.$controls),
            placeholder: "Description",
            menu: this.menu,
            label: "Change Description",
            helpText: "States if information has changed from that reported the previous year. If change='false', the information below can be skipped.",
            value: options.value.description
        })
    };
}
EReportingChangeController.prototype = Object.create(CompositeController.prototype);

function ReportingAuthorityController(options) {
    CompositeController.call(this, util.mixin(options, {class: "reporting-authority"}));
    this.delegates = {
        individualName: new NillableController({
            div: $("<div>").appendTo(this.$controls),
            delegate: StringController,
            menu: this.menu,
            delegateOptions: {
                label: "Individual Name",
                placeholder: "Individual Name",
                helpText: "Name of the related person."
            },
            value: options.value.individualName
        }),
        organisationName: new NillableController({
            div: $("<div>").appendTo(this.$controls),
            delegate: StringController,
            menu: this.menu,
            delegateOptions: {
                label: "Organsiation Name",
                placeholder: "Organsiation Name",
                helpText: "Name of the related organisation."
            },
            value: options.value.organisationName
        }),
        positionName: new NillableController({
            div: $("<div>").appendTo(this.$controls),
            delegate: StringController,
            menu: this.menu,
            delegateOptions: {
                label: "Position Name",
                placeholder: "Position Name",
                helpText: "Position of the party in relation to a resource, such as head of department."
            },
            value: options.value.positionName
        }),
        roles: new ListController({
            div: $("<div>").appendTo(this.$controls),
            label: "Roles",
            helpText: "Role(s) of the party in relation to a resource, such as owner.",
            delegate: NillableController,
            menu: this.menu,
            delegateOptions: {
                delegate: ReferenceController,
                delegateOptions: {
                    label: "Role",
                    placeholder: "Role",
                    helpText: "Role of the party in relation to a resource, such as owner."
                }
            },
            value: options.value.roles
        }),
        contact: new ContactController({
            div: $("<div>").appendTo(this.$controls),
            label: "Contact",
            menu: this.menu,
            value: options.value.contact,
            helpText: "Contact information for the related party."
        })
    };
}
ReportingAuthorityController.prototype = Object.create(CompositeController.prototype);

function ContactController(options) {
    CompositeController.call(this, util.mixin(options, {class: "contact"}));
    this.delegates = {
        contactInstructions: new NillableController({
            div: $("<div>").appendTo(this.$controls),
            delegate: StringController,
            menu: this.menu,
            delegateOptions: {
                label: "Contact Instructions",
                placeholder: "Contact Instructions",
                helpText: "Supplementary instructions on how or when to contact an individual or organisation."
            },
            value: options.value.contactInstructions
        }),
        electronicMailAddress: new NillableController({
            div: $("<div>").appendTo(this.$controls),
            delegate: StringController,
            menu: this.menu,
            label: "E-Mail",
            delegateOptions: {
                label: "E-Mail",
                placeholder: "E-Mail",
                helpText: "An address of the organisation's or individual's electronic mailbox."
            },
            value: options.value.electronicMailAddress
        }),
        hoursOfService: new NillableController({
            div: $("<div>").appendTo(this.$controls),
            delegate: StringController,
            menu: this.menu,
            delegateOptions: {
                label: "Hours of Service",
                placeholder: "Hours of Service",
                helpText: "Periods of time when the organisation or individual can be contacted."
            },
            value: options.value.hoursOfService
        }),
        telephoneVoice: new ListController({
            div: $("<div>").appendTo(this.$controls),
            label: "Phone Numbers",
            delegate: NillableController,
            menu: this.menu,
            delegateOptions: {
                delegate: StringController,
                delegateOptions: {
                    label: "Phone Number",
                    placeholder: "Phone Number",
                    helpText: "Telephone number of the organisation or individual."
                }
            },
            value: options.value.telephoneVoice
        }),
        telephoneFacsimile: new ListController({
            div: $("<div>").appendTo(this.$controls),
            label: "Fax Numbers",
            delegate: NillableController,
            menu: this.menu,
            delegateOptions: {
                delegate: StringController,
                delegateOptions: {
                    label: "Fax Number",
                    placeholder: "Fax Number",
                    helpText: "Number of a facsimile machine of the organisation or individual."
                }
            },
            value: options.value.telephoneFacsimile
        }),
        website: new NillableController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            delegate: StringController,
            delegateOptions: {
                label: "Website",
                placeholder: "Website",
                helpText: "Pages provided on the World Wide Web by the organisation or individual."
            },
            value: options.value.website
        }),
        address: new AddressController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            label: "Address",
            helpText: "Representation of an address spatial object for use in external application schemas that need to include the basic, address information in a readable way. The data type includes the all necessary readable address components as well as the address locator(s), which allows the identification of the address spatial objects, e.g., country, region, municipality, address area, post code, street name and address number. It also includes an optional reference to the full address spatial object. The datatype could be used in application schemas that wish to include address information e.g. in a dataset that registers buildings or properties.",
            value: options.value.address
        })
    };
}
ContactController.prototype = Object.create(CompositeController.prototype);

function AddressController(options) {
    CompositeController.call(this, util.mixin(options, {class: "address"}));
    this.delegates = {
        locatorDesignators: new ListController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            delegate: StringController,
            label: "Locator Designators",
            delegateOptions: {
                label: "Locator Designator",
                placeholder: "Locator Designator",
                helpText: "A number or a sequence of characters which allows a user or an application to interpret, parse and format the locator within the relevant scope. A locator may include more locator designators."
            },
            value: options.value.locatorDesignators,
            helpText: "A number or a sequence of characters which allows a user or an application to interpret, parse and format the locator within the relevant scope. A locator may include more locator designators."
        }),
        postCode: new NillableController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            delegate: StringController,
            delegateOptions: {
                label: "Post Code",
                placeholder: "Post Code",
                helpText: "A code created and maintained for postal purposes to identify a subdivision of addresses and postal delivery points."
            },
            value: options.value.postCode,
            helpText: "A code created and maintained for postal purposes to identify a subdivision of addresses and postal delivery points."
        }),
        adminUnits: new ListController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            delegate: GeographicalNameController,
            label: "Administrative Units",
            delegateOptions: {
                label: "Administrative Unit",
                helpText: "The name of a unit of administration where a Member State has and/or exercises jurisdictional rights, for local, regional and national governance."
            },
            helpText: "The name or names of a unit of administration where a Member State has and/or exercises jurisdictional rights, for local, regional and national governance.",
            value: options.value.adminUnits
        }),
        locatorNames: new ListController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            delegate: GeographicalNameController,
            label: "Locator Names",
            delegateOptions: {
                label: "Locator Name",
                helpText: "Proper noun applied to the real world entity identified by the locator."
            },
            value: options.value.locatorNames,
            helpText: "Proper noun(s) applied to the real world entity identified by the locator."
        }),
        addressAreas: new ListController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            delegate: NillableController,
            label: "Address Areas",
            delegateOptions: {
                delegate: GeographicalNameController,
                label: "Address Area",
                delegateOptions: {
                    label: "Address Area",
                    helpText: "The name or names of a geographic area or locality that groups a number of addressable objects for addressing purposes, without being an administrative unit."
                }
            },
            value: options.value.addressAreas,
            helpText: "The name or names of a geographic area or locality that groups a number of addressable objects for addressing purposes, without being an administrative unit."
        }),
        postNames: new ListController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            delegate: NillableController,
            label: "Post Names",
            delegateOptions: {
                delegate: GeographicalNameController,
                label: "Post Name",
                delegateOptions: {
                    label: "Post Name",
                    helpText: "Name created and maintained for postal purposes to identify a subdivision of addresses and postal delivery points."
                }
            },
            value: options.value.postNames,
            helpText: "One or more names created and maintained for postal purposes to identify a subdivision of addresses and postal delivery points."
        }),
        thoroughfares: new ListController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            delegate: NillableController,
            label: "Thoroughfares",
            delegateOptions: {
                delegate: GeographicalNameController,
                label: "Thoroughfare",
                delegateOptions: {
                    label: "Thoroughfare",
                    helpText: "The name of a passage or way through from one location to another like a road or a waterway."
                }
            },
            value: options.value.thoroughfares,
            helpText: "The name or names of a passage or way through from one location to another like a road or a waterway."
        }),
        addressFeature: new NillableController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            delegate: ReferenceController,
            delegateOptions: {
                label: "Address Feature",
                placeholder: "Address Feature",
                helpText: "Address Feature"
            },
            value: options.value.addressFeature
        })
    };
}
AddressController.prototype = Object.create(CompositeController.prototype);

function GeographicalNameController(options) {
    CompositeController.call(this, util.mixin(options, {class: "geographical-name"}));
    this.delegates = {
        grammaticalGender: new NillableController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            delegate: CodeTypeController,
            delegateOptions: {
                label: "Grammatical Gender",
                helpText: "Class of nouns reflected in the behaviour of associated words."
            },
            value: options.value.grammaticalGender
        }),
        grammaticalNumber: new NillableController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            delegate: CodeTypeController,
            delegateOptions: {
                label: "Grammatical Number",
                helpText: "Grammatical category of nouns that expresses count distinctions."
            },
            value: options.value.grammaticalNumber
        }),
        language: new NillableController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            delegate: StringController,
            delegateOptions: {
                label: "Language",
                helpText: 'Language of the name, given as a three letters code, in accordance with either ISO 639-3 or ISO 639-5. More precisely, this definition refers to the language used by the community that uses the name. The code "mul" for "multilingual" should not be used in general. However it can be used in rare cases like official names composed of two names in different languages. For example, "Vitoria-Gasteiz" is such a multilingual official name in Spain. Even if this attribute is "voidable" for pragmatic reasons, it is of first importance in several use cases in the multi-language context of Europe.'
            },
            value: options.value.language
        }),
        nameStatus: new NillableController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            delegate: CodeTypeController,
            delegateOptions: {
                label: "Name Status",
                helpText: 'Qualitative information enabling to discern which credit should be given to the name with respect to its standardisation and/or its topicality. The Geographical Names application schema does not explicitly make a preference between different names (e.g. official endonyms) of a specific real world entity. The necessary information for making the preference (e.g. the linguistic status of the administrative or geographic area in question), for a certain use case, must be obtained from other data or information sources. For example, the status of the language of the name may be known through queries on the geometries of named places against the geometry of administrative units recorded in a certain source with the language statuses information.'
            },
            value: options.value.nameStatus
        }),
        nativeness: new NillableController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            delegate: CodeTypeController,
            delegateOptions: {
                label: "Nativeness",
                helpText: "Information enabling to acknowledge if the name is the one that is/was used in the area where the spatial object is situated at the instant when the name is/was in use."
            },
            value: options.value.nativeness
        }),
        pronunciation: new NillableController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            delegate: PronunciationController,
            delegateOptions: {
                label: "Pronunciation",
                helpText: "Proper, correct or standard (standard within the linguistic community concerned) pronunciation of the geographical name."
            },
            value: options.value.pronunciation
        }),
        spelling: new ListController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            delegate: SpellingController,
            delegateOptions: {
                label: "Spelling",
                helpText: "A proper way of writing the geographical name. Different spellings should only be used for names rendered in different scripts. While a particular GeographicalName should only have one spelling in a given script, providing different spellings in the same script should be done through the provision of different geographical names associated with the same named place."
            },
            value: options.value.spelling
        }),
        sourceOfName: new NillableController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            delegate: StringController,
            delegateOptions: {
                label: "Source of Name",
                helpText: "Original data source from which the geographical name is taken from and integrated in the data set providing/publishing it. For some named spatial objects it might refer again to the publishing data set if no other information is available (e.g. Gazetteer, geographical names data set)."
            },
            value: options.value.sourceOfName
        })
    };
}
GeographicalNameController.prototype = Object.create(CompositeController.prototype);

function PronunciationController(options) {
    CompositeController.call(this, util.mixin(options, {class: "pronunciation"}));
    this.delegates = {
        ipa: new StringController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            label: "IPA",
            placeholder: "IPA",
            helpText: "Proper, correct or standard (standard within the linguistic community concerned) pronunciation of a name, expressed in International Phonetic Alphabet (IPA).",
            value: options.value.ipa
        }),
        soundLink: new StringController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            label: "Sound Link",
            placeholder: "Sound Link",
            helpText: "Proper, correct or standard (standard within the linguistic community concerned) pronunciation of a name, expressed by a link to any sound file.",
            value: options.value.soundLink
        })
    };
}
PronunciationController.prototype = Object.create(CompositeController.prototype);

function SpellingController(options) {
    CompositeController.call(this, util.mixin(options, {class: "spelling"}));
    this.delegates = {
        text: new StringController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            label: "Text",
            placeholder: "Text",
            helpText: "Way the name is written.",
            value: options.value.text
        }),
        script: new NillableController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            delegate: StringController,
            delegateOptions: {
                label: "Script",
                placeholder: "Script",
                helpText: 'Set of graphic symbols (for example an alphabet) employed in writing the name, expressed using the four letters codes defined in ISO 15924, where applicable (e.g. Cyrillic, Greek, Roman/Latin scripts). The four letter codes for Latin (Roman), Cyrillic and Greek script are "Latn", "Cyrl" and "Grek", respectively. In rare cases other codes could be used (for other scripts than Latin, Greek and Cyrillic). However, this should mainly apply for historical names in historical scripts. This attribute is of first importance in the multi-scriptual context of Europe.'
            },
            value: options.value.script
        }),
        transliterationScheme: new NillableController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            delegate: StringController,
            delegateOptions: {
                label: "Transliteration Scheme",
                placeholder: "Transliteration Scheme",
                helpText: "Method used for the names conversion between different scripts. This attribute should be filled for any transliterated spellings. If the transliteration scheme used is recorded in codelists maintained by ISO or UN, those codes should be preferred."
            },
            value: options.value.transliterationScheme
        })
    };
}
SpellingController.prototype = Object.create(CompositeController.prototype);


function ReportObligationController(options) {
    CompositeController.call(this, util.mixin(options, {class: "report-obligation"}));

    this.delegates = {
        inspireId: new InspireIdController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            label: "Inspire ID",
            value: options.value.inspireId,
            helpText: "External object identifier of the spatial object. An external object identifier is a unique object identifier published by the responsible body, which may be used by external application to reference the spatial object. The identifier is an identifier of the spatial object, not an identifier of the real-world phenomenon."
        }),
        change: new EReportingChangeController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            label: "eReporting Change",
            value: options.value.change
        }),
        reportingPeriod: new NillableController({
            div: $("<div>").appendTo(this.$controls),
            menu: this.menu,
            delegate: TimeController,
            delegateOptions: {
                label: "Reporting Period",
                helpText: "Date defining the reporting period. The reporting period may be represented as either a time period (e.g. 2011-01-01 to 2011-12-31) or as a single date representing the reporting year (e.g. 2011).",
                value: options.value.reportingPeriod
            }
        })
    };
};
ReportObligationController.prototype = Object.create(CompositeController.prototype);

/*
content: new ListController({
    div: $("<div>").appendTo(this.$controls),
    menu: this.menu,
    label: "Contents",
    delegate: NillableController,
    delegateOptions: {
        delegate: ReferenceController,
        label: "Content",
        delegateOptions: {
            label: "Content",
            helpText: "Content"
        }
    },
    value: options.value.content
}),
delete: new ListController({
    div: $("<div>").appendTo(this.$controls),
    menu: this.menu,
    label: "Deletes",
    delegate: NillableController,
    delegateOptions: {
        delegate: ReferenceController,
        label: "Delete",
        delegateOptions: {
            label: "Delete",
            helpText: "Delete"
        }
    },
    value: options.value.content
})
*/

function ReportingHeaderController(options) {
    function createTabHeader(name, target, active) {
        return $("<li>")
            .append($("<a>")
                .attr("href", target)
                .text(name)
                .on("click", function(e) {
                    $(this).tab("show");
                    e.preventDefault();
                }));
    }

    function createTabPane(id, active) {
        return $("<div>")
            .addClass("tab-pane")
            .attr("id", id)
            .append($("<div>")
                .addClass("row")
                .append($("<div>")
                    .addClass("span4")
                    .append($("<ul>")
                        .addClass("content-list well well-small")))
            .append($("<div>")
                .addClass("content span8")));
    }
    options.value = options.value || {};
    this.reportObligations = options.value.reportObligations || [];
    var $content = $(options.div);
    var $header = $("<ul>");
    var $contents = $("<div>");

    $content
        .addClass("tabbable")
        .append($("<div>")
            .addClass("pull-right")
            .append($("<button>")
                .type("button")
                .addClass("btn btn-info")
                .text("Save")
                .on("click", util.hitch(this, "_onSave"))))
        .append($header)
        .append($contents);

    $contents
        .addClass("tab-content");

    var i, name = "Reporting Authority", id = "flow-reporting-authority";
    var $pane = createTabPane(id);

    $header.addClass("nav nav-tabs");
    $header.append(createTabHeader(name, "#" + id));
    $contents.append($pane);

    this.reportingAuthority = new ReportingAuthorityController({
        div: $pane.find(".content"),
        menu: menu.root($pane.find(".content-list")),
        label: "Reporting Authority",
        helpText: "Contact information for the Public Authority responsible for creating or collating the data that represents the Reporting Unit and submitting the data to relevant Authority.",
        value: options.value.reportingAuthority
    });

    for (i = 0; i < this.reportObligations.length; ++i) {
        name = "Flow " + this.reportObligations[i].name;
        id = "flow-" + this.reportObligations[i].id;
        $pane = createTabPane(id);
        $header.append(createTabHeader(name, "#" + id));
        $contents.append($pane);
        this.reportObligations[i].delegate = new ReportObligationController({
            div: $pane.find(".content"),
            menu: menu.root($pane.find(".content-list")),
            label: "Report Obligation (" + this.reportObligations[i].name + ")",
            helpText: this.reportObligations[i].description,
            value: this.reportObligations[i].value
        });
    }

    $content.find(".nav>li:first,.tab-pane:first").addClass("active");

}
ReportingHeaderController.prototype._onSave = function() {
    var value = this.val();
    $.ajax({
        type: "POST",
        data: JSON.stringify(value),
        headers: { "Content-Type": "application/json" },
        complete: function(xhr, status) {
            switch(status) {
                case "success":
                    showSuccess("eReportingHeader settings saved &hellip;");
                    break;
                case "error":
                    showError("Save failed: " + xhr.status + " " + xhr.statusText + "; Message: " + xhr.responseText);
                    break;
                case "timeout":
                    showError("Request timed out &hellip;");
                    break;
                case "abort":
                    showError("Request aborted &hellip;");
                    break;
                case "parsererror":
                    showError("Unparsable response &hellip;");
                    break;
            }

        }
    });
};

ReportingHeaderController.prototype.val = function() {
    var i, ro;
    var ret = {
        reportingAuthority: this.reportingAuthority.val(),
        reportObligations: {}
    };

    for (i = 0; i < this.reportObligations.length; ++i) {
        ret.reportObligations[this.reportObligations[i].id]
                = this.reportObligations[i].delegate.val();
    }
    return ret;
};

$.ajax({
    type: "GET",
    dataType: "json",
    headers: {"Accept": "application/json"},
    success: function(data) {
        new ReportingHeaderController({ div: "#e", value: data });
    }
});
</script>

<jsp:include page="../common/footer.jsp" />
