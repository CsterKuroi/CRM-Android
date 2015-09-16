package com.geminy.productshow.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.geminy.productshow.DAO.GEMINY_ProductDAO;
import com.geminy.productshow.Model.GEMINY_Tb_product;
import com.geminy.productshow.R;

/**
 * Created by Hatsune Miku on 2015/8/20.
 */
public class GEMINY_StockControlActivity extends ActionBarActivity{
    private EditText numEditText;
    private int num;
    private Button subButton;
    private GEMINY_ProductDAO productDAO;
    private GEMINY_Tb_product tb_product;
    public static final String PRO_NAME="";
    private String pro_name;
    private TextView remarkTextView;
    private String remarkUnsave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geminy_activity_stock_control);
        restoreActionBar();
        TextView nameTextView=(TextView)findViewById(R.id.stock_pro_name);
        numEditText=(EditText)findViewById(R.id.stock_pro_num);
        subButton=(Button)findViewById(R.id.stock_pro_sub);
        Button addButton=(Button)findViewById(R.id.stock_pro_add);
        remarkTextView=(TextView)findViewById(R.id.stock_pro_remark);
        pro_name=getIntent().getExtras().getString(GEMINY_ProductDetailInfosActivity.PRODUCT_NAME);
        nameTextView.setText(pro_name);
        productDAO=new GEMINY_ProductDAO(GEMINY_StockControlActivity.this);
        tb_product=productDAO.getByName(pro_name);
        num=tb_product.getNum();
        numEditText.setText(String.valueOf(num));
        remarkUnsave=tb_product.getRemark();
        remarkTextView.setText(tb_product.getRemark());
        numEditText.setSelection(numEditText.getText().toString().length());
        if(num==0){
            subButton.setVisibility(View.INVISIBLE);
        }
        numEditText.addTextChangedListener(new OnTextChangeListener());
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numString = numEditText.getText().toString();
                if (numString == null || numString.equals("")) {
                    num = 0;
                    subButton.setVisibility(View.INVISIBLE);
                    numEditText.setText(String.valueOf(num));
                } else {
                    num = Integer.parseInt(numString);
                    if (num - 1 < 0) {
                        Toast.makeText(GEMINY_StockControlActivity.this, "库存数量不能小于0！", Toast.LENGTH_SHORT).show();
                    } else if (num - 1 == 0) {
                        num = 0;
                        subButton.setVisibility(View.INVISIBLE);
                        numEditText.setText(String.valueOf(num));
                    } else {
                        --num;
                        numEditText.setText(String.valueOf(num));
                    }
                }
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numString = numEditText.getText().toString();
                if (numString == null || numString.equals("")) {
                    num = 0;
                    subButton.setVisibility(View.INVISIBLE);
                    numEditText.setText(String.valueOf(num));
                } else {
                    num=Integer.parseInt(numString);
                    ++num;
                    subButton.setVisibility(View.VISIBLE);
                    numEditText.setText(String.valueOf(num));
                }
            }
        });

        remarkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GEMINY_StockControlActivity.this,GEMINY_StockRemarkEditActivity.class);
                Bundle args=new Bundle();
                args.putString(PRO_NAME,pro_name);
                intent.putExtras(args);
                startActivity(intent);
            }
        });
    }


    private class OnTextChangeListener implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String numString = s.toString();
            if (numString == null || numString.equals("")) {
                num = 0;
            } else {
                int numInt = Integer.parseInt(numString);
                if (numInt < 0) {
                    Toast.makeText(GEMINY_StockControlActivity.this, "请输入一个大于0的数字",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if(numInt==0){
                        subButton.setVisibility(View.INVISIBLE);
                    }else{
                        subButton.setVisibility(View.VISIBLE);
                    }
                    //设置EditText光标位置 为文本末端
                    numEditText.setSelection(numEditText.getText().toString().length());
                    num = numInt;

                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.geminy_stock_control, menu);
        restoreActionBar();
        return super.onCreateOptionsMenu(menu);
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
            tb_product.setRemark(remarkUnsave);
            productDAO.update(tb_product);
            finish();
        }

        else if(item.getItemId()==R.id.stock_menu_save){
            AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(GEMINY_StockControlActivity.this);
            dialogBuilder.setTitle("是否保存库存修改？")
                    .setCancelable(true)
                    .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tb_product.setNum(num);
                            productDAO.update(tb_product);
                            finish();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialog=dialogBuilder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        productDAO=new GEMINY_ProductDAO(GEMINY_StockControlActivity.this);
        tb_product=productDAO.getByName(pro_name);
        remarkTextView.setText(tb_product.getRemark());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            tb_product.setRemark(remarkUnsave);
            productDAO.update(tb_product);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
