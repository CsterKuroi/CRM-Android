package com.kuroi.chance.activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kuroi.chance.R;
import com.kuroi.chance.model.Chance;
import com.kuroi.chance.service.ChanceDELETE;
import com.kuroi.chance.service.ChanceDeleteCallBack;
import com.kuroi.chance.service.ChanceService;
import com.ricky.database.CenterDatabase;

import java.io.File;



public class ChanceDetailActivity extends Activity implements ChanceDeleteCallBack {
    private EditText number=null;
    private EditText name=null;
    private EditText type=null;
    private EditText customer=null;
    private EditText date=null;
    private EditText dateStart=null;
    private EditText dateEnd=null;
    private EditText money=null;
    private EditText discount=null;
    private EditText principal=null;
    private EditText ourSigner=null;
    private EditText cusSigner=null;
    private EditText remark=null;
    private ImageView image=null;
    private Chance chance=null;
    private ChanceService service=null;
    private static final String ACTIVITY_TAG="LogDemo";
    private String userID="101";
	
	    private RelativeLayout iv11;
    private ImageView iv12;
    private ImageView iv14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail_chance);
        CenterDatabase cd = new CenterDatabase(this, null);
        userID = cd.getUID();
        cd.close();
        chance = new Chance();
        init();
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        if (id == -1) {
            finish();
        } else {
            service = new ChanceService(this);
            chance = service.getById(id);
            number.setText(chance.getNumber());
            name.setText(chance.getName());
            type.setText(chance.getType());
            customer.setText(chance.getCustomer());
            date.setText(chance.getDate());
            dateStart.setText(chance.getDateStart());
            dateEnd.setText(chance.getDateEnd());
            money.setText(chance.getMoney());
            discount.setText(chance.getDiscount());
            principal.setText(chance.getPrincipal());
            ourSigner.setText(chance.getOurSigner());
            cusSigner.setText(chance.getCusSigner());
            remark.setText(chance.getRemark());
            Log.d(ACTIVITY_TAG, "3");
        }

//        actionBar.setTitle("      机会详情");

		
        iv11.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        iv12.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog();
            }
        });

        iv14.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ChanceDetailActivity.this, ChanceModifyActivity.class);
                intent.putExtra("id", chance.getId());
                startActivity(intent);
            }
        });
    }
    public void init(){
        number = (EditText)findViewById(R.id.chance_number);
        name = (EditText)findViewById(R.id.chance_name);
        type = (EditText)findViewById(R.id.chance_type);
        customer = (EditText)findViewById(R.id.chance_customer);
        date = (EditText)findViewById(R.id.chance_date);
        dateStart = (EditText)findViewById(R.id.chance_dateStart);
        dateEnd = (EditText)findViewById(R.id.chance_dateEnd);
        money = (EditText)findViewById(R.id.chance_money);
        discount = (EditText)findViewById(R.id.chance_discount);
        principal = (EditText)findViewById(R.id.chance_principal);
        ourSigner = (EditText)findViewById(R.id.chance_ourSigner);
        cusSigner = (EditText)findViewById(R.id.chance_cusSigner);
        remark = (EditText)findViewById(R.id.chance_remark);
        image = (ImageView)findViewById(R.id.image_button);
		        iv11=(RelativeLayout)findViewById(R.id.back);
        iv12=(ImageView)findViewById(R.id.imageView12);
        iv14=(ImageView)findViewById(R.id.imageView14);
    }
    private void dialog(){
        Builder builder = new Builder(ChanceDetailActivity.this);
        builder.setMessage("确定删除吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteToServer();
            }
        });
        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void deleteToServer() {
        ChanceDELETE delete = new ChanceDELETE(this, userID);
        String JSONString = delete.deleteJson(chance);
        delete.delete(JSONString);
    }

    public void deleteCallBack(String payload) {
        if (payload.equals("1")) {
            service.delete(chance.getId());
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "Chance");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return;
                }
            }
            File f1 = new File(mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+chance.getId()+"_"+"100.jpg");
            File f2 = new File(mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+chance.getId()+"_"+"200.jpg");
            File f3 = new File(mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+chance.getId()+"_"+"300.jpg");
            if (f1.exists())  // 判断文件是否存在
                f1.delete();
            if (f2.exists())  // 判断文件是否存在
                f2.delete();
            if (f2.exists())  // 判断文件是否存在
                f2.delete();
            Toast.makeText(this, "删除成功", Toast.LENGTH_LONG).show();
            finish();

        }
        else if (payload.equals("2")) {
            Toast.makeText(this, "删除失败,请检查网络", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_chance, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_modify) {
            Intent intent = new Intent(ChanceDetailActivity.this, ChanceModifyActivity.class);
            intent.putExtra("id", chance.getId());
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_delete) {
            dialog();
            return true;
        }
        if (id == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onRestart() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        if(id == -1){
            finish();
        }else{
            service = new ChanceService(this);
            chance = service.getById(id);
            number.setText(chance.getNumber());
            name.setText(chance.getName());
            type.setText(chance.getType());
            customer.setText(chance.getCustomer());
            date.setText(chance.getDate());
            dateStart.setText(chance.getDateStart());
            dateEnd.setText(chance.getDateEnd());
            money.setText(chance.getMoney());
            discount.setText(chance.getDiscount());
            principal.setText(chance.getPrincipal());
            ourSigner.setText(chance.getOurSigner());
            cusSigner.setText(chance.getCusSigner());
            remark.setText(chance.getRemark());
        }
        super.onRestart();
    }
}
