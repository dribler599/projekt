package cz.muni.fi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static java.time.Month.JANUARY;
import static java.time.Month.OCTOBER;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for class CustomerManagerImpl.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MySpringTestConfig.class})
@Transactional
public class CustomerManagerImplTest {

    @Autowired
    private CustomerManager manager;

    private CustomerBuilder customer1() {
        return new CustomerBuilder()
                .id(null)
                .name("Honza")
                .dateOfBirth(2000,OCTOBER,20)
                .address("HonzaStreet1")
                .email("Honza@mail.null")
                .phoneNumber("666666666");
    }

    private CustomerBuilder customer2() {
        return new CustomerBuilder()
                .id(null)
                .name("Petr")
                .dateOfBirth(1995,JANUARY,13)
                .address("PetrStreet1")
                .email("Petr@mail.null")
                .phoneNumber("111111111");
    }

    @Test
    public void createCustomer() throws Exception{
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
    public void deleteCustomer() throws Exception{
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
    public void updateCustomerName() throws Exception{
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
    public void getAllCustomers() throws Exception{

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
    public void getMovieByName() throws Exception{

        assertThat(manager.getAllCustomers()).isEmpty();

        Customer customer1 = customer1().build();
        Customer customer2 = customer2().build();
        manager.createCustomer(customer1);
        manager.createCustomer(customer2);

        System.out.println(customer1.getName() + customer2.getName());

        assertThat(manager.getCustomerByName("Honza")).containsOnly(customer1);
    }
}