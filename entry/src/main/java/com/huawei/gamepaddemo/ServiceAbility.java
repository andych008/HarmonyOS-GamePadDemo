package com.huawei.gamepaddemo;

import com.huawei.gamepaddemo.controller.ControllerRemote;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.rpc.IRemoteObject;

public class ServiceAbility extends Ability {
    private final ControllerRemote controllerRemote = new ControllerRemote("Controller");

    @Override
    public IRemoteObject onConnect(Intent intent) {
        return controllerRemote.asObject();
    }
}