package com.mogujie.tt.ui.fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.mogujie.tt.DB.entity.GroupEntity;
import com.mogujie.tt.DB.entity.UserEntity;
import com.mogujie.tt.R;
import com.mogujie.tt.config.HandlerConstant;
import com.mogujie.tt.imservice.event.GroupEvent;
import com.mogujie.tt.imservice.event.UserInfoEvent;
import com.mogujie.tt.imservice.manager.IMContactManager;
import com.mogujie.tt.imservice.service.IMService;
import com.mogujie.tt.imservice.support.IMServiceConnector;
import com.mogujie.tt.ui.activity.MainActivity;
import com.mogujie.tt.ui.adapter.ContactAdapter;
import com.mogujie.tt.ui.widget.SortSideBar;
import com.mogujie.tt.ui.widget.SortSideBar.OnTouchingLetterChangedListener;
import com.mogujie.tt.utils.IMUIHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.ricky.database.CenterDatabase;
import com.zhyori.bean.Bean;
import com.zhyori.tree.bean.Node;
import com.zhyori.tree.bean.TreeListViewAdapter;
import com.zhyori.tree_view.SimpleTreeAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 通讯录 （全部、部门）
 */
public class ContactFragment extends MainFragment implements OnTouchingLetterChangedListener {
    private View curView = null;
    private static Handler uiHandler = null;
    private ListView allContactListView;
    //    private ListView departmentContactListView;
    private SortSideBar sortSideBar;
    private TextView dialog;

    public ContactAdapter contactAdapter;
//    private DeptAdapter departmentAdapter;

    private IMService imService;
    private IMContactManager contactMgr;
    private int curTabIndex = 0;

    private ListView mTreeListView;
    private List<Bean> mDatas = new ArrayList<Bean>();
    private List<Bean> mDatasBackUp = new ArrayList<Bean>();
    private TreeListViewAdapter mAdapter;
    private SearchView mSearchView;
    private FrameLayout mOldSearchView;
    public List<UserEntity> commonList = null;
    public List<UserEntity> contactList = null;

