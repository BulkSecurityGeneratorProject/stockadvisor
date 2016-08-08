(function() {
    'use strict';
    angular
        .module('stockadvisorApp')
        .factory('AlertHistory', AlertHistory);

    AlertHistory.$inject = ['$resource', 'DateUtils'];

    function AlertHistory ($resource, DateUtils) {
        var resourceUrl =  'api/alert-histories/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.triggeredAt = DateUtils.convertDateTimeFromServer(data.triggeredAt);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
