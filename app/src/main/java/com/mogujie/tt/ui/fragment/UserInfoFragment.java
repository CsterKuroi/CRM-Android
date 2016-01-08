package com.mogujie.tt.ui.fragment;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mogujie.tt.DB.entity.DepartmentEntity;
import com.mogujie.tt.DB.entity.UserEntity;
import com.mogujie.tt.R;
import com.mogujie.tt.config.DBConstant;
import com.mogujie.tt.config.IntentConstant;
import com.mogujie.tt.config.UrlConstant;
import com.mogujie.tt.imservice.event.UserInfoEvent;
import com.mogujie.tt.imservice.manager.IMLoginManager;
import com.mogujie.tt.imservice.service.IMService;
import com.mogujie.tt.imservice.support.IMServiceConnector;
import com.mogujie.tt.ui.activity.ClipPictureActivity;
import com.mogujie.tt.ui.activity.DetailPortraitActivity;
import com.mogujie.tt.ui.widget.IMBaseImageView;
import com.mogujie.tt.utils.IMUIHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 1.18 添加currentUser变量
 */
public class UserInfoFragment extends MainFragment {

    private View curView = null;
    private IMService imService;
    private UserEntity currentUser;
    private int currentUserId;

    private static final int PHOTO_WITH_DATA = 19;  //从SD卡中得到图片
    private static final int PHOTO_WITH_CAMERA = 1;// 拍摄照片
    private static final int PHOTO_CLIP = 119;// 截取图片

    private String imgPath = "";
    private String imgName = "";

