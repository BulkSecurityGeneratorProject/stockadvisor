(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('watchlist', {
            parent: 'entity',
            url: '/watchlist',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Watchlists'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/watchlist/watchlists.html',
                    controller: 'WatchlistController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('watchlist-detail', {
            parent: 'entity',
            url: '/watchlist/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Watchlist'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/watchlist/watchlist-detail.html',
                    controller: 'WatchlistDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Watchlist', function($stateParams, Watchlist) {
                    return Watchlist.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'watchlist',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('watchlist-detail.edit', {
            parent: 'watchlist-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/watchlist/watchlist-dialog.html',
                    controller: 'WatchlistDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Watchlist', function(Watchlist) {
                            return Watchlist.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('watchlist.new', {
            parent: 'watchlist',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/watchlist/watchlist-dialog.html',
                    controller: 'WatchlistDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                symbol: null,
                                entryPrice: null,
                                entryDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('watchlist', null, { reload: 'watchlist' });
                }, function() {
                    $state.go('watchlist');
                });
            }]
        })
        .state('watchlist.edit', {
            parent: 'watchlist',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/watchlist/watchlist-dialog.html',
                    controller: 'WatchlistDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Watchlist', function(Watchlist) {
                            return Watchlist.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('watchlist', null, { reload: 'watchlist' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('watchlist.delete', {
            parent: 'watchlist',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/watchlist/watchlist-delete-dialog.html',
                    controller: 'WatchlistDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Watchlist', function(Watchlist) {
                            return Watchlist.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('watchlist', null, { reload: 'watchlist' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
