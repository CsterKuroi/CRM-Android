package com.kuroi.contract.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kuroi.contract.R;
import com.kuroi.contract.model.Contract;
import com.kuroi.contract.service.ConDELETE;
import com.kuroi.contract.service.ConDOWN;
import com.kuroi.contract.service.ConDeleteCallBack;
import com.kuroi.contract.service.ConDownCallBack;
import com.kuroi.contract.service.ConService;
import com.ricky.database.CenterDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConMainActivity extends Activity implements ConDeleteCallBack,ConDownCallBack{

    private ListView contract_list=null;
    private EditText search=null;
    private List contracts=null;
    private Contract contract=null;
    private ConService service=null;
    public static final int OPTION_DIALOG = 1;
    private PopupWindow popupWindow;
    private ListView menuListView;
    private RelativeLayout relativeLayout=null;
    private RelativeLayout relativeLayout2=null;
    private RelativeLayout relativeLayout3=null;
    private Button button=null;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    private TextView countview ;
    private int sort=0;
    private static final String[] m={"创建时间排序","签约时间排序","按照金额排序"};
    private static final String ACTIVITY_TAG="LogDemo";
    private String userID="101";
    private Contract cc;

    private RelativeLayout iv9;
    private ImageView iv10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_con);
        CenterDatabase cd = new CenterDatabase(this, null);
        userID = cd.getUID();
        cd.close();
