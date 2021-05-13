package com.huawei.gamepaddemo.model;

public class GrantPermissionEvent {
    private String permission;
    private Boolean isGranted;

    public GrantPermissionEvent(String permission, Boolean isGranted) {
        this.permission = permission;
        this.isGranted = isGranted;
    }

    public String getPermission() {
        return this.permission;
    }

    public Boolean getIsGranted() {
        return this.isGranted;
    }
}
