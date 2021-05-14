# GamePadDemo

 Basketball Slam scenario's Harmony OS demo app.
 
## Structure
There are 2 modules:
- entry: handler module, need to deploy on mobile phone to control the game
- controller: controller module, used to connect with Android app to do the control

## How to deploy
- Deploy the **entry** module to the control device such as mobile phone or tablet
- Deploy the **controller** module with the [Android demo app](https://codehub-g.huawei.com/HarmonyOS/A2HDemo/home) on the target device such as tablet or TV 
- Open the **controller** app on the target device then grant multi-device collaboration permission
- Open the **Android** app on the target device then grant multi-device collaboration permission
- Open the **entry** app on the control device then grant multi-device collaboration permission 
- Connect both devices in the same wifi network
- Login with the same Huawei ID on both devices
- Set up bluetooth connection between both devices
- Open the **entry** app on the control device then click the device name to start the app on target device
- You can control the human position on the target device by click the arrow button on the control device