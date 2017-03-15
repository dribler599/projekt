package cz.muni.fi;

import java.util.List;

/**
 * Class implementing LeaseManager
 */
public class LeaseManagerImpl implements LeaseManager {
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
