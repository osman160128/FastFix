package com.example.firstfix.serviceworker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.firstfix.MainActivity;
import com.example.firstfix.R;
import com.example.firstfix.UsersPrifileActivity;
import com.example.firstfix.customer.CustomerMainActivity;
import com.firebase.geofire.GeoFire;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ServiceManActivity extends AppCompatActivity {

    FragmentManager MapFragmentManager;

    FragmentManager RequstFragmentManager;

    FragmentTransaction MapFragmentTransaction;

    protected final int home = 1;
    protected final int request = 2;
    protected final int service = 3;
    protected final int profile = 4;
    MeowBottomNavigation bottomNavigation;

    String currentServiceManID;

    String customerAccepted, acceptedPrice;

    public static String child;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_man);




        MapFragmentManager = getSupportFragmentManager();
        MapFragmentTransaction = MapFragmentManager.beginTransaction();
        MapFragmentTransaction.replace(R.id.addServiceManMap, new ServiceManMapsFragment());
        MapFragmentTransaction.commit();

        //start toolbar for singout
        Toolbar toolbar = findViewById(R.id.serviceman_toolbar);
        setSupportActionBar(toolbar);
        int toolbarTextColor = getResources().getColor(R.color.white);
        setTitle("FastFix");
        toolbar.setTitleTextColor(toolbarTextColor);

        //end toolbar for singout

        bottomNavigation = findViewById(R.id.service_man_fragment);
        currentServiceManID = FirebaseAuth.getInstance().getCurrentUser().getUid();


         Intent getServiceWorkerChatActivity = getIntent();
        if (getServiceWorkerChatActivity != null) {
            customerAccepted = getServiceWorkerChatActivity.getStringExtra("Accept");
            acceptedPrice = getServiceWorkerChatActivity.getStringExtra("price");


        }


        ServieManREf();




        //bottom navigation start
        bottomNavigation.add(new MeowBottomNavigation.Model(home, R.drawable.home));
        bottomNavigation.add(new MeowBottomNavigation.Model(request, R.drawable.notification_icon));
        bottomNavigation.add(new MeowBottomNavigation.Model(service, R.drawable.img_2));//image_2 i for service icon
        bottomNavigation.add(new MeowBottomNavigation.Model(profile, R.drawable.profile));
        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                Toast.makeText(ServiceManActivity.this, "Item is click", Toast.LENGTH_SHORT).show();
                return null;
            }
        });

        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES
                if (model.getId() == 1) {
                    MapFragmentManager = getSupportFragmentManager();
                    MapFragmentTransaction = MapFragmentManager.beginTransaction();
                    MapFragmentTransaction.replace(R.id.addServiceManMap, new ServiceManMapsFragment());
                    MapFragmentTransaction.commit();
                } else if (model.getId() == 2) {
                    RequstFragmentManager = getSupportFragmentManager();
                    FragmentTransaction RequestFragmentTransaction = RequstFragmentManager.beginTransaction();
                    RequestFragmentTransaction.replace(R.id.addServiceManMap, new ShowCustomerRequestFragment());
                    RequestFragmentTransaction.commit();
                } else if (model.getId() == 3) {
                    Intent servicemanProfile = new Intent(ServiceManActivity.this, AllRegisterServieManActivity.class);
                    startActivity(servicemanProfile);
                } else if (model.getId() == 3) {
                    Intent servicemanProfile = new Intent(ServiceManActivity.this, UsersPrifileActivity.class);
                    startActivity(servicemanProfile);
                }

                return null;
            }
        });
        //bottom navigation end

    }

    private void ServieManREf() {

        DatabaseReference getRefFromUsers = FirebaseDatabase.getInstance().getReference("FastFix").child("Users").child(currentServiceManID);

        getRefFromUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    HashMap<String, String> hashMap = new HashMap<>();

                    if (snapshot.hasChild("image")) {
                        String image = snapshot.child("image").getValue(String.class);
                        hashMap.put("image", image);
                    }


                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String city = snapshot.child("city").getValue(String.class);
                    String deviceToken = snapshot.child("device token").getValue(String.class);


                    hashMap.put("name", name);
                    hashMap.put("email", email);
                    hashMap.put("city", city);
                    hashMap.put("device token", deviceToken);

                    if (customerAccepted != null) {
                        hashMap.put("customer id", customerAccepted);
                    }
                    if (acceptedPrice != null) {
                        hashMap.put("price", acceptedPrice);
                    }
                    //add datra to Serviceman reference
                    SetServiceManRef(hashMap);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SetServiceManRef(HashMap<String, String> hashMap) {
        DatabaseReference CutomerRef = FirebaseDatabase.getInstance().getReference("FastFix").child("Service Man ").child(child);
        CutomerRef.child(currentServiceManID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                MapFragmentManager = getSupportFragmentManager();
                MapFragmentTransaction = MapFragmentManager.beginTransaction();
                MapFragmentTransaction.replace(R.id.addServiceManMap, new ServiceManMapsFragment());
                MapFragmentTransaction.commit();
            }
        });

    }


    //start tool bar for sign out service man

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.service_man_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        /*
            if(item.getItemId() == R.id.serviceman_signout) {

                DatabaseReference ServiceManAvabilityRef = FirebaseDatabase.getInstance().getReference("FastFix").child("Service man available")
                        .child(ServiceManMapsFragment.child).child(currentServiceManID);
                ServiceManAvabilityRef.removeValue();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                ServiceManMapsFragment.mAuth = null;
                Intent profileActivity = new Intent(ServiceManActivity.this, MainActivity.class);
                profileActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(profileActivity);
                finish();

                return true;
            }
            else{
                    return super.onOptionsItemSelected(item);
                }



         */
        return super.onOptionsItemSelected(item);
    }




    //end tool bar for sign out service man
}