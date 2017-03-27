package cz.muni.fi;

import java.util.List;
import javax.sql.DataSource;

/**
 * Class implementing MovieManager
 */
public class MovieManagerImpl implements MovieManager {

    private final DataSource dataSource;

    public MovieManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createMovie(Movie movie) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Movie getMovie(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateMovie(Movie movie) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteMovie(Movie movie) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Movie> getAllMovies() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Movie> getMovieByName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
