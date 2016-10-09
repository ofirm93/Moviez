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
        // The SQL query created is:
        // CREATE TABLE movie_all (_id INTEGER PRIMARY KEY,title TEXT NOT NULL,
        // release_date TEXT NOT NULL, language TEXT NOT NULL, overview TEXT NOT NULL,
        // adult BOOLEAN NOT NULL CHECK(adult IN (0,1)), poster_path TEXT NOT NULL,
        // vote_count INTEGER NOT NULL, avg_score DOUBLE NOT NULL, tmdb_id INTEGER NOT NULL);
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.MAIN_TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_LANGUAGE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_ADULT + " BOOLEAN NOT NULL CHECK(" +
                MovieEntry.COLUMN_ADULT + " IN (0,1)), " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_AVG_SCORE + " DOUBLE NOT NULL," +
                MovieEntry.COLUMN_TMDB_ID + " INTEGER NOT NULL" +
                ");";

        // Create a table to hold the genres. Each genre consists of its id given by TMDB site
        // and its name.
        // The SQL query created is:
        // CREATE TABLE genre_all (_id INTEGER PRIMARY KEY AUTOINCREMENT, genre_id INTEGER NOT NULL,
        // name TEXT NOT NULL);
        final String SQL_CREATE_GENRE_TABLE = "CREATE TABLE " + GenreEntry.MAIN_TABLE_NAME + " (" +
                GenreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GenreEntry.COLUMN_GENRE_ID + " INTEGER NOT NULL, " +
                GenreEntry.COLUMN_NAME + " TEXT NOT NULL" +
                ");";

        // Create a table to hold the relations between movies and their genres. Each relation
        // consists of the id of the movie in our database (not in TMDB's database !!!)
        // and its genre id (again in our database not TMDB's).
        // The SQL query created is:
        // CREATE TABLE relation_all (_id INTEGER PRIMARY KEY AUTOINCREMENT,
        // movie_id INTEGER NOT NULL, genre_id INTEGER NOT NULL,
        // FOREIGN KEY (movie_id) REFERENCES movie_all (tmdb_id),
        // FOREIGN KEY (genre_id) REFERENCES genre_all (genre_id));
        final String SQL_CREATE_RELATION_TABLE = "CREATE TABLE " + RelationEntry.MAIN_TABLE_NAME + " (" +
                RelationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RelationEntry.COLUMN_MOVIE_TMDB_ID + " INTEGER NOT NULL, " +
                RelationEntry.COLUMN_GENRE_ID + " INTEGER NOT NULL, " +
                // Set up the movie_id column as a foreign key to movie table
                // and the genre_id column as a foreign key to genre table.
                "FOREIGN KEY (" + RelationEntry.COLUMN_MOVIE_TMDB_ID + ") REFERENCES " +
                MovieEntry.MAIN_TABLE_NAME + " (" + MovieEntry.COLUMN_TMDB_ID + "), " +
                "FOREIGN KEY (" + RelationEntry.COLUMN_GENRE_ID + ") REFERENCES " +
                GenreEntry.MAIN_TABLE_NAME + " (" + GenreEntry.COLUMN_GENRE_ID + ")" +
                ");";

        // Create a table to hold the favorite movies' details. Each movie consists of its name,
        // release date, language, overview, child appropriateness, poster url path,
        // and votes counted in for the given score.
        // The SQL query created is:
        // CREATE TABLE movie_favorite (_id INTEGER PRIMARY KEY, title TEXT NOT NULL,
        // release_date TEXT NOT NULL, language TEXT NOT NULL, overview TEXT NOT NULL,
        // adult BOOLEAN NOT NULL CHECK(adult IN (0,1)), poster_path TEXT NOT NULL,
        // vote_count INTEGER NOT NULL, avg_score DOUBLE NOT NULL, tmdb_id INTEGER NOT NULL);
        final String SQL_CREATE_MOVIE_FAVORITES_TABLE = "CREATE TABLE " +
                MovieEntry.FAVORITES_TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_LANGUAGE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_ADULT + " BOOLEAN NOT NULL CHECK(" +
                MovieEntry.COLUMN_ADULT + " IN (0,1)), " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_AVG_SCORE + " DOUBLE NOT NULL," +
                MovieEntry.COLUMN_TMDB_ID + " INTEGER NOT NULL" +
                ");";

        // Create a table to hold the favorite movies' genres. Each genre consists of its id given by TMDB site
        // and its name.
        // The SQL query created is:
        // CREATE TABLE genre_favorite (_id INTEGER PRIMARY KEY AUTOINCREMENT,
        // genre_id INTEGER NOT NULL, name TEXT NOT NULL);
        final String SQL_CREATE_GENRE_FAVORITES_TABLE = "CREATE TABLE " + GenreEntry.FAVORITES_TABLE_NAME
                + " (" + GenreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GenreEntry.COLUMN_GENRE_ID + " INTEGER NOT NULL, " +
                GenreEntry.COLUMN_NAME + " TEXT NOT NULL" +
                ");";

        // Create a table to hold the relations between favorite movies and their genres. Each relation
        // consists of the id of the movie in our database (not in TMDB's database !!!)
        // and its genre id (again in our database not TMDB's).
        // The SQL query created is:
        // CREATE TABLE relation_favorite (_id INTEGER PRIMARY KEY AUTOINCREMENT,
        // movie_id INTEGER NOT NULL, genre_id INTEGER NOT NULL,
        // FOREIGN KEY (movie_id) REFERENCES movie_favorite (tmdb_id),
        // FOREIGN KEY (genre_id) REFERENCES genre_favorite (genre_id));
        final String SQL_CREATE_RELATION_FAVORITES_TABLE = "CREATE TABLE " + RelationEntry.FAVORITES_TABLE_NAME + " (" +
                RelationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RelationEntry.COLUMN_MOVIE_TMDB_ID + " INTEGER NOT NULL, " +
                RelationEntry.COLUMN_GENRE_ID + " INTEGER NOT NULL, " +
                // Set up the movie_id column as a foreign key to movie table
                // and the genre_id column as a foreign key to genre table.
                "FOREIGN KEY (" + RelationEntry.COLUMN_MOVIE_TMDB_ID + ") REFERENCES " +
                MovieEntry.FAVORITES_TABLE_NAME + " (" + MovieEntry.COLUMN_TMDB_ID + "), " +
                "FOREIGN KEY (" + RelationEntry.COLUMN_GENRE_ID + ") REFERENCES " +
                GenreEntry.FAVORITES_TABLE_NAME + " (" + GenreEntry.COLUMN_GENRE_ID + ")" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_GENRE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_RELATION_TABLE);
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