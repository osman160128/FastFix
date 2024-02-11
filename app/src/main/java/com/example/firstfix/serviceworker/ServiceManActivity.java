package com.example.firstfix.serviceworker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.firstfix.MainActivity;
import com.example.firstfix.R;
import com.example.firstfix.UsersPrifileActivity;
import com.example.firstfix.customer.CustomerMainActivity;
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
    protected final int profile = 3;

    MeowBottomNavigation bottomNavigation;

    String currentServiceManID;


    String customerAccepted,acceptedPrice;

    public static String child = "child";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_man);

        bottomNavigation = findViewById(R.id.service_man_fragment);
        currentServiceManID = FirebaseAuth.getInstance().getCurrentUser().getUid();



        Intent getServiceWorkerChatActivity = getIntent();
        if(getServiceWorkerChatActivity!= null){
            customerAccepted = getServiceWorkerChatActivity.getStringExtra("Accept");
            acceptedPrice = getServiceWorkerChatActivity.getStringExtra("price");


        }
        ServieManREf();
        //add map fragment


        bottomNavigation.add(new MeowBottomNavigation.Model(home,R.drawable.home));
        bottomNavigation.add(new MeowBottomNavigation.Model(request,R.drawable.menu));
        bottomNavigation.add(new MeowBottomNavigation.Model(profile,R.drawable.menu));
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
                if(model.getId()==1){
                    MapFragmentManager = getSupportFragmentManager();
                    MapFragmentTransaction = MapFragmentManager.beginTransaction();
                    MapFragmentTransaction.replace(R.id.addServiceManMap,new ServiceManMapsFragment());
                    MapFragmentTransaction.commit();
                } else if (model.getId()==2) {
                    RequstFragmentManager = getSupportFragmentManager();
                    FragmentTransaction RequestFragmentTransaction = RequstFragmentManager.beginTransaction();
                    RequestFragmentTransaction.replace(R.id.addServiceManMap,new ShowCustomerRequestFragment());
                    RequestFragmentTransaction.commit();
                }
                else if (model.getId()==3 ) {
                    Intent servicemanProfile = new Intent(ServiceManActivity.this, UsersPrifileActivity.class);
                    startActivity(servicemanProfile);
                }

                return null;
            }
        });

    }

    private void ServieManREf() {

        DatabaseReference getRefFromUsers  = FirebaseDatabase.getInstance().getReference("FastFix").child("Users").child(currentServiceManID);

        getRefFromUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    HashMap<String,String> hashMap = new HashMap<>();

                    if(snapshot.hasChild("image")){
                        String image =snapshot.child("image").getValue(String.class);
                        hashMap.put("image",image);
                    }


                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String city = snapshot.child("city").getValue(String.class);
                    String deviceToken = snapshot.child("device token").getValue(String.class);


                    hashMap.put("name",name);
                    hashMap.put("email",email);
                    hashMap.put("city",city);
                    hashMap.put("device token",deviceToken);

                    if(customerAccepted!= null)
                    {
                        hashMap.put("customer id",customerAccepted);
                    }
                    if(acceptedPrice!=null){
                        hashMap.put("price",acceptedPrice);
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
                MapFragmentTransaction.replace(R.id.addServiceManMap,new ServiceManMapsFragment());
                MapFragmentTransaction.commit();
            }
        });

    }


}