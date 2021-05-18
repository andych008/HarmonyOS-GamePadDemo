package com.huawei.gamepaddemo.controller;

import ohos.rpc.*;

public class ControllerRemoteProxy implements IRemoteBroker {
    private final String TAG = ControllerRemoteProxy.class.getName();
    static final int LOCATION_COMMAND = RemoteObject.MIN_TRANSACTION_ID;
    static final int TERMINATE_COMMAND = RemoteObject.MIN_TRANSACTION_ID + 1;
    private final IRemoteObject remote;

    public ControllerRemoteProxy(IRemoteObject remote) {
        this.remote = remote;
    }

    @Override
    public IRemoteObject asObject() {
        return remote;
    }

    public void sendLocation(float x, float y) {
        MessageParcel data = MessageParcel.obtain();
        MessageParcel reply = MessageParcel.obtain();
        MessageOption option = new MessageOption(MessageOption.TF_SYNC);
        data.writeFloat(x);
        data.writeFloat(y);
        try {
            remote.sendRequest(LOCATION_COMMAND, data, reply, option);
        } catch (RemoteException e) {
            LogUtil.error(TAG, "remote action error " + e.getMessage());
        } finally {
            data.reclaim();
            reply.reclaim();
        }
    }

    public void terminate() {
        MessageParcel data = MessageParcel.obtain();
        MessageParcel reply = MessageParcel.obtain();
        MessageOption option = new MessageOption(MessageOption.TF_SYNC);
        try {
            remote.sendRequest(TERMINATE_COMMAND, data, reply, option);
        } catch (RemoteException e) {
            LogUtil.error(TAG, "remote action error " + e.getMessage());
        } finally {
            data.reclaim();
            reply.reclaim();
        }
    }

}
