package cz.muni.fi.jdbc;

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
    void createMovie(Movie movie) throws MovieException;

    /**
     * Gets movie by its id
     *
     * @param id positive number of type Long
     * @return movie of type Movie or null if movie doesn't exist
     * @throws IllegalArgumentException if id is invalid or null
     */
    Movie getMovie(Long id) throws MovieException;

    /**
     * Updates 'movie' in database.
     *
     * @param movie of type Movie
     * @throws IllegalArgumentException when movie has invalid parameters or is null
     */
    void updateMovie(Movie movie) throws MovieException;

    /**
     * Deletes 'movie' in database.
     *
     * @param movie of type Movie
     * @throws IllegalArgumentException when movie is invalid or null
     */
    void deleteMovie(Movie movie) throws MovieException;

    /**
     * Gets all movies.
     *
     * @return all movies in form of List<Movie>
     */
    List<Movie> getAllMovies() throws MovieException;

    /**
     * Gets movies by their name
     *
     * @param name name of wanted movies
     * @return list of all movies with 'name' in form of List<Movie>
     * @throws IllegalArgumentException if 'name' is invalid or null
     */
    List<Movie> getMovieByName(String name) throws MovieException;
}
