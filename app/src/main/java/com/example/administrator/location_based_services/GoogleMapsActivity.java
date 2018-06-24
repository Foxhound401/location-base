package com.example.administrator.location_based_services;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.stats.ConnectionEventCreator;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;

public class GoogleMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Button btStyle;
    private int state;
    private GoogleApiClient locationClient;
    private Location currentLocation;
    private static final LatLng BEN_THANH_MARKET = new LatLng(10.7731, 106.6983);
    private static final LatLng SAIGON_OPERA_HOUSE = new LatLng(10.7767, 106.7032);
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng p;
    private int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        state = 0;
        status = 0;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {


        return super.onCreateView(name, context, attrs);
    }

    public void clickToFind(View view) {
        EditText txtFind = (EditText) findViewById(R.id.edLocation);
        String strLocation = txtFind.getText().toString();
        if (strLocation != null && !strLocation.trim().equals("")) {
            new GeocoderTask().execute(strLocation);
        }
    }

    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            Geocoder geo = new Geocoder(getBaseContext());
            List<Address> address = null;
            try {
                address = geo.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return address;
        }

        @Override
        protected void onPostExecute(List<Address> result) {
            if (result == null || result.size() == 0) {
                Toast.makeText(getBaseContext(), "Not Found", Toast.LENGTH_SHORT).show();
                return;
            }
            if (map != null) {
                map.clear();
                for (int i = 0; i < result.size(); i++) {
                    Address address = (Address) result.get(i);

                    LatLng findPos = new LatLng(address.getLatitude(), address.getLongitude());
                    String addessText = String.format("%s %s",
                            address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                            address.getCountryName());
                    MarkerOptions mo = new MarkerOptions();
                    mo.position(findPos);
                    mo.title(addessText);

                    map.addMarker(mo);
                    if (i == 0) {
                        map.animateCamera(CameraUpdateFactory.newLatLng(findPos));
                    }
                }
            }

        }
    }

    public void clickToChangeStyle(View view) {
        String title = "";
        switch (status) {
            case 0:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                title = "Normal - Change to Hybrid";
                status = 1;
                break;
            case 1:
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                title = "Hybrid - Change to Satellite";
                status = 2;
                break;
            case 2:
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                title = "Satellite - Change to None";
                status = 3;
                break;
            case 3:
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                title = "Terrain - Change to None";
                status = 4;
                break;
            case 4:
                map.setMapType(GoogleMap.MAP_TYPE_NONE);
                title = "None - Change to Normal";
                status = 0;
                break;
            default:
                map.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
        if (btStyle != null) {
            btStyle.setText(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_showtraffic:
                map.setTrafficEnabled(true);
                break;
            case R.id.menu_zoomin:
                map.animateCamera(CameraUpdateFactory.zoomIn());
            case R.id.menu_zoomout:
                map.animateCamera(CameraUpdateFactory.zoomOut());
            case R.id.menu_gotoLocation:
                CameraPosition cameraPos = new CameraPosition.Builder().target(BEN_THANH_MARKET)
                        .zoom(17).bearing(90).tilt(30).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
                map.addMarker(new MarkerOptions().position(BEN_THANH_MARKET)
                        .title("Ben Thanh Market").snippet("HCM City")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background)));
                break;
            case R.id.menu_showcurrentLocation:
                currentLocation = map.getMyLocation();
                LatLng currentPos = new LatLng(currentLocation.getLatitude(),
                        currentLocation.getLongitude());
                CameraPosition myPosition = new CameraPosition.Builder().target(currentPos)
                        .zoom(17).bearing(90).tilt(30).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(myPosition));
                break;
            case R.id.menu_Lineconnecttwopoins:
                map.addMarker(new MarkerOptions().position(SAIGON_OPERA_HOUSE).title
                        ("SaiGon Opera House").snippet("HCM City").
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                map.addPolyline(new PolylineOptions().add(BEN_THANH_MARKET, SAIGON_OPERA_HOUSE)
                        .width(5).color(Color.RED));
                break;
            case R.id.menu_getLocationData:
                locationListener = new MyLocationListener();
                status = 1;
            case R.id.menu_gotoLocationonTouch:
                map.setOnMapClickListener(new MyOnMapClickListener());
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (status != 0) {
            //locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,0,0,locationListener);
            //locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER,0,0,locationListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (status != 0) {
            if (locationListener != null) {
                locationManager.removeUpdates(locationListener);
            }
        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.d("ddd", "dddd");
                Toast.makeText(getBaseContext(), "Position: " + location.getLatitude() +
                        ":" + location.getLongitude(), Toast.LENGTH_SHORT).show();
                p = new LatLng(location.getLatitude(), location.getLongitude());
                map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(p, 18));
                    }
                });
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            String strStatus = "";
            switch (status) {
                case LocationProvider.AVAILABLE:
                    strStatus = "Available";
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    strStatus = "Out of Service";
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    strStatus = "Temporarity Unavailable";
                    break;
            }
            Toast.makeText(getBaseContext(), provider + " " + strStatus, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getBaseContext(), "Disabled provider " +
                    provider, Toast.LENGTH_SHORT).show();
            ;
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getBaseContext(), "Enabled provider " +
                    provider, Toast.LENGTH_SHORT).show();
            ;

        }
    }

    private class MyOnMapClickListener implements GoogleMap.OnMapClickListener {

        @Override
        public void onMapClick(LatLng latLng) {
            Toast.makeText(getBaseContext(), "Position: " + latLng.latitude + ":" +
                    latLng.longitude, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setCompassEnabled(true);
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        } else {
            //map.setMyLocationEnabled(true);
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            List<String> pr = locationManager.getProviders(true);
            currentLocation = null;
            for (int i = 0; i < pr.size(); i++) {
                //currentLocation = locationManager.getLastKnownLocation(pr.get(i));
                if (currentLocation != null) {
                    LatLng currentPos = new LatLng(currentLocation.getLatitude()
                            , currentLocation.getLongitude());
                    Log.d("aaa", "bbb");
                    Marker currentMarker = map.addMarker(new MarkerOptions().position(currentPos)
                            .title("My Position").snippet("Mobile Programing")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background)));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 15));
                    map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                }
            }

        }

    }
}
