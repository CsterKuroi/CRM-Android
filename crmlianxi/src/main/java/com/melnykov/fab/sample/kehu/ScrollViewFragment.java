package com.melnykov.fab.sample.kehu;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
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
                if(cursor.getString(14).equals("")||cursor.getString(14).equals("haha.jpg"))
                    pic = "mnt/sdcard/mingpian/haha.jpg";
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
        layout1.setVisibility(View.GONE);
        layout2.setVisibility(View.GONE);
        list  = (LinearLayout)root.findViewById(R.id.linelayout);
        update();
        return root;
    }


    public void update(){
        dbHelper = new crmMyDatabaseHelper(getActivity(), "customer.db3", 1);
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from lianxiren where username  is not null", null);
        countries = converCursorToList(cursor);
        list.removeAllViews();
        int i =2 ;
        for (String country : countries) {
            i++;
            final String[] aa = country.split(";");
            LinearLayout itemview = new LinearLayout(getActivity());
            itemview.setOrientation(LinearLayout.HORIZONTAL);
            itemview.setGravity(LinearLayout.SHOW_DIVIDER_MIDDLE | LinearLayout.HORIZONTAL | Gravity.CENTER_HORIZONTAL);
            ImageView img = new ImageView(getActivity());
            final String countryName = country;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            String path = new String(aa[2]);
            if(path.contains("haha.jpg"))
            {
                int flagResId = getResources().getIdentifier("hi", "drawable", getActivity().getPackageName());
                img.setImageResource(flagResId);
            }
            else {
            /*    Bitmap b = BitmapFactory.decodeFile(path, options);
                img.setImageBitmap(b);*/

                FinalBitmap fitmap = FinalBitmap.create(getActivity());
            /*    fitmap.configBitmapLoadThreadSize(3);*/
                fitmap.configLoadingImage(R.drawable.load);
                fitmap.display(img, crmUrlConstant.download_url+path);
            }

            img.setLayoutParams(new AbsoluteLayout.LayoutParams(200, 200, 0, 0));

            TextView textpad = new TextView(getActivity());
            textpad.setText("       ");
            if(i%2==0)
            {
                itemview.setBackgroundColor(getResources().getColor(R.color.lightgray));
                textpad.setTextColor(getResources().getColor(R.color.lightgray));
            }
            else
            {  itemview.setBackgroundColor(getResources().getColor(R.color.white));
                textpad.setTextColor(getResources().getColor(R.color.white));
            }
            TextView text = new TextView(getActivity());
            text.setText("\n       名称：       " + aa[0]+"\n\n       电话：       "+aa[3]);
            text.setTextColor(getResources().getColor(R.color.black));
            text.setTextSize(14);
            itemview.addView(textpad);
            itemview.addView(img);
            itemview.addView(text);
            itemview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("extra", aa[1]);
                    intent.putExtra("name", aa[0]);
                    intent.putExtra("source", "lianxiren");
                    intent.setClass(getActivity(), crm_detail_lianxiren.class);
                    startActivityForResult(intent,0);
                    //bug
                    getActivity().finish();
                }
            });
            list.addView(itemview);
        }
    }

}