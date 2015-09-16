package com.geminy.productshow.Activity;

import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.geminy.productshow.DAO.GEMINY_ProductDAO;
import com.geminy.productshow.GEMINY_ClearEditText;
import com.geminy.productshow.GEMINY_mAdapter;
import com.geminy.productshow.Model.GEMINY_Tb_product;
import com.geminy.productshow.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hatsune Miku on 2015/8/21.
 */
public class GEMINY_SearchActivity extends ActionBarActivity{
    ListView mListView;
    TextView mTextView;
    GEMINY_ClearEditText mClearEditText;

    public static final String PRO_NAME="pro_name";
    public static final String UID="UID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geminy_activity_search);

        initActionBar();
        mClearEditText=(GEMINY_ClearEditText)findViewById(R.id.clearEditText);
        mListView=(ListView)findViewById(R.id.searchResult);
        mTextView=(TextView)findViewById(R.id.search_text);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchWord=mClearEditText.getText().toString();
                if(searchWord.length()==0){
                    Toast.makeText(GEMINY_SearchActivity.this,"产品名称不能为空！",Toast.LENGTH_SHORT).show();
                    return;
                }
                List<GEMINY_Tb_product> searchResult= new ArrayList<GEMINY_Tb_product>();
                GEMINY_ProductDAO productDAO=new GEMINY_ProductDAO(GEMINY_SearchActivity.this);
                searchResult=productDAO.selectByNameLike(searchWord);

                if(searchResult==null||searchResult.size()==0){

                    Toast.makeText(GEMINY_SearchActivity.this,"产品库中没有名称包含\""+searchWord+"\"字段的产品！请重新输入！",Toast.LENGTH_SHORT).show();
                    return;
                }
                List<String> names=new ArrayList<String>();
                List<String> types=new ArrayList<String>();
                List<String> imgIds=new ArrayList<String>();
                List<String> nums=new ArrayList<String>();
                for(GEMINY_Tb_product tb_product:searchResult){
                    names.add(tb_product.getName());
                    types.add(tb_product.getType());
                    imgIds.add(tb_product.getPicture());
                    nums.add("库存"+tb_product.getNum()+"个");
                }
                GEMINY_mAdapter adapter=new GEMINY_mAdapter(GEMINY_SearchActivity.this,names,types,imgIds,nums);
                mListView.setAdapter(adapter);
                Toast.makeText(GEMINY_SearchActivity.this,"已找到"+searchResult.size()+"个含有该字段的产品",Toast.LENGTH_SHORT).show();
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView tv = (TextView) view.findViewById(R.id.list_item_name);
                        String pro_name = tv.getText().toString();
                        String uid = getIntent().getExtras().getString(GEMINY_MainActivity.UID);
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(GEMINY_SearchActivity.this, GEMINY_ProductDetailInfosActivity.class);
                        bundle.putString(PRO_NAME, pro_name);
                        bundle.putString(UID, uid);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }
        });


    }
    private void initActionBar(){
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mTitleView=mInflater.inflate(R.layout.geminy_custom_actionbar,null);

        getSupportActionBar().setCustomView(mTitleView,new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,ActionBar.LayoutParams.WRAP_CONTENT));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;

        }
        return true;
    }



}
