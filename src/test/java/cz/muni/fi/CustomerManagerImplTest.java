package cz.muni.fi;

import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Tests for class CustomerManagerImpl.
 */
public class CustomerManagerImplTest {

    private CustomerManagerImpl manager;

    @org.junit.Before
    public void setUp() throws Exception {
        CustomerManagerImpl manager = new CustomerManagerImpl();
    }

    @org.junit.Test
    public void createCustomer() throws Exception{

        LocalDate day = LocalDate.of(2014, Month.JANUARY, 1);
        Customer customer = new Customer(20L,"Honza", day, "HonzaStreet1",
                "Honza@mail.null", "666666666");

        manager.createCustomer(customer);

        Long customerID = customer.getId();
        assertThat("Customer has null ID.", customer.getId(), is(not(equalTo(null))));

        Customer result = manager.getCustomer(customerID);
        assertThat("Acquired customer differs from the testing one", result, is(equalTo(customer)));
        assertThat("Acquired customer is the same instance.", result, is(not(sameInstance(customer))));
        assertDeepEquals(customer, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullCustomer() {
        manager.createCustomer(null);
    }

    @org.junit.Test
    public void deleteCustomer() throws Exception{
        LocalDate day = LocalDate.of(2014, Month.JANUARY, 1);
        Customer customer1 = new Customer(10L,"Petr", day, "PetrStreet1",
                "Petr@mail.null", "111111111");
        LocalDate day2 = LocalDate.of(2010, Month.APRIL, 10);
        Customer customer2 = new Customer(20L,"Honza", day, "HonzaStreet1",
                "Honza@mail.null", "666666666");

        manager.createCustomer(customer1);
        manager.createCustomer(customer2);

        assertNotNull(manager.getCustomer(customer1.getId()));
        assertNotNull(manager.getCustomer(customer2.getId()));

        manager.deleteCustomer(customer1);

        assertNull(manager.getCustomer(customer1.getId()));
        assertNotNull(manager.getCustomer(customer2.getId()));
    }

    @org.junit.Test
    public void deleteNullCustomer() throws Exception {
        try{
            manager.deleteCustomer(null);
            fail();
        }catch (NullPointerException ex){
            // OK
        }catch (Exception ex){
            fail();
        }
    }

    @org.junit.Test
    public void getAllCustomers() throws Exception{

        assertTrue(manager.getAllCustomers().isEmpty());

        LocalDate day = LocalDate.of(2014, Month.JANUARY, 1);
        Customer customer1 = new Customer(10L,"Petr", day, "PetrStreet1",
                "Petr@mail.null", "111111111");
        LocalDate day2 = LocalDate.of(2010, Month.APRIL, 10);
        Customer customer2 = new Customer(20L,"Honza", day, "HonzaStreet1",
                "Honza@mail.null", "666666666");

        manager.createCustomer(customer1);
        manager.createCustomer(customer2);

        List<Customer> expected = Arrays.asList(customer1, customer2);
        List<Customer> actual = manager.getAllCustomers();

        Collections.sort(actual, idComparator);
        Collections.sort(expected, idComparator);

        assertEquals(expected, actual);
        assertDeepEquals(expected, actual);
    }

    /**
     * Compares lists of customers.
     *
     * @param expectedList List to be compared with.
     * @param actualList List to be compared.
     */
    private void assertDeepEquals(List<Customer> expectedList, List<Customer> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Customer expected = expectedList.get(i);
            Customer actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    /**
     * Compares atributes of customer.
     *
     * @param expected Customer to be compared with.
     * @param actual Customer to be compared.
     */
    private void assertDeepEquals(Customer expected, Customer actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getDateOfBirth(), actual.getDateOfBirth());
        assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber());
    }

    /**
     * Compares two customers by their ID.
     */
    private static Comparator<Customer> idComparator = new Comparator<Customer>() {

        @Override
        public int compare(Customer o1, Customer o2) {
            return o1.getId().compareTo(o2.getId());
        }

    };
}