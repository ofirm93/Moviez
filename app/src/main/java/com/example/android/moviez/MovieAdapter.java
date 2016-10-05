package com.example.android.moviez;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.moviez.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by Ofir on 05/10/2016.
 */
public class MovieAdapter extends CursorAdapter {

    private final static String BASE_PATH = "http://image.tmdb.org/t/p/w500";

    private static final String[] MOVIES_COLUMNS = {
            MovieContract.MovieEntry.MAIN_TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_ADULT,
            MovieContract.MovieEntry.COLUMN_AVG_SCORE
    };

    // These indices are tied to MOVIES_COLUMNS.  If MOVIES_COLUMNS changes, these
    // must change.
    static final int COL_MOVIE_ID = 0;
    static final int COL_TITLE = 1;
    static final int COL_POSTER_PATH = 2;
    static final int COL_ADULT = 3;
    static final int COL_AVG_SCORE = 4;

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.listview_item_imageview, parent,
                false);

        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        Uri uri = Uri.parse(BASE_PATH).buildUpon()
                .appendEncodedPath(cursor.getString(COL_POSTER_PATH))
                .build();
        Picasso.with(context).load(uri.toString()).placeholder(R.drawable.default_movie_poster)
                .into(viewHolder.posterView);
        viewHolder.titleView.setText(cursor.getString(COL_TITLE));
        viewHolder.movieID = cursor.getString(COL_MOVIE_ID);
    }

    public static class ViewHolder {
        public final ImageView posterView;
        public final TextView titleView;
        public String movieID;

        public ViewHolder(View view){
            posterView = (ImageView) view.findViewById(R.id.movie_poster);
            titleView = (TextView) view.findViewById(R.id.movie_title);
            movieID = null;
        }


    }
}
