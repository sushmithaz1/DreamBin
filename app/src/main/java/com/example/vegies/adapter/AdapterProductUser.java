package com.example.vegies.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vegies.FilterProduct;
import com.example.vegies.FilterProductUser;
import com.example.vegies.R;
import com.example.vegies.activities.LoginActivity;
import com.example.vegies.activities.MainSellerActivity;
import com.example.vegies.activities.MainUserActivity;
import com.example.vegies.activities.shopDetailsActivity;
import com.example.vegies.models.ModelProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productsList, filterList;
    private FilterProductUser filter;

   // public String timestamp;

    FirebaseAuth firebaseAuth;


    public AdapterProductUser(Context context, ArrayList<ModelProduct> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterList = productsList;
    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_user, parent, false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {
        firebaseAuth=FirebaseAuth.getInstance();

         final ModelProduct modelProduct = productsList.get(position);
        String discountAvailable = modelProduct.getDiscountAvailable();
      //  String dis=discountAvailable;
        String discountNote = modelProduct.getDiscountNote();
        String discountPrice = modelProduct.getDiscountPrice();
        String productCategory = modelProduct.getProductCategory();
        String originalPrice = modelProduct.getOriginalPrice();
        String productDescription = modelProduct.getProductDescription();
        String productTitle = modelProduct.getProductTitle();
        String productQuantity = modelProduct.getProductQuantity();
        String productId = modelProduct.getProductId();
        String timestamp = modelProduct.getTimestamp();
        String productIcon = modelProduct.getProductIcon();

        holder.titleTv.setText(productTitle);
        holder.descriptionTv.setText(productDescription);
        holder.originalPriceTv.setText("Price: $"+originalPrice);
        holder.discountedPriceTv.setText("After Discount: $"+discountPrice);
        holder.discountedNoteTv.setText(discountNote+"%Off");

        if(discountAvailable==null){
            discountAvailable="empty";
        }
        else if ((discountAvailable.equals(true))) {
            holder.discountedPriceTv.setVisibility(View.VISIBLE);
            holder.discountedNoteTv.setVisibility(View.VISIBLE);
            holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);//add strike through on original price
        } else if(discountPrice=="0") {
            //product is on discount
            holder.discountedPriceTv.setVisibility(View.GONE);
            holder.discountedNoteTv.setVisibility(View.GONE);
            holder.originalPriceTv.setPaintFlags(0);
        }

        try {
            Picasso.get().load(productIcon).placeholder(R.drawable.common_google_signin_btn_icon_light).into(holder.productIconTv);
        } catch (Exception e) {
            holder.productIconTv.setImageResource(R.drawable.common_google_signin_btn_icon_light);
        }
        holder.addToCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add to cart
                showQuantityDialog(modelProduct);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show product details

            }
        });
    }


    private double cost = 0.0;
    private double finalCost = 0;
    private int quantity = 0;

    private void showQuantityDialog(ModelProduct modelProduct) {
        //inflate layout for dialog
        //init layout view
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_quantity, null);
        ImageView productTv = view.findViewById(R.id.productTv);
        final TextView titleTv = view.findViewById(R.id.titleTv);
        final TextView pQuantityTv = view.findViewById(R.id.pQuantityTv);
        TextView descriptionTv = view.findViewById(R.id.descriptionTv);
        TextView discountedNoteTv = view.findViewById(R.id.discountedNoteTv);
        final TextView originalPriceTv = view.findViewById(R.id.originalPriceTv);
        TextView priceDiscountedTv = view.findViewById(R.id.priceDiscountedTv);
        final TextView finalPriceTv = view.findViewById(R.id.finalPriceTv);
        ImageButton decrementBtn = view.findViewById(R.id.decrementBtn);
        final TextView quantityTv = view.findViewById(R.id.quantityTv);
        ImageButton incrementBtn = view.findViewById(R.id.incrementBtn);
        Button continueBtn = view.findViewById(R.id.continueBtn);

        //get data from model

        final String productId = modelProduct.getProductId();
        String title = modelProduct.getProductTitle();
        String productQuantity = modelProduct.getProductQuantity();
        String description = modelProduct.getProductDescription();
        String discountNote = modelProduct.getDiscountNote();
        String image = modelProduct.getProductIcon();

        final String price;
        price = modelProduct.getOriginalPrice();
        String r="";

        if(modelProduct.getDiscountAvailable()==null){
             r=modelProduct.getDiscountAvailable();
            r="emptyString";
        }
       else if(r==null){
            String d=modelProduct.getDiscountAvailable();
            d="empty";
        }

        else if (modelProduct.getDiscountAvailable().equals("true")) {

            //pro hv disciunt
            discountedNoteTv.setVisibility(view.VISIBLE);
            originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);//add strike through on original price
        } else {

            //product doesnt hv discount
            discountedNoteTv.setVisibility(View.GONE);
            priceDiscountedTv.setVisibility(View.GONE);
         //   price = modelProduct.getOriginalPrice();
        }
        cost = Double.parseDouble(price);
        finalCost = Double.parseDouble(price);
        quantity = 1;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        //set data
        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_shopping_cart_black_24dp).into(productTv);
        } catch (Exception e) {
            productTv.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
        }
        titleTv.setText("" + title);
        pQuantityTv.setText("" + productQuantity);
        descriptionTv.setText("" + description);
        discountedNoteTv.setText("" + discountNote);
        quantityTv.setText("" + quantity);
        originalPriceTv.setText("" + modelProduct.getOriginalPrice());
        priceDiscountedTv.setText("" + modelProduct.getDiscountPrice());
        finalPriceTv.setText("" + finalCost);

        final AlertDialog dialog = builder.create();
        dialog.show();
