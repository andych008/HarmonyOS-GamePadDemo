package com.huawei.gamepaddemo.controller;

import com.huawei.gamepaddemo.model.LocationEvent;
import com.huawei.gamepaddemo.model.TerminateEvent;
import ohos.rpc.*;
import org.greenrobot.eventbus.EventBus;

public class ControllerRemote extends RemoteObject implements IRemoteBroker {
    static final int LOCATION_COMMAND = RemoteObject.MIN_TRANSACTION_ID;
    static final int TERMINATE_COMMAND = RemoteObject.MIN_TRANSACTION_ID + 1;

    public ControllerRemote(String descriptor) {
        super(descriptor);
    }

    @Override
    public IRemoteObject asObject() {
        return this;
    }

    @Override
    public boolean onRemoteRequest(int code, MessageParcel data, MessageParcel reply, MessageOption option) throws RemoteException {
        switch (code) {
            case LOCATION_COMMAND:
                float x = data.readFloat();
                float y = data.readFloat();
                EventBus.getDefault().post(new LocationEvent(x, y));
                return true;
            case TERMINATE_COMMAND:
                EventBus.getDefault().post(new TerminateEvent());
        }
        return false;
    }
}