    private IMServiceConnector imServiceConnector = new IMServiceConnector() {
        @Override
        public void onIMServiceConnected() {
            logger.d("contactUI#onIMServiceConnected");

            imService = imServiceConnector.getIMService();
            if (imService == null) {
                logger.e("ContactFragment#onIMServiceConnected# imservice is null!!");
                return;
            }
            contactMgr = imService.getContactManager();

            // 初始化视图
            initAdapter();
            renderEntityList();
            EventBus.getDefault().registerSticky(ContactFragment.this);
        }

        @Override
        public void onServiceDisconnected() {
            if (EventBus.getDefault().isRegistered(ContactFragment.this)) {
                EventBus.getDefault().unregister(ContactFragment.this);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imServiceConnector.connect(getActivity());
        RelativeLayout topLeftBack = (RelativeLayout) topContentView.findViewById(R.id.contact_top_left_container);
        topLeftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).setFragmentIndicator(101);
            }
        });
        initHandler();
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void initHandler() {
        uiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HandlerConstant.HANDLER_CHANGE_CONTACT_TAB:
                        if (null != msg.obj) {
                            curTabIndex = (Integer) msg.obj;
                            MainActivity ac = (MainActivity) getActivity();
                            if (0 == curTabIndex) {
                                allContactListView.setVisibility(View.VISIBLE);
//                                departmentContactListView.setVisibility(View.GONE);
                                mTreeListView.setVisibility(View.GONE);
                                sortSideBar.setVisibility(View.VISIBLE);
                                mSearchView.setVisibility(View.GONE);
                                mOldSearchView.setVisibility(View.VISIBLE);
                                if (ac != null) {
                                    ac.dontRefresh = false;
                                    if (ac.needRefresh) {
                                        ac.needRefresh = false;
                                        renderTreeDeptList();
                                    }
                                }
                            } else {
                                allContactListView.setVisibility(View.GONE);
//                                departmentContactListView.setVisibility(View.GONE);
                                mTreeListView.setVisibility(View.VISIBLE);
                                sortSideBar.setVisibility(View.GONE);
                                mSearchView.setVisibility(View.VISIBLE);
                                mOldSearchView.setVisibility(View.GONE);
                                ac.dontRefresh = true;
                            }
                        }
                        break;
                }
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(ContactFragment.this)) {
            EventBus.getDefault().unregister(ContactFragment.this);
        }
        imServiceConnector.disconnect(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (null != curView) {
            ((ViewGroup) curView.getParent()).removeView(curView);
            return curView;
        }
        curView = inflater.inflate(R.layout.tt_fragment_contact, topContentView);
        initRes();
        initAdapterDept();
        renderTreeDeptList();
        mSearchView = (SearchView) curView.findViewById(R.id.search_tree);
        mSearchView.setIconifiedByDefault(true);
        mSearchView.onActionViewExpanded();
        mSearchView.setFocusable(false);
        mSearchView.clearFocus();
        mSearchView.setOnQueryTextListener(new QueryListener());
        mOldSearchView = (FrameLayout) curView.findViewById(R.id.searchbar);
        return curView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * @Description 初始化界面资源
     */
    private void initRes() {
        // 设置顶部标题栏
        showContactTopBar();
        hideTopBar();

        super.init(curView);
        showProgressBar();

        sortSideBar = (SortSideBar) curView.findViewById(R.id.sidebar);
        dialog = (TextView) curView.findViewById(R.id.dialog);
        sortSideBar.setTextView(dialog);
        sortSideBar.setOnTouchingLetterChangedListener(this);

        allContactListView = (ListView) curView.findViewById(R.id.all_contact_list);
//        departmentContactListView = (ListView) curView.findViewById(R.id.department_contact_list);
        mTreeListView = (ListView) curView.findViewById(R.id.tree_list);

        //this is critical, disable loading when finger sliding, otherwise you'll find sliding is not very smooth
        allContactListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
//        departmentContactListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        mTreeListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        // todo eric
        // showLoadingProgressBar(true);
    }

    private void initAdapter() {
        contactAdapter = new ContactAdapter(getActivity(), imService);
//        departmentAdapter = new DeptAdapter(getActivity(), imService);
        allContactListView.setAdapter(contactAdapter);
//        departmentContactListView.setAdapter(departmentAdapter);

        // 单击视图事件
        allContactListView.setOnItemClickListener(contactAdapter);
        allContactListView.setOnItemLongClickListener(contactAdapter);

//        departmentContactListView.setOnItemClickListener(departmentAdapter);
//        departmentContactListView.setOnItemLongClickListener(departmentAdapter);
    }

    private void initAdapterDept() {
        try {
            mAdapter = new SimpleTreeAdapter<Bean>(mTreeListView, getActivity(), mDatas, 1);
            mAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
                @Override
                public void onClick(Node node, int position) {
                    int id = node.getId() - 10000;
                    if (node.isLeaf() && id > 0) {
                        IMUIHelper.openUserProfileActivity(getActivity(), id);
                    }
                }
            });
            mTreeListView.setAdapter(mAdapter);
        } catch (IllegalAccessException e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void locateDepartment(int departmentId) {
        logger.d("department#locateDepartment id:%s", departmentId);

        if (topContactTitle == null) {
            logger.e("department#TopTabButton is null");
            return;
        }
        Button tabDepartmentBtn = topContactTitle.getTabDepartmentBtn();
        if (tabDepartmentBtn == null) {
            return;
        }
        tabDepartmentBtn.performClick();
//        locateDepartmentImpl(departmentId);
    }

    // TODO
//    private void locateDepartmentImpl(int departmentId) {
//        if (imService == null) {
//            return;
//        }
//        DepartmentEntity department = imService.getContactManager().findDepartment(departmentId);
//        if (department == null) {
//            logger.e("department#no such id:%s", departmentId);
//            return;
//        }
//
//        logger.d("department#go to locate department:%s", department);
//        final int position = departmentAdapter.locateDepartment(department.getDepartName());
//        logger.d("department#located position:%d", position);
//
//        if (position < 0) {
//            logger.i("department#locateDepartment id:%s failed", departmentId);
//            return;
//        }
//        //the first time locate works
//        //from the second time, the locating operations fail ever since
//        departmentContactListView.post(new Runnable() {
//
//            @Override
//            public void run() {
//                departmentContactListView.setSelection(position);
//            }
//        });
//    }


    /**
     * 刷新单个entity
     * 很消耗性能
     */
    private void renderEntityList() {
        hideProgressBar();
        logger.d("contact#renderEntityList");

        if (contactMgr.isUserDataReady()) {
            renderUserList();
//            renderDeptList();
        }
        if (imService.getGroupManager().isGroupReady()) {
            renderGroupList();
        }
        showSearchFrameLayout();
    }


//    private void renderDeptList() {
//        /**---------------------部门数据的渲染------------------------------------------*/
//        List<UserEntity> departmentList = contactMgr.getDepartmentTabSortedList();
//        departmentAdapter.putUserList(departmentList);
//    }

    public void renderTreeDeptList() {
        if (mDatas == null) {
            mDatas = new ArrayList<Bean>();
        }
        if (mDatasBackUp == null) {
            mDatasBackUp = new ArrayList<Bean>();
        }
        mDatas.clear();
        mDatasBackUp.clear();
        CenterDatabase cd = new CenterDatabase(getActivity(), null);
        SQLiteDatabase db = cd.getWritableDatabase();
        Cursor cursor = db.rawQuery("select id, name, pri from " + CenterDatabase.DEPT + " where status = ?", new String[]{"1"});
        if (cursor.moveToFirst()) {
            do {
                String pri = cursor.getString(2);
                if (!pri.contains("-")) {
                    //id是唯一标识符,0为根目录，name为名称
                    mDatas.add(new Bean(cursor.getInt(0), 0, cursor.getString(1)));
                    mDatasBackUp.add(new Bean(cursor.getInt(0), 0, cursor.getString(1)));
                } else {
                    //子一级
                    String[] forid = pri.split(",");
                    mDatas.add(new Bean(cursor.getInt(0), Integer.parseInt(forid[1]), cursor.getString(1)));
                    mDatasBackUp.add(new Bean(cursor.getInt(0), Integer.parseInt(forid[1]), cursor.getString(1)));

                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        cursor = db.rawQuery("select id , name, udept, utouxiang from " + CenterDatabase.USER + " where status = ?", new String[]{"1"});
        if (cursor.moveToFirst()) {
            do {
                String[] g = cursor.getString(2).split(",");
                for (int m = 0; m < g.length; m++) {
                    mDatas.add(new Bean(cursor.getInt(0) + 10000, Integer.parseInt(g[m].substring(0, g[m].indexOf('-'))), cursor.getString(1)));
                    mDatasBackUp.add(new Bean(cursor.getInt(0) + 10000, Integer.parseInt(g[m].substring(0, g[m].indexOf('-'))), cursor.getString(1)));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        cd.close();
        try {
            mAdapter.putTreeList(mDatas);
        } catch (IllegalAccessException e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void renderUserList() {
        contactList = contactMgr.getContactSortedList();
        // 没有任何的联系人数据
        if (contactList.size() <= 0) {
            return;
        }
        List<UserEntity> all = new ArrayList<>();
        if (commonList != null && !commonList.isEmpty()) {
            all.addAll(commonList);
        }
        all.addAll(contactList);
        contactAdapter.putUserList(all);
    }

    private void renderGroupList() {
        logger.d("group#onGroupReady");
        List<GroupEntity> originList = imService.getGroupManager().getNormalGroupSortedList();
        if (originList.size() <= 0) {
            return;
        }
        contactAdapter.putGroupList(originList);
    }

    private ListView getCurListView() {
        if (0 == curTabIndex) {
            return allContactListView;
        } else {
//            return departmentContactListView;
            return mTreeListView;
        }
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        int position;
        if (0 == curTabIndex) {
            position = contactAdapter.getPositionForSection(s.charAt(0));
        } else {
//            position = departmentAdapter.getPositionForSection(s.charAt(0));
            position = -1;
        }
        if (position != -1) {
            getCurListView().setSelection(position);
        }
    }

    public static Handler getHandler() {
        return uiHandler;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onEventMainThread(GroupEvent event) {
        switch (event.getEvent()) {
            case GROUP_INFO_UPDATED:
            case GROUP_INFO_OK:
                renderGroupList();
                searchDataReady();
                break;
        }
    }

    public void onEventMainThread(UserInfoEvent event) {
        switch (event) {
            case USER_INFO_UPDATE:
            case USER_INFO_OK:
//                renderDeptList();
                renderUserList();
                searchDataReady();
                break;
        }
    }

    public void searchDataReady() {
        if (imService.getContactManager().isUserDataReady() &&
                imService.getGroupManager().isGroupReady()) {
            showSearchFrameLayout();
        }
    }

//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        if (hidden) {
//            dontRefresh = false;
//            if (needRefresh) {
//                needRefresh = false;
//                renderTreeDeptList();
//            }
//        } else {
//            dontRefresh = true;
//        }
//    }

    /*-------------------------------------------------------------------*/

    private class QueryListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (TextUtils.isEmpty(newText)) {
                mDatas.clear();
                for (Bean a : mDatasBackUp)
                    mDatas.add(a);
            } else {
                // 过滤出新数据
                mDatas.clear();
                // Log.d("TTT", "mDaatas长度是" + );
                for (Bean b : mDatasBackUp) {
                    if (b.getLabel().contains(newText)) {
                        mDatas.add(b);
                        int temp = b.getId();
                        for (Bean bb : mDatasBackUp) {
                            if ((bb.getpId() == temp) && !mDatas.contains(bb))
                                mDatas.add(bb);
                        }
                        Log.d("TTT", "找到一个符合的部门" + b.getLabel());

                    }
                }

                HashSet h = new HashSet(mDatas);
                mDatas.clear();
                mDatas.addAll(h);

            }
            //mTree.setFilterText(newText); // 设置ListView的过滤关键词
            try {
                mAdapter.putTreeList(mDatas);
//            mAdapter = new SimpleTreeAdapter<Bean>(mTreeListView, getActivity(), mDatas, 10);
//            mTreeListView.setAdapter(mAdapter);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

}
