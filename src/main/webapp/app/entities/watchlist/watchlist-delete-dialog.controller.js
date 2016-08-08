(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('WatchlistDeleteController',WatchlistDeleteController);

    WatchlistDeleteController.$inject = ['$uibModalInstance', 'entity', 'Watchlist'];

    function WatchlistDeleteController($uibModalInstance, entity, Watchlist) {
        var vm = this;

        vm.watchlist = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Watchlist.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
