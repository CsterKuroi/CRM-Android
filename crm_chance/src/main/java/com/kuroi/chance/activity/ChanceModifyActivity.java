package com.kuroi.chance.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kuroi.chance.R;
import com.kuroi.chance.model.Chance;
import com.kuroi.chance.service.ChanceMODIFY;
import com.kuroi.chance.service.ChanceModifyCallBack;
import com.kuroi.chance.service.ChanceService;
import com.ricky.database.CenterDatabase;

import java.util.Calendar;


public class ChanceModifyActivity extends Activity implements ChanceModifyCallBack {

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
    private ChanceService service=null;
    private Calendar c = null;
    private String picName="";
    private Chance chance;
    private String userID;
	
	    private ImageView iv11;
    private ImageView iv12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_modify_chance);
        CenterDatabase cd = new CenterDatabase(this, null);
        userID = cd.getUID();
        cd.close();
        chance = new Chance();
        service = new ChanceService(this);
        init();
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        if(id == -1){
            finish();
        }else{
            chance = service.getById(id);
            number.setText(chance.getNumber());
            name.setText(chance.getName());
            customer.setText(chance.getCustomer());
            date.setText(chance.getDate());
            dateStart.setText(chance.getDateStart());
            dateEnd.setText(chance.getDateEnd());
            money.setText(chance.getMoney());
            principal.setText(chance.getPrincipal());
            ourSigner.setText(chance.getOurSigner());
            cusSigner.setText(chance.getCusSigner());
            remark.setText(chance.getRemark());
            discount.setText(chance.getDiscount());
            type.setText(chance.getType());
        }
        customer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentcus = new Intent(ChanceModifyActivity.this,
                        ChanceKLActivity.class);
                startActivityForResult(intentcus, 100);
            }
        });
        principal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentcus = new Intent(ChanceModifyActivity.this,
                        ChanceCGActivity.class);
                startActivityForResult(intentcus, 200);
            }
        });
        ourSigner.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentcus = new Intent(ChanceModifyActivity.this,
                        ChanceCGActivity.class);
                startActivityForResult(intentcus, 200);
            }
        });
        cusSigner.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentcus = new Intent(ChanceModifyActivity.this,
                        ChanceKLActivity.class);
                startActivityForResult(intentcus, 100);
            }
        });
        discount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(4);
            }
        });
        type.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(5);
            }
        });
//        actionBar.setTitle("      修改信息");
        date.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(1);
            }
        });
        dateStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(2);
            }
        });
        dateEnd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(3);
            }
        });
		
		        iv11.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hintKbTwo();
                finish();
            }
        });

        iv12.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(number.getText().toString().equals(""))
                    Toast.makeText(ChanceModifyActivity.this, "编号不能为空", Toast.LENGTH_LONG).show();
                else if(name.getText().toString().equals(""))
                    Toast.makeText(ChanceModifyActivity.this, "标题不能为空", Toast.LENGTH_LONG).show();
//            else if(customer.getText().toString().equals(""))
//                Toast.makeText(this, "客户不能为空", Toast.LENGTH_LONG).show();
                else if(date.getText().toString().equals(""))
                    Toast.makeText(ChanceModifyActivity.this, "签约日期不能为空", Toast.LENGTH_LONG).show();
