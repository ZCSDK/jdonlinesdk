package com.sobot.online.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sobot.network.http.HttpBaseUtils;
import com.sobot.online.R;
import com.sobot.online.base.SobotOnlineDialogBaseActivity;
import com.sobot.online.util.ImageUtils;
import com.sobot.online.util.SobotOnlineImageUtils;
import com.sobot.online.weight.gif.GifView2;
import com.sobot.online.weight.image.bigimage.PhotoView;
import com.sobot.online.weight.image.bigimage.PhotoViewAttacher;
import com.sobot.onlinecommon.utils.SobotMD5Utils;
import com.sobot.onlinecommon.utils.SobotOnlineLogUtils;
import com.sobot.onlinecommon.utils.SobotSizeUtils;
import com.sobot.pictureframe.SobotBitmapUtil;
import com.sobot.widget.ui.toast.SobotToastUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

//图片预览界面
public class SobotPhotoActivity extends SobotOnlineDialogBaseActivity implements View.OnClickListener {

    private PhotoView big_photo;
    private PhotoViewAttacher mAttacher;
    private GifView2 sobot_image_view;
    private RelativeLayout sobot_rl_gif;

    private TextView tv_save;
    String imageUrL;
    Bitmap bitmap;
    String sdCardPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        win.setAttributes(lp);
        big_photo = (PhotoView) findViewById(R.id.sobot_big_photo);
        sobot_image_view = (GifView2) findViewById(R.id.sobot_image_view);
        sobot_image_view.setIsCanTouch(true);
        sobot_rl_gif = (RelativeLayout) findViewById(R.id.sobot_rl_gif);
        sobot_rl_gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sobot_image_view.setLoadFinishListener(new GifView2.LoadFinishListener() {
            @Override
            public void endCallBack(String pathAbsolute) {
                showView(pathAbsolute);
            }
        });
        tv_save = findViewById(R.id.tv_save);
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = SobotBitmapUtil.compress(sdCardPath, getSobotActivity(), true);
                saveImageToGallery(getSobotActivity(), bitmap);
            }
        });
        initBundleData(savedInstanceState);
        SobotOnlineLogUtils.i("SobotPhotoActivity-------" + imageUrL);

        if (TextUtils.isEmpty(imageUrL)) {
            return;
        }

        if (imageUrL.startsWith("http")) {
            File dirPath = this.getImageDir(this);
            String encode = SobotMD5Utils.getMD5Str(imageUrL);
            File savePath = new File(dirPath, encode);
            sdCardPath = savePath.getAbsolutePath();
            if (!savePath.exists()) {
                displayImage(imageUrL, savePath, sobot_image_view);
            } else {
                showView(savePath.getAbsolutePath());
            }
        } else {
            File gifSavePath = new File(imageUrL);
            sdCardPath = imageUrL;
            if (gifSavePath.exists()) {
                showView(imageUrL);
            }
        }
        sobot_rl_gif.setVisibility(View.VISIBLE);
    }


    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_online_pop_photo;
    }


    @Override
    public void initView() {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void initData() {

    }

    public void saveImageToGallery(Context context, Bitmap bmp) {
        if (!isSdCardExist()) {
            return;
        }
        if (bmp == null) {
            return;
        }
        String savePath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File
                .separator + "Sobot";
        // 首先保存图片
        File appDir = new File(savePath, "sobot_pic");
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyUpdatePic(file, fileName);
    }

    // 最后通知图库更新
    public void notifyUpdatePic(File file, String fileName) {
        try {
            if (file != null && file.exists() && !TextUtils.isEmpty(fileName)) {
                MediaStore.Images.Media.insertImage(getSobotActivity().getContentResolver(), file.getAbsolutePath(), fileName, null);
                MediaScannerConnection.scanFile(getSobotActivity(), new String[]{file.toString()}, null, null);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        SobotToastUtil.showCustomToast(getSobotActivity(), getResources().getString(R.string.sobot_already_save_to_picture));
    }


    @Override
    public void onClick(View v) {
    }


    protected void initBundleData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            imageUrL = getIntent().getStringExtra("imageUrL");
        } else {
            imageUrL = savedInstanceState.getString("imageUrL");
        }

    }

    private void showView(String savePath) {
        tv_save.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(imageUrL)
                && (imageUrL.endsWith(".gif") || imageUrL.endsWith(".GIF"))) {
            showGif(savePath);
        } else {
            bitmap = SobotOnlineImageUtils.compress(savePath, getSobotContext(), true);
            //判断图片是否有旋转，有的话旋转后在发送（小米手机出现选择图库相片发送后和原生的图片方向不一致）
            try {
                int degree = ImageUtils.readPictureDegree(savePath);
                if (degree > 0) {
                    bitmap = ImageUtils.rotateBitmap(bitmap, degree);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            big_photo.setImageBitmap(bitmap);
            mAttacher = new PhotoViewAttacher(big_photo);
            mAttacher
                    .setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                        @Override
                        public void onPhotoTap(View view, float x, float y) {
                            SobotOnlineLogUtils.i("点击图片的时间：" + view + " x:" + x
                                    + "  y:" + y);
                            finish();
                        }
                    });
            mAttacher.update();
            big_photo.setVisibility(View.VISIBLE);
//                mAttacher.setOnLongClickListener(this);
        }
    }

    private void showGif(String savePath) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(savePath);
            bitmap = BitmapFactory.decodeFile(savePath);
