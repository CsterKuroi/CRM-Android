package com.melnykov.fab.sample.kehu;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.melnykov.fab.sample.R;
import com.melnykov.fab.sample.lianxiren.crm_detail_lianxiren;
import com.melnykov.fab.sample.lianxiren.crm_lianxiren;
import com.melnykov.fab.sample.tools.IMApplication;
import com.melnykov.fab.sample.tools.crmMyDatabaseHelper;
import com.melnykov.fab.sample.tools.crmUrlConstant;

import net.tsz.afinal.FinalBitmap;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by renxin on 2015/8/28.
 */
public class ScrollViewFragment extends Fragment {
    ArrayList<String> countries;
    crmMyDatabaseHelper dbHelper;
    String stre;
    String User_id;
    LinearLayout list;

    public ScrollViewFragment(String str)
    {
        stre=str;
    }
    public ScrollViewFragment( )
    {

    }

    public ArrayList<String>
    converCursorToList(Cursor cursor)
    {
        ArrayList<String> result =
                new ArrayList<String>();
        while (cursor.moveToNext())
        {
            if(User_id.equals(cursor.getString(10))&&stre.equals(cursor.getString(11))){
                String pic;
                String strpi =  User_id+"-1-";
                if(cursor.getString(14).equals("")||cursor.getString(14).equals("haha.jpg"))
                    pic = "mnt/sdcard/mingpian/haha.jpg";
                else if(cursor.getString(14).equals(strpi))
                    pic = cursor.getString(14);
                else
                    pic = cursor.getString(14);

                result.add(cursor.getString(1)+";"+cursor.getString(15)+";"+pic+";"+cursor.getString(3)+";"+cursor.getString(11)+";");
            }
        }

        return result;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.crm_activity_addfriends, container, false);
        User_id = IMApplication.getUserid(getActivity());
        RelativeLayout layout1= (RelativeLayout) root.findViewById(R.id.title);
        LinearLayout  layout2= (LinearLayout) root.findViewById(R.id.lay2);
        LinearLayout  layout3= (LinearLayout) root.findViewById(R.id.lay3);
        layout1.setVisibility(View.GONE);
        layout2.setVisibility(View.GONE);
        layout3.setVisibility(View.GONE);
        list  = (LinearLayout)root.findViewById(R.id.linelayout);
        update();
        return root;
    }


    public void update(){
        dbHelper = new crmMyDatabaseHelper(getActivity(), "customer.db3", 1);
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from lianxiren where username  is not null", null);
        countries = converCursorToList(cursor);
        list.removeAllViews();

        for (String country : countries) {

            LinearLayout layout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.crm_lianxiren_item, null);
            TextView text1 = (TextView) layout.findViewById(R.id.text1);
            TextView text2 = (TextView) layout.findViewById(R.id.text2);
//            ImageView img = (ImageView) layout.findViewById(R.id.image);
            Button lianxirenphone = (Button) layout.findViewById(R.id.lianxirenphone);
            final String[] values = country.split(";");
            text1.setText(values[0].trim());

            if(values.length>4) {
                if (values[4].trim() != null)
                    text2.setText(values[4].trim());
            }
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("extra", values[1]);
                    intent.putExtra("name", values[0]);
                    intent.putExtra("source", "lianxiren");
                    intent.setClass(getActivity(), crm_detail_lianxiren.class);
                    startActivityForResult(intent, 0);
                }
            });

            lianxirenphone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    Uri data = Uri.parse("tel:" + values[3]);
                    intent.setData(data);
                    startActivity(intent);
                }
            });
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            String path = new String(values[2]);
            if(path.contains("haha.jpg"))
            {
                int flagResId = getResources().getIdentifier("hi", "drawable", getActivity().getPackageName());
//                img.setImageResource(flagResId);
            }
            else if(path.contains( User_id+"-1-"))
            {
                Bitmap b = BitmapFactory.decodeFile(path, options);
//                img.setImageBitmap(b);
            }
            else {
//                Bitmap b = BitmapFactory.decodeFile(path, options);
//                img.setImageBitmap(b);
//                FinalBitmap fitmap = FinalBitmap.create(crm_lianxiren.this);
////                fitmap.configBitmapLoadThreadSize(3);
//                fitmap.configLoadingImage(R.drawable.load);
//                fitmap.display(img,crmUrlConstant.download_url+path);
            }
//            img.setLayoutParams(new AbsoluteLayout.LayoutParams(200, 200, 0, 0));
            list.addView(layout);

        }

        LinearLayout itemview = new LinearLayout(getActivity());
        itemview.setOrientation(LinearLayout.HORIZONTAL);
        itemview.setGravity(LinearLayout.SHOW_DIVIDER_MIDDLE | LinearLayout.HORIZONTAL | Gravity.CENTER_HORIZONTAL);
        TextView textpad = new TextView(getActivity());
        if(countries.size()==0) {
            textpad.setText("\n                                  该客户尚未添加联系人！  \n  ");
            textpad.setGravity(Gravity.CENTER);
        }
            else {
            textpad.setText("     \n\n  ");
        }itemview.addView(textpad);
        list.addView(itemview);
    }

}