(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('AlertTypeDialogController', AlertTypeDialogController);

    AlertTypeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'AlertType', 'Watchlist'];

    function AlertTypeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, AlertType, Watchlist) {
        var vm = this;

        vm.alertType = entity;
        vm.clear = clear;
        vm.save = save;
        vm.watchlists = Watchlist.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.alertType.id !== null) {
                AlertType.update(vm.alertType, onSaveSuccess, onSaveError);
            } else {
                AlertType.save(vm.alertType, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('stockadvisorApp:alertTypeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
