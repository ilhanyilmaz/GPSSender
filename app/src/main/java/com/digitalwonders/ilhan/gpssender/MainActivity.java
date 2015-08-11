package com.digitalwonders.ilhan.gpssender;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity implements GPSTracker.GPSTrackerListener {

    private Button btnShowLocation;
    private TextView tv;

    // GPSTracker class
    private GPSTracker gps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.textView);
        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);

        gps = new GPSTracker(MainActivity.this);
        gps.setGPSTrackerListener(this);




        // Show mLocation button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Create class object
                Location location;

                // Check if GPS enabled
                if(gps.canGetLocation()) {

                    location = gps.getLocation();

                    if(location != null)
                        tv.append("Lat: " + location.getLatitude() + "  Long: " + location.getLongitude()+ "\n");
                    // \n is for new line
                    //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + mLatitude + "\nLong: " + mLongitude, Toast.LENGTH_LONG).show();
                } else {
                    // Can't get mLocation.
                    // GPS or network is not enabled.
                    // Ask user to enable GPS/network in settings.
                    gps.showSettingsAlert();
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null)
            tv.append("Cannot read location! \n");
        else
            tv.append("Lat: " + location.getLatitude() + "  Long: " + location.getLongitude() + "\n");
    }
}