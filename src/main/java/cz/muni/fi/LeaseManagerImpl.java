package cz.muni.fi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class implementing LeaseManager
 */
public class LeaseManagerImpl implements LeaseManager {

    /**

    final static Logger log = LoggerFactory.getLogger(LeaseManagerImpl.class);

    private JdbcTemplate jdbc;
    private MovieManager movieManager;
    private CustomerManager customerManager;
    private final DataSource dataSource;

    public LeaseManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setMovieManager(MovieManager movieManager) {
        this.movieManager = movieManager;
    }

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    @Override
    public void createLease(Lease lease) throws MovieException {
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("INSERT INTO LEASES (MOVIEID, CUSTOMERID, PRICE, DATEOFRENT, EXPECTEDDATEOFRETURN, DATEOFRETURN) VALUES (?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                st.setLong(1, lease.getMovie().getId());
                st.setLong(2, lease.getCustomer().getId());
                st.setInt(3, lease.getPrice());
                st.setDate(4, Date.valueOf(lease.getDateOfRent()));
                st.setDate(5, Date.valueOf(lease.getExpectedDateOfReturn()));
                st.setDate(6, Date.valueOf(lease.getDateOfReturn()));
                st.executeUpdate();
                ResultSet keys = st.getGeneratedKeys();
                if (keys.next()) {
                    lease.setId(keys.getLong(1));
                }
                log.debug("created lease {}", lease);
            }
        } catch (SQLException e) {
            log.error("cannot insert lease", e);
            throw new MovieException("database insert failed", e);
        }
    }*/

    final static Logger log = LoggerFactory.getLogger(LeaseManagerImpl.class);

    private JdbcTemplate jdbc;
    private MovieManager movieManager;
    private CustomerManager customerManager;

    public LeaseManagerImpl(DataSource dataSource) {
        jdbc = new JdbcTemplate(dataSource);
    }

    public void setMovieManager(MovieManager movieManager) {
        this.movieManager = movieManager;
    }

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    @Override
    public void createLease(Lease lease) {
        SimpleJdbcInsert insertLease = new SimpleJdbcInsert(jdbc).withTableName("leases").usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>(2);
        parameters.put("movieId", lease.getMovie().getId());
        parameters.put("customerId", lease.getCustomer().getId());
        parameters.put("price", lease.getPrice());
        parameters.put("dateofrent", Date.valueOf(lease.getDateOfRent()));
        parameters.put("expecteddateofreturn", Date.valueOf(lease.getExpectedDateOfReturn()));
        parameters.put("dateofreturn", Date.valueOf(lease.getDateOfReturn()));
        Number id = insertLease.executeAndReturnKey(parameters);
        lease.setId(id.longValue());
    }

    private RowMapper<Lease> leasesMapper = new RowMapper<Lease>() {
        @Override
        public Lease mapRow(ResultSet rs, int rowNum) throws SQLException {
            long movieId = rs.getLong("MOVIEID");
            Movie movie = null;
            try {
                movie = movieManager.getMovie(movieId);
            } catch (MovieException e) {
                log.error("cannot find movie", e);
            }
            long customerId = rs.getLong("CUSTOMERID");
            Customer customer = null;
            try {
                customer = customerManager.getCustomer(customerId);
            } catch (IllegalArgumentException e) {
                log.error("cannot find customer", e);
            }
            return new Lease(rs.getLong("id"), movie, customer, rs.getInt("price"), rs.getDate("dateofrent").toLocalDate(), rs.getDate("expecteddateofreturn").toLocalDate(), (rs.getDate("dateofreturn")).toLocalDate());
        }
    };

    @Override
    public Lease getLease(Long id) {
        return jdbc.queryForObject("SELECT * FROM LEASES WHERE ID=?", leasesMapper, id);
    }

    @Override
    public void updateLease(Lease lease) {
        jdbc.update("UPDATE LEASES set MOVIEID=?,CUSTOMERID=?,PRICE=?,DATEOFRENT=?,EXPECTEDDATEOFRETURN=?,DATEOFRETURN=? where ID=?",
                lease.getMovie().getId(), lease.getCustomer().getId(), lease.getPrice(), lease.getDateOfRent(), lease.getExpectedDateOfReturn(), lease.getDateOfReturn(), lease.getId());
    }

    @Override
    public void deleteLease(Lease lease) {
        jdbc.update("DELETE FROM LEASES WHERE ID=?", lease.getId());
    }

    @Override
    public List<Lease> getAllLeases() {
        return jdbc.query("SELECT * FROM LEASES", leasesMapper);
    }

    @Override
    public List<Lease> findLeaseByCustomer(Customer customer) {
        return jdbc.query("SELECT * FROM LEASES WHERE customerId=?", new RowMapper<Lease>() {
            @Override
            public Lease mapRow(ResultSet rs, int rowNum) throws SQLException {
                long movieId = rs.getLong("MOVIEID");
                Movie movie = null;
                try {
                    movie = movieManager.getMovie(movieId);
                } catch (MovieException e) {
                    log.error("cannot find movie", e);
                }
                return new Lease(rs.getLong("id"), movie, customer, rs.getInt("price"), rs.getDate("dateofrent").toLocalDate(), rs.getDate("expecteddateofreturn").toLocalDate(), (rs.getDate("dateofreturn")).toLocalDate());
            }
        },
                customer.getId());
    }

    @Override
    public List<Lease> findLeaseByMovie(Movie movie) {
        return jdbc.query("SELECT * FROM LEASES WHERE customerId=?", new RowMapper<Lease>() {
            @Override
            public Lease mapRow(ResultSet rs, int rowNum) throws SQLException {
                long customerId = rs.getLong("CUSTOMERID");
                Customer customer = null;
                try {
                    customer = customerManager.getCustomer(customerId);
                } catch (IllegalArgumentException e) {
                    log.error("cannot find customer", e);
                }
                return new Lease(rs.getLong("id"), movie, customer, rs.getInt("price"), rs.getDate("dateofrent").toLocalDate(), rs.getDate("expecteddateofreturn").toLocalDate(), (rs.getDate("dateofreturn")).toLocalDate());
            }
        },
                movie.getId());
    }
}
