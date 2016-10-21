package com.example.android.moviez.Sync;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.moviez.Utility;
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
import java.util.Vector;

public class GenreSyncService extends IntentService {

    private final static String LOG_TAG = GenreSyncService.class.getSimpleName();
    private final static String mAPIKey = "33bf92db5dd97f28a99a01826efba1b3"; // TODO delete from the code
    private SharedPreferences mSharedPreferences;

    public final static String PAGE_EXTRA_KEY = "current_page";

    public GenreSyncService() {
        super("GenreSyncService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Create my own intent starter.
    public static void startIntent(Context context) {
        Intent intent = new Intent(context, GenreSyncService.class);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if(mSharedPreferences == null){
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }

        Utility.setLastUpdateNowLoading(mSharedPreferences);

        String genreJsonStr = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        final String BASE_PATH = "https://api.themoviedb.org/3/genre/movie/list";
        final String API_KEY_PARAM = "api_key";
        final String LANGUAGE_PARAM = "language";

        String lang = "en-US"; // TODO make this correspont to the language preference.

        try {
            Uri uri = Uri.parse(BASE_PATH).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, mAPIKey)
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
            genreJsonStr = stringBuilder.toString();
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
        parseJSON(genreJsonStr);
        Utility.updateLastUpdate(mSharedPreferences);
    }

    private void parseJSON(String genreJsonStr) {

        final String JSON_GENRES_ARRAY = "genres";

        final String JSON_GENRE_ID = "id";
        final String JSON_NAME = "name";

        if(genreJsonStr != null) {
            try {
                JSONObject genres = new JSONObject(genreJsonStr);
                JSONArray genresJSONArray = genres.getJSONArray(JSON_GENRES_ARRAY);
                Vector<ContentValues> genresVector = new Vector<>(genresJSONArray.length());
                for(int i = 0; i<genresJSONArray.length(); i++){
                    JSONObject genreJSON = genresJSONArray.getJSONObject(i);
                    String id = genreJSON.getString(JSON_GENRE_ID);
                    String name = genreJSON.getString(JSON_NAME);

                    ContentValues genre = new ContentValues();
                    genre.put(MovieContract.GenreEntry.COLUMN_GENRE_ID, id);
                    genre.put(MovieContract.GenreEntry.COLUMN_NAME, name);

                    genresVector.add(genre);
                }
                int inserted = 0;
                // add to database
                if ( genresVector.size() > 0 ) {
                    ContentValues[] genresArray = new ContentValues[genresVector.size()];
                    genresVector.toArray(genresArray);
                    inserted = getContentResolver().bulkInsert(
                            MovieContract.GenreEntry.CONTENT_URI, genresArray);
                }

                Log.d(LOG_TAG, "Fetching genres' data Complete. " + inserted + " Inserted");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
