(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('source', {
            parent: 'entity',
            url: '/source',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Sources'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/source/sources.html',
                    controller: 'SourceController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('source-detail', {
            parent: 'entity',
            url: '/source/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Source'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/source/source-detail.html',
                    controller: 'SourceDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Source', function($stateParams, Source) {
                    return Source.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'source',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('source-detail.edit', {
            parent: 'source-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/source/source-dialog.html',
                    controller: 'SourceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Source', function(Source) {
                            return Source.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('source.new', {
            parent: 'source',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/source/source-dialog.html',
                    controller: 'SourceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                description: null,
                                link: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('source', null, { reload: true });
                }, function() {
                    $state.go('source');
                });
            }]
        })
        .state('source.edit', {
            parent: 'source',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/source/source-dialog.html',
                    controller: 'SourceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Source', function(Source) {
                            return Source.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('source', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('source.delete', {
            parent: 'source',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/source/source-delete-dialog.html',
                    controller: 'SourceDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Source', function(Source) {
                            return Source.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('source', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