//			sobot_image_view.setGifImageType(GifView.GifImageType.COVER);
            sobot_image_view.setGifImage(in, imageUrL);
            int screenWidth = SobotSizeUtils
                    .getMeasuredWidth(sobot_image_view);
            int screenHeight = SobotSizeUtils
                    .getMeasuredHeight(sobot_image_view);
            int w = SobotSizeUtils.dp2px(
                    bitmap.getWidth());
            int h = SobotSizeUtils.dp2px(
                    bitmap.getHeight());
            if (w == h) {
                if (w > screenWidth) {
                    w = screenWidth;
                    h = w;
                }
            } else {
                if (w > screenWidth) {

                    h = (int) (h * (screenWidth * 1.0f / w));
                    w = screenWidth;
                }
                if (h > screenHeight) {

                    w = (int) (w * (screenHeight * 1.0f / h));
                    h = screenHeight;

                }
            }
            SobotOnlineLogUtils.i("bitmap" + w + "*" + h);
//			sobot_image_view.setShowDimension(w, h);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    w, h);
            sobot_image_view.setLayoutParams(layoutParams);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        sobot_rl_gif.setVisibility(View.VISIBLE);
    }


    public void displayImage(String url, File saveFile, final GifView2 gifView) {
        // 下载图片
        HttpBaseUtils.getInstance().download(url, saveFile, new HttpBaseUtils.FileCallBack() {
            @Override
            public void onResponse(File file) {
                SobotOnlineLogUtils.i("down load onSuccess gif"
                        + file.getAbsolutePath());
                // 把图片文件打开为文件流，然后解码为bitmap
                showView(file.getAbsolutePath());
            }

            @Override
            public void onError(Exception e, String msg, int responseCode) {
                SobotOnlineLogUtils.w("图片下载失败:" + e.getMessage(), e);
            }

            @Override
            public void inProgress(int progress) {
                SobotOnlineLogUtils.i("gif图片下载进度:" + progress);
            }
        });
    }

    public File getFilesDir(Context context, String tag) {
        if (isSdCardExist() == true) {
            return context.getExternalFilesDir(tag);
        } else {
            return context.getFilesDir();
        }
    }

    public File getImageDir(Context context) {
        File file = getFilesDir(context, "images");
        return file;
    }

    public boolean isSdCardExist() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        sobot_image_view.pause();
        if (bitmap != null && bitmap.isRecycled() == false) {
            bitmap.recycle();
            System.gc();
        }
        super.onDestroy();
    }


    protected void onSaveInstanceState(Bundle outState) {
        //被摧毁前缓存一些数据
        outState.putString("imageUrL", imageUrL);
        super.onSaveInstanceState(outState);
    }
}