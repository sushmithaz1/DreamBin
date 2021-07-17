package com.example.vegies.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vegies.adapter.AdapterOrderSeller;
import com.example.vegies.adapter.AdapterOrderedItem;
import com.example.vegies.adapter.AdapterProductSeller;
import com.example.vegies.Constants;
import com.example.vegies.models.ModelOrderSeller;
import com.example.vegies.models.ModelOrderUser;
import com.example.vegies.models.ModelOrderedItem;
import com.example.vegies.models.ModelProduct;
import com.example.vegies.R;
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

public class MainSellerActivity extends AppCompatActivity {
    private TextView nameTv, shopNameTv, emailTv, tabProductsTv, filteredProductsTv, tabOrdersTv;
    private ProgressDialog progressDialog;
    private EditText searchProductEt;
    private ImageButton logoutBtn, addProductBtn, filterProductBtn;
    private ImageView profileTv;
    public String orderBy,orderId;

    private FirebaseAuth firebaseAuth;
    private RelativeLayout productsRl, orderRl;
    private RecyclerView productsRv,ordersRv,itemsRv;

    private ArrayList<ModelProduct> productList;
    private AdapterProductSeller adapterProductSeller;

    private ArrayList<ModelOrderSeller> ordersList;
    private AdapterOrderSeller adapterOrderSeller;

    private ArrayList<ModelOrderedItem> orderedItems;
    private AdapterOrderedItem adapterOrderedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);
        nameTv = findViewById(R.id.nameTv);
        logoutBtn = findViewById(R.id.logoutBtn);
        addProductBtn = findViewById(R.id.addProductBtn);
        searchProductEt = findViewById(R.id.searchProductEt);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);
        productsRv = findViewById(R.id.productsRv);
        ordersRv = findViewById(R.id.ordersRv);
        itemsRv = findViewById(R.id.itemsRv);

        emailTv = findViewById(R.id.emailTv);
        profileTv = findViewById(R.id.profileTv);
        shopNameTv = findViewById(R.id.shopNameTv);
        tabProductsTv = findViewById(R.id.tabProductsTv);
        tabOrdersTv = findViewById(R.id.tabOrdersTv);
        productsRl = findViewById(R.id.productsRl);
        orderRl = findViewById(R.id.ordersRl);
      //  reviewsBtn = findViewById(R.id.reviewsBtn);

        Intent intent=getIntent();
        orderBy=intent.getStringExtra("orderBy");
        orderId=intent.getStringExtra("orderId");


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadAllProducts();

        showProductsUI();

        //search
        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterProductSeller.getFilter().filter(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make offline
                //sign out
                //go to login activity
                makeMeOffline();
            }
        });
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainSellerActivity.this, addProductActivity.class));
            }
        });

        tabProductsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load Products
                showProductsUI();
            }
        });
        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load orders
                showOrdersUI();
            }
        });
        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Choose Category:")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get selected Item
                                String selected = Constants.productCategories1[which];
                                filteredProductsTv.setText(selected);
                                if (selected.equals("All")) {
                                    //load all
                                    loadAllProducts();
                                } else {
                                    //load filtered
                                    loadFilteredProducts(selected);
                                }
                            }
                        })
                        .show();
            }
        });


    }

    private void loadFilteredProducts(final String selected) {
        productList = new ArrayList<>();
        //get all products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("vegies");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //before getting reset
                        productList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String productCategory = "" + ds.child("productCategory").getValue();
                            //if selected cate matches pro categry then add in list
                            if (selected.equals(productCategory)) {
                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                            }
                        }
                        //setup adapter
                        adapterProductSeller = new AdapterProductSeller(MainSellerActivity.this, productList);
                        //set adapter
                        productsRv.setAdapter(adapterProductSeller);
                    }

                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

   private void loadAllProducts() {
       productList = new ArrayList<>();
       //get all products
       DatabaseReference reference = FirebaseDatabase.getInstance().getReference("vegies");
       reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        //setup adapter
                        adapterProductSeller = new AdapterProductSeller(MainSellerActivity.this, productList);
                        //set adapter
                        productsRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
     /*   reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //before getting reset
                        productList.clear();
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            ModelProduct modelProduct=ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        //setup adapter
                        adapterProductSeller=new AdapterProductSeller(MainSellerActivity.this,productList);
                        //set adapter
                        productsRv.setAdapter(adapterProductSeller);
                    }
                    public void onCancelled(DatabaseError databaseError){

                    }
                });
        */
    }

    private void showProductsUI() {
        //show shops UI,hide ordersn UI
        productsRl.setVisibility(View.VISIBLE);
        orderRl.setVisibility(View.GONE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));
        tabProductsTv.setBackgroundResource(R.drawable.shaperect04);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrdersUI() {
        //show orders UI,hide shop UI
        productsRl.setVisibility(View.GONE);
        orderRl.setVisibility(View.VISIBLE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark));
        tabProductsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

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
                        Toast.makeText(MainSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user==null){
            startActivity(new Intent(MainSellerActivity.this, LoginActivity.class));
            finish();
        }
        else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
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
                            String shopName=""+ds.child("shopName").getValue();
                            String profileImage=""+ds.child("profileImage").getValue();

                            //set data to ui
                            nameTv.setText(name+"{"+accountType+"}");
                            shopNameTv.setText(shopName);
                            emailTv.setText(email);
                            try{
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_store).into(profileTv);
                            }
                            catch(Exception e){
                                profileTv.setImageResource(R.drawable.ic_store);
                            }
                            loadOrders();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

  /*  private void loadItems() {
        orderedItems=new ArrayList<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderBy).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        orderedItems.clear();
                        if(dataSnapshot.exists()){
                            for(DataSnapshot ds:dataSnapshot.getChildren()){
                                Toast.makeText(MainSellerActivity.this, "datasnap exists"+dataSnapshot, Toast.LENGTH_SHORT).show();
                                ModelOrderedItem modelOrderedItem=ds.getValue(ModelOrderedItem.class);
                                orderedItems.add(modelOrderedItem);
                            }

                            adapterOrderedItem=new AdapterOrderedItem(MainSellerActivity.this,orderedItems);
                            itemsRv.setAdapter(adapterOrderedItem);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }*/

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
                    ref.orderByChild("orderTo").equalTo(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //    Toast.makeText(MainUserActivity.this, "uid here"+firebaseAuth.getUid(), Toast.LENGTH_SHORT).show();
                                    if(dataSnapshot.exists()){
                                        //     Toast.makeText(MainUserActivity.this, "datasnap exists"+dataSnapshot, Toast.LENGTH_SHORT).show();
                                        for(DataSnapshot dss:dataSnapshot.getChildren()){
                                            ModelOrderSeller modelOrderSeller=dss.getValue(ModelOrderSeller.class);
                                            ordersList.add(modelOrderSeller);
                                        }

                                        adapterOrderSeller=new AdapterOrderSeller(MainSellerActivity.this,ordersList);
                                        ordersRv.setAdapter(adapterOrderSeller);
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
}

