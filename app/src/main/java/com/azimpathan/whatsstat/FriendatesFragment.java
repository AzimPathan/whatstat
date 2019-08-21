package com.azimpathan.whatsstat;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendatesFragment extends Fragment {

    //private static final String fragmentName="Friendates";
    private CircleImageView mypicImage;
    private List<User> userList;
    public String userName,userPic,userThumb,userUploadTime;    //Info about each user
    public static StorageReference storageReference;    //Storage reference for getting the images
    public static String picName,uName=null;    //Display pic and name
    private ListUserAdapter userAdapter;
    private FloatingActionButton imageFab,textFab;
    private TextView nameText,statusText;
    private SwipeRefreshLayout refreshLayout;
    private static final int GALLERY_OK=1;  //Constant for image picking
    private ArrayList<String> keyList;  //An arraylist for holding keys (IDs) of each user used for further UI changes
    private ProgressDialog progressDialog;

    public FriendatesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_friendates, container, false);

        keyList=new ArrayList<>();

        //Initialize UI elements
        ListView updatesListView = view.findViewById(R.id.listViewUpdates);
        mypicImage=view.findViewById(R.id.ivMyPic);
        imageFab=view.findViewById(R.id.fabImage);
        textFab=view.findViewById(R.id.fabText);
        nameText=view.findViewById(R.id.tvAddStatus);
        statusText=view.findViewById(R.id.tvMyStatus);
        refreshLayout=view.findViewById(R.id.swipeRefreshLayout);

        storageReference = FirebaseStorage.getInstance().getReference();

        textFab.setBackgroundColor(Color.GREEN);

        //Initialize other components
        userList=new ArrayList<>();
        userAdapter = new ListUserAdapter(getActivity(), userList);
        updatesListView.setAdapter(userAdapter);    //setting adapter to listview
        if(LoginActivity.UID==null)
        {
            startActivity(new Intent(getActivity(),LoginActivity.class));
        }
        //Toast.makeText(MainActivity.this,"UID : "+LoginActivity.UID,Toast.LENGTH_LONG).show();

        try {
            DatabaseReference mydatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(LoginActivity.UID);
            DatabaseReference statusReference = FirebaseDatabase.getInstance().getReference().child("users").child(LoginActivity.UID).child("Friends");

            mydatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try
                    {
                        /*String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/WhatStat/";
                        if(LoginActivity.firebaseAuth.getCurrentUser()!=null)
                        {
                            Toast.makeText(MainActivity.this,"User not null..",Toast.LENGTH_SHORT).show();
                            path=path.concat(LoginActivity.firebaseAuth.getCurrentUser().getUid()+".jpg");
                            Toast.makeText(MainActivity.this,"Path : "+path,Toast.LENGTH_LONG).show();
                            //File file=new File(path);
                            //Uri uri=Uri.fromFile(file);
                            //mypicImage.setImageURI(uri);
                            Picasso.with(MainActivity.this).load(path).into(mypicImage);
                            //if(file.exists())
                            //{
                                //Bitmap bitmap= BitmapFactory.decodeFile(file.getAbsolutePath());
                                //Toast.makeText(MainActivity.this,"bitmap : "+bitmap.toString(),Toast.LENGTH_LONG).show();
                               // mypicImage.setImageBitmap(bitmap);
                            //}
                        }
                        else
                        {*/
                        String name=dataSnapshot.child("Name").getValue(String.class);
                        statusText.setVisibility(TextView.VISIBLE);
                        nameText.setVisibility(TextView.VISIBLE);
                        nameText.setText(name);
                        String pic = dataSnapshot.child("Pic").getValue(String.class);
                        //userPic=pic;
                        Picasso.with(getActivity()).load(pic).placeholder(R.mipmap.ic_account_circle_black_24dp).into(mypicImage);
                        //}
                    }
                    catch (Throwable e)
                    {
                        e.printStackTrace();
                        Toast.makeText(getActivity(),"Exception : !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+e.toString(),Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            statusReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    final String ID = dataSnapshot.getKey();
                    keyList.add(ID);
                    final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users").child(ID);
                    userReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.child("Name").getValue(String.class);
                            userName = name;

                            String pic = dataSnapshot.child("Pic").getValue(String.class);
                            userPic = pic;

                            String uploadTime=dataSnapshot.child("PicUploadTime").getValue().toString();
                            long lastTime=Long.parseLong(uploadTime);
                            userUploadTime=Time.getTimeAgo(lastTime);

                            int index=keyList.indexOf(ID);
                            User user = new User(userName, userPic, userThumb,userUploadTime);
                            if(index==userList.size())
                                userList.add(user);
                            else
                                userList.set(index,user);
                            userAdapter.notifyDataSetChanged();


                            //DatabaseReference picReference=userReference.child("Pic");
                            /*picReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final int index=keyList.indexOf(ID);
                                    //Toast.makeText(MainActivity.this,"onDataChange() called for Pics",Toast.LENGTH_SHORT).show();


                                    DatabaseReference timeReference=userReference.child("PicUploadTime");
                                    timeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            User user = new User(userName, userPic, userThumb,userUploadTime);
                                            //if(index==userList.size())
                                                userList.add(user);
                                            //else
                                                //userList.set(index,user);
                                            userAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });*/
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    userAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String key=dataSnapshot.getKey();
                    int index=keyList.indexOf(key);
                    userList.remove(index);
                    keyList.remove(key);
                    userAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e)
        {
            //Toast.makeText(MainActivity.this,"Exception : "+e.toString(),Toast.LENGTH_LONG).show();
            Log.v("EXCEPTION : !!!!!!!",e.toString());
        }
        userAdapter.notifyDataSetChanged();

        imageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent picIntent=new Intent();
                picIntent.setType("image/*");
                picIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(picIntent,GALLERY_OK);
            }
        });
        textFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),AddTextActivity.class));
            }
        });

        //-----------Refreshing the Screen------------//
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                userAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==GALLERY_OK && resultCode==getActivity().RESULT_OK)
        {
            Uri imageUri=data.getData();
            CropImage.activity(imageUri).setAspectRatio(1,1).start(getActivity());
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == getActivity().RESULT_OK)
            {
                progressDialog=new ProgressDialog(getActivity());
                progressDialog.setTitle("Uploading image");
                progressDialog.setMessage("Please wait..");
                progressDialog.setCancelable(false);
                progressDialog.show();
                StorageReference filepath=null;
                Uri resultUri = result.getUri();
                if(LoginActivity.UID!=null) {
                    picName = LoginActivity.UID + ".jpg";
                    filepath=storageReference.child("pics").child(picName);
                }
                if(LoginActivity.firebaseAuth.getCurrentUser()!=null)
                {
                    uName=LoginActivity.firebaseAuth.getCurrentUser().getUid();
                    filepath=storageReference.child("pics").child(uName);
                }
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            String imgPath=task.getResult().getDownloadUrl().toString();
                            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(uName);
                            databaseReference.child("Pic").setValue(imgPath);
                            databaseReference.child("PicUploadTime").setValue(ServerValue.TIMESTAMP);
                            Toast.makeText(getActivity(),"Image uploaded successfully",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),"Error while uploading image!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
        {
            //Exception error = result.getError();
            //Log.v("EXCEPTION : !!!!!!!!!!!",error.toString());
        }
    }
}
