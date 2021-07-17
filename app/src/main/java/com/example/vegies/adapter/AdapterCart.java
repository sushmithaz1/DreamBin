package com.example.vegies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vegies.R;
import com.example.vegies.activities.shopDetailsActivity;
import com.example.vegies.models.ModelCartItem;
import com.example.vegies.models.ModelProduct;
//import com.example.vegies.models.cart;
import com.example.vegies.models.cart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.HolderCart> {
    private Context context;
    private ArrayList<cart> cartItems1;
    public Double allTotalPrice=0.0;
    FirebaseAuth firebaseAuth;
    String pid;

    public AdapterCart(Context context, ArrayList<cart> cartItems1){
        this.context=context;
        this.cartItems1=cartItems1;
    }

    @NonNull
    @Override

    public HolderCart onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.row_cart,parent,false);

        return new HolderCart(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HolderCart holder, final int position) {
        firebaseAuth = FirebaseAuth.getInstance();
          final cart car=cartItems1.get(position);
        final String id=car.getId();
         pid=car.getProductId();
        final String title=car.getTitle();
        final String cost=car.getPriceEach();
        String price=car.getPrice();
        String quantity=car.getQuantity();
        final String timestamp=car.getTimestamp();

        holder.itemTitleTv.setText(title);
        holder.itemPriceEachTv.setText(cost);
        holder.itemPriceTv.setText(price);
        holder.itemQuantityTv.setText("["+quantity+"]");

        holder.itemRemoveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref= FirebaseDatabase.getInstance().getReference("vegies");
                ref.child(firebaseAuth.getUid()).child("CartList").child(timestamp).getRef()
                        .removeValue();
                cartItems1.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,cartItems1.size());
            }
        });

    }


    @Override
    public int getItemCount() {
        return cartItems1.size();
    }


    class  HolderCart extends RecyclerView.ViewHolder{

        private TextView itemTitleTv,itemPriceTv,itemPriceEachTv,itemRemoveTv,itemQuantityTv;


        public HolderCart(@NonNull final View itemView) {

            super(itemView);
            itemTitleTv=itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv=itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv=itemView.findViewById(R.id.itemPriceEachTv);

            itemQuantityTv=itemView.findViewById(R.id.itemQuantityTv);
            itemRemoveTv=itemView.findViewById(R.id.itemRemoveTv);



        }


       /* private void remove(TextView itemTitleTv) {
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("vegies");
            ref.child(firebaseAuth.getUid()).child("CartList").child("itemTitleTv").removeValue();
        }*/
    }

}
