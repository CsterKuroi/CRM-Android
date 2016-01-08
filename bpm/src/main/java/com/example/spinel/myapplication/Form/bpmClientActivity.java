package com.example.spinel.myapplication.Form;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.example.spinel.myapplication.bpmMainActivity;
import com.example.spinel.myapplication.bpmStructure;
import com.zhy.tree.bean.KLNode;
import com.zhy.tree_view.KLMainActivity;

import java.util.List;

/**
 * Created by Spinel on 2015/8/27.
 */
public class bpmClientActivity extends KLMainActivity {
    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
        //初始化actionbar
//        final ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(false);
//        View customView = getLayoutInflater().inflate(R.layout.bpm_title, null);
//        actionBar.setCustomView(customView);
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(false);
//        ((TextView)findViewById(R.id.title)).setText("选择客户联系人");
//        ((Button)findViewById(R.id.button_ok)).setText("确定");
//        findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getData();
//            }
//        });
//        findViewById(R.id.imageButton_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setResult(RESULT_CANCELED);
//                finish();
//            }
//        });
//    }

    public String getData() {
        //client value 格式： 公司id_公司名称 address 地址 contact 联系人id_联系人 telephone 电话 contact 联系人1id_联系人2 telephone 电话2
        //"34_云尚公司 address 北航 contact 55_laigus telephone 123456789 contact 48_spinel telephone 987654321";

        String data="";

        if(mAdapter!=null) {
            List<KLNode> all = mAdapter.getAllNode();


            //获得公司
            //公司id_公司名称
            String clientId="";
            for(KLNode node: all){
                if(node.isChecked && node.getId()<10000){
                    clientId += node.getId();
                    data += (node.getId()+"_"+node.getName());
                    break;
                }
            }

            // address 地址
            data += " address";
            if(clientId.isEmpty())
                data += " -";
            else {
                boolean flag = false;
                if(bpmMainActivity.structure!=null && bpmMainActivity.structure.clientList!=null){
                    List<bpmStructure.Client> list = bpmMainActivity.structure.clientList;
                    if(!list.isEmpty()){
                        for(bpmStructure.Client client: list)
                            if(client.id.equals(clientId)){
                                data += (" " + client.address);
                                flag = true;
                                break;
                            }
                    }
                }
                if(!flag)
                    data += " -";
            }

            //获得联系人  contact 联系人id_联系人 telephone 电话
            for(KLNode node: all){
                if(node.isChecked && node.getId()>10000){
                    data += " contact";
                    String id = ((Integer)(node.getId()-10000)).toString();
                    data += " "+(id+"_"+node.getName());

                    data += " telephone";
                    boolean flag = false;
                    if(bpmMainActivity.structure!=null && bpmMainActivity.structure.contactList!=null){
                        List<bpmStructure.Contact> list = bpmMainActivity.structure.contactList;
                        if(!list.isEmpty()){
                            for(bpmStructure.Contact contact: list)
                                if(contact.id.equals(id)){
                                    data += (" " + contact.phone);
                                    flag = true;
                                    break;
                                }
                        }
                    }
                    if(!flag)
                        data += " -";
                }
            }


        }
        Intent intent = new Intent();
        intent.putExtra("data", data);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);

        this.setResult(RESULT_OK, intent);
        this.finish();
        return data;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
