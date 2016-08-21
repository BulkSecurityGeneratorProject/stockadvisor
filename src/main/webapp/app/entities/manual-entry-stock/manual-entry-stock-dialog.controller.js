(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('ManualEntryStockDialogController', ManualEntryStockDialogController);

    ManualEntryStockDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ManualEntryStock'];

    function ManualEntryStockDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ManualEntryStock) {
        var vm = this;

        vm.manualEntryStock = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.manualEntryStock.id !== null) {
                ManualEntryStock.update(vm.manualEntryStock, onSaveSuccess, onSaveError);
            } else {
                ManualEntryStock.save(vm.manualEntryStock, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('stockadvisorApp:manualEntryStockUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.entryDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