//incrse quantity
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost = finalCost + cost;
                quantity++;

                finalPriceTv.setText("" + finalCost);
                quantityTv.setText("" + quantity);
            }
        });
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    finalCost = finalCost - cost;
                    quantity--;

                    finalPriceTv.setText(""+finalCost);
                    quantityTv.setText("" + quantity);
                }
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleTv.getText().toString().trim();
                String priceEach = price;
                String totalPrice = finalPriceTv.getText().toString();
                String quantity = quantityTv.getText().toString().trim();

                addToCart(productId, title, priceEach, totalPrice, quantity);

                dialog.dismiss();
            }
        });
    }

    private int itemId = 1;
    public void addToCart(String productId, String title, String priceEach, String price, String quantity) {
        final String timestamp=""+System.currentTimeMillis();

        itemId++;
        HashMap<String,Object> hashmap=new HashMap<>();
        hashmap.put("productId",productId);
        hashmap.put("title",title);
        hashmap.put("priceEach",priceEach);
        hashmap.put("price",price);
        hashmap.put("quantity",quantity);
        hashmap.put("timestamp",timestamp);


        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("vegies");
        ref.child(firebaseAuth.getUid()).child("CartList").child(timestamp).setValue(hashmap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Cart Added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

       /* try {
            EasyDB easyDB = EasyDB.init(context, "vegies")

                    .setTableName("ITEMS TABLE")
                    .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                    .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                    .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                    .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                    .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                    .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                    .doneTableColumn();

            Boolean b = easyDB.addData("Item_Id", itemId)
                    .addData("Item_PID", productId)
                    .addData("Item_Name", title)
                    .addData("Item_Price_Each", priceEach)
                    .addData("Item_Price", price)
                    .addData("Item_Quantity", quantity)
                    .doneDataAdding();

        } catch (Exception e) {
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }*/

    }


    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterProductUser(this, filterList);
        }
        return filter;
    }


    class HolderProductUser extends RecyclerView.ViewHolder {
        private ImageView productIconTv;
        private TextView discountedNoteTv, titleTv, descriptionTv, addToCartTv,
                discountedPriceTv, originalPriceTv;

        public HolderProductUser(@NonNull View itemView) {
            super(itemView);
            productIconTv = itemView.findViewById(R.id.productIconTv);
            discountedNoteTv = itemView.findViewById(R.id.discountedNoteTv);
            titleTv = itemView.findViewById(R.id.titleTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            addToCartTv = itemView.findViewById(R.id.addToCartTv);
            discountedPriceTv = itemView.findViewById(R.id.discountedPriceTv);
            originalPriceTv = itemView.findViewById(R.id.originalPriceTv);
        }
    }
}
