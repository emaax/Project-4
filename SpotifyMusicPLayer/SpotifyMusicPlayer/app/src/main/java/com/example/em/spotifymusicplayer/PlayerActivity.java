package com.example.em.spotifymusicplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.em.spotifymusicplayer.Activity.MainActivity;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.PlaybackBitrate;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.PlayerStateCallback;
import com.spotify.sdk.android.player.Spotify;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class PlayerActivity extends AppCompatActivity implements
        PlayerNotificationCallback {
    protected static final String CLIENT_ID = "1b83b9968c1e468bb7e72686a240f94c";
    private static final String REDIRECT_URI = "emaax-android-app-login://callback";
    private static String accessToken;
    private static final int REQUEST_CODE = 1337;

    private android.support.v7.app.ActionBar actionBar;
    private ImageView backgroundImageView;
    TextView artistNameView;
    private TextView albumNameView;
    private ImageView trackImageView;
    private TextView trackNameView;
    private TextView currentDuration;
    private SeekBar seekBarView;
    private TextView finalDuration;
    private ImageButton prevButton;
    private ImageButton playButton;
    private ImageButton nextButton;
    private ProgressBar spinner;
    private ImageButton pauseButton;
    private PlayerState state;
    private Player player;

    private Boolean isPlaying;
    private int songPosition;
    String imageUrl;
    private Player mPlayer;

    private static final String KEY_CURRENT_QUERY = "CURRENT_QUERY";
    TopTrackData topTrackData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topTrackData = (TopTrackData) getIntent().getParcelableExtra(Intent.EXTRA_TEXT);


        // set title in the actionbar
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle((topTrackData).trackName);
        actionBar.setSubtitle((topTrackData).trackAlbum);

        setContentView(R.layout.activity_player);
        buildPlayer();

        artistNameView = (TextView) findViewById(R.id.text_view_artist_name);
        albumNameView = (TextView) findViewById(R.id.text_view_album_name);
        trackImageView = (ImageView) findViewById(R.id.image_view_track);
        trackNameView = (TextView) findViewById(R.id.text_view_track_name);
        currentDuration = (TextView) findViewById(R.id.text_view_current_duration);
        seekBarView = (SeekBar) findViewById(R.id.seek_bar_seekBar);
        finalDuration = (TextView) findViewById(R.id.text_view_final_duration);
        playButton = (ImageButton) findViewById(R.id.image_btn_play);
        prevButton = (ImageButton) findViewById(R.id.image_btn_prev);
        nextButton = (ImageButton) findViewById(R.id.image_btn_next);
        pauseButton = (ImageButton) findViewById(R.id.image_btn_pause);
        setViews();

        // Progress Bar to display loading while everything is being set up
        spinner.setVisibility(View.VISIBLE);
    }
    private void setViews() {

        artistNameView.setText((topTrackData.trackArtist));
        albumNameView.setText((topTrackData.trackAlbum));
        trackNameView = (TextView) findViewById(R.id.text_view_track_name);
        currentDuration.setText((topTrackData.trackDuration));
        seekBarView = (SeekBar) findViewById(R.id.seek_bar_seekBar);
        playButton = (ImageButton) findViewById(R.id.image_btn_play);
        prevButton = (ImageButton) findViewById(R.id.image_btn_prev);
        nextButton = (ImageButton) findViewById(R.id.image_btn_next);
        pauseButton = (ImageButton) findViewById(R.id.image_btn_pause);
        imageUrl = TopTenTracksActivity.topTenTrackList.get(songPosition).trackImageLarge;
        trackImageView.setImageURI(Uri.parse(imageUrl));
        prevButton();
        nextButton();

    }
        public void buildPlayer () {
            //start a Spotify player
            Config playerConfig = new Config(this, accessToken, CLIENT_ID);
            player = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                @Override
                public void onInitialized(Player p) {
                    player.addPlayerNotificationCallback(PlayerActivity.this);
                    player.setPlaybackBitrate(PlaybackBitrate.BITRATE_NORMAL);
                    player.getPlayerState(new PlayerStateCallback() {
                        @Override
                        public void onPlayerState(PlayerState playerState) {
                            state = playerState;
                        }
                    });
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e("PlayerActivity", "Could not initialize player: " + throwable.getMessage());
                }
            });
        }



    private void nextButton() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);
                songPosition = songPosition + 1;
                if (songPosition > TopTenTracksActivity.topTenTrackList.size() - 1) {
                    songPosition = 0;
                }
                setViews();
                playButton.setImageResource(R.drawable.ic_skip_next_black_24dp);


                prepareMusic();
            }
        });
    }

    private void prepareMusic() {

        isPlaying = false;

        // disable until prepared
        playButton.setClickable(true);
        playButton.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
        // get track
        final String trackUrl = TopTenTracksActivity.topTenTrackList.get(songPosition).trackUrl;

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    mPlayer.resume();
                    playButton.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
                    isPlaying = true;

                    mPlayer.getPlayerState(new PlayerStateCallback() {
                        @Override
                        public void onPlayerState(PlayerState playerState) {
                            int progress = playerState.positionInMs;

                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();

                            // put the song's metadata
                            bundle.putString("track", TopTenTracksActivity.topTenTrackList.get(songPosition).trackName);
                            bundle.putString("artist", TopTenTracksActivity.topTenTrackList.get(songPosition).trackArtist);
                            bundle.putString("album", TopTenTracksActivity.topTenTrackList.get(songPosition).trackAlbum);
                            bundle.putString("track url", TopTenTracksActivity.topTenTrackList.get(songPosition).trackUrl);

                            // put the song's total duration (in ms)
                            bundle.putLong("duration", Integer.parseInt(TopTenTracksActivity.topTenTrackList.get(songPosition).trackDuration));

                            // put the song's current position
                            bundle.putLong("position", progress);

                            // put the playback status
                            bundle.putBoolean("playing", true);


                            intent.putExtras(bundle);
                            getIntent().getParcelableExtra(Intent.EXTRA_TEXT);
                        }
                    });

                } else {
                    mPlayer.pause();
                    isPlaying = false;
                    playButton.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);


                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();

                    // put the song's metadata
                    bundle.putString("track", TopTenTracksActivity.topTenTrackList.get(songPosition).trackName);
                    bundle.putString("artist", TopTenTracksActivity.topTenTrackList.get(songPosition).trackArtist);
                    bundle.putString("album", TopTenTracksActivity.topTenTrackList.get(songPosition).trackAlbum);

                    // put the song's total duration (in ms)
                    bundle.putLong("duration", Integer.parseInt(TopTenTracksActivity.topTenTrackList.get(songPosition).trackDuration)); // 4:05

                    // put the playback status
                    bundle.putBoolean("playing", false);

                    intent.putExtras(bundle);
                    getIntent().getParcelableExtra(Intent.EXTRA_TEXT);

                }
            }
        });
    }

    private void prevButton() {
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);
                songPosition = songPosition - 1;
                if (songPosition < 0) {
                    songPosition = 0;
                }
                setViews();
                playButton.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);

                prepareMusic();
            }
        });
    }


    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {

    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {

    }
}