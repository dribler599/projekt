package cz.muni.fi;

import org.junit.Before;
import org.junit.Test;

import static java.time.Month.JANUARY;
import static java.time.Month.OCTOBER;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for class CustomerManagerImpl.
 */
public class CustomerManagerImplTest {

    private CustomerManager manager;

    @Before
    public void setUp() throws Exception {
        manager = new CustomerManagerImpl();
    }

    private CustomerBuilder customer1() {
        return new CustomerBuilder()
                .name("Honza")
                .dateOfBirth(2000,OCTOBER,20)
                .address("HonzaStreet1")
                .email("Honza@mail.null")
                .phoneNumber("666666666");
    }

    private CustomerBuilder customer2() {
        return new CustomerBuilder()
                .name("Petr")
                .dateOfBirth(1995,JANUARY,13)
                .address("PetrStreet1")
                .email("Petr@mail.null")
                .phoneNumber("111111111");
    }

    @Test
    public void createCustomer(){
        Customer customer = customer1().build();
        manager.createCustomer(customer);

        Long customerID = customer.getId();
        assertThat(customerID).isNotNull();

        assertThat(manager.getCustomer(customerID))
                .isNotSameAs(customer)
                .isEqualToComparingFieldByField(customer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullCustomer() {
        manager.createCustomer(null);
    }

    @Test
    public void deleteCustomer(){
        Customer customer1 = customer1().build();
        Customer customer2 = customer2().build();
        manager.createCustomer(customer1);
        manager.createCustomer(customer2);


        assertThat(manager.getCustomer(customer1.getId())).isNotNull();
        assertThat(manager.getCustomer(customer2.getId())).isNotNull();

        manager.deleteCustomer(customer1);

        assertThat(manager.getCustomer(customer1.getId())).isNull();
        assertThat(manager.getCustomer(customer2.getId())).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteNullCustomer() {
        manager.deleteCustomer(null);
    }

    @Test
    public void updateCustomerName() {
        Customer customer1 = customer1().build();
        Customer customer2 = customer2().build();
        manager.createCustomer(customer1);
        manager.createCustomer(customer2);

        customer1.setName("Pepa");
        manager.updateCustomer(customer1);

        assertThat(manager.getCustomer(customer1.getId()))
                .isEqualToComparingFieldByField(customer1);

        assertThat(manager.getCustomer(customer2.getId()))
                .isEqualToComparingFieldByField(customer2);
    }


    @Test(expected = IllegalArgumentException.class)
    public void updateNullCustomer() {
        manager.updateCustomer(null);
    }

    @Test
    public void getAllCustomers(){

        assertThat(manager.getAllCustomers()).isEmpty();

        Customer customer1 = customer1().build();
        Customer customer2 = customer2().build();

        manager.createCustomer(customer1);
        manager.createCustomer(customer2);

        assertThat(manager.getAllCustomers())
                .usingFieldByFieldElementComparator()
                .containsOnly(customer1,customer2);
    }

    @Test
    public void getMovieByName(){
        Customer customer1 = customer1().build();
        Customer customer2 = customer2().build();
        manager.createCustomer(customer1);
        manager.createCustomer(customer2);

        assertThat(manager.getCustomerByName("Honza")).containsOnly(customer1);
    }
}