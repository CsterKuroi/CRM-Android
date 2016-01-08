package com.bmj.tree_view;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bmj.bean.CommonContactBean;
import com.bmj.tree.bean.CommonContactNode;
import com.bmj.tree.bean.CommonContactTreeListViewAdapter;
import com.bmj.tree.bean.CommonContactTreeListViewAdapter.OnTreeNodeClickListener;
import com.ricky.database.CenterDatabase;

import java.util.ArrayList;
import java.util.List;

import de.tavendo.autobahn.WebSocketConnection;

public abstract class CommonContactActivity extends Activity {
    //ATTENTION:为了区别部门的id和人的id，人的id保存在Node的id中，我加了MagicNumber：10000，默认部门id<10000,人员的id=id+10000
    //使用mAdapter.getAllnode()得到所有信息
    public List<CommonContactBean> mDatas = new ArrayList<>();
    //private List<FileBean> mDatas2 = new ArrayList<FileBean>();
    private List<CommonContactBean> mBack = new ArrayList<>();
    public ListView mTree;
    public CommonContactTreeListViewAdapter mAdapter;
    public static WebSocketConnection mConnection;
    public boolean isConnected = false;
    //    private String type3uri = "ws://101.200.189.127:8001/ws";
    private String uid = "";
    //    final String type3uri = "ws://192.168.50.11:8000/ws";
    final static int COMMON_CONTANCT = 999;
    final static int ROOT_DIR = 0;
    public TextView tx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.commoncontact_activity_main);
        CenterDatabase cd = new CenterDatabase(this, null);
