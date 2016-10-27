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
var mainApp = angular.module('jsClient', [
    'ngRoute',
    'ui.bootstrap',
    'ui-notification',
    'LocalStorageModule',
    'ui-leaflet',
    'pascalprecht.translate',
    'ngSanitize',
    'ngTable',
    'ngResource',
    'n52.core.alert',
    'n52.core.barChart',
    'n52.core.color',
    'n52.core.dataLoading',
    'n52.core.diagram',
    'n52.core.exportTs',
    'n52.core.favorite',
    'n52.core.favoriteUi',
    'n52.core.flot',
    'n52.core.helper',
    'n52.core.interface',
    'n52.core.legend',
    'n52.core.listSelection',
    'n52.core.locate',
    'n52.core.map',
    'n52.core.menu',
    'n52.core.userSettings',
    'n52.core.legend',
    'n52.core.table',
    'n52.core.exportTs',
    'n52.core.timeUi',
    'n52.core.metadata',
    'n52.core.modal',
    'n52.core.overviewDiagram',
    'n52.core.permalinkEval',
    'n52.core.permalinkGen',
    'n52.core.phenomena',
    'n52.core.provider',
    'n52.core.rawDataOutput',
    'n52.core.userSettings',
    'n52.core.settings',
    'n52.core.sosMetadata',
    'n52.core.startup',
    'n52.core.status',
    'n52.core.style',
    'n52.core.styleTs',
    'n52.core.table',
    'n52.core.time',
    'n52.core.timeUi',
    'n52.core.timeseries',
    'n52.core.tooltip',
    'n52.core.translateSelector',
    'n52.core.utils',
    'n52.core.yAxisHide',
    'n52.client.navigation',
    'n52.client.map',
    'n52.client.mobile'
]);

mainApp.config(['$routeProvider', function ($routeProvider) {
        $routeProvider
                .when('/', {
                    templateUrl: 'templates/views/diagramView.html',
                    reloadOnSearch: false
                })
                .when('/diagram', {
                    templateUrl: 'templates/views/diagramView.html',
                    name: 'navigation.diagram',
                    reloadOnSearch: false
                })
                .when('/map', {
                    templateUrl: 'templates/views/mapView.html',
                    name: 'navigation.map',
                    reloadOnSearch: false
                })
                .when('/mobileDiagram', {
                    templateUrl: 'templates/views/combiView.html',
                    name: 'navigation.trajectories',
                    reloadOnSearch: false
                })
                .when('/favorite', {
                    templateUrl: 'templates/views/favoriteView.html',
                    name: 'navigation.favorite',
                    reloadOnSearch: false
                })
                .when('/map/provider', {
                    name: 'navigation.provider',
                    modal: {
                        controller: 'SwcProviderListModalCtrl',
                        templateUrl: 'templates/map/provider-list-modal.html'
                    },
                    reloadOnSearch: false
                })
                .when('/diagram/listSelection', {
                    name: 'navigation.listSelection',
                    modal: {
                        controller: 'ModalWindowCtrl',
                        templateUrl: 'templates/listSelection/modal-list-selection.html'
                    },
                    reloadOnSearch: false
                })
                .when('/diagram/settings', {
                    name: 'navigation.settings',
                    modal: {
                        controller: 'SwcUserSettingsWindowCtrl',
                        templateUrl: 'templates/settings/user-settings-modal.html'
                    },
                    reloadOnSearch: false
                })
                .otherwise({redirectTo: '/'});
    }]);

mainApp.config(['$translateProvider', 'settingsServiceProvider', function ($translateProvider, settingsServiceProvider) {
        $translateProvider.useStaticFilesLoader({
            prefix: 'i18n/',
            suffix: '.json'
        });
        var suppLang = [];
        angular.forEach(settingsServiceProvider.$get().supportedLanguages, function (lang) {
            suppLang.push(lang.code);
        });
        $translateProvider.registerAvailableLanguageKeys(suppLang);
        $translateProvider.determinePreferredLanguage();
        if ($translateProvider.preferredLanguage() === '' 
                || suppLang.indexOf($translateProvider.preferredLanguage()) === -1) {
            $translateProvider.preferredLanguage('en');
        }
        $translateProvider.useSanitizeValueStrategy('sanitize');
    }]);

