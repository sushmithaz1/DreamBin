package com.example.vegies.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vegies.R;
import com.example.vegies.activities.MainSellerActivity;
import com.example.vegies.activities.OrderDetailsUser;
import com.example.vegies.activities.orderDetailsSeller;
import com.example.vegies.activities.shopDetailsActivity;
import com.example.vegies.models.ModelOrderSeller;
import com.example.vegies.models.ModelOrderUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static java.util.Locale.*;

public class AdapterOrderSeller extends RecyclerView.Adapter<AdapterOrderSeller.HolderOrderSeller> {
    String latitude,longitude,OrderBy,OrderTo;
    FirebaseAuth firebaseAuth;

    private Context context;
    private ArrayList<ModelOrderSeller> orderUsersList;
    public String Name;

    public String address;

    public AdapterOrderSeller(Context context, ArrayList<ModelOrderSeller> orderUsersList) {
        this.context = context;
        this.orderUsersList = orderUsersList;
    }

    @NonNull
    @Override
    public HolderOrderSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_order_seller,parent,false);
        return new HolderOrderSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HolderOrderSeller holder, int position) {
        firebaseAuth=FirebaseAuth.getInstance();
        final ModelOrderSeller modelOrderUser=orderUsersList.get(position);
        final String OrderId=modelOrderUser.getOrderId();
         OrderBy=modelOrderUser.getOrderBy();
         OrderTo=modelOrderUser.getOrderTo();
        String OrderCost=modelOrderUser.getOrderCost();
        String OrderStatus=modelOrderUser.getOrderStatus();
        String OrderTime=modelOrderUser.getOrderTime();

        loadShopInfo(modelOrderUser,holder);

        holder.amountTv.setText("Amount: $"+ OrderCost);
        holder.statusTv.setText(OrderStatus);
        holder.orderIdTv.setText("OrderId: "+OrderId);
        if(OrderStatus.equals("In Progress")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.design_default_color_primary_dark));
        }
        else if(OrderStatus.equals("completed")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.design_default_color_primary_variant));
        }
        else if(OrderStatus.equals("cancelled")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.design_default_color_secondary));
        }

        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(OrderTime));
        String formatedDate= DateFormat.format("dd/MM/yyyy",calendar).toString();

        holder.dateTv.setText(formatedDate);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, orderDetailsSeller.class);
                intent.putExtra("orderBy", OrderBy);
                intent.putExtra("orderId", OrderId);
                intent.putExtra("orderTo", OrderTo);
                context.startActivity(intent);
            }
        });
    }


    private void loadShopInfo(ModelOrderSeller modelOrderUser, final HolderOrderSeller holder) {
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("vegies");
            ref.child(OrderBy)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            latitude=""+dataSnapshot.child("latitude").getValue();
                            longitude=""+dataSnapshot.child("longitude").getValue();
                            Name=""+dataSnapshot.child("name").getValue();
                           holder.shopNameTv.setText(Name);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }


    @Override
    public int getItemCount() {
        return orderUsersList.size();
    }

    class HolderOrderSeller extends RecyclerView.ViewHolder{
        private TextView orderIdTv,dateTv,shopNameTv,amountTv,statusTv,deliveryAddressTv;
        RecyclerView itemsRv;
        public HolderOrderSeller(View view){
            super(view);

            orderIdTv=view.findViewById(R.id.orderIdTv);
            dateTv=view.findViewById(R.id.dateTv);
            shopNameTv=view.findViewById(R.id.shopNameTv);
            amountTv=view.findViewById(R.id.amountTv);
            statusTv=view.findViewById(R.id.statusTv);
            itemsRv=view.findViewById(R.id.itemsRv);

        }
    }
}