//        type3uri = CenterDatabase.URI;
        uid = cd.getUID();
        cd.close();
        tx = (TextView) findViewById(R.id.id_text);
        mTree = (ListView) findViewById(R.id.id_tree);

        RelativeLayout back = (RelativeLayout) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView OK = (TextView) findViewById(R.id.ok);
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        //websocket
        // mConnection = new WebSocketConnection();
        //connect();
        init_from_database();
        initsearchview();


        //拿到listview，传入mData，当中初始化了一个Adapter；


    }

    private void initsearchview() {
        SearchView mSearchView = (SearchView) findViewById(R.id.search);
        mSearchView.setIconifiedByDefault(true);
        mSearchView.onActionViewExpanded();// 写上此句后searchView初始是可以点击输入的状态，如果不写，那么就需要点击下放大镜，才能出现输入框
        mSearchView.setFocusable(false);// 是否获取焦点
        mSearchView.clearFocus();
        mSearchView.setOnQueryTextListener(new QueryListener());
    }

    private class QueryListener implements SearchView.OnQueryTextListener {
        // 当内容被提交时执行
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        // 当搜索框内内容发生改变时执行
        @Override
        public boolean onQueryTextChange(String newText) {
            if (TextUtils.isEmpty(newText)) {
                // templist = mBack;
                mDatas.clear();
                for (CommonContactBean a:mBack)
                {
                    mDatas.add(a);
                }
            } else {
                // 过滤出新数据
                mDatas.clear();
                // Log.d("TTT", "mDaatas长度是" + );
                for (CommonContactBean b : mBack) {
                    if (b.getLabel().contains(newText)) {
                        mDatas.add(b);
                        Log.d("TTT", "找到一个符合的部门" + b.getLabel());

                    }
                }
            }
            //mTree.setFilterText(newText); // 设置ListView的过滤关键词
            try {
                mAdapter= new CommonContactSimpleTreeAdapter<>(tx,mTree, getApplicationContext(), mDatas, 10);
                mTree.setAdapter(mAdapter);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            //  mAdapter.notifyDataSetChanged();  // 通知数据发生了改变


            return false;
        }
    }

    abstract public String getData();

//    public void connect() {
//        try {
//            mConnection.connect(type3uri, new WebSocketHandler() {
//                @Override
//                public void onOpen() {
//                    Toast.makeText(getApplicationContext(), "已连接", Toast.LENGTH_SHORT).show();
//                    isConnected = true;
//                    //发送请求
//                    sendreq();
//                }
//
//                @Override
//                public void onTextMessage(String payload) {
//                    try {
//                        //Toast.makeText(getApplicationContext(), payload, Toast.LENGTH_SHORT).show();
//                        initDatas(payload);
//                    } catch (JSONException | IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//                @Override
//                public void onClose(int code, String reason) {
//                    isConnected = false;
//                    Toast.makeText(getApplicationContext(), "失去连接", Toast.LENGTH_SHORT).show();
//                }
//            });
//        } catch (WebSocketException e) {
//            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
//        }
//    }

//    public void sendreq() {
//
//        if (mConnection == null || !mConnection.isConnected() || !isConnected)
//            return;
//
//        JSONObject request2 = new JSONObject();
//        try {
//            request2.put("type", "3");
//            request2.put("cmd", "zuzhijiagouchangyonglianxiren");
//            request2.put("time", "0");
//            request2.put("uid", uid);
//            mConnection.sendTextMessage(request2.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    //拿到用户传入的数据，转化为List<CommonContactNode>以及设置Node间关系，然后根节点，从根往下遍历进行排序。
//    private void initDatas(String test) throws IOException, JSONException {
///*
//		InputStreamReader isr = new InputStreamReader(getAssets().open("test.json"), "UTF-8");
//		BufferedReader br = new BufferedReader(isr);
//		String line;
//		StringBuilder builder = new StringBuilder();
//		while ((line = br.readLine()) != null) {
//			builder.append(line);
//		}*/
//        String strUTF8 = URLDecoder.decode(test, "UTF-8");
//
//
//        JSONObject root = new JSONObject(strUTF8);
//
//        //得到所有部门的JASON对象保存在一个数组里，传过来的JASON中的部门结构是pri,pID
//        mDatas.add(new CommonContactBean(COMMON_CONTANCT, ROOT_DIR, "常用联系人",false));
//        mBack.add(new CommonContactBean(COMMON_CONTANCT, ROOT_DIR, "常用联系人",false));
//
//        JSONArray usercomman = root.getJSONArray("changyonglianxiren");
//        for (int i = 0; i < usercomman.length(); i++) {
//            JSONObject lan = usercomman.getJSONObject(i);
//            //id是唯一标识符,0为根目录，name为名称
//            mDatas.add(new CommonContactBean(lan.getInt("uid") + 10000, COMMON_CONTANCT, lan.getString("name"),false));
//            mBack.add(new CommonContactBean(lan.getInt("uid") + 10000, COMMON_CONTANCT, lan.getString("name"),false));
//        }
//
//        JSONArray userarray = root.getJSONArray("dept");
//        for (int i = 0; i < userarray.length(); i++) {
//            JSONObject lan = userarray.getJSONObject(i);
//            if (!lan.getString("pri").contains("-")) {
//                //id是唯一标识符,0为根目录，name为名称
//                if (lan.getString("status").equals("1")) {
//                    mDatas.add(new CommonContactBean(lan.getInt("id"), ROOT_DIR, lan.getString("name"),false));
//                    mBack.add(new CommonContactBean(lan.getInt("id"), ROOT_DIR, lan.getString("name"),false));
//
//                }
//
//            } else {
//                //子一级
//                String[] forid = lan.getString("pri").split(",");
//                if (lan.getString("status").equals("1")) {
//                    mDatas.add(new CommonContactBean(lan.getInt("id"), Integer.parseInt(forid[1]), lan.getString("name"),false));
//                    mBack.add(new CommonContactBean(lan.getInt("id"), Integer.parseInt(forid[1]), lan.getString("name"),false));
//
//                }
//
//            }
//        }
//
//        JSONArray array = root.getJSONArray("user");
//        for (int j = 0; j < array.length(); j++) {
//            JSONObject user = array.getJSONObject(j);
//            String[] g = user.getString("udept").split(",");
//            for (int m = 0; m < g.length; m++) {
//                if (user.getString("status").equals("1")) {
//                    mDatas.add(new CommonContactBean(user.getInt("uid") + 10000, Integer.parseInt(g[m].substring(0, g[m].indexOf('-'))), user.getString("name"), false));
//                    mBack.add(new CommonContactBean(user.getInt("uid") + 10000, Integer.parseInt(g[m].substring(0, g[m].indexOf('-'))), user.getString("name"), false));
//                }
//            }
//
//        }
//
//        try {
//            //simpletree adapter
//            try {
//                mAdapter = new CommonContactSimpleTreeAdapter<>(tx, mTree, this, mDatas, 15);
//            } catch (IllegalAccessException e1) {
//                e1.printStackTrace();
//            }
//            mAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener() {
//                @Override
//                public void onClick(CommonContactNode node, int position) {
//
//                    if (node.isLeaf()) {
//                        Toast.makeText(getApplicationContext(), node.getName(),
//                                Toast.LENGTH_SHORT).show();
//                        //do Something if click
//                    }
//                }
//
//            });
//            mTree.setAdapter(mAdapter);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void init_from_database()
    {
        mDatas.clear();
        mBack.clear();
        CenterDatabase cd = new CenterDatabase(this, null);
        SQLiteDatabase db = cd.getWritableDatabase();
        Cursor cursor = db.rawQuery("select id, name, pri from " + CenterDatabase.DEPT + " where status = ?", new String[]{"1"});
        if (cursor.moveToFirst()) {
            do {
                String pri = cursor.getString(2);
                if (!pri.contains("-")) {
                    //id是唯一标识符,0为根目录，name为名称
                    mDatas.add(new CommonContactBean(cursor.getInt(0), 0, cursor.getString(1),false));
                    mBack.add(new CommonContactBean(cursor.getInt(0), 0, cursor.getString(1),false));
                } else {
                    //子一级
                    String[] forid = pri.split(",");
                    mDatas.add(new CommonContactBean(cursor.getInt(0), Integer.parseInt(forid[1]), cursor.getString(1),false));
                    mBack.add(new CommonContactBean(cursor.getInt(0), Integer.parseInt(forid[1]), cursor.getString(1),false));

                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        cursor = db.rawQuery("select uid , name, udept, utouxiang from " + CenterDatabase.USER + " where status = ?", new String[]{"1"});
        if (cursor.moveToFirst()) {
            do {
                String[] g = cursor.getString(2).split(",");
                for (int m = 0; m < g.length; m++) {
                    mDatas.add(new CommonContactBean(cursor.getInt(0) + 10000, Integer.parseInt(g[m].substring(0, g[m].indexOf('-'))), cursor.getString(1),false));
                    mBack.add(new CommonContactBean(cursor.getInt(0) + 10000, Integer.parseInt(g[m].substring(0, g[m].indexOf('-'))), cursor.getString(1),false));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        cd.close();
        try {
            //simpletree adapter
            try {
                mAdapter = new CommonContactSimpleTreeAdapter<>(tx, mTree, this, mDatas, 15);
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }
            mAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener() {
                @Override
                public void onClick(CommonContactNode node, int position) {

                    if (node.isLeaf()) {
                        Toast.makeText(getApplicationContext(), node.getName(),
                                Toast.LENGTH_SHORT).show();
                        //do Something if click
                    }
                }

            });
            mTree.setAdapter(mAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}