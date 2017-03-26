package com.example.dmajc.cinephile_movieinfo.utilities;


import java.util.ArrayList;

/**
 * Created by tetrimino on 26.3.2017..
 */

public abstract class QueryResult {

    protected static final String POSTER_DEFAULT_URL = "https://image.tmdb.org/t/p/w500";
    protected String imagePath;
    protected int id;
    protected ArrayList<Integer> genreIds;
    protected String name;
    protected String releaseDate;
    protected String mediaType;

    public String getImagePath() { return imagePath; }
    public int getId() { return id; }
    public String getName() { return name; }
    public ArrayList<Integer> getGenreIds() { return genreIds; }
    public String getReleaseDate() { return releaseDate; }
    public String getMediaType() { return mediaType; }

}
