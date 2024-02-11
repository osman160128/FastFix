package com.example.firstfix.customer;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.firstfix.R;
import com.example.firstfix.databinding.FragmentAllServiceManFragmentBinding;

public class AllServiceitemFragment extends Fragment {

    TextView dextopbutton;

FragmentManager fragmentManager;
FragmentTransaction fragmentTransaction;
    FragmentAllServiceManFragmentBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_all_service_man_fragment,container,false);
        binding.setAllServiceItemCUstomer(this);
        View view =binding.getRoot();

        return view;
    }
  // desktop button it click it will open map as desktop service
    public void customerDesktop() {
        CustomerMapsFragment.child = "Dextop service";
        CustomerChatActivity.child = "Dextop service";
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.addCustomerMap,new CustomerMapsFragment());
        fragmentTransaction.commit();
    }
    // if laptop button it click it will open map as laptop service
    public void customerLaptop(){
        CustomerMapsFragment.child = "Laptop service";
        CustomerChatActivity.child = "Laptop service";
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.addCustomerMap,new CustomerMapsFragment());
        fragmentTransaction.commit();

    }

     //if phone button it click it will open map as phone service
    public void customerPhone(){
        CustomerMapsFragment.child = "Phone service";
        CustomerChatActivity.child = "Phone service";
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.addCustomerMap,new CustomerMapsFragment());
        fragmentTransaction.commit();
    }
    //if printer/scanner button it click it will open map as printer/scannner serrvice
    public void  customerPrintScanner(){
        CustomerMapsFragment.child = "PritnerScanner service";
        CustomerChatActivity.child = "PritnerScanner service";
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.addCustomerMap,new CustomerMapsFragment());
        fragmentTransaction.commit();
    }
    //if bike button it click it will open map as bike service
    public void  customerBike(){
        CustomerMapsFragment.child = "Biker service";
        CustomerChatActivity.child = "Biker service";
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.addCustomerMap,new CustomerMapsFragment());
        fragmentTransaction.commit();
    }
    //if car button it click it will open map as car service
    public void customerCar(){
        CustomerMapsFragment.child = "Car service";
        CustomerChatActivity.child = "Car service";
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.addCustomerMap,new CustomerMapsFragment());
        fragmentTransaction.commit();
    }
    //if Ac button it click it will open map as Ac service
    public void customerAc(){
        CustomerMapsFragment.child = "Ac service";
        CustomerChatActivity.child = "Ac service";
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.addCustomerMap,new CustomerMapsFragment());
        fragmentTransaction.commit();
    }
    //if Geyser button it click it will open map as Geyser service
    public void customerGeyser(){
        CustomerMapsFragment.child = "Geyser service";;
        CustomerChatActivity.child = "Geyser service";
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.addCustomerMap,new CustomerMapsFragment());
        fragmentTransaction.commit();
    }
    //if Fridge button it click it will open map Fridge phone service
    public void customerFridge(){
        CustomerMapsFragment.child = "Fridge service";
        CustomerChatActivity.child = "Fridge service";
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.addCustomerMap,new CustomerMapsFragment());
        fragmentTransaction.commit();
    }
    //if oven button it click it will open map as oven service
    public void customerOven(){
        CustomerMapsFragment.child = "Oven service";
        CustomerChatActivity.child = "Oven service";
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.addCustomerMap,new CustomerMapsFragment());
        fragmentTransaction.commit();
    }

}