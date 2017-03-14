package cz.muni.fi;

import java.util.List;

/**
 * Interface for managing movies
 */
public interface MovieManager {

    void createMovie(Movie movie);

    Movie getMovie(Long id);

    void updateMovie(Movie movie);

    void deleteMovie(Movie movie);

    List<Movie> getAllMovies();

    Movie getMovieByName(String name);
}