    private IMServiceConnector imServiceConnector = new IMServiceConnector() {
        @Override
        public void onIMServiceConnected() {
            logger.d("detail#onIMServiceConnected");

            imService = imServiceConnector.getIMService();
            if (imService == null) {
                logger.e("detail#imService is null");
                return;
            }

            currentUserId = getActivity().getIntent().getIntExtra(IntentConstant.KEY_PEERID, 0);
            if (currentUserId == 0) {
                logger.e("detail#intent params error!!");
                return;
            }
            currentUser = imService.getContactManager().findContact(currentUserId);
            if (currentUser != null) {
                initBaseProfile();
                initDetailProfile();
            }
            ArrayList<Integer> userIds = new ArrayList<>(1);
            //just single type
            userIds.add(currentUserId);
            imService.getContactManager().reqGetDetaillUsers(userIds);
        }

        @Override
        public void onServiceDisconnected() {
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        imServiceConnector.disconnect(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        imServiceConnector.connect(getActivity());
        if (null != curView) {
            ((ViewGroup) curView.getParent()).removeView(curView);
            return curView;
        }
        curView = inflater.inflate(R.layout.tt_fragment_user_detail, topContentView);
        super.init(curView);
        showProgressBar();
        initRes();
        return curView;
    }

    @Override
    public void onResume() {
        Intent intent = getActivity().getIntent();
        if (null != intent) {
            String fromPage = intent.getStringExtra(IntentConstant.USER_DETAIL_PARAM);
            setTopLeftText(fromPage);
        }
        super.onResume();
    }

    /**
     * @Description 初始化资源
     */
    private void initRes() {
        // 设置标题栏
        setTopTitle(getActivity().getString(R.string.page_user_detail));
        setTopLeftButton(R.drawable.tt_top_back);
        topLeftContainerLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                getActivity().finish();
            }
        });
        setTopLeftText(getResources().getString(R.string.top_left_back));
    }

    @Override
    protected void initHandler() {
    }

    public void onEventMainThread(UserInfoEvent event) {
        switch (event) {
            case USER_INFO_UPDATE:
                UserEntity entity = imService.getContactManager().findContact(currentUserId);
                if (entity != null && currentUser.equals(entity)) {
                    initBaseProfile();
                    initDetailProfile();
                }
                break;
        }
    }


    private void initBaseProfile() {
        logger.d("detail#initBaseProfile");
        IMBaseImageView portraitImageView = (IMBaseImageView) curView.findViewById(R.id.user_portrait);

        setTextViewContent(R.id.nickName, currentUser.getMainName());
        setTextViewContent(R.id.userName, currentUser.getRealName());
        //头像设置
        portraitImageView.setDefaultImageRes(R.drawable.tt_default_user_portrait_corner);
        portraitImageView.setCorner(8);
        portraitImageView.setImageResource(R.drawable.tt_default_user_portrait_corner);
        portraitImageView.setImageUrl(UrlConstant.TOUXIANG_ADD + currentUserId + "_avator.jpg");

        portraitImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailPortraitActivity.class);
                intent.putExtra(IntentConstant.KEY_AVATAR_URL, currentUser.getAvatar());
                intent.putExtra(IntentConstant.KEY_IS_IMAGE_CONTACT_AVATAR, true);

                startActivity(intent);
            }
        });

        //-----------------------------tyc
        portraitImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (currentUserId == imService.getLoginManager().getLoginId()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Light_Dialog));
                    builder.setTitle("选择图片");
                    String[] items = new String[]{"相机拍摄", "本地图片"};

                    builder.setItems(items, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    doTakePhoto();
                                    break;
                                case 1:
                                    doPickPhotoFromGallery();
                                    break;
                            }
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(true);
                    alertDialog.show();

                }
                return false;
            }
        });

        // 设置界面信息
        Button chatBtn = (Button) curView.findViewById(R.id.chat_btn);
        if (currentUserId == imService.getLoginManager().getLoginId()) {
            chatBtn.setVisibility(View.GONE);
        } else {
            chatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    IMUIHelper.openChatActivity(getActivity(), currentUser.getSessionKey());
                    getActivity().finish();
                }
            });

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {  //返回成功
            switch (requestCode) {
                case PHOTO_WITH_CAMERA: {//拍照获取图片
                    String status = Environment.getExternalStorageState();
                    if (status.equals(Environment.MEDIA_MOUNTED)) { //是否有SD卡
                        Bitmap photo = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/image.jpg");
                        //imgName = createPhotoFileName();
                        //写一个方法将此文件保存到本应用下面
                        //savePicture(imgName, bitmap);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                        savePicture("image.jpg", photo);
                        byte[] bitmapByte = baos.toByteArray();
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), ClipPictureActivity.class);
                        //intent.putExtra("bitmap", bitmapByte);
                        intent.putExtra("picname", createPhotoFileName());

                        startActivityForResult(intent, PHOTO_CLIP);
//                        Toast.makeText(getActivity(), "已保存本应用的files文件夹下", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "没有SD卡", Toast.LENGTH_LONG).show();
                    }
                }
                break;
                case PHOTO_WITH_DATA: {//从图库中选择图片
                    ContentResolver resolver = getActivity().getContentResolver();
                    //照片的原始资源地址
                    Uri originalUri = data.getData();
                    //System.out.println(originalUri.toString());  //" content://media/external/images/media/15838 "
                    //setTextViewContent(R.id.userName, currentUser.getRealName());

                    try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                        byte[] bitmapByte = baos.toByteArray();
                        savePicture("image.jpg", photo);
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), ClipPictureActivity.class);
                        //intent.putExtra("bitmap", bitmapByte);
                        intent.putExtra("picname", createPhotoFileName());

                        startActivityForResult(intent, PHOTO_CLIP);
                        //imgName = createPhotoFileName();
                        //写一个方法将此文件保存到本应用下面
                        //savePicture(imgName,photo);
