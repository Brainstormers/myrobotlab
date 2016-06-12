angular.module('mrlapp.service.Mpu6050Gui', [])
.controller('Mpu6050GuiCtrl', ['$log', '$scope', 'mrl', function($log, $scope, mrl) {
    $log.info('Mpu6050GuiCtrl');
    var _self = this;
    var msg = this.msg;
    
    // init
    //$scope.controller = '';
    $scope.controllerName = '';
    $scope.controllers = [];    
    
    // GOOD TEMPLATE TO FOLLOW
    this.updateState = function(service) {
        $scope.service = service;
        $scope.controllerName = service.controllerName;
        $scope.isAttached = service.isAttached;
        $scope.controllers = service.controllers;
        $scope.accelGX = service.accelGX;
        $scope.accelGY = service.accelGY;
        $scope.accelGZ = service.accelGZ;
        $scope.temperatureC = service.temperatureC;
        $scope.gyroDegreeX = service.gyroDegreeX;
        $scope.gyroDegreeY = service.gyroDegreeY;
        $scope.gyroDegreeZ = service.gyroDegreeZ;
    }
    ;
    
    _self.updateState($scope.service);
    
    this.onMsg = function(inMsg) {
        var data = inMsg.data[0];
        switch (inMsg.method) {
        case 'onState':
            _self.updateState(data);
            $scope.$apply();
            break;
        default:
            $log.info("ERROR - unhandled method " + $scope.name + " Method " + inMsg.method);
            break;
        }
        ;
    
    }
    ;
    
    $scope.setControllerName = function(name) {
        $scope.controllerName = name;
    }
    
    // regrettably the onMethodMap dynamic
    // generation of methods failed on this overloaded
    // sweep method - there are several overloads in the
    // Java service - although msg.sweep() was tried for ng-click
    // for some reason Js resolved msg.sweep(null, null, null, null) :P

    msg.subscribe(this);
}
]);
