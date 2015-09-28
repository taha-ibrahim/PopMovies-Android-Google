package com.example.taha.shofmovie;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

//import com.example.taha.shofmovie.FetchMovie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment  {


    public static int LOADER = 0;
    private ArrayList<Movie> mAdapter;
    private ImageAdapter mImageAdapter;
    View rootView;
    GridView gridview;


    public MovieFragment (){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        gridview = (GridView) rootView.findViewById(R.id.gridView);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(rootView.getContext());
        String sorting = prefs.getString(getString(R.string.sorting_preference),
                getString(R.string.sorting_preference_default));
        mAdapter = new ArrayList<Movie>();
        mImageAdapter = new ImageAdapter(rootView.getContext(), mAdapter);
        FetchMovie ft = new FetchMovie(getActivity());
        ft.execute(sorting);


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                if (MainActivity.mTwoPane) {
                    // In two-pane mode, show the detail view in this activity by
                    // adding or replacing the detail fragment using a
                    // fragment transaction.
                    Bundle args = new Bundle();
                    args.putInt(DetailFragment.ARG_PARAM1, getMovieId(position));
                    DetailFragment fragment = new DetailFragment();
                    fragment.setArguments(args);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.Detail_Movie, fragment)
                            .commit();
                } else {
                    Intent intent = new Intent(parent.getContext(), DetailActivity.class);
                    intent.putExtra("EXTRA_MOVIE_ID", getMovieId(position));
                    startActivity(intent);
                    LOADER = 1;
                }


            }
        });
        return rootView;
    }

    public void Updateview(String sorting){
        FetchMovie ft=new FetchMovie(getActivity());
        ft.execute(sorting);
    }

    public int getMovieId (int position)
    {
        return mAdapter.get(position).getMovieId();
    }

    public class FetchMovie extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMovie.class.getSimpleName();
        private final Context mContext;
        private ProgressDialog progressDialog;


        public FetchMovie(Context context) {
            mContext = context;
            //mAdapter = Adapter;
        }


        private Movie[] getPostersURLFromJson(String PostersJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_RESULTS = "results";
            final String OWM_poster = "poster_path";
            final String OWM_ID = "id";
            JSONObject forecastJson = new JSONObject(PostersJsonStr);
            JSONArray MoviesArray = forecastJson.getJSONArray(OWM_RESULTS);


            Movie[] resultMovies = new Movie[MoviesArray.length()];
            for (int i = 0; i < MoviesArray.length(); i++) {

                String PosterLink;
                int id;


                JSONObject movie = MoviesArray.getJSONObject(i);

                PosterLink = movie.getString(OWM_poster);
                id = movie.getInt(OWM_ID);

                resultMovies[i] =new Movie("http://image.tmdb.org/t/p/w185"+PosterLink,id);
            }

            return resultMovies;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();

        }

        @Override
        protected Movie[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            String SortPrefernce = params[0];

            if(SortPrefernce.equals("favourites"))
            {
                MovieSQL entry = new MovieSQL(mContext);
                entry.open();
                Movie[] results2 = entry.getFav();
                entry.close();
                return results2;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {

                final String Key_PARAM = "api_key";
                Uri builtUri;

                if(params[0].equals("popularity.desc")) {
                    final String Movie_BASE_URL =
                            "http://api.themoviedb.org/3/discover/movie?";
                    final String Sort_PARAM = "sort_by";

                    builtUri = Uri.parse(Movie_BASE_URL).buildUpon()
                            .appendQueryParameter(Sort_PARAM, params[0])
                            .appendQueryParameter(Key_PARAM, MainActivity.API_KEY)
                            .build();

                }
                else
                {
                    final String Movie_BASE_URL =
                            "http://api.themoviedb.org/3/movie/top_rated?";

                    builtUri = Uri.parse(Movie_BASE_URL).buildUpon()
                            .appendQueryParameter(Key_PARAM, MainActivity.API_KEY)
                            .build();

                }
                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                return getPostersURLFromJson( forecastJsonStr );
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null && mAdapter != null) {
                mAdapter.clear();
                for(Movie theNewMovie : result) {
                    mAdapter.add(theNewMovie);
                }
                // New data is back from the server.  Hooray!
                gridview.setAdapter(new ImageAdapter(getActivity(), mAdapter));
            }
            progressDialog.hide();
        }

    }

}
