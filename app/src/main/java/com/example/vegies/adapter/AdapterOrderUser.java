package com.example.vegies.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vegies.R;
import com.example.vegies.activities.OrderDetailsUser;
import com.example.vegies.models.ModelOrderUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderUser extends RecyclerView.Adapter<AdapterOrderUser.HolderOrderUser>{

    private Context context;
    private ArrayList<ModelOrderUser> orderUserList;

    public AdapterOrderUser(Context context, ArrayList<ModelOrderUser> orderUserList) {
        this.context = context;
        this.orderUserList = orderUserList;
    }

    @NonNull
    @Override
    public HolderOrderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_order_user,parent,false);
        return new HolderOrderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderUser holder, int position) {
        ModelOrderUser modelOrderUser=orderUserList.get(position);
        final String OrderId=modelOrderUser.getOrderId();
        String OrderBy=modelOrderUser.getOrderBy();
        final String OrderTo=modelOrderUser.getOrderTo();
        String OrderCost=modelOrderUser.getOrderCost();
        String OrderStatus=modelOrderUser.getOrderStatus();
        String OrderTime=modelOrderUser.getOrderTime();

        loadShopInfo(modelOrderUser,holder);

        holder.amountTv.setText("Amount: $"+ OrderCost);
        holder.statusTv.setText( OrderStatus);
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
                Intent intent=new Intent(context, OrderDetailsUser.class);
                intent.putExtra("orderTo", OrderTo);
                intent.putExtra("orderId", OrderId);
                context.startActivity(intent);
            }
        });
    }

    private void loadShopInfo(ModelOrderUser modelOrderUser, final HolderOrderUser holder) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("vegies");
        ref.child(modelOrderUser.getOrderTo())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String shopName=""+dataSnapshot.child("shopName").getValue();
                        //  Toast.makeText(context, "shopname succed"+dataSnapshot, Toast.LENGTH_SHORT).show();
                        holder.shopNameTv.setText(shopName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(context, ""+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public int getItemCount() {
        return orderUserList.size();
    }

    class HolderOrderUser extends RecyclerView.ViewHolder{

        private TextView orderIdTv,dateTv,shopNameTv,amountTv,statusTv,nextTv;
        public HolderOrderUser(View itemview){
            super(itemview);
            orderIdTv = itemview.findViewById(R.id.orderIdTv);
            dateTv = itemview.findViewById(R.id.dateTv);
            shopNameTv = itemview.findViewById(R.id.shopNameTv);
            amountTv = itemview.findViewById(R.id.amountTv);
            statusTv = itemview.findViewById(R.id.statusTv);
        }
    }
}
