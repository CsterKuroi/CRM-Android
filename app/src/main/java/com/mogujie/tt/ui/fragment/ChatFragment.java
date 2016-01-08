package com.mogujie.tt.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.yanhao.task729.yh729_Constant;
import com.mogujie.tt.DB.entity.GroupEntity;
import com.mogujie.tt.R;
import com.mogujie.tt.config.DBConstant;
import com.mogujie.tt.imservice.entity.RecentInfo;
import com.mogujie.tt.imservice.event.GroupEvent;
import com.mogujie.tt.imservice.event.LoginEvent;
import com.mogujie.tt.imservice.event.ReconnectEvent;
import com.mogujie.tt.imservice.event.SessionEvent;
import com.mogujie.tt.imservice.event.SocketEvent;
import com.mogujie.tt.imservice.event.UnreadEvent;
import com.mogujie.tt.imservice.event.UserInfoEvent;
import com.mogujie.tt.imservice.manager.IMLoginManager;
import com.mogujie.tt.imservice.manager.IMReconnectManager;
import com.mogujie.tt.imservice.manager.IMUnreadMsgManager;
import com.mogujie.tt.imservice.service.IMService;
import com.mogujie.tt.imservice.support.IMServiceConnector;
import com.mogujie.tt.protobuf.helper.EntityChangeEngine;
import com.mogujie.tt.ui.activity.MainActivity;
import com.mogujie.tt.ui.adapter.ChatAdapter;
import com.mogujie.tt.utils.IMUIHelper;
import com.mogujie.tt.utils.NetworkUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.List;

import de.greenrobot.event.EventBus;
import yh729_Fragment.yh729_mFragment;
import yh729_UI.yh729_HeadControlPanel;
import yh729_activity.yh729_SettingActivity;

/**
 * @author Nana
 * @Description 最近联系人Fragment页
 * @date 2014-7-24
 */
