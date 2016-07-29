/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
/*! client 2016-07-29 08:57 */
function fetchData(){var a=angular.injector(["ng"]),b=a.get("$http");return b.get("settings.json").then(function(a){mainApp.constant("config",a.data)})}function bootstrapApp(){angular.element(document).ready(function(){var a=angular.bootstrap(document,["jsClient"],{strictDi:!0}),b=a.get("startupService");b.registerServices(["SetTimeseriesOfStatusService","SetTimeParameterService","SetInternalTimeseriesService","SetConstellationService","SetConstellationServiceHack","SetLanguageService"]),b.checkServices(),a.get("mapService")})}var mainApp=angular.module("jsClient",["ngRoute","ui.bootstrap","ui-notification","LocalStorageModule","ui-leaflet","pascalprecht.translate","ngSanitize","ngTable","ngResource","n52.core.alert","n52.core.barChart","n52.core.color","n52.core.dataLoading","n52.core.diagram","n52.core.exportTs","n52.core.favorite","n52.core.favoriteUi","n52.core.flot","n52.core.helper","n52.core.interface","n52.core.legend","n52.core.listSelection","n52.core.locate","n52.core.map","n52.core.menu","n52.core.userSettings","n52.core.legend","n52.core.table","n52.core.exportTs","n52.core.timeUi","n52.core.metadata","n52.core.modal","n52.core.overviewDiagram","n52.core.permalinkEval","n52.core.permalinkGen","n52.core.phenomena","n52.core.provider","n52.core.rawDataOutput","n52.core.userSettings","n52.core.settings","n52.core.sosMetadata","n52.core.startup","n52.core.status","n52.core.style","n52.core.styleTs","n52.core.table","n52.core.time","n52.core.timeUi","n52.core.timeseries","n52.core.tooltip","n52.core.translateSelector","n52.core.utils","n52.core.yAxisHide","n52.client.navigation","n52.client.map"]);mainApp.config(["$routeProvider",function(a){a.when("/",{templateUrl:"templates/views/diagramView.html",reloadOnSearch:!1}).when("/diagram",{templateUrl:"templates/views/diagramView.html",name:"navigation.diagram",reloadOnSearch:!1}).when("/map",{templateUrl:"templates/views/mapView.html",name:"navigation.map",reloadOnSearch:!1}).when("/favorite",{templateUrl:"templates/views/favoriteView.html",name:"navigation.favorite",reloadOnSearch:!1}).when("/map/provider",{name:"navigation.provider",modal:{controller:"SwcProviderListModalCtrl",templateUrl:"templates/map/provider-list-modal.html"},reloadOnSearch:!1}).when("/diagram/listSelection",{name:"navigation.listSelection",modal:{controller:"ModalWindowCtrl",templateUrl:"templates/listSelection/modal-list-selection.html"},reloadOnSearch:!1}).when("/diagram/settings",{name:"navigation.settings",modal:{controller:"SwcUserSettingsWindowCtrl",templateUrl:"templates/settings/user-settings-modal.html"},reloadOnSearch:!1}).otherwise({redirectTo:"/"})}]),mainApp.config(["$translateProvider","settingsServiceProvider",function(a,b){a.useStaticFilesLoader({prefix:"i18n/",suffix:".json"});var c=[];angular.forEach(b.$get().supportedLanguages,function(a){c.push(a.code)}),a.registerAvailableLanguageKeys(c),a.determinePreferredLanguage(),""!==a.preferredLanguage()&&-1!==c.indexOf(a.preferredLanguage())||a.preferredLanguage("en"),a.useSanitizeValueStrategy("sanitize")}]),mainApp.filter("objectCount",function(){return function(a){return a?Object.keys(a).length:0}}),mainApp.config(["$provide",function(a){a.decorator("$log",["$delegate",function(a){var b=a.debug;return a.info=function(){var a=[].slice.call(arguments),c=moment().format("HH:mm:ss.SSS");a[0]=c+" - "+a[0],b.apply(null,a)},a}])}]),fetchData().then(bootstrapApp),angular.module("n52.client.map",[]).controller("LayerControlCtrl",["$scope",function(a){angular.extend(a,{layercontrol:{icons:{uncheck:"glyphicon glyphicon-unchecked",check:"glyphicon glyphicon-check",radio:"glyphicon glyphicon-check",unradio:"glyphicon glyphicon-unchecked"}}})}]).controller("ToDiagramCtrl",["$scope","$location",function(a,b){a.toDiagram=function(){b.url("/diagram")}}]),angular.module("n52.client.navigation",[]).factory("routeNavigation",["$route","$location",function(a,b){var c=[];return angular.forEach(a.routes,function(a,b){a.name&&c.push({path:b,name:a.name,modal:a.modal})}),{routes:c,activeRoute:function(a){return a.path===b.path()}}}]).directive("navigation",["routeNavigation",function(a){return{restrict:"E",replace:!0,templateUrl:"templates/menu/navigation.html",controller:["$scope","$location","$uibModal",function(b,c,d){b.routes=a.routes,b.activeRoute=a.activeRoute,b.open=function(a){a&&(c.url(c.url()),d.open({animation:!0,templateUrl:a.templateUrl,controller:a.controller}))}}]}}]);