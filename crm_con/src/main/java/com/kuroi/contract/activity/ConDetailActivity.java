package com.kuroi.contract.activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kuroi.contract.R;
import com.kuroi.contract.model.Contract;
import com.kuroi.contract.service.ConDELETE;
import com.kuroi.contract.service.ConDeleteCallBack;
import com.kuroi.contract.service.ConPicURL;
import com.kuroi.contract.service.ConPicURLCallBack;
import com.kuroi.contract.service.ConService;
import com.ricky.database.CenterDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static com.kuroi.contract.activity.ConShowActivity.getExifOrientation;


public class ConDetailActivity extends Activity implements ConDeleteCallBack,ConPicURLCallBack{
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
    private ImageView image2=null;
    private ImageView image3=null;
    private Contract contract=null;
    private ConService service=null;
    private String userID="101";

    private static final String ACTIVITY_TAG="LogDemo";

    private ImageView iv11;
    private ImageView iv12;
    private ImageView iv14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail_con);
        CenterDatabase cd = new CenterDatabase(this, null);
        userID = cd.getUID();
        cd.close();
        contract = new Contract();
        init();
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        if (id == -1) {
            finish();
        } else {
            service = new ConService(this);
            contract = service.getById(id);
            number.setText(contract.getNumber());
            name.setText(contract.getName());
            type.setText(contract.getType());
            customer.setText(contract.getCustomer());
            date.setText(contract.getDate());
            dateStart.setText(contract.getDateStart());
            dateEnd.setText(contract.getDateEnd());
            money.setText(contract.getMoney());
            discount.setText(contract.getDiscount());
            principal.setText(contract.getPrincipal());
            ourSigner.setText(contract.getOurSigner());
            cusSigner.setText(contract.getCusSigner());
            remark.setText(contract.getRemark());
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "Contract");
            String uploadFile = mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+contract.getId()+"_"+"100.jpg";
            String uploadFile2 = mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+contract.getId()+"_"+"200.jpg";
            String uploadFile3 = mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+contract.getId()+"_"+"300.jpg";
            File f1=new File(uploadFile);
            File f2=new File(uploadFile2);
            File f3=new File(uploadFile3);
            if(f1.isFile()||f2.isFile()||f3.isFile()) {
                if (f1.isFile()) {
                    int digree = getExifOrientation(uploadFile);
                    Bitmap bm;
                    BitmapFactory.Options option = new BitmapFactory.Options();
                    option.inSampleSize = 10;
                    bm = BitmapFactory.decodeFile(uploadFile, option);
                    if (digree != 0) {
                        // 旋转图片
                        Matrix m = new Matrix();
                        m.postRotate(digree);
                        bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                                bm.getHeight(), m, true);
                    }
                    image.setImageBitmap(bm);
                }
                if (f2.isFile()) {
                    int digree2 = getExifOrientation(uploadFile2);
                    Bitmap bm2;
                    BitmapFactory.Options option2 = new BitmapFactory.Options();
                    option2.inSampleSize = 10;
                    bm2 = BitmapFactory.decodeFile(uploadFile2, option2);
                    if (digree2 != 0) {
                        // 旋转图片
                        Matrix m2 = new Matrix();
                        m2.postRotate(digree2);
                        bm2 = Bitmap.createBitmap(bm2, 0, 0, bm2.getWidth(),
                                bm2.getHeight(), m2, true);
                    }
                    image2.setImageBitmap(bm2);
                }
                if (f3.isFile()) {
                    int digree3 = getExifOrientation(uploadFile3);
                    Bitmap bm3;
                    BitmapFactory.Options option3 = new BitmapFactory.Options();
                    option3.inSampleSize = 10;
                    bm3 = BitmapFactory.decodeFile(uploadFile3, option3);
                    if (digree3 != 0) {
                        // 旋转图片
                        Matrix m3 = new Matrix();
                        m3.postRotate(digree3);
                        bm3 = Bitmap.createBitmap(bm3, 0, 0, bm3.getWidth(),
                                bm3.getHeight(), m3, true);
                    }
                    image3.setImageBitmap(bm3);
                }
            }
            else downpic();
        }

