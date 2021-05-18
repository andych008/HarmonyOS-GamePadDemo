package com.huawei.gamepaddemo.controller;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.IAbilityConnection;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.bundle.ElementName;
import ohos.rpc.*;

import java.util.HashMap;

public class ResultRemote extends ResultStub {
    private final Ability ability;
    private final HashMap<String, IAbilityConnection> connectionMap;
    private final HashMap<String, ControllerRemoteProxy> controllerRemoteProxyMap;

    interface ConnectionDoneCallback {
        void onConnectionDone(ControllerRemoteProxy controllerRemoteProxy);
    }

    public ResultRemote(Ability ability) {
        super("Result remote");
        this.ability = ability;
        connectionMap = new HashMap<>();
        controllerRemoteProxyMap = new HashMap<>();
    }

    @Override
    public IRemoteObject asObject() {
        return this;
    }

    @Override
    public void sendLocation(String deviceId, float x, float y) {
        if (!controllerRemoteProxyMap.containsKey(deviceId)) {
            connectToAbility(deviceId, controllerRemoteProxy -> controllerRemoteProxy.sendLocation(x, y));
        } else {
            ControllerRemoteProxy controllerRemoteProxy = controllerRemoteProxyMap.get(deviceId);
            if (controllerRemoteProxy != null) {
                controllerRemoteProxy.sendLocation(x, y);
            }
        }
    }

    @Override
    public void disconnect(String deviceId) {
        ControllerRemoteProxy controllerRemoteProxy = controllerRemoteProxyMap.get(deviceId);
        if (controllerRemoteProxy != null) {
            controllerRemoteProxy.terminate();
        }
        IAbilityConnection connection = connectionMap.getOrDefault(deviceId, null);
        if (connection != null) {
            ability.disconnectAbility(connection);
        }
    }

    private void connectToAbility(String deviceId, ConnectionDoneCallback callback) {
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId(deviceId)
                .withBundleName(Const.BUNDLE_NAME)
                .withAbilityName(Const.ABILITY_NAME)
                .withFlags(Intent.FLAG_ABILITYSLICE_MULTI_DEVICE)
                .build();
        intent.setOperation(operation);
        IAbilityConnection connection = new IAbilityConnection() {
            @Override
            public void onAbilityConnectDone(ElementName elementName, IRemoteObject remoteObject, int resultCode) {
                connectionMap.put(deviceId, this);
                ControllerRemoteProxy controllerRemoteProxy = new ControllerRemoteProxy(remoteObject);
                controllerRemoteProxyMap.put(deviceId, controllerRemoteProxy);
                callback.onConnectionDone(controllerRemoteProxy);
            }

            @Override
            public void onAbilityDisconnectDone(ElementName elementName, int resultCode) {
                connectionMap.remove(deviceId);
                controllerRemoteProxyMap.remove(deviceId);
            }
        };
        ability.connectAbility(intent, connection);
    }

}
