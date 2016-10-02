package com.example.android.moviez.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.moviez.data.MovieContract.MovieEntry;
import com.example.android.moviez.data.MovieContract.GenreEntry;
import com.example.android.moviez.data.MovieContract.RelationEntry;

/**
 * Manages a local database for movies data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold the movie's details. Each movie consists of its name,
        // release date, language, overview, child appropriateness, poster url path,
        // and votes counted in for the given score.
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.MAIN_TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_LANGUAGE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL " +
                MovieEntry.COLUMN_ADULT + " BOOLEAN NOT NULL CHECK (" +
                MovieEntry.COLUMN_ADULT + " IN (0,1) " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL " +
                MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL " +
                MovieEntry.COLUMN_AVG_SCORE + " DOUBLE NOT NULL " +
                " );";

        // Create a table to hold the genres. Each genre consists of its id given by TMDB site
        // and its name.
        final String SQL_CREATE_GENRE_TABLE = "CREATE TABLE " + GenreEntry.MAIN_TABLE_NAME + " (" +
                GenreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                GenreEntry.COLUMN_GENRE_ID + " INTEGER NOT NULL, " +
                GenreEntry.COLUMN_NAME + " TEXT NOT NULL " +
                " );";

        // Create a table to hold the relations between movies and their genres. Each relation
        // consists of the id of the movie in our database (not in TMDB's database !!!)
        // and its genre id (again in our database not TMDB's).
        final String SQL_CREATE_RELATION_TABLE = "CREATE TABLE " + RelationEntry.MAIN_TABLE_NAME + " (" +
                RelationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RelationEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                RelationEntry.COLUMN_GENRE_ID + " INTEGER NOT NULL, " +
                // Set up the movie_id column as a foreign key to movie table
                // and the genre_id column as a foreign key to genre table.
                " FOREIGN KEY (" + RelationEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.MAIN_TABLE_NAME + " (" + MovieEntry._ID + "), " +
                " FOREIGN KEY (" + RelationEntry.COLUMN_GENRE_ID + ") REFERENCES " +
                GenreEntry.MAIN_TABLE_NAME + " (" + GenreEntry._ID + "), " +
                " );";

        // Create a table to hold the favorite movies' details. Each movie consists of its name,
        // release date, language, overview, child appropriateness, poster url path,
        // and votes counted in for the given score.
        final String SQL_CREATE_MOVIE_FAVORITES_TABLE = "CREATE TABLE " +
                MovieEntry.FAVORITES_TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_LANGUAGE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL " +
                MovieEntry.COLUMN_ADULT + " BOOLEAN NOT NULL CHECK (" +
                MovieEntry.COLUMN_ADULT + " IN (0,1) " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL " +
                MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL " +
                MovieEntry.COLUMN_AVG_SCORE + " DOUBLE NOT NULL " +
                " );";

        // Create a table to hold the favorite movies' genres. Each genre consists of its id given by TMDB site
        // and its name.
        final String SQL_CREATE_GENRE_FAVORITES_TABLE = "CREATE TABLE " + GenreEntry.MAIN_TABLE_NAME
                + " (" + GenreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                GenreEntry.COLUMN_GENRE_ID + " INTEGER NOT NULL, " +
                GenreEntry.COLUMN_NAME + " TEXT NOT NULL " +
                " );";

        // Create a table to hold the relations between favorite movies and their genres. Each relation
        // consists of the id of the movie in our database (not in TMDB's database !!!)
        // and its genre id (again in our database not TMDB's).
        final String SQL_CREATE_RELATION_FAVORITES_TABLE = "CREATE TABLE " + RelationEntry.MAIN_TABLE_NAME + " (" +
                RelationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RelationEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                RelationEntry.COLUMN_GENRE_ID + " INTEGER NOT NULL, " +
                // Set up the movie_id column as a foreign key to movie table
                // and the genre_id column as a foreign key to genre table.
                " FOREIGN KEY (" + RelationEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.MAIN_TABLE_NAME + " (" + MovieEntry._ID + "), " +
                " FOREIGN KEY (" + RelationEntry.COLUMN_GENRE_ID + ") REFERENCES " +
                GenreEntry.MAIN_TABLE_NAME + " (" + GenreEntry._ID + "), " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_GENRE_FAVORITES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_RELATION_FAVORITES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_FAVORITES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_GENRE_FAVORITES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_RELATION_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.MAIN_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GenreEntry.MAIN_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RelationEntry.MAIN_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.FAVORITES_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GenreEntry.FAVORITES_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RelationEntry.FAVORITES_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}