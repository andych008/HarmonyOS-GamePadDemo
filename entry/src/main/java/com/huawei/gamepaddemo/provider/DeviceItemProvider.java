package com.huawei.gamepaddemo.provider;

import com.huawei.gamepaddemo.ResourceTable;
import ohos.agp.components.*;
import ohos.app.Context;
import ohos.distributedschedule.interwork.DeviceInfo;

import java.util.ArrayList;
import java.util.List;

public class DeviceItemProvider extends BaseItemProvider {
    private final List<DeviceInfo> list;
    private final Context context;
    private final Listener listener;

    public interface Listener {
        void onDeviceSelect(DeviceInfo deviceInfo);
    }

    public DeviceItemProvider(Context context, Listener listener) {
        this.context = context;
        this.list = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < list.size()) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Component getComponent(int position, Component convertComponent, ComponentContainer componentContainer) {
        final Component component;
        final ViewHolder viewHolder;
        if (convertComponent == null) {
            component = LayoutScatter.getInstance(context).parse(
                    ResourceTable.Layout_item_device,
                    null,
                    false);
            viewHolder = new ViewHolder();
            viewHolder.deviceName = (Text) component.findComponentById(ResourceTable.Id_device_name);
            component.setTag(viewHolder);
        } else {
            component = convertComponent;
            viewHolder = (ViewHolder) component.getTag();
        }
        DeviceInfo deviceInfo =  list.get(position);
        if (deviceInfo != null) {
            viewHolder.deviceName.setText(deviceInfo.getDeviceName());
            viewHolder.deviceName.setClickedListener(component1 -> {
               listener.onDeviceSelect(deviceInfo);
            });
        }
        return component;
    }

    public void updateItems(List<DeviceInfo> deviceInfoList) {
        list.clear();
        list.addAll(deviceInfoList);
        notifyDataChanged();
    }

    private static class ViewHolder {
        private Text deviceName;
    }
}

