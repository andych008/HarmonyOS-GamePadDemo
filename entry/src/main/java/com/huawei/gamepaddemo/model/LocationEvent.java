package com.huawei.gamepaddemo.model;

public class LocationEvent {
    private final float x;
    private final float y;

    public LocationEvent(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return String.format("Location: %.2f %.2f", x, y);
    }
}
