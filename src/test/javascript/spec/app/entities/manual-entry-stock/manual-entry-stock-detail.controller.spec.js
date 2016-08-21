'use strict';

describe('Controller Tests', function() {

    describe('ManualEntryStock Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockManualEntryStock;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockManualEntryStock = jasmine.createSpy('MockManualEntryStock');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'ManualEntryStock': MockManualEntryStock
            };
            createController = function() {
                $injector.get('$controller')("ManualEntryStockDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'stockadvisorApp:manualEntryStockUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
