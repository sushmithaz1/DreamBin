package com.example.vegies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vegies.R;
import com.example.vegies.models.ModelCartItem;
import com.example.vegies.models.cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCart>{
    private Context context;
    private ArrayList<ModelCartItem> cartItems;
    FirebaseAuth firebaseAuth;


    public AdapterCartItem(Context context,ArrayList<ModelCartItem> cartItems){
        this.context=context;
        this.cartItems=cartItems;
    }

    @NonNull
    @Override
    public AdapterCartItem.HolderCart onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_cart,parent,false);
        return new HolderCart(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCartItem.HolderCart holder, int position) {
         firebaseAuth = FirebaseAuth.getInstance();
        ModelCartItem car=cartItems.get(position);
        String id=car.getId();
        String pid=car.getPid();
        String title=car.getName();
        String priceEach=car.getPrice();
        String cost=car.getCost();
        String quantity=car.getQuantity();

        holder.itemTitleTv.setText(title);
        holder.itemPriceEachTv.setText(priceEach);
        holder.itemQuantityTv.setText(quantity);
       // holder.itemPriceTv.setText(price);


     /* DatabaseReference reference=FirebaseDatabase.getInstance().getReference("vegies");
      reference.orderByChild("uid")
              .addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      for(DataSnapshot ds:dataSnapshot.getChildren()){
                          String d=""+ds.child("price").getValue();
                          allTotalPrice=allTotalPrice+Double.parseDouble(d);
                      }
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {

                  }
              });
*/


    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }


    class  HolderCart extends RecyclerView.ViewHolder{

        private TextView itemTitleTv,itemPriceTv,itemPriceEachTv,itemRemoveTv,itemQuantityTv;

        public HolderCart(@NonNull View itemView) {
            super(itemView);
            itemTitleTv=itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv=itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv=itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv=itemView.findViewById(R.id.itemQuantityTv);
            itemRemoveTv=itemView.findViewById(R.id.itemRemoveTv);

        }
    }

}
