package com.example.vegies.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vegies.R;
import com.example.vegies.adapter.AdapterOrderUser;
import com.example.vegies.adapter.AdapterShop;
import com.example.vegies.models.ModelOrderUser;
import com.example.vegies.models.ModelShop;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MainUserActivity extends AppCompatActivity {
    private TextView nameTv,emailTv,phoneTv,tabShopsTv,tabOrdersTv;
    private ImageButton logoutBtn,editProfileBtn;
    private RelativeLayout shopsRl,ordersRl;
    private FirebaseAuth firebaseAuth;
    private ImageView profileTv;
    private ProgressDialog progressDialog;
    private RecyclerView shopsRv,ordersRv;

    private ArrayList<ModelShop> shopsList;
    private AdapterShop adapterShop;

    private ArrayList<ModelOrderUser> ordersList;
    private AdapterOrderUser adapterOrderUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        nameTv=findViewById(R.id.nameTv);
        logoutBtn=findViewById(R.id.logoutBtn);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        profileTv=findViewById(R.id.profileTv);
        emailTv=findViewById(R.id.emailTv);
        phoneTv=findViewById(R.id.phoneTv);
        tabShopsTv=findViewById(R.id.tabShopsTv);
        tabOrdersTv=findViewById(R.id.tabOrdersTv);
        shopsRl=findViewById(R.id.shopsRl);
        ordersRl=findViewById(R.id.ordersRl);
        shopsRv=findViewById(R.id.shopsRv);
        ordersRv=findViewById(R.id.ordersRv);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        firebaseAuth=FirebaseAuth.getInstance();
        loadMyInfo();
        checkUser();

        //at start show shops UI
        showShopsUI();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeMeOffline();
            }
        });
        tabShopsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show shops
                showShopsUI();
            }
        });
        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrdersUI();
            }
        });
    }

    private void showShopsUI() {
        //show shops UI,hide ordersn UI
        shopsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabShopsTv.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));
        tabShopsTv.setBackgroundResource(R.drawable.shaperect04);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }
    private void showOrdersUI() {
        //show orders UI,hide shop UI
        shopsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tabShopsTv.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark));
        tabShopsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabOrdersTv.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));
        tabOrdersTv.setBackgroundResource(R.drawable.shaperect04);
    }

    private void makeMeOffline(){
        progressDialog.setMessage("Logging Out....");

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("online","false");

        //update value to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("vegies");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //update successfuly
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        progressDialog.setMessage("processing1...");
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user==null){
            startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
            finish();
        }
        else{
            loadMyInfo();
        }
    }


    private void loadMyInfo() {
        progressDialog.setMessage("processing");
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("vegies");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            //get data from db
                            String name=""+ds.child("name").getValue();
                            String accountType=""+ds.child("accountType").getValue();
                            String email=""+ds.child("email").getValue();
                            String phone=""+ds.child("phone").getValue();
                            String city=""+ds.child("city").getValue();
                            String profileImage=""+ds.child("profileImage").getValue();

                            //set data to ui
                            nameTv.setText(name);
                            phoneTv.setText(phone);
                            emailTv.setText(email);
                           try{
                               Picasso.get().load(profileImage).placeholder(R.drawable.common_google_signin_btn_icon_dark_normal).into(profileTv);
                           }
                           catch (Exception e){
                               profileTv.setImageResource(R.drawable.common_google_signin_btn_icon_dark_normal);
                           }

                            //load only those shops  that are in city of user
                            loadShops(city);
                            loadOrders();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
    }

    private void loadOrders() {
        ordersList=new ArrayList<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ordersList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    String uid=""+ds.getRef().getKey();
                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Orders");
                    ref.orderByChild("orderBy").equalTo(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //    Toast.makeText(MainUserActivity.this, "uid here"+firebaseAuth.getUid(), Toast.LENGTH_SHORT).show();
                                    if(dataSnapshot.exists()){
                                        ordersList.clear();
                                        //     Toast.makeText(MainUserActivity.this, "datasnap exists"+dataSnapshot, Toast.LENGTH_SHORT).show();
                                        for(DataSnapshot dss:dataSnapshot.getChildren()){
                                            ModelOrderUser modelOrderSeller=dss.getValue(ModelOrderUser.class);
                                            ordersList.add(modelOrderSeller);
                                        }

                                        adapterOrderUser=new AdapterOrderUser(MainUserActivity.this,ordersList);
                                        ordersRv.setAdapter(adapterOrderUser);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadShops(final String myCity) {
        //init list
        shopsList=new ArrayList<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("vegies");
        ref.orderByChild("accountType").equalTo("Seller")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //clear list before adding
                        shopsList.clear();
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            ModelShop modelShop=ds.getValue(ModelShop.class);

                            String shopCity=""+ds.child("city").getValue();
                            //show only user city shops
                          //  if(shopCity.equals(myCity)){
                                shopsList.add(modelShop);
                          //  }
                            //if u want to display all shops ,ship the if statement and add this
                            //shopList.add(modelshop);
                        }
                        //setup adapter
                        adapterShop=new AdapterShop(MainUserActivity.this,shopsList);
                        //set adapter to recycle view
                        shopsRv.setAdapter(adapterShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
    }
}
