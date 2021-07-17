package com.example.vegies.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vegies.R;
import com.example.vegies.adapter.AdapterCart;
import com.example.vegies.adapter.AdapterCartItem;
import com.example.vegies.models.ModelCartItem;
import com.example.vegies.models.cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class shopDetailsActivity2 extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCart adapterCartItem;

    public Double deliveryFee;

    public TextView sTotalTv,dFeeTv,allTotalPriceTv,dFeeLabelTv,sTotalLabelTv,totalLabelTv;
    private Button CheckoutBtn;
    public Double allTotalPrice=0.0;

    //inflate cart layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firebaseAuth=FirebaseAuth.getInstance();

        cartItemList =new ArrayList<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_cart);
        View view= LayoutInflater.from(this).inflate(R.layout.dialog_cart,null);

        TextView shopNameTv=view.findViewById(R.id.shopNameTv);
        final RecyclerView cartItemsRv=view.findViewById(R.id.cartItemsRv);
        sTotalTv=view.findViewById(R.id.sTotalTv);
        sTotalLabelTv=view.findViewById(R.id.sTotalLabelTv);
        dFeeTv=view.findViewById(R.id.dFeeTv);
        dFeeLabelTv=view.findViewById(R.id.dFeeLabelTv);
        allTotalPriceTv=view.findViewById(R.id.totalTv);
        totalLabelTv=view.findViewById(R.id.totalLabelTv);
         CheckoutBtn=view.findViewById(R.id.CheckoutBtn);

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //set view to dialog
        builder.setView(view);
  //      shopNameTv.setText(shopName);


        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("vegies");
        ref.child(firebaseAuth.getUid()).child("CartList")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        cartItemList.clear();
                    for(DataSnapshot ds:dataSnapshot.getChildren()) {
                            ModelCartItem car = ds.getValue(
                                    ModelCartItem.class);
                         //   String d=ds.getValue(String.class);
                            if(allTotalPrice==0.0){
                               // allTotalPrice=Double.parseDouble(d);
                           //     System.out.println(d);
                            }
                            else {
                             //   allTotalPrice = allTotalPrice + Double.parseDouble(d);
                            }
                            cartItemList.add(car);
                        }

                    //    adapterCartItem=new AdapterCart(shopDetailsActivity2.this,cartItemList);
                        cartItemsRv.setAdapter(adapterCartItem);

                        dFeeTv.setText(""+deliveryFee);
                        sTotalTv.setText(""+String.format("%.2f",allTotalPrice));
                    //    allTotalPriceTv.setText(""+(allTotalPrice+Double.parseDouble(""+deliveryFee)));

                        AlertDialog  dialog=builder.create();
                        dialog.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
