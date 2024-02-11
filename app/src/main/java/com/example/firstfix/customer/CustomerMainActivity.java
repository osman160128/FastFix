package com.example.firstfix.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.firstfix.R;
import com.example.firstfix.UsersPrifileActivity;
import com.example.firstfix.serviceworker.AllRegisterServieManActivity;
import com.example.firstfix.serviceworker.ServiceManActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CustomerMainActivity extends AppCompatActivity {

    FragmentManager MapFragmentManager;

    FragmentTransaction MapFragmentTransaction;

    DrawerLayout CustomerDrawerLayout;
    MaterialToolbar CustomerMetarialToolBar;
    FrameLayout CustomerFrameLayout;
    NavigationView CustomerNavigationView;
    DatabaseReference databaseReference;

    ImageView headerImg;
    TextView headerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);
        CustomerDrawerLayout = findViewById(R.id.CustomerDraweLayout);
        CustomerMetarialToolBar = findViewById(R.id.CustomermetrailToolBar);
        CustomerFrameLayout = findViewById(R.id.addCustomerMap);
        CustomerNavigationView = findViewById(R.id.CustomerNavigationView);


        View NviewHeader = CustomerNavigationView.getHeaderView(0);
        headerImg = NviewHeader.findViewById(R.id.customer_header_image);
        headerName = NviewHeader.findViewById(R.id.customer_header_name);

        String currentUsers = FirebaseAuth.getInstance().getCurrentUser().getUid();

         databaseReference = FirebaseDatabase.getInstance().getReference("FastFix").child("Users").child(currentUsers);

        AddNavigationHeaderDetails();
        //add map fragment
        Intent intent = getIntent();

        if(intent!=null){
            String acceptetServiceManId = intent.getStringExtra("Accepted");
            String child = intent.getStringExtra("child");
            if(acceptetServiceManId!=null){

                CustomerMapsFragment customerMapsFragment = new CustomerMapsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Accepted",acceptetServiceManId);
                bundle.putString("child",child);

                customerMapsFragment.setArguments(bundle);
                MapFragmentManager = getSupportFragmentManager();
                MapFragmentTransaction = MapFragmentManager.beginTransaction();

                MapFragmentTransaction.replace(R.id.addCustomerMap,customerMapsFragment);
                MapFragmentTransaction.commit();
            }
            else {
                MapFragmentManager = getSupportFragmentManager();
                MapFragmentTransaction = MapFragmentManager.beginTransaction();

                MapFragmentTransaction.replace(R.id.addCustomerMap,new AllServiceitemFragment());
                MapFragmentTransaction.commit();
            }

        }



        //crite toogle for DrawerLayout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(CustomerMainActivity.this,CustomerDrawerLayout,CustomerMetarialToolBar,R.string.drawer_close,R.string.drawer_open);
        CustomerDrawerLayout.addDrawerListener(toggle);

        CustomerNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.RegistrationServiceMan){
                 Intent DriverActivity = new Intent(CustomerMainActivity.this, AllRegisterServieManActivity.class);
                 startActivity(DriverActivity);
                }
                else if(item.getItemId()==R.id.userChat){
                    Intent DriverActivity = new Intent(CustomerMainActivity.this, CustomerChatActivity.class);
                    startActivity(DriverActivity);
                } else if (item.getItemId()==R.id.userProfile) {
                    Intent profileActivity = new Intent(CustomerMainActivity.this, UsersPrifileActivity.class);
                    startActivity(profileActivity);
                }
                return true;
            }
        });

    }

    private void AddNavigationHeaderDetails() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if(snapshot.hasChild("image")){
                        String imageUrl  = snapshot.child("image").getValue().toString();
                        Picasso.get().load(imageUrl).into(headerImg);
                    }
                    String name = snapshot.child("name").getValue().toString();
                    headerName.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}