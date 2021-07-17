package com.example.vegies.adapter;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vegies.R;
import com.example.vegies.models.ModelShowProduct;
import com.example.vegies.models.cart;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class AdapterShowProduct extends RecyclerView.Adapter<AdapterShowProduct.HolderShowProduct> {
    Context context;
    private ArrayList<ModelShowProduct> product;
    FirebaseAuth firebaseAuth;

    public AdapterShowProduct(Context context, ArrayList<ModelShowProduct> product) {
        this.context = context;
        this.product = product;
    }

    @NonNull
    @Override
    public HolderShowProduct onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_cart,parent,false);
        return new HolderShowProduct(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull HolderShowProduct holder, int position) {
        firebaseAuth = FirebaseAuth.getInstance();
        final ModelShowProduct car=product.get(position);
        String productId = car.getProductId();
        String productTitle = car.getProductTitle();
        String productDescription = car.getProductDescription();
        String productCategory = car.getProductCategory();
        String productQuantity = car.getProductQuantity();
        String productIcon = car.getProductIcon();
        String originalPrice = car.getOriginalPrice();
        String discountPrice = car.getDiscountPrice();
        String discountNote = car.getDiscountNote();
        String discountAvailable = car.getDiscountAvailable();

        holder.titleEt.setText(productId);
        holder.descriptionEt.setText(productDescription);
        holder.categoryTv.setText(productCategory);
        holder.quantityTv.setText(productQuantity);
        holder.PriceTv.setText(originalPrice);
        holder.discountedPrice.setText(discountPrice);
        holder.discountedNoteTv.setText(discountNote);
        holder.productIconTv.setImageIcon(Icon.createWithContentUri(productIcon));
    }

    @Override
    public int getItemCount() {
        return product.size();
    }

    class HolderShowProduct extends RecyclerView.ViewHolder{
        private EditText titleEt,  descriptionEt,categoryTv,quantityTv,PriceTv,discountedPrice
                ,discountedNoteTv;
        private ImageButton productIconTv;

        public HolderShowProduct(@NonNull View context) {
            super(context);
        }
    }
}
