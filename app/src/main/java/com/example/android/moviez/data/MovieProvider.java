package com.example.android.moviez.data;
/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIES = 100;
//    static final int MOVIES_BY_POPULARITY = 101;
//    static final int MOVIES_BY_RATING = 102;
    static final int MOVIE_BY_ID = 103;
//    static final int FIND_MOVIE = 104;

    static final int GENRE = 200;
//    static final int MOVIES_BY_GENRE = 201;

    static final int RELATION = 300;

    static final int FAVORITE_MOVIES = 400;
    static final int FAVORITE_MOVIE_BY_ID = 401;

    static final int FAVORITES_GENRES = 500;
    static final int FAVORITES_BY_GENRE = 501;

    static final int FAVORITES_RELATION = 600;

    private static final SQLiteQueryBuilder sMovieByIdQueryBuilder;
    private static final SQLiteQueryBuilder sFavoriteMoviesQueryBuilder;

    static{
        sMovieByIdQueryBuilder = new SQLiteQueryBuilder();
        sFavoriteMoviesQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //relation INNER JOIN movie ON relation.movie_id = movie._id INNER JOIN
        // genre ON relation.genre_id = genre._id
        sMovieByIdQueryBuilder.setTables(
                MovieContract.RelationEntry.MAIN_TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.MAIN_TABLE_NAME +
                        " ON " + MovieContract.RelationEntry.MAIN_TABLE_NAME +
                        "." + MovieContract.RelationEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.MovieEntry.MAIN_TABLE_NAME +
                        "." + MovieContract.MovieEntry._ID +
                        " INNER JOIN " + MovieContract.GenreEntry.MAIN_TABLE_NAME +
                        " ON " + MovieContract.RelationEntry.MAIN_TABLE_NAME +
                        "." + MovieContract.RelationEntry.COLUMN_GENRE_ID +
                        " = " + MovieContract.GenreEntry.MAIN_TABLE_NAME +
                        "." + MovieContract.GenreEntry._ID);

        sFavoriteMoviesQueryBuilder.setTables(
                MovieContract.RelationEntry.FAVORITES_TABLE_NAME + " INNER JOIN " +
                MovieContract.MovieEntry.FAVORITES_TABLE_NAME +
                " ON " + MovieContract.RelationEntry.FAVORITES_TABLE_NAME +
                "." + MovieContract.RelationEntry.COLUMN_MOVIE_ID +
                " = " + MovieContract.MovieEntry.FAVORITES_TABLE_NAME +
                "." + MovieContract.MovieEntry._ID +
                " INNER JOIN " + MovieContract.GenreEntry.FAVORITES_TABLE_NAME +
                " ON " + MovieContract.RelationEntry.FAVORITES_TABLE_NAME +
                "." + MovieContract.RelationEntry.COLUMN_GENRE_ID +
                " = " + MovieContract.GenreEntry.FAVORITES_TABLE_NAME +
                "." + MovieContract.GenreEntry._ID);
    }

    //movie.all._ID = ?
    private static final String sAllMoviesIdSelection =
            MovieContract.MovieEntry.MAIN_TABLE_NAME +
                    "." + MovieContract.MovieEntry._ID + " = ? ";

    //movie.favorite._ID = ?
    private static final String sFavoriteMoviesIdSelection =
            MovieContract.MovieEntry.MAIN_TABLE_NAME +
                    "." + MovieContract.MovieEntry._ID + " = ? ";
    private static final String sFavoriteMoviesGenreSelection =
            MovieContract.GenreEntry.FAVORITES_TABLE_NAME +
                    "." + MovieContract.GenreEntry.COLUMN_NAME + " = ? ";
