package com.example.jogle.attendance;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;


public class JGShowActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.jg_activity_show);

        String picPath = getIntent().getStringExtra("pic_path");
        if (picPath != null) {
            ImageView pic = (ImageView) findViewById(R.id.show_pic);
            Bitmap bmp = JGDataSet.getPicBitMap(picPath);
            pic.setImageBitmap(bmp);
        }

        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return false;
        }
        return false;
    }
}
