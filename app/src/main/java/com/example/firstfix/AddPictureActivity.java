package com.example.firstfix;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.firstfix.customer.CustomerMainActivity;
import com.example.firstfix.databinding.ActivityAddPictureBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class AddPictureActivity extends AppCompatActivity {

    ActivityAddPictureBinding activityAddPictureBinding;

    ImageView profileImage;
    Uri imageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_picture);
        activityAddPictureBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_picture);
        activityAddPictureBinding.setAddPicture(this);

        profileImage = findViewById(R.id.add_picture);

    }

   public void AddPictureActivity(){
       Toast.makeText(this, "Addpicture is cliked", Toast.LENGTH_SHORT).show();
       Intent  intent = new Intent();
       intent.setType("image/*");
       intent.setAction(Intent.ACTION_GET_CONTENT);
       startActivityForResult(Intent.createChooser(intent,"Select Image From Here"),1);
   }

   public void UploadPicture(){

       StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + UUID.randomUUID().toString());
        if(imageUrl!=null){
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            //upload image into firebase and save image link to real time database
            storageReference.putFile(imageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imgUri = uri.toString();
                            String  currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference("FastFix").child("Users").child(currentUser);
                           UsersRef.child("image").setValue(imgUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   Intent intent = new Intent(AddPictureActivity.this, CustomerMainActivity.class);
                                   startActivity(intent);
                               }
                           });
                        }
                    });
                }
            });
        }
   }
    public void SkipButton(){
        Intent intent = new Intent(AddPictureActivity.this, CustomerMainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode ==RESULT_OK && data!=null && data.getData()!=null){
            imageUrl = data.getData();
            Picasso.get().load(data.getData()).into(profileImage);
        }
    }

}