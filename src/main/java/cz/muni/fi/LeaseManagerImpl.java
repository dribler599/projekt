package cz.muni.fi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 * Class implementing LeaseManager
 */
public class LeaseManagerImpl implements LeaseManager {

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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Lease getLease(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateLease(Lease lease) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteLease(Lease lease) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Lease> getAllLeases() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Lease> findLeaseByCustomer(Customer customer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Lease> findLeaseByMovie(Movie movie) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
