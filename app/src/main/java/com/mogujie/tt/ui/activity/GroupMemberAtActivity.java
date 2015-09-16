
package com.mogujie.tt.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.mogujie.tt.R;
import com.mogujie.tt.config.IntentConstant;

public class GroupMemberAtActivity extends FragmentActivity {

    public int groupId;
    public Intent i;
    public String atmem;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        groupId = getIntent().getIntExtra(IntentConstant.KEY_PEERID, 0);
        setContentView(R.layout.tt_activity_group_member_at);
        //Intent i = new Intent();
       // i.putExtra("atmem", atmem);
        //setResult(RESULT_OK, i);
        //finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK != resultCode)
            return;
    }
}
