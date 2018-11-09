package com.velocityappsdj.gallerycleaner;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.Resource;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by dhiraj on 6/3/2017.
 */

public class CustomAdapter extends BaseAdapter {
    ArrayList<DataModel> undlist=new ArrayList<DataModel>();
    Context context;
    CustomAdapter(ArrayList<DataModel> ulist,Context c){
        undlist=ulist;
        context=c;




    }
    @Override
    public int getCount() {
        return undlist.size();
    }

    @Override
    public Object getItem(int i) {
        return undlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View row=inflater.inflate(R.layout.undo_item,viewGroup,false);
        TextView name= (TextView) row.findViewById(R.id.undo_item_name);
        ImageView imageView= (ImageView) row.findViewById(R.id.thumbnail);

        DataModel single_row=undlist.get(i);
        name.setText(single_row.name);
        Picasso.with(context).load(new File(single_row.path)).resize(384,384).into(imageView);
      //  imageView.setImageBitmap(single_row.thumb);


        return row;
    }
}

class DataModel{
    String name;
    String path;
    Bitmap thumb;
    public DataModel(String name,String path,Bitmap thumb){
        this.name=name;
        this.path=path;
        this.thumb=thumb;

    }

}
