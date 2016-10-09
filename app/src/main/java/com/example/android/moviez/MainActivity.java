package com.example.android.moviez;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MovieGalleryFragment.Callback{

    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    @Override
    public void onItemSelected(Uri movieUri) {
        Intent infoIntent = new Intent(this, InfoActivity.class).setData(movieUri);
        startActivity(infoIntent);
    }
}
