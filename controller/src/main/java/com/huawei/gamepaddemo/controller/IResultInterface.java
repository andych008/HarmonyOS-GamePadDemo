package com.huawei.gamepaddemo.controller;

import ohos.rpc.IRemoteBroker;

public interface IResultInterface extends IRemoteBroker {
    void sendLocation(String deviceId, float x, float y);
    void disconnect(String deviceId);
}