/* TODO Delete if irrelavant.

    //location.location_setting = ? AND date >= ?
    private static final String sLocationSettingWithStartDateSelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " >= ? ";

    //location.location_setting = ? AND date = ?
    private static final String sLocationSettingAndDaySelection =
            WeatherContract.LocationEntry.TABLE_NAME +
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " = ? ";


    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        long startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == 0) {
            selection = sLocationSettingSelection;
            selectionArgs = new String[]{locationSetting};
        } else {
            selectionArgs = new String[]{locationSetting, Long.toString(startDate)};
            selection = sLocationSettingWithStartDateSelection;
        }

        return sMovieByIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getWeatherByLocationSettingAndDate(
            Uri uri, String[] projection, String sortOrder) {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        long date = WeatherContract.WeatherEntry.getDateFromUri(uri);

        return sMovieByIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sLocationSettingAndDaySelection,
                new String[]{locationSetting, Long.toString(date)},
                null,
                null,
                sortOrder
        );
    }
*/
    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the MOVIES_BY_POPULARITY, MOVIES_BY_RATING, MOVIE_BY_ID,
        and MOVIES_BY_GENRE integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIES);
//        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/popularity", MOVIES_BY_POPULARITY);
//        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/rating", MOVIES_BY_RATING);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_BY_ID);
//        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/find/*", FIND_MOVIE);

        matcher.addURI(authority, MovieContract.PATH_GENRE, GENRE);
//        matcher.addURI(authority, MovieContract.PATH_GENRE + "/*", MOVIES_BY_GENRE);

        matcher.addURI(authority, MovieContract.PATH_RELATION, RELATION);

        matcher.addURI(authority, MovieContract.PATH_FAVORITE_MOVIE, FAVORITE_MOVIES);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE_MOVIE + "/#", FAVORITE_MOVIE_BY_ID);

        matcher.addURI(authority, MovieContract.PATH_FAVORITES_GENRE, FAVORITES_GENRES);
        matcher.addURI(authority, MovieContract.PATH_FAVORITES_GENRE + "/*", FAVORITES_BY_GENRE);

        matcher.addURI(authority, MovieContract.PATH_FAVORITES_RELATION, FAVORITES_RELATION);

        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new WeatherDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.
     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
//            case MOVIES_BY_POPULARITY:
//                return MovieContract.MovieEntry.CONTENT_TYPE;
//            case MOVIES_BY_RATING:
//                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_BY_ID:
                return MovieContract.MovieEntry.CONTENT_TYPE;
//            case FIND_MOVIE:
//                return MovieContract.MovieEntry.CONTENT_TYPE;
            case GENRE:
                return MovieContract.GenreEntry.CONTENT_TYPE;
//            case MOVIES_BY_GENRE:
//                return MovieContract.GenreEntry.CONTENT_TYPE;
            case RELATION:
                return MovieContract.RelationEntry.CONTENT_TYPE;
            case FAVORITE_MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case FAVORITE_MOVIE_BY_ID:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case FAVORITES_GENRES:
                return MovieContract.GenreEntry.CONTENT_TYPE;
            case FAVORITES_BY_GENRE:
                return MovieContract.GenreEntry.CONTENT_TYPE;
            case FAVORITES_RELATION:
                return MovieContract.RelationEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIES:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.MAIN_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
//            case MOVIES_BY_POPULARITY:{
//                updateDbToPopular();
//                // TODO Check that retCursor doesn't get a value until the update ends
//                retCursor = mOpenHelper.getReadableDatabase().query(
//                        MovieContract.MovieEntry.MAIN_TABLE_NAME,
//                        projection,
//                        selection,
//                        selectionArgs,
//                        null,
//                        null,
//                        sortOrder);
//                break;
//            }
//            case MOVIES_BY_RATING:{
//                updateDbToRating();
//                // TODO Check that retCursor doesn't get a value until the update ends
//                retCursor = mOpenHelper.getReadableDatabase().query(
//                        MovieContract.MovieEntry.MAIN_TABLE_NAME,
//                        projection,
//                        selection,
//                        selectionArgs,
//                        null,
//                        null,
//                        sortOrder);
//                break;
//            }
            case MOVIE_BY_ID:{
                retCursor = sMovieByIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        sAllMoviesIdSelection,
                        new String[]{MovieContract.getMovieIdFromUri(uri)},
                        null,
                        null,
                        sortOrder);
                break;
            }
//            case FIND_MOVIE: {
//                // TODO Work on this feature later in version 2.0.
//                // Remember to delete the current insertion to retCursor.
//                retCursor = mOpenHelper.getReadableDatabase().query(
//                        MovieContract.GenreEntry.MAIN_TABLE_NAME,
//                        projection,
//                        selection,
//                        selectionArgs,
//                        null,
//                        null,
//                        sortOrder);
//                break;
//            }
            case GENRE:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.GenreEntry.MAIN_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