//            else if(principal.getText().toString().equals(""))
//                Toast.makeText(this, "负责人不能为空", Toast.LENGTH_LONG).show();
                else if(money.getText().toString().equals(""))
                    Toast.makeText(ChanceModifyActivity.this, "总金额不能为空", Toast.LENGTH_LONG).show();
                else {
                    modifyToServer();
                }
            }
        });
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case 1:
                c = Calendar.getInstance();
                dialog = new DatePickerDialog(
                        this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker dp, int year,int month, int dayOfMonth) {
                                date.setText(new StringBuilder().append(year).append(
                                        (month + 1) < 10 ? "0" + (month + 1) : (month + 1)).append(
                                        (dayOfMonth < 10) ? "0" + dayOfMonth : dayOfMonth));
                            }
                        },
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)
                );
                break;
            case 2:
                c = Calendar.getInstance();
                dialog = new DatePickerDialog(
                        this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker dp, int year,int month, int dayOfMonth) {
                                dateStart.setText(new StringBuilder().append(year).append(
                                        (month + 1) < 10 ? "0" + (month + 1) : (month + 1)).append(
                                        (dayOfMonth < 10) ? "0" + dayOfMonth : dayOfMonth));
                            }
                        },
                        c.get(Calendar.YEAR), // 传入年份
                        c.get(Calendar.MONTH), // 传入月份
                        c.get(Calendar.DAY_OF_MONTH) // 传入天数
                );
                break;
            case 3:
                c = Calendar.getInstance();
                dialog = new DatePickerDialog(
                        this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker dp, int year,int month, int dayOfMonth) {
                                dateEnd.setText(new StringBuilder().append(year).append(
                                        (month + 1) < 10 ? "0" + (month + 1) : (month + 1)).append(
                                        (dayOfMonth < 10) ? "0" + dayOfMonth : dayOfMonth));
                            }
                        },
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)
                );
                break;
            case 4:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final ChoiceOnClickListener choiceListener =
                        new ChoiceOnClickListener();
                builder.setSingleChoiceItems(R.array.chance_discount, 0, choiceListener);
                DialogInterface.OnClickListener btnListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                int choiceWhich = choiceListener.getWhich();
                                String dis =
                                        getResources().getStringArray(R.array.chance_discount)[choiceWhich];
                                discount.setText(dis);
                            }
                        };
                builder.setPositiveButton("确定", btnListener);
                dialog = builder.create();
                break;
            case 5:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                final ChoiceOnClickListener choiceListener2 =
                        new ChoiceOnClickListener();
                builder2.setSingleChoiceItems(R.array.chance_type, 0, choiceListener2);
                DialogInterface.OnClickListener btnListener2 =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                int choiceWhich = choiceListener2.getWhich();
                                String typ =
                                        getResources().getStringArray(R.array.chance_type)[choiceWhich];
                                type.setText(typ);
                            }
                        };
                builder2.setPositiveButton("确定", btnListener2);
                dialog = builder2.create();
                break;
        }
        return dialog;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 100) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Bundle bundle = data.getExtras();
                    String re = bundle .getString("re");
                    String re2 = bundle .getString("re2");
                    customer.setText(re);
                    cusSigner.setText(re2);
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
        if (requestCode == 200) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Bundle bundle = data.getExtras();
                    String re = bundle .getString("re");
                    String re2 = bundle .getString("re2");
                    principal.setText(re2);
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
        if (requestCode == 300) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Bundle bundle = data.getExtras();
                    String re = bundle .getString("re");
                    String re2 = bundle .getString("re2");
                    ourSigner.setText(re2);
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
    }

    private void init(){
        number = (EditText)findViewById(R.id.chance_number);
        name = (EditText)findViewById(R.id.chance_name);
        customer = (EditText)findViewById(R.id.chance_customer);
        date = (EditText)findViewById(R.id.chance_date);
        dateStart = (EditText)findViewById(R.id.chance_dateStart);
        dateEnd = (EditText)findViewById(R.id.chance_dateEnd);
        money = (EditText)findViewById(R.id.chance_money);
        discount = (EditText)findViewById(R.id.chance_discount);
        type = (EditText)findViewById(R.id.chance_type);
        principal = (EditText)findViewById(R.id.chance_principal);
        ourSigner = (EditText)findViewById(R.id.chance_ourSigner);
        cusSigner = (EditText)findViewById(R.id.chance_cusSigner);
        remark = (EditText)findViewById(R.id.chance_remark);
        image = (ImageView)findViewById(R.id.image_view);
		
		        iv11=(ImageView)findViewById(R.id.imageView11);
        iv12=(ImageView)findViewById(R.id.imageView12);
    }
    private Chance getContent(){
        Chance c = new Chance();
        c.setId(chance.getId());
        c.setNumber(number.getText().toString());
        c.setName(name.getText().toString());
        c.setType(type.getText().toString());
        c.setCustomer(customer.getText().toString());
        c.setDate(date.getText().toString());
        c.setDateStart(dateStart.getText().toString());
        c.setDateEnd(dateEnd.getText().toString());
        c.setMoney(money.getText().toString());
        c.setDiscount(discount.getText().toString());
        c.setPrincipal(principal.getText().toString());
        c.setOurSigner(ourSigner.getText().toString());
        c.setCusSigner(cusSigner.getText().toString());
        c.setRemark(remark.getText().toString());
        return c;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_modify_chance, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            if(name.getText().toString().equals(""))
                Toast.makeText(this, "标题不能为空", Toast.LENGTH_LONG).show();
//            else if(customer.getText().toString().equals(""))
//                Toast.makeText(this, "客户不能为空", Toast.LENGTH_LONG).show();
            else if(date.getText().toString().equals(""))
                Toast.makeText(this, "签约日期不能为空", Toast.LENGTH_LONG).show();
//            else if(principal.getText().toString().equals(""))
//                Toast.makeText(this, "负责人不能为空", Toast.LENGTH_LONG).show();
            else if(money.getText().toString().equals(""))
                Toast.makeText(this, "总金额不能为空", Toast.LENGTH_LONG).show();
            else {
                    modifyToServer();
            }
            return true;
        }
        if (id == android.R.id.home)
        {
            hintKbTwo();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void modifyToServer() {
        ChanceMODIFY upload = new ChanceMODIFY(this, userID);
        String JSONString = upload.modifyJson(getContent());
        upload.modify(JSONString);
    }

    public void modifyCallBack(String payload) {
        if (payload.equals("1")) {
            boolean flag = service.update(getContent());
            if(flag) {
                hintKbTwo();
                Toast.makeText(this, "修改成功", Toast.LENGTH_LONG).show();
                finish();
            }
            else
                Toast.makeText(this, "修改失败,请检查网络", Toast.LENGTH_LONG).show();

        }
        else if (payload.equals("2")) {
            Toast.makeText(this, "修改失败,请检查网络", Toast.LENGTH_LONG).show();
        }
    }
    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&getCurrentFocus()!=null){
            if (getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
    private class ChoiceOnClickListener implements DialogInterface.OnClickListener {

        private int which = 0;
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            this.which = which;
        }

        public int getWhich() {
            return which;
        }
    }
}