//        ActionBar actionBar=getActionBar();
//        actionBar.setDisplayShowHomeEnabled(false);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setTitle("      合同详情");
        image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "Contract");
                String uploadFile = mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+contract.getId()+"_"+"100.jpg";
                Intent intent = new Intent(ConDetailActivity.this, ConShowActivity.class);
                intent.putExtra("picName", uploadFile);
                startActivity(intent);
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "Contract");
                String uploadFile = mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+contract.getId()+"_"+"200.jpg";
                Intent intent = new Intent(ConDetailActivity.this, ConShowActivity.class);
                intent.putExtra("picName", uploadFile);
                startActivity(intent);
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "Contract");
                String uploadFile = mediaStorageDir.getAbsolutePath() + File.separator + userID+"_"+contract.getId()+"_"+"300.jpg";
                Intent intent = new Intent(ConDetailActivity.this, ConShowActivity.class);
                intent.putExtra("picName", uploadFile);
                startActivity(intent);
            }
        });
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
                Intent intent = new Intent(ConDetailActivity.this, ConModifyActivity.class);
                intent.putExtra("id", contract.getId());
                startActivity(intent);
            }
        });
    }
    public void init(){
        number = (EditText)findViewById(R.id.contract_number);
        name = (EditText)findViewById(R.id.contract_name);
        type = (EditText)findViewById(R.id.contract_type);
        customer = (EditText)findViewById(R.id.contract_customer);
        date = (EditText)findViewById(R.id.contract_date);
        dateStart = (EditText)findViewById(R.id.contract_dateStart);
        dateEnd = (EditText)findViewById(R.id.contract_dateEnd);
        money = (EditText)findViewById(R.id.contract_money);
        discount = (EditText)findViewById(R.id.contract_discount);
        principal = (EditText)findViewById(R.id.contract_principal);
        ourSigner = (EditText)findViewById(R.id.contract_ourSigner);
        cusSigner = (EditText)findViewById(R.id.contract_cusSigner);
        remark = (EditText)findViewById(R.id.contract_remark);
        Log.d(ACTIVITY_TAG, "1");
        image = (ImageView)findViewById(R.id.image_button);
        image2 = (ImageView)findViewById(R.id.imageView7);
        image3 = (ImageView)findViewById(R.id.imageView8);
        iv11=(ImageView)findViewById(R.id.imageView11);
        iv12=(ImageView)findViewById(R.id.imageView12);
        iv14=(ImageView)findViewById(R.id.imageView14);

    }
    private void dialog(){
        Builder builder = new Builder(ConDetailActivity.this);
        builder.setMessage("确定删除吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteToServer();
                finish();
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

    private void downpic() {
        ConPicURL ppp=new ConPicURL(this, userID);
        String JSONString = ppp.downpicJson(contract);
        ppp.downpic(JSONString);
    }
    public void picURLCallBack(String payload) {
        try {

            JSONObject object=null;
            String strUTF8 = URLDecoder.decode(payload, "UTF-8");
            object = new JSONObject(strUTF8);
            String err = object.getString("error");
        if (err.equals("1")) {
            JSONArray array = object.getJSONArray("pic");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = (JSONObject) array.get(i);
                if(obj.getString("type").equals("100")){
                    ConPicURL u100=new ConPicURL(this,obj.getString("url"),userID+"_"+contract.getId()+"_"+"100.jpg", userID);
                    u100.start();
                }
                else if(obj.getString("type").equals("200")){
                    ConPicURL u200=new ConPicURL(this,obj.getString("url"),userID+"_"+contract.getId()+"_"+"200.jpg", userID);
                    u200.start();
                }
                else if(obj.getString("type").equals("300")){
                    ConPicURL u300=new ConPicURL(this,obj.getString("url"),userID+"_"+contract.getId()+"_"+"300.jpg", userID);
                    u300.start();
                }
            }
            //set image
        }
        else if (err.equals("2")) {
            Toast.makeText(this, "下载图片失败，请检查网络", Toast.LENGTH_LONG).show();
        }
        else if (err.equals("9")) {
        }
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
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
            Toast.makeText(this, "删除成功", Toast.LENGTH_LONG).show();
            finish();

        }
        else if (payload.equals("2")) {
            Toast.makeText(this, "删除失败,请检查网络", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_con, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_modify) {
            Intent intent = new Intent(ConDetailActivity.this, ConModifyActivity.class);
            intent.putExtra("id", contract.getId());
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
            service = new ConService(this);
            contract = service.getById(id);
            number.setText(contract.getNumber());
            name.setText(contract.getName());
            type.setText(contract.getType());
            customer.setText(contract.getCustomer());
            date.setText(contract.getDate());
            dateStart.setText(contract.getDateStart());
            dateEnd.setText(contract.getDateEnd());
            money.setText(contract.getMoney());
            discount.setText(contract.getDiscount());
            principal.setText(contract.getPrincipal());
            ourSigner.setText(contract.getOurSigner());
            cusSigner.setText(contract.getCusSigner());
            remark.setText(contract.getRemark());
        }
        super.onRestart();
    }
}
