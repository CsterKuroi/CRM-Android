package com.example.dt.testapp3;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dt.testapp3.GodLiuSortListView.GodLiuSortListMainActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.tavendo.autobahn.WebSocketConnection;

//import static com.example.dt.testapp3.GodLiuStructure.*;

public class VisitMainActivity extends Activity implements VisitDatePickerFragment.OnFragmentDatePickListener {

    private String UID = "172";
    private String UNAME = "UNAME";
    private ListView list;

    private VisitOverviewFragment visitOverviewFragment;
    private VisitContentFragment visitContentFragment;
    private VisitLoadingFragment visitLoadingFragment;
    private VisitEvaluationFragment visitEvaluationFragment;

    private FragmentManager fm;
    private FragmentTransaction ft;
    private int currentFg = -1;
    private boolean isAdding = false;
    private boolean isVisiting = false;

    private VisitDB visitdb;
    private SQLiteDatabase dbw;
    private int MaxId;
    private ArrayList<VisitData> dbData;
    private ArrayList<ApprovalData> approvalDataList;
    private ArrayList<VisitData> app2dataList;

    private int tmpCompanyId;
    private String tmpCompany;
    private int[] tmpTargetId;
    private String[] tmpTarget;

    private WebSocketConnection mConnection;

    private MenuItem addItem;
    private MenuItem finishItem;
    private MenuItem submitItem;
    private MenuItem tapeItem;
    private MenuItem playItem;

    private boolean tapeItemVisible;
    private boolean playItemVisible;

    private MediaRecorder recorder;
    private int MaxTapeId;
    private String name;
    private MediaPlayer player;
    private ArrayList<String> newTapeList;

    private int IntentId;
    private int selector;
    private int selectorId = 0;

    private boolean isTaping;
    private boolean check;

    private GodLiuStructure godLiuStructure;
    private String[] userList;

    private ArrayList<ApprovalJSONOperate> approvalJsonList;
    private ArrayList<VisitJSONOperate> visitJsonList;

    private int evalLevel;
    private String[] evalTape;
    private String evalDetail;

    private CountDownLatch latch;

    //    public Handler afterSavingHandler;
//    public Handler afterInitHandler;
//    public Handler showHandler;
    public Handler evaluationHandler;
    public Handler syncHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_visit_main);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        fm = getFragmentManager();

        callLoading(-1);

        visitJsonList = new ArrayList<>();
        approvalJsonList = new ArrayList<>();
        isTaping = false;
//        selectorId = 1;
//        selector = "星网宇达";

