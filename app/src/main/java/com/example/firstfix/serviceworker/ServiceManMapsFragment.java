package com.example.firstfix.serviceworker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.firstfix.R;
import com.example.firstfix.customer.CustomerMainActivity;
import com.example.firstfix.databinding.FragmentServiceManMapsBinding;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServiceManMapsFragment extends Fragment  implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        RoutingListener,
        com.google.android.gms.location.LocationListener {
    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;

    FragmentServiceManMapsBinding binding;
    String currentServiceManID;
    DatabaseReference ServiceManAvabilityRef,ServiceManWorkingRef,ServiceMnaWorkingLocationRef;
    DatabaseReference AssignUsersRefernce,AssignUserPickUplocation;
    String assignUserID="";
    DatabaseReference UsersRef;

    String userImag,userName;

    LatLng usersLatlan;
    LatLng serviceManLatLan;


    Marker servicemanMarker;

    GeoFire geoFireAvability;
    GeoFire geoFireWorking;

    CardView buttonAlartShow;

    boolean findCustomer = true;
    public static String child = "child";
    Marker userMarker;
    private ValueEventListener AssignedCustomerPickUpRefListner;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout using data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_service_man_maps,container, false);

        // Bind the fragment instance to the layout
        binding.setServiceMapActivity(this);



        //the driver available location is add this reference
        ServiceManAvabilityRef = FirebaseDatabase.getInstance().getReference("FastFix").child("Service man available").child(child);
        ServiceManWorkingRef = FirebaseDatabase.getInstance().getReference("FastFix").child("Service man working").child(child);
        UsersRef= FirebaseDatabase.getInstance().getReference("FastFix").child("Users");
        currentServiceManID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Bundle bundle = getArguments();
        if (bundle != null) {;
            child = bundle.getString("child");

        }
        GetAssignUsersRequest();
        return binding.getRoot();
    }

    //if service man accept  users request fetch the user id from firebase
    private void GetAssignUsersRequest() {

        String currenServicManId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        AssignUsersRefernce = FirebaseDatabase.getInstance().getReference("FastFix").child("Working Accepted").child(child).child(currenServicManId);
        AssignUsersRefernce.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    binding.showAlertServiceManButton.setVisibility(View.VISIBLE);
                    assignUserID = snapshot.getValue().toString();
                    GEtAssignedCUstomerPickUpLocation();
                    ShowCustomer(assignUserID);
                    Log.d("osmanndtryey",assignUserID);
                }
                else {
                    assignUserID="";

                    if(userMarker!=null){
                        userMarker.remove();
                    }


                    if (AssignedCustomerPickUpRefListner != null)
                    {
                        AssignUserPickUplocation.removeEventListener(AssignedCustomerPickUpRefListner);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "assignUserId error"+error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //it fetch user infromation from  firbase
    private void ShowCustomer(String assignUserID) {

        UsersRef.child(assignUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.hasChild("image")){
                        userImag = snapshot.child("image").getValue(String.class);
                    }
                    userName = snapshot.child("name").getValue(String.class);
                    Log.d("userNamesssssss",userName);
                    ShowUsersDataDialogBox();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //it shows the user informatioon in dialagbox
    public void ShowUsersDataDialogBox() {
        if (getContext() != null && isAdded()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
            View dialogView = getLayoutInflater().inflate(R.layout.alart_dialog_service_man, null);

            TextView textName = dialogView.findViewById(R.id.alart_dilog_sermiceman_name);
            ImageView imagePic = dialogView.findViewById(R.id.alart_dilog_sermicemanImg);

            textName.setText("you pick up "+ userName + " work");

            if (userImag != null) {
                Picasso.get().load(userImag).into(imagePic);
            }

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
            }, 2000);
        }
    }


    //show users location who is accepted by current service man
    private void GEtAssignedCUstomerPickUpLocation() {

        AssignUserPickUplocation = FirebaseDatabase.getInstance().getReference().child("FastFix").child("Customer Available").child(child)
                .child(assignUserID).child("l");
        AssignedCustomerPickUpRefListner = AssignUserPickUplocation.addValueEventListener(new ValueEventListener() {
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

                    usersLatlan = new LatLng(usersLocation.getLatitude(),usersLocation.getLongitude());

                    Log.d("servicmenafdasfas",usersLatlan.toString());
                    userMarker = mMap.addMarker(new MarkerOptions().position(usersLatlan).title("Your customer location"));

                    //get serviceman location from server

                    servicemanLocation();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void servicemanLocation() {
        ServiceMnaWorkingLocationRef= FirebaseDatabase.getInstance().getReference("FastFix").child("Service man working").child(child).child(currentServiceManID).child("l");
        ServiceMnaWorkingLocationRef.addValueEventListener(new ValueEventListener() {
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

                    Location  servicemanLocation = new Location("");
                    servicemanLocation.setLatitude(LocationLat);
                    servicemanLocation.setLongitude(LocationLon);

                    serviceManLatLan= new LatLng(servicemanLocation.getLatitude(),servicemanLocation.getLongitude());

                    mMap.addMarker(new MarkerOptions().position(serviceManLatLan).title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.dirction)));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    Findroutes();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
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

        //show the current location
        lastLocation =location;

        LatLng latLng= new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        mMap.addMarker(new MarkerOptions().position(latLng).title("Service Man Location"));


        geoFireAvability = new GeoFire(ServiceManAvabilityRef);
        geoFireWorking = new GeoFire(ServiceManWorkingRef);

        switch (assignUserID) {
            case "":
                geoFireWorking.removeLocation(currentServiceManID);
                geoFireAvability.setLocation(currentServiceManID, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()));
                break;
            default:
                geoFireAvability.removeLocation(currentServiceManID);
                geoFireWorking.setLocation(currentServiceManID, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()));
                break;
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
    protected synchronized void buildGOogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    private void  Findroutes() {

        if(assignUserID!=null) {

            if (usersLatlan == null || serviceManLatLan == null) {
                Toast.makeText(getContext(), "Unable to get location", Toast.LENGTH_LONG).show();
            } else {
                Routing routing = new Routing.Builder()
                        .travelMode(AbstractRouting.TravelMode.DRIVING)
                        .withListener(this)
                        .alternativeRoutes(true)
                        .waypoints(serviceManLatLan, usersLatlan)
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
        Findroutes();
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
        Findroutes();
    }


    public  void buttonClick() {
    }
}