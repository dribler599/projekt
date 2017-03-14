package cz.muni.fi;

import java.util.List;

/**
 * Interface for managing movies
 */
public interface MovieManager {

    /**
     * Creates and adds movie to database.
     *
     * @param movie of type Movie
     * @throws IllegalArgumentException when movie has invalid parameters or is null
     */
    void createMovie(Movie movie);

    /**
     * Gets movie by its id
     *
     * @param id positive number of type Long
     * @return movie of type Movie or null if movie doesn't exist
     * @throws IllegalArgumentException if id is invalid or null
     */
    Movie getMovie(Long id);

    /**
     * Updates 'movie' in database.
     *
     * @param movie of type Movie
     * @throws IllegalArgumentException when movie has invalid parameters or is null
     */
    void updateMovie(Movie movie);

    /**
     * Deletes 'movie' in database.
     *
     * @param movie of type Movie
     * @throws IllegalArgumentException when movie is invalid or null
     */
    void deleteMovie(Movie movie);

    /**
     * Gets all movies.
     *
     * @return all movies in form of List<Movie>
     */
    List<Movie> getAllMovies();

    /**
     * Gets movies by their name
     *
     * @param name name of wanted movies
     * @return list of all movies with 'name' in form of List<Movie>
     * @throws IllegalArgumentException if 'name' is invalid or null
     */
    List<Movie> getMovieByName(String name);
}
