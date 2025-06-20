package com.sobot.online.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sobot.common.utils.SobotPathManager;
import com.sobot.network.http.HttpBaseUtils;
import com.sobot.network.http.db.SobotDownloadManager;
import com.sobot.network.http.download.SobotDownload;
import com.sobot.network.http.download.SobotDownloadListener;
import com.sobot.network.http.download.SobotDownloadTask;
import com.sobot.network.http.model.SobotProgress;
import com.sobot.online.R;
import com.sobot.online.base.SobotOnlineBaseActivity;
import com.sobot.online.util.FileOpenHelper;
import com.sobot.online.util.SobotFileTypeUtil;
import com.sobot.onlinecommon.api.apiutils.OnlineConstant;
import com.sobot.onlinecommon.model.SobotCacheFile;
import com.sobot.onlinecommon.utils.SobotFileSizeUtil;

import java.io.File;

//文件预览
public class SobotFileDetailActivity extends SobotOnlineBaseActivity implements View.OnClickListener {

    private TextView sobot_file_icon;
    private TextView sobot_file_name;
    private TextView sobot_tv_file_size;
    private TextView sobot_tv_progress;
    private TextView sobot_btn_start;
    private TextView sobot_tv_decribe;

    private LinearLayout sobot_ll_progress;
    private ProgressBar sobot_pb_progress;
    private TextView sobot_btn_cancel;

    private String mProgressStr;
    private SobotCacheFile mCacheFile;
    private SobotDownloadTask mTask;
    private SobotDownloadListener mDownloadListener;

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_activity_file_detail;
    }

    @Override
    protected void initView() {
        setHearderTitle(getResString("sobot_file_preview"));
        sobot_file_icon = (TextView) findViewById(R.id.sobot_file_icon);
        sobot_file_name = (TextView) findViewById(R.id.sobot_file_name);
        sobot_tv_file_size = (TextView) findViewById(R.id.sobot_tv_file_size);
        sobot_tv_progress = (TextView) findViewById(R.id.sobot_tv_progress);
        sobot_btn_start = (TextView) findViewById(R.id.sobot_btn_start);
        sobot_btn_start.setText(getString(R.string.sobot_file_download));
        sobot_ll_progress = (LinearLayout) findViewById(R.id.sobot_ll_progress);
        sobot_pb_progress = (ProgressBar) findViewById(R.id.sobot_pb_progress);
        sobot_btn_cancel = (TextView) findViewById(R.id.sobot_btn_cancel);
        sobot_tv_decribe = (TextView) findViewById(R.id.sobot_tv_decribe);

        mProgressStr = getResString("sobot_file_downloading");
        sobot_btn_start.setOnClickListener(this);
        sobot_btn_cancel.setOnClickListener(this);
        getHearderLeftView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mDownloadListener = new SobotDownloadListener(SobotDownload.CancelTagType.SOBOT_TAG_DOWNLOAD_ACT) {
            @Override
            public void onStart(SobotProgress progress) {
                refreshUI(progress);
            }

            @Override
            public void onProgress(SobotProgress progress) {
                refreshUI(progress);
            }

            @Override
            public void onError(SobotProgress progress) {
                refreshUI(progress);
            }

            @Override
            public void onFinish(File result, SobotProgress progress) {
                refreshUI(progress);
            }

            @Override
            public void onRemove(SobotProgress progress) {

            }
        };
    }

    @Override
    protected void initData() {
        try {
            Intent intent = getIntent();
            mCacheFile = (SobotCacheFile) intent.getSerializableExtra(OnlineConstant.SOBOT_INTENT_DATA_SELECTED_FILE);
            if (mCacheFile == null ) {
                return;
            }
            sobot_file_icon.setBackgroundResource(SobotFileTypeUtil.getFileIcon(getApplicationContext(), mCacheFile.getFileType()));
            sobot_file_name.setText(mCacheFile.getFileName());
            if (TextUtils.isEmpty(mCacheFile.getFileSize())) {
                SobotFileSizeUtil.getFileUrlSize(mCacheFile.getUrl(), new SobotFileSizeUtil.CallBack<String>() {
                    @Override
                    public void call(final String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCacheFile.setFileSize(s);
                                sobot_tv_file_size.setText(getResString("sobot_file_size")+"："+mCacheFile.getFileSize());
                            }
                        });

                    }
                });
            } else {
                sobot_tv_file_size.setText(getResString("sobot_file_size")+"："+mCacheFile.getFileSize());
            }
            SobotDownload.getInstance().setFolder(SobotPathManager.getInstance().getCacheDir());
