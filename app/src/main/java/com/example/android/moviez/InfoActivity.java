package com.example.android.moviez;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class InfoActivity extends AppCompatActivity {
    private final static String LOG_TAG = InfoActivity.class.getSimpleName();
    private final String TMDB_API_KEY = "33bf92db5dd97f28a99a01826efba1b3";
    private final String FANART_API_KEY = "5a64d56910b0a1a6f4f70e5ed6fa32b5";
    private String movieID;
    private String movieDataStr;
    private String movieArtworkStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        if(intent!=null && intent.hasExtra(Intent.EXTRA_TEXT)){
            movieID = intent.getStringExtra(Intent.EXTRA_TEXT);
            fetchMovieData();
        }
    }

    private void fetchMovieData() {
        AsyncTask<String, Void, String> fetchMovieData = new FetchMovieData();
        fetchMovieData.execute(movieID);
        AsyncTask<String, Void, String> fetchMovieArtwork = new FetchMovieArtwork();
        fetchMovieArtwork.execute(movieID);
    }

    public class FetchMovieData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String moviesJsonStr = null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            final String BASE_PATH = "https://api.themoviedb.org/3/movie";
            final String API_KEY_PARAM = "api_key";
            try {
                Uri uri = Uri.parse(BASE_PATH).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(API_KEY_PARAM, TMDB_API_KEY)
                        .build();

                URL url = new URL(uri.toString());

                Log.d(LOG_TAG, uri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return moviesJsonStr;
        }

        @Override
        protected void onPostExecute(String s){
            movieDataStr = s;
            parseTMDBJSON();
        }
    }

    public class FetchMovieArtwork extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonStr = null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            final String BASE_PATH = "http://webservice.fanart.tv/v3/movies/";
            final String API_KEY_PARAM = "api_key";
            try {
                Uri uri = Uri.parse(BASE_PATH).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(API_KEY_PARAM, FANART_API_KEY)
                        .build();

                URL url = new URL(uri.toString());

                Log.d(LOG_TAG, uri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return jsonStr;
        }

        @Override
        protected void onPostExecute(String s){
            movieArtworkStr = s;
            parseFANARTJSON();
        }
    }

    private void parseFANARTJSON() {
        if(movieArtworkStr!=null){
            try {
                JSONObject jsonObject = new JSONObject(movieArtworkStr);
                JSONArray banners = jsonObject.getJSONArray("moviethumb");
                boolean found = false;
                String url = null;
                for(int i = 0; i<banners.length() || !found; i++) {
                    JSONObject banner = banners.getJSONObject(i);
                    if(banner.getString("lang").equals("en")){
                        url = banner.getString("url");
                        found = true;
                    }
                }
                if(url!=null){
                    ImageView toolbarImageView = (ImageView) findViewById(R.id.info_toolbar_image);
                    Picasso.with(this).load(url).into(toolbarImageView);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseTMDBJSON() {
        if(movieDataStr!=null) {
            try {
                JSONObject movie = new JSONObject(movieDataStr);
                Boolean adult = movie.getBoolean("adult");
                JSONArray genreJSON = movie.getJSONArray("genres");
                String[] genres = new String[genreJSON.length()];
                for(int i = 0; i<genres.length; i++) {
                    JSONObject genre = genreJSON.getJSONObject(i);
                    genres[i] = genre.getString("name");
                }
                String original_language = movie.getString("original_language");
                String overview = movie.getString("overview");
                String release_date = movie.getString("release_date");
                String title = movie.getString("title");
                double vote_average = movie.getDouble("vote_average");
                int vote_count = movie.getInt("vote_count");
                displayDetails(adult, genres, original_language, overview, release_date, title,
                        vote_average, vote_count);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void displayDetails(Boolean isAdult, String[] genres, String language, String overview,
                                String releaseDate, String title, double voteAvg, int voteCnt){
        if(isAdult){
            ImageView adultView = (ImageView) findViewById(R.id.info_age_limit);
            adultView.setVisibility(View.VISIBLE);
        }
        StringBuffer genreStr = new StringBuffer("Genre :");
        for(int i=0; i<genres.length; i++){
            genreStr.append(" ");
            genreStr.append(genres[i]);
            if(i<genres.length-1){
                genreStr.append(";");
            }
        }
        TextView genreView = (TextView) findViewById(R.id.info_genres);
        genreView.setText(genreStr);
        TextView langView = (TextView) findViewById(R.id.info_language);
        langView.setText("Language : "+language);
        TextView overviewView = (TextView) findViewById(R.id.info_overview);
        overviewView.setText("Overview : "+overview);
        TextView releaseDateView = (TextView) findViewById(R.id.info_release_date);
        releaseDateView.setText("Release Date : "+releaseDate);
        CollapsingToolbarLayout layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        layout.setTitle(title);
        /**
         * change view info_rank to relate voteAvg
         */
        TextView voteCntView = (TextView) findViewById(R.id.info_vote_count);

        if(voteCnt>1){
            voteCntView.setText(voteCnt + " Votes");
        }
        else if(voteCnt>0){
            voteCntView.setText("1 Vote");
        }
        else{
            voteCntView.setText("No votes");
        }
    }
}
