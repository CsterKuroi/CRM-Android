package com.kuroi.chance.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.kuroi.chance.R;
import com.kuroi.chance.model.Chance;
import com.kuroi.chance.service.ChanceDELETE;
import com.kuroi.chance.service.ChanceDOWN;
import com.kuroi.chance.service.ChanceDeleteCallBack;
import com.kuroi.chance.service.ChanceDownCallBack;
import com.kuroi.chance.service.ChanceService;
import com.ricky.database.CenterDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChanceMainActivity extends Activity implements ChanceDeleteCallBack,ChanceDownCallBack {

    private ListView chance_list=null;
    private EditText search=null;
    private List chances=null;
    private Chance chance=null;
    private ChanceService service=null;
    private Button button=null;
    private RelativeLayout relativeLayout=null;
    private RelativeLayout relativeLayout2=null;
    private RelativeLayout relativeLayout3=null;
    public static final int OPTION_DIALOG = 1;
    private PopupWindow popupWindow;
    private ListView menuListView;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    private TextView countview ;
    private int sort=0;
    private static final String[] m={"按照创建时间排序","预计成交时间排序","预计成交金额排序"};
    private static final String ACTIVITY_TAG="LogDemo";
    private String picName="";
    private static final int CAPTURE_REQUEST_CODE = 100;
    private String userID="101";
	
	    private RelativeLayout iv9;
    private ImageView iv10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_chance);
        CenterDatabase cd = new CenterDatabase(this, null);
        userID = cd.getUID();
        cd.close();
        service = new ChanceService(this);
        init();
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        getContent();

//        actionBar.setTitle("      机会");
        adapter = new ArrayAdapter<String>(this,R.layout.myspinner_chance,m);
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
                Intent intent = new Intent(ChanceMainActivity.this, ChanceAddActivity.class);
                startActivity(intent);
            }
        });
}
    private void init(){
        chance_list = (ListView)findViewById(R.id.chance_list);
        chance_list.setCacheColorHint(Color.TRANSPARENT);
        chance_list.setOnItemClickListener(new ViewItemListener());
        search = (EditText)findViewById(R.id.search_chance);
        search.addTextChangedListener(new SearchTextChangedListener());
        spinner = (Spinner) findViewById(R.id.Spinner01);
        relativeLayout=(RelativeLayout) findViewById(R.id.chance_search_go);
        relativeLayout2=(RelativeLayout) findViewById(R.id.chance_ss);
        relativeLayout3=(RelativeLayout) findViewById(R.id.chance_search);
        button=(Button)findViewById(R.id.chance_search_button);
		        iv9=(RelativeLayout)findViewById(R.id.back);
        iv10=(ImageView)findViewById(R.id.imageView10);
    }
    private void getContent(){
        downToServer();

    }
    private void downToServer() {
        ChanceDOWN upload = new ChanceDOWN(this, userID);
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
                JSONArray array = object.getJSONArray("jihui");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = (JSONObject) array.get(i);
                    Chance dataSet = new Chance();
                    dataSet.setId(obj.getInt("jihuiid"));
                    dataSet.setNumber(obj.getString("num"));
                    dataSet.setName(obj.getString("name"));
                    dataSet.setType(obj.getString("type"));
                    dataSet.setCustomer(obj.getString("customer"));
                    dataSet.setDate(obj.getString("date"));
                    dataSet.setDateStart(obj.getString("datestart"));
                    dataSet.setDateEnd(obj.getString("dateend"));
                    dataSet.setMoney(obj.getString("money"));
                    dataSet.setDiscount(obj.getString("stage"));
                    dataSet.setPrincipal(obj.getString("principal"));
                    dataSet.setOurSigner(obj.getString("ourcontacts"));
                    dataSet.setCusSigner(obj.getString("cuscontacts"));
                    dataSet.setRemark(obj.getString("remark"));
                    service.save(dataSet);
                }
                countview=(TextView) findViewById(R.id.countText);
