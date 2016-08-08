(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('AlertHistoryDialogController', AlertHistoryDialogController);

    AlertHistoryDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'AlertHistory', 'Watchlist'];

    function AlertHistoryDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, AlertHistory, Watchlist) {
        var vm = this;

        vm.alertHistory = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
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
            if (vm.alertHistory.id !== null) {
                AlertHistory.update(vm.alertHistory, onSaveSuccess, onSaveError);
            } else {
                AlertHistory.save(vm.alertHistory, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('stockadvisorApp:alertHistoryUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.triggeredAt = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
