package com.example.em.spotifymusicplayer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by emiliaaxen on 16-05-10.
 */
public class SearchArtistActivityFragment extends Fragment {

    private EditText searchForArtistEditText;
    private static ArtistAdapter artistAdapter;
    private ArrayList<ArtistListData> artistList;
    ListView artistView;
    private ProgressBar progressBar;
    private SearchForArtistTask task;

    public SearchArtistActivityFragment() {
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // get saved datasource if present
        if (savedInstanceState != null) {
            artistList = savedInstanceState.getParcelableArrayList("savedArtistList");
            bindView();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save data source
        if (artistList != null) {
            outState.putParcelableArrayList("savedArtistList", artistList);
        }
    }

    /**
     * RootView used to fill in the rest of the content into the activity screen or fragment.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_artist, container, false);

        // Progress Bar -> initially no progress
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_1);
        progressBar.setVisibility(View.GONE);

        // Listener for when the user is done typing the artist name in the edittext feild
        searchForArtistEditText = (EditText) rootView.findViewById(R.id.edit_text_search_artist);
        searchForArtistEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (searchForArtistEditText.length() != 0) {

                    //Display the progress bar
                    progressBar.setVisibility(View.VISIBLE);

                    if (task != null) {
                        task.cancel(false);
                    }

                    //Search for artist
                    task = new SearchForArtistTask();
                    task.execute("*" + searchForArtistEditText.getText().toString() + "*");
                } else {
                    // Removes the old result if there is no text
                    if (task != null) {
                        task.cancel(false);
                    }
                    progressBar.setVisibility(View.GONE);
                    artistList.clear();
                    artistAdapter.notifyDataSetChanged();
                }
            }
        });

        artistList = new ArrayList<>();
        artistView = (ListView) rootView.findViewById(R.id.list_view_artist);
        bindView();

        // open top 10 track view
        artistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String artistId = artistList.get(position).artistId;
                String artistName = artistList.get(position).artistName;
                Intent intent = new Intent(getActivity(), TopTenTracksActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, new String[]{artistId, artistName});
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void bindView() {

        // initialize adapter
        artistAdapter = new ArtistAdapter(getActivity(), artistList);

        // bind listview
        artistView.setAdapter(artistAdapter);
    }

    public class SearchForArtistTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... artistName) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // for catching network extra exceptions
            try {
                //  Make the Spotify transaction
                SpotifyApi api = new SpotifyApi();
                api.setAccessToken(SearchArtistActivity.getAccessToken());  //                api.setAccessToken(SearchArtistActivity.getAccessToken());

                SpotifyService spotify = api.getService();

                // Sets option
                Map<String, Object> options = new HashMap<>();
                options.put("limit", 20);

                // Check if empty
                if (artistName[0].equals("")) {
                    return false;
                }

                // Search for the artist
                ArtistsPager artistsPager = spotify.searchArtists(artistName[0], options);

                // Update the data
                artistList.clear();
                for (Artist artist : artistsPager.artists.items) {
                    ArtistListData currentArtist = new ArtistListData(artist);
                    artistList.add(currentArtist);
                }

                // return true if data source refreshed
                return !artistList.isEmpty();
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
            if (isDataSourceRefreshed) {
                progressBar.setVisibility(View.GONE);
                artistAdapter.notifyDataSetChanged();
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "No results found for \"" + searchForArtistEditText.getText() + "\". Please refine your search.", Toast.LENGTH_LONG).show();
            }
        }
    }


    public class ArtistAdapter extends BaseAdapter {
        ArrayList artistList = new ArrayList();
        Context context;


        public ArtistAdapter(Context context, ArrayList artistList) {
            this.artistList = artistList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return artistList.size();
        }

        @Override
        public ArtistListData getItem(int position) {
            return (ArtistListData) artistList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.artistlistview_layout, viewGroup, false);

            // Sets the image for the artist
            ImageView artistImageView = (ImageView) row.findViewById(R.id.image_view_artist);
            artistImageView.setImageBitmap(null);
            String url = getItem(position).artistImage;
            Picasso.with(row.getContext()).load(url).placeholder(R.drawable.ic_play_circle_filled_black_24dp).error(R.drawable.ic_pause_circle_outline_black_24dp).into(artistImageView);

            // Sets the name for the artist
            TextView artistName = (TextView) row.findViewById(R.id.text_view_artist_name);
            artistName.setText(getItem(position).artistName);

            return row;
        }
    }


}
