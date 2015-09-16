package com.example.spinel.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class bpmLoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bpm_activity_login);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bpm_menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.login_ok) {
            TextView tv = (TextView) findViewById(R.id.editText_login);
            if (!tv.getText().toString().isEmpty()) {

                Intent intent = new Intent(bpmLoginActivity.this, bpmMainActivity.class);
                intent.putExtra("userId", tv.getText().toString());
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
