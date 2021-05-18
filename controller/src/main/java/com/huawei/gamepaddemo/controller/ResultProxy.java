package com.huawei.gamepaddemo.controller;

import ohos.rpc.*;

public class ResultProxy implements IResultInterface {
    private final IRemoteObject remote;

    public ResultProxy(IRemoteObject remote) {
        this.remote = remote;
    }

    @Override
    public void sendLocation(String deviceId, float x, float y) {
        MessageParcel data = MessageParcel.obtain();
        MessageParcel reply = MessageParcel.obtain();
        MessageOption option = new MessageOption(MessageOption.TF_SYNC);

        data.writeInterfaceToken(ResultStub.DESCRIPTOR);
        data.writeString(deviceId);
        data.writeFloat(x);
        data.writeFloat(y);
        try {
            remote.sendRequest(ResultStub.LOCATION_COMMAND, data, reply, option);
            reply.writeNoException();
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            data.reclaim();
            reply.reclaim();
        }
    }

    @Override
    public void disconnect(String deviceId) {
        MessageParcel data = MessageParcel.obtain();
        MessageParcel reply = MessageParcel.obtain();
        MessageOption option = new MessageOption(MessageOption.TF_SYNC);

        data.writeInterfaceToken(ResultStub.DESCRIPTOR);
        data.writeString(deviceId);
        try {
            remote.sendRequest(ResultStub.DISCONNECT_COMMAND, data, reply, option);
            reply.writeNoException();
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            data.reclaim();
            reply.reclaim();
        }
    }

    @Override
    public IRemoteObject asObject() {
        return remote;
    }
}
