package com.huawei.gamepaddemo.slice;

import com.huawei.gamepaddemo.MainAbility;
import com.huawei.gamepaddemo.ResourceTable;
import com.huawei.gamepaddemo.controller.Const;
import com.huawei.gamepaddemo.model.GrantPermissionEvent;
import com.huawei.gamepaddemo.provider.DeviceItemProvider;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.IntentParams;
import ohos.agp.components.ListContainer;
import ohos.bundle.IBundleManager;
import ohos.distributedschedule.interwork.DeviceInfo;
import ohos.distributedschedule.interwork.DeviceManager;
import ohos.distributedschedule.interwork.IDeviceStateCallback;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;
import ohos.security.SystemPermission;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class DeviceListAbilitySlice extends AbilitySlice {

    private ListContainer deviceList;
    private DeviceItemProvider provider;
    private static final int EVENT_STATE_CHANGE = 10001;

    private final EventHandler handler = new EventHandler(EventRunner.current()) {
        @Override
        protected void processEvent(InnerEvent event) {
            if (event.eventId == EVENT_STATE_CHANGE) {
                updateDeviceList();
            }
        }
    };

    private final IDeviceStateCallback callback = new IDeviceStateCallback() {
        @Override
        public void onDeviceOffline(String deviceId, int deviceType) {
            handler.sendEvent(EVENT_STATE_CHANGE);
        }

        @Override
        public void onDeviceOnline(String deviceId, int deviceType) {
            handler.sendEvent(EVENT_STATE_CHANGE);
        }
    };

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        setupUI();
        initData();
    }

    @Override
    protected void onActive() {
        super.onActive();
        if (provider.getCount() == 0) {
            updateDeviceList();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        DeviceManager.unregisterDeviceStateCallback(callback);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGrantPermissionEvent(GrantPermissionEvent event) {
        if (event.getPermission().equals(SystemPermission.DISTRIBUTED_DATASYNC)
                && event.getIsGranted()) {
           updateDeviceList();
        }
    }

    private void updateDeviceList() {
        List<DeviceInfo> deviceInfoList = DeviceManager.getDeviceList(DeviceInfo.FLAG_GET_ONLINE_DEVICE);
        provider.updateItems(deviceInfoList);
        deviceList.setItemProvider(provider);
    }

    private void setupUI() {
        setUIContent(ResourceTable.Layout_ability_device_list);
        deviceList = (ListContainer) findComponentById(ResourceTable.Id_device_list);
        provider = new DeviceItemProvider(this, this::startHandle);
    }

    private void initData() {
        requestPermissions(SystemPermission.DISTRIBUTED_DATASYNC);
        EventBus.getDefault().register(this);
        DeviceManager.registerDeviceStateCallback(callback);
    }

    private void requestPermissions(String... permissions) {
        for (String permission : permissions) {
            if (verifyCallingOrSelfPermission(permission) != IBundleManager.PERMISSION_GRANTED) {
                requestPermissionsFromUser(
                        new String[] {
                                permission
                        },
                        MainAbility.REQUEST_CODE);
            }
        }
    }

    private void startHandle(DeviceInfo deviceInfo) {
        Intent intent = new Intent();
        IntentParams params = new IntentParams();
        params.setParam(Const.DEVICE_ID_KEY, deviceInfo.getDeviceId());
        intent.setParams(params);
        present(new HandleAbilitySlice(), intent);
    }
}
