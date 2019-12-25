package com.example.smartoffice3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.example.smartoffice3.activitys.Room;
import com.example.smartoffice3.activitys.managers.RoomManager;
import com.example.smartoffice3.lib.DevicesButons;
import com.example.smartoffice3.lib.Vector;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

public class MainActivity extends Activity implements View.OnClickListener {

    // use a compound button so either checkbox or switch widgets work.
    private boolean autoFocus;
    private boolean useFlash;
    private String statusMessage;
    private String barcodeValue;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    public static int countID=0;

    private Button button;
    private RelativeLayout relativeLayout;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private Vector<DevicesButons> devicesButons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (RoomManager.NUMBER_ROOMS>0) {
            setContentView(R.layout.activity_main);
            findViewById(R.id.add_device_button).setOnClickListener(this);
        }else {
            setContentView(R.layout.first_enter_activity);
            findViewById(R.id.add_device_in_wellkom);

        }

        //statusMessage = (TextView)findViewById(R.id.status_message);
        //barcodeValue = (TextView)findViewById(R.id.barcode_value);

        autoFocus = true;
        useFlash = false;





        relativeLayout = (RelativeLayout) findViewById(R.id.rer_layoyt);
        linearLayout = (LinearLayout) findViewById(R.id.lin_layout);
        scrollView = (ScrollView) findViewById(R.id.scroll);
        devicesButons = new Vector<DevicesButons>(0);

    }

    public void addDevise(String device) {
        RoomManager.NUMBER_ROOMS++;
        RoomManager.NAME_ROOMS.push_back(device);
        devicesButons.push_back(new DevicesButons(getApplicationContext(),
                device,
                devicesButons.size()));
        devicesButons.get(devicesButons.size()-1).setText(devicesButons.get(devicesButons.size()-1).getIdDevice() );
        devicesButons.get(devicesButons.size()-1).setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
        );

        devicesButons.get(devicesButons.size()-1).setId(devicesButons.size()-1);
        devicesButons.get(devicesButons.size()-1).setOnClickListener(this);
        linearLayout.addView(devicesButons.get(devicesButons.size()-1));
        countID++;
    }





    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_device_button | v.getId()==R.id.add_device_in_wellkom) {
            // launch barcode activity.
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus);
            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash);

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }else {
            for (int i = 0; i < devicesButons.size(); i++) {
                if (v.getId()==devicesButons.get(i).getId()){
                    Intent intent = new Intent(this, Room.class);
                    Room.ID_DEVICE = devicesButons.get(i).getIdDevice();
                    startActivity(intent);
                }
            }
        }

    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);

                    barcodeValue=barcode.displayValue;
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                    addDevise(barcodeValue);
                } else {
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                statusMessage=(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