//            LogUtils.i("initFolder:" +SobotPathManager.getInstance().getCacheDir()));
            if (!TextUtils.isEmpty(mCacheFile.getFilePath())) {
                showFinishUi();
            } else {
                restoreTask();
            }
        } catch (Exception e) {
            //ignor
        }
    }

    /**
     * 恢复任务
     */
    private void restoreTask() {
        //更新数据
        SobotProgress progress = SobotDownloadManager.getInstance().get(mCacheFile.getUrl());
        if (progress != null) {
            mTask = SobotDownload.restore(progress).register(mDownloadListener);
            refreshUI(mTask.progress);
        } else {
            showCommonUi();
        }
    }

    /**
     * 根据任务状态显示对应的ui
     *
     * @param progress
     */
    private void refreshUI(SobotProgress progress) {
        switch (progress.status) {
            case SobotProgress.NONE:
            case SobotProgress.PAUSE:
            case SobotProgress.ERROR:
                showCommonUi();
                break;
            case SobotProgress.WAITING:
                showCommonUi();
                break;
            case SobotProgress.LOADING:
                showLoadingUi(progress.fraction, progress.currentSize, progress.totalSize);
                break;
            case SobotProgress.FINISH:
                showFinishUi();
                mCacheFile.setFilePath(progress.filePath);
                break;
        }
    }

    private void showLoadingUi(float fraction, long pcurrentSize, long ptotalSize) {
        sobot_btn_start.setVisibility(View.GONE);
        sobot_tv_decribe.setVisibility(View.GONE);
        sobot_tv_file_size.setVisibility(View.GONE);
        sobot_tv_progress.setVisibility(View.VISIBLE);
        sobot_ll_progress.setVisibility(View.VISIBLE);
        String currentSize = Formatter.formatFileSize(this, pcurrentSize);
        String totalSize = Formatter.formatFileSize(this, ptotalSize);
        sobot_tv_progress.setText(mProgressStr+"..."+currentSize+"/"+totalSize);
        sobot_pb_progress.setProgress((int) (fraction * 100));
    }

    private void showCommonUi() {
        sobot_btn_start.setSelected(false);
        sobot_btn_start.setText(getResString("sobot_file_download"));
        sobot_tv_file_size.setVisibility(View.VISIBLE);
        sobot_tv_progress.setVisibility(View.GONE);
        sobot_btn_start.setVisibility(View.VISIBLE);
        sobot_tv_decribe.setVisibility(View.GONE);
        sobot_ll_progress.setVisibility(View.GONE);
    }

    private void showFinishUi() {
        sobot_tv_file_size.setVisibility(View.VISIBLE);
        sobot_tv_progress.setVisibility(View.GONE);
        sobot_btn_start.setText(getResString("sobot_file_open"));
        sobot_btn_start.setVisibility(View.VISIBLE);
        sobot_tv_decribe.setVisibility(View.VISIBLE);
        sobot_ll_progress.setVisibility(View.GONE);
        sobot_btn_start.setSelected(true);
    }


    @Override
    public void onClick(View v) {
        if (v == sobot_btn_cancel) {
            //取消任务
            showCommonUi();
            if (mTask != null) {
                mTask.remove(true);
            }
        }

        if (v == sobot_btn_start) {
            if (sobot_btn_start.isSelected()) {
                //打开文件
                if (mCacheFile != null) {
                    File file = new File(mCacheFile.getFilePath());
                    if (!file.exists()) {
                        showCommonUi();
                        mCacheFile.setFilePath(null);
                        if (mTask != null) {
                            mTask.remove(true);
                        }
                    } else {
                        // 打开文件
                        FileOpenHelper.openFileWithType(getSobotContext(), file);
                    }
                }
            } else {
                if (mTask != null) {
                    if (mTask.progress.isUpload) {
                        mTask.remove(true);
                    } else {
                        mTask.progress.request = HttpBaseUtils.getInstance().obtainGetRequest(mCacheFile.getUrl(), null,null);
                    }
                }
                mTask = HttpBaseUtils.getInstance().addDownloadFileTask(mCacheFile.getUrl(), mCacheFile.getUrl(), mCacheFile.getFileName(), null,null);
                if (mTask != null) {
                    mTask.register(mDownloadListener).start();
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        SobotDownload.getInstance().unRegister(SobotDownload.CancelTagType.SOBOT_TAG_DOWNLOAD_ACT);
        if (mTask != null && (mTask.progress.status == SobotProgress.FINISH
                || mTask.progress.status == SobotProgress.NONE
                || mTask.progress.status == SobotProgress.ERROR)) {
            SobotDownload.getInstance().removeTask(mTask.progress.tag);
        }
        super.onDestroy();
    }

}