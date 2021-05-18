package com.huawei.gamepaddemo.controller;

import ohos.rpc.*;

public abstract class ResultStub extends RemoteObject implements IResultInterface {
    static final String DESCRIPTOR = "com.huawei.gamepaddemo.controller.IResultInterface";
    static final int LOCATION_COMMAND = RemoteObject.MIN_TRANSACTION_ID;
    static final int DISCONNECT_COMMAND = RemoteObject.MIN_TRANSACTION_ID + 1;

    public ResultStub(String descriptor) {
        super(descriptor);
    }

    @Override
    public IRemoteObject asObject() {
        return this;
    }

    @Override
    public boolean onRemoteRequest(int code, MessageParcel data, MessageParcel reply, MessageOption option) throws RemoteException {
        String token = data.readInterfaceToken();
        if (!token.equals(DESCRIPTOR)) {
            return false;
        }
        String deviceId = data.readString();
        switch (code) {
            case LOCATION_COMMAND:
                float x = data.readFloat();
                float y = data.readFloat();
                sendLocation(deviceId, x, y);
                return true;
            case DISCONNECT_COMMAND:
                disconnect(deviceId);
                return true;
            default:
                break;
        }
        return false;
    }
}
