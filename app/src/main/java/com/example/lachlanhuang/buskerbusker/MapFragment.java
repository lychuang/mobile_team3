package com.example.lachlanhuang.buskerbusker;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.lachlanhuang.buskerbusker.database.BuskEvent;
import com.example.lachlanhuang.buskerbusker.database.TimeDate;
import com.example.lachlanhuang.buskerbusker.database.User;
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
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
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
    private Marker touchMarker;


    private HashMap<String, Marker> buskerMarkerHashMap;
    private HashMap<String, BuskEvent> buskEventHashMap;

    private Marker geoLocateMarker;

    private User mUser = new User();

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





    private DatePickerDialog datePickerDialog;

    //variables for time/date
    int mYear;
    int mMonth;
    int mDay;

    int mHour;
    int mMinute;

    int mStartHour;
    int mStartMin;
    int mStartYear;
    int mStartMonth;
    int mStartDay;


    Calendar mCalendar = new GregorianCalendar();
    TimeZone mTimeZone = mCalendar.getTimeZone();
    int timeZone = mTimeZone.getRawOffset();


    String mDescription;

    boolean settingDone = true;


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



    public void setmUser(User user) {

        this.mUser = user;
    }

    public void setmUserId(String uid) {

        this.mUser.setId(uid);
    }

    public String getmUserId() {

        return this.mUser.getId();
    }



    private void descriptionRetriever(final MyLatLng latLng) {


        final EditText txtUrl = new EditText(mContext);

        txtUrl.setHint("Whatcha gonna do?");

        new AlertDialog.Builder(mContext)
                .setTitle("Set Description")
                .setMessage("Write a description of your performance!")
                .setView(txtUrl)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        mDescription = txtUrl.getText().toString();
                        Log.d("TAGA", "Description get");
                        settingDone = true;

                        TimeDate start = new TimeDate(mStartYear, mStartMonth, mStartDay, mStartHour, mStartMin);
                        TimeDate end = new TimeDate(mStartYear, mStartMonth, mStartDay, mHour, mMinute);

                        Calendar mCalendar = new GregorianCalendar();
                        TimeZone mTimeZone = mCalendar.getTimeZone();
                        timeZone = mTimeZone.getRawOffset();

                        BuskEvent buskEvent = new BuskEvent(getmUserId(), getmUserId(), latLng,
                                start, end, timeZone, mDescription);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        dialog.cancel();
                        settingDone = true;
                    }
                })
                .show();
    }




    private void datePicker(final MyLatLng latLng){

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        mStartYear = year;
                        mStartMonth = monthOfYear;
                        mStartDay = dayOfMonth;
                        //*************Call Time Picker Here ********************
                        timePicker(latLng, true);
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.setTitle("MOO");
        datePickerDialog.show();


    }


    private void timePicker(final MyLatLng latLng, boolean start){
        // Get Current Time
        final boolean startPick = start;
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        //mStartHour = mHour;
        //mStartMin = mMinute;

        //TimePickerDialog p = new TimePickerDialog.Builder(mContext)
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        if (startPick) {

                            mHour = hourOfDay;
                            mMinute = minute;

                            mStartHour = mHour;
                            mStartMin = mMinute;

                            timePicker(latLng, false);

                        } else {

                            mHour = hourOfDay;
                            mMinute = minute;
                            ///////
                            descriptionRetriever(latLng);
                        }




                    }
                }, mHour, mMinute, false);

        timePickerDialog.show();

        if (start) {

            /**LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.text_layout, null);
            TextView texts=(TextView) dialogView.findViewById(R.id.textss);
            texts.setText("Set Start Time");
            timePickerDialog.setCustomTitle(dialogView);*/

            Log.d("TAGA", "SET CUSTOM TITLE");
            timePickerDialog.setTitle("Choose Start Time");

        } else {
            timePickerDialog.setTitle("Choose End Time");
        }

    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(mContext);


        /**if (mapView != null) {

            mapView = inflater.inflate(R.layout.fragment_map, null);
            return mapView;
        }**/

        mapView = inflater.inflate(R.layout.fragment_map, null, false);
        mSearchText = (AutoCompleteTextView) mapView.findViewById(R.id.input_search);

        buskerMarkerHashMap = new HashMap<>();
        buskEventHashMap = new HashMap<>();

        Bundle args = getArguments();

        String uid = args.getString("USER_ID", "");

        mUser = new User();
        setmUserId(uid);

        Log.d("TAGA", getmUserId());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mUserRef = database.getReference().child("user").child(mUser.getId());

        // Attach a listener to read the data at our posts reference
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                setmUser(dataSnapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        updateDisplay();




        Button button = (Button) mapView.findViewById(R.id.share_location);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {

                if (v.getId() == R.id.share_location) {

                    Log.d("COOOWEEE", "CLICK");
                    if (touchMarker == null) {

                        Log.d("COOOWEEE", "not null");
                        MyLatLng latLng = new MyLatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                        //Log.d("TAGA", getmUserId());

                        settingDone = false;
                        datePicker(latLng);

                        /**if (settingDone) {
                            BuskEvent buskEvent = new BuskEvent(getmUserId(), getmUserId(), latLng, mYear,
                                    mMonth, mDay, mHour, mMinute, mDescription);
                        }**/

                    } else {

                        MyLatLng latLng = new MyLatLng(touchMarker.getPosition().latitude,
                                                        touchMarker.getPosition().longitude);
                        //Log.d("TAGA", getmUserId());



                        settingDone = false;
                        datePicker(latLng);

                        //Log.d("TAGA", "moo" + mDay + "mee" + mHour + mDescription);
                        /**if (settingDone) {
                            BuskEvent buskEvent = new BuskEvent(getmUserId(), getmUserId(), latLng, mYear,
                                    mMonth, mDay, mHour, mMinute, mDescription);
                        }**/

                    }

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


    //return 0 if future
    //       1 if live
    //       -1 if passed
    private int isBuskEventNow(BuskEvent event) {

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        //Log.d("TAGA", "testing");

        int dateCode = (mYear * 10000) + (mMonth * 100) + mDay;
        int timeCode = mHour * 60 + mMinute;


        int timeZoneEffect = (timeZone / 60000) - (event.getTimeZone() / 60000); // mins different

        Log.d("TAGA", String.format("timezone %d, event tz %d", timeZone, event.getTimeZone()));

        int startDateCode = (event.getStartTime().getmYear() * 10000)
                                + (event.getStartTime().getmMonth() * 100)
                                + event.getStartTime().getmDay();

        int startTimeCode = event.getStartTime().getmHour()*60 + event.getStartTime().getmMinute();

        int endTimeCode = event.getEndTime().getmHour()*60 + event.getEndTime().getmMinute();


        //timezone management
        int shiftedStart = startTimeCode + timeZoneEffect;
        int shiftedEnd = endTimeCode + timeZoneEffect;

        if (shiftedStart < 0) {

            startDateCode -= 1;
            startTimeCode = 24 * 60 + shiftedStart;
            endTimeCode = 24 * 60 + shiftedEnd;
        } else if (shiftedStart > (24 * 60 - 1)) {

            startDateCode += 1;
            startTimeCode = shiftedStart - 24 * 60;
            endTimeCode = shiftedEnd - 24 * 60;
        } else {

            startTimeCode = shiftedStart;
            endTimeCode = shiftedEnd;
        }

        //Log.d("TAGA", String.format("date: %d vs %d time: %d in %d to %d", dateCode, startDateCode, timeCode, startTimeCode, endTimeCode));

        Log.d("TAGA", String.format("timezone: %d", timeZone));


        Log.d("TAGA", String.format("date: %d vs %d", dateCode, startDateCode));
        if (dateCode == startDateCode) { //today


            if ((timeCode >= startTimeCode) &&
                    timeCode <= endTimeCode) { //in the window

                return 1;

            } else if (timeCode > endTimeCode) { //past event

                return -1;
            } else { //future event

                return 0;
            }

        } else if (dateCode > startDateCode) {

            return -1; //past
        } else {

            return 0; //future date
        }

    }


    private void updateDisplay() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();

                for(Map.Entry<String, BuskEvent> entry : buskEventHashMap.entrySet()) {
                    String key = entry.getKey();
                    BuskEvent event = entry.getValue();

                    int now = isBuskEventNow(event);

                    switch (now) {

                        case 1:
                            //it's on baby
                            //maybe set flag in database?
                            event.setLive(true);
                            myRef.child("buskEvent").child(event.getUserId()).setValue(event);
                            break;

                        case 0:
                            //in the future, leave it
                            break;

                        case -1:
                            //should delete from database, check if that removes marker?
                            //also need to delete from hashmap etc
                            myRef.child("buskEvent").child(event.getUserId()).removeValue();
                            break;
                    }
                }

            }

        },0,5000);//Update text every second
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


        //BuskerLocation b1 = new BuskerLocation("1", "Lachlan", brisbane);

        //BuskerLocation b2 = new BuskerLocation("2", "Tram's Dad", boston);


        mMap.setInfoWindowAdapter(new InfoWindowAdapter(this.mContext));


        //mMap.animateCamera(CameraUpdateFactory.newLatLng());




        // Get a reference to our posts
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("buskEvent");


        //final List<BuskerLocation> buskerList = new ArrayList<>();

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //BuskerLocation bl = new BuskerLocation();
                BuskEvent bl = dataSnapshot.getValue(BuskEvent.class);
                //buskerList.add(bl);


                MarkerOptions mo = new MarkerOptions();

                mo.position(bl.getLatLng().convertToMapsLatLng());
                mo.title(bl.getUsername());

                String sTime = "12:30";
                String eTime = "idk";
                String duration = "1";

                String description = "Lel dis gon be gud hehe!!";

                sTime = String.format("%02d:%02d", bl.getStartTime().getmHour(), bl.getStartTime().getmMinute());
                eTime = String.format("%02d:%02d", bl.getEndTime().getmHour(), bl.getEndTime().getmMinute());

                description = bl.getmDescription();

                String snippet = "Start Time: " + sTime + "\n" + "End Time: " + eTime + "\n" +
                        "Description: " + description;

                mo.snippet(snippet);

                if (bl.isLive()) {

                    Marker buskMarker = mMap.addMarker(mo);
                    //add the updated marker into the Hash Table
                    buskerMarkerHashMap.put(bl.getUserId(), buskMarker); //put overrides old value
                }


                //store the event
                buskEventHashMap.put(bl.getUserId(), bl);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //here we should
                //1. remove the marker
                //2. add the updated position marker

                BuskEvent bl = dataSnapshot.getValue(BuskEvent.class);

