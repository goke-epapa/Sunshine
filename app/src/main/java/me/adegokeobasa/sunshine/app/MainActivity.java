package me.adegokeobasa.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (id == R.id.action_view_location) {
            openPreferredLocation();
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocation() {
        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        String location = PreferenceManager.getDefaultSharedPreferences(this).
                getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default_value));
        Uri uri = Uri.parse("geo:0,0").buildUpon()
                .appendQueryParameter("q", location).build();

        mapIntent.setData(uri);
        if(mapIntent.resolveActivity(getPackageManager()) != null){
            startActivity(mapIntent);
        } else {
            Log.d(TAG, "Couldn't find package");
        }
    }


}