//        ActionBar actionBar=getActionBar();
//        actionBar.setDisplayShowHomeEnabled(false);
//        actionBar.setDisplayHomeAsUpEnabled(true);
        service = new ConService(this);
        init();
        getContent();
        adapter = new ArrayAdapter<String>(this,R.layout.myspinner_con,m);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        spinner.setVisibility(View.VISIBLE);
        initPopupWindow();
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                relativeLayout2.setVisibility(View.GONE);
                relativeLayout3.setVisibility(View.VISIBLE);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                relativeLayout3.setVisibility(View.GONE);
                relativeLayout2.setVisibility(View.VISIBLE);
                search.setText("");
            }
        });
        iv9.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        iv10.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(popupWindow.isShowing())
                    popupWindow.dismiss();
                else
                    popUp();
            }
        });
    }
    private void init(){
        contract_list = (ListView)findViewById(R.id.contract_list);
        contract_list.setCacheColorHint(Color.TRANSPARENT);
        contract_list.setOnItemClickListener(new ViewItemListener());
        search = (EditText)findViewById(R.id.search);
        search.addTextChangedListener(new SearchTextChangedListener());
        spinner = (Spinner) findViewById(R.id.Spinner01);
        relativeLayout=(RelativeLayout) findViewById(R.id.con_search_go);
        relativeLayout2=(RelativeLayout) findViewById(R.id.con_ss);
        relativeLayout3=(RelativeLayout) findViewById(R.id.con_search);
        button=(Button)findViewById(R.id.con_search_button);
        iv9=(RelativeLayout)findViewById(R.id.back);
        iv10=(ImageView)findViewById(R.id.imageView10);
    }
    private void getContent(){
        downToServer();
    }

    private void downToServer() {
        ConDOWN upload = new ConDOWN(this, userID);
        String JSONString = upload.downJson();
        upload.down(JSONString);
    }
    public void downCallBack(String payload){
        try {
            JSONObject object=null;
            String strUTF8 = payload;
            object = new JSONObject(strUTF8);
            String err = object.getString("error");
            if (err.equals("1")) {
                service.delete();
                JSONArray array = object.getJSONArray("hetong");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = (JSONObject) array.get(i);
                    Contract dataSet = new Contract();
                    dataSet.setId(obj.getInt("hetongid"));
                    dataSet.setNumber(obj.getString("num"));
                    dataSet.setName(obj.getString("name"));
                    dataSet.setType(obj.getString("type"));
                    dataSet.setCustomer(obj.getString("customer"));
                    dataSet.setDate(obj.getString("date"));
                    dataSet.setDateStart(obj.getString("dateStart"));
                    dataSet.setDateEnd(obj.getString("dateEnd"));
                    dataSet.setMoney(obj.getString("money"));
                    dataSet.setDiscount(obj.getString("discount"));
                    dataSet.setPrincipal(obj.getString("principal"));
                    dataSet.setOurSigner(obj.getString("ourSigner"));
                    dataSet.setCusSigner(obj.getString("cusSigner"));
                    dataSet.setRemark(obj.getString("remark"));
                    service.save(dataSet);
                }
                countview=(TextView) findViewById(R.id.countText);
//        countview.setText("合同总数:" + service.getCount().toString());
                List mylist = new ArrayList();
                String queryName = search.getText().toString();
                //down - dele -save
                contracts = service.getByName(queryName,sort); // get an contracts array
                if(contracts != null){
                    for(int i=0; i<contracts.size(); i++){
                        Contract contract = (Contract)contracts.get(i);
                        Calendar cal = Calendar.getInstance();
                        int year=cal.get(Calendar.YEAR);
                        int month=cal.get(Calendar.MONTH);
                        int dayOfMonth=cal.get(Calendar.DAY_OF_MONTH);
                        String now= new StringBuilder().append(year).append(
                                (month + 1) < 10 ? "0" + (month + 1) : (month + 1)).append(
                                (dayOfMonth < 10) ? "0" + dayOfMonth : dayOfMonth).toString();
                        String check=(now.compareTo(contract.getDate())>0&&!contract.getDate().equals(""))?"完成":contract.getDate();
                        // HashMap
                        HashMap map = new HashMap();
                        map.put("tv_number", contract.getNumber());
                        map.put("tv_money", contract.getMoney());
                        map.put("tv_name", contract.getName());
                        map.put("tv_date", check);
                        mylist.add(map);
                    }
                }
                SimpleAdapter adapter = new SimpleAdapter(this, mylist,R.layout.my_list_item_con,
                        new String[] {"tv_number","tv_money","tv_name","tv_date"},
                        new int[] {R.id.item_number,R.id.item_money,R.id.item_name,R.id.item_date});
                contract_list.setAdapter(adapter);
            }
            else if (err.equals("2")) {
                service.delete();
                Toast.makeText(this, "查询失败,请检查网络", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_con, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_contract) {
            if(popupWindow.isShowing())
                popupWindow.dismiss();
            else
                popUp();
            return true;
        }
        if (id == android.R.id.home)  // 返回
        {
            finish();
        }
//        if(id == R.id.camera) {
//            Log.d(ACTIVITY_TAG, "open");
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
//            startActivityForResult(intent, CAPTURE_REQUEST_CODE);
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }
    //显示PopupWindow菜单
    private void popUp(){
        //设置位置
        popupWindow.showAsDropDown(this.findViewById(R.id.imageView10), 0, 2);
    }



    @Override
    protected void onRestart() {
        getContent();
        Log.e("gggggggg", "2222222222222");
        super.onRestart();
    }
    protected Dialog onCreateDialog(int id){
        Dialog dialog;
        switch(id){
            case OPTION_DIALOG:
                dialog = createOptionDialog();
                dialog.setCanceledOnTouchOutside(true);
                break;
            default:
                dialog = null;
        }
        return dialog;
    }
    private Dialog createOptionDialog(){
        final Dialog optionDialog;
        View optionDialogView = null;
        LayoutInflater li = LayoutInflater.from(this);
        optionDialogView = li.inflate(R.layout.option_dialog_con, null);
        optionDialog = new AlertDialog.Builder(this).setView(optionDialogView).create();
        ImageButton ibCall = (ImageButton)optionDialogView.findViewById(R.id.dialog_call);
        ImageButton ibView = (ImageButton)optionDialogView.findViewById(R.id.dialog_view);
        ImageButton ibSms = (ImageButton)optionDialogView.findViewById(R.id.dialog_sms);
        ibCall.setOnClickListener(new ImageButtonListener());
        ibView.setOnClickListener(new ImageButtonListener());
        ibSms.setOnClickListener(new ImageButtonListener());
        return optionDialog;
    }
    private void initPopupWindow(){
        View view = getLayoutInflater().inflate(R.layout.popup_window_con, null);
        menuListView = (ListView)view.findViewById(R.id.popup_list_view);
        popupWindow = new PopupWindow(view, 220, WindowManager.LayoutParams.WRAP_CONTENT);
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("add","新建合同");
        data.add(map);
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("add","照片上传");
        data.add(map2);
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.popup_list_item_con,
                new String[]{"add"},
                new int[]{R.id.add});
        menuListView.setAdapter(adapter);

        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(ConMainActivity.this, ConAddActivity.class);
                        startActivity(intent);
                        popupWindow.dismiss();
                        break;
                    case 1:
                        Log.d(ACTIVITY_TAG, "open");
                        popupWindow.dismiss();
                        Intent intentp = new Intent(ConMainActivity.this, ConPicActivity.class);
                        startActivity(intentp);
                        break;
                }
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
    }
