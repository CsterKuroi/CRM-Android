package com.mogujie.tt.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mogujie.tt.DB.entity.GroupEntity;
import com.mogujie.tt.DB.entity.PeerEntity;
import com.mogujie.tt.DB.entity.UserEntity;
import com.mogujie.tt.R;
import com.mogujie.tt.config.DBConstant;
import com.mogujie.tt.config.IntentConstant;
import com.mogujie.tt.imservice.event.GroupEvent;
import com.mogujie.tt.imservice.manager.IMContactManager;
import com.mogujie.tt.imservice.manager.IMGroupManager;
import com.mogujie.tt.imservice.service.IMService;
import com.mogujie.tt.imservice.support.IMServiceConnector;
import com.mogujie.tt.ui.activity.GroupMemberAtActivity;
import com.mogujie.tt.ui.adapter.GroupSelectAdapter;
import com.mogujie.tt.ui.widget.SearchEditText;
import com.mogujie.tt.ui.widget.SortSideBar;
import com.mogujie.tt.ui.widget.SortSideBar.OnTouchingLetterChangedListener;
import com.mogujie.tt.utils.IMUIHelper;
import com.mogujie.tt.utils.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;


/**
 * @TYC  - -!
 * 选好人时候，跳回聊天页面
 */
public class GroupMemberAtFragment extends MainFragment
        implements OnTouchingLetterChangedListener {

    private static Logger logger = Logger.getLogger(GroupMemberAtFragment.class);

    private View curView = null;
    private IMService imService;

    /**列表视图
     * 1. 需要两种状态:选中的成员List  --》确定之后才会回话页面或者详情
     * */
    private GroupSelectAdapter adapter;
    private ListView contactListView;

    private SortSideBar sortSideBar;
    private TextView dialog;
    private SearchEditText searchEditText;

    private int currentGroupId;
    private PeerEntity peerEntity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        imServiceConnector.connect(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        imServiceConnector.disconnect(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null != curView) {
            ((ViewGroup) curView.getParent()).removeView(curView);
            return curView;
        }
        curView = inflater.inflate(R.layout.tt_fragment_group_member_at, topContentView);
        super.init(curView);
        initRes();
        return curView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private IMServiceConnector imServiceConnector = new IMServiceConnector(){
        @Override
        public void onIMServiceConnected() {
            logger.d("groupselmgr#onIMServiceConnected");

            imService = imServiceConnector.getIMService();
            currentGroupId = ((GroupMemberAtActivity)getActivity()).groupId;
            peerEntity = imService.getGroupManager().findGroup(currentGroupId);
            initContactList();
        }

        @Override
        public void onServiceDisconnected() {}
    };


    private void initContactList() {
        // 根据拼音排序
        adapter = new GroupSelectAdapter(getActivity(),imService);
        contactListView.setAdapter(adapter);

        contactListView.setOnItemClickListener(adapter);
        contactListView.setOnItemLongClickListener(adapter);
        /////////////////////tyc///////////////////
        List<UserEntity> memberList = imService.getGroupManager().getGroupMembers(peerEntity.getPeerId());
        adapter.setAllUserList(memberList);
    }


    /**
     * @Description 初始化资源
     */
    private void initRes() {
        // 设置标题栏
        // todo eric
        setTopTitle(getString(R.string.choose_at));
        setTopRightText(getActivity().getString(R.string.confirm));
        topLeftContainerLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        setTopLeftText(getResources().getString(R.string.cancel));

        topRightTitleTxt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                logger.d("tempgroup#on 'save' btn clicked");

                if(adapter.getCheckListSet().size()<=0){
                    getActivity().finish();
                    return;
                }

                Set<Integer> checkListSet =  adapter.getCheckListSet();
                IMContactManager manager = imService.getContactManager();
                List<UserEntity> memberList = new ArrayList<>();
                String s = "";
                for(Integer memId:checkListSet){
                    UserEntity user =  manager.findContact(memId);
                    if(user!=null){
                        memberList.add(user);
                        s = s + user.getMainName() + " " + "@";
                    }
                }
                s = s.substring(0, s.length()-1);
                ((GroupMemberAtActivity)getActivity()).atmem = s;
                ((GroupMemberAtActivity)getActivity()).i = new Intent();
                ((GroupMemberAtActivity)getActivity()).i.putExtra("atmem", s);
                getActivity().setResult(((GroupMemberAtActivity) getActivity()).RESULT_OK, ((GroupMemberAtActivity) getActivity()).i);
                getActivity().finish();
            }
        });

        sortSideBar = (SortSideBar) curView.findViewById(R.id.sidebar);
        sortSideBar.setOnTouchingLetterChangedListener(this);

        dialog = (TextView) curView.findViewById(R.id.dialog);
        sortSideBar.setTextView(dialog);

        contactListView = (ListView) curView.findViewById(R.id.all_contact_list);
        contactListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                 //如果存在软键盘，关闭掉
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                //txtName is a reference of an EditText Field
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        searchEditText = (SearchEditText) curView.findViewById(R.id.filter_edit);
        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                String key = s.toString();
                if(TextUtils.isEmpty(key)){
                    adapter.recover();
                    sortSideBar.setVisibility(View.VISIBLE);
                }else{
                    sortSideBar.setVisibility(View.INVISIBLE);
                    adapter.onSearch(key);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    public void onEventMainThread(GroupEvent event){
        getActivity().finish();
    }

    @Override
    protected void initHandler() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        // TODO Auto-generated method stub
        int position = adapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
            contactListView.setSelection(position);
        }
    }

}
