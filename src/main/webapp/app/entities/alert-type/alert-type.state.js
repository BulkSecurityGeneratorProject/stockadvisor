(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('alert-type', {
            parent: 'entity',
            url: '/alert-type',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'AlertTypes'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/alert-type/alert-types.html',
                    controller: 'AlertTypeController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('alert-type-detail', {
            parent: 'entity',
            url: '/alert-type/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'AlertType'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/alert-type/alert-type-detail.html',
                    controller: 'AlertTypeDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'AlertType', function($stateParams, AlertType) {
                    return AlertType.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'alert-type',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('alert-type-detail.edit', {
            parent: 'alert-type-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alert-type/alert-type-dialog.html',
                    controller: 'AlertTypeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AlertType', function(AlertType) {
                            return AlertType.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('alert-type.new', {
            parent: 'alert-type',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alert-type/alert-type-dialog.html',
                    controller: 'AlertTypeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                fqdn: null,
                                paramType: null,
                                paramValue: null,
                                description: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('alert-type', null, { reload: true });
                }, function() {
                    $state.go('alert-type');
                });
            }]
        })
        .state('alert-type.edit', {
            parent: 'alert-type',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alert-type/alert-type-dialog.html',
                    controller: 'AlertTypeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AlertType', function(AlertType) {
                            return AlertType.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('alert-type', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('alert-type.delete', {
            parent: 'alert-type',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alert-type/alert-type-delete-dialog.html',
                    controller: 'AlertTypeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['AlertType', function(AlertType) {
                            return AlertType.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('alert-type', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
