package com.example.em.spotifymusicplayer.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.em.spotifymusicplayer.R;
import com.example.em.spotifymusicplayer.SearchArtistActivity;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

public class MainActivity extends AppCompatActivity implements
        PlayerNotificationCallback, ConnectionStateCallback{

    protected static final String CLIENT_ID = "1b83b9968c1e468bb7e72686a240f94c";
    private static final String REDIRECT_URI = "emaax-android-app-login://callback";
    private static String accessToken;
    private static final int REQUEST_CODE = 1337;

        private Player mPlayer;

        protected static String getAccessToken() {
            return accessToken;
        }

        private static void setAccessToken(String accessToken) {
            MainActivity.accessToken = accessToken;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_search_artist);
            AuthenticationRequest.Builder builder =
                    new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
            builder.setScopes(new String[]{"user-read-private", "playlist-read", "playlist-read-private", "streaming"});
            AuthenticationRequest request = builder.build();

            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
            super.onActivityResult(requestCode, resultCode, intent);



            // Check if result comes from the correct activity
            if (requestCode == REQUEST_CODE) {
                AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);


                switch (response.getType()) {

                    case TOKEN:
                        Toast.makeText(MainActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
                        setAccessToken(response.getAccessToken());
                       // if logged in, intent to go to search artist activity
                        Intent intent1 = new Intent(MainActivity.this, SearchArtistActivity.class);
                        startActivity(intent1);

                        break;

                    case ERROR:
                        Toast.makeText(MainActivity.this, "Could not log in, please restart app!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }


        @Override
        public void onLoggedIn() {
            Log.d("MainActivity", "User logged in");
        }

        @Override
        public void onLoggedOut() {
            Log.d("MainActivity", "User logged out");
        }

        @Override
        public void onLoginFailed(Throwable error) {
            Log.d("MainActivity", "Login failed");
        }

        @Override
        public void onTemporaryError() {
            Log.d("MainActivity", "Temporary error occurred");
        }

        @Override
        public void onConnectionMessage(String message) {
            Log.d("MainActivity", "Received connection message: " + message);
        }

        @Override
        public void onPlaybackEvent(PlayerNotificationCallback.EventType eventType, PlayerState playerState) {
            Log.d("MainActivity", "Playback event received: " + eventType.name());
            switch (eventType) {
                // Handle event type as necessary
                default:
                    break;
            }
        }

        @Override
        public void onPlaybackError(PlayerNotificationCallback.ErrorType errorType, String errorDetails) {
            Log.d("MainActivity", "Playback error received: " + errorType.name());
            switch (errorType) {
                // Handle error type as necessary
                default:
                    break;
            }
        }

        @Override
        protected void onDestroy() {
            // VERY IMPORTANT! This must always be called or else you will leak resources
            Spotify.destroyPlayer(this);
            super.onDestroy();
        }
}