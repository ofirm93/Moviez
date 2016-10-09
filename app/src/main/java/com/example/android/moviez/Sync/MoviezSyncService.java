package com.example.android.moviez.Sync;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.moviez.R;
import com.example.android.moviez.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import static com.example.android.moviez.data.MovieContract.MovieEntry.COLUMN_POSTER_PATH;

public class MoviezSyncService extends IntentService {

    private final static String LOG_TAG = MoviezSyncService.class.getSimpleName();
    private final static int MOVIES_IN_PAGE = 20;

    private static final String[] MOVIES_COLUMNS = {
            MovieContract.MovieEntry.MAIN_TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };

    // These indices are tied to MOVIES_COLUMNS.  If MOVIES_COLUMNS changes, these
    // must change.
    static final int COL_MOVIE_ID = 0;
    static final int COL_POSTER_PATH = 1;

    private static final String[] GENRES_COLUMNS = {
            MovieContract.GenreEntry.MAIN_TABLE_NAME + "." + MovieContract.GenreEntry._ID
    };

    // These indices are tied to GENRES_COLUMNS.  If GENRES_COLUMNS changes, these
    // must change.
    static final int COL_GENRE_ID = 0;

    private final String myAPIKey = "33bf92db5dd97f28a99a01826efba1b3";
    private SharedPreferences sharedPreferences;

    public final static String PAGE_EXTRA_KEY = "current_page";

    public MoviezSyncService() {
        super("MoviezSyncService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Create my own intent starter.
    public static void startIntent(Context context, int page) {
        Intent intent = new Intent(context, MoviezSyncService.class);
        intent.putExtra(PAGE_EXTRA_KEY, page);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        boolean isDownloadedGenres = false;
        while(!isDownloadedGenres){
            if(GenreSyncService.getNuberOfCalls() > 0) {
                isDownloadedGenres = true;
            }
        }
        if(sharedPreferences == null){
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
        String moviesJsonStr = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

/*        final String BASE_PATH = "https://api.themoviedb.org/3/discover/movie";
        final String TOP_RATED = "vote_average.desc";
        final String POPULARITY = "popularity.desc";*/

        final String BASE_PATH = "https://api.themoviedb.org/3/movie";
        final String TOP_RATED = "top_rated";
        final String POPULAR = "popular";
        final String PAGE_PARAM = "page";
        final String API_KEY_PARAM = "api_key";
        final String LANGUAGE_PARAM = "language";

        final int currentPage = intent.getIntExtra("current_page", 1);

        String lang = "en-US";
        String categoryPref = sharedPreferences.
                getString(getString(R.string.pref_key_sorting_option),
                        getString(R.string.pref_sort_rating));

        if(categoryPref.equals(getString(R.string.pref_sort_popularity))){
            categoryPref = POPULAR;
        }
        else{
            categoryPref = TOP_RATED;
        }
        try {
            Uri uri = Uri.parse(BASE_PATH).buildUpon()
                    .appendPath(categoryPref)
                    .appendQueryParameter(PAGE_PARAM, Integer.toString(currentPage))
                    .appendQueryParameter(API_KEY_PARAM, myAPIKey)
                    .appendQueryParameter(LANGUAGE_PARAM, lang)
                    .build();

            URL url = new URL(uri.toString());

            Log.d(LOG_TAG, uri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                stringBuilder.append(line).append("\n");
            }

            if (stringBuilder.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            moviesJsonStr = stringBuilder.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return;
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
        parseJSON(moviesJsonStr);

    }

    private void parseJSON(String moviesJsonStr) {

        final String JSON_MOVIE_ARRAY = "results";

        final String JSON_TITLE = "title";
        final String JSON_RELEASE_DATE = "release_date";
        final String JSON_LANGUAGE = "original_language";
        final String JSON_OVERVIEW = "overview";
        final String JSON_ADULT = "adult";
        final String JSON_POSTER_PATH = "poster_path";
        final String JSON_VOTE_COUNT = "vote_count";
        final String JSON_AVG_SCORE = "vote_average";
        final String JSON_GENRE_IDS = "genre_ids";
        final String JSON_MOVIE_TMDB_ID = "id";

        if(moviesJsonStr != null) {
            try {
                JSONObject movies = new JSONObject(moviesJsonStr);
                JSONArray moviesJSONArray = movies.getJSONArray(JSON_MOVIE_ARRAY);
                Vector<ContentValues> moviesVector = new Vector<>(moviesJSONArray.length());
                Vector<ContentValues> relationsVector = new Vector<>();
                for(int i = 0; i<moviesJSONArray.length(); i++){
                    JSONObject movieJSON = moviesJSONArray.getJSONObject(i);
                    String title = movieJSON.getString(JSON_TITLE);
                    String releaseDate = movieJSON.getString(JSON_RELEASE_DATE);
                    String language = movieJSON.getString(JSON_LANGUAGE);
                    String overview = movieJSON.getString(JSON_OVERVIEW);
                    boolean adult = movieJSON.getBoolean(JSON_ADULT);
                    int adultInt = adult ? 1 : 0;
                    String posterPath = movieJSON.getString(JSON_POSTER_PATH);
                    int voteCount = movieJSON.getInt(JSON_VOTE_COUNT);
                    double avgScore = movieJSON.getDouble(JSON_AVG_SCORE);
                    int movieTMDBId = movieJSON.getInt(JSON_MOVIE_TMDB_ID);

                    JSONArray genresJSONArray = movieJSON.getJSONArray(JSON_GENRE_IDS);
                    for (int j = 0; j<genresJSONArray.length(); j++){
                        int genreId = genresJSONArray.getInt(j);

                        ContentValues relation = new ContentValues();
                        relation.put(MovieContract.RelationEntry.COLUMN_MOVIE_TMDB_ID, movieTMDBId);
                        relation.put(MovieContract.RelationEntry.COLUMN_GENRE_ID, genreId);
                        relationsVector.add(relation);
                    }

                    ContentValues movie = new ContentValues();
                    movie.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                    movie.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
                    movie.put(MovieContract.MovieEntry.COLUMN_LANGUAGE, language);
                    movie.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
                    movie.put(MovieContract.MovieEntry.COLUMN_ADULT, adultInt);
                    movie.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
                    movie.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, voteCount);
                    movie.put(MovieContract.MovieEntry.COLUMN_AVG_SCORE, avgScore);
                    movie.put(MovieContract.MovieEntry.COLUMN_TMDB_ID, movieTMDBId);

                    moviesVector.add(movie);
                }
                int moviesInserted = 0;
                // add to database
                if ( moviesVector.size() > 0 ) {
                    ContentValues[] moviesArray = new ContentValues[moviesVector.size()];
                    moviesVector.toArray(moviesArray);
                    moviesInserted = getContentResolver().bulkInsert(
                            MovieContract.MovieEntry.CONTENT_URI, moviesArray);
                }

                int relationsInserted = 0;

                if ( relationsVector.size() > 0 ) {
                    ContentValues[] relationsArray = new ContentValues[relationsVector.size()];
                    relationsVector.toArray(relationsArray);
                    relationsInserted = getContentResolver().bulkInsert(
                            MovieContract.RelationEntry.CONTENT_URI, relationsArray);
                }

                Log.d(LOG_TAG, "Fetching movies' data Complete. " + moviesInserted +
                        " movies inserted and" + relationsInserted + "relations inserted");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
