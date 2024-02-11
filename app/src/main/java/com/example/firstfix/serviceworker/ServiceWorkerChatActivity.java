package com.example.firstfix.serviceworker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firstfix.ChatRecylerViewAdapter;
import com.example.firstfix.R;
import com.example.firstfix.customer.CustomerMapsFragment;
import com.example.firstfix.customer.MessageModelClass;
import com.example.firstfix.databinding.ActivityServiceWorkerChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceWorkerChatActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    DatabaseReference MessageRef;

    ImageView sendButton,serviceManToolbarImg;
    TextView serviceManToolbarName;

    EditText messagetxt;

    String currentServiceManID; //sender is  service man
    String reciverId; //reciver is customer;

    List<MessageModelClass> messageModelClassList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    ChatRecylerViewAdapter chatRecylerViewAdapter;

    RecyclerView chatRecylerView;

    String messagePushId;

    public static String child = "child";
    ActivityServiceWorkerChatBinding activityServiceWorkerChatBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_worker_chat);

        activityServiceWorkerChatBinding = DataBindingUtil.setContentView(this, R.layout.activity_service_worker_chat);
        activityServiceWorkerChatBinding.setServiceManChat(this);

        mAuth = FirebaseAuth.getInstance();
        currentServiceManID = mAuth.getCurrentUser().getUid(); //curent usuer id

        sendButton = findViewById(R.id.send_button);
        messagetxt = findViewById(R.id.messagetxt);
        chatRecylerView = findViewById(R.id.chat_recylerView);
        serviceManToolbarImg = findViewById(R.id.serviceManToolbarImg);
        serviceManToolbarName = findViewById(R.id.servicemanToolbarName);

        MessageRef = FirebaseDatabase.getInstance().getReference("FastFix");

        chatRecylerViewAdapter = new ChatRecylerViewAdapter(messageModelClassList);

        linearLayoutManager = new LinearLayoutManager(this);

        chatRecylerView.setLayoutManager(linearLayoutManager);
        chatRecylerView.setAdapter(chatRecylerViewAdapter);


        //get the reciverID from ShowCustomerFragment
        reciverId = getIntent().getExtras().get("reciverId").toString();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        ShowServicemanDataInToolBar();

    }

    private void SendMessage() {
        String messageTxt = messagetxt.getText().toString();

        if (TextUtils.isEmpty(messageTxt)) {
            Toast.makeText(this, "First write your message ", Toast.LENGTH_SHORT).show();
        } else {

            String messageSenderRef = "Messages /" +child+"/"+ currentServiceManID + "/" + reciverId;
            String messageReciverRef = "Messages /"+child+"/" + reciverId + "/" + currentServiceManID;


            DatabaseReference userMessageKeyRef = MessageRef.child("Messages").child(child).child(messageSenderRef).child(messageReciverRef).push();

            messagePushId = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();

            messageTextBody.put("message", messageTxt);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", currentServiceManID);

            Map messageBodyDetails = new HashMap<>();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messageTextBody);
            messageBodyDetails.put(messageReciverRef + "/" + messagePushId, messageTextBody);

            MessageRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ServiceWorkerChatActivity.this, "children upload ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ServiceWorkerChatActivity.this, "children upload error ", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }


    @Override
    public void onStart() {
        super.onStart();

        //show message ;
        String messageSenderRef = "Messages /"+child+"/" + currentServiceManID + "/" + reciverId;

        DatabaseReference userMessagesRef = MessageRef.child(messageSenderRef);


        userMessagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageModelClassList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Assuming each child corresponds to a message
                    String messageId = snapshot.getKey();
                    String messageText = snapshot.child("message").getValue(String.class);
                    String messageType = snapshot.child("type").getValue(String.class);
                    String fromUserId = snapshot.child("from").getValue(String.class);
                    MessageModelClass message = new MessageModelClass();
                    message.setMessage(messageText);
                    message.setType(messageType);
                    message.setFrom(fromUserId);

                    messageModelClassList.add(message);
                    chatRecylerViewAdapter.notifyDataSetChanged();
                    // Example: Log the message details


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
                Log.e("FirebaseError", "Error fetching messages: " + databaseError.getMessage());
            }
        });

    }



    //acceptbutton dialog show

    public void AcceptedButtonShow() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);

            // Inflate the custom layout
            View view = inflater.inflate(R.layout.accept_dialog_box, null);
            builder.setView(view);

            // Get references to the TextView and EditText in the layout
            TextView textView = view.findViewById(R.id.accept_dialog_box_text);
            EditText editText = view.findViewById(R.id.accept_dialog_box_editText);


            // Set positive button click listener
            builder.setPositiveButton("Accecpt ", (dialog, which) -> {
                // Get the edited text from the EditText
                    String price = editText.getText().toString();
                    Log.d("price ",price);
                    Intent  ServiceManMap  = new Intent(ServiceWorkerChatActivity.this,ServiceManActivity.class);
                    ServiceManMap.putExtra("Accept",reciverId);
                    ServiceManMap.putExtra("price",price);
                    ServiceManMap.putExtra("child",child);
                    CustomerMapsFragment.child = child;
                    startActivity(ServiceManMap);

            });

            // Set negative button click listener

           builder.setNegativeButton("Cancle",(dialog,which)->{
                Intent  ServiceManMap  = new Intent(ServiceWorkerChatActivity.this,ServiceManActivity.class);

                String messageSenderRef = "Messages /" + currentServiceManID ;
                DatabaseReference userMessagesRef = MessageRef.child(messageSenderRef);
                userMessagesRef.removeValue();

                String messageReciveRef = "Messages /" +reciverId ;
                DatabaseReference reciverMessagesRef = MessageRef.child(messageReciveRef );
                reciverMessagesRef.removeValue();
                startActivity(ServiceManMap);

          });
            // Show the dialog
            builder.create().show();
          }

    private void ShowServicemanDataInToolBar() {

        if(reciverId!=null) {

            DatabaseReference ServiceManUsersRef = FirebaseDatabase.getInstance().getReference("FastFix").child("Users").child(reciverId);
            ServiceManUsersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        if (snapshot.hasChild("image")) {
                            String imageUrl = snapshot.child("image").getValue().toString();
                            Picasso.get().load(imageUrl).into(serviceManToolbarImg);
                        }
                        String name = snapshot.child("name").getValue().toString();
                        serviceManToolbarName.setText(name);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


}