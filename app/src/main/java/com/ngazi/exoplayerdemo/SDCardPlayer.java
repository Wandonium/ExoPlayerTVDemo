package com.ngazi.exoplayerdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import java.io.File;

public class SDCardPlayer extends Activity {

    private StyledPlayerView playerView;
    private ExoPlayer player;
    private Context context;

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
        player.release();
        player = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopMedia();
    }
}