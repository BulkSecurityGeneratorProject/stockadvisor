(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('AlertTypeDetailController', AlertTypeDetailController);

    AlertTypeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'AlertType', 'Watchlist'];

    function AlertTypeDetailController($scope, $rootScope, $stateParams, previousState, entity, AlertType, Watchlist) {
        var vm = this;

        vm.alertType = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('stockadvisorApp:alertTypeUpdate', function(event, result) {
            vm.alertType = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
