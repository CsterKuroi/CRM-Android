package com.zhyori.tree_view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.zhyori.bean.Bean;
import com.zhyori.tree.bean.Node;
import com.zhyori.tree.bean.TreeListViewAdapter;

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

/**
 * Created by SpongeBob_PC on 2015/8/2.
 */
public class MainFragment extends Fragment {
    private List<Bean> mDatas = new ArrayList<Bean>();
    //private List<FileBean> mDatas2 = new ArrayList<FileBean>();
    private ListView mTree;
    private TreeListViewAdapter mAdapter;
    public static WebSocketConnection mConnection;
    private boolean isConnected = false;
    //final String type3uri = ws://101.200.189.127:1234/ws
    final String type3uri = "ws://192.168.50.101:8001/ws";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mConnection = new WebSocketConnection();
        connect();
        return inflater.inflate(R.layout.fragment_main, container, false);
    }
    public void connect()
    {
        try {
            mConnection.connect(type3uri, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    Toast.makeText(getActivity().getApplicationContext(), "已经链接", Toast.LENGTH_SHORT).show();
                    isConnected = true;
                    //发送请求
                    sendreq();
                }

                @Override
                public void onTextMessage(String payload) {

                    try {
                        initDatas(payload);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onClose(int code, String reason) {
                    isConnected = false;
                   Toast.makeText(getActivity().getApplicationContext(), "失去连接", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (WebSocketException e) {
            Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void sendreq(){

        if(mConnection==null || !mConnection.isConnected() || !isConnected)
            return;

        JSONObject request = new JSONObject();
        try {
            request.put("type", "3");
            mConnection.sendTextMessage(request.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // mTree = (ListView) view.findViewById(R.id.id_tree);


    }

    //拿到用户传入的数据，转化为List<Node>以及设置Node间关系，然后根节点，从根往下遍历进行排序。
    private void initDatas(String builder) throws IOException, JSONException {
//		本地调试使用的代码
/*		InputStreamReader isr = new InputStreamReader(getAssets().open("test.json"), "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		String line;
		StringBuilder builder = new StringBuilder();
		while ((line = br.readLine()) != null) {
			builder.append(line);
		}*/
        //Toast.makeText(getActivity().getApplicationContext(), builder, Toast.LENGTH_SHORT).show();

        String strUTF8 = URLDecoder.decode(builder, "UTF-8");


        JSONObject root = new JSONObject(strUTF8);

        //得到所有部门的JASON对象保存在一个数组里，传过来的JASON中的部门结构是pri,pID
        JSONArray userarray = root.getJSONArray("dept");
        for (int i = 0; i < userarray.length(); i++) {
            JSONObject lan = userarray.getJSONObject(i);
            if (!lan.getString("pri").contains("-")) {
                //id是唯一标识符,0为根目录，name为名称
                mDatas.add(new Bean(lan.getInt("id"), 0, lan.getString("name")));
            } else {
                //子一级
                String[] forid = lan.getString("pri").split(",");
                mDatas.add(new Bean(lan.getInt("id"), Integer.parseInt(forid[1]), lan.getString("name")));

            }
        }

        JSONArray array = root.getJSONArray("user");
        for (int j = 0; j < array.length(); j++) {
            JSONObject user = array.getJSONObject(j);
            String []g = user.getString("udept").split(",");
            for(int m=0;m<g.length;m++)
            {
                mDatas.add(new Bean(user.getInt("id")+10000, Integer.parseInt(g[m].substring(0,g[m].indexOf('-'))), user.getString("name")));
            }

        }
        try {
            mTree = (ListView) getActivity().findViewById(R.id.id_tree);
            try {
                mAdapter = new SimpleTreeAdapter<Bean>(mTree, getActivity().getApplicationContext(), mDatas, 10);
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }
            mAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
                @Override
                public void onClick(Node node, int position) {
                    if (node.isLeaf()) {
                        Toast.makeText(getActivity().getApplicationContext(), node.getName(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

            });
            mTree.setAdapter(mAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
