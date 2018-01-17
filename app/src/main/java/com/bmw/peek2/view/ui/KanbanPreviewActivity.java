package com.bmw.peek2.view.ui;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import com.bmw.peek2.R;
import com.bmw.peek2.view.view.CustomerVideoView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class KanbanPreviewActivity extends BaseActivity {

    @Bind(R.id.vdv_kanban)
    CustomerVideoView videoView;
    @Bind(R.id.sb_kanbanPlayer)
    SeekBar seekBar;
    private int duration;
    private boolean isFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanban_preview);
        ButterKnife.bind(this);

        String path = getIntent().getStringExtra("path");
        File file = new File(path);
        if(!file.exists()){
            toast("预览文件不存在！");
            return;
        }

        startPlayVideo(path);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                long time = mp.getDuration();
                duration = (int) time;
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        videoView.seekTo(duration);
                    }
                });
            }
        });



        seekBar.setOnSeekBarChangeListener(change);

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (!isFinish) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            log("current = " + videoView.getCurrentPosition() + " all = " + duration);
                            int currentPosition = videoView.getCurrentPosition();
                            if (duration != 0)
                                seekBar.setProgress(currentPosition * 100 / duration);
                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


    }


    private SeekBar.OnSeekBarChangeListener change = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 当进度条停止修改的时候触发
            // 取得当前进度条的刻度
            int progress = seekBar.getProgress();
            videoView.seekTo(progress * duration / 100);
            if(!videoView.isPlaying()){
                videoView.start();
            }


        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
//            Player.getInstance().pause(iPort,1);

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

        }
    };


    public void startPlayVideo(String mPath) {

        if (mPath == null) {
            toast("文件不存在！");
            return;
        }


        videoView.setVideoURI(Uri.parse(mPath));


        videoView.start();

    }

    @OnClick({R.id.img_goback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_goback:
                finish();
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isFinish = true;
    }
}
