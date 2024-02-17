package com.example.firstfix;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firstfix.customer.CustomerChatActivity;
import com.example.firstfix.customer.CustomerMainActivity;
import com.example.firstfix.databinding.ActivityMainBinding;
import com.example.firstfix.serviceworker.ServiceManActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    TextInputEditText edtName,edtEmail,edtPassword,edtCity;
    TextView dontHaveAcount;
    Button loginButton;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    ActivityMainBinding activityMainBinding;

    FirebaseAuth mAuth;
    Uri imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleMapRequestPermission();

        activityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        activityMainBinding.setMainActivity(MainActivity.this);
        edtName = findViewById(R.id.CustomerName);
        edtEmail = findViewById(R.id.CustomerEmail);
        edtPassword = findViewById(R.id.CustomerPassword);
        loginButton = findViewById(R.id.LoginButton);
        edtCity = findViewById(R.id.CustomerCity);
        dontHaveAcount = findViewById(R.id.dontHaveAcount);


        //set visibilty gone whose dose not need for login acitivity
        edtCity.setVisibility(View.GONE);
        edtName.setVisibility(View.GONE);



        mAuth = FirebaseAuth.getInstance();

        //ask noootification permission


    }

    private void GoogleMapRequestPermission() {

        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, proceed with map initialization
            //Intent intent = new Intent(MainActivity.this, CustomerMapActivity.class);
            //startActivity(intent);\
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with map initialization
                //RegisterFirevase();

            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
                Toast.makeText(this, "Please allow the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void ButtonCLick(){

        String name = edtName.getText().toString();
        String password = edtPassword.getText().toString();
        String email = edtEmail.getText().toString();
        String city = edtCity.getText().toString();
        String login = loginButton.getText().toString();


        if(login.equals("Login")){ //if login   button it clik osman
            LoginFunction(password,email);
        }
        else{
            RegistrationFunction(name,password,email,city);
        }
    }
    public void DontHaveACcount(){
        loginButton.setText("Register");
        edtName.setVisibility(View.VISIBLE);
        edtCity.setVisibility(View.VISIBLE);
        dontHaveAcount.setVisibility(View.INVISIBLE);

    }

    private void RegistrationFunction(String name, String password, String email,String city) {

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("name",name);
        hashMap.put("email",email);
        hashMap.put("city",city);

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    String  currentUserid = mAuth.getCurrentUser().getUid();
                    DatabaseReference CutomerRef = FirebaseDatabase.getInstance().getReference("FastFix").child("Users");
                    CutomerRef.child(currentUserid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent CustomerMapActivity = new Intent(MainActivity.this, AddPictureActivity.class);
                            startActivity(CustomerMapActivity);
                        }
                    });
                }
                else {
                    Log.d("osgadfgasgasdfgasdf",task.getException().getMessage());
                }

            }
        });
    }

    private void LoginFunction(String password, String email) {
        Toast.makeText(this, "email "+email+"Password "+password,Toast.LENGTH_SHORT).show();

        Toast.makeText(this, "Login buttoni s click", Toast.LENGTH_SHORT).show();
        mAuth.signInWithEmailAndPassword(email,password ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(MainActivity.this,AddPictureActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
    //notification permission start
    //notification permission end

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            Intent CustomerMapActivity = new Intent(MainActivity.this, CustomerMainActivity.class);
            startActivity(CustomerMapActivity);
        }
    }
}