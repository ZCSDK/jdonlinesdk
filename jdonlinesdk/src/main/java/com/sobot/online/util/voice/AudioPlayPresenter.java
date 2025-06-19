package com.sobot.online.util.voice;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.widget.ImageView;

import com.sobot.common.utils.SobotPathManager;
import com.sobot.network.http.HttpBaseUtils;
import com.sobot.online.R;
import com.sobot.online.weight.toast.SobotToastUtil;
import com.sobot.onlinecommon.gson.SobotGsonUtil;
import com.sobot.onlinecommon.model.ChatMessageAudioModel;
import com.sobot.onlinecommon.model.ChatMessageModel;
import com.sobot.onlinecommon.utils.SobotOnlineLogUtils;

import java.io.File;
import java.io.IOException;

public class AudioPlayPresenter {

    private Context mContent;
    private ChatMessageModel mCurrentMsg;
    private ImageView mIvCurrentAudioPlay;
    private AudioPlayCallBack mCallbak;

    public AudioPlayPresenter(Context content) {
        this.mContent = content;
    }

    public synchronized void clickAudio(final ChatMessageModel message, AudioPlayCallBack callbak, ImageView ivAudioPlay) {
        if (AudioTools.getInstance().isPlaying()) {
            AudioTools.stop();// 停止语音的播放
        }
        this.mCallbak = callbak;
        if (mCurrentMsg != message) {
            if (mCurrentMsg != null) {
                mCurrentMsg.setVoideIsPlaying(false);
                if (mCallbak != null) {
                    mCallbak.onPlayEnd(mCurrentMsg, mIvCurrentAudioPlay);
                    mCurrentMsg = null;
                    mIvCurrentAudioPlay = null;
                }
            }
            playVoiceByPath(message, ivAudioPlay);
        } else {
            // 点击同一个的元素
            AudioTools.stop();// 停止语音的播放
            message.setVoideIsPlaying(false);
            if (mCallbak != null) {
                mCallbak.onPlayEnd(message, ivAudioPlay);
                mCurrentMsg = null;
                mIvCurrentAudioPlay = null;
            }
        }
    }

    private void playVoiceByPath(final ChatMessageModel message, final ImageView ivAudioPlay) {
        ChatMessageAudioModel messageAudioModel = null;
        if (message.getMessage() != null && message.getMessage().getContent() != null) {
            String temp = SobotGsonUtil.gsonString(message.getMessage().getContent());
            if (!TextUtils.isEmpty(temp)) {
                messageAudioModel = SobotGsonUtil.gsonToBean(temp, ChatMessageAudioModel.class);
            }
        }
        final String path = messageAudioModel.getUrl();
        String contentPath;
        if (!TextUtils.isEmpty(path)) {
            if (message.getIsHistory() == 1) {
                //是历史记录  就创建文件夹进行下载
                contentPath = path.substring(path.lastIndexOf("/") + 1, path.length());
                String tmpFilePath = SobotPathManager.getInstance().getVoiceDir() + contentPath;
                File directory = new File(tmpFilePath).getParentFile();
                if (!directory.exists() && !directory.mkdirs()) {
                    try {
                        boolean success = directory.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                contentPath = tmpFilePath;
            } else {
                contentPath = path;
            }
            SobotOnlineLogUtils.i("contentPath：" + contentPath);
            final File file = new File(contentPath);
            if (!file.exists()) {
                // 下载
                if (TextUtils.isEmpty(path) || !path.startsWith("http")) {
                    SobotToastUtil.showCustomToast(mContent, mContent.getResources().getString(R.string.sobot_voice_file_error));
                    return;
                }
                HttpBaseUtils.getInstance().download(path, file, new HttpBaseUtils.FileCallBack() {

                    @Override
                    public void onResponse(File response) {
                        playVoice(message, response, ivAudioPlay);
                    }

                    @Override
                    public void onError(Exception e, String msg, int responseCode) {
                        e.printStackTrace();
                    }

                    @Override
                    public void inProgress(int progress) {
                    }
                });

            } else {
                //直接拿地址播放
                playVoice(message, file, ivAudioPlay);
            }
        }
    }

    private void playVoice(final ChatMessageModel message, File voidePath, final ImageView ivAudioPlay) {
        try {
            AudioTools.getInstance();
            if (AudioTools.getIsPlaying()) {
                AudioTools.stop();
            }
            AudioTools.getInstance().setAudioStreamType(
                    AudioManager.STREAM_MUSIC);

            AudioTools.getInstance().reset();
            // 设置要播放的文件的路径
            AudioTools.getInstance().setDataSource(voidePath.toString());
            // 准备播放
            // AudioTools.getInstance().prepare();
            AudioTools.getInstance().prepareAsync();
            // 开始播放
            // mMediaPlayer.start();
            AudioTools.getInstance().setOnPreparedListener(
                    new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                            message.setVoideIsPlaying(true);
                            if (mCallbak != null) {
                                mCurrentMsg = message;
                                mIvCurrentAudioPlay = ivAudioPlay;
                                mCallbak.onPlayStart(message, ivAudioPlay);
                            }
                        }
                    });
            // 这在播放的动画
            AudioTools.getInstance().setOnCompletionListener(
                    new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer arg0) {
                            // 停止播放
                            message.setVoideIsPlaying(false);
                            AudioTools.getInstance().stop();
                            SobotOnlineLogUtils.i("----语音播放完毕----");
                            if (mCallbak != null) {
                                mCallbak.onPlayEnd(message, ivAudioPlay);
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            SobotOnlineLogUtils.i("音频播放失败");
            message.setVoideIsPlaying(false);
            AudioTools.getInstance().stop();
            if (mCallbak != null) {
                mCallbak.onPlayEnd(message, ivAudioPlay);
            }
        }
    }
}