//            case MOVIES_BY_GENRE:{
//                updateMovieDbToGenre(uri);
//                // TODO Check that retCursor doesn't get a value until the update ends
//                retCursor = mOpenHelper.getReadableDatabase().query(
//                        MovieContract.MovieEntry.MAIN_TABLE_NAME,
//                        projection,
//                        selection,
//                        selectionArgs,
//                        null,
//                        null,
//                        sortOrder);
//                break;
//            }
            case RELATION:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.RelationEntry.MAIN_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case FAVORITE_MOVIES:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.FAVORITES_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case FAVORITE_MOVIE_BY_ID:{
                retCursor = sFavoriteMoviesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        sFavoriteMoviesIdSelection,
                        new String[]{MovieContract.getMovieIdFromUri(uri)},
                        null,
                        null,
                        sortOrder);
                break;
            }
            case FAVORITES_GENRES:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.GenreEntry.FAVORITES_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case FAVORITES_BY_GENRE:{
                retCursor = sFavoriteMoviesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        sFavoriteMoviesGenreSelection,
                        new String[]{MovieContract.getGenreFromUri(uri)},
                        null,
                        null,
                        sortOrder);
                break;
            }
            case FAVORITES_RELATION:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.RelationEntry.FAVORITES_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES: {
                long _id = db.insert(MovieContract.MovieEntry.MAIN_TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case GENRE: {
                long _id = db.insert(MovieContract.GenreEntry.MAIN_TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.GenreEntry.buildgGenreUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case RELATION: {
                long _id = db.insert(MovieContract.RelationEntry.MAIN_TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.RelationEntry.buildgRelationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITE_MOVIES: {
                long _id = db.insert(MovieContract.MovieEntry.FAVORITES_TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildFavoriteMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITES_GENRES: {
                long _id = db.insert(MovieContract.GenreEntry.FAVORITES_TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.GenreEntry.buildFavoriteGenreUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITES_RELATION: {
                long _id = db.insert(MovieContract.RelationEntry.FAVORITES_TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.RelationEntry.buildFavoriteRelationsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.MAIN_TABLE_NAME, selection, selectionArgs);
                break;
            case GENRE:
                rowsDeleted = db.delete(
                        MovieContract.GenreEntry.MAIN_TABLE_NAME, selection, selectionArgs);
                break;
            case RELATION:
                rowsDeleted = db.delete(
                        MovieContract.RelationEntry.MAIN_TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE_MOVIE_BY_ID:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.FAVORITES_TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITES_GENRES:
                rowsDeleted = db.delete(
                        MovieContract.GenreEntry.FAVORITES_TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITES_RELATION:
                rowsDeleted = db.delete(
                        MovieContract.RelationEntry.FAVORITES_TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

//    private void normalizeDate(ContentValues values) { TODO Delete method if irrelevant
//        // normalize the date value
//        if (values.containsKey(WeatherContract.WeatherEntry.COLUMN_DATE)) {
//            long dateValue = values.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);
//            values.put(WeatherContract.WeatherEntry.COLUMN_DATE, WeatherContract.normalizeDate(dateValue));
//        }
//    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIES:
                rowsUpdated = db.update(MovieContract.MovieEntry.MAIN_TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case GENRE:
                rowsUpdated = db.update(MovieContract.GenreEntry.MAIN_TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case RELATION:
                rowsUpdated = db.update(MovieContract.RelationEntry.MAIN_TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FAVORITE_MOVIES:
                rowsUpdated = db.update(MovieContract.MovieEntry.FAVORITES_TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FAVORITES_GENRES:
                rowsUpdated = db.update(MovieContract.GenreEntry.FAVORITES_TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FAVORITES_RELATION:
                rowsUpdated = db.update(MovieContract.RelationEntry.FAVORITES_TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.MAIN_TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case GENRE: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.GenreEntry.MAIN_TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case RELATION: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.RelationEntry.MAIN_TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case FAVORITE_MOVIES: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.FAVORITES_TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case FAVORITES_GENRES: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.GenreEntry.FAVORITES_TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case FAVORITES_RELATION: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.RelationEntry.FAVORITES_TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}