package com.geminy.productshow.Activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.geminy.productshow.DAO.GEMINY_ProductDAO;
import com.geminy.productshow.Model.GEMINY_Tb_product;
import com.geminy.productshow.R;

/**
 * Created by Hatsune Miku on 2015/8/23.
 */
public class GEMINY_StockRemarkEditActivity extends ActionBarActivity{
    private EditText mEditText;
    private GEMINY_ProductDAO productDAO;
    private GEMINY_Tb_product tb_product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geminy_activity_stock_remark_edit);
        mEditText=(EditText)findViewById(R.id.stock_remark_edit);
        productDAO=new GEMINY_ProductDAO(this);
        String pro_name=getIntent().getExtras().getString(GEMINY_StockControlActivity.PRO_NAME);
        tb_product=productDAO.getByName(pro_name);
        mEditText.setText(tb_product.getTempRemark());
        mEditText.setSelection(mEditText.getText().toString().length());
        restoreActionBar();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            if(mEditText.getText().toString()!=null||mEditText.getText().toString()!=""){
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("是否保存草稿？")
                        .setCancelable(false)
                        .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tb_product.setTempRemark(mEditText.getText().toString());
                                productDAO.update(tb_product);
                                finish();
                            }
                        })
                        .setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tb_product.setTempRemark("");
                                productDAO.update(tb_product);
                                finish();
                            }
                        });
                builder.create().show();
            }else{
                finish();
            }
        }
        if(item.getItemId()==R.id.stock_remark_menu){
            tb_product.setTempRemark("");
            tb_product.setRemark(mEditText.getText().toString());
            productDAO.update(tb_product);

            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(mEditText.getText().toString().length()!=0){
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("是否保存草稿？")
                        .setCancelable(true)
                        .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tb_product.setTempRemark(mEditText.getText().toString());
                                productDAO.update(tb_product);
                                finish();
                            }
                        })
                        .setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tb_product.setTempRemark("");
                                productDAO.update(tb_product);
                                finish();
                            }
                        });
                builder.create().show();
            }else{
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.geminy_stock_remark_edit,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
