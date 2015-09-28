package com.example.taha.shofmovie;

/**
 * Created by Taha on 9/17/2015.
 */
public class Movie{
    private String MoviePoster;
    private int MovieId;

    Movie(String poster , int id){
        MoviePoster = poster;
        MovieId = id;
    }

    public int getMovieId()
    {return MovieId;}

    public String getMoviePoster()
    {return MoviePoster;}


}
