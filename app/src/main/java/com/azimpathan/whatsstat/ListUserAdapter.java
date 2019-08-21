package com.azimpathan.whatsstat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AZIM_2 on 18/12/2017.
 */

/*
    A class defining BaseAdapter/Custom Adapter for containing users from users' arraylist
        AND
    setting custom view accordingly
*/
public class ListUserAdapter extends BaseAdapter {

    private Context context;
    private List<User> userList;

    ListUserAdapter(){}

    ListUserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int i) {
        return userList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //Toast.makeText(context,"inside getView() method..",Toast.LENGTH_SHORT).show();
        View myView=View.inflate(context,R.layout.list_item_view,null);
        TextView nameText=myView.findViewById(R.id.tvUserName);
        TextView uploadTimeText=myView.findViewById(R.id.tvUploadTime);
        ImageView picImage=myView.findViewById(R.id.imageView);
        nameText.setText(userList.get(i).getName());
        uploadTimeText.setText(userList.get(i).getUploadTime());
        //Toast.makeText(context,"Name : "+userList.get(i).getName(),Toast.LENGTH_SHORT).show();
        Picasso.with(context).load(userList.get(i).getPic()).into(picImage);
        return myView;
    }
}