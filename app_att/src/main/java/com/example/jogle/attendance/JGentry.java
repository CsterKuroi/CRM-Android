package com.example.jogle.attendance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.jogle.calendar.JGCalendarActivity;

public class JGentry extends Activity {
    private Button b1;
    private Button b2;
    private Button b3;
    private EditText et2;
    private EditText et3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jg_activity_entry);
        b1 = (Button) findViewById(R.id.button);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button) findViewById(R.id.button3);
        et2 = (EditText) findViewById(R.id.editText2);
        et3 = (EditText) findViewById(R.id.editText3);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JGentry.this, JGMainActivity.class);
                try {
                    intent.putExtra("uid", Integer.parseInt(et2.getText().toString()));
                    intent.putExtra("name", et3.getText().toString());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JGentry.this, JGMain2Activity.class);
                try {
                    intent.putExtra("uid", Integer.parseInt(et2.getText().toString()));
                    intent.putExtra("name", et3.getText().toString());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JGentry.this, JGCalendarActivity.class);
                try {
                    intent.putExtra("uid", Integer.parseInt(et2.getText().toString()));
                    intent.putExtra("name", et3.getText().toString());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
