(function() {
    'use strict';
    angular
        .module('stockadvisorApp')
        .factory('AlertType', AlertType);

    AlertType.$inject = ['$resource'];

    function AlertType ($resource) {
        var resourceUrl =  'api/alert-types/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
