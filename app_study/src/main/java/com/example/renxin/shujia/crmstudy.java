package com.example.renxin.shujia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.renxin.shujia.karl.reader.BookShelfActivity;
import com.example.renxin.shujia.metro.MyImageView;

public class crmstudy extends AppCompatActivity {
    MyImageView zhengce;
    MyImageView tongzhi;
    MyImageView guize;
    MyImageView anli;
    MyImageView peixun;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crm_study);

        back = (ImageView) findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crmstudy.this.finish();
            }
        });


        zhengce = (MyImageView) findViewById(R.id.zhengce);
        zhengce.setOnClickIntent(new MyImageView.OnViewClickListener()
        {

            @Override
            public void onViewClick(MyImageView view)
            {
                Intent intent = new Intent(crmstudy.this, BookShelfActivity.class);
                intent.putExtra("module", "政策");
                startActivityForResult(intent, 0);

            }
        });


        tongzhi = (MyImageView) findViewById(R.id.tongzhi);
        tongzhi.setOnClickIntent(new MyImageView.OnViewClickListener()
        {

            @Override
            public void onViewClick(MyImageView view)
            {
                Intent  intent = new Intent(crmstudy.this, BookShelfActivity.class);
                intent.putExtra("module","通知");
                startActivityForResult(intent,0);

            }
        });

        guize = (MyImageView) findViewById(R.id.guize);
        guize.setOnClickIntent(new MyImageView.OnViewClickListener()
        {

            @Override
            public void onViewClick(MyImageView view)
            {
                Intent  intent = new Intent(crmstudy.this, BookShelfActivity.class);
                intent.putExtra("module","规则");
                startActivityForResult(intent,0);

            }
        });

        anli = (MyImageView) findViewById(R.id.anli);
        anli.setOnClickIntent(new MyImageView.OnViewClickListener()
        {

            @Override
            public void onViewClick(MyImageView view)
            {
                Intent  intent = new Intent(crmstudy.this, BookShelfActivity.class);
                intent.putExtra("module","案例");
                startActivityForResult(intent,0);

            }
        });

        peixun = (MyImageView) findViewById(R.id.peixun);
        peixun.setOnClickIntent(new MyImageView.OnViewClickListener()
        {

            @Override
            public void onViewClick(MyImageView view)
            {
                Intent  intent = new Intent(crmstudy.this, BookShelfActivity.class);
                intent.putExtra("module","培训");
                startActivityForResult(intent,0);

            }
        });


    }
}
