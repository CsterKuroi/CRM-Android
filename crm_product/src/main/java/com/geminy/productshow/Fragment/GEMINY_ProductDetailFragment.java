package com.geminy.productshow.Fragment;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.geminy.productshow.Activity.GEMINY_ProductDetailInfosActivity;
import com.geminy.productshow.DAO.GEMINY_ProductDAO;
import com.geminy.productshow.GEMINY_ImgScroll;
import com.geminy.productshow.Model.GEMINY_Tb_product;
import com.geminy.productshow.R;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hatsune Miku on 2015/8/6.
 */
public class GEMINY_ProductDetailFragment extends Fragment{
    private GEMINY_ImgScroll mGEMINYImgScroll;
    private List<View> listViews;
    private TextView detailSummary;
    private static final int ScrollTime=3000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.geminy_fragment_selection_launch,container,false);
        mGEMINYImgScroll =(GEMINY_ImgScroll)rootView.findViewById(R.id.myImgScroll);
        LinearLayout ovalLayout=(LinearLayout)rootView.findViewById(R.id.vb);
        detailSummary=(TextView)rootView.findViewById(R.id.pro_summary);
        InitViewPager();
        mGEMINYImgScroll.start(getActivity(), listViews, ScrollTime, ovalLayout, R.layout.geminy_img_bottom_dot_line, R.id.dotLine, R.drawable.geminy_dot_focused, R.drawable.geminy_dot_normal);
        ScrollView scrollView=(ScrollView)rootView.findViewById(R.id.scroll);
        scrollView.smoothScrollTo(0, 20);
        /*Button button=(Button) rootView.findViewById(R.id.test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog=new AlertDialog.Builder(getActivity()).setTitle("产品参数").setMessage("this_is_message").create();
                Window window=dialog.getWindow();
                window.setGravity(Gravity.BOTTOM);
                window.setWindowAnimations(R.style.geminy_anistyle);
                dialog.show();
            }
        });*/
        return rootView;
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void InitViewPager(){
        listViews=new ArrayList<View>();
        GEMINY_ProductDAO GEMINYProductDAO =new GEMINY_ProductDAO(getActivity());
        Bundle bundle=getArguments();
        String pro_name=bundle.getString(GEMINY_ProductDetailInfosActivity.PRODUCT_NAME, "");
        GEMINY_Tb_product tb_product= GEMINYProductDAO.getByName(pro_name);
        String imgs=tb_product.getPicture();
        String pro_summary=tb_product.getDetail();
        detailSummary.setText(pro_summary);
        String[] imgLoc;
        try{imgLoc=imgs.split(";");}
        catch (Exception e){
            Toast.makeText(getActivity(),"数据库图片路径缺少';'！",Toast.LENGTH_LONG).show();
            return;
        }
        if(imgs.equals(";")){
            Toast.makeText(getActivity(),"无图片",Toast.LENGTH_LONG).show();
            return;
        }
        for (int i=1;i<imgLoc.length;i++){
            ImageView imageView=new ImageView(getActivity());

            File file=new File(imgLoc[i]);
            if(file.exists()) {
                Bitmap bm = BitmapFactory.decodeFile(imgLoc[i]);
                imageView.setImageBitmap(bm);
            }

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            listViews.add(imageView);
        }

    }


    @Override
    public void onResume() {
        mGEMINYImgScroll.startTimer();
        super.onResume();
    }

    @Override
    public void onStop() {
        mGEMINYImgScroll.stopTimer();
        super.onStop();
    }
    public void stop(View v){
        mGEMINYImgScroll.stopTimer();
    }
}
