package com.example.vegies.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vegies.R;
import com.example.vegies.adapter.AdapterOrderSellerDetails;
import com.example.vegies.models.ModelOrderSellerDetails;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class orderDetailsSeller extends AppCompatActivity {
private TextView completed,cancelled,orderStatusTv,deliveryAddressTv,phoneTv;
private RecyclerView items1Rv;
FirebaseAuth firebaseAuth;
    private String orderBy,orderId,orderTo,deliveryFee;
    private TextView orderIdTv,DateTv,UserNameTv,totalItemsTv,amountTv,addressTv;
    public String latitude,longitude,myLatitude,myLongitude,phone;

    ArrayList<ModelOrderSellerDetails> orderSellerDetails;
    AdapterOrderSellerDetails adapterOrderSellerDetails;
    private String orderSts;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_seller);

        cancelled=findViewById(R.id.cancelled);
        completed=findViewById(R.id.completed);

        items1Rv=findViewById(R.id.items1Rv);
        orderIdTv=findViewById(R.id.orderIdTv);
        DateTv=findViewById(R.id.DateTv);
        orderStatusTv=findViewById(R.id.orderStatusTv);
        UserNameTv=findViewById(R.id.userNameTv);
        totalItemsTv=findViewById(R.id.totalItemsTv);
        amountTv=findViewById(R.id.amountTv);
        addressTv=findViewById(R.id.addressTv);

        Intent intent=getIntent();
        orderBy=intent.getStringExtra("orderBy");
        orderId=intent.getStringExtra("orderId");
        orderTo=intent.getStringExtra("orderTo");

        firebaseAuth=FirebaseAuth.getInstance();
      //  Toast.makeText(this, ""+orderBy+","+orderId, Toast.LENGTH_SHORT).show();
        loadmyinfo();
        loadShopInfo();
        loadItems();
        loadOrderDetails();
       // findAddress(latitude,longitude);
      //  Toast.makeText(orderDetailsSeller.this, "Suucess"+orderTo+"...."+orderBy, Toast.LENGTH_SHORT).show();

        completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,Object> hashMap=new HashMap<>();
                hashMap.put("orderStatus","completed");
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
                ref.child(orderTo).child("Orders").child(orderId).updateChildren(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                             //   Toast.makeText(orderDetailsSeller.this, "Suucess", Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });
        cancelled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,Object> hashMap=new HashMap<>();
                hashMap.put("orderStatus","cancelled");
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
                ref.child(orderTo).child("Orders").child(orderId).updateChildren(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            //    Toast.makeText(orderDetailsSeller.this, "Suucess", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        }

    private void loadOrderDetails() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String orderBy=""+dataSnapshot.child("orderBy").getValue();
                        String orderCost=""+dataSnapshot.child("orderCost").getValue();
                        String orderId=""+dataSnapshot.child("orderId").getValue();
                        String orderStatus=""+dataSnapshot.child("orderStatus").getValue();
                        String orderTime=""+dataSnapshot.child("orderTime").getValue();
                        String orderTo=""+dataSnapshot.child("orderTo").getValue();
                        //   String deliveryFee=""+dataSnapshot.child("deliveryFee").getValue();
                        //  String latitude=""+dataSnapshot.child("latitude").getValue();
                        // String longitude=""+dataSnapshot.child("longitude").getValue();

                        orderSts=orderStatus;

                        Calendar calendar=Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));
                        String formatedDate = DateFormat.format("dd/MM/yyyy hh:mm a",calendar).toString();

                        if(orderStatus.equals("In Progress")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.design_default_color_primary));
                        }
                        else if(orderStatus.equals("completed")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.design_default_color_secondary));
                        }
                        else if(orderStatus.equals("cancelled")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));
                        }

                        orderIdTv.setText(orderId);
                        orderStatusTv.setText(orderStatus);
                        amountTv.setText("$"+orderCost+"[Including delivery fee $"+deliveryFee+"]");
                        DateTv.setText(formatedDate);

                        findAddress(latitude,longitude);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void loadmyinfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("vegies");
        ref.child(orderTo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    myLatitude=""+dataSnapshot.child("latitude").getValue();
                    myLongitude=""+dataSnapshot.child("longitude").getValue();
                    deliveryFee=""+dataSnapshot.child("deliveryFee").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void findAddress(String latitude, String longitude) {
        double lat=Double.parseDouble(latitude);
        double lon=Double.parseDouble(longitude);

        Geocoder geocoder;
        List<Address> addresses;
        geocoder=new Geocoder(this, Locale.getDefault());

        try{
            addresses=geocoder.getFromLocation(lat,lon,1);

            String address=addresses.get(0).getAddressLine(0);
            addressTv.setText(address);
        }
        catch(Exception e){

        }

    }

    public void loadShopInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("vegies");
        ref.child(orderBy)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        latitude=""+dataSnapshot.child("latitude").getValue();
                        longitude=""+dataSnapshot.child("longitude").getValue();
                        phone=""+dataSnapshot.child("phone").getValue();
                        name=""+dataSnapshot.child("name").getValue();
                        UserNameTv.setText(name);
                        findAddress(latitude,longitude);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    private void loadItems() {
        //Toast.makeText(this, ""+latitude+","+longitude, Toast.LENGTH_SHORT).show();
        orderSellerDetails=new ArrayList<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderSellerDetails.clear();
               int y=0;
                if(dataSnapshot.exists()){
                    y++;
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        ModelOrderSellerDetails modelOrderSellerDetails=ds.getValue(ModelOrderSellerDetails.class);
                        orderSellerDetails.add(modelOrderSellerDetails);
                    }

                    adapterOrderSellerDetails=new AdapterOrderSellerDetails(orderDetailsSeller.this,orderSellerDetails);
                    items1Rv.setAdapter(adapterOrderSellerDetails);
                    totalItemsTv.setText(""+y);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    }
