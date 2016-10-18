package com.example.android.moviez;

import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends BottomSheetDialogFragment {

    public SearchActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        // get seekbar from view
        final CrystalRangeSeekbar ratingRangebar = (CrystalRangeSeekbar) rootView.findViewById(R.id.filter_rating_rangebar);

        // get min and max text view
        final TextView ratingRangebarLow = (TextView) rootView.findViewById(R.id.filter_rating_low);
        final TextView ratingRangebarHigh = (TextView) rootView.findViewById(R.id.filter_rating_high);

        // set listener
        ratingRangebar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                ratingRangebarLow.setText(String.format("%.1f", minValue));
                ratingRangebarHigh.setText(String.format("%.1f", maxValue));
            }
        });

        return rootView;
    }
}
