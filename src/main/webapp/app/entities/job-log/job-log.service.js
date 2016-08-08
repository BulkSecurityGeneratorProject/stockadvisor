(function() {
    'use strict';
    angular
        .module('stockadvisorApp')
        .factory('JobLog', JobLog);

    JobLog.$inject = ['$resource', 'DateUtils'];

    function JobLog ($resource, DateUtils) {
        var resourceUrl =  'api/job-logs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.runDate = DateUtils.convertDateTimeFromServer(data.runDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
