package com.example.vegies.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vegies.R;
import com.example.vegies.activities.shopDetailsActivity;
import com.example.vegies.models.ModelShop;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterShop extends RecyclerView.Adapter<AdapterShop.HolderShop> {

    private Context context;
    public ArrayList<ModelShop> shopsList;

    public AdapterShop(Context context,ArrayList<ModelShop> shopsList){
        this.context=context;
        this.shopsList=shopsList;
    }

    @NonNull
    @Override
    public HolderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_shop.xml
        View view= LayoutInflater.from(context).inflate(R.layout.row_shop,parent,false);
        return new HolderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderShop holder, int position) {
        //get diata
        ModelShop modelShop=shopsList.get(position);
        String accountType=modelShop.getAccountType();
        String address=modelShop.getAddress();
        String city=modelShop.getCity();
        String country=modelShop.getCountry();
      //
        //  String deliveryFee=modelShop.getDeliveryFee();
        String email=modelShop.getEmail();
        String latitude=modelShop.getLatitude();
        String longitude=modelShop.getLongitude();
        String online=modelShop.getOnline();
        String name=modelShop.getName();
        String phone=modelShop.getPhone();
        final String uid=modelShop.getuid();
        String timestamp=modelShop.getTimestamp();
        final String shopOpen=modelShop.getShopOpen();
        String state=modelShop.getState();
        String profileImage=modelShop.getProfileImage();
        String shopName=modelShop.getShopName();

        //set data
        holder.shopNameTv.setText(shopName);
        holder.phoneTv.setText(phone);
        holder.addressTv.setText(address);

        //check if shop open
        if((shopOpen.equals("true"))){
            //shop open
            holder.shopClosedTv.setVisibility(View.GONE);
        }
        else{
            //shop closed
            holder.shopClosedTv.setVisibility(View.VISIBLE);
        }
        try{
            Picasso.get().load(profileImage).placeholder(R.drawable.ic_store).into(holder.shopTv);
        }
        catch(Exception e){
            holder.shopTv.setImageResource(R.drawable.ic_store);
        }
        //handle click listener show shop details
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, shopDetailsActivity.class);
                intent.putExtra("shopUid",uid);

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        //return no of recods
        return shopsList.size();
    }

    class HolderShop extends RecyclerView.ViewHolder
    {
        private ImageView shopTv,onlineTv;
        private RatingBar ratingBar;
        private TextView shopClosedTv,shopNameTv,phoneTv,addressTv;
        public HolderShop(@NonNull View itemView){
            super(itemView);
            shopTv=itemView.findViewById(R.id.shopTv);
            shopClosedTv=itemView.findViewById(R.id.shopClosedTv);
            shopNameTv=itemView.findViewById(R.id.shopNameTv);
            phoneTv=itemView.findViewById(R.id.phoneTv);
            addressTv=itemView.findViewById(R.id.addressTv);
        }
    }
}
