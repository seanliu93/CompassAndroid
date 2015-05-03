package seanliu93.compass_android;


import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Timer;
import java.util.TimerTask;

import MyHttpClient.*;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private com.google.android.gms.location.LocationListener mListener;
    private SearchView search_view;
    private Marker userMarker;
    private Timer t;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Initialize containers in resource tree on for current user
        new initializeContainersTask().execute();

        // Search bar listeners
        search_view = (SearchView) findViewById(R.id.search_view);
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String userid) {
                // TODO Auto-generated method stub to handle Search Requests

                final String[] USERID = {userid};

                // Make toast saying it's searching
                Toast.makeText(MapsActivity.this, "Searching for " +userid+"...", Toast.LENGTH_SHORT).show();

                new getGPSCoordsTask1().execute(USERID);



                // Use timer to periodically create GPScoord contentInstances in SeanLocationAE
                if (t != null)
                {
                    t.cancel();
                    t.purge();
                }
                t = new Timer();
                t.scheduleAtFixedRate(new TimerTask()
                {
                    public void run()
                    {
                        new getGPSCoordsTask2().execute(USERID);

                    }
                }, 5000, 5000);


                return true;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {
                // TODO Auto-generated method stub

                return true;

            }
        });

        setUpMapIfNeeded();

        // Use timer to periodically create GPScoord contentInstances in SeanLocationAE
        Timer t;
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                String lastCoords = Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
                String[] LastCoords = {lastCoords};

                System.out.println("Posting new coords: " + lastCoords + "...");
                new PushGPSCoordsTask().execute(LastCoords);
            }
        }, 0, 5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        Location location = mMap.getMyLocation();
        LatLng myLocation;
        if (location != null) {
            myLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());
        }
        else
        {
            myLocation = new LatLng(42.349106,-71.105525);
        }



        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14));
    }

    private class initializeContainersTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {
            String response = MyHttpClient.createNewUserContainer(getApplicationContext());
            response = MyHttpClient.createNewUUIDContainer(getApplicationContext(),true);
            response = MyHttpClient.createNewUUIDContainer(getApplicationContext(),false);
            response = MyHttpClient.initializeLocContainers(getApplicationContext());
            response = MyHttpClient.putEnableFlagForLocGPS(getApplicationContext(),true);
            return response;
        }

        protected void onPostExecute(String response) {

            //System.out.print(response);
        }

    }

    private class putDisableFlagForLocGPSTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {
            String response = MyHttpClient.putEnableFlagForLocGPS(getApplicationContext(),false);
            return response;
        }

        protected void onPostExecute(String response) {

            //System.out.print(response);
        }

    }

    private class getGPSCoordsTask2 extends AsyncTask<String, Void, String[]> {

        protected String[] doInBackground(String... userid) {
            // idcoords[0] contains the userid, idcoords[1] contains the coordinates. String type.
            String[] idcoords = new String[2];

            idcoords[0] = userid[0];

            String uuid = MyHttpClient.getUUID(userid[0]);
            idcoords[1] = MyHttpClient.getGPSCoords(uuid);

            return idcoords;
        }

        protected void onPostExecute(String[] idcoords) {

            if (userMarker != null)
            {
                userMarker.remove();
            }

            if (idcoords[1].equals(""))
            {
                return;
            }
            else
            {
                String[] latlon = idcoords[1].split(",");
                double lat = Double.parseDouble(latlon[0]);
                double lon = Double.parseDouble(latlon[1]);
                LatLng gpsLoc = new LatLng(lat,lon);

                // Add the marker
                userMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(idcoords[0]));
            }

        }

    }

    private class getGPSCoordsTask1 extends AsyncTask<String, Void, String[]> {

        protected String[] doInBackground(String... userid) {
            // idcoords[0] contains the userid, idcoords[1] contains the coordinates. String type.
            String[] idcoords = new String[2];

            idcoords[0] = userid[0];

            String uuid = MyHttpClient.getUUID(userid[0]);
            idcoords[1] = MyHttpClient.getGPSCoords(uuid);

            return idcoords;
        }

        protected void onPostExecute(String[] idcoords) {

            if (userMarker != null)
            {
                userMarker.remove();
            }

            if (idcoords[1].equals(""))
            {
                Toast.makeText(MapsActivity.this, "Error: " +idcoords[0] +" could not be found. Server may be down or user is not stored in database.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                try {
                    String[] latlon = idcoords[1].split(",");
                    double lat = Double.parseDouble(latlon[0]);
                    double lon = Double.parseDouble(latlon[1]);
                    LatLng gpsLoc = new LatLng(lat, lon);
                    // Construct a CameraPosition focusing on userid location
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(gpsLoc)      // Sets the center of the map to Mountain View
                            .zoom(14)                   // Sets the zoom
                            .build();                   // Creates a CameraPosition from the builder

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    // Add the marker
                    userMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(idcoords[0]));

                    // Make toast
                    Toast.makeText(MapsActivity.this, idcoords[0] +" has been found.", Toast.LENGTH_SHORT).show();

                }
                catch (NumberFormatException e)
                {
                    e.printStackTrace();
                }

            }

            }

    }

    private class PushGPSCoordsTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... coords) {

            // Makes the HTTP Post request to create contentInstance with coords in it in AE Tree
            String params = "";
            params = MyHttpClient.createLocGPSContentInstance(getApplicationContext(),coords[0]);

            return params;
        }

        protected void onPostExecute(String params) {

        }

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        new putDisableFlagForLocGPSTask().execute();
    }
}
