/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
    'n52.core.barChart',
    'n52.core.base',
    'n52.core.dataLoading',
    'n52.core.diagram',
    'n52.core.exportTs',
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
    'n52.core.phenomena',
    'n52.core.provider',
    'n52.core.userSettings',
    'n52.core.startup',
    'n52.core.style',
    'n52.core.table',
    'n52.core.timeUi',
    'n52.core.translate',
    'n52.client.navigation',
    'n52.client.map'
]);

mainApp.config(['$routeProvider',
    function($routeProvider) {
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
            // .when('/favorite', {
            //     templateUrl: 'templates/views/favoriteView.html',
            //     name: 'navigation.favorite',
            //     reloadOnSearch: false
            // })
            // .when('/map/provider', {
            //     name: 'navigation.provider',
            //     modal: {
            //         controller: 'SwcProviderListModalCtrl',
            //         templateUrl: 'templates/map/provider-list-modal.html'
            //     },
            //     reloadOnSearch: false
            // })
            // .when('/diagram/listSelection', {
            //     name: 'navigation.listSelection',
            //     modal: {
            //         controller: 'ModalWindowCtrl',
            //         templateUrl: 'templates/listSelection/modal-list-selection.html'
            //     },
            //     reloadOnSearch: false
            // })
            .when('/diagram/settings', {
                name: 'navigation.settings',
                modal: {
                    controller: 'SwcUserSettingsWindowCtrl',
                    templateUrl: 'templates/settings/user-settings-modal.html'
                },
                reloadOnSearch: false
            })
            .otherwise({
                redirectTo: '/'
            });
    }
]);

mainApp.config(['$translateProvider', 'settingsServiceProvider', '$locationProvider',
    function($translateProvider, settingsServiceProvider, $locationProvider) {
        $translateProvider.useStaticFilesLoader({
            prefix: 'i18n/',
            suffix: '.json'
        });
        $locationProvider.hashPrefix('');
        var suppLang = [];
        angular.forEach(settingsServiceProvider.$get().supportedLanguages, function(lang) {
            suppLang.push(lang.code);
        });
        $translateProvider.registerAvailableLanguageKeys(suppLang);
        $translateProvider.determinePreferredLanguage();
        if ($translateProvider.preferredLanguage() === '' ||
            suppLang.indexOf($translateProvider.preferredLanguage()) === -1) {
            $translateProvider.preferredLanguage('en');
        }
        $translateProvider.useSanitizeValueStrategy(null);
    }
]);

mainApp.filter('objectCount', function() {
    return function(item) {
        if (item) {
            return Object.keys(item).length;
        } else {
            return 0;
        }
    };
});

