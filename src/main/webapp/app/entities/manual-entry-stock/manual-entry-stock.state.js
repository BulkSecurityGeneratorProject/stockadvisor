(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('manual-entry-stock', {
            parent: 'entity',
            url: '/manual-entry-stock',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ManualEntryStocks'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/manual-entry-stock/manual-entry-stocks.html',
                    controller: 'ManualEntryStockController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('manual-entry-stock-detail', {
            parent: 'entity',
            url: '/manual-entry-stock/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ManualEntryStock'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/manual-entry-stock/manual-entry-stock-detail.html',
                    controller: 'ManualEntryStockDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'ManualEntryStock', function($stateParams, ManualEntryStock) {
                    return ManualEntryStock.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'manual-entry-stock',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('manual-entry-stock-detail.edit', {
            parent: 'manual-entry-stock-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/manual-entry-stock/manual-entry-stock-dialog.html',
                    controller: 'ManualEntryStockDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ManualEntryStock', function(ManualEntryStock) {
                            return ManualEntryStock.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('manual-entry-stock.new', {
            parent: 'manual-entry-stock',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/manual-entry-stock/manual-entry-stock-dialog.html',
                    controller: 'ManualEntryStockDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                symbols: null,
                                entryDate: null,
                                processed: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('manual-entry-stock', null, { reload: 'manual-entry-stock' });
                }, function() {
                    $state.go('manual-entry-stock');
                });
            }]
        })
        .state('manual-entry-stock.edit', {
            parent: 'manual-entry-stock',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/manual-entry-stock/manual-entry-stock-dialog.html',
                    controller: 'ManualEntryStockDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ManualEntryStock', function(ManualEntryStock) {
                            return ManualEntryStock.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('manual-entry-stock', null, { reload: 'manual-entry-stock' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('manual-entry-stock.delete', {
            parent: 'manual-entry-stock',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/manual-entry-stock/manual-entry-stock-delete-dialog.html',
                    controller: 'ManualEntryStockDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ManualEntryStock', function(ManualEntryStock) {
                            return ManualEntryStock.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('manual-entry-stock', null, { reload: 'manual-entry-stock' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
