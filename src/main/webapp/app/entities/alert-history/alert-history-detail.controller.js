(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('AlertHistoryDetailController', AlertHistoryDetailController);

    AlertHistoryDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'AlertHistory', 'Watchlist'];

    function AlertHistoryDetailController($scope, $rootScope, $stateParams, previousState, entity, AlertHistory, Watchlist) {
        var vm = this;

        vm.alertHistory = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('stockadvisorApp:alertHistoryUpdate', function(event, result) {
            vm.alertHistory = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
