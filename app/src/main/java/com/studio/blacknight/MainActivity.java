package com.studio.blacknight;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {



    private Button lockTaskButton;
    public DevicePolicyManager mDevicePolicyManager;


    private PackageManager mPackageManager;
    private ComponentName mAdminComponentName;

    // Whitelist two apps.
    public static final String KIOSK_PACKAGE = "com.studio.blacknight";
    public static final String WAZE_PACKAGE = "com.waze";
    public static final String AUTO_PACKAGE = "com.google.android.projection.gearhead";
    public static final String YOUTUBEMUSIC_PACKAGE = "com.google.android.apps.youtube.music";
    public static final String GOOGLEMAPS_PACKAGE = "com.google.android.apps.maps";
    public static final String[] APP_PACKAGES = {KIOSK_PACKAGE, WAZE_PACKAGE, AUTO_PACKAGE,YOUTUBEMUSIC_PACKAGE, GOOGLEMAPS_PACKAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve Device Policy Manager so that we can check whether we can
        // lock to screen later
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);


        // Retrieve DeviceAdminReceiver ComponentName so we can make
        // device management api calls later
        mAdminComponentName = DeviceAdminReceiver.getComponentName(this);

        // Retrieve Package Manager so that we can enable and
        // disable LockedActivity
        mPackageManager = this.getPackageManager();

        mDevicePolicyManager.setLockTaskPackages(mAdminComponentName, APP_PACKAGES);

        lockTaskButton = findViewById(R.id.start_lock_button);
        lockTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mDevicePolicyManager.isLockTaskPermitted(getApplicationContext().getPackageName())) {
                    Intent lockIntent = new Intent(getApplicationContext(), LockedActivity.class);

                    //enable the lock acvity before the intent was send
                    mPackageManager.setComponentEnabledSetting(
                            new ComponentName(getApplicationContext(),
                                    LockedActivity.class),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);
                    startActivity(lockIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.not_lock_whitelisted_string,Toast.LENGTH_LONG).show();
                }
            }
        });



        // Check to see if started by LockActivity and disable LockActivity if so
        Intent intent = getIntent();

        if(intent.getIntExtra(LockedActivity.LOCK_ACTIVITY_KEY,0) ==
                LockedActivity.FROM_LOCK_ACTIVITY){
            mDevicePolicyManager.clearPackagePersistentPreferredActivities(
                    mAdminComponentName,getPackageName());
            mPackageManager.setComponentEnabledSetting(
                    new ComponentName(getApplicationContext(), LockedActivity.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }


    }
}
