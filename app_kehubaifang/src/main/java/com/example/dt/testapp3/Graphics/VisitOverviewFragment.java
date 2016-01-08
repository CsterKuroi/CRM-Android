package com.example.dt.testapp3.Graphics;


import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.dt.testapp3.R;
import com.example.dt.testapp3.VisitDataPackage.VisitDB;
import com.example.dt.testapp3.VisitNetworks.VisitJSONOperate;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * A simple {@link Fragment} subclass.
 */
public class VisitOverviewFragment extends Fragment {
    private ListView tovisitList;
    private ListView visitedList;
    private VisitDB visitdb;
    private SQLiteDatabase dbr;
    private TovisitBaseAdapter tovisitBaseAdapter;
    private SimpleAdapter visitedAdapter;
    private ArrayList<HashMap<String, String>> tovisitItems;
    private ArrayList<HashMap<String, String>> visitedItems;
    private int maxId;
    private String UID;
    public Handler handler;
    //    private int refreshId;
    private int selectorId;
    private int selector;

    private int tabIndex;

    private VisitMainActivity act;

    private ArrayList<VisitJSONOperate> visitJsonList;

    public VisitOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        visitJsonList = new ArrayList<>();
        tabIndex = 0;
        handler = new Handler() {
            public void handleMessage(Message msg) {
                refreshList();
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Object a = R;
        // Inflate the layout for this fragment
        final View layout = inflater.inflate(R.layout.fragment_visit_overview, container, false);

        act = (VisitMainActivity) getActivity();

        UID = getArguments().getString("uid");
        selectorId = getArguments().getInt("selectorId");
        selector = getArguments().getInt("selector");

        final TabHost tabs = (TabHost) layout.findViewById(R.id.overview_tabs);
        tabs.setup();
        tabs.addTab(tabs.newTabSpec("tab1").setIndicator("未提交拜访").setContent(R.id.tab1));
        TextView tabTitle1 = (TextView) tabs.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        tabTitle1.setTextSize(16);

        tabs.addTab(tabs.newTabSpec("tab2").setIndicator("已提交拜访").setContent(R.id.tab2));
        ((TextView) tabs.getTabWidget().getChildAt(1).findViewById(android.R.id.title)).setTextSize(16);

        tabs.setCurrentTab(tabIndex);

        tovisitList = (ListView) layout.findViewById(R.id.tovisit_list);
        visitedList = (ListView) layout.findViewById(R.id.visited_list);
        tovisitItems = new ArrayList<HashMap<String, String>>();
        visitedItems = new ArrayList<HashMap<String, String>>();


        this.refreshDatabase();

        //        ((VisitMainActivity) getActivity()).setMaxId(maxId);          //set max id
        //        tovisitAdapter = new SimpleAdapter(this.getActivity(),tovisitItems,R.layout.listview_visited_overview,
        //                new String[]{"date","company","target","overview","result"},
        //                new int[]{R.id.list_visited_item_date,R.id.list_item_company,R.id.list_visited_item_contact,R.id.list_visited_item_overview,R.id.list_visited_item_result});
        visitedAdapter = new SimpleAdapter(this.getActivity(), visitedItems, R.layout.listview_visited_overview,
                new String[]{"date", "company", "target", "overview", "result"},
                new int[]{R.id.list_visited_item_date, R.id.list_visited_item_company, R.id.list_visited_item_contact, R.id.list_visited_item_overview, R.id.list_visited_item_condition});
        tovisitBaseAdapter = new TovisitBaseAdapter(this.getActivity());

        tovisitList.setAdapter(tovisitBaseAdapter);
        visitedList.setAdapter(visitedAdapter);


        tovisitList.setOnItemClickListener(getListItemClickListener(tovisitItems));
        visitedList.setOnItemClickListener(getListItemClickListener(visitedItems));
        tovisitList.setOnItemLongClickListener(getListItemLongClickListener(tovisitList));
        visitedList.setOnItemLongClickListener(getListItemLongClickListener(visitedList));

        tovisitBaseAdapter.notifyDataSetChanged();
        visitedAdapter.notifyDataSetChanged();

        return layout;
    }

    @Override
    public void onStart() {
        act.setAddAndSaveText("添加临时拜访");
        act.setAddAndSaveVisible(true);
        super.onStart();
    }

    @Override
    public void onStop() {
        act.setAddAndSaveVisible(false);
        super.onStop();
    }

    public void refreshList() {
        refreshDatabase();
        Log.e("OverviewFragment", "Refreshing List");
        tovisitBaseAdapter.notifyDataSetChanged();
        visitedAdapter.notifyDataSetChanged();
    }

    private void refreshDatabase() {
        tovisitItems.clear();
        visitedItems.clear();
        //        ArrayList<HashMap<String, String>> tmpItems = tovisitItems;
        visitdb = new VisitDB(this.getActivity(), "USER" + UID, null, 1);
        dbr = visitdb.getReadableDatabase();
        Cursor visitCursor = dbr.query("visitTable", null, null, null, null, null, "date desc");
        //Log.d("kms","Cursor visitCursor = dbr.query(); ===== " + visitCursor.getCount());
        //final ArrayList<HashMap<String,String>> items = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;
        maxId = 0;
        int tmpid;
        Log.e("Cursor Count", String.valueOf(visitCursor.getCount()));
        for (int i = 0; i < visitCursor.getCount(); i++) {
            map = new HashMap<String, String>();
            //Log.d("kms","kkkkkkkkkkkkkkkkkkkkkkkk ==== " + i);
            visitCursor.moveToPosition(i);
            String company = visitCursor.getString(visitCursor.getColumnIndex("company"));
            int companyid = visitCursor.getInt(visitCursor.getColumnIndex("companyId"));
            String target = visitCursor.getString(visitCursor.getColumnIndex("target"));
            String targetId = visitCursor.getString(visitCursor.getColumnIndex("targetId"));
            int isTemp = visitCursor.getInt(visitCursor.getColumnIndex("isTemp"));
            map.put("date", visitCursor.getString(visitCursor.getColumnIndex("date")) + "  ");
            map.put("company", company.equals("") ? "拜访客户:无" : "拜访客户:" + company);
            map.put("target", target.equals("[]") ? "拜访对象:[无]" : "拜访对象:" + target);
            map.put("overview", visitCursor.getString(visitCursor.getColumnIndex("todo")));
            map.put("isTemp", String.valueOf(isTemp));
            map.put("condition", String.valueOf(visitCursor.getString(visitCursor.getColumnIndex("condition"))));
            String rst;
            boolean visitedFlag = true;
            int istempInt = visitCursor.getInt(visitCursor.getColumnIndex("condition"));
            //            Log.e("RefreshDatabase", "condition = " + istempInt);

            //todo change to 2 to recover
            visitedFlag = (istempInt >= 4);
            //            switch (visitCursor.getInt(visitCursor.getColumnIndex("result"))) {
            //                case 0:
            //                    rst = "等待拜访";
            //                    break;
            //                case 1:
            //                    rst = "未达成";
            //                    break;
            //                case 2:
            //                    rst = "基本达成";
            //                    break;
            //                case 3:
            //                    rst = "全部达成";
            //                    break;
            //                default:
            //                    rst = "超额达成";
            //            }
            switch (istempInt) {
                case 5:
                    rst = "评价：优秀";
                    break;
                case 6:
                    rst = "评价：良好";
                    break;
                case 7:
                    rst = "评价：合格";
                    break;
                case 8:
                    rst = "评价：较差";
                    break;
                default:
                    rst = "未评价";
            }
            map.put("result", rst);

            tmpid = visitCursor.getInt(visitCursor.getColumnIndex("id"));
            if (maxId < tmpid)
                maxId = tmpid;
            map.put("visitId", String.valueOf(tmpid));
            //selector
            int[] tmps = new int[0];
            switch (selectorId) {
                case 1:
                    tmps = new int[]{companyid};
                    break;
                case 2:
                    targetId = targetId.substring(1, targetId.length() - 1);
                    String[] st = targetId.split(", ");
                    if (!st[0].equals("")) {
                        tmps = new int[st.length];
                        for (int k = 0; k < st.length; k++) {
                            tmps[k] = Integer.parseInt(st[k]);
                        }
                    }
                    break;
            }
            if (selectorId == 0) {
                if (visitedFlag)
                    visitedItems.add(map);
                else
                    tovisitItems.add(0, map);
            }
            else {
                for (int s : tmps) {
                    Log.e("tmpsID ======== ", String.valueOf(s));
                    if (s == selector) {
                        if (visitedFlag)
                            visitedItems.add(map);
                        else
                            tovisitItems.add(0, map);
                    }
                }
            }

        }
        dbr.close();
    }


    public OnItemClickListener getListItemClickListener(final ArrayList<HashMap<String, String>> _tmpItems) {
        return new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //the same as button
                int VisitId = Integer.parseInt(_tmpItems.get(position).get("visitId"));
                int condi = Integer.parseInt(_tmpItems.get(position).get("condition"));
                if (condi >= 4) {
                    tabIndex = 1;
                }
                else {
                    tabIndex = 0;
                }
                VisitContentFragment detailFragment = new VisitContentFragment();
                Bundle nBundle = new Bundle();
                nBundle.putInt("id", VisitId);
                nBundle.putString("uid", UID);
                detailFragment.setArguments(nBundle);
                FragmentTransaction ft = act.getFragmentManager().beginTransaction();
                ft.replace(R.id.content, detailFragment);
                ft.addToBackStack(null);
                ft.commit();

                //a little different
                act.setAddAndSaveText("保存");
                //todo : change here
                act.setAddAndSaveVisible(false);
                act.setIntoContentFlag();
                act.setVisitContentFragment(detailFragment);

            }
        };
    }

    public AdapterView.OnItemLongClickListener getListItemLongClickListener(final ListView _tmpList) {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                //                final int pos = position;
                final int pos = position;
                final HashMap<String, String> map = (HashMap<String, String>) _tmpList.getItemAtPosition(pos);
                Log.e("Delete Info", "isTemp = " + map.get("isTemp"));
                int codi = Integer.parseInt(map.get("condition"));
                if (map.get("isTemp").equals("1") && codi < 4) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("提示")
                            .setMessage("确定要删除这条拜访记录吗？")
                            .setNegativeButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.e("position", String.valueOf(pos));
                                    Log.e("_tmplist", _tmpList.toString());

                                    Log.e("map is null?", (map == null ? "yes" : "no"));
                                    visitJsonList.add(
                                            new VisitJSONOperate(map)
                                                    .setAct(act)
                                                    .setFragment(VisitOverviewFragment.this)
                                                    .setUID(UID)
                                                    .delete());
                                    //items.remove(map);
                                    //adapter.notifyDataSetChanged();
                                }
                            })
                            .setPositiveButton("不删除", null).show();
                }
                else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("删除错误(((￣へ￣井)")
                            .setMessage("不能删除一条已经审批的记录。")
                            .setNegativeButton("是", null)
                            .setPositiveButton("确定", null)
                            .show();
                }

                return true;
            }

        };
    }

    private class TovisitBaseAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public TovisitBaseAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return tovisitItems.size();
        }

        @Override
        public Object getItem(int position) {
            return tovisitItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (holder == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.listview_tovisit_overview, null);
                holder.dateText = (TextView) convertView.findViewById(R.id.list_tovisit_item_date);
                holder.companyText = (TextView) convertView.findViewById(R.id.list_tovisit_item_company);
                holder.contactText = (TextView) convertView.findViewById(R.id.list_tovisit_item_contact);
                holder.overviewText = (TextView) convertView.findViewById(R.id.list_tovisit_item_overview);
                //todo : get rid of this annotation
                //                holder.startButton = (Button) convertView.findViewById(R.id.list_tovisit_item_button);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.dateText.setText(tovisitItems.get(position).get("date"));
            holder.companyText.setText(tovisitItems.get(position).get("company"));
            holder.contactText.setText(tovisitItems.get(position).get("target"));
            holder.overviewText.setText(tovisitItems.get(position).get("overview"));
//            todo : get rid of these annotation
//            final ViewHolder finalHolder = holder;
//            holder.startButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //Toast.makeText(VisitOverviewFragment.this.getActivity(), "Click事件已触发.", Toast.LENGTH_SHORT).show();
//
//                    String today = new StringBuilder().append(String.format("%d-%02d-%02d", Calendar.getInstance().get(Calendar.YEAR),
//                            Calendar.getInstance().get(Calendar.MONTH) + 1,
//                            Calendar.getInstance().get(Calendar.DATE))).toString();
//                    String tmpDate = tovisitItems.get(position).get("date").trim();
//                    if (today.compareTo(tmpDate) < 0) {
//                        Toast.makeText(act, "还没到拜访日期！", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    //here the same as OnItemClickListener
//                    Toast.makeText(act, "拜访已经开始。", Toast.LENGTH_SHORT).show();
//                    int VisitId = Integer.parseInt(tovisitItems.get(position).get("visitId"));
//                    VisitContentFragment detailFragment = new VisitContentFragment();
//                    Bundle nBundle = new Bundle();
//                    nBundle.putInt("id", VisitId);
//                    nBundle.putString("uid", UID);
//                    nBundle.putBoolean("start", true);
//                    detailFragment.setArguments(nBundle);
//                    FragmentTransaction ft = act.getFragmentManager().beginTransaction();
//                    ft.replace(R.id.content, detailFragment);
//                    ft.addToBackStack(null);
//                    ft.commit();
//                    //a little different
//                    act.setAddAndSaveText("保存");       /////////////
//                    act.setIntoContentFlag();
//                    act.setVisitContentFragment(detailFragment);
//
//                    act.setFinishVisible(true);
//                    finalHolder.startButton.setText("正在拜访");
//
//
//                    //todo : Notification
//                    act.callNotification();
//
//                }
//            });
            return convertView;
        }
    }

    private class ViewHolder {
        public TextView dateText;
        public TextView companyText;
        public TextView contactText;
        public TextView overviewText;
        public Button startButton;
    }

    @Override
    public void onDestroy() {
        for (VisitJSONOperate json : visitJsonList) {
            json.disconnect();
        }
        super.onDestroy();
    }
}
