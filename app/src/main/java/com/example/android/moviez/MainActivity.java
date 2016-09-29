package com.example.android.moviez;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    private final String myAPIKey = "33bf92db5dd97f28a99a01826efba1b3";
    private String moviesDataStr;
    private String[] newMoviePosters = {};
    private String[] newMovieTitles = {};
    private String[] newMovieIDs = {};
    private ImageListAdapter galleryViewAdapter;
    private int currentPage = 1;
    private boolean isLoading = false;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        galleryViewAdapter = new ImageListAdapter(this, R.layout.listview_item_imageview, newMoviePosters);
        GridView gallery = (GridView) findViewById(R.id.activity_main_movies_grid);
        gallery.setAdapter(galleryViewAdapter);
        gallery.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int prevTotalImages = 0;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(isLoading){
                    if(totalItemCount > prevTotalImages){
                        isLoading = false;
                        prevTotalImages = totalItemCount;
                        currentPage++;
                    }
                }
                if(totalItemCount!=0 && !isLoading && firstVisibleItem + visibleItemCount >= totalItemCount-20){
                    updateMoviesData();
                }
            }
        });
        setDefaultValues();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    private void setDefaultValues(){
        PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_display, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_notification, false);

    }
    @Override
    protected void onStart() {
        super.onStart();
        updateMoviesData();
    }

    private void updateMoviesData() {
        FetchMoviesData fetchMoviesData = new FetchMoviesData();
        isLoading = true;
        fetchMoviesData.execute();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                return true;
        }
        return false;
    }

    private void getMoviesPoster(){
        String[] moviesPosters = null;
        if(moviesDataStr!=null) {
            try {
                JSONObject movies = new JSONObject(moviesDataStr);
                JSONArray moviesData = movies.getJSONArray("results");
                moviesPosters = new String[moviesData.length()];
                for(int i = 0; i<moviesData.length(); i++){
                    JSONObject movie = moviesData.getJSONObject(i);
                    moviesPosters[i] = movie.getString("poster_path");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        this.newMoviePosters = moviesPosters;
    }

    private void getMoviesTitles(){
        String[] movieTitles = null;
        if(moviesDataStr!=null) {
            try {
                JSONObject movies = new JSONObject(moviesDataStr);
                JSONArray moviesData = movies.getJSONArray("results");
                movieTitles = new String[moviesData.length()];
                for(int i = 0; i<moviesData.length(); i++){
                    JSONObject movie = moviesData.getJSONObject(i);
                    movieTitles[i] = movie.getString("title");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        this.newMovieTitles = movieTitles;
    }

    private void getMoviesIDs() {
        String[] movieIDs = null;
        if(moviesDataStr!=null) {
            try {
                JSONObject movies = new JSONObject(moviesDataStr);
                JSONArray moviesData = movies.getJSONArray("results");
                movieIDs = new String[moviesData.length()];
                for(int i = 0; i<moviesData.length(); i++){
                    JSONObject movie = moviesData.getJSONObject(i);
                    movieIDs[i] = movie.getString("id");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        this.newMovieIDs = movieIDs;
    }

    public class FetchMoviesData extends AsyncTask<String, Void, String>{
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
            final String TOP_RATED = "top_rated";
            final String POPULAR = "popular";
            final String PAGE_PARAM = "page";
            final String API_KEY_PARAM = "api_key";
            try {

                String categoryPref = sharedPreferences.
                        getString(getString(R.string.pref_key_sorting_option),
                                getString(R.string.pref_sort_rating));

                if(categoryPref.equals(getString(R.string.pref_sort_popularity))){
                    categoryPref = POPULAR;
                }
                else{
                    categoryPref = TOP_RATED;
                }

                Uri uri = Uri.parse(BASE_PATH).buildUpon()
                        .appendPath(categoryPref)
                        .appendQueryParameter(PAGE_PARAM, Integer.toString(currentPage))
                        .appendQueryParameter(API_KEY_PARAM, myAPIKey)
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
                    return null;
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
                    return null;
                }
                moviesJsonStr = stringBuilder.toString();
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
            moviesDataStr = s;
            getMoviesPoster();
            getMoviesTitles();
            getMoviesIDs();
            galleryViewAdapter.addAll(new ArrayList<>(Arrays.asList(newMoviePosters)),
                    new ArrayList<>(Arrays.asList(newMovieTitles)),
                    new ArrayList<>(Arrays.asList(newMovieIDs)));
        }
    }

    public class ImageListAdapter extends ArrayAdapter<String>{

        private Context context;
        private LayoutInflater inflater;
        private final static String BASE_PATH = "http://image.tmdb.org/t/p/w500";
        private List<String> titles;
        private List<String> IDs;
        public ImageListAdapter(Context context, int resource, String[] imageAddresses) {
            super(context, resource, new ArrayList<>(Arrays.asList(imageAddresses)));

            this.context = context;
            this.inflater = LayoutInflater.from(context);
            this.titles = new ArrayList<>();
            this.IDs = new ArrayList<>();
        }

        public void addAll(Collection<? extends String> newPosters,
                           Collection<? extends String> newTitles,
                           Collection<? extends String> newIDs) {
            super.addAll(newPosters);
            titles.addAll(newTitles);
            IDs.addAll(newIDs);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = inflater.inflate(R.layout.listview_item_imageview, parent, false);
            }
            Uri uri = Uri.parse(BASE_PATH).buildUpon()
                    .appendEncodedPath(getItem(position))
                    .build();
            ImageView poster = (ImageView) convertView.findViewById(R.id.movie_poster);
//            Choose between using Picasso or Glide and delete other dependencies
            Picasso.with(context).load(uri.toString()).placeholder(R.drawable.default_movie_poster).into(poster);
//            Glide.with(context).load(uri.toString()).into(poster);
            TextView title = (TextView) convertView.findViewById(R.id.movie_title);
            title.setText(titles.get(position));
            convertView.setTag(IDs.get(position));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Object tag = view.getTag();
                    if(tag instanceof String){
                        String id = (String) tag;
                        Intent info = new Intent(getApplicationContext(), InfoActivity.class);
                        info.putExtra(Intent.EXTRA_TEXT, id);
                        startActivity(info);
                    }
                }
            });
            return convertView;
        }
    }
}
