package com.example.android.moviez;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviez.data.MovieContract;
import com.example.android.moviez.data.MovieProvider;
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
import java.util.List;

public class InfoActivity extends AppCompatActivity {
    private final static String LOG_TAG = InfoActivity.class.getSimpleName();
    private final String TMDB_API_KEY = "33bf92db5dd97f28a99a01826efba1b3";
    private final String FANART_API_KEY = "5a64d56910b0a1a6f4f70e5ed6fa32b5";

    private String movieId;
    private Uri mMovieUri;
    private boolean isFavorite = false;
    private ContentValues movieValues;
    private ContentValues[] movirRelationsValues;

    private static final String[] MOVIES_COLUMNS = {
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_LANGUAGE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_ADULT,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_VOTE_COUNT,
            MovieContract.MovieEntry.COLUMN_AVG_SCORE,
            MovieContract.MovieEntry.COLUMN_TMDB_ID,
            MovieContract.GenreEntry.MAIN_TABLE_NAME + "." + MovieContract.GenreEntry.COLUMN_GENRE_ID,
            MovieContract.GenreEntry.COLUMN_NAME
    };

    // These indices are tied to MOVIES_COLUMNS.  If MOVIES_COLUMNS changes, these
    // must change.
    static final int COL_TITLE = 0;
    static final int COL_RELEASE_DATE = 1;
    static final int COL_LANGUAGE = 2;
    static final int COL_OVERVIEW = 3;
    static final int COL_ADULT = 4;
    static final int COL_POSTER_PATH = 5;
    static final int COL_VOTE_COUNT = 6;
    static final int COL_AVG_SCORE = 7;
    static final int COL_TMDB_ID = 8;
    static final int COL_GENRE_ID = 9;
    static final int COL_GENRE_NAME = 10;

    private static final String[] FAVORITE_MOVIES_COLUMNS = {
            MovieContract.MovieEntry.COLUMN_TITLE
    };

