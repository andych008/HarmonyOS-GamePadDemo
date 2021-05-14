package jp.huawei.a2hdemo;

import ohos.rpc.*;

public abstract class GameServiceStub extends RemoteObject implements IGameInterface {

    static final String DESCRIPTOR = "jp.huawei.a2hdemo.IGameInterface";
    static final int REMOTE_COMMAND = IRemoteObject.MIN_TRANSACTION_ID;

    public GameServiceStub(String descriptor) {
        super(descriptor);
    }

    @Override
    public IRemoteObject asObject() {
        return this;
    }

    public static IGameInterface asInterface(IRemoteObject remoteObject) {
        if (remoteObject == null) {
            return null;
        }
        IRemoteBroker broker = remoteObject.queryLocalInterface(DESCRIPTOR);
        if (broker instanceof IGameInterface) {
            return (IGameInterface) broker;
        } else {
            return new GameServiceProxy(remoteObject);
        }
    }

    @Override
    public boolean onRemoteRequest(int code, MessageParcel data, MessageParcel reply, MessageOption option) throws RemoteException {
        String token = data.readInterfaceToken();
        if (!DESCRIPTOR.equals(token)) {
            return false;
        }
        if (code == REMOTE_COMMAND) {
            String deviceId = data.readString();
            String action = data.readString();
            action(deviceId, action);
            return true;
        } else {
            return super.onRemoteRequest(code, data, reply, option);
        }
    }
}
