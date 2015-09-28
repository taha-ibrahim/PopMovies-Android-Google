package com.example.taha.shofmovie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Taha on 9/18/2015.
 */
public class MovieSQL {

    private static final String TABLE_NAME = "movie";
    private static final String DATABASE_NAME = "moviedb";
    private static final int DATABASE_VERSION = 2;


    public static final String COLUMN_MOVIE_NAME = "original_title";
    public static final String COLUMN_MOVIE_ID = "id";

    public static final String COLUMN_MOVIE_OVERVIEW = "overview";

    public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";
    public static final String COLUMN_MOVIE_POSTER_PATH = "poster_path";
    public static final String COLUMN_MOVIE_Favourite = "favourite";
    public static final String COLUMN_MOVIE_RUNTIME = "runtime";
    public static final String COLUMN_MOVIE_VOTING = "voting";
    public static final String COLUMN_MOVIE_TRAILERS_JSON = "trailer";
    public static final String COLUMN_MOVIE_REVIEWS_JSON = "review";

    private MovDbHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    public MovieSQL(Context c)
    {
        ourContext = c;
    }

    public MovieSQL open()
    {
        ourHelper = new MovDbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        ourHelper.close();
    }

    public long createEntry(String[] entryies, int fav)
    {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MOVIE_ID,Integer.parseInt(entryies[6]));
        cv.put(COLUMN_MOVIE_Favourite,fav);
        cv.put(COLUMN_MOVIE_NAME,entryies[1]);
        cv.put(COLUMN_MOVIE_OVERVIEW,entryies[0]);
        cv.put(COLUMN_MOVIE_POSTER_PATH,entryies[4]);
        cv.put(COLUMN_MOVIE_RELEASE_DATE,entryies[3]);
        cv.put(COLUMN_MOVIE_VOTING,entryies[2]);
        cv.put(COLUMN_MOVIE_RUNTIME, Integer.parseInt(entryies[5]));
        cv.put(COLUMN_MOVIE_TRAILERS_JSON, entryies[8]);
        cv.put(COLUMN_MOVIE_REVIEWS_JSON,entryies[9]);
        return ourDatabase.insert(TABLE_NAME,null,cv);
    }

    public void updateFav(int movie_id,int fav){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MOVIE_Favourite,fav);
        ourDatabase.update(TABLE_NAME, cv, "id =" + movie_id, null);
    }


    public Movie[] getFav()
    {
        Cursor cursor = ourDatabase.query(TABLE_NAME,null,"favourite =" + 1,null,null,null,null);
        Movie[] results = new Movie[cursor.getCount()];
        cursor.moveToFirst();
        for(int i=0; i< cursor.getCount(); i++)
        {
            results[i]= new Movie("http://image.tmdb.org/t/p/w185"+cursor.getString(3),cursor.getInt(0));
            cursor.moveToNext();
        }
        return results;
    }

    public String[] FindMovie(int movie_id)
    {
        String[] reuslts;
        Cursor cursor = ourDatabase.query(TABLE_NAME,null,"id =" + movie_id,null,null,null,null);
        if(cursor.moveToFirst() == false)
        {
            reuslts=null;
        }
        else
        {
            reuslts = new String[10];
            reuslts[6]= Integer.toString(cursor.getInt(0));
            reuslts[5]= Integer.toString(cursor.getInt(5));
            reuslts[7]= Integer.toString(cursor.getInt(6));
            reuslts[1]= cursor.getString(1);
            reuslts[0]= cursor.getString(2);
            reuslts[4]= cursor.getString(3);
            reuslts[3]= cursor.getString(4);
            reuslts[2]= cursor.getString(7);
            reuslts[8]= cursor.getString(8);
            reuslts[9]= cursor.getString(9);
            return reuslts;

        }


        return reuslts;
    }


    public class MovDbHelper extends SQLiteOpenHelper {

        // If you change the database schema, you must increment the database version.

        public MovDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +

                    COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +


                    COLUMN_MOVIE_NAME + " TEXT NOT NULL, " +
                    COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                    COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                    COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL," +
                    COLUMN_MOVIE_RUNTIME + " INTEGER NOT NULL," +
                    COLUMN_MOVIE_Favourite + " INTEGER NOT NULL," +
                    COLUMN_MOVIE_VOTING + " TEXT NOT NULL,"+
                    COLUMN_MOVIE_TRAILERS_JSON + " TEXT,"+
                    COLUMN_MOVIE_REVIEWS_JSON + " TEXT)";


            sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            // Note that this only fires if you change the version number for your database.
            // It does NOT depend on the version number for your application.
            // If you want to update the schema without wiping data, commenting out the next 2 lines
            // should be your top priority before modifying this method.
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }
}
