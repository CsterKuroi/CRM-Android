package com.example.dt.testapp3.Graphics;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;

import com.zhy.tree.bean.KLNode;
import com.zhy.tree_view.KLMainActivity;

import java.util.ArrayList;
import java.util.List;

public class VisitContactActivity extends KLMainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_visit_contact);
    }

    @Override
    public String getData() {
        if (mAdapter == null) {
            return "";
        }
        List<KLNode> T = mAdapter.getAllNode();
        StringBuilder data = new StringBuilder();

        int companyId = -1;
        String company = "";
        ArrayList<Integer> targetId = new ArrayList<>();
        ArrayList<String> target = new ArrayList<>();

        for (KLNode mynode : T) {
            if (mynode.isChecked) {
                if (mynode.getId() < 10000) {
                    companyId = mynode.getId();
                    company = mynode.getName();
                } else {
                    targetId.add(mynode.getId() - 10000);
                    target.add(mynode.getName());
                }
            }
        }
        Intent intent = new Intent();
        intent.putExtra("companyId", companyId);
        intent.putExtra("company", company);
        int[] tmpId = new int[targetId.size()];
        for (int i = 0; i < targetId.size(); i++) {
            tmpId[i] = targetId.get(i);
        }
        intent.putExtra("targetId", tmpId);
        intent.putExtra("target", target.toArray(new String[0]));
        setResult(1, intent);
        Log.e("Send contact:", data.toString());
        this.finish();
        return data.toString();
    }
}
