package com.huawei.gamepaddemo.controller;

import jp.huawei.a2hdemo.GameServiceStub;
import jp.huawei.a2hdemo.IGameInterface;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.IAbilityConnection;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.bundle.ElementName;
import ohos.rpc.*;

public class GameRemote extends RemoteObject implements IRemoteBroker {
    private final String TAG = GameRemote.class.getName();
    static final int REMOTE_COMMAND = 0;
    private final Ability ability;
    private boolean isConnected;
    private IGameInterface remoteService;
    private String firstDeviceId;

    private final IAbilityConnection connection = new IAbilityConnection() {
        @Override
        public void onAbilityConnectDone(ElementName elementName, IRemoteObject remote, int resultCode) {
            remoteService = GameServiceStub.asInterface(remote);
            LogUtil.info(TAG, "Android service connect done!");
            if (firstDeviceId != null) {
                remoteService.action(firstDeviceId, Const.START);
            }
        }

        @Override
        public void onAbilityDisconnectDone(ElementName elementName, int i) {
            LogUtil.info(TAG, "Android service disconnect done!");
            isConnected = false;
            ability.disconnectAbility(connection);
        }
    };

    public GameRemote(Ability ability) {
        super("Game remote");
        this.ability = ability;
    }

    @Override
    public IRemoteObject asObject() {
        return this;
    }

    @Override
    public boolean onRemoteRequest(int code, MessageParcel data, MessageParcel reply, MessageOption option) throws RemoteException {
        if (code == REMOTE_COMMAND) {
            String deviceId = data.readString();
            String action = data.readString();
            switch (action) {
                case Const.START:
                    if (!isConnected) {
                        startAndroidApp();
                        connectToAndroidService();
                        this.firstDeviceId = deviceId;
                    } else {
                        sendAction(deviceId, action);
                    }
                    break;
                case Const.FINISH:
                    sendAction(deviceId, action);
                    ability.disconnectAbility(connection);
                    break;
                default:
                    sendAction(deviceId, action);
                    break;
            }
            return true;
        }
        return false;
    }

    private void startAndroidApp() {
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withBundleName(Const.ANDROID_PACKAGE_NAME)
                .withAbilityName(Const.ANDROID_ACTIVITY_NAME)
                .withFlags(Intent.FLAG_NOT_OHOS_COMPONENT)
                .build();
        intent.setOperation(operation);
        ability.startAbility(intent);
    }

    private void connectToAndroidService() {
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withBundleName(Const.ANDROID_PACKAGE_NAME)
                .withAbilityName(Const.ANDROID_SERVICE_NAME)
                .withFlags(Intent.FLAG_NOT_OHOS_COMPONENT)
                .build();
        intent.setOperation(operation);
        isConnected = ability.connectAbility(intent, connection);
    }

    private void sendAction(String deviceId, String action) {
        if (remoteService != null) {
            remoteService.action(deviceId, action);
        }
    }

}
