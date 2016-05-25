package com.example.em.spotifymusicplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class SearchArtistActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    protected static final String CLIENT_ID = "7af8a0889cc34a35b18838154e84c0b4";
    private static final String REDIRECT_URI = "spotifystreamer://callback";
    private static String accessToken;
    private static final int REQUEST_CODE = 1337;

    protected static String getAccessToken() {
        return accessToken;
    }

    private static void setAccessToken(String accessToken) {
        SearchArtistActivity.accessToken = accessToken;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_artist);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {

                case TOKEN:
                    Toast.makeText(SearchArtistActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
                    setAccessToken(response.getAccessToken());
                    Intent intent1 = new Intent(SearchArtistActivity.this, TopTenTracksActivity.class);
                    startActivity(intent1);
                    break;

                case ERROR:
                    Toast.makeText(SearchArtistActivity.this, "Could not log in, please restart app!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_artist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = new Intent(this, TopTenTracksActivity.class);
        startActivity(intent);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }
}