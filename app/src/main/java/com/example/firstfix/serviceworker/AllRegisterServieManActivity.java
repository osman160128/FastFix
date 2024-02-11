package com.example.firstfix.serviceworker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.firstfix.R;
import com.example.firstfix.customer.CustomerMainActivity;
import com.example.firstfix.databinding.ActivityAllRegisterServieManBinding;

public class AllRegisterServieManActivity extends AppCompatActivity {


    ActivityAllRegisterServieManBinding activityAllRegisterServieManBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_register_servie_man);

        activityAllRegisterServieManBinding = DataBindingUtil.setContentView(this,R.layout.activity_all_register_servie_man);

        activityAllRegisterServieManBinding.setServiceItem(this);


    }
    //if Desktop button is click  will  register as as Desktop  service man
    public void  DesktopFix(){
        ServiceManActivity.child = "Dextop service";
        ServiceManMapsFragment.child="Dextop service";
        ServiceWorkerChatActivity.child="Dextop service";
        ShowCustomerRequestFragment.child="Dextop service";
        Intent serviceManActivity = new Intent(AllRegisterServieManActivity.this,ServiceManActivity.class);
        startActivity(serviceManActivity);
        finish();
    }
    //if laptop button is click  will  register as as laptop  service man
    public void  LaptopFix(){
        ServiceManActivity.child = "Laptop service";
        ServiceManMapsFragment.child="Laptop service";
        ServiceWorkerChatActivity.child="Laptop service";
        ShowCustomerRequestFragment.child="Laptop service";
        Intent serviceManActivity = new Intent(AllRegisterServieManActivity.this,ServiceManActivity.class);
        startActivity(serviceManActivity);
        finish();
    }
    //if phone button is click  will  register as as phone  service man
    public void  PhoneFix(){
        ServiceManActivity.child = "Phone service";
        ServiceManMapsFragment.child="Phone service";
        ServiceWorkerChatActivity.child="Phone service";
        ShowCustomerRequestFragment.child="Phone service";
        Intent serviceManActivity = new Intent(AllRegisterServieManActivity.this,ServiceManActivity.class);
        startActivity(serviceManActivity);
        finish();
    }
    //if printer/scanner button is click  will  register as as printer/scanner  service man
    public void  PrinterScannerFix(){
        ServiceManActivity.child = "PritnerScanner service";
        ServiceManMapsFragment.child="PritnerScanner service";
        ServiceWorkerChatActivity.child="PritnerScanner service";
        ShowCustomerRequestFragment.child="PritnerScanner service";
        Intent serviceManActivity = new Intent(AllRegisterServieManActivity.this,ServiceManActivity.class);
        startActivity(serviceManActivity);
        finish();
    }
    //if Bike button is click  will  register as as bike  service man
    public void  BikeFixer(){
        ServiceManActivity.child = "Biker service";
        ServiceManMapsFragment.child="Biker service";;
        ServiceWorkerChatActivity.child="Biker service";;
        ShowCustomerRequestFragment.child="Biker service";;
        Intent serviceManActivity = new Intent(AllRegisterServieManActivity.this,ServiceManActivity.class);
        startActivity(serviceManActivity);
        finish();
    }
    //if car button is click  will  register as as car  service man
    public void  CarFixer(){
        ServiceManActivity.child = "Car service";
        ServiceManMapsFragment.child="Car service";
        ServiceWorkerChatActivity.child="Car service";
        ShowCustomerRequestFragment.child="Car service";
        Intent serviceManActivity = new Intent(AllRegisterServieManActivity.this,ServiceManActivity.class);
        startActivity(serviceManActivity);
        finish();
    }
    //if Ac  button is click  will  register as as Ac  service man
    public void  ACFixer(){
        ServiceManActivity.child = "Ac service";
        ServiceManMapsFragment.child="Ac service";
        ServiceWorkerChatActivity.child="Ac service";
        ShowCustomerRequestFragment.child="Ac service";
        Intent serviceManActivity = new Intent(AllRegisterServieManActivity.this,ServiceManActivity.class);
        startActivity(serviceManActivity);
        finish();
    }
    //if Geyser button is click  will  register as as Geyser  service man
    public void  GeyserFixer(){
        ServiceManActivity.child = "Geyser service";
        ServiceManMapsFragment.child="Geyser service";;
        ServiceWorkerChatActivity.child="Geyser service";;
        ShowCustomerRequestFragment.child="Geyser service";;
        Intent serviceManActivity = new Intent(AllRegisterServieManActivity.this,ServiceManActivity.class);
        startActivity(serviceManActivity);
        finish();
    }

    //if fridge button is click  will  register as as frudge  service man
    public void  FridgeFix(){
        ServiceManActivity.child = "Fridge service";
        ServiceManMapsFragment.child="Fridge service";
        ServiceWorkerChatActivity.child="Fridge service";
        ShowCustomerRequestFragment.child="Fridge service";
        Intent serviceManActivity = new Intent(AllRegisterServieManActivity.this,ServiceManActivity.class);
        startActivity(serviceManActivity);
        finish();
    }
    //if Oven button is click  will  register as as Oven  service man
    public void OvenFixer(){
        ServiceManActivity.child = "Oven service";
        ServiceManMapsFragment.child="Oven service";
        ServiceWorkerChatActivity.child="Oven service";
        ShowCustomerRequestFragment.child="Oven service";
        Intent serviceManActivity = new Intent(AllRegisterServieManActivity.this,ServiceManActivity.class);
        startActivity(serviceManActivity);
        finish();
    }

}