(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('SourceDialogController', SourceDialogController);

    SourceDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Source', 'Watchlist'];

    function SourceDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Source, Watchlist) {
        var vm = this;

        vm.source = entity;
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
            if (vm.source.id !== null) {
                Source.update(vm.source, onSaveSuccess, onSaveError);
            } else {
                Source.save(vm.source, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('stockadvisorApp:sourceUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
