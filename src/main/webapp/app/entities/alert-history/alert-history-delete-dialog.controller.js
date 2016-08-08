(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('AlertHistoryDeleteController',AlertHistoryDeleteController);

    AlertHistoryDeleteController.$inject = ['$uibModalInstance', 'entity', 'AlertHistory'];

    function AlertHistoryDeleteController($uibModalInstance, entity, AlertHistory) {
        var vm = this;

        vm.alertHistory = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            AlertHistory.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
