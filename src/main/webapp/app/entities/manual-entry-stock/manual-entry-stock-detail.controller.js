(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('ManualEntryStockDetailController', ManualEntryStockDetailController);

    ManualEntryStockDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ManualEntryStock'];

    function ManualEntryStockDetailController($scope, $rootScope, $stateParams, previousState, entity, ManualEntryStock) {
        var vm = this;

        vm.manualEntryStock = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('stockadvisorApp:manualEntryStockUpdate', function(event, result) {
            vm.manualEntryStock = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
