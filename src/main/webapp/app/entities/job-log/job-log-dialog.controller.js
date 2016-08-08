(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('JobLogDialogController', JobLogDialogController);

    JobLogDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'JobLog'];

    function JobLogDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, JobLog) {
        var vm = this;

        vm.jobLog = entity;
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
            if (vm.jobLog.id !== null) {
                JobLog.update(vm.jobLog, onSaveSuccess, onSaveError);
            } else {
                JobLog.save(vm.jobLog, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('stockadvisorApp:jobLogUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.runDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
