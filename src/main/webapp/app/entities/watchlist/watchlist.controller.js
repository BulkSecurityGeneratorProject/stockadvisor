(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('WatchlistController', WatchlistController);

    WatchlistController.$inject = ['$scope', '$state', 'Watchlist'];

    function WatchlistController ($scope, $state, Watchlist) {
        var vm = this;
        
        vm.watchlists = [];

        loadAll();

        function loadAll() {
            Watchlist.query(function(result) {
                vm.watchlists = result;
            });
        }
    }
})();