/**
                if (bl.isLive()) {

                    buskerMarkerHashMap.get(bl.getUserId()).isVisible();
                }
                //remove the marker
                buskerMarkerHashMap.get(bl.getUserId()).remove();

**/
                //create new marker, add it
                MarkerOptions mo = new MarkerOptions();

                mo.position(bl.getLatLng().convertToMapsLatLng());
                mo.title(bl.getUsername());

                String sTime = "12:30";
                String eTime = "idk";
                String duration = "1";

                String description = "Lel dis gon be gud hehe!!";

                sTime = String.format("%02d:%02d", bl.getStartTime().getmHour(), bl.getStartTime().getmMinute());
                eTime = String.format("%02d:%02d", bl.getEndTime().getmHour(), bl.getEndTime().getmMinute());

                description = bl.getmDescription();

                String snippet = "Start Time: " + sTime + "\n" + "End Time: " + eTime + "\n" +
                        "Description: " + description;

                mo.snippet(snippet);


                if (bl.isLive()) {

                    Marker buskMarker = mMap.addMarker(mo);
                    //add the updated marker into the Hash Table
                    buskerMarkerHashMap.put(bl.getUserId(), buskMarker); //put overrides old value
                }


                buskEventHashMap.put(bl.getUserId(), bl);

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                BuskEvent bl = dataSnapshot.getValue(BuskEvent.class);
                buskEventHashMap.remove(bl.getUserId());

                //remove the marker from map and hashmap
                buskerMarkerHashMap.get(bl.getUserId()).remove();
                buskerMarkerHashMap.remove(bl.getUserId());

                Log.d("TAGA", "removed a busk event");


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


        //TOUCH MARKER INIT

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                if (touchMarker != null) {

                    touchMarker.remove();
                }

                touchMarker = mMap.addMarker(new MarkerOptions().position(point).title("Touched Here").snippet(" "));
            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {


            @Override
            public boolean onMarkerClick(Marker marker) {

                if (!marker.isInfoWindowShown()) {

                    marker.showInfoWindow();
                    Log.d("TAGA", "MOOOOOOOO");
                } else {

                    marker.hideInfoWindow();
                }

                return false;
            }
        });


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
        markerOptions.snippet("You are here!");
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



