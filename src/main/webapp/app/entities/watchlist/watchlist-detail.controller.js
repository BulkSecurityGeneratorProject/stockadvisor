(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('WatchlistDetailController', WatchlistDetailController);

    WatchlistDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Watchlist', 'AlertHistory', 'Source', 'AlertType'];

    function WatchlistDetailController($scope, $rootScope, $stateParams, previousState, entity, Watchlist, AlertHistory, Source, AlertType) {
        var vm = this;

        vm.watchlist = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('stockadvisorApp:watchlistUpdate', function(event, result) {
            vm.watchlist = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
