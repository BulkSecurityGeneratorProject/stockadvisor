(function() {
    'use strict';

    angular
        .module('stockadvisorApp')
        .controller('SourceDetailController', SourceDetailController);

    SourceDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Source', 'Watchlist'];

    function SourceDetailController($scope, $rootScope, $stateParams, previousState, entity, Source, Watchlist) {
        var vm = this;

        vm.source = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('stockadvisorApp:sourceUpdate', function(event, result) {
            vm.source = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
