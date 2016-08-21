(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('ManualEntryStockDeleteController',ManualEntryStockDeleteController);

    ManualEntryStockDeleteController.$inject = ['$uibModalInstance', 'entity', 'ManualEntryStock'];

    function ManualEntryStockDeleteController($uibModalInstance, entity, ManualEntryStock) {
        var vm = this;

        vm.manualEntryStock = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ManualEntryStock.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
