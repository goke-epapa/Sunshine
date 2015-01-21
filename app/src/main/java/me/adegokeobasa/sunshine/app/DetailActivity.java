package me.adegokeobasa.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.adegokeobasa.sunshine.app.data.WeatherContract;

public class DetailActivity extends ActionBarActivity {

    private static final int FORECAST_LOADER = 0;
    ShareActionProvider mShareActionProvider;
    public static final String SHARE_HASH_TAG = "#SunshineApp";
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying. On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };


    // These indices are tied to FORECAST_COLUMNS. If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        mShareActionProvider.setShareIntent(new Intent(Intent.ACTION_SEND)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, getIntent().getStringExtra(Intent.EXTRA_TEXT) + SHARE_HASH_TAG));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(DetailActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        TextView detailsView;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            detailsView = (TextView) rootView.findViewById(R.id.details);
            return rootView;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (!getActivity().getIntent().hasExtra(Intent.EXTRA_TEXT)) {
                return null;
            }

            String mLocation = Utility.getPreferredLocation(getActivity());
            String weatherDate = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);
            // Sort order: Ascending, by date.
            String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

            Uri dayWeatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(mLocation, weatherDate);

            return new CursorLoader(
                    getActivity(),
                    dayWeatherUri,
                    FORECAST_COLUMNS,
                    null,
                    null,
                    sortOrder);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if(data != null && data.moveToFirst())
                detailsView.setText(data.getString(COL_WEATHER_MAX_TEMP));
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            getLoaderManager().initLoader(FORECAST_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }
    }
}