public class ChatFragment extends MainFragment
        implements
        OnItemSelectedListener,
        OnItemClickListener,
        OnItemLongClickListener {

    private ChatAdapter contactAdapter;
    private ListView contactListView;
    private View curView = null;
    private View noNetworkView;
    private View noChatView;
    private ImageView notifyImage;
    private TextView displayView;
    private ProgressBar reconnectingProgressBar;
    private IMService imService;
    private TextView tabBtnRemind;
    private TextView tabBtnChat;
    public int myCurrentView = 0;

    //是否是手动点击重练。fasle:不显示各种弹出小气泡. true:显示小气泡直到错误出现
    private volatile boolean isManualMConnect = false;


    //    public static ChatFragment yh729MainFragment;
    private Activity mMainActivity;
    private TextView tv1;
    private TextView tv2;
    private View underline1;
    private View underline2;
    private FragmentManager fragmentManager = null;
    private String tab = "";
    private FragmentTransaction fragmentTransaction = null;
    private int CHECKED_COLOR = Color.rgb(243, 152, 0);
    private int UNCHECKED_COLOR = Color.rgb(64, 64, 64);
    //    private CenterDatabase cdb;
//    private yh729_mSQLiteOpenHelper mySQLiteOpenHelper;
    private String type = yh729_Constant.MY_REMIND;
    private ViewFlipper mViewFlipperMain;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private IMServiceConnector imServiceConnector = new IMServiceConnector() {

        @Override
        public void onServiceDisconnected() {
            if (EventBus.getDefault().isRegistered(ChatFragment.this)) {
                EventBus.getDefault().unregister(ChatFragment.this);
            }
        }

        @Override
        public void onIMServiceConnected() {
            // TODO Auto-generated method stub
            logger.d("chatfragment#recent#onIMServiceConnected");
            imService = imServiceConnector.getIMService();
            if (imService == null) {
                // why ,some reason
                return;
            }
            // 依赖联系人回话、未读消息、用户的信息三者的状态
            onRecentContactDataReady();
            EventBus.getDefault().registerSticky(ChatFragment.this);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imServiceConnector.connect(getActivity());
        topRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).setFragmentIndicator(100);
            }
        });
        //tyc
        topRightBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String session = EntityChangeEngine.getSessionKey(imService.getLoginManager().getLoginId(), DBConstant.SESSION_TYPE_SINGLE);
                IMUIHelper.openGroupMemberSelectActivity(getActivity(), session);
                return false;
            }
        });
        topRightTitleTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), yh729_SettingActivity.class);
                startActivity(intent);
            }
        });
        logger.d("chatfragment#onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle bundle) {
        logger.d("onCreateView");
        if (null != curView) {
            logger.d("curView is not null, remove it");
            ((ViewGroup) curView.getParent()).removeView(curView);
        }
        curView = inflater.inflate(R.layout.tt_fragment_chat, topContentView);
        tabBtnRemind = (TextView) curView.findViewById(R.id.tab_btn_remind);
        tabBtnChat = (TextView) curView.findViewById(R.id.tab_btn_chat);
        mViewFlipperMain = (ViewFlipper) curView.findViewById(R.id.view_flipper_main);
        // 多端登陆也在用这个view
        noNetworkView = curView.findViewById(R.id.layout_no_network);
        noChatView = curView.findViewById(R.id.layout_no_chat);
        reconnectingProgressBar = (ProgressBar) curView.findViewById(R.id.progressbar_reconnect);
        displayView = (TextView) curView.findViewById(R.id.disconnect_text);
        notifyImage = (ImageView) curView.findViewById(R.id.imageWifi);
        tabBtnRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressRemind();
            }
        });
        tabBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressChat();
            }
        });

        setTopRightText("设置");

        super.init(curView);
        initTitleView();// 初始化顶部view
        initContactListView(); // 初始化联系人列表视图
        showProgressBar();// 创建时没有数据，显示加载动画
        onCreateViewRemind();
        return curView;
    }

    /**
     * @Description 设置顶部按钮
     */
    private void initTitleView() {
        // 设置标题
        setTopTitle(getActivity().getString(R.string.chat_title));
    }

    private void initContactListView() {
        contactListView = (ListView) curView.findViewById(R.id.ContactListView);
        contactListView.setOnItemClickListener(this);
        contactListView.setOnItemLongClickListener(this);
        contactAdapter = new ChatAdapter(getActivity());
        contactListView.setAdapter(contactAdapter);

        // this is critical, disable loading when finger sliding, otherwise
        // you'll find sliding is not very smooth
        contactListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(),
                true, true));
    }

    @Override
    public void onStart() {
        logger.d("chatfragment#onStart");
        super.onStart();
    }

    @Override
    public void onStop() {
        logger.d("chatfragment#onStop");
        super.onStop();
    }

    @Override
    public void onPause() {
        logger.d("chatfragment#onPause");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(ChatFragment.this)) {
            EventBus.getDefault().unregister(ChatFragment.this);
        }
        imServiceConnector.disconnect(getActivity());
        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    // 这个地方跳转一定要快
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        RecentInfo recentInfo = contactAdapter.getItem(position);
        if (recentInfo == null) {
            logger.e("recent#null recentInfo -> position:%d", position);
            return;
        }
        IMUIHelper.openChatActivity(getActivity(), recentInfo.getSessionKey());
    }

    public void onEventMainThread(SessionEvent sessionEvent) {
        logger.d("chatfragment#SessionEvent# -> %s", sessionEvent);
        switch (sessionEvent) {
            case RECENT_SESSION_LIST_UPDATE:
            case RECENT_SESSION_LIST_SUCCESS:
            case SET_SESSION_TOP:
                onRecentContactDataReady();
                break;
        }
    }

    public void onEventMainThread(GroupEvent event) {
        switch (event.getEvent()) {
            case GROUP_INFO_OK:
            case CHANGE_GROUP_MEMBER_SUCCESS:
                onRecentContactDataReady();
                searchDataReady();
                break;

            case GROUP_INFO_UPDATED:
                onRecentContactDataReady();
                searchDataReady();
                break;
            case SHIELD_GROUP_OK:
                // 更新最下栏的未读计数、更新session
                onShieldSuccess(event.getGroupEntity());
                break;
            case SHIELD_GROUP_FAIL:
            case SHIELD_GROUP_TIMEOUT:
                onShieldFail();
                break;
        }
    }

    public void onEventMainThread(UnreadEvent event) {
        switch (event.event) {
            case UNREAD_MSG_RECEIVED:
            case UNREAD_MSG_LIST_OK:
            case SESSION_READED_UNREAD_MSG:
                onRecentContactDataReady();
                break;
        }
    }

    public void onEventMainThread(UserInfoEvent event) {
        switch (event) {
            case USER_INFO_UPDATE:
            case USER_INFO_OK:
                onRecentContactDataReady();
                searchDataReady();
                break;
        }
    }

    public void onEventMainThread(LoginEvent loginEvent) {
        logger.d("chatfragment#LoginEvent# -> %s", loginEvent);
        switch (loginEvent) {
            case LOCAL_LOGIN_SUCCESS:
            case LOGINING: {
                logger.d("chatFragment#login#recv handleDoingLogin event");
                if (reconnectingProgressBar != null) {
                    reconnectingProgressBar.setVisibility(View.VISIBLE);
                }
            }
            break;

            case LOCAL_LOGIN_MSG_SERVICE:
            case LOGIN_OK: {
                isManualMConnect = false;
                logger.d("chatfragment#loginOk");
                noNetworkView.setVisibility(View.GONE);
            }
            break;

            case LOGIN_AUTH_FAILED:
            case LOGIN_INNER_FAILED: {
                onLoginFailure(loginEvent);
            }
            break;

            case PC_OFFLINE:
            case KICK_PC_SUCCESS:
                onPCLoginStatusNotify(false);
                break;

            case KICK_PC_FAILED:
                Toast.makeText(getActivity(), getString(R.string.kick_pc_failed), Toast.LENGTH_SHORT).show();
                break;
            case PC_ONLINE:
                onPCLoginStatusNotify(true);
                break;

            default:
                reconnectingProgressBar.setVisibility(View.GONE);
                break;
        }
    }


    public void onEventMainThread(SocketEvent socketEvent) {
        switch (socketEvent) {
            case MSG_SERVER_DISCONNECTED:
                handleServerDisconnected();
                break;

            case CONNECT_MSG_SERVER_FAILED:
            case REQ_MSG_SERVER_ADDRS_FAILED:
                handleServerDisconnected();
                onSocketFailure(socketEvent);
                break;
        }
    }

    public void onEventMainThread(ReconnectEvent reconnectEvent) {
        switch (reconnectEvent) {
            case DISABLE: {
                handleServerDisconnected();
            }
            break;
        }
    }

    private void onLoginFailure(LoginEvent event) {
        if (!isManualMConnect) {
            return;
        }
        isManualMConnect = false;
        String errorTip = getString(IMUIHelper.getLoginErrorTip(event));
        logger.d("login#errorTip:%s", errorTip);
        reconnectingProgressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), errorTip, Toast.LENGTH_SHORT).show();
    }

    private void onSocketFailure(SocketEvent event) {
        if (!isManualMConnect) {
            return;
        }
        isManualMConnect = false;
        String errorTip = getString(IMUIHelper.getSocketErrorTip(event));
        logger.d("login#errorTip:%s", errorTip);
        reconnectingProgressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), errorTip, Toast.LENGTH_SHORT).show();
    }

    // 更新页面以及 下面的未读总计数
    private void onShieldSuccess(GroupEntity entity) {
        if (entity == null) {
            return;
        }
        // 更新某个sessionId
        contactAdapter.updateRecentInfoByShield(entity);
        IMUnreadMsgManager unreadMsgManager = imService.getUnReadMsgManager();

        int totalUnreadMsgCnt = unreadMsgManager.getTotalUnreadCount();
        logger.d("unread#total cnt %d", totalUnreadMsgCnt);
        ((MainActivity) getActivity()).setUnreadMessageCnt(totalUnreadMsgCnt);
    }

    private void onShieldFail() {
        Toast.makeText(getActivity(), R.string.req_msg_failed, Toast.LENGTH_SHORT).show();
    }

    /**
     * 搜索数据OK
     * 群组数据与 user数据都已经完毕
     */
    public void searchDataReady() {
        if (imService.getContactManager().isUserDataReady() &&
                imService.getGroupManager().isGroupReady()) {
            showSearchFrameLayout();
        }
    }

    /**
     * 多端，PC端在线状态通知
     *
     * @param isOnline
     */
    public void onPCLoginStatusNotify(boolean isOnline) {
        logger.d("chatfragment#onPCLoginStatusNotify");
        if (isOnline) {
            reconnectingProgressBar.setVisibility(View.GONE);
            noNetworkView.setVisibility(View.VISIBLE);
            notifyImage.setImageResource(R.drawable.pc_notify);
            displayView.setText(R.string.pc_status_notify);
            /**添加踢出事件*/
            noNetworkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reconnectingProgressBar.setVisibility(View.VISIBLE);
                    imService.getLoginManager().reqKickPCClient();
                }
            });
        } else {
            noNetworkView.setVisibility(View.GONE);
        }
    }

    private void handleServerDisconnected() {
        logger.d("chatfragment#handleServerDisconnected");

        if (reconnectingProgressBar != null) {
            reconnectingProgressBar.setVisibility(View.GONE);
        }

        if (noNetworkView != null) {
            notifyImage.setImageResource(R.drawable.warning);
            noNetworkView.setVisibility(View.VISIBLE);
            if (imService != null) {
                if (imService.getLoginManager().isKickout()) {
                    displayView.setText(R.string.disconnect_kickout);
                } else {
                    displayView.setText(R.string.no_network);
                }
            }
            /**重连【断线、被其他移动端挤掉】*/
            noNetworkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logger.d("chatFragment#noNetworkView clicked");
                    IMReconnectManager manager = imService.getReconnectManager();
                    if (NetworkUtil.isNetWorkAvalible(getActivity())) {
                        isManualMConnect = true;
                        IMLoginManager.instance().relogin();
                    } else {
                        Toast.makeText(getActivity(), R.string.no_network_toast, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    reconnectingProgressBar.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    /**
     * 这个处理有点过于粗暴
     */
    private void onRecentContactDataReady() {
        boolean isUserData = imService.getContactManager().isUserDataReady();
        boolean isSessionData = imService.getSessionManager().isSessionListReady();
        boolean isGroupData = imService.getGroupManager().isGroupReady();

        if (!(isUserData && isSessionData && isGroupData)) {
            return;
        }
        IMUnreadMsgManager unreadMsgManager = imService.getUnReadMsgManager();

        int totalUnreadMsgCnt = unreadMsgManager.getTotalUnreadCount();
        logger.d("unread#total cnt %d", totalUnreadMsgCnt);
        ((MainActivity) getActivity()).setUnreadMessageCnt(totalUnreadMsgCnt);

        List<RecentInfo> recentSessionList = imService.getSessionManager().getRecentListInfo();

        setNoChatView(recentSessionList);
        contactAdapter.setData(recentSessionList);
        hideProgressBar();
        showSearchFrameLayout();
    }

    private void setNoChatView(List<RecentInfo> recentSessionList) {
        if (recentSessionList.size() == 0) {
            noChatView.setVisibility(View.VISIBLE);
        } else {
            noChatView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {

        RecentInfo recentInfo = contactAdapter.getItem(position);
        if (recentInfo == null) {
            logger.e("recent#onItemLongClick null recentInfo -> position:%d", position);
            return false;
        }
        if (recentInfo.getSessionType() == DBConstant.SESSION_TYPE_SINGLE) {
            handleContactItemLongClick(getActivity(), recentInfo);
        } else {
            handleGroupItemLongClick(getActivity(), recentInfo);
        }
        return true;
    }

    private void handleContactItemLongClick(final Context ctx, final RecentInfo recentInfo) {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ctx, android.R.style.Theme_Holo_Light_Dialog));
        builder.setTitle(recentInfo.getName());
        final boolean isTop = imService.getConfigSp().isTopSession(recentInfo.getSessionKey());

        int topMessageRes = isTop ? R.string.cancel_top_message : R.string.top_message;
        String[] items = new String[]{ctx.getString(R.string.check_profile),
                ctx.getString(R.string.delete_session),
                ctx.getString(topMessageRes)};

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        IMUIHelper.openUserProfileActivity(ctx, recentInfo.getPeerId());
                        break;
                    case 1:
                        imService.getSessionManager().reqRemoveSession(recentInfo);
                        break;
                    case 2: {
                        imService.getConfigSp().setSessionTop(recentInfo.getSessionKey(), !isTop);
                    }
                    break;
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    // 现在只有群组存在免打扰的
    private void handleGroupItemLongClick(final Context ctx, final RecentInfo recentInfo) {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ctx, android.R.style.Theme_Holo_Light_Dialog));
        builder.setTitle(recentInfo.getName());

        final boolean isTop = imService.getConfigSp().isTopSession(recentInfo.getSessionKey());
        final boolean isForbidden = recentInfo.isForbidden();
        int topMessageRes = isTop ? R.string.cancel_top_message : R.string.top_message;
        int forbidMessageRes = isForbidden ? R.string.cancel_forbid_group_message : R.string.forbid_group_message;

        String[] items = new String[]{ctx.getString(R.string.delete_session), ctx.getString(topMessageRes), ctx.getString(forbidMessageRes)};

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        imService.getSessionManager().reqRemoveSession(recentInfo);
                        break;
                    case 1: {
                        imService.getConfigSp().setSessionTop(recentInfo.getSessionKey(), !isTop);
                    }
                    break;
                    case 2: {
                        // 底层成功会事件通知
                        int shieldType = isForbidden ? DBConstant.GROUP_STATUS_ONLINE : DBConstant.GROUP_STATUS_SHIELD;
                        imService.getGroupManager().reqShieldGroup(recentInfo.getPeerId(), shieldType);
                    }
                    break;
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    @Override
    protected void initHandler() {
        // TODO Auto-generated method stub
    }

    /**
     * 滚动到有未读消息的第一个联系人
     * 这个还是可行的
     */
    public void scrollToUnreadPosition() {
        if (contactListView != null) {
            pressChat();
            int currentPosition = contactListView.getFirstVisiblePosition();
            int needPosition = contactAdapter.getUnreadPositionOnView(currentPosition);
            // 下面这个不管用!!
            //contactListView.smoothScrollToPosition(needPosition);
            contactListView.setSelection(needPosition);
        }
    }

    @Override
    protected void showSearchFrameLayout() {
        setTopLeftButton(R.drawable.tt_top_search);
        topLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showSearchView();
            }
        });
    }

    public void pressRemind() {
        if (myCurrentView == 1) {
            topLeftBtn.setVisibility(View.GONE);
            setTopRightText("设置");
            hideTopRightButton();
            mViewFlipperMain.setInAnimation(getActivity(), R.anim.my_leftright_in);
            mViewFlipperMain.setOutAnimation(getActivity(), R.anim.my_leftright_out);
            mViewFlipperMain.showNext();
            tabBtnRemind.setBackgroundResource(R.drawable.my_tab_sel);
            tabBtnRemind.setTextColor(getResources().getColor(R.color.my_tab_sel_color));
            tabBtnChat.setBackgroundColor(getResources().getColor(android.R.color.white));
            tabBtnChat.setTextColor(getResources().getColor(android.R.color.black));
            myCurrentView = 0;

        }
    }

    public void pressChat() {
        if (myCurrentView == 0) {
            topLeftBtn.setVisibility(View.VISIBLE);
            setTopRightButton(R.drawable.tt_top_right_contact);
            topRightTitleTxt.setVisibility(View.GONE);
            mViewFlipperMain.setInAnimation(getActivity(), R.anim.my_rightleft_in);
            mViewFlipperMain.setOutAnimation(getActivity(), R.anim.my_rightleft_out);
            mViewFlipperMain.showNext();
            tabBtnChat.setBackgroundResource(R.drawable.my_tab_sel);
            tabBtnChat.setTextColor(getResources().getColor(R.color.my_tab_sel_color));
            tabBtnRemind.setBackgroundColor(getResources().getColor(android.R.color.white));
            tabBtnRemind.setTextColor(getResources().getColor(android.R.color.black));
            myCurrentView = 1;
        }
    }

    /*------------------------------------------------------------------------------*/

    private void onCreateViewRemind() {
        try {
            //initService();
//            cdb = new CenterDatabase(getActivity(), null);
//            yh729MainFragment = this;
            mMainActivity = getActivity();
            tv1 = (TextView) curView.findViewById(R.id.title1);
            tv2 = (TextView) curView.findViewById(R.id.title2);
            underline1 = curView.findViewById(R.id.title1_bar);
            underline2 = curView.findViewById(R.id.title2_bar);
            try {
                fragmentManager = getFragmentManager();
            } catch (Exception e) {
                Log.i("test", e.toString());
                e.printStackTrace();
            }
            InitUI();
//            mySQLiteOpenHelper = new yh729_mSQLiteOpenHelper(mMainActivity, "remind_" + cdb.getUID(), null, 1);
//            cdb.close();
        } catch (Exception e) {
            Log.i("test", e.toString());
            e.printStackTrace();
        }
    }

    private void InitUI() {
//        Intent intent = mMainActivity.getIntent();
        Intent intent = new Intent();
        if (intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            type = bundle.getString("type");
        }
        yh729_HeadControlPanel headControlPanel = (yh729_HeadControlPanel) curView.findViewById(R.id.m_head);
        headControlPanel.setMiddleTitle(type);
        headControlPanel.setVisibility(View.GONE);
        initTab();
        tab = (String) tv1.getText();
        Checked(tv1, underline1);
        unChecked(tv2, underline2);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.m_context, new yh729_mFragment(), tab.equals("") ? type : tab);
        fragmentTransaction.commit();
    }

    private void initTab() {
        switch (type) {
            case yh729_Constant.INSTRUCTION_REMIND:
            case yh729_Constant.DAIRY_REMIND:
            case yh729_Constant.CHECK_REMIND:
            case yh729_Constant.FOCUS_REMIND:
            case yh729_Constant.NEEDREPLY:
            case yh729_Constant.ATME:
            case yh729_Constant.ATMYSECTOR:
            case yh729_Constant.SCHEDULE_REMIND:
                tv1.setVisibility(View.VISIBLE);
                tv2.setVisibility(View.VISIBLE);
                underline1.setVisibility(View.VISIBLE);
                underline2.setVisibility(View.VISIBLE);
                setTabname();
                FrameLayout frameLayout = (FrameLayout) curView.findViewById(R.id.m_context);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();
                lp.addRule(RelativeLayout.BELOW, R.id.base_activity_subline);
                tv1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attachFragment(tv1);
                        Checked(tv1, underline1);
                        unChecked(tv2, underline2);
                    }
                });
                tv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attachFragment(tv2);
                        Checked(tv2, underline2);
                        unChecked(tv1, underline1);
                    }
                });
                break;
            default:
                tv1.setVisibility(View.INVISIBLE);
                tv2.setVisibility(View.INVISIBLE);
                underline1.setVisibility(View.INVISIBLE);
                underline2.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void Checked(TextView tv, View underline) {
        tv.setTextColor(CHECKED_COLOR);
        underline.setBackgroundColor(CHECKED_COLOR);
    }

    private void unChecked(TextView tv, View underline) {
        tv.setTextColor(UNCHECKED_COLOR);
        underline.setBackgroundColor(Color.WHITE);
    }

    private void setTabname() {
        switch (type) {
            case yh729_Constant.INSTRUCTION_REMIND:
                tv1.setText(yh729_Constant.INSTRUCTION_REMIND1);
                tv2.setText(yh729_Constant.INSTRUCTION_REMIND2);
                break;
            case yh729_Constant.DAIRY_REMIND:
                tv1.setText(yh729_Constant.DAIRY_REMIND1);
                tv2.setText(yh729_Constant.DAIRY_REMIND2);
                break;
            case yh729_Constant.CHECK_REMIND:
                tv1.setText(yh729_Constant.CHECK_REMIND1);
                tv2.setText(yh729_Constant.CHECK_REMIND2);
                break;
            case yh729_Constant.FOCUS_REMIND:
                tv1.setText(yh729_Constant.FOCUS_REMIND1);
                tv2.setText(yh729_Constant.FOCUS_REMIND2);
                break;
            case yh729_Constant.NEEDREPLY:
                tv1.setText(yh729_Constant.NEEDREPLY1);
                tv2.setText(yh729_Constant.NEEDREPLY2);
                break;
            case yh729_Constant.ATME:
                tv1.setText(yh729_Constant.ATME1);
                tv2.setText(yh729_Constant.ATME2);
                break;
            case yh729_Constant.ATMYSECTOR:
                tv1.setText(yh729_Constant.ATMYSECTOR1);
                tv2.setText(yh729_Constant.ATMYSECTOR2);
                break;
            case yh729_Constant.SCHEDULE_REMIND:
                tv1.setText(yh729_Constant.SCHEDULE_REMIND1);
                tv2.setText(yh729_Constant.SCHEDULE_REMIND2);
                break;
            default:
                tv1.setText("");
                tv2.setText("");
        }
    }

    private void attachFragment(TextView tv) {
        fragmentManager.beginTransaction().replace(R.id.m_context, new yh729_mFragment(), (String) tv.getText()).commit();
    }

//    public yh729_mSQLiteOpenHelper getMySQLiteOpenHelper() {
//        return mySQLiteOpenHelper;
//    }

}
