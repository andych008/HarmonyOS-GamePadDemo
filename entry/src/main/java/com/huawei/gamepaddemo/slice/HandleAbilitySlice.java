package com.huawei.gamepaddemo.slice;

import com.huawei.gamepaddemo.ResourceTable;
import com.huawei.gamepaddemo.controller.Const;
import com.huawei.gamepaddemo.controller.HandleRemoteProxy;
import com.huawei.gamepaddemo.controller.LogUtil;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.IAbilityConnection;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.window.dialog.ToastDialog;
import ohos.bundle.AbilityInfo;
import ohos.bundle.ElementName;
import ohos.bundle.IBundleManager;
import ohos.data.distributed.common.KvManagerConfig;
import ohos.data.distributed.common.KvManagerFactory;
import ohos.distributedschedule.interwork.DeviceInfo;
import ohos.distributedschedule.interwork.DeviceManager;
import ohos.distributedschedule.interwork.IDeviceStateCallback;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;
import ohos.rpc.IRemoteObject;
import ohos.rpc.RemoteException;

import java.util.List;

public class HandleAbilitySlice extends AbilitySlice {
    private static final String TAG = HandleAbilitySlice.class.getSimpleName();
    private static final int EVENT_STATE_CHANGE = 10001;
    private HandleRemoteProxy remoteProxy;
    private String deviceId;

    private final EventHandler handler = new EventHandler(EventRunner.current()) {
        @Override
        protected void processEvent(InnerEvent event) {
            if (event.eventId == EVENT_STATE_CHANGE) {
                getTabletDevice();
            }
        }
    };

    private final IDeviceStateCallback callback = new IDeviceStateCallback() {
        @Override
        public void onDeviceOffline(String deviceId, int deviceType) {
            if (HandleAbilitySlice.this.deviceId.equals(deviceId)) {
                showToast("Device offline");
                disconnectAbility(connection);
            }
        }

        @Override
        public void onDeviceOnline(String deviceId, int deviceType) {
            handler.sendEvent(EVENT_STATE_CHANGE);
        }
    };

    private final IAbilityConnection connection = new IAbilityConnection() {
        @Override
        public void onAbilityConnectDone(ElementName elementName, IRemoteObject remote, int resultCode) {
            String localDeviceId = KvManagerFactory.getInstance()
                    .createKvManager(new KvManagerConfig(HandleAbilitySlice.this))
                    .getLocalDeviceInfo()
                    .getId();
            remoteProxy = new HandleRemoteProxy(remote, localDeviceId);
            LogUtil.info(TAG, "ability connect done!");
            remoteProxy.remoteControl(Const.START);
            setupRemoteButton();
        }

        @Override
        public void onAbilityDisconnectDone(ElementName elementName, int i) {
            LogUtil.info(TAG, "ability disconnect done!");
            disconnectAbility(connection);
        }
    };

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_handle);
        initData(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        onBackPressed();
    }

    @Override
    protected void onBackPressed() {
        super.onBackPressed();
        remoteProxy.remoteControl(Const.FINISH);
        disconnectAbility(connection);
        DeviceManager.unregisterDeviceStateCallback(callback);
    }

    private void initData(Intent intent) {
        Object obj = intent.getParams().getParam(Const.DEVICE_ID_KEY);
        if (obj instanceof String) {
            deviceId = (String) obj;
            connectToRemoteService();
        }
        DeviceManager.registerDeviceStateCallback(callback);
    }

    private void connectToRemoteService() {
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId(deviceId)
                .withBundleName(Const.BUNDLE_NAME)
                .withAbilityName(Const.ABILITY_NAME)
                .withFlags(Intent.FLAG_ABILITYSLICE_MULTI_DEVICE)
                .build();
        intent.setOperation(operation);
        try {
            List<AbilityInfo> abilityInfoList = getBundleManager().queryAbilityByIntent(
                    intent,
                    IBundleManager.GET_BUNDLE_DEFAULT,
                    0);
            if (abilityInfoList != null && !abilityInfoList.isEmpty()) {
                connectAbility(intent, connection);
                LogUtil.info(TAG, "connect service on tablet with id " + deviceId );
            } else {
                showToast("Cannot connect service on tablet");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void setupRemoteButton() {
        findComponentById(ResourceTable.Id_up_button).setClickedListener(component -> remoteProxy.remoteControl(Const.UP));
        findComponentById(ResourceTable.Id_down_button).setClickedListener(component -> remoteProxy.remoteControl(Const.DOWN));
        findComponentById(ResourceTable.Id_left_button).setClickedListener(component -> remoteProxy.remoteControl(Const.LEFT));
        findComponentById(ResourceTable.Id_right_button).setClickedListener(component -> remoteProxy.remoteControl(Const.RIGHT));
    }

    private void getTabletDevice() {
        List<DeviceInfo> devices = DeviceManager.getDeviceList(DeviceInfo.FLAG_GET_ONLINE_DEVICE);
        if (devices.isEmpty()) {
            showToast("No device found");
        } else {
            devices.forEach(deviceInfo -> {
                DeviceInfo.DeviceType deviceType = deviceInfo.getDeviceType();
                String deviceId = deviceInfo.getDeviceId();
                LogUtil.info(TAG, "Found device " + deviceInfo.getDeviceType());
                if (deviceType == DeviceInfo.DeviceType.SMART_PAD && !deviceId.equals(this.deviceId)) {
                    this.deviceId = deviceId;
                    connectToRemoteService();
                }
            });
        }
    }

    private void showToast(String text) {
        LogUtil.info(TAG, text);
        new ToastDialog(this)
                .setText(text)
                .setAutoClosable(false)
                .show();
    }
}
