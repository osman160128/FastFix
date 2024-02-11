package com.example.firstfix.serviceworker;



import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.firstfix.R;
import com.example.firstfix.customer.UsersModelClass;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowCustomerRequestFragment extends Fragment {

    DatabaseReference CustomerRquestRef;

    RecyclerView  recyclerView;

    String userRequestId,currenServicManId;

    public static String child="child";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_show_customer_request, container, false);

        recyclerView = view.findViewById(R.id.customer_request_recylerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        CustomerRquestRef = FirebaseDatabase.getInstance().getReference("FastFix").child("Customer Request").child(child) ;

        currenServicManId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<UsersModelClass> options = new FirebaseRecyclerOptions.Builder<UsersModelClass>()
                .setQuery(CustomerRquestRef,UsersModelClass.class)
                .build();

        FirebaseRecyclerAdapter<UsersModelClass,RequestsViewHolder> adapter = new FirebaseRecyclerAdapter<UsersModelClass, RequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestsViewHolder holder, int position, @NonNull UsersModelClass model) {

                userRequestId = getRef(position).getKey();

                Log.d("userRequestId","userRequestId"+userRequestId);

                DatabaseReference UsersDetailsRef = FirebaseDatabase.getInstance().getReference("FastFix").child("Users");

                UsersDetailsRef.child(userRequestId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String UsersName = snapshot.child("name").getValue(String.class);

                        holder.txtName.setText(UsersName+" is request for service");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //aftercliking accept button it will go serviceman chatactivity
                holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatabaseReference RequestAccptedRef = FirebaseDatabase.getInstance().getReference("FastFix").
                                child("Working Accepted").child(child).child(currenServicManId);
                        RequestAccptedRef.setValue(userRequestId);

                        Intent serviceManChatIntent = new Intent(getContext(),ServiceWorkerChatActivity.class);
                        serviceManChatIntent.putExtra("reciverId",userRequestId);
                        startActivity(serviceManChatIntent);

                    }
                });
            }

            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_layout,parent,false);
                RequestsViewHolder holder = new RequestsViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static  class RequestsViewHolder extends RecyclerView.ViewHolder{

        TextView txtName,btnAccept,btnCancel;
        ImageView ImgCustomerProfile;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.request_customer_name);
            ImgCustomerProfile = itemView.findViewById(R.id.request_customer_img);
            btnAccept = itemView.findViewById(R.id.request_customer_accept);
            btnCancel =itemView.findViewById(R.id.request_customer_cancel);


        }
    }
}