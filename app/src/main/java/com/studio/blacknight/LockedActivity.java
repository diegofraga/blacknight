package com.studio.blacknight;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.admin.SystemUpdatePolicy;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.os.UserManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LockedActivity extends Activity {


    private Button stopLockButton;
    private Button wazeButton;
    private Button autoButton;
    private DevicePolicyManager mDevicePolicyManager;


    private ComponentName mAdminComponentName;
    public static final String LOCK_ACTIVITY_KEY = "lock_activity";
    public static final int FROM_LOCK_ACTIVITY = 1;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locked);

        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        // Setup stop lock task button
        stopLockButton = findViewById(R.id.stop_lock_button);
        stopLockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager am = (ActivityManager) getSystemService(
                        Context.ACTIVITY_SERVICE);
                if (am.getLockTaskModeState() ==
                        ActivityManager.LOCK_TASK_MODE_LOCKED) {
                    stopLockTask();
                }
                // revert Default blacknight policy
                setDefaultblacknightPolicies(false);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                intent.putExtra(LOCK_ACTIVITY_KEY, FROM_LOCK_ACTIVITY);
                startActivity(intent);
                finish();
            }
        });

        wazeButton = findViewById(R.id.WazeButton);
        wazeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //test lauch Waze

                mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                mDevicePolicyManager.setLockTaskPackages(mAdminComponentName, MainActivity.APP_PACKAGES);



                // Start app's main activity with our lock task mode option.
                PackageManager packageManager = LockedActivity.this.getPackageManager();
                Intent launchIntent = packageManager.getLaunchIntentForPackage(MainActivity.WAZE_PACKAGE);

                if (launchIntent != null) {
                    startActivity(launchIntent);//null pointer check in case package name was not found
                }else{
                        Toast.makeText(getApplicationContext(),
                           "Waze App not installed", Toast.LENGTH_SHORT).show();
                }



            }
        });


        autoButton = findViewById(R.id.android_auto_button);
        autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //test launch Auto Android

                mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                mDevicePolicyManager.setLockTaskPackages(mAdminComponentName, MainActivity.APP_PACKAGES);


                // Start our kiosk app's main activity with our lock task mode option.
                PackageManager packageManager = LockedActivity.this.getPackageManager();
                Intent launchIntent = packageManager.getLaunchIntentForPackage(MainActivity.AUTO_PACKAGE);

                if (launchIntent != null) {
                    startActivity(launchIntent);//null pointer check in case package name was not found
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Auto Android not installed", Toast.LENGTH_SHORT).show();
                }


            }
        });



        // Set Default blacknight policy
        mAdminComponentName = DeviceAdminReceiver.getComponentName(this);
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        if(mDevicePolicyManager.isDeviceOwnerApp(getPackageName())){
            setDefaultblacknightPolicies(true);
        }
        else {
            Toast.makeText(getApplicationContext(),
                    R.string.not_device_owner, Toast.LENGTH_SHORT).show();
        }


        //enableStayOnWhilePluggedIn(true);
        //need to check the restrions it's posible to use/ API mDevicePolicyManager
        //setUserRestriction(restricion,true)



    }

    @Override
    protected void onStart() {
        super.onStart();

        // Start lock task mode if its not already active
        if(mDevicePolicyManager.isLockTaskPermitted(this.getPackageName())){
            ActivityManager am = (ActivityManager) getSystemService(
                    Context.ACTIVITY_SERVICE);
            if (am.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_NONE) {
                startLockTask();
            }
        }
    }



    private void setDefaultblacknightPolicies(boolean active){

        // Set user restrictions
        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, active);
        setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, active);
        setUserRestriction(UserManager.DISALLOW_ADD_USER, active);
        setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, active);
        setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, active);

        // Disable keyguard and status bar
        mDevicePolicyManager.setKeyguardDisabled(mAdminComponentName, active);
        mDevicePolicyManager.setStatusBarDisabled(mAdminComponentName, active);

        // Enable STAY_ON_WHILE_PLUGGED_IN
        enableStayOnWhilePluggedIn(active);

        // Set system update policy
        if (active){
            mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName,
                    SystemUpdatePolicy.createWindowedInstallPolicy(60, 120));
        } else {
            mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName,
                    null);
        }

        // set this Activity as a lock task package
        mDevicePolicyManager.setLockTaskPackages(mAdminComponentName,
                active ? new String[]{getPackageName()} : new String[]{});

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        if (active) {
            // set Cosu activity as home intent receiver so that it is started
            // on reboot
            mDevicePolicyManager.addPersistentPreferredActivity(
                    mAdminComponentName, intentFilter, new ComponentName(
                            getPackageName(), LockedActivity.class.getName()));
        } else {
            mDevicePolicyManager.clearPackagePersistentPreferredActivities(
                    mAdminComponentName, getPackageName());
        }
    }

    private void setUserRestriction(String restriction, boolean disallow){
        if (disallow) {
            mDevicePolicyManager.addUserRestriction(mAdminComponentName,
                    restriction);
        } else {
            mDevicePolicyManager.clearUserRestriction(mAdminComponentName,
                    restriction);
        }
    }

    private void enableStayOnWhilePluggedIn(boolean enabled){
        if (enabled) {
            mDevicePolicyManager.setGlobalSetting(
                    mAdminComponentName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                    Integer.toString(BatteryManager.BATTERY_PLUGGED_AC
                            | BatteryManager.BATTERY_PLUGGED_USB
                            | BatteryManager.BATTERY_PLUGGED_WIRELESS));
        } else {
            mDevicePolicyManager.setGlobalSetting(
                    mAdminComponentName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                    "0"
            );
        }
    }


}
