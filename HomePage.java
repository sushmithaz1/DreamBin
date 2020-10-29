package com.example.vegies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vegies.activities.sdu;


public class HomePage extends AppCompatActivity {
    private Toolbar toolbar;
    private Button button;
  // final ModelProduct modelProduct=productsList.get(position);

    ListView lst;

    String[] fruitname={"Mango","Banana","Watermelon","Grapes","Kiwi","Apple"};
    String[] desc={"This is mango","This is Banana","This is Watermelon","This is Grapes","This is Kiwi","This is Apple"};
    Integer[] imgid={R.drawable.m,R.drawable.p,R.drawable.u,R.drawable.v,R.drawable.w,R.drawable.x};
    private Activity context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_home_page);

        lst=(ListView)findViewById(R.id.listview);

        CustomListView customListView=new CustomListView(this,fruitname,desc,imgid);
        lst.setAdapter(customListView);

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                my();
            }

        });
    }

    private void my() {
        Intent intent=new Intent(this, sdu.class);
        startActivity(intent);
    }


    /*
   private double cost=0;
    private double finalCost=0;
    private int quantity=0;
    private void openactivity(ModelProduct modelProduct){
        View view= LayoutInflater.from(context).inflate(R.layout.dialog_quantity,null);
        ImageView productTv=view.findViewById(R.id.productTv);
        final TextView titleTv=view.findViewById(R.id.titleTv);
        TextView pQuantityTv=view.findViewById(R.id.pQuantityTv);
        TextView descriptionTv=view.findViewById(R.id.descriptionTv);
        TextView discountedNoteTv=view.findViewById(R.id.discountedNoteTv);
        final TextView originalPriceTv=view.findViewById(R.id.originalPriceTv);
        TextView priceDiscountedTv=view.findViewById(R.id.priceDiscountedTv);
        final TextView finalPriceTv=view.findViewById(R.id.finalPriceTv);
        TextView decrementBtn=view.findViewById(R.id.decrementBtn);
        final TextView quantityTv=view.findViewById(R.id.quantityTv);
        TextView incrementBtn=view.findViewById(R.id.incrementBtn);
        TextView continueBtn=view.findViewById(R.id.continueButton);

        final String productId=modelProduct.getProductId();
        String title=modelProduct.getProductTitle();
        String productQuantity=modelProduct.getProductQuantity();
        String description=modelProduct.getProductDescription();
        String discountNote=modelProduct.getDiscountNote();
        String Image=modelProduct.modelProduct.getProductIcon();


        String price;
       if(modelProduct.getDiscountAvailable().equals("true")){
            price=modelProduct.getDiscountPrice();
            discountedNoteTv.setVisibility(view.VISIBLE);
            originalPriceTv.setPaintflags(originalPriceTv.getPaintflags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            discountedNoteTv.setVisibility(View.GONE);
            priceDiscountedTv.setVisibility(View.GONE);
            price=modelProduct.getOriginalPrice();
        }
        cost=Double.parseDouble(price.replaceAll("$",""));
        finalCost=Double.parseDouble(price.replaceAll("$",""));
        quantity=1;

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(view);

        try{
            Picasso.get().load(image).placeholder(R.drawable.ic_shopping_cart_black_24dp).inta(productTv);
        }
        catch(Exception e){
            productTv.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
        }
        titleTv.setText(""+title);
        pQuantityTv.setText(""+productQuantity);
        discountedNoteTv.setText(""+discountNote);
        quantityTv.setText(""+quantity);
        originalPriceTv.setText("$"+modelProduct.getOriginalPrice);
        priceDiscountedTv.setText("$"+modelProduct.getDiscountPRice);
        finalPriceTv.setText("$"+modelProduct.finalCost);

        final AlertDialog dialog=builder.create();
        dialog.show();

        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost=finalCost+cost;
                quantity++;

                finalPriceTv.setText("$"+finalCost);
                quantityTv.setText(""+quantity);
            }
        });
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity>1){
                    finalCost=finalCost-cost;
                    quantity--;

                    finalPriceTv.setText("$"+finalCost);
                    quantityTv.setText(""+quantity);
                }
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=titleTv.getText().toString().trim();
                String priceEach=originalPriceTv.getText().toString().trim().replace("$","");
                String price=originalPriceTv.getText().toString().replace("","");
                String quantity=quantityTv.getText().toString().trim();

                addToCart(productId,title,priceEach,price,quantity);

                dialog.dismiss();
            }

            private int itemId=1;
            private void addToCart(String productId, String title, String priceEach, String price, String quantity) {
                itemId++;
                EasyDB=EasyDB.init(context,"ITEMS_DB");
            }
        });

    }

*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.share){
            Toast.makeText(getApplicationContext(),"You click share",Toast.LENGTH_SHORT).show();
        }
        else  if(id==R.id.about){
            Toast.makeText(getApplicationContext(),"You click about",Toast.LENGTH_SHORT).show();
        }
        else  if(id==R.id.exit){
            Toast.makeText(getApplicationContext(),"You click exit",Toast.LENGTH_SHORT).show();
        }
        else  if(id==R.id.search){
            Toast.makeText(getApplicationContext(),"You click search",Toast.LENGTH_SHORT).show();
        }
        else  if(id==R.id.setting){
            Toast.makeText(getApplicationContext(),"You click setting",Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}




