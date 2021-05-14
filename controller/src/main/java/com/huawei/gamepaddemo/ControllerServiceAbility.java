package com.huawei.gamepaddemo;

import com.huawei.gamepaddemo.controller.GameRemote;
import com.huawei.gamepaddemo.controller.LogUtil;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.rpc.IRemoteObject;

public class ControllerServiceAbility extends Ability {
    private static final String TAG = ControllerServiceAbility.class.getName();
    private final GameRemote remote = new GameRemote(this);

    @Override
    public void onStart(Intent intent) {
        LogUtil.debug(TAG, "ServiceAbility::onStart");
        super.onStart(intent);
    }

    @Override
    public void onBackground() {
        LogUtil.debug(TAG, "ServiceAbility::onBackground");
        super.onBackground();
    }

    @Override
    public void onStop() {
        LogUtil.debug(TAG, "ServiceAbility::onStop");
        super.onStop();
    }

    @Override
    public void onCommand(Intent intent, boolean restart, int startId) {
    }

    @Override
    public IRemoteObject onConnect(Intent intent) {
        return remote.asObject();
    }

    @Override
    public void onDisconnect(Intent intent) {
    }
}