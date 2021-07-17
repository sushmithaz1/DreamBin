package com.example.vegies.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vegies.R;
import com.example.vegies.adapter.AdapterOrderedItem;
import com.example.vegies.models.ModelCartItem;
import com.example.vegies.models.ModelOrderedItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OrderDetailsUser extends AppCompatActivity {

    private String orderTo,orderId;
    private ImageButton backBtn;
    private TextView orderIdTv;
    private TextView DateTv;
    private static TextView orderStatusTv;
    private TextView shopNameTv;
    private TextView totalItemsTv;
    private TextView amountTv;
    private TextView addressTv;
    private RecyclerView itemsRv;
    private String latitude,longitude,deliveryFee,orderSts;

    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelOrderedItem> orderedItemArrayList;
    private AdapterOrderedItem adapterOrderedItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_user);

        backBtn=findViewById(R.id.backBtn);
        orderIdTv=findViewById(R.id.orderIdTv);
        DateTv=findViewById(R.id.DateTv);
        orderStatusTv=findViewById(R.id.orderStatusTv);
        shopNameTv=findViewById(R.id.shopNameTv);
        totalItemsTv=findViewById(R.id.totalItemsTv);
        amountTv=findViewById(R.id.amountTv);
        addressTv=findViewById(R.id.addressTv);
        itemsRv=findViewById(R.id.itemsRv);

        Intent intent=getIntent();
        orderTo=intent.getStringExtra("orderTo");
        orderId=intent.getStringExtra("orderId");

        Intent intent1=getIntent();
        orderTo=intent.getStringExtra("orderTo");
        orderId=intent.getStringExtra("orderId");

        firebaseAuth=FirebaseAuth.getInstance();
        loadShopInfo();
        loadOrderDetails();
        loadOrderedItems();
;
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void loadOrderedItems() {
        orderedItemArrayList=new ArrayList<>();

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        orderedItemArrayList.clear();
                        int y=0;
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            y++;
                            ModelOrderedItem modelOrderedItem=ds.getValue(ModelOrderedItem.class);
                            orderedItemArrayList.add(modelOrderedItem);
                        }
                        adapterOrderedItem=new AdapterOrderedItem(OrderDetailsUser.this,orderedItemArrayList);
                        itemsRv.setAdapter(adapterOrderedItem);

                        totalItemsTv.setText(""+y);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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

    private void loadShopInfo() {
        Toast.makeText(this, ""+orderTo, Toast.LENGTH_SHORT).show();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("vegies");
        ref.child(orderTo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String shopName=""+dataSnapshot.child("shopName").getValue();
                         deliveryFee=""+dataSnapshot.child("deliveryFee").getValue();
                         latitude=""+dataSnapshot.child("latitude").getValue();
                         longitude=""+dataSnapshot.child("longitude").getValue();
                        shopNameTv.setText(shopName);
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
}
