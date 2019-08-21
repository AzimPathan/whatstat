package com.azimpathan.whatsstat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;

/*
    An activity class for uploading text status
*/

public class AddTextActivity extends AppCompatActivity {

    private EditText statusET;
    private String URL=null;
    private String colors[],fonts[];
    private String colorString="#ff99cc00",fontString="segoe_marker";
    private ProgressDialog progressDialog;
    private FloatingActionButton uploadStatusFab,changeBGcolorFab,changeFontFab;
    private int counterForBackgroundColor,counterForFont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);
        counterForBackgroundColor=0;
        counterForFont=0;
        final RelativeLayout layout=findViewById(R.id.status_relative_layout);
        statusET=findViewById(R.id.etText);
        uploadStatusFab=findViewById(R.id.fabUploadStatus);
        changeBGcolorFab=findViewById(R.id.fabChangeBGcolor);
        changeFontFab=findViewById(R.id.fabChangeFont);
        uploadStatusFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(statusET.getText().length()>0)
                {
                    statusET.setFreezesText(true);
                    layout.requestFocus();
                    uploadStatus();
                }
            }
        });

        colors=getResources().getStringArray(R.array.bg_colors);
        changeBGcolorFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    if(counterForBackgroundColor==13)
                        counterForBackgroundColor=0;
                    colorString=colors[counterForBackgroundColor];
                    counterForBackgroundColor++;
                    //Toast.makeText(AddTextActivity.this,"counterForColor : "+String.valueOf(counterForColor)+fontString,Toast.LENGTH_SHORT).show();
                    View v=getWindow().getDecorView().getRootView();
                    v.findViewById(R.id.etText).setBackgroundColor(Color.parseColor(colorString));
                    v.findViewById(R.id.status_relative_layout).setBackgroundColor(Color.parseColor(colorString));
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
        });

        fonts=getResources().getStringArray(R.array.my_fonts);
        changeFontFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    if(counterForFont==20)
                        counterForFont=0;
                    fontString=fonts[counterForFont];
                    counterForFont++;
                    //Toast.makeText(AddTextActivity.this,"Font : "+fontString,Toast.LENGTH_SHORT).show();
                    Typeface typeface;
                    typeface=Typeface.createFromAsset(AddTextActivity.this.getAssets(),fontString);
                    statusET.setTypeface(typeface);
                }
                catch(Throwable e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    void uploadStatus()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading status");
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String path=null;
        path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/WhatsStat/";
        File dir=new File(path);
        if(!dir.exists())
            dir.mkdirs();
        File file=null;
        if(LoginActivity.firebaseAuth.getCurrentUser()!=null)
            file=new File(dir,LoginActivity.firebaseAuth.getCurrentUser().getUid()+".jpg");
        else
            file=new File(dir,"temp.jpg");
        try
        {
            View view=getWindow().getDecorView().getRootView().findFocus();
            view.setBackgroundColor(Color.parseColor(colorString));
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap=Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            int quality=100;
            bitmap.compress(Bitmap.CompressFormat.JPEG,quality,fileOutputStream);
            //fileOutputStream.flush();
            //fileOutputStream.close();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        if(file==null)
            Toast.makeText(AddTextActivity.this,"File : "+file,Toast.LENGTH_LONG).show();
        Uri picUri=Uri.fromFile(file);
        if(picUri==null)
            Toast.makeText(AddTextActivity.this,"Image Uri : "+picUri,Toast.LENGTH_LONG).show();
        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("pics").child(LoginActivity.firebaseAuth.getCurrentUser().getUid());
        storageReference.putFile(picUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    startActivity(new Intent(AddTextActivity.this,MainActivity.class));
                    URL=task.getResult().getDownloadUrl().toString();
                    setPicForUser();
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(AddTextActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    void setPicForUser()
    {
        if(LoginActivity.firebaseAuth.getCurrentUser()!=null)
        {
            String userID=LoginActivity.firebaseAuth.getCurrentUser().getUid();
            final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(userID);
            databaseReference.child("Pic").setValue(URL).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        databaseReference.child("PicUploadTime").setValue(ServerValue.TIMESTAMP);
                        Toast.makeText(AddTextActivity.this, "Status uploaded successfully", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(AddTextActivity.this,"Oops! Something went wrong..",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}