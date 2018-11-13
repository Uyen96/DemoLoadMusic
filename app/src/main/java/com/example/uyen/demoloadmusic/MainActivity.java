package com.example.uyen.demoloadmusic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static android.media.MediaExtractor.MetricsConstants.FORMAT;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {
    private static final int TIME_DELAY = 100;
    private static final int TIME_UPDATE = 500;
    private static final int ID_NOTIFICATION = 1;
    private static final String TIME_FORMAT = "mm:ss";
    private static int pos = 0;

    private TextView mTextView;
    private TextView mTextTimeCurrent;
    private TextView mTextTimeTotal;
    private SeekBar mSeekBarSong;
    private ImageButton mButtonBack;
    private ImageButton mButtonChangeState;
    private ImageButton mButtonNext;

    private List<Song> mSongs;
    private MediaPlayer mPlayer;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        addSongs();
        initMediaPlayer();
        mPlayer.start();
    }

    public void initView() {
        mTextTimeCurrent = findViewById(R.id.text_start);
        mTextTimeTotal = findViewById(R.id.text_stop);
        mSeekBarSong = findViewById(R.id.seek_bar);
        mButtonBack = findViewById(R.id.button_back);
        mButtonChangeState = findViewById(R.id.button_change_state);
        mButtonNext = findViewById(R.id.button_next);
        mTextView = findViewById(R.id.text_name);
        mSeekBarSong.setOnSeekBarChangeListener(this);
        mButtonChangeState.setOnClickListener(this);
        mButtonBack.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);
    }

    public void addSongs() {
        mSongs = new ArrayList<>();
        mSongs.add(new Song(Constants.NAME_SONG_1, R.raw.votinh));
        mSongs.add(new Song(Constants.NAME_SONG_2, R.raw.chap_nhan));
        mSongs.add(new Song(Constants.NAME_SONG_3, R.raw.chuyen_tinh_toi));
        mSongs.add(new Song(Constants.NAME_SONG_4, R.raw.hongkong1));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_change_state :
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                    mButtonChangeState.setImageResource(R.drawable.ic_pause);
                    return;
                }
                mPlayer.start();
                mButtonChangeState.setImageResource(R.drawable.ic_play);
                break;
            case R.id.button_next:
                mPlayer.stop();
                pos++;
                if (pos > mSongs.size() - 1) {
                    pos = 0;
                }
                initMediaPlayer();
                mPlayer.start();
                updateTimeSong();
                break;
            case R.id.button_back:
                mPlayer.stop();
                pos--;
                if (pos < 0) {
                    pos = mSongs.size() - 1;
                }
                initMediaPlayer();
                mPlayer.start();
            default:
                break;
        }
    }

    public void initMediaPlayer() {
        mPlayer = MediaPlayer.create(this, mSongs.
                get(pos).getFile());
        mTextView.setText(mSongs.get(pos).getName());
        setTimeTotal();
        updateTimeSong();
    }

    public void setTimeTotal() {
        SimpleDateFormat time = new SimpleDateFormat(TIME_FORMAT);
        mTextTimeTotal.setText(time.format(mPlayer.getDuration()));
        mSeekBarSong.setMax(mPlayer.getDuration());
    }

    public void updateTimeSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat time = new SimpleDateFormat(TIME_FORMAT);
                mTextTimeCurrent.setText(time.format(mPlayer.getCurrentPosition()));
                mSeekBarSong.setProgress(mPlayer.getCurrentPosition());
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        pos++;
                        if (pos > mSongs.size() - 1) {
                            pos = 0;
                        }
                        initMediaPlayer();
                        mPlayer.start();
                        updateTimeSong();
                    }
                });
                handler.postDelayed(this, TIME_UPDATE);
            }
        }, TIME_DELAY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, SongService.class));
        buildNotification();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(this, SongService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNotificationManager.cancel(ID_NOTIFICATION);
    }

    private void buildNotification() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),
                1, intent, 0);
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.music_icon)
                .setContentTitle(Constants.NOTIFICATION_TITLE)
                .setDeleteIntent(pendingIntent);

        mNotificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(ID_NOTIFICATION, builder.build());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mPlayer.seekTo(seekBar.getProgress());
    }
}
