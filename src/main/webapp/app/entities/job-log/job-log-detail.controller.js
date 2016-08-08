(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('JobLogDetailController', JobLogDetailController);

    JobLogDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'JobLog'];

    function JobLogDetailController($scope, $rootScope, $stateParams, previousState, entity, JobLog) {
        var vm = this;

        vm.jobLog = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('stockadvisorApp:jobLogUpdate', function(event, result) {
            vm.jobLog = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
