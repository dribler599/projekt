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
import javax.sql.DataSource;

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
            try (PreparedStatement st = con.prepareStatement("INSERT INTO MOVIE (NAME, YEAR, CLASSIFICATION, DESCRIPTION, LOCATION)" +
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
                log.debug("created movie {}",movie);
            }
        } catch (SQLException e) {
            log.error("cannot insert movie", e);
            throw new MovieException("database insert failed", e);
        }
    }

    @Override
    public Movie getMovie(Long id) throws MovieException{
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("SELECT * FROM MOVIE WHERE ID = ?")) {
                st.setLong(1, id);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    Long nid = rs.getLong("ID");
                    String name = rs.getString("NAME");
                    int year = rs.getInt("YEAR");
                    String classification = rs.getString("CLASSIFICATION");
                    String description = rs.getString("DESCRIPTION");
                    String location = rs.getString("LOCATION");
                    return new Movie(nid, name, year, classification, description, location);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            log.error("cannot select movie", e);
            throw new MovieException("database select failed", e);
        }
    }

    @Override
    public void updateMovie(Movie movie) throws MovieException {
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("UPDATE MOVIE SET NAME = ?, YEAR = ?, " +
                    "CLASSIFICATION = ?, DESCRIPTION = ?, LOCATION = ? WHERE ID = ?")) {
                st.setString(1, movie.getName());
                st.setInt(2, movie.getYear());
                st.setString(3, movie.getClassification());
                st.setString(4, movie.getDescription());
                st.setString(5, movie.getLocation());
                st.setLong(6, movie.getId());
                int n = st.executeUpdate();
                if (n == 0) {
                    throw new MovieException("not updated movie with id " + movie.getId(), null);
                }
                if (n > 1) {
                    throw new MovieException("more than 1 movie with id " + movie.getId(), null);
                }
                log.debug("updated movie {}", movie);
            }
        } catch (SQLException e) {
            log.error("cannot update movie", e);
            throw new MovieException("database update failed", e);
        }
    }

    @Override
    public void deleteMovie(Movie movie) throws MovieException{
        long id = movie.getId();
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("DELETE FROM MOVIE WHERE ID = ?")) {
                st.setLong(1, id);
                int n = st.executeUpdate();
                if (n == 0) {
                    throw new MovieException("not deleted movie with id " + id, null);
                }
                log.debug("deleted movie {}",id);
            }
        } catch (SQLException e) {
            log.error("cannot delete movie", e);
            throw new MovieException("database delete failed", e);
        }
    }

    @Override
    public List<Movie> getAllMovies(){
        String sql = "SELECT * FROM MOVIE";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            List<Movie> movies = new ArrayList<>();
            while (rs.next()) {
                String classification = rs.getString("CLASSIFICATION");
                String description = rs.getString("DESCRIPTION");
                String location = rs.getString("LOCATION");
                Long id = rs.getLong("ID");
                String name = rs.getString("NAME");
                int year = rs.getInt("YEAR");

                movies.add(new Movie(id, name, year, classification, description, location));
            }
            log.debug("getting all {} movies", movies.size());
            return movies;

        } catch (SQLException e) {
            log.error("cannot select movies", e);
            throw new RuntimeException(e);

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }

    @Override
    public List<Movie> getMovieByName(String n) {
        String sql = "SELECT * FROM MOVIE WHERE NAME = ?";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, n);
            ResultSet rs = ps.executeQuery();
            List<Movie> movies = new ArrayList<>();
            while (rs.next()) {
                Long id = rs.getLong("ID");
                String name = rs.getString("NAME");
                int year = rs.getInt("YEAR");
                String classification = rs.getString("CLASSIFICATION");
                String description = rs.getString("DESCRIPTION");
                String location = rs.getString("LOCATION");

                movies.add(new Movie(id, name, year, classification, description, location));
            }
            log.debug("getting all {} movies", movies.size());
            return movies;

        } catch (SQLException e) {
            log.error("cannot select movies", e);
            throw new RuntimeException(e);

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }
}
