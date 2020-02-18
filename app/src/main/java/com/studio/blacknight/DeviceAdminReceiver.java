package com.studio.blacknight;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

// Handles events related to managed profiles and devices
public class DeviceAdminReceiver extends android.app.admin.DeviceAdminReceiver {
    private static final String TAG = "DeviceAdminReceiver";

    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(),  DeviceAdminReceiver.class);
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);


    }
}