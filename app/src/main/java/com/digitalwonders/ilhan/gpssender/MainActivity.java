package com.digitalwonders.ilhan.gpssender;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;


public class MainActivity extends Activity implements GPSTracker.GPSTrackerListener {

    private static final String TAG = "GPSTracker";

    private static final double EARTHRADIUS = 6371000;

    private Button btnShowLocation;
    private TextView tv;

    // GPSTracker class
    private GPSTracker gps;
    private Location initLocation;

    private ArrayList<Point> mHistory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.textView);
        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);

        gps = new GPSTracker(MainActivity.this);
        gps.setGPSTrackerListener(this);

        mHistory = new ArrayList<>();


        // Show mLocation button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Create class object


                // Check if GPS enabled
                if(gps.canGetLocation()) {

                    initLocation = gps.getLocation();
                    if(initLocation != null)
                        tv.append("Lat: " + initLocation.getLatitude() + "  Long: " + initLocation.getLongitude()+ "\n");
                }
                else {
                    // Can't get mLocation.
                    // GPS or network is not enabled.
                    // Ask user to enable GPS/network in settings.
                    gps.showSettingsAlert();
                }
            }
        });
    }

    @Override
    public void onStop() {
        writeHistory();
        super.onStop();
    }

    private void writeHistory() {
        String filename = "gpsdata.txt";
        FileOutputStream outputStream;
        String s;
        try {
            outputStream = openFileOutput(filename, Context.MODE_APPEND);
            Iterator<Point> iterator = mHistory.iterator();
            Point p;
            while (iterator.hasNext()) {
                p = iterator.next();
                s = "Distance: " + p.x + ", Angle: " + p.y + "\n";
                outputStream.write(s.getBytes());
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "written data to gpsdata.txt");
    }

    @Override
    public void onLocationChanged(Location location) {
        if(initLocation == null)
            return;
        if(location == null) {
            tv.append("Cannot read location! \n");
            return;
        }

        double x = (location.getLatitude() - initLocation.getLatitude())*111000.0;
        double y = (location.getLongitude() - initLocation.getLongitude())*111000.0;

        double angle = Math.atan2(x, y)*180.0/Math.PI;
        //double distance = Math.sqrt(x*x+y*y);
        double distance = getDistance(initLocation, location);

        mHistory.add(new Point((int) distance, (int)angle));

        tv.append("Distance: " + distance + ", Angle: " + angle + "\n");
    }



    private double getDistance(Location loc1, Location loc2) {

        double lat1 = loc1.getLatitude();
        double lat2 = loc2.getLatitude();
        double lon1 = loc1.getLongitude();
        double lon2 = loc2.getLongitude();
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = (Math.sin(dlat / 2))*(Math.sin(dlat / 2)) + Math.cos(lat1) * Math.cos(lat2) * (Math.sin(dlon/2))*(Math.sin(dlon/2));
        double c = 2 * Math.atan2( Math.sqrt(a), Math.sqrt(1-a) );
        double d = EARTHRADIUS * c;
        return d;
    }
}