angular.module('mrlapp.service.I2CMuxGui', [])
.controller('I2CMuxGuiCtrl', ['$log', '$scope', 'mrl', function($log, $scope, mrl) {
    $log.info('I2CMuxGuiCtrl');
    var _self = this;
    var msg = this.msg;
    
    // init
    $scope.controllerName = '';
    $scope.controllers = [];    
    
    // GOOD TEMPLATE TO FOLLOW
    this.updateState = function(service) {
        $scope.service = service;
        $scope.controllerName = service.controllerName;
        $scope.isAttached = service.isAttached;
        $scope.controllers = service.controllers;
        $scope.muxAddressList = service.muxAddressList;
        $scope.muxAddress = service.muxAddress;
        $scope.muxBusList = service.muxBusList;
        $scope.muxBus = service.muxBus;
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
    
    $scope.SetMuxAddress = function(address) {
        $scope.muxAddress = address;
    }
    
    $scope.SetMuxBus = function(bus) {
        $scope.muxBus = bus;
    }
    
    // regrettably the onMethodMap dynamic
    // generation of methods failed on this overloaded
    // sweep method - there are several overloads in the
    // Java service - although msg.sweep() was tried for ng-click
    // for some reason Js resolved msg.sweep(null, null, null, null) :P

    msg.subscribe(this);
}
]);