//    private void popUp(){
//        popupWindow.showAsDropDown(this.findViewById(R.id.more), 0, 2);
//    }
    //**************** internal class as Listener ******************
    class SearchTextChangedListener implements TextWatcher{
        @Override
        public void afterTextChanged(Editable s) {}
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {getContent();Log.e("gggggggg", "4444444444444444");}
    }
    class ViewItemListener implements OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // get the contract from the contracts array.
            contract = (Contract)contracts.get(position);
            showDialog(OPTION_DIALOG);
        }
    }
    class ImageButtonListener implements OnClickListener{
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.dialog_call) {
                    Intent intentm = new Intent(ConMainActivity.this, ConModifyActivity.class);
                    intentm.putExtra("id", contract.getId());
                    startActivity(intentm);
                    dismissDialog(OPTION_DIALOG);
            } else if (v.getId() == R.id.dialog_view) {
                    Intent intent = new Intent(ConMainActivity.this, ConDetailActivity.class);
                    intent.putExtra("id", contract.getId());
                    startActivity(intent);
                    dismissDialog(OPTION_DIALOG);
            } else if (v.getId() == R.id.dialog_sms) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ConMainActivity.this);
                    builder.setMessage("确定删除吗?");
                    builder.setTitle("提示");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            deleteToServer();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    dismissDialog(OPTION_DIALOG);
            } else {
                    dismissDialog(OPTION_DIALOG);
            }
        }
    }
    private void deleteToServer() {
        ConDELETE delete = new ConDELETE(this, userID);
        String JSONString = delete.deleteJson(contract);
        delete.delete(JSONString);
    }

    public void deleteCallBack(String payload) {
        if (payload.equals("1")) {
            service.delete(contract.getId());
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "Contract");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return;
                }
            }
            File f1 = new File(mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+contract.getId()+"_"+"100.jpg");
            File f2 = new File(mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+contract.getId()+"_"+"200.jpg");
            File f3 = new File(mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+contract.getId()+"_"+"300.jpg");
            if (f1.exists())  // 判断文件是否存在
                f1.delete();
            if (f2.exists())  // 判断文件是否存在
                f2.delete();
            if (f2.exists())  // 判断文件是否存在
                f2.delete();
            boolean flag = true;
            if(flag) {
                Toast.makeText(this, "删除成功", Toast.LENGTH_LONG).show();
                getContent();
                Log.e("gggggggg", "55555555555555555555555");
            }
            else
                Toast.makeText(this, "删除失败,请检查网络", Toast.LENGTH_LONG).show();

        }
        else if (payload.equals("2")) {
            Toast.makeText(this, "删除失败,请检查网络", Toast.LENGTH_LONG).show();
        }
    }
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            sort=arg2;
            getContent();
            Log.e("gggggggg", "333333333333333333");
        }
        public void onNothingSelected(AdapterView<?> arg0) {}
    }
}