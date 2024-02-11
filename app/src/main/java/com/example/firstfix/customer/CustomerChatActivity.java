package com.example.firstfix.customer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firstfix.ChatRecylerViewAdapter;
import com.example.firstfix.R;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerChatActivity extends AppCompatActivity {
    FirebaseAuth mAuth;

    DatabaseReference MessageRef;

    ImageView sendButton,showAccaptButton;

    EditText messagetxt;

    String currentUserId; //sender is  service man
    String reciverId; //reciver is customer;

    List<MessageModelClass> messageModelClassList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    ChatRecylerViewAdapter chatRecylerViewAdapter;

    DatabaseReference ServiceManWorkingRef;

    RecyclerView chatRecylerView;
    String messagePushId;

    CircleImageView toolbarImg;
    TextView toolbarName;

    String acceptedPrice,asstedName,accetedImgUrl;
    public static String child = "child";

    public  boolean showTolBar = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_chat);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();//curent usuer id


        messagetxt = findViewById(R.id.users_txt_message);
        sendButton = findViewById(R.id.users_message_send_button);
        chatRecylerView = findViewById(R.id.users_chat_recylerview);
        chatRecylerView = findViewById(R.id.users_chat_recylerview);
        toolbarImg = findViewById(R.id.userToolbarImg);
        toolbarName = findViewById(R.id.userToolbarName);


        showAccaptButton = findViewById(R.id.show_accept_customer_button);
        MessageRef = FirebaseDatabase.getInstance().getReference("FastFix");

        chatRecylerViewAdapter = new ChatRecylerViewAdapter(messageModelClassList);

        linearLayoutManager = new LinearLayoutManager(this);

        chatRecylerView.setLayoutManager(linearLayoutManager);
        chatRecylerView.setAdapter(chatRecylerViewAdapter);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        showAccaptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetAccepteServiceManID();
            }
        });

        //show serviceman information in toolba


    }

    private void ShowInToolBar() {

        if (reciverId != null) {
            DatabaseReference CutomerRef = FirebaseDatabase.getInstance().getReference("FastFix").child("Service Man ").child(child);
            CutomerRef.child(reciverId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.hasChild("image")) {
                            accetedImgUrl = snapshot.child("image").getValue().toString();
                            Picasso.get().load(accetedImgUrl).into(toolbarImg);
                        }
                        if(snapshot.hasChild("price")){
                            acceptedPrice = snapshot.child("name").getValue().toString();
                        }
                        asstedName = snapshot.child("name").getValue().toString();
                        toolbarName.setText(asstedName);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    //if serviceman accepted users this function will work

    private void SendMessage() {
        String messageTxt = messagetxt.getText().toString();

        if (TextUtils.isEmpty(messageTxt)) {
            Toast.makeText(this, "First write your message ", Toast.LENGTH_SHORT).show();
        } else {

            String messageSenderRef = "Messages /"+ child+"/"+ currentUserId + "/" + reciverId;
            String messageReciverRef = "Messages /"+ child+"/" + reciverId + "/" + currentUserId;


            DatabaseReference userMessageKeyRef = MessageRef.child("Messages").child(child).child(messageSenderRef).child(messageReciverRef).push();

            messagePushId = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();

            messageTextBody.put("message", messageTxt);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", currentUserId);

            Map messageBodyDetails = new HashMap<>();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messageTextBody);
            messageBodyDetails.put(messageReciverRef + "/" + messagePushId, messageTextBody);

            MessageRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                }
            });


        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    //show massage
        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String senderRef = "Messages /" + child+"/"+ currentUserId;
        DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference("FastFix");

        //get serviceman id from message refrence





            RootRef.child(senderRef).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        // Assuming each child under "Messages" corresponds to a message
                        messageModelClassList.clear();
                        reciverId = userSnapshot.getKey();

                        String messageSenderRef = "Messages /" + child + "/" + currentUserId + "/" + reciverId;

                        DatabaseReference userMessagesRef = MessageRef.child(messageSenderRef);
                        if (showTolBar) {
                            ShowInToolBar();
                            showTolBar = false;
                        }

                        userMessagesRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                MessageModelClass usersModelClass = snapshot.getValue(MessageModelClass.class);
                                messageModelClassList.add(usersModelClass);
                                chatRecylerViewAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    // Replace senderId and receiverId with the actual values
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

    }

    private void GetAccepteServiceManID() {

            AlertDialog.Builder alartdialogBuilder = new AlertDialog.Builder(CustomerChatActivity.this);

            alartdialogBuilder.setTitle("FastFix");
            alartdialogBuilder.setMessage(asstedName + " is accept your request");

            alartdialogBuilder.setPositiveButton("Accecpt ", (dialog, which) -> {
                Intent intent = new Intent(CustomerChatActivity.this, CustomerMainActivity.class);
                intent.putExtra("Accepted", reciverId);
                intent.putExtra("child", child);

                startActivity(intent);
            });

              alartdialogBuilder.setNegativeButton("Cancel " ,(dialog, which) -> {
                DatabaseReference usersLocationref = FirebaseDatabase.getInstance().getReference().child("FastFix").child("Customer Request").child(currentUserId);
                usersLocationref.removeValue();


                String messageSenderRef = "Messages /" + currentUserId ;
                DatabaseReference userMessagesRef = MessageRef.child(messageSenderRef);
                userMessagesRef.removeValue();

                String messageReciveRef = "Messages /" + reciverId;
                DatabaseReference reciverMessagesRef = MessageRef.child(messageReciveRef );
                reciverMessagesRef.removeValue();


                Intent intent = new Intent(CustomerChatActivity.this, CustomerMainActivity.class);
                startActivity(intent);
           });
            AlertDialog alertDialog = alartdialogBuilder.create();
            alertDialog.show();
            
        }

}