/*
 * Copyright (C) 2014 Thalmic Labs Inc.
 * Distributed under the Myo SDK license agreement. See LICENSE.txt for details.
 */

package com.thalmic.android.sample.helloworld;

import android.app.Activity;
import android.util.Log;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.UUID;

public class HelloWorldActivity extends Activity {

    private TextView mLockStateView;
    private TextView mTextView;
    float roll;
    float pitch;
    float yaw;
    float state = 1;
    float reps = 1;
    float filteredData;

    //pebble variables//
    private static final UUID WATCHAPP_UUID = UUID.fromString("6092637b-8f58-4199-94d8-c606b1e45040");
    private static final String WATCHAPP_FILENAME = "android-example.pbw";

    private static final int
            KEY_BUTTON = 0,
            KEY_VIBRATE1 = 2,
            KEY_VIBRATE = 1,
            BUTTON_UP = 0,
            BUTTON_SELECT = 1,
            BUTTON_DOWN = 2;
    //----------------//

    // Classes that inherit from AbstractDeviceListener can be used to receive events from Myo devices.
    // If you do not override an event, the default behavior is to do nothing.
    private DeviceListener mListener = new AbstractDeviceListener() {

        // onConnect() is called whenever a Myo has been connected.
        @Override
        public void onConnect(Myo myo, long timestamp) {
            // Set the text color of the text view to cyan when a Myo connects.
            mTextView.setTextColor(Color.CYAN);
        }

        // onDisconnect() is called whenever a Myo has been disconnected.
        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            // Set the text color of the text view to red when a Myo disconnects.
            mTextView.setTextColor(Color.RED);
        }

