package cz.muni.fi;

import java.util.List;

/**
 * Interface for managing customers.
 */
public interface CustomerManager {

    /**
     * Creates new customer.
     *
     * @param customer Customer to be created.
     */
    void createCustomer(Customer customer);

    /**
     * Returns customer with given id.
     *
     * @param id Customer's id.
     * @return Customer with given id.
     */
    Customer getCustomer(Long id);

    /**
     * Updates customer.
     *
     * @param customer Customer to be updated.
     */
    void updateCustomer(Customer customer);

    /**
     *Deletes customer from database.
     *
     * @param customer Customer to be deleted.
     */
    void deleteCustomer(Customer customer);

    /**
     * Returns list of all customers.
     *
     * @return List of customers.
     */
    List<Customer> getAllCustomers();

    /**
     * Returns list of customers with given name.
     *
     * @param name Name of customer.
     * @return List of customers with given name.
     */
    List<Customer> getCustomerByName(String name);
}
