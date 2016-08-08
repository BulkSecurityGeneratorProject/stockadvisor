(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('JobLogController', JobLogController);

    JobLogController.$inject = ['$scope', '$state', 'JobLog'];

    function JobLogController ($scope, $state, JobLog) {
        var vm = this;
        
        vm.jobLogs = [];

        loadAll();

        function loadAll() {
            JobLog.query(function(result) {
                vm.jobLogs = result;
            });
        }
    }
})();
