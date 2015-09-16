package com.code.bmj.groupnotifycation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.bmj.bean.CommonContactBean;
import com.bmj.tree.bean.CommonContactNode;
import com.bmj.tree_view.CommonContactActivity;

import java.util.List;

/**
 * Created by SpongeBob_PC on 2015/8/25.
 */
public class GetFanweiActivity extends CommonContactActivity {

    private String getchoosename="";
    private String getchooseid="";
    @Override
    public String getData() {
       List<CommonContactNode> list =  mAdapter.getAllNode();
        String st = "";
        String num= "";
        for(CommonContactNode ccb:list)
        {
           if(ccb.isChecked&&ccb.getId()>10000) {
               st =st+ ccb.getName() + ",";
               num = num + (ccb.getId()-10000)+ ",";
           }
        }
        getchooseid = num;
        getchoosename =st;
        if(!st.equals(""))
        {
            getchooseid = getchooseid.substring(0,getchooseid.length()-1);
            getchoosename =getchoosename.substring(0,getchoosename.length()-1);
            Intent intent=new Intent();
            intent.putExtra("choose_id", getchooseid.toString());
            intent.putExtra("choose_name", getchoosename.toString());
            setResult(RESULT_OK, intent);
            finish();

        }
        else
            Toast.makeText(getApplicationContext(), "请选择通知范围！", Toast.LENGTH_SHORT).show();

        return st;
    }

}
