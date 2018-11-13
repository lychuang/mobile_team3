package com.example.lachlanhuang.buskerbusker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MapFragment extends Fragment implements OnMapReadyCallback,
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener,
            LocationListener {


    protected GeoDataClient mGeoDataClient;

    //Interface Variables
    private GoogleMap mMap;
    private View mapView;


    //Location Requesting Variables
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;

    //Other Marker Variables
    private ArrayList<Marker> markers;
    private int markerCount = 0;

    private HashMap<String, Marker> buskerMarkerHashMap;

    private Marker geoLocateMarker;

    //CHANGE THIS LATER//
    ///
    ///
    ///
    ///
    private int idCount = 3;

    private KeyEvent keyEvent;

    //widgets
    private AutoCompleteTextView mSearchText;

    private PlaceAutocompleteAdapter placeAutocompleteAdapter;

    public final static int REQUEST_LOCATION_CODE = 99;

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136)
    );


    private PlaceInfo mPlaceInfo;


    //this is required since this is a fragment
    private Context mContext;



    public static MapFragment newInstance() {

        Bundle args = new Bundle();

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.mContext = context;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(mContext);

        View mapView = inflater.inflate(R.layout.fragment_map, null);
        mSearchText = (AutoCompleteTextView) mapView.findViewById(R.id.input_search);

        buskerMarkerHashMap = new HashMap<>();

        Button button = (Button) mapView.findViewById(R.id.share_location);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {

                if (v.getId() == R.id.share_location) {

                    MyLatLng latLng = new MyLatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    String id = String.valueOf(idCount);
                    idCount++;

                    BuskerLocation bl = new BuskerLocation(id, "Jeremy", latLng);
                }

            }

        });

        this.mapView = mapView;

        return mapView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    private void init() {

        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(mContext, mGeoDataClient, LAT_LNG_BOUNDS,null);

        mSearchText.setAdapter(placeAutocompleteAdapter);

        mSearchText.setOnItemClickListener(mAutoCompleteListener);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                //execute method for searching
                geoLocate();
            }

                return false;
            }
        });
    }



    private void geoLocate() {

        //retrieve the search text
        String searchString = mSearchText.getText().toString();
        List<Address> addressList = new ArrayList<>();

        MarkerOptions mo = new MarkerOptions();

        Geocoder geocoder = new Geocoder(this.mContext);
        try {

            addressList = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {

            e.printStackTrace();
        }

        //remove the marker from previous search
        if (geoLocateMarker != null) {

            geoLocateMarker.remove();
        }

        for (int i = 0; i < addressList.size(); i++) {

            Address myAddress = addressList.get(i);
            LatLng latlng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
            mo.position(latlng);
            mo.title(myAddress.getAddressLine(0));
            geoLocateMarker = mMap.addMarker(mo);
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
        }


    }


    /**
     * This function will generate a unique id if the user wishes to add a new busker marker to the map/database
     * @return
     */
    private String generateUUID() {

        final String uuid = UUID.randomUUID().toString().replace("-", "");
        System.out.println("uuid = " + uuid);

        return uuid;
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission is granted
                    if (ContextCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if (client == null) {

                            //create a client
                            buildGoogleApiClient();
                        }

                        mMap.setMyLocationEnabled(true);
                    }
                } else {

                    Toast.makeText(this.mContext, "Permission Denied!", Toast.LENGTH_LONG).show();
                }
                return;
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (ContextCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));


        MyLatLng brisbane = new MyLatLng(-28.0, 153.0);
        MyLatLng boston = new MyLatLng(42.0, -71.0);


        BuskerLocation b1 = new BuskerLocation("1", "Lachlan", brisbane);

        BuskerLocation b2 = new BuskerLocation("2", "Tram's Dad", boston);


        // Get a reference to our posts
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("busker");


        //final List<BuskerLocation> buskerList = new ArrayList<>();

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //BuskerLocation bl = new BuskerLocation();
                BuskerLocation bl = dataSnapshot.getValue(BuskerLocation.class);
                //buskerList.add(bl);


                MarkerOptions mo = new MarkerOptions();

                mo.position(bl.getLatLng().convertToMapsLatLng());
                mo.title(bl.getUsername());

                Marker buskMarker = mMap.addMarker(mo);

                //add the new child to the Hash Table
                buskerMarkerHashMap.put(bl.getUserId(), buskMarker);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //here we should
                //1. remove the marker
                //2. add the updated position marker

                BuskerLocation bl = dataSnapshot.getValue(BuskerLocation.class);

                //remove the marker
                buskerMarkerHashMap.get(bl.getUserId()).remove();

                //create new marker, add it
                MarkerOptions mo = new MarkerOptions();

                mo.position(bl.getLatLng().convertToMapsLatLng());
                mo.title(bl.getUsername());

                Marker buskMarker = mMap.addMarker(mo);

                //add the updated marker into the Hash Table
                buskerMarkerHashMap.put(bl.getUserId(), buskMarker); //put overrides old value

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }

    }


    protected synchronized void buildGoogleApiClient() {

        client = new GoogleApiClient.Builder(this.mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .build();

        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        //store the new location in class variable - as backup
        lastLocation = location;

        //we need to remove the previous location marker on the map
        if (currentLocationMarker != null) {

            currentLocationMarker.remove();
        }

        //create the new marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //can change color and type of marker later
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location"); //any name will do
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

        //add it to the map
        currentLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        //stop updating for now
        if (client != null) {

            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();

        // initialise the frequency of polling - arbitrary values atm
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        // need to wrap the request in a permission check
        if (ContextCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }


    }







/*
public void onClick (View v) {

    List<Address> addressList = new ArrayList<>();

    if (v.getId() == R.id.b_search) {

        EditText tf_location = (EditText) findViewById(R.id.TF_location);
        String location = tf_location.getText().toString();


        MarkerOptions mo = new MarkerOptions();

        if (!location.equals("")) {

            Geocoder geocoder = new Geocoder(this);
            try {

                addressList = geocoder.getFromLocationName(location, 5);
            } catch (IOException e) {

                e.printStackTrace();
            }

            for (int i = 0; i < addressList.size(); i++) {

                Address myAddress = addressList.get(i);
                LatLng latlng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                mo.position(latlng);
                mMap.addMarker(mo);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
            }
        }
    }
}
*/

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getLocality()).append("\n");
                result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }




    /*
    * ---------------------- GOOGLE PLACES API AUTOCOMPLETE SUGGESTIONS -----------------
     */

    private AdapterView.OnItemClickListener mAutoCompleteListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

            final String placeId;
            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(i);
            if (item != null) {
                placeId = item.getPlaceId();

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(client, placeId);
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            }
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {


        @Override
        public void onResult(@NonNull PlaceBuffer places) {

            if (!places.getStatus().isSuccess()) {

                places.release();
                return;
            }

            final Place place = places.get(0);

            mPlaceInfo = new PlaceInfo();
            mPlaceInfo.setAddress(place.getAddress().toString());
            //mPlaceInfo.setAttributions(place.getAttributions().toString());
            mPlaceInfo.setId(place.getId());
            mPlaceInfo.setLatLng(place.getLatLng());
            mPlaceInfo.setName(place.getName().toString());
            mPlaceInfo.setPhoneNumber(place.getPhoneNumber().toString());
            mPlaceInfo.setRating(place.getRating());
            mPlaceInfo.setWebsiteUri(place.getWebsiteUri());

            //remove the marker from previous search
            if (geoLocateMarker != null) {

                geoLocateMarker.remove();
            }

            MarkerOptions mo = new MarkerOptions();
            mo.position(mPlaceInfo.getLatLng());
            mo.title(mPlaceInfo.getName());
            geoLocateMarker = mMap.addMarker(mo);

            mMap.animateCamera(CameraUpdateFactory.newLatLng(mPlaceInfo.getLatLng()));

        }
    };


}



