package com.studio.blacknight;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {



    private Button lockTaskButton;
    public DevicePolicyManager mDevicePolicyManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve Device Policy Manager so that we can check whether we can
        // lock to screen later
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);


        lockTaskButton = findViewById(R.id.start_lock_button);
        lockTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mDevicePolicyManager.isLockTaskPermitted(getApplicationContext().getPackageName())) {
                    Intent lockIntent = new Intent(getApplicationContext(), LockedActivity.class);
                    startActivity(lockIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.not_lock_whitelisted_string,Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
