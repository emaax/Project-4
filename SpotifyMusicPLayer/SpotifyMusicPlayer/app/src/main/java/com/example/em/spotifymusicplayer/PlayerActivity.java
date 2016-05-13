package com.example.em.spotifymusicplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.em.spotifymusicplayer.Activity.MainActivity;
import com.spotify.sdk.android.player.Player;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class PlayerActivity extends AppCompatActivity {


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

    private Boolean isPlaying;
    private int songPosition;
    String imageUrl;
    private Player mPlayer;


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

    }
}