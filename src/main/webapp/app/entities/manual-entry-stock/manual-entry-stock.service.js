(function() {
    'use strict';
    angular
        .module('stockadvisorApp')
        .factory('ManualEntryStock', ManualEntryStock);

    ManualEntryStock.$inject = ['$resource', 'DateUtils'];

    function ManualEntryStock ($resource, DateUtils) {
        var resourceUrl =  'api/manual-entry-stocks/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.entryDate = DateUtils.convertLocalDateFromServer(data.entryDate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.entryDate = DateUtils.convertLocalDateToServer(data.entryDate);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.entryDate = DateUtils.convertLocalDateToServer(data.entryDate);
                    return angular.toJson(data);
                }
            }
        });
    }
})();
