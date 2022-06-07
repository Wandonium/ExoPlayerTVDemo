package com.ngazi.exoplayerdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import java.io.File;

public class SDCardPlayer extends Activity {

    private static final String LOG_TAG = SDCardPlayer.class.getCanonicalName();

    private StyledPlayerView playerView;
    private ExoPlayer player;
    private Context context;
    private Handler handler;
    private Runnable[] runnables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdcard_player);
        playerView = findViewById(R.id.player_view);
        context = getApplicationContext();
    }

    private void initializePlayer() {
        player = new ExoPlayer.Builder(context).build();

        playerView.setPlayer(player);
        String filepath = Environment.getExternalStorageDirectory() + File.separator + "Download"
                + File.separator + "vid.mp4";
        Log.e("filepath: ", filepath);
        Uri videoUri = Uri.parse(filepath);
        // Build the media item.
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        // Set the media item to be played.
        player.setMediaItem(mediaItem);
        // Prepare the player.
        player.prepare();
        // Start the playback.
        player.play();
        handler = new Handler(Looper.getMainLooper());
        runnables = new Runnable[1];
        final int[] second = {0};
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Player.Listener.super.onPlayerStateChanged(playWhenReady, playbackState);
                if(playWhenReady && playbackState == Player.STATE_READY) {
                    Log.d(LOG_TAG, "PLAYBACK IS RESUMED!!!");
                    final boolean[] doSqueezeBack = {true};
                    handler.postDelayed( runnables[0] = new Runnable() {
                        @Override
                        public void run() {
                            if(player == null) {
                                Log.e(LOG_TAG, "FATAL ERROR! Player is equal to null!");
                            } else {
                                if(second[0] != 0 && second[0] % 10 == 0 && doSqueezeBack[0]) {
//                                    squeezeBack();
//                                    playerView.measure(0, 0);
                                    int initialWidth = playerView.getWidth();
                                    int initialHeight = playerView.getHeight();
                                    Log.d(LOG_TAG, "Current player view width: " + initialWidth);
                                    Log.d(LOG_TAG, "Current player view height: " + initialHeight);
                                    int squeezeBackWidth = (int) ((int) initialWidth * 0.75);
                                    int squeezeBackHeight = (int) ((int) initialHeight * 0.75);
                                    Log.d(LOG_TAG, "Squeeze back player view width: " + squeezeBackWidth);
                                    Log.d(LOG_TAG, "Squeeze back player view height: " + squeezeBackHeight);
                                    Log.d(LOG_TAG, "doSqueezeBack: " + doSqueezeBack[0]);
                                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) playerView.getLayoutParams();
                                    params.width = squeezeBackWidth;
                                    params.height = squeezeBackHeight;
                                    params.leftMargin = 63;
                                    playerView.setLayoutParams(params);
                                    doSqueezeBack[0] = false;
                                }
                                second[0]++;
                                Log.d("current position ", Long.toString(player.getCurrentPosition()));
                                Log.d("current second: ", Integer.toString(second[0]));
                            }
                            handler.postDelayed(runnables[0], 1000);
                        }
                    }, 1000);
                } else {
                    handler.removeCallbacks(runnables[0]);
                    Log.d(LOG_TAG, "PLAYBACK IS PAUSED!!!");
                }
            }

        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        playerView.showController();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(player == null) {
            initializePlayer();
        }
    }

    private void stopMedia() {
        if(player == null) {
            return;
        }
        handler.removeCallbacks(runnables[0]);
        player.release();
        player = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopMedia();
    }
}