package com.example.android.moviez;

import android.support.design.widget.BottomSheetDialogFragment;
import android.os.Bundle;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;

import java.util.Calendar;

import static android.R.attr.rating;
import static android.R.attr.y;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends BottomSheetDialogFragment {

    private float ratingLowValue;
    private float ratingHighValue;

    private long yearLowValue;
    private long yearHighValue;

    public SearchActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        // get seekbar from view
        final  CrystalRangeSeekbar ratingRangebar = (CrystalRangeSeekbar) rootView.findViewById(R.id.filter_rating_rangebar);

        // get min and max text view
        final TextView ratingRangebarLowTextView = (TextView) rootView.findViewById(R.id.filter_rating_low);
        final TextView ratingRangebarHighTextView = (TextView) rootView.findViewById(R.id.filter_rating_high);

        // set listener
        ratingRangebar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                ratingRangebarLowTextView.setText(String.format("%.1f", minValue));
                ratingRangebarHighTextView.setText(String.format("%.1f", maxValue));
            }
        });

        final CrystalRangeSeekbar yearRangebar = (CrystalRangeSeekbar) rootView.findViewById(R.id.filter_year_rangebar);

        yearRangebar.setMaxValue(Calendar.getInstance().get(Calendar.YEAR));

        // get min and max text view
        final TextView yearRangebarLowTextView = (TextView) rootView.findViewById(R.id.filter_year_low);
        final TextView yearRangebarHighTextView = (TextView) rootView.findViewById(R.id.filter_year_high);

        // set listener
        yearRangebar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                yearRangebarLowTextView.setText(String.valueOf(minValue));
                yearRangebarHighTextView.setText(String.valueOf(maxValue));
            }
        });

        ratingLowValue = (float) ratingRangebar.getSelectedMinValue();
        ratingHighValue = (float) ratingRangebar.getSelectedMaxValue();

        yearLowValue = (long) yearRangebar.getSelectedMinValue();
        yearHighValue = (long) yearRangebar.getSelectedMaxValue();

        ratingRangebar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                ratingLowValue = (float) minValue;
                ratingHighValue = (float) maxValue;
            }
        });

        yearRangebar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                yearLowValue = (int) minValue;
                yearHighValue = (int) maxValue;
            }
        });

        Button filterButton = (Button) rootView.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] genres = getGenresFromCheckboxes();
                Callback callback = (Callback) getActivity();
                callback.onFilterSelected(genres, ratingLowValue, ratingHighValue,
                        (int) yearLowValue, (int) yearHighValue);
            }
        });
        return rootView;
    }

    private String[] getGenresFromCheckboxes() {
        return new String[0];
    }

    public interface Callback{
        /**
         * SearchActivityFragmnent callback method for when a filter has been selected.
         */
        public void onFilterSelected(String[] genres, float minRating, float maxRating, int minYear, int maxYear);
    }
}
