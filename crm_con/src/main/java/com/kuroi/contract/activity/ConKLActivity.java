package com.kuroi.contract.activity;

import android.content.Intent;
import android.os.Bundle;

import com.zhy.tree.bean.KLNode;
import com.zhy.tree_view.KLMainActivity;

import java.util.List;

public class ConKLActivity extends KLMainActivity {

    public String getData() {
        String returnstring = "";
        String returnstring2 = "";
        if(mAdapter!=null) {
            List<KLNode> all = mAdapter.getAllNode();
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).isChecked && all.get(i).getId() > 10000) {
                    int j = i - 1;
                    while (all.get(j).getId() > 10000)
                        j--;
                    returnstring += all.get(j).getName() + " ";
                    returnstring2 += all.get(i).getName() + " ";
                }
            }
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("re", returnstring);
        bundle.putString("re2", returnstring2);
        intent.putExtras(bundle);
        this.setResult(RESULT_OK, intent);
        this.finish();
        return returnstring;
    }
}
