# Blacknight

The app implements the Kiosk MODE. This mode allows the users to disable all the major system UI features such as home button, notifications, recent apps button, and global actions. Setting up lock task mode on the device transforms the device into an immersive kiosk, with only the whitelisted apps active on the device.

## Compatibility 

Android 9

To guarantee system permissions was necessary add device administrator:

### Set:

__$ adb shell dpm set-device-owner com.studio.blacknight/.DeviceAdminReceiver__

### Remove:

__$ adb shell dpm remove-active-admin com.studio.blacknight/.DeviceAdminReceiver__

## Features

- Set Lock Task Mode
- Set Stay Awake 
- Disable Safe Boot
- Disable Factory Reset
- Disable Add new User
- Disable Mount Physical Media
- Disable Adjust Volume
- Disable Keyguard
- Disable Status Bar



## Goals




## Resources

### Add DevicePolicyManager

[Google API] https://developer.android.com/work/dpc/dedicated-devices

[Google API] https://developer.android.com/work/dpc/dedicated-devices/lock-task-mode

[Google API] https://developer.android.com/reference/android/app/admin/DevicePolicyManager

[Policy Manager] https://technostacks.com/blog/android-kiosk-mode/

[Policy Manager] https://snow.dog/blog/kiosk-mode-android

[Policy Manager] https://codelabs.developers.google.com/codelabs/cosu/index.html#2


## Contact

Diego Fraga

Email: diego.rfraga@gmail.com
