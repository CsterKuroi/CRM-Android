package com.mogujie.tt.ui.activity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.yanhao.task729.yh729_Constant;
import com.melnykov.fab.sample.tools.crmMyDatabaseHelper;
import com.mogujie.tt.DB.DBInterface;
import com.mogujie.tt.DB.entity.UserEntity;
import com.mogujie.tt.R;
import com.mogujie.tt.config.IntentConstant;
import com.mogujie.tt.imservice.event.LoginEvent;
import com.mogujie.tt.imservice.event.UnreadEvent;
import com.mogujie.tt.imservice.service.IMService;
import com.mogujie.tt.imservice.support.IMServiceConnector;
import com.mogujie.tt.ui.fragment.ChatFragment;
import com.mogujie.tt.ui.fragment.ContactFragment;
import com.mogujie.tt.ui.widget.NaviTabButton;
import com.mogujie.tt.utils.Logger;
import com.ricky.database.CenterDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import yh729_ServiceAndThread.yh729_AlarmNotificationService;


public class MainActivity extends FragmentActivity {
    private Fragment[] mFragments;
    private ContactFragment cttFragment;
    private boolean ctt = false;
    private NaviTabButton[] mTabButtons;
    private Logger logger = Logger.getLogger(MainActivity.class);
    private IMService imService;

    private IMServiceConnector imServiceConnector = new IMServiceConnector() {
        @Override
        public void onIMServiceConnected() {
            imService = imServiceConnector.getIMService();
//            if (realLogin) {
//                UserEntity loginContact = imService.getLoginManager().getLoginInfo();
//                CenterDatabase centerDatabase = new CenterDatabase(MainActivity.this, null);
//                centerDatabase.putID(0, loginContact.getRealName());
//                centerDatabase.close();
//            }
        }

        @Override
        public void onServiceDisconnected() {
        }
    };
    private boolean realLogin = true;
    private ViewFlipper chatCttShift;
    public boolean dontRefresh = false;
    public boolean needRefresh = false;
    private String wsurl = CenterDatabase.URI;
    private WebSocketHandler wsHandler;
    private WebSocketConnection mConnection;
    private CenterDatabase dbOpenHelper;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logger.d("MainActivity#savedInstanceState:%s", savedInstanceState);
        //todo eric when crash, this will be called, why?
        if (savedInstanceState != null) {
            logger.w("MainActivity#crashed and restarted, just exit");
            jumpToLoginPage();
            finish();
        }

        // 在这个地方加可能会有问题吧
        EventBus.getDefault().register(this);

        realLogin = getIntent().getBooleanExtra("login", true);
        imServiceConnector.connect(this);

