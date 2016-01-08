package com.example.renxin.shujia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.renxin.shujia.karl.reader.BookShelfActivity;

public class crmstudy extends AppCompatActivity {
    RelativeLayout zhengce;
    RelativeLayout tongzhi;
    RelativeLayout guize;
    RelativeLayout anli;
    RelativeLayout peixun;
    RelativeLayout back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crm_study);

        back = (RelativeLayout) findViewById(R.id.ImageButton_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crmstudy.this.finish();
            }
        });


        zhengce = (RelativeLayout) findViewById(R.id.button1);
        zhengce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(crmstudy.this, BookShelfActivity.class);
                intent.putExtra("module", "政策");
                startActivityForResult(intent, 0);
            }
        });

            tongzhi = (RelativeLayout) findViewById(R.id.button2);
            tongzhi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent  intent = new Intent(crmstudy.this, BookShelfActivity.class);
                    intent.putExtra("module","通知");
                    startActivityForResult(intent,0);
                }
            });

            guize = (RelativeLayout) findViewById(R.id.button3);
            guize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent  intent = new Intent(crmstudy.this, BookShelfActivity.class);
                    intent.putExtra("module","规则");
                    startActivityForResult(intent,0);
                }
            });
            anli = (RelativeLayout) findViewById(R.id.button4);
            anli.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent  intent = new Intent(crmstudy.this, BookShelfActivity.class);
                    intent.putExtra("module","案例");
                    startActivityForResult(intent,0);
                }
            });

            peixun = (RelativeLayout) findViewById(R.id.button5);
            peixun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent  intent = new Intent(crmstudy.this, BookShelfActivity.class);
                    intent.putExtra("module","培训");
                    startActivityForResult(intent,0);
                }
            });

    }
}