        // onArmSync() is called whenever Myo has recognized a Sync Gesture after someone has put it on their
        // arm. This lets Myo know which arm it's on and which way it's facing.
        @Override
        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
            mTextView.setText(myo.getArm() == Arm.LEFT ? R.string.arm_left : R.string.arm_right);
        }

        // onArmUnsync() is called whenever Myo has detected that it was moved from a stable position on a person's arm after
        // it recognized the arm. Typically this happens when someone takes Myo off of their arm, but it can also happen
        // when Myo is moved around on the arm.
        @Override
        public void onArmUnsync(Myo myo, long timestamp) {
            int Test = 1;
            mTextView.setText("Reps: " + Test);
        }

        // onUnlock() is called whenever a synced Myo has been unlocked. Under the standard locking
        // policy, that means poses will now be delivered to the listener.
        @Override
        public void onUnlock(Myo myo, long timestamp) {
            mLockStateView.setText("Welcome Back!");
        }

        // onLock() is called whenever a synced Myo has been locked. Under the standard locking
        // policy, that means poses will no longer be delivered to the listener.
        @Override
        public void onLock(Myo myo, long timestamp) {
            mLockStateView.setText("Welcome Back!");
        }

        // onOrientationData() is called whenever a Myo provides its current orientation,
        // represented as a quaternion.
        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
            // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
             roll = (float) Math.toDegrees(Quaternion.roll(rotation));
             pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
             yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));
            //filteredData += (pitch - filteredData) / 5;

           // Log.d("TAG", "Roll: " + roll + "Pitch: " + pitch + "Yaw: " + yaw);
           // Log.d("TAG", "FilteredData" +filteredData);
            if (state == 1)
            {
                if(stage1(pitch) )
                {
                    state = 1.5f;
                    Log.d("TAG", "State1 Finished ");
                    Log.d("TAG", "FilteredData" +pitch);
                }
            }

            if (state == 1.5f) {
                if (stage2(pitch)) {
                    state = 2;
                    Log.d("TAG", "State2 Finished ");
                    Log.d("TAG", "FilteredData" +pitch);
                }
            }
            if (state == 2)
            {
                if(stage3(pitch) )
                {
                    //Log.d("TAG","FILTERED DATA" + filteredData);
                    state = 1;
                    reps++;

                    //Pebble stuff//

                    // Send KEY_VIBRATE to Pebble
                    if(reps > 5){
                    PebbleDictionary out = new PebbleDictionary();
                    out.addInt32(KEY_VIBRATE1, 0);
                    PebbleKit.sendDataToPebble(getApplicationContext(), WATCHAPP_UUID, out);
                }
                   else{
                    PebbleDictionary out = new PebbleDictionary();
                    out.addInt32(KEY_VIBRATE, 0);
                    PebbleKit.sendDataToPebble(getApplicationContext(), WATCHAPP_UUID, out); }



                    //-----------//


                    Log.d("TAG", "State3 Finished ");
                    Log.d("TAG", "FilteredData" +pitch);
                }
            }


            // Adjust roll and pitch for the orientation of the Myo on the arm.
            if (myo.getXDirection() == XDirection.TOWARD_ELBOW) {
                roll *= -1;
                pitch *= -1;
            }

            // Next, we apply a rotation to the text view using the roll, pitch, and yaw.
            mTextView.setRotation(roll);
            mTextView.setRotationX(pitch);
            mTextView.setRotationY(yaw);

        }

        // onPose() is called whenever a Myo provides a new pose.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            // Handle the cases of the Pose enumeration, and change the text of the text view
            // based on the pose we receive.
            switch (pose) {
                case UNKNOWN:
                    int Test = 1;
                    mTextView.setText("Reps: " + Test);
                    break;
                case REST:
                case DOUBLE_TAP:
                    int restTextId = R.string.NumberofReps;


                    switch (myo.getArm()) {
                        case LEFT:
                            restTextId = R.string.arm_left;
                            break;
                        case RIGHT:
                            restTextId = R.string.arm_right;
                            break;
                    }
                    int Test1 = 1;
                    mTextView.setText("Reps: " + Test1);
                    break;
                case FIST:
                    mTextView.setText(getString(R.string.pose_fist));
                    break;
                case WAVE_IN:
                    mTextView.setText(getString(R.string.pose_wavein));
                    break;
                case WAVE_OUT:
                    mTextView.setText(getString(R.string.pose_waveout));
                    break;
                case FINGERS_SPREAD:
                    mTextView.setText(getString(R.string.pose_fingersspread));
                    break;
            }

            if (pose != Pose.UNKNOWN && pose != Pose.REST) {
                // Tell the Myo to stay unlocked until told otherwise. We do that here so you can
                // hold the poses without the Myo becoming locked.
                myo.unlock(Myo.UnlockType.HOLD);

                // Notify the Myo that the pose has resulted in an action, in this case changing
                // the text on the screen. The Myo will vibrate.
                myo.notifyUserAction();
            } else {
                // Tell the Myo to stay unlocked only for a short period. This allows the Myo to
                // stay unlocked while poses are being performed, but lock after inactivity.
                myo.unlock(Myo.UnlockType.TIMED);
            }
        }
    };

    public boolean stage1(float pitch){
        if(pitch >= -70 && pitch <= 0)
        {
            return true;
        }
        return false;
    }

    public boolean stage2(float pitch){
        if(pitch > 0 && pitch < 45)
        {
            return true;

        }
        return false;
    }

    public boolean stage3(float pitch){

        if(pitch < -65 && pitch > -70)
        {

            Log.d("TAG" , "REPSSSSSS:" + reps);
            return true;


        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);

        mLockStateView = (TextView) findViewById(R.id.lock_state);
        mTextView = (TextView) findViewById(R.id.text);

        // First, we initialize the Hub singleton with an application identifier.
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // We don't want any callbacks when the Activity is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);

        if (isFinishing()) {
            // The Activity is finishing, so shutdown the Hub. This will disconnect from the Myo.
            Hub.getInstance().shutdown();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.action_scan == id) {
            onScanActionSelected();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onScanActionSelected() {
        // Launch the ScanActivity to scan for Myos to connect to.
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }
}