        //检查提醒服务
        initRemindService();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tt_activity_main);
        chatCttShift = (ViewFlipper) findViewById(R.id.chat_ctt_shift);
        initTab();
        initFragment();
        setFragmentIndicator(0);

        dbOpenHelper = new CenterDatabase(this, null);
        uid = dbOpenHelper.getUID();
        wsHandler = new WebSocketHandler() {
            @Override
            public void onOpen() {
//                Toast.makeText(MainActivity.this, "建立连接", Toast.LENGTH_SHORT).show();
                sendWebSocketMessage(0);
            }

            @Override
            public void onTextMessage(String payload) {
                try {
                    String strUTF8 = URLDecoder.decode(payload, "UTF-8");
                    JSONObject root = new JSONObject(strUTF8);
                    if (root.getString("cmd").equals("zuzhijiagou")) {
                        if (updateDatabase(root)) {
                            if (dontRefresh) {
                                needRefresh = true;
                            } else {
                                cttFragment.renderTreeDeptList();
//                                Toast.makeText(MainActivity.this, "通讯录已更新", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (cttFragment.commonList == null) {
                            JSONObject request = new JSONObject();
                            try {
                                request.put("type", "3");
                                request.put("cmd", "changyonglianxiren");
                                request.put("uid", uid);
                                mConnection.sendTextMessage(request.toString());
                            } catch (JSONException e) {
                                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (root.getString("cmd").equals("changyonglianxiren")) {
                        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
                        JSONArray userarray = root.getJSONArray("result");
                        cttFragment.commonList = new ArrayList<>();
                        for (int i = 0; i < userarray.length(); i++) {
                            String uid = userarray.getJSONObject(i).getString("uid");
                            Cursor cursor = db.rawQuery("select id from " + CenterDatabase.USER + " where uid = ?", new String[]{uid});
                            if (cursor.moveToFirst()) {
                                UserEntity ue = DBInterface.instance().getByLoginId(cursor.getInt(0));
                                ue.getPinyinElement().pinyin = "常用联系人";
                                cttFragment.commonList.add(ue);
                            } else {
//                                Toast.makeText(MainActivity.this, "常用联系人获取失败", Toast.LENGTH_SHORT).show();
                            }
                            cursor.close();
                        }
                        if (!cttFragment.commonList.isEmpty()) {
                            List<UserEntity> all = new ArrayList<>();
                            all.addAll(cttFragment.commonList);
                            if (cttFragment.contactList != null) {
                                all.addAll(cttFragment.contactList);
                            }
                            cttFragment.contactAdapter.putUserList(all);
                        }
                        db.close();
                    } else if (root.getString("cmd").equals("updatecrm")) {
                        updateCRM(root);
                    }
                } catch (UnsupportedEncodingException | JSONException e) {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onClose(int code, String reason) {
//                Toast.makeText(MainActivity.this, "失去连接", Toast.LENGTH_SHORT).show();
            }
        };
        new Thread() {
            private final int gap = 1000 * 60;
            private int count = 0;

            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        sendWebSocketMessage(count);
                        count = (count + 1) % 5;
                        sleep(gap);
                    } catch (InterruptedException e) {
                        interrupt();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        if (ctt) {
            setFragmentIndicator(101);
            return;
        }
        //don't let it exit
        //super.onBackPressed();

        //nonRoot	If false then this only works if the activity is the root of a task; if true it will work for any activity in a task.
        //document http://developer.android.com/reference/android/app/Activity.html

        //moveTaskToBack(true);

        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);

    }


    private void initFragment() {
        mFragments = new Fragment[5];
        mFragments[0] = getSupportFragmentManager().findFragmentById(R.id.fragment_chat);
        mFragments[1] = getSupportFragmentManager().findFragmentById(R.id.fragment_work);
        mFragments[2] = getSupportFragmentManager().findFragmentById(R.id.fragment_crm);
        mFragments[3] = getSupportFragmentManager().findFragmentById(R.id.fragment_internal);
        mFragments[4] = getSupportFragmentManager().findFragmentById(R.id.fragment_my);
        cttFragment = (ContactFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_contact);
    }

    private void initTab() {
        mTabButtons = new NaviTabButton[5];

        mTabButtons[0] = (NaviTabButton) findViewById(R.id.tabbutton_chat);
        mTabButtons[1] = (NaviTabButton) findViewById(R.id.tabbutton_work);
        mTabButtons[2] = (NaviTabButton) findViewById(R.id.tabbutton_crm);
        mTabButtons[3] = (NaviTabButton) findViewById(R.id.tabbutton_app);
        mTabButtons[4] = (NaviTabButton) findViewById(R.id.tabbutton_my);

        mTabButtons[0].setTitle(getString(R.string.main_msg));
        mTabButtons[0].setIndex(0);
        mTabButtons[0].setSelectedImage(getResources().getDrawable(R.drawable.tt_tab_chat_sel));
        mTabButtons[0].setUnselectedImage(getResources().getDrawable(R.drawable.tt_tab_chat_nor));

        mTabButtons[1].setTitle(getString(R.string.main_work));
        mTabButtons[1].setIndex(1);
        mTabButtons[1].setSelectedImage(getResources().getDrawable(R.drawable.tt_tab_work_sel));
        mTabButtons[1].setUnselectedImage(getResources().getDrawable(R.drawable.tt_tab_work_nor));

        mTabButtons[2].setTitle(getString(R.string.main_crm));
        mTabButtons[2].setIndex(2);
        mTabButtons[2].setSelectedImage(getResources().getDrawable(R.drawable.tt_tab_crm_sel));
        mTabButtons[2].setUnselectedImage(getResources().getDrawable(R.drawable.tt_tab_crm_nor));

        mTabButtons[3].setTitle(getString(R.string.main_app));
        mTabButtons[3].setIndex(3);
        mTabButtons[3].setSelectedImage(getResources().getDrawable(R.drawable.tt_tab_app_sel));
        mTabButtons[3].setUnselectedImage(getResources().getDrawable(R.drawable.tt_tab_app_nor));

        mTabButtons[4].setTitle(getString(R.string.main_me_tab));
        mTabButtons[4].setIndex(4);
        mTabButtons[4].setSelectedImage(getResources().getDrawable(R.drawable.tt_tab_me_sel));
        mTabButtons[4].setUnselectedImage(getResources().getDrawable(R.drawable.tt_tab_me_nor));
    }

    public void setFragmentIndicator(int which) {
        if (which == 100) {
            dontRefresh = true;
            ctt = true;
            chatCttShift.setInAnimation(this, R.anim.my_rightleft_in);
            chatCttShift.setOutAnimation(this, R.anim.my_rightleft_out);
            chatCttShift.showNext();
        } else if (which == 101) {
            dontRefresh = false;
            if (needRefresh) {
                needRefresh = false;
                cttFragment.renderTreeDeptList();
            }
            ctt = false;
            chatCttShift.setInAnimation(this, R.anim.my_leftright_in);
            chatCttShift.setOutAnimation(this, R.anim.my_leftright_out);
            chatCttShift.showNext();
        } else if (which == 0) {
            dontRefresh = false;
            if (needRefresh) {
                needRefresh = false;
                cttFragment.renderTreeDeptList();
            }
            getSupportFragmentManager().beginTransaction().hide(mFragments[1]).hide(mFragments[2]).hide(mFragments[3]).hide(mFragments[4]).commit();
            chatCttShift.setVisibility(View.VISIBLE);
            selTabButton(0);
            if (ctt) {
                ctt = false;
                chatCttShift.setInAnimation(this, R.anim.my_leftright_in);
                chatCttShift.setOutAnimation(this, R.anim.my_leftright_out);
                chatCttShift.showNext();
            }
        } else {
            dontRefresh = false;
            if (needRefresh) {
                needRefresh = false;
                cttFragment.renderTreeDeptList();
            }
            getSupportFragmentManager().beginTransaction().hide(mFragments[1]).hide(mFragments[2]).hide(mFragments[3]).hide(mFragments[4]).show(mFragments[which]).commit();
            chatCttShift.setVisibility(View.GONE);
            selTabButton(which);
            if (ctt) {
                ctt = false;
                chatCttShift.setInAnimation(null);
                chatCttShift.setOutAnimation(null);
                chatCttShift.showNext();
            }
        }
    }

    private void selTabButton(int which) {
        mTabButtons[0].setSelectedButton(false);
        mTabButtons[1].setSelectedButton(false);
        mTabButtons[2].setSelectedButton(false);
        mTabButtons[3].setSelectedButton(false);
        mTabButtons[4].setSelectedButton(false);
        mTabButtons[which].setSelectedButton(true);
    }

    public void setUnreadMessageCnt(int unreadCnt) {
        mTabButtons[0].setUnreadNotify(unreadCnt);
    }


    /**
     * 双击事件
     */
    public void chatDoubleListener() {
        setFragmentIndicator(0);
//        ((ChatFragment) mFragments[0]).scrollToUnreadPosition();
        ChatFragment tmp = (ChatFragment) mFragments[0];
        if (tmp.myCurrentView == 0) {
            tmp.pressChat();
        } else if (tmp.myCurrentView == 1) {
            tmp.pressRemind();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleLocateDepratment(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        new crmMyDatabaseHelper(this, "customer.db3", 1);
    }

    private void handleLocateDepratment(Intent intent) {
        int departmentIdToLocate = intent.getIntExtra(IntentConstant.KEY_LOCATE_DEPARTMENT, -1);
        if (departmentIdToLocate == -1) {
            return;
        }

        logger.d("department#got department to locate id:%d", departmentIdToLocate);
//        setFragmentIndicator(100);
        ContactFragment fragment = cttFragment;
        if (fragment == null) {
            logger.e("department#fragment is null");
            return;
        }
        fragment.locateDepartment(departmentIdToLocate);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        logger.d("mainactivity#onDestroy");
        EventBus.getDefault().unregister(this);
        imServiceConnector.disconnect(this);
        if (mConnection != null && mConnection.isConnected()) {
            mConnection.disconnect();
        }
        super.onDestroy();
    }


    public void onEventMainThread(UnreadEvent event) {
        switch (event.event) {
            case SESSION_READED_UNREAD_MSG:
            case UNREAD_MSG_LIST_OK:
            case UNREAD_MSG_RECEIVED:
                showUnreadMessageCount();
                break;
        }
    }

    private void showUnreadMessageCount() {
        //todo eric when to
        if (imService != null) {
            int unreadNum = imService.getUnReadMsgManager().getTotalUnreadCount();
            mTabButtons[0].setUnreadNotify(unreadNum);
        }

    }

    public void onEventMainThread(LoginEvent event) {
        switch (event) {
            case LOGIN_OUT:
                handleOnLogout();
                break;
        }
    }

    private void handleOnLogout() {
        logger.d("mainactivity#login#handleOnLogout");
        finish();
        logger.d("mainactivity#login#kill self, and start login activity");
        jumpToLoginPage();

    }

    private void jumpToLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(IntentConstant.KEY_LOGIN_NOT_AUTO, true);
        startActivity(intent);
    }

    /*----------------------------------检查提醒服务--------------------------------------------*/

    public void initRemindService() {
        Log.i("test", "check service");
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : serviceList) {
            ComponentName serviceName = runningServiceInfo.service;
            if ((serviceName.getPackageName().equals("com.mogujie.tt"))
                    && (serviceName.getClassName().equals("yh729_ServiceAndThread"
                    + ".yh729_AlarmNotificationService"))) {
                Log.i("test", "service exist");
                return;
            }
        }
        Intent intent = new Intent(getApplicationContext(), yh729_AlarmNotificationService.class);
        intent.setAction(yh729_Constant.NOTIFY_SERVICE_FLAG);
        startService(intent);
        Log.i("test", "start service");
    }

    private void sendWebSocketMessage(int count) {
        try {
            if (!mConnection.isConnected()) {
                try {
                    mConnection.connect(wsurl, wsHandler);
                } catch (WebSocketException wse) {
                    Toast.makeText(this, wse.toString(), Toast.LENGTH_SHORT).show();
                }
            }
            SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
            String time = "0";
            if (count == 0) {
                Cursor cursor = db.rawQuery("select time from " + CenterDatabase.TIME, null);
                if (cursor.moveToLast()) {
                    time = cursor.getString(0);
                }
                JSONObject request = new JSONObject();
                request.put("type", "3");
                request.put("cmd", "zuzhijiagou");
                request.put("time", time);
                mConnection.sendTextMessage(request.toString());
                cursor.close();
            }
            time = "0";
            Cursor cursor = db.rawQuery("select time from " + CenterDatabase.TIME2, null);
            if (cursor.moveToLast()) {
                time = cursor.getString(0);
            }
            JSONObject request = new JSONObject();
            request.put("type", "2");
            request.put("cmd", "updatecrm");
            request.put("uid", uid);
            request.put("updatetime", time);
            mConnection.sendTextMessage(request.toString());
            cursor.close();
            db.close();
        } catch (JSONException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            if (mConnection == null) {
                mConnection = new WebSocketConnection();
                try {
                    mConnection.connect(wsurl, wsHandler);
                } catch (WebSocketException wse) {
                    Toast.makeText(this, wse.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void updateCRM(JSONObject root) {
        try {
            String error = root.getString("error");
            if (error.equals("3")) {
                return;
            } else if (error.equals("1")) {
                SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
                JSONArray customers = root.getJSONArray("customers");
                for (int i = 0; i < customers.length(); i++) {
                    JSONObject kehu = customers.getJSONObject(i);
                    int id = kehu.getInt("id");
                    ContentValues values = new ContentValues();
                    values.put("id", id);
                    values.put("uid", kehu.getString("uid"));
                    values.put("uname", kehu.getString("uname"));
                    values.put("userphone", kehu.getString("userphone"));
                    values.put("useremail", kehu.getString("useremail"));
                    values.put("userfox", kehu.getString("userfox"));
                    values.put("useraddress", kehu.getString("useraddress"));
                    values.put("leixing", kehu.getString("leixing"));
                    values.put("xingzhi", kehu.getString("xingzhi"));
                    values.put("guimo", kehu.getString("guimo"));
                    values.put("userbeizhu", kehu.getString("userbeizhu"));
                    values.put("kehustate", kehu.getString("kehustate"));
                    values.put("kehurank", kehu.getString("kehurank"));
                    values.put("status", kehu.getString("status"));
                    Cursor cursor = db.rawQuery("select id from " + CenterDatabase.KEHU + " where id = " + id, null);
                    if (cursor.moveToFirst()) {
                        db.update(CenterDatabase.KEHU, values, "id = " + id, null);
                    } else {
                        db.insert(CenterDatabase.KEHU, null, values);
                    }
                    cursor.close();
                }
                JSONArray contacters = root.getJSONArray("contacters");
                for (int j = 0; j < contacters.length(); j++) {
                    JSONObject lianxiren = contacters.getJSONObject(j);
                    int id = lianxiren.getInt("id");
                    ContentValues values = new ContentValues();
                    values.put("id", id);
                    values.put("uid", lianxiren.getString("uid"));
                    values.put("customer", lianxiren.getString("customer"));
                    values.put("username", lianxiren.getString("username"));
                    values.put("strsex", lianxiren.getString("strsex"));
                    values.put("workphone", lianxiren.getString("workphone"));
                    values.put("yidongphone", lianxiren.getString("yidongphone"));
                    values.put("strqq", lianxiren.getString("strqq"));
                    values.put("strweixin", lianxiren.getString("strweixin"));
                    values.put("strinterest", lianxiren.getString("strinterest"));
                    values.put("strgrowth", lianxiren.getString("strgrowth"));
                    values.put("strpaixi", lianxiren.getString("strpaixi"));
                    values.put("address", lianxiren.getString("address"));
                    values.put("degree", lianxiren.getString("degree"));
                    values.put("status", lianxiren.getString("status"));
                    values.put("pic", lianxiren.getString("pic"));
                    values.put("email", lianxiren.getString("email"));
                    values.put("relation", lianxiren.getString("relation"));
                    Cursor cursor = db.rawQuery("select id from " + CenterDatabase.LIANXIREN + " where id = " + id, null);
                    if (cursor.moveToFirst()) {
                        db.update(CenterDatabase.LIANXIREN, values, "id = " + id, null);
                    } else {
                        db.insert(CenterDatabase.LIANXIREN, null, values);
                    }
                    cursor.close();
                }
                String time = root.getString("servertime");
                ContentValues values = new ContentValues();
                values.put("time", time);
                db.insert(CenterDatabase.TIME2, null, values);
                db.close();
            }
        } catch (JSONException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    //拿到用户传入的数据，转化为List<Node>以及设置Node间关系，然后根节点，从根往下遍历进行排序。
    private boolean updateDatabase(JSONObject root) {
        boolean changed = false;
        try {
            SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
            JSONArray userarray = root.getJSONArray("dept");
            for (int i = 0; i < userarray.length(); i++) {
                JSONObject lan = userarray.getJSONObject(i);
                int id = lan.getInt("id");
                ContentValues values = new ContentValues();
                values.put("id", id);
                values.put("name", lan.getString("name"));
                values.put("pri", lan.getString("pri"));
                values.put("manager", lan.getString("manager"));
                values.put("status", lan.getString("status"));
                Cursor cursor = db.rawQuery("select id from " + CenterDatabase.DEPT + " where id = " + id, null);
                if (cursor.moveToFirst()) {
                    db.update(CenterDatabase.DEPT, values, "id = " + id, null);
                } else {
                    db.insert(CenterDatabase.DEPT, null, values);
                }
                cursor.close();
                changed = true;
            }
            JSONArray array = root.getJSONArray("user");
            for (int j = 0; j < array.length(); j++) {
                JSONObject user = array.getJSONObject(j);
                int id = user.getInt("id");
                ContentValues values = new ContentValues();
                values.put("id", id);
                values.put("uid", user.getString("uid"));
                values.put("name", user.getString("name"));
                values.put("utouxiang", user.getString("touxiang"));
                values.put("udept", user.getString("udept"));
                values.put("status", user.getString("status"));
                Cursor cursor = db.rawQuery("select id from " + CenterDatabase.USER + " where id = " + id, null);
                if (cursor.moveToFirst()) {
                    db.update(CenterDatabase.USER, values, "id = " + id, null);
                } else {
                    db.insert(CenterDatabase.USER, null, values);
                }
                cursor.close();
                changed = true;
            }
            if (changed) {
                String time = root.getString("time");
                ContentValues values = new ContentValues();
                values.put("time", time);
                db.insert(CenterDatabase.TIME, null, values);
            }
            db.close();
        } catch (JSONException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return changed;
    }
}
