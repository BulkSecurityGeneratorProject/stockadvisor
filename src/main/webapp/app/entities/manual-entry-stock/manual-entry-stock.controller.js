(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('ManualEntryStockController', ManualEntryStockController);

    ManualEntryStockController.$inject = ['$scope', '$state', 'ManualEntryStock'];

    function ManualEntryStockController ($scope, $state, ManualEntryStock) {
        var vm = this;
        
        vm.manualEntryStocks = [];

        loadAll();

        function loadAll() {
            ManualEntryStock.query(function(result) {
                vm.manualEntryStocks = result;
            });
        }
    }
})();
