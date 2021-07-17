package com.example.vegies.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vegies.R;
import com.example.vegies.models.ModelProduct;
import com.example.vegies.models.cart;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.Attributes;

public class Main2Activity extends AppCompatActivity {
    private EditText amountEt,noteEt,nameEt,upiIdEt;
    Button send;
    private String amountt,myLatitude,myLongitude,shopUid;
    private FirebaseAuth firebaseAuth;

    private ArrayList<cart> cartItemList;
            final int UPI_PAYMENT=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initializeViews();
        firebaseAuth=FirebaseAuth.getInstance();

        Intent intent=getIntent();
        amountt=intent.getStringExtra("amount");
        shopUid=intent.getStringExtra("shopUid");
        myLatitude=intent.getStringExtra("myLatitude");
        myLongitude=intent.getStringExtra("myLongitude");

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount="1";
                String note="Hi";
                String name="umesh kotari";
                String upiId="umeshkotari99@oksbi";
                payUsingUpi(amount,upiId,name,note);
            }
        });
    }

     void payUsingUpi(String amount, String upiId, String name, String note) {
        Uri uri=Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa",upiId)
                .appendQueryParameter("pn",name)
                .appendQueryParameter("tn",note)
                .appendQueryParameter("am",amount)
                .appendQueryParameter("cu","INR")
                .build();

        Intent upiPayIntent=new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        Intent chooser=Intent.createChooser(upiPayIntent,"Pay with");

        if(null!=chooser.resolveActivity(getPackageManager())){
            startActivityForResult(chooser,UPI_PAYMENT);
        }
        else{
            Toast.makeText(this, "No UPI app found,Please install Google pay or Phone pay", Toast.LENGTH_SHORT).show();
        }
    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        switch(requestCode)
        {
            case UPI_PAYMENT:
                if((RESULT_OK==resultCode)||(resultCode==11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult:" + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.d("UPI", "onActivityResult:" + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                }
                    else{
                        Log.d("UPI","onActivityResult:"+"Return data is null");
                        ArrayList<String> dataList=new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                    break;
                }
        }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if(isConnectionAvailable(Main2Activity.this)){
            String str= data.get(0);
            Log.d("UPIPAY","upiPaymentDataOperation:"+str);
            String paymentCancel="";
            if(str==null)  str="discard";
                String status="";
                String approvalRefNo="";
                String response[]=str.split("&");
                for(int i=0;i<response.length;i++) {
                    String equalStr[] = response[i].split("=");
                    if (equalStr.length >= 2) {
                        if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                            status = equalStr[1].toLowerCase();
                        } else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                            approvalRefNo = equalStr[1];
                        }
                    } else {
                        paymentCancel = "Payment cancelled by user";
                    }
                }
                    if(status.equals("success")){
                        Toast.makeText(this, "Transaction successfull", Toast.LENGTH_SHORT).show();
                        Log.d("UPI","responseStr:"+approvalRefNo);
                        submitOrder();
                    }
                    else if("Payment cancelled by user.".equals(paymentCancel)){
                        Toast.makeText(this, "Payment cancelled by user", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                    }
                }

        else{
            Toast.makeText(this, "Internet connection is not visible.Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitOrder() {
     //  progreeDialog.setMessage("Placing Order");
     //   progressDialog.show();


        final String timestamp=""+System.currentTimeMillis();
        String cost=amountt;

        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("orderId",""+timestamp);
        hashMap.put("orderTime",""+timestamp);
        hashMap.put("orderStatus","InProgress");
        hashMap.put("orderCost",""+cost);
        hashMap.put("orderBy",""+firebaseAuth.getUid());
        hashMap.put("orderTo",""+shopUid);
        hashMap.put("latitude",""+myLatitude);
        hashMap.put("longitude",""+myLongitude);

        final DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users").child(shopUid).child("Orders");
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
                     //   progressDialog.dismiss();
                        Toast.makeText(Main2Activity.this, "Order Placed Successfully...", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(Main2Activity.this, OrderDetailsUser.class);
                        intent.putExtra("orderTo", shopUid);
                        intent.putExtra("orderId", timestamp);
                        startActivity(intent);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    //    progressDialog.dismiss();
                        Toast.makeText(Main2Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected() && netInfo.isConnectedOrConnecting()) {
                return true;
            }
            else return false;
        }
        return false;
    }

     void initializeViews() {
        send=findViewById(R.id.send);
        amountEt=findViewById(R.id.amount_et);
        noteEt=findViewById(R.id.note);
        nameEt=findViewById(R.id.name);
        upiIdEt=findViewById(R.id.upi_id);
    }
}
