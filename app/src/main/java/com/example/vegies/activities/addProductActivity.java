package com.example.vegies.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vegies.Constants;
import com.example.vegies.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class addProductActivity extends AppCompatActivity {

   // private static final String WRITE_EXTERNAL_STORAGE ="" ;
    private ImageButton backBtn;
    private ImageView productIconTv;
    private EditText titleEt,descriptionEt;
    private TextView categoryTv,quantityTv,PriceTv,discountedPrice
            ,discountedNoteTv;
    private Button addProductBtn;
    private SwitchCompat discountSwitch;

    private static final int CAMERA_REQUEST_CODE=200;
    private static final int STORAGE_REQUEST_CODE=300;
    private static final int IMAGE_PICK_GALLERY_CODE=400;
    private static final int IMAGE_PICK_CAMERA_CODE=500;

    private String[] cameraPermissions;
    private String[] storagePermission;

    private Uri image_uri;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        backBtn=findViewById(R.id.backBtn);
        productIconTv=findViewById(R.id.productIconTv);
        titleEt=findViewById(R.id.titleEt);
        descriptionEt=findViewById(R.id.descriptionEt);
        categoryTv=findViewById(R.id.categoryTv);
        quantityTv=findViewById(R.id.quantityTv);
        PriceTv=findViewById(R.id.PriceTv);
        discountSwitch=findViewById(R.id.discountSwitch);
        discountedPrice=findViewById(R.id.discountedPrice);
        discountedNoteTv=findViewById(R.id.discountedNoteTv);
        addProductBtn=findViewById(R.id.addProcuctBtn);

        discountedPrice.setVisibility(View.GONE);
        discountedNoteTv.setVisibility(View.GONE);

        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        cameraPermissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        discountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    discountedPrice.setVisibility(View.VISIBLE);
                    discountedNoteTv.setVisibility(View.VISIBLE);
                }
                else{
                    discountedPrice.setVisibility(View.GONE);
                    discountedNoteTv.setVisibility(View.GONE);
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        productIconTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });
        categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
            }
        });
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //flow,imput data,add data to db,validate
                inputData();
            }
        });
    }

    private String productTitle,productDescription,productCategory,productQuantity,originalPrice,discountPrice,discountNote;
    private boolean discountAvailable=false;

    private void inputData() {
        productTitle=titleEt.getText().toString().trim();
        productDescription=descriptionEt.getText().toString().trim();
        productCategory=categoryTv.getText().toString().trim();
        productQuantity=quantityTv.getText().toString().trim();
        originalPrice=PriceTv.getText().toString().trim();
        discountAvailable=discountSwitch.isChecked();

        //validation
        if(TextUtils.isEmpty(productTitle)){
            Toast.makeText(this, "Title is requird...", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(productCategory)){
            Toast.makeText(this, "category is requird...", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(originalPrice)){
            Toast.makeText(this, "price is requird...", Toast.LENGTH_SHORT).show();
            return;
        }
        if(discountAvailable){
            discountPrice=discountedPrice.getText().toString().trim();
            discountNote=discountedNoteTv.getText().toString().trim();
            if(TextUtils.isEmpty(discountPrice)){
                Toast.makeText(this, "Discount Price...", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else{
            discountPrice="0";
            discountNote="";
        }
        addProduct();

    }

    private void addProduct() {
        progressDialog.setMessage("Adding Product...");
        progressDialog.show();

        final String timestamp=""+System.currentTimeMillis();
        if(image_uri==null){
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("productId",""+timestamp);
            hashMap.put("productTitle",""+productTitle);
            hashMap.put("productDescription",""+productDescription);
            hashMap.put("productCategory",""+productCategory);
            hashMap.put("productQuantity",""+productQuantity);
            hashMap.put("productIcon","");
            hashMap.put("originalPrice",""+originalPrice);
            hashMap.put("discountPrice",""+discountPrice);
            hashMap.put("discountNote",""+discountNote);
            hashMap.put("discountAvailable",""+discountAvailable);
            hashMap.put("timestamp",""+timestamp);
            hashMap.put("uid",""+firebaseAuth.getUid());

            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("vegies");
            reference.child(firebaseAuth.getUid()).child("Products").child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(addProductActivity.this, "Product added", Toast.LENGTH_SHORT).show();
                            clearData();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(addProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            //upload im,first upload to storage then by using path of img to be uploaded
            String filePathAndName="product_images/" +""+timestamp;
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //image uploaded
                            //get url of uploaded img
                            Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                                Uri downloadImageUri=uriTask.getResult();
                                if(uriTask.isSuccessful()){
                                    HashMap<String,Object> hashMap=new HashMap<>();
                                    hashMap.put("productId",""+timestamp);
                                    hashMap.put("productTitle",""+productTitle);
                                    hashMap.put("productDescription",""+productDescription);
                                    hashMap.put("productCategory",""+productCategory);
                                    hashMap.put("productQuantity",""+productQuantity);
                                    hashMap.put("productIcon",""+downloadImageUri);
                                    hashMap.put("originalPrice",""+originalPrice);
                                    hashMap.put("discountPrice",""+discountPrice);
                                    hashMap.put("discountNote",""+discountNote);
                                    hashMap.put("discountAvailable",""+discountAvailable);
                                    hashMap.put("timestamp",""+timestamp);
                                    hashMap.put("uid",""+firebaseAuth.getUid());

                                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("vegies");
                                    reference.child(firebaseAuth.getUid()).child("Products").child(timestamp).setValue(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(addProductActivity.this, "Product added", Toast.LENGTH_SHORT).show();
                                                    clearData();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(addProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(addProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void clearData() {
        //clear data
        titleEt.setText("");
        descriptionEt.setText("");
        categoryTv.setText("");
        quantityTv.setText("");
        PriceTv.setText("");
        discountedPrice.setText("");
        discountedNoteTv.setText("");
        productIconTv.setImageResource(R.drawable.ic_add_circle);
        image_uri=null;

    }

    private void categoryDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Product Company").setItems(Constants.productCategories, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //get picked category
                String category=Constants.productCategories[which];
                //set picked category
                categoryTv.setText(category);
            }
        }).show();
    }

    private void showImagePickDialog() {
        String[] options={"Camera","Gallery"};
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        builder.setTitle("Pick Image").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(addProductActivity.this, "ok", Toast.LENGTH_SHORT).show();
                if(which==0){
                    if(checkCameraPermission()){
                        pickFromCamera();
                    }
                    else{
                        requestCameraPermission();
                    }
                }
                else{
                    if(checkStoragePermission()){
                        pickFromGallery();
                    }
                    else{
                        requestStoragePermission();
                    }
                }
            }
        }).show();
    }

    private void pickFromGallery() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/e");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }
    private void pickFromCamera(){
        //USING mediastore to pick high quality image
        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Image_Description");

        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }
    private boolean checkStoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermission(){
        boolean result=ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1=ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

        return result&& result1;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);
    }

    //handle permission results

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        //both pemission granted
                        pickFromCamera();
                    }
                    else{
                        Toast.makeText(this, "Camera And Storage Permissions are required...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        pickFromGallery();
                    }
                    else{
                        Toast.makeText(this, "Stroage Permision required...", Toast.LENGTH_SHORT).show();
                    }

                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==IMAGE_PICK_GALLERY_CODE){
                image_uri=data.getData();
                productIconTv.setImageURI(image_uri);
            }
            else if(requestCode==IMAGE_PICK_CAMERA_CODE){
                productIconTv.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