mainApp.filter('objectCount', function () {
    return function (item) {
        if (item) {
            return Object.keys(item).length;
        } else {
            return 0;
        }
    };
});

mainApp.config(["$provide", function ($provide)
    {
        // Use the `decorator` solution to substitute or attach behaviors to
        // original service instance; @see angular-mocks for more examples....

        $provide.decorator('$log', ["$delegate", function ($delegate)
            {
                // Save the original $log.debug()
                var debugFn = $delegate.debug;

                $delegate.info = function ( )
                {
                    var args = [].slice.call(arguments),
                            now = moment().format('HH:mm:ss.SSS');

                    // Prepend timestamp
                    args[0] = now + " - " + args[0];

                    // Call the original with the output prepended with formatted timestamp
                    debugFn.apply(null, args);
                };

                return $delegate;
            }]);
    }]);

// start the app after loading the settings.json
fetchData().then(bootstrapApp);

function fetchData() {
    var initInjector = angular.injector(["ng"]);
    var $http = initInjector.get("$http");
    return $http.get("settings.json").then(function (response) {
        mainApp.constant("config", response.data);
    });
}

function bootstrapApp() {
    angular.element(document).ready(function () {
        var injector = angular.bootstrap(document, ["jsClient"], {strictDi: true});
        // initilize parameter reader
        var startupService = injector.get('startupService');
        startupService.registerServices([
            'SetTimeseriesOfStatusService',
            'SetTimeParameterService',
            'SetInternalTimeseriesService',
            'SetConstellationService',
            'SetConstellationServiceHack',
            'SetLanguageService'
        ]);
        startupService.checkServices();
        // init mapService to have load stations directly
        injector.get('mapService');
    });
}

angular.module('n52.client.map', [])
    .controller('LayerControlCtrl', ['$scope', function($scope) {
        angular.extend($scope, {
            layercontrol: {
                icons: {
                    uncheck: "glyphicon glyphicon-unchecked",
                    check: "glyphicon glyphicon-check",
                    radio: "glyphicon glyphicon-check",
                    unradio: "glyphicon glyphicon-unchecked"
                }
            }
        });
    }])
    .controller('ToDiagramCtrl', ['$scope', '$location', function($scope, $location) {
        $scope.toDiagram = function() {
            $location.url('/diagram');
        };
    }]);

angular.module('n52.client.navigation', [])
    .factory('routeNavigation', ['$route', '$location', function($route, $location) {
        var routes = [];
        angular.forEach($route.routes, function(route, path) {
            if (route.name) {
                routes.push({
                    path: path,
                    name: route.name,
                    modal: route.modal
                });
            }
        });
        return {
            routes: routes,
            activeRoute: function(route) {
                return route.path === $location.path();
            }
        };
    }])
    .directive('navigation', ['routeNavigation', function(routeNavigation) {
        return {
            restrict: "E",
            replace: true,
            templateUrl: "templates/menu/navigation.html",
            controller: ['$scope', '$location', '$uibModal', function($scope, $location, $uibModal) {
                $scope.routes = routeNavigation.routes;
                $scope.activeRoute = routeNavigation.activeRoute;
                $scope.open = function(modal) {
                    if (modal) {
                        $location.url($location.url());
                        $uibModal.open({
                            animation: true,
                            templateUrl: modal.templateUrl,
                            controller: modal.controller
                        });
                    }
                };
            }]
        };
    }])
    // switch to map view after new provider is selected
    .config(['$provide',
        function($provide) {
            $provide.decorator('providerService', ['$delegate', '$location',
                function($delegate, $location) {
                    var oldSelectProvider = $delegate.selectProvider;
                    $delegate.selectProvider = function(selection) {
                        oldSelectProvider(selection);
                        $location.url('/map');
                    };
                    return $delegate;
                }
            ]);
        }
    ]);

