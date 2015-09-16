package com.example.jogle.attendance;

import com.zhy.tree.bean.KLNode;
import com.zhy.tree_view.KLMainActivity;

import java.util.List;


public class JGCustomerPickerActivity extends KLMainActivity {
    @Override
    public String getData() {

        String returnstring = "";
        if(mAdapter!=null) {
            List<KLNode> all = mAdapter.getAllNode();
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).isChecked && all.get(i).getId() > 10000) {
                    int j = i - 1;
                    while (all.get(j).getId() > 10000)
                        j--;
                    returnstring += all.get(j).getName() + ":" + all.get(i).getName() + ",";
                }
            }
            JGMainActivity.dataSet.setCustomers(returnstring);
        }
        finish();
        return returnstring;
    }
}
