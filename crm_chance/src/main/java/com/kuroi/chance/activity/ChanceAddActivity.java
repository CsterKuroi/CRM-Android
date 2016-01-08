package com.kuroi.chance.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kuroi.chance.R;
import com.kuroi.chance.model.Chance;
import com.kuroi.chance.service.ChanceService;
import com.kuroi.chance.service.ChanceUPLOAD;
import com.kuroi.chance.service.ChanceUploadCallBack;
import com.ricky.database.CenterDatabase;

import java.util.Calendar;

public class ChanceAddActivity extends Activity implements ChanceUploadCallBack {
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
    private String userID;
	
	private RelativeLayout iv11;
    private ImageView iv12;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_chance);
        CenterDatabase cd = new CenterDatabase(this, null);
        userID = cd.getUID();
        cd.close();
        service = new ChanceService(this);
        init();

        customer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentcus = new Intent(ChanceAddActivity.this,
                        ChanceKLActivity.class);
                startActivityForResult(intentcus, 100);
            }
        });
        principal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentcus = new Intent(ChanceAddActivity.this,
                        ChanceCGActivity.class);
                startActivityForResult(intentcus, 200);
            }
        });
        ourSigner.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentcus = new Intent(ChanceAddActivity.this,
                        ChanceCGActivity.class);
                startActivityForResult(intentcus, 300);
            }
        });
        cusSigner.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentcus = new Intent(ChanceAddActivity.this,
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

        iv11.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hintKbTwo();
                finish();
            }
        });

        iv12.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            if(name.getText().toString().equals(""))
                    Toast.makeText(ChanceAddActivity.this, "标题不能为空", Toast.LENGTH_LONG).show();
//            else if(customer.getText().toString().equals(""))
//                Toast.makeText(this, "客户不能为空", Toast.LENGTH_LONG).show();
                else if(date.getText().toString().equals(""))
                    Toast.makeText(ChanceAddActivity.this, "签约日期不能为空", Toast.LENGTH_LONG).show();
//            else if(principal.getText().toString().equals(""))
//                Toast.makeText(this, "负责人不能为空", Toast.LENGTH_LONG).show();
                else if(money.getText().toString().equals(""))
                    Toast.makeText(ChanceAddActivity.this, "总金额不能为空", Toast.LENGTH_LONG).show();
                else {
                    uploadToServer();
                }
            }
        });
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

    @Override
    protected Dialog onCreateDialog(int id) {//日期选择
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
                        c.get(Calendar.YEAR), // 传入年份
                        c.get(Calendar.MONTH), // 传入月份
                        c.get(Calendar.DAY_OF_MONTH) // 传入天数
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
                        c.get(Calendar.YEAR), // 传入年份
                        c.get(Calendar.MONTH), // 传入月份
                        c.get(Calendar.DAY_OF_MONTH) // 传入天数
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
    private void init(){
        number = (EditText)findViewById(R.id.chance_number);  // get all EditText views
        name = (EditText)findViewById(R.id.chance_name);
        type = (EditText)findViewById(R.id.chance_type);
        customer = (EditText)findViewById(R.id.chance_customer);
        date = (EditText)findViewById(R.id.chance_date);
        dateStart = (EditText)findViewById(R.id.chance_dateStart);
        dateEnd = (EditText)findViewById(R.id.chance_dateEnd);
        money = (EditText)findViewById(R.id.chance_money);
        principal = (EditText)findViewById(R.id.chance_principal);
        ourSigner = (EditText)findViewById(R.id.chance_ourSigner);
        cusSigner = (EditText)findViewById(R.id.chance_cusSigner);
        remark = (EditText)findViewById(R.id.chance_remark);
        image = (ImageView)findViewById(R.id.image_view);
        discount=(EditText)findViewById(R.id.chance_discount);
		
		        iv11=(RelativeLayout)findViewById(R.id.back);
        iv12=(ImageView)findViewById(R.id.imageView12);
    }
    private Chance getContent(){//获取表单
        Chance chance = new Chance();
        chance.setId(service.getMax()+1);
        chance.setNumber(number.getText().toString());
        chance.setName(name.getText().toString());
        chance.setType(type.getText().toString());
        chance.setCustomer(customer.getText().toString());
        chance.setDate(date.getText().toString());
        chance.setDateStart(dateStart.getText().toString());
        chance.setDateEnd(dateEnd.getText().toString());
        chance.setMoney(money.getText().toString());
        chance.setDiscount(discount.getText().toString());
        chance.setPrincipal(principal.getText().toString());
        chance.setOurSigner(ourSigner.getText().toString());
        chance.setCusSigner(cusSigner.getText().toString());
        chance.setRemark(remark.getText().toString());
        chance.setImg(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/Chance/"+picName);
        return chance;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_chance, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {  // 保存
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
                uploadToServer();
            }

            return true;
        }
        if (id == android.R.id.home)  // 返回
        {
            hintKbTwo();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void uploadToServer() {
        ChanceUPLOAD upload = new ChanceUPLOAD(this, userID);
        String JSONString = upload.changeArrayDateToJson(getContent());
        upload.up(JSONString);
    }

    public void uploadCallBack(String payload) {
        if (payload.equals("1")) {
            boolean flag = service.save(getContent());
            if(flag) {
                hintKbTwo();
                Toast.makeText(this, "添加成功", Toast.LENGTH_LONG).show();
                finish();
            }
            else
                Toast.makeText(this, "添加失败,请检查网络", Toast.LENGTH_LONG).show();

        }
        else if (payload.equals("2")) {
            Toast.makeText(this, "添加失败,请检查网络", Toast.LENGTH_LONG).show();
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
