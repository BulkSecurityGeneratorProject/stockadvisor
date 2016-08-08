(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('AlertHistoryController', AlertHistoryController);

    AlertHistoryController.$inject = ['$scope', '$state', 'AlertHistory'];

    function AlertHistoryController ($scope, $state, AlertHistory) {
        var vm = this;
        
        vm.alertHistories = [];

        loadAll();

        function loadAll() {
            AlertHistory.query(function(result) {
                vm.alertHistories = result;
            });
        }
    }
})();
