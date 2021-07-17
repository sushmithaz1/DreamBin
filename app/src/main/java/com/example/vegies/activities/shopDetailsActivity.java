package com.example.vegies.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ColorSpace;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vegies.Constants;
import com.example.vegies.R;
import com.example.vegies.adapter.AdapterCart;
import com.example.vegies.adapter.AdapterProductUser;
import com.example.vegies.models.ModelCartItem;
import com.example.vegies.models.ModelProduct;
import com.example.vegies.models.cart;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class shopDetailsActivity extends AppCompatActivity {
    final int UPI_PAYMENT=0;
    private ImageView shopTv;
    private ImageButton callBtn, mapBtn, cartBtn, backBtn, filterProductBtn;
    private EditText searchProductEt;
    private RecyclerView productsRv;
    private TextView shopNameTv, phoneTv, emailTv, filteredProductsTv, openCloseTv, deliveryFeeTv, addressTv,cartCountTv;

    private String shopUid;
    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelProduct> productsList;
    private AdapterProductUser adapterProductUser;

    private ArrayList<cart> cartItemList;
    private AdapterCart adaptercart;

    public String deliveryFee;
    private ProgressDialog progressDialog;

    private String myLatitude, myLongitude,myPhone;
    private String shopName, shopEmail, shopPhone, shopAddress, shopLatitude, shopLongitude, shopOpen;
    private String amount,M_id;
     String customer_id,order_id,url,callBackUrl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        shopTv = findViewById(R.id.shopTv);
        shopNameTv = findViewById(R.id.shopNameTv);
        phoneTv = findViewById(R.id.phoneTv);
        emailTv = findViewById(R.id.emailTv);
        openCloseTv = findViewById(R.id.openCloseTv);
        deliveryFeeTv = findViewById(R.id.deliveryFeeTv);
        addressTv = findViewById(R.id.addressTv);
        callBtn = findViewById(R.id.callBtn);
        mapBtn = findViewById(R.id.mapBtn);
        cartBtn = findViewById(R.id.cartBtn);
        backBtn = findViewById(R.id.backBtn);
        searchProductEt = findViewById(R.id.searchProductEt);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        productsRv = findViewById(R.id.productsRv);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);
        cartCountTv = findViewById(R.id.cartCountTv);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        //get uid of shop from Intent
        shopUid = getIntent().getStringExtra("shopUid");
        firebaseAuth = FirebaseAuth.getInstance();

        loadMyInfo();
        loadShopDetails();
        loadShopProducts();
        cartCount();

        //each shop hv its own products and order so if user add otems to cart and go back and open cart in diff shop then cart
        //  should be different so delete cart dats whenever user this activity
        deleteCartData();

        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterProductUser.getFilter().filter(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
              //  showCartDialog();
                public void onClick(View v) {
                showCartDialog();
            }

        });
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhone();
            }
        });
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });
        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(shopDetailsActivity.this);
                builder.setTitle("Choose Category:")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get selected Item
                                String selected = Constants.productCategories1[which];
                                filteredProductsTv.setText(selected);
                                if (selected.equals("All")) {
                                    //load all
                                    loadShopProducts();
                                } else {
                                    //load filtered
                                    adapterProductUser.getFilter().filter(selected);
                                }
                            }
                        })
                        .show();
            }
        });
    }

    public void cartCount() {
    }


    public TextView sTotalTv,dFeeTv,allTotalPriceTv,dFeeLabelTv,sTotalLabelTv,totalLabelTv,removeTv,itemTitleTv;
    private Button CheckoutBtn;
    private RecyclerView cartItemsRv;
    public Double allTotalPrice=0.0;
    private void showCartDialog() {
        View view= LayoutInflater.from(this).inflate(R.layout.dialog_cart,null);
        View view1= LayoutInflater.from(this).inflate(R.layout.row_cart,null);


        TextView shopNameTv=view.findViewById(R.id.shopNameTv);
        cartItemsRv=view.findViewById(R.id.cartItemsRv);
        sTotalTv=view.findViewById(R.id.sTotalTv);
        sTotalLabelTv=view.findViewById(R.id.sTotalLabelTv);
        dFeeTv=view.findViewById(R.id.dFeeTv);
        dFeeLabelTv=view.findViewById(R.id.dFeeLabelTv);
        allTotalPriceTv=view.findViewById(R.id.totalTv);
        totalLabelTv=view.findViewById(R.id.totalLabelTv);
        CheckoutBtn=view.findViewById(R.id.CheckoutBtn);

         removeTv=view1.findViewById(R.id.itemRemoveTv);
        itemTitleTv=view1.findViewById(R.id.itemTitleTv);

        final TextView titleTv=view1.findViewById(R.id.itemTitleTv);

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //set view to dialog
        builder.setView(view);

        final AlertDialog.Builder builder1=new AlertDialog.Builder(this);
        builder1.setView(view1);
        //      shopNameTv.setText(shopName);
        final String timestamp=""+System.currentTimeMillis();

        cartItemList=new ArrayList<>();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("vegies");
        ref.child(firebaseAuth.getUid()).child("CartList").orderByChild(timestamp)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        cartItemList.clear();
                       for(DataSnapshot ds:dataSnapshot.getChildren()) {
                            cart car = ds.getValue(cart.class);
                          //    String d = ds.getValue(String.class);
                           String t=""+ds.child("price").getValue();
                           if (allTotalPrice == 0.0) {
                                   allTotalPrice = Double.parseDouble(t);
                                   //     System.out.println(d);
                               }
                               else {
                                      allTotalPrice = allTotalPrice + Double.parseDouble(t);
                               }
                       // cart cc=cart.class(d);
                          cartItemList.add(car);
                        }

                        adaptercart=new AdapterCart(shopDetailsActivity.this,cartItemList);
                        cartItemsRv.setAdapter(adaptercart);

                        dFeeTv.setText(""+deliveryFee);
                        sTotalTv.setText(""+String.format("%.2f", allTotalPrice));
                            allTotalPriceTv.setText(""+(allTotalPrice+Double.parseDouble(""+deliveryFee)));
                            amount=allTotalPriceTv.getText().toString().trim().replace("$","");
                        AlertDialog  dialog=builder.create();
                        dialog.show();

                        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                allTotalPrice=0.00;
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        CheckoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        submitOrder();

                if(cartItemList.size()==0){
                    Toast.makeText(shopDetailsActivity.this, "No item in cart", Toast.LENGTH_SHORT).show();
                    return;
                }
             //   intent.putExtra("amount", amount);
             //   intent.putExtra("shopUid", shopUid);
            //    intent.putExtra("myLatitude", myLatitude);
            //    intent.putExtra("myLongitude", myLongitude);
            //    String amount="1";
            //    String note="Hi";
            //    String name="umesh kotari";
            //    String upiId="umeshkotari99@oksbi";
                if (ContextCompat.checkSelfPermission(shopDetailsActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(shopDetailsActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
                }
                 M_id="cjchdS36685633506587";
                 customer_id=FirebaseAuth.getInstance().getUid();
                 order_id= UUID.randomUUID().toString().substring(0,28);
                 url="https://dreambin.000webhostapp.com/paytm/generateChecksum.php";
                 callBackUrl="https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

                RequestQueue requestQueue= Volley.newRequestQueue(shopDetailsActivity.this);

                StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                           JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("CHECKSUMHASH")){
                                String CHECKSUMHASH=jsonObject.getString("CHECKSUMHASH");

                                PaytmPGService paytmPGService = PaytmPGService.getService();
                                HashMap<String, String> paramMap = new HashMap<String,String>();
                                paramMap.put( "MID" , "cjchdS36685633506587");
                                paramMap.put( "ORDER_ID" , order_id);
                                paramMap.put( "CUST_ID" , customer_id);
                                paramMap.put( "CHANNEL_ID" , "WAP");
                                paramMap.put( "TXN_AMOUNT" , amount);
                                paramMap.put( "WEBSITE" , "WEBSTAGING");
                                paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
                                paramMap.put( "CALLBACK_URL", callBackUrl);
                                paramMap.put("CHECKSUMHASH",CHECKSUMHASH);

                                PaytmOrder order=new PaytmOrder(paramMap);
                                
                                paytmPGService.initialize(order,null);
                                paytmPGService.startPaymentTransaction(shopDetailsActivity.this, true, true, new PaytmPaymentTransactionCallback() {
                                    @Override
                                    public void onTransactionResponse(Bundle inResponse) {

                                            Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                                        }

                                    @Override
                                    public void networkNotAvailable() {
                                        Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void clientAuthenticationFailed(String inErrorMessage) {
                                        Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void someUIErrorOccurred(String inErrorMessage) {
                                        Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage , Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                                        Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onBackPressedCancelTransaction() {
                                        Toast.makeText(getApplicationContext(), "Transaction cancelled" , Toast.LENGTH_LONG).show();

                                    }
                                    @Override
                                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {

                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(shopDetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }){
                    protected Map<String,String> getParams() throws AuthFailureError{
                        Map<String, String> paramMap = new HashMap<String,String>();
                        paramMap.put( "MID" , "cjchdS36685633506587");
                        paramMap.put( "ORDER_ID" , order_id);
                        paramMap.put( "CUST_ID" , customer_id);
                        paramMap.put( "CHANNEL_ID" , "WAP");
                        paramMap.put( "TXN_AMOUNT" , "1");
                        paramMap.put( "WEBSITE" , "WEBSTAGING");
                        paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
                        paramMap.put( "CALLBACK_URL", callBackUrl);
                        return paramMap;
                    }
                };
                requestQueue.add(stringRequest);
             //   payUsingUpi(amount,upiId,name,note);
                //submitOrder();
            }
        });

    }


    private void submitOrder() {
        progressDialog.setMessage("Placing order..");
        progressDialog.show();


        final String timestamp=""+System.currentTimeMillis();
        String cost=allTotalPriceTv.getText().toString().trim().replace("$","");

        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("orderId",""+timestamp);
        hashMap.put("orderTime",""+timestamp);
        hashMap.put("orderStatus","InProgress");
        hashMap.put("orderCost",""+cost);
        hashMap.put("orderBy",""+firebaseAuth.getUid());
        hashMap.put("orderTo",""+shopUid);
        hashMap.put("latitude",""+myLatitude);
        hashMap.put("longitude",""+myLongitude);

        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(shopUid).child("Orders");
        ref.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        for(int i=0;i<cartItemList.size();i++){
                            String pId=cartItemList.get(i).getProductId();
                            String id=cartItemList.get(i).getId();
                            String name=cartItemList.get(i).getTitle();
                            String cost=cartItemList.get(i).getPriceEach();
                            String price=cartItemList.get(i).getPrice();
                            String quantity=cartItemList.get(i).getQuantity();

                            HashMap<String,String> hashmap1=new HashMap<>();
                            hashmap1.put("pId",pId);
                            hashmap1.put("title",name);
                            hashmap1.put("cost",cost);
                            hashmap1.put("price",price);
                            hashmap1.put("quantity",quantity);

                           ref.child(timestamp).child("Items").child(pId).setValue(hashmap1);
                        }
                        progressDialog.dismiss();
                        Toast.makeText(shopDetailsActivity.this, "Order Placed Successfully...", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(shopDetailsActivity.this, OrderDetailsUser.class);
                        intent.putExtra("orderTo", shopUid);
                        intent.putExtra("orderId", timestamp);
                        startActivity(intent);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(shopDetailsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void deleteCartData() {
        //delt all records from cart

    }



    private void openMap() {
        String address="https://maps.google.com/maps?saddr="+myLatitude+","+myLongitude+"&addr="+shopLatitude+","+shopLongitude;
        Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(address));
        startActivity(intent);
    }

    private void dialPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(shopPhone))));
        Toast.makeText(this,""+shopPhone,Toast.LENGTH_SHORT).show();
    }

    private void loadMyInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("vegies");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()) {
                            //get uder data
                            String name = "" + ds.child("name").getValue();
                            String email = "" + ds.child("email").getValue();
                            String myPhone = "" + ds.child("phone").getValue();
                            String profileImage = "" + ds.child("profileImage").getValue();
                            String city = "" + ds.child("city").getValue();
                            String accountType = "" + ds.child("accountType").getValue();
                             myLatitude = "" + ds.child("Latitude").getValue();
                             myLongitude = "" + ds.child("Longitude").getValue();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadShopDetails() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("vegies");
        ref.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get shop data
                String name=""+dataSnapshot.child("name").getValue();
                 shopName=""+dataSnapshot.child("shopName").getValue();
                 shopEmail=""+dataSnapshot.child("email").getValue();
                 shopPhone=""+dataSnapshot.child("phone").getValue();
                 shopAddress=""+dataSnapshot.child("address").getValue();
                 shopLatitude=""+dataSnapshot.child("latitude").getValue();
                 shopLongitude=""+dataSnapshot.child("longitude").getValue();
                 deliveryFee=""+dataSnapshot.child("deliveryFee").getValue();
                String profileImage=""+dataSnapshot.child("profileImage").getValue();
                String shopOpen=""+dataSnapshot.child("shopOpen").getValue();

                //set data
                shopNameTv.setText(shopName);
                emailTv.setText(shopEmail);
                deliveryFeeTv.setText("delivery fee...$"+deliveryFee);
                addressTv.setText(shopAddress);
                phoneTv.setText(shopPhone);
                if(shopOpen.equals("true")){
                    openCloseTv.setText("Open");
                }
                else{
                    openCloseTv.setText("Closed");
                }
                try{
                    Picasso.get().load(profileImage).into(shopTv);
                }
                catch(Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadShopProducts() {
        productsList=new ArrayList<>();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("vegies");
        reference.child(shopUid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        productsList.clear();
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            ModelProduct modelProduct=ds.getValue(ModelProduct.class);
                            productsList.add(modelProduct);
                        }
                        //setup adapter
                        adapterProductUser=new AdapterProductUser(shopDetailsActivity.this,productsList);

                        productsRv.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    protected void onPause(){
        super.onPause();
    }
}
