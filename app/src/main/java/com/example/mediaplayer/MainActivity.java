package com.example.mediaplayer;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mediaplayer.R;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.Player;




import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SimpleExoPlayer exoPlayer;
    private PlayerView playerView;
    private Button play;

    private List<Integer> rawResourceIds = new ArrayList<>();
    private int currentResourceIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.player_view);
        play = findViewById(R.id.play);

        // Add your raw resource IDs to the list
        rawResourceIds.add(R.raw.makhna_me);
        rawResourceIds.add(R.raw.another_song);
        rawResourceIds.add(R.raw.remix);


        // Create a SimpleExoPlayer instance
        exoPlayer = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(exoPlayer);

        // Build the media source from the raw resource
        MediaSource mediaSource = buildRawResourceMediaSource(rawResourceIds.get(currentResourceIndex));

        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);

        play.setOnClickListener(view -> {
            if (exoPlayer.getPlayWhenReady()) {
                exoPlayer.setPlayWhenReady(false);
                play.setText("Play");
            } else {
                exoPlayer.setPlayWhenReady(true);
                play.setText("Pause");
            }
        });

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    // Move to the next song when playback ends
                    playNextSong();
                }
            }
        });



    }

    private void playNextSong() {
        currentResourceIndex = (currentResourceIndex + 1) % rawResourceIds.size();
        MediaSource mediaSource = buildRawResourceMediaSource(rawResourceIds.get(currentResourceIndex));
        exoPlayer.setMediaSource(mediaSource);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);
    }

    private MediaSource buildRawResourceMediaSource(int rawResourceId) {
        Uri uri = Uri.parse("rawresource://" + getPackageName() + "/" + rawResourceId);
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "ExoPlayerDemo"));
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.release();
        }
    }
}
