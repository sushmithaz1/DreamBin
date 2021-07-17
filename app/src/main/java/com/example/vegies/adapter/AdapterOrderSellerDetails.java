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
import com.example.vegies.models.ModelOrderSellerDetails;
import com.example.vegies.models.ModelOrderedItem;

import java.util.ArrayList;

public class AdapterOrderSellerDetails extends RecyclerView.Adapter<AdapterOrderSellerDetails.HolderOrderedItem> {
    private Context context;
    private ArrayList<ModelOrderSellerDetails> orderedItemArrayList;

    public AdapterOrderSellerDetails(Context context, ArrayList<ModelOrderSellerDetails> orderedItemArrayList) {
        this.context = context;
        this.orderedItemArrayList = orderedItemArrayList;
    }

    @NonNull
    @Override
    public HolderOrderedItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_cart_item,parent,false);
        return new HolderOrderedItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderedItem holder, int position) {
        ModelOrderSellerDetails modelOrderedItem=orderedItemArrayList.get(position);
        String pId=modelOrderedItem.getpId();
        String Title=modelOrderedItem.getTitle();
        String Cost=modelOrderedItem.getCost();
        String Price=modelOrderedItem.getPrice();
        String Quantity=modelOrderedItem.getQuantity();

        holder.itemTitleTv.setText(Title);
        holder.itemPriceEachTv.setText("$"+Cost);
        holder.itemPriceTv.setText(Price);
        holder.itemQuantityTv.setText("["+Quantity+"]");
    }

    @Override
    public int getItemCount() {
        return orderedItemArrayList.size();
    }

    class HolderOrderedItem extends RecyclerView.ViewHolder{

        private TextView itemTitleTv,itemPriceTv,itemPriceEachTv,itemQuantityTv;
        public HolderOrderedItem(@NonNull View itemView) {
            super(itemView);
                       itemTitleTv=itemView.findViewById(R.id.itemTitleTv);
                       itemPriceTv=itemView.findViewById(R.id.itemPriceTv);
                       itemPriceEachTv=itemView.findViewById(R.id.itemPriceEachTv);
                       itemQuantityTv=itemView.findViewById(R.id.itemQuantityTv);
        }


    }
}
