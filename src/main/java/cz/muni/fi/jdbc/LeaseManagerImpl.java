package cz.muni.fi.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
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
        if (lease == null) {
            throw new IllegalArgumentException("lease is null.");
        }
        if (lease.getId() != null) {
            throw new IllegalArgumentException("lease id is not null.");
        }
        if (lease.getMovie() == null) {
            throw new IllegalArgumentException("null parameter in lease Movie.");
        }
        if (lease.getCustomer() == null) {
            throw new IllegalArgumentException("null parameter in lease Customer.");
        }
        if (lease.getMovie() == null || lease.getCustomer() == null || lease.getPrice() == null
                || lease.getDateOfRent() == null || lease.getExpectedDateOfReturn() == null) {
            throw new IllegalArgumentException("null parameter in lease.");
        }

        if (lease.getPrice() < 0 ) {
            throw new IllegalArgumentException("negative price parameter in lease.");
        }
        if (lease.getDateOfRent().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("wrong dateofrent parameter in lease.");
        }
        if (lease.getDateOfReturn() != null) {
            if (lease.getDateOfReturn().isBefore(lease.getDateOfRent())) {
                throw new IllegalArgumentException("wrong dateofreturn parameter in lease.");
            }
        }

        SimpleJdbcInsert insertLease = new SimpleJdbcInsert(jdbc).withTableName("leases").usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>(2);
        parameters.put("movieId", lease.getMovie().getId());
        parameters.put("customerId", lease.getCustomer().getId());
        parameters.put("price", lease.getPrice());
        parameters.put("dateofrent", Date.valueOf(lease.getDateOfRent()));
        parameters.put("expecteddateofreturn", Date.valueOf(lease.getExpectedDateOfReturn()));
        LocalDate dor = lease.getDateOfReturn();
        parameters.put("dateofreturn", dor != null ? Date.valueOf(dor) : null);
        Number id = insertLease.executeAndReturnKey(parameters);
        lease.setId(id.longValue());

        log.debug("created lease {}", lease);
    }

    private RowMapper<Lease> leasesMapper = new RowMapper<Lease>() {
        @Override
        public Lease mapRow(ResultSet rs, int rowNum) throws SQLException {
            long movieId = rs.getLong("MOVIEID");
            Movie movie = null;
            try {
                movie = movieManager.getMovie(movieId);
            } catch (Exception e) {
                log.error("cannot find movie", e);
            }
            long customerId = rs.getLong("CUSTOMERID");
            Customer customer = null;
            try {
                customer = customerManager.getCustomer(customerId);
            } catch (IllegalArgumentException e) {
                log.error("cannot find customer", e);
            } catch (CustomerException e) {
                e.printStackTrace();
            }
            Date dor = rs.getDate("dateofreturn");
            return new Lease(rs.getLong("id"), movie, customer, rs.getInt("price"), rs.getDate("dateofrent").toLocalDate(), rs.getDate("expecteddateofreturn").toLocalDate(), dor!=null ? (dor).toLocalDate() : null);
        }
    };

    @Override
    public Lease getLease(Long id) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("wrong id.");
        }
        try {
            return jdbc.queryForObject("SELECT * FROM LEASES WHERE ID=?", leasesMapper, id);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            log.error("cannot get lease", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateLease(Lease lease) {
        if (lease == null) {
            throw new IllegalArgumentException("lease is null.");
        }
        if (lease.getId() == null || lease.getId() < 1) {
            throw new IllegalArgumentException("wrong id.");
        }
        if (lease.getMovie() == null || lease.getCustomer() == null || lease.getPrice() == null
                || lease.getDateOfRent() == null || lease.getExpectedDateOfReturn() == null || lease.getId() == null) {
            throw new IllegalArgumentException("null parameter in lease.");
        }

        if (lease.getPrice() < 0 ) {
            throw new IllegalArgumentException("negative price parameter in lease.");
        }
        if (lease.getDateOfRent().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("wrong dateofrent parameter in lease.");
        }
        if (lease.getDateOfReturn() != null) {
            if (lease.getDateOfReturn().isBefore(lease.getDateOfRent())) {
                throw new IllegalArgumentException("wrong dateofreturn parameter in lease.");
            }
        }
        LocalDate dor = lease.getDateOfReturn();
        try {
            jdbc.update("UPDATE LEASES set MOVIEID=?,CUSTOMERID=?,PRICE=?,DATEOFRENT=?,EXPECTEDDATEOFRETURN=?,DATEOFRETURN=? where ID=?",
                    lease.getMovie().getId(), lease.getCustomer().getId(), lease.getPrice(), Date.valueOf(lease.getDateOfRent()),
                    Date.valueOf(lease.getExpectedDateOfReturn()), dor != null ? Date.valueOf(dor) : null, lease.getId());
        } catch (DataAccessException e) {
            log.error("cannot update lease", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void deleteLease(Lease lease) {
        if (lease == null) {
            throw new IllegalArgumentException("lease is null.");
        }
        if (lease.getId() == null || lease.getId() < 1) {
            throw new IllegalArgumentException("wrong id.");
        }
        try {
            jdbc.update("DELETE FROM LEASES WHERE ID=?", lease.getId());
            log.debug("deleted lease {}", lease);
        } catch (DataAccessException e) {
            log.error("cannot delete lease", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Lease> getAllLeases() {
        try {
            return jdbc.query("SELECT * FROM LEASES", leasesMapper);
        } catch (DataAccessException e) {
            log.error("cannot get all leases", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Lease> findLeaseByCustomer(Customer customer) {
        if (customer == null || customer.getId() == null || customer.getId() < 1) {
            throw new IllegalArgumentException("invalid customer");
        }
        try {
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
        } catch (DataAccessException e) {
            log.error("cannot find leases", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Lease> findLeaseByMovie(Movie movie) {
        if (movie == null || movie.getId() == null || movie.getId() < 1) {
            throw new IllegalArgumentException("invalid movie");
        }
        try {
            return jdbc.query("SELECT * FROM LEASES WHERE MOVIEID=?", new RowMapper<Lease>() {
                        @Override
                        public Lease mapRow(ResultSet rs, int rowNum) throws SQLException {
                            long customerId = rs.getLong("CUSTOMERID");
                            Customer customer = null;
                            try {
                                customer = customerManager.getCustomer(customerId);
                            } catch (IllegalArgumentException e) {
                                log.error("cannot find customer", e);
                            } catch (CustomerException e) {
                                e.printStackTrace();
                            }
                            return new Lease(rs.getLong("id"), movie, customer, rs.getInt("price"), rs.getDate("dateofrent").toLocalDate(), rs.getDate("expecteddateofreturn").toLocalDate(), (rs.getDate("dateofreturn")).toLocalDate());
                        }
                    },
                    movie.getId());
        } catch (DataAccessException e) {
            log.error("cannot find leases", e);
            throw new RuntimeException(e);
        }
    }
}
