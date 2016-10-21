package com.example.android.moviez;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.IntegerRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.android.moviez.Sync.MoviezSyncService;
import com.example.android.moviez.data.MovieContract;
import com.example.android.moviez.data.MovieProvider;

import java.util.Calendar;
import java.util.Set;

import static com.example.android.moviez.R.layout.toolbar;

public class MainActivity extends AppCompatActivity implements MovieGalleryFragment.Callback,
        SearchActivityFragment.Callback,
        NavigationView.OnNavigationItemSelectedListener{

    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    private final static String MOVIE_GALLERY_FRAGMENT_TAG = "movie_gallery";
    private final String mAPIKey = "33bf92db5dd97f28a99a01826efba1b3"; // TODO delete from the code

    private final float MIN_RATING = 0;
    private final float MAX_RATING = 10;
    private final int MIN_YEAR = 1874;
    private final int MAX_YEAR = Calendar.getInstance().get(Calendar.YEAR);

    private static final String[] GENRES_COLUMNS = {
            MovieContract.GenreEntry.COLUMN_GENRE_ID
    };

    // These indices are tied to GENRES_COLUMNS.  If GENRES_COLUMNS changes, these
    // must change.
    static final int COL_GENRE_ID = 0;

    private SharedPreferences mSharedPreferences;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private boolean mIsFiltered;
    private Uri mUri;
    private MovieGalleryFragment mMovieGalleryFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            mMovieGalleryFragment = new MovieGalleryFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_gallery_container, mMovieGalleryFragment,
                            MOVIE_GALLERY_FRAGMENT_TAG)
                    .commit();
        }
        else {
            mMovieGalleryFragment = (MovieGalleryFragment) getSupportFragmentManager()
                    .findFragmentByTag(MOVIE_GALLERY_FRAGMENT_TAG);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        mUri = getGeneralMoviesUri();
        // TODO replace this by getApplicationContext() if doesn;t working
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mIsFiltered = false;
    }

/*
    */
/* Called whenever we call invalidateOptionsMenu() *//*

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
*/


    private void setDefaultValues(){
        PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_display, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_notification, false);

    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri movieUri) {
        Intent infoIntent = new Intent(this, InfoActivity.class).setData(movieUri);
        startActivity(infoIntent);
    }

    @Override
    public void updateMoviesGallery(int currentPage) {
        final String PAGE_PARAM = "page";
        Uri pageUri = mUri.buildUpon()
                .appendQueryParameter(PAGE_PARAM, Integer.toString(currentPage))
                .build();
        MoviezSyncService.startIntent(this, pageUri);
        mMovieGalleryFragment.onChanged();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.drawer_all_movies) {
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity(mainActivity);
        } else if (id == R.id.drawer_favorites) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFilterSelected(String[] genres, float minRating, float maxRating, int minYear, int maxYear) {
        ContentValues queryParams = new ContentValues();
        String[] genresId = null;

        final String API_KEY_PARAM = "api_key";
        final String GENRES_PARAM = "with_genres";
        final String MIN_RATING_PARAM = "vote_average.gte";
        final String MAX_RATING_PARAM = "vote_average.lte";
        final String MIN_YEAR_PARAM = "primary_release_date.gte";
        final String MAX_YEAR_PARAM = "primary_release_date.lte";

        boolean isFiltered = false;
        if(genres.length > 0){
            isFiltered = true;
            Cursor genresCursor = getContentResolver().query(MovieContract.GenreEntry.CONTENT_URI, null, MovieProvider.sAllGenresNameSelection, genres, null);
            genresCursor.moveToFirst();
            genresId = new String[genres.length];
            int i = 1;
            do{
                genresId[i] = genresCursor.getString(COL_GENRE_ID);
                i++;
            }
            while (genresCursor.moveToNext());
        }
        if(minRating > MIN_RATING){
            isFiltered = true;
            queryParams.put(MIN_RATING_PARAM, minRating);
        }
        if (maxRating < MAX_RATING){
            isFiltered = true;
            queryParams.put(MAX_RATING_PARAM, maxRating);
        }
        if (minYear > MIN_YEAR){
            isFiltered = true;
            queryParams.put(MIN_YEAR_PARAM, minYear);
        }
        if (maxYear < MAX_YEAR){
            isFiltered = true;
            queryParams.put(MAX_YEAR_PARAM, maxYear);
        }
        if(isFiltered){
            Uri.Builder uriBuilder = Uri.parse("https://api.themoviedb.org/3/discover/movie").buildUpon();
            uriBuilder.appendQueryParameter(API_KEY_PARAM, mAPIKey);
            if(genresId != null){
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 0; i < genresId.length - 1; i++){
                    stringBuilder.append(genresId[i]).append(",");
                }
                stringBuilder.append(genresId[genresId.length -1]);
                uriBuilder.appendQueryParameter(GENRES_PARAM, stringBuilder.toString());
            }
            Set<String> keys = queryParams.keySet();
            if(!keys.isEmpty()){
                for(String key : keys){
                    uriBuilder.appendQueryParameter(key, queryParams.getAsString(key));
                }
            }

            mUri = uriBuilder.build();
        }
        else {
            mUri = getGeneralMoviesUri();
        }
        Utility.resetPages(mSharedPreferences);
        updateMoviesGallery(Utility.getPages(mSharedPreferences));
    }

    private Uri getGeneralMoviesUri(){

        final String BASE_PATH = "https://api.themoviedb.org/3/movie";
        final String TOP_RATED = "top_rated";
        final String POPULAR = "popular";
        final String API_KEY_PARAM = "api_key";
        final String LANGUAGE_PARAM = "language";

        String lang = "en-US"; // TODO make this correspont to the language preference.

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String categoryPref = sharedPreferences.
                getString(getString(R.string.pref_key_sorting_option),
                        getString(R.string.pref_sort_rating));

        if(categoryPref.equals(getString(R.string.pref_sort_popularity))){
            categoryPref = POPULAR;
        }
        else{
            categoryPref = TOP_RATED;
        }
        return Uri.parse(BASE_PATH).buildUpon()
                .appendPath(categoryPref)
                .appendQueryParameter(API_KEY_PARAM, mAPIKey)
                .appendQueryParameter(LANGUAGE_PARAM, lang)
                .build();
    }
}
