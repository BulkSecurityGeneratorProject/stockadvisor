(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('alert-history', {
            parent: 'entity',
            url: '/alert-history',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'AlertHistories'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/alert-history/alert-histories.html',
                    controller: 'AlertHistoryController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('alert-history-detail', {
            parent: 'entity',
            url: '/alert-history/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'AlertHistory'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/alert-history/alert-history-detail.html',
                    controller: 'AlertHistoryDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'AlertHistory', function($stateParams, AlertHistory) {
                    return AlertHistory.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'alert-history',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('alert-history-detail.edit', {
            parent: 'alert-history-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alert-history/alert-history-dialog.html',
                    controller: 'AlertHistoryDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AlertHistory', function(AlertHistory) {
                            return AlertHistory.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('alert-history.new', {
            parent: 'alert-history',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alert-history/alert-history-dialog.html',
                    controller: 'AlertHistoryDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                triggeredAt: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('alert-history', null, { reload: true });
                }, function() {
                    $state.go('alert-history');
                });
            }]
        })
        .state('alert-history.edit', {
            parent: 'alert-history',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alert-history/alert-history-dialog.html',
                    controller: 'AlertHistoryDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AlertHistory', function(AlertHistory) {
                            return AlertHistory.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('alert-history', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('alert-history.delete', {
            parent: 'alert-history',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alert-history/alert-history-delete-dialog.html',
                    controller: 'AlertHistoryDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['AlertHistory', function(AlertHistory) {
                            return AlertHistory.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('alert-history', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