angular.module('n52.client.mobile', [])
    .directive('swcCombiMobile', [
        function() {
            return {
                restrict: 'E',
                templateUrl: 'templates/mobile/combi-mobile.html',
                replace: true,
                controller: ['$scope', 'combinedSrvc', 'leafletData',
                    function($scope, combinedSrvc, leafletData) {
                        var mouseValueLabel, mouseTimeLabel, pointG, mouseRect;
                        $scope.events = {
                            geometry: {
                                enable: ['mouseover']
                            }
                        };
                        $scope.geometry = combinedSrvc.geometry;
                        $scope.series = combinedSrvc.series;
                        $scope.highlight = combinedSrvc.highlight;
                        $scope.selectedSection = combinedSrvc.selectedSection;
                        $scope.paths = {
                            section: {
                                color: 'blue',
                                weight: 4,
                                latlngs: []
                            }
                        };

                        $scope.$watch('geometry', function(geometry) {
                            if (geometry && geometry.data && geometry.data.coordinates.length > 0) {
                                centerMap();
                                resetHighlighter();
                            }
                        }, true);

                        $scope.$watch('highlight', function(hl) {
                            if (hl.latlng) {
                                drawMapMarker(hl);
                            } else {
                                hideMapMarker();
                            }
                        }, true);

                        $scope.$on('leafletDirectiveMap.mobileCombiMap.zoomend', function(temp) {
                            if ($scope.highlight.latlng !== undefined) {
                                drawMapMarker($scope.highlight);
                            }
                        });

                        $scope.$watchCollection('selectedSection', function(selection) {
                            if (selection && selection.values && selection.values.length > 0) {
                                $scope.paths.section.latlngs = [];
                                var ll = [];
                                angular.forEach(selection.values, function(value) {
                                    $scope.paths.section.latlngs.push({
                                        lat: value.latlng.lat,
                                        lng: value.latlng.lng
                                    });
                                    ll.push(value.latlng);
                                });
                                leafletData.getMap('mobileCombiMap').then(function(map) {
                                    map.fitBounds(ll);
                                });
                            } else {
                                centerMap();
                                $scope.paths.section.latlngs = [];
                            }
                        }, true);

                        $scope.$on('leafletDirectiveGeoJson.mobileCombiMap.mouseover', function(event, path) {
                            if (path && path.leafletEvent && path.leafletEvent.latlng) {
                                combinedSrvc.showHighlightedItem(path.leafletEvent.latlng);
                            }
                        });

                        var centerMap = function() {
                            if ($scope.geometry && $scope.geometry.data.coordinates.length > 0) {
                                leafletData.getMap('mobileCombiMap').then(function(map) {
                                    var latlngs = [];
                                    angular.forEach($scope.geometry.data.coordinates, function(coords) {
                                        latlngs.push(L.GeoJSON.coordsToLatLng(coords));
                                    });
                                    map.fitBounds(latlngs);
                                });
                            }
                        };

                        function resetHighlighter() {
                            if (pointG) {
                                pointG.remove();
                                mouseRect.remove();
                                mouseValueLabel.remove();
                                mouseTimeLabel.remove();
                                pointG = undefined;
                            }
                        }

                        function drawMapMarker(highlighted) {
                            leafletData.getMap('mobileCombiMap').then(function(map) {
                                var layerpoint = map.latLngToLayerPoint(highlighted.latlng);

                                if (!pointG) {
                                    var g = d3.select(".leaflet-overlay-pane svg")
                                        .append("g");

                                    pointG = g.append("g");
                                    pointG.append("svg:circle")
                                        .attr("r", 6)
                                        .attr("cx", 0)
                                        .attr("cy", 0)
                                        .attr("class", "height-focus circle-lower");

                                    mouseRect = g.append('svg:rect')
                                        .attr('class', 'map-highlight-label');
                                    mouseValueLabel = g.append("svg:text")
                                        .attr("class", "focus-label")
                                        .style("pointer-events", "none");
                                    mouseTimeLabel = g.append("svg:text")
                                        .attr("class", "focus-label")
                                        .style("pointer-events", "none");

                                }
                                pointG.attr("transform", "translate(" + layerpoint.x + "," + layerpoint.y + ")")
                                    .style("visibility", "visible");
                                mouseValueLabel.attr("x", layerpoint.x + 10)
                                    .attr("y", layerpoint.y)
                                    .text(highlighted.value + $scope.series.uom)
                                    .style("visibility", "visible");
                                mouseTimeLabel.attr("x", layerpoint.x + 10)
                                    .attr("y", layerpoint.y + 13)
                                    .text(moment(highlighted.timestamp).format('DD.MM.YY HH:mm'))
                                    .style("visibility", "visible");
                                mouseRect.attr('x', layerpoint.x + 8)
                                    .attr('y', layerpoint.y - 11)
                                    .attr('width', 100)
                                    .attr('height', 28);
                            });
                        }

                        function hideMapMarker() {
                            if (mouseRect) {
                                mouseTimeLabel.style("visibility", "hidden");
                                mouseValueLabel.style("visibility", "hidden");
                                mouseRect.style("visibility", "hidden");
                            }
                            if (pointG) {
                                pointG.style("visibility", "hidden");
                            }
                        }

                    }
                ]
            };
        }
    ])
    .directive('d3LinearChart', ['$window', 'combinedSrvc',
        function($window, combinedSrvc) {
            return {
                restrict: 'EA',
                link: function(scope, elem, attrs) {
                    scope.data = combinedSrvc.data;
                    scope.series = combinedSrvc.series;
                    scope.highlight = combinedSrvc.highlight;

                    var margin = {
                        top: 10,
                        right: 20,
                        bottom: 40,
                        left: 40
                    };
                    var background,
                        pathClass = "path",
                        xScale, yScale, xAxisGen, yAxisGen, lineFun, area,
                        focusG, highlightFocus, focuslabelValue, focuslabelTime, focuslabelY,
                        dragging, dragStart, dragCurrent, dragRect, dragRectG;

                    var d3 = $window.d3;

                    d3.select(elem[0])
                        .append('svg')
                        .attr('width', '100%')
                        .attr('height', '100%');

                    var rawSvg = elem.find('svg');

                    var svgElem = d3.select(rawSvg[0]);

                    var graph = svgElem
                        .append("g")
                        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

                    scope.$watchCollection('data', function() {
                        if (scope.data.values.length > 0) {
                            drawLineChart();
                        }
                    });

                    scope.$watchCollection('highlight', function() {
                        if (scope.highlight.xDiagCoord) {
                            showDiagramIndicator(scope.highlight, scope.highlight.xDiagCoord);
                        }
                    });

                    angular.element($window).bind('resize', function() {
                        drawLineChart();
                    });

                    function height() {
                        return rawSvg.height() - margin.top - margin.bottom;
                    }

                    function width() {
                        return rawSvg.width() - margin.left - margin.right;
                    }

                    function setChartParameters() {
                        xScale = d3.scale.linear()
                            .domain([scope.data.values[0].dist, scope.data.values[scope.data.values.length - 1].dist])
                            .range([0, width()]);

                        var range = scope.data.range.max - scope.data.range.min;
                        var rangeOffset = range * 0.05;
                        yScale = d3.scale.linear()
                            .domain([scope.data.range.min - rangeOffset, scope.data.range.max + rangeOffset])
                            .range([height(), 0]);

                        xAxisGen = d3.svg.axis()
                            .scale(xScale)
                            .orient("bottom")
                            .ticks(10);

                        yAxisGen = d3.svg.axis()
                            .scale(yScale)
                            .orient("left")
                            .ticks(5);

                        lineFun = d3.svg.line()
                            .x(function(d) {
                                var xDiagCoord = xScale(d.dist);
                                d.xDiagCoord = xDiagCoord;
                                return xDiagCoord;
                            })
                            .y(function(d) {
                                return yScale(d.value);
                            })
                            .interpolate("linear");
                        area = d3.svg.area()
                            .x(function(d) {
                                var xDiagCoord = xScale(d.dist);
                                d.xDiagCoord = xDiagCoord;
                                return xDiagCoord;
                            })
                            .y0(height())
                            .y1(function(d) {
                                return yScale(d.value);
                            })
                            .interpolate("linear");
                    }

                    function make_x_axis() {
                        return d3.svg.axis()
                            .scale(xScale)
                            .orient("bottom")
                            .ticks(10);
                    }

                    function make_y_axis() {
                        return d3.svg.axis()
                            .scale(yScale)
                            .orient("left")
                            .ticks(5);
                    }

                    function drawLineChart() {
                        graph.selectAll("*").remove();

                        setChartParameters();

                        // draw the x grid lines
                        graph.append("svg:g")
                            .attr("class", "grid")
                            .attr("transform", "translate(0," + height() + ")")
                            .call(make_x_axis().tickSize(-height(), 0, 0).tickFormat(''));

                        graph.append("text") // text label for the x axis
                            .attr("x", width() / 2)
                            .attr("y", height() + margin.bottom - 5)
                            .style("text-anchor", "middle")
                            .text("Distance");

                        // draw the y grid lines
                        graph.append("svg:g")
                            .attr("class", "grid")
                            .call(make_y_axis().tickSize(-width(), 0, 0).tickFormat(''));

                        graph.append("text")
                            .attr("transform", "rotate(-90)")
                            .attr("y", 0 - margin.left)
                            .attr("x", 0 - (height() / 2))
                            .attr("dy", "1em")
                            .style("text-anchor", "middle")
                            .text(scope.series.uom);

                        // draw filled area
                        graph.append("svg:path")
                            .datum(scope.data.values)
                            .attr({
                                d: area,
                                "class": "graphArea"
                            });

                        // draw x axis
                        graph.append("svg:g")
                            .attr("class", "x axis")
                            .attr("transform", "translate(0," + height() + ")")
                            .call(xAxisGen);

                        // draw right axis as border
                        graph.append("svg:g")
                            .attr("class", "x axis")
                            .call(d3.svg.axis()
                                .scale(xScale)
                                .orient("top")
                                .tickSize(0)
                                .tickFormat(''));

                        // draw y axis
                        graph.append("svg:g")
                            .attr("class", "y axis")
                            .call(yAxisGen);

                        // draw right axis as border
                        graph.append("svg:g")
                            .attr("class", "y axis")
                            .attr("transform", "translate(" + width() + ", 0)")
                            .call(d3.svg.axis()
                                .scale(yScale)
                                .orient("right")
                                .tickSize(0)
                                .tickFormat(''));

                        // draw the value line
                        graph.append("svg:path")
                            .attr({
                                d: lineFun(scope.data.values),
                                "stroke": "blue",
                                "stroke-width": 2,
                                "fill": "none",
                                "class": pathClass
                            });

                        background = graph.append("svg:rect")
                            .attr({
                                "width": width(),
                                "height": height(),
                                "fill": "none",
                                "stroke": "none",
                                "pointer-events": "all"
                            })
                            .on("mousemove.focus", mousemoveHandler)
                            .on("mouseout.focus", mouseoutHandler)
                            .on("mousedown.drag", dragStartHandler)
                            .on("mousemove.drag", dragHandler)
                            .on("mouseup.drag", dragEndHandler);

                        focusG = graph.append("g");
                        highlightFocus = focusG.append('svg:line')
                            .attr('class', 'mouse-focus-line')
                            .attr('x2', '0')
                            .attr('y2', '0')
                            .attr('x1', '0')
                            .attr('y1', '0');
                        focuslabelValue = focusG.append("svg:text")
                            .style("pointer-events", "none")
                            .attr("class", "mouse-focus-label-x");
                        focuslabelTime = focusG.append("svg:text")
                            .style("pointer-events", "none")
                            .attr("class", "mouse-focus-label-x");
                        focuslabelY = focusG.append("svg:text")
                            .style("pointer-events", "none")
                            .attr("class", "mouse-focus-label-y");
                    }

                    function mousemoveHandler(d, i, ctx) {
                        if (!scope.data.values || scope.data.values.length === 0) {
                            return;
                        }
                        var coords = d3.mouse(background.node());
                        combinedSrvc.highlightByIdx(getItemForX(coords[0]));
                        scope.$apply();
                    }

                    function mouseoutHandler() {
                        hideDiagramIndicator();
                    }

                    function dragStartHandler() {
                        d3.event.preventDefault();
                        d3.event.stopPropagation();
                        dragging = false;
                        dragStart = d3.mouse(background.node());
                    }

                    function dragHandler() {
                        d3.event.preventDefault();
                        d3.event.stopPropagation();
                        dragging = true;
                        drawDragRectangle();
                    }

                    function dragEndHandler() {
                        if (!dragStart || !dragging) {
                            dragStart = null;
                            dragging = false;
                            resetDrag();
                        } else {
                            combinedSrvc.setSelection(getItemForX(dragStart[0]), getItemForX(dragCurrent[0]));
                            dragStart = null;
                            dragging = false;
                        }
                        scope.$apply();
                    }

                    function drawDragRectangle() {
                        if (!dragStart) {
                            return;
                        }

                        dragCurrent = d3.mouse(background.node());

                        var x1 = Math.min(dragStart[0], dragCurrent[0]),
                            x2 = Math.max(dragStart[0], dragCurrent[0]);

                        if (!dragRect && !dragRectG) {

                            dragRectG = graph.append("g");

                            dragRect = dragRectG.append("rect")
                                .attr("width", x2 - x1)
                                .attr("height", height())
                                .attr("x", x1)
                                .attr('class', 'mouse-drag')
                                .style("pointer-events", "none");
                        } else {
                            dragRect.attr("width", x2 - x1)
                                .attr("x", x1);
                        }
                    }

                    function resetDrag() {
                        combinedSrvc.resetSelection();
                        if (dragRectG !== null) {
                            dragRectG.remove();
                            dragRectG = null;
                            dragRect = null;
                        }
                    }

                    function getItemForX(x) {
                        var bisect = d3.bisector(function(d) {
                            return d.dist;
                        }).left;
                        var xinvert = xScale.invert(x);
                        return bisect(scope.data.values, xinvert);
                    }

                    function hideDiagramIndicator() {
                        focusG.style("visibility", "hidden");
                    }

                    function showDiagramIndicator(item, xCoordinate) {
                        focusG.style("visibility", "visible");
                        highlightFocus.attr('x1', xCoordinate)
                            .attr('y1', 0)
                            .attr('x2', xCoordinate)
                            .attr('y2', height())
                            .classed('hidden', false);

                        var alt = item.value,
                            dist = item.dist,
                            numY = alt,
                            numX = dist;

                        focuslabelValue
                            .attr("x", xCoordinate + 2)
                            .attr("y", 13)
                            .text(numY + scope.series.uom);
                        focuslabelTime
                            .attr('x', xCoordinate - 95)
                            .attr('y', 13)
                            .text(moment(item.timestamp).format('DD.MM.YY HH:mm'));
                        focuslabelY
                            .attr("y", height() - 5)
                            .attr("x", xCoordinate + 2)
                            .text(numX + " km");
                    }
                }
            };
        }
    ])
    .service('combinedSrvc', ['interfaceService', 'statusService',
        function(interfaceService, statusService) {
            this.highlight = {};
            this.selectedSection = {
                values: []
            };
            this.geometry = {
                style: {
                    weight: 2,
                    opacity: 1,
                    color: 'red',
                    dashArray: '10, 5',
                    clickable: true
                },
                data: {
                    coordinates: [],
                    type: 'LineString'
                }
            };
            this.data = {
                values: [],
                range: {
                    max: 0,
                    min: Infinity
                },
                dist: 0
            };
            this.series = {};

            this.loadSeries = function(id, url) {
                statusService.status.mobile = {
                    id: id,
                    url: url
                };
                this.series.loading = true;
                interfaceService.getDatasets(id, url)
                    .then(s => {
                        angular.extend(this.series, s);
                        var timespan = {
                            start: s.firstValue.timestamp,
                            end: s.lastValue.timestamp
                        };
                        interfaceService.getDatasetData(s.id, url, timespan, {
                                expanded: true
                            })
                            .then(data => {
                                this.processData(data[id].values);
                                this.series.loading = false;
                            });
                    });
            };

            this.processData = function(data) {
                this.resetGeometry();
                this.resetData();
                for (var i = 0; i < data.length; i++) {
                    this.addToGeometry(data[i]);
                    this.addToData(data[i], data[i ? i - 1 : 0]);
                }
            };

            this.addToGeometry = function(entry) {
                this.geometry.data.coordinates.push(entry.geometry.coordinates);
            };

            this.addToData = function(entry, previous) {
                var s = new L.LatLng(entry.geometry.coordinates[1], entry.geometry.coordinates[0]);
                var e = new L.LatLng(previous.geometry.coordinates[1], previous.geometry.coordinates[0]);
                var newdist = s.distanceTo(e);
                this.data.dist = this.data.dist + Math.round(newdist / 1000 * 100000) / 100000;
                this.data.range.max = this.data.range.max < entry.value ? entry.value : this.data.range.max;
                this.data.range.min = this.data.range.min > entry.value ? entry.value : this.data.range.min;
                this.data.values.push({
                    dist: Math.round(this.data.dist * 10) / 10,
                    timestamp: entry.timestamp,
                    value: entry.value,
                    x: entry.geometry.coordinates[0],
                    y: entry.geometry.coordinates[1],
                    latlng: s
                });
            };

            this.resetGeometry = function() {
                this.geometry.data.coordinates = [];
            };

            this.resetData = function() {
                this.data.values = [];
                this.data.dist = 0;
                this.data.range.max = 0;
                this.data.range.min = Infinity;
            };

            this.findItemForLatLng = function(latlng) {
                var result = null,
                    d = Infinity;
                angular.forEach(this.data.values, function(item) {
                    var dist = latlng.distanceTo(item.latlng);
                    if (dist < d) {
                        d = dist;
                        result = item;
                    }
                });
                return result;
            };

            this.highlightByIdx = function(idx) {
                angular.extend(this.highlight, this.data.values[idx]);
            };

            this.showHighlightedItem = function(latlng) {
                angular.extend(this.highlight, this.findItemForLatLng(latlng));
            };

            this.setSelection = function(startIdx, endIdx) {
                var start = Math.min(startIdx, endIdx),
                    end = Math.max(startIdx, endIdx);
                this.selectedSection.values = this.data.values.slice(start, end);
            };

            this.resetSelection = function() {
                this.selectedSection.values = [];
            };

            if (statusService.status.mobile) {
                let lastEntry = statusService.status.mobile;
                if (lastEntry.id && lastEntry.url) {
                    this.loadSeries(lastEntry.id, lastEntry.url);
                }
            }
        }
    ])
    .service('mobilePresentDataset', ['$location', 'combinedSrvc',
        function($location, combinedSrvc) {
            this.presentDataset = function(dataset, providerUrl) {
                combinedSrvc.loadSeries(dataset.id, providerUrl);
                $location.url('/mobileDiagram');
            };
        }
    ]);

