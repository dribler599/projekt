package cz.muni.fi;

import java.util.List;

/**
 * Interface managing leases
 */
public interface LeaseManager {

    void createLease(Lease lease);

    Lease getLease(Long id);

    void updateLease(Lease lease);

    void deleteLease(Lease lease);

    List<Lease> getAllLeases();

    List<Lease> findLeaseByCustomer(Customer customer);

    List<Lease> findLeaseByMovie(Movie movie);
}