//        countview.setText("机会总数:" + service.getCount().toString());
                List mylist = new ArrayList();
                String queryName = search.getText().toString();
                chances = service.getByName(queryName,sort); // get an chances array
                if(chances != null){
                    for(int i=0; i<chances.size(); i++){
                        Chance chance = (Chance)chances.get(i);
                        Calendar cal = Calendar.getInstance();
                        int year=cal.get(Calendar.YEAR);
                        int month=cal.get(Calendar.MONTH);
                        int dayOfMonth=cal.get(Calendar.DAY_OF_MONTH);
                        String now= new StringBuilder().append(year).append(
                                (month + 1) < 10 ? "0" + (month + 1) : (month + 1)).append(
                                (dayOfMonth < 10) ? "0" + dayOfMonth : dayOfMonth).toString();
                        String check=(now.compareTo(chance.getDate())>0&&!chance.getDate().equals(""))?"完成":chance.getDate();
                        // HashMap
                        HashMap map = new HashMap();
                        map.put("tv_number", chance.getId());
                        map.put("tv_money", chance.getMoney());
                        map.put("tv_name", chance.getName());
                        map.put("tv_date", chance.getDate());
                        mylist.add(map);
                    }
                }
                SimpleAdapter adapter = new SimpleAdapter(this, mylist,R.layout.my_list_item_chance,
                        new String[] {"tv_number","tv_money","tv_name","tv_date"},
                        new int[] {R.id.item_number,R.id.item_money,R.id.item_name,R.id.item_date});
                chance_list.setAdapter(adapter);
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
        getMenuInflater().inflate(R.menu.menu_main_chance, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_chance) {
            Intent intent = new Intent(ChanceMainActivity.this, ChanceAddActivity.class);
            startActivity(intent);
        }
        if (id == android.R.id.home)
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
    public Uri getOutputMediaFileUri() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Chance");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        picName = "IMG_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + picName);
        return Uri.fromFile(mediaFile);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        popupWindow.dismiss();
        if (requestCode == CAPTURE_REQUEST_CODE) {
            Log.d(ACTIVITY_TAG,"ok");
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.d(ACTIVITY_TAG, picName);
                    Chance cc=new Chance();
//                    cc.setName("照片模式");
                    cc.setImg(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES) + "/Chance/" + picName);
                    service.save(cc);
                    Log.d(ACTIVITY_TAG, picName);
                    break;
                case Activity.RESULT_CANCELED:
                    Log.d(ACTIVITY_TAG, "nonononononono");
                    break;
            }
            Log.d(ACTIVITY_TAG,"zzzzzzzzzzzzzzz");
        }
    }
    @Override
    protected void onRestart() {
        getContent();
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
        optionDialogView = li.inflate(R.layout.option_dialog_chance, null);
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
        View view = getLayoutInflater().inflate(R.layout.popup_window_chance, null);
        menuListView = (ListView)view.findViewById(R.id.popup_list_view);
        popupWindow = new PopupWindow(view, 220, WindowManager.LayoutParams.WRAP_CONTENT);
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("add","新建机会");
        data.add(map);
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("add","照片上传");
        data.add(map2);
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.popup_list_item_chance,
                new String[]{"add"},
                new int[]{R.id.add});
        menuListView.setAdapter(adapter);

        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(ChanceMainActivity.this, ChanceAddActivity.class);
                        startActivity(intent);
                        popupWindow.dismiss();
                        break;
                    case 1:
                        Log.d(ACTIVITY_TAG, "open");
                        popupWindow.dismiss();
                        Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
                        intent2.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                        startActivityForResult(intent2, CAPTURE_REQUEST_CODE);
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
        public void onTextChanged(CharSequence s, int start, int before, int count) {getContent();}
    }
    class ViewItemListener implements OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // get the chance from the chances array.
            chance = (Chance)chances.get(position);
            showDialog(OPTION_DIALOG);
        }
    }
    class ImageButtonListener implements OnClickListener{
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.dialog_call) {
                Intent intentm = new Intent(ChanceMainActivity.this, ChanceModifyActivity.class);
                intentm.putExtra("id", chance.getId());
                startActivity(intentm);
                dismissDialog(OPTION_DIALOG);

            } else if (i == R.id.dialog_view) {
                Intent intent = new Intent(ChanceMainActivity.this, ChanceDetailActivity.class);
                intent.putExtra("id", chance.getId());
                startActivity(intent);
                dismissDialog(OPTION_DIALOG);

            } else if (i == R.id.dialog_sms) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChanceMainActivity.this);
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
        }
        public void onNothingSelected(AdapterView<?> arg0) {}
    }
}