angular.module('n52.client.mobile')
    .controller('ListSelectionMobileButtonCtrl', ['$scope', '$uibModal', 'combinedSrvc',
        function($scope, $uibModal, combinedSrvc) {
            $scope.openListSelectionMobile = function() {
                $uibModal.open({
                    animation: true,
                    templateUrl: 'templates/mobile/modal-list-selection-mobile.html',
                    controller: 'ModalListSelectionMobileCtrl'
                });
            };

            if (Object.keys(combinedSrvc.series).length === 0) {
                $scope.openListSelectionMobile();
            }
        }
    ])
    .controller('ModalListSelectionMobileCtrl', ['$scope', '$uibModalInstance',
        function($scope, $uibModalInstance) {
            $scope.modalInstance = $uibModalInstance;
            $scope.platformParams = [{
                type: 'platform',
                header: 'trajectories.headers.platform'
            }, {
                type: 'offering',
                header: 'trajectories.headers.offering'
            }, {
                type: 'features',
                header: 'trajectories.headers.track'
            }, {
                type: 'phenomenon',
                header: 'trajectories.headers.phenomenon'
            }, {
                type: 'dataset',
                header: 'trajectories.headers.dataset'
            }];
            $scope.phenomenonParams = [{
                type: 'phenomenon',
                header: 'trajectories.headers.phenomenon'
            }, {
                type: 'offering',
                header: 'trajectories.headers.offering'
            }, {
                type: 'features',
                header: 'trajectories.headers.track'
            }, {
                type: 'dataset',
                header: 'trajectories.headers.dataset'
            }];

            $scope.close = function() {
                $uibModalInstance.close();
            };
        }
    ])
    .controller('SwcProviderListCtrl', ['$scope', 'providerService',
        function($scope, providerService) {
            $scope.providerselected = null;

            $scope.providerList = providerService.getAllProviders('mobile');

            $scope.selectProvider = function(provider) {
                $scope.providerselected = provider;
            };
        }
    ])
    .directive('swcListSelectionMobile', [
        function() {
            return {
                restrict: 'E',
                templateUrl: 'templates/listSelection/list-selection.html',
                scope: {
                    parameters: '=',
                    provider: '='
                },
                controller: 'ListSelectionMobileCtrl'
            };
        }
    ])
    .controller('ListSelectionMobileCtrl', ['$scope', 'interfaceService', 'combinedSrvc',
        function($scope, interfaceService, combinedSrvc) {
            var url = $scope.provider.url;
            angular.forEach($scope.parameters, function(param, openedIdx) {
                $scope.$watch('parameters[' + openedIdx + '].isOpen', function(newVal, oldVal) {
                    if (newVal) {
                        $scope.selectedParameterIndex = openedIdx;
                        angular.forEach($scope.parameters, function(param, idx) {
                            if (idx > openedIdx) {
                                param.isDisabled = true;
                                delete param.selectedId;
                                delete param.items;
                            }
                            if (idx >= openedIdx) {
                                delete param.headerAddition;
                            }
                        });
                    }
                });
            });

            $scope.createParams = function() {
                var params = {
                    platformTypes: 'mobile'
                };
                angular.forEach($scope.parameters, function(parameter) {
                    if (parameter.selectedId) {
                        params[parameter.type] = parameter.selectedId;
                    }
                });
                return params;
            };

            $scope.getItems = function(currParam) {
                currParam.loading = 1;
                if (currParam.type === 'platform') {
                    interfaceService.getPlatforms(null, url, $scope.createParams())
                        .then(data => {
                            updateParams(currParam, data);
                        })
                        .catch(errorOnGetData);
                } else if (currParam.type === 'features') {
                    interfaceService.getFeatures(null, url, $scope.createParams())
                        .then(data => {
                            updateParams(currParam, data);
                        })
                        .catch(errorOnGetData);
                } else if (currParam.type === 'phenomenon') {
                    interfaceService.getPhenomena(null, url, $scope.createParams())
                        .then(data => {
                            updateParams(currParam, data);
                        })
                        .catch(errorOnGetData);
                } else if (currParam.type === 'dataset') {
                    interfaceService.getDatasets(null, url, $scope.createParams())
                        .then(data => {
                            updateParams(currParam, data);
                        })
                        .catch(errorOnGetData);
                } else if (currParam.type === 'offering') {
                    interfaceService.getOfferings(null, url, $scope.createParams())
                        .then(data => {
                            updateParams(currParam, data);
                        })
                        .catch(errorOnGetData);
                }
            };

            updateParams = function(currParam, data) {
                currParam.items = data;
                currParam.loading--;
            };

            errorOnGetData = function() {
                currParam.error = true;
            };

            $scope.openNext = function(idx) {
                $scope.parameters[idx].isDisabled = false;
                $scope.selectedParameterIndex = idx;
                $scope.parameters[idx].isOpen = true;
                $scope.getItems($scope.parameters[idx]);
            };

            $scope.openItem = function(item) {
                $scope.parameters[$scope.selectedParameterIndex].selectedId = item.id;
                $scope.parameters[$scope.selectedParameterIndex].headerAddition = item.label;
                if ($scope.selectedParameterIndex < $scope.parameters.length - 1) {
                    $scope.openNext($scope.selectedParameterIndex + 1);
                } else {
                    combinedSrvc.loadSeries(item.id, url);
                    $scope.$parent.modalInstance.close();
                }
            };

            $scope.openNext(0);
        }
    ]);
