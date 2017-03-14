package cz.muni.fi;

import java.util.List;

/**
 * Interface for managing customers
 */
public interface CustomerManager {

    void createCustomer(Customer customer);

    Customer getCustomer(Long id);

    void updateCustomer(Customer customer);

    void deleteCustomer(Customer customer);

    List<Customer> getAllCustomers();

    Customer getCustomerByName(String name);
}
