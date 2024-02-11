package com.example.firstfix.customer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothClass;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.firstfix.MainActivity;
import com.example.firstfix.R;
import com.example.firstfix.databinding.ActivityCustomerMainBinding;
import com.example.firstfix.databinding.ActivityMainBinding;
import com.example.firstfix.databinding.FragmentCustomerMapsBinding;
import com.example.firstfix.serviceworker.ServiceManActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomerMapsFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        RoutingListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;
    FragmentCustomerMapsBinding binding;

    int radouis = 3;
    List<String> DriverAvailableList = new ArrayList<>();

    ProgressDialog progressDialog;

    DatabaseReference ServiceManAvabilityRef, ServiceManWorkingRef;

    Marker serviceManMarker;
    boolean OpenUsersChat = true;

    FirebaseAuth mAuth;

    FusedLocationProviderClient fusedLocationClient;

    String receivedData;
    String reciverName;
    String reciverImage;
    float distance;
    private List<Polyline> polylines=null;

    LatLng usersLOcation,servicemanLocation;
    DatabaseReference getRefFromUsers;

    String currentUserId;

    private ValueEventListener ServiceManLocationRefListner;
    GeoFire  geoFire;
    GeoQuery geoQuery;

    public static String child = "child";

    boolean openChat = true;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        // Inflate the layout using data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_maps, container, false);

        // Bind the fragment instance to the layout
        binding.setCustomerMap(this);

        ServiceManAvabilityRef = FirebaseDatabase.getInstance().getReference("FastFix").child("Service man available").child(child);

        String currrentUser = FirebaseAuth.getInstance().toString();
        // Retrieve data from arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            receivedData = bundle.getString("Accepted");
            child = bundle.getString("child");
            // Now you have the data in 'receivedData'
            // Do something with the data

            GetClosestDrivrLocation(receivedData);
            ShowServiceManiNformattion();


        }

        GetFCMtoken();
        askNotificationPermission();

        progressDialog = new ProgressDialog(getContext());
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();



        return binding.getRoot();

    }

    private void showALlNearestServiceMan(LatLng latLng) {
        GeoFire geoFire = new GeoFire(ServiceManAvabilityRef);

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), radouis);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {


                String driverID = dataSnapshot.getKey();

                DriverAvailableList.clear();

                DriverAvailableList.add(driverID);

                for (String serviceman : DriverAvailableList) {

                    ShowServiceManLocation(serviceman);

                }

            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


    }

    private void ShowServiceManLocation(String serviceman) {

        ServiceManAvabilityRef.child(serviceman).child("l").addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {

                  if (snapshot.exists()) {
                      List<Object> driverLocationMap = (List<Object>) snapshot.getValue();

                      double LocationLat = 0;
                      double LocationLon = 0;
                      //binding.customerCallDriver.setText("Driver Found");

                      if (driverLocationMap.get(0) != null) {
                          LocationLat = Double.parseDouble(driverLocationMap.get(0).toString());
                      }

                      if (driverLocationMap.get(1) != null) {
                          LocationLon = Double.parseDouble(driverLocationMap.get(1).toString());
                      }

                      LatLng ServniceManLatLan = new LatLng(LocationLat, LocationLon);
                      if (serviceManMarker != null) {
                          serviceManMarker.remove();
                      }

                      Location location2 = new Location("");
                      location2.setLatitude(ServniceManLatLan.latitude);
                      location2.setLongitude(ServniceManLatLan.longitude);

                      LatLng servicemanLocation = new LatLng(location2.getLatitude(), location2.getLongitude());

                      serviceManMarker = mMap.addMarker(new MarkerOptions().position(servicemanLocation).
                              title("Your service man location").icon(BitmapDescriptorFactory.fromResource(R.drawable.servicemanicon)));

                  }


              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {
                  Log.d("CustomerMapConnet", "onCancelled:"+error.getMessage().toString());
              }
          });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        lastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        mMap.addMarker(new MarkerOptions().position(latLng).title("Customer Pick up Location"));

        DatabaseReference CustomerAvabilityRef = FirebaseDatabase.getInstance().getReference("FastFix").child("Customer Available").child(child);
        String currentCustomerUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        geoFire = new GeoFire(CustomerAvabilityRef);
        geoFire.setLocation(currentCustomerUserID, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()));
        if(receivedData==null){

            showALlNearestServiceMan(latLng);
        }




    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        buildGOogleApiClient();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);


    }

    protected synchronized void buildGOogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    //call driver functtion
    public void CallDriverFuncation() {

        if(receivedData!=null){
            ShowServiceManDataDIalogBox();

            //after getting the reciver id it will show reciver dilaog box;
        }
        else{

            DatabaseReference CustomerAvabilityRef = FirebaseDatabase.getInstance().getReference("FastFix").child("Customer Request").child(child);
            String currentCustomerUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            geoFire = new GeoFire(CustomerAvabilityRef);

            geoFire.setLocation(currentCustomerUserID, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()));
            usersLOcation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
//            mMap.addMarker(new MarkerOptions().position(usersLOcation).title("Pick up customer from here"));
            progressDialog.setTitle("FastFix");
            progressDialog.setMessage("Please wiat for accept your request");
            progressDialog.show();
            GetClosestDrivrCab();
            OpenChatActivity();

        }

    }

    //get closest driver location
    private void GetClosestDrivrCab() {

        GeoFire geoFire = new GeoFire(ServiceManAvabilityRef);

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(usersLOcation.latitude, usersLOcation.longitude), radouis);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {


                String driverID = dataSnapshot.getKey();

                DriverAvailableList.clear();

                DriverAvailableList.add(driverID);

                for (String serviceman : DriverAvailableList) {

                    GetDriverDeviceToken(serviceman);
                    ShowServiceManLocation(serviceman);
                }

            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void GetClosestDrivrLocation(String receivedData) {
        ServiceManWorkingRef = FirebaseDatabase.getInstance().getReference("FastFix").child("Service man working").child(child);
        ServiceManLocationRefListner = ServiceManWorkingRef.child(receivedData).child("l")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            List<Object> driverLocationMap = (List<Object>) snapshot.getValue();

                            double LocationLat = 0;
                            double LocationLon = 0;
                            //binding.customerCallDriver.setText("Driver Found");

                            if (driverLocationMap.get(0) != null) {
                                LocationLat = Double.parseDouble(driverLocationMap.get(0).toString());
                            }

                            if (driverLocationMap.get(1) != null) {
                                LocationLon = Double.parseDouble(driverLocationMap.get(1).toString());
                            }

                            LatLng ServniceManLatLan = new LatLng(LocationLat, LocationLon);
                            if (serviceManMarker != null) {
                                serviceManMarker.remove();
                            }

                            Location location2 = new Location("");
                            location2.setLatitude(ServniceManLatLan.latitude);
                            location2.setLongitude(ServniceManLatLan.longitude);

                           servicemanLocation = new LatLng(location2.getLatitude(),location2.getLongitude());
                           if(mMap!=null){
                               serviceManMarker = mMap.addMarker(new MarkerOptions().position(servicemanLocation).
                                       title("Your service man location").icon(BitmapDescriptorFactory.fromResource(R.drawable.servicemanicon)));
                           }


                            //getCustomerLocation()
                            UsersLocation();


                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void UsersLocation() {
         DatabaseReference usersLocationref = FirebaseDatabase.getInstance().getReference().child("FastFix").child("Customer Available").child(child)
                .child(currentUserId).child("l");

        usersLocationref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    List<Object> CustomerLocationMap = (List<Object>) snapshot.getValue();

                    double LocationLat = 0;
                    double LocationLon = 0;

                    if(CustomerLocationMap.get(0)!=null){
                        LocationLat = Double.parseDouble(CustomerLocationMap.get(0).toString());
                    }

                    if(CustomerLocationMap.get(1)!=null){
                        LocationLon = Double.parseDouble(CustomerLocationMap.get(1).toString());

                    }

                    Location  usersLocation = new Location("");
                    usersLocation.setLatitude(LocationLat);
                    usersLocation.setLongitude(LocationLon);

                    usersLOcation = new LatLng(usersLocation.getLatitude(),usersLocation.getLongitude());


                    Findroutes();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //fetch service man data from firebase
    private void ShowServiceManiNformattion() {

        DatabaseReference CutomerRef = FirebaseDatabase.getInstance().getReference("FastFix").child("Service Man ").child(child);
        CutomerRef.child(receivedData).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if (snapshot.hasChild("image")){
                        reciverImage = snapshot.child("image").getValue(String.class);
                        reciverName = snapshot.child("name").getValue(String.class);
                        ShowServiceManDataDIalogBox();
                        binding.CallButton.setText(reciverName + " pick up your work ");
                    }
                    else {
                        reciverName = snapshot.child("name").getValue(String.class);
                        ShowServiceManDataDIalogBox();
                        binding.CallButton.setText(reciverName  + " pick up your work ");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //show service man data at dialog box

    public void ShowServiceManDataDIalogBox(){


        DatabaseReference MessageRef = FirebaseDatabase.getInstance().getReference("FastFix");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.alart_dialog_service_man,null);

        TextView textName = dialogView.findViewById(R.id.alart_dilog_sermiceman_name);
        ImageView imagePic = dialogView.findViewById(R.id.alart_dilog_sermicemanImg);

        textName.setText(reciverName+" picked your work");

        if(reciverImage!=null){
            Picasso.get().load(reciverImage).into(imagePic);
        }
        alertDialogBuilder.setPositiveButton("Finish",(dialogInterface ,which )->{

            String messageSenderRef = "Messages /"+child+"/" + currentUserId ;
            DatabaseReference userMessagesRef = MessageRef.child(messageSenderRef);
            userMessagesRef.removeValue();

            String messageReciveRef = "Messages /"+child+"/"  + receivedData  ;
            DatabaseReference reciverMessagesRef = MessageRef.child(messageReciveRef );
            reciverMessagesRef.removeValue();


            DatabaseReference RequestAccptedRef = FirebaseDatabase.getInstance().getReference("FastFix").
                    child("Working Accepted").child(receivedData);
            RequestAccptedRef.removeValue();

            binding.CallButton.setText("Call service man");
            DatabaseReference usersLocationref = FirebaseDatabase.getInstance().getReference().child("FastFix").child("Customer Available").child(child).child(currentUserId);
            usersLocationref.removeValue();

           DatabaseReference getRefFromUsers  = FirebaseDatabase.getInstance().getReference("FastFix").child("Working Accepted").child(child);
            getRefFromUsers.removeValue();

            ServiceManWorkingRef.removeEventListener(ServiceManLocationRefListner);

            receivedData =null;
                 FragmentManager MapFragmentManager = getFragmentManager();
            FragmentTransaction MapFragmentTransaction = MapFragmentManager.beginTransaction();

            MapFragmentTransaction.replace(R.id.addCustomerMap,new AllServiceitemFragment());
            MapFragmentTransaction.commit();


        });

        alertDialogBuilder.setNegativeButton("Cancel",(dialogInterface,which)->{

            String messageSenderRef = "Messages /"+child+"/" + currentUserId ;
            DatabaseReference userMessagesRef = MessageRef.child(messageSenderRef);

            userMessagesRef.removeValue();

            String messageReciveRef = "Messages /"+child+"/"  + receivedData  ;
            DatabaseReference reciverMessagesRef = MessageRef.child(messageReciveRef );
            reciverMessagesRef.removeValue();

             DatabaseReference usersLocationref = FirebaseDatabase.getInstance().getReference().child("FastFix").child("Customer Available").child(child).child(currentUserId);
             usersLocationref.removeValue();

            DatabaseReference getRefFromUsers  = FirebaseDatabase.getInstance().getReference("FastFix").child("Working Accepted").child(child);
            getRefFromUsers.removeValue();
            ServiceManWorkingRef.removeEventListener(ServiceManLocationRefListner);

            receivedData =null;

        });

        alertDialogBuilder.setView(dialogView);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        }, 4000);

    }


    //fetch device token from firebase
    private void GetDriverDeviceToken(String driver) {

        DatabaseReference DriverAvailableRef = FirebaseDatabase.getInstance().getReference("FastFix").child("Service Man ").child(child).child(driver);

        DriverAvailableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String deviceToken = snapshot.child("device token").getValue(String.class);
                //send notifification near by service man
                SendNotificaiton(deviceToken);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("error to get toke",error.getMessage().toString());
            }
        });


    }

    //start send notification for request;
    private void SendNotificaiton(String deviceToken) {

        // send those as notification
        String token = deviceToken;
        String title = "FastFix";
        String body = "A request is send from near by you";

        String url = "https://parvej6t9.000webhostapp.com/apps/notification.php?d="+token+"&t="+title+"&b="+body;//hit this link to send notification;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getContext(), "Send notification successful", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Send notification Failed", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    //end send notification for request

    //start upload tokan in to firebase customer details
    private void GetFCMtoken() {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();

                  //      Log.d("token", "Refreshed token: " + token);
                        // hashMap.put("devicetoken",token);

                        //get current user id
                        if(token!=null){
                            String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference   customerDatabaseRef = FirebaseDatabase.getInstance().getReference("FastFix").child("Users").child(currentUser);

                            customerDatabaseRef.child("device token").setValue(token).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        }

                    }
                });
    }

    //end upload tokan in to firebase customer details

    //start   notification
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    //wnd notification

    //star ---->after someone accept your request it will take to user chat
    public void OpenChatActivity() {


        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String senderRef = "Messages /";
        DatabaseReference RootRef  = FirebaseDatabase.getInstance().getReference("FastFix");

        RootRef.child(senderRef).child(child).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && openChat)  // if anything change in message it will call intent again and agai thats why i add openChat condition
                    for(DataSnapshot data:snapshot.getChildren()){
                    String usersID = data.getKey();
                    progressDialog.dismiss();
                    if(usersID.equals(senderId)){
                        openChat = false;
                        DatabaseReference usersLocationref = FirebaseDatabase.getInstance().getReference().child("FastFix").child("Customer Request").child(child).child(currentUserId);
                        usersLocationref.removeValue();//delete request reference so except acceted service man no one can get customer id after accepting
                        Intent usersChatActivity = new Intent(getContext(),CustomerChatActivity.class);
                        startActivity(usersChatActivity);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //end ---->after someone accept your request it will take to user chat
    private void Findroutes() {

        if(receivedData!=null) {

            if (usersLOcation == null || servicemanLocation == null) {
                Toast.makeText(getContext(), "Unable to get location", Toast.LENGTH_LONG).show();
            } else {
                Routing routing = new Routing.Builder()
                        .travelMode(AbstractRouting.TravelMode.DRIVING)
                        .withListener(this)
                        .alternativeRoutes(true)
                        .waypoints(usersLOcation, servicemanLocation)
                        .key("AIzaSyAyRa77-CF1T0pb2MH79IxdLTA0Xy5wnMw")
                        .build();
                routing.execute();
            }
        }

    }
    //Routing call back functions.
    @Override
    public void onRoutingFailure(RouteException e) {
        Log.d("routing",e.getMessage().toString());
        // Findroutes();
    }

    @Override
    public void onRoutingStart() {
      
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> routeList, int shortestRouteIndex) {

        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.BLUE);
        polyOptions.width(10);

        for (int i = 0; i < routeList.size(); i++) {
            if (i == shortestRouteIndex) {
                List<LatLng> points = routeList.get(i).getPoints();
                polyOptions.addAll(points);
                mMap.addPolyline(polyOptions);
            }
        }
    }

    @Override
    public void onRoutingCancelled() {

    }

}