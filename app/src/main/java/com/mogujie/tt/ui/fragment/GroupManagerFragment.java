package com.mogujie.tt.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.mogujie.tt.DB.DBInterface;
import com.mogujie.tt.DB.entity.GroupEntity;
import com.mogujie.tt.DB.entity.PeerEntity;
import com.mogujie.tt.DB.entity.UserEntity;
import com.mogujie.tt.DB.sp.ConfigurationSp;
import com.mogujie.tt.R;
import com.mogujie.tt.config.DBConstant;
import com.mogujie.tt.config.IntentConstant;
import com.mogujie.tt.imservice.event.GroupEvent;
import com.mogujie.tt.imservice.service.IMService;
import com.mogujie.tt.imservice.support.IMServiceConnector;
import com.mogujie.tt.ui.adapter.GroupManagerAdapter;
import com.mogujie.tt.ui.base.TTBaseFragment;
import com.mogujie.tt.ui.helper.CheckboxConfigHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;


/**
 * @YM
 * 个人与群组的聊天详情都会来到这个页面
 * single: 这有sessionId的头像，以及加号"+" ， 创建群成功之后，跳到聊天的页面
 * group:  群成员，加减号 ， 修改成功之后，跳到群管理页面
 * 临时群任何人都可以加人，但是只有群主可以踢人”这个逻辑修改下，正式群暂时只给createId开放
 */
public class GroupManagerFragment extends TTBaseFragment{
    private View curView = null;
    /**adapter配置*/
    private GridView gridView;
    private GroupManagerAdapter adapter;


    /**详情的配置  勿扰以及指定聊天*/
    CheckboxConfigHelper checkBoxConfiger = new CheckboxConfigHelper();
    CheckBox noDisturbCheckbox;
    CheckBox topSessionCheckBox;

    /**需要的状态参数*/
    private IMService imService;
    private String curSessionKey;
    private PeerEntity peerEntity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imServiceConnector.connect(getActivity());
        EventBus.getDefault().register(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if (null != curView) {
            ((ViewGroup) curView.getParent()).removeView(curView);
            return curView;
        }
        curView = inflater.inflate(R.layout.tt_fragment_group_manage, topContentView);
        noDisturbCheckbox = (CheckBox) curView.findViewById(R.id.NotificationNoDisturbCheckbox);
        topSessionCheckBox = (CheckBox) curView.findViewById(R.id.NotificationTopMessageCheckbox);
        initRes();
        return curView;
    }

    private void initRes() {
        // 设置标题栏
        setTopLeftButton(R.drawable.tt_top_back);
        setTopLeftText(getActivity().getString(R.string.top_left_back));
        topLeftContainerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        imServiceConnector.disconnect(getActivity());
    }

    @Override
    protected void initHandler() {
    }


    private IMServiceConnector imServiceConnector = new IMServiceConnector(){
        @Override
        public void onServiceDisconnected() {
        }

        @Override
        public void onIMServiceConnected() {
            logger.d("groupmgr#onIMServiceConnected");
            imService = imServiceConnector.getIMService();
            if(imService == null){
              Toast.makeText(GroupManagerFragment.this.getActivity(),
                        getResources().getString(R.string.im_service_disconnected), Toast.LENGTH_SHORT).show();
               return;
            }
            checkBoxConfiger.init(imService.getConfigSp());
            initView();
            initAdapter();
        }
    };


