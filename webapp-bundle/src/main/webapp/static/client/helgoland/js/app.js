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

mainApp.config(['$routeProvider', function($routeProvider) {
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
        .otherwise({
            redirectTo: '/'
        });
}]);

mainApp.config(['$translateProvider', 'settingsServiceProvider', function($translateProvider, settingsServiceProvider) {
    $translateProvider.useStaticFilesLoader({
        prefix: 'i18n/',
        suffix: '.json'
    });
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
}]);

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
fetchData().then(bootstrapApp);

function fetchData() {
    var initInjector = angular.injector(["ng"]);
    var $http = initInjector.get("$http");
    return $http.get("settings.json").then(function(response) {
        mainApp.constant("config", response.data);
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
