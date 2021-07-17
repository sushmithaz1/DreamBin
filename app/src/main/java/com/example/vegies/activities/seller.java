package com.example.vegies.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.example.vegies.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class seller extends AppCompatActivity {

   private  FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_seller);
        firebaseAuth = FirebaseAuth.getInstance();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user==null){
                    //user not logged in
                    startActivity(new Intent(seller.this, LoginActivity.class));
                    finish();
                }
                else{
                    //user is logger in,check user type
                 //   checkUserType();
                    startActivity(new Intent(seller.this, LoginActivity.class));
                    finish();
                }
            }
        },110);
    }

  /*  private void checkUserType() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("vegies");
        ref.child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String accountType=""+dataSnapshot.child("accountType").getValue();
                        if(accountType.equals("Seller")){
                            //user is seller
                            startActivity(new Intent(seller.this,MainSellerActivity.class));
                            finish();
                        }
                        else{
                            //user is buyer
                            startActivity(new Intent(seller.this,MainUserActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }*/


}
