package com.example.android.moviez.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

import com.example.android.moviez.BuildConfig;

/**
 * Defines table and column names for the movies database.
 */
public class MoviesContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.
    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID; // TODO delete if this line works "com.example.android.moviez.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.moviez.app/movie/ is a valid path for
    // looking at movies data.
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_GENRE = "genre";
    public static final String PATH_RELATIONSHIP = "relationship";


    // TODO Delete this method if not needed.
    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /* Inner class that defines the table contents of the movies table */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // Table name
        public static final String TABLE_NAME = "movie";

        // The Columns which will be in the table
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_LANGUAGE = "language";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_AVG_SCORE = "avg_score";

        // This method builds a URI that corresponds to the given id
        public static Uri buildMoviesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the genres table */
    public static final class GenreEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GENRE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GENRE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GENRE;

        public static final String TABLE_NAME = "genre";

        // The Columns which will be in the table
        public static final String COLUMN_GENRE_ID = "genre_id";
        public static final String COLUMN_NAME = "name";

        // This method builds a URI that corresponds to the given id
        public static Uri buildgGenreUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /* Inner class that defines the table contents of the movies and genres relationship table*/
        public static final class RelationshipEntry implements BaseColumns {

            public static final Uri CONTENT_URI =
                    BASE_CONTENT_URI.buildUpon().appendPath(PATH_RELATIONSHIP).build();

            public static final String CONTENT_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RELATIONSHIP;
            public static final String CONTENT_ITEM_TYPE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RELATIONSHIP;

            public static final String TABLE_NAME = "relationship";

            // The Columns which will be in the table
            // Column with the foreign key into the genres table.
            public static final String COLUMN_GENRE_ID = "genre_id";
            // Column with the foreign key into the movies table.
            public static final String COLUMN_MOVIE_ID = "name";

            // This method builds a URI that corresponds to the given id
            public static Uri buildgRelationUri(long id) {
                return ContentUris.withAppendedId(CONTENT_URI, id);
            }
        }
        /*
            Student: This is the buildWeatherLocation function you filled in.
            TODO Delete the unnecessary method or add as required
         */
/*        public static Uri buildWeatherLocation(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, long startDate) {
            long normalizedDate = normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationSetting, long date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendPath(Long.toString(normalizeDate(date))).build();
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }*/
    }
}
