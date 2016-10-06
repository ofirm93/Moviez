package com.example.android.moviez;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;

import com.example.android.moviez.Sync.MoviezSyncService;
import com.example.android.moviez.data.MovieContract;

public class MovieGallaryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private final static int MOVIES_LOADER_ID = 0;
    private int currentPage = 1;
    private MovieAdapter mGalleryViewAdapter;
    private int totalViewInGrid = 0;
    private boolean isDBUpdates = false;

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

    public MovieGallaryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGalleryViewAdapter = new MovieAdapter(getContext(), null, 0);
        MoviezSyncService.startIntent(getContext(), currentPage);
        isDBUpdates = true;
        currentPage++;
    }

    private void updateMoviesData(int page){
        MoviezSyncService.startIntent(getContext(), page);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_gallary, container, false);

        GridView gallery = (GridView) view.findViewById(R.id.fragment_movie_gallery_grid);

        gallery.setAdapter(mGalleryViewAdapter);
        gallery.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(!isDBUpdates && firstVisibleItem + visibleItemCount >= totalItemCount && currentPage<=200){
                    updateMoviesData(currentPage);
                    isDBUpdates = true;
                    currentPage++;
                    onChanged();
                }
                if(totalViewInGrid < totalItemCount){
                    totalViewInGrid = totalItemCount;
                    isDBUpdates = false;
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), MovieContract.MovieEntry.CONTENT_URI, MOVIES_COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mGalleryViewAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mGalleryViewAdapter.swapCursor(null);
    }

    // TODO: If there is a change in the settings that causes change in the database call this method
    void onChanged(){
        getLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);

    }
}
