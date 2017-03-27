package cz.muni.fi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class implementing MovieManager
 */
public class MovieManagerImpl implements MovieManager {

    final static Logger log = LoggerFactory.getLogger(MovieManagerImpl.class);
    private final DataSource dataSource;

    public MovieManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createMovie(Movie movie) throws MovieException{
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("insert into movie (name, year, classification, description, location)" +
                    " values (?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                st.setString(1, movie.getName());
                st.setInt(2, movie.getYear());
                st.setString(3, movie.getClassification());
                st.setString(4, movie.getDescription());
                st.setString(5, movie.getLocation());
                st.executeUpdate();
                ResultSet keys = st.getGeneratedKeys();
                if (keys.next()) {
                    movie.setId(keys.getLong(1));
                }
                log.debug("created book {}",movie);
            }
        } catch (SQLException e) {
            log.error("cannot insert book", e);
            throw new MovieException("database insert failed", e);
        }
    }

    @Override
    public Movie getMovie(Long id) throws MovieException{
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("select * from movie where id = ?")) {
                st.setLong(1, id);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    Long nid = rs.getLong("id");
                    String name = rs.getString("name");
                    int year = rs.getInt("year");
                    String classification = rs.getString("classification");
                    String description = rs.getString("description");
                    String location = rs.getString("location");
                    return new Movie(nid, name, year, classification, description, location);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            log.error("cannot select books", e);
            throw new MovieException("database select failed", e);
        }
    }

    @Override
    public void updateMovie(Movie movie) throws MovieException {
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("update movie set name=?, year=?, " +
                    "classification=?, description=?, location=? where id=?")) {
                st.setString(1, movie.getName());
                st.setInt(2, movie.getYear());
                st.setString(3, movie.getClassification());
                st.setString(4, movie.getDescription());
                st.setString(5, movie.getLocation());
                st.setLong(6, movie.getId());
                int n = st.executeUpdate();
                if (n == 0) {
                    throw new MovieException("not updated book with id " + movie.getId(), null);
                }
                if (n > 1) {
                    throw new MovieException("more than 1 book with id " + movie.getId(), null);
                }
                log.debug("updated book {}", movie);
            }
        } catch (SQLException e) {
            log.error("cannot update books", e);
            throw new MovieException("database update failed", e);
        }
    }

    @Override
    public void deleteMovie(Movie movie) throws MovieException{
        long id = movie.getId();
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("delete from movie where id=?")) {
                st.setLong(1, id);
                int n = st.executeUpdate();
                if (n == 0) {
                    throw new MovieException("not deleted book with id " + id, null);
                }
                log.debug("deleted book {}",id);
            }
        } catch (SQLException e) {
            log.error("cannot delete book", e);
            throw new MovieException("database delete failed", e);
        }
    }

    @Override
    public List<Movie> getAllMovies() throws MovieException{
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("select * from books")) {
                ResultSet rs = st.executeQuery();
                List<Movie> books = new ArrayList<>();
                    while (rs.next()) {
                    Long id = rs.getLong("id");
                    String name = rs.getString("name");
                    int year = rs.getInt("year");
                    String classification = rs.getString("classification");
                    String description = rs.getString("description");
                    String location = rs.getString("location");
                    books.add(new Movie(id, name, year, classification, description, location));
                }
                log.debug("getting all {} books",books.size());
                return books;
            }
        } catch (SQLException e) {
            log.error("cannot select books", e);
            throw new MovieException("database select failed", e);
        }
    }

    @Override
    public List<Movie> getMovieByName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
