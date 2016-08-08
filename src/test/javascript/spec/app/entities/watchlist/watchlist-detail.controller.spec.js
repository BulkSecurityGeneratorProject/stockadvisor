'use strict';

describe('Controller Tests', function() {

    describe('Watchlist Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockWatchlist, MockAlertHistory, MockSource, MockAlertType;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockWatchlist = jasmine.createSpy('MockWatchlist');
            MockAlertHistory = jasmine.createSpy('MockAlertHistory');
            MockSource = jasmine.createSpy('MockSource');
            MockAlertType = jasmine.createSpy('MockAlertType');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Watchlist': MockWatchlist,
                'AlertHistory': MockAlertHistory,
                'Source': MockSource,
                'AlertType': MockAlertType
            };
            createController = function() {
                $injector.get('$controller')("WatchlistDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'stockadvisorApp:watchlistUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
