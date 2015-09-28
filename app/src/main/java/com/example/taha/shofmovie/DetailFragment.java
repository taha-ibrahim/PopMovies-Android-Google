package com.example.taha.shofmovie;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    static final String ARG_PARAM1 = "param1";
    private View rootView;
    private Button favouriteButton;
    private TextView TextMovieDate;
    private TextView TextMovieTitle;
    private TextView TextMovieOverview;
    private TextView TextMovieRuntime;
    private TextView TextMovieRating;


    private ImageView iv;

    private ListView mListView_trailers;
    private ListView mListView_reviews;

    //an array for the trailers to pass for the listview each with the index
    String [] trailers = null;

    ArrayAdapter<String> adapter_Trailers;
    ArrayAdapter<String> adapter_reviews;
    ScrollView mScrollView;

    String [] Reviews_content;

    private int mParam1;
    // array of stings of all details needed for the movie
    String[] results;

    // a flag to determine whether the activity is running on tablet and in initial state so to blank the detail or not
    int notcreate;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment DetailFragment.
     */

    public static DetailFragment newInstance(int param1) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
        }
        results = new String[10];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        FetchMovieDetail MovieDe = new FetchMovieDetail(getActivity());
        mScrollView = (ScrollView) rootView.findViewById(R.id.detail_scroll);
        favouriteButton = (Button)rootView.findViewById(R.id.fav);
        final Button fav = (Button)rootView.findViewById(R.id.fav);
        MovieDe.execute(Integer.toString(mParam1));
        final int movieId = mParam1;
        final MovieSQL entry = new MovieSQL(getActivity());

        TextMovieDate=(TextView)rootView.findViewById(R.id.movie_date);



        TextMovieTitle=(TextView)rootView.findViewById(R.id.movie_name);


        TextMovieOverview=(TextView)rootView.findViewById(R.id.movie_overview);


        TextMovieRuntime=(TextView)rootView.findViewById(R.id.movie_time);


        TextMovieRating =(TextView)rootView.findViewById(R.id.movie_rating);

        iv = (ImageView) rootView.findViewById(R.id.movie_poster);

        mListView_trailers = (ListView) rootView.findViewById(R.id.trailer_listview);
        mListView_reviews = (ListView) rootView.findViewById(R.id.reviews_listview);

        fav.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                entry.open();

                if (fav.getText().equals("MARK AS FAVOURITE")) {
                    entry.updateFav(movieId, 1);


                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getActivity(), "Movie has been added to favourites", duration);
                    toast.show();
                    fav.setText("UNFAVOURITE");
                } else {
                    entry.updateFav(movieId, 0);

                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getActivity(), "Movie has been unfavoured", duration);
                    toast.show();
                    fav.setText("MARK AS FAVOURITE");
                }
                entry.close();
            }
        });

        mListView_trailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                try{
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailers[position]));
                    startActivity(intent);
                }catch (ActivityNotFoundException ex){
                    Intent intent=new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v="+id));
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }


    public class FetchMovieDetail extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchMovieDetail.class.getSimpleName();
        private final Context mContext;
        ProgressDialog progressDialog;

        public FetchMovieDetail(Context context) {
            mContext = context;

        }


        private void getDetailsURLFromJson(String PostersJsonStr, String TrailerJSON, String ReviewsJSON)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.

            JSONObject movie = new JSONObject(PostersJsonStr);

            results[0]=movie.getString("overview");
            results[1]=movie.getString("original_title");
            results[2]=movie.getString("vote_average");
            results[3]=movie.getString("release_date");
            results[4]=movie.getString("poster_path");
            results[5]=Integer.toString(movie.getInt("runtime"));
            results[6]=Integer.toString(movie.getInt("id"));
            results[7]="0";
            results[8]=TrailerJSON;
            results[9]=ReviewsJSON;

            MovieSQL entry = new MovieSQL(mContext);
            entry.open();
            entry.createEntry(results, 0);
            entry.close();

        }

        private void getTrailersURLFromJson(String TrailerJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.

            JSONObject movie = new JSONObject(TrailerJsonStr);
            JSONArray TrailerArray = movie.getJSONArray("results");

            String[] resultTrailers = new String[TrailerArray.length()];
            for (int i = 0; i < TrailerArray.length(); i++) {


                // Get the JSON object representing the day
                JSONObject trailer = TrailerArray.getJSONObject(i);
                resultTrailers[i] =trailer.getString("key");
            }

            trailers = resultTrailers;


        }

        private void getReviewsURLFromJson(String ReviewsJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.

            JSONObject movie = new JSONObject(ReviewsJsonStr);
            JSONArray ReviewArray = movie.getJSONArray("results");

            if (ReviewArray.length()==0){
                Reviews_content = new String[1];
                Reviews_content[0] = "No User Review";
                return;
            }

            String[] resultReviews = new String[ReviewArray.length()];
            for (int i = 0; i < ReviewArray.length(); i++) {


                // Get the JSON object representing the day
                JSONObject review = ReviewArray.getJSONObject(i);
                resultReviews[i] =review.getString("author") + "\n \t " +  review.getString("content");
            }
            Reviews_content = resultReviews;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rootView.setVisibility(View.GONE);
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(String... params) {

            // if passed 0 then nothing to create and set notcreate flag wit 0
            if (params[0].equals("0")) {
                notcreate = 0;
                return null;
            }
            notcreate =1;
            String MovieId = params[0];


            // first check whether this movie is in the sql or not
            MovieSQL entry = new MovieSQL(mContext);
            entry.open();
            String[] results2 = entry.FindMovie(Integer.parseInt(MovieId));
            entry.close();
            if(results2 != null)
            {
                results = results2;
                return null;
            }

            // if not in the database start to fetch it from the api
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String TrailersJsonStr = null;
            String ReviewsJsonStr = null;

            // start by getting the trailers json

            try {

                final String Movie_BASE_URL =
                        "http://api.themoviedb.org/3/movie/" + MovieId + "/videos";

                final String Key_PARAM = "api_key";

                Uri builtUri = Uri.parse(Movie_BASE_URL).buildUpon()
                        .appendQueryParameter(Key_PARAM, MainActivity.API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    trailers = null;
                } else {
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    TrailersJsonStr = buffer.toString();

                }
            }
            catch(IOException e){
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
            }

            urlConnection = null;
            reader = null;

            // get the user reviews
            try {

                final String Movie_BASE_URL =
                        "http://api.themoviedb.org/3/movie/" + MovieId + "/reviews";

                final String Key_PARAM = "api_key";

                Uri builtUri = Uri.parse(Movie_BASE_URL).buildUpon()
                        .appendQueryParameter(Key_PARAM, MainActivity.API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    Reviews_content = new String[1];
                    Reviews_content[0] = "No Reviews";
                } else {
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {

                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    ReviewsJsonStr = buffer.toString();

                }
            }

            catch(IOException e){
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
            }

            urlConnection = null;
            reader = null;

            // Will contain the raw JSON response as a string.
            String MovieJsonStr = null;

            try {

                final String Movie_BASE_URL =
                        "http://api.themoviedb.org/3/movie/" + MovieId;

                final String Key_PARAM = "api_key";

                Uri builtUri = Uri.parse(Movie_BASE_URL).buildUpon()
                        .appendQueryParameter(Key_PARAM, MainActivity.API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());


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
                MovieJsonStr = buffer.toString();
                getDetailsURLFromJson(MovieJsonStr,TrailersJsonStr,ReviewsJsonStr);
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
        protected void onPostExecute(Void s) {
            //check first to create or not
            if (notcreate != 0) {
                rootView.setVisibility(View.VISIBLE);
                progressDialog.hide();
                int favourite_state = Integer.parseInt(results[7]);
                if (favourite_state == 0) {
                    favouriteButton.setText("MARK AS FAVOURITE");
                    favouriteButton.setEnabled(true);
                } else {
                    favouriteButton.setText("UNFAVOURITE");
                    favouriteButton.setEnabled(true);
                }
                TextMovieDate.setText(results[3].substring(0, 4));
                TextMovieRating.setText(results[2] + "/10");
                TextMovieRuntime.setText(results[5] + "min");
                TextMovieOverview.setText(results[0]);
                TextMovieTitle.setText(results[1]);
                Picasso.with(rootView.getContext()).load("http://image.tmdb.org/t/p/w185" + results[4]).into(iv);
                try {
                    getReviewsURLFromJson(results[9]);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }

                if (Reviews_content != null) {
                    adapter_reviews = new ArrayAdapter<String>(mContext,
                            R.layout.list_review_item, R.id.user_review, Reviews_content);
                    mListView_reviews.setAdapter(adapter_reviews);
                    Utility.setListViewHeightBasedOnChildren(mListView_reviews, 1);

                }
                try {
                    getTrailersURLFromJson(results[8]);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                if (trailers != null) {

                    String[] values = new String[trailers.length];
                    for (int i = 0; i < trailers.length; i++)
                        values[i] = "Trailer " + (i + 1);
                    adapter_Trailers = new ArrayAdapter<String>(mContext,
                            R.layout.list_trailer_item, R.id.TrailerNo, values);
                    mListView_trailers.setAdapter(adapter_Trailers);
                    Utility.setListViewHeightBasedOnChildren(mListView_trailers, 0);

                }

                mScrollView.smoothScrollTo(0, 0);
            }
            else
                progressDialog.hide();
        }
    }


}