    private void initView() {
        setTopTitle(getString(R.string.chat_detail));
        if (null == imService || null == curView ) {
            logger.e("groupmgr#init failed,cause by imService or curView is null");
            return;
        }

        curSessionKey =  getActivity().getIntent().getStringExtra(IntentConstant.KEY_SESSION_KEY);
        if (TextUtils.isEmpty(curSessionKey)) {
            logger.e("groupmgr#getSessionInfoFromIntent failed");
            return;
        }
        peerEntity = imService.getSessionManager().findPeerEntity(curSessionKey);
        if(peerEntity == null){
            logger.e("groupmgr#findPeerEntity failed,sessionKey:%s",curSessionKey);
            return;
        }
        switch (peerEntity.getType()){
            case DBConstant.SESSION_TYPE_GROUP:{
                GroupEntity groupEntity = (GroupEntity) peerEntity;
                // 群组名称的展示
                TextView groupNameView = (TextView) curView.findViewById(R.id.group_manager_title);
                groupNameView.setText(groupEntity.getMainName());
            }break;

            case DBConstant.SESSION_TYPE_SINGLE:{
                // 个人不显示群聊名称
                View groupNameContainerView = curView.findViewById(R.id.group_manager_name);
                groupNameContainerView.setVisibility(View.GONE);
            }break;
        }

        //------------tyc----------------
        View groupNameContainerView  = curView.findViewById(R.id.group_manager_name);
        groupNameContainerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupEntity groupEntity = (GroupEntity) peerEntity;
                if (imService.getLoginManager().getLoginId() == (groupEntity.getCreatorId())) { //只有群主可以改群名称
                    ShowDialogForTempGroupname((GroupEntity) peerEntity);       //弹出窗口
                }
            }
        });
        // 初始化配置checkBox
        initCheckbox();
    }

    private void ShowDialogForTempGroupname(final GroupEntity groupEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(),
                android.R.style.Theme_Holo_Light_Dialog));

        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialog_view = inflater.inflate(R.layout.tt_custom_dialog, null);
        final EditText editText = (EditText)dialog_view.findViewById(R.id.dialog_edit_content);
        TextView textText = (TextView)dialog_view.findViewById(R.id.dialog_title);
        textText.setText(R.string.create_temp_group_dialog_title);
        builder.setView(dialog_view);

        builder.setPositiveButton(getString(R.string.tt_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tempGroupName = editText.getText().toString();
                tempGroupName = tempGroupName.trim();
                groupEntity.setMainName(tempGroupName);
                DBInterface dbInterface = DBInterface.instance();
                dbInterface.insertOrUpdateGroup(groupEntity);
                TextView groupNameView = (TextView) curView.findViewById(R.id.group_manager_title);
                groupNameView.setText(groupEntity.getMainName());
            }
        });
        builder.setNegativeButton(getString(R.string.tt_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                InputMethodManager inputManager =
                        (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(editText.getWindowToken(),0);
            }
        });
        final AlertDialog alertDialog = builder.create();

        /**只有输入框中有值的时候,确定按钮才可以按下*/
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString().trim())) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });

        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        /**对话框弹出的时候，下面的键盘也要跟上来*/
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager inputManager =
                        (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 100);
    }
//-----------------tyc-----------------------

    private void initAdapter(){
        logger.d("groupmgr#initAdapter");

        gridView = (GridView) curView.findViewById(R.id.group_manager_grid);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));// 去掉点击时的黄色背影
        gridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

        adapter = new GroupManagerAdapter(getActivity(),imService,peerEntity);
        gridView.setAdapter(adapter);
    }

    /**事件驱动通知*/
    public void onEventMainThread(GroupEvent event){
        switch (event.getEvent()){

            case CHANGE_GROUP_MEMBER_FAIL:
            case CHANGE_GROUP_MEMBER_TIMEOUT:{
                Toast.makeText(getActivity(), getString(R.string.change_temp_group_failed), Toast.LENGTH_SHORT).show();
                return;
            }
            case CHANGE_GROUP_MEMBER_SUCCESS:{
                onMemberChangeSuccess(event);
            }break;
        }
    }

    private void onMemberChangeSuccess(GroupEvent event){
        int groupId = event.getGroupEntity().getPeerId();
        if(groupId != peerEntity.getPeerId()){
            return;
        }
        List<Integer> changeList = event.getChangeList();
        if(changeList == null || changeList.size()<=0){
            return;
        }
        int changeType = event.getChangeType();

        switch (changeType){
            case DBConstant.GROUP_MODIFY_TYPE_ADD:
                ArrayList<UserEntity> newList = new ArrayList<>();
                for(Integer userId:changeList){
                    UserEntity userEntity =  imService.getContactManager().findContact(userId);
                    if(userEntity!=null) {
                        newList.add(userEntity);
                    }
                }
                adapter.add(newList);
                break;
            case DBConstant.GROUP_MODIFY_TYPE_DEL:
                for(Integer userId:changeList){
                    adapter.removeById(userId);
                }
                break;
        }
    }

	private void initCheckbox() {
        checkBoxConfiger.initCheckBox(noDisturbCheckbox, curSessionKey, ConfigurationSp.CfgDimension.NOTIFICATION);
        checkBoxConfiger.initTopCheckBox(topSessionCheckBox,curSessionKey);
    }
}
