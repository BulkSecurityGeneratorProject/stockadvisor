(function() {
    'use strict';
    angular
        .module('stockadvisorApp')
        .factory('Watchlist', Watchlist);

    Watchlist.$inject = ['$resource', 'DateUtils'];

    function Watchlist ($resource, DateUtils) {
        var resourceUrl =  'api/watchlists/:id';

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