mainApp.config(["$provide", function($provide) {
    // Use the `decorator` solution to substitute or attach behaviors to
    // original service instance; @see angular-mocks for more examples....

    $provide.decorator('$log', ["$delegate", function($delegate) {
        // Save the original $log.debug()
        var debugFn = $delegate.debug;

        $delegate.info = function() {
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
angular.injector(["ng"]).get("$q").all([fetchConfig(), fetchTemplates()]).then(bootstrapApp);

function fetchConfig() {
    return angular.injector(["ng"]).get("$http").get("settings.json").then(function(response) {
        mainApp.constant("config", response.data);
    });
}

function fetchTemplates() {
    return angular.injector(["ng"]).get("$http").get('templates/templates.json').then(response => {
        mainApp.constant("templatesMapping", response.data);
    });
}

function bootstrapApp() {
    angular.element(document).ready(function() {
        var injector = angular.bootstrap(document, ["jsClient"], {
            strictDi: true
        });
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

angular.module('n52.core.map')
    .service('ecmwfPlatformPresenter', ['$uibModal', 'mapService',
        function($uibModal, mapService) {
            this.presentPlatform = function(platform) {
                $uibModal.open({
                    animation: true,
                    templateUrl: 'templates/ecmwf/stationary-insitu-ecmwf.html',
                    resolve: {
                        selection: function() {
                            var url = platform.url;
                            var phenomenonId;
                            if (mapService.map.selectedPhenomenon) {
                                angular.forEach(mapService.map.selectedPhenomenon.provider, function(provider) {
                                    if (url === provider.url)
                                        phenomenonId = provider.phenomenonID;
                                });
                            }
                            return {
                                id: platform.id,
                                phenomenonId: phenomenonId,
                                url: url
                            };
                        }
                    },
                    controller: 'SwcModalEcmwfCtrl'
                });
            };
        }
    ])
    .controller('SwcModalEcmwfCtrl', [
        '$scope',
        '$uibModalInstance',
        'selection',
        'seriesApiInterface',
        'serviceFinder',
        function(
            $scope,
            $uibModalInstance,
            selection,
            seriesApiInterface,
            serviceFinder
        ) {
            $scope.serviceUrl = selection.url;

            seriesApiInterface.getPlatforms(selection.id, selection.url)
                .then((platform) => {
                    $scope.platform = platform;
                    seriesApiInterface.getPlatformExtras(selection.id, selection.url, {
                            fields: 'parents'
                        })
                        .then((result) => {
                            $scope.parentProcedures = result.parents.procedures;
                        });
                    if (platform.datasets.length > 0) {
                        seriesApiInterface.getDatasetExtras(platform.datasets[0].id, selection.url, {
                                fields: 'resultTimes'
                            })
                            .then((result) => {
                                $scope.resultTimes = result.resultTimes;
                            });
                    }
                });

            $scope.close = function() {
                $uibModalInstance.close();
            };

            $scope.selectProcedure = function(item) {
                $scope.selectedProcedure = item;
            };

            $scope.selectResultTime = function(item) {
                $scope.selectedResultTime = item;
                $scope.datasets = [];
                seriesApiInterface.getDatasets(null, selection.url, {
                    platforms: selection.id,
                    procedures: $scope.selectedProcedure.id
                }).then((datasets) => {
                    $scope.datasets = datasets;
                    $scope.datasets.forEach(entry => {
                        seriesApiInterface.getDatasets(entry.id, selection.url, {
                                resultTime: $scope.selectedResultTime
                            })
                            .then(dataset => {
                                dataset.selected = true;
                                angular.extend(entry, dataset);
                            });
                    });
                });
            };

            $scope.toggled = function() {
                var allSelected = true;
                angular.forEach($scope.platform.datasets, function(dataset) {
                    if (!dataset.selected)
                        allSelected = false;
                });
                $scope.isAllSelected = allSelected;
            };

            $scope.presentSelection = function() {
                angular.forEach($scope.datasets, (dataset) => {
                    dataset.filter = {
                        resultTime: $scope.selectedResultTime
                    };
                    if (dataset.selected && (!selection.phenomenonId || dataset.seriesParameters.phenomenon.id === selection.phenomenonId)) {
                        serviceFinder
                            .getDatasetPresenter(dataset.valueType, dataset.seriesParameters.platform.platformType, selection.url)
                            .presentDataset(dataset, selection.url);
                    }
                });
                $scope.close();
            };
        }
    ])
    .component('swcEcmwfProcedureSelection', {
        bindings: {
            items: '<',
            onSelect: '&'
        },
        templateUrl: 'n52.ecmwf.map.procedure-selection',
        controller: ['seriesApiInterface', 'utils',
            function(seriesApiInterface, utils) {
                this.$onInit = function() {};

                this.onChange = function(item) {
                    this.onSelect({
                        item: this.selection
                    });
                };
            }
        ]
    })
    .component('swcEcmwfResultTimeSelection', {
        bindings: {
            items: '<',
            onSelect: '&'
        },
        templateUrl: 'n52.ecmwf.map.result-time-selection',
        controller: [
            function() {
                this.onChange = function(item) {
                    this.onSelect({
                        item: this.selection
                    });
                };
            }
        ]
    })
    .component('swcEcmwfLegend', {
        bindings: {
            items: '<'
        },
        templateUrl: 'n52.ecmwf.legend',
        controller: ['timeseriesService', 'seriesApiInterface',
            function(timeseriesService, seriesApiInterface) {

                this.$doCheck = function() {
                    if (Object.keys(this.items).length !== this.previousCount) {
                        this.createEntries();
                        this.previousCount = Object.keys(this.items).length;
                    }
                };

                this.createEntries = function() {
                    this.entries = [];
                    for (var key in this.items) {
                        if (this.items.hasOwnProperty(key)) {
                            // if (this.items[key].apiUrl.startsWith('http://192.168.52.128:8080/52n-sos-webapp/api/')) {
                                var platformID = this.items[key].seriesParameters.platform.id;
                                var resultTime = this.items[key].filter.resultTime;
                                this.addToEntries(this.items[key], platformID, resultTime);
                            // } else {
                            //     this.entries.push(this.items[key]);
                            // }
                        }
                    }
                };

                this.addToEntries = function(item, platformID, resultTime) {
                    seriesApiInterface.getProcedures(item.seriesParameters.procedure.id, item.apiUrl)
                        .then((procedure) => {
                            var parentProcedureLabel = procedure.parents[0].label;
                            // find entry
                            var added = false;
                            this.entries.forEach((entry) => {
                                if (entry.ecmwfGroup &&
                                    entry.platformID === platformID &&
                                    entry.resultTime === resultTime &&
                                    entry.parentProcedureLabel === parentProcedureLabel) {
                                    entry.items.push(item);
                                    added = true;
                                }
                            });
                            // create new entry
                            if (!added) {
                                this.entries.push({
                                    ecmwfGroup: true,
                                    platformID: platformID,
                                    resultTime: resultTime,
                                    parentProcedureLabel: parentProcedureLabel,
                                    items: [item]
                                });
                            }
                        });
                };
            }
        ]
    })
    .component('swcEcmwfLegendEntry', {
        bindings: {
            item: '<'
        },
        templateUrl: 'n52.ecmwf.legend-entry',
        controller: ['styleService', 'timeseriesService', 'locateStationService', '$location',
            function(styleService, timeseriesService, locateStationService, $location) {

                this.toggleSelection = function() {
                    this.item.items.forEach((entry) => {
                        styleService.toggleSelection(entry);
                    });
                };

                this.toggleVisibility = function() {
                    this.item.items.forEach((entry) => {
                        styleService.toggleTimeseriesVisibility(entry);
                    });
                };

                this.removeAll = function() {
                    this.item.items.forEach((entry) => {
                        timeseriesService.removeTimeseries(entry.internalId);
                    });
                };

                this.showInMap = function() {
                    locateStationService.showPlatform('mapService', this.item.items[0]);
                    $location.url('/map');
                };

                this.toggled = function(entry) {
                    styleService.triggerStyleUpdate(entry);
                };
            }
        ]
    })
    .config(['$provide',
        function($provide) {
            $provide.decorator('utils', ['$delegate', '$q', '$http',
                function($delegate, $q, $http) {
                    $delegate.oldCreateInternalId = $delegate.createInternalId;
                    $delegate.createInternalId = function(ts) {
                        if (ts.filter && ts.filter.resultTime) {
                            return $delegate.oldCreateInternalId(ts) + ts.filter.resultTime;
                        }
                        return $delegate.oldCreateInternalId(ts);
                    };
                    return $delegate;
                }
            ]);
        }
    ]);

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
                    $delegate.oldSelectProvider = $delegate.selectProvider;
                    $delegate.selectProvider = function(selection) {
                        $delegate.oldSelectProvider(selection);
                        $location.url('/map');
                    };
                    return $delegate;
                }
            ]);
        }
    ]);
