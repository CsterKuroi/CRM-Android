package com.zhy.tree_view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ricky.database.CenterDatabase;
import com.zhy.bean.KLBean;
import com.zhy.tree.bean.KLNode;
import com.zhy.tree.bean.KLTreeListViewAdapter;
import com.zhy.tree.bean.KLTreeListViewAdapter.OnTreeNodeClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

//ATTENTION:为了区分公司id和人员id（防止重叠）,在代码里设置人员的id自动添加10000的人员id，还有这里的uid调试写死了，在使用时候注意更改
//使用者重写右上角的g出发的方法，在getData重写方法、
//目前使用测试数据来自uid（101），使用者getmyUID，然后sendreq方法中封装了发送给服务器的请求，使用测试服务器兰天url
public abstract class KLMainActivity extends Activity {
    public List<KLBean> mDatas = new ArrayList<>();
    public List<KLBean> mBack = new ArrayList<>();

    public ListView mTree;
    public KLTreeListViewAdapter mAdapter;
    public static WebSocketConnection mConnection;
    public boolean isConnected = false;
    //final String type3uri = ws://101.200.189.127:1234/ws
    private String type3uri = "ws://101.200.189.127:8001/ws";
    private String uid = "";
    public TextView tx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.kl_activity_main);

        CenterDatabase cd = new CenterDatabase(this, null);
        type3uri = CenterDatabase.URI;
        uid = cd.getUID();
        cd.close();

        tx = (TextView) findViewById(R.id.id_textview);

        RelativeLayout back = (RelativeLayout) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
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
        mConnection = new WebSocketConnection();
        connect();
//        try {
//            initDatas("");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        initsearchview();

    }

    /*-------------------------Connect with Cloud----------------------------------*/

    private void initsearchview() {
        SearchView mSearchView = (SearchView) findViewById(R.id.search);
        mSearchView.setIconifiedByDefault(true);
        mSearchView.onActionViewExpanded();// 写上此句后searchView初始是可以点击输入的状态，如果不写，那么就需要点击下放大镜，才能出现输入框
        mSearchView.setFocusable(false);// 是否获取焦点
        mSearchView.clearFocus();
        mSearchView.setOnQueryTextListener(new QueryListener());
    }

    private void connect() {
        try {
            mConnection.connect(type3uri, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    Toast.makeText(getApplicationContext(), "已连接", Toast.LENGTH_SHORT).show();
                    isConnected = true;
                    //发送请求
                    sendreq();
                }

                @Override
                public void onTextMessage(String payload) {

                    try {
                        initDatas(payload);
                        // Toast.makeText(getApplicationContext(),payload, Toast.LENGTH_SHORT).show();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onClose(int code, String reason) {
                    isConnected = false;
                    Toast.makeText(getApplicationContext(), "失去连接", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (WebSocketException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendreq() {

        if (mConnection == null || !mConnection.isConnected() || !isConnected)
            return;

        JSONObject request = new JSONObject();
        try {
            request.put("type", "3");
            request.put("cmd", "mylianxiren");
            request.put("uid", uid);
            mConnection.sendTextMessage(request.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    abstract public String getData();

//    public String getData()
//    {
//        List<KLNode> all =  mAdapter.getAllNode();
//        String returnstring = "";
//
//        for(KLNode n:all)
//        {
//            if (n.isChecked==true)
//                returnstring=returnstring+n.getName()+",";
//        }
//
//        return returnstring;
//    }


    //拿到用户传入的数据，转化为List<KLNode>以及设置Node间关系，然后根节点，从根往下遍历进行排序。
    void initDatas(String test) throws IOException, JSONException {
//        InputStreamReader isr = new InputStreamReader(getAssets().open("testnew.json"), "UTF-8");
//        BufferedReader br = new BufferedReader(isr);
//        String line;
//        StringBuilder builder = new StringBuilder();
//        while ((line = br.readLine()) != null) {
//            builder.append(line);
//        }
        String strUTF8 = URLDecoder.decode(test, "UTF-8");


        JSONObject root = new JSONObject(strUTF8);

        JSONArray userarraynew = root.getJSONArray("company");
        for (int k = 0; k < userarraynew.length(); k++) {
            JSONObject bumen = userarraynew.getJSONObject(k);
            mDatas.add(new KLBean(bumen.getInt("id"), 0, bumen.getString("name"),false));
            mBack.add(new KLBean(bumen.getInt("id"), 0, bumen.getString("name"),false));

        }

        JSONArray array2 = root.getJSONArray("person");
        for (int j = 0; j < array2.length(); j++) {
            JSONObject user = array2.getJSONObject(j);
            String[] ggg = user.getString("company").split(",");
            mDatas.add(new KLBean(Integer.parseInt(ggg[0]) + 10000, Integer.parseInt(ggg[1]), user.getString("name"),false));
            mBack.add(new KLBean(Integer.parseInt(ggg[0]) + 10000, Integer.parseInt(ggg[1]), user.getString("name"),false));

        }

        try {
            //simpletree adapter
            mTree = (ListView) findViewById(R.id.id_tree);
            mTree.setTextFilterEnabled(true);
            try {
                mAdapter = new KLSimpleTreeAdapter<>(tx, mTree, this, mDatas, 10);

            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }
            mAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener() {
                @Override
                public void onClick(KLNode node, int position) {

                    if (node.isLeaf()) {
                        Toast.makeText(getApplicationContext(), node.getName(),
                                Toast.LENGTH_SHORT).show();
                        //do Something if click
                    }
                }

            });
            mTree.setAdapter(mAdapter);
            mTree.setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    // TODO 自动生成的方法存根
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(mTree.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /*-------------------------innder calss of simpleTreeAdapter----------------------------------*/

    /*-------------------------Querylistener------------------------------------------------------*/
    private class QueryListener implements OnQueryTextListener {
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
                for (KLBean a : mBack)
                    mDatas.add(a);
            } else {
                // 过滤出新数据
                mDatas.clear();
                // Log.d("TTT", "mDaatas长度是" + );
                for (KLBean b : mBack) {
                    if (b.getLabel().contains(newText)) {
                        mDatas.add(b);
                        Log.d("TTT", "找到一个符合的部门" + b.getLabel());

                    }
                }
            }
            //mTree.setFilterText(newText); // 设置ListView的过滤关键词
            try {
                KLTreeListViewAdapter chageadapter = new KLSimpleTreeAdapter<>(tx, mTree, getApplicationContext(), mDatas, 10);
                mTree.setAdapter(chageadapter);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            //  mAdapter.notifyDataSetChanged();  // 通知数据发生了改变


            return false;
        }
    }
}