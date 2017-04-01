package com.example.dmajc.cinephile_movieinfo.utilities;

import java.util.ArrayList;

/**
 * Created by tetrimino on 26.3.2017..
 */

public class QueryResultFactory {
    public QueryResult makeQueryResult(String mediaType, String name, String imagePath, int id, ArrayList<Integer> genreIds, String releaseDate){
        QueryResult newQueryResult = null;

        if(null == mediaType) {
            return new MovieQueryResult(name, imagePath, id, genreIds, releaseDate);
        }

        switch(mediaType) {
            case "movie":
                return new MovieQueryResult(name, imagePath, id, genreIds, releaseDate);
        }

        return null;
    }
}