    // These indices are tied to FAVORITE_MOVIES_COLUMNS.  If FAVORITE_MOVIES_COLUMNS changes, these
    // must change.
    static final int COL_FAVORITE_TITLE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.info_favorite_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFavorite){
                    getContentResolver().delete(MovieContract.MovieEntry.FAVORITE_CONTENT_URI,
                            MovieProvider.sFavoriteMoviesTMDBIdSelection, new String[]{movieId});
                    getContentResolver().delete(MovieContract.RelationEntry.FAVORITE_CONTENT_URI,
                            MovieProvider.sFavoriteRelationsTMDBIdSelection, new String[]{movieId});
                    isFavorite = false;
                    switchFavoriteState();
                    Toast.makeText(getApplication(),"The movie is no longer favorite", Toast.LENGTH_LONG).show();
                }
                else {
                    getContentResolver().insert(MovieContract.MovieEntry.FAVORITE_CONTENT_URI,
                            movieValues);
                    getContentResolver().bulkInsert(MovieContract.RelationEntry.FAVORITE_CONTENT_URI,
                            movirRelationsValues);
                    isFavorite = true;
                    switchFavoriteState();
                    Toast.makeText(getApplication(),"The movie is in your favorites", Toast.LENGTH_LONG).show();
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mMovieUri = getIntent().getData();
        bindDataToViews();
    }

    private void checkIfFavorite(){
        Cursor data = getContentResolver().query(MovieContract.MovieEntry.FAVORITE_CONTENT_URI,
                FAVORITE_MOVIES_COLUMNS, MovieProvider.sFavoriteMoviesTMDBIdSelection,
                new String[]{movieId}, null);
        if(data.moveToFirst()){
            isFavorite = true;
        }
        else {
            isFavorite = false;
        }
        data.close();
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkIfFavorite();
        switchFavoriteState();
    }

    private void switchFavoriteState() {
        if(isFavorite){
            FloatingActionButton favoriteButton =
                    (FloatingActionButton) findViewById(R.id.info_favorite_button);
            favoriteButton.setImageResource(R.drawable.ic_favorite_white_24dp);
        }
        else {
            FloatingActionButton favoriteButton =
                    (FloatingActionButton) findViewById(R.id.info_favorite_button);
            favoriteButton.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }
    }

    private void bindDataToViews() {
        Cursor data = getContentResolver().query(mMovieUri,MOVIES_COLUMNS, null, null, null);

        if(data.moveToFirst()){
            int intAdult = data.getInt(COL_ADULT);
            boolean adultInt = intAdult == 1;
            String language = data.getString(COL_LANGUAGE);
            String overview = data.getString(COL_OVERVIEW);
            String releaseDate = data.getString(COL_RELEASE_DATE);
            String title = data.getString(COL_TITLE);
            double voteAvg = data.getDouble(COL_AVG_SCORE);
            int voteCnt = data.getInt(COL_VOTE_COUNT);
            String posterPath = data.getString(COL_POSTER_PATH);

            movieId = data.getString(COL_TMDB_ID);
            fetchMovieArtwork(movieId, posterPath);
            fetchMovieData(movieId);
            List<String> genreNameList = new ArrayList<>();
            List<Integer> genreIdList = new ArrayList<>();
            do{
                genreIdList.add(data.getInt(COL_GENRE_ID));
                genreNameList.add(data.getString(COL_GENRE_NAME));
            }
            while (data.moveToNext());
            String[] genres = genreNameList.toArray(new String[0]);

            ContentValues movieData = new ContentValues();
            movieData.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            movieData.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieData.put(MovieContract.MovieEntry.COLUMN_LANGUAGE, language);
            movieData.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
            movieData.put(MovieContract.MovieEntry.COLUMN_ADULT, intAdult);
            movieData.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
            movieData.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, voteCnt);
            movieData.put(MovieContract.MovieEntry.COLUMN_AVG_SCORE, voteAvg);
            movieData.put(MovieContract.MovieEntry.COLUMN_TMDB_ID, movieId);
            movieValues = movieData;

            ContentValues[] movieRelationsData = new ContentValues[genreIdList.size()];
            int i = 0;
            for (Integer genreId : genreIdList){
                ContentValues relation = new ContentValues();
                relation.put(MovieContract.RelationEntry.COLUMN_MOVIE_TMDB_ID, movieId);
                relation.put(MovieContract.RelationEntry.COLUMN_GENRE_ID, genreId);
                movieRelationsData[i] = relation;
                i++;
            }
            movirRelationsValues = movieRelationsData;

            displayDetails(adultInt, genres, language, overview,
                    releaseDate, title, voteAvg, voteCnt);
        }

    }

    private void fetchMovieArtwork(String movieId, String posterPath) {
        FetchMovieArtwork fetchMovieArtwork = new FetchMovieArtwork();
        fetchMovieArtwork.execute(movieId, posterPath);
    }

    private void fetchMovieData(String movieId) {
        FetchMovieData fetchMovieData = new FetchMovieData();
        fetchMovieData.execute(movieId);
    }

    public class FetchMovieData extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
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
            int runtime = parseTMDBJSON(moviesJsonStr);
            return runtime;
        }

        @Override
        protected void onPostExecute(Integer runtime){
            displayRuntime(runtime);
        }

        private int parseTMDBJSON(String movieDataStr) {
            if(movieDataStr!=null) {
                try {
                    final String JSON_RUNTIME = "runtime";

                    JSONObject movie = new JSONObject(movieDataStr);
                    int runtime = movie.getInt(JSON_RUNTIME);
                    return runtime;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return -1;
        }
    }

    public class FetchMovieArtwork extends AsyncTask<String, Void, String> {
        private final static String BASE_PATH = "http://image.tmdb.org/t/p/w500";

        private String posterPath;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            posterPath = params[1];
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
            String fanartUrl = parseFanartJSON(jsonStr);
            return fanartUrl;
        }

        @Override
        protected void onPostExecute(String fanartUrl){
            ImageView toolbarImageView = (ImageView) findViewById(R.id.info_toolbar_image);
            if(fanartUrl!=null){
                Picasso.with(getApplication()).load(fanartUrl).into(toolbarImageView);
            }
            else{
                Uri uri = Uri.parse(BASE_PATH).buildUpon()
                        .appendEncodedPath(posterPath)
                        .build();
                Picasso.with(getApplication()).load(uri.toString()).into(toolbarImageView);
            }
        }
        private String parseFanartJSON(String jsonStr) {
            if(jsonStr!=null){
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONArray banners = jsonObject.getJSONArray("moviethumb");
                    boolean found = false;
                    for(int i = 0; i<banners.length() || !found; i++) {
                        JSONObject banner = banners.getJSONObject(i);
                        if(banner.getString("lang").equals("en")){
                            String url = banner.getString("url");
                            return url;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private void displayDetails(Boolean isAdult, String[] genres, String language, String overview,
                                String releaseDate, String title, double voteAvg, int voteCnt){
        if(isAdult){
            ImageView adultView = (ImageView) findViewById(R.id.info_age_limit);
            adultView.setVisibility(View.VISIBLE);
        }
        StringBuffer genreStr = new StringBuffer();
        for(int i=0; i<genres.length; i++){
            genreStr.append(genres[i]);
            if(i<genres.length-1){
                genreStr.append(", ");
            }
        }
        TextView genreView = (TextView) findViewById(R.id.info_genres);
        genreView.setText(genreStr);
        TextView langView = (TextView) findViewById(R.id.info_language);
        langView.setText(language);
        TextView overviewView = (TextView) findViewById(R.id.info_overview);
        overviewView.setText(overview);
        TextView releaseDateView = (TextView) findViewById(R.id.info_release_date);
        releaseDateView.setText("Release Date : "+releaseDate);
        TextView titleView = (TextView) findViewById(R.id.info_title);
        titleView.setText(title);
        CollapsingToolbarLayout layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        layout.setTitle("Details");
        layout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        layout.setTitleEnabled(true);
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

    private void displayRuntime(int runtime){
        TextView genreView = (TextView) findViewById(R.id.info_runtime);
        genreView.setText(runtime + " minutes");
    }
}
