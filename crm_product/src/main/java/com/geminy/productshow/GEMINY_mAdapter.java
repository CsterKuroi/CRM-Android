package com.geminy.productshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Hatsune Miku on 2015/8/9.
 */
public class GEMINY_mAdapter extends BaseAdapter {
    private View[] itemViews;
    private Context context;
    public GEMINY_mAdapter(Context context, List<String> itemNames, List<String> itemTypes, List<String> itemImgIds,List<String> itemNums){
        this.context=context;
        itemViews = new View[itemNames.size()];
        for (int i=0;i<itemViews.length;i++){
            itemViews[i]=makeItemView(itemNames.get(i),itemTypes.get(i),itemImgIds.get(i).substring(itemImgIds.get(i).equals(";")?0:1, itemImgIds.get(i).equals(";")?itemImgIds.get(i).indexOf(";"):itemImgIds.get(i).indexOf(";", 1)),itemNums.get(i));

        }
    }

    private View makeItemView(String strName,String strType,String imgId,String strNum){
        LayoutInflater inflater=LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.geminy_list_item_1,null);
        TextView num=(TextView)itemView.findViewById(R.id.list_item_num);

        TextView name=(TextView)itemView.findViewById(R.id.list_item_name);

        TextView type=(TextView)itemView.findViewById(R.id.list_item_type);

        ImageView img=(ImageView)itemView.findViewById(R.id.list_item_pic);

        num.setText(strNum);
        name.setText(strName);
        type.setText(strType);

        File file=new File(imgId);
        if(file.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(imgId);
            img.setImageBitmap(bm);
        }

        return itemView;
    }

    @Override
    public int getCount() {
        return itemViews.length;
    }

    @Override
    public View getItem(int position) {
        return itemViews[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder=null;
        if(convertView==null){
            viewHolder=new ViewHolder();
            LayoutInflater inflater=LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.geminy_list_item_1, null);
            viewHolder.num=(TextView)convertView.findViewById(R.id.list_item_num);

            viewHolder.name=(TextView)convertView.findViewById(R.id.list_item_name);

            viewHolder.type=(TextView)convertView.findViewById(R.id.list_item_type);

            viewHolder.img=(ImageView)convertView.findViewById(R.id.list_item_pic);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        View itemView=getItem(position);
        if(itemView!=null){
            viewHolder.num.setText(((TextView)itemView.findViewById(R.id.list_item_num)).getText());
            viewHolder.name.setText(((TextView)itemView.findViewById(R.id.list_item_name)).getText());
            viewHolder.type.setText(((TextView)itemView.findViewById(R.id.list_item_type)).getText());
            viewHolder.img.setImageDrawable(((ImageView)itemView.findViewById(R.id.list_item_pic)).getDrawable());
        }

        return convertView;
    }
    private static class ViewHolder{
        TextView num;
        TextView name;
        TextView type;
        ImageView img;
    }
}