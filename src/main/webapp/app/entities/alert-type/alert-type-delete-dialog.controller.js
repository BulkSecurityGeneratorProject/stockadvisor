(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('AlertTypeDeleteController',AlertTypeDeleteController);

    AlertTypeDeleteController.$inject = ['$uibModalInstance', 'entity', 'AlertType'];

    function AlertTypeDeleteController($uibModalInstance, entity, AlertType) {
        var vm = this;

        vm.alertType = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            AlertType.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
