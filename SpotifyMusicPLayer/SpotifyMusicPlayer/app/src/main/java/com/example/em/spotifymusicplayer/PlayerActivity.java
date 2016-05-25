package com.example.em.spotifymusicplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.PlaybackBitrate;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.PlayerStateCallback;
import com.spotify.sdk.android.player.Spotify;
import com.squareup.picasso.Picasso;

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
    String trackUrl;
    private Handler seekHandler = new Handler();
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

        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle((topTrackData).trackName);
        actionBar.setSubtitle((topTrackData).trackArtist);

        setContentView(R.layout.activity_player);
        initViews();
        setViews();
        buildPlayer();

    }

    private void initViews() {
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
    }

    private void setViews() {

        artistNameView.setText((topTrackData.trackArtist));
        albumNameView.setText((topTrackData.trackAlbum));
        trackNameView.setText((topTrackData.trackName));
        currentDuration.setText((topTrackData.trackDuration));

        imageUrl = topTrackData.trackImageLarge;
        Log.i("PlayerActivity", "setViews: " + imageUrl);
        Picasso.with(this).load(Uri.parse(imageUrl)).into(trackImageView);

        prevButton();
        nextButton();

    }

    public void buildPlayer() {
        //Configure the Spotify Player
        Config playerConfig = new Config(this, accessToken, CLIENT_ID);
        player = Spotify.getPlayer(playerConfig, MainActivity.class, new Player.InitializationObserver() {
            @Override
            public void onInitialized(Player p) {

                p.addPlayerNotificationCallback(PlayerActivity.this);
                p.setPlaybackBitrate(PlaybackBitrate.BITRATE_NORMAL);
                prepareMusic();

                p.getPlayerState(new PlayerStateCallback() {
                    @Override
                    public void onPlayerState(PlayerState playerState) {
                        state = playerState;
                    }
                });
                player = p;
                setSeekBar();

            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("PlayerActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });
    }

    private void setSeekBar() {
        if (player != null) {
            player.getPlayerState(new PlayerStateCallback() {
                @Override
                public void onPlayerState(PlayerState playerState) {
                    seekBarView.setProgress(playerState.positionInMs);
                    int seconds = ((playerState.positionInMs / 1000) % 60);
                    int minutes = ((playerState.positionInMs / 1000) / 60);
                    if (seconds < 10) {
                        currentDuration.setText(String.valueOf(minutes) + ":0" + String.valueOf(seconds));
                    } else {
                        currentDuration.setText(String.valueOf(minutes) + ":" + String.valueOf(seconds));
                    }
                }
            });
        }
        seekHandler.postDelayed(run, 1000);
    }
    Runnable run = new Runnable() {
        @Override
        public void run() {
            setSeekBar();
        }
    };

    private void prepareMusic() {

        isPlaying = false;

        playButton.setClickable(true);
        playButton.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    player.play(topTrackData.trackUrl);
                    player.resume();

                    isPlaying = true;
                    playButton.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);

                    player.getPlayerState(new PlayerStateCallback() {
                        @Override
                        public void onPlayerState(PlayerState playerState) {
                            int progress = playerState.positionInMs;

                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            // metadata of the song
                            bundle.putString("track", TopTenTracksActivity.topTenTrackList.get(songPosition).trackName);
                            bundle.putString("artist", TopTenTracksActivity.topTenTrackList.get(songPosition).trackArtist);
                            bundle.putString("album", TopTenTracksActivity.topTenTrackList.get(songPosition).trackAlbum);
                            bundle.putString("track url", TopTenTracksActivity.topTenTrackList.get(songPosition).trackUrl);

                            // Get the song's total duration (in ms)
                            bundle.putLong("duration", Integer.parseInt(TopTenTracksActivity.topTenTrackList.get(songPosition).trackDuration));

                            // current position of the song
                            bundle.putLong("position", progress);

                            // playback status
                            bundle.putBoolean("playing", true);

                            intent.putExtras(bundle);
                            getIntent().getParcelableExtra(Intent.EXTRA_TEXT);
                        }
                    });

                } else {

                    player.pause();
                    isPlaying = false;
                    playButton.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);


                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();

                    // metadata of the song
                    bundle.putString("track", TopTenTracksActivity.topTenTrackList.get(songPosition).trackName);
                    bundle.putString("artist", TopTenTracksActivity.topTenTrackList.get(songPosition).trackArtist);
                    bundle.putString("album", TopTenTracksActivity.topTenTrackList.get(songPosition).trackAlbum);

                    // Get the song's total duration (in ms)
                    bundle.putLong("duration", Integer.parseInt(TopTenTracksActivity.topTenTrackList.get(songPosition).trackDuration)); // 4:05

                    bundle.putLong("position", 0L);
                    // playback status
                    bundle.putBoolean("playing", false);

                    intent.putExtras(bundle);
                    getIntent().getParcelableExtra(Intent.EXTRA_TEXT);

                }
            }
        });

        currentDuration.setText("00:00");



    }

    private void nextButton() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songPosition = songPosition + 1;
                if (songPosition > TopTenTracksActivity.topTenTrackList.size() - 1) {
                    songPosition = 0;

                }
                setViews();
                nextButton.setImageResource(R.drawable.ic_skip_next_black_24dp);


                prepareMusic();
            }
        });
    }

    private void prevButton() {
        prevButton.setClickable(true);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }
}
