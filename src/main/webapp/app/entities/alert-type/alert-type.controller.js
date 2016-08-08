(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('AlertTypeController', AlertTypeController);

    AlertTypeController.$inject = ['$scope', '$state', 'AlertType'];

    function AlertTypeController ($scope, $state, AlertType) {
        var vm = this;
        
        vm.alertTypes = [];

        loadAll();

        function loadAll() {
            AlertType.query(function(result) {
                vm.alertTypes = result;
            });
        }
    }
})();
