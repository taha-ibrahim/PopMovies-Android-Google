package com.example.taha.shofmovie;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.taha.shofmovie.MovieFragment;


public class MainActivity extends ActionBarActivity  {

// the api key got from movie api

    public static final String API_KEY = "" ;

    /////

    public static String mSorting;
    public static Boolean mTwoPane;
    MovieFragment mainfrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSorting = Utility.getPreferredLocation(this);
        setContentView(R.layout.activity_main);
        mainfrag = new MovieFragment();
        if(savedInstanceState == null)
        {
            if (findViewById(R.id.Detail_Movie) != null) {
                mTwoPane = true;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.Detail_Movie, new DetailFragment())
                        .commit();
            }
            else
            {
                mTwoPane = false;
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mainfrag)
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sorting = Utility.getPreferredLocation(this);
        // update the location in our second pane using the fragment manager
        if ((sorting != null && !sorting.equals(mSorting)) || MovieFragment.LOADER == 1) {
            MovieFragment ff = (MovieFragment)getSupportFragmentManager().findFragmentById(R.id.container);
            if ( null != ff ) {
                ff.Updateview(sorting);
            }
            MovieFragment.LOADER=0;
            mSorting = sorting;
        }
    }

}
