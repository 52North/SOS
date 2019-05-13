/*
 * Copyright (C) 2012-2019 52°North Initiative for Geospatial Open Source
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
/*! client 2017-04-28 09:58 */
function fetchConfig(){return angular.injector(["ng"]).get("$http").get("settings.json").then(function(a){mainApp.constant("config",a.data)})}function fetchTemplates(){return angular.injector(["ng"]).get("$http").get("templates/templates.json").then(function(a){mainApp.constant("templatesMapping",a.data)})}function bootstrapApp(){angular.element(document).ready(function(){var a=angular.bootstrap(document,["jsClient"],{strictDi:!0}),b=a.get("startupService");b.registerServices(["SetTimeseriesOfStatusService","SetTimeParameterService","SetInternalTimeseriesService","SetConstellationService","SetConstellationServiceHack","SetLanguageService"]),b.checkServices(),a.get("mapService")})}var mainApp=angular.module("jsClient",["ngRoute","ui.bootstrap","ui-notification","LocalStorageModule","ui-leaflet","pascalprecht.translate","ngSanitize","ngTable","ngResource","n52.core.barChart","n52.core.base","n52.core.dataLoading","n52.core.diagram","n52.core.exportTs","n52.core.favoriteUi","n52.core.flot","n52.core.helper","n52.core.interface","n52.core.legend","n52.core.listSelection","n52.core.locate","n52.core.map","n52.core.menu","n52.core.userSettings","n52.core.legend","n52.core.table","n52.core.exportTs","n52.core.timeUi","n52.core.metadata","n52.core.modal","n52.core.overviewDiagram","n52.core.phenomena","n52.core.provider","n52.core.userSettings","n52.core.startup","n52.core.style","n52.core.table","n52.core.timeUi","n52.core.translate","n52.client.navigation","n52.client.map"]);mainApp.config(["$routeProvider",function(a){a.when("/",{templateUrl:"templates/views/diagramView.html",reloadOnSearch:!1}).when("/diagram",{templateUrl:"templates/views/diagramView.html",name:"navigation.diagram",reloadOnSearch:!1}).when("/map",{templateUrl:"templates/views/mapView.html",name:"navigation.map",reloadOnSearch:!1}).when("/diagram/settings",{name:"navigation.settings",modal:{controller:"SwcUserSettingsWindowCtrl",templateUrl:"templates/settings/user-settings-modal.html"},reloadOnSearch:!1}).otherwise({redirectTo:"/"})}]),mainApp.config(["$translateProvider","settingsServiceProvider","$locationProvider",function(a,b,c){a.useStaticFilesLoader({prefix:"i18n/",suffix:".json"}),c.hashPrefix("");var d=[];angular.forEach(b.$get().supportedLanguages,function(a){d.push(a.code)}),a.registerAvailableLanguageKeys(d),a.determinePreferredLanguage(),""!==a.preferredLanguage()&&-1!==d.indexOf(a.preferredLanguage())||a.preferredLanguage("en"),a.useSanitizeValueStrategy(null)}]),mainApp.filter("objectCount",function(){return function(a){return a?Object.keys(a).length:0}}),mainApp.config(["$provide",function(a){a.decorator("$log",["$delegate",function(a){var b=a.debug;return a.info=function(){var a=[].slice.call(arguments),c=moment().format("HH:mm:ss.SSS");a[0]=c+" - "+a[0],b.apply(null,a)},a}])}]),angular.injector(["ng"]).get("$q").all([fetchConfig(),fetchTemplates()]).then(bootstrapApp),angular.module("n52.core.map").service("ecmwfPlatformPresenter",["$uibModal","mapService",function(a,b){this.presentPlatform=function(c){a.open({animation:!0,templateUrl:"templates/ecmwf/stationary-insitu-ecmwf.html",resolve:{selection:function(){var a,d=c.url;return b.map.selectedPhenomenon&&angular.forEach(b.map.selectedPhenomenon.provider,function(b){d===b.url&&(a=b.phenomenonID)}),{id:c.id,phenomenonId:a,url:d}}},controller:"SwcModalEcmwfCtrl"})}}]).controller("SwcModalEcmwfCtrl",["$scope","$uibModalInstance","selection","seriesApiInterface","serviceFinder",function(a,b,c,d,e){a.serviceUrl=c.url,d.getPlatforms(c.id,c.url).then(function(b){a.platform=b,d.getPlatformExtras(c.id,c.url,{fields:"parents"}).then(function(b){a.parentProcedures=b.parents.procedures}),b.datasets.length>0&&d.getDatasetExtras(b.datasets[0].id,c.url,{fields:"resultTimes"}).then(function(b){a.resultTimes=b.resultTimes})}),a.close=function(){b.close()},a.selectProcedure=function(b){a.selectedProcedure=b},a.selectResultTime=function(b){a.selectedResultTime=b,a.datasets=[],d.getDatasets(null,c.url,{platforms:c.id,procedures:a.selectedProcedure.id}).then(function(b){a.datasets=b,a.datasets.forEach(function(b){d.getDatasets(b.id,c.url,{resultTime:a.selectedResultTime}).then(function(a){a.selected=!0,angular.extend(b,a)})})})},a.toggled=function(){var b=!0;angular.forEach(a.platform.datasets,function(a){a.selected||(b=!1)}),a.isAllSelected=b},a.presentSelection=function(){angular.forEach(a.datasets,function(b){b.filter={resultTime:a.selectedResultTime},!b.selected||c.phenomenonId&&b.seriesParameters.phenomenon.id!==c.phenomenonId||e.getDatasetPresenter(b.valueType,b.seriesParameters.platform.platformType,c.url).presentDataset(b,c.url)}),a.close()}}]).component("swcEcmwfProcedureSelection",{bindings:{items:"<",onSelect:"&"},templateUrl:"n52.ecmwf.map.procedure-selection",controller:["seriesApiInterface","utils",function(a,b){this.$onInit=function(){},this.onChange=function(a){this.onSelect({item:this.selection})}}]}).component("swcEcmwfResultTimeSelection",{bindings:{items:"<",onSelect:"&"},templateUrl:"n52.ecmwf.map.result-time-selection",controller:[function(){this.onChange=function(a){this.onSelect({item:this.selection})}}]}).component("swcEcmwfLegend",{bindings:{items:"<"},templateUrl:"n52.ecmwf.legend",controller:["timeseriesService","seriesApiInterface",function(a,b){this.$doCheck=function(){Object.keys(this.items).length!==this.previousCount&&(this.createEntries(),this.previousCount=Object.keys(this.items).length)},this.createEntries=function(){this.entries=[];for(var a in this.items)if(this.items.hasOwnProperty(a)){var b=this.items[a].seriesParameters.platform.id,c=this.items[a].filter.resultTime;this.addToEntries(this.items[a],b,c)}},this.addToEntries=function(a,c,d){var e=this;b.getProcedures(a.seriesParameters.procedure.id,a.apiUrl).then(function(b){var f=b.parents[0].label,g=!1;e.entries.forEach(function(b){b.ecmwfGroup&&b.platformID===c&&b.resultTime===d&&b.parentProcedureLabel===f&&(b.items.push(a),g=!0)}),g||e.entries.push({ecmwfGroup:!0,platformID:c,resultTime:d,parentProcedureLabel:f,items:[a]})})}}]}).component("swcEcmwfLegendEntry",{bindings:{item:"<"},templateUrl:"n52.ecmwf.legend-entry",controller:["styleService","timeseriesService","locateStationService","$location",function(a,b,c,d){this.toggleSelection=function(){this.item.items.forEach(function(b){a.toggleSelection(b)})},this.toggleVisibility=function(){this.item.items.forEach(function(b){a.toggleTimeseriesVisibility(b)})},this.removeAll=function(){this.item.items.forEach(function(a){b.removeTimeseries(a.internalId)})},this.showInMap=function(){c.showPlatform("mapService",this.item.items[0]),d.url("/map")},this.toggled=function(b){a.triggerStyleUpdate(b)}}]}).config(["$provide",function(a){a.decorator("utils",["$delegate","$q","$http",function(a,b,c){return a.oldCreateInternalId=a.createInternalId,a.createInternalId=function(b){return b.filter&&b.filter.resultTime?a.oldCreateInternalId(b)+b.filter.resultTime:a.oldCreateInternalId(b)},a}])}]),angular.module("n52.client.map",[]).controller("LayerControlCtrl",["$scope",function(a){angular.extend(a,{layercontrol:{icons:{uncheck:"glyphicon glyphicon-unchecked",check:"glyphicon glyphicon-check",radio:"glyphicon glyphicon-check",unradio:"glyphicon glyphicon-unchecked"}}})}]).controller("ToDiagramCtrl",["$scope","$location",function(a,b){a.toDiagram=function(){b.url("/diagram")}}]),angular.module("n52.client.navigation",[]).factory("routeNavigation",["$route","$location",function(a,b){var c=[];return angular.forEach(a.routes,function(a,b){a.name&&c.push({path:b,name:a.name,modal:a.modal})}),{routes:c,activeRoute:function(a){return a.path===b.path()}}}]).directive("navigation",["routeNavigation",function(a){return{restrict:"E",replace:!0,templateUrl:"templates/menu/navigation.html",controller:["$scope","$location","$uibModal",function(b,c,d){b.routes=a.routes,b.activeRoute=a.activeRoute,b.open=function(a){a&&(c.url(c.url()),d.open({animation:!0,templateUrl:a.templateUrl,controller:a.controller}))}}]}}]).config(["$provide",function(a){a.decorator("providerService",["$delegate","$location",function(a,b){return a.oldSelectProvider=a.selectProvider,a.selectProvider=function(c){a.oldSelectProvider(c),b.url("/map")},a}])}]);