//                        Toast.makeText(getActivity(), "已保存本应用的files文件夹下", Toast.LENGTH_LONG).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        } else if (resultCode == PHOTO_CLIP) {
            setTextViewContent(R.id.userName, currentUser.getRealName());

            ImageLoader.getInstance().clearMemoryCache();
            ImageLoader.getInstance().clearDiskCache();
            IMBaseImageView portraitImageView = (IMBaseImageView) curView.findViewById(R.id.user_portrait);
            portraitImageView.setImageUrl(UrlConstant.TOUXIANG_ADD + currentUserId + "_avator.jpg");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 从相册获取图片
     **/
    public void doPickPhotoFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");  // 开启Pictures画面Type设定为image
        intent.setAction(Intent.ACTION_GET_CONTENT); //使用Intent.ACTION_GET_CONTENT这个Action
        startActivityForResult(intent, PHOTO_WITH_DATA); //取得相片后返回到本画面
    }

    /**
     * 拍照获取相片
     **/
    public void doTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //调用系统相机

        //Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"image.jpg"));
        Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "image.jpg"));

        //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        //直接使用，没有缩小
        startActivityForResult(intent, PHOTO_WITH_CAMERA);  //用户点击了从相机获取
    }

    /**
     * 保存图片到本应用下
     **/
    private void savePicture(String fileName, Bitmap bitmap) {

        FileOutputStream fos = null;
        //try {//直接写入名称即可，没有会被自动创建；私有：只有本应用才能访问，重新内容写入会被覆盖
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            File dir = new File(UrlConstant.TOUXIANG_ADD2);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File photoFile = new File(UrlConstant.TOUXIANG_ADD2, fileName); //在指定路径下创建文件
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (bitmap != null) {
                    if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                            fileOutputStream)) {
                        fileOutputStream.flush();
                    }
                }
            } catch (FileNotFoundException e) {
                photoFile.delete();
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 创建图片不同的文件名
     **/
    private String createPhotoFileName() {
        String fileName = "";
        fileName = imService.getLoginManager().getLoginId() + "_avator.jpg";
        return fileName;
    }

    //--------------------------------tyc

    private void initDetailProfile() {
        logger.d("detail#initDetailProfile");
        hideProgressBar();
        DepartmentEntity deptEntity = imService.getContactManager().findDepartment(currentUser.getDepartmentId());
        setTextViewContent(R.id.department, deptEntity.getDepartName());
        setTextViewContent(R.id.telno, currentUser.getPhone());
        setTextViewContent(R.id.email, currentUser.getEmail());

        View phoneView = curView.findViewById(R.id.phoneArea);
        View emailView = curView.findViewById(R.id.emailArea);
        IMUIHelper.setViewTouchHightlighted(phoneView);
        IMUIHelper.setViewTouchHightlighted(emailView);

        emailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUserId == IMLoginManager.instance().getLoginId())
                    return;
                IMUIHelper.showCustomDialog(getActivity(), View.GONE, String.format(getString(R.string.confirm_send_email), currentUser.getEmail()), new IMUIHelper.dialogCallback() {
                    @Override
                    public void callback() {
                        Intent data = new Intent(Intent.ACTION_SENDTO);
                        data.setData(Uri.parse("mailto:" + currentUser.getEmail()));
                        data.putExtra(Intent.EXTRA_SUBJECT, "");
                        data.putExtra(Intent.EXTRA_TEXT, "");
                        startActivity(data);
                    }
                });
            }
        });

        phoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUserId == IMLoginManager.instance().getLoginId())
                    return;
                IMUIHelper.showCustomDialog(getActivity(), View.GONE, String.format(getString(R.string.confirm_dial), currentUser.getPhone()), new IMUIHelper.dialogCallback() {
                    @Override
                    public void callback() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                IMUIHelper.callPhone(getActivity(), currentUser.getPhone());
                            }
                        }, 0);
                    }
                });
            }
        });

        setSex(currentUser.getGender());
    }

    private void setTextViewContent(int id, String content) {
        TextView textView = (TextView) curView.findViewById(id);
        if (textView == null) {
            return;
        }

        textView.setText(content);
    }

    private void setSex(int sex) {
        if (curView == null) {
            return;
        }

        TextView sexTextView = (TextView) curView.findViewById(R.id.sex);
        if (sexTextView == null) {
            return;
        }

        int textColor = Color.rgb(255, 138, 168); //xiaoxian
        String text = getString(R.string.sex_female_name);

        if (sex == DBConstant.SEX_MAILE) {
            textColor = Color.rgb(144, 203, 1);
            text = getString(R.string.sex_male_name);
        }

        sexTextView.setVisibility(View.VISIBLE);
        sexTextView.setText(text);
        sexTextView.setTextColor(textColor);
    }

}
