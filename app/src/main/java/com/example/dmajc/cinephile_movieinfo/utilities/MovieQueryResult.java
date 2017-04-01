package com.example.dmajc.cinephile_movieinfo.utilities;

import com.example.dmajc.cinephile_movieinfo.utilities.QueryResult;

import java.util.ArrayList;

/**
 * Created by tetrimino on 26.3.2017..
 */

public class MovieQueryResult extends QueryResult {
    public MovieQueryResult(String name, String imagePath, int id, ArrayList<Integer> genreIds, String releaseDate) {
        this.name = name;
        this.imagePath = POSTER_DEFAULT_URL + imagePath;
        this.id = id;
        this.genreIds = genreIds;
        this.releaseDate = releaseDate;
        this.clicked = false;
    }
}
