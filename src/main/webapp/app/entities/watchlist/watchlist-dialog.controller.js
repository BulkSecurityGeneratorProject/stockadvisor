(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('WatchlistDialogController', WatchlistDialogController);

    WatchlistDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Watchlist', 'AlertHistory', 'Source', 'AlertType'];

    function WatchlistDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Watchlist, AlertHistory, Source, AlertType) {
        var vm = this;

        vm.watchlist = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.alerthistories = AlertHistory.query();
        vm.sources = Source.query();
        vm.alerttypes = AlertType.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.watchlist.id !== null) {
                Watchlist.update(vm.watchlist, onSaveSuccess, onSaveError);
            } else {
                Watchlist.save(vm.watchlist, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('stockadvisorApp:watchlistUpdate', result);
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
