package com.sobot.online.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.sobot.common.utils.SobotPathManager;
import com.sobot.online.R;
import com.sobot.online.base.SobotOnlineBaseActivity;
import com.sobot.online.weight.camera.StCameraView;
import com.sobot.online.weight.camera.listener.StCameraListener;
import com.sobot.online.weight.camera.listener.StClickListener;
import com.sobot.online.weight.camera.listener.StErrorListener;
import com.sobot.online.weight.camera.util.FileUtil;
import com.sobot.onlinecommon.ui.statusbar.SobotStatusBarCompat;
import com.sobot.widget.ui.permission.SobotPermissionListenerImpl;

/**
 * 拍照录像界面
 */
public class SobotCameraActivity extends SobotOnlineBaseActivity {
    private static final String EXTRA_ACTION_TYPE = "EXTRA_ACTION_TYPE";
    private static final String EXTRA_IMAGE_FILE_PATH = "EXTRA_IMAGE_FILE_PATH";
    private static final String EXTRA_VIDEO_FILE_PATH = "EXTRA_VIDEO_FILE_PATH";
    public static final int RESULT_CODE = 103;

    public static final int ACTION_TYPE_PHOTO = 0;
    public static final int ACTION_TYPE_VIDEO = 1;

    private StCameraView jCameraView;

    /**
     * @param context 应用程序上下文
     * @return
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, SobotCameraActivity.class);
    }

    /**
     * 获取快照
     *
     * @param intent
     * @return
     */
    public static String getSelectedImage(Intent intent) {
        return intent.getStringExtra(EXTRA_IMAGE_FILE_PATH);
    }

    /**
     * 获取视频路径
     *
     * @param intent
     * @return
     */
    public static String getSelectedVideo(Intent intent) {
        return intent.getStringExtra(EXTRA_VIDEO_FILE_PATH);
    }

    /**
     * 获取动作类型
     *
     * @param intent
     * @return
     */
    public static int getActionType(Intent intent) {
        return intent.getIntExtra(EXTRA_ACTION_TYPE, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //setContentView(ResourceUtils.getIdByName(SobotCameraActivity.this, "layout", "sobot_activity_camera"));
        //MyApplication.getInstance().addActivity(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
//        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else if (Build.VERSION.SDK_INT >= 16) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        jCameraView.onPause();
    }

    @Override
    protected void onDestroy() {
        if(null!=jCameraView){
            jCameraView.setErrorLisenter(null);
        }
        super.onDestroy();
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_activity_camera;
    }

    @Override
    protected void initView() {
        jCameraView = (StCameraView) findViewById(R.id.sobot_cameraview);
        //设置视频保存路径
        jCameraView.setSaveVideoPath(SobotPathManager.getInstance().getVideoDir());
        jCameraView.setFeatures(StCameraView.BUTTON_STATE_BOTH);
        jCameraView.setTip(getString(R.string.online_camera_tip));
        jCameraView.setMediaQuality(StCameraView.MEDIA_QUALITY_MIDDLE);
        jCameraView.setErrorLisenter(new StErrorListener() {
            @Override
            public void onError() {
                //错误监听
                finish();
            }

            @Override
            public void AudioPermissionError() {
                permissionListener = new SobotPermissionListenerImpl() {
                };
                if (checkIsShowPermissionPop(getString(R.string.sobot_microphone), getString(R.string.sobot_microphone_yongtu), 2,3)) {
                } else {
                    if (!checkAudioPermission()) {
                    }
                }
            }
        });
        //JCameraView监听
        jCameraView.setJCameraLisenter(new StCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
                Intent intent = new Intent();
                intent.putExtra(EXTRA_ACTION_TYPE, ACTION_TYPE_PHOTO);
                if (bitmap != null) {
                    String path = FileUtil.saveBitmap(100, bitmap);
                    intent.putExtra(EXTRA_IMAGE_FILE_PATH, path);
                }
                setResult(RESULT_CODE, intent);
                finish();
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                //获取视频路径
                Intent intent = new Intent();
                intent.putExtra(EXTRA_ACTION_TYPE, ACTION_TYPE_VIDEO);
                if (firstFrame != null) {
                    String path = FileUtil.saveBitmap(80, firstFrame);
                    intent.putExtra(EXTRA_IMAGE_FILE_PATH, path);
                }
                intent.putExtra(EXTRA_VIDEO_FILE_PATH, url);
                setResult(RESULT_CODE, intent);
                finish();
            }
        });

        jCameraView.setLeftClickListener(new StClickListener() {
            @Override
            public void onClick() {
                SobotCameraActivity.this.finish();
            }
        });

        SobotStatusBarCompat.setNavigationBarColor(this, 0x33000000);
    }

    @Override
    protected void initData() {

    }

}
