package com.example.vegies.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vegies.FilterProduct;
import com.example.vegies.activities.activity_Product;
import com.example.vegies.models.ModelProduct;
import com.example.vegies.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProductSeller extends RecyclerView.Adapter<AdapterProductSeller.HolderProductSeller>implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList,filterList;
    private FilterProduct filter;


    public AdapterProductSeller(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList=productList;
    }


    @NonNull
    @Override
    public HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view= LayoutInflater.from(context).inflate(R.layout.row_product_seller,parent,false);
        return new HolderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HolderProductSeller holder, int position) {
        //get data
        final ModelProduct modelProduct=productList.get(position);
        final String id=modelProduct.getProductId();
        String uid=modelProduct.getUid();
        String discountAvailable=modelProduct.getDiscountAvailable();
        String discountNote=modelProduct.getDiscountNote();
        String discountPrice=modelProduct.getDiscountPrice();
        String productCategory=modelProduct.getProductCategory();
        String productDescription=modelProduct.getProductDescription();
        String icon=modelProduct.getProductIcon();
        String quantity=modelProduct.getProductQuantity();
        final String title=modelProduct.getProductTitle();
        String originalPrice=modelProduct.getOriginalPrice();
        String timestamp=modelProduct.getTimestamp();

        holder.titleTv.setText(title);
        holder.quantityTv.setText(quantity);
        holder.discountedNoteTv.setText(discountNote+"%Off");
        holder.discountedPriceTv.setText("After Discount: $"+discountPrice);
        holder.originalPriceTv.setText("Price: $"+originalPrice);

        if(discountAvailable==null){
            discountAvailable="empty";
        }

      else if(discountPrice=="0") {
            //product is on discount
            holder.discountedPriceTv.setVisibility(View.GONE);
            holder.discountedNoteTv.setVisibility(View.GONE);
            holder.originalPriceTv.setPaintFlags(0);
        }
      else if((discountAvailable.equals("true")))
      {
            //product is on discount
          holder.discountedPriceTv.setVisibility(View.VISIBLE);
          holder.discountedNoteTv.setVisibility(View.VISIBLE);
          holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);//add strike through on original price
        }

        try{
            Picasso.get().load(icon).placeholder(R.drawable.common_google_signin_btn_icon_light).into(holder.productIconTv);
        }
        catch(Exception e){
            holder.productIconTv.setImageResource(R.drawable.common_google_signin_btn_icon_light);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, activity_Product.class);
                intent.putExtra("Id", id);
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
       // return 11;
       return productList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new FilterProduct(this,filterList);
        }
        return filter;
    }

    class HolderProductSeller extends RecyclerView.ViewHolder{

        /*holds f recycler view*/
        private ImageView productIconTv;
        private TextView discountedNoteTv,titleTv,quantityTv,discountedPriceTv,originalPriceTv;

        public HolderProductSeller(@NonNull View itemView) {
            super(itemView);

            productIconTv=itemView.findViewById(R.id.productIconTv);
            discountedNoteTv=itemView.findViewById(R.id.discountedNoteTv);
            titleTv=itemView.findViewById(R.id.titleTv);
            quantityTv=itemView.findViewById(R.id.quantityTv);
            discountedPriceTv=itemView.findViewById(R.id.discountedPriceTv);
            originalPriceTv=itemView.findViewById(R.id.originalPriceTv);

        }
    }
}
