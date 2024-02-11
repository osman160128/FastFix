package com.example.firstfix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class UsersPrifileActivity extends AppCompatActivity {

    ImageView profileImage;

    TextView profileName,profileEmail,profileCity,updateProfile,updatButton;
    EditText edtProfileName,edtProfileEmail,edtProfileCity;
    String CurrentUSers;
    DatabaseReference getRefFromUsers;



    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_prifile);

        profileImage= findViewById(R.id.profileImage);
        profileName = findViewById(R.id.TxTprofileName);
        profileEmail = findViewById(R.id.TxTprofileEmail);
        profileCity = findViewById(R.id.TxTprofileCity);
        updateProfile =findViewById(R.id.update_profile);
        edtProfileName = findViewById(R.id.EditTprofileName);
        edtProfileEmail = findViewById(R.id.EditTprofileEmail);
        edtProfileCity = findViewById(R.id.EditTxTprofileCity);

        progressDialog = new ProgressDialog(this);

        updatButton = findViewById(R.id.update_button);

        CurrentUSers = FirebaseAuth.getInstance().getCurrentUser().getUid();

        getRefFromUsers  = FirebaseDatabase.getInstance().getReference("FastFix").child("Users").child(CurrentUSers);

        ShowUsersInformation();

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileName.setVisibility(View.GONE);
                profileEmail.setVisibility(View.GONE);
                profileCity.setVisibility(View.GONE);
                edtProfileName.setVisibility(View.VISIBLE);
                edtProfileEmail.setVisibility(View.VISIBLE);
                edtProfileCity.setVisibility(View.VISIBLE);
                updatButton.setVisibility(View.VISIBLE);


            }
        });

        updatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInformation();
            }
        });
    }

    private void updateInformation() {

        progressDialog.setTitle("FastFix");
        progressDialog.setTitle("Updating.......");

        progressDialog.show();

        String name = edtProfileName.getText().toString();
        String email = edtProfileEmail.getText().toString();
        String city = edtProfileCity.getText().toString();

        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put("name",name);
        hashMap.put("email",email);
        hashMap.put("city",city);
        getRefFromUsers.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(UsersPrifileActivity.this, "Your profile is update", Toast.LENGTH_SHORT).show();
                    updatButton.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }
            }
        });

    }

    private void ShowUsersInformation() {
        progressDialog.setTitle("FastFix");
        progressDialog.setTitle("Loding.......");

        progressDialog.show();

        getRefFromUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    if (snapshot.hasChild("image")) {
                        String image = snapshot.child("image").getValue(String.class);
                        Picasso.get().load(image).into(profileImage);
                    }

                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String city = snapshot.child("city").getValue(String.class);

                    profileName.setText(name);
                    profileEmail.setText(email);
                    profileCity.setText(city);
                    edtProfileName.setText(name);
                    edtProfileEmail.setText(email);
                    edtProfileCity.setText(city);
                    progressDialog.dismiss();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}