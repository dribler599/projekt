package cz.muni.fi;

import java.util.List;

/**
 * Interface managing leases
 */
public interface LeaseManager {

    /**
     * Creates and adds 'lease' to database.
     *
     * @param lease of type Lease
     * @throws IllegalArgumentException when lease has invalid parameters or is null
     */
    void createLease(Lease lease);

    /**
     * Gets lease by its id
     *
     * @param id positive number of type Long
     * @return lease of type Lease or null if lease doesn't exist
     * @throws IllegalArgumentException if id is invalid or null
     */
    Lease getLease(Long id);

    /**
     * Updates 'lease' in database.
     *
     * @param lease of type Lease
     * @throws IllegalArgumentException when 'lease' has invalid parameters or is null
     */
    void updateLease(Lease lease);

    /**
     * Deletes 'lease' in database.
     *
     * @param lease of type Lease
     * @throws IllegalArgumentException when 'lease' is invalid or null
     */
    void deleteLease(Lease lease);

    /**
     * Get all leases.
     *
     * @return all leases in form of List<Lease>
     */
    List<Lease> getAllLeases();

    /**
     * Gets all customers leases.
     *
     * @param customer of type Customer
     * @return all customer's leases in form of List<Lease>
     * @throws IllegalArgumentException if customer is null
     */
    List<Lease> findLeaseByCustomer(Customer customer);

    /**
     * Gets all movie's leases.
     *
     * @param movie of type Movie
     * @return all movies's leases in form of List<Movie>
     * @throws IllegalArgumentException if movie is null
     */
    List<Lease> findLeaseByMovie(Movie movie);
}
