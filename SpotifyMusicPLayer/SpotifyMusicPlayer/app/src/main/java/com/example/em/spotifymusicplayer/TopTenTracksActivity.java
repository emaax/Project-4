package com.example.em.spotifymusicplayer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class TopTenTracksActivity extends AppCompatActivity {
    String[] artistInfo;
    private static TopTenTrackAdapter topTenTrackAdapter;

    public static ArrayList<TopTrackData> topTenTrackList;
    ListView topTenTrackView;
    private ProgressBar spinner;
    private String TAG = "TopTenTracksActivity";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save data source
        if (topTenTrackList != null) {
            outState.putParcelableArrayList("savedTopTenTrackList", topTenTrackList);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten_tracks);

        // get Intent2
        artistInfo = getIntent().getStringArrayExtra(Intent.EXTRA_TEXT);
        spinner = (ProgressBar) findViewById(R.id.progress_bar_2);
        topTenTrackList = new ArrayList<>();
        topTenTrackView = (ListView) findViewById(R.id.topTenTrackListView);
        setViews();

        Boolean isRestoringState = savedInstanceState != null;
        // Run Async if no data to restore
        if (!isRestoringState) {
            Log.i(TAG, "onCreate: " + "not restoring states");

            // Get top ten tracks of the artist (async task)
            getArtistTopTenTracks task = new getArtistTopTenTracks();
            assert artistInfo != null;
            spinner.setVisibility(View.GONE);
            task.execute(artistInfo[0]);

        } else {
            // Get saved data source
            topTenTrackList = savedInstanceState.getParcelableArrayList("savedTopTenTrackList");
            setViews();
            spinner.setVisibility(View.GONE);
        }
        // TODO implement listener to start PlayMusicActivity
        topTenTrackView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // start Player Activity
                TopTrackData trackPosition = topTenTrackList.get(position);
                Intent intent = new Intent(TopTenTracksActivity.this, PlayerActivity.class).putExtra(Intent.EXTRA_TEXT, trackPosition);
                startActivity(intent);
            }
        });
    }

    private void setViews() {
        topTenTrackAdapter = new TopTenTrackAdapter(TopTenTracksActivity.this, topTenTrackList);
        topTenTrackView.setAdapter(topTenTrackAdapter);
    }


    public class getArtistTopTenTracks extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... artistId) {
            // Network exception
            try {
                // Get spotify service
                SpotifyApi api = new SpotifyApi();
                api.setAccessToken(SearchArtistActivity.getAccessToken());
                SpotifyService spotify = api.getService();

                // Get top 10 tracks of the artist
                Tracks topTracks = spotify.getArtistTopTrack(artistId[0], "SE");
                topTenTrackList.clear();
                for (Track track : topTracks.tracks) {
                    TopTrackData currentTrack = new TopTrackData(track);
                    currentTrack.trackArtist = artistInfo[1];
                    topTenTrackList.add(currentTrack);
                }

                // If data source is refreshed, return true
                return !topTenTrackList.isEmpty();
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean isDataSourceRefreshed) {

            topTenTrackAdapter.notifyDataSetChanged();
            String[] artistInfo = getIntent().getExtras().getStringArray(Intent.EXTRA_TEXT);
            assert artistInfo != null;
        }
    }

    // Adapter - custom
    public class TopTenTrackAdapter extends BaseAdapter {
        ArrayList topTenTrackList = new ArrayList();
        Context context;

        public TopTenTrackAdapter(Context context, ArrayList topTenTrackList) {
            this.topTenTrackList = topTenTrackList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return topTenTrackList.size();
        }

        @Override
        public TopTrackData getItem(int position) {
            return (TopTrackData) topTenTrackList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.toptentracklistview_layout, viewGroup, false);

            //Track image
            ImageView trackImageView = (ImageView) view.findViewById(R.id.image_view_track);
            trackImageView.setImageBitmap(null);
            String url = getItem(position).trackImageSmall;
            Picasso.with(view.getContext()).load(url).placeholder(R.drawable.ic_play_circle_filled_black_24dp).error(R.drawable.ic_pause_circle_outline_black_24dp).into(trackImageView);
            //Track name
            TextView trackName = (TextView) view.findViewById(R.id.text_view_track_name);
            trackName.setText(getItem(position).trackName);

            // Track album
            TextView trackAlbum = (TextView) view.findViewById(R.id.text_view_track_album);
            trackAlbum.setText(getItem(position).trackAlbum);

            return view;
        }
    }
}