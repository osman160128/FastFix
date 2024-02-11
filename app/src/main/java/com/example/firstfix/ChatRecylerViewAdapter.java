package com.example.firstfix;

import android.app.Notification;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstfix.customer.MessageModelClass;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class ChatRecylerViewAdapter extends RecyclerView.Adapter<ChatRecylerViewAdapter.MyViewHolder> {

    List<MessageModelClass> messageList;

    FirebaseAuth mAuth;
    public ChatRecylerViewAdapter(List<MessageModelClass> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout,parent,false);

        mAuth = FirebaseAuth.getInstance();
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String senderId = mAuth.getCurrentUser().getUid();

        MessageModelClass messages= messageList.get(position);

        String reciverId = messages.getFrom();

        holder.reciverMessage.setVisibility(View.INVISIBLE);
        holder.reciverImage.setVisibility(View.INVISIBLE);

        if(reciverId.equals(senderId)){
            holder.senderMessage.setText(messages.getMessage());
        }
        else {
            holder.senderMessage.setVisibility(View.INVISIBLE);
            holder.reciverMessage.setVisibility(View.VISIBLE);
            holder.reciverImage.setVisibility(View.VISIBLE);
            holder.reciverMessage.setText(messages.getMessage());
        }
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder{

        TextView senderMessage,reciverMessage;

        CircleImageView reciverImage;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            reciverMessage = itemView.findViewById(R.id.reciver_message);
            reciverImage  = itemView.findViewById(R.id.reciver_image);
            senderMessage = itemView.findViewById(R.id.sender_message);
        }
    }
}