//        afterSavingHandler = new Handler(){
//            public void handleMessage(Message msg){
//                afterSaving();
//            }
//        };
//        afterInitHandler = new Handler(){
//            public void handleMessage(Message msg){
//                afterInit();
//            }
//        };
//
//        showHandler = new Handler(){
//            public void handleMessage(Message msg){
//                showVisitOverviewFragment();
//            }
//        };

        evaluationHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                callEvaluation();
            }
        };
        syncHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                sendSignId();
            }
        };

        dbData = new ArrayList<VisitData>();

        visitJsonList.add(new VisitJSONOperate().setAct(this).setUID(UID).getAll(dbData));

        setLoadingText("正在同步本地数据库...");

        visitdb = new VisitDB(VisitMainActivity.this, "USER" + UID, null, 1);
        dbw = visitdb.getWritableDatabase();

        Log.e("ArrayList.size() : ", String.valueOf(dbData.size()));

        Intent intent = getIntent();
        if (intent != null) {

            IntentId = intent.getIntExtra("type", 0);
            switch (IntentId) {
                case 1:
                case 2:
                    selectorId = IntentId;
                    selector = intent.getIntExtra("name", -1);
                    ;
                    break;
                default:
            }
        }

        //initialize variables
        tmpCompanyId = -1;
        tmpCompany = null;
        tmpTargetId = null;
        tmpTarget = null;
        newTapeList = new ArrayList<>();

    }


    public void setMaxId(int _MaxId) {
        MaxId = _MaxId;
    }

    public void setLoadingText(String txt) {
        visitLoadingFragment.setText(txt);
    }

    public void setTapeId(int _MaxTapeId) {
        MaxTapeId = _MaxTapeId;
    }

    public void setIntoContent() {
        currentFg = 1;
        tapeItem.setVisible(true);
        playItem.setVisible(true);
    }

    public void addApprovalJsonItem(ApprovalJSONOperate js) {
        synchronized (approvalJsonList) {
            approvalJsonList.add(js);
        }
    }

    public void setVisitContentFragment(VisitContentFragment _Visit_contentFragment) {
        visitContentFragment = _Visit_contentFragment;
    }

    public void setNewTapeList(ArrayList<String> _list) {
        newTapeList = _list;
    }

    public void setTapeAndPlayVisible(boolean kms) {
        tapeItem.setVisible(kms);
        playItem.setVisible(kms);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_visit_main, menu);
        addItem = menu.findItem(R.id.action_add);
        finishItem = menu.findItem(R.id.action_finish);
        submitItem = menu.findItem(R.id.action_submit);
        tapeItem = menu.findItem(R.id.action_tape);
        playItem = menu.findItem(R.id.action_play);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        if (id == R.id.action_add){
            switch (currentFg) {
                case 0:
                    clickToContent();
                    break;
                case 1:
                    if (visitContentFragment.getCondition() > 3)
                        return true;
                    clickAddAndSave(false, false, null, null);
            }
            return true;
        }
        else if (id == R.id.action_submit){
            if (currentFg == 1)
                callCensor();
            return true;
        }
        else if (id == android.R.id.home){
            clickBack();
            return true;
        }
        else if (id == R.id.action_tape){
            if (currentFg == 1)
                clickTape();
            return true;
        }
        else if (id == R.id.action_play){
            if (currentFg == 1)
                clickPlay();
            return true;
        }
        else if (id == R.id.action_finish){
            clickFinish();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }


    private void clickAddAndSave(boolean visit, final boolean isCensor, String censorId, String censorName) {

        //TODO : save it to SQLite.
        callLoading(2);


        visitdb = new VisitDB(VisitMainActivity.this, "USER" + UID, null, 1);
        dbw = visitdb.getWritableDatabase();

        String _date = ((TextView) findViewById(R.id.Date)).getText().toString();            //拜访日期
        String _place = ((TextView) findViewById(R.id.Place)).getText().toString();          //地点
//                String companyData = ((TextView) findViewById(R.id.Company)).getText().toString();      //客户
//                String targetData = ((TextView) findViewById(R.id.Target)).getText().toString();        //对象
        String _todo = ((EditText) findViewById(R.id.Todo)).getText().toString();            //目的
        int _type = ((Spinner) findViewById(R.id.Type)).getSelectedItemPosition();           //类型
        String _record = ((EditText) findViewById(R.id.Record)).getText().toString();        //记录
        int _result = ((Spinner) findViewById(R.id.Result)).getSelectedItemPosition();       //结果
        String _remark = ((EditText) findViewById(R.id.Remark)).getText().toString();        //备注
        String _tape = visitContentFragment.getTapeList().toString();                        //录音
        Log.e("tapeList.toString() = ", _tape);
        _tape = _tape.substring(1, _tape.length() - 1);


        if (isAdding) {
            isAdding = false;
            ++MaxId;
            if (tmpTargetId == null) {
                tmpTargetId = new int[0];
            }
            if (tmpTarget == null) {
                tmpTarget = new String[0];
            }
            if (tmpCompany == null) {
                tmpCompany = "";
            }
            VisitData AddVisitData = new VisitData(MaxId, UID, UID, UNAME, _date, _place,
                    tmpCompanyId, tmpCompany, tmpTargetId, tmpTarget, _todo,
                    _type, _record, _result, _remark, _tape);
            String dataStr = AddVisitData.getAddSQLString();
            String addStr = "insert into visitTable values ( " + dataStr + ")";
//                    visitData.setId(MaxId);
            visitJsonList.add(
                    new VisitJSONOperate(AddVisitData)
                            .setAct(this)
                            .setFragment(visitOverviewFragment)
                            .setUID(UID)
                            .addJson(addStr));

        } else {
            final VisitContentFragment v = visitContentFragment;
            if (tmpTargetId == null) {
                tmpTargetId = v.getTargetId();
            }
            if (tmpTarget == null) {
                tmpTarget = v.getTarget();
            }
            if (tmpCompanyId == -1) {
                tmpCompanyId = v.getCompanyId();
            }
            if (tmpCompany == null) {
                tmpCompany = v.getCompany();
            }
            VisitData updateVisitData = new VisitData(v.getVisitId(), UID, v.getSubmitId(), v.getSubmitName(),
                    _date, v.getDateInt(), _place, tmpCompanyId, tmpCompany, tmpTargetId, tmpTarget,
                    _todo, _type, _record, _result, _remark, _tape, v.getCondition(),
                    v.getPartnerId(), v.getPartnerName(), v.getPidL(), v.getPidC(), v.getIsTemp(), null, null);

            if (visit) {
                NotificationManager notifiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notifiManager.cancel(0);
                isVisiting = false;
                updateVisitData.setCondition(2);
            }

            if (isCensor) {
                updateVisitData.setCensor(censorId, censorName).setCondition(4);
                String updateStr = "update visitTable set " +
                        updateVisitData.getUpdateSQLString() +
                        " where id = " + v.getVisitId();
                approvalJsonList.add(new ApprovalJSONOperate(UID).setAct(this).submit(updateVisitData));
                visitJsonList.add(
                        new VisitJSONOperate(updateVisitData)
                                .setAct(this)
                                .setFragment(visitOverviewFragment)
                                .setUID(UID)
                                .updateJson(updateStr));
            } else {
                String updateStr = "update visitTable set " +
                        updateVisitData.getUpdateSQLString() +
                        " where id = " + v.getVisitId();
                visitJsonList.add(
                        new VisitJSONOperate(updateVisitData)
                                .setAct(this)
                                .setFragment(visitOverviewFragment)
                                .setUID(UID)
                                .updateJson(updateStr));
            }


//                    json.updateJson(updateStr);
        }


        dbw.close();
        setLoadingText("正在保存至远程服务器...");
//        Toast.makeText(this, "正在保存至远程服务器...", Toast.LENGTH_SHORT).show();
    }

    private void clickToContent() {
        isAdding = true;
        ft = fm.beginTransaction();
        visitContentFragment = new VisitContentFragment();
        Bundle nbundle = new Bundle();
        nbundle.putInt("id", -1);
        nbundle.putString("uid", UID);
        nbundle.putString("uname",UNAME);
        visitContentFragment.setArguments(nbundle);
        ft.replace(R.id.content, visitContentFragment);
        ft.addToBackStack("overview");
        ft.commit();
        currentFg = 1;
        addItem.setTitle("保存");
        tapeItem.setVisible(true);
        playItem.setVisible(true);
    }

    private void clickBack() {
        submitItem.setVisible(false);
        switch (currentFg) {
            case -1:
            case 0:
                onBackPressed();
                break;
            case 1:
                backFromContent();
                break;
            case 2:
                backFromLoading();
                break;
            case 3:
                backFromCensor();
                break;
        }
    }

    private void clickTape() {
        if (tapeItem.getTitle().equals("录音")) {
            tapeItem.setTitle("停止录音");
            recorder = new MediaRecorder();// new出MediaRecorder对象
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置MediaRecorder的音频源为麦克风
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
            // 设置MediaRecorder录制的音频格式
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
            // 设置MediaRecorder录制音频的编码为amr.
//                Log.e("/sdcard/", Environment.getExternalStorageDirectory().getPath());
            ++MaxTapeId;
            name = String.format("tape_%04d_%08d_%04d.amr", Integer.parseInt(UID), (isAdding ? MaxId + 1 : visitContentFragment.getVisitId()), MaxTapeId);
            String dir = String.format("%s/SaleCircle/%s", Environment.getExternalStorageDirectory().getPath(), name);
//                format : tape+uid(%4d)+id(%8d)+number(%4d);
            Log.e("directory : ", dir);
            recorder.setOutputFile(dir);
            // 设置录制好的音频文件保存路径

            try {
                Log.e("Recorder:", "Recorder is loading!!!!!!!");
                recorder.prepare();// 准备录制
                Log.e("Recorder:", "Recorder is preparing!!!!!!!");
                recorder.start();// 开始录制
                Toast.makeText(this, "正在后台录音...", Toast.LENGTH_SHORT).show();
                isTaping = true;
                Log.e("Recorder:", "Recorder is recording!!!!!!!");
            } catch (IllegalStateException e) {
                Toast.makeText(this, "初始化失败。录音设备状态错误。请重试。", Toast.LENGTH_SHORT).show();
                tapeItem.setTitle("录音");
                if (recorder != null) {
                    recorder.stop();
                    recorder.reset();
                    recorder.release();
                }
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(this, "录音设备初始化失败。I/O错误。请重试。", Toast.LENGTH_SHORT).show();
                tapeItem.setTitle("录音");
                e.printStackTrace();
            }
        } else {//(tapeItem.getTitle().equals("播放")) {
            tapeItem.setTitle("录音");
            recorder.stop();// 停止刻录
            Log.e("Recorder:", "Recorder is stoping!!!!!!!");
            //recorder.reset(); // 重新启动MediaRecorder.
            recorder.release(); // 刻录完成一定要释放资源
            Log.e("Recorder:", "Recorder is releasing!!!!!!!");
            visitContentFragment.addTape(name);
            newTapeList.add(name);
            Toast.makeText(this, "录音已停止。", Toast.LENGTH_SHORT).show();
            isTaping = false;

        }
    }

    private void clickPlay() {
        if (playItem.getTitle().equals("播放")) {
            Spinner tapeSpinner = (Spinner) findViewById(R.id.Tape_spinner);
            String str = tapeSpinner.getSelectedItem().toString();
            player = new MediaPlayer();
            try {
                String str2 = String.format("%s/SaleCircle/%s", Environment.getExternalStorageDirectory().getPath(), str);
                Log.e("player data source", str2);
                player.setDataSource(str2);
                player.prepare();
                player.start();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        playItem.setTitle("播放");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            playItem.setTitle("停止播放");
        } else {
            player.stop();
            player.release();
            playItem.setTitle("播放");
        }
    }

    private void clickFinish(){
        clickAddAndSave(true, false, null, null);
    }


    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            clickBack();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
//        if (keyCode == KeyEvent.KEYCODE_BACK && fm.getBackStackEntryCount() != 0) {
//            backFromContent();
//            return true;
//        } else if (keyCode == KeyEvent.KEYCODE_BACK && player != null && player.isPlaying()) {
//            new AlertDialog.Builder(this)
//                    .setTitle("提示")
//                    .setMessage("有一条录音正在播放，\n是否停止播放并返回？")
//                    .setNegativeButton("否", null)
//                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            player.stop();
//                            player.release();
//                            playItem.setTitle("播放");
//                        }
//                    })
//                    .show();
//            return true;
//        }

    }


    public void onFragmentDatePick(String date) {
        //ft = fm.beginTransaction();
        TextView dateText = (TextView) findViewById(R.id.Date);
        dateText.setText(date);
        dateText.setTextColor(0xff000000);
        //dateText.setText(date.YEAR + "-" + (date.MONTH+1) + "-" + date.DATE);
    }


    public void afterInit() {
        //write database.
//        MaxId = _maxId;
        dbw.execSQL("delete from visitTable");
        for (VisitData tmp : dbData) {
            String dataStr = tmp.getAddSQLString();
            Log.e("test : ", String.valueOf(tmp.getId()) + ",,,," + dataStr);
            String insertStr = "insert into visitTable values ( " + dataStr + ")";
            Log.e("test : ", "insert successfully");
            dbw.execSQL(insertStr);
        }

        //change model at here.
//        showVisitOverviewFragment();
        getApprovalInfo();
    }

    @Override
    public void onResume(){
        if (fm == null)
            fm = getFragmentManager();
        super.onResume();
    }
    @Override
    public void onPause(){
        fm = null;
        super.onPause();
    }

    public void showVisitOverviewFragment() {
        if (fm == null)
            return;
        ft = fm.beginTransaction();
        visitOverviewFragment = new VisitOverviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uid", UID);
        bundle.putInt("selectorId", selectorId);
        bundle.putInt("selector", selector);
        visitOverviewFragment.setArguments(bundle);
        ft.replace(R.id.content, visitOverviewFragment);
        ft.commit();
        currentFg = 0;
        Log.e("MainActivity", "show VisitOverviewFragment");
    }

    public void afterSaving() {
        //upload tape files
        for (String s : newTapeList) {
            Thread t = new Thread(new VisitHTTPUploader(s));
            t.start();
        }
        //reinitialize some variables
        tmpCompanyId = -1;
        tmpCompany = null;
        tmpTargetId = null;
        tmpTarget = null;

        backFromSaving();

    }

    public void getApprovalInfo() {
        //todo : get approval info
        setLoadingText("正在获取新审批数据......");
        Log.e("ApprovalInfo", "getting.");
        approvalDataList = new ArrayList<ApprovalData>();
        approvalJsonList.add(new ApprovalJSONOperate(UID).setAct(this).getApprovalInfo(approvalDataList));
    }

    public void SyncApprovalInfo() {
        setLoadingText("正在同步新审批数据......");
        Log.e("ApprovalInfo", "Syncing");
        app2dataList = new ArrayList<>();
        latch = new CountDownLatch(approvalDataList.size());
        for (ApprovalData d : approvalDataList) {
            Log.e("ApprovalInfo", "Adding");
            VisitData tmp = new VisitData(UID, d).setId(++MaxId);
            app2dataList.add(tmp);
            String dataStr = tmp.getAddSQLString();
            Log.e("SQL ApprovalInfo : ", String.valueOf(tmp.getId()) + ",,,," + dataStr);
            String insertStr = "insert into visitTable values ( " + dataStr + ")";
            Log.e("SQL ApprovalInfo : ", "insert successfully");
            visitJsonList.add(
                    new VisitJSONOperate(tmp)
                            .setAct(this)
                            .setFragment(visitOverviewFragment)
                            .setLatch(latch)
                            .setUID(UID)
                            .addJson(insertStr));
//            dbw.execSQL(insertStr);
        }
        Log.e("approvalDataList.size()", " = " + approvalDataList.size());

        Thread syncThread = new Thread(new VisitSyncNewApproval().setAct(this).setLatch(latch).setList(app2dataList));
        syncThread.start();

//        showVisitOverviewFragment();
    }

    public void sendSignId() {
        if (approvalDataList.size() != 0) {
            approvalJsonList.add(new ApprovalJSONOperate(UID).setAct(this).sendSignedId(app2dataList));
        }
        approvalJsonList.add(new ApprovalJSONOperate(UID).setAct(this).getUserList());
    }

    public void callContact() {
        Intent intent = new Intent(this, VisitContactActivity.class);
        startActivityForResult(intent, 1);
    }

    public void analyzeUserList(GodLiuStructure _Visit_structure) {
        godLiuStructure = _Visit_structure;
        List<GodLiuStructure.User> users = _Visit_structure.userList; //读保存的structure
        userList = new String[users.size()];

        for (int i = 0; i < users.size(); i++) {
            userList[i] = users.get(i).name;
        }
    }

    public void callCensor() {
        Intent intent = new Intent(this, GodLiuSortListMainActivity.class);
        intent.putExtra("title", "选择执行人");
        intent.putExtra("data", userList);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == 1) {
                    tmpCompanyId = data.getIntExtra("companyId", -1);
                    tmpCompany = data.getStringExtra("company");
                    tmpTargetId = data.getIntArrayExtra("targetId");
                    tmpTarget = data.getStringArrayExtra("target");

                    Log.e("Get contact", tmpCompanyId + ", " + tmpCompany + ", " +
                            Arrays.toString(tmpTargetId) + ", " + Arrays.toString(tmpTarget));
                    ((TextView) findViewById(R.id.Place)).setText("");

                    TextView company = (TextView) findViewById(R.id.Company);
                    TextView target = (TextView) findViewById(R.id.Target);
                    company.setText(tmpCompany);
                    target.setText(Arrays.toString(tmpTarget));
                    visitJsonList.add(new VisitJSONOperate().setAct(this).setUID(UID).getAddress(tmpCompany));
                }
                break;
            case 2:
                //get user name & id.
                if (data == null)
                    return;
                int index = data.getIntExtra("index", 0);
                GodLiuStructure.User user = godLiuStructure.userList.get(index); //user里有id和name
                clickAddAndSave(false, true, user.id, user.name);
                break;

        }

    }

    public void setPlace(String placeStr) {
        String place = ((TextView) findViewById(R.id.Place)).getText().toString();
        place += placeStr;
        ((TextView) findViewById(R.id.Place)).setText(place);
    }

    public void setActionAdd(String str) {
        addItem.setTitle(str);
    }

    public void setFinishVisible(boolean b){
        finishItem.setVisible(b);
    }

    public void callNotification() {
        NotificationManager notifiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Log.e("NotificationManager", "initial finished.");
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("客户拜访")
                .setContentText("你有一个拜访正在进行中...")
                .setSmallIcon(R.drawable.ic_launcher_godliu)
                .setAutoCancel(false)
                .setOngoing(true)
                .build();
        Log.e("Notification Build", " finished.");
        notifiManager.notify(0, notification);
        Log.e("Notification Notify", " finished.");
        isVisiting = true;
    }

    public boolean checkTaping() {
        if (isTaping) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("正在录音，\n是否停止录音？")
                    .setNegativeButton("否", null)
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tapeItem.setTitle("录音");
                            recorder.stop();// 停止刻录
                            recorder.release(); // 刻录完成一定要释放资源
                            visitContentFragment.addTape(name);
                            Toast.makeText(VisitMainActivity.this, "录音已停止。", Toast.LENGTH_SHORT).show();
                            isTaping = false;

                        }
                    })
                    .show();
        }
        if (player != null && player.isPlaying())
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("有一条录音正在播放，\n是否停止播放？")
                    .setNegativeButton("否", null)
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            player.stop();
                            player.release();
                            playItem.setTitle("播放");
                            VisitMainActivity.this.backFromContent();
                        }
                    })
                    .show();
        return (isTaping || (player != null && player.isPlaying()));
    }

    private void backFromContent() {
        if (checkTaping())
            return;

        for (String n : newTapeList) {
            File f = new File(Environment.getExternalStorageDirectory().getPath()
                    + File.separator + "SaleCircle" + File.separator + n);
            if (f.exists()) {
                f.delete();
            }
        }
        ft = fm.beginTransaction();
        ft.remove(visitContentFragment);
        ft.commit();
        fm.popBackStack();
        addItem.setTitle("添加临时拜访");
        finishItem.setVisible(false);
        this.setTitle("客户拜访");
        submitItem.setVisible(false);
        tapeItem.setVisible(false);
        playItem.setVisible(false);
        currentFg = 0;
    }

    private void backFromSaving() {
        ft = fm.beginTransaction();
        ft.remove(visitLoadingFragment);
        ft.commit();
        fm.popBackStack();
        fm.popBackStack();
        addItem.setVisible(true);
        addItem.setTitle("添加临时拜访");
        finishItem.setVisible(false);
        submitItem.setVisible(false);
        tapeItem.setVisible(false);
        playItem.setVisible(false);
        currentFg = 0;
    }

    private void backFromLoading() {
        //todo
        ft = fm.beginTransaction();
        ft.remove(visitLoadingFragment);
        ft.commit();
        fm.popBackStack();
        addItem.setVisible(true);
//        addItem.setTitle("添加临时拜访");
//        submitItem.setVisible(false);
//        tapeItem.setVisible(false);
//        playItem.setVisible(false);
        currentFg = 1;
    }


    private void backFromCensor() {
        ft = fm.beginTransaction();
        ft.remove(visitEvaluationFragment);
        ft.commit();
        fm.popBackStack();
        currentFg = 1;

        addItem.setVisible(true);
        submitItem.setVisible(evalLevel == 2);
        tapeItem.setVisible(tapeItemVisible);
        playItem.setVisible(playItemVisible);
    }

    public void setSubmitButtonVisible(boolean _subbtn) {
        submitItem.setVisible(_subbtn);
    }

    @Override
    public void onDestroy() {
        for (VisitJSONOperate json : visitJsonList) {
            json.disconnect();
        }
        for (ApprovalJSONOperate json : approvalJsonList)
            json.disconnect();
        super.onDestroy();
    }

    public void callLoading(int flag) {

        FragmentTransaction ft = fm.beginTransaction();
        visitLoadingFragment = new VisitLoadingFragment();
        ft.replace(R.id.content, visitLoadingFragment);
        if (flag == 2) {
            if (checkTaping())
                return;
            ft.addToBackStack("content");
        }

        ft.commit();

        currentFg = flag;
    }

    public void setEvaluation(int level, String[] Etapes, String detail) {
        evalLevel = level;
        evalTape = Etapes;
        evalDetail = detail;
        if (level == -1)
            return;
        if (level != visitContentFragment.getCondition()) {
            visitContentFragment.setCondition(level);
            visitJsonList.add(
                    new VisitJSONOperate()
                            .setAct(this)
                            .updateCondition(visitContentFragment.getVisitId(), UID, level));
        }
    }

    public void updateSQLCondition(int _id, int _cond) {
        dbw = visitdb.getWritableDatabase();
        dbw.execSQL("update visitTable set condition = " + _cond + " where id = " + _id);
        dbw.close();
    }

    public void scanCensorTape() {
        Log.e("Censor Tape", "Scanning. Level = " + evalLevel);
        if (evalLevel == -1)
            return;
//        setBeforeCallCensor();
        String dir = Environment.getExternalStorageDirectory().getPath()
                + File.separator
                + "SaleCircle"
                + File.separator
                + "VisitCensorTape";
        File f = new File(dir + File.separator + evalTape[0]);
        if (f.exists()) {
            callEvaluation();
        } else {
            Thread t = new Thread(new GodLiuCensorTapeDownloader(evalTape[0]).setAct(this));
            t.start();
            Log.e("Thread download", "start.");
        }
    }

    public void callEvaluation() {
        if (evalLevel == -1)
            return;

        Bundle bundle = new Bundle();
        bundle.putInt("level", evalLevel);
        bundle.putStringArray("tapes", evalTape);
        bundle.putString("detail", evalDetail);

        visitEvaluationFragment = new VisitEvaluationFragment();
        visitEvaluationFragment.setArguments(bundle);
        ft = fm.beginTransaction();
        ft.replace(R.id.content, visitEvaluationFragment);
        ft.addToBackStack("content");
        ft.commit();

        currentFg = 3;
    }

}
